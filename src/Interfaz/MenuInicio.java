/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Cuentas.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author najma
 */
public class MenuInicio extends JFrame {

    // ====== Rutas de recursos (coloca tus imágenes en src/Interfaz/Imagenes/) ======
    private static final String FONDO_LOGIN = "/Interfaz/Imagenes/Fondo_Login.png";
    private static final String FONDO_MENU  = "/Interfaz/Imagenes/Fondo_Menu.png";
    private static final String LOGO        = "/Interfaz/Imagenes/logo.png";   // opcional
    // ===============================================================================

    private final GestorUsuarios gestorUsuarios;
    private Usuario usuarioEnSesion;

    // Imágenes
    private BufferedImage imagenLogin, imagenMenu, imagenLogo;

    // Contenedores principales
    private final PanelFondo panelFondo = new PanelFondo();          // pinta imagen a pantalla completa
    private final JPanel capaOverlay    = new JPanel(new BorderLayout()); // **glass pane**
    private final JPanel contenedorIzquierdo   = new JPanel(new BorderLayout());
    private final JPanel zonaBotoneraInferior  = new JPanel(new BorderLayout());
    private final JPanel columnaBotones        = new JPanel();

    // Layout / estilo
    private final int anchoColumna = 360;

    // Campos de formularios
    private JTextField campoUsuarioSignUp, campoUsuarioLogin;
    private JPasswordField campoPasswordSignUp, campoPasswordLogin;

    private enum Estado { AUTENTICACION, MENU }
    private Estado estadoActual = Estado.AUTENTICACION;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public MenuInicio(GestorUsuarios gestorUsuarios) {
        super("Vampire Wargame");
        this.gestorUsuarios = gestorUsuarios;

        cargarRecursos();
        configurarVentana();
        construirColumnaIzquierda();
        mostrarAutenticacion();

        // Si cambian dimensiones y hay overlay, que se recalcule al instante
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (capaOverlay.isVisible()) {
                    capaOverlay.revalidate();
                    capaOverlay.repaint();
                }
            }
        });
    }

    // =========================================================
    // Carga de imágenes
    // =========================================================
    private void cargarRecursos() {
        try {
            URL urlLogin = getClass().getResource(FONDO_LOGIN);
            URL urlMenu  = getClass().getResource(FONDO_MENU);
            if (urlLogin != null) imagenLogin = ImageIO.read(urlLogin);
            if (urlMenu  != null) imagenMenu  = ImageIO.read(urlMenu);
        } catch (IOException ignored) {}

        try {
            URL urlLogo = getClass().getResource(LOGO);
            if (urlLogo != null) imagenLogo = ImageIO.read(urlLogo);
        } catch (IOException ignored) {}
    }

    // =========================================================
    // Estructura principal
    // =========================================================
    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 720));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Fondo a pantalla completa
        panelFondo.setLayout(new BorderLayout());
        add(panelFondo, BorderLayout.CENTER);

        // Columna izquierda (logo arriba / botones abajo)
        contenedorIzquierdo.setOpaque(false);
        contenedorIzquierdo.setPreferredSize(new Dimension(anchoColumna, 1));
        panelFondo.add(contenedorIzquierdo, BorderLayout.WEST);

        zonaBotoneraInferior.setOpaque(false);
        contenedorIzquierdo.add(zonaBotoneraInferior, BorderLayout.SOUTH);

        columnaBotones.setOpaque(false);
        columnaBotones.setLayout(new BoxLayout(columnaBotones, BoxLayout.Y_AXIS));
        columnaBotones.setBorder(BorderFactory.createEmptyBorder(16, 20, 24, 20));
        zonaBotoneraInferior.add(columnaBotones, BorderLayout.CENTER);

        // ===== Overlay como GLASS PANE (clave para evitar duplicados) =====
        capaOverlay.setOpaque(false);
        capaOverlay.setLayout(new BorderLayout());
        setGlassPane(capaOverlay);
        // ==================================================================

        // Redibuja fondo al redimensionar
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { refrescarFondo(); }
        });
    }

    private void construirColumnaIzquierda() {
        // Panel para logo / título
        JPanel panelLogo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                if (imagenLogo != null) {
                    int maxAncho = anchoColumna - 40;
                    int nuevoAncho = Math.min(maxAncho, imagenLogo.getWidth());
                    int nuevoAlto  = (int)((double) imagenLogo.getHeight() * (nuevoAncho / (double) imagenLogo.getWidth()));
                    g.drawImage(imagenLogo.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH), 20, 20, null);
                    setPreferredSize(new Dimension(anchoColumna, nuevoAlto + 40));
                } else {
                    setPreferredSize(new Dimension(anchoColumna, 110));
                    g.setColor(new Color(245,245,245,230));
                    g.setFont(getFont().deriveFont(Font.BOLD, 24f));
                    g.drawString("Vampire Wargame", 20, 70);
                }
            }
        };
        contenedorIzquierdo.add(panelLogo, BorderLayout.NORTH);
    }

    private void refrescarFondo() {
        panelFondo.setImagen((estadoActual == Estado.AUTENTICACION) ? imagenLogin : imagenMenu);
    }

    // =========================================================
    // Estados
    // =========================================================
    private void mostrarAutenticacion() {
        estadoActual = Estado.AUTENTICACION;
        usuarioEnSesion = null;
        construirBotoneraAutenticacion();
        cerrarOverlay();
        refrescarFondo();
    }

    private void mostrarMenu() {
        estadoActual = Estado.MENU;
        construirBotoneraMenuPrincipal();
        cerrarOverlay();
        refrescarFondo();
    }

    // =========================================================
    // Botoneras
    // =========================================================
    private void construirBotoneraAutenticacion() {
        columnaBotones.removeAll();
        columnaBotones.add(botonPrincipal("Sign Up", e -> abrirOverlaySignUp()));
        columnaBotones.add(Box.createVerticalStrut(12));
        columnaBotones.add(botonPrincipal("Log In",  e -> abrirOverlayLogin()));
        columnaBotones.add(Box.createVerticalStrut(12));
        columnaBotones.add(botonPrincipal("Salir",   e -> dispose()));
        columnaBotones.revalidate();
        columnaBotones.repaint();
    }

    private void construirBotoneraMenuPrincipal() {
        columnaBotones.removeAll();

        // 1) Jugar
        columnaBotones.add(botonPrincipal("JUGAR VAMPIRE WARGAME", e -> iniciarPartida()));
        columnaBotones.add(Box.createVerticalStrut(18));

        // 2) Mi Cuenta  -> abre overlay con opciones
        columnaBotones.add(botonPrincipal("MI CUENTA", e -> abrirOverlayMiCuenta()));
        columnaBotones.add(Box.createVerticalStrut(18));

        // 3) Reportes   -> abre overlay con opciones
        columnaBotones.add(botonPrincipal("REPORTES", e -> abrirOverlayReportes()));
        columnaBotones.add(Box.createVerticalStrut(18));

        // 4) Log Out
        columnaBotones.add(botonPrincipal("LOG OUT", e -> mostrarAutenticacion()));

        columnaBotones.revalidate();
        columnaBotones.repaint();
    }

    // =========================================================
    // Overlays (modales)
    // =========================================================

    /** Botón azul para acciones principales en diálogos. */
    private JButton botonPlano(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(60, 90, 160));
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return boton;
    }

    /** Botón gris (Cancelar / secundario). */
    private JButton botonGris(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(90, 90, 90));
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return boton;
    }

    private void abrirOverlaySignUp() {
        JPanel tarjeta = crearTarjetaBasica("Crear cuenta");

        GridBagConstraints r = baseGrid();
        JLabel lUser = etiqueta("Nombre de usuario");
        campoUsuarioSignUp = new JTextField(); campoUsuarioSignUp.setPreferredSize(new Dimension(260,30));
        JLabel lPass = etiqueta("Contraseña (exactamente 5, ≥1 número, ≥1 especial)");
        campoPasswordSignUp = crearCampoPassword();

        r.gridx=0; r.gridy=1; tarjeta.add(lUser, r);
        r.gridx=1; r.gridy=1; tarjeta.add(campoUsuarioSignUp, r);
        r.gridx=0; r.gridy=2; tarjeta.add(lPass, r);
        r.gridx=1; r.gridy=2; tarjeta.add(campoPasswordSignUp, r);

        JCheckBox ver = crearCheckMostrar(campoPasswordSignUp);
        r.gridx=1; r.gridy=3; r.anchor=GridBagConstraints.LINE_START;
        tarjeta.add(ver, r);

        JPanel acciones = filaDerecha(
                botonPlano("Aceptar", e -> ejecutarSignUp()),
                botonGris("Cancelar", e -> cerrarOverlay())
        );
        r.gridx=0; r.gridy=4; r.gridwidth=2;
        tarjeta.add(acciones, r);

        abrirOverlay(tarjeta);
    }

    private void abrirOverlayLogin() {
        JPanel tarjeta = crearTarjetaBasica("Iniciar sesión");

        GridBagConstraints r = baseGrid();
        JLabel lUser = etiqueta("Nombre de usuario");
        campoUsuarioLogin = new JTextField(); campoUsuarioLogin.setPreferredSize(new Dimension(260,30));
        JLabel lPass = etiqueta("Contraseña");
        campoPasswordLogin = crearCampoPassword();

        r.gridx=0; r.gridy=1; tarjeta.add(lUser, r);
        r.gridx=1; r.gridy=1; tarjeta.add(campoUsuarioLogin, r);
        r.gridx=0; r.gridy=2; tarjeta.add(lPass, r);
        r.gridx=1; r.gridy=2; tarjeta.add(campoPasswordLogin, r);

        JCheckBox ver = crearCheckMostrar(campoPasswordLogin);
        r.gridx=1; r.gridy=3; r.anchor=GridBagConstraints.LINE_START;
        tarjeta.add(ver, r);

        JPanel acciones = filaDerecha(
                botonPlano("Aceptar", e -> ejecutarLogin()),
                botonGris("Cancelar", e -> cerrarOverlay())
        );
        r.gridx=0; r.gridy=4; r.gridwidth=2;
        tarjeta.add(acciones, r);

        abrirOverlay(tarjeta);
    }

    /** Overlay para cambiar el password. El check 'Mostrar' aplica a AMBOS campos. */
    private void abrirOverlayCambiarPassword() {
        JPanel tarjeta = crearTarjetaBasica("Cambiar password");

        GridBagConstraints r = baseGrid();

        JLabel lAct = etiqueta("Password actual");
        JPasswordField campoAct = crearCampoPassword();

        JLabel lNew = etiqueta("Password nueva (exactamente 5, ≥1 número, ≥1 especial)");
        JPasswordField campoNew = crearCampoPassword();

        r.gridx=0; r.gridy=1; tarjeta.add(lAct, r);
        r.gridx=1;           tarjeta.add(campoAct, r);

        r.gridx=0; r.gridy=2; tarjeta.add(lNew, r);
        r.gridx=1;           tarjeta.add(campoNew, r);

        JCheckBox ver = new JCheckBox("Mostrar");
        ver.setOpaque(false); ver.setForeground(Color.WHITE);
        final char ecoA = campoAct.getEchoChar();
        final char ecoN = campoNew.getEchoChar();
        ver.addActionListener(e -> {
            boolean s = ver.isSelected();
            campoAct.setEchoChar(s ? (char)0 : ecoA);
            campoNew.setEchoChar(s ? (char)0 : ecoN);
        });
        r.gridx=1; r.gridy=3; r.anchor=GridBagConstraints.LINE_START;
        tarjeta.add(ver, r);

        JPanel acciones = filaDerecha(
                botonPlano("Aceptar", e -> {
                    String actual = new String(campoAct.getPassword());
                    String nueva  = new String(campoNew.getPassword());
                    if (!UtilSeguridad.validarContrasena(nueva)) {
                        advertencia("La contraseña nueva debe tener EXACTAMENTE 5 caracteres, con al menos 1 número y 1 caracter especial.");
                        return;
                    }
                    boolean ok = gestorUsuarios.cambiarPassword(usuarioEnSesion, actual, nueva);
                    if (ok) { cerrarOverlay(); informacion("Password actualizado."); }
                    else    { error("Password actual incorrecto o no cumple la regla."); }
                }),
                botonGris("Cancelar", e -> cerrarOverlay())
        );
        r.gridx=0; r.gridy=4; r.gridwidth=2; r.anchor=GridBagConstraints.CENTER;
        tarjeta.add(acciones, r);

        abrirOverlay(tarjeta);
    }

    /** Overlay con opciones de Mi Cuenta. */
    private void abrirOverlayMiCuenta() {
        JPanel tarjeta = crearTarjetaBasica("Mi cuenta");

        GridBagConstraints c = baseGrid();
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;

        tarjeta.add(botonOpcionOverlay("Ver mi información",
                e -> { cerrarOverlay(); verMiInformacion(); }), c);

        c.gridy++;
        tarjeta.add(botonOpcionOverlay("Cambiar password",
                e -> abrirOverlayCambiarPassword()), c);

        c.gridy++;
        tarjeta.add(botonOpcionOverlay("Cerrar mi cuenta",
                e -> { cerrarOverlay(); confirmarEliminarCuenta(); }), c);

        c.gridy++;
        JPanel acciones = filaDerecha(botonGris("Cerrar", e -> cerrarOverlay()));
        tarjeta.add(acciones, c);

        abrirOverlay(tarjeta);
    }

    /** Overlay con opciones de Reportes. */
    private void abrirOverlayReportes() {
        JPanel tarjeta = crearTarjetaBasica("Reportes");

        GridBagConstraints c = baseGrid();
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL;

        tarjeta.add(botonOpcionOverlay("Ranking jugadores",
                e -> { cerrarOverlay(); mostrarRanking(); }), c);

        c.gridy++;
        tarjeta.add(botonOpcionOverlay("Logs de mis últimos juegos",
                e -> { cerrarOverlay(); mostrarMisLogs(); }), c);

        c.gridy++;
        JPanel acciones = filaDerecha(botonGris("Cerrar", e -> cerrarOverlay()));
        tarjeta.add(acciones, c);

        abrirOverlay(tarjeta);
    }

    // =========================================================
    // Acciones
    // =========================================================
    private void ejecutarSignUp() {
        String nombre = campoUsuarioSignUp.getText().trim();
        String pass   = new String(campoPasswordSignUp.getPassword());
        if (!UtilSeguridad.validarContrasena(pass)) {
            advertencia("Contraseña inválida: EXACTAMENTE 5 caracteres, con al menos 1 número y 1 caracter especial.");
            return;
        }
        if (!gestorUsuarios.registrarUsuario(nombre, pass)) {
            error("No se pudo crear la cuenta (¿usuario ya existe?).");
            return;
        }
        cerrarOverlay();
        informacion("Cuenta creada con éxito.");
    }

    private void ejecutarLogin() {
        String nombre = campoUsuarioLogin.getText().trim();
        String pass   = new String(campoPasswordLogin.getPassword());
        Usuario u = gestorUsuarios.iniciarSesion(nombre, pass);
        if (u == null) {
            error("Nombre o contraseña incorrectos.");
            return;
        }
        usuarioEnSesion = u;
        mostrarMenu();
    }

    private void iniciarPartida() {
        if (usuarioEnSesion == null) { advertencia("Primero inicia sesión."); return; }
        // TODO: abre tu ventana del juego/tablero
        informacion("Aquí abriría la Ventana de Juego.");
    }

    private void verMiInformacion() {
        if (usuarioEnSesion == null) return;
        EstadisticaCuenta est = usuarioEnSesion.getEstadistica();
        String info = "Usuario: " + usuarioEnSesion.getNombreUsuario()
                + "\nIngreso: " + usuarioEnSesion.getFechaIngresoTexto()
                + "\nPuntos: " + usuarioEnSesion.getPuntos()
                + "\nGanadas: " + est.getPartidasGanadas()
                + "\nPerdidas: " + est.getPartidasPerdidas()
                + "\nVictorias: " + String.format("%.1f%%", est.getPorcentajeVictorias());
        JOptionPane.showMessageDialog(this, info, "Mi información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarEliminarCuenta() {
        if (usuarioEnSesion == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar permanentemente tu cuenta?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            gestorUsuarios.eliminarUsuario(usuarioEnSesion);
            usuarioEnSesion = null;
            mostrarAutenticacion();
            informacion("Cuenta eliminada.");
        }
    }

    private void mostrarRanking() {
        List<Usuario> ranking = gestorUsuarios.obtenerRankingPorPuntos();
        StringBuilder sb = new StringBuilder("Ranking\n");
        int pos = 1;
        for (Usuario u : ranking) {
            sb.append(pos++).append(") ").append(u.getNombreUsuario())
              .append(" - ").append(u.getPuntos()).append(" pts\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Ranking", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarMisLogs() {
        if (usuarioEnSesion == null) return;
        List<String> logs = gestorUsuarios.obtenerLogs(usuarioEnSesion.getNombreUsuario(), 30);
        JOptionPane.showMessageDialog(this,
                logs.isEmpty() ? "(Sin registros)" : String.join("\n", logs),
                "Mis últimos juegos", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // Helpers UI
    // =========================================================
    private JButton botonPrincipal(String texto, ActionListener accion) {
        JButton b = new JButton(texto);
        b.addActionListener(accion);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(anchoColumna - 40, 46));
        b.setPreferredSize(new Dimension(anchoColumna - 40, 46));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(50, 70, 120));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBorder(BorderFactory.createEmptyBorder(10,18,10,18));
        return b;
    }

    private JButton botonOpcionOverlay(String texto, ActionListener accion) {
        JButton b = new JButton(texto);
        b.addActionListener(accion);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(60, 90, 160));
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(340, 40));
        return b;
    }

    private JPanel crearTarjetaBasica(String titulo) {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(20,20,20,230));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90,90,120), 1, true),
                BorderFactory.createEmptyBorder(18,20,18,20)
        ));
        GridBagConstraints r = baseGrid();
        JLabel t = new JLabel(titulo);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 22f));
        r.gridx=0; r.gridy=0; r.gridwidth=2;
        tarjeta.add(t, r);
        return tarjeta;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JPasswordField crearCampoPassword() {
        JPasswordField campo = new JPasswordField();
        campo.setPreferredSize(new Dimension(260,30));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return campo;
    }

    private JCheckBox crearCheckMostrar(JPasswordField campo) {
        JCheckBox check = new JCheckBox("Mostrar");
        check.setOpaque(false); check.setForeground(Color.WHITE);
        char eco = campo.getEchoChar();
        check.addActionListener(e -> campo.setEchoChar(check.isSelected() ? (char)0 : eco));
        return check;
    }

    private JPanel filaDerecha(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        for (JButton b : botones) panel.add(b);
        return panel;
    }

    private GridBagConstraints baseGrid() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    // ===== Glass Pane Overlay =====
    /** Muestra overlay centrado usando el glass pane (sin duplicados). */
    private void abrirOverlay(JPanel contenido) {
        // panel que pinta el difuminado
        JPanel capa = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 140));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        capa.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.CENTER;
        capa.add(contenido, c);

        capaOverlay.removeAll();
        capaOverlay.add(capa, BorderLayout.CENTER);
        capaOverlay.setVisible(true);

        capaOverlay.revalidate();
        capaOverlay.repaint();
        SwingUtilities.invokeLater(contenido::requestFocusInWindow);
    }

    private void cerrarOverlay() {
        capaOverlay.setVisible(false);
        capaOverlay.removeAll();
        capaOverlay.revalidate();
        capaOverlay.repaint();
    }

    private void informacion(String texto) { JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String texto)       { JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE); }
    private void advertencia(String texto) { JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.WARNING_MESSAGE); }

    /** Panel que dibuja la imagen de fondo escalada al tamaño de la ventana. */
    static class PanelFondo extends JPanel {
        private BufferedImage imagen;
        void setImagen(BufferedImage imagen) { this.imagen = imagen; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
            }
        }
    }

    // ====================== MAIN de prueba ======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Usa repositorio en memoria para probar rápido
            IRepositorioUsuarios repo = new RepositorioUsuariosMemoria();
            GestorUsuarios gestor = new GestorUsuarios(repo);

            MenuInicio ui = new MenuInicio(gestor);
            ui.setExtendedState(JFrame.MAXIMIZED_BOTH);   // abrir maximizado
            ui.setVisible(true);
        });
    }
}