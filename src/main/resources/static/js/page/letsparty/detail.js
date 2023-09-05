$("#btn").click(function() {
	let postNo = $("#postNo").val();
    let partyNo = $("#partySelect").val();
    let content = $("#content").val();

    // 여기서 AJAX를 이용해 서버에 댓글 데이터를 전송
    $.ajax({
        url: "/letsparty/post/" + postNo+ "/comment", // 적절한 URL로 변경
        method: "POST",
        data: {
            partyNo: partyNo,
            content: content
        }
    })
    .done(function(response) {
		if (response.status === "success") {
			
		// 댓글이 성공적으로 등록된 경우 처리
		// 날짜 변환해서 추가 - 타임리프의 시간 변환 객체 여기서 사용불가
		function formatDate(dateString) {
			$("#comment-count").replaceWith(
				response.savedPost.commentCnt
			);
		    const date = new Date(dateString);
		    const year = date.getFullYear();
		    const month = date.getMonth() + 1;
		    const day = date.getDate();
		    const hours = date.getHours();
		    const minutes = date.getMinutes();
		    const seconds = date.getSeconds();
		    
		    return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}:${seconds}`;
		}
		const commentDate = response.comment.createdAt; //서버로부터 받아온 댓글의 날짜
		const formattedDate = formatDate(commentDate);
		
        let dropdownHtml = `
        <div class="d-flex justify-content-end">
		        <div class="dropstart" style="position: relative;">
		            <a href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
		                <i class="bi bi-three-dots-vertical my-0 py-0 text-muted"></i>
		            </a>
		            <ul class="dropdown-menu" aria-labelledby="dropdownMenuLink">`;
		
		        if (response.isAuthor) {
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
				<!-- 댓글 -->
				<div class="row pb-2">
				    <div class="col-12" id="comment">
				        <div class="row pe-3 border-bottom">
				            <div class="col-12">
				                <div class="row d-flex" style="margin-right: -75px;">
				                    <!-- 프로필 -->
				                    <div class="col-1 post-comment-profile-container ">
				                        <div class="post-comment-profile-container-inner">
				                            <img src="${response.comment.party.filename}" alt="" class="comment-image">
				                        </div>
				                    </div>
				                    <div class="col-11" style="margin: 0 -12px;">
				                        <div class="col-12 d-flex justify-content-between">
			                                <!-- 파티명 및 닉네임 구간 -->
			                                <div class="d-flex justify-content-start party-name-id-container">
			                                    <!-- 파티명 -->
			                                    <p class="ellipsis-party-name">
			                                        <a href="/party/${response.comment.party.no}">${response.comment.party.name}</a>
			                                    </p>
			                                    <!-- 닉네임 -->
			                                    <span>(${response.comment.profile.nickname})</span>
			                                </div>
				                            `
				                            + dropdownHtml +
				                        `
				                        </div>
				                        <!-- 내용 -->
				                        <div class="d-flex justify-content-between">
				                            <p style="margin-bottom: -5px;">${response.comment.content}</p>
				                        </div>
				                        <div class="d-flex align-items-center">
				                            <!-- 날짜 -->
				                            <div class="mt-1 mb-1" >
				                                <small class="text-muted">${formattedDate}</small>  
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
        $("#comment-container").append(commentHtml);
        } else if (response.status === "error") {
			alert(response.message); 
		}
    })
    .fail(function(jqXHR) {
	    let response = jqXHR.responseJSON;
	    if (response && response.message) {
	        alert(response.message);
	    } else {
	        alert("댓글 추가 중 오류가 발생했습니다.");
	    }
	});
});

