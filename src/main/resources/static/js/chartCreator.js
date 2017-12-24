$(function() {
	$('#generateChartButton').click(function(){
		
		// get values from form
		var portfolioId = $('#portfolioId').val();
		var chartType = $('#chartType').val();
		var lastHours = $('#lastHours').val();
		var intervalInMinutes = $('#intervalInMinutes').val();
		
		var url = "";
		
		switch(chartType) {
			case "areaChart_portfolio_value":
				// load the area chart
				url = "/portfolio/areachart";
				break;
			case "pieChart_portfolio_distribution":
				url = "/portfolio/piechart";
				break;
			case "lineChart_portfolio_roi":
				url = "/portfolio/linechart";
				break;
		}
		
		// temporary show waiting message
		$('#chartHolder').html("<p>The chart is being generated. Please wait...</p>");
		
		$.get(url, 
			{
				portfolioId: portfolioId,
				lastHours: lastHours,
				intervalInMinutes: intervalInMinutes
			},
			function(data){
				$('#chartHolder').html(data);
			}
		);
	});
	
	$('#chartType').on('change', function() {
		var chartType = $('#chartType').val();
		if (chartType == "pieChart_portfolio_distribution"){
			$('#lastHours').prop('disabled', 'disabled');
			$('#intervalInMinutes').prop('disabled', 'disabled');
			$('#lastHours').attr('style', 'background-color: grey;');
			$('#intervalInMinutes').attr('style', 'background-color: grey;');
		} else {
			$('#lastHours').prop('disabled', false);
			$('#intervalInMinutes').prop('disabled', false);
			$('#lastHours').removeAttr('style');
			$('#intervalInMinutes').removeAttr('style');
		}
	});
});