let $changeProfile = $("#change-profile");
let $showProfile = $("#show-profile");
let $submit = $("#submit");
let $cancel = $("#cancel");
let $joinForm = $("#join-form");

console.log($("option:eq(1)"));

let selectFirst = function(){
	$("option:eq(1)").prop("selected", "selected");
	let value = $("option:eq(1)").val();
	$.ajax({
		url:"/my/profile/" + value,
		type: 'GET',
		dataType: "json"
	}).done(function(response){
		// 화면에 이미지 띄우기
		$showProfile.empty();
		let newElement = `
			<div class="show-img">
				<img src="${response.filename}" alt="">
			</div>
			<div class="show-nickname">
				<span>${response.nickname}</span>
			</div>
		`
		$showProfile.html(newElement);
	}).fail(function(){
		console.log("error 발생");
	})
}
selectFirst();

$changeProfile.on("change", function(){
	let value = $changeProfile.val();
	$.ajax({
		url:"/my/profile/" + value,
		type: 'GET',
		dataType: "json"
	}).done(function(response){
		// 화면에 이미지 띄우기
		$showProfile.empty();
		let newElement = `
			<div class="show-img">
				<img src="${response.filename}" alt="">
			</div>
			<div class="show-nickname">
				<span>${response.nickname}</span>
			</div>
		`
		$showProfile.html(newElement);
	}).fail(function(){
		console.log("error 발생");
	})
})

$submit.on("click", function(){
	if (confirm("가입하시겠습니까?")){
		$joinForm.submit();
	}
})
$cancel.on("click", function(){
	history.back();
})
