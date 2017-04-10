function requestData(requestContent, callBack) {
	var content = JSON.stringify(requestContent);
	$.post("request.htm", {
		requestContent : content
	}, function(data) {
		callBack(data);
	});
}

function getHome() {
	requestData({
		originType : "1",
		requestContent : "管家"
	}, function(data) {
		$("#main-content").html(data);
	});
}

$(document).ready(function() {

	setTimeout("getHome();", 2000);

	$("#main-search-input").keyup(function(event) {
		var input = $(this);
		if (event.keyCode == 13) {
			requestData({
				originType : "1",
				requestContent : input.val()
			}, function(data) {
				$("#main-content").append(data);
				input.val("");
			});
		}
	});

});