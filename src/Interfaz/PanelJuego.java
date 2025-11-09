/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 *
 * @author najma
 */
public class PanelJuego extends JPanel {

    // Rutas (ajusta si tus archivos se llaman distinto)
    private static final String RUTA_LIBRO = "/Interfaz/Imagenes/Libro.png";
    private static final String RUTA_STATS = "/Interfaz/Imagenes/Ficha_Stats.jpg";

    private final JLabel lblTiempo = new JLabel("00:00");
    private final JLabel lblNegro  = new JLabel("Negras");
    private final JLabel lblBlanco = new JLabel("Blancas");
    private final JButton btnLibro = new JButton();

    // Cronómetro
    private Timer timer;
    private long inicioMs;

    // Imagen stats
    private BufferedImage imgStats;

    public PanelJuego(JComponent tablero, String nombreNegras, String nombreBlancas) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Encabezado (cronómetro + nombres)
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        lblTiempo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTiempo.setForeground(Color.WHITE);
        lblTiempo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        lblNegro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNegro.setForeground(Color.WHITE);
        lblNegro.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        lblBlanco.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlanco.setForeground(Color.WHITE);
        lblBlanco.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        top.add(lblTiempo, BorderLayout.WEST);
        top.add(lblNegro,  BorderLayout.CENTER);
        top.add(lblBlanco, BorderLayout.EAST);

        // Fondo semitransparente para legibilidad
        JPanel topWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 110));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topWrap.setOpaque(false);
        topWrap.add(top, BorderLayout.CENTER);

        add(topWrap, BorderLayout.NORTH);

        // Centro: tablero (inyectado)
        add(tablero, BorderLayout.CENTER);

        // Botón del libro (abajo-derecha)
        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomRight.setOpaque(false);

        configurarBotonLibro();
        bottomRight.add(btnLibro);
        add(bottomRight, BorderLayout.SOUTH);

        setNombres(nombreNegras, nombreBlancas);
        cargarImagenStats();
        iniciarCronometro();
    }

    private void configurarBotonLibro() {
        btnLibro.setFocusPainted(false);
        btnLibro.setBorderPainted(false);
        btnLibro.setContentAreaFilled(false);
        btnLibro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLibro.setToolTipText("Ver estadísticas de piezas");

        // Icono del libro (si no existe, se deja texto 📖)
        try {
            URL u = getClass().getResource(RUTA_LIBRO);
            if (u != null) {
                Image img = ImageIO.read(u);
                btnLibro.setIcon(new ImageIcon(img.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            } else {
                btnLibro.setText("📖");
            }
        } catch (Exception e) {
            btnLibro.setText("📖");
        }

        btnLibro.addActionListener(e -> mostrarStats());
    }

    private void cargarImagenStats() {
        try {
            URL u = getClass().getResource(RUTA_STATS);
            if (u != null) {
                imgStats = ImageIO.read(u);
            }
        } catch (Exception ignored) {}
    }

    private void mostrarStats() {
        if (imgStats == null) {
            JOptionPane.showMessageDialog(this,
                    "Agrega tu imagen de stats en: " + RUTA_STATS,
                    "Imagen no encontrada", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Escalar imagen a un tamaño más pequeño (por ejemplo, 60% del tamaño original)
        int ancho = imgStats.getWidth() / 2;
        int alto  = imgStats.getHeight() / 2;
        Image imgEscalada = imgStats.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

        // Crear etiqueta centrada con la imagen
        JLabel lbl = new JLabel(new ImageIcon(imgEscalada));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear panel contenedor centrado con fondo semitransparente opcional
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0, 0, 0, 200));
        panel.add(lbl);

        // Mostrar en un diálogo centrado
        JOptionPane.showMessageDialog(
                this,
                panel,
                "Estadísticas de Piezas",
                JOptionPane.PLAIN_MESSAGE
        );
    }


    // ===== Cronómetro =====
    private void iniciarCronometro() {
        inicioMs = System.currentTimeMillis();
        timer = new Timer(1000, e -> actualizarTiempo());
        timer.start();
        actualizarTiempo();
    }

    public void detenerCronometro() { if (timer != null) timer.stop(); }

    private void actualizarTiempo() {
        long trans = (System.currentTimeMillis() - inicioMs) / 1000L;
        long min = trans / 60L;
        long seg = trans % 60L;
        lblTiempo.setText(String.format("%02d:%02d", min, seg));
    }

    // ===== API pública =====
    public void setNombres(String negras, String blancas) {
        lblNegro.setText(negras != null ? negras : "Negras");
        lblBlanco.setText(blancas != null ? blancas : "Blancas");
    }

    /** Permite enganchar comportamientos al botón del libro si lo necesitas. */
    public void setAccionLibro(ActionListener l) {
        for (var ls : btnLibro.getActionListeners()) btnLibro.removeActionListener(ls);
        configurarBotonLibro(); // vuelve a poner el default (mostrarStats)
        btnLibro.addActionListener(l);
    }
}