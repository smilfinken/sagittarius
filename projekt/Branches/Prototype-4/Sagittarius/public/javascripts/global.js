$("html, body").ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
	$(this).html( "Triggered ajaxError handler.<br/>" + jqXHR.responseText );
});

$(window).bind('resize', function() {
	atResizing();	
}).trigger('resize');


function atResizing() {

	// Text size
	var preferredWidth = 1024;
	var displayWidth = $(window).width();
	var widthPercentage = displayWidth / preferredWidth;
	var newFontSize = Math.floor(40 * widthPercentage) - 1;
	$('.tilted').css("font-size", newFontSize+'px');
	
	// Text position
	var displayHeight = $(window).height();
	var textHeight = $('.tilted').height();
	var ratioPercent = textHeight / displayHeight;
	ratioPercent = Math.floor(ratioPercent*2*100);
	var presentTop = superTop; // Global, defined below
	presentTop = (''+presentTop).substring(0, 2);
	var top = Math.floor(Number(presentTop)+Number(ratioPercent))+'%'
	$('.tilted').css('top', top);
};

var superTop;
$(document).ready(function(){
	superTop = '50%';
	atResizing();
});