$(function() {
    $("#letsparty-categorys").on('click', 'a', function(event) { 
        
        event.preventDefault();
    
        $('.nav-link.active').removeClass("active");
        $(this).addClass("active");
           
    })
    
})
$("#letsparty-categorys .nav-link").on("click", function(e){
	e.preventDefault();
	let categoryNo = $(this).data("value");
	console.log(categoryNo)
	
	changeCategory(categoryNo);
})

function changeCategory(categoryNo){
	$("input[name=category]").val(categoryNo);
	$("input[name=page]").val(1);
	$("input[name=sort]").val(1);
	$("input[name=rows]").val(1);
	
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
