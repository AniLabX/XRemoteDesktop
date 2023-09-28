package com.crazyxacker.apps.xremote.controls.preferences;

import com.crazyxacker.apps.xremote.utils.FXUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PreferenceSection extends VBox {
    @FXML
    private Label lblHeader;

    private final StringProperty header = new SimpleStringProperty(null);
    private ObjectProperty<Paint> textFill;

    public PreferenceSection() {
        FXUtils.loadComponent(this, "/fxml/preferences/PreferenceSection.fxml");
    }

    @FXML
    private void initialize() {
        lblHeader.textProperty().bind(header);
    }

    //**************************//
    //*       Properties      **//
    //**************************//
    public final Label getHeaderNode() {
        return lblHeader;
    }

    public final void setHeader(String value) {
        header.set(value);
    }

    public final String getHeader() {
        return header.get();
    }

    public final void setHeaderStyle(String value) {
        lblHeader.setStyle(value);
    }

    public final String getHeaderStyle() {
        return lblHeader.getStyle();
    }

    public final void setTextFill(Paint value) {
        textFillProperty().set(value);
    }

    public final Paint getTextFill() {
        return textFill == null ? Color.BLACK : textFill.get();
    }

    public final ObjectProperty<Paint> textFillProperty() {
        if (textFill == null) {
            textFill = new StyleableObjectProperty<>(Color.BLACK) {

                @Override
                public CssMetaData<Labeled, Paint> getCssMetaData() {
                    return null;
                }

                @Override
                public Object getBean() {
                    return PreferenceSection.this;
                }

                @Override
                public String getName() {
                    return "textFill";
                }
            };
        }
        return textFill;
    }
}
