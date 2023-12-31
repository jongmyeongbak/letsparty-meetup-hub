$(function () {
  const userLocale = navigator.language || navigator.userLanguage;
  const currentPath = window.location.pathname;
  const roomId = currentPath.substring(currentPath.lastIndexOf("/") + 1);
  const myNo = parseInt(document.body.getAttribute("data-my-no"));
  const $chatHeadLeft = $(".chat-head-left");
  const $chatHeadRight = $(".chat-head-right");
  const isPrivate = $chatHeadLeft.text() === "";
  const $chatDialogue = $(".chat-dialogue");
  const $mentions = $(".mentions");
  const $sendBtn = $(".send");

  let socket = new SockJS("/ws");
  let stompClient = webstomp.over(socket);
  let isScrollable = true;

  const chatUsers = {};
  let nicknames;
  const deferreds = [];
  let lastReadMessageNo;

  $.ajax({
    async: false,
    type: "GET",
    url: `/chat/init/${roomId}`,
    dataType: "json",
  })
    .done(function (initInfo) {
      let chatUserResponses = initInfo[0];
      let chatMessages = initInfo[1];
      lastReadMessageNo = initInfo[2];

      $.each(chatUserResponses, function (_, chatUser) {
        const { userNo, nickname, filename, url } = chatUser;
        chatUsers[userNo] = { nickname, filename, url };
      });

      let requiredUserNos = new Set();
      $.each(chatMessages, function (_, log) {
        let no = log.userNo;
        if (!chatUsers[no] && no !== myNo) {
          requiredUserNos.add(no);
        }
      });

      if (isPrivate) {
        $chatHeadLeft.text(setNickTitle(chatUsers));
      }

      if (requiredUserNos.size) {
        addUserInfoBatch(requiredUserNos).then(function () {
          $.each(chatMessages, function (_, log) {
            const deferred = $.Deferred();
            addLogForInit(log, deferred);
          });
        });
      } else {
        $.each(chatMessages, function (_, log) {
          const deferred = $.Deferred();
          addLogForInit(log, deferred);
        });
      }
    })
    .fail(function (error) {
      alert("입장 실패: 창을 다시 열거나 새로고침하세요: ", error.statusText);
      window.close();
    });

  $.when(...deferreds).done(() => {
    adjustScroll(lastReadMessageNo);
  });

  stompClient.connect({}, function () {
    // 구독한 채팅방에서 메시지 들어옴
    stompClient.subscribe("/topic/chat/" + roomId, function (frame) {
      // 받은 데이터
      let data = JSON.parse(frame.body);

      if (data.type === 3) {
        // 방에 접속한 경우
        decreaseUnreadCntAfter(data.unreadCnt);
      } else {
        addLog(data);
      }

      if (isScrollable) {
        $chatDialogue.scrollTop(1e9);
      }
    });
  });

  function addLog(log) {
    const date = new Date(log.createdAt);
    const yearMonthDay = getYearMonthDay(date);
    let logData = document.getElementById(yearMonthDay);

    if (!logData) {
      logData = getLogDataElem(date, yearMonthDay);
    }

    switch (log.type) {
      // TALK
      case 0:
        if (log.userNo === myNo) {
          addLogMyElem(log, date, yearMonthDay);
        } else {
          let chatUser = chatUsers[log.userNo];
          if (chatUser) {
            addLogFriendElem(logData, log, date, yearMonthDay);
          } else {
            addUserInfo(
              log.userNo,
              addLogFriendElem,
              logData,
              log,
              date,
              yearMonthDay
            );
          }
        }
        break;

      // JOIN
      case 1:
        const { userNo, nickname, filename, url } = log;
        if (!chatUsers[userNo]) {
          chatUsers[userNo] = { nickname, filename, url };
        }

        addLogMoveElem(logData, log, date, "들어왔습니다", yearMonthDay);
        $chatHeadRight.text(parseInt($chatHeadRight.text()) + 1);
        appendNickToTitle(nickname);
        break;

      // EXIT
      case 2:
        if (log.userNo === myNo) {
          window.close();
          return;
        }
        decreaseUnreadCntAfter(log.unreadCnt);
        addLogMoveElem(logData, log, date, "나갔습니다", yearMonthDay);
        $chatHeadRight.text($chatHeadRight.text() - 1);
        removeNickFromTitle(chatUsers[log.userNo].nickname);
        break;

      default:
        break;
    }
  }

  function addLogForInit(log, deferred) {
    const date = new Date(log.createdAt);
    const yearMonthDay = getYearMonthDay(date);
    let logData = document.getElementById(yearMonthDay);

    if (!logData) {
      logData = getLogDataElem(date, yearMonthDay);
    }

    switch (log.type) {
      // TALK
      case 0:
        if (log.userNo === myNo) {
          addLogMyElem(log, date, yearMonthDay);
        } else {
          addLogFriendElem(logData, log, date, yearMonthDay);
        }
        break;

      // JOIN
      case 1:
        addLogMoveElem(logData, log, date, "들어왔습니다", yearMonthDay);
        break;

      // EXIT
      case 2:
        addLogMoveElem(logData, log, date, "나갔습니다", yearMonthDay);
        break;

      default:
        break;
    }
    deferred.resolve();
  }

  function insertLog(logElement, newLogNo, yearMonthDay) {
    const targetContainer = $(`#${yearMonthDay}`);
    let inserted = false;

    targetContainer.find(".log-talk").each(function () {
      const currentLogNo = parseInt($(this).attr("data-log-no"), 10);

      if (newLogNo < currentLogNo) {
        $(this).before(logElement);
        inserted = true;
        return false;
      }
    });

    if (!inserted) {
      targetContainer.append(logElement);
    }
  }

  function addLogMoveElem(logData, log, date, action, yearMonthDay) {
    const logMoveElem = document.createElement("div");
    logMoveElem.className = "log log-move text-center";
    logMoveElem.dataset.logNo = log.no;

    const pElem = document.createElement("p");
    if (log.userNo === myNo) {
      pElem.textContent = `채팅방에 ${action}.`;
    } else {
      pElem.textContent = `${chatUsers[log.userNo].nickname}님이 ${action}.`;
    }
    logMoveElem.appendChild(pElem);
    insertLog(logMoveElem, log.no, yearMonthDay);
  }

  function decreaseUnreadCntAfter(lastReadCnt) {
    const logTalks = document.querySelectorAll(".log-talk[data-log-no]");
    logTalks.forEach((logTalk) => {
      const dataLogNo = parseInt(logTalk.getAttribute("data-log-no"));
      if (dataLogNo > lastReadCnt) {
        const timeElem = logTalk.querySelector(".read");
        let currentNumber = parseInt(timeElem.textContent);
        if (!isNaN(currentNumber)) {
          timeElem.textContent = currentNumber === 1 ? "" : currentNumber - 1;
        }
      }
    });
  }

  function getLogDataElem(date, yearMonthDay) {
    const logDataElem = document.createElement("div");
    logDataElem.className = "log-data";
    logDataElem.id = yearMonthDay;

    const logDateElem = document.createElement("div");
    logDateElem.className = "log-date text-center";

    const timeElem = document.createElement("time");
    timeElem.textContent = getDateString(date);

    logDateElem.appendChild(timeElem);
    logDataElem.appendChild(logDateElem);
    $chatDialogue.append(logDataElem);

    return logDataElem;
  }

  function addLogFriendElem(logData, log, date, yearMonthDay) {
    const logFriendElem = document.createElement("div");
    logFriendElem.className = "log log-talk log-friend";
    logFriendElem.dataset.logNo = log.no;

    const pfImageElem = document.createElement("div");
    pfImageElem.className = "pf-image";

    const imgElem = document.createElement("img");
    imgElem.setAttribute(
      "src",
      `${
        chatUsers[log.userNo].url
          ? ""
          : "https://d2j14nd8cs76n6.cloudfront.net/images/profiles/"
      }${chatUsers[log.userNo].filename}`
    );
    imgElem.setAttribute("width", "40");
    imgElem.setAttribute("height", "40");
    imgElem.setAttribute("alt", chatUsers[log.userNo].nickname);
    imgElem.setAttribute("loading", "lazy");
    pfImageElem.appendChild(imgElem);
    logFriendElem.appendChild(pfImageElem);

    const logContentElem = document.createElement("div");
    logContentElem.className = "log-content";

    const nicknameElem = document.createElement("div");
    nicknameElem.className = "nickname";
    nicknameElem.textContent = chatUsers[log.userNo].nickname;
    logContentElem.appendChild(nicknameElem);

    const logSectionElem = document.createElement("div");
    logSectionElem.className = "log-section";

    const logBubbleElem = document.createElement("div");
    logBubbleElem.className = "log-bubble";
    logBubbleElem.textContent = log.text;
    logSectionElem.appendChild(logBubbleElem);

    const logAsideElem = document.createElement("div");
    logAsideElem.className = "log-aside";

    const readElem = document.createElement("div");
    readElem.className = "read";
    readElem.textContent = log.unreadCnt === 0 ? "" : log.unreadCnt;
    logAsideElem.appendChild(readElem);

    const timeElem = document.createElement("div");
    timeElem.className = "time";
    timeElem.textContent = getTimeString(date);
    logAsideElem.appendChild(timeElem);
    logSectionElem.appendChild(logAsideElem);
    logContentElem.appendChild(logSectionElem);
    logFriendElem.appendChild(logContentElem);

    insertLog(logFriendElem, log.no, yearMonthDay);
  }

  function addLogMyElem(log, date, yearMonthDay) {
    let chatUser = chatUsers[log.userNo];

    const logMyElem = document.createElement("div");
    logMyElem.className = "log log-talk log-my";
    logMyElem.dataset.logNo = log.no;

    const logContentElem = document.createElement("div");
    logContentElem.className = "log-content";

    const logSectionElem = document.createElement("div");
    logSectionElem.className = "log-section";

    const logAsideElem = document.createElement("div");
    logAsideElem.className = "log-aside";

    const readElem = document.createElement("div");
    readElem.className = "read";
    readElem.textContent = log.unreadCnt === 0 ? "" : log.unreadCnt;
    logAsideElem.appendChild(readElem);

    const timeElem = document.createElement("div");
    timeElem.className = "time";
    timeElem.textContent = getTimeString(date);
    logAsideElem.appendChild(timeElem);
    logSectionElem.appendChild(logAsideElem);

    const logBubbleElem = document.createElement("div");
    logBubbleElem.className = "log-bubble";
    logBubbleElem.textContent = log.text;
    logSectionElem.appendChild(logBubbleElem);
    logContentElem.appendChild(logSectionElem);
    logMyElem.appendChild(logContentElem);

    insertLog(logMyElem, log.no, yearMonthDay);
  }

  function addUserInfo(userNo, callback, ...args) {
    $.ajax({
      type: "GET",
      url: `/chat/members/${roomId}/${userNo}`,
      dataType: "json",
    })
      .done(function (response) {
        const { userNo, nickname, filename, url } = response;
        chatUsers[userNo] = { nickname, filename, url };
        if (callback && typeof callback === "function") {
          callback(...args);
        }
      })
      .fail(function (error) {
        alert("연결 실패: 창을 다시 열거나 새로고침하세요: ", error.statusText);
        window.close();
      });
  }

  function addUserInfoBatch(userNos) {
    return new Promise((resolve, reject) => {
      $.ajax({
        type: "POST",
        url: `/chat/members/${roomId}`,
        data: JSON.stringify(Array.from(userNos)),
        contentType: "application/json",
        dataType: "json",
      })
        .done(function (responses) {
          $.each(responses, function (_, response) {
            const { userNo, nickname, filename, url } = response;
            chatUsers[userNo] = { nickname, filename, url };
          });
          resolve();
        })
        .fail(function (error) {
          reject(error);
        });
    });
  }

  function getYearMonthDay(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return year * 10000 + month * 100 + day;
  }

  function getDateString(date) {
    const dateOptions = {
      year: "numeric",
      month: "long",
      day: "numeric",
      weekday: "long",
    };
    return date.toLocaleDateString(undefined, dateOptions);
  }

  function getTimeString(date) {
    if (userLocale === "ko-KR") {
      const hours = date.getHours();
      const minutes = String(date.getMinutes()).padStart(2, "0");

      const displayHours = ((hours + 11) % 12) + 1;

      return `${hours < 12 ? "오전 " : "오후 "}${displayHours}:${minutes}`;
    } else {
      return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });
    }
  }

  let isNotComposing = true;

  $(".mentions").on("compositionstart", function (e) {
    isNotComposing = false;
  });

  $(".mentions").on("compositionend", function (e) {
    isNotComposing = true;
  });

  $(".mentions").on("keydown", function (e) {
    if (isNotComposing && e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      send();
    }
  });

  $sendBtn.click(function () {
    send();
  });

  function send() {
    let payload = $mentions.val();
    if (payload.trim() === "") {
      $mentions.focus();
      return;
    }
    stompClient.send("/app/chat/" + roomId, payload);
    $mentions.val("");
    isScrollable = true;
  }

  $chatDialogue.scroll((e) => {
    const { scrollHeight, scrollTop, clientHeight } = e.target;

    if (clientHeight + scrollTop < scrollHeight) {
      isScrollable = false;
      return;
    }
    isScrollable = true;
  });

  $("#btn-go-party").click(function () {
    Swal.fire({
      title: "채팅방 나가기",
      text: "바로 나가시겠습니까?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "나가기",
      cancelButtonText: "아니오",
    }).then((result) => {
      if (result.isConfirmed) {
        $.ajax({
          type: "POST",
          url: "/chat/exit",
          data: {
            roomId: roomId,
          },
        })
          .done(function () {
            Swal.fire("성공", "방에서 나왔습니다.", "success").then(
              (result) => {
                if (result.isConfirmed) {
                  window.close(); // 브라우저 창 닫기
                }
              }
            );
          })
          .fail(function () {
            Swal.fire("오류", "오류가 발생했습니다.", "error");
          });
      }
    });
  });

  let delay = 300;
  let timer = null;
  $(window).resize(function () {
    clearTimeout(timer);
    timer = setTimeout(function () {
      if (window.innerWidth < 380) {
        window.resizeTo(400, 644);
      }
    }, delay);
  });

  $(document).ready(function () {
    $mentions.focus();
  });

  function setNickTitle(chatUsers) {
    if (isPrivate) {
      nicknames = Object.values(chatUsers).map((user) => user.nickname);
      const joinedNicknames = nicknames.join(", ");
      $chatHeadLeft.text(joinedNicknames);
    }
  }

  function appendNickToTitle(nickname) {
    if (isPrivate) {
      nicknames.push(nickname);
      const joinedNicknames = nicknames.join(", ");
      $chatHeadLeft.text(joinedNicknames);
    }
  }

  function removeNickFromTitle(oldNick) {
    if (isPrivate) {
      nicknames = nicknames.filter((nickname) => nickname !== oldNick);
      const joinedNicknames = nicknames.join(", ");
      $chatHeadLeft.text(joinedNicknames);
    }
  }

  function adjustScroll(lastReadMessageNo) {
    let targetElem = null;

    $(".log-talk[data-log-no]").each((_, elem) => {
      const dataLogNo = parseInt($(elem).attr("data-log-no"));
      if (
        dataLogNo > lastReadMessageNo &&
        (targetElem === null ||
          dataLogNo < parseInt($(targetElem).attr("data-log-no")))
      ) {
        targetElem = elem;
      }
    });

    if (targetElem) {
      $chatDialogue
        .stop()
        .animate({ scrollTop: $(targetElem).position().top - 155 }, 500);
    } else {
      $chatDialogue
        .stop()
        .animate({ scrollTop: $chatDialogue[0].scrollHeight }, 500);
    }
  }
});
