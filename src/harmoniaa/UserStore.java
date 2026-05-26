package harmoniaa;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserStore {
  
    
    
    
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static Usuario usuarioActivo = null;
 
    // ── Ruta del archivo (en la carpeta del proyecto) ──────────────────────────
    private static final String ARCHIVO = "usuarios.txt";
    private static final String SEPARADOR = "|";
 
    // ── ZONA 1: bloque estático — primero carga el archivo, si no hay nada usa defaults ──
    static {
        cargarDesdeArchivo();
        System.out.println(new File(ARCHIVO).getAbsolutePath());
        // Si el archivo no existía o estaba vacío, cargamos los usuarios por defecto
        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario(
                1, "Carlos Admin", "carlos@harmonia.com", "admin123",
                new PerfilUsuario(TipoPerfil.PROFESIONAL, "Guitarra"),
                Rol.ADMIN
            ));
            usuarios.add(new Usuario(
                2, "Laura Admin", "laura@harmonia.com", "admin456",
                new PerfilUsuario(TipoPerfil.PRODUCTOR, "Piano"),
                Rol.ADMIN
            ));
            usuarios.add(new Usuario(
                3, "Juan Pérez", "juan@gmail.com", "juan123",
                new PerfilUsuario(TipoPerfil.ESTUDIANTE, "Batería"),
                Rol.COMPRADOR
            ));
            // Guardamos los defaults para que la próxima vez los lea del archivo
            guardarEnArchivo();
        }
    }
 
    
    
    
    // ── ZONA 2: lógica de archivo (nueva) ─────────────────────────────────────
 
    /**
     * Lee usuarios.txt y los agrega a la lista en memoria.
     * Formato de cada línea: id|nombre|email|contraseña|tipoPerfil|instrumento|rol
     */
    private static void cargarDesdeArchivo() {
        Path ruta = Paths.get(ARCHIVO);
        if (!Files.exists(ruta)) return;
 
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
 
                String[] partes = linea.split("\\" + SEPARADOR, -1);
                if (partes.length < 7) continue;   // línea incompleta, la saltamos
 
                int    id          = Integer.parseInt(partes[0]);
                String nombre      = partes[1];
                String email       = partes[2];
                String contrasena  = partes[3];
                TipoPerfil tipo    = TipoPerfil.valueOf(partes[4]);
                String instrumento = partes[5];
                Rol    rol         = Rol.valueOf(partes[6]);
 
                PerfilUsuario perfil = new PerfilUsuario(tipo, instrumento);
                usuarios.add(new Usuario(id, nombre, email, contrasena, perfil, rol));
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("⚠ Error al leer " + ARCHIVO + ": " + e.getMessage());
        }
    }
 
    /**
     * Escribe todos los usuarios actuales en usuarios.txt.
     * Sobreescribe el archivo completo (más simple y seguro).
     */
    private static void guardarEnArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Usuario u : usuarios) {
                PerfilUsuario p = u.getPerfil();
                writer.write(
                    u.getId()              + SEPARADOR +
                    u.getNombre()          + SEPARADOR +
                    u.getEmail()           + SEPARADOR +
                    u.getContrasena()      + SEPARADOR +
                    p.getTipo().name()     + SEPARADOR +
                    p.getInstrumento()     + SEPARADOR +
                    u.getRol().name()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("⚠ Error al guardar " + ARCHIVO + ": " + e.getMessage());
        }
    }
 
    // ── ZONA 3: métodos públicos — igual que antes, registrar llama a guardar ──
 
    public static Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }
 
    public static LoginResultado intentarLogin(String email, String contrasena) {
        Usuario u = buscarPorEmail(email);
        if (u == null)                             return LoginResultado.EMAIL_NO_EXISTE;
        if (!u.getContrasena().equals(contrasena)) return LoginResultado.CONTRASENA_INCORRECTA;
        usuarioActivo = u;
        return LoginResultado.OK;
    }
 
    /** Registra un nuevo usuario y lo persiste en el archivo. */
    public static boolean registrar(Usuario nuevo) {
        if (buscarPorEmail(nuevo.getEmail()) != null) return false;
        usuarios.add(nuevo);
        usuarioActivo = nuevo;
        guardarEnArchivo();   // ← única línea añadida respecto al original
        return true;
    }
 
    public static int siguienteId() {
        int max = 0;
        for (Usuario u : usuarios) if (u.getId() > max) max = u.getId();
        return max + 1;
    }
 
    public static Usuario getUsuarioActivo() { return usuarioActivo; }
    public static void    cerrarSesion()     { usuarioActivo = null; }
 
    /** Devuelve una copia inmutable de todos los usuarios registrados. */
    public static java.util.List<Usuario> getTodos() {
        return java.util.Collections.unmodifiableList(usuarios);
    }
 
        public enum LoginResultado {
        OK,
        EMAIL_NO_EXISTE,
        CONTRASENA_INCORRECTA
    }
    
}
