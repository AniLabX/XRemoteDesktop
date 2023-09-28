package com.crazyxacker.apps.xremote.controls;

import com.jfoenix.assets.JFoenixResources;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXColorPicker is the metrial design implementation of color picker.
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class JFXDefaultColorPicker extends ColorPicker {

    /**
     * {@inheritDoc}
     */
    public JFXDefaultColorPicker() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXDefaultColorPicker(Color color) {
        super(color);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new ColorPickerSkin(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResources.load("css/controls/jfx-color-picker.css").toExternalForm();
    }


    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * Initialize the style class to 'jfx-color-picker'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-color-picker";

    private double[] preDefinedColors = null;

    public double[] getPreDefinedColors() {
        return preDefinedColors;
    }

    public void setPreDefinedColors(double[] preDefinedColors) {
        this.preDefinedColors = preDefinedColors;
    }

    /**
     * disable animation on button action
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_ANIMATION,
            JFXDefaultColorPicker.this,
            "disableAnimation",
            false);

    public final StyleableBooleanProperty disableAnimationProperty() {
        return this.disableAnimation;
    }

    public final Boolean isDisableAnimation() {
        return disableAnimation != null && this.disableAnimationProperty().get();
    }

    public final void setDisableAnimation(final Boolean disabled) {
        this.disableAnimationProperty().set(disabled);
    }

    private static class StyleableProperties {

        private static final CssMetaData<JFXDefaultColorPicker, Boolean> DISABLE_ANIMATION =
                new CssMetaData<>("-jfx-disable-animation", BooleanConverter.getInstance(), false) {
                    @Override
                    public boolean isSettable(JFXDefaultColorPicker control) {
                        return control.disableAnimation == null || !control.disableAnimation.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(JFXDefaultColorPicker control) {
                        return control.disableAnimationProperty();
                    }
                };


        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(ColorPicker.getClassCssMetaData());
            Collections.addAll(styleables, DISABLE_ANIMATION);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }


    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }
}