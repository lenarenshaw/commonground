package edu.brown.cs.commonground.music.playlist;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.brown.cs.commonground.main.Server;
import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.users.group.Group;

/**
 * Class for internal data of a PlaylistProxy. Holds instance variables with the
 * playlist's ID in the database, its group, and a list of the playlist's songs.
 */
public class PlaylistBean implements Playlist {
  private int id;
  private String name;
  private Group group;
  private List<Song> songs;
  private int reqSize;
  private double percentKnown;
  private List<String> genres;

  /**
   * Constructor for PlaylistProxy.
   *
   * @param id
   *          : ID of playlist in database
   * @param name
   *          : name of playlist in database
   * @param group
   *          : group that generated this playlist
   * @param songs
   *          : ordered list of songs in this playlist
   * @param reqSize
   *          : requested size of playlist
   * @param percentKnown
   *          : requested percent known of playlist
   * @param genres
   *          : requested genres of playlist
   */
  public PlaylistBean(int id, String name, Group group, List<Song> songs,
      int reqSize, double percentKnown, List<String> genres) {
    this.id = id;
    this.name = name;
    this.group = group;
    this.songs = songs;
    this.reqSize = reqSize;
    this.percentKnown = percentKnown;
    this.genres = genres;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getCode() {
    return Server.encrypt(id);
  }

  @Override
  public String getLinkName() {
    return name.replace("\"", "").replaceAll("\'", "");
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Group getGroup() {
    return group;
  }

  @Override
  public List<Song> getSongs() {
    return Collections.unmodifiableList(songs);
  }

  @Override
  public int getReqSize() {
    return reqSize;
  }

  @Override
  public double getPercentKnown() {
    return percentKnown;
  }

  @Override
  public List<String> getGenres() {
    return genres;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Playlist.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Playlist playlist = (Playlist) object;
    return id == playlist.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
