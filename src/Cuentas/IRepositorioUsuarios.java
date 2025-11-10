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
public interface IRepositorioUsuarios {
    
    // Usuarios
    List<Usuario> cargarTodos();
    void guardarTodos(List<Usuario> usuarios);
    Usuario buscarPorNombre(String nombreUsuario);
    void agregar(Usuario usuario);
    void actualizar(Usuario usuario);
    void eliminar(String nombreUsuario);

    // Logs
    void anexarLog(String nombreUsuario, String linea);
    List<String> leerLogs(String nombreUsuario, int maxLineas);
}