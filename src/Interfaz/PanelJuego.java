/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Cuentas.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author najma
 */
public class PanelJuego extends JPanel {

    private final Usuario negro;
    private final Usuario blanco;
    private final GestorUsuarios gestor;

    private final JLabel lblCrono = new JLabel("00:00");
    private final JLabel lblNombreNegro = new JLabel();
    private final JLabel lblNombreBlanco = new JLabel();

    private final JButton btnGirar = new JButton("Girar ruleta");
    private final JButton btnRenunciar = new JButton("Renunciar");
    private final JButton btnLibro = new JButton("📖");

    private final PanelTablero tablero = new PanelTablero();

    private Timer timer;
    private int segundos = 0;

    private Turno turnoActual = Turno.NEGRAS;
    public enum Turno { NEGRAS, BLANCAS }

    // estado de piezas para habilitar renuncia
    private int piezasNegrasVivas = 6;
    private int piezasBlancasVivas = 6;

    public PanelJuego(Usuario uNegro, Usuario uBlanco, GestorUsuarios gestor) {
        this.negro = uNegro;
        this.blanco = uBlanco;
        this.gestor = gestor;

        setLayout(new BorderLayout());
        construirUI();
        iniciarCronometro();
        actualizarTurnoYControles();
    }

    private void construirUI() {
        // HUD superior
        JPanel hudArriba = new JPanel(new BorderLayout());
        hudArriba.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        hudArriba.setBackground(new Color(20,20,30));

        lblCrono.setForeground(Color.WHITE);
        lblCrono.setFont(new Font("Segoe UI", Font.BOLD, 18));

        lblNombreNegro.setText("🦇 " + negro.getNombreUsuario() + " (Negras)");
        lblNombreBlanco.setText("🧛 " + blanco.getNombreUsuario() + " (Blancas)");
        lblNombreNegro.setForeground(Color.WHITE);
        lblNombreBlanco.setForeground(Color.WHITE);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);
        izq.add(lblNombreNegro);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        der.setOpaque(false);
        der.add(lblNombreBlanco);

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        centro.setOpaque(false);
        centro.add(lblCrono);

        hudArriba.add(izq, BorderLayout.WEST);
        hudArriba.add(centro, BorderLayout.CENTER);
        hudArriba.add(der, BorderLayout.EAST);

        add(hudArriba, BorderLayout.NORTH);

        // Tablero al centro
        add(tablero, BorderLayout.CENTER);

        // HUD inferior
        JPanel hudAbajo = new JPanel(new BorderLayout());
        hudAbajo.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        hudAbajo.setBackground(new Color(20,20,30));

        JPanel izqInf = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izqInf.setOpaque(false);
        izqInf.add(btnGirar);

        JPanel derInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derInf.setOpaque(false);
        btnLibro.setToolTipText("Ver estadísticas de personajes");
        derInf.add(btnRenunciar);
        derInf.add(btnLibro);

        hudAbajo.add(izqInf, BorderLayout.WEST);
        hudAbajo.add(derInf, BorderLayout.EAST);

        add(hudAbajo, BorderLayout.SOUTH);

        // Listeners
        btnGirar.addActionListener(e -> onGirar());
        btnRenunciar.addActionListener(e -> onRenunciar());
        btnLibro.addActionListener(this::onLibro);
    }

    private void iniciarCronometro() {
        timer = new Timer(1000, e -> {
            segundos++;
            int m = segundos / 60;
            int s = segundos % 60;
            lblCrono.setText(String.format("%02d:%02d", m, s));
        });
        timer.start();
    }

    private void onGirar() {
        // Aquí va tu lógica real de ruleta.
        // Para demo: alternamos turno.
        alternarTurno();
    }

    private void alternarTurno() {
        turnoActual = (turnoActual == Turno.NEGRAS) ? Turno.BLANCAS : Turno.NEGRAS;
        actualizarTurnoYControles();
    }

    private void actualizarTurnoYControles() {
        boolean turnoNegras = (turnoActual == Turno.NEGRAS);
        // El que NO está en turno queda bloqueado para girar y mover (aquí solo deshabilito "Girar")
        btnGirar.setEnabled(turnoNegras); // el que invita empieza (negras)

        // Renuncia: cuando el jugador EN TURNO tiene <= mitad de piezas
        int totalInicial = 6;
        boolean activarRenuncia = turnoNegras
                ? (piezasNegrasVivas <= totalInicial / 2)
                : (piezasBlancasVivas <= totalInicial / 2);
        btnRenunciar.setEnabled(activarRenuncia);

        // Indica al tablero quién puede mover (si quieres)
        tablero.setBloqueado(!turnoNegras); // ejemplo básico
    }

    private void onRenunciar() {
        int r = JOptionPane.showConfirmDialog(this,
                "¿Deseas renunciar? Tu rival ganará la partida.",
                "Confirmar renuncia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            finDePartida(turnoActual == Turno.NEGRAS ? blanco : negro);
        }
    }

    private void onLibro(ActionEvent e) {
        // Muestra un overlay simple con un panel (luego pegas la imagen que me enviarás)
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Estadísticas", Dialog.ModalityType.MODELESS);
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(600, 380));
        p.add(new JLabel("Aquí irá la IMAGEN con ataque/vida/escudo de cada personaje."));
        dlg.setContentPane(p);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void finDePartida(Usuario ganador) {
        timer.stop();
        JOptionPane.showMessageDialog(this,
                "¡Ganador: " + ganador.getNombreUsuario() + "!",
                "Partida terminada", JOptionPane.INFORMATION_MESSAGE);
        // aquí podrías actualizar estadísticas/puntos en gestor/usuarios
    }

    // === métodos que puede invocar tu motor de juego ===
    public void setPiezasVivas(int negras, int blancas) {
        this.piezasNegrasVivas = negras;
        this.piezasBlancasVivas = blancas;
        actualizarTurnoYControles();
    }

    public void bloquearPorTurno(boolean bloquear) {
        btnGirar.setEnabled(!bloquear);
        tablero.setBloqueado(bloquear);
    }
}
