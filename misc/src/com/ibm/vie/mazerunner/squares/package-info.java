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

/** This package contains the different square types used on a board.
 * 
 * When interacting with a board, the
 * {@link com.ibm.vie.mazerunner.IBoard#getSquareAt(com.ibm.vie.mazerunner.Location)} returns an
 * {@link ISquare} type which can be used to check
 * <ul>
 * <li>how many times you've stepped on the square.</li>
 * <li>how many steps it will cost to move to the square.</li>
 * <li>if you can move to the square based on your current position.</li>
 * <li>the type of square using the {@link ISquare#getTypeString()} method.</li>
 * </ul>
 * 
 */
package com.ibm.vie.mazerunner.squares;

