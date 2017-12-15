$(function() {
	$.get("/menu", function(data) {
		$("#menuHodler").html(data);
	});
});