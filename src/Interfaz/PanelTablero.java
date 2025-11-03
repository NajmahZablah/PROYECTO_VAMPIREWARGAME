/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author najma
 */
public class PanelTablero extends JPanel {
    
    private final int filas = 6;
    private final int columnas = 6;
    private Color colorClaro = new Color(230, 230, 230, 170); // blanco con transparencia
    private Color colorOscuro = new Color(30, 35, 60, 170);   // azul oscuro con transparencia
    private Color colorBorde  = new Color(10, 10, 20, 200);   // borde sutil
    private int grosorBorde = 2;

    PanelTablero() {
        // permitimos que se vea el fondo del castillo detrás
        setOpaque(false);
        // Los botones/celdas irán con GridLayout sobre este panel
        setLayout(new GridLayout(filas, columnas, 0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        int anchoCelda = w / columnas;
        int altoCelda  = h / filas;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cuadros alternos
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                boolean claro = ((f + c) % 2 == 0);
                g2.setColor(claro ? colorClaro : colorOscuro);
                int x = c * anchoCelda;
                int y = f * altoCelda;
                g2.fillRect(x, y, anchoCelda, altoCelda);
            }
        }

        // Borde exterior
        g2.setStroke(new BasicStroke(grosorBorde));
        g2.setColor(colorBorde);
        g2.drawRect(0, 0, columnas * anchoCelda - 1, filas * altoCelda - 1);

        g2.dispose();
    }

    // Setters por si quieres cambiar paleta desde fuera
    public void setColores(Color claro, Color oscuro) {
        this.colorClaro = claro;
        this.colorOscuro = oscuro;
        repaint();
    }
}