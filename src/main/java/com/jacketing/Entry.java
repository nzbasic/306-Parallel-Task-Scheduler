// ********************************************************************************
// Copyright 2021 Team Jacketing All Rights Reserved.
//
// NOTICE: All information contained herein is, and remains the property of Team
// Jacketing (the author) and their affiliates, if any. The intellectual and
// technical concepts contained herein are proprietary to Team Jacketing , and
// are protected by copyright law. Dissemination of this information or
// reproduction of this material is strictly forbidden unless prior written
// permission is obtained from the author.
// *******************************************************************************

package com.jacketing;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.jacketing.io.cli.ProgramContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Entry extends Application {

  private static ProgramContext programContext;

  public static void main(String... argv) {
    programContext = new ProgramContext();

    try {
      JCommander.newBuilder().addObject(programContext).build().parse(argv);
      programContext.validate();

      if (programContext.isVisualized()) {
        launch();
      } else {
        beginSearch();
      }
    } catch (ParameterException e) {
      System.out.println(e.getMessage());
      System.out.println(programContext.helpText());
    }
  }

  public static void beginSearch() {
    System.out.println("Starting search...");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mockup.fxml"));
    Parent root;

    root = loader.load();
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
    new Thread(Entry::beginSearch).start();
  }
}
