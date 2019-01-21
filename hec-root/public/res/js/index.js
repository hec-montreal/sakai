(function() {
	var urlHome = "/portal";
	var useMockMessage = false;
	var photoCount = 4;

	// Execute get request
	var get = function(url, callback) {
		if (url.indexOf("announcement") >= 0 && useMockMessage) {
			callback(mockMessage());
		} else {
			$.getJSON(url, callback);
		}
	};

	// Show dialog
	var showMessage = function (title, body) {
		$('.message .title').html(title);
		$('.message .body').html(body);
		$('.message').show();
	};

	// Message d'acceuil
	var getMessage = function() {
		get("/direct/announcement/motd.json?n=1&" + new Date().getTime(), function(data) {
			if (data && data.announcement_collection.length > 0) {
				var announcement = data.announcement_collection[0];

				showMessage(announcement.title, announcement.body);
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

	var showLogin = function () {
		$('.loading').hide();
		$('.login').fadeIn();
	};

	// Mock un message
	var mockMessage = function() {
		var body = 'This is a sample message. ';

		for(var i = 0; i < 5; i++) {
			body += body;
		}

		return {
			announcement_collection: [{
				body: body,
				title: 'Une annonce'
			}]
		};
	};

	// Page init
	var init = function() {
		getMessage();

		checkSession();

		// Choose a picture at random
		var picId = Math.floor(Math.random() * photoCount) + 1;
		var url = '/access/content/public/login-' + picId + '.png';

		$('.photo').css('background-image', 'url(' + url + ')');
	};

	$(document).ready(function () {
		init();
	});	

	window.showMessage = showMessage;
})();
