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

import com.ibm.vie.mazerunner.Location;

/**
 * An impassible square -- players who traverse into one will lose all their steps.
 */
public class Wall extends Space {

  /**
   * Students will not create walls. Ignore this.
   * 
   * @param row The row index the square will be located at
   * @param col The column index the square will be located at
   */
  public Wall(int row, int col) {
    super(row, col);
  }

  /**
   * Students will not create walls. Ignore this.
   * 
   * @param other The Wall to clone from
   */
  public Wall(Wall other) {
    super(other);
  }

  /**
   * Students will not create walls. Ignore this.
   * 
   * @param loc The Location where the sqaure will be
   */
  public Wall(Location loc) {
    super(loc);
  }

  public String getTypeString() {
    return "Wall";
  }

  public String getSpriteName() {
    return "Wall";
  }

  public int getStepCost() {
    return Integer.MAX_VALUE;
  }
}
