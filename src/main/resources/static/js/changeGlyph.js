$(function() {
	changeGlyph(0);
});

$('#transactionType').change(function(){
	var id = $(this).val();
	changeGlyph(id);
});

function changeGlyph(transactionTypeId){
	console.log(transactionTypeId);
	var transactionType = $("#transactionType option[value='" + transactionTypeId + "']").text();
	console.log(transactionType);
	var glyph = "";
	switch(transactionType){
		case "Deposit":
			glyph = "glyphicon glyphicon-piggy-bank";
			break;
		case "Withdrawal":
			glyph = "glyphicon glyphicon-fire";
			break;
		default:
			glyph = "glyphicon glyphicon-piggy-bank";
	}

	// set the class
	$('#typeIcon').removeClass();
	$('#typeIcon').addClass(glyph);
}