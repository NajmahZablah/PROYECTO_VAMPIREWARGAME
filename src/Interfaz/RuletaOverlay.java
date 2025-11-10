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
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
        super(owner, "🎰 Ruleta de la Suerte", ModalityType.APPLICATION_MODAL);
        this.listener = listener;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(650, 720);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 20));
        getContentPane().setBackground(new Color(20, 25, 40));

        crearInterfaz();
        
        timer = new Timer(16, e -> {
            anguloActual += velocidadAngular;
            if (anguloActual >= 360) anguloActual -= 360;
            panelRueda.repaint();
        });
        
        iniciarGiro();
    }

    private void crearInterfaz() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel("GIRA LA RULETA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(255, 215, 0));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Presiona STOP para elegir tu pieza", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(200, 200, 220));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelTitulo.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTitulo.add(lblSubtitulo);
        
        add(panelTitulo, BorderLayout.NORTH);

        // Panel de la ruleta
        panelRueda = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                int r = Math.min(w, h) - 60;
                int cx = w / 2, cy = h / 2;
                int x = cx - r / 2, y = cy - r / 2;

                // Guardar transformación original
                AffineTransform original = g2.getTransform();

                // Sombra de la ruleta
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillOval(x + 10, y + 10, r, r);

                // Círculo exterior dorado
                g2.setStroke(new BasicStroke(8f));
                g2.setColor(new Color(180, 140, 60));
                g2.drawOval(x, y, r, r);

                // Disco de fondo con gradiente
                GradientPaint gradient = new GradientPaint(
                    cx, y, new Color(40, 50, 80),
                    cx, y + r, new Color(25, 30, 50)
                );
                g2.setPaint(gradient);
                g2.fillOval(x, y, r, r);

                double paso = 360.0 / SECTORES.length;
                double ang = -90;

                // Dibujar sectores con diseño mejorado
                for (int i = 0; i < SECTORES.length; i++) {
                    // Colores alternados más vibrantes
                    Color color1 = new Color(60, 90, 180, 240);
                    Color color2 = new Color(90, 120, 200, 240);
                    g2.setColor(i % 2 == 0 ? color1 : color2);
                    g2.fillArc(x, y, r, r, (int) ang, (int) paso);
                    
                    // Borde brillante del sector
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawArc(x, y, r, r, (int) ang, (int) paso);

                    ang += paso;
                }

                // ROTAR LA RULETA PARA EL GIRO
                g2.rotate(Math.toRadians(anguloActual), cx, cy);

                // Dibujar texto en los sectores (ahora rotará con la ruleta)
                ang = -90;
                for (int i = 0; i < SECTORES.length; i++) {
                    String txt = switch (SECTORES[i]) {
                        case HOMBRE_LOBO -> "LOBO";
                        case VAMPIRO -> "VAMPIRO";
                        case NIGROMANTE -> "MUERTE";
                        default -> "?";
                    };
                    
                    g2.setFont(new Font("Arial", Font.BOLD, 22));
                    FontMetrics fm = g2.getFontMetrics();
                    
                    // Calcular posición del texto
                    double anguloTexto = ang + paso / 2;
                    double rad = Math.toRadians(anguloTexto);
                    double distancia = r * 0.35;
                    
                    // Guardar transformación actual
                    AffineTransform textoTransform = g2.getTransform();
                    
                    // Mover al centro del sector
                    double textX = cx + distancia * Math.cos(rad);
                    double textY = cy + distancia * Math.sin(rad);
                    g2.translate(textX, textY);
                    
                    // Rotar el texto para que sea legible (perpendicular al radio)
                    g2.rotate(rad + Math.PI / 2);
                    
                    int tx = -fm.stringWidth(txt) / 2;
                    int ty = fm.getAscent() / 2;
                    
                    // Sombra del texto (efecto 3D)
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.drawString(txt, tx + 2, ty + 2);
                    
                    // Contorno del texto
                    g2.setColor(new Color(0, 0, 0, 200));
                    g2.setStroke(new BasicStroke(3f));
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx != 0 || dy != 0) {
                                g2.drawString(txt, tx + dx, ty + dy);
                            }
                        }
                    }
                    
                    // Texto principal brillante
                    g2.setColor(new Color(255, 255, 255));
                    g2.drawString(txt, tx, ty);
                    
                    // Brillo en el texto
                    g2.setColor(new Color(255, 255, 200, 100));
                    g2.drawString(txt, tx - 1, ty - 1);
                    
                    // Restaurar transformación
                    g2.setTransform(textoTransform);
                    
                    ang += paso;
                }

                // Centro decorativo de la ruleta
                int centerSize = r / 6;
                int centerX = cx - centerSize / 2;
                int centerY = cy - centerSize / 2;
                
                // Círculo central con gradiente
                GradientPaint centerGradient = new GradientPaint(
                    cx, centerY, new Color(255, 215, 0),
                    cx, centerY + centerSize, new Color(180, 140, 60)
                );
                g2.setPaint(centerGradient);
                g2.fillOval(centerX, centerY, centerSize, centerSize);
                
                // Borde del centro
                g2.setColor(new Color(120, 80, 30));
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(centerX, centerY, centerSize, centerSize);

                // Restaurar transform para dibujar el puntero fijo
                g2.setTransform(original);

                // PUNTERO FIJO (flecha mejorada)
                int px = cx, py = cy - r/2 - 35;
                Polygon flecha = new Polygon();
                flecha.addPoint(px, py);
                flecha.addPoint(px - 28, py + 25);
                flecha.addPoint(px, py + 15);
                flecha.addPoint(px + 28, py + 25);
                
                // Sombra de la flecha
                g2.setColor(new Color(0, 0, 0, 100));
                g2.translate(3, 3);
                g2.fill(flecha);
                g2.translate(-3, -3);
                
                // Gradiente de la flecha
                GradientPaint arrowGradient = new GradientPaint(
                    px, py, new Color(255, 80, 80),
                    px, py + 25, new Color(200, 40, 40)
                );
                g2.setPaint(arrowGradient);
                g2.fill(flecha);
                
                // Borde de la flecha
                g2.setColor(new Color(150, 20, 20));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(flecha);
                
                // Brillo en la flecha
                g2.setColor(new Color(255, 150, 150, 150));
                g2.setStroke(new BasicStroke(1.5f));
                Polygon shine = new Polygon();
                shine.addPoint(px, py + 2);
                shine.addPoint(px - 10, py + 10);
                shine.addPoint(px, py + 8);
                g2.draw(shine);

                g2.dispose();
            }
        };

        panelRueda.setOpaque(false);
        panelRueda.setPreferredSize(new Dimension(580, 580));
        add(panelRueda, BorderLayout.CENTER);

        // Botón STOP mejorado
        JButton btnStop = new JButton("STOP");
        btnStop.setFont(new Font("Arial", Font.BOLD, 36));
        btnStop.setForeground(Color.WHITE);
        btnStop.setPreferredSize(new Dimension(0, 85));
        btnStop.setFocusPainted(false);
        btnStop.setBorderPainted(false);
        btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Gradiente para el botón
        btnStop.setBackground(new Color(220, 50, 50));
        btnStop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnStop.setBackground(new Color(255, 80, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnStop.setBackground(new Color(220, 50, 50));
            }
        });
        btnStop.addActionListener(e -> detenerRuleta());
        
        JPanel panelBoton = new JPanel(new BorderLayout());
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 50, 25, 50));
        panelBoton.add(btnStop, BorderLayout.CENTER);
        
        add(panelBoton, BorderLayout.SOUTH);
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