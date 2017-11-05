$(function() {
	// first add the menuHolder div
	$("body").prepend("<div class='header' id='header'>Loading header...</div>");
	// now get the content
	$.get("/header.html", function(data) {
		$("#header").html(data);
	});
});