package edu.brown.cs.commonground.users.user;

import java.util.List;
import java.util.Set;

import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.orm.Entity;
import edu.brown.cs.commonground.users.group.Group;

/**
 * Interface representing a Movie. Extends the Entity interface.
 *
 */
public interface User extends Entity {

  /**
   * Gets the name of the user.
   *
   * @return name of the user
   */
  String getName();

  /**
   * Gets the Spotify Display name for a user.
   * 
   * @return Display name
   */
  String getDisplayName();

  /**
   * Gets a set of this user's groups.
   *
   * @return set of user's groups
   */
  Set<Group> getGroups();

  /**
   * Gets a set of this user's saved songs in Spotify.
   *
   * @return set of user's saved songs in Spotify
   */
  Set<Song> getAllSongs();

  /**
   * Gets a set of this user's saved songs in Spotify.
   *
   * @param genre
   *          of the types of genres of songs to get
   * @return set of user's saved songs in Spotify
   */
  Set<Song> getSongs(List<String> genre);

  /**
   * Gets a user's Spotify ID.
   *
   * @return user's Spotify ID
   */
  String getSpotifyId();

  /**
   * Gets user of a specified ID.
   *
   * @param id
   *          : ID of the user
   * @return user of this ID if the user exists, null otherwise
   */
  static User ofId(int id) {
    return new UserProxy(id);
  }
}
