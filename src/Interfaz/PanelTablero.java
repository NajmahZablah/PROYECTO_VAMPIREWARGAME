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

    private boolean bloqueado = false;

    public PanelTablero() {
        setPreferredSize(new Dimension(900, 600));
    }

    public void setBloqueado(boolean b) { bloqueado = b; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo “oscuro” para que combine con tu arte
        g.setColor(new Color(10, 10, 20));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Tablero 6x6
        int filas = 6, cols = 6;
        int cw = getWidth() / cols;
        int ch = getHeight() / filas;

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < cols; c++) {
                boolean oscuro = (r + c) % 2 == 0;
                g.setColor(oscuro ? new Color(25,35,65,220) : new Color(210,220,235,180));
                g.fillRect(c * cw, r * ch, cw, ch);
            }
        }

        if (bloqueado) {
            g.setColor(new Color(0,0,0,80));
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }
}