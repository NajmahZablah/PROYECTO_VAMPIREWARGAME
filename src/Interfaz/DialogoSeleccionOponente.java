/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Cuentas.GestorUsuarios;
import Cuentas.Usuario;
import Modelo.ColorJugador;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author najma
 */
public class DialogoSeleccionOponente extends JDialog {
    
    private Usuario usuarioActual;
    private Usuario oponenteSeleccionado;
    private ColorJugador colorElegido;
    private boolean confirmado = false;

    public DialogoSeleccionOponente(Frame parent, Usuario usuarioActual, GestorUsuarios gestor) {
        super(parent, "Seleccionar Oponente", true);
        this.usuarioActual = usuarioActual;
        
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(20, 25, 35));
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Título
        JLabel lblTitulo = new JLabel("Selecciona tu oponente y color de piezas", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCentral.setBackground(new Color(20, 25, 35));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Lista de oponentes
        JPanel panelOponentes = new JPanel(new BorderLayout(5, 5));
        panelOponentes.setBackground(new Color(30, 35, 45));
        panelOponentes.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 120, 150), 2),
            "Oponentes Disponibles",
            0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));

        List<Usuario> todos = gestor.obtenerRankingPorPuntos();
        DefaultListModel<String> modeloLista = new DefaultListModel<>();
        
        for (Usuario u : todos) {
            if (!u.getNombreUsuario().equals(usuarioActual.getNombreUsuario()) && u.isActivo()) {
                modeloLista.addElement(u.getNombreUsuario() + " (" + u.getPuntos() + " pts)");
            }
        }

        if (modeloLista.isEmpty()) {
            modeloLista.addElement("No hay oponentes disponibles");
        }

        JList<String> listaOponentes = new JList<>(modeloLista);
        listaOponentes.setFont(new Font("Arial", Font.PLAIN, 14));
        listaOponentes.setBackground(new Color(40, 45, 55));
        listaOponentes.setForeground(Color.WHITE);
        listaOponentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollOponentes = new JScrollPane(listaOponentes);
        scrollOponentes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelOponentes.add(scrollOponentes, BorderLayout.CENTER);

        // Selección de color
        JPanel panelColor = new JPanel(new BorderLayout(5, 5));
        panelColor.setBackground(new Color(30, 35, 45));
        panelColor.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 120, 150), 2),
            "Elige tu Color",
            0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE
        ));

        ButtonGroup grupoColor = new ButtonGroup();
        JRadioButton rbBlancas = new JRadioButton("Piezas Blancas (inicia primero)");
        JRadioButton rbNegras = new JRadioButton("Piezas Negras");
        
        rbBlancas.setFont(new Font("Arial", Font.PLAIN, 14));
        rbNegras.setFont(new Font("Arial", Font.PLAIN, 14));
        rbBlancas.setForeground(Color.WHITE);
        rbNegras.setForeground(Color.WHITE);
        rbBlancas.setBackground(new Color(30, 35, 45));
        rbNegras.setBackground(new Color(30, 35, 45));
        rbBlancas.setFocusPainted(false);
        rbNegras.setFocusPainted(false);
        
        grupoColor.add(rbBlancas);
        grupoColor.add(rbNegras);
        rbBlancas.setSelected(true);

        JPanel panelRadios = new JPanel(new GridLayout(2, 1, 5, 10));
        panelRadios.setBackground(new Color(30, 35, 45));
        panelRadios.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelRadios.add(rbBlancas);
        panelRadios.add(rbNegras);
        panelColor.add(panelRadios, BorderLayout.CENTER);

        panelCentral.add(panelOponentes);
        panelCentral.add(panelColor);
        add(panelCentral, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setBackground(new Color(20, 25, 35));

        JButton btnConfirmar = new JButton("JUGAR");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirmar.setBackground(new Color(50, 150, 50));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setPreferredSize(new Dimension(120, 40));
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBackground(new Color(150, 50, 50));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnConfirmar.addActionListener(e -> {
            String seleccion = listaOponentes.getSelectedValue();
            if (seleccion == null || seleccion.equals("No hay oponentes disponibles")) {
                JOptionPane.showMessageDialog(this,
                    "Debes seleccionar un oponente",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Extraer nombre del oponente
            String nombreOponente = seleccion.substring(0, seleccion.indexOf(" ("));
            oponenteSeleccionado = gestor.obtener(nombreOponente);
            
            colorElegido = rbBlancas.isSelected() ? ColorJugador.BLANCO : ColorJugador.NEGRO;
            confirmado = true;
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });

        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Usuario getOponenteSeleccionado() {
        return oponenteSeleccionado;
    }

    public ColorJugador getColorElegido() {
        return colorElegido;
    }
}