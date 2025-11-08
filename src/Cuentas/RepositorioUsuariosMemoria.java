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