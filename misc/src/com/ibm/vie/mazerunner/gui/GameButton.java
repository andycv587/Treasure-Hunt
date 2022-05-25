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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Custom JButton implementation which adds mouse over and button pressed effects, an image
 * background, and a location using the default width and height.
 */
public class GameButton extends JButton {

  private BufferedImage image;
  private int xLoc;
  private int yLoc;

  /**
   * Constructor.
   */
  public GameButton() {
    super();
    init();
  }

  /**
   * Constructor.
   * 
   * @param image The image to draw for the button
   * @param x The X location to draw the button
   * @param y The Y location to draw the button
   */
  public GameButton(BufferedImage image, int x, int y) {
    super(new ImageIcon(image));
    this.image = image;
    this.xLoc = x;
    this.yLoc = y;
    init();
  }

  /**
   * Initializes all settings for the button.
   */
  private void init() {
    this.setBorder(BorderFactory.createLineBorder(Color.black));
    this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    this.setBounds(new Rectangle(xLoc, yLoc, image.getWidth(), image.getHeight()));
    this.setBackground(Color.black);

    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.yellow));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.black));
      }

      @Override
      public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.red));
      }
    });
  }
}
