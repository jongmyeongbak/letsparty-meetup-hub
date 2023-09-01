$(function(){
	// url의 요청 파라미터에 따라 카테고리 탭 active클래스 추가 제거.
    const categoryValue = new URL(window.location.href).searchParams.get("categoryNo") || "0";
    const categoryElements = document.querySelectorAll("#letsparty-categorys .nav-link");

    categoryElements.forEach(function(el){
        if (el.getAttribute("data-value") === categoryValue) {
            el.classList.add("active");
        } else {
            el.classList.remove("active");
        }
    });
    
	// 카테고리 변경
	$("#letsparty-categorys .nav-link").on("click", function(e){
	    e.preventDefault();
	    let categoryNo = $(this).data("value");
	    $("input[name=categoryNo]").val(categoryNo);
	    $("input[name=page]").val(1);
	    $("#form-letsparty-search").submit();
	});
	    
	// 정렬 변경
	$("select[name=sort]").on("change", function(){
		let sort = $("select[name=sort]").val();
	    $("input[name=sort]").val(sort);
	    $("input[name=page]").val(1);
	    $("#form-letsparty-search").submit();
	});
	
	// 행 개수 변경
	$("select[name=rows]").on("change", function(){
		let rows = $("select[name=rows]").val();
	    $("input[name=rows]").val(rows);
	    $("input[name=page]").val(1);
	    $("#form-letsparty-search").submit();
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
	    $("#form-letsparty-search").submit();
	});
	
});

// 페이지 변경
function changePage(event, page) {
   event.preventDefault();
   $("input[name=page]").val(page);
   $("#form-letsparty-search").submit();
}
