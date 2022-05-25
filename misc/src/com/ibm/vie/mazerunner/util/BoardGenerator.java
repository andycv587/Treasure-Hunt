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

package com.ibm.vie.mazerunner.util;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.ibm.vie.mazerunner.IBoard;
import com.ibm.vie.mazerunner.Location;
import com.ibm.vie.mazerunner.MapBoard;
import com.ibm.vie.mazerunner.squares.ISquare;
import com.ibm.vie.mazerunner.squares.Treasure;
import com.ibm.vie.mazerunner.squares.Wall;

/**
 * 
 * Command line tool to generate a board
 * 
 * Extract the packaged product, cd to the lib directory and: java -cp
 * "commons-cli-1.5.0.jar:javacsv.jar:treasurehunt.jar" com.ibm.vie.mazerunner.util.BoardGenerator
 * -name="Nick" -height=50 -width=50 -treasure=20 -branch=100 -open=0 -steps 1000
 * 
 * 
 * @author ntl
 *
 */
public class BoardGenerator {

  public static void main(String[] args) throws Error, Exception {
    Options cmdOps = new Options();
    cmdOps.addOption(Option.builder("n") //
        .longOpt("name") //
        .argName("bname") //
        .hasArg() //
        .desc("board name") //
        .valueSeparator('=') //
        .required() //
        .build());
    cmdOps.addOption( //
        Option.builder("h") //
            .longOpt("height") //
            .argName("height") //
            .hasArg() //
            .desc("board height") //
            .valueSeparator('=') //
            .required().build());
    cmdOps.addOption(Option.builder("w") //
        .longOpt("width") //
        .argName("width") //
        .hasArg() //
        .valueSeparator('=').desc("board width") //
        .required() //
        .build());
    cmdOps.addOption( //
        Option.builder("t") //
            .longOpt("treasure") //
            .argName("treasure") //
            .hasArg() //
            .valueSeparator('=') //
            .desc("num treasures") //
            .required() //
            .build());
    cmdOps.addOption(Option.builder("b") //
        .longOpt("branch") //
        .argName("bpct") //
        .hasArg() //
        .valueSeparator('=').desc("percent of branches visited") //
        .required() //
        .build());
    cmdOps.addOption(Option.builder("o") //
        .argName("opct") //
        .longOpt("open") //
        .hasArg() //
        .valueSeparator('=').desc("percent unvisited squares that are open") //
        .required() //
        .build());
    cmdOps.addOption(Option.builder("s") //
        .argName("steps") //
        .longOpt("steps") //
        .hasArg() //
        .valueSeparator('=') //
        .desc("max number of steps") //
        .required() //
        .build());

    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(cmdOps, args);
      final IBoard board = MapBoard.randomizedPrim(//
          line.getOptionValue("b"), Integer.parseInt(line.getOptionValue("w")),
          Integer.parseInt(line.getOptionValue("h")), Integer.parseInt(line.getOptionValue("t")),
          Integer.parseInt(line.getOptionValue("b")), Integer.parseInt(line.getOptionValue("o")),
          Integer.parseInt(line.getOptionValue("s")));

      String file_name = line.getOptionValue("n") + ".csv";
      writeBoardToCSV(file_name, board);
    } catch (ParseException exp) {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("com.ibm.vie.mazerunner.util.BoardGenerator", cmdOps, true);
      System.exit(1);
    }

  }


  public static void writeBoardToCSV(String path, IBoard board)
      throws FileNotFoundException, IOException {
    try (FileOutputStream fos = new FileOutputStream(path)) {
      StringBuffer outputContents = new StringBuffer();

      for (int y = 0; y < board.getHeight(); y++) {
        for (int x = 0; x < board.getWidth(); x++) {

          ISquare s = board.getSquareAt(new Location(y, x));
          if (s instanceof Treasure)
            outputContents.append('T');
          else if (s instanceof Wall)
            outputContents.append('#');
          else if ((board.getPlayerLocation().getCol() == x)
              && (board.getPlayerLocation().getRow() == y))
            outputContents.append('P');
          else
            outputContents.append(' ');

          if (x < board.getWidth() - 1)
            outputContents.append(',');
          else
            outputContents.append('\n');
        }
      }

      // Add a line for max moves
      outputContents.append(board.getMaxSteps());
      for (int x = 0; x < board.getWidth() - 2; x++)
        outputContents.append(',');
      outputContents.append('\n');

      fos.write(outputContents.toString().getBytes());
      System.out.write(outputContents.toString().getBytes());
    } finally {

    }
  }

}
