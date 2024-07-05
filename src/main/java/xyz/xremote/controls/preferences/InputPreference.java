package xyz.xremote.controls.preferences;

import xyz.xremote.utils.FXUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class InputPreference extends HBox {
    @FXML
    private Node root;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private TextField textField;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);
    private final DoubleProperty inputWidth = new SimpleDoubleProperty(50);

    public InputPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/InputPreference.fxml");
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

        // Input
        textField.minWidthProperty().bind(inputWidth);
        textField.prefWidthProperty().bind(inputWidth);

        // On Action
        root.setOnMouseClicked(event -> textField.requestFocus());
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

    public final DoubleProperty inputWidthProperty() {
        return inputWidth;
    }

    public final void setInputWidth(double value) {
        inputWidth.set(value);
    }

    public final double getInputWidth() {
        return inputWidth.get();
    }

    public final void setSummary(String value) {
        summary.set(value);
    }

    public final String getSummary() {
        return summary.get();
    }

    public final StringProperty textProperty() {
        return textField.textProperty();
    }

    public final void setText(String value) {
        textField.textProperty().set(value);
    }

    public final String getText() {
        return textField.textProperty().get();
    }
}
