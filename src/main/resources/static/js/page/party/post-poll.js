document.addEventListener("DOMContentLoaded", function() {
    var openPollBtn = document.getElementById("openpoll");
    var pollModal = document.getElementById("poll-modal");
    var pollTitleInput = document.querySelector(".titleWrap input"); // 투표 제목 입력란
    var pollSubject = document.querySelector(".poll-subject"); // 항목을 추가할 컨테이너
    var addItemButton = document.querySelector(".add-item");
    var itemCounter = 3; // 초기 항목 개수

    
	var pollAnonymusCheckbox = document.getElementById("anonymous-option");
    var pollDuplicableCheckbox = document.getElementById("duplicable-option");
    
    openPollBtn.onclick = function() {
        // 모달 열 때마다 내용 초기화
        pollTitleInput.value = ""; // 투표 제목 초기화
        pollSubject.innerHTML = "";
        itemCounter = 3; // 항목 개수 초기화

        for (var i = 0; i < itemCounter; i++) {
            var newItem = createPollItem(i + 1);
            pollSubject.appendChild(newItem);
        }

        // 중복투표와 익명투표 체크박스 초기화
        if (pollAnonymusCheckbox) {
            pollAnonymusCheckbox.checked = false;
        }
        if (pollDuplicableCheckbox) {
            pollDuplicableCheckbox.checked = false;
        }

        // 모달 보이기
        pollModal.style.display = "block";
    }

    addItemButton.addEventListener("click", function() {
        var newItem = createPollItem(itemCounter + 1);
        pollSubject.appendChild(newItem);
        itemCounter++;
    });

    function createPollItem(itemNumber) {
        var newItem = document.createElement("li");
        newItem.classList.add("poll-item");
        newItem.innerHTML = `
            <span class="item-number">${itemNumber}.</span>
            <input type="text" class="inputItem" maxlength="200" value="" name="poll_item" placeholder="항목 입력">
            ${itemNumber > 3 ? '<button class="delete-item"><i class="fa-regular fa-square-minus"></i></button>' : ''}
        `;

        if (itemNumber > 3) {
            var deleteButton = newItem.querySelector(".delete-item");
            deleteButton.addEventListener("click", function() {
                pollSubject.removeChild(newItem);
                itemCounter--;
            });
        }

        return newItem;
    }

    var attachPollButton = document.getElementById("poll-attachment-button"); // 첨부 버튼
		attachPollButton.addEventListener("click", function() {
    var pollTitle = document.getElementById("pollTitle").value;
    var pollItemsInputs = document.querySelectorAll(".inputItem"); // 항목 input 요소들을 선택
    var pollItemsValues = Array.from(pollItemsInputs).map(input => input.value); // 항목 값들을 배열로 추출

   	var pollAnonymusValue = pollAnonymusCheckbox ? pollAnonymusCheckbox.checked : false;
    var pollDuplicableValue = pollDuplicableCheckbox ? pollDuplicableCheckbox.checked : false;

    var pollItemContents = pollItemsValues.map((itemValue, index) => {
        return {
            number: index + 1,
            content: itemValue
        };
    });

    var pollStatusDiv = document.querySelector("#poll-short-form .poll-status");

    var newPoll = document.createElement("div");
    newPoll.classList.add("col-11", "my-1");

    var pollAnonymousInput = document.getElementById("poll-is-anonymous");
    var pollDuplicateInput = document.getElementById("poll-is-duplicable");
	var pollTitlein = document.getElementById("poll-title");
    var pollItemsHTML = pollItemContents.map(item => `
        <input type="hidden" name="pollOptionForm.items" value="${item.number}:${item.content}" >
    `).join("");

    newPoll.innerHTML = `
        <div class="d-flex">
            <div class="col-1 me-2 d-flex justify-content-center align-items-center">
                <div class="">
                    <i class="fa-solid fa-square-poll-horizontal fa-xl post-item-color"></i>
                </div>
            </div>
            <div class="col-11 my-1">
                <div class="d-flex">
                    <h6 class="me-2">
                        <strong class="post-item-text-color">투표 중</strong>
                    </h6>
                    <h6 class="text-muted"> 0명 참여</h6>
                </div>
                <h6 class="my-1"><strong>${pollTitle}</strong></h6>
                <div id="poll-delete-btn" class="d-flex justify-content-end">
                    <button class="btn d-flex pb-3 pe-3">
                        <i class="fa-regular fa-circle-xmark fa-xl"></i>
                    </button>
               </div>
               <div class="poll-option-input-div"> 
                	${pollItemsHTML} <!-- 항목들을 삽입 -->
               </div>
            </div>
        </div>
    `;

    if (pollStatusDiv) {
        pollStatusDiv.innerHTML = "";
        pollStatusDiv.appendChild(newPoll);
    }
    if (pollAnonymousInput) {
        pollAnonymousInput.value = pollAnonymusValue ? 1 : 0;
    }
    if (pollDuplicateInput) {
        pollDuplicateInput.value = pollDuplicableValue ? 1 : 0;
    }
    if (pollTitlein) {
		pollTitlein.value = pollTitle;
	}
   

    var modal = bootstrap.Modal.getInstance(pollModal);
    modal.hide();
    
   	var pollDeleteButton = newPoll.querySelector("#poll-delete-btn");
        pollDeleteButton.addEventListener("click", function() {
            // 해당 투표 영역을 삭제
            pollStatusDiv.removeChild(newPoll);
        });
    
});
});
