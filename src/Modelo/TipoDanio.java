/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author najma
 */
public enum TipoDanio {
    NORMAL,         // consume escudo y luego vida
    ATAQUE_LANZA,   // ignora escudo; aplica (ataque/2) directo a vida
    ATAQUE_ZOMBIE,  // daño directo de 1 punto a vida
    CHUPASANGRE     // daño directo de 1 punto a vida (y el vampiro cura 1)
}