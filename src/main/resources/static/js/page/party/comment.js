const currentPath = window.location.pathname;
const paths = currentPath.split("/");
let partyNo = paths[2]; // 원하는 값으로 설정
let postNo = paths[4]; // 원하는 값으로 설정
let skip = 0; // 처음에 0으로 설정
let loadingComments = false; // 댓글을 로딩 중인지 여부를 나타내는 플래그
let displayedCommentNos = []; // 화면에 이미 표시된 댓글 번호를 저장하는 배열

document.addEventListener("DOMContentLoaded", function () {
    let moreButton = document.querySelector(".more-button");
    let CommentPopUp = document.querySelector(".comment-pop-up");
    let commentButton = document.querySelector(".comment-wirte-btn");
    let CommentWritePopUp = document.querySelector(".Comment-write-pop-up");


    // 초기에는 댓글 창 숨기기
    CommentPopUp.style.display = "none";

    // 날짜 형식을 포맷팅하는 함수
    function formatDate(dateString) {
        var createdAt = new Date(dateString);
        var year = createdAt.getFullYear();
        var month = createdAt.getMonth() + 1;
        var day = createdAt.getDate();
        var hours = createdAt.getHours();
        var minutes = createdAt.getMinutes();
        var seconds = createdAt.getSeconds();
        return `${year}년 ${month}월 ${day}일 - ${hours} : ${minutes} : ${seconds}`;
    }

    // 댓글 목록 불러오기 함수
    function loadComments() {
        if (loadingComments) return; // 이미 로딩 중인 경우 무시
        loadingComments = true; // 로딩 중 플래그 설정

        $.ajax({
            type: 'GET',
            url: '/party/' + partyNo + '/post/' + postNo + '/comment?skip=' + skip,
            success: function (comments) {
                loadingComments = false; // 로딩 완료 플래그 해제

                // 댓글 목록을 받아와서 화면에 출력
                var commentList = $('#comment-list');

                $.each(comments, function (index, comment) {
                    // 이미 표시된 댓글인지 확인
                    if (!displayedCommentNos.includes(comment.no)) {
                        var formattedDate = formatDate(comment.createdAt); // 날짜 포맷팅
                        var commentHtml = `
                             <div class="row pb-2">
                                <div class="col-12" id="comment">
                                    <div class="row pe-3 border-bottom">
                                        <div class="col-12">
                                            <div class="row d-flex" style="margin-right: -24px;">
                                                <!-- 프로필 -->
                                                <div class="col-1 post-comment-profile-container ">
                                                    <div class="post-comment-profile-container-inner">
                                                         <img src="${comment.profile.filename}" alt="" class="comment-image">
                                                    </div>
                                                </div>
                                                
                                                <div class="col-11" style="margin: 0 -12px;">
                                                    <div class="col-12 d-flex justify-content-between">
                                                        <!-- 닉네임 -->
                                                        <span>${comment.profile.nickname}</span>
                                                      
                                                        <!-- 드롭다운 구간 -->
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
                                            
                                                    
                                                    <!-- 내용 -->
                                                    <div class="d-flex justify-content-between">
                                                        <p style="margin-bottom: -5px;">${comment.content}</p>
                                                    </div>

                                                    <div class="d-flex align-items-center">
                                                        <!-- 날짜 -->
                                                        <div class="mt-1 mb-1" >
                                                            <small class="text-muted">${formattedDate}</small>
                                                             <span class="text-muted"></span>
                                                        </div>
                                                        
                                                    </div>
                                                    
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- 댓글 하나 끝 -->
                        `;
                        commentList.append(commentHtml);
                        displayedCommentNos.push(comment.no); // 표시된 댓글 번호 저장
                    }
                });

                skip += comments.length; // skip 값을 업데이트
            }
        });
    }

    // moreButton을 댓글을 볼 수 있는 댓글 쓰기가 열린다.
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
              // 댓글 작성 영역으로 스크롤 이동
        CommentWritePopUp.scrollIntoView({ behavior: "smooth" });
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

    // 무한 스크롤 이벤트 리스너 추가
    window.addEventListener("scroll", function () {
        if (loadingComments) return; // 댓글을 로딩 중인 경우, 무시

        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
            // 스크롤이 페이지 하단에 도달하면 추가 댓글 로드
            loadComments();
        }
    });
});