/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Cuentas.GestorUsuarios;
import Cuentas.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author najma
 */
public class MenuPrincipal extends JFrame {
    
    private final Usuario usuarioActual;
    private final GestorUsuarios gestorUsuarios;
    private BufferedImage fondoMenu;
    
    public MenuPrincipal(Usuario usuario, GestorUsuarios gestor) {
        super("Vampire Wargame");
        this.usuarioActual = usuario;
        this.gestorUsuarios = gestor;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        
        cargarFondo();
        inicializarComponentes();
    }
    
    private void cargarFondo() {
        try {
            fondoMenu = ImageIO.read(new File("src/Interfaz/Imagenes/Fondo_Menu.png"));
        } catch (Exception e) {
            try {
                fondoMenu = ImageIO.read(new File("Interfaz/Imagenes/Fondo_Menu.png"));
            } catch (Exception e2) {
                System.err.println("No se pudo cargar Fondo_Menu.png");
            }
        }
    }
    
    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondoMenu != null) {
                    g.drawImage(fondoMenu, 0, 0, getWidth(), getHeight(), null);
                } else {
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(10, 20, 40),
                        0, getHeight(), new Color(30, 50, 90)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panelPrincipal.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Panel lateral izquierdo con menú
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setOpaque(false);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));
        
        // Título
        JLabel lblTitulo = new JLabel("Vampire Wargame");
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 52));
        lblTitulo.setForeground(new Color(240, 240, 255));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelMenu.add(lblTitulo);
        
        panelMenu.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Info usuario
        JLabel lblUsuario = new JLabel("Bienvenido: " + usuarioActual.getNombreUsuario());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 22));
        lblUsuario.setForeground(new Color(200, 220, 255));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelMenu.add(lblUsuario);
        
        panelMenu.add(Box.createRigidArea(new Dimension(0, 60)));
        
        // Botones
        JButton btnJugar = crearBotonMenu("JUGAR VAMPIRE WARGAME");
        btnJugar.addActionListener(e -> verificarYJugar());
        panelMenu.add(btnJugar);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JButton btnMiCuenta = crearBotonMenu("MI CUENTA");
        btnMiCuenta.addActionListener(e -> abrirMiCuenta());
        panelMenu.add(btnMiCuenta);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JButton btnReportes = crearBotonMenu("REPORTES");
        btnReportes.addActionListener(e -> abrirReportes());
        panelMenu.add(btnReportes);
        panelMenu.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JButton btnLogOut = crearBotonRojo("LOG OUT");
        btnLogOut.addActionListener(e -> cerrarSesion());
        panelMenu.add(btnLogOut);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(panelMenu, gbc);
        
        setContentPane(panelPrincipal);
    }
    
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(new Color(50, 80, 140));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(450, 70));
        btn.setPreferredSize(new Dimension(450, 70));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 100, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 80, 140));
            }
        });
        
        return btn;
    }
    
    private JButton crearBotonRojo(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(new Color(140, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(450, 70));
        btn.setPreferredSize(new Dimension(450, 70));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(170, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(140, 50, 50));
            }
        });
        
        return btn;
    }
    
    private JButton crearBotonAzul(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(50, 80, 140));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
     
    // Jugar
    private void verificarYJugar() {
        List<Usuario> posiblesOponentes = gestorUsuarios.obtenerRankingPorPuntos()
            .stream()
            .filter(u -> !u.getNombreUsuario().equals(usuarioActual.getNombreUsuario()))
            .filter(Usuario::isActivo)
            .collect(Collectors.toList());
        
        if (posiblesOponentes.isEmpty()) {
            mostrarDialogoInfo("Sin Oponentes", 
                "No hay oponentes disponibles para jugar.\n\n" +
                "Debes crear al menos otra cuenta desde el menú de inicio\n" +
                "para poder iniciar una partida.");
            return;
        }
        
        VentanaJuego.iniciarDesdeMenu(this, usuarioActual, gestorUsuarios);
    }
    
    // Mi cuenta
    private void abrirMiCuenta() {
        JDialog dialogoCuenta = new JDialog(this, "Mi Cuenta", true);
        dialogoCuenta.setSize(600, 550);
        dialogoCuenta.setLocationRelativeTo(this);
        dialogoCuenta.setUndecorated(true);
        
        // Panel semi-transparente
        JPanel panelContenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 40, 60, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelContenido.setOpaque(false);
        panelContenido.setLayout(null);
        panelContenido.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 3));
        
        // Título
        JLabel lblTitulo = new JLabel("Mi Cuenta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 600, 35);
        panelContenido.add(lblTitulo);
        
        // Información del usuario
        int yPos = 90;
        agregarInfoLabel(panelContenido, "Usuario:", usuarioActual.getNombreUsuario(), 80, yPos);
        yPos += 50;
        agregarInfoLabel(panelContenido, "Puntos:", String.valueOf(usuarioActual.getPuntos()), 80, yPos);
        yPos += 50;
        agregarInfoLabel(panelContenido, "Fecha Registro:", usuarioActual.getFechaIngresoTexto(), 80, yPos);
        yPos += 50;
        agregarInfoLabel(panelContenido, "Partidas Ganadas:", 
            String.valueOf(usuarioActual.getEstadistica().getPartidasGanadas()), 80, yPos);
        yPos += 50;
        agregarInfoLabel(panelContenido, "Partidas Perdidas:", 
            String.valueOf(usuarioActual.getEstadistica().getPartidasPerdidas()), 80, yPos);
        yPos += 50;
        agregarInfoLabel(panelContenido, "% Victorias:", 
            String.format("%.1f%%", usuarioActual.getEstadistica().getPorcentajeVictorias()), 80, yPos);
        
        // Botones
        JButton btnCambiar = crearBotonAzul("Cambiar Password");
        btnCambiar.setBounds(80, 440, 160, 45);
        btnCambiar.addActionListener(e -> cambiarPassword(dialogoCuenta));
        panelContenido.add(btnCambiar);
        
        JButton btnEliminar = new JButton("Eliminar Cuenta");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEliminar.setBackground(new Color(140, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBounds(260, 440, 160, 45);
        btnEliminar.addActionListener(e -> eliminarCuenta(dialogoCuenta));
        panelContenido.add(btnEliminar);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCerrar.setBackground(new Color(100, 100, 120));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBounds(440, 440, 100, 45);
        btnCerrar.addActionListener(e -> dialogoCuenta.dispose());
        panelContenido.add(btnCerrar);
        
        dialogoCuenta.setContentPane(panelContenido);
        dialogoCuenta.setVisible(true);
    }
    
    private void agregarInfoLabel(JPanel panel, String etiqueta, String valor, int x, int y) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 16));
        lblEtiqueta.setForeground(new Color(200, 220, 255));
        lblEtiqueta.setBounds(x, y, 180, 30);
        panel.add(lblEtiqueta);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 16));
        lblValor.setForeground(Color.WHITE);
        lblValor.setBounds(x + 200, y, 300, 30);
        panel.add(lblValor);
    }
    
    private void cambiarPassword(JDialog padre) {
        JDialog dialogo = new JDialog(padre, "Cambiar Password", true);
        dialogo.setSize(520, 380);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setUndecorated(true);
        
        JPanel panelContenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 40, 60, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelContenido.setOpaque(false);
        panelContenido.setLayout(null);
        panelContenido.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 2));
        
        JLabel lblTitulo = new JLabel("Cambiar Password", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 520, 30);
        panelContenido.add(lblTitulo);
        
        // Password actual
        JLabel lblActual = new JLabel("Password Actual:");
        lblActual.setFont(new Font("Arial", Font.BOLD, 15));
        lblActual.setForeground(Color.WHITE);
        lblActual.setBounds(60, 90, 160, 30);
        panelContenido.add(lblActual);
        
        JPasswordField txtActual = new JPasswordField();
        txtActual.setFont(new Font("Arial", Font.PLAIN, 14));
        txtActual.setBounds(230, 90, 240, 35);
        panelContenido.add(txtActual);
        
        // Password nueva
        JLabel lblNueva = new JLabel("Password Nueva:");
        lblNueva.setFont(new Font("Arial", Font.BOLD, 15));
        lblNueva.setForeground(Color.WHITE);
        lblNueva.setBounds(60, 150, 160, 30);
        panelContenido.add(lblNueva);
        
        JPasswordField txtNueva = new JPasswordField();
        txtNueva.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNueva.setBounds(230, 150, 240, 35);
        panelContenido.add(txtNueva);
        
        // Confirmar
        JLabel lblConfirmar = new JLabel("Confirmar:");
        lblConfirmar.setFont(new Font("Arial", Font.BOLD, 15));
        lblConfirmar.setForeground(Color.WHITE);
        lblConfirmar.setBounds(60, 210, 160, 30);
        panelContenido.add(lblConfirmar);
        
        JPasswordField txtConfirmar = new JPasswordField();
        txtConfirmar.setFont(new Font("Arial", Font.PLAIN, 14));
        txtConfirmar.setBounds(230, 210, 240, 35);
        panelContenido.add(txtConfirmar);
        
        // Checkbox mostrar
        JCheckBox chkMostrar = new JCheckBox("Mostrar");
        chkMostrar.setFont(new Font("Arial", Font.PLAIN, 13));
        chkMostrar.setForeground(Color.WHITE);
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.setBounds(230, 250, 100, 25);
        chkMostrar.addActionListener(e -> {
            char echo = chkMostrar.isSelected() ? (char) 0 : '•';
            txtActual.setEchoChar(echo);
            txtNueva.setEchoChar(echo);
            txtConfirmar.setEchoChar(echo);
        });
        panelContenido.add(chkMostrar);
        
        // Botones
        JButton btnAceptar = crearBotonAzul("Aceptar");
        btnAceptar.setBounds(140, 300, 110, 45);
        btnAceptar.addActionListener(e -> {
            String actual = new String(txtActual.getPassword());
            String nueva = new String(txtNueva.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            
            if (!nueva.equals(confirmar)) {
                mostrarDialogoError(dialogo, "Las contraseñas no coinciden");
                return;
            }
            
            if (gestorUsuarios.cambiarPassword(usuarioActual, actual, nueva)) {
                mostrarDialogoInfo("Éxito", "Password cambiado exitosamente");
                dialogo.dispose();
            } else {
                mostrarDialogoError(dialogo, "Error al cambiar password.\nVerifica el password actual y los requisitos.");
            }
        });
        panelContenido.add(btnAceptar);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCancelar.setBackground(new Color(140, 50, 50));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBounds(270, 300, 110, 45);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelContenido.add(btnCancelar);
        
        dialogo.setContentPane(panelContenido);
        dialogo.setVisible(true);
    }
    
    private void eliminarCuenta(JDialog padre) {
        int confirmar = JOptionPane.showConfirmDialog(padre,
            "¿Estás seguro de eliminar tu cuenta?\nEsta acción NO se puede deshacer.",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            gestorUsuarios.eliminarUsuario(usuarioActual);
            JOptionPane.showMessageDialog(padre, "Cuenta eliminada exitosamente");
            padre.dispose();
            dispose();
            new MenuInicio(gestorUsuarios).setVisible(true);
        }
    }
    
    // Reportes
    private void abrirReportes() {
        JDialog dialogoReportes = new JDialog(this, "Reportes", true);
        dialogoReportes.setSize(750, 600);
        dialogoReportes.setLocationRelativeTo(this);
        dialogoReportes.setUndecorated(true);
        
        JPanel panelContenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 40, 60, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BorderLayout(10, 10));
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 120, 180), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitulo = new JLabel("Reportes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        panelContenido.add(lblTitulo, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(40, 50, 70));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.addTab("Ranking de Jugadores", crearPanelRanking());
        tabbedPane.addTab("Mis Últimos Juegos", crearPanelLogs());
        
        panelContenido.add(tabbedPane, BorderLayout.CENTER);
        
        JButton btnCerrar = crearBotonAzul("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(120, 40));
        btnCerrar.addActionListener(e -> dialogoReportes.dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);
        panelContenido.add(panelBoton, BorderLayout.SOUTH);
        
        dialogoReportes.setContentPane(panelContenido);
        dialogoReportes.setVisible(true);
    }
    
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 50, 70));
        
        String[] columnas = {"Posición", "Usuario", "Puntos", "Ganadas", "Perdidas", "% Victoria"};
        Object[][] datos = obtenerDatosRanking();
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.setEnabled(false);
        tabla.setBackground(new Color(50, 60, 80));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(new Color(70, 80, 100));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(35, 45, 65));
        tabla.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(50, 60, 80));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] obtenerDatosRanking() {
        List<Usuario> ranking = gestorUsuarios.obtenerRankingPorPuntos();
        Object[][] datos = new Object[ranking.size()][6];
        
        for (int i = 0; i < ranking.size(); i++) {
            Usuario u = ranking.get(i);
            datos[i][0] = (i + 1);
            datos[i][1] = u.getNombreUsuario();
            datos[i][2] = u.getPuntos();
            datos[i][3] = u.getEstadistica().getPartidasGanadas();
            datos[i][4] = u.getEstadistica().getPartidasPerdidas();
            datos[i][5] = String.format("%.1f%%", u.getEstadistica().getPorcentajeVictorias());
        }
        return datos;
    }
    
    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 50, 70));
        
        List<String> logs = gestorUsuarios.obtenerLogs(usuarioActual.getNombreUsuario(), 20);
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        if (logs.isEmpty()) {
            modelo.addElement("No hay registros de partidas");
        } else {
            logs.forEach(modelo::addElement);
        }
        
        JList<String> lista = new JList<>(modelo);
        lista.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lista.setBackground(new Color(50, 60, 80));
        lista.setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(lista);
        scroll.getViewport().setBackground(new Color(50, 60, 80));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Log out
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Deseas cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new MenuInicio(gestorUsuarios).setVisible(true);
        }
    }
    
    private void mostrarDialogoInfo(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarDialogoError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void mostrar(Usuario usuario, GestorUsuarios gestor) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal(usuario, gestor).setVisible(true));
    }
}