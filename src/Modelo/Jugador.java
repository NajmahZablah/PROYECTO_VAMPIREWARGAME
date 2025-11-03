/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class Jugador {

    private final String nombreUsuario;
    private final ColorJugador colorJugador;
    private int puntos = 0;

    public Jugador(String nombreUsuario, ColorJugador colorJugador) {
        this.nombreUsuario = nombreUsuario;
        this.colorJugador = colorJugador;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public ColorJugador getColorJugador() { return colorJugador; }
    public int getPuntos() { return puntos; }
    public void sumarPuntos(int cantidad) { puntos += cantidad; }
}