$(function() {
	$(".profitLoss").each(function() {
		var valueHtml = $(this).html();
		console.log(valueHtml);
		
		var fixedNrStr = valueHtml.replace(/€ /i, "");
		fixedNrStr = fixedNrStr.replace(/,/i, ".");
		
		console.log(fixedNrStr);
		
		var fixedNrInt = parseInt(fixedNrStr);
		
		if(fixedNrInt > 0){
			$(this).css('color', '#33cc33');
		}else{
			$(this).css('color', 'red');
		}
	});
});