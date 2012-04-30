function _post(url) {_http(url, 'POST')}
function _get(url) {_http(url, 'GET')}
function _put(url) {_http(url, 'PUT')}
function _delete(url) {_http(url, 'DELETE')}

function _http(url, type) {
    $.ajax({
        url: url,
        type: type,
        cache: false,
        success: function(html){
            document.write(html);
        }
    });    
}