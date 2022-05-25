/**
 * (C) Copyright IBM Corp. 2016,2022. All Rights Reserved. US Government Users Restricted Rights - Use,
 * duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ibm.vie.mazerunner.squares;

import java.awt.Color;

import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.Location;

/**
 * An open square which costs only one step to traverse.
 * 
 * Superclass for all "squares" on a board. The game engine uses these objects to manage the game.
 * The game engine will provide copies of these objects for you by your player to interact with the
 * board.
 */
public class Space implements ISquare {
  protected int stepCount = 0;
  protected Location location;
  private Color[] stepColors = new Color[] {new Color(22, 144, 42), new Color(86, 103, 42),
      new Color(88, 83, 23), new Color(58, 55, 15)};

  /**
   * Ignore this. Used for game setup.
   * 
   * @param row The row index the square will be located at
   * @param col The column index the square will be located at
   * @return Space with player on it
   */
  public static Space createPlayerStartSpace(int row, int col) {
    Space s = new Space(row, col);
    s.stepCount = 1; // indicate that we visited the start square
    return s;
  }

  /**
   * Students will not create spaces. Ignore this.
   * 
   * @param row The row index the square will be located at
   * @param col The column index the square will be located at
   */
  public Space(int row, int col) {
    this.location = new Location(row, col);
  }

  /**
   * Students will not create spaces. Ignore this.
   * 
   * @param other The Space to clone from
   */
  public Space(Space other) {
    this(other.location);
    this.stepCount = other.stepCount;
  }

  /**
   * Students will not create spaces. Ignore this.
   * 
   * @param loc location where the sqaure will be located at
   */
  public Space(Location loc) {
    this.location = loc;
  }

  public boolean moveTo(IBoard board) {
    // validate if it's legal to move here
    boolean ok = isValidMove(board);
    if (ok) {
      ++stepCount;
    }
    return ok;
  }

  /**
   * Returns true if it is valid to move to this square
   */
  public boolean isValidMove(IBoard board) {
    Location playerLocation = board.getPlayerLocation();

    return 1 == this.getLocation().distance(playerLocation);

  }

  public Location getLocation() {
    return this.location;
  }

  /**
   * Used by the game engine to change the color of the space based on the number of times your
   * player has moved to the space. Students will not use this method. Use {@link #getStepCount()}
   * instead.
   *
   * @return Color of the (empty) space.
   */
  public Color getColor() {
    return stepColors[Math.min(stepColors.length - 1, stepCount)];
  }

  public int getStepCount() {
    return stepCount;
  }

  public int getStepCost() {
    return 1;
  }

  public String getSpriteName() {
    return "Grass_" + Math.min(stepCount, 3);
  }

  /**
   * @return "Space" string
   */
  public String getTypeString() {
    return "Space";
  }

  /**
   * Students will not use this directly. Builds a string representation of the space useful for
   * logging.
   *
   * @return Human-readable representation of this object.
   *
   */
  public String toParamString() {
    return new StringBuilder().append(this.location.toString()).append(";stepCount=")
        .append(stepCount).toString();
  }

  /**
   * For debugging your player you might find it useful to print information about the space to
   * {@link System#out}
   *
   * @return Human-readable representation of this object.
   */
  public String toString() {
    return new StringBuilder().append(getTypeString()).append("[").append(toParamString())
        .append("]").toString();
  }
}
