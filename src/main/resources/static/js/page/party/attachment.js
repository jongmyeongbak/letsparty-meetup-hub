var pathSegments = window.location.href.split('/');
	        var partyNoIndex = pathSegments.indexOf('party') + 1;
	        var partyId = pathSegments[partyNoIndex];
		    function loadContent(partyId, contentPath) {
		        var fullUrl = "/party/" + partyId + "/" + contentPath;
		        $.ajax({
		            url: fullUrl,
		            method: "GET",
		            dataType: "html",
		            success: function(data) {
		                $(".attachment-content").html(data);
		            },
		            error: function() {
		                console.log("컨텐츠를 가져오는 중 오류가 발생했습니다.");
		            }
		        });
		    }
		
		    $(document).ready(function() {
		        $("#albumButton").click(function() {
		            var contentPath = "attachment";
		            var newUrl = "/party/" + partyId + "/" + contentPath;
		            window.location.href = newUrl; // 페이지 리다이렉트
		        });
		
		        $("#pollButton").click(function() {
		            loadContent(partyId, "poll"); // 예시 파티 ID와 컨텐츠 경로
		        });
		
		        $("#fileButton").click(function() {
		            loadContent(partyId, "file"); // 예시 파티 ID와 컨텐츠 경로
		        });
		    });