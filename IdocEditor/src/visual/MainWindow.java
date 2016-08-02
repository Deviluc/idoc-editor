package visual;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

import parser.IdocParser;

public class MainWindow extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox pane = new VBox();
		
		final IdocView treeView = new IdocView();
		
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem menuItemOpen = new MenuItem("Open");
		menuItemOpen.setOnAction(a -> {
			Optional.ofNullable(new FileChooser().showOpenDialog(primaryStage)).ifPresent(f -> {
				try {
					treeView.loadIdoc(IdocParser.parse(f.getAbsolutePath()));
				} catch (IOException e) {
					//TODO Error dialog
					e.printStackTrace();
				}
			});
		});
		
		MenuItem menuItemSave = new MenuItem("Save");
		menuItemSave.setOnAction(a -> {
			Optional.ofNullable(new FileChooser().showSaveDialog(primaryStage)).ifPresent(f -> {
				try {
					FileWriter writer = new FileWriter(f);
					writer.write(treeView.getIdoc().generateIdocFile());
					writer.close();
				} catch (IOException e) {
					//TODO Error dialog
					e.printStackTrace();
				}
			});
		});
		
		fileMenu.getItems().addAll(menuItemOpen, menuItemSave);
		menuBar.getMenus().addAll(fileMenu);
		
		pane.getChildren().add(menuBar);
		
		
		
		pane.getChildren().add(treeView);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Idoc Editor");
		primaryStage.setMaximized(true);
		primaryStage.show();		
	}
	
	public static void main(final String[] args) {
		launch(args);
	}

}
