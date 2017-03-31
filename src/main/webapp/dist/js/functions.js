function requestData(requestContent, callBack) {
	var content = JSON.stringify(requestContent);
	$.post("request.htm", {
		requestContent : content
	}, function(data) {
		callBack(data);
	});
}
