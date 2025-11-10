/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 *
 * @author najma
 */
public class ControlTurno {

    private ColorJugador turno;
    private int intentosPermitidos;
    private int intentosRestantes;

    private final List<ResultadoRuleta> resultados = new ArrayList<>();
    private ResultadoRuleta elegido;

    public ControlTurno() {
        this(ColorJugador.BLANCO);
    }

    public ControlTurno(ColorJugador empieza) {
        this.turno = Objects.requireNonNull(empieza);
        this.intentosPermitidos = 1;
        this.intentosRestantes  = 1;
    }

    public ColorJugador getTurno()              { 
        return turno; }
    public int getIntentosPermitidos()          { 
        return intentosPermitidos; }
    public int getIntentosRestantes()           { 
        return intentosRestantes; }
    public List<ResultadoRuleta> getResultados(){ 
        return new ArrayList<>(resultados); }
    public ResultadoRuleta getElegido()         { 
        return elegido; }

    public void comenzarTurno(int piezasIniciales, int piezasVivasTurno) {
        int perdidas = Math.max(0, piezasIniciales - piezasVivasTurno);

        if (perdidas >= 4)      intentosPermitidos = 3;
        else if (perdidas >= 2) intentosPermitidos = 2;
        else                    intentosPermitidos = 1;

        intentosRestantes = intentosPermitidos;
        resultados.clear();
        elegido = null;
    }

    public boolean puedeGirar(ColorJugador jugador) {
        return jugador == turno && intentosRestantes > 0;
    }

    public ResultadoRuleta girar(RuletaTurno ruleta, ColorJugador jugador) {
        if (!puedeGirar(jugador)) return null;

        TipoPieza tipo = ruleta.girar(); // Tu ruleta devuelve un TipoPieza
        ResultadoRuleta r = new ResultadoRuleta(jugador, tipo, System.currentTimeMillis());

        resultados.add(r);
        intentosRestantes--;
        return r;
    }

    public boolean elegirResultado(ResultadoRuleta r) {
        if (r == null) {
            if (resultados.isEmpty()) return false;
            elegido = resultados.get(resultados.size() - 1);
            return true;
        }
        if (!resultados.contains(r)) return false;
        elegido = r;
        return true;
    }

    public void finalizarTurno() {
        turno = (turno == ColorJugador.BLANCO) ? ColorJugador.NEGRO : ColorJugador.BLANCO;
    }
}
