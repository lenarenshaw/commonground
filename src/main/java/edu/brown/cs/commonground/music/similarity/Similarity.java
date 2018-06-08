package edu.brown.cs.commonground.music.similarity;

import edu.brown.cs.commonground.graph.Edge;
import edu.brown.cs.commonground.music.song.Song;

/**
 * Class implementing the edge connecting songs, representing how similar two
 * songs are. Uses data collected from lastfm, pandora, spotify, and musicbrainz
 * to create a similarity index used as an edge weight.
 */
public class Similarity implements Edge<Song, Similarity> {
  private Song src;
  private Song dest;
  private int lastfm;
  private int pandora;
  private int spotify;
  private int musicbrainz;
  private int gracenote;

  /**
   * Constructor for similarity.
   *
   * @param src
   *          : first song
   * @param dest
   *          : second song
   * @param lastfm
   *          : lastfm ranking in database
   * @param pandora
   *          : pandora ranking in database
   * @param spotify
   *          : spotify ranking in database
   * @param musicbrainz
   *          : musicbrainz ranking in database
   * @param gracenote
   *          : gracenote ranking in database
   */
  public Similarity(Song src, Song dest, int lastfm, int pandora, int spotify,
      int musicbrainz, int gracenote) {
    this.src = src;
    this.dest = dest;
    this.lastfm = lastfm;
    this.pandora = pandora;
    this.spotify = spotify;
    this.musicbrainz = musicbrainz;
    this.gracenote = gracenote;
  }

  @Override
  public Song getSource() {
    return this.src;
  }

  @Override
  public Song getDestination() {
    return this.dest;
  }

  // TODO: MODIFY AS NEEDED
  @Override
  public double getWeight() {
    return 1.0 / (lastfm + pandora + spotify + musicbrainz + gracenote);
  }
}
