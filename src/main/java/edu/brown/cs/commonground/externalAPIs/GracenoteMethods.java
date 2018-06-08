package edu.brown.cs.commonground.externalAPIs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import edu.brown.cs.commonground.radams.gracenote.webapi.GracenoteException;
import edu.brown.cs.commonground.radams.gracenote.webapi.GracenoteWebAPI;

public class GracenoteMethods {

  private static String clientID = "1936966750"; // Put your clientID here.
  private static String clientTag = "4912F149A2635450520978F325305ACB";
  private static String client = "1936966750-4912F149A2635450520978F325305ACB";

  public static Map<Map<String, String>, Integer> getSimilarSongs(String artist,
      String song) {
    Map<Map<String, String>, Integer> rankings = new HashMap<>();
    try {
      // song, artist -> ranking
      GracenoteWebAPI api = new GracenoteWebAPI(clientID, clientTag);
      String userID = api.register();
      URIBuilder ub;
      int len = 10;
      try {
        ub = new URIBuilder(
            "https://c1234567.web.cddbp.net/webapi/json/1.0/radio/create");
        ub.addParameter("artist_name", artist);
        ub.addParameter("track_title", song);
        ub.addParameter("client", client);
        ub.addParameter("user", userID);
        String url1 = ub.toString();
        StringBuilder result = new StringBuilder();
        try {
          URL url = new URL(url1);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");
          BufferedReader rd = new BufferedReader(
              new InputStreamReader(conn.getInputStream()));
          String line;
          while ((line = rd.readLine()) != null) {
            result.append(line);
          }
          rd.close();
          List<String> list = new ArrayList<String>();
          Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*")
              .matcher(result.toString());
          while (m.find())
            list.add(m.group(1));
          List<Integer> saved = new ArrayList<Integer>();
          String currentArtist = "";
          for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("\"TITLE\"")) {
              saved.add(i + 6);
              Map<String, String> pair = new HashMap<>();
              pair.put(list.get(i + 6).replace("\"", ""), currentArtist);
              rankings.put(pair, len);
              len--;
              return rankings;
            }
            if (list.get(i).equals("\"ARTIST\"")) {
              saved.add(i + 6);
              currentArtist = list.get(i + 6).replace("\"", "");
            }
          }
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
      } catch (URISyntaxException e) {
        System.out.println(e.getMessage());
      }
    } catch (GracenoteException e) {
      System.out.println(e.getMessage());
    }
    return rankings;
  }
}
