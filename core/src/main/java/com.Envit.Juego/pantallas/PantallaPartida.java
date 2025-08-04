package com.Envit.Juego.pantallas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


public class PantallaPartida {
    private Random random;
    
    
    // Añadir ventana para mostrar la partida
    private JFrame ventana;

    public PantallaPartida() {
        random = new Random();
    }

    // Nuevo método para mostrar la ventana de la partida
    public void mostrarVentana() {
        // Deshabilitamos completamente la funcionalidad
        /*
        ventana = new JFrame("Envit - Partida");
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setSize(1200, 750);
        ventana.setLocationRelativeTo(null);
        
        // Crear panel para mostrar las cartas
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar fondo verde
                g.setColor(new Color(0, 128, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Dibujar cartas (simulación simple)
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar 3 cartas
                int cartaWidth = 80;
                int cartaHeight = 120;
                for (int i = 0; i < 3; i++) {
                    int x = (getWidth() / 4) * (i + 1) - cartaWidth / 2;
                    int y = getHeight() / 2 - cartaHeight / 2;
                    
                    // Dibujar carta
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(x, y, cartaWidth, cartaHeight, 10, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(x, y, cartaWidth, cartaHeight, 10, 10);
                    
                    // Dibujar número aleatorio en la carta
                    g2d.setColor(Color.BLACK);
                    Font font = new Font("Arial", Font.BOLD, 20);
                    g2d.setFont(font);
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = String.valueOf(random.nextInt(12) + 1);
                    int textX = x + (cartaWidth - fm.stringWidth(text)) / 2;
                    int textY = y + (cartaHeight + fm.getAscent()) / 2 - 3;
                    g2d.drawString(text, textX, textY);
                }
                
                g2d.dispose();
            }
        };
        
        panel.setLayout(null);
        ventana.add(panel);
        
        // Botón para volver al menú
        JButton volverButton = new JButton("Volver al Menú");
        volverButton.setBounds(500, 650, 200, 40);
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.dispose();
                // Mostrar nuevamente la ventana del menú
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // En lugar de crear PantallaMenu directamente, mostramos un mensaje
                        // Esto evita el problema de dependencia circular
                        // new PantallaMenu();
                        new PantallaMenu().setVisible(true);
                    }
                });
            }
        });
        panel.add(volverButton);
        
        ventana.setVisible(true);
        */
        
        // Mostramos un mensaje indicando que esta funcionalidad está deshabilitada
        JOptionPane.showMessageDialog(null, 
            "La pantalla de partida ha sido deshabilitada", 
            "Funcionalidad no disponible", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Añadimos método main para ejecutar directamente esta pantalla
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // En lugar de mostrar la partida, mostramos un mensaje
                JOptionPane.showMessageDialog(null, 
                    "La pantalla de partida ha sido deshabilitada", 
                    "Funcionalidad no disponible", 
                    JOptionPane.INFORMATION_MESSAGE);
                // new PantallaPartida().mostrarVentana();
            }
        });
    }
}