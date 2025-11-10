/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class Tablero {

    private final Pieza[][] celdas = new Pieza[6][6];

    public boolean estaDentro(Posicion posicion) {
        return posicion.fila >= 0 && posicion.fila < 6
            && posicion.columna >= 0 && posicion.columna < 6;
    }

    public Pieza obtenerPieza(Posicion posicion) {
        if (!estaDentro(posicion)) return null;
        return celdas[posicion.fila][posicion.columna];
    }

    public void colocarPieza(Posicion posicion, Pieza pieza) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException("Posición fuera del tablero: " + posicion);
        }
        celdas[posicion.fila][posicion.columna] = pieza;
    }

    public void moverPieza(Posicion origen, Posicion destino) {
        if (!estaDentro(origen) || !estaDentro(destino)) {
            throw new IllegalArgumentException("Movimiento fuera del tablero.");
        }
        Pieza piezaEnOrigen = obtenerPieza(origen);
        if (piezaEnOrigen == null) {
            throw new IllegalArgumentException("No hay pieza en la posición de origen.");
        }
        if (obtenerPieza(destino) != null) {
            throw new IllegalArgumentException("La posición destino no está vacía.");
        }
        celdas[origen.fila][origen.columna] = null;
        celdas[destino.fila][destino.columna] = piezaEnOrigen;
        piezaEnOrigen.setPosicion(destino);
    }

    public void imprimirTablero() {
        for (int indiceFila = 0; indiceFila < 6; indiceFila++) {
            for (int indiceColumna = 0; indiceColumna < 6; indiceColumna++) {
                Pieza pieza = celdas[indiceFila][indiceColumna];
                if (pieza == null) {
                    System.out.print("[   ]");
                } else {
                    System.out.print("[" + pieza.getAbreviatura() + "]");
                }
            }
            System.out.println();
        }
    }

    // Función Recursiva
    public int contarPiezasVivas(ColorJugador colorJugador) {
        return contarPiezasVivasRecursivo(colorJugador, 0, 0);
    }

    private int contarPiezasVivasRecursivo(ColorJugador colorJugador, int filaActual, int columnaActual) {
        if (filaActual >= 6) return 0; // condición de parada

        int cuenta = 0;
        Pieza pieza = celdas[filaActual][columnaActual];
        if (pieza != null && pieza.getColorJugador() == colorJugador && pieza.estaViva()) {
            cuenta = 1;
        }

        int siguienteFila = filaActual;
        int siguienteColumna = columnaActual + 1;
        if (siguienteColumna == 6) {
            siguienteFila++;
            siguienteColumna = 0;
        }
        return cuenta + contarPiezasVivasRecursivo(colorJugador, siguienteFila, siguienteColumna);
    }
}