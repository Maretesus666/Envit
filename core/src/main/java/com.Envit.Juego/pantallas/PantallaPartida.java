package com.Envit.Juego.pantallas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class PantallaPartida {
    private Random random;
    
    // Añadir ventana para mostrar la partida
    private JFrame ventana;
    private BufferedImage barajaImage;
    private BufferedImage[][] cartaImages;

    public PantallaPartida() {
        random = new Random();
        cargarBaraja();
    }

    // Nuevo método para cargar y dividir la imagen de la baraja
    private void cargarBaraja() {
        try {
            // Cargar la imagen de la baraja
            barajaImage = ImageIO.read(getClass().getResourceAsStream("/baraja.png"));
            
            // Dividir la imagen en 12 columnas y 4 filas
            int cartaWidth = barajaImage.getWidth() / 12;
            int cartaHeight = barajaImage.getHeight() / 4;
            
            cartaImages = new BufferedImage[4][12];
            
            for (int fila = 0; fila < 4; fila++) {
                for (int columna = 0; columna < 12; columna++) {
                    cartaImages[fila][columna] = barajaImage.getSubimage(
                        columna * cartaWidth, 
                        fila * cartaHeight, 
                        cartaWidth, 
                        cartaHeight
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            cartaImages = null;
        }
    }

    // Nuevo método para mostrar la ventana de la partida
    public void mostrarVentana() {

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
                    
                    // Seleccionar un número aleatorio entre 1 y 12
                    int numeroCarta = random.nextInt(12) + 1;
                    
                    // Dibujar la carta usando la imagen recortada
                    if (cartaImages != null) {
                        // Calcular la posición en la cuadrícula (fila y columna)
                        // Para simplificar, usamos la primera fila y la columna correspondiente al número
                        int fila = 0; // Podemos variar la fila también si queremos
                        int columna = numeroCarta - 1; // 0-indexed
                        
                        // Escalar la imagen de la carta al tamaño deseado
                        Image cartaEscalada = cartaImages[fila][columna].getScaledInstance(
                            cartaWidth, cartaHeight, Image.SCALE_SMOOTH
                        );
                        
                        g2d.drawImage(cartaEscalada, x, y, null);
                    } else {
                        // Fallback si no se pudo cargar la imagen
                        g2d.setColor(Color.WHITE);
                        g2d.fillRoundRect(x, y, cartaWidth, cartaHeight, 10, 10);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRoundRect(x, y, cartaWidth, cartaHeight, 10, 10);
                        
                        // Dibujar número aleatorio en la carta
                        g2d.setColor(Color.BLACK);
                        Font font = new Font("Arial", Font.BOLD, 20);
                        g2d.setFont(font);
                        FontMetrics fm = g2d.getFontMetrics();
                        String text = String.valueOf(numeroCarta);
                        int textX = x + (cartaWidth - fm.stringWidth(text)) / 2;
                        int textY = y + (cartaHeight + fm.getAscent()) / 2 - 3;
                        g2d.drawString(text, textX, textY);
                    }
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
                        //new PantallaMenu().setVisible(true);
                    }
                });
            }
        });
        panel.add(volverButton);
        
        ventana.setVisible(true);
        
        
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
                new PantallaPartida().mostrarVentana();
            }
        });
    }
}