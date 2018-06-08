package edu.brown.cs.commonground.music.song;

import edu.brown.cs.commonground.artist.Artist;
import edu.brown.cs.commonground.graph.Vertex;
import edu.brown.cs.commonground.music.similarity.Similarity;
import edu.brown.cs.commonground.orm.Entity;

/**
 * Interface representing a song. Extends the Entity interface.
 *
 * @author calderhoover
 *
 */
public interface Song extends Entity, Vertex<Song, Similarity> {

  /**
   * Gets the name of the song.
   *
   * @return name of the song
   */
  String getName();

  /**
   * Gets the artist of the song.
   *
   * @return artist of the song
   */
  Artist getArtist();

  String getSpotifyId();

  /**
   * Gets song of a specified ID.
   *
   * @param id
   *          : ID of the song
   * @return song of this ID if the song exists, null otherwise
   */
  static Song ofId(int id) {
    return new SongProxy(id);
  }
}
