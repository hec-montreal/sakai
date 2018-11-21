(function() {
	var urlHome = "/portal";
	var useMockMessage = false;
	var dialogVisible = false;

	// Execute get request
	var get = function(url, callback) {
		if (url.indexOf("announcement") >= 0 && useMockMessage) {
			callback(mockMessage());
		} else {
			$.getJSON(url, callback);
		}
	};

	// Show dialog
	var showDialog = function (title, body) {
		dialogVisible = true;

		$('.dialog .title').html(title);
		$('.dialog .body').html(body);

		$('body').css('overflow', 'hidden');

		$('.dialog-mask').fadeIn();
		$('.dialog-host').fadeIn();
	};

	var closeDialog = function () {
		dialogVisible = false;

		$('body').css('overflow', 'visible');

		$('.dialog-mask').hide();
		$('.dialog-host').hide();
	};

	// Message d'acceuil
	var getMessage = function() {
		get("/direct/announcement/motd.json?n=1&" + new Date().getTime(), function(data) {
			console.log(data);

			if (data && data.announcement_collection.length > 0) {
				var announcement = data.announcement_collection[0];

				showDialog(announcement.title, announcement.body);
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
	};

	$(document).ready(function () {
		$('.dialog .close').click(function () {
			closeDialog();
		});

		$(document).keyup(function (e) {
			if(e.key === 'Escape') {
				if(dialogVisible) {
					closeDialog();
				}
			}
		});

		init();
	});	

	window.showDialog = showDialog;
})();
