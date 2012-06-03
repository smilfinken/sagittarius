if( typeof( sagittarius ) == "undefined" ) sagittarius = {};

sagittarius.AutoScrollProto = function (_jQueryElement) {
	
    this.jQueryElement = _jQueryElement;
    this.stopped = false;
    this.dir = 1;
    this.scrollDelay = ($.browser.mozilla) ? 80 : 40;
    this.movement = ($.browser.mozilla) ? 2 : 1;
}

sagittarius.AutoScrollProto.prototype.scroll = function() {
	if (this.jQueryElement == undefined || this.jQueryElement == null) return;
	var that = this;
	var b4 = this.jQueryElement.scrollTop();
	
	var changeDirection = false;
	if (!this.stopped) {
		this.jQueryElement.scrollTop( b4 + this.movement*this.dir );
		changeDirection = b4==this.jQueryElement.scrollTop();
		this.dir = changeDirection ? (-1*this.dir) : this.dir;
	}
	
	setTimeout(function(){that.scroll();}, ( changeDirection ? 2000: that.scrollDelay)); // Call this method again with a delay of either 2000 or SCROLLDELAY    
};

sagittarius.AutoScrollProto.prototype.clear = function() {
	console.log('Clearing AutoScrollProto');
	delete this.jQueryElement;
	this.jQueryElement = null;
};