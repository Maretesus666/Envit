package com.Envit.Juego.pantallas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class PantallaMenu extends JFrame {
    private BufferedImage backgroundImage;
    private MenuButton playButton, optionsButton, exitButton;
    private Timer crtTimer;
    private Random random = new Random();
    private float scanlineOffset = 0;
    private float crtFlicker = 0;
    private Font font;
    private boolean crtEnabled = true;
    private boolean flickerEnabled = true;
    private boolean shakeEnabled = true; // Se agregó el punto y coma faltante

    // Variables para el modo opciones
    private boolean inOptionsMode = false;
    private JCheckBox  crtCheckBox, flickerCheckBox, shakeCheckBox;
    private MenuButton closeButton;
    private BufferedImage optionsBackgroundImage;

    public PantallaMenu() {
        loadCustomFont();
        initializeFrame();
        loadBackgroundImage();
        createButtons();
        setupCRTEffect();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Envit");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con override de paintComponent para efectos CRT
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Dibujar fondo
                if (backgroundImage != null && !inOptionsMode) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else if (inOptionsMode && optionsBackgroundImage != null) {
                    g2d.drawImage(optionsBackgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else if (inOptionsMode) {
                    // Fondo procedural para opciones
                    createProceduralOptionsBackground(g2d);
                }

                // Aplicar efecto CRT al fondo
                applyCRTEffect(g2d);

                g2d.dispose();
            }
        };

        mainPanel.setLayout(null);
        add(mainPanel);

        // Agregar botones al panel
        playButton = new MenuButton("JUGAR", 300, 200);
        optionsButton = new MenuButton("OPCIONES", 300, 280);
        exitButton = new MenuButton("SALIR", 300, 360);

        mainPanel.add(playButton);
        mainPanel.add(optionsButton);
        mainPanel.add(exitButton);

        // Agregar listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleOptions();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Crear elementos de la pantalla de opciones (inicialmente ocultos)
        createOptionsElements(mainPanel);
    }

    private void createOptionsElements(JPanel mainPanel) {
        // Título de opciones
        JLabel optionsTitle = new JLabel("Envit");
        optionsTitle.setFont(font.deriveFont(64f));
        optionsTitle.setForeground(Color.CYAN);
        optionsTitle.setBounds(500, 100, 240, 50);
        optionsTitle.setVisible(false);
        mainPanel.add(optionsTitle);

        // Checkboxes

        crtCheckBox = createCustomCheckBox("Efectos CRT", crtEnabled);
        crtCheckBox.setBounds(500, 220, 240, 40);
        crtCheckBox.setFont(font.deriveFont(24f));
        crtCheckBox.setVisible(false);
        mainPanel.add(crtCheckBox);

        flickerCheckBox = createCustomCheckBox("Destellos", flickerEnabled);
        flickerCheckBox.setBounds(500, 260, 240, 40);
        flickerCheckBox.setFont(font.deriveFont(24f));
        flickerCheckBox.setVisible(false);
        mainPanel.add(flickerCheckBox);

        shakeCheckBox = createCustomCheckBox("Temblor", shakeEnabled);
        shakeCheckBox.setBounds(500, 300, 240, 40);
        shakeCheckBox.setFont(font.deriveFont(24f));
        shakeCheckBox.setVisible(false);
        mainPanel.add(shakeCheckBox);

        // Botón de cerrar
        closeButton = new MenuButton("CERRAR", 500, 360);
        closeButton.setVisible(false);
        closeButton.addActionListener(e -> toggleOptions());
        mainPanel.add(closeButton);

        // Cargar imagen de fondo para opciones
        try {
            File imageFile = new File("assets/fondos/fondoOpciones.png");
            if (imageFile.exists()) {
                optionsBackgroundImage = ImageIO.read(imageFile);
            }
        } catch (IOException e) {
            // Si no se puede cargar, se usará el fondo procedural
        }
    }

    private void createProceduralOptionsBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Gradiente de fondo para opciones
        GradientPaint gradient1 = new GradientPaint(
                0, 0, new Color(10, 10, 30),
                width/2, height/2, new Color(40, 10, 60)
        );
        g2d.setPaint(gradient1);
        g2d.fillRect(0, 0, width, height);

        GradientPaint gradient2 = new GradientPaint(
                width, 0, new Color(60, 20, 80, 100),
                0, height, new Color(20, 40, 100, 100)
        );
        g2d.setPaint(gradient2);
        g2d.fillRect(0, 0, width, height);
    }

    private void toggleOptions() {
        inOptionsMode = !inOptionsMode;

        // Mostrar/ocultar elementos del menú principal
        playButton.setVisible(!inOptionsMode);
        optionsButton.setVisible(!inOptionsMode);
        exitButton.setVisible(!inOptionsMode);

        // Mostrar/ocultar elementos de opciones
        Component[] components = ((Container) getContentPane().getComponent(0)).getComponents();
        for (Component component : components) {
            if (component instanceof JLabel && ((JLabel) component).getText().equals("Envit")) {
                component.setVisible(inOptionsMode);
            } else if (
                    component == crtCheckBox ||
                            component == flickerCheckBox ||
                            component == shakeCheckBox ||
                            component == closeButton) {
                component.setVisible(inOptionsMode);
            }
        }

        // Actualizar estado de los checkboxes
        if (inOptionsMode) {
            crtCheckBox.setSelected(crtEnabled);
            flickerCheckBox.setSelected(flickerEnabled);
            shakeCheckBox.setSelected(shakeEnabled);
        } else {
            // Guardar preferencias cuando se sale del modo opciones
            crtEnabled = crtCheckBox.isSelected();
            flickerEnabled = flickerCheckBox.isSelected();
            shakeEnabled = shakeCheckBox.isSelected();
        }

        // Repintar la ventana
        getContentPane().getComponent(0).repaint();
    }

    private void loadCustomFont() {
        try {
            // Cargar la fuente personalizada
            File fontFile = new File("assets/fuentes/medieval.ttf");
            if (fontFile.exists()) {
                font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(18f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
            } else {
                System.out.println("No se encontró medieval.ttf, usando fuente por defecto");
                font = new Font("Dialog", Font.BOLD, 18);
            }
        } catch (Exception e) {
            System.err.println("Error cargando fuente personalizada: " + e.getMessage());
            font = new Font("Dialog", Font.BOLD, 18);
        }
    }

    private void loadBackgroundImage() {
        try {
            // Intentar cargar imagen desde archivo
            // Puedes cambiar la ruta por la de tu imagen
            File imageFile = new File("assets/fondos/fondo.png"); // o "background.png"

            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
                System.out.println("Imagen de fondo cargada: " + imageFile.getAbsolutePath());
            } else {
                // Si no existe el archivo, crear imagen procedural
                System.out.println("No se encontró fondo.png, creando fondo procedural...");
                createProceduralBackground();
            }

        } catch (IOException e) {
            System.err.println("Error cargando imagen de fondo: " + e.getMessage());
            createProceduralBackground();
        }
    }

    private void createProceduralBackground() {
        // Crear una imagen de fondo procedural más elaborada
        backgroundImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = backgroundImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradiente de fondo más complejo
        GradientPaint gradient1 = new GradientPaint(
                0, 0, new Color(10, 10, 30),
                400, 300, new Color(40, 10, 60)
        );
        g2d.setPaint(gradient1);
        g2d.fillRect(0, 0, 800, 600);

        GradientPaint gradient2 = new GradientPaint(
                800, 0, new Color(60, 20, 80, 100),
                0, 600, new Color(20, 40, 100, 100)
        );
        g2d.setPaint(gradient2);
        g2d.fillRect(0, 0, 800, 600);

        // Agregar elementos gráficos retro
        g2d.setColor(new Color(0, 255, 255, 30));
        for (int i = 0; i < 15; i++) {
            int x = random.nextInt(800);
            int y = random.nextInt(600);
            int size = random.nextInt(150) + 50;
            g2d.drawOval(x, y, size, size);
        }

        // Líneas de circuito
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 0, 255, 40));
        for (int i = 0; i < 20; i++) {
            int x1 = random.nextInt(800);
            int y1 = random.nextInt(600);
            int x2 = x1 + random.nextInt(200) - 100;
            int y2 = y1 + random.nextInt(200) - 100;
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
    }

    private void createButtons() {
        // Los botones ya se crean en initializeFrame()
    }

    private void setupCRTEffect() {
        crtTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Solo actualizar efectos si están habilitados
                if (crtEnabled) {
                    scanlineOffset += 0.5f;
                    if (scanlineOffset > 4) scanlineOffset = 0;
                }

                // Solo actualizar flicker si está habilitado
                if (flickerEnabled) {
                    crtFlicker += 0.1f;
                    if (crtFlicker > Math.PI * 2) crtFlicker = 0;
                }

                repaint();

                // Repintar botones para aplicar efecto CRT
                playButton.repaint();
                optionsButton.repaint();
                exitButton.repaint();

                // Repintar elementos de opciones si están visibles
                if (inOptionsMode) {
                    crtCheckBox.repaint();
                    flickerCheckBox.repaint();
                    shakeCheckBox.repaint();
                    closeButton.repaint();
                }
            }
        });
        crtTimer.start();
    }

    private void applyCRTEffect(Graphics2D g2d) {
        // Solo aplicar efectos si están habilitados
        if (!crtEnabled) return;

        int width = getWidth();
        int height = getHeight();

        // Efecto de líneas de escaneo
        g2d.setColor(new Color(0, 0, 0, 40));
        for (int y = (int)scanlineOffset; y < height; y += 3) {
            g2d.drawLine(0, y, width, y);
        }

        // Líneas de escaneo verticales ocasionales
        if (random.nextInt(50) < 2) {
            g2d.setColor(new Color(0, 0, 0, 20));
            for (int x = 0; x < width; x += 2) {
                g2d.drawLine(x, 0, x, height);
            }
        }


        // Efecto de brillo/resplandor con flicker (solo si está habilitado)
        if (flickerEnabled) {
            float flickerIntensity = (float)(Math.sin(crtFlicker) * 0.5 + 0.5);
            if (random.nextInt(100) < 8) {
                g2d.setColor(new Color(255, 255, 255, (int)(15 * flickerIntensity)));
                g2d.fillRect(0, 0, width, height);
            }
        }

        // Ruido estático
        if (random.nextInt(150) < 3) {
            for (int i = 0; i < 80; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int intensity = random.nextInt(150);
                g2d.setColor(new Color(intensity, intensity, intensity, random.nextInt(100)));
                g2d.fillRect(x, y, 2, 2);
            }
        }

        // Distorsión horizontal ocasional (solo si está habilitado)
        if (shakeEnabled && random.nextInt(300) < 2) {
            int distortY = random.nextInt(height);
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.fillRect(0, distortY, width, 3);
        }
    }

    // Método público para aplicar CRT a los botones
    public void applyCRTToButton(Graphics2D g2d, int width, int height) {
        // Solo aplicar efectos si están habilitados
        if (!crtEnabled) return;

        // Líneas de escaneo para botones
        g2d.setColor(new Color(0, 0, 0, 60));
        for (int y = (int)scanlineOffset; y < height; y += 3) {
            g2d.drawLine(0, y, width, y);
        }

        // Efecto de flicker sutil (solo si está habilitado)
        if (flickerEnabled) {
            float flickerIntensity = (float)(Math.sin(crtFlicker * 2) * 0.3 + 0.7);
            if (random.nextInt(200) < 3) {
                g2d.setColor(new Color(255, 255, 255, (int)(20 * flickerIntensity)));
                g2d.fillRect(0, 0, width, height);
            }
        }

        // Ruido en botones (menos intenso)
        if (random.nextInt(400) < 2) {
            for (int i = 0; i < 5; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                g2d.setColor(new Color(255, 255, 255, random.nextInt(80)));
                g2d.fillRect(x, y, 1, 1);
            }
        }
    }

    private void startGame() {
        // Crear y mostrar la pantalla de partida
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Ocultar la ventana actual
                setVisible(false);

                // En lugar de crear PantallaPartida, simplemente ocultamos esta ventana
                // y mostramos un mensaje
                JOptionPane.showMessageDialog(null,
                        "Funcionalidad de partida deshabilitada",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);

                // Mostramos nuevamente el menú principal
                setVisible(true);
            }
        });
    }


    // Método para crear checkboxes personalizados con cuadros negros
    private JCheckBox createCustomCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar cuadro negro
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, 16, 16);

                // Dibujar borde
                g2d.setColor(Color.CYAN);
                g2d.drawRect(0, 0, 15, 15);

                // Dibujar marca si está seleccionado
                if (isSelected()) {
                    g2d.setColor(Color.CYAN);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(4, 8, 7, 11);
                    g2d.drawLine(7, 11, 12, 5);
                }

                g2d.dispose();

                // Dibujar texto
                FontMetrics fm = g.getFontMetrics();
                g.setColor(Color.CYAN);
                g.drawString(getText(), 20, fm.getAscent() + fm.getLeading() - 2);
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                return new Dimension(20 + fm.stringWidth(getText()), Math.max(16, fm.getHeight()));
            }
        };

        checkBox.setFont(font.deriveFont(14f));
        checkBox.setOpaque(false);
        checkBox.setForeground(Color.CYAN);
        checkBox.setFocusPainted(false);

        // Añadir listener para actualizar las preferencias en tiempo real
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Actualizar preferencias inmediatamente cuando se cambia una opción
                if (checkBox == crtCheckBox) {
                    crtEnabled = checkBox.isSelected();
                } else if (checkBox == flickerCheckBox) {
                    flickerEnabled = checkBox.isSelected();
                } else if (checkBox == shakeCheckBox) {
                    shakeEnabled = checkBox.isSelected();
                }
            }
        });

        return checkBox;
    }

    // Método auxiliar para establecer la fuente recursivamente en un componente
    private void setFontRecursively(Container container, Font font) {
        for (Component component : container.getComponents()) {
            component.setFont(font);
            if (component instanceof Container) {
                setFontRecursively((Container) component, font);
            }
        }
    }

    // Clase interna para botones personalizados con efectos hover y CRT
    private class MenuButton extends JButton {
        private boolean isHovered = false;
        private float glowIntensity = 0.0f;
        private Timer glowTimer;

        public MenuButton(String text, int x, int y) {
            super(text);
            setBounds(x, y, 200, 50);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            // Usar la fuente personalizada
            setFont(font.deriveFont(Font.BOLD, 18f));
            setForeground(Color.CYAN);

            // Timer para animación de brillo
            glowTimer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isHovered && glowIntensity < 1.0f) {
                        glowIntensity += 0.1f;
                    } else if (!isHovered && glowIntensity > 0.0f) {
                        glowIntensity -= 0.1f;
                    }
                    repaint();
                }
            });
            glowTimer.start();

            // Listeners para efectos hover
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    setForeground(Color.WHITE);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    setForeground(Color.CYAN);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo del botón con efecto de brillo
            if (glowIntensity > 0) {
                // Efecto de resplandor
                int glowSize = (int)(25 * glowIntensity);

                for (int i = glowSize; i > 0; i--) {
                    int alpha = (int)(40 * glowIntensity / (i + 1));
                    g2d.setColor(new Color(0, 255, 255, alpha));
                    g2d.fillRoundRect(-i, -i, getWidth() + 2*i, getHeight() + 2*i, 15, 15);
                }
            }

            // Fondo del botón
            Color bgColor = isHovered ?
                    new Color(0, 120, 120, (int)(180 + 50 * glowIntensity)) :
                    new Color(0, 60, 60, 120);
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            // Borde del botón
            g2d.setStroke(new BasicStroke(2));
            Color borderColor = isHovered ? Color.WHITE : Color.CYAN;
            g2d.setColor(borderColor);
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);

            // APLICAR EFECTO CRT AL BOTÓN
            applyCRTToButton(g2d, getWidth(), getHeight());

            // Texto del botón
            FontMetrics fm = g2d.getFontMetrics(getFont());
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent()) / 2 - 2;

            // Efecto de sombra en el texto
            g2d.setColor(Color.BLACK);
            g2d.setFont(getFont());
            g2d.drawString(getText(), textX + 2, textY + 2);

            // Texto principal con efecto CRT
            Color textColor = getForeground();
            // Aplicar ligero flicker al texto (solo si está habilitado)
            float textFlicker = 1.0f;
            if (flickerEnabled) {
                textFlicker = (float)(Math.sin(crtFlicker * 3) * 0.1 + 0.9);
            }
            int red = (int)(textColor.getRed() * textFlicker);
            int green = (int)(textColor.getGreen() * textFlicker);
            int blue = (int)(textColor.getBlue() * textFlicker);
            g2d.setColor(new Color(red, green, blue));
            g2d.drawString(getText(), textX, textY);

            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    // Establecer la fuente por defecto para todos los componentes Swing
                    UIManager.put("Button.font", new Font("Dialog", Font.BOLD, 18));
                    UIManager.put("OptionPane.font", new Font("Dialog", Font.PLAIN, 14));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // Mostrar instrucciones para la imagen de fondo
                System.out.println("=== INSTRUCCIONES PARA LA IMAGEN DE FONDO ===");
                System.out.println("Coloque su imagen de fondo en la carpeta del proyecto con el nombre:");
                System.out.println("- fondo.png (para PNG)");
                System.out.println("Si no encuentra la imagen, usará un fondo procedural.");
                System.out.println("===============================================");

                new PantallaMenu();
            }
        });
    }
}