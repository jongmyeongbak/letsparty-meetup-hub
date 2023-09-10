$(document).ready(function() {
	$(".kick-button").on('click', function(e) {
        e.preventDefault();
        if (confirm("멤버를 탈퇴시키겠습니까?")) {
            kickUser($(this));
        }
    });
    function kickUser(buttonEl) {
        let userId = buttonEl.closest('form').find('input[name="userId"]').val();
        let partyNo = $("#partyNo").val();
        let userItem = buttonEl.closest('.user-item');

        $.ajax({
            url: `/party/${partyNo}/setting/kick`,
            type: 'POST',
            data: { userId: userId }
        }).done(function(response) {
            if (response.status === "success") {
                userItem.remove();
                $("h5 span").text(response.currentMemberCount);
				// 성공시에도 알림참을 띄울 수 있지만 지금은 실패시에만 창 띄움.            
                /*alert(response.message);*/
            } else if (response.status === "error") {
                alert(response.message);
            }
        }).fail(function(jqXHR) {
            if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
                alert(jqXHR.responseJSON.message);
            } else {
                alert("멤버 탈퇴 처리 중 오류가 발생했습니다.");
            }
        });
    }
});
