$(document).ready(() => {

    const $name = $("#playlist-name");
    const $size = $("#playlist-size");
    const $percKnown = $("#known-slider");
    const $percKnownText = $("#known-text");
    const $genres = $(".chosen-select option:selected");
    const $newPlaylistDiv = $("#new-playlist-container");
    const $newPlaylistPre = $("#new-playlist-pre");
    const $newPlaylistShowButton = $("#new-playlist-show-button")

    $("#generate-playlist").click(() => {
        if ($size.val() == "" || parseInt($size.val()) < 1 || parseInt($size.val()) > 50) {
          alert("Please enter a playlist size between 1 and 50.");
        } else {
          if ($name.val().length > 0) {
            const name = $name.val();
            const size = $size.val();
            const percKnown = $percKnown.val();
            const genres = JSON.stringify($(".chosen-select").chosen().val());
            const postParameters = {
              name: name,
              size: size,
              percKnown: percKnown,
              genres: genres
            };
            $("#playListMessage").text("Generating Playlist...");
            $.post("/addplaylist", postParameters, responseJSON => {
            	$("playlistMessage").text("Done!");
                const responseObject = JSON.parse(responseJSON);
                console.log(responseObject.code);
                const queryMap = {code: responseObject.code};
                const queryParams = $.param(queryMap);
                window.location.href = '/commonground/playlist/' + $name.val() + "?" + queryParams;
            });
          } else {
        	   console.log("No name");
        	    alert("Please type a name.");
          }
        }

    });

    $percKnown.on("input change", function() {
        console.log($percKnown.val() + "%");
        console.log("TEXT: " +$("#known-text").innerHTML);
        $("#known-text").html("Playlist Familiarity: " +$percKnown.val() + "%");
    });

    $newPlaylistShowButton.click(() => {
        console.log("showing playlist div");
        $newPlaylistPre.hide();
        $newPlaylistDiv.show();


    });



});
