package edu.brown.cs.commonground.music.playlist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.main.Server;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.music.song.SongProxy;
import edu.brown.cs.commonground.users.group.Group;
import edu.brown.cs.commonground.users.group.GroupProxy;

/**
 * Class representing a proxy for a playlist's data. Only retrieves playlist's
 * data (which it stores in an internal PlaylistBean) when needed - otherwise,
 * just keeps track of the playlist's ID.
 */
public class PlaylistProxy implements Playlist {
  private int id;
  private Playlist internal = null;

  /**
   * Constructor for PlaylistProxy.
   *
   * @param id
   *          : ID of playlist in database
   */
  public PlaylistProxy(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getCode() {
    return Server.encrypt(id);
  }

  @Override
  public String getName() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getName();
  }

  @Override
  public String getLinkName() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getLinkName();
  }

  @Override
  public Group getGroup() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getGroup();
  }

  @Override
  public List<Song> getSongs() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getSongs();
  }

  @Override
  public int getReqSize() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return -1;
    }
    return internal.getReqSize();
  }

  @Override
  public double getPercentKnown() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return Double.NaN;
    }
    return internal.getPercentKnown();
  }

  @Override
  public List<String> getGenres() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getGenres();
  }

  /**
   * Fills internal PlaylistBean from cache if possible, and creates a new
   * PlaylistBean otherwise.
   *
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  private void fill() throws IllegalStateException, SQLException {
    fillFromCache();
    if (internal == null) {
      // Gets name, group, size, and percent known requested for this playlist.
      String name;
      Group group;
      int reqSize;
      double percentKnown;
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT name, grp, size, percent_known FROM playlists "
              + "WHERE id = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            name = rs.getString(1);
            group = new GroupProxy(rs.getInt(2));
            reqSize = rs.getInt(3);
            percentKnown = rs.getDouble(4);
          } else {
            throw new SQLException(
                "ERROR: Playlist does not have a name or group"
                    + "in the playlists table.");
          }
        }
      }

      // Gets list of songs in this playlist.
      List<Song> songs = new ArrayList<>();
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT song FROM playlist_songs " + "WHERE playlist = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            songs.add(new SongProxy(rs.getInt(1)));
          }
        }
      }

      // Gets requested genres of this playlist.
      List<String> genres = new ArrayList<>();
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT genre FROM playlist_genres " + "WHERE playlist = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            genres.add(rs.getString(1));
          }
        }
      }

      internal = new PlaylistBean(id, name, group, songs, reqSize, percentKnown,
          genres);
      Database.putInCache("Playlist", id, internal);
    }
  }

  /**
   * Fills internal PlaylistBean from cache if possible.
   */
  private void fillFromCache() {
    if (internal == null) {
      internal = (Playlist) Database.getFromCache("Playlist", id);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Playlist.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Playlist playlist = (Playlist) object;
    return id == playlist.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
