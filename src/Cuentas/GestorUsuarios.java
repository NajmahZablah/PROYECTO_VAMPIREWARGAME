/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author najma
 */
public class GestorUsuarios {

    private final IRepositorioUsuarios repo;
    private final Map<String, Usuario> indice; // en memoria por nombre

    public GestorUsuarios(IRepositorioUsuarios repo) {
        this.repo = repo;
        this.indice = new HashMap<>();
        for (Usuario u : repo.cargarTodos()) {
            indice.put(u.getNombreUsuario().toLowerCase(Locale.ROOT), u);
        }
    }

    // ========== Registro / Login ==========

    public boolean registrarUsuario(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return false;
        if (!UtilSeguridad.validarContrasena(contrasena)) return false;
        String key = nombreUsuario.toLowerCase(Locale.ROOT);
        if (indice.containsKey(key)) return false;

        String hash = UtilSeguridad.hashSHA256(contrasena);
        Usuario nuevo = Usuario.nuevo(nombreUsuario, hash);
        indice.put(key, nuevo);
        repo.agregar(nuevo);
        repo.anexarLog(nombreUsuario, ts() + " Cuenta creada");
        return true;
    }

    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        Usuario u = obtener(nombreUsuario);
        if (u == null || !u.isActivo()) return null;
        if (!UtilSeguridad.comprobarPassword(contrasena, u.getHashPassword())) return null;
        repo.anexarLog(nombreUsuario, ts() + " Inicio de sesión correcto");
        return u;
    }

    // ========== Cuenta ==========

    public boolean cambiarPassword(Usuario usuario, String passwordActual, String passwordNueva) {
        if (usuario == null) return false;
        if (!UtilSeguridad.comprobarPassword(passwordActual, usuario.getHashPassword())) return false;
        if (!UtilSeguridad.validarContrasena(passwordNueva)) return false;

        usuario.setHashPassword(UtilSeguridad.hashSHA256(passwordNueva));
        repo.actualizar(usuario);
        repo.anexarLog(usuario.getNombreUsuario(), ts() + " Cambio de password");
        return true;
    }

    public void eliminarUsuario(Usuario usuario) {
        if (usuario == null) return;
        indice.remove(usuario.getNombreUsuario().toLowerCase(Locale.ROOT));
        repo.eliminar(usuario.getNombreUsuario());
    }

    // ========== Juego / Puntos / Estadística ==========

    /** Llama esto al finalizar una partida. Suma puntos y registra log. */
    public void registrarResultadoPartida(Usuario usuario, boolean gano, boolean retiroDelRival) {
        if (usuario == null) return;
        if (gano) {
            usuario.getEstadistica().sumarGanada();
            usuario.setPuntos(usuario.getPuntos() + 3);
        } else {
            usuario.getEstadistica().sumarPerdida();
        }
        repo.actualizar(usuario);

        String detalle = gano ? "GANA" : "PIERDE";
        if (gano && retiroDelRival) detalle += " por RETIRO del rival";
        repo.anexarLog(usuario.getNombreUsuario(),
                ts() + " Resultado: " + detalle + " | Puntos=" + usuario.getPuntos());
    }

    /** Ranking por puntos (desc). */
    public List<Usuario> obtenerRankingPorPuntos() {
        return indice.values().stream()
                .sorted(Comparator.comparingInt(Usuario::getPuntos).reversed()
                        .thenComparing(Usuario::getNombreUsuario))
                .collect(Collectors.toList());
    }

    /** Últimos N logs del usuario (máximo N; si N<=0 devuelve todos). */
    public List<String> obtenerLogs(String nombreUsuario, int max) {
        return repo.leerLogs(nombreUsuario, max);
    }

    // ========== Helpers ==========

    public Usuario obtener(String nombreUsuario) {
        if (nombreUsuario == null) return null;
        return indice.get(nombreUsuario.toLowerCase(Locale.ROOT));
    }

    private static String ts() {
        return Instant.now().toString();
    }
}