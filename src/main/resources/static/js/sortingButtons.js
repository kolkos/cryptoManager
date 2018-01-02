$(function() {

  var sortBy = $('#sortBy').val();
  var direction = $('#direction').val();
  var search = $('#search').val();
  
  // loop through the button holders
  $(".buttonHolder").each(function() {
	var columnName = $(this).attr('class').split(' ')[1];
	var type = $(this).attr('class').split(' ')[2];
    var button = "";
    
    if (type == 'alphabet'){
    		icon = 'glyphicon glyphicon-sort-by-alphabet';
    }else if(type == 'numeric'){
    		icon = 'glyphicon glyphicon-sort-by-order';
    }else{
    		icon = 'glyphicon glyphicon-sort-by-attributes';
    }
    
    if(columnName == sortBy){
    	// sorted on this column
      // now check the direction
      if(direction == "ASC"){
      	button = "<a href='?page=1&sortBy=" + sortBy + "&direction=DESC&search=" + search + "'>";
      	button += "<span class='" + icon + "'></span>";
        button += "</a>";
      }else{
      	button = "<a href='?page=1&sortBy=" + sortBy + "&direction=ASC&search=" + search + "'>";
      	button += "<span class='" + icon + "-alt'></span>";
        button += "</a>";
      }
      
    }else{
    	  button = "<a href='?page=1&sortBy=" + columnName + "&direction=ASC&search=" + search + "'>";
      button += "<span class='glyphicon glyphicon-sort'></span>";
      button += "</a>";
    }
    $(this).html(button);
    
  });
  
});