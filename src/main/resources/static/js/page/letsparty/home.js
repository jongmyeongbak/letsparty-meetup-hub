    // 카테고리 변경
    $("#letsparty-categorys").on('click', 'a', function(event) { 
        event.preventDefault();
        $('.nav-link.active').removeClass("active");
        $(this).addClass("active");
    });

    $("#letsparty-categorys .nav-link").on("click", function(e){
        e.preventDefault();
        let categoryNo = $(this).data("value");
        console.log(categoryNo);
        changeCategory(categoryNo);
    });
    
    // 정렬 변경
    $("select[name=sort]").on("change", changeSort);

    // 행 개수 변경
    $("select[name=rows]").on("change", changeRows);

    // 검색
    $("#outline-btn").on("click", searchLetsParty);

function changeCategory(categoryNo){
    $("input[name=categoryNo]").val(categoryNo);
    $("input[name=page]").val(1);
    $("#form-letsparty-search").submit();
}

function changeSort() {
    let sort = $("select[name=sort]").val();
    $("input[name=sort]").val(sort);
    $("input[name=page]").val(1);
    $("#form-letsparty-search").submit();
}

function changeRows() {
    let rows = $("select[name=rows]").val();
    $("input[name=rows]").val(rows);
    $("input[name=page]").val(1);
    $("#form-letsparty-search").submit();
}

function searchLetsParty() {
    let keyword = $("input[name=keyword]").val();
    if (keyword.trim() === "") {
        alert("키워드를 입력하세요!");
        $("input[name=keyword]").focus();
        return;
    }
    $("input[name=page]").val(1);
    $("#form-letsparty-search").submit();
}
