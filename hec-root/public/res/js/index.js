(function() {
	var urlHome = "/portal";
	var useMockMessage = false;
	var spinner;

	// Execute get request
	var get = function(url, callback) {
		if (url.indexOf("announcement") >= 0 && useMockMessage) {
			callback(mockMessage());
		} else {
			$.getJSON(url, callback);
		}
	};

	// Message d'acceuil
	var getMessage = function() {
		get("/direct/announcement/motd.json?n=1", function(data) {
			if (data && data.announcement_collection.length > 0) {
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

				$("#submit").focus();
			}
		});
	};

	// Session
	var checkSession = function() {
		get("/direct/user/current.json?" + new Date().getTime(), function(data) {
			if (data && data.id && data.id.length > 0) {
				goToHome();
			} else {
				showLogin();
			}
		});
	};

	// Redirige à sakai
	var goToHome = function() {
		window.location.replace(urlHome);
	};

	// Affiche le login
	var showLogin = function() {
		if (spinner) {
			spinner.stop();
		}

		$("#loading").hide();
		$("#loginform").fadeIn();
	};

	// Init le loading
	var initLoading = function() {
		spinner = new Spinner({
			lines: 12,
			top: "16%",
			color: "#fff",
			scale: 2
		}).spin(document.getElementById("spinner"));
	};

	// Mock un message
	var mockMessage = function() {
		return {
			announcement_collection: [{
				body: "Some message body",
				title: "Some message title"
			}]
		};
	};

	// Page init
	var init = function() {
		initLoading();

		getMessage();

		checkSession();
	};

	init();

})();