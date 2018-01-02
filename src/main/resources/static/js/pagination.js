$(function() {
		// hardcoded values
		var resultsPerPage = 25;
		var maxPages = 10;

		// get the current sorting options
		var sortBy = $('#sortBy').val();
		var direction = $('#direction').val();
		var search = $('#search').val();

	    // get the current page number
	    var curPageNr = parseInt($('#page').val());
	    // get the number of results
	    var nrOfResults = $('#nrOfResults').val();

	    // calculate the number of pages
	    var nrOfPages = Math.ceil(nrOfResults / resultsPerPage);
	    // form base href
	    var href = '&sortBy=' + sortBy + '&direction=' + direction + '&search=' + search;

		// now generate the new html
		var newHtml = "";
		// first the previous button
		var previousPage = 1;
		var liClass = 'class="disabled" ';
		if(curPageNr > 1){
			previousPage = curPageNr - 1;
			liClass = '';
		}
		
		newHtml += '<li ' + liClass + '><a href="?page=' + previousPage + href + '" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';

		// check if the number of needed pages is smaller than the max number of pages
		if(nrOfPages < maxPages){
			// number is smaller, just generate all the pages
			for(var i = 1; i <= nrOfPages; i++){

				var curPageClass = '';
				if(i == curPageNr){
					curPageClass = 'class="active" ';
				}
				
				// <li><a href="#">1</a></li>
				newHtml += '<li ' + curPageClass + '><a href="?page=' + i + href + '">' + i + '</a></li>';
			}
		}else{
			// the number of pages is greater than the max number of pages, shorten the list
			// first the first x number of pages (depending on the max pages variable)
			for(var i = 1; i <= (maxPages / 2); i++){
				var curPageClass = '';
				if(i == curPageNr){
					curPageClass = 'class="active" ';
				}
				
				newHtml += '<li ' + curPageClass + '><a href="?page=' + i + href + '">' + i + '</a></li>';
			}
			// add a seperator
			newHtml += '<li class="disabled"><a href="#">...</a></li>';

			// now the last x number of pages
			var tempPageNr = nrOfPages - (maxPages / 2) + 1;
			for(var i = nrOfPages; i > nrOfPages - (maxPages / 2); i--) {
				var curPageClass = '';
				if(tempPageNr == curPageNr){
					curPageClass = 'class="active" ';
				}

				newHtml += '<li ' + curPageClass + '><a href="?page=' + tempPageNr + href + '">' + tempPageNr + '</a></li>';
				tempPageNr++;
			}
		}

		// calculate the next page 
		var nextPage = curPageNr + 1;
		// check if the next page isn't outside the range
		liClass = "";
		if(nextPage > nrOfPages){
			// the next page is greater than the nr of pages, use the current page nr
			nextPage = curPageNr;
			liClass = 'class="disabled" ';
		}
		
		newHtml += '<li ' + liClass + '><a href="?page=' + nextPage + href + '" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
		
		
		// add it to the containers
		$('#paginationTop').html(newHtml);
		$('#paginationBottom').html(newHtml);
		
});