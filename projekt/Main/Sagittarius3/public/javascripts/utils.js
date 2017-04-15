(function (window) {
    function addCompetitor(teamId) {
        var element = document.getElementById("competitor");
        if (!!element) {
            window.location.href = "/team/addCompetitor/" + teamId + "/" + element.options[element.selectedIndex].value;
        }
    }

	window.utils = {
		addCompetitor: addCompetitor
	};
})(window);
