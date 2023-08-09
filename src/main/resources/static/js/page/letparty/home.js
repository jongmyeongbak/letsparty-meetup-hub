$(function() {
    $("#letparty-category").on('click', 'a', function(event) { 
        
        event.preventDefault();
    
        $('.nav-link.active').removeClass("active");
        $(this).addClass("active");
           
    })
})