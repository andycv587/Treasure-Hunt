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

import java.util.Objects;

/**
 * Represents an immutable row and column location.
 *
 * The 0th row is the most northern row (top), and the 0th column is the western (left) side of an
 * {@link IBoard}. For example, a board with the following squares:
 * 
 * <pre>
 * * 0 1 2 3 4
 * 0 A B C D E
 * 1 F G H I J
 * 2 K L M N O
 * 3 P Q R S T
 * </pre>
 * 
 * The square at letter A has coordinates 0,0. The square at letter D, would have (0, 3). The square
 * at L would have (2, 1) and so on.
 * 
 * @author ntl
 *
 */
public class Location {
  final int row;
  final int col;

  /**
   * Constructs a new location given a row and column index
   * 
   * @param row The row index of the location
   * @param col The column index of the location
   */
  public Location(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Location)) {
      return false;
    } else {
      Location other_location = (Location) o;
      return (other_location.row == this.row && other_location.col == this.col);
    }
  }

  /**
   * Gets the column index of the location
   * 
   * @return An integer indicating the column index
   */
  public int getCol() {
    return col;
  }

  /**
   * Gets the row index of the location
   * 
   * @return An integer indicating the row index
   */
  public int getRow() {
    return row;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.col);
  }

  public String toString() {
    return "(row=" + this.row + ", col=" + this.col + ")";
  }

  /**
   * Returns the Manhattan distance between this location and another.
   * 
   * @param other the Location to compare to this one
   * @return distance between this point and another point
   */
  public int distance(Location other) {
    return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
  }
}
