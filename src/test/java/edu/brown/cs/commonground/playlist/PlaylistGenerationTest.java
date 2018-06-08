package edu.brown.cs.commonground.playlist;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.music.playlist.PlaylistGenerator;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.users.group.Group;
import edu.brown.cs.commonground.users.group.GroupProxy;

public class PlaylistGenerationTest {

  // @Test
  public void testPlaylistCreation() {
    // URIBuilder ub;
    // try {
    //// ub = new URIBuilder("");
    //// ub.addParameter("query", "we");
    // // ub.addParameter("searchText", "Abba");
    // // ub.addParameter("userAuthToken", "");
    // // // ub.addParameter("syncTime", "");
    // // // ub.addParameter("auth_token", "");
    // // // ub.addParameter("partner_id", "");
    // // // ub.addParameter("user_id", "");
    // // ub.addParameter("includeNearMatches", "true");
    // // ub.addParameter("includeGenreStations", "true");
    // String url = ub.toString();
    // System.out.println(url);
    // System.out.println(
    // PandoraMethods.executePost("http://musicbrainz.org/ws/2/recording?",
    // url.substring(1, url.length())));
    // } catch (URISyntaxException e) {
    // System.out.println(e.getMessage());
    // }

    // System.out.println(
    // SpotifyMethods.searchTrack("Good Good", "0L9xkvBPcEp1nrhDrodxc5"));
    // try {
    // Database.setDatabase("data/music.sqlite3");
    // MusicDatabase.populateTrackIds();
    // } catch (SQLException e) {
    // System.out.println(e.getMessage());
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    try {
      Database.setDatabase("data/music.sqlite3");
      PlaylistGenerator pg = new PlaylistGenerator();
      Group keigroup = new GroupProxy(65);
      List<String> genres = new ArrayList<>();
      genres.add("christian music");
      genres.add("free jazz");
      Playlist p = pg.generatePlaylist("Our playlist", keigroup, 10, 1, genres);
      System.out.println("PLAYLIST:");
      for (Song s : p.getSongs()) {
        System.out.println(s.getName());
      }
    } catch (IllegalStateException | SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

}
