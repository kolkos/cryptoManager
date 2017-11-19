$(function() {

  var sortBy = $('#sortBy').val();
  var direction = $('#direction').val();
  
  
  // loop through the button holders
  $(".buttonHolder").each(function() {
	var columnName = $(this).attr('class').split(' ')[1];
    var button = "";
    
    if(columnName == sortBy){
    	// sorted on this column
      // now check the direction
      if(direction == "ASC"){
      	button = "<a href='?page=1&sortBy=" + sortBy + "&direction=DESC'>";
        button += "<i class='fa fa-sort-desc' style='font-size:24px;color:white;'></i>";
        button += "</a>";
      }else{
      	button = "<a href='?page=1&sortBy=" + sortBy + "&direction=ASC'>";
        button += "<i class='fa fa-sort-asc' style='font-size:24px;color:white;'></i>";
        button += "</a>";
      }
      
    }else{
    	  button = "<a href='?page=1&sortBy=" + columnName + "&direction=ASC'>";
      button += "<i class='fa fa-sort' style='font-size:24px;color:white;'></i>";
      button += "</a>";
    }
    $(this).html(button);
    
  });
  
});