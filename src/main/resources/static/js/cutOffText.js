$(function() {
    	$(".table tbody tr td.cutoff").each(function() {
    		var valueHtml = $(this).html();
    		
    		if (valueHtml.length > 20){
    			
			var cutOffText = valueHtml.substr(0,17) + '...';
			var newValue = '<span title="' + valueHtml + '">' + cutOffText + '</span>';

			$(this).html(newValue);
    		}
    	});
});