package edu.brown.cs.commonground.externalAPIs;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

public class ClientCredentials {
  private static final String clientId = "fcee77f8419342908f021021751a79a3";
  private static final String clientSecret = "4b4dba04c9fc46b9ba0faf83c17d56af";

  private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setClientId(clientId).setClientSecret(clientSecret).build();
  private static final ClientCredentialsRequest clientCredentialsRequest =
      spotifyApi.clientCredentials().build();

  public static String clientCredentials_Sync() {
    try {
      final com.wrapper.spotify.model_objects.credentials.ClientCredentials clientCredentials =
          clientCredentialsRequest.execute();

      // Set access token for further "spotifyApi" object usage
      spotifyApi.setAccessToken(clientCredentials.getAccessToken());

      // System.out.println("Expires in: " + clientCredentials.getExpiresIn());
    } catch (IOException | SpotifyWebApiException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
    return spotifyApi.getAccessToken();
  }
}
