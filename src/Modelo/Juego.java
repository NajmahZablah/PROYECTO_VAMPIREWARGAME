/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class Juego {

    private final Tablero tablero;
    private final Jugador jugadorBlanco;
    private final Jugador jugadorNegro;

    public Juego(Jugador jugadorBlanco, Jugador jugadorNegro) {
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.tablero = new Tablero();
    }

    public Tablero getTablero() { 
        return tablero; }
    public Jugador getJugadorBlanco() { 
        return jugadorBlanco; }
    public Jugador getJugadorNegro() { 
        return jugadorNegro; }

    public void iniciarPartida() {
        colocarFilaInicial(0, ColorJugador.BLANCO);
        colocarFilaInicial(5, ColorJugador.NEGRO);
    }

    private void colocarFilaInicial(int numeroFila, ColorJugador colorJugador) {
        Pieza[] ordenInicial = new Pieza[] {
            new HombreLobo(colorJugador, 5, 2, 5),
            new Vampiro(colorJugador, 4, 5, 3),
            new Nigromante(colorJugador, 3, 1, 4),
            new Nigromante(colorJugador, 3, 1, 4),
            new Vampiro(colorJugador, 4, 5, 3),
            new HombreLobo(colorJugador, 5, 2, 5)
        };
        for (int indiceColumna = 0; indiceColumna < 6; indiceColumna++) {
            Posicion posicion = new Posicion(numeroFila, indiceColumna);
            ordenInicial[indiceColumna].setPosicion(posicion);
            tablero.colocarPieza(posicion, ordenInicial[indiceColumna]);
        }
    }

    public boolean jugadorTienePiezaVivaDeTipo(ColorJugador colorJugador, TipoPieza tipoPieza) {
        for (int indiceFila = 0; indiceFila < 6; indiceFila++) {
            for (int indiceColumna = 0; indiceColumna < 6; indiceColumna++) {
                Pieza pieza = tablero.obtenerPieza(new Posicion(indiceFila, indiceColumna));
                if (pieza != null
                        && pieza.getColorJugador() == colorJugador
                        && pieza.getTipoPieza() == tipoPieza
                        && pieza.estaViva()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int contarPiezasVivas(ColorJugador colorJugador) {
        return tablero.contarPiezasVivas(colorJugador);
    }
}