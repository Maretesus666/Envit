package com.Envit.Juego;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Envit");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        new Lwjgl3Application(new Principal(), config);
    }
}
