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

import java.util.Optional;

/**
 * Move indicates a direction in which to move on an {@link IBoard}.
 * 
 * You can get the target location of the move by applying the move to a starting location.
 * 
 * For example if you are located at (row=5,col=5) and you want the location after moving south, you
 * could code this as:
 * 
 * <pre>
 * Location currentLocation = new Location(5,5);
 * Location newLocation = currentLocation.apply(Move.SOUTH);
 * 
 * System.out.println(newLocation)
 * 
 * (row=6,col=5)
 * </pre>
 * 
 * @see Location
 */
public enum Move {
  /**
   * Move one square north (up).
   */
  NORTH(-1, 0),
  /**
   * Move one square south (down).
   */
  SOUTH(1, 0),
  /**
   * Move one square east (right).
   */
  EAST(0, 1),
  /**
   * Move one square west (left).
   */
  WEST(0, -1),
  /**
   * This move tells the game to "turn around and go back to where the player came from". <BR>
   * <BR>
   * Imagine that each time you make a non-backtrack move a bread crumb is dropped on the square
   * that you moved from. <BR>
   * <BR>
   * When you backtrack, the player moves back to the square with the most recent bread crumb and
   * picks it up. If you repeatably backtrack enough times, you will arrive at the starting
   * square.<BR>
   * <BR>
   * Backtracking is important for dealing with squares where there are no good moves (dead ends),
   * because it allows you to go back to a point where there are other moves to consider and follow
   * "the path not taken". <BR>
   * <BR>
   * If you attempt to backtrack past the first move/starting square, then there is no move history
   * (bread crumbs) to follow. An Exception will be thrown and the game will end if this happens.
   */
  BACKTRACK(null, null);

  private Integer rowChange;
  private Integer colChange;

  private Move(Integer rowChange, Integer colChange) {
    this.rowChange = rowChange;
    this.colChange = colChange;
  }

  /**
   * This method will take a {@link Location} and compute the new location after making the move.
   * 
   * @param loc - location from which to apply the move.
   * 
   * @return The new {@link Location} translated by this move.
   * @throws UnsupportedOperationException if the move is a backtrack
   */
  public Location apply(Location loc) {
    if (this == BACKTRACK) {
      throw new UnsupportedOperationException(
          "BACKTRACK move cannot be applied because it depends on the player's move history.");
    }
    return new Location(loc.getRow() + this.rowChange, loc.getCol() + this.colChange);
  }

  /**
   * Finds the inverse direction of a Move, if possible.
   * 
   * @return a Move of the opposite direction if one is defined.
   */
  public Optional<Move> inverse() {
    switch (this) {
      case NORTH:
        return Optional.of(SOUTH);
      case SOUTH:
        return Optional.of(NORTH);
      case EAST:
        return Optional.of(WEST);
      case WEST:
        return Optional.of(EAST);
      default:
        return Optional.empty();
    }
  }
}
