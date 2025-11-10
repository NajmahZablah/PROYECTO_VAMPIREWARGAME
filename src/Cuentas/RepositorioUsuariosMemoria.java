/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.util.*;
/**
 *
 * @author najma
 */
public class RepositorioUsuariosMemoria implements IRepositorioUsuarios {

    private final Map<String, Usuario> mapa = new HashMap<>();
    private final Map<String, Deque<String>> logs = new HashMap<>();

    /**
     * Constructor: crea usuarios de prueba automáticamente
     */
    public RepositorioUsuariosMemoria() {
        crearUsuariosDePrueba();
    }

    /**
     * Crea usuarios de prueba para poder jugar inmediatamente
     */
    private void crearUsuariosDePrueba() {
        try {
            // Usuario 1: Admin (con estadísticas)
            Usuario admin = Usuario.nuevo("Seible", UtilSeguridad.hashSHA256("S3!bl"));
            admin.setPuntos(100);
            admin.getEstadistica().sumarGanada();
            admin.getEstadistica().sumarGanada();
            admin.getEstadistica().sumarPerdida();
            mapa.put("Seible", admin);
            
            // Usuario 2: Jugador1
            Usuario jugador1 = Usuario.nuevo("Kaelix", UtilSeguridad.hashSHA256("Ka3l!"));
            jugador1.setPuntos(50);
            jugador1.getEstadistica().sumarGanada();
            mapa.put("Kaelix", jugador1);
            
            // Usuario 3: Jugador2
            Usuario jugador2 = Usuario.nuevo("Zeal", UtilSeguridad.hashSHA256("Z3@li"));
            jugador2.setPuntos(0);
            mapa.put("Zeal", jugador2);
            
            System.out.println("\n╔══════════════════════════════════════════════════╗");
            System.out.println("║ USUARIOS DE PRUEBA CREADOS EN MEMORIA  ║");
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  Usuario: Seible   | Password: S3!bl   ║");
            System.out.println("║  Usuario: Kaelix   | Password: Ka3l!   ║");
            System.out.println("║  Usuario: Zeal     | Password: Z3@li   ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");
        } catch (Exception e) {
            System.err.println("Error creando usuarios de prueba: " + e.getMessage());
        }
    }

    @Override
    public synchronized List<Usuario> cargarTodos() {
        return new ArrayList<>(mapa.values());
    }

    @Override
    public synchronized void guardarTodos(List<Usuario> usuarios) {
        mapa.clear();
        for (Usuario u : usuarios) mapa.put(u.getNombreUsuario().toLowerCase(Locale.ROOT), u);
    }

    @Override
    public synchronized Usuario buscarPorNombre(String nombreUsuario) {
        return mapa.get(nombreUsuario.toLowerCase(Locale.ROOT));
    }

    @Override
    public synchronized void agregar(Usuario usuario) {
        String k = usuario.getNombreUsuario().toLowerCase(Locale.ROOT);
        if (mapa.containsKey(k)) throw new IllegalArgumentException("Ya existe");
        mapa.put(k, usuario);
    }

    @Override
    public synchronized void actualizar(Usuario usuario) {
        mapa.put(usuario.getNombreUsuario().toLowerCase(Locale.ROOT), usuario);
    }

    @Override
    public synchronized void eliminar(String nombreUsuario) {
        mapa.remove(nombreUsuario.toLowerCase(Locale.ROOT));
        logs.remove(nombreUsuario.toLowerCase(Locale.ROOT));
    }

    @Override
    public synchronized void anexarLog(String nombreUsuario, String linea) {
        String k = nombreUsuario.toLowerCase(Locale.ROOT);
        logs.computeIfAbsent(k, __ -> new ArrayDeque<>()).addLast(linea);
        // limita a 500 por usuario para no crecer infinito
        while (logs.get(k).size() > 500) logs.get(k).removeFirst();
    }

    @Override
    public synchronized List<String> leerLogs(String nombreUsuario, int maxLineas) {
        Deque<String> dq = logs.get(nombreUsuario.toLowerCase(Locale.ROOT));
        if (dq == null) return Collections.emptyList();
        List<String> todas = new ArrayList<>(dq);
        Collections.reverse(todas); // últimas primero
        return (maxLineas > 0 && todas.size() > maxLineas) ? todas.subList(0, maxLineas) : todas;
    }
}