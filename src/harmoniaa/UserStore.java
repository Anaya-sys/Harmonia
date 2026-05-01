/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.util.List;

/**
 *
 * @author CARLOS ANAYA
 */


import java.util.ArrayList;

public class UserStore {

  
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static Usuario usuarioActivo = null;

    static {
        usuarios.add(new Usuario(
            1, "Carlos Admin", "carlos@harmonia.com", "admin123",
            new PerfilUsuario(TipoPerfil.PROFESIONAL, "Guitarra", 5000.0),
            Rol.ADMIN
        ));
        usuarios.add(new Usuario(
            2, "Laura Admin", "laura@harmonia.com", "admin456",
            new PerfilUsuario(TipoPerfil.PRODUCTOR, "Piano", 8000.0),
            Rol.ADMIN
        ));
        usuarios.add(new Usuario(
            3, "Juan Pérez", "juan@gmail.com", "juan123",
            new PerfilUsuario(TipoPerfil.ESTUDIANTE, "Batería", 1500.0),
            Rol.COMPRADOR
        ));
    }

    
    
    public static Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    
    public static LoginResultado intentarLogin(String email, String contrasena) {
        Usuario u = buscarPorEmail(email);
        if (u == null)                              return LoginResultado.EMAIL_NO_EXISTE;
        if (!u.getContrasena().equals(contrasena))  return LoginResultado.CONTRASENA_INCORRECTA;
        usuarioActivo = u;
        return LoginResultado.OK;
    }

    
    public static boolean registrar(Usuario nuevo) {
        if (buscarPorEmail(nuevo.getEmail()) != null) return false;
        usuarios.add(nuevo);
        usuarioActivo = nuevo;
        return true;
    }

   
    public static int siguienteId() {
        int max = 0;
        for (Usuario u : usuarios) if (u.getId() > max) max = u.getId();
        return max + 1;
    }

    public static Usuario getUsuarioActivo() { return usuarioActivo; }
    public static void    cerrarSesion()     { usuarioActivo = null; }

    
    public enum LoginResultado {
        OK,
        EMAIL_NO_EXISTE,
        CONTRASENA_INCORRECTA
    }
}
