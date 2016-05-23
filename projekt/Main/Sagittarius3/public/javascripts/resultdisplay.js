(function (window) {
    var _pause = 2000;
    var _delay = 10;
    var _speed = 1;
    var _direction = 1;
    var _pageHeight = 0;
    var _clientHeight = 0;

    function startScroll() {
        _pageHeight = Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);
        _clientHeight = window.innerHeight;
        _direction = window.scrollY == 0 ? 1 : -1;
        window.setTimeout(_scroll, _pause);
    }

    function _scroll() {
        window.scrollBy(0, _speed * _direction);

        if (window.scrollY >= _pageHeight - _clientHeight) {
            _direction = -1;
            window.setTimeout(_scroll, _pause);
            return;
        } else if (window.scrollY <= 0) {
            location.reload();
            _direction = 1;
            window.setTimeout(_scroll, _pause);
            return;
        } else {
            window.setTimeout(_scroll, _delay);
        }
    };

	window.scroller = {
		startScroll: startScroll
	};
})(window);
