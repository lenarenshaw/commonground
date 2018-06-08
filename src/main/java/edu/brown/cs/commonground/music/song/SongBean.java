package edu.brown.cs.commonground.music.song;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.artist.Artist;
import edu.brown.cs.commonground.music.similarity.Similarity;

/**
 * Class for internal data of a SongProxy. Holds instance variables with the
 * song's ID in the database, its name, its artist, and a set of users who have
 * saved it in Spotify.
 *
 * @author calderhoover
 *
 */
public class SongBean implements Song {
  private int id;
  private String name;
  private Artist artist;
  private Set<Similarity> edges;
  private String spotifyId;

  /**
   * Constructor for MovieBean.
   *
   * @param id
   *          : ID of movie in database
   * @param name
   *          : name of movie
   * @param artist
   *          : artist of song
   * @param edges
   *          : Set of Similarities between this song and similar songs
   */
  public SongBean(int id, String name, Artist artist, Set<Similarity> edges,
      String spotifyId) {
    this.id = id;
    this.name = name;
    this.artist = artist;
    this.edges = edges;
    this.spotifyId = spotifyId;
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
  public Artist getArtist() {
    return artist;
  }

  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  @Override
  public Set<Similarity> getEdges() {
    return Collections.unmodifiableSet(edges);
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Song.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Song song = (Song) object;
    return id == song.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
