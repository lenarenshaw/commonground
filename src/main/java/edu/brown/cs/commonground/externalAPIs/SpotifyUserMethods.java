package edu.brown.cs.commonground.externalAPIs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import com.wrapper.spotify.requests.data.playlists.AddTracksToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import edu.brown.cs.commonground.music.playlist.Playlist;
import edu.brown.cs.commonground.music.song.Song;

public class SpotifyUserMethods {

  /**
   * @param accessToken
   *          the access token of a given user
   * @return a Track[] representing the users top tracks. Null if exception is
   *         thrown.
   */
  public static Track[] getUsersTopTracks(String accessToken) {
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi
        .getUsersTopTracks().limit(50).offset(0).time_range("medium_term")
        .build();
    try {
      final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
      final Track[] result = trackPaging.getItems();
      return result;
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return null;
  }

  /**
   * @param accessToken
   *          the access token of a given user.
   * @return the User object representing the user profile. Null if exception is
   *         thrown.
   */
  public static User getUserProfile(String accessToken) {
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
        .getCurrentUsersProfile().build();

    try {
      final User user = getCurrentUsersProfileRequest.execute();
      return user;
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return null;
  }

  public static String addPlaylistToSpotify(String accessToken,
      Playlist playlist) {
    String spotifyId = "";
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    System.out.println("getting user data:");
    GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi
        .getCurrentUsersProfile().build();
    try {
      final User user = getCurrentUsersProfileRequest.execute();
      System.out.println("attempting to make playlist for user: " + user.getId()
          + "dname: " + user.getDisplayName());
      CreatePlaylistRequest createPlaylistRequest = spotifyApi
          .createPlaylist(user.getId(), playlist.getName()).collaborative(false)
          .public_(true)
          .description("Playlist created with CommonGround. Enjoy!").build();
      try {
        final com.wrapper.spotify.model_objects.specification.Playlist newPlaylist = createPlaylistRequest
            .execute();
        spotifyId = newPlaylist.getId();
        List<String> songURIs = new ArrayList<>();
        for (Song s : playlist.getSongs()) {
          if (!s.getSpotifyId().equals("") && s.getSpotifyId() != null) {
            songURIs.add("spotify:track:" + s.getSpotifyId());
            // System.out.println("SONG URI:[" + s.getSpotifyId() + "]");
          }
        }
        String[] songURIarray = songURIs.toArray(new String[0]);
        // String[] songURIarray = new String[]{"6ctLYR4qUQ6kaXFfbYDlwZ"};
        for (String s : songURIarray) {
          System.out.println("ARRAY SONG URI:[" + s + "]");
        }
        AddTracksToPlaylistRequest addTracksToPlaylistRequest = spotifyApi
            .addTracksToPlaylist(user.getId(), newPlaylist.getId(),
                songURIarray)
            .position(0).build();
        try {
          System.out.println("executing request");
          final SnapshotResult snapshotResult = addTracksToPlaylistRequest
              .execute();

          System.out.println("Snapshot ID: " + snapshotResult.getSnapshotId());
        } catch (IOException | SpotifyWebApiException e) {
          System.out.println("Error: " + e.getMessage());
        }

      } catch (IOException | SpotifyWebApiException e) {
        System.out.println("ERROR: tried to make playlist: " + e.getMessage());
      }

    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("ERROR: in SpotifyUserMethods addPlaylistToSpotify: "
          + e.getMessage());
    }
    return spotifyId;
  }
}
