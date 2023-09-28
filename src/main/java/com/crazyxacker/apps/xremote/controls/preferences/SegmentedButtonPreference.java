package com.crazyxacker.apps.xremote.controls.preferences;

import com.crazyxacker.apps.xremote.utils.FXUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.controlsfx.control.SegmentedButton;

import java.util.function.Consumer;

public class SegmentedButtonPreference extends VBox {
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private SegmentedButton segmentedButton;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);

    public SegmentedButtonPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/SegmentedButtonPreference.fxml");
    }

    @FXML
    private void initialize() {
        FXUtils.createOnMouseEnterExitedBorderEffect(this);
        lblName.textProperty().bind(title);

        lblSummary.textProperty().bind(summary);
        lblSummary.visibleProperty().bind(lblSummary.textProperty().isNotEmpty());
        lblSummary.managedProperty().bind(lblSummary.visibleProperty());
    }

    public <E extends Enum<E>> void addButton(E eEnum, boolean selected, Consumer<String> buttonIdConsumer) {
        addButton(eEnum, selected, buttonIdConsumer, false);
    }

    public <E extends Enum<E>> void addButton(E eEnum, boolean selected, Consumer<String> buttonIdConsumer, boolean showNeedRestartSnackbar) {
        addButton(eEnum.name(), eEnum.name(), selected, buttonIdConsumer, showNeedRestartSnackbar);
    }

    public void addButton(String buttonName, String buttonId, boolean selected, Consumer<String> buttonIdConsumer) {
        addButton(buttonName, buttonId, selected, buttonIdConsumer, false);
    }

    public void addButton(String buttonName, String buttonId, boolean selected, Consumer<String> buttonIdConsumer, boolean showNeedRestartSnackbar) {
        segmentedButton.getButtons().add(createToggleButton(buttonName, buttonId, selected, buttonIdConsumer, showNeedRestartSnackbar));
    }

    private ToggleButton createToggleButton(String toggleName, String toggleId, boolean selected, Consumer<String> toggleIdConsumer, boolean showNeedRestartSnackbar) {
        ToggleButton tg = new ToggleButton(toggleName);
        tg.setId(toggleId);
        tg.setSelected(selected);
        setToggleButtonSelectedListener(tg, toggleIdConsumer, showNeedRestartSnackbar);
        return tg;
    }

    private void setToggleButtonSelectedListener(ToggleButton tg, Consumer<String> toggleIdConsumer, boolean showNeedRestartSnackbar) {
        tg.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                toggleIdConsumer.accept(tg.getId());
                if (showNeedRestartSnackbar) {
                    FXUtils.showNeedRestartSnackbar();
                }
            }
        });
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

    public final void setSummary(String value) {
        summary.set(value);
    }

    public final String getSummary() {
        return summary.get();
    }
}
