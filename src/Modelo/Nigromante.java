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
        super(colorJugador, TipoPieza.NIGROMANTE, 3, 1, 4);
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

    // Segunda función recursiva
    private boolean hayZombiePropioAdyacente(Tablero tablero, Posicion objetivo) {
        return hayZombiePropioAdyacenteRecursivo(tablero, objetivo, -1, -1);
    }

    private boolean hayZombiePropioAdyacenteRecursivo(Tablero tablero, Posicion objetivo,
                                                      int desplazamientoFila, 
                                                      int desplazamientoColumna) {
        
        if (desplazamientoFila > 1) {
            return false;
        }
        
        if (desplazamientoFila == 0 && desplazamientoColumna == 0) {
            // Avanzar a la siguiente columna
            return hayZombiePropioAdyacenteRecursivo(tablero, objetivo, 
                                                     desplazamientoFila, 
                                                     desplazamientoColumna + 1);
        }
        
        Posicion posicionVecina = new Posicion(
            objetivo.fila + desplazamientoFila,
            objetivo.columna + desplazamientoColumna
        );
        
        if (tablero.estaDentro(posicionVecina)) {
            Pieza piezaVecina = tablero.obtenerPieza(posicionVecina);
            
            boolean esZombiePropio = piezaVecina != null
                    && piezaVecina.getTipoPieza() == TipoPieza.ZOMBIE
                    && piezaVecina.getColorJugador() == this.colorJugador;
            
            if (esZombiePropio) {
                return true;
            }
        }

        int siguienteColumna = desplazamientoColumna + 1;
        int siguienteFila = desplazamientoFila;
        
        if (siguienteColumna > 1) {
            siguienteColumna = -1;
            siguienteFila++;
        }
        
        return hayZombiePropioAdyacenteRecursivo(tablero, objetivo, 
                                                 siguienteFila, 
                                                 siguienteColumna);
    }
}