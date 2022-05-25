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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * This is the base class for screens to be displayed during the game.
 */
public class Screen extends JPanel {

  protected CardLayout screenManager;
  protected JPanel screens;

  /**
   * Constructor which sets member variables.
   * 
   * @param screenManager Used to switch between actively displayed screen
   * @param screens Panel which contains the screens to display
   */
  public Screen(CardLayout screenManager, JPanel screens) {
    this.screenManager = screenManager;
    this.screens = screens;
  }

  /**
   * Used to display the requested screen
   * 
   * @param screenName The screen name to display
   * @return An listener to be used as an action for components such as buttons
   */
  protected ActionListener getScreenAction(final String screenName) {
    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        screenManager.show(screens, screenName);
      }
    };
    return action;
  }

  /**
   * Redraws the screen
   */
  protected void redraw() {
    revalidate();
    validate();
    repaint();
  }
}
