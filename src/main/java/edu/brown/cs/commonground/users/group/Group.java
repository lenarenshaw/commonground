package edu.brown.cs.commonground.users.group;

import java.util.Set;

import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.orm.Entity;
import edu.brown.cs.commonground.users.user.User;

/**
 * Interface representing a group. Extends the Entity interface.
 *
 * @author calderhoover
 *
 */
public interface Group extends Entity {

  /**
   * Gets the name of the group.
   *
   * @return name of the group
   */
  String getName();

  /**
   * Gets a set of this group's leaders.
   *
   * @return set of this group's leaders
   */
  Set<User> getLeaders();

  /**
   * Gets the name of the group.
   *
   * @return name of the group
   */
  String getLinkName();

  /**
   * Gets a set of this group's users.
   *
   * @return set of this group's users
   */
  Set<User> getUsers();

  /**
   * Gets a set of this group's generated playlists.
   *
   * @return set of playlists in this group
   */
  Set<Playlist> getPlaylists();

  /**
   * Gets the secret code for this group; based on id.
   *
   * @return an integer
   */
  int getCode();

  /**
   * Gets group of a specified ID.
   *
   * @param id
   *          : ID of the group in database
   * @return group of this ID if the group exists, null otherwise
   */
  static Group ofId(int id) {
    return new GroupProxy(id);
  }
}
