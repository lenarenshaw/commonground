package edu.brown.cs.commonground.artist;

import java.util.Set;

import edu.brown.cs.commonground.orm.Entity;

/**
 * Interface representing a artist. Extends the Entity interface.
 *
 */
public interface Artist extends Entity {

  /**
   * Gets the name of the artist.
   *
   * @return name of the artist
   */
  String getName();

  /**
   * Gets the artist of the artist.
   *
   * @return artist of the artist
   */
  Set<String> getGenres();
}
