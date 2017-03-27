/**
 * 	Dedicated javascript file for all related Twitch tv api
 * 	tasks.
 * 
 *  -------------------------------------------------------
 *  Planned tasks
 *  
 *  - Mime type versioning v2 or v3 (Specified at load)
 *  - Self describing api handling.
 *  - Error handling.
 *  - Rate limit (Where applicable)
 *  
 */

var version;
var version2 = "v2";
var version3 = "v3";

//===============================================

var MAIN_URL = "https://api.twitch.tv/kraken/";		//<---- ROOT URL
var APP_TYPE;
//-----------------------------------------------

/*
 * Strings used for self describing link validation
 */

var GAMES = "games";

var USER = { name : "user",	link : null };
var CHANNEL = { name : "channel",	link : null };
var SEARCH = { name : "search",	link : null };
var STREAMS = { name : "streams",	link : null };
var INGESTS = { name : "ingests",	link : null };
var TEAMS = { name : "teams",	link : null };

var v3_KEY_ARRAY = [ USER, CHANNEL, SEARCH, STREAMS, INGESTS, TEAMS ];

function init_TwitchHandler(requiredVersion)
{
	//Init relevant parameters
	if(requiredVersion == 2){
		version = version2;
		APP_TYPE = "application/vnd.twitchtv."+version;
	}
	else if(requiredVersion == 3){
		version = version3;
		
	}
	else throw new InvalidArgumentException("Wrong version number!");
	if(version != null)
	{
		APP_TYPE = "application/vnd.twitchtv."+version;
	}
	rootLink_Validator();
}

/*
 * Function that dynamically assigns links to the required URL arguments
 */
function rootLink_Validator()
{
	//
	$.ajax(
    {
        url: MAIN_URL,
        contentType: APP_TYPE,
        dataType: 'jsonp',
        error: function(jqXHR, textStatus, errorThrown){
        	console.log(errorThrown+": "+textStatus);
        },
        success: function (data) 
        {
        	var arrayOfLinks = data._links;
        	for (var key in arrayOfLinks) 
        	{
        		  if(arrayOfLinks.hasOwnProperty(key))
        		  {
        			  var value = arrayOfLinks[key];
        			  if(checkKeys(key,value))
        			  {
        				  console.log("Found & Mapped link for: "+key+" @ "+value);  
        			  }
        			  
        		  }
        	}
        }
    });
}

/*
 * Main URL request processor function
 * 
 * Correctly grabs the url for the request required
 * in either v2 or v3 api links.
 */
function RequestURL_ForAction(action)
{
	if(version == version2)
	{
		//TODO: implement version 2 Request validation!
	}
	else if(version == version3)
	{
		for(var index = 0; index < v3_KEY_ARRAY.length; index++)
		{
			if(v3_KEY_ARRAY[index].name==action)
			{
				var processedURL = v3_KEY_ARRAY[index].link;
				console.log("Requested action for "+action+" URL. Returning ::: "+processedURL+" [ API Version: "+version+" ]")
				return processedURL;
			}
		}
	}
}


/*
 * Function that checks for valid and correct names for
 * the passed keys.
 */
function checkKeys(key,value)
{
	if(version == version2)
	{
		//TODO: implement version 2 twitch root validation!
	}
	else if(version == version3)
	{
		for(var index = 0; index < v3_KEY_ARRAY.length; index++){
			//loop through and validate each name against the key!
			if(v3_KEY_ARRAY[index].name==key)
			{
				v3_KEY_ARRAY[index].link=value;
				return true;
			}
		}
	}
}

/*
 *	Exception Function dedicated to wrong version
 *	of the mime type. 
 */
function InvalidArgumentException(message)
{
	this.message = message;
	this.name = "InvalidArgumentException";
}