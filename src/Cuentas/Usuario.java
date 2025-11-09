/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author najma
 */
public class Usuario {
    private final String nombreUsuario;
    private String hashPassword;
    private long fechaIngresoEpoch;   // epoch seconds
    private boolean activo;
    private int puntos;
    private final EstadisticaCuenta estadistica;

    public Usuario(String nombreUsuario, String hashPassword, long fechaIngresoEpoch,
                   boolean activo, int puntos, int ganadas, int perdidas) {
        this.nombreUsuario = nombreUsuario;
        this.hashPassword = hashPassword;
        this.fechaIngresoEpoch = fechaIngresoEpoch;
        this.activo = activo;
        this.puntos = puntos;
        this.estadistica = new EstadisticaCuenta(ganadas, perdidas);
    }

    public static Usuario nuevo(String nombreUsuario, String hashPassword) {
        long ahora = Instant.now().getEpochSecond();
        return new Usuario(nombreUsuario, hashPassword, ahora, true, 0, 0, 0);
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }
    public long getFechaIngresoEpoch() { return fechaIngresoEpoch; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public EstadisticaCuenta getEstadistica() { return estadistica; }

    public String getFechaIngresoTexto() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(fechaIngresoEpoch));
    }
}