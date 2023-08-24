/**
 * 
 */
$(function () {
  const currentPath = window.location.pathname;
  const roomId = currentPath.substring(currentPath.lastIndexOf('/') + 1);
  let $logData = $('.log-data');
  let $logFriend = $('.log-friend');
  let $logMy = $('.log-my');
  let $mentions = $('.mentions');
  const $sendBtn = $('.send');

  let socket = new SockJS("/ws");
  let stompClient = webstomp.over(socket);

  stompClient.connect({}, function () {
    // 구독한 채팅방에서 메시지 들어옴
    stompClient.subscribe("/topic/chat/" + roomId, function (frame) {
      // 받은 데이터
      let data = JSON.parse(frame.body);
      // 내가 아니면
      if (data.userNo != 9999999) {
          // TODO: 채팅방 입장 시 유저정보 받은 뒤 변환해야 함
        addLogFriend(data.no, data.userNo, data.unreadCnt, data.time, data.text);
      }
      // 그러고 로그 표시하기
    });
  });

  $sendBtn.click(function () {
    send();
  });
  
  function send() {
    let message = $mentions.val();
    let payload = {
      type: 0,
      text: message.trim()
    };
    stompClient.send("/app/chat/" + roomId, JSON.stringify(payload));
    $mentions.val("");
  }

  function addLogFriend(no, nickname, unreadCnt, time, text) {
    var chatHtml = `
      <div class="log-friend" data-log-no=${no}>
        <div class="pf-image"></div>
        <div class="log-content">
          <div class="nickname">${nickname}</div>
          <div class="log-bubble">${text}</div>
        </div>
        <div class="log-aside">
          <div class="read">${unreadCnt}</div>
          <div class="time">${time}</div>
        </div>
      </div>
    `;
    $logData.append(chatHtml);
  }
});