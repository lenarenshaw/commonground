package edu.brown.cs.commonground.users.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.music.song.Song;
import edu.brown.cs.commonground.users.group.Group;

/**
 * Class for internal data of a UserProxy. Holds instance variables with the
 * user's ID in the database, their name, and their groups.
 */
public class UserBean implements User {
  private int id;
  private String name;
  private String displayName;
  private Set<Group> groups;
  private Set<Song> songs;
  private String spotifyId;

  /**
   * Constructor for UserBean.
   *
   * @param id
   *          : ID of user in database
   * @param name
   *          : name of user
   * @param groups
   *          : set of this user's groups
   * @param songs
   *          : set of this user's saved songs in Spotify
   * @param spotifyId
   *          : user's Spotify ID
   */
  public UserBean(int id, String name, Set<Group> groups, Set<Song> songs,
      String spotifyId, String displayName) {
    this.id = id;
    this.name = name;
    this.groups = groups;
    this.songs = songs;
    this.spotifyId = spotifyId;
    this.displayName = displayName;
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
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public Set<Group> getGroups() {
    return Collections.unmodifiableSet(groups);
  }

  @Override
  public Set<Song> getAllSongs() {
    return Collections.unmodifiableSet(songs);
  }

  @Override
  public Set<Song> getSongs(List<String> genre) {
    Set<Song> result = new HashSet<>();
    for (Song s : songs) {
      for (String g : s.getArtist().getGenres()) {
        if (genre.contains(g)) {
          result.add(s);
        }
      }
    }
    return result;
  }

  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !User.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    User user = (User) object;
    return id == user.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
