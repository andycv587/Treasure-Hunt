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

import com.ibm.vie.mazerunner.gui.GameData;
import com.ibm.vie.mazerunner.gui.LocationPointUtil;
import com.ibm.vie.mazerunner.squares.*;
import javax.swing.*;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class BoardPanel extends JPanel {
  private int w, h;

  public BoardPanel() {

  }

  public void loadBoard(IBoard board) {
    w = board.getWidth();
    h = board.getHeight();
    setLayout(new GridLayout(h, w));
    removeAll();
    for (int j = 0; j < h; ++j) {
      for (int i = 0; i < w; ++i) {
        add(new MapSquarePanel(board, i, j));
      }
    }
    revalidate();
    repaint();
  }

  public void repaint(int row, int col) {
    getComponent(row * w + col).repaint();
  }

  public void repaint(Point p) {
    repaint(p.y, p.x);
  }

  private class MapSquarePanel extends JComponent {
    private Point pointLocation;
    private IBoard board;
    private final int squareSize = 10;
    private ISquare square;

    private BufferedImage treeImage;
    private BufferedImage treasureClosedImage;
    private BufferedImage treasureOpenImage;
    private BufferedImage playerStillUpImage;
    private BufferedImage playerStillDownImage;
    private BufferedImage playerStillLeftImage;
    private BufferedImage playerStillRightImage;
    private BufferedImage grass0Image;
    private BufferedImage grass1Image;
    private BufferedImage grass2Image;
    private BufferedImage grass3Image;

    public MapSquarePanel(IBoard board, int x, int y) {
      this.pointLocation = new Point(x, y);
      this.square = board.getSquareAt(new Location(y, x));
      this.board = board;

      // setPreferredSize(new Dimension(squareSize, squareSize));
      try {
        setPreferredSize(GameData.getInstance().getSpriteDimension(square.getSpriteName()));
      } catch (Exception e) {
        System.err.println("Failed to get sprite dimension for " + square.getSpriteName());
      }

      // treeImage = GameData.getInstance().getSprite("Tree");
      // treasureClosedImage = GameData.getInstance().getSprite("Treasure_closed");
      // treasureOpenImage = GameData.getInstance().getSprite("Treasure_open");
      playerStillUpImage = GameData.getInstance().getSprite("Hunter_Stationary_Up");
      playerStillDownImage = GameData.getInstance().getSprite("Hunter_Stationary_Down");
      playerStillLeftImage = GameData.getInstance().getSprite("Hunter_Stationary_Left");
      playerStillRightImage = GameData.getInstance().getSprite("Hunter_Stationary_Right");
      // grass0Image = GameData.getInstance().getSprite("Grass_0");
      // grass1Image = GameData.getInstance().getSprite("Grass_1");
      // grass2Image = GameData.getInstance().getSprite("Grass_2");
      // grass3Image = GameData.getInstance().getSprite("Grass_3");
    }

    public void paintComponent(Graphics g) {
      ISquare t = board.getSquareAt(LocationPointUtil.toLocation(pointLocation));

      GameData.getInstance().drawSquare(t, g);

      // String type = t.getTypeString();
      // if (type.equals("Space") || type.equals("Treasure")) {
      // if (t.getStepCount() == 0) {
      // g.drawImage(grass0Image, 0, 0, getWidth(), getHeight(), null);

      // } else if (t.getStepCount() == 1) {
      // g.drawImage(grass1Image, 0, 0, getWidth(), getHeight(), null);
      // } else if (t.getStepCount() == 2) {
      // g.drawImage(grass2Image, 0, 0, getWidth(), getHeight(), null);
      // } else {
      // g.drawImage(grass3Image, 0, 0, getWidth(), getHeight(), null);
      // }
      // if (type.equals("Treasure")) {
      // if (((Treasure) t).spaceHasTreasure()) {
      // g.drawImage(treasureClosedImage, 0, 0, getWidth(), getHeight(), null);
      // } else {
      // g.drawImage(treasureOpenImage, 0, 0, getWidth(), getHeight(), null);
      // }
      // }
      // } else if (type.equals("Wall") || type.equals("Lava") || type.equals("Water")
      // || type.equals("Mountain") || type.equals("Trees")
      // || type.equals("Bushes") || type.equals("Mud") ) {
      // g.drawImage(treeImage, 0, 0, getWidth(), getHeight(), null);
      // }

      if (board.getPlayerLocation().equals(LocationPointUtil.toLocation(pointLocation))) {
        Point curr = GameData.currentPointLocation;
        Point prev = GameData.previousPointLocation;

        if (curr.x == (prev.x - 1)) { // Determine if moving left
          g.drawImage(playerStillLeftImage, 0, 0, getWidth(), getHeight(), null);
        } else if (curr.x == (prev.x + 1)) { // Determine if moving right
          g.drawImage(playerStillRightImage, 0, 0, getWidth(), getHeight(), null);
        } else if (curr.y == (prev.y - 1)) { // Determine if moving up
          g.drawImage(playerStillUpImage, 0, 0, getWidth(), getHeight(), null);
        } else if (curr.y == (prev.y + 1)) { // Determine if moving down
          g.drawImage(playerStillDownImage, 0, 0, getWidth(), getHeight(), null);
        } else { // Likely player stayed still?
          g.drawImage(playerStillDownImage, 0, 0, getWidth(), getHeight(), null);
        }
      }
    }
  }
}
