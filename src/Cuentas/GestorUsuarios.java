/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.util.List;
/**
 *
 * @author najma
 */
public class GestorUsuarios {

    private final IRepositorioUsuarios repositorio;

    public GestorUsuarios(IRepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }

    /** Sign Up: valida reglas de contraseña, crea hash y registra. */
    public boolean registrarUsuario(String nombreUsuario, String contrasenaPlano) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) return false;
        if (!UtilSeguridad.validarContrasena(contrasenaPlano)) return false;

        String hash = UtilSeguridad.hashSHA256(contrasenaPlano);
        Usuario nuevo = new Usuario(nombreUsuario.trim(), hash);
        return repositorio.agregarUsuario(nuevo);
    }

    /** Log In: compara hash de la contraseña ingresada con el almacenado. */
    public Usuario iniciarSesion(String nombreUsuario, String contrasenaPlano) {
        Usuario existente = repositorio.buscarPorNombre(nombreUsuario);
        if (existente == null) return null;
        String hashIngresado = UtilSeguridad.hashSHA256(contrasenaPlano);
        if (existente.getContrasenaHash().equals(hashIngresado)) {
            return existente;
        }
        return null;
    }

    public EstadisticaCuenta obtenerEstadistica(String nombreUsuario) {
        Usuario usuario = repositorio.buscarPorNombre(nombreUsuario);
        return (usuario != null) ? usuario.getEstadistica() : null;
    }

    public List<Usuario> listarUsuarios() {
        return repositorio.listarUsuarios();
    }
}