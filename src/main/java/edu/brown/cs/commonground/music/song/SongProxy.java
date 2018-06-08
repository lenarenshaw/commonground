package edu.brown.cs.commonground.music.song;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.artist.Artist;
import edu.brown.cs.commonground.artist.ArtistProxy;
import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.music.similarity.Similarity;

/**
 * Class representing a proxy for a song's data. Only retrieves song's data
 * (which it stores in an internal SongBean) when needed - otherwise, just keeps
 * track of the movie's ID.
 */
public class SongProxy implements Song {
  private int id;
  private Song internal = null;

  /**
   * Constructor for SongProxy.
   *
   * @param id
   *          : ID of song in database
   */
  public SongProxy(int id) {
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
  public String getSpotifyId() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getSpotifyId();
  }

  @Override
  public Artist getArtist() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getArtist();
  }

  @Override
  public Set<Similarity> getEdges() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getEdges();
  }

  /**
   * Fills internal MovieBean from cache if possible, and creates a new
   * MovieBean otherwise.
   *
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  private void fill() throws IllegalStateException, SQLException {
    fillFromCache();
    if (internal == null) {
      // Gets song's name and artist.
      String name;
      Artist artist;
      String spotifyId;
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT song, artist, spotifyId FROM songs WHERE id = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            name = rs.getString(1);
            artist = new ArtistProxy(rs.getInt(2));
            spotifyId = rs.getString(3);
          } else {
            System.out.println(id);
            System.out.println(rs.getString(1));
            System.out.println(rs.getInt(2));
            System.out.println(rs.getString(3));
            throw new SQLException("ERROR: Song does not have a name or artist "
                + "in the songs table.");
          }
        }
      }
      Set<Similarity> edges = new HashSet<>();

      // Adds all similarities where this song is the first ID.
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT id2, lastfm, pandora, spotify, musicbrainz, gracenote "
              + "FROM songrelation WHERE id1 = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            int id2 = rs.getInt(1);
            int lastfm = rs.getInt(2);
            int pandora = rs.getInt(3);
            int spotify = rs.getInt(4);
            int musicbrainz = rs.getInt(5);
            int gracenote = rs.getInt(6);
            edges.add(new Similarity(this, new SongProxy(id2), lastfm, pandora,
                spotify, musicbrainz, gracenote));
          }
        }
      }

      // Adds all similarities where this song is the second ID.
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT id1, lastfm, pandora, spotify, musicbrainz, gracenote "
              + "FROM songrelation WHERE id2 = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            int id1 = rs.getInt(1);
            int lastfm = rs.getInt(2);
            int pandora = rs.getInt(3);
            int spotify = rs.getInt(4);
            int musicbrainz = rs.getInt(5);
            int gracenote = rs.getInt(6);
            edges.add(new Similarity(this, new SongProxy(id1), lastfm, pandora,
                spotify, musicbrainz, gracenote));
          }
        }
      }
      internal = new SongBean(id, name, artist, edges, spotifyId);
      Database.putInCache("Song", id, internal);
    }
  }

  /**
   * Fills internal SongBean from cache if possible.
   */
  private void fillFromCache() {
    if (internal == null) {
      internal = (Song) Database.getFromCache("Song", id);
    }
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
