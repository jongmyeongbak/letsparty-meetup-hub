// 필드 값 설정 후 폼 제출 함수
function updateAndSubmit(name, value, resetPage = true) {
	$(`input[name=${name}]`).val(value);
    if (resetPage) {
        $("input[name=page]").val(1);
    }
    $("#form-letsparty-search").submit();
}

$(function(){
	// url의 요청 파라미터에 따라 카테고리 탭 active클래스 추가 제거.
    const categoryValue = new URL(window.location.href).searchParams.get("categoryNo") || "0";
    const categoryElements = $("#letsparty-categorys .nav-link").toArray();

	$(categoryElements).each(function() {
	    if ($(this).data("value").toString() === categoryValue) {
	        $(this).addClass("active");
	    } else {
	        $(this).removeClass("active");
	    }
    });
    
	// 카테고리 변경
	$("#letsparty-categorys .nav-link").on("click", function(e){
	    e.preventDefault();
	    let categoryNo = $(this).data("value");
	    updateAndSubmit("categoryNo", categoryNo);
	});
	    
	// 정렬 변경
	$("select[name=sort]").on("change", function(){
		let sort = $("select[name=sort]").val();
	    updateAndSubmit("sort", sort);
	});
	
	// 행 개수 변경
	$("select[name=rows]").on("change", function(){
		let rows = $("select[name=rows]").val();
	    $("input[name=rows]").val(rows);
	    updateAndSubmit("rows", rows);
	});
	
	// 검색
	$("#outline-btn").on("click", function(){
		let keyword = $("input[name=keyword]").val();
	    if (keyword.trim() === "") {
	        alert("키워드를 입력하세요!");
	        $("input[name=keyword]").focus();
	        return;
	    }
	    $("input[name=page]").val(1);
	    updateAndSubmit("keyword", keyword);
	});
	
});

// 페이지 변경
function changePage(e, page) {
   e.preventDefault();
   updateAndSubmit("page", page, false);
}