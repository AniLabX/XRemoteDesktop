package com.crazyxacker.apps.xremote.controller;

import com.crazyxacker.apps.xremote.controls.preferences.*;
import com.crazyxacker.apps.xremote.layout.InputGroup;
import com.crazyxacker.apps.xremote.managers.LocaleManager;
import com.crazyxacker.apps.xremote.managers.Settings;
import com.crazyxacker.apps.xremote.managers.UpdatesChecker;
import com.crazyxacker.apps.xremote.managers.XRemoteManager;
import com.crazyxacker.apps.xremote.models.data.Subtitles;
import com.crazyxacker.apps.xremote.player.MPVPlayer;
import com.crazyxacker.apps.xremote.remote.XRemoteServer;
import com.crazyxacker.apps.xremote.utils.FXUtils;
import com.crazyxacker.apps.xremote.utils.Utils;
import com.jfoenix.controls.JFXButton;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.Optional;
import java.util.function.Consumer;

public class MainController {
    private static MainController INSTANCE;

    private static final String DEFAULT_BUTTON_STYLE = "-fx-background-radius: 60; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: ";
    private static final String STARTED_COLOR = "#c0392b";
    private static final String STOPPED_COLOR = "#0a84ff";

    private static final String STARTED_ICON = "/images/round_stop_circle_48.png";
    private static final String STOPPED_ICON = "/images/round_play_circle_48.png";

    @FXML
    private StackPane root;

    // API Version
    @FXML
    private Label lblAPIVersion;

    // Port
    @FXML
    private InputGroup igPort;
    @FXML
    private TextField tfPort;
    @FXML
    private MFXToggleButton tbAutostart;

    // Server info
    @FXML
    private StackPane spServerInfo;
    @FXML
    private InputGroup igServerInfo;
    @FXML
    private ImageView ivQRServerIp;
    @FXML
    private TextField tfServerIp;

    // Start/Stop button
    @FXML
    private JFXButton btnStartStop;
    @FXML
    private ImageView ivStartStop;

    // Logs
    @FXML
    private StackPane spLogs;
    @FXML
    private TextArea taLog;

    // Settings
    @FXML
    private VBox vbSettings;

    // App Language
    @FXML
    private SegmentedButtonPreference sbpAppLanguage;

    // MPV
    @FXML
    private ButtonPreference bpMPVPlayer;

    // Player
    @FXML
    private SwitchPreference swLaunchInFullscreen;
    @FXML
    private SegmentedButtonPreference sbpLaunchOnScreen;
    @FXML
    private SwitchPreference swLaunchOnTop;

    // Watching
    @FXML
    private SegmentedButtonPreference sbpSpeed;

    // Volume
    @FXML
    private SliderPreference spVolume;
    @FXML
    private SwitchPreference spMute;

    // Subtitles
    @FXML
    private SegmentedButtonPreference sbpPrioritySubtitleType;
    @FXML
    private InputPreference ipSubtitlesFontSize;
    @FXML
    private ColorPickerPreference cppFontBackColor;

    // Progress
    @FXML
    private VBox vbProgress;
    @FXML
    private Label lblProgress;
    @FXML
    private ProgressBar pbProgress;

    private final BooleanProperty serverStartedProperty = new SimpleBooleanProperty();

    private final Consumer<String> logConsumer = str -> {
        System.out.println(str);
        Platform.runLater(() -> {
            Optional.ofNullable(taLog.getText())
                    .filter(Utils::isNotEmpty)
                    .map(text -> text + "\n" + str)
                    .ifPresentOrElse(text -> taLog.setText(text), () -> taLog.setText(str));
        });
    };

    public static StackPane getRoot() {
        return INSTANCE.root;
    }

    public static void updateProgress(long count, long total, String text) {
        Platform.runLater(() -> {
            FXUtils.setNodeVisibleAndManagedFlag(count != total, INSTANCE.vbProgress);

            INSTANCE.lblProgress.setText(text);

            double progress = (double) count / total;
            INSTANCE.pbProgress.setProgress(progress);
        });
    }

    public static void stop() {
        XRemoteManager.stopServer();
    }

    @FXML
    void initialize() {
        INSTANCE = this;

        lblAPIVersion.setText("V" + XRemoteServer.API_VERSION);

        // Configure XRemote Manager
        XRemoteManager.configure(logConsumer);

        // Hide server info, logs and progress on launch
        FXUtils.setNodeGone(spServerInfo, igServerInfo, spLogs, vbProgress);

        // Configure properties (eg, change listeners)
        configureProperties();

        // Load and set port info from settings
        configurePortInput();

        // Load and set autostart info from settings
        configureAutostart();

        // Configure server info
        configureServerInfo();

        // Configure Settings
        configureSettings();

        // Change server started state
        serverStartedProperty.set(XRemoteManager.isServerRunning());

        // Download MPV Player
        MPVPlayer.download(false);

        // Request focus on root
        Platform.runLater(() -> root.requestFocus());

        // Check updates
        FXUtils.runDelayed(() -> {
            UpdatesChecker.check(getRoot(), false);
        }, 1500);
    }

    private void configurePortInput() {
        tfPort.setText(String.valueOf(Settings.XRemote.getXRemotePort()));
        tfPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfPort.setText(newValue.replaceAll("\\D", ""));
            }
            Settings.XRemote.putXRemotePort(Utils.getIntDef(tfPort.getText(), Settings.XRemote.DEFAULT_XREMOTE_PORT));
        });
    }

    private void configureAutostart() {
        FXUtils.setMFXToggleColors(tbAutostart);
        tbAutostart.setSelected(Settings.XRemote.isStartXRemoteOnLaunch());
        tbAutostart.selectedProperty().addListener((observable, oldValue, newValue) -> Settings.XRemote.putStartXRemoteOnLaunch(newValue));
    }

    private void configureProperties() {
        serverStartedProperty.addListener((o, wasStarted, started) -> {
            ivStartStop.setImage(new Image(started ? STARTED_ICON : STOPPED_ICON));
            btnStartStop.setStyle(DEFAULT_BUTTON_STYLE + (started ? STARTED_COLOR : STOPPED_COLOR));

            FXUtils.setNodeVisibleAndManagedFlag(!started, igPort, tbAutostart, vbSettings);
            FXUtils.setNodeVisibleAndManagedFlag(started, spServerInfo, igServerInfo, spLogs);

            fillServerInfo();
        });
    }

    private void configureServerInfo() {
        FXUtils.clipRoundedCorners(ivQRServerIp, 250, 250, 22, 22);
    }

    private void fillServerInfo() {
        Optional.ofNullable(XRemoteManager.getServerIp())
                .ifPresent(ip -> {
                    tfServerIp.setText(ip);
                    ivQRServerIp.setImage(null);
                    ivQRServerIp.setImage(new Image(Utils.createQRCodeUrl(ip), true));
                });
    }

    @FXML
    private void startStopServer() {
        if (XRemoteManager.isServerRunning()) {
            XRemoteManager.stopServer();
        } else {
            XRemoteManager.startServer(Settings.XRemote.getXRemotePort());
        }

        serverStartedProperty.set(XRemoteManager.isServerRunning());
    }

    @FXML
    private void resetPort() {
        tfPort.setText(String.valueOf(Settings.XRemote.DEFAULT_XREMOTE_PORT));
    }

    @FXML
    private void copyServerIp() {
        FXUtils.copyToClipboard(tfServerIp.getText());
    }

    @FXML
    private void copyLogs() {
        FXUtils.copyToClipboard(taLog.getText());
    }

    @FXML
    private void clearLogs() {
        taLog.clear();
    }

    // Settings
    private void configureSettings() {
        MPVPlayer.mpvVersionProperty().addListener((o, oldVersion, version) -> Platform.runLater(() -> configureMPV(version)));
        configureAppLanguageSection();
        configureMPV(MPVPlayer.getMPVVersion());
        configurePlayerSection();
        configureWatchingSection();
        configureAudioSection();
        configurePrioritySubtitleType();
        configureSubtitlesSection();
    }

    private void configureAppLanguageSection() {
        String defaultAppLanguageCode = Utils.getSystemLanguageCode();
        Consumer<String> toggleIdConsumer = Settings::putDefaultAppLanguageCode;
        sbpAppLanguage.addButton(LocaleManager.getString("gui.settings.language.en"), "en", defaultAppLanguageCode.equals("en"), toggleIdConsumer, true);
        sbpAppLanguage.addButton(LocaleManager.getString("gui.settings.language.ru"), "ru", defaultAppLanguageCode.equals("ru"), toggleIdConsumer, true);
        sbpAppLanguage.addButton(LocaleManager.getString("gui.settings.language.uk"), "uk", defaultAppLanguageCode.equals("uk"), toggleIdConsumer, true);
    }

    private void configureMPV(String mpvVersion) {
        bpMPVPlayer.setButtonText(LocaleManager.getString("gui.download"));
        bpMPVPlayer.setSummary(LocaleManager.getStringFormatted("gui.version", mpvVersion));
        bpMPVPlayer.setVisibleAndManagedButton(mpvVersion == null);

        bpMPVPlayer.setOnAction(event -> MPVPlayer.download(true));
    }

    private void configurePlayerSection() {
        swLaunchInFullscreen.setSelected(Settings.Player.isLaunchInFullscreen());
        swLaunchInFullscreen.selectedProperty().addListener((observable, oldValue, newValue) -> Settings.Player.putLaunchInFullscreen(newValue));

        String screenPosition = Settings.Player.getScreenPosition();
        Consumer<String> toggleIdConsumer = Settings.Player::putScreenPosition;
        for (int i = 0; i < Screen.getScreens().size(); i++) {
            String position = String.valueOf(i);
            sbpLaunchOnScreen.addButton(position, position, position.equals(screenPosition), toggleIdConsumer);
        }
        FXUtils.setNodeVisibleAndManagedFlag(Screen.getScreens().size() > 1, sbpLaunchOnScreen);

        swLaunchOnTop.setSelected(Settings.Player.isLaunchOnTop());
        swLaunchOnTop.selectedProperty().addListener((observable, oldValue, newValue) -> Settings.Player.putLaunchOnTop(newValue));
    }

    private void configureWatchingSection() {
        String speed = Settings.Player.getSpeed();
        Consumer<String> toggleIdConsumer = Settings.Player::putSpeed;
        sbpSpeed.addButton("x0.25", "0.25", speed.equals("0.4"), toggleIdConsumer);
        sbpSpeed.addButton("x0.5", "0.5", speed.equals("0.5"), toggleIdConsumer);
        sbpSpeed.addButton("x0.75", "0.75", speed.equals("0.75"), toggleIdConsumer);
        sbpSpeed.addButton("x0.9", "0.9", speed.equals("0.9"), toggleIdConsumer);
        sbpSpeed.addButton("x1.0", "1", speed.equals("1"), toggleIdConsumer);
        sbpSpeed.addButton("x1.1", "1.1", speed.equals("1.1"), toggleIdConsumer);
        sbpSpeed.addButton("x1.25", "1.25", speed.equals("1.25"), toggleIdConsumer);
        sbpSpeed.addButton("x1.4", "1.4", speed.equals("1.4"), toggleIdConsumer);
        sbpSpeed.addButton("x1.5", "1.5", speed.equals("1.5"), toggleIdConsumer);
        sbpSpeed.addButton("x1.6", "1.6", speed.equals("1.6"), toggleIdConsumer);
        sbpSpeed.addButton("x1.75", "1.75", speed.equals("1.75"), toggleIdConsumer);
        sbpSpeed.addButton("x1.9", "1.9", speed.equals("1.9"), toggleIdConsumer);
        sbpSpeed.addButton("x2.0", "2", speed.equals("2"), toggleIdConsumer);
    }

    private void configureAudioSection() {
        spVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.Player.putVolume(newValue.intValue());
            spVolume.setValueText(String.valueOf(newValue.intValue()));
        });
        spVolume.setValue(Settings.Player.getVolume());

        spMute.setSelected(Settings.Player.isMute());
        spMute.selectedProperty().addListener((observable, oldValue, newValue) -> Settings.Player.putMute(newValue));
    }

    private void configurePrioritySubtitleType() {
        Subtitles.SubtitleType subtitleType = Settings.Player.getPrioritySubtitleType();
        Consumer<String> toggleIdConsumer = id -> Settings.Player.putPrioritySubtitleType(Utils.valueOf(Subtitles.SubtitleType.class, id));
        sbpPrioritySubtitleType.addButton(LocaleManager.getString("gui.automatic"), Subtitles.SubtitleType.ANY.toString(), subtitleType == Subtitles.SubtitleType.ANY, toggleIdConsumer);
        sbpPrioritySubtitleType.addButton(Subtitles.SubtitleType.ASS, subtitleType == Subtitles.SubtitleType.ASS, toggleIdConsumer);
        sbpPrioritySubtitleType.addButton(Subtitles.SubtitleType.SSA, subtitleType == Subtitles.SubtitleType.SSA, toggleIdConsumer);
        sbpPrioritySubtitleType.addButton(Subtitles.SubtitleType.VTT, subtitleType == Subtitles.SubtitleType.VTT, toggleIdConsumer);
        sbpPrioritySubtitleType.addButton(Subtitles.SubtitleType.TTML, subtitleType == Subtitles.SubtitleType.TTML, toggleIdConsumer);
        sbpPrioritySubtitleType.addButton(Subtitles.SubtitleType.SRT, subtitleType == Subtitles.SubtitleType.SRT, toggleIdConsumer);
    }

    private void configureSubtitlesSection() {
        ipSubtitlesFontSize.setText(Settings.Player.getSubtitlesFontSize());
        ipSubtitlesFontSize.textProperty().addListener((observable, oldValue, newValue) -> Settings.Player.putSubtitlesFontSize(newValue));

        cppFontBackColor.setColor(Color.web(FXUtils.convertARGBToRGBAHex(Settings.Player.getSubtitlesBackColor())));
        cppFontBackColor.colorProperty().addListener((observable, oldValue, newValue) -> Settings.Player.putSubtitlesBackColor(FXUtils.toARGBHexString(newValue)));
    }
}