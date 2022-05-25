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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import com.csvreader.CsvReader;
import com.ibm.vie.mazerunner.squares.Bushes;
import com.ibm.vie.mazerunner.squares.ISquare;
import com.ibm.vie.mazerunner.squares.Lava;
import com.ibm.vie.mazerunner.squares.Mountain;
import com.ibm.vie.mazerunner.squares.Mud;
import com.ibm.vie.mazerunner.squares.Space;
import com.ibm.vie.mazerunner.squares.Treasure;
import com.ibm.vie.mazerunner.squares.Trees;
import com.ibm.vie.mazerunner.squares.Wall;
import com.ibm.vie.mazerunner.squares.Water;

public class MapBoard implements IAnalysisBoard {

  private String name;
  private int width, height;
  private int maxSteps;
  private int remainingSteps;

  private ISquare[][] boardState;

  private Location playerLocation;
  private Location startingLocation;

  private List<Treasure> treasures;
  private int initialTreasureCount;

  private Stack<Move> moveHistory;

  private MapBoard(String boardName, int w, int h, int max) {
    name = boardName;
    width = w;
    height = h;
    remainingSteps = maxSteps = max;

    moveHistory = new Stack<>();
    treasures = new ArrayList<>();

    boardState = new ISquare[height][width];
  }


  @Override
  public String getName() {
    return name;
  }

  public MapBoard(MapBoard otherBoard) {
    this(otherBoard.name, otherBoard.width, otherBoard.height, otherBoard.maxSteps);

    playerLocation = otherBoard.playerLocation;
    startingLocation = otherBoard.startingLocation;
    this.remainingSteps = otherBoard.remainingSteps;
    this.moveHistory = new Stack<>();
    this.moveHistory.addAll(otherBoard.moveHistory);

    for (int j = 0; j < height; ++j) {
      for (int i = 0; i < width; ++i) {
        boardState[j][i] = otherBoard.boardState[j][i].duplicate();
        if (boardState[j][i] instanceof Treasure) {
          Treasure t = (Treasure) boardState[j][i];
          if (t.spaceHasTreasure()) {
            treasures.add(t);
          }
        }
      }
    }
    initialTreasureCount = otherBoard.initialTreasureCount;
  }

  public static MapBoard parseBoard(String boardConfig) {
    try {
      CsvReader csvr = new CsvReader(boardConfig);

      int w = 0;
      int h = 0;
      int max_steps = 100;

      List<String[]> records = new ArrayList<>();

      // Pre-scan
      boolean hasMore = csvr.readRecord();
      String[] vals;
      while (hasMore) {
        vals = csvr.getValues();
        hasMore = csvr.readRecord();
        if (hasMore) {
          // Not the last record, this is map data.
          w = Math.max(w, csvr.getColumnCount());
          records.add(vals);
        } else {
          // This was the last record, lets pull meta data from it
          if (vals.length > 0) {
            max_steps = Integer.parseInt(vals[0]);
          }
        }
      }
      h = records.size();

      String boardName = new java.io.File(boardConfig).getName();
      MapBoard board = new MapBoard(boardName, w, h, max_steps);

      // Process the data
      char c;
      for (int j = 0; j < h; ++j) {
        vals = records.get(j);
        for (int i = 0; i < w; ++i) {
          if (j == 0 || j == h - 1 || i == 0 || i == w - 1) { // Bounding square, always walls
            board.boardState[j][i] = new Wall(j, i);
          } else if (i < vals.length && vals[i].length() > 0) {
            c = vals[i].charAt(0);
            switch (c) {
              case '#':
              case 'W':
                board.boardState[j][i] = new Wall(j, i);
                break;
              case 'T':
                board.treasures.add((Treasure) (board.boardState[j][i] = new Treasure(j, i)));
                break;
              case 'w':
                board.boardState[j][i] = new Water(j, i);
                break;
              case 'B':
                board.boardState[j][i] = new Bushes(j, i);
                break;
              case 't':
                board.boardState[j][i] = new Trees(j, i);
                break;
              case 'M':
                board.boardState[j][i] = new Mountain(j, i);
                break;
              case 'L':
                board.boardState[j][i] = new Lava(j, i);
                break;
              case 'm':
                board.boardState[j][i] = new Mud(j, i);
                break;
              case 'P':
                board.playerLocation = new Location(j, i);
                board.boardState[j][i] = Space.createPlayerStartSpace(j, i);
                board.startingLocation = new Location(j, i);
                break;
              default:
                board.boardState[j][i] = new Space(j, i);
            }
          } else {
            board.boardState[j][i] = new Space(j, i);
          }
        }
      }

      if (board.playerLocation == null) {
        throw new RuntimeException("Invalid board: missing [P]layer space");
      }

      if (board.treasures.size() == 0) {
        throw new RuntimeException("Invalid board: At least one [T]reasure space is required.");
      }

      board.initialTreasureCount = board.treasures.size();
      return board;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void move(Move mv) {

    if (isComplete()) {
      throw new RuntimeException("Cannot move, this board is complete. Remaining Treasures = "
          + treasures.size() + ", remaining steps = " + getRemainingSteps());
    }

    if (mv == Move.BACKTRACK) {
      if (moveHistory.size() == 0) {
        throw new IllegalBackTrackException();
      }
      mv = moveHistory.pop().inverse()
          .orElseThrow(() -> new UnsupportedOperationException("Move in history had no inverse!"));
    } else {
      moveHistory.push(mv);
    }

    Location np = mv.apply(playerLocation);

    ISquare dest = getSquareAt(np);
    remainingSteps -= dest.getStepCost();

    if (remainingSteps < 0) {
      remainingSteps = 0;
    }

    // Returns true if the move is legal
    if (dest.moveTo(this)) {
      playerLocation = dest.getLocation();
    } else {
      moveHistory.pop(); // unrecord a dead move
      throw new RuntimeException("Illegal " + mv + " to " + dest + " from " + playerLocation);
    }
  }

  private boolean isSquareUnexplored(ISquare spc) {
    return spc.isValidMove(this) && spc.getStepCount() == 0;
  }

  @Override
  public List<Move> getUnexploredMoves() {
    List<Move> moves = new ArrayList<>(4);

    if (isSquareUnexplored(getSquareAt(Move.NORTH.apply(playerLocation)))) {
      moves.add(Move.NORTH);
    }
    if (isSquareUnexplored(getSquareAt(Move.SOUTH.apply(playerLocation)))) {
      moves.add(Move.SOUTH);
    }
    if (isSquareUnexplored(getSquareAt(Move.EAST.apply(playerLocation)))) {
      moves.add(Move.EAST);
    }
    if (isSquareUnexplored(getSquareAt(Move.WEST.apply(playerLocation)))) {
      moves.add(Move.WEST);
    }

    return moves;
  }

  @Override
  public Location getPlayerLocation() {
    return playerLocation;
  }

  @Override
  public Location getStartingLocation() {
    return startingLocation;
  }

  public void removeTreasure(Treasure t) {
    treasures.remove(t);
  }

  @Override
  public List<Treasure> getTreasures() {
    return treasures; // return unmodifiable list so the player cannot manipulate it
  }

  // Count up remaining treasures. we're not going to do any mutating of the list.
  @Override
  public int getRemainingTreasureCount() {
    return treasures.size();
  }

  @Override
  public int getObtainedTreasureCount() {
    return initialTreasureCount - treasures.size();
  }

  @Override
  public ISquare getSquareAt(Location l) {
    return boardState[l.getRow()][l.getCol()];
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getMaxSteps() {
    return maxSteps;
  }

  @Override
  public int getRemainingSteps() {
    return remainingSteps;
  }

  @Override
  public boolean isComplete() {
    return getRemainingTreasureCount() == 0 || getRemainingSteps() <= 0;
  }

  @Override
  public int getScore() {
    if (getRemainingTreasureCount() == 0) { // Got all the treasure
      return getRemainingSteps() + getObtainedTreasureCount() * 100 + 500;
    } else {
      return getObtainedTreasureCount() * 100;
    }
  }

  private static Set<Location> getAdjacentPoints(Location l, int maxx, int maxy) {
    Set<Location> result = new HashSet<>();

    if (l.getCol() > 1)
      result.add(new Location(l.getRow(), l.getCol() - 1));
    if (l.getCol() < maxx - 2)
      result.add(new Location(l.getRow(), l.getCol() + 1));
    if (l.getRow() > 1)
      result.add(new Location(l.getRow() - 1, l.getCol()));
    if (l.getRow() < maxy - 2)
      result.add(new Location(l.getRow() + 1, l.getCol()));

    return result;

  }

  public static IBoard randomizedPrim(String boardName, int width, int height, int treasures,
      int branchFactor, // 100 = max number of branches
      int openFactor, // 100 = empty board
      int max_steps) {
    Random rnd = new Random();

    MapBoard board = new MapBoard(boardName, width, height, max_steps);

    // Create points that might be in the maze
    // These all start as walls
    List<Location> locs = new LinkedList<>();
    for (int w = 1; w < width - 1; w++) {
      for (int h = 1; h < height - 1; h++) {
        locs.add(new Location(h, w));
      }
    }

    // choose starting point
    Location startingPoint = locs.get(rnd.nextInt(locs.size()));

    Set<Location> visitedSquares = new LinkedHashSet<>();

    visitedSquares.add(startingPoint);

    // Initialize the Wall List
    List<Location> wallList = new LinkedList<>();

    wallList.addAll(getAdjacentPoints(startingPoint, width, height));

    while (wallList.size() > 0) {
      // Pick a random wall from the list
      Location randomWall = wallList.get(rnd.nextInt(wallList.size()));
      Set<Location> adjacent = getAdjacentPoints(randomWall, width, height);


      int numVisited = 0;
      for (Location p : adjacent) {
        if (visitedSquares.contains(p))
          numVisited++;
      }

      if (numVisited == 1
          && ((wallList.size() <= 4) || (rnd.nextInt(100) > (100 - branchFactor)))) {
        visitedSquares.add(randomWall);
        for (Location p : adjacent) {
          if (!visitedSquares.contains(p) && !wallList.contains(p))
            wallList.add(p);
        }
      }

      wallList.remove(randomWall);

    }

    // Choose Treasure Squares and starting square
    Location[] orderedSquares = new Location[visitedSquares.size()];
    visitedSquares.toArray(orderedSquares);

    board.playerLocation = startingPoint;

    Set<Location> treasureSquares = new HashSet<>();


    // make sure the treasure is not on the start square
    int treasureLocation = orderedSquares.length - 1;
    while ((treasureSquares.size() < treasures) && (treasureLocation > 0)) {
      if (!orderedSquares[treasureLocation].equals(board.playerLocation))
        treasureSquares.add(orderedSquares[treasureLocation]);
      treasureLocation--;
    }


    for (int w = 0; w < width; w++) {
      for (int h = 0; h < height; h++) {
        Location p = new Location(w, h);
        if (visitedSquares.contains(p) || ((w >= 1 && (h >= 1) && (h < height - 1)
            && (w < width - 1) && (rnd.nextInt(100) >= (100 - openFactor))))) {
          if (treasureSquares.contains(p)) {
            board.boardState[h][w] = new Treasure(p);
            board.treasures.add((Treasure) board.boardState[h][w]);
          } else {
            board.boardState[h][w] = new Space(p);
          }
        } else
          board.boardState[h][w] = new Wall(p);
      }
    }
    return board;
  }
}
