package edu.brown.cs.commonground.music.playlist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.brown.cs.commonground.database.MusicDatabase;
import edu.brown.cs.commonground.graph.CenterOfMass;
import edu.brown.cs.commonground.music.similarity.Similarity;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.users.group.Group;
import edu.brown.cs.commonground.users.user.User;

/**
 * Class implementing playlist generation.
 */
public class PlaylistGenerator {

  /**
   * Generates a new playlist for a group of a requested size and percentage of
   * songs known.
   *
   * @param name
   *          : name of playlist
   * @param group
   *          : group creating the playlist
   * @param size
   *          : number of songs in the playlist
   * @param percentKnown
   *          : double from 0 to 1 representing the percentage of songs that
   *          someone in the group has saved.
   * @return new Playlist for the group of size with given percent known
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  public Playlist generatePlaylist(String name, Group group, int size,
      double percentKnown, List<String> genres)
      throws IllegalStateException, SQLException {
    Set<Song> newPlaylist = new HashSet<>();
    int eachSize = size / genres.size() + 1;
    List<Integer> artistIds = new ArrayList<>();
    for (String g : genres) {
      List<String> gen = new ArrayList<>();
      gen.add(g);
      // 1. Get list of group's users' songs from database ordered from most
      // common among users to least.
      List<Song> orderedSongs = new ArrayList<>();
      Set<User> users = group.getUsers();
      Map<Song, Integer> songToCount = new HashMap<>();
      for (User user : users) {
        for (Song song : user.getSongs(gen)) {
          orderedSongs.add(song);
          songToCount.put(song, songToCount.getOrDefault(song, 0) + 1);
        }
      }
      List<Song> allKnown = new ArrayList<>(songToCount.keySet());
      allKnown.sort(new SongCountComparator(songToCount));
      // If 100% of songs should be known and orderedSongs has enough songs to
      // fill size, then we can stop here.
      // 2. Calculate number of known and unknown songs required.
      int numKnown = (int) (eachSize * percentKnown);
      int numUnknown = eachSize - numKnown;

      CenterOfMass<Song, Similarity> com = new CenterOfMass<>();
      List<Song> songs = new ArrayList<>();
      if (allKnown.size() > 2 && numUnknown > 0) {
        songs = com.getSimilar(allKnown, numUnknown);
      }

      List<Song> finalSongs = new ArrayList<>();

      Collections.shuffle(orderedSongs);
      for (Song s : orderedSongs) {
        if (finalSongs.size() < numKnown && !finalSongs.contains(s)
            && !artistIds.contains(s.getArtist().getId())) {
          finalSongs.add(s);
          artistIds.add(s.getArtist().getId());
        }
      }

      Collections.shuffle(songs);
      for (Song s : songs) {
        if (finalSongs.size() < eachSize && !finalSongs.contains(s)
            && !artistIds.contains(s.getArtist().getId())) {
          finalSongs.add(s);
          artistIds.add(s.getArtist().getId());
        }
      }

      if (finalSongs.size() < eachSize) {
        List<Song> extraSongs = MusicDatabase.getSongsByGenres(gen,
            3 * (finalSongs.size() + size));
        Collections.shuffle(extraSongs);
        for (Song s : extraSongs) {
          if (finalSongs.size() < eachSize && !finalSongs.contains(s)
              && !artistIds.contains(s.getArtist().getId())) {
            finalSongs.add(s);
            artistIds.add(s.getArtist().getId());
          }
        }
      }
      System.out.println(g);
      for (Song s : newPlaylist) {
        System.out.println(s.getName());
      }

      newPlaylist.addAll(finalSongs);
    }

    if (newPlaylist.size() < size) {
      List<Song> extraSongs = MusicDatabase.getSongsByGenres(genres,
          3 * (newPlaylist.size() + size));
      Collections.shuffle(extraSongs);
      for (Song s : extraSongs) {
        if (newPlaylist.size() < size && !newPlaylist.contains(s)
            && !artistIds.contains(s.getArtist().getId())) {
          newPlaylist.add(s);
          artistIds.add(s.getArtist().getId());
        }
      }
    }

    List<Song> result = new ArrayList<>(newPlaylist);
    Collections.shuffle(result);
    if (result.size() > size) {
      result = result.subList(0, size);
    }
    return MusicDatabase.addPlaylist(name, group, result, size, percentKnown,
        genres);
  }

  /**
   * Comparator implementation allowing comparison of songs by their number of
   * occurrences across users. Compare method results in descending order.
   */
  private class SongCountComparator implements Comparator<Song> {
    private Map<Song, Integer> songToCount;

    /**
     * Constructor for SongCountComparator.
     *
     * @param songToCount
     *          : map from song to number of occurrences across users
     */
    SongCountComparator(Map<Song, Integer> songToCount) {
      this.songToCount = songToCount;
    }

    /**
     * Returns negative integer if s1 occurs more than s2, positive integer if
     * s1 occurs less than s2, and 0 if s1 and s2 occur equal number of times.
     */
    @Override
    public int compare(Song s1, Song s2) {
      if (songToCount.get(s1) > songToCount.get(s2)) {
        return -1;
      } else if (songToCount.get(s1) < songToCount.get(s2)) {
        return 1;
      } else {
        return 0;
      }
    }
  }
}
