package xyz.xremote.controls.preferences;

import xyz.xremote.utils.FXUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class SliderPreference extends HBox {
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private Slider slider;
    @FXML
    private Label lblValue;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty valueText = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);

    public SliderPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/SliderPreference.fxml");
    }

    @FXML
    private void initialize() {
        FXUtils.createOnMouseEnterExitedBorderEffect(this);
        lblName.textProperty().bind(title);
        lblValue.textProperty().bind(valueText);
        lblSummary.textProperty().bind(summary);
        lblSummary.visibleProperty().bind(lblSummary.textProperty().isNotEmpty());
        lblSummary.managedProperty().bind(lblSummary.visibleProperty());
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

    public final void setValueText(String value) {
        valueText.set(value);
    }

    public final String getValueText() {
        return valueText.get();
    }

    public final void setSummary(String value) {
        summary.set(value);
    }

    public final String getSummary() {
        return summary.get();
    }

    public final DoubleProperty valueProperty() {
        return slider.valueProperty();
    }

    public final void setValue(double value) {
        slider.valueProperty().set(value);
    }

    public final double getValue() {
        return slider.valueProperty().get();
    }

    public final DoubleProperty minProperty() {
        return slider.minProperty();
    }

    public final void setMin(double value) {
        slider.minProperty().set(value);
    }

    public final double getMin() {
        return slider.minProperty().get();
    }

    public final DoubleProperty maxProperty() {
        return slider.maxProperty();
    }

    public final void setMax(double value) {
        slider.maxProperty().set(value);
    }

    public final double getMax() {
        return slider.maxProperty().get();
    }
}
