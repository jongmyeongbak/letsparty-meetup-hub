let $approve = $("#approve");
let $change = $("#change");
let avatar = document.getElementById('avatar');
let image = document.getElementById('image');
let input = document.getElementById('input');
let $modal = $('#modal');
let cropper;
let $cancel = $("#cancel");
let $profileForm = $("#profile-form")
let isChanged = false;
console.log(avatar.src);

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
$cancel.on("click", function(){
	// 홈화면으로 돌아간다
	window.location = "/";
})

// 사진변경 버튼을 누를 경우
$change.on("click", function(){
	// <input type="file">을 실행한다.
	input.click();
})

// 사진 파일을 변경할 시 나타나는 이벤트
input.addEventListener('change', function (e) {
	let files = e.target.files;
	let size = input.files[0].size / 1024 / 1024;
	// 파일 사이즈가 큰 경우
	if(size > 30) {
		alert("이미지 파일 크기가 너무 큽니다. 30MB 이하의 파일을 업로드 해주세요.");
		input.value = "";
		return;
	}
	
	// 파일 사이즈가 크지 않은 경우 사진 편집 창을 연다.
	let done = function (url) {
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
			reader.onload = function () {
			done(reader.result);
			};
			reader.readAsDataURL(file);
		}
	}
});

// 모달 창을 열고 닫는다.
$modal.on('shown.bs.modal', function () {
	// Cropper 생성(옵션값)
	cropper = new Cropper(image, {
		aspectRatio: 1,
		viewMode: 1,
		minContainerHeight: 400,
		cropBoxResizable: false,
		dragMode: 'move',
		background: false
	});
}).on('hidden.bs.modal', function () {
	cropper.destroy();
	cropper = null;
});

// 사진 편집 창에서 "자르기"버튼을 누른 경우
document.getElementById('crop').addEventListener('click', function () {
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


$approve.on("click", function(){
	let src = avatar.src;	
	// 사진이 변경된 경우
	if (isChanged) {
		console.log(isChanged);
		// 미리보기 이미지의 src를 b64데이터에서 Blob객체로 변환
		let blob = b64toBlob(src, 'image/png');
		
		// Blob객체를 image.png 파일로 변환
		let file = new File([blob], "image", {type : "image/png"});
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
		}).done(function(response){
			// S3에서 제공하는 nanoid
			let savedName = response.savedName;
			// 새로운 <input>태그를 만들어서 nanoid를 필드로 삽입 
			$("<input>").attr({
				type: "hidden",
				name: "filename",
				value: savedName
			}).appendTo($profileForm);

			$("<input>").attr({
				type: "hidden",
				name: "isUrl",
				value: false
			}).appendTo($profileForm);
			
			// 기본 프로필 이미지가 저장된 필드 삭제
			input.remove();
			
			console.log("이미지 업로드 성공");
			
			$profileForm.submit();
			
		}).fail(function(){
			console.log("이미지 업로드 실패")
		})
		// 사진을 변경하지 않은 경우
	} else {
		console.log(isChanged);
		// input의 타입을 file에서 text롭 변환해서 파일명을 그대로 저장한다.
		input.type = "text";
		$profileForm.submit();
	}
})