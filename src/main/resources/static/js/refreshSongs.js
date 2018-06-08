$(document).ready(() => {
  $("#refreshSongs").click(() => {
    const queryMap = {
      client_id: "fcee77f8419342908f021021751a79a3",
      response_type: "code",
      show_dialog: "true",
      // redirect_uri: "http://localhost:4567/commonground/loading",
      redirect_uri: "http://b5e85938.ngrok.io/commonground/loading",
      scope: "playlist-read-collaborative playlist-read-private user-read-recently-played user-top-read user-follow-read user-library-read playlist-modify-private playlist-modify-public "
    };
    const url = "https://accounts.spotify.com/authorize";
    const queryParams = $.param(queryMap);

    const postParameters = {
      refresh: true
    };
    $.post("/refreshSongs", postParameters, responseJSON => {
      window.location.href = url + "?" + queryParams;
    });
  });
});
