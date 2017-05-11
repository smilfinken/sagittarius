(function (window) {
    function addCompetitor(teamId) {
        var element = document.getElementById("competitor");
        if (!!element) {
            window.location.href = "/team/addCompetitor/" + teamId + "/" + element.options[element.selectedIndex].value;
        }
    }

    function toggleFoldedSection(section) {
        var element = document.getElementById(section)
        if (!!element) {
            element.classList.toggle("folded");
        }
    }

	window.utils = {
		addCompetitor: addCompetitor,
		toggleFoldedSection: toggleFoldedSection
	};
})(window);
