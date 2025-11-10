/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public abstract class Pieza {
    
    protected int vidaMaxima;
    protected int vida;
    protected int escudo;
    protected int ataque;

    protected ColorJugador colorJugador;
    protected TipoPieza tipoPieza;
    protected Posicion posicion;
    protected boolean viva = true;

    protected Pieza(ColorJugador colorJugador, TipoPieza tipoPieza,
                    int vidaMaxima, int escudo, int ataque) {
        this.colorJugador = colorJugador;
        this.tipoPieza = tipoPieza;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.escudo = escudo;
        this.ataque = ataque;
    }

    public ColorJugador getColorJugador() { return colorJugador; }
    public TipoPieza getTipoPieza() { return tipoPieza; }
    public Posicion getPosicion() { return posicion; }
    public void setPosicion(Posicion nuevaPosicion) { this.posicion = nuevaPosicion; }
    public boolean estaViva() { return viva; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getEscudo() { return escudo; }
    public int getAtaque() { return ataque; }

    public String getAbreviatura() {
        switch (tipoPieza) {
            case HOMBRE_LOBO: return "HL";
            case VAMPIRO:     return "VA";
            case NIGROMANTE:  return "NI";
            case ZOMBIE:      return "ZO";
            default:          return "??";
        }
    }

    public abstract boolean puedeMover(Tablero tablero, Posicion destino);
    public abstract boolean puedeAtacar(Tablero tablero, Posicion objetivo);

    public InformeDanio atacar(Tablero tablero, Posicion objetivo) {
        if (!puedeAtacar(tablero, objetivo)) {
            throw new IllegalArgumentException("Ataque inválido: objetivo no permitido o fuera de alcance.");
        }
        Pieza piezaEnemiga = tablero.obtenerPieza(objetivo);
        if (piezaEnemiga == null || piezaEnemiga.colorJugador == this.colorJugador) {
            throw new IllegalArgumentException("Objetivo inválido: debe ser una pieza enemiga.");
        }
        return piezaEnemiga.recibirDanio(this.ataque, TipoDanio.NORMAL);
    }

    public InformeDanio recibirDanio(int cantidad, TipoDanio tipoDanio) {
        int escudoPerdido = 0;
        int vidaPerdida = 0;

        switch (tipoDanio) {
            case ATAQUE_LANZA: {
                int directoALaVida = cantidad / 2; // ignora escudo
                vidaPerdida = Math.min(vida, directoALaVida);
                vida -= vidaPerdida;
                break;
            }
            case ATAQUE_ZOMBIE:
            case CHUPASANGRE: {
                vidaPerdida = Math.min(vida, 1);
                vida -= vidaPerdida;
                break;
            }
            case NORMAL:
            default: {
                escudoPerdido = Math.min(escudo, cantidad);
                escudo -= escudoPerdido;
                int restante = cantidad - escudoPerdido;
                if (restante > 0) {
                    vidaPerdida = Math.min(vida, restante);
                    vida -= vidaPerdida;
                }
                break;
            }
        }

        if (vida <= 0) {
            viva = false;
        }
        return new InformeDanio(escudoPerdido, vidaPerdida, !viva);
    }

    protected boolean esAdyacente(Posicion origen, Posicion destino) {
        int diferenciaFila = Math.abs(origen.fila - destino.fila);
        int diferenciaColumna = Math.abs(origen.columna - destino.columna);
        return diferenciaFila <= 1 && diferenciaColumna <= 1
                && !(diferenciaFila == 0 && diferenciaColumna == 0);
    }
}