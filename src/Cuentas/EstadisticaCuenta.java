/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

/**
 *
 * @author najma
 */
public class EstadisticaCuenta {
    
    private int partidasGanadas;
    private int partidasPerdidas;

    public EstadisticaCuenta(int ganadas, int perdidas) {
        this.partidasGanadas = Math.max(0, ganadas);
        this.partidasPerdidas = Math.max(0, perdidas);
    }

    public int getPartidasGanadas() { return partidasGanadas; }
    public int getPartidasPerdidas() { return partidasPerdidas; }
    public void sumarGanada() { partidasGanadas++; }
    public void sumarPerdida() { partidasPerdidas++; }

    public double getPorcentajeVictorias() {
        int tot = partidasGanadas + partidasPerdidas;
        return tot == 0 ? 0.0 : (partidasGanadas * 100.0) / tot;
    }
}