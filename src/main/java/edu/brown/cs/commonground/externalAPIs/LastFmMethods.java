package edu.brown.cs.commonground.externalAPIs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.umass.lastfm.Caller;
import de.umass.lastfm.Track;

/**
 * LastFmMethods is a class containing static methods referencing the LastFm
 * API. These are written with the help of a wrapper package.
 *
 */
public final class LastFmMethods {

  private static final int MAX_SIZE = 10;

  private LastFmMethods() {
  }

  /**
   * getSimilarSongs returns a list of songs similar to a given Song, as
   * determined by the lastfm API.
   *
   * @param artist
   *          String representing the song artist
   * @param song
   *          String representing the song title
   * @return a HashMap containing a map of a song title to its artist,
   *         associated with an integer representing the lastfm ranking of
   *         similarity. an empty map is returned if the input is invalid or
   *         lastfm cannot find similar songs.
   */
  public static Map<Map<String, String>, Integer> getSimilarSongs(String artist,
      String song) {
    Caller.getInstance().setUserAgent("tst");
    String key = "93d7018944e97c9dad7363e89aaa6853";
    Collection<Track> tracks = Track.getSimilar(artist.trim(), song.trim(),
        key);
    Map<Map<String, String>, Integer> songArtist = new HashMap<>();
    int num = MAX_SIZE;
    for (Track track : tracks) {
      while (track.getMbid() != "" && num > 1) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(track.getName(), track.getArtist());
        songArtist.put(mapping, num);
        num--;
        if (songArtist.size() > MAX_SIZE) {
          return songArtist;
        }
      }
    }
    return songArtist;
  }
}
