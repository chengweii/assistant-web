window.assistant = {
	requestData : function(requestContent, callBack) {
		requestContent.originType = "1";
		var content = JSON.stringify(requestContent);
		$.post("request.htm", {
			requestContent : content
		}, function(data) {
			callBack(data);
		});
	},
	requestDataBatch : function(requestContentArray) {
		for ( var index in requestContentArray) {
			assistant.requestData({
				requestContent : requestContentArray[index].requestContent
			}, function(data) {
				setTimeout(function() {
					if ($(".MainAssistant-container").size() > 0) {
						$(".MainAssistant-container").remove();
					}
					$("#main-content").append(assistant.bindAnimate(data));
				}, 1500);
			});
		}
	},
	bindAnimate : function(data) {
		var animate = $(data).attr("animate");
		var newData = data;
		if (animate) {
			newData = $(data).addClass("animated").css("animation-name",
					"bounceInDown");
		}
		assistant.scrollBottom($("body"));
		return newData;
	},
	scrollBottom : function(container) {
		container.animate({
			scrollTop : container[0].scrollHeight
		}, 1000);
	},
	scrollTop : function(container) {
		container.animate({
			scrollTop : 0
		}, 200);
	},
	getHome : function() {
		assistant.requestData({
			requestContent : "MyAssistant"
		}, function(data) {
			$("#main-content").html(assistant.bindAnimate(data));
		});
	},
	keepAssistantContainerUnique : function(styleClass) {
		var size = $("." + styleClass).size();
		$("." + styleClass).each(function(index, item) {
			if (index < size - 1) {
				$(item).remove();
			}
		});
	},
	appendContainer : function(data, styleClass) {
		$("." + styleClass).html(data);
	},
	modal : {
		showDialog : function(params) {
			if (params.title)
				$('#modalDefault .modal-title').html(params.title);
			if (params.okText)
				$('#modalDefault .btn-ok').html(params.okText);
			if (params.cancelText)
				$('#modalDefault .btn-cancel').html(params.cancelText);
			if (params.content)
				$('#modalDefault .modal-body').html(params.content);
			if (params.height)
				$('#modalDefault .modal-body').css("height",
						params.height + "px");
			if (params.width && $("body").width() > 768)
				$('#modalDefault .modal-dialog').css("width",
						params.width + "px");
			if (params.modalType == '1') {
				$('#modalDefault .modal-footer').hide();
				if (params.hideCallback)
					$('#modalDefault').on('hidden.bs.modal',
							params.hideCallback);
			} else if (params.modalType == '2') {
				if (params.cancelCallback)
					$('#modalDefault .btn-cancel').unbind().click(
							params.cancelCallback);
				if (params.okCallback)
					$('#modalDefault .btn-ok').unbind()
							.click(params.okCallback);
			}

			$('#modalDefault').modal('show');
		},
		alert : function(msg, hideCallback) {
			assistant.modal.showDialog({
				modalType : 1,
				content : msg,
				hideCallback : hideCallback,
				width : 200,
				height : 50
			});
		},
		confirm : function(msg, okCallback, cancelCallback) {
			assistant.modal.showDialog({
				modalType : 2,
				content : msg,
				okCallback : okCallback,
				cancelCallback : cancelCallback,
				width : 200,
				height : 50
			});
		}
	}
};

$(document).ready(
		function() {

			setTimeout("assistant.getHome();", 200);

			$("#main-search-input").keyup(
					function(event) {
						var input = $(this);
						if (event.keyCode == 13) {
							var value = input.val();
							if (!value || $.trim(value) == '') {
								assistant.modal.alert(
										"Please say something at least.",
										function() {
											input.val("");
											input.focus();
										});
								return;
							}
							assistant.requestData({
								requestContent : value
							}, function(data) {
								if ($(".MainAssistant-container").size() > 0) {
									$(".MainAssistant-container").remove();
								}
								$("#main-content").append(
										assistant.bindAnimate(data));
								input.val("");
							});
						}
					});
			
			$("body").dblclick(function(){
				assistant.scrollTop($("body"));
			});

		});