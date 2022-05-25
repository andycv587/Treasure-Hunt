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

import java.lang.reflect.InvocationTargetException;
import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.IPlayer;
import com.ibm.vie.mazerunner.Location;

/**
 * The top-level interface students will call to find out information about a square.
 */
public interface ISquare {
  /**
   * Get the location of this square on the board
   * 
   * @return location
   */
  public Location getLocation();

  /**
   * Returns true if this is a legal square to move to FROM THE PLAYERS CURRENT POSITION
   * 
   * @param board The game board the controller has provided you
   * @return true if legal move, false otherwise
   */
  public boolean isValidMove(IBoard board);

  /**
   * Students do not need this. Indicate your move by returning a
   * {@link com.ibm.vie.mazerunner.Move} from
   * {@link com.ibm.vie.mazerunner.IPlayer#selectMove(IBoard)}. If you do decide to invoke it, there
   * will be no effect.
   * 
   * @param board - the board to make the move on
   * @return true if the move was legal and was made, false otherwise
   */
  public boolean moveTo(IBoard board);

  /**
   * The number of times your player has moved to this space. Sometimes it will be necessary to move
   * to a space more than once as different paths to treasure might overlap.
   *
   * @return The number of times the player has moved to this space.
   */
  public int getStepCount();

  /**
   * Gets the string name for the type of square. May be useful for inspecting which squares are
   * what if using 'instanceof' is not familiar enough.
   * 
   * @return A string indicating the type of space. Subclasses will return a different value.
   */
  public String getTypeString();

  /**
   * The number of steps it takes to move to this square.
   * 
   * @return An integer of the step cost.
   */
  public int getStepCost();

  /**
   * Get the appropriate name of the sprite that reflects the current state of this square
   * 
   * @return the String name of the sprite
   */
  public String getSpriteName();

  /**
   * Duplicates the square dynamically,
   * 
   * Students do not need to call this
   * 
   * @return a copy of ISquare
   */
  public default ISquare duplicate() {
    Class<? extends ISquare> classToLoad = this.getClass();
    Class<?>[] args = {classToLoad};
    try {
      return classToLoad.getDeclaredConstructor(args).newInstance(this);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(
          "There is no copy constructor for class " + classToLoad.getSimpleName(), e);
    }

  }

}
