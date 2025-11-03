/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public final class Zombie extends Pieza {

    public Zombie(ColorJugador colorJugador, int vidaMaxima, int escudo, int ataque) {
        super(colorJugador, TipoPieza.ZOMBIE, vidaMaxima, escudo, ataque);
    }

    @Override
    public boolean puedeMover(Tablero tablero, Posicion destino) { return false; }

    @Override
    public boolean puedeAtacar(Tablero tablero, Posicion objetivo) { return false; }
}