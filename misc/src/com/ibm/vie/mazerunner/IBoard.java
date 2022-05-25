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

package com.ibm.vie.mazerunner;

import com.ibm.vie.mazerunner.squares.*;
import java.util.List;

/**
 * This interface allows the Player to inspect the board and state of the game.
 *
 * An IBoard does not include any methods that change the state of the game.
 */
public interface IBoard {

  /**
   * Do not depend on the board name in your implementation. The IBM judges may:
   * <ul>
   * <li>Use new boards you have not seen</li>
   * <li>Might not use every sample board</li>
   * </ul>
   *
   * @return The name of the board.
   */
  public String getName();

  /**
   * The current location of your player. In the analyze phase this corresponds to your starting
   * position when the selectMove phase starts. This will report your new location after a move.
   *
   * @return The current location of the player.
   */
  public Location getPlayerLocation();

  /**
   * The location where the player entered the maze. This will never change for a specific board.
   * This can be used to determine if you have backtracked (or retraced) your steps all the way back
   * to the beginning.
   * 
   * @return The location that the player started the game at
   */
  public Location getStartingLocation();

  /**
   * List of yet to be obtained treasures on the board.
   *
   * @return The list of treasures on the board. Boards contain at least one treasure.
   */
  public List<Treasure> getTreasures();

  /**
   * The number of treasures remaining on the board for you to obtain.
   * 
   * @return An integer of the number of treasures remaining.
   */
  public int getRemainingTreasureCount();

  /**
   * A treasure is obtained when your player moves to a space which contains a treasure.
   *
   * @return The number of treasures on the board you have obtained.
   *
   */
  public int getObtainedTreasureCount();

  /**
   * Each board allows a maximum number of steps. Play of the board stops when your player exceeds
   * this.
   *
   * @return The maximum number of steps your player may take on this board.
   */
  public int getMaxSteps();

  /**
   * The number of steps your player has left on this board.
   * 
   * @return An integer of the remaining steps left
   */
  public int getRemainingSteps();

  /**
   * Get the square at the location. This object can be used to determine the type of square, as
   * well as how many times it has been visited.
   *
   * @param l Location to check
   * @return Square at this position
   */
  public ISquare getSquareAt(Location l);

  /**
   * Get the width of the board, including the outer walls.
   * 
   * @return An integer of the board width
   */
  public int getWidth();

  /**
   * Get the height of the board, including the outer walls.
   * 
   * @return An integer of the board height
   */
  public int getHeight();


  /**
   * Your player might need to know if it has stepped in a certain square or has not explored a
   * certain square from its current position. For some boards it may be necessary to move to a
   * space more than once as paths to different treasures can overlap.
   *
   * @return The list of zero to four {@link Move} possibilities from the player's current position
   *         that have not been taken.
   */
  public List<Move> getUnexploredMoves();

  /**
   * A board is complete when the player has no more steps remaining, or when all treasures have been
   * obtained. No more moves are allowed when the board is complete.
   *
   * @return true when your player has found all of the treasure or run out of steps.
   */
  public boolean isComplete();


  /**
   * Get the current score for the board.
   * 
   * @return An integer of the score.
   */
  public int getScore();
}
