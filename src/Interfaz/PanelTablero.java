/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author najma
 */
/* ============================================================================
   ARCHIVO: PanelTablero.java
   ============================================================================ */
public class PanelTablero extends JPanel {

    public interface ClickListener {
        void onCeldaClick(int fila, int col);
    }

    private static final int N = 6;
    private final BufferedImage[][] piezas = new BufferedImage[N][N];
    private final Set<Point> highlights = new HashSet<>();
    private BufferedImage fondoTablero;
    private int side;
    private int offX, offY;
    private ClickListener clickListener;

    public PanelTablero() {
        setOpaque(false);
        setPreferredSize(new Dimension(880, 880));
        
        cargarFondo();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point cell = cellFromPixel(e.getPoint());
                if (cell != null && clickListener != null) {
                    clickListener.onCeldaClick(cell.y, cell.x);
                }
            }
        });
    }

    private void cargarFondo() {
        try {
            fondoTablero = ImageIO.read(new File("src/Interfaz/Imagenes/tablero_fondo.png"));
        } catch (Exception e) {
            try {
                fondoTablero = ImageIO.read(new File("Interfaz/Imagenes/tablero_fondo.png"));
            } catch (Exception e2) {
                System.err.println("No se pudo cargar tablero_fondo.png: " + e2.getMessage());
            }
        }
    }

    public void setOnClick(ClickListener l) { 
        this.clickListener = l; 
    }

    public void setPieza(int fila, int col, BufferedImage img) {
        piezas[fila][col] = img;
        repaint();
    }

    public void limpiarPiezas() {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                piezas[r][c] = null;
            }
        }
        repaint();
    }

    public void setHighlights(Set<Point> hs) {
        highlights.clear();
        if (hs != null) highlights.addAll(hs);
        repaint();
    }

    private void recomputeGeometry() {
        int cw = getWidth() / N;
        int ch = getHeight() / N;
        side = Math.min(cw, ch);
        int tabW = side * N, tabH = side * N;
        offX = (getWidth() - tabW) / 2;
        offY = (getHeight() - tabH) / 2;
    }

    private Point cellFromPixel(Point p) {
        recomputeGeometry();
        int x = p.x - offX, y = p.y - offY;
        if (x < 0 || y < 0) return null;
        int col = x / side, fila = y / side;
        if (col < 0 || col >= N || fila < 0 || fila >= N) return null;
        return new Point(col, fila);
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        recomputeGeometry();

        // Dibujar fondo de imagen escalado
        if (fondoTablero != null) {
            g2.drawImage(fondoTablero, 0, 0, getWidth(), getHeight(), null);
        }

        // Tablero con opacidad (60% para ver mejor el fondo)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        // Casillas alternadas
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                boolean oscuro = (r + c) % 2 == 0;
                g2.setColor(oscuro ? 
                    new Color(25, 35, 60, 200) : 
                    new Color(180, 190, 210, 200));
                g2.fillRect(offX + c * side, offY + r * side, side, side);
            }
        }

        // Restaurar opacidad completa
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Highlights
        g2.setStroke(new BasicStroke(4f));
        for (Point p : highlights) {
            int x = offX + p.x * side, y = offY + p.y * side;
            g2.setColor(new Color(255, 215, 0, 140));
            g2.fillRect(x, y, side, side);
            g2.setColor(new Color(255, 215, 0, 220));
            g2.drawRect(x, y, side, side);
        }

        // Piezas escaladas
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                BufferedImage img = piezas[r][c];
                if (img != null) {
                    int margin = (int) (side * 0.08);
                    Image esc = img.getScaledInstance(side - 2 * margin, side - 2 * margin, Image.SCALE_SMOOTH);
                    g2.drawImage(esc, offX + c * side + margin, offY + r * side + margin, null);
                }
            }
        }
        
        g2.dispose();
    }
}