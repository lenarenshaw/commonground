package edu.brown.cs.commonground.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class implementing REPL functionality.
 */
public class Repl {

  /**
   * Simple constructor.
   */
  public Repl() {
  }

  /**
   * Runs REPL, taking important functionality from helper methods.
   */
  public void run() {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
      String input;
      while ((input = br.readLine()) != null) {
        String[] keywords = parseQuotesSpaces(input);
        if (keywords.length == 0) {
          continue;
        }
        String command = keywords[0];
        String[] args = Arrays.copyOfRange(keywords, 1, keywords.length);
        switch (command) {
          // case commands
          /*
           * e.g. case "stars": callfunction(); break; case "connect":
           * callfunction(); break;
           */
          default:
            /*
             * System.out.println("ERROR: Invalid command. Possible commands: "
             * + "stars <filepath>, neighbors k x y z, neighbors k " +
             * "\"<name>\", radius r x y z, radius r \"<name>\", " +
             * "corpus <filepath>, ac <input text>, prefix <on|off>, " +
             * "whitespace <on|off>, smart <on|off>, led <integer>, " +
             * "mdb <sql_db>, connect <name1> <name2>.");
             */
            break;
        }
      }
    } catch (IOException e) {
      System.out.println("ERROR: Repl could not be initialized.");
    }
  }

  /**
   * Parses input string into tokens separated by quotes and if not, by spaces.
   *
   * @param input
   *          : string to split
   * @return String array with tokens separated by quotes or spaces
   */
  public String[] parseQuotesSpaces(String input) {
    List<String> keywordsList = new ArrayList<>();
    // Pattern defined to look for strings separated by spaces as well as
    // strings separated by quotations.
    Pattern words = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
    Matcher wordsMatcher = words.matcher(input);
    while (wordsMatcher.find()) {
      keywordsList.add(wordsMatcher.group());
    }
    String[] keywords = new String[keywordsList.size()];
    keywordsList.toArray(keywords);
    return keywords;
  }

  /**
   * Returns name without quotations.
   *
   * @param name
   *          : name to remove quotes from
   * @return name without quotations
   */
  public String nameWithoutQuotes(String name) {
    if (name.charAt(0) != '\"' || name.charAt(name.length() - 1) != '\"') {
      System.out.println("ERROR: Names of both actors must be in quotations.");
      return null;
    }
    return name.replace("\"", "");
  }
}
