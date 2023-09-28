package com.crazyxacker.apps.xremote.controls.preferences;

import com.crazyxacker.apps.xremote.controls.JFXDefaultColorPicker;
import com.crazyxacker.apps.xremote.utils.FXUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ColorPickerPreference extends HBox {
    @FXML
    private Node root;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private JFXDefaultColorPicker colorPicker;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);

    public ColorPickerPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/ColorPickerPreference.fxml");
    }

    @FXML
    private void initialize() {
        FXUtils.createOnMouseEnterExitedBorderEffect(this);

        // Name
        lblName.textProperty().bind(title);

        // Summary
        lblSummary.textProperty().bind(summary);
        lblSummary.visibleProperty().bind(summary.isNotEmpty());
        lblSummary.managedProperty().bind(summary.isNotEmpty());

        // On Action
        root.setOnMouseClicked(event -> colorPicker.show());
    }

    //**************************//
    //*       Properties      **//
    //**************************//

    public final void setTitle(String value) {
        title.set(value);
    }

    public final String getTitle() {
        return title.get();
    }

    public final ObjectProperty<Color> colorProperty() {
        return colorPicker.valueProperty();
    }

    public final void setColor(Color color) {
        colorPicker.valueProperty().set(color);
    }

    public final Color getColor() {
        return colorPicker.valueProperty().get();
    }

    public final void setSummary(String value) {
        summary.set(value);
    }

    public final String getSummary() {
        return summary.get();
    }
}