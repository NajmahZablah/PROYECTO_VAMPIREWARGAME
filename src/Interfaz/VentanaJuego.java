/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

import Cuentas.GestorUsuarios;
import Cuentas.Usuario;
import Modelo.ColorJugador;
import Modelo.TipoPieza;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 *
 * @author najma
 */
public class VentanaJuego extends JFrame {

    private static final int N = 6;

    private static class Celda {
        TipoPieza tipo;
        ColorJugador color;
        int vida;
        int escudo;
        
        Celda(TipoPieza t, ColorJugador c) {
            tipo = t;
            color = c;
            switch (t) {
                case HOMBRE_LOBO:
                    vida = 6; escudo = 3; break;
                case VAMPIRO:
                    vida = 4; escudo = 5; break;
                case NIGROMANTE:
                    vida = 5; escudo = 2; break;
                case ZOMBIE:
                    vida = 1; escudo = 0; break;
            }
        }
        
        boolean estaViva() { return vida > 0; }
    }
    
    private final Celda[][] board = new Celda[N][N];
    private final PanelTablero panelTablero = new PanelTablero();
    private final PanelJuego hud = new PanelJuego(panelTablero);

    private ColorJugador turno = ColorJugador.BLANCO;
    private TipoPieza tipoSeleccionadoPorRuleta;
    private Point piezaSeleccionada;
    private Set<Point> highlights = new HashSet<>();
    private int intentosRuletaRestantes = 1;

    // Sistema de usuarios
    private final Usuario jugador1;
    private final Usuario jugador2;
    private final ColorJugador colorJugador1;
    private final GestorUsuarios gestorUsuarios;

    public VentanaJuego(Usuario jugador1, Usuario jugador2, ColorJugador colorJugador1, GestorUsuarios gestor) {
        super("Vampire Wargame - Tablero");
        
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.colorJugador1 = colorJugador1;
        this.gestorUsuarios = gestor;
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(hud);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Configurar nombres según color elegido
        if (colorJugador1 == ColorJugador.NEGRO) {
            hud.setNombres(jugador1.getNombreUsuario() + " (Negras)", 
                          jugador2.getNombreUsuario() + " (Blancas)");
        } else {
            hud.setNombres(jugador2.getNombreUsuario() + " (Negras)", 
                          jugador1.getNombreUsuario() + " (Blancas)");
        }

        colocarInicial();
        refrescarIconos();

        hud.onGirarBlanco(e -> girarRuletaAlAzar(ColorJugador.BLANCO));
        hud.onGirarNegro(e -> girarRuletaAlAzar(ColorJugador.NEGRO));
        hud.onRetirar(e -> confirmarRetiro());

        panelTablero.setOnClick((fila, col) -> handleClick(col, fila));

        actualizarHUD();
    }

    /* ==================== RULETA ==================== */
    
    private void girarRuletaAlAzar(ColorJugador lado) {
        if (lado != turno) return;
        
        new RuletaOverlay(this, turno, new RuletaOverlay.Listener() {
            @Override 
            public void onElegido(TipoPieza tipo) {
                tipoSeleccionadoPorRuleta = tipo;
                intentosRuletaRestantes--;
                
                String nombreTipo = tipo == TipoPieza.HOMBRE_LOBO ? "Hombre Lobo" :
                                   tipo == TipoPieza.VAMPIRO ? "Vampiro" : "Nigromante";
                
                if (!tienePiezaDeTipo(tipo, turno)) {
                    if (intentosRuletaRestantes > 0) {
                        JOptionPane.showMessageDialog(VentanaJuego.this, 
                            "No tienes piezas de tipo " + nombreTipo + 
                            ".\nIntentos restantes: " + intentosRuletaRestantes,
                            "Sin Piezas", JOptionPane.WARNING_MESSAGE);
                        actualizarHUD();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(VentanaJuego.this, 
                            "No tienes piezas de tipo " + nombreTipo + 
                            " y no quedan intentos.\n¡PIERDES EL TURNO!",
                            "Turno Perdido", JOptionPane.ERROR_MESSAGE);
                        finalizarTurno();
                        return;
                    }
                }
                
                seleccionarPiezaDeTipo(tipo, turno);
            }
            
            @Override 
            public void onCancel() {}
        }).setVisible(true);
    }

    private boolean tienePiezaDeTipo(TipoPieza tipo, ColorJugador color) {
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                Celda cel = board[r][c];
                if (cel != null && cel.tipo == tipo && cel.color == color) {
                    return true;
                }
            }
        }
        return false;
    }

    /* ==================== SELECCIÓN Y MOVIMIENTO ==================== */
    
    private void seleccionarPiezaDeTipo(TipoPieza tipo, ColorJugador lado) {
        highlights.clear();
        piezaSeleccionada = null;

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                Celda cel = board[r][c];
                if (cel != null && cel.tipo == tipo && cel.color == lado) {
                    highlights.add(new Point(c, r));
                }
            }
        }
        panelTablero.setHighlights(highlights);
    }

    private void handleClick(int col, int fila) {
        if (tipoSeleccionadoPorRuleta == null) return;

        Point p = new Point(col, fila);
        
        if (piezaSeleccionada == null) {
            Celda cel = board[fila][col];
            if (cel != null && cel.tipo == tipoSeleccionadoPorRuleta && cel.color == turno) {
                piezaSeleccionada = p;
                highlights = calcularMovimientosYAtaquesLegales(piezaSeleccionada, cel.tipo, turno);
                panelTablero.setHighlights(highlights);
            }
            return;
        }

        if (highlights.contains(p)) {
            Celda destino = board[fila][col];
            
            if (destino == null) {
                if (board[piezaSeleccionada.y][piezaSeleccionada.x].tipo == TipoPieza.NIGROMANTE) {
                    int opcion = JOptionPane.showOptionDialog(this,
                        "¿Qué deseas hacer?",
                        "Acción Nigromante",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Mover", "Conjurar Zombie"},
                        "Mover");
                    
                    if (opcion == 1) {
                        conjurarZombie(p);
                    } else {
                        moverPieza(piezaSeleccionada, p);
                    }
                } else {
                    moverPieza(piezaSeleccionada, p);
                }
                finalizarTurno();
            }
            else if (destino.color == turno) {
                JOptionPane.showMessageDialog(this, 
                    "No puedes atacar tus propias piezas",
                    "Acción Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else {
                elegirTipoAtaque(piezaSeleccionada, p);
            }
        }
    }

    /* ==================== ATAQUES ==================== */
    
    private void elegirTipoAtaque(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        
        java.util.List<String> opciones = new ArrayList<>();
        opciones.add("Ataque Normal");
        
        if (atacante.tipo == TipoPieza.VAMPIRO && esAdyacente(desde, objetivo)) {
            opciones.add("Chupar Sangre");
        }
        else if (atacante.tipo == TipoPieza.NIGROMANTE) {
            if (distancia(desde, objetivo) == 2 && sinObstruccion(desde, objetivo)) {
                opciones.add("Lanzar Lanza");
            }
            if (hayZombieAdyacenteA(objetivo, turno)) {
                opciones.add("Ataque Zombie");
            }
        }
        
        if (opciones.size() == 1) {
            ejecutarAtaqueNormal(desde, objetivo);
            finalizarTurno();
        } else {
            int eleccion = JOptionPane.showOptionDialog(this,
                "Selecciona tipo de ataque:",
                "Ataque",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones.toArray(),
                opciones.get(0));
            
            if (eleccion >= 0) {
                String opcion = opciones.get(eleccion);
                switch (opcion) {
                    case "Ataque Normal":
                        ejecutarAtaqueNormal(desde, objetivo);
                        break;
                    case "Chupar Sangre":
                        ejecutarChuparSangre(desde, objetivo);
                        break;
                    case "Lanzar Lanza":
                        ejecutarLanzarLanza(desde, objetivo);
                        break;
                    case "Ataque Zombie":
                        ejecutarAtaqueZombie(objetivo);
                        break;
                }
                finalizarTurno();
            }
        }
    }

    private void ejecutarAtaqueNormal(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        Celda victima = board[objetivo.y][objetivo.x];
        
        int danio = switch (atacante.tipo) {
            case HOMBRE_LOBO -> 5;
            case VAMPIRO -> 3;
            case NIGROMANTE -> 4;
            case ZOMBIE -> 1;
        };
        
        aplicarDanio(victima, danio, true);
        mostrarResultadoAtaque(atacante, victima, danio, objetivo);
    }

    private void ejecutarChuparSangre(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        Celda victima = board[objetivo.y][objetivo.x];
        
        aplicarDanio(victima, 1, true);
        atacante.vida = Math.min(atacante.vida + 1, 4);
        
        JOptionPane.showMessageDialog(this,
            "¡Vampiro chupó sangre!\nDaño: 1 punto\nVida restaurada: +1",
            "Chupar Sangre", JOptionPane.INFORMATION_MESSAGE);
        
        mostrarResultadoAtaque(atacante, victima, 1, objetivo);
    }

    private void ejecutarLanzarLanza(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        Celda victima = board[objetivo.y][objetivo.x];
        
        aplicarDanio(victima, 2, false);
        
        JOptionPane.showMessageDialog(this,
            "¡Nigromante lanzó su lanza!\nDaño: 2 puntos (ignora escudo)",
            "Lanzar Lanza", JOptionPane.INFORMATION_MESSAGE);
        
        mostrarResultadoAtaque(atacante, victima, 2, objetivo);
    }

    private void ejecutarAtaqueZombie(Point objetivo) {
        Celda victima = board[objetivo.y][objetivo.x];
        
        aplicarDanio(victima, 1, true);
        
        JOptionPane.showMessageDialog(this,
            "¡Zombie atacó!\nDaño: 1 punto",
            "Ataque Zombie", JOptionPane.INFORMATION_MESSAGE);
        
        if (!victima.estaViva()) {
            board[objetivo.y][objetivo.x] = null;
        }
        refrescarIconos();
    }

    private void aplicarDanio(Celda victima, int danio, boolean considerarEscudo) {
        if (considerarEscudo) {
            if (victima.escudo >= danio) {
                victima.escudo -= danio;
            } else {
                int danioRestante = danio - victima.escudo;
                victima.escudo = 0;
                victima.vida -= danioRestante;
            }
        } else {
            victima.vida -= danio;
        }
        
        if (victima.vida < 0) victima.vida = 0;
    }

    private void mostrarResultadoAtaque(Celda atacante, Celda victima, int danio, Point pos) {
        String nombreVictima = switch (victima.tipo) {
            case HOMBRE_LOBO -> "Hombre Lobo";
            case VAMPIRO -> "Vampiro";
            case NIGROMANTE -> "Nigromante";
            case ZOMBIE -> "Zombie";
        };
        
        if (victima.estaViva()) {
            JOptionPane.showMessageDialog(this,
                String.format("SE ATACÓ %s\nDaño: %d puntos\nEscudo restante: %d\nVida restante: %d",
                    nombreVictima, danio, victima.escudo, victima.vida),
                "Resultado Ataque", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                String.format("¡SE DESTRUYÓ %s!", nombreVictima),
                "Pieza Destruida", JOptionPane.WARNING_MESSAGE);
            board[pos.y][pos.x] = null;
        }
        
        refrescarIconos();
    }

    /* ==================== MOVIMIENTO Y HELPERS ==================== */
    
    private void moverPieza(Point desde, Point hacia) {
        Celda src = board[desde.y][desde.x];
        board[hacia.y][hacia.x] = src;
        board[desde.y][desde.x] = null;
        refrescarIconos();
    }

    private void conjurarZombie(Point pos) {
        board[pos.y][pos.x] = new Celda(TipoPieza.ZOMBIE, turno);
        JOptionPane.showMessageDialog(this,
            "¡Nigromante conjuró un Zombie!",
            "Conjuración", JOptionPane.INFORMATION_MESSAGE);
        refrescarIconos();
    }

    private Set<Point> calcularMovimientosYAtaquesLegales(Point pos, TipoPieza tipo, ColorJugador lado) {
        Set<Point> resultado = new HashSet<>();
        
        int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        
        for (int[] d : dirs) {
            int nr = pos.y + d[0], nc = pos.x + d[1];
            if (nr >= 0 && nr < N && nc >= 0 && nc < N) {
                Celda cel = board[nr][nc];
                if (cel == null || cel.color != lado) {
                    resultado.add(new Point(nc, nr));
                }
            }
        }
        
        if (tipo == TipoPieza.HOMBRE_LOBO) {
            for (int[] d : dirs) {
                int nr = pos.y + d[0], nc = pos.x + d[1];
                int nr2 = pos.y + 2*d[0], nc2 = pos.x + 2*d[1];
                if (nr2 >= 0 && nr2 < N && nc2 >= 0 && nc2 < N) {
                    if (board[nr][nc] == null && board[nr2][nc2] == null) {
                        resultado.add(new Point(nc2, nr2));
                    }
                }
            }
        }
        
        if (tipo == TipoPieza.NIGROMANTE) {
            for (int[] d : dirs) {
                int nr2 = pos.y + 2*d[0], nc2 = pos.x + 2*d[1];
                if (nr2 >= 0 && nr2 < N && nc2 >= 0 && nc2 < N) {
                    Celda cel = board[nr2][nc2];
                    if (cel != null && cel.color != lado) {
                        resultado.add(new Point(nc2, nr2));
                    }
                }
            }
        }
        
        return resultado;
    }

    private boolean esAdyacente(Point a, Point b) {
        return Math.abs(a.x - b.x) <= 1 && Math.abs(a.y - b.y) <= 1;
    }

    private int distancia(Point a, Point b) {
        return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }

    private boolean sinObstruccion(Point desde, Point hasta) {
        int dx = Integer.compare(hasta.x - desde.x, 0);
        int dy = Integer.compare(hasta.y - desde.y, 0);
        
        int x = desde.x + dx, y = desde.y + dy;
        while (x != hasta.x || y != hasta.y) {
            if (board[y][x] != null) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    private boolean hayZombieAdyacenteA(Point pos, ColorJugador color) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = pos.y + dr, nc = pos.x + dc;
                if (nr >= 0 && nr < N && nc >= 0 && nc < N) {
                    Celda cel = board[nr][nc];
                    if (cel != null && cel.tipo == TipoPieza.ZOMBIE && cel.color == color) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* ==================== GESTIÓN DE TURNOS ==================== */
    
    private void finalizarTurno() {
        tipoSeleccionadoPorRuleta = null;
        piezaSeleccionada = null;
        highlights.clear();
        panelTablero.setHighlights(null);
        
        turno = (turno == ColorJugador.BLANCO) ? ColorJugador.NEGRO : ColorJugador.BLANCO;
        
        int piezasPerdidas = contarPiezasPerdidas(turno);
        if (piezasPerdidas >= 4) {
            intentosRuletaRestantes = 3;
        } else if (piezasPerdidas >= 2) {
            intentosRuletaRestantes = 2;
        } else {
            intentosRuletaRestantes = 1;
        }
        
        actualizarHUD();
        verificarFinDeJuego();
    }

    private int contarPiezasPerdidas(ColorJugador color) {
        int piezasActuales = 0;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                Celda cel = board[r][c];
                if (cel != null && cel.color == color && cel.tipo != TipoPieza.ZOMBIE) {
                    piezasActuales++;
                }
            }
        }
        return 6 - piezasActuales;
    }

    private void verificarFinDeJuego() {
        int piezasBlancas = contarTodasLasPiezas(ColorJugador.BLANCO);
        int piezasNegras = contarTodasLasPiezas(ColorJugador.NEGRO);
        
        if (piezasBlancas == 0) {
            terminarJuego(ColorJugador.NEGRO, false);
        } else if (piezasNegras == 0) {
            terminarJuego(ColorJugador.BLANCO, false);
        }
    }

    private int contarTodasLasPiezas(ColorJugador color) {
        int total = 0;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                Celda cel = board[r][c];
                if (cel != null && cel.color == color) {
                    total++;
                }
            }
        }
        return total;
    }

    private void confirmarRetiro() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro que deseas retirarte?\nEl jugador contrario ganará automáticamente.",
            "Confirmar Retiro",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            ColorJugador ganador = (turno == ColorJugador.BLANCO) ? ColorJugador.NEGRO : ColorJugador.BLANCO;
            terminarJuego(ganador, true);
        }
    }

    private void terminarJuego(ColorJugador ganador, boolean porRetiro) {
        hud.detenerCronometro();
        
        Usuario usuarioGanador, usuarioPerdedor;
        
        if (colorJugador1 == ganador) {
            usuarioGanador = jugador1;
            usuarioPerdedor = jugador2;
        } else {
            usuarioGanador = jugador2;
            usuarioPerdedor = jugador1;
        }
        
        gestorUsuarios.registrarResultadoPartida(usuarioGanador, true, porRetiro);
        gestorUsuarios.registrarResultadoPartida(usuarioPerdedor, false, false);
        
        String mensaje = porRetiro ?
            String.format("JUGADOR %s SE HA RETIRADO\n¡FELICIDADES %s!\nHAS GANADO 3 PUNTOS",
                usuarioPerdedor.getNombreUsuario(), usuarioGanador.getNombreUsuario()) :
            String.format("¡%s HA VENCIDO A %s!\nFELICIDADES, HAS GANADO 3 PUNTOS",
                usuarioGanador.getNombreUsuario(), usuarioPerdedor.getNombreUsuario());
        
        JOptionPane.showMessageDialog(this, mensaje, "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    /* ==================== INICIALIZACIÓN ==================== */
    
    private void colocarInicial() {
        for (int r = 0; r < N; r++) Arrays.fill(board[r], null);

        TipoPieza[] orden = {
            TipoPieza.HOMBRE_LOBO, TipoPieza.VAMPIRO, TipoPieza.NIGROMANTE,
            TipoPieza.NIGROMANTE, TipoPieza.VAMPIRO, TipoPieza.HOMBRE_LOBO
        };
        
        for (int c = 0; c < N; c++) {
            board[0][c] = new Celda(orden[c], ColorJugador.NEGRO);
            board[N-1][c] = new Celda(orden[c], ColorJugador.BLANCO);
        }
    }

    private void refrescarIconos() {
        panelTablero.limpiarPiezas();
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                Celda cel = board[r][c];
                if (cel != null) {
                    BufferedImage img = hud.imagen(cel.tipo, cel.color);
                    if (img != null) {
                        panelTablero.setPieza(r, c, img);
                    }
                }
            }
        }
    }

    private void actualizarHUD() {
        hud.habilitarGiro(turno, true);
        
        int piezasBlancas = contarTodasLasPiezas(ColorJugador.BLANCO);
        int piezasNegras = contarTodasLasPiezas(ColorJugador.NEGRO);
        
        hud.setPiezasRestantes(ColorJugador.BLANCO, piezasBlancas);
        hud.setPiezasRestantes(ColorJugador.NEGRO, piezasNegras);
        
        int maxIntentos = intentosRuletaRestantes == 3 ? 3 : 
                         intentosRuletaRestantes == 2 ? 2 : 1;
        hud.setIntentos(turno, intentosRuletaRestantes, maxIntentos);
        
        SwingUtilities.invokeLater(this::refrescarIconos);
    }

    /* ==================== MÉTODO ESTÁTICO PARA INICIAR DESDE MENÚ ==================== */
    
    public static void iniciarDesdeMenu(Frame menuPrincipal, Usuario usuarioActual, GestorUsuarios gestor) {
        DialogoSeleccionOponente dialogo = new DialogoSeleccionOponente(menuPrincipal, usuarioActual, gestor);
        dialogo.setVisible(true);
        
        if (dialogo.isConfirmado()) {
            Usuario oponente = dialogo.getOponenteSeleccionado();
            ColorJugador colorElegido = dialogo.getColorElegido();
            
            SwingUtilities.invokeLater(() -> {
                VentanaJuego juego = new VentanaJuego(usuarioActual, oponente, colorElegido, gestor);
                juego.setVisible(true);
            });
        }
    }
}