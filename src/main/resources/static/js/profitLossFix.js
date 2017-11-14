$(function() {
	$(".profitLoss").each(function() {
		var valueHtml = $(this).html();
		
		var fixedNrStr = valueHtml.replace(/€ /i, "");
		fixedNrStr = fixedNrStr.replace(/,/i, ".");
		
		
		var fixedNrInt = parseInt(fixedNrStr);
		
		if(fixedNrInt < 0){
			$(this).css('color', 'red');
		}else{
			$(this).css('color', '#33cc33');
		}
	});
});