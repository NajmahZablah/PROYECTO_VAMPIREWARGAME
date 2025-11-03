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

    boolean agregarUsuario(Usuario nuevoUsuario);
    Usuario buscarPorNombre(String nombreUsuario);
    List<Usuario> listarUsuarios();
}