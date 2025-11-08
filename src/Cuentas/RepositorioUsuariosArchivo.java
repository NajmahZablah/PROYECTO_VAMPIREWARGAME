/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
/**
 *
 * @author najma
 */
public class RepositorioUsuariosArchivo implements IRepositorioUsuarios {

    private final Path archivoCsv;
    private final Path carpetaLogs;
    private final List<Usuario> cache = new ArrayList<>();

    // CSV: nombre,hash,fechaEpoch,activo,puntos,ganadas,perdidas
    public RepositorioUsuariosArchivo(String nombreArchivo) {
        this.archivoCsv = Paths.get(nombreArchivo);
        this.carpetaLogs = Paths.get("logs");
        try {
            if (Files.notExists(archivoCsv)) {
                Files.createFile(archivoCsv);
                Files.write(archivoCsv,
                        Collections.singletonList("nombre,hash,fecha,activo,puntos,ganadas,perdidas"),
                        StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
            if (Files.notExists(carpetaLogs)) Files.createDirectory(carpetaLogs);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo preparar el repositorio", e);
        }
        // Cargar a cache
        cache.addAll(cargarTodos());
    }

    // =============== Usuarios ===============

    @Override
    public synchronized List<Usuario> cargarTodos() {
        try (BufferedReader br = Files.newBufferedReader(archivoCsv, StandardCharsets.UTF_8)) {
            List<Usuario> out = new ArrayList<>();
            String linea;
            boolean header = true;
            while ((linea = br.readLine()) != null) {
                if (header) { header = false; continue; }
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split(",", -1);
                if (p.length < 7) continue;
                String nombre = p[0];
                String hash   = p[1];
                long fecha    = parseLongSeguro(p[2]);
                boolean activo= Boolean.parseBoolean(p[3]);
                int puntos    = parseIntSeguro(p[4]);
                int ganadas   = parseIntSeguro(p[5]);
                int perdidas  = parseIntSeguro(p[6]);
                out.add(new Usuario(nombre, hash, fecha, activo, puntos, ganadas, perdidas));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo CSV", e);
        }
    }

    @Override
    public synchronized void guardarTodos(List<Usuario> usuarios) {
        List<String> lineas = new ArrayList<>();
        lineas.add("nombre,hash,fecha,activo,puntos,ganadas,perdidas");
        for (Usuario u : usuarios) {
            lineas.add(String.join(",",
                    escape(u.getNombreUsuario()),
                    u.getHashPassword(),
                    String.valueOf(u.getFechaIngresoEpoch()),
                    String.valueOf(u.isActivo()),
                    String.valueOf(u.getPuntos()),
                    String.valueOf(u.getEstadistica().getPartidasGanadas()),
                    String.valueOf(u.getEstadistica().getPartidasPerdidas())
            ));
        }
        try {
            Files.write(archivoCsv, lineas, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            // Actualiza cache
            cache.clear();
            cache.addAll(usuarios);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo CSV", e);
        }
    }

    @Override
    public synchronized Usuario buscarPorNombre(String nombreUsuario) {
        return cache.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst().orElse(null);
    }

    @Override
    public synchronized void agregar(Usuario usuario) {
        if (buscarPorNombre(usuario.getNombreUsuario()) != null)
            throw new IllegalArgumentException("Ya existe el usuario");
        cache.add(usuario);
        guardarTodos(cache);
    }

    @Override
    public synchronized void actualizar(Usuario usuario) {
        eliminar(usuario.getNombreUsuario());
        cache.add(usuario);
        guardarTodos(cache);
    }

    @Override
    public synchronized void eliminar(String nombreUsuario) {
        List<Usuario> nueva = cache.stream()
                .filter(u -> !u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .collect(Collectors.toList());
        guardarTodos(nueva);
        // borra logs opcionalmente (no obligatorio)
        try {
            Files.deleteIfExists(carpetaLogs.resolve(nombreUsuario + ".log"));
        } catch (IOException ignored) {}
    }

    // =============== Logs ===============

    @Override
    public synchronized void anexarLog(String nombreUsuario, String linea) {
        Path log = carpetaLogs.resolve(nombreUsuario + ".log");
        String lineaOut = linea.replaceAll("\\R", " ").trim();
        try {
            Files.write(log, Collections.singletonList(lineaOut + System.lineSeparator()),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir log", e);
        }
    }

    @Override
    public synchronized List<String> leerLogs(String nombreUsuario, int maxLineas) {
        Path log = carpetaLogs.resolve(nombreUsuario + ".log");
        if (Files.notExists(log)) return Collections.emptyList();
        try {
            List<String> todas = Files.readAllLines(log, StandardCharsets.UTF_8);
            // últimas primero
            Collections.reverse(todas);
            if (maxLineas > 0 && todas.size() > maxLineas) {
                return new ArrayList<>(todas.subList(0, maxLineas));
            }
            return todas;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    // =============== Utils ===============

    private static int parseIntSeguro(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    private static long parseLongSeguro(String s) {
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return 0L; }
    }
    private static String escape(String s) {
        return (s == null) ? "" : s.replace(",", " ");
    }
}