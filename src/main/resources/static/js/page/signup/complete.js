let $approve = $("#approve");
let $change = $("#change");
let avatar = document.getElementById('avatar');
let image = document.getElementById('image');
let input = document.getElementById('input');
let $modal = $('#modal');
let cropper;
let $cancel = $("#cancel");
let $profileForm = $("#profile-form")

$cancel.on("click", function(){
	console.log("click")
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
$change.on("click", function(){
	input.click();
})

$approve.on("click", function(){
	console.log("approve");
	let src = avatar.src;	
	let blob = b64toBlob(src, 'image/png');
	let blobURL = URL.createObjectURL(blob);
	console.log(blobURL);
	$("#newimg").attr("src", blobURL);
	let file = new File([blob], "image", {type : "image/png"});
	let formdata = new FormData();
	formdata.append("file", file);
	console.log(formdata.has("file"));
	$.ajax({
		url: "/upload/profile",
		type: 'POST',
		data: formdata,
		processData: false,
		contentType: false
	}).done(function(response){
		let savedName = response.savedName;
		$("<input>").attr({
			type: "hidden",
			name: "filename",
			value: savedName
		}).appendTo($profileForm);
		input.remove();
		
		console.log(savedName);
		console.log($profileForm);
		console.log("이미지 업로드 성공")
		
		$profileForm.submit();
		
	}).fail(function(){
		console.log("이미지 업로드 실패")
	})
})

input.addEventListener('change', function (e) {
	let files = e.target.files;
	let size = input.files[0].size / 1024 / 1024;
	if(size > 30) {
		alert("이미지 파일 크기가 너무 큽니다. 30MB 이하의 파일을 업로드 해주세요.");
		input.value = "";
		return;
	}
	let done = function (url) {
	input.value = '';
	image.src = url;
	$modal.modal('show');
	};
	let reader;
	let file;

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

$modal.on('shown.bs.modal', function () {
	cropper = new Cropper(image, {
		aspectRatio: 1,
		viewMode: 1,
		minContainerHeight: 600,
		cropBoxResizable: false,
		dragMode: 'move',
		background: false
	});
}).on('hidden.bs.modal', function () {
	cropper.destroy();
	cropper = null;
});

document.getElementById('crop').addEventListener('click', function () {
	let initialAvatarURL;
	let canvas;

	$modal.modal('hide');

	if (cropper) {
		canvas = cropper.getCroppedCanvas({
			width: 160,
			height: 160,
		});
		initialAvatarURL = avatar.src;
		avatar.src = canvas.toDataURL();
		// canvas.toBlob(function (blob) {
			// let formData = new FormData();
			// formData.append('avatar', blob, 'avatar.jpg');


			// $.ajax('https://jsonplaceholder.typicode.com/posts', {
			// 	method: 'POST',
			// 	data: formData,
			// 	processData: false,
			// 	contentType: false,

			// 	xhr: function () {
			// 		let xhr = new XMLHttpRequest();

			// 		xhr.upload.onprogress = function (e) {
			// 		let percent = '0';
			// 		let percentage = '0%';

			// 		if (e.lengthComputable) {
			// 			percent = Math.round((e.loaded / e.total) * 100);
			// 			percentage = percent + '%';
			// 			$progressBar.width(percentage).attr('aria-valuenow', percent).text(percentage);
			// 			}
			// 		};
			// 		return xhr;
			// 	},
			// 	success: function () {
			// 		$alert.show().addClass('alert-success').text('Upload success');
			// 	},
			// 	error: function () {
			// 		avatar.src = initialAvatarURL;
			// 		$alert.show().addClass('alert-warning').text('Upload error');
			// 	},
			// 	complete: function () {
			// 		$progress.hide();
			// 	},
			// });
		// });
	}
});

