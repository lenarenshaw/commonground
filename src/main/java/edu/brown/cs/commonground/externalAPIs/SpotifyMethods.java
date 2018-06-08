package edu.brown.cs.commonground.externalAPIs;

import java.io.IOException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchPlaylistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SpotifyMethods {

  public static PlaylistTrack[] getPlaylist(String playlistId, String userId) {
    PlaylistTrack[] result = new PlaylistTrack[] {};
    String accessToken = ClientCredentials.clientCredentials_Sync();
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    GetPlaylistsTracksRequest getPlaylistsTracksRequest = spotifyApi
        .getPlaylistsTracks(userId, playlistId)
        .fields("items(track(name,artists,id))").limit(15).offset(0)
        .market(CountryCode.SE).build();
    try {
      final Paging<PlaylistTrack> playlistTrackPaging = getPlaylistsTracksRequest
          .execute();
      result = playlistTrackPaging.getItems();
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return result;
  }

  public static PlaylistSimplified[] searchPlaylists(String playlist) {
    PlaylistSimplified[] result = new PlaylistSimplified[] {};
    String accessToken = ClientCredentials.clientCredentials_Sync();
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    SearchPlaylistsRequest searchPlaylistsRequest = spotifyApi
        .searchPlaylists(playlist).market(CountryCode.SE).limit(10).offset(0)
        .build();
    try {
      final Paging<PlaylistSimplified> playlistSimplifiedPaging = searchPlaylistsRequest
          .execute();
      result = playlistSimplifiedPaging.getItems();
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return result;
  }

  public static String searchArtist(String artist) {
    String result = "";
    try {
      String accessToken = ClientCredentials.clientCredentials_Sync();
      SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setAccessToken(accessToken).build();
      SearchArtistsRequest searchArtistsRequest = spotifyApi
          .searchArtists(artist).market(CountryCode.SE).limit(1).offset(0)
          .build();
      final Paging<Artist> artistPaging = searchArtistsRequest.execute();
      Artist[] artists = artistPaging.getItems();
      if (artists.length != 0) {
        result = artists[0].getId();
      }
      // System.out.println("Total: " + artistPaging.getTotal());
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return result;
  }

  public static Track[] getArtistsTopTracks(String id) {
    Track[] result = new Track[] {};
    try {
      String accessToken = ClientCredentials.clientCredentials_Sync();
      CountryCode countryCode = CountryCode.SE;
      SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setAccessToken(accessToken).build();
      GetArtistsTopTracksRequest getArtistsTopTracksRequest = spotifyApi
          .getArtistsTopTracks(id, countryCode).build();
      Track[] tracks = getArtistsTopTracksRequest.execute();
      result = tracks;
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return result;
  }

  public static String[] getArtistGenres(String id) {
    String accessToken = ClientCredentials.clientCredentials_Sync();
    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
        .build();
    GetArtistRequest getArtistRequest = spotifyApi.getArtist(id).build();
    try {
      final Artist artist = getArtistRequest.execute();
      return artist.getGenres();
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return null;
  }

  public static String searchTrack(String song, String artist) {
    try {
      String accessToken = ClientCredentials.clientCredentials_Sync();
      SpotifyApi spotifyApi = new SpotifyApi.Builder()
          .setAccessToken(accessToken).build();
      SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(song)
          .market(CountryCode.SE).limit(20).offset(0).build();
      Paging<Track> trackPaging = searchTracksRequest.execute();
      for (Track t : trackPaging.getItems()) {
        if (t.getArtists()[0].getName().equals(artist)) {
          return t.getId();
        }
      }
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("Error: " + e.getMessage());
    }
    return "";
  }
}
