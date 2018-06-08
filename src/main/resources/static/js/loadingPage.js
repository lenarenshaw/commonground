$(document).ready(() => {

  window.location.href = "/commonground/welcome";

  const postParameters = {};
  $.post("/loadingSongs", postParameters, responseJSON => {
      const responseObject = JSON.parse(responseJSON);
      const songs = responseObject.songs;
      const artists = responseObject.artists;
      const $curr = $("#curr-song");
      let doNext = null;
      let i = 0;
      doNext = function() {
        let song = songs[i];
        let artist = artists[i];
        $curr.text(song + " - " + artist);
        // do work
        if (i < songs.length - 1) {
          i++;
          setTimeout(doNext, 500);
        }
      }
      doNext();
  });

});
