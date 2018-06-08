$(document).ready(() => {

    const $playlistID = $("#playlist-id");

    $("#savePlaylistButton").click(() => {
        console.log($playlistID.text());
        const postParameters = {
            playlistID: $playlistID.text()
        };

        $.post("/addPlaylistToSpotify", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const id = responseObject.spotifyPlaylistId;
            console.log("name");
            console.log(responseObject.userName);
            var x = document.getElementById("embedded");
            const url = "https://open.spotify.com/embed/user/" + responseObject.userName + "/playlist/" + id;
            document.getElementById('myPlaylist').src = url;
            x.style.display = "block";
        });
    });

    $("#refreshPlaylistButton").click(() => {
        console.log($playlistID.text());
        const postParameters = {
            playlistID: $playlistID.text()
        };
        console.log(postParameters);
        console.log("making post req to backend");

        $.post("/refreshplaylist", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            const name = responseObject.name;
            const code = responseObject.code;
            window.location.href = '/commonground/playlist/' + name + "?" + "code=" + code;
        });
    });

    window.onSpotifyWebPlaybackSDKReady = () => {
        // You can now initialize Spotify.Player and use the SDK

    };
});
