/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author najma
 */
public class RepositorioUsuariosArchivo implements IRepositorioUsuarios {

    private final File archivo;
    private final List<Usuario> usuarios = new ArrayList<>();

    public RepositorioUsuariosArchivo(String rutaArchivo) {
        this.archivo = new File(rutaArchivo);
        cargarDesdeArchivo();
    }

    @Override
    public synchronized boolean agregarUsuario(Usuario nuevoUsuario) {
        if (buscarPorNombre(nuevoUsuario.getNombreUsuario()) != null) return false;
        usuarios.add(nuevoUsuario);
        guardarEnArchivo();
        return true;
    }

    @Override
    public synchronized Usuario buscarPorNombre(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) return usuario;
        }
        return null;
    }

    @Override
    public synchronized List<Usuario> listarUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    /** Llamar cuando cambien estadísticas (ganadas/perdidas). */
    public synchronized void guardarEnArchivo() {
        try (BufferedWriter escritor = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            for (Usuario u : usuarios) {
                int ganadas = u.getEstadistica().getPartidasGanadas();
                int perdidas = u.getEstadistica().getPartidasPerdidas();
                escritor.write(u.getNombreUsuario() + ";" + u.getContrasenaHash() + ";" + ganadas + ";" + perdidas);
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de usuarios: " + archivo, e);
        }
    }

    private void cargarDesdeArchivo() {
        if (!archivo.exists()) return;
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length < 4) continue;
                String nombre = partes[0];
                String hash = partes[1];
                int ganadas = parseEnteroSeguro(partes[2]);
                int perdidas = parseEnteroSeguro(partes[3]);

                Usuario usuario = new Usuario(nombre, hash);
                // reconstruir estadísticas
                for (int i = 0; i < ganadas; i++) usuario.getEstadistica().registrarVictoria();
                for (int i = 0; i < perdidas; i++) usuario.getEstadistica().registrarDerrota();
                usuarios.add(usuario);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de usuarios: " + archivo, e);
        }
    }

    private int parseEnteroSeguro(String texto) {
        try { return Integer.parseInt(texto.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}