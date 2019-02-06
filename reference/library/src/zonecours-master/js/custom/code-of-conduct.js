function showDialog(locale) {
	var dialogWidth = $(window).width()*0.8;
	var dialogHeight = $(window).height()*0.8;
	var height = dialogHeight -100;

    if (locale === 'fr-CA') {
        title = sessionStorage.getItem('titleFr');
        $('div#codeOfConduct').html(sessionStorage.getItem('bodyFr'));
    }
    else {
        title = sessionStorage.getItem('titleEn');
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
        buttons:  
        {
            "J'ai lu et j'accepte": sendAccept
        }
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
    return $.getJSON('/direct/code_of_conduct/code_of_conduct.json?' + new Date().getTime(), function(response){
        sessionStorage.setItem('titleEn', response.data.titleEn);
        sessionStorage.setItem('titleFr', response.data.titleFr);
        sessionStorage.setItem('bodyEn', response.data.bodyEn);
        sessionStorage.setItem('bodyFr', response.data.bodyFr);
        sessionStorage.setItem('hasUserAccepted', response.data.hasUserAccepted);
        sessionStorage.setItem('userType', response.data.userType);
    });
}

function showCodeOfConduct(opts){    
    if (!sessionStorage.getItem('hasUserAccepted')) {
        retrieveCodeOfConduct().then(function () {
            if (!opts.checkHasUserAccepted || (sessionStorage.getItem('hasUserAccepted') === 'false' && sessionStorage.getItem('userType') === 'student')) {
                showDialog(opts.userLocale);
            }
            else if (opts.tutorial === 'true') {
                startTutorial(opts.showTutorialLocationOnHide);
            }
        });
    }
    else {
        if (!opts.checkHasUserAccepted || (sessionStorage.getItem('hasUserAccepted') === 'false' && sessionStorage.getItem('userType') === 'student')) {            
            showDialog(opts.userLocale);
        }
    }
    
    if (opts.tutorial === 'true') {
        // show tutorial when closing code of conduct
        sessionStorage.setItem('showTutorialLocationOnHide', opts.showTutorialLocationOnHide);
    }
}
