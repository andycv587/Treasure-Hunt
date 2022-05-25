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

/**
 * This is thrown when a backtrack is attempted, but there are no more moves to backtrack.
 * 
 * Essentially the player has already backtracked to the starting position.
 * 
 * @see Move_old#BACKTRACK
 * @author ntl
 *
 */
public class IllegalBackTrackException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IllegalBackTrackException() {
    super("A BACKTRACK move was attempted, but the player is already back to the beginning");
  }

}
