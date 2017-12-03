$(function() {
	$.get("/menu.html", function(data) {
		$("#menuHodler").html(data);
	});
});