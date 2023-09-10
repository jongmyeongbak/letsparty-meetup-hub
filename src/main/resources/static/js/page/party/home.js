document.addEventListener("DOMContentLoaded", function () {
    var moreButton = document.querySelector(".more-button");
    var CommentPopUp = document.querySelector(".comment-pop-up");
    var commentButton = document.querySelector(".comment-wirte-btn");
    var CommentWritePopUp = document.querySelector(".Comment-write-pop-up");

   	var partyNoElement = document.getElementById("partyNo");
	var postNoElement = document.getElementById("postId");
	
	if (partyNoElement && postNoElement) {
	    var partyNo = partyNoElement.value;
	    var postNo = postNoElement.value;
	    // 나머지 코드 실행
	} else {
	    console.error("partyNo 또는 postId 엘리먼트를 찾을 수 없습니다.");
	}
    // 댓글 목록 불러오기 함수
    function loadComments() {
        $.ajax({
            type: 'GET',
            url: '/party/' + partyNo + '/post/' + postNo,
            success: function (comments) {
                // 댓글 목록을 받아와서 화면에 출력
                var commentList = $('#comment-list');
                commentList.empty(); // 기존 댓글 목록 삭제

                $.each(comments, function (index, comment) {
                    var commentHtml = `
                        <!-- 댓글 하나 시작 -->
                        <div class="row pb-2">
                            <div class="col-1 post-comment-profile-container">
                                <div class="post-comment-profile-container-inner">
                                    <img src="/images/party/profile-default.png" alt="" class="comment-image">
                                </div>
                            </div>
                            <div class="col-11">
                                <div class="col-12 d-flex justify-content-between">
                                    <span>${comment.user.username}</span>
                                    <div class="dropstart" style="position: relative;">
                                        <a href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i class="bi bi-three-dots-vertical my-0 py-0 text-muted"></i>
                                        </a>
                                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                                            <li>
                                                <a class="dropdown-item" href="">댓글 수정</a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item" href="">댓글 삭제</a>
                                            </li>
                                            <li>
                                                <a class="dropdown-item" href="">신고-타인</a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <p style="margin-bottom: -5px;">${comment.content}</p>
                                </div>
                                <div class="d-flex align-items-center">
                                    <div class="mt-1 mb-1">
                                        <small class="text-muted">${comment.createdAt}</small>
                                        <span class="text-muted"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- 댓글 하나 끝 -->
                    `;
                    commentList.append(commentHtml);
                });
            }
        });
    }

    // 팝업 숨기는 메소드
    CommentPopUp.style.display = "none";
    CommentWritePopUp.style.display = "none";

    // more-button을 댓글을 볼 수 있는 댓글 쓰기가 열린다.
    moreButton.addEventListener("click", function () {
        if (CommentPopUp.style.display === "none" || CommentPopUp.style.display === "") {
            CommentPopUp.style.display = "block";
            loadComments(); // 댓글 목록 불러오기
        } else {
            CommentPopUp.style.display = "none";
        }
    });

    // 댓글 쓰기를 클릭하면 댓글을 작성할 수 있다.
    commentButton.addEventListener("click", function () {
        if (CommentWritePopUp.style.display === "none" || CommentWritePopUp.style.display === "") {
            CommentWritePopUp.style.display = "block";
        } else {
            CommentWritePopUp.style.display = "none";
        }
    });

    // 댓글 제출 폼에서 유효성 검사
    var commentForm = document.getElementById("party-comment-form");

    commentForm.addEventListener("submit", function (event) {
        var contentField = document.querySelector("input[name='content']");
        var content = contentField.value.trim();

        if (!content) {
            alert("댓글 내용을 입력해주세요.");
            event.preventDefault(); 
        }
    });
});