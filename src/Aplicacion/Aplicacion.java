/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aplicacion;

import Cuentas.GestorUsuarios;
import Cuentas.RepositorioUsuariosMemoria;
import Interfaz.MenuInicio;

import javax.swing.SwingUtilities;

/**
 *
 * @author najma
 */

public class Aplicacion {
    
    /**
     * Inicializa el sistema y muestra el menú de inicio.
     */
    public static void iniciar() {
        
        RepositorioUsuariosMemoria repositorio = new RepositorioUsuariosMemoria();
        
        GestorUsuarios gestorUsuarios = new GestorUsuarios(repositorio);
        
        SwingUtilities.invokeLater(() -> {
            MenuInicio menuInicio = new MenuInicio(gestorUsuarios);
            menuInicio.setVisible(true);
        });
    }
}
