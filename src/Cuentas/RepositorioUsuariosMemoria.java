/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author najma
 */
public class RepositorioUsuariosMemoria implements IRepositorioUsuarios {

    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public synchronized boolean agregarUsuario(Usuario nuevoUsuario) {
        if (buscarPorNombre(nuevoUsuario.getNombreUsuario()) != null) return false;
        return usuarios.add(nuevoUsuario);
    }

    @Override
    public synchronized Usuario buscarPorNombre(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Usuario> listarUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }
}  