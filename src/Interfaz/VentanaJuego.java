/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaz;

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
/**
 * VentanaJuego CORREGIDA según especificaciones del PDF:
 * - Ruleta al azar (no por clicks)
 * - Movimiento vs Ataque automático según casilla destino
 * - Sistema de vida/escudo/ataque
 * - Ataques especiales correctos
 * - Zombies se conjuran opcionalmente
 * - Sistema de intentos según piezas perdidas
 */
public class VentanaJuego extends JFrame {

    private static final int N = 6;

    /* ====== Estado con Vida/Escudo según PDF ====== */
    private static class Celda {
        TipoPieza tipo;
        ColorJugador color;
        int vida;
        int escudo;
        
        Celda(TipoPieza t, ColorJugador c) {
            tipo = t;
            color = c;
            // Según imagen del PDF: HL(6,3,5), VA(4,5,3), NI(5,2,4), ZO(1,0,1)
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

    public VentanaJuego() {
        super("Vampire Wargame - Tablero");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(hud);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        hud.setNombres("Jugador (Negras)", "Rival (Blancas)");

        colocarInicial();
        refrescarIconos();

        hud.onGirarBlanco(e -> girarRuletaAlAzar(ColorJugador.BLANCO));
        hud.onGirarNegro(e -> girarRuletaAlAzar(ColorJugador.NEGRO));

        panelTablero.setOnClick((fila, col) -> handleClick(col, fila));

        actualizarHUD();
    }

    /* ================== RULETA AL AZAR (CORREGIDO) ================== */
    
    private void girarRuletaAlAzar(ColorJugador lado) {
        if (lado != turno) return;
        
        // Simular "giro" aleatorio
        TipoPieza[] tipos = {TipoPieza.HOMBRE_LOBO, TipoPieza.VAMPIRO, TipoPieza.NIGROMANTE};
        Random rand = new Random();
        TipoPieza resultado = tipos[rand.nextInt(tipos.length)];
        
        tipoSeleccionadoPorRuleta = resultado;
        intentosRuletaRestantes--;
        
        // Mostrar resultado
        String nombreTipo = resultado == TipoPieza.HOMBRE_LOBO ? "Hombre Lobo" :
                           resultado == TipoPieza.VAMPIRO ? "Vampiro" : "Nigromante";
        JOptionPane.showMessageDialog(this, 
            "Ruleta: " + nombreTipo + "\nSelecciona tu pieza de este tipo",
            "Resultado Ruleta", JOptionPane.INFORMATION_MESSAGE);
        
        // Verificar si tiene piezas de este tipo
        if (!tienePiezaDeTipo(resultado, turno)) {
            if (intentosRuletaRestantes > 0) {
                JOptionPane.showMessageDialog(this, 
                    "No tienes piezas de este tipo. Intentos restantes: " + intentosRuletaRestantes,
                    "Sin Piezas", JOptionPane.WARNING_MESSAGE);
                return; // Puede girar de nuevo
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No tienes piezas de este tipo y no quedan intentos. PIERDES EL TURNO",
                    "Turno Perdido", JOptionPane.ERROR_MESSAGE);
                finalizarTurno();
                return;
            }
        }
        
        // Resaltar piezas válidas
        seleccionarPiezaDeTipo(resultado, turno);
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

    /* ================== SELECCIÓN Y MOVIMIENTO (CORREGIDO) ================== */
    
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
        
        // PASO 1: Seleccionar pieza propia
        if (piezaSeleccionada == null) {
            Celda cel = board[fila][col];
            if (cel != null && cel.tipo == tipoSeleccionadoPorRuleta && cel.color == turno) {
                piezaSeleccionada = p;
                highlights = calcularMovimientosYAtaquesLegales(piezaSeleccionada, cel.tipo, turno);
                panelTablero.setHighlights(highlights);
            }
            return;
        }

        // PASO 2: Ejecutar movimiento o ataque
        if (highlights.contains(p)) {
            Celda destino = board[fila][col];
            
            // CASO A: Casilla vacía → MOVIMIENTO (o conjurar zombie si es Nigromante)
            if (destino == null) {
                if (board[piezaSeleccionada.y][piezaSeleccionada.x].tipo == TipoPieza.NIGROMANTE) {
                    // Preguntar: ¿Mover o conjurar zombie?
                    int opcion = JOptionPane.showOptionDialog(this,
                        "¿Qué deseas hacer?",
                        "Acción Nigromante",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Mover", "Conjurar Zombie"},
                        "Mover");
                    
                    if (opcion == 1) { // Conjurar zombie
                        conjurarZombie(p);
                    } else { // Mover
                        moverPieza(piezaSeleccionada, p);
                    }
                } else {
                    moverPieza(piezaSeleccionada, p);
                }
                finalizarTurno();
            }
            // CASO B: Casilla con pieza propia → ERROR
            else if (destino.color == turno) {
                JOptionPane.showMessageDialog(this, 
                    "No puedes atacar tus propias piezas",
                    "Acción Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // CASO C: Casilla con enemigo → ATAQUE
            else {
                elegirTipoAtaque(piezaSeleccionada, p);
            }
        }
    }

    /* ================== ATAQUES (CORREGIDO CON ESPECIALES) ================== */
    
    private void elegirTipoAtaque(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        
        // Opciones según tipo de pieza
        java.util.List<String> opciones = new ArrayList<>();
        opciones.add("Ataque Normal");
        
        // Ataques especiales según tipo
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
            // Solo ataque normal disponible
            ejecutarAtaqueNormal(desde, objetivo);
            finalizarTurno();
        } else {
            // Mostrar opciones
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
        
        // Daño según tipo (espada del PDF)
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
        
        // Quita 1 punto, restaura 1 vida al vampiro
        aplicarDanio(victima, 1, true);
        atacante.vida = Math.min(atacante.vida + 1, 4); // Max 4 según PDF
        
        JOptionPane.showMessageDialog(this,
            "¡Vampiro chupó sangre!\nDaño: 1 punto\nVida restaurada: +1",
            "Chupar Sangre", JOptionPane.INFORMATION_MESSAGE);
        
        mostrarResultadoAtaque(atacante, victima, 1, objetivo);
    }

    private void ejecutarLanzarLanza(Point desde, Point objetivo) {
        Celda atacante = board[desde.y][desde.x];
        Celda victima = board[objetivo.y][objetivo.x];
        
        // Ignora escudo, daño = 2 (mitad del poder normal)
        aplicarDanio(victima, 2, false); // false = ignora escudo
        
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
            // Primero se gasta el escudo
            if (victima.escudo >= danio) {
                victima.escudo -= danio;
            } else {
                int danioRestante = danio - victima.escudo;
                victima.escudo = 0;
                victima.vida -= danioRestante;
            }
        } else {
            // Ignora escudo (lanzar lanza)
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

    /* ================== MOVIMIENTO Y HELPERS ================== */
    
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
        
        // Movimientos adyacentes (8 direcciones)
        int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        
        for (int[] d : dirs) {
            int nr = pos.y + d[0], nc = pos.x + d[1];
            if (nr >= 0 && nr < N && nc >= 0 && nc < N) {
                Celda cel = board[nr][nc];
                // Puede moverse a vacía o atacar enemigo
                if (cel == null || cel.color != lado) {
                    resultado.add(new Point(nc, nr));
                }
            }
        }
        
        // Hombre Lobo: hasta 2 casillas vacías
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
        
        // Nigromante: lanzar lanza a 2 casillas
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
        // Verifica si hay piezas entre desde y hasta (línea recta)
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

    /* ================== GESTIÓN DE TURNOS ================== */
    
    private void finalizarTurno() {
        tipoSeleccionadoPorRuleta = null;
        piezaSeleccionada = null;
        highlights.clear();
        panelTablero.setHighlights(null);
        
        // Cambiar turno
        turno = (turno == ColorJugador.BLANCO) ? ColorJugador.NEGRO : ColorJugador.BLANCO;
        
        // Calcular intentos según piezas perdidas
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
        // Inicia con 6 piezas principales (sin contar zombies)
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
            JOptionPane.showMessageDialog(this,
                "¡JUGADOR NEGRO HA VENCIDO!\nFELICIDADES, HAS GANADO 3 PUNTOS",
                "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else if (piezasNegras == 0) {
            JOptionPane.showMessageDialog(this,
                "¡JUGADOR BLANCO HA VENCIDO!\nFELICIDADES, HAS GANADO 3 PUNTOS",
                "Fin del Juego", JOptionPane.INFORMATION_MESSAGE);
            dispose();
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

    /* ================== INICIALIZACIÓN ================== */
    
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
                    if (img != null) panelTablero.setPieza(r, c, img);
                }
            }
        }
    }

    private void actualizarHUD() {
        hud.habilitarGiro(turno, true);
        hud.setIntentos(turno, intentosRuletaRestantes, 
            turno == ColorJugador.BLANCO ? 
            (contarPiezasPerdidas(ColorJugador.BLANCO) >= 4 ? 3 : contarPiezasPerdidas(ColorJugador.BLANCO) >= 2 ? 2 : 1) :
            (contarPiezasPerdidas(ColorJugador.NEGRO) >= 4 ? 3 : contarPiezasPerdidas(ColorJugador.NEGRO) >= 2 ? 2 : 1)
        );
        SwingUtilities.invokeLater(this::refrescarIconos);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}