/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class Nigromante extends Pieza {

    public Nigromante(ColorJugador colorJugador, int vidaMaxima, int escudo, int ataque) {
        super(colorJugador, TipoPieza.NIGROMANTE, vidaMaxima, escudo, ataque);
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

    /** Ataque especial con lanza (ignora escudo, daño/2) a 2 casillas como máximo. */
    public InformeDanio atacarConLanza(Tablero tablero, Posicion objetivo) {
        if (!tablero.estaDentro(objetivo)) {
            throw new IllegalArgumentException("Objetivo fuera del tablero para ataque con lanza.");
        }
        Pieza enemigo = tablero.obtenerPieza(objetivo);
        if (enemigo == null || enemigo.getColorJugador() == this.colorJugador) {
            throw new IllegalArgumentException("Objetivo inválido para ataque con lanza.");
        }

        int diferenciaFila = Math.abs(objetivo.fila - posicion.fila);
        int diferenciaColumna = Math.abs(objetivo.columna - posicion.columna);
        if (diferenciaFila > 2 || diferenciaColumna > 2 || (diferenciaFila == 0 && diferenciaColumna == 0)) {
            throw new IllegalArgumentException("El objetivo debe estar a 1 o 2 casillas de distancia.");
        }

        return enemigo.recibirDanio(this.ataque, TipoDanio.ATAQUE_LANZA);
    }

    /** Invoca un zombie propio en una casilla vacía del tablero. */
    public void invocarZombie(Tablero tablero, Posicion destinoZombie) {
        if (!tablero.estaDentro(destinoZombie)) {
            throw new IllegalArgumentException("Posición fuera del tablero para invocar zombie.");
        }
        if (tablero.obtenerPieza(destinoZombie) != null) {
            throw new IllegalArgumentException("La casilla no está vacía para invocar zombie.");
        }
        Zombie nuevoZombie = new Zombie(this.colorJugador, 1, 0, 1);
        nuevoZombie.setPosicion(destinoZombie);
        tablero.colocarPieza(destinoZombie, nuevoZombie);
    }

    /** Ataque a través de un zombie propio adyacente al objetivo (daño 1 directo). */
    public InformeDanio atacarATravesDeZombie(Tablero tablero, Posicion objetivo) {
        if (!tablero.estaDentro(objetivo)) {
            throw new IllegalArgumentException("Objetivo fuera del tablero.");
        }
        Pieza enemigo = tablero.obtenerPieza(objetivo);
        if (enemigo == null || enemigo.getColorJugador() == this.colorJugador) {
            throw new IllegalArgumentException("Objetivo inválido para ataque a través de zombie.");
        }
        if (!hayZombiePropioAdyacente(tablero, objetivo)) {
            throw new IllegalArgumentException("No hay zombie propio adyacente al objetivo.");
        }
        return enemigo.recibirDanio(1, TipoDanio.ATAQUE_ZOMBIE);
    }

    /** Revisa las 8 casillas vecinas del objetivo en busca de un zombie propio. */
    private boolean hayZombiePropioAdyacente(Tablero tablero, Posicion objetivo) {
        for (int desplazamientoFila = -1; desplazamientoFila <= 1; desplazamientoFila++) {
            for (int desplazamientoColumna = -1; desplazamientoColumna <= 1; desplazamientoColumna++) {
                if (desplazamientoFila == 0 && desplazamientoColumna == 0) continue;
                Posicion posicionVecina = new Posicion(objetivo.fila + desplazamientoFila,
                                                       objetivo.columna + desplazamientoColumna);
                if (!tablero.estaDentro(posicionVecina)) continue;
                Pieza piezaVecina = tablero.obtenerPieza(posicionVecina);
                boolean esZombiePropio = piezaVecina != null
                        && piezaVecina.getTipoPieza() == TipoPieza.ZOMBIE
                        && piezaVecina.getColorJugador() == this.colorJugador;
                if (esZombiePropio) return true;
            }
        }
        return false;
    }
}