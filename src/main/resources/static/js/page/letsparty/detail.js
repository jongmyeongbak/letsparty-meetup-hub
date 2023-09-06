let postNo = $("#postNo").val();

// 최초 상세화면 이동시 댓글 2개 불러옴
$(function() {
	fetchAndRenderLatestTwoComments();
});

// 댓글 더보기 버튼 클릭시 전체 댓글 렌더링, 한 번 더 클릭하면 다시 최신 댓글 2개를 표시
$(".more-button").click(function() {
    let commentContainer = $("#comment-container");
    if (commentContainer.hasClass("two-show")) { 
		commentContainer.removeClass("two-show d-none").addClass("all-show");
	 	fetchAndRenderComments();
		$(this).toggleClass("bi-chevron-down bi-chevron-up");
	} else if(commentContainer.hasClass("all-show")) {
		commentContainer.removeClass("all-show").addClass("two-show");
		fetchAndRenderLatestTwoComments();
		$(this).toggleClass("bi-chevron-down bi-chevron-up");
	}
});

// 댓글쓰기 버튼을 누르면 댓글 등록 폼위치로 스크롤 이동
$("#write-comment-btn").click(function(e) {
	e.preventDefault();
    $('html, body').animate({
        scrollTop: $("#comment-form").offset().top
    });
});

// 최근 댓글 2개만 불러오는 ajax 요청
function fetchAndRenderLatestTwoComments() {
    $.ajax({
        url: "/letsparty/post/" + postNo + "/latest-two-comments",  // 최신 댓글 2개를 가져올 서버의 주소
        method: "post",
        })
    .done(function(response) {
        renderComments(response);
    })
    .fail(function(){
		 alert("댓글을 가져오는 데 오류가 발생했습니다.");
	});
}
		

// 댓글 전체를 불러오는 ajax 요청
function fetchAndRenderComments() {
	$.ajax({
	    url: "/letsparty/post/" + postNo + "/all-comments",  // 댓글 데이터를 가져올 서버의 주소
	    method: "post"
	})
    .done(function(response) {
        renderComments(response);
    })
    .fail(function(){
		 alert("댓글을 가져오는 데 오류가 발생했습니다.");
	});
}

// 게시물에 달린 댓글 수를 렌더링 하는 함수
function renderCommentCnt(savedPost) {
    $("#comment-count").text(savedPost.commentCnt);
}

// 댓글을 렌더링하는 함수
function renderComments(comments) {
    // 댓글이 성공적으로 등록된 경우 처리
	// 날짜 변환 함수
	function formatDate(dateString) {
	    let date = new Date(dateString);
	    let year = date.getFullYear();
	    let month = date.getMonth() + 1;
	    let day = date.getDate();
	    let hours = date.getHours();
	    let minutes = date.getMinutes();
	    let seconds = date.getSeconds();
	    
	    // 시, 분, 초 한 자리 수 일 때 앞에 0을 붙임
        hours = hours >= 10 ? hours : '0' + hours;
        minutes = minutes >= 10 ? minutes : '0' + minutes;
        seconds = seconds >= 10 ? seconds : '0' + seconds;
	    
	    return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}:${seconds}`;
	}
	
	$("#comment-container").empty(); 
	comments.forEach(function(comment) {
    	const formattedDate = formatDate(comment.createdAt);
        let dropdownHtml = `
            <div class="d-flex justify-content-end">
                <div class="dropstart" style="position: relative;">
                    <a href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="bi bi-three-dots-vertical my-0 py-0 text-muted"></i>
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">`;

        if (comment.author) {
            dropdownHtml += `
                <li>
                    <a class="dropdown-item" href="">댓글 수정</a>
                    <a class="dropdown-item" href="">댓글 삭제</a>
                </li>`;
        } else {
            dropdownHtml += `
                <li>
                    <a class="dropdown-item" href="">신고</a>
                </li>`;
        }

        dropdownHtml += `</ul></div></div>`;
        
        let commentHtml = `
            <div class="row pb-2">
                <div class="col-12">
                    <div class="row pe-3 border-bottom">
                        <div class="col-12">
                            <div class="row d-flex" style="margin-right: -75px;">
                                <div class="col-1 post-comment-profile-container">
                                    <div class="post-comment-profile-container-inner">
                                        <img src="${comment.party.filename}" alt="" class="comment-image">
                                    </div>
                                </div>
                                <div class="col-11" style="margin: 0 -12px;">
                                    <div class="col-12 d-flex justify-content-between">
                                        <div class="d-flex justify-content-start party-name-id-container">
                                            <p class="ellipsis-party-name">
                                                <a href="/party/${comment.party.no}">${comment.party.name}</a>
                                            </p>
                                            <span>(${comment.profile.nickname})</span>
                                        </div>
                                        ${dropdownHtml}
                                    </div>
                                    <div class="d-flex justify-content-between">
                                        <p style="margin-bottom: -5px;">${comment.content}</p>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <div class="mt-1 mb-1">
                                            <small class="text-muted">${formattedDate}</small>  
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        $("#comment-container").append(commentHtml);
        });
}

// 엔터키로도 등록
$("#content").keypress(function(event) {
    if (event.which == 13) { 
        event.preventDefault(); 
        $("#btn").click();
    }
});

// 클릭 새 댓글 등록
$("#btn").click(function() {
	let partyNo = $("#partySelect").val();
	let content = $("#content").val();
	$(".comment-textarea").val("");
    $.ajax({
        url: "/letsparty/post/" + postNo + "/comment", 
        method: "post",
        data: {
            partyNo: partyNo,
            content: content
        }
    })
    .done(function(response) {
	    if (response.status === "success") {
			// 댓글을 등록한 뒤 신규 댓글 포함 전체 댓글을 불러와 렌더링
	        fetchAndRenderComments();		
	        // 총 댓글 수 렌더링
	        renderCommentCnt(response.savedPost);
	    } else if (response.status === "error") {
			// 댓글 유효성 검사 실패시 설정한 각 메시지 표시
	        alert(response.message);		
	    }
	})
    .fail(function() {
        alert("댓글 추가 중 오류가 발생했습니다.");
	});
});