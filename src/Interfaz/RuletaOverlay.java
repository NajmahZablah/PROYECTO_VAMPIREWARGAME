/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Modelo.ColorJugador;
import Modelo.TipoPieza;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 *
 * @author najma
 */
public class RuletaOverlay extends JDialog {

    public interface Listener {
        void onElegido(TipoPieza tipo);
        void onCancel();
    }

    private final Listener listener;
    private Timer timer;
    private double anguloActual = 0;
    private double velocidadAngular = 20;
    private boolean girando = false;
    private JPanel panelRueda;

    private static final TipoPieza[] SECTORES = {
        TipoPieza.HOMBRE_LOBO, TipoPieza.VAMPIRO, TipoPieza.NIGROMANTE,
        TipoPieza.HOMBRE_LOBO, TipoPieza.VAMPIRO, TipoPieza.NIGROMANTE
    };

    public RuletaOverlay(Window owner, ColorJugador turno, Listener listener) {
        super(owner, "🎰 Ruleta - Presiona STOP", ModalityType.APPLICATION_MODAL);
        this.listener = listener;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(550, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 15));
        getContentPane().setBackground(new Color(15, 15, 25));

        panelRueda = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int r = Math.min(w, h) - 60;
                int cx = w / 2, cy = h / 2;

                AffineTransform original = g2.getTransform();
                g2.rotate(Math.toRadians(anguloActual), cx, cy);

                int x = cx - r / 2, y = cy - r / 2;

                g2.setColor(new Color(25, 25, 40));
                g2.fillOval(x, y, r, r);
                
                g2.setStroke(new BasicStroke(6f));
                g2.setColor(new Color(150, 150, 200));
                g2.drawOval(x, y, r, r);

                double paso = 360.0 / SECTORES.length;
                double ang = -90;

                for (int i = 0; i < SECTORES.length; i++) {
                    Color color1 = new Color(50, 70, 140, 220);
                    Color color2 = new Color(90, 110, 180, 220);
                    g2.setColor(i % 2 == 0 ? color1 : color2);
                    g2.fillArc(x, y, r, r, (int) ang, (int) paso);
                    
                    g2.setColor(new Color(200, 200, 240, 120));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawArc(x, y, r, r, (int) ang, (int) paso);

                    String txt = switch (SECTORES[i]) {
                        case HOMBRE_LOBO -> "HL";
                        case VAMPIRO -> "VA";
                        case NIGROMANTE -> "NI";
                        default -> "?";
                    };
                    
                    g2.setFont(new Font("Arial", Font.BOLD, 36));
                    FontMetrics fm = g2.getFontMetrics();
                    double rad = Math.toRadians(ang + paso / 2);
                    int tx = (int) (cx + (r * 0.32) * Math.cos(rad)) - fm.stringWidth(txt) / 2;
                    int ty = (int) (cy + (r * 0.32) * Math.sin(rad)) + fm.getAscent() / 2;
                    
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.drawString(txt, tx + 3, ty + 3);
                    
                    g2.setColor(Color.WHITE);
                    g2.drawString(txt, tx, ty);

                    ang += paso;
                }

                g2.setTransform(original);

                int px = cx, py = cy - r/2 - 25;
                Polygon flecha = new Polygon();
                flecha.addPoint(px, py);
                flecha.addPoint(px - 20, py + 15);
                flecha.addPoint(px + 20, py + 15);
                
                g2.setColor(new Color(255, 60, 60));
                g2.fill(flecha);
                g2.setColor(new Color(200, 0, 0));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(flecha);

                g2.dispose();
            }
        };

        panelRueda.setOpaque(false);
        panelRueda.setPreferredSize(new Dimension(500, 500));
        add(panelRueda, BorderLayout.CENTER);

        JButton btnStop = new JButton("⏸ STOP");
        btnStop.setFont(new Font("Arial", Font.BOLD, 32));
        btnStop.setBackground(new Color(220, 50, 50));
        btnStop.setForeground(Color.WHITE);
        btnStop.setPreferredSize(new Dimension(0, 80));
        btnStop.setFocusPainted(false);
        btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStop.addActionListener(e -> detenerRuleta());
        
        JPanel panelBoton = new JPanel(new BorderLayout());
        panelBoton.setBackground(new Color(15, 15, 25));
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        panelBoton.add(btnStop, BorderLayout.CENTER);
        
        add(panelBoton, BorderLayout.SOUTH);

        timer = new Timer(16, e -> {
            anguloActual += velocidadAngular;
            if (anguloActual >= 360) anguloActual -= 360;
            panelRueda.repaint();
        });
        
        iniciarGiro();
    }

    private void iniciarGiro() {
        girando = true;
        velocidadAngular = 20;
        timer.start();
    }

    private void detenerRuleta() {
        if (!girando) return;
        
        girando = false;

        Timer desaceleracion = new Timer(30, null);
        
        desaceleracion.addActionListener(e -> {
            if (velocidadAngular > 0.5) {
                velocidadAngular *= 0.92;
                anguloActual += velocidadAngular;
                if (anguloActual >= 360) anguloActual -= 360;
                panelRueda.repaint();
            } else {
                desaceleracion.stop();
                timer.stop();
                
                double paso = 360.0 / SECTORES.length;
                double anguloNormalizado = (360 - anguloActual) % 360;
                int idx = (int) (anguloNormalizado / paso) % SECTORES.length;
                TipoPieza elegido = SECTORES[idx];
                
                Timer delay = new Timer(500, ev -> {
                    listener.onElegido(elegido);
                    dispose();
                });
                delay.setRepeats(false);
                delay.start();
            }
        });
        
        desaceleracion.start();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            iniciarGiro();
        }
        super.setVisible(visible);
    }
}