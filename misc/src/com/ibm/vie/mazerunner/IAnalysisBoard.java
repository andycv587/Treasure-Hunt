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
 * This interface allows the player to change the state of the board.
 * 
 * It is used by {@link com.ibm.vie.mazerunner.IPlayer#analyzeBoard(IAnalysisBoard)
 * IPlayer#analzeBoard(IAnalysisBoard)}, because this method receives a copy of the board.
 * 
 * @author ntl
 *
 */
public interface IAnalysisBoard extends IBoard {
  /**
   * Changes the player's position on the board.
   * 
   * @param mv the move to make
   */
  public void move(Move mv);
}
