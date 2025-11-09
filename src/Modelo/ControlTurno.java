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
/**
 * Controla turnos y giros de la ruleta por turno.
 * Reglas de giros:
 *   - 0–1 piezas perdidas  -> 1 giro
 *   - 2–3 piezas perdidas  -> 2 giros
 *   - 4+  piezas perdidas  -> 3 giros
 *
 * Expone además un “historial” de resultados por turno y
 * la posibilidad de elegir cuál resultado usar (por ahora
 * dejamos que valga el último si no se especifica otro).
 */
public class ControlTurno {

    private ColorJugador turno;          // quién juega ahora
    private int intentosPermitidos;      // 1/2/3 según piezas perdidas
    private int intentosRestantes;       // cuántos giros le quedan este turno

    private final List<ResultadoRuleta> resultados = new ArrayList<>();
    private ResultadoRuleta elegido;

    /** Constructor por defecto: empieza BLANCO. */
    public ControlTurno() {
        this(ColorJugador.BLANCO);
    }

    /** Constructor indicando quién empieza. */
    public ControlTurno(ColorJugador empieza) {
        this.turno = Objects.requireNonNull(empieza);
        this.intentosPermitidos = 1;
        this.intentosRestantes  = 1;
    }

    // -------- Getters usados por PanelJuego / VentanaJuego --------
    public ColorJugador getTurno()              { return turno; }
    public int getIntentosPermitidos()          { return intentosPermitidos; }
    public int getIntentosRestantes()           { return intentosRestantes; }
    public List<ResultadoRuleta> getResultados(){ return new ArrayList<>(resultados); }
    public ResultadoRuleta getElegido()         { return elegido; }

    /**
     * Llamar al INICIO de cada turno.
     * @param piezasIniciales  normalmente 6
     * @param piezasVivasTurno piezas vivas del jugador que va a jugar ahora
     */
    public void comenzarTurno(int piezasIniciales, int piezasVivasTurno) {
        int perdidas = Math.max(0, piezasIniciales - piezasVivasTurno);

        if (perdidas >= 4)      intentosPermitidos = 3;
        else if (perdidas >= 2) intentosPermitidos = 2;
        else                    intentosPermitidos = 1;

        intentosRestantes = intentosPermitidos;
        resultados.clear();
        elegido = null;
    }

    /** ¿Puede girar el jugador indicado en este momento? */
    public boolean puedeGirar(ColorJugador jugador) {
        return jugador == turno && intentosRestantes > 0;
    }

    /**
     * Realiza un giro (si procede), lo agrega al historial del turno y lo devuelve.
     * @return null si no se podía girar.
     */
    public ResultadoRuleta girar(RuletaTurno ruleta, ColorJugador jugador) {
        if (!puedeGirar(jugador)) return null;

        TipoPieza tipo = ruleta.girar(); // Tu ruleta devuelve un TipoPieza
        ResultadoRuleta r = new ResultadoRuleta(jugador, tipo, System.currentTimeMillis());

        resultados.add(r);
        intentosRestantes--;
        return r;
    }

    /**
     * El jugador elige cuál de los resultados del turno usará.
     * Si pasas null, se toma el último giro.
     */
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

    /** Llamar cuando el jugador ya movió/atacó y el turno termina. */
    public void finalizarTurno() {
        turno = (turno == ColorJugador.BLANCO) ? ColorJugador.NEGRO : ColorJugador.BLANCO;
        // El siguiente begin de turno recalculará intentos.
    }
}
