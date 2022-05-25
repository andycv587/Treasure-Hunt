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
 * A square containing a treasure chest.
 * 
 * Finding spaces with treasure is the goal of the game. When you move into a space containing a
 * treasure, you have found it. Boards will have at least one treasure and may contain multiple
 * treasures.
 */
public class Treasure extends Space {
  private boolean spaceHasTreasure = true;

  /**
   * Students will not create Treasure. That would be cheating <code>: )</code>.
   * 
   * @param row The row index the square will be located at
   * @param col The column index the square will be located at
   */
  public Treasure(int row, int col) {
    super(row, col);
  }

  /**
   * Students will not create Treasure. Ignore this.
   * 
   * @param other The Treasure to clone from
   */
  public Treasure(Treasure other) {
    super(other);
    this.spaceHasTreasure = other.spaceHasTreasure;
  }

  /**
   * Students will not create Treasure. Ignore this.
   * 
   * @param l Location where the sqaure will be located at
   */
  public Treasure(Location l) {
    this(l.getRow(), l.getCol());
  }

  public boolean moveTo(IBoard board) {
    boolean ok = super.moveTo(board); // Do normal validation in Space class
    if (ok) {
      if (spaceHasTreasure) { // if the treasure is still here... well now it's not. the player has
                              // it!
        spaceHasTreasure = false;
        if (board instanceof com.ibm.vie.mazerunner.MapBoard) {
          // This is the controller processing this, not the player. Remove the treasure from the
          // list
          ((com.ibm.vie.mazerunner.MapBoard) board).removeTreasure(this);
        }
        // do other treasury things?
      } else {
        // Treasure's already been gotten, nothing to do
      }
    }
    return ok;
  }

  /**
   * Returns true if the square has unobtained treasure.
   * 
   * @return true if square has treasure; false if not.
   */
  public boolean spaceHasTreasure() {
    return spaceHasTreasure;
  }

  public Color getColor() {
    return spaceHasTreasure() ? new Color(22, 144, 42) : Color.ORANGE;
  }

  public String getTypeString() {
    return "Treasure";
  }

  public String getSpriteName() {
    return "Treasure_" + (spaceHasTreasure ? "closed" : "open");
  }

  public String toParamString() {
    return new StringBuilder(super.toParamString()).append(";hasTreasure=")
        .append(spaceHasTreasure()).toString();
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof Treasure) {
      Treasure ot = (Treasure) o;
      return ot.location.equals(this.location);
    } else {
      return false;
    }
  }
}
