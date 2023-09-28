package com.crazyxacker.apps.xremote.dialog;

import com.crazyxacker.apps.xremote.controls.JFXCustomDialogLayout;
import com.crazyxacker.apps.xremote.managers.LocaleManager;
import com.crazyxacker.apps.xremote.utils.FXUtils;
import com.crazyxacker.apps.xremote.utils.Triple;
import com.crazyxacker.apps.xremote.utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.reactivex.annotations.Nullable;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DialogBuilder {
    public enum DialogType {NODE, LIST, FILTERABLE_LIST, INPUT, WAIT}

    private final JFXCustomDialog dialog;

    private DialogType dialogType;
    private JFXCustomDialog.DialogTransition transition = JFXCustomDialog.DialogTransition.CENTER;
    private int minWidth = 380;
    private int minHeight = 0;
    private Region customRegion;
    private String heading;
    private boolean noBodyPadding = false;
    private boolean noButtons = false;
    private boolean closable = true;

    private MFXListView<String> list;
    private ObservableList<String> listData;
    private OnListSelectCallback onListSelectCallback;

    private MFXTextField input;
    private Label inputValidationLabel;
    private Consumer<String> inputChangeConsumer;

    private MFXProgressBar progressBar;
    private Runnable onProgressDoneRunnable;

    private final List<JFXCheckBox> checkboxes = new ArrayList<>();

    private final List<Node> bodies = new ArrayList<>();
    private final List<Triple<ButtonType, String, ButtonCallback>> actions = new ArrayList<>();

    private Runnable onDialogOpenRunnable;
    private Runnable onDialogCloseRunnable;

    private List<URL> stylesheetUrls;

    public static DialogBuilder create(StackPane container) {
        return new DialogBuilder(container);
    }

    private DialogBuilder(StackPane container) {
        this.dialog = new JFXCustomDialog();
        dialog.setDialogContainer(container);
    }

    public DialogBuilder withDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
        if (dialogType == DialogType.LIST) {
            list = new MFXListView<>();
        } else if (dialogType == DialogType.FILTERABLE_LIST) {
            list = new MFXListView<>();
            input = new MFXTextField();
            createInputValidationLabel();
        } else if (dialogType == DialogType.INPUT) {
            input = new MFXTextField();
            createInputValidationLabel();
        } else if (dialogType == DialogType.WAIT) {
            progressBar = new MFXProgressBar();
        }
        return this;
    }

    private void createInputValidationLabel() {
        inputValidationLabel = new Label();
        inputValidationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #c0392b");
        inputValidationLabel.setWrapText(true);
        FXUtils.setNodeGone(inputValidationLabel);
    }

    public DialogBuilder withTransition(JFXCustomDialog.DialogTransition transition) {
        this.transition = transition;
        return this;
    }

    public DialogBuilder withMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public DialogBuilder withMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public DialogBuilder withCustomRegion(Region customRegion) {
        this.customRegion = customRegion;
        return this;
    }

    public DialogBuilder withHeading(String heading) {
        this.heading = heading;
        return this;
    }

    public DialogBuilder withListMapData(ObservableList<String> listData) {
        this.listData = listData;
        return this;
    }

    public DialogBuilder withInputText(String text) {
        Optional.ofNullable(input).ifPresentOrElse(node -> node.setText(text), () -> System.err.println("Unable to set TextField text. TextField is null!"));
        return this;
    }

    public DialogBuilder withInputFloatingText(String floatingText) {
        Optional.ofNullable(input).ifPresentOrElse(node -> node.setFloatingText(floatingText), () -> System.err.println("Unable to set TextField Floating text. TextField is null!"));
        return this;
    }

    public DialogBuilder withInputChange(Consumer<String> inputChangeConsumer) {
        this.inputChangeConsumer = inputChangeConsumer;
        return this;
    }

    public DialogBuilder withCheckbox(String text, boolean selected, ChangeListener<Boolean> changeListener) {
        JFXCheckBox checkbox = new JFXCheckBox(text);
        checkbox.setSelected(selected);
        checkbox.selectedProperty().addListener(changeListener);
        this.checkboxes.add(checkbox);
        return this;
    }

    public DialogBuilder withOnProgressDone(Runnable onProgressDoneRunnable) {
        this.onProgressDoneRunnable = onProgressDoneRunnable;
        return this;
    }

    public DialogBuilder withBody(String body) {
        Label label = new Label(body);
        label.setStyle("-fx-font-size: 15");
        bodies.add(label);
        return this;
    }

    public DialogBuilder withBody(String... bodies) {
        for (String body : bodies) {
            Label label = new Label(body);
            label.setStyle("-fx-font-size: 15");
            this.bodies.add(label);
        }

        return this;
    }

    public DialogBuilder withBody(Node body) {
        bodies.add(body);
        return this;
    }

    public DialogBuilder withBody(Node... bodies) {
        this.bodies.addAll(List.of(bodies));
        return this;
    }

    public DialogBuilder withNoBodyPadding() {
        noBodyPadding = true;
        return this;
    }

    public DialogBuilder withNoButtons() {
        noButtons = true;
        return this;
    }

    public DialogBuilder withButton(ButtonType button, @Nullable String title, @Nullable ButtonCallback callback) {
        actions.add(new Triple<>(button, title, callback));
        return this;
    }

    public DialogBuilder withClosable(boolean closable) {
        this.closable = closable;
        return this;
    }

    public DialogBuilder withOnListSelectCallback(OnListSelectCallback onListSelectCallback) {
        this.onListSelectCallback = onListSelectCallback;
        return this;
    }

    public DialogBuilder withOnOpenRunnable(Runnable onDialogOpenRunnable) {
        this.onDialogOpenRunnable = onDialogOpenRunnable;
        return this;
    }

    public DialogBuilder withOnCloseRunnable(Runnable onDialogCloseRunnable) {
        this.onDialogCloseRunnable = onDialogCloseRunnable;
        return this;
    }

    public DialogBuilder withStylesheets(List<URL> stylesheetUrls) {
        this.stylesheetUrls = stylesheetUrls;
        return this;
    }

    public DialogBuilder build() {
        buildList();
        return this;
    }

    public JFXCustomDialog show() {
        configureDialog(dialog);
        addStylesheets(dialog);

        FXUtils.runNowOrLater(dialog::show);
        return dialog;
    }

    public void close() {
        dialog.close();
    }

    public void updateProgress(int current, int total) {
        Platform.runLater(() -> {
            progressBar.setProgress((double) current / total);
            if (current == total) {
                Utils.safeRun(onProgressDoneRunnable);
                dialog.close();
            }
        });
    }

    private void configureDialog(JFXCustomDialog dialog) {
        dialog.setTransitionType(transition);
        dialog.setContent(getContent(dialog));
        dialog.setOnDialogOpened(event -> Optional.ofNullable(onDialogOpenRunnable).ifPresent(Runnable::run));
        dialog.setOnDialogClosed(event -> Optional.ofNullable(onDialogCloseRunnable).ifPresent(Runnable::run));
        dialog.setOverlayClose(closable);
    }

    private Region getContent(JFXCustomDialog dialog) {
        if (customRegion == null) {
            JFXCustomDialogLayout layout = new JFXCustomDialogLayout(Utils.isEmpty(heading), noBodyPadding, noButtons);
            if (Utils.isNotEmpty(heading)) {
                layout.setHeading(new Label(heading));
            }

            VBox vBox = new VBox();
            vBox.setMinWidth(minWidth);
            if (minHeight > 0) {
                vBox.setMinHeight(minHeight);
            }
            vBox.setSpacing(12);

            if (Utils.isNotEmpty(bodies)) {
                vBox.getChildren().addAll(bodies);
            }

            if (dialogType == DialogType.LIST) {
                vBox.getChildren().add(list);
            } else if (dialogType == DialogType.FILTERABLE_LIST) {
                vBox.getChildren().addAll(list, input, inputValidationLabel);
            } else if (dialogType == DialogType.INPUT) {
                vBox.getChildren().addAll(input, inputValidationLabel);
                FXUtils.runLaterDelayed(input::requestFocus, 250);
            } else if (dialogType == DialogType.WAIT) {
                vBox.getChildren().add(progressBar);
            }

            if (Utils.isNotEmpty(checkboxes)) {
                vBox.getChildren().addAll(checkboxes);
            }

            vBox.getChildren()
                    .stream()
                    .filter(Region.class::isInstance)
                    .map(Region.class::cast)
                    .forEach(region -> region.minWidthProperty().bind(vBox.minWidthProperty()));

            layout.setBody(vBox);

            setActions(dialog, layout);

            return layout;
        }
        return customRegion;
    }

    private void setActions(JFXCustomDialog dialog, JFXCustomDialogLayout layout) {
        if (Utils.isNotEmpty(this.actions)) {
            List<Node> actions = new ArrayList<>();
            this.actions.forEach(triple -> {
                ButtonBar.ButtonData buttonData = triple.first().getButtonData();
                String buttonText = triple.second();
                if (Utils.isEmpty(buttonText)) {
                    buttonText = LocaleManager.getString("gui." + buttonData.toString().toLowerCase());
                }

                JFXButton action = new JFXButton(buttonText);
                action.setStyle("-fx-text-fill: white; -fx-font-size: 15; -fx-background-color: "
                        + (buttonData.isCancelButton() ? "transparent" : "#0a84ff"));

                action.setOnMouseClicked(event -> {
                    if (!buttonData.isCancelButton() && dialogType == DialogType.INPUT) {
                        Optional.ofNullable(inputChangeConsumer).ifPresent(consumer -> consumer.accept(input.getText()));
                    }
                    Optional.ofNullable(triple.third()).ifPresent(callback -> callback.onAction(dialog));

                    if (closable) {
                        dialog.close();
                    }
                });

                actions.add(action);
            });

            layout.setActions(actions);
        }
    }

    private void addStylesheets(JFXCustomDialog dialog) {
        Optional.ofNullable(stylesheetUrls).ifPresent(urls -> {
            ObservableList<String> stylesheets = dialog.getStylesheets();
            urls.stream()
                    .map(URL::toExternalForm)
                    .forEach(stylesheets::add);
        });
    }

    private void buildList() {
        if (dialogType == DialogType.LIST || dialogType == DialogType.FILTERABLE_LIST) {
            // Items
            list.setItems(listData);
            list.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
                for (Map.Entry<Integer, String> value : newValue.entrySet()) {
                    onListSelectCallback.onSelect(value.getValue(), value.getKey());
                    close();
                    break;
                }
            });

            if (dialogType == DialogType.FILTERABLE_LIST) {
                input.textProperty().addListener((observable, oldValue, newValue) -> Optional.ofNullable(inputChangeConsumer).ifPresent(consumer -> consumer.accept(newValue)));
            }
        }
    }

    public interface OnListSelectCallback {
        void onSelect(String value, int index);
    }

    public interface ButtonCallback {
        void onAction(JFXCustomDialog dialog);
    }
}
