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

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class TestResult {
  private final String jarPath;
  private final String studentName;
  private final String boardName;
  private final int score;
  private final Throwable exception;

  private static String[] CSV_HEADERS =
      {"Jar Path", "Student Name", "Board Name", "Score", "Failure"};

  public TestResult(String jarPath, String studentName, String boardName, int score) {
    this.jarPath = jarPath;
    this.studentName = (studentName != null) ? studentName : "";
    this.boardName = boardName;
    this.score = score;
    this.exception = null;
  }

  public TestResult(String jarPath, String studentName, String boardName, int score,
      Throwable exception) {
    this.jarPath = jarPath;
    this.studentName = (studentName != null) ? studentName : "";
    this.boardName = boardName;
    this.score = score;
    this.exception = exception;
  }

  public Throwable getException() {
    return exception;
  }

  public int getScore() {
    return score;
  }

  public static void printResults(FileWriter fw, Iterable<TestResult> results) {
    CSVFormat format = CSVFormat.DEFAULT.builder().setHeader(TestResult.CSV_HEADERS).build();
    try (CSVPrinter printer = new CSVPrinter(fw, format)) {
      for (TestResult result : results) {
        printer.printRecord(result.jarPath, result.studentName, result.boardName, result.score,
            (result.exception != null) ? result.exception.getClass().getSimpleName() : "");
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  public String toString() {
    return "Jar=" + jarPath + "\n" + "Name=" + studentName + "\nBoard=" + boardName + "\nScore="
        + score + "\nError="
        + ((this.exception != null) ? this.exception.getClass().getSimpleName() : "null");
  }
}
