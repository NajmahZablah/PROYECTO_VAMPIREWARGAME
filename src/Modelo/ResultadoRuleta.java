/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public class ResultadoRuleta {
    private final ColorJugador color;
    private final TipoPieza tipo;
    private final long timestampMs;

    public ResultadoRuleta(ColorJugador color, TipoPieza tipo, long timestampMs) {
        this.color = color; this.tipo = tipo; this.timestampMs = timestampMs;
    }
    public ColorJugador getColor() { return color; }
    public TipoPieza getTipo() { return tipo; }
    public long getTimestampMs() { return timestampMs; }
    @Override public String toString() { return "Ruleta{" + color + "→" + tipo + "}"; }
}