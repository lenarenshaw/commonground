package edu.brown.cs.commonground.orm;

/**
 * Class implementing basic functionality of an Entity.
 *
 * @author calderhoover
 *
 */
public class SimpleEntity implements Entity {
  private int id;

  /**
   * Constructor for SimpleEntity.
   *
   * @param id
   *          : ID of SimpleEntity
   */
  public SimpleEntity(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
