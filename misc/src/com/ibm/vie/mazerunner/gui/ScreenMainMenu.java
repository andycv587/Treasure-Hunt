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

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * This is the first screen that is displayed when the Treasur Hunter game begins and displays the
 * game's main menu. This is a derived class of "Screen".
 */
public class ScreenMainMenu extends Screen {

  /**
   * Constructor.
   * 
   * @param screenManager Refer to base class
   * @param screens Refer to base class
   */
  public ScreenMainMenu(CardLayout screenManager, JPanel screens) {
    super(screenManager, screens);
    CreateLayout();
  }

  /**
   * Refer to base class
   */
  private void CreateLayout() {
    GameButton boardsButton =
        new GameButton(GameData.getInstance().getSprite("button_boards"), 20, 450);
    boardsButton.addActionListener(super.getScreenAction("Boards"));
    // GameButton builderButton = new GameButton(GameData.getInstance().getSprite("button_builder"),
    // 20, 450);
    // builderButton.addActionListener(super.getScreenAction("Builder"));
    GameButton quitButton =
        new GameButton(GameData.getInstance().getSprite("button_quit"), 20, 500);
    quitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    this.setLayout(null);
    this.add(boardsButton);
    // this.add(builderButton);
    this.add(quitButton);
  }

  /**
   * Draws the background on the screen.
   */
  @Override
  public void paintComponent(Graphics graphics) {
    BufferedImage background = GameData.getInstance().getSprite("Title");
    graphics.drawImage(background, 0, 0, null);
  }
}
