$(function() {
    let randomImageIndex = Math.floor(Math.random() * 7) + 1;
    // 페이지 로딩 완료 후 실행 로드 되자마자 랜덤 이미지를 선택하게 함
    let defaultImageSrc = $("#coverlist img:eq("+randomImageIndex+")").attr('src');  // coverlist 내의 랜덤index에 해당하는 이미지를 가져옴
    $('#mainCover img').attr('src', defaultImageSrc);  // mainCover의 img 태그의 src에 랜덤으로 선택된 이미지의 src를 설정
    

    // 사용자가 사이트에서 기본으로 제공되는 이미지를 클릭했을 때의 스크립트
    $("#coverlist img:not(#addPhotoSpan img)").click(function() {
        // 마우스포인터가 옮겨가 썸네일 이미지 엘리먼트의 src 속성값을 조회한다.
        let imagepath = $(this).attr("src");
        // 위에서 조회한 이미지 경로를 큰 이미지로 표현하는 src의 속성값으로 지정한다.
        $("#mainCover img").attr("src", imagepath);

        // preview를 보이게 하고, result를 숨긴다.
        $("#preview").show();
        $("#result").hide();
    })

    // 사용자가 사진추가 이미지를 눌러 로컬에서 이미지를 선택할 수 있게 하는 스크립트
    $('#addPhotoSpan img').on('click', function() {
        $('#input').click();
    });

  var avatar = document.getElementById('avatar');
  var $image = $("#image");
  var $input = $("#input");
  var $result = $("#result");
  var $cropbutton = $("#cropbutton");

  var $progress = $('.progress');
  var $progressBar = $('.progress-bar');
  var $alert = $('.alert');
  var $modal = $('#modal');
  var cropper;


  $('[data-toggle="tooltip"]').tooltip();

  $input.on('change', function (e) {
      var files = e.target.files;
      console.log(files);
      var done = function (url) {
        console.log(url)
      $input.val("");
      $image.attr("src", url);
      $alert.hide();
      $modal.modal('show');
    };
    var reader;
    var file;
    var url;

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

  $modal.on('shown.bs.modal', function () {
    cropper = new Cropper(image, {
      aspectRatio: 6/5,
      viewMode: 3,
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
        $result.html('');
        $result.append(cropper.getCroppedCanvas());
        $modal.modal('hide');
        // result를 보이게 하고, preview를 숨긴다.
        $("#result").show();
        $("#preview").hide();
    })
    
  }).on('hidden.bs.modal', function () {
    cropper.destroy();
    cropper = null;
  });

})
