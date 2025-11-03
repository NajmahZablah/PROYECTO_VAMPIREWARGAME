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

    public void registrarVictoria() { partidasGanadas++; }
    public void registrarDerrota() { partidasPerdidas++; }

    public int getPartidasGanadas() { return partidasGanadas; }
    public int getPartidasPerdidas() { return partidasPerdidas; }

    public int getPartidasTotales() { return partidasGanadas + partidasPerdidas; }

    public double getPorcentajeVictorias() {
        int total = getPartidasTotales();
        if (total == 0) return 0.0;
        return (partidasGanadas * 100.0) / total;
    }

    @Override
    public String toString() {
        return "Ganadas=" + partidasGanadas +
               ", Perdidas=" + partidasPerdidas +
               ", % Victorias=" + String.format("%.1f", getPorcentajeVictorias()) + "%";
    }
}