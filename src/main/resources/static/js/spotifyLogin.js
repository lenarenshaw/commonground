$(document).ready(() => {
	let queryMap =
	{client_id: "fcee77f8419342908f021021751a79a3",
		response_type: "code",
		show_dialog: "true",
		// redirect_uri: "http://localhost:4567/commonground/loading",
        redirect_uri: "http://b5e85938.ngrok.io/commonground/loading",
		scope: "playlist-read-collaborative playlist-read-private user-read-recently-played user-top-read user-follow-read user-library-read playlist-modify-private playlist-modify-public "};
	let url = "https://accounts.spotify.com/authorize";

	let queryParams = $.param(queryMap);

	$("#login").click(() => {
		console.log("before api call");
		window.location.href = url + "?" + queryParams;
		console.log("after api call");
	});

});
