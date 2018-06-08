package edu.brown.cs.commonground.users.group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.main.Server;
import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.music.playlist.PlaylistProxy;
import edu.brown.cs.commonground.users.user.User;
import edu.brown.cs.commonground.users.user.UserProxy;

/**
 * Class representing a proxy for a group's data. Only retrieves group's data
 * (which it stores in an internal GroupBean) when needed - otherwise, just
 * keeps track of the group's ID.
 */
public class GroupProxy implements Group {
  private int id;
  private Group internal = null;

  /**
   * Constructor for GroupProxy.
   *
   * @param id
   *          : ID of group in database
   */
  public GroupProxy(int id) {
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
  public Set<User> getLeaders() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getLeaders();
  }

  @Override
  public Set<User> getUsers() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getUsers();
  }

  @Override
  public Set<Playlist> getPlaylists() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getPlaylists();
  }

  /**
   * Fills internal GroupBean from cache if possible, and creates a new
   * GroupBean otherwise.
   *
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  private void fill() throws IllegalStateException, SQLException {
    // fillFromCache();
    // if (internal == null) {
    // Gets group's name and leaders.
    String name = null;
    Set<User> leaders = new HashSet<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT name, leader FROM groups WHERE id = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          name = rs.getString(1);
          leaders.add(new UserProxy(rs.getInt(2)));
        }
      }
    }

    // Gets group's users.
    Set<User> users = new HashSet<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT user FROM group_users WHERE grp = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          users.add(new UserProxy(rs.getInt(1)));
        }
      }
    }

    // Gets group's playlists.
    Set<Playlist> playlists = new HashSet<>();
    try (PreparedStatement ps = Database
        .prepareStatement("SELECT id FROM playlists WHERE grp = ?;")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          playlists.add(new PlaylistProxy(rs.getInt(1)));
        }
      }
    }

    internal = new GroupBean(id, name, leaders, users, playlists);
    // Database.putInCache("Group", id, internal);
    // }
  }

  /**
   * Fills internal GroupBean from cache if possible.
   */
  private void fillFromCache() {
    if (internal == null) {
      internal = (Group) Database.getFromCache("Group", id);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Group.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Group group = (Group) object;
    return id == group.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public int getCode() {
    return Server.encrypt(id);
  }
}
