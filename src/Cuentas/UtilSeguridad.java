/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cuentas;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 *
 * @author najma
 */
public class UtilSeguridad {

    private static final Pattern REGEX_CONTRASENA =
            Pattern.compile("^(?=(?:.*\\d){1,})(?=(?:.*[^\\w\\s]){1,}).{5}$");

    public static boolean validarContrasena(String contrasena) {
        return contrasena != null && REGEX_CONTRASENA.matcher(contrasena).matches();
    }

    public static String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo calcular hash", e);
        }
    }

    public static boolean comprobarPassword(String textoPlano, String hashEsperado) {
        return hashSHA256(textoPlano).equals(hashEsperado);
    }
}