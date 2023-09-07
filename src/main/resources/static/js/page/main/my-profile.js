let $save = $("#save");
let $approve = $("#approve");
let $delete = $("#delete");
let avatar = document.getElementById('avatar');
let image = document.getElementById('image');
let input = document.getElementById('input');
let $modal = $('#modal');
let $pfChange = $("#pf-change");
let cropper;
let $cancel = $("#cancel");
let $profileForm = $("#profile-form")
let isChanged = false;
let staticBackdropLabel = document.getElementById('staticBackdrop')
let profileNo;
let img;
let $img = $("#change-img");

avatar.addEventListener("click", function() {
	$("#input").click();
})

staticBackdropLabel.addEventListener('show.bs.modal', event => {
	profileNo = event.relatedTarget.getAttribute('data')
	if (profileNo == 0){
		avatar.src = "/images/party/profile-default.png";
		$("#approve").text("추가");
		$("#pf-change").attr("action", "/my/add-my-profile");
		$("#delete").remove();
	} else {
		$("#pf-change").attr("action", "/my/change-my-profile");
		let count = $(event.relatedTarget).parent().prev().children().length;
		let nick = $("#pf-nickname" + profileNo).text();
		$("#nick").val(nick);
		img = $("#pf-show" + profileNo).attr("src");
		if (count == 2) {
			$("#default").prop('checked', true);
		} else {
			$("#default").prop('checked', false);
		}
		avatar.src = img;
	}
})

// b64데이터를 Blob객체로 변환하는 함수
function b64toBlob(b64Data, contentType = '') {
	const image_data = atob(b64Data.split(',')[1]); // data:image/gif;base64 필요없으니 떼주고, base64 인코딩을 풀어준다

	const arraybuffer = new ArrayBuffer(image_data.length);
	const view = new Uint8Array(arraybuffer);

	for (let i = 0; i < image_data.length; i++) {
		view[i] = image_data.charCodeAt(i) & 0xff;
		// charCodeAt() 메서드는 주어진 인덱스에 대한 UTF-16 코드를 나타내는 0부터 65535 사이의 정수를 반환
		// 비트연산자 & 와 0xff(255) 값은 숫자를 양수로 표현하기 위한 설정
	}

	return new Blob([arraybuffer], { type: contentType });
}

// 프로필 변경을 하지 않을 경우
$cancel.on("click", function() {
	// 홈화면으로 돌아간다
	window.location = "/";
})

// 사진 파일을 변경할 시 나타나는 이벤트
input.addEventListener('change', function(e) {
	let files = e.target.files;
	let size = input.files[0].size / 1024 / 1024;
	// 파일 사이즈가 큰 경우
	if (size > 30) {
		alert("이미지 파일 크기가 너무 큽니다. 30MB 이하의 파일을 업로드 해주세요.");
		input.value = "";
		return;
	}

	// 파일 사이즈가 크지 않은 경우 사진 편집 창을 연다.
	let done = function(url) {
		input.value = '';
		image.src = url;
		$modal.modal('show');
	};

	let reader;
	let file;

	// 사진 파일을 편집해서 변경할 경우 FileReader가 파일을 읽는다.
	if (files && files.length > 0) {
		file = files[0];
		if (URL) {
			done(URL.createObjectURL(file));
		} else if (FileReader) {
			reader = new FileReader();
			reader.onload = function() {
				done(reader.result);
			};
			reader.readAsDataURL(file);
		}
	}
});

// 모달 창을 열고 닫는다.
$modal.on('shown.bs.modal', function() {
	// Cropper 생성(옵션값)
	cropper = new Cropper(image, {
		aspectRatio: 1,
		viewMode: 1,
		minContainerHeight: 400,
		cropBoxResizable: false,
		dragMode: 'move',
		background: false
	});
}).on('hidden.bs.modal', function() {
	cropper.destroy();
	cropper = null;
});

// 사진 편집 창에서 "자르기"버튼을 누른 경우
document.getElementById('crop').addEventListener('click', function() {
	let canvas;

	// 편집 창을 닫는다.
	$modal.modal('hide');

	if (cropper) {
		canvas = cropper.getCroppedCanvas({
			width: 160,
			height: 160,
		});
		// 미리보기 이미지 src에 편집한 이미지의 url을 넣는다.
		avatar.src = canvas.toDataURL();
	}
	isChanged = true;
});


$approve.on("click", function() {
	console.log(profileNo)
	let src = avatar.src;
	$("#profile-no").val(profileNo);
	// 사진이 변경된 경우
	if (isChanged) {
		console.log(isChanged);
		// 미리보기 이미지의 src를 b64데이터에서 Blob객체로 변환
		let blob = b64toBlob(src, 'image/png');
		$("#pf-nick" + profileNo).val(nick);
		// Blob객체를 image.png 파일로 변환
		let file = new File([blob], "image", { type: "image/png" });
		// 새로운 폼객체 생성후 이미지 파일 첨부
		let formdata = new FormData();
		formdata.append("file", file);

		// ajax로 파일을 S3로 전송후 nanoid 발급
		$.ajax({
			url: "/upload/profile",
			type: 'POST',
			data: formdata,
			processData: false,
			contentType: false
		}).done(function(response) {
			// S3에서 제공하는 nanoid
			let savedName = response.savedName;
			$("<input>").attr({
				type: "hidden",
				name: "isUrl",
				value: false,
			}).appendTo($pfChange);
			$("#input").prop("type", "text").val(savedName)
			
			$pfChange.submit();

			console.log("이미지 업로드 성공");

		}).fail(function() {
			console.log("이미지 업로드 실패")
		})
		// 사진을 변경하지 않은 경우
	} else {
		console.log(isChanged);
		// input의 타입을 file에서 text로 변환해서 파일명을 그대로 저장한다.
		document.querySelector("#input").type = "text";
		$("#input").val(" ");
		$("#pf-nick" + profileNo).val(nick);
		$pfChange.submit();
	}
})

$delete.on("click", function(){
	if(confirm("프로필을 삭제하시겠습니까?")){
		if ($(":checkbox").prop("checked")){
			console.log("기본값 설정이라 삭제 안됨")
			alert("기본 프로필은 삭제할 수 없습니다.")
		}
		else {
			console.log("삭제")
			let formdata = new FormData();
			formdata.append("text", profileNo);
			
			$.ajax({
				url: "/my/delete/" + profileNo,
				type: "POST",
				data: formdata,
				processData: false,
				contentType: false
			}).done(function(){
				console.log("삭제완료");
				location.reload();
			}).fail(function(){
				console.log("삭제실패");
			})
		}
	}else{
		console.log("취소")
	}
})

$save.on("click", function() {
	$profileForm.submit();
})