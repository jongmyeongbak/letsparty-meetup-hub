let $openImage = $("#openimage");
let $openVideo = $("#openvideo");
let $imageInput = $("#imageInput");
let $videoInput = $("#videoInput");
let $imageContainer = $(".image-container")
let $videoContainer = $(".video-container")
let $partyPostForm = $("#party-post-form")
let num = 0;
$openImage.on("click", function() {
	$imageInput.click();
})
$openVideo.on("click", function() {
	$videoInput.click();
})

// 이미지 추가하고 표시하기
function addAndDisplayImage(file, savedName, num) {
    let newHTML = `
        <div class="image-item image${num}">
            <img src="${URL.createObjectURL(file)}" alt="">
            <i class="fa-regular fa-circle-xmark" onclick="deleteImage(${num})"></i>
        </div>`;
    
    $imageContainer.append(newHTML);
    
    $("<input>").attr({
        type: "hidden",
        name: "imageName[]",
        class: "image" + num
    }).val(savedName).appendTo($partyPostForm);
    
    console.log("이미지 업로드 성공");
}

// 동영상 추가하고 표시하기
function addAndDisplayVideo(file, savedName, num) {
    let newHTML = `
    	<div class="video-item video${num}">
            <video src="${URL.createObjectURL(file)}" alt=""></video>
            <i class="fa-solid fa-circle-play"></i>
            <i class="cancel fa-regular fa-circle-xmark " onclick=deleteVideo(${num})></i>
        </div>`
    
    $videoContainer.append(newHTML);
    
    $("<input>").attr({
        type: "hidden",
        name: "videoName[]",
        class: "video" + num
    }).val(savedName).appendTo($partyPostForm);
    
    console.log("동영상 업로드 성공");
}

// 이미지 서버로 보내고 nanoId 발급
async function uploadImageAndGetSavedName(file) {
    let formdata = new FormData();
    formdata.append("file", file);
    
    return new Promise(function(resolve, reject) {
        $.ajax({
            url: "/upload/media",
            type: 'POST',
            data: formdata,
            processData: false,
            contentType: false
        }).done(function(response) {
            let savedName = response.savedName;
            resolve(savedName);
        }).fail(function() {
            reject();
        });
    });
}

// 동영상 서버로 보내고 nanoId 발급
async function uploadVideoAndGetSavedName(file) {
    let formdata = new FormData();
    formdata.append("file", file);
    
    return new Promise(function(resolve, reject) {
        $.ajax({
            url: "/upload/media",
            type: 'POST',
            data: formdata,
            processData: false,
            contentType: false
        }).done(function(response) {
            let savedName = response.savedName;
            resolve(savedName);
        }).fail(function() {
            reject();
        });
    });
}

$imageInput.on("change", async function(e) {
	let files = Array.from(e.target.files)
	console.log(files);
	if (files && files.length > 0) {
		for (let i = 0; i < files.length; i++) {
			let file = files[i];
			if (file.size >= 30*1024*1024) {
				alert("파일 크기는 30MB를 초과할 수 없습니다.");
				return;
			}
			if ($(".image-item").length > 8){
				alert("이미지는 최대 9개까지만 추가 가능합니다.");
				return;
			}
			
			try {
				let savedName = await uploadImageAndGetSavedName(file);
				addAndDisplayImage(file, savedName, num);
				num++;
			} catch(error) {
				console.log("이미지 업로드 실패");
			}
		}
	}
})

$videoInput.on("change", async function(e) {
	let files = Array.from(e.target.files)
	console.log(files);
	
	if (files && files.length > 0) {
		for (let i = 0; i < files.length; i++) {
			let file = files[i];
			if (file.size >= 30*1024*1024) {
				alert("파일 크기는 30MB를 초과할 수 없습니다.");
				return;
			}
			if ($(".video-item").length > 2){
				alert("동영상은 최대 3개까지만 추가 가능합니다.");
				return;
			}
			try {
				let savedName = await uploadVideoAndGetSavedName(file);
				console.log("비디오 업로드 성공");
				console.log(num);
				console.log(savedName);
				addAndDisplayVideo(file, savedName, num);
				num++;
			} catch(error){
				console.log("비디오 업로드 실패");
			}
		}
	}
})
function deleteImage(i) {
	if (confirm("이미지를 삭제하시겠습니까?")) {
		$(".image" + i).remove();
	}
}

function deleteVideo(i) {
	if (confirm("동영상을 삭제하시겠습니까?")) {
		$(".video" + i).remove();
	}
}

let urlParams = new URLSearchParams(window.location.search);
const showAlert = urlParams.get('req');
console.log(showAlert);
if (showAlert === 'fail') {
    alert('파티의 가입조건과 맞지 않습니다.');
}