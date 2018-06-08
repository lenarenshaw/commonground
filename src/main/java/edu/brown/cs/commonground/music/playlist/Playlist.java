package edu.brown.cs.commonground.music.playlist;

import java.util.List;

import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.orm.Entity;
import edu.brown.cs.commonground.users.group.Group;

/**
 * Interface representing a playlist. Extends the Entity interface.
 *
 * @author calderhoover
 *
 */
public interface Playlist extends Entity {

  /**
   * Gets the name of the playlist.
   *
   * @return name of the playlist
   */
  String getName();

  /**
   * Gets the name of the playlist.
   *
   * @return name of the playlist
   */
  String getLinkName();

  /**
   * Gets the group that generated this playlist.
   *
   * @return Set of Actors in this Movie
   */
  Group getGroup();

  /**
   * Gets a list of this playlist's songs in order.
   *
   * @return ordered list of playlist's songs
   */
  List<Song> getSongs();

  /**
   * Gets the public code for this playlist based on its ID in the database.
   * Calls Server's encrypt method.
   *
   * @return public code for this playlist
   */
  int getCode();

  /**
   * Gets requested size of playlist.
   *
   * @return requested size of playlist
   */
  int getReqSize();

  /**
   * Gets requested percent known of playlist.
   *
   * @return requested percent known of playlist
   */
  double getPercentKnown();

  /**
   * Gets requested genres of playlist.
   *
   * @return requested genres of playlist
   */
  List<String> getGenres();

  /**
   * Gets playlist of a specified ID.
   *
   * @param id
   *          : ID of the playlist in database
   * @return playlist of this ID if the playlist exists, null otherwise
   */
  static Playlist ofId(int id) {
    return new PlaylistProxy(id);
  }
}
