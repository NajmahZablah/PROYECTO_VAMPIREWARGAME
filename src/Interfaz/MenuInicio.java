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
public class MenuInicio extends JFrame {
    
    private final GestorUsuarios gestorUsuarios;
    private BufferedImage fondoLogin;
    
    public MenuInicio(GestorUsuarios gestor) {
        super("Vampire Wargame");
        this.gestorUsuarios = gestor;
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
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
                        0, 0, new Color(15, 25, 45),
                        0, getHeight(), new Color(35, 50, 85)
                    );
                    g2.setPaint(gradient);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        panelPrincipal.setLayout(null);
        
        // Título centrado arriba
        JLabel lblTitulo = new JLabel("Vampire Wargame", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 56));
        lblTitulo.setForeground(new Color(240, 240, 255));
        lblTitulo.setBounds(200, 70, 600, 70);
        panelPrincipal.add(lblTitulo);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Autenticación", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtitulo.setForeground(new Color(180, 190, 210));
        lblSubtitulo.setBounds(200, 145, 600, 30);
        panelPrincipal.add(lblSubtitulo);
        
        // Botones a la IZQUIERDA
        int botonWidth = 430;
        int botonHeight = 70;
        int xIzquierda = 40; // Posición a la izquierda
        
        JButton btnLogin = crearBotonAzul("LOG IN");
        btnLogin.setBounds(xIzquierda, 280, botonWidth, botonHeight);
        btnLogin.addActionListener(e -> abrirLogin());
        panelPrincipal.add(btnLogin);
        
        JButton btnCrear = crearBotonAzul("CREAR PLAYER");
        btnCrear.setBounds(xIzquierda, 370, botonWidth, botonHeight);
        btnCrear.addActionListener(e -> abrirCrearPlayer());
        panelPrincipal.add(btnCrear);
        
        JButton btnSalir = crearBotonRojo("SALIR");
        btnSalir.setBounds(xIzquierda, 460, botonWidth, botonHeight);
        btnSalir.addActionListener(e -> System.exit(0));
        panelPrincipal.add(btnSalir);
        
        setContentPane(panelPrincipal);
    }
    
    private JButton crearBotonAzul(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setBackground(new Color(50, 80, 140));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
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
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setBackground(new Color(140, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
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
    
    /* ==================== LOG IN CON FONDO OPACO Y MOSTRAR PASSWORD ==================== */
    
    private void abrirLogin() {
        // Crear diálogo con fondo opaco
        JDialog dialogo = new JDialog(this, "Iniciar Sesión", true);
        dialogo.setSize(520, 320);
        dialogo.setLocationRelativeTo(this);
        dialogo.setUndecorated(true);
        dialogo.setBackground(new Color(0, 0, 0, 0));
        
        // Panel con fondo semi-transparente
        JPanel panelContenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 40, 60, 230)); // Semi-transparente
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelContenido.setOpaque(false);
        panelContenido.setLayout(null);
        panelContenido.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 2));
        
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 520, 35);
        panelContenido.add(lblTitulo);
        
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 16));
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setBounds(60, 90, 100, 30);
        panelContenido.add(lblUsuario);
        
        JTextField txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsuario.setBounds(170, 90, 290, 35);
        panelContenido.add(txtUsuario);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(60, 145, 100, 30);
        panelContenido.add(lblPassword);
        
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        txtPassword.setBounds(170, 145, 290, 35);
        panelContenido.add(txtPassword);
        
        // Checkbox para mostrar contraseña
        JCheckBox chkMostrar = new JCheckBox("Mostrar");
        chkMostrar.setFont(new Font("Arial", Font.PLAIN, 13));
        chkMostrar.setForeground(Color.WHITE);
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.setBounds(170, 185, 100, 25);
        chkMostrar.addActionListener(e -> {
            if (chkMostrar.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        panelContenido.add(chkMostrar);
        
        // Botones
        JButton btnOk = crearBotonAzul("OK");
        btnOk.setBounds(100, 235, 130, 45);
        btnOk.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Completa todos los campos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Usuario usuarioLogueado = gestorUsuarios.iniciarSesion(usuario, password);
            if (usuarioLogueado != null) {
                dialogo.dispose();
                MenuPrincipal.mostrar(usuarioLogueado, gestorUsuarios);
                dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, 
                    "Usuario o contraseña incorrectos", 
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelContenido.add(btnOk);
        
        JButton btnCancelar = crearBotonRojo("Cancelar");
        btnCancelar.setBounds(290, 235, 130, 45);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelContenido.add(btnCancelar);
        
        dialogo.setContentPane(panelContenido);
        dialogo.setVisible(true);
    }
    
    /* ==================== CREAR PLAYER CON FONDO OPACO Y MOSTRAR PASSWORD ==================== */
    
    private void abrirCrearPlayer() {
        JDialog dialogo = new JDialog(this, "Crear Nueva Cuenta", true);
        dialogo.setSize(560, 450);
        dialogo.setLocationRelativeTo(this);
        dialogo.setUndecorated(true);
        dialogo.setBackground(new Color(0, 0, 0, 0));
        
        // Panel con fondo semi-transparente
        JPanel panelContenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(30, 40, 60, 230)); // Semi-transparente
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panelContenido.setOpaque(false);
        panelContenido.setLayout(null);
        panelContenido.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 180), 2));
        
        JLabel lblTitulo = new JLabel("Crear Nueva Cuenta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 25, 560, 35);
        panelContenido.add(lblTitulo);
        
        // Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 16));
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setBounds(70, 90, 120, 30);
        panelContenido.add(lblUsuario);
        
        JTextField txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsuario.setBounds(200, 90, 300, 35);
        panelContenido.add(txtUsuario);
        
        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(70, 150, 120, 30);
        panelContenido.add(lblPassword);
        
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        txtPassword.setBounds(200, 150, 300, 35);
        panelContenido.add(txtPassword);
        
        // Confirmar
        JLabel lblConfirmar = new JLabel("Confirmar:");
        lblConfirmar.setFont(new Font("Arial", Font.BOLD, 16));
        lblConfirmar.setForeground(Color.WHITE);
        lblConfirmar.setBounds(70, 210, 120, 30);
        panelContenido.add(lblConfirmar);
        
        JPasswordField txtConfirmar = new JPasswordField();
        txtConfirmar.setFont(new Font("Arial", Font.PLAIN, 15));
        txtConfirmar.setBounds(200, 210, 300, 35);
        panelContenido.add(txtConfirmar);
        
        // Checkbox para mostrar contraseña
        JCheckBox chkMostrar = new JCheckBox("Mostrar");
        chkMostrar.setFont(new Font("Arial", Font.PLAIN, 13));
        chkMostrar.setForeground(Color.WHITE);
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.setBounds(200, 250, 100, 25);
        chkMostrar.addActionListener(e -> {
            char echoChar = chkMostrar.isSelected() ? (char) 0 : '•';
            txtPassword.setEchoChar(echoChar);
            txtConfirmar.setEchoChar(echoChar);
        });
        panelContenido.add(chkMostrar);
        
        // Info
        JLabel lblInfo = new JLabel("<html><center>Password: 5 caracteres<br>1 dígito, 1 especial</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(200, 200, 220));
        lblInfo.setBounds(200, 275, 300, 35);
        panelContenido.add(lblInfo);
        
        // Botones
        JButton btnCrear = crearBotonAzul("Crear");
        btnCrear.setBounds(135, 350, 130, 50);
        btnCrear.addActionListener(e -> {
            String usuario = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            
            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Completa todos los campos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmar)) {
                JOptionPane.showMessageDialog(dialogo, "Las contraseñas no coinciden", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (gestorUsuarios.registrarUsuario(usuario, password)) {
                JOptionPane.showMessageDialog(dialogo, "¡Cuenta creada exitosamente!", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                Usuario nuevoUsuario = gestorUsuarios.iniciarSesion(usuario, password);
                if (nuevoUsuario != null) {
                    dialogo.dispose();
                    MenuPrincipal.mostrar(nuevoUsuario, gestorUsuarios);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, 
                    "Error al crear cuenta.\nVerifica que el usuario no exista\n" +
                    "y que el password cumpla los requisitos.", 
                    "Error de Registro", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelContenido.add(btnCrear);
        
        JButton btnCancelar = crearBotonRojo("Cancelar");
        btnCancelar.setBounds(295, 350, 130, 50);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelContenido.add(btnCancelar);
        
        dialogo.setContentPane(panelContenido);
        dialogo.setVisible(true);
    }
    
    /* ==================== MAIN ==================== */
    
    public static void main(String[] args) {
        GestorUsuarios gestor = new GestorUsuarios(
            new Cuentas.RepositorioUsuariosMemoria()
        );
        
        SwingUtilities.invokeLater(() -> new MenuInicio(gestor).setVisible(true));
    }
}