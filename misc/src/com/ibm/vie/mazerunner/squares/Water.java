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

import java.awt.*;

/**
 * A square full of water is fairly easy to traverse. Water costs 13 steps to navigate.
 */
public class Water extends Space {

  /**
   * Students will not create water. Ignore this.
   * 
   * @param row The row index the square will be located at
   * @param col The column index the square will be located at
   */
  public Water(int row, int col) {
    super(row, col);
  }

  /**
   * Students will not create water. Ignore this.
   * 
   * @param other The Water to clone from
   */
  public Water(Water other) {
    super(other);
  }

  /**
   * Students will not create water. Ignore this.
   * 
   * @param p A Point where the sqaure will be located at
   */
  public Water(Point p) {
    this(p.x, p.y);
  }

  public String getTypeString() {
    return "Water";
  }

  public String getSpriteName() {
    return "Water";
  }

  public int getStepCost() {
    return 13;
  }
}
