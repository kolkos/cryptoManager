$(function() {
	$('#showHideFilter').click(function() {
		var link = $(this);
		$('#filter').slideToggle('fast', function() {
			if ($(this).is(':visible')) {
				link.text('Hide filter');
			} else {
				link.text('Show filter');
			}
		});
	});
});