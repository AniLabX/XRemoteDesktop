package xyz.xremote.controls.preferences;

import xyz.xremote.utils.FXUtils;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SwitchPreference extends HBox {
    @FXML
    private Node root;
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private MFXToggleButton toggleButton;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);
    private final StringProperty summaryOn = new SimpleStringProperty(null);
    private final StringProperty summaryOff = new SimpleStringProperty(null);

    public SwitchPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/SwitchPreference.fxml");
    }

    @FXML
    private void initialize() {
        FXUtils.createOnMouseEnterExitedBorderEffect(this);
        configureName();
        configureSummary();
        configureOnAction();
        FXUtils.setMFXToggleColors(toggleButton);
    }

    private void configureName() {
        lblName.textProperty().bind(title);
    }

    private void configureSummary() {
        setSummaryText(false);
        selectedProperty().addListener((observable, oldValue, newValue) -> setSummaryText(newValue));

        lblSummary.visibleProperty().bind(lblSummary.textProperty().isNotEmpty());
        lblSummary.managedProperty().bind(lblSummary.visibleProperty());
    }

    private void setSummaryText(boolean selected) {
        Platform.runLater(() -> {
            if (summary.isNotEmpty().get()) {
                lblSummary.textProperty().bind(summary);
            } else if (selected) {
                lblSummary.textProperty().bind(summaryOn);
            } else {
                lblSummary.textProperty().bind(summaryOff);
            }
        });
    }

    private void configureOnAction() {
        root.setOnMouseClicked(event -> toggleButton.setSelected(!toggleButton.isSelected()));
    }

    //**************************//
    //*       Properties      **//
    //**************************//
    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return SwitchPreference.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public final void setOnAction(EventHandler<ActionEvent> value) {
        onAction.set(value);
        root.setOnMouseClicked(event -> onAction.get().handle(new ActionEvent()));
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

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

    public final void setSummaryOn(String value) {
        summaryOn.set(value);
    }

    public final String getSummaryOn() {
        return summaryOn.get();
    }

    public final void setSummaryOff(String value) {
        summaryOff.set(value);
    }

    public final String getSummaryOff() {
        return summaryOff.get();
    }

    public final BooleanProperty selectedProperty() {
        return toggleButton.selectedProperty();
    }

    public final void setSelected(boolean value) {
        toggleButton.selectedProperty().set(value);
    }

    public final boolean getSelected() {
        return toggleButton.selectedProperty().get();
    }

    public final BooleanProperty switchDisableProperty() {
        return toggleButton.disableProperty();
    }

    public final void setSwitchDisabled(boolean value) {
        toggleButton.disableProperty().set(value);
    }

    public final boolean isSwitchDisabled() {
        return toggleButton.disableProperty().get();
    }
}
