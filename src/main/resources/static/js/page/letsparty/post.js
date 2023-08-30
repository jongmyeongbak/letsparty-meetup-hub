$("#cancel-btn").on("click", function() {
	confirm('작성 중인 내용이 저장되지 않고 사라집니다.') && history.back();
})
$(".select-party").change(function() {
    let selectedOption = $(this).find("option:selected");
    $("#categoryNo").val(selectedOption.data("category"));
});
