package com.crazyxacker.apps.xremote;

import javafx.application.Application;

public class FXRunner {

    public static void main(String[] args) {
        // FX Runner that fixes fatJar launching error (JavaFX libs not found) when building it with ShadowJar
        Application.launch(FXApp.class, args);
    }
}