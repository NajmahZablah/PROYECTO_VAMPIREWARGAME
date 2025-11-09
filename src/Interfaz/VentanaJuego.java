/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;


/**
 *
 * @author najma
 */
public class VentanaJuego extends JFrame {

    // ===== Rutas (CLASSPATH) =====
    private static final String FONDO_TABLERO = "/Interfaz/Imagenes/tablero_fondo.png";

    private static final String HL_NEGRO      = "/Interfaz/Imagenes/hl_negro.png";
    private static final String HL_BLANCO     = "/Interfaz/Imagenes/hl_blanco.png";
    private static final String VAMPIRO_NEGRO = "/Interfaz/Imagenes/vampiro_negro.png";
    private static final String VAMPIRO_BLANCO= "/Interfaz/Imagenes/vampiro_blanco.png";
    private static final String MUERTE_NEGRO  = "/Interfaz/Imagenes/muerte_negro.png";
    private static final String MUERTE_BLANCO = "/Interfaz/Imagenes/muerte_blanco.png";
    // ============================

    // Panel que pinta el fondo
    private final PanelFondo panelFondo = new PanelFondo();

    // Tablero (6×6) sobre el fondo
    private final PanelTablero panelTablero = new PanelTablero();

    // Matriz de botones (celdas)
    private final BotonCelda[][] celdas = new BotonCelda[6][6];

    // Imágenes en memoria
    private BufferedImage imagenFondo;
    private final Map<String, BufferedImage> imagenesPiezas = new HashMap<>();

    // Orden inicial (PDF)
    private enum Tipo { HOMBRE_LOBO, VAMPIRO, MUERTE }

    public VentanaJuego() {
        super("Vampire Wargame - Tablero");
        cargarRecursos();
        configurarVentana();
        construirTablero();
        colocarPiezasIniciales();
    }

    // =================== Carga de recursos ===================

    private void cargarRecursos() {
        try {
            URL uFondo = getClass().getResource(FONDO_TABLERO);
            if (uFondo == null) throw new RuntimeException("Fondo no encontrado: " + FONDO_TABLERO);
            imagenFondo = ImageIO.read(uFondo);

            cargar("HL_N", HL_NEGRO);
            cargar("HL_B", HL_BLANCO);
            cargar("VA_N", VAMPIRO_NEGRO);
            cargar("VA_B", VAMPIRO_BLANCO);
            cargar("MU_N", MUERTE_NEGRO);
            cargar("MU_B", MUERTE_BLANCO);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar imágenes. Revisa rutas en VentanaJuego.\n" + e.getMessage(),
                    "Recursos no encontrados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargar(String clave, String ruta) throws Exception {
        URL url = getClass().getResource(ruta);
        if (url == null) throw new RuntimeException("Recurso no encontrado: " + ruta);
        imagenesPiezas.put(clave, ImageIO.read(url));
    }

    // =================== Construcción UI ===================

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        panelFondo.setLayout(new GridBagLayout());
        panelTablero.setOpaque(false);
        panelTablero.setPreferredSize(new Dimension(720, 720));

        add(panelFondo, BorderLayout.CENTER);
        panelFondo.add(panelTablero, new GridBagConstraints());

        // ⬇️ Cuando el tablero tenga dimensiones válidas, redibujamos iconos
        panelTablero.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (panelTablero.getWidth() > 0 && panelTablero.getHeight() > 0) {
                    panelFondo.setImagen(imagenFondo);
                    redibujarIconos();
                }
            }
        });

        // Ya no necesitamos redibujar en resize del frame entero
    }

    private void construirTablero() {
        for (int fila = 0; fila < 6; fila++) {
            for (int col = 0; col < 6; col++) {
                BotonCelda boton = new BotonCelda(fila, col);
                boton.setOpaque(false);
                boton.setContentAreaFilled(false);
                boton.setBorderPainted(false);
                boton.setFocusPainted(false);
                boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                celdas[fila][col] = boton;
                panelTablero.add(boton);
            }
        }
        panelFondo.setImagen(imagenFondo);

        // ⬇️ Diferir colocación: nos aseguramos de que ya haya tamaño
        SwingUtilities.invokeLater(this::colocarPiezasIniciales);
    }

    // =================== Piezas iniciales ===================

    private void colocarPiezasIniciales() {
        // Limpia íconos
        for (BotonCelda[] fila : celdas) for (BotonCelda b : fila) b.setIcon(null);

        // Fila superior (NEGRAS)
        Tipo[] orden = {
                Tipo.HOMBRE_LOBO, Tipo.VAMPIRO, Tipo.MUERTE,
                Tipo.MUERTE, Tipo.VAMPIRO, Tipo.HOMBRE_LOBO
        };
        for (int col = 0; col < 6; col++) {
            ponerIcono(0, col, iconoDe(orden[col], true)); // negras arriba
        }
        // Fila inferior (BLANCAS)
        for (int col = 0; col < 6; col++) {
            ponerIcono(5, col, iconoDe(orden[col], false)); // blancas abajo
        }
    }

    private ImageIcon iconoDe(Tipo tipo, boolean negro) {
        String clave;
        switch (tipo) {
            case HOMBRE_LOBO: clave = negro ? "HL_N" : "HL_B"; break;
            case VAMPIRO:     clave = negro ? "VA_N" : "VA_B"; break;
            case MUERTE:      clave = negro ? "MU_N" : "MU_B"; break;
            default:          clave = negro ? "VA_N" : "VA_B";
        }
        BufferedImage img = imagenesPiezas.get(clave);
        return escalarA(img, tamanoCelda());
    }

    private Dimension tamanoCelda() {
        int w = panelTablero.getWidth();
        int h = panelTablero.getHeight();
        if (w <= 0 || h <= 0) { // usar preferido como fallback
            Dimension pref = panelTablero.getPreferredSize();
            w = (w <= 0) ? pref.width  : w;
            h = (h <= 0) ? pref.height : h;
        }
        int lado = Math.min(w / 6, h / 6);
        if (lado <= 0) lado = 100; // valor seguro por si acaso
        return new Dimension(lado, lado);
    }

    private ImageIcon escalarA(BufferedImage img, Dimension d) {
        if (img == null || d == null || d.width <= 0 || d.height <= 0) return null;
        Image esc = img.getScaledInstance(d.width - 10, d.height - 10, Image.SCALE_SMOOTH);
        return new ImageIcon(esc);
    }

    private void ponerIcono(int fila, int col, ImageIcon icono) {
        if (icono == null) return;
        celdas[fila][col].setIcon(icono);
    }

    private void redibujarIconos() {
    if (panelTablero.getWidth() <= 0 || panelTablero.getHeight() <= 0) return;
    colocarPiezasIniciales();
    panelTablero.revalidate();
    panelTablero.repaint();

    }

    // =================== Celda ===================

    /** Botón de celda: guarda su posición (fácil para la lógica luego). */
    private static class BotonCelda extends JButton {
        final int fila;
        final int columna;
        BotonCelda(int fila, int columna) { this.fila = fila; this.columna = columna; }
    }

    // =================== PanelFondo ===================

    /** Panel que pinta el fondo escalado al tamaño del frame. */
    private static class PanelFondo extends JPanel {
        private BufferedImage imagen;
        void setImagen(BufferedImage imagen) { this.imagen = imagen; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
            }
        }
    }

    // =================== main de prueba ===================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaJuego v = new VentanaJuego();
            v.setVisible(true);
        });
    }
}