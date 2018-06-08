package edu.brown.cs.commonground.main;

import java.sql.SQLException;

import edu.brown.cs.commonground.database.Database;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static Repl repl;
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          : An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      Server.runSparkServer((int) options.valueOf("port"));
    }
    repl = new Repl();
    repl.run();

    // Close database
    try {
      Database.close();
    } catch (SQLException e) {
      // Only happens if connection cannot be closed for some reason. Should not
      // happen.
      System.out.println(e.getMessage());
    }
  }
}
