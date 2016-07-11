(function() {
	var getMessage = function() {
		// Message d'acceuil
		$.getJSON('/direct/announcement/motd.json?n=1', function(data) {
			if (data.announcement_collection.length > 0) {
				var announcement = data.announcement_collection[0];
				$("#dialog").html(announcement.body);
				$("#dialog").attr('title', announcement.title);
				$("#dialog").dialog({
					position: {
						my: "center center",
						at: "right-175 top+135",
						of: window
					}
				});
			}
		});
	};

	getMessage();

})();

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-2831257-12']);
_gaq.push(['_trackPageview']);

(function() {
	var ga = document.createElement('script');
	ga.type = 'text/javascript';
	ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(ga, s);
})();
