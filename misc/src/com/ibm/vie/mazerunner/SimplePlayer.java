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

import java.util.List;
import java.awt.Point;
import com.ibm.vie.mazerunner.squares.*;

public class SimplePlayer implements IPlayer {

  public String getName() {
    return "IBM-Simple Player";
  }

  public void analyzeBoard(IAnalysisBoard board) {
    /* ADD YOUR CODE HERE, MAYBE */
  }

  public Move selectMove(IBoard board) throws Error {

    /*
     * This unnamed algorithm is small and compact, but may be better than the Road Less Traveled
     * algorithm because it tries to pick the best move toward the treasure.
     */

    /*
     * Grab a list of unexplored moves (north, east, west, south) around the player, if any. If all
     * squares around player are explored, the BACKTRACK move is passed back
     */
    List<Move> moves = board.getUnexploredMoves();
    double minDist = Double.MAX_VALUE;

    // Grab the first move in the list
    Move minMove = moves.get(0);

    // Get the treasures and player location
    List<Treasure> treasures = board.getTreasures();
    Location pl = board.getPlayerLocation();

    double tmpDist = 0;

    // Find closest treasure and choose that move

    // Iterate through all of the treasures
    for (Treasure treasure : treasures) {

      if (treasure.spaceHasTreasure()) {
        // Iterate through all of our moves and calculate the distance
        // to the treasure
        for (Move mv : moves) {
          tmpDist = mv.apply(pl).distance(treasure.getLocation());
          if (tmpDist < minDist) {
            // We have a move that's closer to a treasure, update
            // our distance to beat, and record the move
            minDist = tmpDist;
            minMove = mv;
          }
        }
      }
    }
    return minMove;
  }

  /**
   * Game board completed: Let's see how we did.
   */
  public void gameCompleted(IBoard board) {
    if (!board.isComplete()) {
      System.out.println("Shucks! I must my solution took too long!");
    }
  }

  /**
   * Game launcher. Create an instance of your player class and run the game with it.
   *
   * @param args
   */
  public static void main(String args[]) {
    MyTreasureHunt.run(new SimplePlayer());
  }
}
