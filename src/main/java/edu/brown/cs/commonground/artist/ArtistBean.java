package edu.brown.cs.commonground.artist;

import java.util.Objects;
import java.util.Set;

/**
 * Class for internal data of an ArtistProxy. Holds instance variables with the
 * artist's ID in the database, their name, and their genres.
 */
public class ArtistBean implements Artist {
  private int id;
  private String name;
  private Set<String> genres;

  /**
   * Constructor for ArtistBean.
   *
   * @param id
   *          : id of artist in database
   * @param name
   *          : name of artist
   * @param genres
   *          : artist's genres
   */
  public ArtistBean(int id, String name, Set<String> genres) {
    this.id = id;
    this.name = name;
    this.genres = genres;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Set<String> getGenres() {
    return genres;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Artist.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Artist artist = (Artist) object;
    return id == artist.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
