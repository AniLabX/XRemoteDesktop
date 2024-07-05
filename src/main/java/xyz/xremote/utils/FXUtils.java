package xyz.xremote.utils;

import xyz.xremote.FXApp;
import xyz.xremote.controller.MainController;
import xyz.xremote.controls.Snackbar;
import xyz.xremote.managers.LocaleManager;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.reactivex.annotations.Nullable;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FXUtils {
    public static final String DEFAULT_BORDER_CSS = "border-transparent";
    public static final String HOVER_BORDER_CSS = "border-colored";

    public static void loadComponent(Object rootAndController, String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(FXUtils.class.getResource(fxmlPath), LocaleManager.getResourceBundle());
        fxmlLoader.setRoot(rootAndController);
        fxmlLoader.setController(rootAndController);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void runNowOrLater(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            Platform.runLater(runnable);
        }
    }

    public static void runDelayed(Runnable runnable, long delay) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        new Timer().schedule(task, delay);
    }

    public static void runLaterDelayed(Runnable runnable, long delay) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(runnable);
            }
        };
        new Timer().schedule(task, delay);
    }

    public static void openUrl(String url) {
        FXApp.getAppHostServices().showDocument(url);
    }

    public static void copyToClipboard(String clip) {
        ClipboardContent content = new ClipboardContent();
        content.putString(clip);

        Clipboard.getSystemClipboard().setContent(content);

        Snackbar.showSnackBar(MainController.getRoot(), LocaleManager.getString("gui.copied_to_clipboard"), Snackbar.Type.SUCCESS);
    }

    public static void setNodeVisible(@Nullable Node...nodes) {
        setNodeVisibleAndManagedFlag(true, true, nodes);
    }

    public static void setNodeInvisible(@Nullable Node...nodes) {
        setNodeVisibleAndManagedFlag(false, true, nodes);
    }

    public static void setNodeUnmanaged(@Nullable Node...nodes) {
        setNodeVisibleAndManagedFlag(true, false, nodes);
    }

    public static void setNodeGone(@Nullable Node...nodes) {
        setNodeVisibleAndManagedFlag(false, false, nodes);
    }

    public static void setNodeVisibleAndManagedFlag(boolean isVisibleAndManaged, @Nullable Node...nodes) {
        setNodeVisibleAndManagedFlag(isVisibleAndManaged, isVisibleAndManaged, nodes);
    }

    public static void setNodeVisibleAndManagedFlag(boolean isVisible, boolean isManaged, @Nullable Node...nodes) {
        if (Utils.isNotEmpty(nodes)) {
            for (Node node : nodes) {
                node.setVisible(isVisible);
                node.setManaged(isManaged);
            }
        }
    }

    public static void clipRoundedCorners(Node node, double width, double height, int arcWidth, int arcHeight) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setArcWidth(arcWidth);
        rectangle.setArcHeight(arcHeight);
        node.setClip(rectangle);
    }

    public static void createOnMouseEnterExitedBorderEffect(Node... nodes) {
        createOnMouseEnterBorderEffect(nodes);
        createOnMouseExitedBorderEffect(nodes);
    }

    public static void createOnMouseEnterBorderEffect(Node... nodes) {
        createOnMouseEnterBorderEffect(DEFAULT_BORDER_CSS, HOVER_BORDER_CSS, nodes);
    }

    public static void createOnMouseExitedBorderEffect(Node... nodes) {
        createOnMouseExitedBorderEffect(DEFAULT_BORDER_CSS, HOVER_BORDER_CSS, nodes);
    }

    public static void createOnMouseEnterBorderEffect(String defaultStyleName, String hoverBorderEffect, Node... nodes) {
        for (Node node : nodes) {
            node.setOnMouseEntered(event -> {
                ObservableList<String> styleClass = node.getStyleClass();
                styleClass.remove(defaultStyleName);
                styleClass.add(hoverBorderEffect);
            });
        }
    }

    public static void createOnMouseExitedBorderEffect(String defaultStyleName, String hoverBorderEffect, Node... nodes) {
        for (Node node : nodes) {
            node.setOnMouseExited(event -> {
                ObservableList<String> styleClass = node.getStyleClass();
                styleClass.remove(hoverBorderEffect);
                styleClass.add(defaultStyleName);
            });
        }
    }

    public static void showNeedRestartSnackbar() {
        Snackbar.showSnackBar(
                MainController.getRoot(),
                LocaleManager.getString("gui.language_change_need_restart"),
                Snackbar.Type.WARNING
        );
    }

    public static void setMFXToggleColors(MFXToggleButton... toggleButtons) {
        for (MFXToggleButton toggleButton : toggleButtons) {
            toggleButton.setStyle("-mfx-main: #0a84ff; -mfx-secondary: #ffffff; -fx-text-fill: white;");
        }
    }

    public static String toARGBHexString(Color value) {
        return "#" + (format(value.getOpacity()) + format(value.getRed()) + format(value.getGreen()) + format(value.getBlue())).toUpperCase();
    }

    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public static String convertARGBToRGBAHex(String hexColor) {
        return hexColor.replaceAll("#(..)(.*)", "#$2$1");
    }
}
