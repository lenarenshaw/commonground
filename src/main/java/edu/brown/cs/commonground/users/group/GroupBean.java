package edu.brown.cs.commonground.users.group;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import edu.brown.cs.commonground.main.Server;
import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.users.user.User;

/**
 * Class for internal data of a GroupProxy. Holds instance variables with the
 * group's ID in the database, the group leader's ID in the database, and a set
 * with the group's playlists.
 */
public class GroupBean implements Group {
  private int id;
  private String name;
  private Set<User> leaders;
  private Set<User> users;
  private Set<Playlist> playlists;

  /**
   * Constructor for GroupBean.
   *
   * @param id
   *          : ID of group in database
   * @param name
   *          : name of group in database
   * @param leaders
   *          : set of group's leaders
   * @param users
   *          : set of group's users
   * @param playlists
   *          : set of this group's playlists
   */
  public GroupBean(int id, String name, Set<User> leaders, Set<User> users,
      Set<Playlist> playlists) {
    this.id = id;
    this.name = name;
    this.leaders = leaders;
    this.users = users;
    this.playlists = playlists;
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
  public String getLinkName() {
    return name.replace("\"", "").replaceAll("\'", "");
  }

  @Override
  public Set<User> getLeaders() {
    return leaders;
  }

  @Override
  public Set<User> getUsers() {
    return users;
  }

  @Override
  public Set<Playlist> getPlaylists() {
    return Collections.unmodifiableSet(playlists);
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !Group.class.isAssignableFrom(object.getClass())) {
      return false;
    }
    Group group = (Group) object;
    return id == group.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public int getCode() {
    return Server.encrypt(id);
  }
}
