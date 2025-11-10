/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.*;
/**
 *
 * @author najma
 */
public class RuletaTurno {
    
    public static class Segmento {
        public final TipoPieza tipo; public final int peso;
        public Segmento(TipoPieza tipo, int peso) { this.tipo = tipo; this.peso = Math.max(1, peso); }
    }
    private final List<Segmento> segs = new ArrayList<>();
    private final Random rnd;

    public RuletaTurno() { this(new Random()); }
    public RuletaTurno(Random r) {
        rnd = (r==null? new Random(): r);
        // “2 imágenes de cada tipo”
        segs.add(new Segmento(TipoPieza.HOMBRE_LOBO, 2));
        segs.add(new Segmento(TipoPieza.VAMPIRO,     2));
        segs.add(new Segmento(TipoPieza.NIGROMANTE,  2));
    }
    public void setPesos(int hl, int va, int mu) {
        segs.clear();
        segs.add(new Segmento(TipoPieza.HOMBRE_LOBO, hl));
        segs.add(new Segmento(TipoPieza.VAMPIRO,     va));
        segs.add(new Segmento(TipoPieza.NIGROMANTE,  mu));
    }
    public TipoPieza girar() {
        int tot = segs.stream().mapToInt(s -> s.peso).sum();
        int t = rnd.nextInt(tot)+1, acc = 0;
        for (Segmento s: segs) { acc += s.peso; if (t<=acc) return s.tipo; }
        return TipoPieza.VAMPIRO;
    }
}