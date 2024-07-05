package xyz.xremote.managers;

import xyz.xremote.Constants;
import xyz.xremote.controls.Snackbar;
import xyz.xremote.dialog.DialogBuilder;
import xyz.xremote.dialog.JFXCustomDialog;
import xyz.xremote.helpers.OkHttpHelper;
import xyz.xremote.models.AppUpdate;
import xyz.xremote.utils.FXUtils;
import xyz.xremote.utils.Utils;
import com.google.gson.Gson;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.NonNull;

import java.util.List;

public class UpdatesChecker {

    public static void check(@NonNull StackPane dialogRootPane, boolean showNoUpdatesNotification) {
        OkHttpHelper.GET(Constants.APP_UPDATES_CHECK_URL)
                .subscribeOn(Schedulers.newThread())
                .map(info -> new Gson().fromJson(info, AppUpdate.class))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(
                        update -> Platform.runLater(() -> showUpdateDialog(dialogRootPane, update, showNoUpdatesNotification)),
                        throwable -> {
                            throwable.printStackTrace();
                            Snackbar.showSnackBar(dialogRootPane, LocaleManager.getString("gui.unable_check_updates"), Snackbar.Type.ERROR);
                        }
                );
    }

    private static void showUpdateDialog(@NonNull StackPane dialogRootPane, AppUpdate update, boolean showNoUpdatesNotification) {
        int versionCode = Constants.APP_VERSION_CODE;
        if (update.getVersionCode() > versionCode) {
            Label versionLabel = new Label(String.format("%s (v%s)\n", Constants.APP_NAME, update.getVersionName()));
            versionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16");

            List<String> changelog;
            if (Utils.isCurrentSystemLocaleIsRussian()) {
                changelog = update.getChangelogRussian();
            } else if (Utils.isCurrentSystemLocaleIsUkrainian()) {
                changelog = update.getChangelogUkrainian();
            } else {
                changelog = update.getChangelogEnglish();
            }

            Label changelogText = new Label(Utils.join("\n", changelog));
            changelogText.setStyle("-fx-font-size: 11");

            DialogBuilder.create(dialogRootPane)
                    .withTransition(JFXCustomDialog.DialogTransition.CENTER)
                    .withHeading(LocaleManager.getString("gui.update_available"))
                    .withBody(versionLabel)
                    .withBody(changelogText)
                    .withButton(ButtonType.YES, LocaleManager.getString("gui.download"), dialog -> FXUtils.openUrl(update.getUpdateUrl()))
                    .withClosable(versionCode >= update.getMinVersionCode())
                    .show();
        } else if (showNoUpdatesNotification) {
            Snackbar.showSnackBar(dialogRootPane, LocaleManager.getString("gui.updates_latest_version_installed"), Snackbar.Type.SUCCESS);
        }
    }
}
