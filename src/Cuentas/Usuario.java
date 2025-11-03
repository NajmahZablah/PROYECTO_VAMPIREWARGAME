/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

/**
 *
 * @author najma
 */
public class Usuario {

    private final String nombreUsuario;
    private final String contrasenaHash; // guardamos hash, no texto plano
    private final EstadisticaCuenta estadistica = new EstadisticaCuenta();

    public Usuario(String nombreUsuario, String contrasenaHash) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getContrasenaHash() { return contrasenaHash; }
    public EstadisticaCuenta getEstadistica() { return estadistica; }
}