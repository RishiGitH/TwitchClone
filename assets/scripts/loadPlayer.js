/**
 * 
 */
function loadPlayer() 
		{
			console.log("App has called the jsFunction on load!")


			window.onPlayerEvent = function(data) 
			{
				console.log("Entered the onPlayerEvent fucntion!");
				data.forEach(function(event)
				{
					if (event.event == "playerInit") 
					{
						var player = $("#twitch_embed_player")[0];
						player.playVideo();
						player.mute();
					}
				});
			}

		}
