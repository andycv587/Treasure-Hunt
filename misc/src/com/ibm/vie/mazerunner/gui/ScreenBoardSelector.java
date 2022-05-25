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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingUtilities;
import javax.swing.SpringLayout;
import javax.swing.JProgressBar;
import javax.swing.JDialog;


import com.ibm.vie.mazerunner.MapBoard;
import com.ibm.vie.mazerunner.IPlayer;
import com.ibm.vie.mazerunner.MapBoardView;
import com.ibm.vie.mazerunner.Move;
import com.ibm.vie.mazerunner.util.TestHarness;
import com.ibm.vie.mazerunner.util.TestResult;

/**
 * Screen used to load and launch boards, view and save last run boards to file, and run all loaded
 * boards (when implemented).
 */
public class ScreenBoardSelector extends Screen {

  private Map<String, MapBoard> loadedBoards;
  private JFileChooser fileLoader;
  private JFileChooser fileSaver;
  private JTextArea results;
  private JScrollPane resultsPane;
  private JScrollPane boardsPane;
  private JPanel boardLaunchers;

  /**
   * Constructor.
   *
   * @param screenManager Refer to base class
   * @param screens Refer to base class
   */
  public ScreenBoardSelector(CardLayout screenManager, JPanel screens) {
    super(screenManager, screens);
    init();
  }

  /**
   * Updates the stats on the screen. Called by GameData.
   */
  public void updateStats() {
    results.setText(GameData.getInstance().getBoardStats());
  }

  /**
   * Initializes the file loader and saver
   */
  private void init() {

    fileLoader = new JFileChooser(getDefaultBoardPath());
    FileNameExtensionFilter loadFilter =
        new FileNameExtensionFilter("CSV Files (Comma Separated Values)", "csv");
    fileLoader.setFileFilter(loadFilter);
    fileLoader.setMultiSelectionEnabled(true);

    fileSaver = new JFileChooser();
    FileNameExtensionFilter saveFilter = new FileNameExtensionFilter("Text Files", "txt");
    fileSaver.setFileFilter(saveFilter);

    loadedBoards = new LinkedHashMap<String, MapBoard>();

    createLayout();
    loadDefaultBoards();
  }

  /**
   * Refer to base class
   */
  private void createLayout() {

    this.setBackground(new Color(173, 131, 57));
    this.setOpaque(true);

    GameButton menuButton = new GameButton(GameData.getInstance().getSprite("button_menu"), 20, 20);
    menuButton.addActionListener(getScreenAction("Menu"));
    GameButton loadBoardButton =
        new GameButton(GameData.getInstance().getSprite("button_load_board"), 143, 20);
    loadBoardButton.addActionListener(loadBoard(this));
    GameButton runAllButton =
        new GameButton(GameData.getInstance().getSprite("button_run_all"), 321, 20);
    runAllButton.addActionListener(runAllBoards(this));
    GameButton saveButton =
        new GameButton(GameData.getInstance().getSprite("button_save"), 848, 20);
    saveButton.addActionListener(saveStatistics(this));

    GridLayout boardLaunchersLayout = new GridLayout(0, 1, 5, 5);
    boardLaunchers = new JPanel();
    boardLaunchers.setLayout(boardLaunchersLayout);
    boardLaunchers.setBackground(new Color(173, 131, 57));
    boardLaunchers.setOpaque(true);
    JPanel flowPane = new JPanel();
    flowPane.setLayout(new FlowLayout());
    flowPane.add(boardLaunchers);
    flowPane.setBackground(new Color(173, 131, 57));
    boardsPane = new JScrollPane(flowPane);
    boardsPane.setBounds(new Rectangle(20, 83, 436, 467));

    results = new JTextArea();
    results.setEditable(false);
    resultsPane = new JScrollPane(results);
    resultsPane.setBounds(new Rectangle(480, 83, 460, 467));

    results.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
    results.setText(GameData.getInstance().getBoardStats());

    this.setLayout(null);

    this.add(boardsPane);
    this.add(resultsPane);
    this.add(menuButton);
    this.add(loadBoardButton);
    this.add(runAllButton);
    this.add(saveButton);
  }

  /**
   * Attempts to load all valid boards from the default path.
   */
  private void loadDefaultBoards() {
    File boardFolder = getDefaultBoardPath();
    if (boardFolder.exists() && boardFolder.isDirectory()) {
      loadBoards(boardFolder.listFiles(new java.io.FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith(".csv");
        }
      }));
    } else {
      System.out.println("Warning: failed to find default boards");
    }
  }

  /**
   * Uses the file loader to load one or more boards and puts them into the baord viewer if the load
   * was successful.
   *
   * @param boards The parent class the JFileChooser belongs to.
   * @return The listener for this action
   */
  private ActionListener loadBoard(final ScreenBoardSelector boards) {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        int result = fileLoader.showOpenDialog(boards);
        if (result == JFileChooser.APPROVE_OPTION) {
          loadBoards(fileLoader.getSelectedFiles());
        }
      }
    };
    return action;
  }

  /**
   * Loads one or more boards and puts them into the baord viewer if the load was successful.
   * 
   * @param files An array of files to attemp to load.
   */
  private void loadBoards(File[] files) {
    StringBuilder errors = new StringBuilder();
    for (File file : files) {
      String fullPath = file.getAbsolutePath();
      String filename = file.getName();
      final String boardName = filename.substring(0, filename.length() - 4);
      try {
        if (loadedBoards.containsKey(boardName)) {
          errors.append(boardName + " already loaded\n");
        } else {
          MapBoard board = MapBoard.parseBoard(fullPath);
          loadedBoards.put(boardName, board);

          final JPanel boardPanel = new JPanel();
          SpringLayout boardPanelLayout = new SpringLayout();
          boardPanel.setLayout(boardPanelLayout);

          JLabel nameLabel = new JLabel(boardName, JLabel.LEFT);
          nameLabel.setFont(new Font("Verdana", Font.BOLD, 20));
          nameLabel.setForeground(Color.black);

          GameButton boardButton =
              new GameButton(GameData.getInstance().getSprite("button_launch"), 0, 0);
          boardButton.addActionListener(launchBoard(board, boardName));

          GameButton closeButton =
              new GameButton(GameData.getInstance().getSprite("button_remove"), 0, 0);
          closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              loadedBoards.remove(boardName);
              boardLaunchers.remove(boardPanel);
              boardLaunchers.revalidate();
            }
          });

          boardPanelLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5, SpringLayout.NORTH,
              boardPanel);
          boardPanelLayout.putConstraint(SpringLayout.VERTICAL_CENTER, boardButton, 0,
              SpringLayout.VERTICAL_CENTER, boardPanel);
          boardPanelLayout.putConstraint(SpringLayout.VERTICAL_CENTER, closeButton, 0,
              SpringLayout.VERTICAL_CENTER, boardButton);
          boardPanelLayout.putConstraint(SpringLayout.WEST, nameLabel, 5, SpringLayout.WEST,
              boardPanel);
          boardPanelLayout.putConstraint(SpringLayout.EAST, nameLabel, 20, SpringLayout.WEST,
              boardButton);
          boardPanelLayout.putConstraint(SpringLayout.EAST, boardButton, -5, SpringLayout.WEST,
              closeButton);
          boardPanelLayout.putConstraint(SpringLayout.EAST, closeButton, -5, SpringLayout.EAST,
              boardPanel);

          boardPanel.add(nameLabel);
          boardPanel.add(boardButton);
          boardPanel.add(closeButton);

          boardPanel.setPreferredSize(new Dimension(405, 50));
          boardPanel.setBackground(new Color(173, 131, 57));

          int boardIdx = findBoardInsertIndex(boardName);
          boardLaunchers.add(boardPanel, boardIdx);
          boardLaunchers.revalidate();
        }
      } catch (Exception e) {
        errors.append("Failed to read file: " + fullPath + "\n");
        e.printStackTrace();
      }
    }
    if (errors.length() > 0) {
      JOptionPane.showMessageDialog(null, errors.toString(), "IO Error", JOptionPane.ERROR_MESSAGE);

    }
  }

  // Lazy search
  private int findBoardInsertIndex(String boardNameToAdd) {
    int boardIdx = 0;

    while (boardIdx < boardLaunchers.getComponentCount()) {
      JPanel board = (JPanel) boardLaunchers.getComponent(boardIdx);
      String boardName = ((JLabel) board.getComponent(0)).getText();
      int compareVal = boardNameToAdd.compareTo(boardName);
      if (compareVal <= 0) {
        break;
      } else {
        boardIdx++;
      }
    }

    if (boardIdx == boardLaunchers.getComponentCount()) {
      // JPanel.add says to use -1 to append to end
      boardIdx = -1;
    }

    return boardIdx;
  }

  /**
   * Return the File object for the path to the default boards.
   *
   * @return defaultBoardPath
   */
  private static File getDefaultBoardPath() {
    String[] paths = new String[] {"../boards", "./boards", "./maps"};

    File boardFolder = new File(".");
    for (String path : paths) {
      boardFolder = new File(path);
      if (boardFolder.exists() && boardFolder.isDirectory()) {
        break;
      }
    }
    return boardFolder;
  }


  /**
   * Saves the statistics of the last ran board(s) to file.
   *
   * @param boards The parent component the JFileChooser should belong to
   * @return The listener for this action
   */
  private ActionListener saveStatistics(final ScreenBoardSelector boards) {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        int result = fileSaver.showSaveDialog(boards);
        if (result == JFileChooser.APPROVE_OPTION) {
          try {
            String outPath = fileSaver.getSelectedFile().getAbsolutePath();
            if (!outPath.endsWith(".txt")) {
              outPath += ".txt";
            }
            PrintWriter writer = new PrintWriter(outPath);
            writer.print(GameData.getInstance().getBoardStats().replace("\n",
                System.getProperty("line.separator")));
            writer.close();
          } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Failed to save file: " + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
        }
      }
    };
    return action;
  }

  /**
   * Launches a selected board into the "BoardLauncher" screen to be ran
   *
   * @param board The board to run
   * @param boardName The name of the baord to be ran
   * @return The listener for this action
   */
  private ActionListener launchBoard(final MapBoard board, final String boardName) {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        MapBoard mBoardCopy = new MapBoard(board);
        GameData.getInstance().setBoard(mBoardCopy, boardName);
        try {
          GameData.getInstance().reloadBoardPlayer();
        } catch (ScreenBoardPlayer.AnalyzeBoardException ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          throw new RuntimeException("Unable to initialize the board", ex);
        }
        screenManager.show(screens, "BoardLauncher");
      }
    };
    return action;
  }

  /**
   * Runs all the loaded boards agains the IPlayer and displays the results.
   *
   * @param boards The parent component the JFileChooser should belong to
   * @return The listener for this action
   */
  private ActionListener runAllBoards(final ScreenBoardSelector boards) {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        final IPlayer player = GameData.getInstance().getBoardPlayer().getPlayer();
        final StringBuilder stats = new StringBuilder();

        final int boardCount[] = {0};
        final boolean running[] = {true};

        final JDialog dlg = new JDialog(SwingUtilities.windowForComponent(ScreenBoardSelector.this),
            "Run All Progress", Dialog.ModalityType.APPLICATION_MODAL);
        final JProgressBar dpb = new JProgressBar(0, loadedBoards.size());
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(300, 75);
        dlg.setLocationRelativeTo(ScreenBoardSelector.this);

        dlg.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            running[0] = false;
          }
        });

        // Runnable to throw back on the EDT to update GUI
        final Runnable updateStats = new Runnable() {
          public void run() {
            GameData.getInstance().setBoardStats(stats.toString());
            GameData.getInstance().reloadBoardsScreen();
            dpb.setValue(boardCount[0]);
            if (dpb.getValue() == loadedBoards.size()) {
              dlg.setVisible(false);
            }
          }
        };

        // Spawn main work in new thread to keep GUI responsive.
        new Thread(new Runnable() {
          public void run() {
            if (loadedBoards.size() < 1) {
              stats.append("There are no boards loaded to run");
            } else {
              int grandTotal = 0;
              List<String> boards = new LinkedList<>(loadedBoards.keySet());
              Collections.sort(boards);
              DecimalFormat num_formatter = new DecimalFormat("#,###");
              stats.append(String.format("%-12s", "Board Name"));
              stats.append(String.format("%8s", "Score"));
              stats.append("\n_________________________\n");
              for (String boardName : boards) {
                stats.append("\n" + String.format("%-12s", boardName + ":"));
                SwingUtilities.invokeLater(updateStats);

                TestResult result = TestHarness.runBoard(player.getClass().getName(), player,
                    new MapBoard(loadedBoards.get(boardName)));


                if (result.getException() != null) {
                  stats.append(result.getException().toString());
                } else {
                  stats.append(String.format("%8s", num_formatter.format(result.getScore())));
                }
                SwingUtilities.invokeLater(updateStats);

                grandTotal += result.getScore();
                ++boardCount[0];
                SwingUtilities.invokeLater(updateStats);
                if (!running[0]) {
                  stats.append("\nCancelled by user");
                  break;
                }
              }
              stats.append("\n\n");
              stats.append(String.format("%-12s", "Grand Total:")
                  + String.format("%8s", num_formatter.format(grandTotal)));
            }
            SwingUtilities.invokeLater(updateStats);
          }
        }).start();
        dlg.setVisible(true);
      }
    };
    return action;
  }
}
