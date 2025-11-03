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
import java.io.File;
import java.io.IOException;
/**
 *
 * @author najma
 */
public class MenuInicio extends JFrame {

    // ===== Recursos en el CLASSPATH (pon las imágenes en src/Interfaz/Imagenes/) =====
    private static final String RECURSO_FONDO_AUTENTICACION = "/Interfaz/Imagenes/Fondo_Login.png";
    private static final String RECURSO_FONDO_MENU          = "/Interfaz/Imagenes/Fondo_Menu.png";
    // ==================================================================================

    private final GestorUsuarios gestorUsuarios;
    private Usuario usuarioEnSesion;

    private BufferedImage imagenAutenticacion;
    private BufferedImage imagenMenu;

    // Panel que pinta el fondo
    private final PanelFondo panelFondo = new PanelFondo();

    // Barra de botones (SE VE SOBRE LA IMAGEN)
    private final JPanel barraBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 18));

    // Overlay modal para formularios (Sign Up / Log In)
    private final JPanel overlay = new JPanel(new GridBagLayout());

    // Referencias de campos para acciones
    private JTextField campoUsuarioSignUp;
    private JPasswordField campoContrasenaSignUp;

    private JTextField campoUsuarioLogin;
    private JPasswordField campoContrasenaLogin;

    private enum EstadoPantalla { AUTENTICACION, MENU }
    private EstadoPantalla estadoActual = EstadoPantalla.AUTENTICACION;

    public MenuInicio(GestorUsuarios gestorUsuarios) {
        super("Vampire Wargame - Menú de Inicio");
        this.gestorUsuarios = gestorUsuarios;
        cargarRecursosGraficos();
        configurarVentanaBase();
        construirEstructura();
        mostrarAutenticacion();
    }

    // ==================== Recursos e inicialización ====================

    private void cargarRecursosGraficos() {
    try {
        java.net.URL urlLogin = MenuInicio.class.getResource(RECURSO_FONDO_AUTENTICACION);
        java.net.URL urlMenu  = MenuInicio.class.getResource(RECURSO_FONDO_MENU);

        // Diagnóstico: imprime lo que encontró
        System.out.println("Login URL: " + urlLogin);
        System.out.println("Menu  URL: " + urlMenu);

        if (urlLogin == null || urlMenu == null) {
            throw new IOException(
                "Recurso(s) no encontrado(s) en el classpath.\n" +
                "Esperado en: src" + RECURSO_FONDO_AUTENTICACION + " y src" + RECURSO_FONDO_MENU);
        }

        imagenAutenticacion = javax.imageio.ImageIO.read(urlLogin);
        imagenMenu = javax.imageio.ImageIO.read(urlMenu);
    } catch (IOException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se pudieron cargar las imágenes de fondo desde el classpath.\n" +
            "Verifica carpeta: src/Interfaz/Imagenes/ y nombres de archivo.\n\n" +
            "Ruta buscada:\n  " + RECURSO_FONDO_AUTENTICACION + "\n  " + RECURSO_FONDO_MENU,
            "Recursos no encontrados", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}

    private void configurarVentanaBase() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        // El frame usa BorderLayout; el centro será el panel de fondo
        setLayout(new BorderLayout());

        // Fondo a pantalla completa
        panelFondo.setLayout(new BorderLayout());
        add(panelFondo, BorderLayout.CENTER);

        // Barra de botones grande y transparente (va SOBRE la imagen)
        barraBotones.setOpaque(false);
        panelFondo.add(barraBotones, BorderLayout.SOUTH);

        // Overlay (modal) encima del fondo
        overlay.setOpaque(false);
        panelFondo.add(overlay, BorderLayout.CENTER);

        // Reescalar al redimensionar
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) { refrescarFondo(); }
        });
    }

    private void construirEstructura() {
        // Nada más aquí; las barras se reconstruyen según el estado
    }

    // ==================== Estados de pantalla ====================

    private void mostrarAutenticacion() {
        estadoActual = EstadoPantalla.AUTENTICACION;
        usuarioEnSesion = null;
        overlay.setVisible(false);
        construirBarraAutenticacion();
        refrescarFondo();
    }

    private void mostrarMenu() {
        estadoActual = EstadoPantalla.MENU;
        overlay.setVisible(false);
        construirBarraMenu();
        refrescarFondo();
    }

    private void refrescarFondo() {
        BufferedImage img = (estadoActual == EstadoPantalla.AUTENTICACION) ? imagenAutenticacion : imagenMenu;
        panelFondo.setImagen(img);
        panelFondo.repaint();
    }

    // ==================== Barras de botones (sobre imagen) ====================

    private void construirBarraAutenticacion() {
        barraBotones.removeAll();
        botonPlanoGrande("Sign Up", e -> mostrarOverlaySignUp(), barraBotones);
        botonPlanoGrande("Log In",  e -> mostrarOverlayLogin(),  barraBotones);
        botonPlanoGrande("Salir",   e -> dispose(),              barraBotones);
        barraBotones.revalidate();
        barraBotones.repaint();
    }

    private void construirBarraMenu() {
        barraBotones.removeAll();

        JLabel etiquetaSesion = new JLabel(
                "Sesión: " + (usuarioEnSesion != null ? usuarioEnSesion.getNombreUsuario() : "—"));
        etiquetaSesion.setForeground(Color.WHITE);
        etiquetaSesion.setFont(etiquetaSesion.getFont().deriveFont(Font.BOLD, 16f));
        barraBotones.add(etiquetaSesion);

        botonPlanoGrande("Jugar partida",     e -> iniciarPartida(),      barraBotones);
        botonPlanoGrande("Estado de cuenta",  e -> mostrarEstadoCuenta(), barraBotones);
        botonPlanoGrande("Cerrar sesión",     e -> mostrarAutenticacion(),barraBotones);
        botonPlanoGrande("Salir",             e -> dispose(),             barraBotones);

        barraBotones.revalidate();
        barraBotones.repaint();
    }

    // ==================== Overlays (formularios) ====================

    private void mostrarOverlaySignUp() {
        JPanel tarjeta = crearTarjetaFormulario(
                "Crear cuenta",
                "Nombre de usuario",
                "Contraseña (mín 6, +1 número, +2 especiales)",
                true,
                e -> ejecutarSignUp(),
                e -> cerrarOverlay()
        );
        abrirOverlayCon(tarjeta);
    }

    private void mostrarOverlayLogin() {
        JPanel tarjeta = crearTarjetaFormulario(
                "Iniciar sesión",
                "Nombre de usuario",
                "Contraseña",
                false,
                e -> ejecutarLogin(),
                e -> cerrarOverlay()
        );
        abrirOverlayCon(tarjeta);
    }

    private void abrirOverlayCon(JPanel contenido) {
        overlay.removeAll();
        overlay.setOpaque(true);
        overlay.setBackground(new Color(0, 0, 0, 110)); // capa oscura transparente
        overlay.add(contenido, new GridBagConstraints());
        overlay.setVisible(true);
        refrescarFondo();
    }

    private void cerrarOverlay() {
        overlay.setVisible(false);
        overlay.setOpaque(false);
        overlay.removeAll();
        overlay.revalidate();
        overlay.repaint();
        refrescarFondo();
    }

    private JPanel crearTarjetaFormulario(String titulo,
                                          String etiquetaUsuarioTxt,
                                          String etiquetaContrasenaTxt,
                                          boolean esSignUp,
                                          ActionListener accionAceptar,
                                          ActionListener accionCancelar) {

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(20, 20, 20, 225));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 120), 1, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setForeground(Color.WHITE);
        tituloLbl.setFont(tituloLbl.getFont().deriveFont(Font.BOLD, 22f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 3;
        tarjeta.add(tituloLbl, c);

        // Usuario
        JLabel lblUsuario = etiquetaPlano(etiquetaUsuarioTxt);
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        tarjeta.add(lblUsuario, c);

        JTextField campoUsuario = new JTextField();
        Dimension tamCampo = new Dimension(260, 30);
        campoUsuario.setPreferredSize(tamCampo);
        campoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoUsuario.setColumns(18);
        c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
        tarjeta.add(campoUsuario, c);

        // Contraseña
        JLabel lblContrasena = etiquetaPlano(etiquetaContrasenaTxt);
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 2;
        tarjeta.add(lblContrasena, c);

        JPasswordField campoContrasena = new JPasswordField();
        campoContrasena.setPreferredSize(tamCampo);
        campoContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoContrasena.setColumns(18);
        c.gridx = 1; c.gridy = 2;
        tarjeta.add(campoContrasena, c);

        // Mostrar / ocultar contraseña
        JCheckBox chkMostrar = new JCheckBox("Mostrar");
        chkMostrar.setOpaque(false);
        chkMostrar.setForeground(Color.WHITE);
        char echoOriginal = campoContrasena.getEchoChar();
        chkMostrar.addActionListener(ev ->
                campoContrasena.setEchoChar(chkMostrar.isSelected() ? (char)0 : echoOriginal));
        c.gridx = 2; c.gridy = 2; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.LINE_START;
        tarjeta.add(chkMostrar, c);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Guardar referencias para acciones
        if (esSignUp) {
            campoUsuarioSignUp = campoUsuario;
            campoContrasenaSignUp = campoContrasena;
        } else {
            campoUsuarioLogin = campoUsuario;
            campoContrasenaLogin = campoContrasena;
        }

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnAceptar  = botonPlanoMediano("Aceptar",  accionAceptar,  panelBotones);
        JButton btnCancelar = botonPlanoMediano("Cancelar", accionCancelar, panelBotones);
        btnAceptar.setBackground(new Color(60, 90, 160));
        btnCancelar.setBackground(new Color(90, 90, 90));

        c.gridx = 0; c.gridy = 3; c.gridwidth = 3;
        tarjeta.add(panelBotones, c);

        return tarjeta;
    }

    // ==================== Acciones ====================

    private void ejecutarSignUp() {
        String nombreUsuario = campoUsuarioSignUp.getText().trim();
        String contrasena = new String(campoContrasenaSignUp.getPassword());

        if (!UtilSeguridad.validarContrasena(contrasena)) {
            JOptionPane.showMessageDialog(this,
                    "Contraseña inválida.\nReglas: mínimo 6, al menos 1 número y 2 caracteres especiales.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean creado = gestorUsuarios.registrarUsuario(nombreUsuario, contrasena);
        if (!creado) {
            JOptionPane.showMessageDialog(this, "No se pudo crear. ¿Nombre ya existe?", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cerrarOverlay();
        JOptionPane.showMessageDialog(this, "Cuenta creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        // podrías refrescar algo visual aquí si quieres
    }

    private void ejecutarLogin() {
        String nombreUsuario = campoUsuarioLogin.getText().trim();
        String contrasena = new String(campoContrasenaLogin.getPassword());

        Usuario usuario = gestorUsuarios.iniciarSesion(nombreUsuario, contrasena);
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Nombre o contraseña incorrectos.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.usuarioEnSesion = usuario;
        cerrarOverlay();
        mostrarMenu();
    }

    private void iniciarPartida() {
        if (usuarioEnSesion == null) {
        JOptionPane.showMessageDialog(this, "Inicia sesión primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }
    VentanaJuego v = new VentanaJuego(); // luego le pasamos Juego/Usuario
    v.setVisible(true);
    }

    private void mostrarEstadoCuenta() {
        if (usuarioEnSesion == null) {
            JOptionPane.showMessageDialog(this, "No hay sesión activa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        EstadisticaCuenta est = usuarioEnSesion.getEstadistica();
        String msg = "Usuario: " + usuarioEnSesion.getNombreUsuario() + "\n" +
                     "Ganadas: " + est.getPartidasGanadas() + "\n" +
                     "Perdidas: " + est.getPartidasPerdidas() + "\n" +
                     "Victorias: " + String.format("%.1f", est.getPorcentajeVictorias()) + "%";
        JOptionPane.showMessageDialog(this, msg, "Estado de cuenta", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== Utilidades de estilo ====================

    private JLabel etiquetaPlano(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    /** Botón grande para la barra inferior (sobre la imagen). */
    private JButton botonPlanoGrande(String texto, ActionListener accion, Container contenedor) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(50, 70, 120));
        b.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.addActionListener(accion);
        if (contenedor != null) contenedor.add(b);
        return b;
    }

    /** Botón mediano para formularios (Aceptar/Cancelar). */
    private JButton botonPlanoMediano(String texto, ActionListener accion, Container contenedor) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(50, 70, 120));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.addActionListener(accion);
        if (contenedor != null) contenedor.add(b);
        return b;
    }

    // ==================== Panel que pinta el fondo ====================

    /** Panel custom que pinta la imagen escalada al tamaño actual. */
    static class PanelFondo extends JPanel {
        private BufferedImage imagen;
        public void setImagen(BufferedImage imagen) { this.imagen = imagen; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
            }
        }
    }

    // ==================== Main de prueba ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IRepositorioUsuarios repo = new RepositorioUsuariosArchivo("usuarios.csv");
            GestorUsuarios gestor = new GestorUsuarios(repo);
            MenuInicio ventana = new MenuInicio(gestor);
            ventana.setVisible(true);
        });
    }
}