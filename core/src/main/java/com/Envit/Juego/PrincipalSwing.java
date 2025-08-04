package com.Envit.Juego;

import com.Envit.Juego.pantallas.PantallaMenu;

import javax.swing.*;

public class PrincipalSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new PantallaMenu().setVisible(true);
            }
        });
    }
}