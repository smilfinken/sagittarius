/**
 * JS Namespace for managing a page/element with auto-scroll capability.
 * 
 * A known issue is render movement for FF, less then 2 will show weird behavior - Set to two, and adjust speed accordingly!
 * 
 * */

var sagittarius = sagittarius || {};
sagittarius.autoscroll = (function() {
	var element;
	var dir = 1;
	var movement = ($.browser.mozilla) ? 2 : 1;
	var scrollDelay = ($.browser.mozilla) ? 80 : 40;
	var stopped = false;
	
	function _setup(_element) {
		element = _element;
	}
	
	function _pageScroll() {
		var el = (element != undefined) ? element : $(window);
		var b4 = el.scrollTop();
		
		var changeDirection = false;
		if (!stopped) {
			el.scrollTop( b4 + movement*dir );
			changeDirection = b4==el.scrollTop();
			dir = changeDirection ? (-1*dir) : dir;
		}
		
		setTimeout(function(){_pageScroll();}, ( changeDirection ? 2000: scrollDelay)); // Call this method again with a delay of either 2000 or SCROLLDELAY
	}
	
	function _scrollSpeed(slower) {
		scrollDelay += slower == true ? 10 : -10;
	}
	
	function _stopScroll(stop) {
		stopped = stop;
	}

    return {
        //someProperty: 'prop value',
    	
    	// If not used, scrolling element will default to Browser Window
        setup: function(element) {
            setup(element);
        },
        
    	// Call ONCE to start scroll of page/element
        start: function() {
            _pageScroll();
        },
        
        // Call with TRUE to SLOW down and FALSE to SPEED UP.
        speedUp: function() {
            _scrollSpeed(false);
        },
        
        // Call with TRUE to SLOW down and FALSE to SPEED UP.
        speedDown: function() {
        	_scrollSpeed(true);
        },
        
        // Stop scrolling
        pause: function() {
        	_stopScroll(true);
        },

        // Start scrolling        
        resume: function() {
        	_stopScroll(false);
        }
    };
})();