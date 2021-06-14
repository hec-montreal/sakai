function showDialog(locale) {
	var dialogWidth = $(window).width()*0.8;
	var dialogHeight = $(window).height()*0.8;
	var height = dialogHeight -100;

    console.log('Locale: ' + locale);

    if (locale === 'fr_CA' || locale === 'fr-CA') {
        title = sessionStorage.getItem('titleFr');
	    acceptButton = sessionStorage.getItem('acceptButtonFr');
        $('div#codeOfConduct').html(sessionStorage.getItem('bodyFr'));
    }
    else {
        title = sessionStorage.getItem('titleEn');
	    acceptButton = sessionStorage.getItem('acceptButtonEn');
        $('div#codeOfConduct').html(sessionStorage.getItem('bodyEn'));
    }

    $('div#codeOfConduct').dialog({
        title: title,
        open: function (event, ui) {
            $( this ).dialog('option', 'height', dialogHeight);
            $( this ).dialog('option', 'width', dialogWidth);
            $( this ).dialog('option', 'position', 'top');
            $( this ).css({height:(height-50)});
        },
        close: closeDialog,
        modal:true,
        buttons:  [
            {
                text: acceptButton,
                click : sendAccept
            }
        ]
    });
}

function sendAccept() {
    if (sessionStorage.getItem('hasUserAccepted') !== 'true') {
        $.ajax({
            type: 'POST',
            url: '/direct/code_of_conduct/new.json',
            data: 'saved',
            dataType: 'json',
            contentType: 'application/json',
            failure: function failure(data){
                // TODO: internationalize
                alert('The user was not savec properly');
            }
        })
        .always(function() {
            sessionStorage.setItem('hasUserAccepted', 'true');
        });
    }
    $( 'div#codeOfConduct' ).dialog( 'close' );
}

function closeDialog() {
    if (sessionStorage.getItem('showTutorialLocationOnHide')) {
        startTutorial(sessionStorage.getItem('showTutorialLocationOnHide'));
        sessionStorage.removeItem('showTutorialLocationOnHide');
    }
}

function retrieveCodeOfConduct(locale, tutorial, showTutorialLocationOnHide) {

    return Promise.all([
        $.get("access/content/public/codeDeConduite_EN.html", (response) => sessionStorage.setItem('bodyEn', response) ),
        $.get("access/content/public/codeDeConduite_FR.html", (response) => sessionStorage.setItem('bodyFr', response) ),
        $.getJSON('/direct/code_of_conduct/code_of_conduct.json?' + new Date().getTime(), function(response){
            sessionStorage.setItem('titleEn', response.data.titleEn);
            sessionStorage.setItem('titleFr', response.data.titleFr);
            sessionStorage.setItem('acceptButtonEn', response.data.acceptButtonEn);
            sessionStorage.setItem('acceptButtonFr', response.data.acceptButtonFr);
            sessionStorage.setItem('hasUserAccepted', response.data.hasUserAccepted);
            sessionStorage.setItem('userType', response.data.userType);
        })]);
}

function showCodeOfConduct(opts){
    var locale = opts.userLocale ? opts.userLocale : sakai.locale.userLocale;

    if (!sessionStorage.getItem('hasUserAccepted')) {
        retrieveCodeOfConduct().then(function () {
            if (!opts.checkHasUserAccepted || (sessionStorage.getItem('hasUserAccepted') === 'false' && sessionStorage.getItem('userType') === 'student')) {
                showDialog(locale);
            }
            else if (opts.tutorial === 'true') {
                startTutorial(opts.showTutorialLocationOnHide);
            }
        });
    }
    else {
        if (!opts.checkHasUserAccepted || (sessionStorage.getItem('hasUserAccepted') === 'false' && sessionStorage.getItem('userType') === 'student')) {            
            showDialog(locale);
        }
    }
    
    if (opts.tutorial === 'true') {
        // show tutorial when closing code of conduct
        sessionStorage.setItem('showTutorialLocationOnHide', opts.showTutorialLocationOnHide);
    }
}
