package edu.brown.cs.commonground.orm;

/**
 * Class representing anything with a unique ID (such as an element in a
 * database).
 *
 * @author calderhoover
 *
 */
public interface Entity {

  /**
   * Gets the ID of this Entity.
   *
   * @return ID of this Entity
   */
  int getId();
}
