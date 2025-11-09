/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author najma
 */
public class VentanaJuego extends JFrame {

    // ==== Rutas en classpath ====
    private static final String FONDO_TABLERO   = "/Interfaz/Imagenes/tablero_fondo.png";
    private static final String HL_NEGRO        = "/Interfaz/Imagenes/hl_negro.png";
    private static final String HL_BLANCO       = "/Interfaz/Imagenes/hl_blanco.png";
    private static final String VAMPIRO_NEGRO   = "/Interfaz/Imagenes/vampiro_negro.png";
    private static final String VAMPIRO_BLANCO  = "/Interfaz/Imagenes/vampiro_blanco.png";
    private static final String MUERTE_NEGRO    = "/Interfaz/Imagenes/muerte_negro.png";
    private static final String MUERTE_BLANCO   = "/Interfaz/Imagenes/muerte_blanco.png";
    // ============================

    // Fondo
    private final PanelFondo panelFondo = new PanelFondo();

    // Contenido central en capas: tablero pintado + celdas (botones)
    private final JLayeredPane capas = new JLayeredPane() {
        @Override public Dimension getPreferredSize() {    // 👈 clave
            return new Dimension(900, 600);
        }
    };
    private final PanelTablero panelTablero = new PanelTablero();
    private final JButton[][] celdas = new JButton[6][6];

    // HUD (tu PanelJuego)
    private PanelJuego panelHUD;

    // Recursos
    private BufferedImage imgFondo;
    private final Map<String, Image> imgs = new HashMap<>();

    private enum Tipo { HOMBRE_LOBO, VAMPIRO, MUERTE }

    public VentanaJuego() {
        super("Vampire Wargame - Tablero");
        cargarRecursos();
        construirUI();
        construirCeldas();
        // Primer layout y primer pintado cuando ya hay tamaños:
        SwingUtilities.invokeLater(() -> {
            relayoutCapas();      // coloca tablero y celdas
            colocarPiezasIniciales();
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    // ---------- recursos ----------
    private void cargarRecursos() {
        try {
            URL u = getClass().getResource(FONDO_TABLERO);
            if (u != null) imgFondo = ImageIO.read(u);
            cargar("HL_N", HL_NEGRO);
            cargar("HL_B", HL_BLANCO);
            cargar("VA_N", VAMPIRO_NEGRO);
            cargar("VA_B", VAMPIRO_BLANCO);
            cargar("MU_N", MUERTE_NEGRO);
            cargar("MU_B", MUERTE_BLANCO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando imágenes: " + e.getMessage());
        }
    }
    private void cargar(String k, String ruta) throws Exception {
        URL u = getClass().getResource(ruta);
        if (u == null) throw new RuntimeException("Falta: " + ruta);
        imgs.put(k, ImageIO.read(u));
    }

    // ---------- UI ----------
    private void construirUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Fondo a pantalla completa
        panelFondo.setLayout(new GridBagLayout());
        panelFondo.setImagen(imgFondo);
        add(panelFondo, BorderLayout.CENTER);

        // Config capas (NO usamos setBounds aquí; BorderLayout lo gestiona)
        capas.setOpaque(false);
        capas.setLayout(null);              // control absoluto adentro
        capas.add(panelTablero, JLayeredPane.DEFAULT_LAYER);

        // Cuando el propio 'capas' cambie de tamaño, ajustamos tablero/celdas
        capas.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                relayoutCapas();
                colocarPiezasIniciales();   // reescala piezas al nuevo tamaño
            }
        });

        // HUD recibe el tablero (capas) como CENTER
        panelHUD = new PanelJuego(capas, "Jugador (Negras)", "Rival (Blancas)");
        panelFondo.add(panelHUD, new GridBagConstraints());
    }

    private void relayoutCapas() {
        int W = capas.getWidth();
        int H = capas.getHeight();
        if (W <= 0 || H <= 0) { // primer layout
            Dimension pref = capas.getPreferredSize();
            W = (W <= 0) ? pref.width  : W;
            H = (H <= 0) ? pref.height : H;
        }

        panelTablero.setBounds(0, 0, W, H);

        int cw = Math.max(1, W / 6);
        int ch = Math.max(1, H / 6);
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                JButton b = celdas[r][c];
                if (b != null) b.setBounds(c * cw, r * ch, cw, ch);
            }
        }
        capas.revalidate();
        capas.repaint();
    }


    private void construirCeldas() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                JButton b = new JButton();
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                celdas[r][c] = b;
                capas.add(b, JLayeredPane.PALETTE_LAYER);
            }
        }
    }

    // ---------- piezas ----------
    private void colocarPiezasIniciales() {
        // limpia iconos
        for (JButton[] fila : celdas) for (JButton b : fila) b.setIcon(null);

        Tipo[] orden = { Tipo.HOMBRE_LOBO, Tipo.VAMPIRO, Tipo.MUERTE,
                         Tipo.MUERTE,      Tipo.VAMPIRO, Tipo.HOMBRE_LOBO };

        for (int col = 0; col < 6; col++) ponerIcono(0, col, iconoDe(orden[col], true));   // negras arriba
        for (int col = 0; col < 6; col++) ponerIcono(5, col, iconoDe(orden[col], false));  // blancas abajo
    }

    private ImageIcon iconoDe(Tipo t, boolean negro) {
        String k =
            t == Tipo.HOMBRE_LOBO ? (negro ? "HL_N" : "HL_B") :
            t == Tipo.VAMPIRO     ? (negro ? "VA_N" : "VA_B") :
                                    (negro ? "MU_N" : "MU_B");

        Image base = imgs.get(k);
        if (base == null) return null;

        int cw = Math.max(1, capas.getWidth()  / 6);
        int ch = Math.max(1, capas.getHeight() / 6);
        int lado = Math.max(24, Math.min(cw, ch) - 12); // padding
        Image esc = base.getScaledInstance(lado, lado, Image.SCALE_SMOOTH);
        return new ImageIcon(esc);
    }

    private void ponerIcono(int fila, int col, ImageIcon ic) {
        if (ic == null) return;
        JButton b = celdas[fila][col];
        b.setIcon(ic);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setVerticalAlignment(SwingConstants.CENTER);
    }

    // ---------- fondo ----------
    private static class PanelFondo extends JPanel {
        private BufferedImage img;
        void setImagen(BufferedImage i) { img = i; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
            }
        }
    }

    // ---------- main de prueba ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaJuego::new);
    }
}