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


import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.Location;
import com.ibm.vie.mazerunner.squares.ISquare;
import com.ibm.vie.mazerunner.squares.Space;
import com.ibm.vie.mazerunner.MapBoard;

/**
 * Singleton class used to hold game data that must be persistent across different screens.
 * 
 * To call the methods within this class: GameData.getInstance(). <name_of_method>()
 */
public class GameData {

  private static GameData instance = null;

  private MapBoard currentBoard;
  private String currentBoardName;
  private static String currentBoardStats;
  private BufferedImage spriteSheet;
  private Map<String, Rectangle> spriteDescriberLookup;
  private ScreenBoardPlayer boardPlayer;
  private ScreenBoardSelector boardsScreen;

  private static Boolean spritesLoaded;
  private static Boolean boardLoaded;
  private static Boolean boardsScreenLoaded;

  public static Point previousPointLocation;
  public static Point currentPointLocation;

  /**
   * Exists for instantiation purposes
   */
  protected GameData() {}

  /**
   * Used to get the instance of this singleton class. Assures only one instance exists at any time.
   * 
   * @return The single instance of this class
   */
  public static GameData getInstance() {
    if (instance == null) {
      instance = new GameData();
      spritesLoaded = false;
      boardLoaded = false;
      boardsScreenLoaded = false;
      currentBoardStats = "\n    Statistics of last run...";
      previousPointLocation = new Point(0, 0);
      currentPointLocation = new Point(0, 0);
    }
    return instance;
  }

  /**
   * Sets the active board to be used within the board player.
   * 
   * @param board The board to play
   * @param boardName The name of the board to play
   */
  public void setBoard(MapBoard board, String boardName) {
    this.currentBoard = board;
    this.currentBoardName = boardName;
  }

  /**
   * Gets the active board to play
   * 
   * @return The board to play
   */
  public MapBoard getBoard() {
    return currentBoard;
  }

  /**
   * Gets the name of the active board
   * 
   * @return The name of the active board
   */
  public String getBoardName() {
    return currentBoardName;
  }

  /**
   * Clears the currently active board information
   */
  public void clearBoard() {
    currentBoard = null;
    currentBoardName = "";
    currentBoardStats = "";
  }

  /**
   * Sets the stats of a board that has been ran to be displayed and saved on the board loader
   * screen.
   * 
   * @param stats The stats to save/display
   */
  public void setBoardStats(String stats) {
    currentBoardStats = stats;
  }

  /**
   * Getter to retrieve the board stats to be displayed/saved.
   * 
   * @return The stats of the last ran board
   */
  public String getBoardStats() {
    return currentBoardStats;
  }

  /**
   * Must be ran before "getSprite(...)" can be called. Loads a sprite sheet and its xml descriptor
   * from file for use of drawing images on the screen.
   * 
   * @param spriteSheetImage The image file containing the sprite sheet
   * @param spriteSheetXml The xml file which describes the sprite sheet
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public void loadSprites(String spriteSheetImage, String spriteSheetXml)
      throws IOException, ParserConfigurationException, SAXException {
    // Load sprite sheet image
    spriteSheet = ImageIO.read(this.getClass().getResourceAsStream("spriteSheetImage"));

    // Load xml describer for sprite sheet
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document sheetDescriber = dBuilder.parse(this.getClass().getResourceAsStream("spriteSheetXml"));

    // Creates image lookup
    buildImageTable(sheetDescriber);

    spritesLoaded = true;
  }

  /**
   * Must be ran before "getSprite(...)" can be called. Loads a sprite sheet and its xml descriptor
   * from file for use of drawing images on the screen.
   * 
   * @param spriteSheetImage The image file containing the sprite sheet
   * @param spriteSheetXml The xml file which describes the sprite sheet
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public void loadSprites(InputStream spriteSheetImageIs, InputStream spriteSheetXmlIs)
      throws IOException, ParserConfigurationException, SAXException {
    // Load sprite sheet image
    spriteSheet = ImageIO.read(spriteSheetImageIs);

    // Load xml describer for sprite sheet
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document sheetDescriber = dBuilder.parse(spriteSheetXmlIs);

    // Creates image lookup
    buildImageTable(sheetDescriber);

    spritesLoaded = true;
  }


  /**
   * Used to retrieve a sprite from a loaded sprite sheet.
   * 
   * Must call "loadSprites(...)" before this method can be called.
   * 
   * @param sprite The name of the sprite to retrieve
   * @return The sprite to retrieve
   */
  public BufferedImage getSprite(String sprite) {
    if (!spritesLoaded) {
      throw new IllegalStateException();
    }
    Rectangle spriteLoc = spriteDescriberLookup.get(sprite);
    return spriteSheet.getSubimage(spriteLoc.x, spriteLoc.y, spriteLoc.width, spriteLoc.height);
  }

  /**
   * Return the dimensions of the named sprite
   * 
   * @param sprite The name of the sprite to retrieve the dimensions for
   * @return The dimensions of the sprite
   */
  public Dimension getSpriteDimension(String sprite) {
    if (!spritesLoaded) {
      throw new IllegalStateException();
    }
    return spriteDescriberLookup.get(sprite).getSize();
  }

  /**
   * Draw the given square onto the Graphics context
   * 
   * @param quare The quare to draw
   * @param g The graphics to draw to at 0, 0
   */
  public void drawSquare(ISquare square, Graphics g) {
    Rectangle bounds = spriteDescriberLookup.get(square.getSpriteName());
    Rectangle clipBounds = g.getClipBounds();
    Space space = (Space) square;
    g.drawImage(spriteSheet, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width,
        clipBounds.y + clipBounds.height, bounds.x, bounds.y, bounds.x + bounds.width,
        bounds.y + bounds.height, space.getColor(), null);
  }

  /**
   * Sets the ScreenBoardPlayer screen that will be used to draw the board being played. This is
   * used by other screens to force a reload of this screen when a new board is requested to be
   * played.
   * 
   * @param player The instance of ScreenBoardPlayer to set
   */
  public void setBoardPlayer(ScreenBoardPlayer player) {
    this.boardPlayer = player;
    boardLoaded = true;
  }

  /**
   * Gets the ScreenBoardPlayer screen.
   * 
   * @return ScreenBoardPlayer screen.
   */
  public ScreenBoardPlayer getBoardPlayer() {
    return boardPlayer;
  }

  /**
   * Used to reload the ScreenBoardPlayer for when a new board has been requested to be played.
   * "setBoardPlayer" must be called prior to this call.
   */
  public void reloadBoardPlayer() throws ScreenBoardPlayer.AnalyzeBoardException {
    if (!boardLoaded) {
      throw new IllegalStateException("Board is not Loaded");
    }
    this.boardPlayer.reloadBoard();

  }

  /**
   * Sets the board selector/loader screen which will be used throughout the application.
   * 
   * @param screen The screen to be used.
   */
  public void setBoardsScreen(ScreenBoardSelector screen) {
    this.boardsScreen = screen;
    boardsScreenLoaded = true;
  }

  /**
   * Used to reload the ScreenBoardSelector by other screens. An example of use is when the board
   * player wants to refresh the stats for a newly ran board. "setBoardsScreen" must be called prior
   * to this call.
   */
  public void reloadBoardsScreen() {
    if (!boardsScreenLoaded) {
      throw new IllegalStateException();
    }
    boardsScreen.updateStats();
  }

  /**
   * Used to build the image table from the sprite sheet.
   * 
   * @param sheetDescriber The xml document with image descriptors to parse
   */
  private void buildImageTable(Document sheetDescriber) {
    sheetDescriber.getDocumentElement().normalize();

    spriteDescriberLookup = new HashMap<String, Rectangle>();

    // Iterate through each sprite xml entry and find the sprite name and
    // rect location in the sprite sheet
    NodeList spriteNodes = sheetDescriber.getElementsByTagName("sprite");
    for (int currentSpriteNode = 0; currentSpriteNode < spriteNodes
        .getLength(); currentSpriteNode++) {
      Node spriteNode = spriteNodes.item(currentSpriteNode);
      if (spriteNode.getNodeType() == Node.ELEMENT_NODE) {

        Element spriteElement = (Element) spriteNode;

        String spriteName = spriteElement.getAttribute("n");
        int spriteX = Integer.parseInt(spriteElement.getAttribute("x"));
        int spriteY = Integer.parseInt(spriteElement.getAttribute("y"));
        int spriteW = Integer.parseInt(spriteElement.getAttribute("w"));
        int spriteH = Integer.parseInt(spriteElement.getAttribute("h"));

        Rectangle spriteLocation = new Rectangle(spriteX, spriteY, spriteW, spriteH);
        spriteDescriberLookup.put(spriteName, spriteLocation);
      }
    }
  }
}
