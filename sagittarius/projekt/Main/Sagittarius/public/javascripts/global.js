$("html, body").ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
	$(this).html( "Triggered ajaxError handler.<br/>" + jqXHR.responseText );
});