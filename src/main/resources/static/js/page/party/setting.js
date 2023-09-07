$("#withdraw").on("click", function(e){
	if(!confirm("정말로 탈퇴하시겠습니까?")){
		console.log("취소")
		e.preventDefault();
	}
});