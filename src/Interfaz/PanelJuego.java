/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Modelo.ColorJugador;
import Modelo.TipoPieza;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;



/**
 *
 * @author najma
 */
/* ============================================================================
   ARCHIVO: PanelJuego.java
   HUD con botón de libro (stats), cronómetro y carga de imágenes reales
   ============================================================================ */
public class PanelJuego extends JPanel {
    private final PanelTablero tablero;
    private final Map<String, BufferedImage> imagenes = new HashMap<>();
    
    private JButton btnGirarNegro, btnGirarBlanco, btnRetirar, btnLibro;
    private JLabel lblNombreNegro, lblNombreBlanco;
    private JLabel lblIntentosNegro, lblIntentosBlanco;
    private JLabel lblEstadoNegro, lblEstadoBlanco;
    private JLabel lblCronometro;
    
    private Timer cronometro;
    private int segundos = 0;
    private BufferedImage imgLibro, imgFichaStats;

    public PanelJuego(PanelTablero tablero) {
        this.tablero = tablero;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 25));

        cargarImagenes();

        // Panel superior (Jugador Negro)
        JPanel panelNegro = crearPanelJugador(ColorJugador.NEGRO);
        add(panelNegro, BorderLayout.NORTH);

        // Centro: Tablero
        add(tablero, BorderLayout.CENTER);

        // Panel inferior (Jugador Blanco)
        JPanel panelBlanco = crearPanelJugador(ColorJugador.BLANCO);
        add(panelBlanco, BorderLayout.SOUTH);

        // Panel derecho: Cronómetro, Libro y Retirar
        JPanel panelDerecha = crearPanelDerecha();
        add(panelDerecha, BorderLayout.EAST);
        
        // Iniciar cronómetro
        iniciarCronometro();
    }

    private JPanel crearPanelJugador(ColorJugador color) {
        JPanel panel = new JPanel();
        panel.setBackground(color == ColorJugador.NEGRO ? 
            new Color(30, 30, 40) : 
            new Color(40, 40, 55));
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));

        JLabel lblNombre = new JLabel("Jugador");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 20));
        lblNombre.setForeground(color == ColorJugador.NEGRO ? 
            Color.LIGHT_GRAY : 
            new Color(220, 220, 255));
        
        JButton btnGirar = new JButton("🎰 GIRAR RULETA");
        btnGirar.setFont(new Font("Arial", Font.BOLD, 16));
        btnGirar.setBackground(new Color(60, 100, 180));
        btnGirar.setForeground(Color.WHITE);
        btnGirar.setFocusPainted(false);
        btnGirar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIntentos = new JLabel("Intentos: 1/1");
        lblIntentos.setFont(new Font("Arial", Font.BOLD, 15));
        lblIntentos.setForeground(new Color(255, 215, 0));
        
        JLabel lblEstado = new JLabel("Piezas: 6");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEstado.setForeground(new Color(150, 200, 150));

        panel.add(lblNombre);
        panel.add(btnGirar);
        panel.add(lblIntentos);
        panel.add(lblEstado);

        if (color == ColorJugador.NEGRO) {
            lblNombreNegro = lblNombre;
            btnGirarNegro = btnGirar;
            lblIntentosNegro = lblIntentos;
            lblEstadoNegro = lblEstado;
        } else {
            lblNombreBlanco = lblNombre;
            btnGirarBlanco = btnGirar;
            lblIntentosBlanco = lblIntentos;
            lblEstadoBlanco = lblEstado;
        }

        return panel;
    }

    private JPanel crearPanelDerecha() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 15, 25));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(200, 0));
        
        // Cronómetro
        lblCronometro = new JLabel("⏱ 00:00");
        lblCronometro.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblCronometro.setForeground(new Color(100, 255, 100));
        lblCronometro.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón Retirar
        btnRetirar = new JButton("RETIRAR");
        btnRetirar.setFont(new Font("Arial", Font.BOLD, 18));
        btnRetirar.setForeground(Color.WHITE);
        btnRetirar.setBackground(new Color(180, 30, 30));
        btnRetirar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRetirar.setMaximumSize(new Dimension(150, 50));
        btnRetirar.setFocusPainted(false);
        
        // Botón Libro (Stats)
        btnLibro = new JButton();
        if (imgLibro != null) {
            Image scaled = imgLibro.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            btnLibro.setIcon(new ImageIcon(scaled));
        } else {
            btnLibro.setText("📖");
            btnLibro.setFont(new Font("Arial", Font.PLAIN, 48));
        }
        btnLibro.setPreferredSize(new Dimension(100, 100));
        btnLibro.setMaximumSize(new Dimension(100, 100));
        btnLibro.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLibro.setContentAreaFilled(false);
        btnLibro.setBorderPainted(false);
        btnLibro.setFocusPainted(false);
        btnLibro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLibro.setToolTipText("Ver Estadísticas de Piezas");
        
        btnLibro.addActionListener(e -> mostrarFichaStats());
        
        panel.add(Box.createVerticalGlue());
        panel.add(lblCronometro);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(btnRetirar);
        panel.add(Box.createVerticalGlue());
        panel.add(btnLibro);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return panel;
    }

    private void cargarImagenes() {
        // Cargar imágenes de piezas
        String[] tipos = {"hl", "vampiro", "muerte", "zombie"};
        String[] colores = {"blanco", "negro"};
        
        for (String tipo : tipos) {
            for (String color : colores) {
                try {
                    // Intentar primero desde el paquete
                    String ruta = "src/Interfaz/Imagenes/" + tipo + "_" + color + ".png";
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(ruta));
                    } catch (Exception e1) {
                        // Si falla, intentar ruta relativa
                        ruta = "Interfaz/Imagenes/" + tipo + "_" + color + ".png";
                        img = ImageIO.read(new File(ruta));
                    }
                    
                    // Mapear a los nombres de TipoPieza
                    String keyTipo = tipo.equals("hl") ? "HOMBRE_LOBO" :
                                   tipo.equals("vampiro") ? "VAMPIRO" :
                                   tipo.equals("muerte") ? "NIGROMANTE" : "ZOMBIE";
                    String keyColor = color.toUpperCase();
                    
                    imagenes.put(keyTipo + "_" + keyColor, img);
                } catch (Exception e) {
                    System.err.println("No se pudo cargar imagen: " + tipo + "_" + color + ".png - " + e.getMessage());
                }
            }
        }
        
        // Cargar libro y ficha de stats
        try {
            imgLibro = ImageIO.read(new File("src/Interfaz/Imagenes/Libro.png"));
        } catch (Exception e) {
            try {
                imgLibro = ImageIO.read(new File("Interfaz/Imagenes/Libro.png"));
            } catch (Exception e2) {
                System.err.println("No se pudo cargar Libro.png: " + e2.getMessage());
            }
        }
        
        try {
            imgFichaStats = ImageIO.read(new File("src/Interfaz/Imagenes/Ficha_Stats.jpg"));
        } catch (Exception e) {
            try {
                imgFichaStats = ImageIO.read(new File("Interfaz/Imagenes/Ficha_Stats.jpg"));
            } catch (Exception e2) {
                System.err.println("No se pudo cargar Ficha_Stats.jpg: " + e2.getMessage());
            }
        }
    }

    private void mostrarFichaStats() {
        if (imgFichaStats == null) {
            JOptionPane.showMessageDialog(this, 
                "No se pudo cargar la imagen de estadísticas",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Crear diálogo centrado con la imagen
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Estadísticas de Piezas", true);
        dialog.setLayout(new BorderLayout());
        
        // Escalar imagen para que quepa bien
        int maxWidth = 800;
        int maxHeight = 600;
        Image scaled = imgFichaStats.getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
        JLabel lblImagen = new JLabel(new ImageIcon(scaled));
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        dialog.add(lblImagen, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void iniciarCronometro() {
        cronometro = new Timer(1000, e -> {
            segundos++;
            int mins = segundos / 60;
            int secs = segundos % 60;
            lblCronometro.setText(String.format("⏱ %02d:%02d", mins, secs));
        });
        cronometro.start();
    }

    public void detenerCronometro() {
        if (cronometro != null) {
            cronometro.stop();
        }
    }

    public BufferedImage imagen(TipoPieza tipo, ColorJugador color) {
        String key = tipo.name() + "_" + color.name();
        return imagenes.get(key);
    }

    public void setNombres(String nombreNegro, String nombreBlanco) {
        lblNombreNegro.setText(nombreNegro);
        lblNombreBlanco.setText(nombreBlanco);
    }

    public void onGirarNegro(ActionListener listener) {
        btnGirarNegro.addActionListener(listener);
    }

    public void onGirarBlanco(ActionListener listener) {
        btnGirarBlanco.addActionListener(listener);
    }

    public void onRetirar(ActionListener listener) {
        btnRetirar.addActionListener(listener);
    }

    public void habilitarGiro(ColorJugador turno, boolean enabled) {
        if (turno == ColorJugador.NEGRO) {
            btnGirarNegro.setEnabled(enabled);
            btnGirarBlanco.setEnabled(false);
            btnGirarNegro.setBackground(enabled ? new Color(60, 100, 180) : Color.GRAY);
        } else {
            btnGirarBlanco.setEnabled(enabled);
            btnGirarNegro.setEnabled(false);
            btnGirarBlanco.setBackground(enabled ? new Color(60, 100, 180) : Color.GRAY);
        }
    }

    public void setIntentos(ColorJugador color, int restantes, int total) {
        String texto = String.format("Intentos: %d/%d", restantes, total);
        if (color == ColorJugador.NEGRO) {
            lblIntentosNegro.setText(texto);
        } else {
            lblIntentosBlanco.setText(texto);
        }
    }

    public void setPiezasRestantes(ColorJugador color, int cantidad) {
        String texto = String.format("Piezas: %d", cantidad);
        if (color == ColorJugador.NEGRO) {
            lblEstadoNegro.setText(texto);
        } else {
            lblEstadoBlanco.setText(texto);
        }
    }
}