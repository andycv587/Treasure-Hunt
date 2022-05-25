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

package com.ibm.vie.mazerunner.gui;

import java.awt.Point;

import com.ibm.vie.mazerunner.Location;

/**
 * Utilities for converting from Point -> Location and Location -> Point
 * 
 * Graphics code uses an (x,y) system where (0,0) is in the top left and (w,h) is in the bottom
 * right.
 * 
 * This doesn't fit well with row and column models used by the location class.
 * 
 * These methods are utils, so that they don't need to show up in student facing interfaces.
 * 
 * @author ntl
 *
 */
public class LocationPointUtil {

  public static Point toPoint(Location loc) {
    return new Point(loc.getCol(), loc.getRow());
  }

  public static Location toLocation(Point p) {
    return new Location((int) p.getY(), (int) p.getX());
  }

}
