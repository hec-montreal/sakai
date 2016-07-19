var codeOfConductUrl = '/direct/code_of_conduct/code_of_conduct.json';
var postCodeOfConductUrl = '/direct/code_of_conduct/new.json';
var userLocale;
var optsCache;
var title;
var version;
var type = 'student';

function findVersion()
{
	return version.replace("-", "_");
}

function startCodeOfConduct(opts){
	showCodeOfConductPage( opts);
}

/*
 * The code is the same as showCodeOfConductPage but this code launches the tutorial tooltip if necessary and after 
 * the dialog box is closed.
 * Any change to showCodeOfConductPage should be done here too
 */
function firstShowCodeOfConductPage( opts){	
	//store options in cache so we can use the same options from start to end of the tutorial:
	if(opts != null)
		optsCache = opts;
	else if(optsCache != null)
		opts = optsCache;
	else{
		opts = {};
		optsCache = opts;
	}
	
	var tutorial = opts.tutorial;
	var dialogWidth = $(window).width()*0.8;
	var dialogHeight = $(window).height()*0.8;
	var height = dialogHeight -100;
	var localSeen, jsonSeen;
	
	var showTutorialLocationOnHide = opts.showTutorialLocationOnHide;
	version = opts.userLocale;
	var el = document.createElement( 'div' );

	//For now userLocale is empty when the language selected is english
	if (version == '')
		version = 'en_US';
		
	$.getJSON(codeOfConductUrl,
			function(response){
		
				try{
					jsonSeen = response.data.seenAtLogin;
					localSeen = localStorage.getItem('seenAtLogin');
					el.innerHTML = response.data.title;
					seen = localStorage.getItem('seenAtLogin');
				
					if (!response.data.hasUserAccepted && response.data.type==type && localSeen != jsonSeen){
						 $('div#codeOfConduct').dialog({
							 title: el.innerHTML,
							 open: function (event, ui) {
								 $( this ).dialog("option", "height",dialogHeight);
								 $( this ).dialog("option", "width",dialogWidth);
								 $( this ).dialog("option", "position",'top');
								 $('div#codeOfConduct').html(response.data.content);
								 $('div#codeOfConduct').css({height:(height-50)});
							    },
							    modal:true,
								 buttons:  
					           {
					        	   "J'ai lu et j'accepte": function() {
					        		   $.ajax({
					        			   type: 'POST',
					        			   url: postCodeOfConductUrl,
							                data: "saved",
							                dataType: 'json',
							                contentType: 'application/json',
							                async:false,
							                 failure: function failure(data){
							                    // TODO: internationalize
							                    alert("The user was not savec properly");
							                    },
							           	});
					        		   $('div#codeOfConduct').html("");
					        		   $( this ).dialog( "destroy" );
					        		   localStorage.setItem('seenAtLogin', response.data.seenAtLogin);
					        		   if (tutorial== 'true')
					        			   startTutorial(showTutorialLocationOnHide);
			    	      }
					           },
					    	  
							  close: function()	{
								  $('div#codeOfConduct').html("");
								  $( this ).dialog( "destroy" );
								  localStorage.setItem('seenAtLogin', response.data.seenAtLogin);
								  if (tutorial== 'true')
									  startTutorial(showTutorialLocationOnHide);
					}
					 });
						
					} else{
						  if (tutorial== 'true')
								startTutorial(showTutorialLocationOnHide);
					}
					
					
				}catch(e){
					$('div#codeOfConduct').html("");
					$(this).dialog("destroy");
					if (tutorial== 'true')
						startTutorial(showTutorialLocationOnHide);
				}
		}
	);
	
	 
}

function showCodeOfConductPage( opts){

	
	//store options in cache so we can use the same options from start to end of the tutorial:
	if(opts != null)
		optsCache = opts;
	else if(optsCache != null)
		opts = optsCache;
	else{
		opts = {};
		optsCache = opts;
	}
	
	var dialogWidth = $(window).width()*0.8;
	var dialogHeight = $(window).height()*0.8;
	var height = dialogHeight-100;
	var el = document.createElement( 'div' );
		
	version = opts.userLocale;
	
	//For now userLocale is empty when the language selected is english
	if (version == '')
		version = 'en_US';
		
	$.getJSON(codeOfConductUrl,
			function(response){
				try{
					el.innerHTML = response.data.title;
					 $('div#codeOfConduct').dialog({
						 title: el.innerHTML,
						 open: function (event, ui) {
							 $( this ).dialog("option", "height",dialogHeight);
							 $( this ).dialog("option", "width",dialogWidth);
							 $( this ).dialog("option", "position",'top');
							 $('div#codeOfConduct').html(response.data.content);
							 $('div#codeOfConduct').css({height:(height)});
						    },
						    modal:true,
							 buttons:{
				        	   "J'ai lu et j'accepte": function() {
				        		   $.ajax({
				        			   type: 'POST',
				        			   url: postCodeOfConductUrl,
						                data: "saved",
						                dataType: 'json',
						                contentType: 'application/json',
						                async:false,
						                 failure: function failure(data){
						                    // TODO: internationalize
						                    alert("The user was not savec properly");
						                    },
						           	});
				        		   $('div#codeOfConduct').html("");
				        		   $( this ).dialog( "destroy" );
				    	      }
				           },
				    	  
						  close: function()	{
							  $('div#codeOfConduct').html("");
							  $( this ).dialog( "destroy" );
						}
				 });
					
				}catch(e){
					$('div#codeOfConduct').html("");
					$(this).dialog("destroy");
				}
		}
	);
	
	 
}



function englishVersion(){
	var el = document.createElement( 'div' );
	if (findVersion() == 'fr_CA')
		$.getJSON(codeOfConductUrl,
				function(response){
			el.innerHTML = response.data.otherVersionTitle;
			$('div#codeOfConduct').html(response.data.otherVersionContent);
			$('div#codeOfConduct').dialog({title: el.innerHTML});
			}
		);
	else
		$.getJSON(codeOfConductUrl,
			function(response){
		el.innerHTML = response.data.title;
		$('div#codeOfConduct').html(response.data.content);
		$('div#codeOfConduct').dialog({title: el.innerHTML});
		}
	);

	 
}

function frenchVersion(){
	var el = document.createElement( 'div' );
	if (findVersion() == 'fr_CA')
		$.getJSON(codeOfConductUrl,
				function(response){
			el.innerHTML = response.data.title;
			$('div#codeOfConduct').html(response.data.content);
			$('div#codeOfConduct').dialog({title: el.innerHTML});
			}
		);
	else
		$.getJSON(codeOfConductUrl,
				function(response){
			el.innerHTML = response.data.otherVersionTitle;
			$('div#codeOfConduct').html(response.data.otherVersionContent);
			$('div#codeOfConduct').dialog({title: el.innerHTML});
			}
		);
	
	 
}
