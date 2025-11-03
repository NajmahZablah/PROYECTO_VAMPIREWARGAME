/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author najma
 */
public final class UtilSeguridad {
    private UtilSeguridad() {}

    private static final String CARACTERES_ESPECIALES = "!@#$%^&*()_+-={}[]:;\"'<>,.?/\\|~`";

    public static boolean validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 6) return false;

        boolean tieneNumero = false;
        int cantidadEspeciales = 0;

        for (char caracter : contrasena.toCharArray()) {
            if (Character.isDigit(caracter)) tieneNumero = true;
            if (CARACTERES_ESPECIALES.indexOf(caracter) >= 0) cantidadEspeciales++;
        }
        return tieneNumero && cantidadEspeciales >= 2;
    }

    public static String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Nunca debería ocurrir en una JVM normal
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}