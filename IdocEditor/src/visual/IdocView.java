package visual;


import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.FieldDescription;
import model.Idoc;

public class IdocView extends ScrollPane {
	
	private Idoc idoc;
	
	public IdocView() {
		super();
		
		setFitToHeight(true);
		setFitToWidth(true);
	}
	
	public void loadIdoc(final Idoc file) {
		VBox box = new VBox();
		
		AtomicInteger id = new AtomicInteger(0);
		
		file.getSections().forEach(sec -> {
			TitledPane secPane = new TitledPane();
			secPane.setText("SECTION_" + id.getAndIncrement());
			VBox secBox = new VBox();
			
			sec.getSegments().forEach(s -> {
				TitledPane pane = new TitledPane();
				pane.setText(s.getSegmentDescription().getName());
				GridPane paneContent = new GridPane();
				final AtomicInteger i = new AtomicInteger(0);
				s.getFields().forEach(f -> {
					FieldDescription description = f.getFieldDescription();
					
					Label fieldLabel = new Label(description.getName());
					paneContent.add(fieldLabel, 0, i.get());
					
					TextField fieldText = new TextField(description.getFormattedContent(f));
					fieldText.textProperty().addListener((obs, oldV, newV) -> {
						String newText = newV;
						if (newV.length() > description.getLength()) {
							newText = newV.substring(0, description.getLength()).toUpperCase();
						} else {
							newText = newV.toUpperCase();
						}
						
						fieldText.setText(newText);
						f.setContent(newText);
					});
					paneContent.add(fieldText, 1, i.get());
					i.incrementAndGet();
				});
				
				ColumnConstraints col1 = new ColumnConstraints();
				col1.setPercentWidth(50d);
				paneContent.getColumnConstraints().add(col1);
				paneContent.setMaxHeight(Double.POSITIVE_INFINITY);
				paneContent.setMaxWidth(Double.POSITIVE_INFINITY);
				paneContent.getChildren().forEach(n -> {
					GridPane.setHgrow(n, Priority.ALWAYS);
					GridPane.setVgrow(n, Priority.ALWAYS);
				});
				
				pane.setContent(paneContent);
				pane.setExpanded(false);
				secBox.getChildren().add(pane);
			});
			
			secPane.setExpanded(false);
			secPane.setContent(secBox);
			box.getChildren().add(secPane);
		});
		
		setContent(box);
		
		idoc = file;
	}
	
	public Idoc getIdoc() {
		return idoc;
	}

}
