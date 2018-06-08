package edu.brown.cs.commonground.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.commonground.orm.Entity;
import edu.brown.cs.commonground.orm.Pair;

/**
 * Database is a class for holding and interacting with a database. It maintains
 * a connection that can be used by other classes in the codebase.
 *
 */
public final class Database {

  private static volatile Database db = null;
  private static Connection conn = null;
  private static Map<String, String> statements = new HashMap<>();
  private static Map<Pair<String, Integer>, Entity> cache = new HashMap<>();
  private static final int MAX_CACHE_SIZE = 100000;

  private Database(String path) throws SQLException, FileNotFoundException {
    File f = new File(path);
    if (f.exists() && path.contains("sqlite")) {
      try {
        Class.forName("org.sqlite.JDBC");
        String urlToDB = "jdbc:sqlite:" + path;
        conn = DriverManager.getConnection(urlToDB);
      } catch (ClassNotFoundException | SQLException e) {
        throw new SQLException("Could not create the database.");
      }
    } else {
      throw new FileNotFoundException("Invalid file path.");
    }
  }

  /**
   * setDatabase sets the database based on a String representing a path to the
   * database.
   *
   * @param path
   *          the path of the database
   * @throws SQLException
   *           thrown if there is an issue setting the database.
   * @throws FileNotFoundException
   *           if the File is not found
   */
  public static void setDatabase(String path)
      throws SQLException, FileNotFoundException {
    if (db != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        throw new SQLException("Could not set the database.");
      }
    }
    db = new Database(path);
  }

  /**
   * prepareStatement prepares a statement to the connection using a String
   * representing a database query.
   *
   * @param query
   *          the query to the database
   * @return a PreparedStatement to the database
   * @throws SQLException
   *           if the statement is unable to be prepared
   * @throws IllegalStateException
   *           if the database has not been loaded yet
   * @throws SQLException
   *           faulty query
   */
  public static PreparedStatement prepareStatement(String query)
      throws SQLException, IllegalStateException {
    if (conn == null) {
      throw new IllegalStateException(
          "ERROR: Database has not been loaded yet.");
    }
    try {
      return conn.prepareStatement(query);
    } catch (SQLException | NullPointerException e) {
      System.out.println(e.getMessage());
      throw new SQLException("Faulty query: " + query);
    }
  }

  // TODO: delete once populating DB methods are done
  public static Statement createStatement()
      throws SQLException, IllegalStateException {
    if (conn == null) {
      throw new IllegalStateException(
          "ERROR: Database has not been loaded yet.");
    }
    try {
      return conn.createStatement();
    } catch (SQLException e) {
      throw new SQLException("Could not create statement");
    }
  }

  /**
   * addQuery adds a query to the map of strings to queries. It takes in a key
   * to this query and a String representing the query, prepares it, and saves
   * it.
   *
   * @param key
   *          the key to this query
   * @param query
   *          the query as a string
   * @throws SQLException
   *           if there is an issue with the database.
   */
  public static void addQuery(String key, String query) throws SQLException {
    statements.put(key, query);
  }

  /**
   * getQuery get a query from the map of strings to queries. It takes in a key
   * to this query and returns a PreparedStatement representing the query,
   *
   * @param key
   *          the key to this query
   * @return the query as a string
   */
  public static String getQuery(String key) {
    return statements.get(key);
  }

  /**
   * If there is room in the cache, stores an Entity in the cache which is
   * mapped to by a key with two String identifiers.
   *
   * @param a
   *          : String in key
   * @param b
   *          : Integer in key
   * @param e
   *          : Entity to map to from a and b as a single key
   * @return true if e was added to cache, false if cache has reached maximum
   *         size
   */
  public static boolean putInCache(String a, int b, Entity e) {
    if (cache.size() < MAX_CACHE_SIZE) {
      cache.put(new Pair<String, Integer>(a, b), e);
      return true;
    }
    return false;
  }

  /**
   * Gets Entity from cache if it exists.
   *
   * @param a
   *          : String in key
   * @param b
   *          : Integer in key
   * @return Entity mapped to by the key with a and b if the key exists, null
   *         otherwise
   */
  public static Entity getFromCache(String a, int b) {
    return cache.get(new Pair<String, Integer>(a, b));
  }

  /**
   * Gets Entity from cache if it exists, and returns default Entity passed in
   * otherwise.
   *
   * @param a
   *          : String in key
   * @param b
   *          : Integer in key
   * @param def
   *          : default Entity
   * @return Entity mapped to by the key with a and b if the key exists, def
   *         otherwise
   */
  public static Entity getFromCacheOrDefault(String a, int b, Entity def) {
    return cache.getOrDefault(new Pair<String, Integer>(a, b), def);
  }

  /**
   * Gets maximum cache size.
   *
   * @return maximum cache size
   */
  public static int getMaxCacheSize() {
    return MAX_CACHE_SIZE;
  }

  /**
   * Clears the cache.
   *
   * @return number of entries cleared from the cache
   */
  public static int clearCache() {
    int entriesCleared = cache.size();
    cache = null;
    return entriesCleared;
  }

  /**
   * close database closes the database when everything is done.
   *
   * @throws SQLException
   *           thrown if there is an issue setting the database.
   */
  public static void close() throws SQLException {
    try {
      if (conn != null) {
        conn.close();
      }
      db = null;
      conn = null;
      statements = new HashMap<>();
    } catch (SQLException e) {
      throw new SQLException("Could not close database");
    }
  }
}
