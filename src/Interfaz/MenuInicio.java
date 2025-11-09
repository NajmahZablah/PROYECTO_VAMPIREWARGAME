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


/**
 *
 * @author najma
 */
/* ============================================================================
   ARCHIVO 1: MenuInicio.java
   Maneja LOGIN y CREAR PLAYER
   ============================================================================ */
public class MenuInicio extends JFrame {
    
    private final GestorUsuarios gestorUsuarios;
    private BufferedImage fondoLogin;
    
    public MenuInicio(GestorUsuarios gestor) {
        super("Vampire Wargame");
        this.gestorUsuarios = gestor;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        cargarFondo();
        inicializarComponentes();
    }
    
    private void cargarFondo() {
        try {
            fondoLogin = ImageIO.read(new File("src/Interfaz/Imagenes/Fondo_Login.png"));
        } catch (Exception e) {
            try {
                fondoLogin = ImageIO.read(new File("Interfaz/Imagenes/Fondo_Login.png"));
            } catch (Exception e2) {
                System.err.println("No se pudo cargar Fondo_Login.png");
            }
        }
    }
    
    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondoLogin != null) {
                    g.drawImage(fondoLogin, 0, 0, getWidth(), getHeight(), null);
                } else {
                    Graphics2D g2 = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(15, 20, 40),
                        0, getHeight(), new Color(40, 50, 90)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panelPrincipal.setLayout(null);
        
        // Título
        JLabel lblTitulo = new JLabel("Vampire Wargame");
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 42));
        lblTitulo.setForeground(new Color(220, 220, 255));
        lblTitulo.setBounds(100, 40, 400, 50);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Autenticación");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(180, 180, 200));
        lblSubtitulo.setBounds(100, 95, 400, 20);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblSubtitulo);
        
        // Botones
        int botonWidth = 300;
        int botonHeight = 60;
        int xCentro = (600 - botonWidth) / 2;
        
        JButton btnLogin = crearBoton("LOG IN", new Color(60, 100, 180));
        btnLogin.setBounds(xCentro, 180, botonWidth, botonHeight);
        btnLogin.addActionListener(e -> abrirLogin());
        panelPrincipal.add(btnLogin);
        
        JButton btnCrear = crearBoton("CREAR PLAYER", new Color(80, 140, 100));
        btnCrear.setBounds(xCentro, 260, botonWidth, botonHeight);
        btnCrear.addActionListener(e -> abrirCrearPlayer());
        panelPrincipal.add(btnCrear);
        
        JButton btnSalir = crearBoton("SALIR", new Color(150, 60, 60));
        btnSalir.setBounds(xCentro, 340, botonWidth, botonHeight);
        btnSalir.addActionListener(e -> System.exit(0));
        panelPrincipal.add(btnSalir);
        
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
    
    /* ==================== LOG IN ==================== */
    
    private void abrirLogin() {
        JTextField txtUsuario = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, 
            "Iniciar Sesión", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String usuario = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Debes completar todos los campos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Usuario usuarioLogueado = gestorUsuarios.iniciarSesion(usuario, password);
            
            if (usuarioLogueado != null) {
                JOptionPane.showMessageDialog(this, 
                    "¡Bienvenido " + usuarioLogueado.getNombreUsuario() + "!", 
                    "Inicio Exitoso", JOptionPane.INFORMATION_MESSAGE);
                
                // Abrir menú principal
                MenuPrincipal.mostrar(usuarioLogueado, gestorUsuarios);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Usuario o contraseña incorrectos.\n" +
                    "Verifica que la cuenta esté activa.", 
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /* ==================== CREAR PLAYER ==================== */
    
    private void abrirCrearPlayer() {
        JTextField txtUsuario = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JPasswordField txtConfirmar = new JPasswordField(20);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Usuario (único):"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Password (5 caracteres):"));
        panel.add(txtPassword);
        panel.add(new JLabel("Confirmar Password:"));
        panel.add(txtConfirmar);
        panel.add(new JLabel(""));
        
        JLabel lblInfo = new JLabel("<html><small>Password debe tener exactamente 5 caracteres,<br>" +
                                     "con al menos 1 dígito y 1 carácter especial</small></html>");
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo);
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, 
            "Crear Nueva Cuenta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (resultado == JOptionPane.OK_OPTION) {
            String usuario = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            
            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Debes completar todos los campos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmar)) {
                JOptionPane.showMessageDialog(this, 
                    "Las contraseñas no coinciden", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (gestorUsuarios.registrarUsuario(usuario, password)) {
                JOptionPane.showMessageDialog(this, 
                    "¡Cuenta creada exitosamente!\n" +
                    "Usuario: " + usuario + "\n" +
                    "Ahora puedes iniciar sesión.", 
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                
                // Auto login
                Usuario nuevoUsuario = gestorUsuarios.iniciarSesion(usuario, password);
                if (nuevoUsuario != null) {
                    MenuPrincipal.mostrar(nuevoUsuario, gestorUsuarios);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo crear la cuenta.\n\n" +
                    "Posibles razones:\n" +
                    "• El nombre de usuario ya existe\n" +
                    "• El password no cumple los requisitos\n" +
                    "  (5 caracteres, 1 dígito, 1 especial)", 
                    "Error de Registro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /* ==================== MAIN ==================== */
    
    public static void main(String[] args) {
        // Inicializar gestor de usuarios (puedes usar Archivo o Memoria)
        GestorUsuarios gestor = new GestorUsuarios(
            new Cuentas.RepositorioUsuariosArchivo("usuarios.csv")
            // O para pruebas: new Cuentas.RepositorioUsuariosMemoria()
        );
        
        SwingUtilities.invokeLater(() -> {
            MenuInicio menu = new MenuInicio(gestor);
            menu.setVisible(true);
        });
    }
}