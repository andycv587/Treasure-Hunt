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

/** This package provides the objects and methods needed to interact with the game.
 * 
 * The {@link Move} class defines the moves that can be returned from
 * {@link com.ibm.vie.mazerunner.IPlayer#selectMove IPlayer#selectMove}.
 * <UL>
 * <LI>{@link com.ibm.vie.mazerunner.Move#NORTH Move.NORTH}</LI>
 * <LI>{@link com.ibm.vie.mazerunner.Move#SOUTH Move.SOUTH}</LI>
 * <LI>{@link com.ibm.vie.mazerunner.Move#EAST Move.EAST}</LI>
 * <LI>{@link com.ibm.vie.mazerunner.Move#WEST Move.WEST}</LI>
 * <LI>{@link com.ibm.vie.mazerunner.Move#BACKTRACK Move.BACKTRACK}</LI>
 * </UL>
 * <BR>
 * The player's current position can be obtained from the {@link com.ibm.vie.mazerunner.IBoard
 * IBoard} that is provided to the {@link com.ibm.vie.mazerunner.IPlayer#selectMove
 * IPlayer#selectMove} method by calling the
 * {@link com.ibm.vie.mazerunner.IBoard#getPlayerLocation() getPlayerLocation()} method. <BR>
 * <BR>
 * 
 * The {@link com.ibm.vie.mazerunner.IBoard IBoard} instance automatically keeps track of the
 * squares that your character has already explored. You can retrieve the moves from the player's
 * current position that will move the player to unexplored squares by calling
 * {@link com.ibm.vie.mazerunner.IBoard#getUnexploredMoves() getUnexploredMoves()}. <BR>
 * <BR>
 * Just because a square is unexplored does not mean it is a good move. Some squares cost more than
 * others to walk into. The most extreme example (Walls) will cost all of your steps if you walk
 * into them. At all levels except level 8, there is a path to the treasure(s) that will only cost
 * you one step per move. A good approach would be to start by only considering moves that will cost
 * one step, as that will work for almost all boards. You can get the cost of a potential move using
 * the following approach.
 * <OL>
 * <LI>Get the player's current position</LI>
 * <LI>Find the target location of the move by applying the move to the current position.
 * {@link com.ibm.vie.mazerunner.Move#apply(com.ibm.vie.mazerunner.Location)
 * Move#apply(com.ibm.vie.mazerunner.Location)}.</LI>
 * <LI>Get the square on the board for the target location.
 * {@link com.ibm.vie.mazerunner.IBoard#getSquareAt(com.ibm.vie.mazerunner.Location)
 * IBoard#getSquareAt(com.ibm.vie.mazerunner.Location)}.</LI>
 * <LI>Get the cost of moving to the square.
 * {@link com.ibm.vie.mazerunner.squares.ISquare#getStepCost() ISquare#getStepCost()}</LI>
 * </OL>
 * <BR>
 * You can get the locations of all remaining treasures by using the
 * {@link com.ibm.vie.mazerunner.IBoard#getTreasures() getTreasures()} method. The method returns a
 * list {@link com.ibm.vie.mazerunner.squares.Treasure Treasure} objects, and you can call the
 * {@link com.ibm.vie.mazerunner.squares.Treasure#getLocation() getLocation()} method on these
 * objects. <BR>
 * <BR>
 * There are several different ways to calculate the distance between two points; a simple one is
 * the
 * <a href="https://www.omnicalculator.com/math/manhattan-distance#what-is-the-manhattan-distance">
 * manhatten distance </a>. <BR>
 * <BR>
 * One possible approach to choosing the next move might be to use a heuristic. In other words,
 * score each {@link com.ibm.vie.mazerunner.Move#NORTH NORTH},
 * {@link com.ibm.vie.mazerunner.Move#SOUTH SOUTH} {@link com.ibm.vie.mazerunner.Move#EAST EAST} and
 * {@link com.ibm.vie.mazerunner.Move#WEST WEST} move and choose the move with the best score. <BR>
 * You might use combinations the following criteria to calculate a score for a move:
 * <UL>
 * <LI>Distance from the treasure</LI>
 * <LI>Cost of making the move</LI>
 * <LI>Number of times the square has already been visited</LI>
 * </UL>
 * You might also invent some metrics that are not included here. <BR>
 * A successful algorithm will make moves that explore new squares until it arrives at the treasure.
 * Repeated exploration of the same squares has a cost, and does not yield new information. Top
 * algorithms will avoid repetition.<BR>
 * <BR>
 * <BR>
 * The {@link com.ibm.vie.mazerunner.Move#BACKTRACK BACKTRACK} move and the
 * {@link com.ibm.vie.mazerunner.IBoard#getUnexploredMoves() getUnexploredMoves()} method enable you
 * to implement a path finding solution. <BR>
 * A simple algorithm might be:
 * <OL>
 * <LI>When you arrive at a square where there are (good) unexplored moves, choose one of the moves.
 * </LI>
 * <LI>If there are no good unexplored moves, then turn around and return (backtrack).</LI>
 * </OL>
 * This algorithm can be used to traverse the maze since it will follow all unexplored (good) moves.
 * It does not find an optimal solution. In addition, there is a significant cost to actually
 * returning a backtrack move from {@link com.ibm.vie.mazerunner.IPlayer#selectMove selectMove},
 * since the player will be traversing the same squares several times.<BR>
 * <BR>
 * More advanced algorithms build on the simple algorithm, and these can be used to plan a solution
 * path that will avoid repeated visits to the same square. When multiple solutions exist, many
 * advanced algorithms still do not guarantee the optimal solution. Planning a path in advance is
 * not easy, but is a great way to improve your score.
 */
package com.ibm.vie.mazerunner;

