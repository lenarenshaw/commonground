package edu.brown.cs.commonground.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;

import edu.brown.cs.commonground.externalAPIs.GracenoteMethods;
import edu.brown.cs.commonground.externalAPIs.LastFmMethods;
import edu.brown.cs.commonground.externalAPIs.SpotifyMethods;
import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.music.playlist.PlaylistBean;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.music.song.SongProxy;
import edu.brown.cs.commonground.users.group.Group;
import edu.brown.cs.commonground.users.user.User;

/**
 * Class for accessing and modifying the music.sqlite3 database.
 */
public class MusicDatabase {
  // TODO: CACHING

  /**
   * Adds new group to the database and returns group's id in the database.
   *
   * @param spotifyId
   *          : spotify id of group's leader
   * @param groupName
   *          : name of the group
   * @return id of group that was just added
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static synchronized int addGroup(String spotifyId, String groupName)
      throws IllegalStateException, SQLException {
    try {
      Database.setDatabase("data/music.sqlite3");
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String q1 = "INSERT INTO groups (name, leader) VALUES "
        + "(?, (SELECT id from users WHERE spotifyId=?));";
    try (PreparedStatement ps = Database.prepareStatement(q1)) {
      ps.setString(1, groupName);
      ps.setString(2, spotifyId);
      ps.executeUpdate();
    }
    String q2 = "select last_insert_rowid();";
    try (PreparedStatement ps2 = Database.prepareStatement(q2)) {
      try (ResultSet rs2 = ps2.executeQuery()) {
        return rs2.getInt(1);
      }
    }
  }

  /**
   * Determines whether a group exists in the database.
   *
   * @param groupId
   *          : group's id in the database
   * @param groupName
   *          : group's name in the database
   * @return true if the group exists, false otherwise
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static boolean isValidGroup(int groupId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT id, name, leader FROM groups WHERE id=?;";
    try (PreparedStatement ps = Database.prepareStatement(q1)) {
      ps.setInt(1, groupId);
      ps.executeQuery();
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isUserInGroup(String spotifyId, int groupId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT * FROM group_users WHERE user=(SELECT id FROM users WHERE spotifyId = ?)"
        + " AND grp = ?;";
    try (PreparedStatement ps = Database.prepareStatement(q1)) {
      ps.setString(1, spotifyId);
      ps.setInt(2, groupId);
      ps.executeQuery();
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Determines whether a playlist exists in the database.
   *
   * @param playlistId
   *          : playlist's id in the database
   * @param playlistName
   *          : playlist's name
   * @param groupId
   *          : id of the group that created the playlist
   * @return true if playlist exists, false otherwise
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */

  public static boolean isValidPlaylist(int playlistId, int groupId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT id, name, grp FROM playlists WHERE id = ? "
        + "AND grp = ?;";
    try (PreparedStatement ps = Database.prepareStatement(q1)) {
      ps.setInt(1, playlistId);
      ps.setInt(2, groupId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Removes a playlist of a certain id from the database.
   *
   * @param playlistId
   *          : id of playlist
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static void removePlaylist(int playlistId)
      throws IllegalStateException, SQLException {
    try (PreparedStatement ps = Database
        .prepareStatement("DELETE FROM playlists WHERE id = ?;")) {
      ps.setInt(1, playlistId);
      ps.executeUpdate();
    }
    try (PreparedStatement ps = Database
        .prepareStatement("DELETE FROM playlist_genres WHERE playlist = ?;")) {
      ps.setInt(1, playlistId);
      ps.executeUpdate();
    }
    try (PreparedStatement ps = Database
        .prepareStatement("DELETE FROM playlist_songs WHERE playlist = ?;")) {
      ps.setInt(1, playlistId);
      ps.executeUpdate();
    }
  }

  /**
   * Gets a group's name given its database id.
   *
   * @param groupId
   *          : group's id in database.
   * @return group's name
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static String getGroupFromId(int groupId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT name FROM groups WHERE id=?;";
    String toReturn;
    try (PreparedStatement ps = Database.prepareStatement(q1)) {
      ps.setInt(1, groupId);
      ps.executeQuery();
      try (ResultSet rs = ps.executeQuery()) {
        toReturn = rs.getString(1);
      }
    }
    return toReturn;

  }

  /**
   * Gets all genres stored in database.
   *
   * @return genres in database
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static List<String> getGenres()
      throws IllegalStateException, SQLException {
    List<String> genres = new ArrayList<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT DISTINCT genre FROM artist_genres, songs "
            + "where songs.artist = artist_genres.artist;")) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          genres.add(rs.getString(1));
        }
      }
    }
    return genres;
  }

  /**
   * Adds user to group.
   *
   * @param groupId
   *          : group's id in database
   * @param dbUserId
   *          : user's id in database
   * @return 0 if successfuly added, 1 if group does not exist 2 if user is
   *         already in group, 3 if something went wrong.
   * @throws IllegalStateException
   *           : if database has not been loaded
   * @throws SQLException
   *           : if SQL error occurs
   */
  public static int addUserToGroup(int groupId, String dbUserId)
      throws IllegalStateException, SQLException {

    boolean groupExists = false;
    String q = "SELECT * FROM groups WHERE id = ?;";
    try (PreparedStatement prep = Database.prepareStatement(q)) {
      prep.setInt(1, groupId);
      try (ResultSet rs = prep.executeQuery()) {
        if (rs.next()) {
          groupExists = true;
        }
      }
    }

    if (!groupExists) {
      return 1;
    }

    boolean userExistsInGroup = false;
    if (groupExists) {
      String q2 = "SELECT id FROM group_users WHERE grp = ? and user = ?;";
      try (PreparedStatement prep = Database.prepareStatement(q2)) {
        prep.setInt(1, groupId);
        prep.setString(2, dbUserId);
        try (ResultSet rs = prep.executeQuery()) {
          if (rs.next()) {
            userExistsInGroup = true;
          }
        }
      }
    }

    if (userExistsInGroup) {
      return 2;
    }

    boolean added = false;
    if (groupExists & !userExistsInGroup) {
      String q1 = "INSERT INTO group_users (grp, user) VALUES (?,?);";
      try (PreparedStatement ps = Database.prepareStatement(q1)) {
        ps.setInt(1, groupId);
        ps.setString(2, dbUserId);
        ps.executeUpdate();
        added = true;
      }
    }

    if (added) {
      return 0;
    }
    return 3;
  }

  /**
   * Get user of a specified Spotify ID.
   *
   * @param spotifyId
   *          : user's Spotify ID
   * @return user of specified Spotify ID
   */
  public static User getUserBySpotifyId(String spotifyId) {
    String q1 = "SELECT id FROM users WHERE spotifyId = ?;";
    try (PreparedStatement prep1 = Database.prepareStatement(q1)) {
      prep1.setString(1, spotifyId);
      try (ResultSet rs = prep1.executeQuery()) {
        if (rs.next()) {
          return User.ofId(rs.getInt(1));
        } else {
          System.out.println("DEBUG: User id with spotify id " + spotifyId
              + "could not be found in users table.");
          return null;
        }
      }
    } catch (SQLException e) {
      // SHOULD NEVER HAPPEN
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Get a users id.
   *
   * @param spotifyId
   *          user's spotify id.
   * @return the user's id (Primary Key) in the database.
   * @throws SQLException
   *           : if SQL error occurs
   * @throws IllegalStateException
   *           : if database has not been loaded
   */
  public static String getUserId(String spotifyId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT id from users WHERE spotifyId=?";
    PreparedStatement prep1 = Database.prepareStatement(q1);
    prep1.setString(1, spotifyId);
    try (ResultSet rs = prep1.executeQuery()) {
      while (rs.next()) {
        return rs.getString(1);
      }
    }
    return null;
  }

  /**
   * Gets user's Spotify id given their database id.
   *
   * @param userDbId
   *          : user's database id
   * @return returns Spotify id of user
   * @throws IllegalStateException
   *           : thrown if database has not been loaded
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  public static String getSpotifyId(String userDbId)
      throws IllegalStateException, SQLException {
    String q1 = "SELECT spotifyId from users WHERE id=?";
    try (PreparedStatement prep1 = Database.prepareStatement(q1)) {
      prep1.setString(1, userDbId);
      try (ResultSet rs = prep1.executeQuery()) {
        while (rs.next()) {
          return rs.getString(1);
        }
      }
    }
    return null;
  }

  /**
   * Adds a user to the database given their Spotify id.
   *
   * @param spotifyId
   *          : user's Spotify id
   * @return true if user could be added, false if the user already exists in
   *         the database
   */
  public static boolean addUser(String spotifyId, String displayName) {
    try {
      String q1 = "SELECT id FROM users WHERE spotifyId = ?;";
      try (PreparedStatement prep1 = Database.prepareStatement(q1)) {
        prep1.setString(1, spotifyId);
        try (ResultSet rs = prep1.executeQuery()) {
          if (!rs.next()) {
            String q2 = "INSERT INTO users (spotifyId, display_name) VALUES (?, ?);";
            try (PreparedStatement prep2 = Database.prepareStatement(q2)) {
              prep2.setString(1, spotifyId);
              prep2.setString(2, displayName);
              prep2.executeUpdate();
            }
          } else {
            return false;
          }
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return true;
  }

  public static void addUserTrack(String userId, String songName,
      String artistName) {
    System.out.println(userId + "\'s tracks");
    System.out.println(songName);
    System.out.println(artistName);
    String spotifyTrackId = SpotifyMethods.searchTrack(songName, artistName);
    if (!spotifyTrackId.equals("")) {
      try {
        Map<Map<String, String>, Integer> r = LastFmMethods
            .getSimilarSongs(artistName, songName);
        Map<String, String> thisSong = new HashMap<>();
        thisSong.put(songName, artistName);
        r.put(thisSong, 0);
        String q1 = "SELECT id FROM artists WHERE spotifyId = ?;";
        String q2 = "INSERT INTO artists (artist, spotifyId) VALUES (?,?);";
        String q3 = "SELECT id FROM songs WHERE song = ?;";
        String q34 = "SELECT id from artists WHERE artist=?;";
        PreparedStatement prep34 = Database.prepareStatement(q34);
        String q4 = "INSERT INTO songs (song, artist, spotifyId) VALUES (?,?,?);";
        PreparedStatement prep1 = Database.prepareStatement(q1);
        PreparedStatement prep2 = Database.prepareStatement(q2);
        PreparedStatement prep3 = Database.prepareStatement(q3);
        PreparedStatement prep4 = Database.prepareStatement(q4);
        String q5 = "SELECT lastfm FROM songrelation WHERE id1 = ? AND id2 = ?";
        PreparedStatement prep5 = Database.prepareStatement(q5);
        String q6 = "SELECT id FROM songs WHERE song = ?;";
        PreparedStatement prep6 = Database.prepareStatement(q6);
        String q7 = "INSERT INTO songrelation (id1, id2, lastfm) VALUES (?,?,?);";
        PreparedStatement prep7 = Database.prepareStatement(q7);
        String q8 = "UPDATE songrelation SET lastfm = "
            + "? WHERE id1 = ? AND id2 = ?;";
        PreparedStatement prep8 = Database.prepareStatement(q8);
        String q11 = "SELECT id FROM user_songs WHERE user = (SELECT id from users WHERE spotifyId=?) AND song = ?;";
        PreparedStatement prep11 = Database.prepareStatement(q11);
        String q9 = "INSERT INTO user_songs (user, song) VALUES ((SELECT id from users WHERE spotifyId=?),?);";
        PreparedStatement prep9 = Database.prepareStatement(q9);
        String q10 = "INSERT INTO artist_genres (artist, genre) VALUES (?,?);";
        PreparedStatement prep10 = Database.prepareStatement(q10);
        for (Map<String, String> m : r.keySet()) {
          for (String s : m.values()) {
            String spotifyId = SpotifyMethods.searchArtist(s);
            prep1.setString(1, spotifyId);
            ResultSet rs2 = prep1.executeQuery();
            if (!rs2.next()) {
              String name = s.replaceAll("\"", "\\\\\"").replaceAll("\'",
                  "\''");
              prep2.setString(1, name);
              prep2.setString(2, spotifyId);
              prep2.executeUpdate();
              prep1.setString(1, spotifyId);
              ResultSet newrs2 = prep1.executeQuery();
              String[] genres = SpotifyMethods.getArtistGenres(spotifyId);
              for (String genre : genres) {
                String[] subGenres = genre.split(" ");
                if (genre.trim().equals("hip hop")) {
                  prep10.setString(1, newrs2.getString(1));
                  prep10.setString(2, "hip hop");
                  prep10.executeUpdate();
                } else if (genre.trim().equals("rock 'n roll")) {
                  prep10.setString(1, newrs2.getString(1));
                  prep10.setString(2, "rock 'n roll");
                  prep10.executeUpdate();
                } else {
                  for (String subGenre : subGenres) {
                    prep10.setString(1, newrs2.getString(1));
                    prep10.setString(2, subGenre);
                    prep10.executeUpdate();
                  }
                }
              }
            }
            rs2.close();
          }
          for (String s : m.keySet()) {
            String song = s.replaceAll("\"", "\\\\\"").replaceAll("\'", "\''");
            prep3.setString(1, song);
            try (ResultSet rs2 = prep3.executeQuery()) {
              String artist = m.get(s).replaceAll("\"", "\\\\\"")
                  .replaceAll("\'", "\''");
              prep34.setString(1, artist);
              String sId = SpotifyMethods.searchTrack(song, artist);
              try (ResultSet rs34 = prep34.executeQuery()) {
                if (!rs2.next() && rs34.next() && !sId.equals("")) {
                  prep4.setString(1, song);
                  prep4.setString(2, rs34.getString(1));
                  prep4.setString(3, sId);
                  prep4.executeUpdate();
                }
              }
            }
          }
        }
        r.remove(thisSong);
        prep6.setString(1,
            songName.replaceAll("\"", "\\\\\"").replaceAll("\'", "\''"));
        try (ResultSet rs3 = prep6.executeQuery()) {
          String originalId = "";
          if (rs3.next()) {
            originalId = rs3.getString(1);
          }
          for (Map<String, String> m : r.keySet()) {
            for (String s : m.keySet()) {
              prep6.setString(1,
                  s.replaceAll("\"", "\\\\\"").replaceAll("\'", "\''"));
              try (ResultSet rs4 = prep6.executeQuery()) {
                String sid = "";
                if (rs4.next()) {
                  sid = rs4.getString(1);
                }
                String songId = sid.replaceAll("\"", "\\\\\"").replaceAll("\'",
                    "\''");
                if (songId.compareTo(originalId) < 0) {
                  prep5.setString(1, songId);
                  prep5.setString(2, originalId);
                  try (ResultSet rs5 = prep5.executeQuery()) {
                    // THIS IS VERY IMPORTANT - PREVENTS NULL ENTRIES
                    if (!rs5.next() && !songId.equals("")) {
                      prep7.setString(1, songId);
                      prep7.setString(2, originalId);
                      prep7.setInt(3, r.get(m));
                      prep7.executeUpdate();
                    } else {
                      int newNum = rs5.getInt(1) + r.get(m);
                      prep8.setInt(1, newNum);
                      prep8.setString(2, songId);
                      prep8.setString(3, originalId);
                      prep8.executeUpdate();
                    }
                  }
                }
              }
            }
          }
          if (!originalId.equals("")) {
            prep11.setString(1, userId);
            prep11.setString(2, originalId);
            try (ResultSet rs11 = prep11.executeQuery()) {
              if (!rs11.next()) {
                prep9.setString(1, userId);
                prep9.setString(2, originalId);
                prep9.executeUpdate();
              }
            }
          }
        }
        prep1.close();
        prep2.close();
        prep3.close();
        prep34.close();
        prep5.close();
        prep6.close();
        prep7.close();
        prep8.close();
        prep9.close();
        prep10.close();
        prep11.close();
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Adds playlist to the database and returns a playlist given its group and an
   * ordered list of its songs.
   *
   * @param name
   *          : name of playlist
   * @param group
   *          : group creating the playlist
   * @param songs
   *          : ordered list of this playlist's songs
   * @param reqSize
   *          : requested size of playlist
   * @param percentKnown
   *          : requested percent known of playlist
   * @param genres
   *          : requested genres of playlist
   * @return new Playlist for group with songs
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  public static Playlist addPlaylist(String name, Group group, List<Song> songs,
      int reqSize, double percentKnown, List<String> genres)
      throws IllegalStateException, SQLException {

    // Adds playlist with its name and group to the playlists table.
    try (PreparedStatement ps = Database.prepareStatement(
        "INSERT INTO playlists (name, grp, size, percent_known) "
            + "VALUES (?, ?, ?, ?);")) {
      ps.setString(1, name);
      ps.setInt(2, group.getId());
      ps.setInt(3, reqSize);
      ps.setDouble(4, percentKnown);
      ps.executeUpdate();
    }

    // Gets playlist's new id.
    int id;
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT last_insert_rowid();")) {
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          id = rs.getInt(1);
        } else {
          throw new SQLException(
              "ERROR: New playlist's name and group were not correctly set.");
        }
      }
    }

    // Adds playlist's songs to playlist_songs table.
    try (PreparedStatement ps = Database.prepareStatement(
        "INSERT INTO playlist_songs (playlist, song) " + "VALUES (?, ?);")) {
      for (Song song : songs) {
        ps.setInt(1, id);
        ps.setInt(2, song.getId());
        ps.addBatch();
      }
      ps.executeBatch();
    }

    // Adds playlist's genres to playlist_genres table.
    try (PreparedStatement ps = Database.prepareStatement(
        "INSERT INTO playlist_genres (playlist, genre) " + "VALUES (?, ?);")) {
      for (String genre : genres) {
        ps.setInt(1, id);
        ps.setString(2, genre);
        ps.addBatch();
      }
      ps.executeBatch();
    }

    // Returns PlaylistBean instead of PlaylistProxy since we already have all
    // the data required to instantiate a PlaylistBean.
    return new PlaylistBean(id, name, group, songs, reqSize, percentKnown,
        genres);
  }

  public static List<Song> getSongsByGenres(List<String> genres, int size) {
    Set<Song> songs = new HashSet<>();
    try {
      for (String genre : genres) {
        System.out.println("genre: " + genre);
        String q = "SELECT songs.id FROM songs, artist_genres WHERE "
            + "songs.artist = artist_genres.artist AND artist_genres.genre = ?;";
        try (PreparedStatement prep = Database.prepareStatement(q)) {
          prep.setString(1, genre);
          try (ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {
              songs.add(new SongProxy(rs.getInt(1)));
            }
          }
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    List<Song> extraSongs = new ArrayList<>(songs);
    Collections.shuffle(extraSongs);
    if (extraSongs.size() > size) {
      return extraSongs.subList(0, size);
    }
    return extraSongs;
  }

  // TO DELETE EVENTUALLY: DATABASE POPULATING METHODS

  public static List<Map<Map<String, String>, Integer>> gracenoteSimilarity() {
    List<Map<Map<String, String>, Integer>> result = new ArrayList<>();
    try {
      Database.setDatabase("data/music.sqlite3");
      String q = "SELECT artist, song, id FROM songs WHERE id > 8953;";
      PreparedStatement prep = Database.prepareStatement(q);
      String q5 = "SELECT artist FROM artists WHERE id = ?;";
      PreparedStatement prep5 = Database.prepareStatement(q5);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          try {
            String originalArtistId = rs.getString(1);
            System.out.println(rs.getString(3));
            String originalArtist = "";
            prep5.setString(1, originalArtistId);
            try (ResultSet rs5 = prep5.executeQuery()) {
              if (rs5.next()) {
                originalArtist = rs5.getString(1);
              }
            }
            String originalSong = rs.getString(2);
            String originalId = rs.getString(3).replaceAll("\"", "\\\\\"")
                .replaceAll("\'", "\''");
            Map<Map<String, String>, Integer> r = GracenoteMethods
                .getSimilarSongs(originalArtist, originalSong);
            System.out.println(originalArtist);
            System.out.println(originalSong);
            String query = "SELECT id FROM artists WHERE spotifyId = ?;";
            try (PreparedStatement prep1 = Database.prepareStatement(query)) {
              for (Map<String, String> m : r.keySet()) {
                for (String s : m.values()) {
                  String id = SpotifyMethods.searchArtist(s);
                  prep1.setString(1, id);
                  ResultSet rs2 = prep1.executeQuery();
                  if (!rs2.next() && !id.equals("")) {
                    String name = s.replaceAll("\"", "\\\\\"").replaceAll("\'",
                        "\''");
                    String sql = "INSERT INTO artists (artist, spotifyId) VALUES (\'"
                        + name + "\',\"" + id + "\");";
                    try (Statement stmt = Database.createStatement()) {
                      stmt.execute(sql);
                    }
                    prep1.setString(1, id);
                    rs2.close();
                    rs2 = prep1.executeQuery();
                    String q10 = "INSERT INTO artist_genres (artist, genre) VALUES (?,?);";
                    try (PreparedStatement prep10 = Database
                        .prepareStatement(q10)) {
                      String[] genres = SpotifyMethods.getArtistGenres(id);
                      for (String genre : genres) {
                        prep10.setString(1, rs2.getString(1));
                        prep10.setString(2, genre);
                        prep10.executeUpdate();
                        System.out.println(rs2.getString(1));
                      }
                    }
                  }
                  rs2.close();
                }
                for (String s : m.keySet()) {
                  String q1 = "SELECT id FROM songs WHERE song = ?;";
                  try (
                      PreparedStatement prep2 = Database.prepareStatement(q1)) {
                    String song = s.replaceAll("\"", "\\\\\"").replaceAll("\'",
                        "\''");
                    prep2.setString(1, song);
                    String artist = m.get(s).replaceAll("\"", "\\\\\"")
                        .replaceAll("\'", "\''");
                    try (ResultSet rs2 = prep2.executeQuery()) {
                      if (!rs2.next()) {
                        PreparedStatement pre = Database.prepareStatement(
                            "SELECT id FROM artists WHERE artist = ?;");
                        pre.setString(1, artist);
                        ResultSet res = pre.executeQuery();
                        if (res.next()) {
                          String sql = "INSERT INTO songs (song, artist) VALUES (\'"
                              + song + "\',\'" + res.getInt(1) + "\');";
                          try (Statement stmt = Database.createStatement()) {
                            stmt.execute(sql);
                          }
                        }
                      }
                    }
                  }
                }
              }
              String q2 = "SELECT gracenote FROM songrelation WHERE id1 = ? AND id2 = ?";
              try (PreparedStatement prep2 = Database.prepareStatement(q2)) {
                for (Map<String, String> m : r.keySet()) {
                  for (String s : m.keySet()) {
                    String q1 = "SELECT id FROM songs WHERE song = ?;";
                    try (PreparedStatement p1 = Database.prepareStatement(q1)) {
                      String song = s.replaceAll("\"", "\\\\\"")
                          .replaceAll("\'", "\''");
                      p1.setString(1, song);
                      String sid = "";
                      try (ResultSet rs3 = p1.executeQuery()) {
                        if (rs3.next()) {
                          sid = rs3.getString(1);
                        }
                      }
                      String songId = sid.replaceAll("\"", "\\\\\"")
                          .replaceAll("\'", "\''");
                      if (songId.compareTo(originalId) < 0 && !songId.equals("")
                          && !originalId.equals("")) {
                        prep2.setString(1, songId);
                        prep2.setString(2, originalId);
                        try (ResultSet rs4 = prep2.executeQuery()) {
                          if (!rs4.next()) {
                            String sql = "INSERT INTO songrelation (id1, id2, gracenote) VALUES (\'"
                                + songId + "\',\'" + originalId + "\',\'"
                                + r.get(m) + "\');";
                            try (Statement stmt = Database.createStatement()) {
                              stmt.execute(sql);
                              System.out.println("new relation");
                            }
                          } else {
                            int newNum = rs4.getInt(1) + r.get(m);
                            String sql = "UPDATE songrelation SET gracenote = "
                                + newNum + " WHERE id1 = \'" + songId
                                + "\' AND id2 = \'" + originalId + "\';";
                            try (Statement stmt = Database.createStatement()) {
                              stmt.execute(sql);
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
        }
      }
      prep.close();
      prep5.close();
      System.out.println("all songs complete");
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return result;
  }

  public static List<Map<Map<String, String>, Integer>> lastFmSimilarity() {
    List<Map<Map<String, String>, Integer>> result = new ArrayList<>();
    try {
      Database.setDatabase("data/music.sqlite3");
      String q = "SELECT artist, song, id FROM songs;";
      PreparedStatement prep = Database.prepareStatement(q);
      String q5 = "SELECT artist FROM artists WHERE id = ?;";
      PreparedStatement prep5 = Database.prepareStatement(q5);
      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          try {
            String originalArtistId = rs.getString(1);
            String originalArtist = "";
            prep5.setString(1, originalArtistId);
            try (ResultSet rs5 = prep5.executeQuery()) {
              if (rs5.next()) {
                originalArtist = rs5.getString(1);
              }
            }
            String originalSong = rs.getString(2);
            String originalId = rs.getString(3).replaceAll("\"", "\\\\\"")
                .replaceAll("\'", "\''");
            Map<Map<String, String>, Integer> r = LastFmMethods
                .getSimilarSongs(originalArtist, originalSong);
            System.out.println(originalArtist);
            System.out.println(originalSong);
            String query = "SELECT id FROM artists WHERE spotifyId = ?;";
            try (PreparedStatement prep1 = Database.prepareStatement(query)) {
              for (Map<String, String> m : r.keySet()) {
                for (String s : m.values()) {
                  String id = SpotifyMethods.searchArtist(s);
                  prep1.setString(1, id);
                  ResultSet rs2 = prep1.executeQuery();
                  if (!rs2.next()) {
                    String name = s.replaceAll("\"", "\\\\\"").replaceAll("\'",
                        "\''");
                    String sql = "INSERT INTO artists (artist, spotifyId) VALUES (\'"
                        + name + "\',\"" + id + "\");";
                    try (Statement stmt = Database.createStatement()) {
                      stmt.execute(sql);
                    }
                    prep1.setString(1, id);
                    rs2.close();
                    rs2 = prep1.executeQuery();
                    String q10 = "INSERT INTO song_genres (song, genre) VALUES (?,?);";
                    PreparedStatement prep10 = Database.prepareStatement(q10);
                    String[] genres = SpotifyMethods.getArtistGenres(id);
                    for (String genre : genres) {
                      prep10.setString(1, rs2.getString(1));
                      prep10.setString(2, genre);
                      prep10.executeUpdate();
                      System.out.println(rs2.getString(1));
                    }
                  }
                  rs2.close();
                }

                for (String s : m.keySet()) {
                  String q1 = "SELECT id FROM songs WHERE song = ?;";
                  try (
                      PreparedStatement prep2 = Database.prepareStatement(q1)) {
                    String song = s.replaceAll("\"", "\\\\\"").replaceAll("\'",
                        "\''");
                    prep2.setString(1, song);
                    try (ResultSet rs2 = prep2.executeQuery()) {
                      if (!rs2.next()) {
                        String artist = m.get(s).replaceAll("\"", "\\\\\"")
                            .replaceAll("\'", "\''");
                        String sql = "INSERT INTO songs (song, artist) VALUES (\'"
                            + song + "\',\"" + artist + "\");";
                        try (Statement stmt = Database.createStatement()) {
                          stmt.execute(sql);
                        }
                      }
                    }
                  }
                }
              }
              String q2 = "SELECT lastfm FROM songrelation WHERE "
                  + "id1 = ? AND id2 = ?";
              try (PreparedStatement prep2 = Database.prepareStatement(q2)) {
                for (Map<String, String> m : r.keySet()) {
                  for (String s : m.keySet()) {
                    String q1 = "SELECT id FROM songs WHERE song = ?;";
                    String sid = "";
                    try (PreparedStatement p1 = Database.prepareStatement(q1)) {
                      String song = s.replaceAll("\"", "\\\\\"")
                          .replaceAll("\'", "\''");
                      p1.setString(1, song);
                      try (ResultSet rs3 = p1.executeQuery()) {
                        if (rs3.next()) {
                          sid = rs3.getString(1);
                        }
                      }
                    }
                    String songId = sid.replaceAll("\"", "\\\\\"")
                        .replaceAll("\'", "\''");
                    if (songId.compareTo(originalId) < 0) {
                      prep2.setString(1, songId);
                      prep2.setString(2, originalId);
                      try (ResultSet rs4 = prep2.executeQuery()) {
                        if (!rs4.next() && !songId.equals("")
                            && !originalId.equals("")) {
                          String sql = "INSERT INTO songrelation (id1, id2, lastfm) VALUES (\'"
                              + songId + "\',\'" + originalId + "\',\'"
                              + r.get(m) + "\');";
                          try (Statement stmt = Database.createStatement()) {
                            stmt.execute(sql);
                          }
                          System.out.println("added");
                        } else {
                          int newNum = rs4.getInt(1) + r.get(m);
                          String sql = "UPDATE songrelation SET lastfm = "
                              + newNum + " WHERE id1 = \'" + songId
                              + "\' AND id2 = \'" + originalId + "\';";
                          try (Statement stmt = Database.createStatement()) {
                            stmt.execute(sql);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
            System.out.println("finished map");
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
        }
      }
      prep.close();
      prep5.close();
      System.out.println("all songs complete");
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return result;
  }

  public static void spotifyPlaylistSimilarity(String type) {
    try {
      Database.setDatabase("data/music.sqlite3");
      PlaylistSimplified[] p = SpotifyMethods.searchPlaylists(type);
      Map<String, String> plstIds = new HashMap<>();
      for (PlaylistSimplified list : p) {
        plstIds.put(list.getId(), list.getOwner().getId());
      }
      String q = "SELECT id FROM artists WHERE spotifyId = ?;";
      String q1 = "SELECT id FROM songs WHERE song = ?;";
      String q2 = "SELECT spotify FROM songrelation WHERE id1 = ? AND id2 = ?";
      String q3 = "UPDATE songrelation SET spotify = ? WHERE id1 = ? AND id2 = ?;";
      // querying the database to return a list of lists of all the actors.
      PreparedStatement prep = Database.prepareStatement(q);
      PreparedStatement prep1 = Database.prepareStatement(q1);
      PreparedStatement prep2 = Database.prepareStatement(q2);
      PreparedStatement prep3 = Database.prepareStatement(q3);
      for (String id : plstIds.keySet()) {
        PlaylistTrack[] tracks = SpotifyMethods.getPlaylist(id,
            plstIds.get(id));
        for (PlaylistTrack t : tracks) {
          System.out.println(t.getTrack().getArtists()[0].getId());
          prep.setString(1, t.getTrack().getArtists()[0].getId());
          try (ResultSet rs = prep.executeQuery()) {
            if (!rs.next()) {
              String name = t.getTrack().getArtists()[0].getName()
                  .replaceAll("\"", "\\\\\"").replaceAll("\'", "\''");
              String sql = "INSERT INTO artists (artist, spotifyId) VALUES (\'"
                  + name + "\',\"" + t.getTrack().getArtists()[0].getId()
                  + "\");";
              try (Statement stmt = Database.createStatement()) {
                stmt.execute(sql);
              }
              prep.setString(1, t.getTrack().getArtists()[0].getId());
              ResultSet newRs = prep.executeQuery();
              String q10 = "INSERT INTO artist_genres (artist, genre) VALUES (?,?);";
              try (PreparedStatement prep10 = Database.prepareStatement(q10)) {
                String[] genres = SpotifyMethods
                    .getArtistGenres(t.getTrack().getArtists()[0].getId());
                for (String genre : genres) {
                  prep10.setString(1, newRs.getString(1));
                  prep10.setString(2, genre);
                  prep10.executeUpdate();
                }
              }
            }
          }
          prep1.setString(1, t.getTrack().getName());
          try (ResultSet rs = prep1.executeQuery()) {
            if (!rs.next()) {
              String name = t.getTrack().getName().replaceAll("\"", "\\\\\"")
                  .replaceAll("\'", "\''");
              String sql = "INSERT INTO songs (song, artist) VALUES (\'" + name
                  + "\',\"" + t.getTrack().getArtists()[0].getId() + "\");";
              try (Statement stmt = Database.createStatement()) {
                stmt.execute(sql);
              }
            }
          }
        }
        for (PlaylistTrack t : tracks) {
          for (PlaylistTrack t1 : tracks) {
            if (t.getTrack().getId() != null && t1.getTrack().getId() != null) {
              if (t.getTrack().getId().compareTo(t1.getTrack().getId()) < 0) {
                String tId = "";
                String t1Id = "";
                prep1.setString(1, t.getTrack().getName());
                try (ResultSet rs = prep1.executeQuery()) {
                  if (rs.next()) {
                    tId = rs.getString(1);
                  }
                }
                prep1.setString(1, t1.getTrack().getName());
                try (ResultSet rs = prep1.executeQuery()) {
                  if (rs.next()) {
                    t1Id = rs.getString(1);
                  }
                }
                prep2.setString(1, tId);
                prep2.setString(2, t1Id);
                try (ResultSet rs = prep2.executeQuery()) {
                  if (rs.next()) {
                    int newNum = rs.getInt(1) + 1;
                    System.out.println(rs.getInt(1));
                    System.out.println("newnum" + newNum);
                    String sql = "UPDATE songrelation SET spotify = " + newNum
                        + " WHERE id1 = " + tId + " AND id2 = " + t1Id + ";";
                    try (Statement stmt = Database.createStatement()) {
                      stmt.execute(sql);
                    }
                  } else {
                    String sql = "INSERT INTO songrelation (id1, id2, spotify) VALUES (\'"
                        + tId + "\',\'" + t1Id + "\',\'" + 1 + "\');";
                    try (Statement stmt = Database.createStatement()) {
                      stmt.execute(sql);
                    }
                  }
                }
              }
            }
          }
        }
      }
      System.out.println("done");
      prep.close();
      prep1.close();
      prep2.close();
      prep3.close();
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void populateSongTable() {
    int count = 0;
    try {
      Database.setDatabase("data/music.sqlite3");
      String q = "SELECT id, spotifyId FROM artists WHERE spotifyId != \"\";";
      // querying the database to return a list of lists of all the actors.
      try (PreparedStatement prep = Database.prepareStatement(q)) {
        try (ResultSet rs = prep.executeQuery()) {
          while (rs.next()) {
            int id = rs.getInt(1);
            String spotifyId = rs.getString(2);
            Track[] tracks = SpotifyMethods.getArtistsTopTracks(spotifyId);
            // create a new table
            for (Track t : tracks) {
              String name = t.getName().replaceAll("\"", "\\\\\"")
                  .replaceAll("\'", "\''");
              String sql = "INSERT INTO songs (song, artist) VALUES (\'" + name
                  + "\',\"" + id + "\");";
              try (Statement stmt = Database.createStatement()) {
                // create a new table
                stmt.execute(sql);
              } catch (SQLException e) {
                System.out.println(sql);
                System.out.println(e.getMessage());
              }
            }
            System.out.println(id);
            System.out.println("Count" + count++);
          }
        }
      }
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return;
  }

  public static void populateDbSpotifyId() {
    int count = 0;
    try {
      Database.setDatabase("data/music.sqlite3");
      String q = "SELECT artist FROM artists;";
      // querying the database to return a list of lists of all the actors.
      try (PreparedStatement prep = Database.prepareStatement(q)) {
        try (ResultSet rs = prep.executeQuery()) {
          while (rs.next()) {
            String artist = rs.getString(1);
            String id = SpotifyMethods.searchArtist(artist);
            // create a new table
            String sql = "UPDATE artists SET spotifyId = \"" + id
                + "\" WHERE artist = \"" + artist + "\";";
            try (Statement stmt = Database.createStatement()) {
              // create a new table
              stmt.execute(sql);
            } catch (SQLException e) {
              System.out.println(sql);
              System.out.println(e.getMessage());
            }
            System.out.println(id);
            System.out.println(count++);
          }
        }
      }
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return;
  }

  public static void fillDb() {
    // for (int i = 0; i < seen.size(); i++) {
    String sql = "ALTER TABLE artists\n" + "  ADD spotifyId TEXT;";
    // String sql = "INSERT INTO artists (artist) VALUES (\""
    // + seen.get(i).trim() + "\");";
    try (
        Connection conn = DriverManager
            .getConnection("jdbc:sqlite:data/music.sqlite3");
        Statement stmt = conn.createStatement()) {
      // create a new table
      stmt.execute(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    // }
  }

  public static List<String> createArtistList(File filePath) {
    List<String> lst = artistList(filePath);
    List<String> seen = new ArrayList<>();
    boolean next = false;
    for (int i = 0; i < lst.size(); i++) {
      if (next && !seen.contains(lst.get(i))) {
        seen.add(lst.get(i));
        next = false;
      } else {
        try {
          Integer.parseInt(lst.get(i));
          next = true;
        } catch (NumberFormatException nfe) {
          next = false;
        }
      }
    }
    return seen;
  }

  public static List<String> createList(File filePath) {
    List<String> lst = generateList(filePath);
    List<String> seen = new ArrayList<>();
    boolean next = false;
    for (int i = 0; i < lst.size(); i++) {
      if (next && !seen.contains(lst.get(i))) {
        seen.add(lst.get(i));
        next = false;
      } else {
        try {
          Integer.parseInt(lst.get(i));
          next = true;
        } catch (NumberFormatException nfe) {
          next = false;
        }
      }
    }
    return seen;
  }

  public static List<String> artistList(File file) {
    List<String> lst = new ArrayList<>();
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      InputStreamReader inputStreamReader = new InputStreamReader(
          fileInputStream, "UTF-8");
      BufferedReader br = new BufferedReader(inputStreamReader);
      String st;
      while ((st = br.readLine()) != null) {
        String[] arr = st.split("\\+");
        for (int i = 0; i < arr.length; i++) {
          lst.add(arr[i].split("feat.")[0].split("ft.")[0].split("Feat.")[0]
              .split("Featuring.")[0].split("Ft.")[0].trim());
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return lst;
  }

  public static List<String> generateList(File file) {
    List<String> lst = new ArrayList<>();
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      InputStreamReader inputStreamReader = new InputStreamReader(
          fileInputStream, "UTF-8");
      BufferedReader br = new BufferedReader(inputStreamReader);
      String st;
      while ((st = br.readLine()) != null) {
        String[] arr = st.split("\\t");
        for (int i = 0; i < arr.length; i++) {
          lst.add(arr[i].split("feat.")[0].split("ft.")[0].split("Feat.")[0]
              .split("Featuring.")[0].split("Ft.")[0].trim());
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return lst;
  }

  /**
   * Connect to a sample database.
   *
   * @param fileName
   *          the database file name
   */
  public static void createNewDatabase(String fileName) {
    String url = "jdbc:sqlite:" + fileName;
    try (Connection conn = DriverManager.getConnection(url)) {
      if (conn != null) {
        DatabaseMetaData meta = conn.getMetaData();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Create a new table in the test database.
   *
   * @param fileName
   *          : file path of database
   */
  public static void createNewTable(String fileName) {
    String url = "jdbc:sqlite:" + fileName;
    String sql = "";
    // SQL statement for creating a new table
    // sql = "CREATE TABLE IF NOT EXISTS songrelation (\n"
    // + "id integer PRIMARY KEY AUTOINCREMENT,\n" + "id1 integer,\n"
    // + "id2 integer,\n" + "spotify integer,\n"
    // + "FOREIGN KEY(id1) REFERENCES songs(id),\n"
    // + "FOREIGN KEY(id2) REFERENCES songs(id)\n" + ");";
    // sql = "CREATE TABLE IF NOT EXISTS songs (\n"
    // + " id integer PRIMARY KEY AUTOINCREMENT,\n" + " song text NOT NULL,\n"
    // + " artist INTEGER, FOREIGN KEY(artist) REFERENCES artists(id));";
    // String sql = "CREATE TABLE IF NOT EXISTS artistrelation (\n"
    // + " id1 integer AUTOINCREMENT,\n"
    // + " id2 integer AUTOINCREMENT,\n"
    // + " lastfm integer, \n"
    // + " pandora integer, \n"
    // + " spotify integer, \n"
    // + " musicbrainz integer, "
    // + " PRIMARY KEY (id1, id2)\n);";
    // sql = "CREATE TABLE IF NOT EXISTS songrelation (\n"
    // + " id1 integer AUTOINCREMENT,\n" + " id2 integer AUTOINCREMENT,\n"
    // + " lastfm integer, \n" + " pandora integer, \n"
    // + " spotify integer, \n" + " musicbrainz integer, "
    // + " PRIMARY KEY (id1, id2)\n);";
    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement()) {
      // create a new table
      stmt.execute(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // // CreateMusicDatabase.createNewDatabase("data/music.sqlite3");
  // // CreateMusicDatabase.createNewTable("data/music.sqlite3");
  // List<String> allArtists = new ArrayList<>();
  // File dir = new File("data/songdata");
  // File[] directoryListing = dir.listFiles();
  // if (directoryListing != null) {
  // for (File child : directoryListing) {
  // List<String> data = CreateMusicDatabase.createList(child);
  // for (int i = 0; i < data.size(); i++) {
  // if (!allArtists.contains(data.get(i))) {
  // allArtists.add(data.get(i));
  // }
  // }
  // }
  // } else {
  // // Handle the case where dir is not really a directory.
  // // Checking dir.isDirectory() above would not be sufficient
  // // to avoid race conditions with another process that deletes
  // // directories.
  // }
  // dir = new File("data/genredata");
  // directoryListing = dir.listFiles();
  // if (directoryListing != null) {
  // for (File child : directoryListing) {
  // List<String> data = CreateMusicDatabase.createArtistList(child);
  // for (int i = 0; i < data.size(); i++) {
  // if (!allArtists.contains(data.get(i))) {
  // allArtists.add(data.get(i));
  // }
  // }
  // }
  // } else {
  // // Handle the case where dir is not really a directory.
  // // Checking dir.isDirectory() above would not be sufficient
  // // to avoid race conditions with another process that deletes
  // // directories.
  // }
  // System.out.println(allArtists);
  // System.out.println(allArtists.size());
  // CreateMusicDatabase.fillDb(allArtists);

  // try {
  //
  // String url = "https://accounts.spotify.com/authorize";
  //
  // URL obj = new URL(url);
  // HttpURLConnection con = (HttpURLConnection) obj.openConnection();
  //
  // // optional default is GET
  // con.setRequestMethod("GET");
  //
  // // add request header
  // con.setRequestProperty("client_id", "fcee77f8419342908f021021751a79a3");
  // con.setRequestProperty("response_type", "code");
  // con.setRequestProperty("redirect_uri", "/commonground");
  //
  // int responseCode = con.getResponseCode();
  // System.out.println("\nSending 'GET' request to URL : " + url);
  // System.out.println("Response Code : " + responseCode);
  //
  // BufferedReader in = new BufferedReader(
  // new InputStreamReader(con.getInputStream()));
  // String inputLine;
  // StringBuffer response = new StringBuffer();
  //
  // while ((inputLine = in.readLine()) != null) {
  // response.append(inputLine);
  // }
  // in.close();
  //
  // // print result
  // System.out.println("AAAAA" + response.toString());
  // } catch (Exception e) {
  // System.out.println(e.getMessage());
  // }
  //
  // // try {
  // // String url = "https://api.spotify.com/v1/search";
  // //
  // // URL obj = new URL(url);
  // // HttpURLConnection con = (HttpURLConnection) obj.openConnection();
  // //
  // // // optional default is GET
  // // con.setRequestMethod("GET");
  // //
  // // // add request header
  // // con.setRequestProperty("grant_type", "authorization_code");
  // // con.setRequestProperty("client_id", "fcee77f8419342908f021021751a79a3");
  // // con.setRequestProperty("response_type", "code");
  // // con.setRequestProperty("redirect_uri", "/commonground");
  // // con.setRequestProperty("q", "Michael%20Jackson");
  // //
  // // int responseCode = con.getResponseCode();
  // // System.out.println("\nSending 'GET' request to URL : " + url);
  // // System.out.println("Response Code : " + responseCode);
  // //
  // // BufferedReader in = new BufferedReader(
  // // new InputStreamReader(con.getInputStream()));
  // // String inputLine;
  // // StringBuffer response = new StringBuffer();
  // //
  // // while ((inputLine = in.readLine()) != null) {
  // // response.append(inputLine);
  // // }
  // // in.close();
  // //
  // // // print result
  // // System.out.println("AAAAA" + response.toString());
  // // } catch (Exception e) {
  // // System.out.println(e.getMessage());
  // // }
  //
  // // try {
  // //
  // // String charset = "UTF-8";
  // // String client_id = "fcee77f8419342908f021021751a79a3";
  // // String client_secret = "4b4dba04c9fc46b9ba0faf83c17d56af";
  // //
  // // String urlParams = String.format("q=%s&client_id=%s&client_secret=%s",
  // // URLEncoder.encode("Michael Jackson", "UTF-8"),
  // // URLEncoder.encode(client_id, charset),
  // // URLEncoder.encode(client_secret, charset));
  // // CreateMusicDatabase.executePost("https://api.spotify.com/v1/search",
  // // urlParams);
  // // } catch (UnsupportedEncodingException e1) {
  // // // TODO Auto-generated catch block
  // // e1.printStackTrace();
  // // }

  public static void populateGenres() {
    try {
      Database.setDatabase("data/music.sqlite3");
      String q1 = "SELECT id, spotifyId FROM artists WHERE id > 6756;";
      try (PreparedStatement prep1 = Database.prepareStatement(q1)) {
        try (ResultSet rs = prep1.executeQuery()) {
          while (rs.next()) {
            try (PreparedStatement prep2 = Database.prepareStatement(
                "INSERT INTO artist_genres (artist, genre) VALUES (?,?);")) {
              String[] genres = SpotifyMethods.getArtistGenres(rs.getString(2));
              for (String genre : genres) {
                String[] subGenres = genre.split(" ");
                for (String subGenre : subGenres) {
                  System.out.println(subGenre);
                  prep2.setString(1, rs.getString(1));
                  prep2.setString(2, subGenre);
                  prep2.executeUpdate();
                }
              }
            }
            System.out.println(rs.getString(1));
          }
        }
      }
    } catch (SQLException | FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
    System.out.println("finished");
  }

  public static void populateTrackIds() {
    try {
      Database.setDatabase("data/music.sqlite3");
      String q1 = "SELECT songs.id, songs.song, artists.spotifyId FROM songs, artists WHERE songs.artist = artists.id AND songs.id > 53266;";
      try (PreparedStatement prep1 = Database.prepareStatement(q1)) {
        try (ResultSet rs = prep1.executeQuery()) {
          while (rs.next()) {
            System.out.println(
                SpotifyMethods.searchTrack(rs.getString(2), rs.getString(3)));
            String q2 = "UPDATE songs SET spotifyId = ? WHERE id = ?";
            PreparedStatement prep2 = Database.prepareStatement(q2);
            prep2.setString(1,
                SpotifyMethods.searchTrack(rs.getString(2), rs.getString(3)));
            prep2.setString(2, rs.getString(1));
            prep2.executeUpdate();
          }
        }
      }
      System.out.println("finished");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
