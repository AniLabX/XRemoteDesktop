package xyz.xremote.controls.preferences;

import xyz.xremote.utils.FXUtils;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ButtonPreference extends HBox {
    @FXML
    private Label lblName;
    @FXML
    private Label lblSummary;
    @FXML
    private JFXButton button;

    private final StringProperty title = new SimpleStringProperty(null);
    private final StringProperty summary = new SimpleStringProperty(null);
    private final StringProperty buttonText = new SimpleStringProperty(null);

    public ButtonPreference() {
        FXUtils.loadComponent(this, "/fxml/preferences/ButtonPreference.fxml");
    }

    @FXML
    private void initialize() {
        FXUtils.createOnMouseEnterExitedBorderEffect(this);
        lblName.textProperty().bind(title);
        lblSummary.textProperty().bind(summary);
        lblSummary.visibleProperty().bind(lblSummary.textProperty().isNotEmpty());
        lblSummary.managedProperty().bind(lblSummary.visibleProperty());
        button.textProperty().bind(buttonText);
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
            return ButtonPreference.this;
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

    public final void setButtonText(String value) {
        buttonText.set(value);
    }

    public final String getButtonText() {
        return buttonText.get();
    }

    public final void setButtonStyle(String value) {
        button.styleProperty().set(value);
    }

    public final String getButtonStyle() {
        return button.styleProperty().get();
    }

    public void setDisableButton(boolean disable) {
        button.setDisable(disable);
    }

    public void setVisibleAndManagedButton(boolean visibleAndManaged) {
        button.setVisible(visibleAndManaged);
        button.setManaged(visibleAndManaged);
    }
}
