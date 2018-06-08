$(document).ready(() => {

    const $name = $("#playlist-name");
    const $size = $("#playlist-size");
    const $percKnown = $("#known-slider");
    const $percKnownText = $("#known-text");
    const $genres = $(".chosen-select").chosen();

    $("#generate-playlist").click(() => {
        if ($size.val() == "" || parseInt($size.val()) < 1 || parseInt($size.val()) > 50) {
            alert("Please enter a playlist size between 1 and 50.");
        } else if ($name.val().length == 0) {
            alert("Please enter a playlist name.");
        } else if ($genres.val().length == 0) {
            alert("Please select at least one genre.");
        } else {
            const name = $name.val();
            const size = $size.val();
            const percKnown = $percKnown.val();
            const genres = JSON.stringify($genres.val());
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
        }
    });


    $percKnown.on("input change", function() {
        console.log($percKnown.val() + "%");
        console.log("TEXT: " +$("#known-text").innerHTML);
        $("#known-text").html("Playlist Familiarity: " +$percKnown.val() + "%");
    });

});
