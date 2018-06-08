package edu.brown.cs.commonground.users.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.music.song.SongProxy;
import edu.brown.cs.commonground.users.group.Group;
import edu.brown.cs.commonground.users.group.GroupProxy;

/**
 * Class representing a proxy for a user's data. Only retrieves user's data
 * (which it stores in an internal UserBean) when needed - otherwise, just keeps
 * track of the user's ID.
 */
public class UserProxy implements User {
  private int id;
  private User internal = null;

  /**
   * Constructor for UserProxy.
   *
   * @param id
   *          : ID of user in database
   */
  public UserProxy(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
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
  public String getDisplayName() {
	  try {
		  fill();
	  } catch (IllegalStateException | SQLException e) {
		  System.out.println(e.getMessage());
		  return null;
	  }
	  return internal.getDisplayName();
  }

  @Override
  public Set<Group> getGroups() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getGroups();
  }

  @Override
  public Set<Song> getAllSongs() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getAllSongs();
  }

  @Override
  public Set<Song> getSongs(List<String> genre) {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getSongs(genre);
  }

  @Override
  public String getSpotifyId() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getSpotifyId();
  }

  /**
   * Fills internal UserBean from cache if possible, and creates a new UserBean
   * otherwise.
   *
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  private void fill() throws IllegalStateException, SQLException {
    // fillFromCache();
    // if (internal == null) {
    // Gets user's name.
    String name;
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT spotifyId FROM users WHERE id = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          name = rs.getString(1);
        } else {
          throw new SQLException(
              "ERROR: User does not have a spotifyId in the users table.");
        }
      }
    }

    // Gets user's groups.
    Set<Group> groups = new HashSet<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT grp FROM group_users WHERE user = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          groups.add(new GroupProxy(rs.getInt(1)));
        }
      }
    }

    // Gets user's songs.
    Set<Song> songs = new HashSet<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT song FROM user_songs WHERE user = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          songs.add(new SongProxy(rs.getInt(1)));
        }
      }
    }

    // Gets user's Spotify ID.
    String spotifyId;
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT spotifyId FROM users WHERE id = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          spotifyId = rs.getString(1);
        } else {
          throw new SQLException(
              "ERROR: User does not have a spotifyId in the users table.");
        }
      }
    }
    
    String displayName;
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT display_name FROM users WHERE id = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          displayName = rs.getString(1);
        } else {
          throw new SQLException(
              "ERROR: User does not have a display name in the users table.");
        }
      }
    }

    internal = new UserBean(id, name, groups, songs, spotifyId, displayName);
    // Database.putInCache("User", id, internal);
    // }
  }

  /**
   * Fills internal UserBean from cache if possible.
   */
  private void fillFromCache() {
    if (internal == null) {
      internal = (User) Database.getFromCache("User", id);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !User.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    User user = (User) object;
    return id == user.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
