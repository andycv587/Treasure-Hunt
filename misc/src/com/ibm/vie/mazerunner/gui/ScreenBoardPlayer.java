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
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

import com.ibm.vie.mazerunner.BoardPanel;
import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.IPlayer;
import com.ibm.vie.mazerunner.MapBoard;
import com.ibm.vie.mazerunner.MapBoardView;
import com.ibm.vie.mazerunner.Move;

/**
 * Screen used to display and run a map/board currently set within the GameData.
 */
public class ScreenBoardPlayer extends Screen implements Runnable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final int MIN_SPEED = 1;
  private final int MAX_SPEED = 20;
  private final int DEFAULT_SPEED = 16;

  private JLabel totalStepsLabel;
  private JLabel stepsLeftLabel;
  private JLabel stepsTakenLabel;
  private JLabel treasureCountLabel;
  private JLabel treasuresLeftLabel;
  private JLabel treasuresFoundLabel;
  private JLabel scoreLabel;
  private JLabel nameLabel;

  private JSlider speedSelector;

  private IPlayer player;
  private BoardPanel board;

  private Boolean runGame;


  /**
   * Constructor.
   * 
   * @param screenManager Refer to base class
   * @param screens Refer to base class
   * @param player Player implementation
   */
  public ScreenBoardPlayer(CardLayout screenManager, JPanel screens, IPlayer player) {
    super(screenManager, screens);
    CreateLayout();
    if (player == null) {
      throw new IllegalArgumentException("Player implementation must be non-null");
    }
    this.player = player;
    runGame = false;
  }

  public static class AnalyzeBoardException extends Exception {

    private static final long serialVersionUID = 1L;

    public AnalyzeBoardException(String message, Throwable e) {
      super(message, e);
    }

  }

  /**
   * Reloads and redraws the board
   * 
   * @throws AnalyzeBoardException
   */
  public void reloadBoard() throws AnalyzeBoardException {
    if (board != null) {
      this.remove(board);
    }
    board = new BoardPanel();
    final MapBoard gameBoard = GameData.getInstance().getBoard();
    board.loadBoard(gameBoard);
    board.setBounds(new Rectangle(20, 83, 532, 467));
    this.add(board);
    updateLabels();

    final JProgressBar dpb = new JProgressBar();
    dpb.setIndeterminate(true);

    final JDialog dlg = new JDialog(SwingUtilities.windowForComponent(this), "Please wait...",
        Dialog.ModalityType.APPLICATION_MODAL);
    dlg.add(dpb);
    dlg.add(BorderLayout.NORTH, new JLabel("Running IPlayer.analyzeBoard(IBoard board)..."));
    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dlg.setSize(300, 100);
    dlg.setLocationRelativeTo(this);

    class AnalyzeBoardWorker extends SwingWorker<Void, Void> {

      public RuntimeException getEx() {
        return ex;
      }

      private RuntimeException ex = null;

      @Override
      protected void done() {
        super.done();
        dlg.dispose();
      }

      @Override
      protected Void doInBackground() {
        try {
          player.analyzeBoard(new MapBoard(gameBoard));
        } catch (RuntimeException ex) {
          this.ex = ex;
        }

        return null;
      }

    };

    AnalyzeBoardWorker sw = new AnalyzeBoardWorker();
    sw.execute();
    dlg.setVisible(true);
    if (sw.getEx() != null) {
      throw new AnalyzeBoardException(player.getClass().getName() + " .analyzeBoard() threw a "
          + sw.getEx().getClass().getSimpleName() + "!", sw.getEx());
    }

  }

  /**
   * Refer to base class
   */
  private void CreateLayout() {
    this.setBackground(new Color(173, 131, 57));
    this.setOpaque(true);

    GameButton backButton =
        new GameButton(GameData.getInstance().getSprite("button_back_to_boards"), 20, 20);
    backButton.addActionListener(updateGameDataStats());
    backButton.addActionListener(getScreenAction("Boards"));
    GameButton runButton = new GameButton(GameData.getInstance().getSprite("button_run"), 236, 20);
    runButton.addActionListener(runGameAnimation());
    GameButton pauseButton =
        new GameButton(GameData.getInstance().getSprite("button_pause"), 338, 20);
    pauseButton.addActionListener(pauseGameAnimation());
    GameButton stepButton =
        new GameButton(GameData.getInstance().getSprite("button_step"), 461, 20);
    stepButton.addActionListener(singleStepAction());

    speedSelector = new JSlider(JSlider.HORIZONTAL, MIN_SPEED, MAX_SPEED, DEFAULT_SPEED);
    speedSelector.setBackground(new Color(173, 131, 57));
    speedSelector.setBounds(new Rectangle(565, 470, 300, 25));

    nameLabel = createLabel("Board: ", 30, 572, 83);
    totalStepsLabel = createLabel("Steps available: ", 10, 572, 133);
    stepsLeftLabel = createLabel("Steps remaining: ", 10, 572, 183);
    stepsTakenLabel = createLabel("Steps taken: ", 10, 572, 233);
    treasureCountLabel = createLabel("Total treasures: ", 10, 572, 283);
    treasuresLeftLabel = createLabel("Treasures remaining: ", 10, 572, 333);
    treasuresFoundLabel = createLabel("Treasures found: ", 10, 572, 383);
    JLabel speedLabel = createLabel("Speed Setting", 10, 572, 433);

    scoreLabel = new JLabel("Score: 99999999");
    scoreLabel.setFont(new Font("Verdana", Font.BOLD, 30));
    scoreLabel.setBounds(new Rectangle(600, 20, scoreLabel.getPreferredSize().width,
        scoreLabel.getPreferredSize().height));
    scoreLabel.setForeground(Color.green);

    this.setLayout(null);
    this.add(backButton);
    this.add(runButton);
    this.add(pauseButton);
    this.add(stepButton);
    this.add(totalStepsLabel);
    this.add(stepsLeftLabel);
    this.add(stepsTakenLabel);
    this.add(treasureCountLabel);
    this.add(treasuresLeftLabel);
    this.add(treasuresFoundLabel);
    this.add(speedLabel);
    this.add(speedSelector);
    this.add(scoreLabel);
    this.add(nameLabel);

    runGame = false;
  }

  /**
   * Updates the game statistics for the currently running board
   */
  private void updateLabels() {
    IBoard mapBoard = GameData.getInstance().getBoard();
    String boardName = mapBoard.getName().substring(0, mapBoard.getName().length() - 4);
    int totalTreasures = mapBoard.getRemainingTreasureCount() + mapBoard.getObtainedTreasureCount();
    nameLabel.setText("Board: " + boardName);
    totalStepsLabel.setText("Steps available: " + mapBoard.getMaxSteps());
    stepsLeftLabel.setText("Steps remaining: " + mapBoard.getRemainingSteps());
    stepsTakenLabel
        .setText("Steps taken: " + (mapBoard.getMaxSteps() - mapBoard.getRemainingSteps()));
    treasureCountLabel.setText("Total treasures: " + totalTreasures);
    treasuresLeftLabel.setText("Treasures remaining: " + mapBoard.getRemainingTreasureCount());
    treasuresFoundLabel.setText("Treasures found: " + mapBoard.getObtainedTreasureCount());
    scoreLabel.setText("Score: " + mapBoard.getScore());
  }

  /**
   * Creates and draws a label onto the screen
   * 
   * @param text The text to display in the label
   * @param additionalSpaces The extra spaces to account for within the label
   * @param x The X location to draw the label
   * @param y The Y location to draw the label
   * @return The requested JLabel to draw
   */
  private JLabel createLabel(String text, int additionalSpaces, int x, int y) {
    String spaces = "";
    for (int i = 0; i < additionalSpaces; ++i) {
      spaces += " ";
    }
    JLabel label = new JLabel(text + spaces);
    label.setFont(new Font("Verdana", Font.BOLD, 20));
    label.setBounds(
        new Rectangle(x, y, label.getPreferredSize().width, label.getPreferredSize().height));
    label.setForeground(Color.black);
    return label;
  }

  public static class PlayerMoveException extends Exception {
    private static final long serialVersionUID = 1L;

    public PlayerMoveException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Used to time and call an individual move iteration.
   * 
   * @param mapBoard The board to iterate over
   */
  private void moveAndRedraw(MapBoard mapBoard) throws PlayerMoveException {
    GameData.previousPointLocation
        .setLocation(LocationPointUtil.toPoint(mapBoard.getPlayerLocation()));
    try {
      Move move = player.selectMove(new MapBoardView(mapBoard));
      mapBoard.move(move);
    } catch (Exception e) {
      throw new PlayerMoveException(player.getClass().getSimpleName() + ".selectMove() caused a "
          + e.getClass().getSimpleName(), e);
    }
    GameData.currentPointLocation
        .setLocation(LocationPointUtil.toPoint(mapBoard.getPlayerLocation()));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        updateLabels();
        // from the EDT, lets just call repaint on the squares that are being updated
        board.repaint(GameData.previousPointLocation);
        board.repaint(GameData.currentPointLocation);
      }
    });
  }

  /**
   * Used to step through an iteration of the player's choice
   * 
   * @return The listener for this action
   */
  private ActionListener singleStepAction() {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        MapBoard mapBoard = GameData.getInstance().getBoard();
        if (!mapBoard.isComplete()) {
          try {
            moveAndRedraw(mapBoard);
          } catch (PlayerMoveException e) {
            e.printStackTrace(); // Student will be able to see this in their output
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    };
    return action;
  }

  /**
   * Used to start the continuous iterations of the players moves
   * 
   * @return The listener for this action
   */
  private ActionListener runGameAnimation() {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        IBoard mapBoard = GameData.getInstance().getBoard();
        if (!mapBoard.isComplete() && !runGame) {
          runGame = true;
          new Thread(ScreenBoardPlayer.this).start();
        }
      }
    };
    return action;
  }

  /**
   * Updates the game stats within GameData to be used for viewing/saving stats.
   * 
   * @return The listener for this action
   */
  private ActionListener updateGameDataStats() {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        IBoard mapBoard = GameData.getInstance().getBoard();
        int totalTreasures =
            mapBoard.getRemainingTreasureCount() + mapBoard.getObtainedTreasureCount();
        String stats = "\n   " + GameData.getInstance().getBoardName();
        stats += "\n\n     Steps available: " + mapBoard.getMaxSteps();
        stats += "\n     Steps remaining: " + mapBoard.getRemainingSteps();
        stats += "\n     Steps taken: " + (mapBoard.getMaxSteps() - mapBoard.getRemainingSteps());
        stats += "\n     Total treasures: " + totalTreasures;
        stats += "\n     Treasures remaining: " + mapBoard.getRemainingTreasureCount();
        stats += "\n     Treasures found: " + mapBoard.getObtainedTreasureCount();
        stats += "\n     Score: " + mapBoard.getScore();

        if (!mapBoard.isComplete()) {
          stats += " (UNFINISHED)";
        }

        GameData.getInstance().setBoardStats(stats);
        GameData.getInstance().reloadBoardsScreen();
        runGame = false;
      }
    };
    return action;
  }

  /**
   * Pauses any currently running game animation. If the board is not currently iterating through
   * player's moves, this has no effect.
   * 
   * @return The listener for this action
   */
  private ActionListener pauseGameAnimation() {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        runGame = false;
      }
    };
    return action;
  }

  /**
   * Returns the instance of IPlayer that is playing the game.
   * 
   * @return The IPlayer playing the game.
   */
  public IPlayer getPlayer() {
    return player;
  }

  /**
   * Runs the game animation (watching the players move iterate over the board.
   */
  @Override
  public void run() {
    long beforeTime, timeDiff, sleep;
    beforeTime = System.currentTimeMillis();
    while (runGame) {
      MapBoard mapBoard = GameData.getInstance().getBoard();
      if (!mapBoard.isComplete()) {
        try {
          moveAndRedraw(mapBoard);
        } catch (PlayerMoveException e) {
          e.printStackTrace(); // Student will be able to see this in their output
          JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          runGame = false;
        }
      } else {
        scoreLabel.setText("Score: " + mapBoard.getScore());
        runGame = false;
        board.revalidate();
        board.repaint();
        redraw();
      }

      int frameDelay = 1010 - (50 * speedSelector.getValue());
      timeDiff = System.currentTimeMillis() - beforeTime;
      sleep = frameDelay - timeDiff;

      if (sleep < 0) {
        sleep = 2;
      }

      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {

      }

      beforeTime = System.currentTimeMillis();
    }
  }
}
