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
/* ============================================================================
ARCHIVO 2: MenuPrincipal.java
Menú principal después del login
============================================================================ */
public class MenuPrincipal extends JFrame {
    
    private final Usuario usuarioActual;
    private final GestorUsuarios gestorUsuarios;
    private BufferedImage fondoMenu;
    
    public MenuPrincipal(Usuario usuario, GestorUsuarios gestor) {
        super("Vampire Wargame");
        this.usuarioActual = usuario;
        this.gestorUsuarios = gestor;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
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
                        0, 0, new Color(10, 15, 30),
                        0, getHeight(), new Color(30, 45, 80)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panelPrincipal.setLayout(null);
        
        // Título
        JLabel lblTitulo = new JLabel("Vampire Wargame");
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 48));
        lblTitulo.setForeground(new Color(220, 220, 255));
        lblTitulo.setBounds(150, 50, 500, 60);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo);
        
        // Info usuario
        JLabel lblBienvenida = new JLabel("Bienvenido: " + usuarioActual.getNombreUsuario());
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        lblBienvenida.setForeground(new Color(200, 220, 255));
        lblBienvenida.setBounds(250, 120, 300, 25);
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblBienvenida);
        
        JLabel lblPuntos = new JLabel("Puntos: " + usuarioActual.getPuntos());
        lblPuntos.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPuntos.setForeground(new Color(255, 215, 0));
        lblPuntos.setBounds(250, 150, 300, 20);
        lblPuntos.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblPuntos);
        
        // Botones
        int botonWidth = 350;
        int botonHeight = 60;
        int xCentro = (800 - botonWidth) / 2;
        int yInicio = 220;
        int espaciado = 20;
        
        JButton btnJugar = crearBoton("JUGAR VAMPIRE WARGAME", new Color(60, 100, 180));
        btnJugar.setBounds(xCentro, yInicio, botonWidth, botonHeight);
        btnJugar.addActionListener(e -> verificarYJugar());
        panelPrincipal.add(btnJugar);
        
        JButton btnMiCuenta = crearBoton("MI CUENTA", new Color(80, 120, 160));
        btnMiCuenta.setBounds(xCentro, yInicio + (botonHeight + espaciado), botonWidth, botonHeight);
        btnMiCuenta.addActionListener(e -> abrirMiCuenta());
        panelPrincipal.add(btnMiCuenta);
        
        JButton btnReportes = crearBoton("REPORTES", new Color(100, 140, 180));
        btnReportes.setBounds(xCentro, yInicio + 2 * (botonHeight + espaciado), botonWidth, botonHeight);
        btnReportes.addActionListener(e -> abrirReportes());
        panelPrincipal.add(btnReportes);
        
        JButton btnLogOut = crearBoton("LOG OUT", new Color(150, 60, 60));
        btnLogOut.setBounds(xCentro, yInicio + 3 * (botonHeight + espaciado), botonWidth, botonHeight);
        btnLogOut.addActionListener(e -> cerrarSesion());
        panelPrincipal.add(btnLogOut);
        
        JLabel lblVersion = new JLabel("v1.0 - Proyecto Programación 2");
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(150, 150, 170));
        lblVersion.setBounds(250, 630, 300, 20);
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblVersion);
        
        setContentPane(panelPrincipal);
    }
    
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorFondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(colorFondo);
            }
        });
        
        return btn;
    }
    
    /* ==================== JUGAR ==================== */
    
    private void verificarYJugar() {
        List<Usuario> posiblesOponentes = gestorUsuarios.obtenerRankingPorPuntos()
            .stream()
            .filter(u -> !u.getNombreUsuario().equals(usuarioActual.getNombreUsuario()))
            .filter(Usuario::isActivo)
            .collect(Collectors.toList());
        
        if (posiblesOponentes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay oponentes disponibles para jugar.\n\n" +
                "Debes crear al menos otra cuenta desde el menú de inicio\n" +
                "para poder iniciar una partida.",
                "Sin Oponentes",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        VentanaJuego.iniciarDesdeMenu(this, usuarioActual, gestorUsuarios);
    }
    
    /* ==================== MI CUENTA ==================== */
    
    private void abrirMiCuenta() {
        JDialog dialogoCuenta = new JDialog(this, "Mi Cuenta", true);
        dialogoCuenta.setSize(500, 400);
        dialogoCuenta.setLocationRelativeTo(this);
        dialogoCuenta.setLayout(new BorderLayout(10, 10));
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(new Color(30, 35, 50));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        agregarInfoLabel(panelInfo, "Usuario:", usuarioActual.getNombreUsuario());
        agregarInfoLabel(panelInfo, "Puntos:", String.valueOf(usuarioActual.getPuntos()));
        agregarInfoLabel(panelInfo, "Fecha de Registro:", usuarioActual.getFechaIngresoTexto());
        agregarInfoLabel(panelInfo, "Partidas Ganadas:", 
            String.valueOf(usuarioActual.getEstadistica().getPartidasGanadas()));
        agregarInfoLabel(panelInfo, "Partidas Perdidas:", 
            String.valueOf(usuarioActual.getEstadistica().getPartidasPerdidas()));
        agregarInfoLabel(panelInfo, "% Victorias:", 
            String.format("%.1f%%", usuarioActual.getEstadistica().getPorcentajeVictorias()));
        
        dialogoCuenta.add(panelInfo, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(30, 35, 50));
        
        JButton btnCambiarPassword = new JButton("Cambiar Password");
        btnCambiarPassword.setBackground(new Color(80, 120, 180));
        btnCambiarPassword.setForeground(Color.WHITE);
        btnCambiarPassword.setFocusPainted(false);
        btnCambiarPassword.addActionListener(e -> cambiarPassword(dialogoCuenta));
        
        JButton btnEliminarCuenta = new JButton("Eliminar Cuenta");
        btnEliminarCuenta.setBackground(new Color(180, 60, 60));
        btnEliminarCuenta.setForeground(Color.WHITE);
        btnEliminarCuenta.setFocusPainted(false);
        btnEliminarCuenta.addActionListener(e -> eliminarCuenta(dialogoCuenta));
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(100, 100, 120));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dialogoCuenta.dispose());
        
        panelBotones.add(btnCambiarPassword);
        panelBotones.add(btnEliminarCuenta);
        panelBotones.add(btnCerrar);
        
        dialogoCuenta.add(panelBotones, BorderLayout.SOUTH);
        dialogoCuenta.setVisible(true);
    }
    
    private void agregarInfoLabel(JPanel panel, String etiqueta, String valor) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        linea.setBackground(new Color(30, 35, 50));
        
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 14));
        lblEtiqueta.setForeground(new Color(200, 200, 220));
        lblEtiqueta.setPreferredSize(new Dimension(150, 20));
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 14));
        lblValor.setForeground(Color.WHITE);
        
        linea.add(lblEtiqueta);
        linea.add(lblValor);
        panel.add(linea);
    }
    
    private void cambiarPassword(JDialog padre) {
        JPasswordField txtActual = new JPasswordField(15);
        JPasswordField txtNueva = new JPasswordField(15);
        JPasswordField txtConfirmar = new JPasswordField(15);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Password Actual:"));
        panel.add(txtActual);
        panel.add(new JLabel("Password Nueva:"));
        panel.add(txtNueva);
        panel.add(new JLabel("Confirmar:"));
        panel.add(txtConfirmar);
        
        int resultado = JOptionPane.showConfirmDialog(padre, panel, 
            "Cambiar Password", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String actual = new String(txtActual.getPassword());
            String nueva = new String(txtNueva.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            
            if (!nueva.equals(confirmar)) {
                JOptionPane.showMessageDialog(padre, "Las contraseñas no coinciden");
                return;
            }
            
            if (gestorUsuarios.cambiarPassword(usuarioActual, actual, nueva)) {
                JOptionPane.showMessageDialog(padre, "Password cambiado exitosamente");
            } else {
                JOptionPane.showMessageDialog(padre, 
                    "Error al cambiar password.\nVerifica los requisitos.");
            }
        }
    }
    
    private void eliminarCuenta(JDialog padre) {
        int confirmar = JOptionPane.showConfirmDialog(padre,
            "¿Estás seguro de eliminar tu cuenta?\nEsta acción NO se puede deshacer.",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            gestorUsuarios.eliminarUsuario(usuarioActual);
            JOptionPane.showMessageDialog(padre, "Cuenta eliminada");
            padre.dispose();
            dispose();
            // Volver al menú de inicio
            new MenuInicio(gestorUsuarios).setVisible(true);
        }
    }
    
    /* ==================== REPORTES ==================== */
    
    private void abrirReportes() {
        JDialog dialogoReportes = new JDialog(this, "Reportes", true);
        dialogoReportes.setSize(600, 500);
        dialogoReportes.setLocationRelativeTo(this);
        dialogoReportes.setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ranking", crearPanelRanking());
        tabbedPane.addTab("Mis Juegos", crearPanelLogs());
        
        dialogoReportes.add(tabbedPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogoReportes.dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        dialogoReportes.add(panelBoton, BorderLayout.SOUTH);
        
        dialogoReportes.setVisible(true);
    }
    
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"Pos", "Usuario", "Puntos", "Ganadas", "Perdidas", "% Victoria"};
        Object[][] datos = obtenerDatosRanking();
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setRowHeight(25);
        tabla.setEnabled(false);
        
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
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
        List<String> logs = gestorUsuarios.obtenerLogs(usuarioActual.getNombreUsuario(), 20);
        
        DefaultListModel<String> modelo = new DefaultListModel<>();
        if (logs.isEmpty()) {
            modelo.addElement("No hay registros de partidas");
        } else {
            logs.forEach(modelo::addElement);
        }
        
        JList<String> lista = new JList<>(modelo);
        lista.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        return panel;
    }
    
    /* ==================== LOG OUT ==================== */
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Deseas cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new MenuInicio(gestorUsuarios).setVisible(true);
        }
    }
    
    /* ==================== MÉTODO ESTÁTICO ==================== */
    
    public static void mostrar(Usuario usuario, GestorUsuarios gestor) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal(usuario, gestor).setVisible(true));
    }
}
