/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class Vampiro extends Pieza {

    public Vampiro(ColorJugador colorJugador, int vidaMaxima, int escudo, int ataque) {
        super(colorJugador, TipoPieza.VAMPIRO, 4, 5, 3);
    }

    @Override
    public boolean puedeMover(Tablero tablero, Posicion destino) {
        if (!tablero.estaDentro(destino)) return false;
        if (tablero.obtenerPieza(destino) != null) return false;
        return esAdyacente(this.posicion, destino);
    }

    @Override
    public boolean puedeAtacar(Tablero tablero, Posicion objetivo) {
        if (!tablero.estaDentro(objetivo)) return false;
        Pieza piezaEnemiga = tablero.obtenerPieza(objetivo);
        return piezaEnemiga != null
                && piezaEnemiga.getColorJugador() != this.colorJugador
                && esAdyacente(this.posicion, objetivo);
    }

    public InformeDanio succionarSangre(Tablero tablero, Posicion objetivo) {
        if (!puedeAtacar(tablero, objetivo)) {
            throw new IllegalArgumentException("Succionar sangre requiere objetivo enemigo adyacente.");
        }
        Pieza enemigo = tablero.obtenerPieza(objetivo);
        InformeDanio informe = enemigo.recibirDanio(1, TipoDanio.CHUPASANGRE);
        if (this.vida < this.vidaMaxima) {
            this.vida = Math.min(this.vidaMaxima, this.vida + 1);
        }
        return informe;
    }
}