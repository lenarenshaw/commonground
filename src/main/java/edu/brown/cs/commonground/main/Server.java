package edu.brown.cs.commonground.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;

import edu.brown.cs.commonground.database.Database;
import edu.brown.cs.commonground.database.MusicDatabase;
import edu.brown.cs.commonground.externalAPIs.SpotifyUserMethods;
import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.music.playlist.PlaylistGenerator;
import edu.brown.cs.commonground.users.group.Group;
import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Set up a spark server and web application.
 */
public final class Server {

  // Maps session id to user id.
  private static Map<String, String> userIds = new HashMap<>();

  // Spotify API authentication info.
  // private static final String redirect_uri =
  // "http://localhost:4567/commonground/loading";
  private static final String redirect_uri = "http://b5e85938.ngrok.io/commonground/loading";
  private static final String client_id = "fcee77f8419342908f021021751a79a3";
  private static final String client_secret = "4b4dba04c9fc46b9ba0faf83c17d56af";

  // Secret numbers for encryption and decryption.
  private static final int secretAdd = 103837;
  private static final int secretMultiply = 7;

  private static final Gson GSON = new Gson();

  /**
   * Empty private constructor to satisfy checkstyle, which demands that a
   * utility class not have a public or default constructor.
   */
  private Server() {
  }

  /**
   * Run spark server.
   *
   * @param port
   *          port number for server
   */
  public static void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    Spark.get("/commonground", new FrontHandler(), freeMarker);
    Spark.get("/commonground/user/:spotifyid", new UserPageHandler(),
        freeMarker);
    Spark.get("/commonground/newgroup", new MakeGroupHandler(), freeMarker);
    Spark.get("/commonground/group/:groupurl", new GroupPageHandler(),
        freeMarker);
    Spark.get("/commonground/welcome", new WelcomeHandler(), freeMarker);

    Spark.get("/commonground/playlist/:playlisturl", new PlaylistPageHandler(),
        freeMarker);
    Spark.get("/commonground/joingroup", new JoinGroupPageHandler(),
        freeMarker);
    Spark.post("/addgroup", new AddGroupHandler());
    Spark.post("/joingroup", new JoinGroupHandler());
    Spark.post("/addplaylist", new AddPlaylistHandler());
    Spark.get("/commonground/loading", new LoadingPageHandler(), freeMarker);
    Spark.post("/addPlaylistToSpotify", new AddPlaylistToSpotifyHandler());
    Spark.post("/refreshSongs", new RefreshHandler());
    Spark.get("/commonground/newplaylist", new MakePlaylistPageHandler(),
        freeMarker);
    Spark.get("/commonground/pastplaylists/:groupurl",
        new PastPlaylistsPageHandler(), freeMarker);
    Spark.post("/refreshplaylist", new RefreshPlaylistHandler());
    Spark.post("/loadingSongs", new LoadSongsHandler());
  }

  /**
   * Encrypts a given id.
   *
   * @param id
   *          : id to encrypt
   * @return encrypted id
   */
  public static int encrypt(int id) {
    return secretMultiply * id + secretAdd;
  }

  /**
   * Decrypts a given code.
   *
   * @param code
   *          : code to decrypt
   * @return id produced by decrypting code
   */
  public static double decrypt(int code) {
    return (code - secretAdd) / (double) secretMultiply;
  }

  private static String getInfo(String authToken, String url) throws Exception {

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    // optional default is GET
    con.setRequestMethod("GET");

    // add request header
    con.setRequestProperty("Authorization", "Bearer " + authToken);

    int responseCode = con.getResponseCode();
    // System.out.println("\nSending 'GET' request to URL : " + url);
    // System.out.println("authentication token : " + authToken);
    //
    // System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    // print result
    // System.out.println("AAAAA" + response.toString());

    return response.toString();
  }

  private static Map<String, String> getRefreshAccess(String authCode)
      throws IOException {

    String url = "https://accounts.spotify.com/api/token";
    String charset = "UTF-8";
    String grant_type = "authorization_code";

    String urlParams = String.format(
        "grant_type=%s&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
        URLEncoder.encode(grant_type, charset),
        URLEncoder.encode(authCode, charset),
        URLEncoder.encode(redirect_uri, charset),
        URLEncoder.encode(client_id, charset),
        URLEncoder.encode(client_secret, charset));

    byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
    int postDataLength = postData.length;

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setInstanceFollowRedirects(false);
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    con.setRequestProperty("charset", charset);
    con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
    con.setUseCaches(false);

    con.setDoOutput(true);

    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(urlParams);
    wr.flush();
    wr.close();

    int responseCode = con.getResponseCode();
    // System.out.println("\nSending 'GET' request to URL : " + url + "?"
    // + urlParams);
    // System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    String parsedResponseString = response.toString().replace("{", "")
        .replace("}", "").replace("\"", "");

    Map<String, String> responseMap = Splitter.on(",")
        .withKeyValueSeparator(":").split(parsedResponseString);

    return responseMap;
  }

  private static boolean addUserData(User user)
      throws SQLException, FileNotFoundException {
    Database.setDatabase("data/music.sqlite3");
    String spotifyId = user.getId(); // username
    String displayName = user.getDisplayName();
    if (displayName == null || displayName.equals("")) {
      System.out.println("Display name is null");
      displayName = spotifyId;
    }
    System.out.println("ID: " + spotifyId + " Display Name: " + displayName);
    return MusicDatabase.addUser(spotifyId, displayName);
  }

  private static void addUserSongs(String spotifyId, Track[] topTracks) {
    for (Track t : topTracks) {
      MusicDatabase.addUserTrack(spotifyId, t.getName(),
          t.getArtists()[0].getName());
    }
  }

  /**
   * Handles adding a new group.
   */
  private static class AddGroupHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
      String spotifyId = req.session().attribute("userId");
      QueryParamsMap qm = req.queryMap();
      String groupName = qm.value("groupname");
      int groupId = MusicDatabase.addGroup(spotifyId, groupName);
      String dbUserId = MusicDatabase.getUserId(spotifyId);
      System.out.println("group Id = " + groupId);
      System.out.println("spotifyID = " + spotifyId);
      System.out.println("db ID = " + dbUserId);
      System.out.println(MusicDatabase.addUserToGroup(groupId, dbUserId));
      // secret code obtained by the following formula: 5*groupNum +
      // secretNumber
      int code = encrypt(groupId);
      Map<String, Object> variables = ImmutableMap.of("code", code, "name",
          spotifyId);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles joining a new group.
   */
  private static class JoinGroupHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {

      String message = "";
      String spotifyId = req.session().attribute("userId");

      String dbUserId = "";
      try {
        dbUserId = MusicDatabase.getUserId(spotifyId);
      } catch (IllegalStateException | SQLException e) {
        System.out.println(e.getMessage());
      }
      QueryParamsMap qm = req.queryMap();
      String groupCode = qm.value("groupcode");
      double groupId = decrypt(Integer.parseInt(groupCode));

      int status = 3;
      String groupName = "";
      String link = "";
      if ((int) groupId == Math.floor(groupId)) {
        try {
          status = MusicDatabase.addUserToGroup((int) groupId, dbUserId);
        } catch (IllegalStateException | SQLException e) {
          System.out.println("A " + e.getMessage());
        }

        try {
          groupName = MusicDatabase.getGroupFromId((int) groupId);
        } catch (IllegalStateException | SQLException e) {
          System.out.println("B " + e.getMessage());
        }
      } else {
        System.out.println("Bad id decryption.");
        status = 1;
      }
      if (status == 0) {
        link = "/commonground/group/" + groupName + "?" + "code=" + groupCode;
        message = "Successfully joined group: " + groupName
            + ". Click the link below to go to the group!";
      } else if (status == 1) {
        message = "ERROR: Invalid code.";

      } else if (status == 2) {
        message = "ERROR: You are already in this group.";

      }
      Map<String, Object> variables = ImmutableMap.of("message", message,
          "link", link, "name", spotifyId);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles page after having joined a group.
   */
  private static class JoinGroupPageHandler implements TemplateViewRoute {
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", req.session().attribute("userId"));
        return new ModelAndView(variables, "joingroup.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles adding a playlist to Spotify.
   */
  private static class AddPlaylistToSpotifyHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {

      QueryParamsMap qm = req.queryMap();
      // Playlist playlist = qm.get

      Map<String, String> tokens = req.session().attribute("authTokens");
      System.out.println("Access token: " + tokens.get("access_token"));
      System.out.println("parsing int");

      int playlistID = Integer.parseInt(qm.value("playlistID"));
      Playlist newPlaylist = Playlist.ofId(playlistID);
      String spotifyPlaylistId = SpotifyUserMethods
          .addPlaylistToSpotify(tokens.get("access_token"), newPlaylist);
      Map<String, Object> variables = ImmutableMap.of("code", tokens,
          "spotifyPlaylistId", spotifyPlaylistId, "userName",
          req.session().attribute("userId"));
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles the welcome page.
   */
  private static class WelcomeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) throws Exception {
      if (req.session().attribute("loggedIn") != null) {
        Map<String, String> tokens = req.session().attribute("authTokens");
        String accessToken = tokens.get("access_token");
        Track[] topTracks = SpotifyUserMethods.getUsersTopTracks(accessToken);
        User userProfile = SpotifyUserMethods.getUserProfile(accessToken);
        String userId = userProfile.getId();
        String displayName = userProfile.getDisplayName();
        if (displayName == null) {
          System.out.println("Display name is null in welcome handler");
          displayName = userId;
        } else {
          System.out.println("Display name: " + displayName);
        }
        req.session().attribute("userId", userId);
        try {
          if (addUserData(userProfile)
              || (req.session().attribute("refresh") != null
                  && ((boolean) req.session().attribute("refresh")))) {
            addUserSongs(userProfile.getId(), topTracks);
            req.session().attribute("refresh", false);
          }
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
        String name = userProfile.getId();
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", name, "displayName", displayName);
        return new ModelAndView(variables, "welcomeBack.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles the start page.
   */
  private static class FrontHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) throws IOException {
      QueryParamsMap qm = req.queryMap();
      String logout = qm.value("logout");
      if (logout != null && logout.equals("true")) {
        req.session().removeAttribute("loggedIn");
        req.session().removeAttribute("userId");
        System.out.println("successfully logged out.");
      }
      Map<String, Object> variables = ImmutableMap.of("title", "CommonGround");
      return new ModelAndView(variables, "startPage.ftl");
    }
  }

  /**
   * Handles adding a new group.
   */
  private static class MakeGroupHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", req.session().attribute("userId"));
        return new ModelAndView(variables, "makeGroup.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles user pages.
   */
  private static class UserPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {

        String spotifyId = req.params(":spotifyid");
        System.out.println("Requested user id is: " + spotifyId);
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", req.session().attribute("userId"), "user",
            MusicDatabase.getUserBySpotifyId(spotifyId));
        return new ModelAndView(variables, "userpage.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles group pages.
   */
  private static class GroupPageHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        String url = req.params(":groupurl");
        String name = url.split("\\?")[0];
        QueryParamsMap qm = req.queryMap();
        String code = qm.value("code");
        String leaderSpotifyId = req.session().attribute("userId");

        System.out.println(name);
        // TODO: decide whether to keep leader access
        String leaderId = null;
        try {
          leaderId = MusicDatabase.getUserId(leaderSpotifyId);
        } catch (IllegalStateException | SQLException e) {
          // SHOULD NEVER HAPPEN
          System.out.println("DEBUG: Could not get leader's ID in database.");
        }
        System.out.println("CODE " + code);
        double id = decrypt(Integer.parseInt(code)); // groupId as given by the
        // encoded request.

        boolean isValidGroup = false;
        if ((int) id == id) {
          try {
            System.out.println(id + ";" + name + ";" + leaderId);
            isValidGroup = MusicDatabase.isValidGroup((int) id);
            System.out.println(isValidGroup);
          } catch (IllegalStateException | SQLException e) {
            // SHOULD NEVER HAPPEN
            System.out.println("DEBUG: Could not verify if valid group.");
          }
        }

        if (isValidGroup) {
          Group group = Group.ofId((int) id);
          req.session().attribute("currGroup", group);
          List<String> genres;
          boolean userInGroup = false;
          try {
            userInGroup = MusicDatabase
                .isUserInGroup(req.session().attribute("userId"), (int) id);
          } catch (IllegalStateException | SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }

          if (!userInGroup) {
            Map<String, Object> variables = ImmutableMap
                .<String, Object>builder().put("title", "CommonGround")
                .put("userName", req.session().attribute("userId"))
                .put("error", "Looks like you're not a member of this group.")
                .build();
            return new ModelAndView(variables, "invalidURL.ftl");
          }
          // TODO: FILTER OUT GENRES WITH LESS THAN 2 SONGS
          try {
            genres = MusicDatabase.getGenres();
          } catch (IllegalStateException | SQLException e) {
            // SHOULD NEVER HAPPEN
            System.out.println("DEBUG (GroupPageHandler): " + e.getMessage());
            return null;
          }

          // TODO: REMOVE PRINTLN
          System.out.println("USERS ");
          for (edu.brown.cs.commonground.users.user.User u : group.getUsers()) {
            System.out.println(u.getName());
          }

          Map<String, Object> variables = ImmutableMap.<String, Object>builder()
              .put("title", "CommonGround").put("group", group)
              .put("code", code).put("genres", genres)
              .put("userName", req.session().attribute("userId")).build();
          return new ModelAndView(variables, "groupPage.ftl");
        } else {
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "error",
              "Looks like we couldn't find this page.");
          return new ModelAndView(variables, "invalidURL.ftl");
        }
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles adding a playlist.
   */
  private static class AddPlaylistHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
      QueryParamsMap qm = req.queryMap();
      String name = qm.value("name");
      int size = Integer.parseInt(qm.value("size"));
      double percentKnown = Double.parseDouble(qm.value("percKnown")) / 100;
      // TODO: change genres declaration
      // List<String> genres = new ArrayList<>();
      String parsedResponseString = qm.value("genres").toString()
          .replace("[", "").replace("]", "").replace("\"", "");

      Iterable<String> it = Splitter.on(",").split(parsedResponseString);
      List<String> genres = Lists.newArrayList(it);
      System.out.println(genres);
      try {
        Playlist playlist = (new PlaylistGenerator()).generatePlaylist(name,
            req.session().attribute("currGroup"), size, percentKnown, genres);
        System.out.println(playlist);
        int code = encrypt(playlist.getId());
        Map<String, Object> variables = ImmutableMap.of("code", code);
        return GSON.toJson(variables);
      } catch (IllegalStateException | SQLException e) {
        // SHOULD NEVER HAPPEN
        System.out.println(
            "DEBUG: Database has not been loaded or SQL exception occurred.");
        return null;
      }
    }
  }

  private static class RefreshPlaylistHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
      try {
        QueryParamsMap qm = req.queryMap();
        int id = Integer.parseInt(qm.value("playlistID"));
        Playlist old = Playlist.ofId(id);
        Playlist playlist = (new PlaylistGenerator()).generatePlaylist(
            old.getName(), req.session().attribute("currGroup"),
            old.getReqSize(), old.getPercentKnown(), old.getGenres());
        MusicDatabase.removePlaylist(id);
        Map<String, Object> variables = ImmutableMap.of("code",
            encrypt(playlist.getId()), "name", playlist.getName());
        return GSON.toJson(variables);
      } catch (IllegalStateException | SQLException e) {
        // SHOULD NEVER HAPPEN
        System.out.println(
            "DEBUG: Database has not been loaded or SQL exception occurred.");
        return null;
      }
    }
  }

  private static class MakePlaylistPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        List<String> genres;
        try {
          genres = MusicDatabase.getGenres();
        } catch (IllegalStateException | SQLException e) {
          // SHOULD NEVER HAPPEN
          System.out.println("DEBUG (GroupPageHandler): " + e.getMessage());
          return null;
        }
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", req.session().attribute("userId"), "genres", genres);
        return new ModelAndView(variables, "makePlaylist.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  private static class PastPlaylistsPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        String url = req.params(":groupurl");
        QueryParamsMap qm = req.queryMap();
        String code = qm.value("code");
        double id = decrypt(Integer.parseInt(code));

        boolean isValidGroup = false;
        if ((int) id == id) {
          try {
            isValidGroup = MusicDatabase.isValidGroup((int) id);
            System.out.println(isValidGroup);
          } catch (IllegalStateException | SQLException e) {
            // SHOULD NEVER HAPPEN
            System.out.println("DEBUG: Could not verify if valid group.");
          }
        }

        if (isValidGroup) {
          Group group = Group.ofId((int) id);
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "userName", req.session().attribute("userId"),
              "group", group);
          return new ModelAndView(variables, "pastPlaylists.ftl");
        } else {
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "error",
              "Looks like we couldn't find this page.");
          return new ModelAndView(variables, "invalidURL.ftl");
        }
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles playlist pages.
   */
  private static class PlaylistPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      if (req.session().attribute("loggedIn") != null) {
        String playlistUrl = req.params(":playlisturl");
        QueryParamsMap qm = req.queryMap();
        String code = qm.value("code");

        // Decodes playlist id.
        double playlistId = decrypt(Integer.parseInt(code));

        // Checks if playlist url requested directs to a valid playlist.
        boolean isValidPlaylist = false;
        if ((int) playlistId == playlistId) {
          try {

            isValidPlaylist = MusicDatabase.isValidPlaylist((int) playlistId,
                ((Group) req.session().attribute("currGroup")).getId());
            // TODO: REMOVE PRINTLN
            System.out.println("isValidPlaylist = " + isValidPlaylist);
          } catch (IllegalStateException | SQLException e) {
            // SHOULD NEVER HAPPEN
            System.out.println("DEBUG: Could not verify if valid group.");
          }
        }

        // We know that this playlist exists because of isValidPlaylist check
        // above, so we can safely assume that Playlist.ofId() returns a valid
        // playlist.
        Playlist playlist = Playlist.ofId((int) (playlistId));
        if (playlist != null) {
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "playlist", playlist, "userName",
              req.session().attribute("userId"));
          return new ModelAndView(variables, "playlistPage.ftl");
        } else {
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "error",
              "Looks like you're not a member of this group.");
          return new ModelAndView(variables, "invalidURL.ftl");
        }
      } else {
        Map<String, Object> variables = ImmutableMap.of("title",
            "CommonGround");
        return new ModelAndView(variables, "loginRedirectPage.ftl");
      }
    }
  }

  /**
   * Handles the loading page.
   */
  private static class LoadingPageHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) throws IOException {
      QueryParamsMap qm = req.queryMap();
      String code = qm.value("code");
      if (req.session().attribute("loggedIn") == null
          || (req.session().attribute("refresh") != null
              && ((boolean) req.session().attribute("refresh")))) {
        if (code != null) {
          req.session().attribute("loggedIn", "true");
          Map<String, String> tokens = new HashMap<>();
          try {
            tokens = getRefreshAccess(code);
          } catch (IOException e) {
            Map<String, Object> variables = ImmutableMap.of("title",
                "CommonGround");
            return new ModelAndView(variables, "loginRedirectPage.ftl");
          }
          // save spotify auth tokens in session
          req.session().attribute("authTokens", tokens);
          String accessToken = tokens.get("access_token");
          Track[] topTracks = SpotifyUserMethods.getUsersTopTracks(accessToken);
          req.session().attribute("lastLoadingSongs", topTracks);
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround", "code", code);
          return new ModelAndView(variables, "loadingPage.ftl");
        } else {
          Map<String, Object> variables = ImmutableMap.of("title",
              "CommonGround");
          return new ModelAndView(variables, "loginRedirectPage.ftl");
        }
      } else {
        Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
            "userName", req.session().attribute("userId"), "displayName",
            req.session().attribute("userId"));
        return new ModelAndView(variables, "welcomeBack.ftl");
      }
    }
  }

  private static class LoadSongsHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
      List<String> songs = new ArrayList<>();
      List<String> artists = new ArrayList<>();
      for (Track track : ((Track[]) (req.session()
          .attribute("lastLoadingSongs")))) {
        songs.add(track.getName());
        artists.add(track.getArtists()[0].getName());
      }
      Map<String, Object> variables = ImmutableMap.of("title", "CommonGround",
          "songs", songs, "artists", artists);
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles refreshing.
   */
  private static class RefreshHandler implements Route {

    @Override
    public Object handle(Request req, Response res) throws IOException {
      req.session().attribute("refresh", true);
      return GSON.toJson(new HashMap<>());
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Display an error page when an exception occurs in the server.
   *
   * @author jj
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
