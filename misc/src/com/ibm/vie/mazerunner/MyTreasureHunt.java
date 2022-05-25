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

import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ibm.vie.mazerunner.gui.*;

/**
 * Main launcher for the "My Treasure Hunt" application.
 */
public class MyTreasureHunt {

  private JFrame window;
  private final String TREASURE_HUNT_PNG = "gui/TreasureHunt.png";
  private final String TREASURE_HUNT_XML = "gui/TreasureHunt.xml";

  /**
   * Constructor.
   * 
   * @param IPlayer player Player implementation to run.
   */
  public MyTreasureHunt(IPlayer player) {
    loadSprites();
    init(player);
  }

  /**
   * Initializes the windows and all of its screens to display.
   * 
   * @param IPlayer player Player implementation to run
   */
  public void init(IPlayer player) {
    window = new JFrame();
    window.setTitle("My Treasure Hunt");
    window.setSize(960, 600);
    window.setLocationRelativeTo(null);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);

    CardLayout screenSwitcher = new CardLayout();
    JPanel screens = new JPanel(screenSwitcher);

    ScreenMainMenu menu = new ScreenMainMenu(screenSwitcher, screens);
    ScreenBoardSelector boards = new ScreenBoardSelector(screenSwitcher, screens);
    ScreenBoardPlayer boardPlayer = new ScreenBoardPlayer(screenSwitcher, screens, player);

    GameData.getInstance().setBoardPlayer(boardPlayer);
    GameData.getInstance().setBoardsScreen(boards);

    screens.add(menu, "Menu");
    screens.add(boards, "Boards");
    screens.add(boardPlayer, "BoardLauncher");

    window.add(screens);
    window.setVisible(true);
  }

  /**
   * Loads the game sprites from file
   */
  private void loadSprites() {
    GameData data = GameData.getInstance();
    try {
      data.loadSprites(this.getClass().getResourceAsStream(TREASURE_HUNT_PNG),
          this.getClass().getResourceAsStream(TREASURE_HUNT_XML));

      // Success, bail out.
      return;
    } catch (Exception e) {
      System.out.println("Error loading sprites, will try loading from current directory");
      e.printStackTrace();
    }

    try {

      data.loadSprites(TREASURE_HUNT_PNG, TREASURE_HUNT_XML);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Failed to load sprite sheet: " + e.toString(),
          "IO Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  /**
   * Run the Treasure Hunt game.
   * 
   * @param IPlayer player Player implementation to run
   */
  public static void run(final IPlayer player) {
    EventQueue.invokeLater(new Runnable() {
      /**
       * Runs the application
       */
      @Override
      public void run() {
        MyTreasureHunt myTreasureHunt = new MyTreasureHunt(player);
      }
    });
  }

  /**
   * Main execution point. Mainly for IBMers running the simple player. Students could call this to
   * run the SimplePlayer example.
   * 
   * @param args
   */
  public static void main(String[] args) {
    MyTreasureHunt.run(new SimplePlayer());
  }

}
