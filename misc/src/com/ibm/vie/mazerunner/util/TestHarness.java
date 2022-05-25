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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * (C) Copyright IBM Corp. 2016. All Rights Reserved. US Government Users Restricted Rights - Use,
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
/*
 * TestHarness has to exist in the default package so it can "see" MyPlayer Three is no way to
 * "import" the default package
 */
import com.ibm.vie.mazerunner.IPlayer;
import com.ibm.vie.mazerunner.MapBoard;
import com.ibm.vie.mazerunner.Move;

public class TestHarness {
  private static long SECONDS_FOR_ANALYZE = 10;
  private static long SECONDS_FOR_COMPLETE = 10;
  private static long SECONDS_FOR_MOVE = 3;


  private static List<MapBoard> loadBoards(File boardDir) {
    if (!boardDir.exists() || !boardDir.isDirectory()) {
      throw new RuntimeException(
          "The specified base directory for boards does not appear to be valid: " + boardDir);
    }

    List<MapBoard> boards = new LinkedList<>();
    for (File boardFile : boardDir
        .listFiles(file -> file.getName().toLowerCase().endsWith(".csv"))) {
      System.out.println("Loading board " + boardFile.getAbsolutePath());
      try {
        boards.add(MapBoard.parseBoard(boardFile.getAbsolutePath()));
      } catch (Exception e) {
        throw new RuntimeException("Could not load board " + boardFile.getAbsolutePath(), e);
      }
    }

    return boards;
  }

  private static IPlayer loadPlayer(File jarFile) {
    try {
      URLClassLoader loader = new StudentClassLoader(jarFile.toURI().toURL());
      try {
        Class<?> clazz = Class.forName("MyPlayer", true, loader);
        return (IPlayer) clazz.getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException
          | ClassNotFoundException e) {
        throw new RuntimeException("Could not load MyPlayer from jar " + jarFile.getName(), e);
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

  }

  public static void main(String[] args) {

    CommandLineArgs cliArgs = CommandLineArgs.parse(args);
    if (cliArgs != null) { // valid syntax
      List<MapBoard> boards = loadBoards(cliArgs.getBoardDirectory());
      Collection<TestResult> results = processPlayerJarDirectory(cliArgs.getJarDirectory(), boards);
      try {
        TestResult.printResults(new FileWriter(cliArgs.csvOutputFile), results);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private static Collection<TestResult> processPlayerJarDirectory(File directory,
      List<MapBoard> boards) {
    if (!directory.exists() || !directory.isDirectory()) {
      throw new RuntimeException(directory.getAbsolutePath() + " is not a directory");
    }

    List<TestResult> results = new LinkedList<>();
    for (File jar : directory.listFiles(file -> file.getName().endsWith(".jar"))) {
      try {
        IPlayer player = loadPlayer(jar);
        for (MapBoard board : boards) {
          System.out.println(
              "\nProcessing " + jar + " for " + player.getName() + " on board " + board.getName());
          TestResult result = runBoard(jar.getName(), player, new MapBoard(board));
          System.out.println(result);
          results.add(result);
        }
      } catch (Exception e) {
        System.err.println("Problem processing jar " + jar.getName() + e.getMessage());
        e.printStackTrace();
      }
    }
    return results;
  }

  public static TestResult runBoard(final String jar, final IPlayer player, final MapBoard board) {
    ExecutorService service = Executors.newCachedThreadPool();
    PrintStream oldOut = System.out;
    ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    try {
      // Silence any output coming from student code
      // This is essential to running speedy solutions
      System.setOut(new PrintStream(newOut));

      runWithTimeOut(service, () -> player.analyzeBoard(new MapBoard(board)), SECONDS_FOR_ANALYZE);

      while (!board.isComplete()) {
        runWithTimeOut(service, () -> {
          Move mv = player.selectMove(new MapBoard(board));
          board.move(mv);
        }, SECONDS_FOR_MOVE);
      }

      runWithTimeOut(service, () -> player.gameCompleted(new MapBoard(board)),
          SECONDS_FOR_COMPLETE);

      return new TestResult(jar, player.getName(), board.getName(), board.getScore());

    } catch (InterruptedException e) {
      throw new RuntimeException("Unexpected Framework error", e);
    } catch (ExecutionException e) {
      // Problem with student solution
      return new TestResult(jar, player.getName(), board.getName(), 0, e.getCause());
    } catch (TimeoutException e) {
      // student solution never returned us control
      return new TestResult(jar, player.getName(), board.getName(), 0, e);
    } finally {
      System.setOut(oldOut);
      service.shutdownNow();
    }
  }

  private static void runWithTimeOut(ExecutorService service, Runnable r, long seconds)
      throws InterruptedException, ExecutionException, TimeoutException {
    Future<?> f = service.submit(r);
    f.get(seconds, TimeUnit.SECONDS);
  }

  private static class CommandLineArgs {
    public File getJarDirectory() {
      return new File(jarDirectory);
    }

    public File getBoardDirectory() {
      return new File(boardDirectory);
    }

    private final String jarDirectory;
    private final String boardDirectory;
    private final String csvOutputFile;

    private CommandLineArgs(String jarDirectory, String boardDirectory, String csvOutputFile) {
      this.jarDirectory = jarDirectory;
      this.boardDirectory = boardDirectory;
      this.csvOutputFile = csvOutputFile;
    }

    public static CommandLineArgs parse(String[] args) {
      Options cmdOps = new Options();
      cmdOps.addOption(Option.builder("j") //
          .longOpt("jardir") //
          .argName("dir") //
          .hasArg() //
          .desc("Directory with student jars") //
          .valueSeparator('=') //
          .required() //
          .build());

      cmdOps.addOption(Option.builder("b") //
          .longOpt("boarddir") //
          .argName("dir") //
          .hasArg() //
          .desc("Directory with Board csvs") //
          .valueSeparator('=') //
          .required() //
          .build());

      cmdOps.addOption(Option.builder("c") //
          .longOpt("csvfile") //
          .argName("file") //
          .hasArg() //
          .desc("Output CSV") //
          .valueSeparator('=') //
          .required() //
          .build());

      CommandLineParser parser = new DefaultParser();
      try {
        CommandLine line = parser.parse(cmdOps, args);
        return new CommandLineArgs(line.getOptionValue("j"), line.getOptionValue("b"),
            line.getOptionValue("c"));
      } catch (ParseException e) {
        System.err.println("Invalid Usage: " + e.getMessage());
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.ibm.vie.mazerunner.util.TestHarness", cmdOps, true);
        return null;
      }
    }
  }
}

