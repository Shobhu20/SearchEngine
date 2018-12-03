var prefixCollection = [ "jalaj", "cash meet singh", "shobhu25", "pokemon"];
$(document).ready(function() {
	$("input").keyup(function() {
		getSuggestion();
	});

});

function getSuggestion() {
	var xhttp;
	
	xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			prefixCollection = $.parseJSON(xhttp.responseText);
			$('#inputValue').autocomplete({
				source : prefixCollection
			});}};
	var str = $('#inputValue').val();
	xhttp.open("GET", "searchController?action=autocomplete&searchStr=" + str, true);
	xhttp.send();
}

function getTopUrls() {
	var xhttp;
	
	xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			console.log(xhttp.responseText);
			console.log($.parseJSON(xhttp.responseText));
			var createTable = "";

			if ($.parseJSON(xhttp.responseText).length > 0) {
				
				$.each($.parseJSON(xhttp.responseText), function(index, value) {
					createTable += "<tr><td  style=\"text-align: left;\"><a href = \"" + value
							+ "\">" + value + "</td></tr>";
					console.log(createTable);
				});
				document.getElementById("solutionBar").style.display = "block";
				$("#test").replaceWith("<div id = \"test\" style=\"width:100%;\"></div>");
				$("#test").append(
								"<table class=\"mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp\" style=\"width:100%;\"><thead><tr><th style=\"text-align: center;\">Result(s) Found:</th></tr></thead><tbody>"
										+ createTable + "</tbody></table>");
			} else {
				
				getrecommendedWord();
				
			}
		}
	};
	var str = $('#inputValue').val();
	xhttp.open("GET", "searchController?action=getTopUrl&searchStr=" + str, true);
	xhttp.send();

}

function getrecommendedWord() {
	var xhttp;
	console.log("Calling get getRecommendeWord!!!!");
	xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			console.log($.parseJSON(xhttp.responseText));
			var createTable = "";

			
			console.log(xhttp.responseText);
			if ($.parseJSON(xhttp.responseText).length > 0) {
				
				$.each($.parseJSON(xhttp.responseText), function(index, value) {
					createTable += "<tr><td style=\"text-align: left;\">" + value + "</td></tr>";
					console.log(createTable);
				});
				document.getElementById("solutionBar").style.display = "block";
				$("#test").replaceWith("<div id = \"test\" style=\"width:100%;\"></div>");
				$("#test")
						.append(
								"<table class=\"mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp\" style=\"width:100%;\"><thead><tr><th style=\"text-align: center;\">No results found. Did you mean this ?</th></tr></thead><tbody>"
										+ createTable + "</tbody></table>");
			} else {
				document.getElementById("solutionBar").style.display = "block";
				$("#test").replaceWith("<div id = \"test\" style=\"width:100%;\"></div>");
				$("#test")
						.append(
								"<table class=\"mdl-data-table mdl-js-data-table mdl-data-table--selectable mdl-shadow--2dp\" style=\"width:100%;\"><thead><tr><th style=\"text-align: center;\">No results found</th></tr></thead></table>");
			}
		}
	};
	var str = $('#inputValue').val();
	xhttp.open("GET", "searchController?action=getWordSuggestion&searchStr=" + str, true);
	xhttp.send();

}
