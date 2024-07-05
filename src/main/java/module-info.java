open module XRemote {
    // GraalVM
    requires org.graalvm.nativeimage;

    // JNA
    requires jna;
    requires jna.platform;

    // Java core
    requires java.prefs;

    // JavaFX core
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Annotations
    requires lombok;

    // JavaFX libs
    requires com.jfoenix;
    requires MaterialFX;
    requires org.controlsfx.controls;

    // Ikonli
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    // OkHttp
    requires okhttp3;
    requires okhttp3.logging;

    // JSON
    requires com.google.gson;

    // RXJava
    requires io.reactivex.rxjava2;
    requires rxjavafx;
    requires rxbonjour;
    requires rxbonjour.platform.desktop;

    // SevenZip
    requires sevenzipjbinding;

    // Apache Commons
    requires org.apache.commons.io;

    // Natural Comparator
    requires natural.comparator;

    // Exports
    exports xyz.xremote;
    exports xyz.xremote.controller to javafx.fxml;
    exports xyz.xremote.layout to javafx.fxml;
    exports xyz.xremote.controls to javafx.fxml;
    exports xyz.xremote.controls.preferences to javafx.fxml;
    exports xyz.xremote.models to com.google.gson;
}