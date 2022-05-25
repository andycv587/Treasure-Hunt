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


import java.util.Collections;
import java.util.List;
import com.ibm.vie.mazerunner.squares.ISquare;
import com.ibm.vie.mazerunner.squares.Treasure;

// Provides a read-only view of an IBoard to be passed to an IPlayer's selectMove method
// The move method is disabled for this object
public class MapBoardView implements IAnalysisBoard {
  private IBoard board;

  public MapBoardView(IBoard board) {
    this.board = board;
  }

  @Override
  public String getName() {
    return board.getName();
  }

  @Override
  public Location getPlayerLocation() {
    return board.getPlayerLocation();
  }

  @Override
  public List<Treasure> getTreasures() {
    return Collections.unmodifiableList(board.getTreasures());
  }

  @Override
  public int getRemainingTreasureCount() {
    return board.getRemainingTreasureCount();
  }

  @Override
  public int getObtainedTreasureCount() {
    return board.getObtainedTreasureCount();
  }

  @Override
  public int getMaxSteps() {
    return board.getMaxSteps();
  }

  @Override
  public int getRemainingSteps() {
    return board.getRemainingSteps();
  }

  @Override
  public ISquare getSquareAt(Location loc) {
    return board.getSquareAt(loc);
  }

  @Override
  public int getWidth() {
    return board.getWidth();
  }

  @Override
  public int getHeight() {
    return board.getHeight();
  }

  @Override
  public void move(Move mv) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public List<Move> getUnexploredMoves() {
    return board.getUnexploredMoves();
  }

  @Override
  public boolean isComplete() {
    return board.isComplete();
  }

  @Override
  public int getScore() {
    return board.getScore();
  }

  @Override
  public Location getStartingLocation() {
    return board.getStartingLocation();
  }
}
