/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.Objects;
/**
 *
 * @author najma
 */
public final class Posicion {

    public final int fila;
    public final int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public Posicion trasladar(int desplazamientoFila, int desplazamientoColumna) {
        return new Posicion(this.fila + desplazamientoFila, this.columna + desplazamientoColumna);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Posicion)) return false;
        Posicion otra = (Posicion) objeto;
        return fila == otra.fila && columna == otra.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}