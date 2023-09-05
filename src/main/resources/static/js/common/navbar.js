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