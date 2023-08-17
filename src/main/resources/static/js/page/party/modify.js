$(function() {
    let randomImageIndex = Math.floor(Math.random() * 7) + 1;
    // 페이지 로딩 완료 후 실행 로드 되자마자 랜덤 이미지를 선택하게 함
    let defaultImageSrc = $("#coverlist img:eq("+randomImageIndex+")").attr('src');  // coverlist 내의 랜덤index에 해당하는 이미지를 가져옴
    $('#mainCover img').attr('src', defaultImageSrc);  // mainCover의 img 태그의 src에 랜덤으로 선택된 이미지의 src를 설정

    // 사용자가 사이트에서 기본으로 제공되는 이미지를 클릭했을 때의 스크립트
    $("#coverlist img:not(#addPhotoSpan img)").click(function() {
        // 마우스포인터가 옮겨가 썸네일 이미지 엘리먼트의 src 속성값을 조회한다.
        let imagepath = $(this).attr("src");
       	let saveImageName = $(this).attr("name");
        // 위에서 조회한 이미지 경로를 큰 이미지로 표현하는 src의 속성값으로 지정한다.
        $("#mainCover img").attr("src", imagepath);

        // preview를 보이게 하고, result를 숨긴다.
        $("#preview").show();
        $("#result").hide();
        
        // editedImage 대신 defaultImage를 입력 필드에 설정
    	$("#defaultImage").val(saveImageName);
    })

    // 사용자가 사진추가 이미지를 눌러 로컬에서 이미지를 선택할 수 있게 하는 스크립트
    $('#addPhotoSpan img').on('click', function() {
        $('#input').click();
    });

	var $image = $("#image");
  	var $input = $("#input");
  	var $result = $("#result");
  	var $cropbutton = $("#cropbutton");

  	var $alert = $('.alert');
  	var $modal = $('#modal');
 	var cropper;

	$('[data-toggle="tooltip"]').tooltip();

	$input.on('change', function (e) {
		var files = e.target.files;
		var done = function (url) {
        
		$input.val("");
		$image.attr("src", url);
     	$alert.hide();
     	$modal.modal('show');
    };
    
	var reader;
    var file;
	
	// 이미지 미리보기 
    if (files && files.length > 0) {
      file = files[0];

      if (URL) {
        done(URL.createObjectURL(file));
      } else if (FileReader) {
        reader = new FileReader();
        reader.onload = function (e) {
          done(reader.result);
        };
        reader.readAsDataURL(file);
      }
    }
  });

	// 사진 편집 모달
	$modal.on('shown.bs.modal', function () {
		cropper = new Cropper(image, {
			aspectRatio: 6/5,
    		viewMode: 2,
			minContainerHeight: 600,
      		zoomable: false,
      		cropBoxResizable: false,
      		dragMode: 'move',

			data: {
        		width: 300,
        		height: 250,
			},
		});
    
		$cropbutton.on("click", function() {
			let croppedCanvas = cropper.getCroppedCanvas();
			let editedImage = croppedCanvas.toDataURL();
			
	        $result.html('');
	        $result.append(cropper.getCroppedCanvas());
	        $("#imageFile").val(editedImage);
	        $modal.modal('hide');
	        
	        // result를 보이게 하고, preview를 숨긴다.
	        $("#result").show();
	        $("#preview").hide();
			})
	    
		}).on('hidden.bs.modal', function () {
	    cropper.destroy();
	    cropper = null;
	});

	// base64 -> file 변환 코드
	function dataURLtoBlob(dataurl) {
    var base64Data = dataurl.split(',')[1];
    var byteString = atob(base64Data);
    var arrayBuffer = new ArrayBuffer(byteString.length);
    var uint8Array = new Uint8Array(arrayBuffer);
    for (var i = 0; i < byteString.length; i++) {
        uint8Array[i] = byteString.charCodeAt(i);
  	  }
    return new Blob([uint8Array], { type: 'image/png' });
	}	  
	  // 파티 생성 버튼을 눌렀을 때
	$('#btn').on('click', function(e) {
		if ($("#imageFile").val() !== "") {
			e.preventDefault();
			// 쓰이지 않는 필드 삭제
			$('#defaultImage').remove();
			
		    // hidden input에서 base64 데이터를 가져옴
			let base64Data = $("#imageFile").val();
			let formData = new FormData();
	        let blob = dataURLtoBlob(base64Data);
	        
	       	// 파일 형태, 파일 이름, 타입 지정
	        let file = new File([blob], 'image', { type: 'image/png' });
	        formData.append('file', file);
	        
	       // AJAX를 사용하여 폼 데이터 제출
			$.ajax({
		        url:"/upload/cover", // 서버 URL 실제 커버 업로드는 /upload/cover로
		        type: 'POST',
		        data: formData,
		        processData: false, // jQuery가 데이터를 처리하지 않도록 설정
		        contentType: false, // 서버로 전송될 컨텐츠 유형을 자동 설정하지 않도록 설정
			}).done(function(response) {
			    // 이미지를 먼저 S3에 업로드하고 나머지 파티 정보를 폼으로 제출하기 위해 쓰이지 않는 필드를 지운다. 
				$('#imageFile').remove();
				
				let savedName = response.savedName;
	            // 업로드 후 반환값으로 내려온 파일 이름을 저장해서 폼으로 전송한다.
		        $('<input>').attr({
		          type: 'hidden',
		          name: 'savedName',
		          value: savedName
		        }).appendTo('#party-form');
			        
	            console.log("이미지 업로드 성공!");
	   			$('#party-form').submit();
	        }).fail(function(jqXHR, textStatus, errorThrown) {
	            console.log("이미지 업로드 실패!");
		    });
		    
	    } else if ($("#defaultImage").val() !== "") {
	        // 기본 이미지를 사용하는 경우 폼 제출 방식으로 전송
	        $('#imageFile').remove();
	        $('#party-form').submit();
    	}
    })
});	
