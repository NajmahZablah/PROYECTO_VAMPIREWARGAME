/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class HombreLobo extends Pieza {

    public HombreLobo(ColorJugador colorJugador, int vidaMaxima, int escudo, int ataque) {
        super(colorJugador, TipoPieza.HOMBRE_LOBO, 5, 2, 5);
    }

    @Override
    public boolean puedeMover(Tablero tablero, Posicion destino) {
        if (!tablero.estaDentro(destino)) return false;
        if (tablero.obtenerPieza(destino) != null) return false;

        int diferenciaFila = Math.abs(destino.fila - posicion.fila);
        int diferenciaColumna = Math.abs(destino.columna - posicion.columna);
        if (diferenciaFila == 0 && diferenciaColumna == 0) return false;

        return diferenciaFila <= 2 && diferenciaColumna <= 2;
    }

    @Override
    public boolean puedeAtacar(Tablero tablero, Posicion objetivo) {
        if (!tablero.estaDentro(objetivo)) return false;
        Pieza piezaEnemiga = tablero.obtenerPieza(objetivo);
        return piezaEnemiga != null
                && piezaEnemiga.getColorJugador() != this.colorJugador
                && esAdyacente(this.posicion, objetivo);
    }
}