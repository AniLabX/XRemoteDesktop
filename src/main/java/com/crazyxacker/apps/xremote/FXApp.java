package com.crazyxacker.apps.xremote;

import com.crazyxacker.apps.xremote.controller.MainController;
import com.crazyxacker.apps.xremote.helpers.JavaHelper;
import com.crazyxacker.apps.xremote.jna.DwmAttribute;
import com.crazyxacker.apps.xremote.jna.StageOps;
import com.crazyxacker.apps.xremote.managers.LocaleManager;
import com.crazyxacker.apps.xremote.managers.Workspace;
import com.crazyxacker.apps.xremote.player.MPVPlayer;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import net.sf.sevenzipjbinding.SevenZip;

import java.io.File;
import java.util.Objects;

public class FXApp extends Application {
    @Getter
    private static HostServices appHostServices;

    private static String getAppName() {
        return String.format("%s (v%s)", Constants.APP_NAME, Constants.APP_VERSION);
    }

    @Override
    public void init() {
        // Configure workspace
        Workspace.configureWorkspace();

        // Load SevenZipJBindings native libraries
        loadSevenZip();

        // Configure MPV Player
        MPVPlayer.setMPVDirectory(Workspace.BIN_DIR);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (JavaHelper.inNativeImage() && JavaHelper.Platform.isMac()) {
            // On MacOS there is some strange error with JNI/JNA libs in Native Image mode that prevents app from launching
            // Catching all Throwable instances is a workaround. App will launch as expected and rethrow any Throwable
            // that is not instance of UnsatisfiedLinkError
            safeStartInternal(primaryStage);
        } else {
            startInternal(primaryStage);
        }
    }

    private void safeStartInternal(Stage primaryStage) throws Exception {
        try {
            startInternal(primaryStage);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (!(throwable instanceof UnsatisfiedLinkError)) {
                throw throwable; // Rethrow exception
            }
        }
    }

    private void startInternal(Stage primaryStage) throws Exception {
        // Load Parent node
        Parent layout = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Main.fxml")), LocaleManager.getResourceBundle());

        // Create Scene with node
        Scene scene = new Scene(layout, 1200, 620);

        // Load CSS theme
        scene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("/css/dark.css")).toExternalForm());

        // Save HostServices instance for URL opening
        appHostServices = getHostServices();

        // Set title text and icon
        primaryStage.getIcons().add(new Image("/images/remote_256.png"));
        primaryStage.setTitle(getAppName());

        // Set min window width/height
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        // Set scene and show Window
        primaryStage.setScene(scene);
        primaryStage.show();

        // Enable dark window style and Mica material on Windows
        manipulateWithNativeWindow();
    }

    private void loadSevenZip() {
        try {
            SevenZip.initSevenZipFromPlatformJAR(SevenZip.getPlatformBestMatch(), new File(Workspace.BIN_DIR));
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Unable to load SevenZip!");
        }
    }

    private void manipulateWithNativeWindow() {
        if (JavaHelper.Platform.isWindows()) {
            // Enable Mica material and dark mode
            StageOps.WindowHandle handle = new StageOps.WindowHandle(getAppName());

            // Enable the dark mode
            StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);

            // Enable the Mica material
            if (!StageOps.dwmSetIntValue(handle, DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value)) {
                StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_MICA_EFFECT, true); // This is the "old" way
            }
        }
    }

    @Override
    public void stop() {
        // Destroy MPV Player
        MPVPlayer.destroy();

        // Stop server and save settings
        MainController.stop();

        // Explicitly terminate main thread because of running FX app from runner
        System.exit(0);
    }
}