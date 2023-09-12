$("#search").click(function(){
	$("#search-party").submit();
})

$(".noti-outside").click(function(){
    close($(this).parent().siblings().children("div"));
    if (!$(this).children().hasClass("click")) {
        $(".noti-outside").children().removeClass("click");
        $(this).children("i").addClass("click");
        open($(this).next());
    } else {
        $(".noti-outside").children().removeClass("click");
        close($(this).next());
    }
})
let close = function(el) {
    el.removeClass("block").addClass("none");
}
let open = function(el) {
    el.removeClass("none").addClass("block");
}

$('html').click(function(e) {   
    if(!$(e.target).hasClass("noti")) {
        close($(".news-container"));
        close($(".message-container"));
        $(".noti-outside").children("i").removeClass("click");
    }
});

function openPopup(url) {
  let width = 400;
  let height = 644;
  let left = (window.innerWidth - width) / 2 + window.screenX;
  let top = (window.innerHeight - height) / 2 + window.screenY;

  let popup = window.open(
    url,
    "_blank",
    "width=" + width + ", height=" + height + ", left=" + left + ", top=" + top
  );

  if (popup) {
    popup.focus();
  } else {
    alert("팝업 차단을 해제해 주세요.");
  }
}