$(function() {
	// first add the menuHolder div
	$("body").prepend("<div class='menuHolder' id='menuHolder'>Menu loading...</div>");
	// now get the content
	$.get("/menu.html", function(data) {
		$("#menuHolder").html(data);
	});
});