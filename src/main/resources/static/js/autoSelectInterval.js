$(function() {
	$('#lastHours').on('change', function() {
		var value = parseInt(this.value);
		
		var intervalValue = 0;
		switch(value) {
	    		case 1:
	    		case 2:
	    			intervalValue = 5;
	    			break;
	    		case 3:
	    		case 4:
	    		case 5:
	    			intervalValue = 10;
	        		break;
	    		case 24:
	    		case 48:	
	    			intervalValue = 60;
	        		break;
	    		case 168:
	    		case 336:
	    		case 720:	
	    			intervalValue = 1440;
	    			break;
	    		default:
	    			intervalValue = 5;
		}
		$("#intervalInMinutes").val(intervalValue);
	})
});