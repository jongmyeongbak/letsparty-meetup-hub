$("#withdraw").on("click", function(e){
	if(!confirm("정말로 탈퇴하시겠습니까?")){
		console.log("취소")
		e.preventDefault();
	}
});

$("#delete-btn").on("click", function(e){
	if(!confirm("정말로 파티를 삭제하시겠습니까?")){
		e.preventDefault();
	}
});