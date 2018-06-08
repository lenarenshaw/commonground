package edu.brown.cs.commonground.artist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.database.Database;

/**
 * Class representing a proxy for an artist's data. Only retrieves artist's data
 * (which it stores in an internal ArtistBean) when needed - otherwise, just
 * keeps track of the artist's ID.
 */
public class ArtistProxy implements Artist {
  private int id;
  private Artist internal;

  /**
   * Constructor for ArtistProxy.
   *
   * @param id
   *          : id of artist in database
   */
  public ArtistProxy(int id) {
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
  public Set<String> getGenres() {
    try {
      fill();
    } catch (IllegalStateException | SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return internal.getGenres();
  }

  /**
   * Fills internal ArtistBean from cache if possible, and creates a new
   * ArtistBean otherwise.
   *
   * @throws IllegalStateException
   *           : thrown if database has not been loaded yet
   * @throws SQLException
   *           : thrown if SQL error occurs
   */
  private void fill() throws IllegalStateException, SQLException {
    fillFromCache();
    if (internal == null) {
      // Gets artist's name.
      String name;
      try (PreparedStatement ps = Database
          .prepareStatement("SELECT artist FROM artists WHERE id = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            name = rs.getString(1);
          } else {
            System.out.println("ARTIST ID = " + id);
            throw new SQLException(
                "ERROR: Artist does not have an artist in the artists table.");
          }
        }
      }

      // Gets artist's genres.
      Set<String> genres = new HashSet<>();
      try (PreparedStatement ps = Database.prepareStatement(
          "SELECT genre FROM artist_genres WHERE artist = ?;")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            genres.add(rs.getString(1));
          }
        }
      }

      internal = new ArtistBean(id, name, genres);
      Database.putInCache("Artist", id, internal);
    }
  }

  /**
   * Fills internal UserBean from cache if possible.
   */
  private void fillFromCache() {
    if (internal == null) {
      internal = (Artist) Database.getFromCache("Artist", id);
    }
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
