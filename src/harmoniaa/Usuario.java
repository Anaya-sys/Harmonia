/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.util.ArrayList;

/**
 *
 * @author CARLOS ANAYA
 */
public class Usuario {
   private int                 id;
    private String              nombre;
    private String              email;
    private String              contrasena;
    private PerfilUsuario       perfil;
    private Rol                 rol;         
    private ArrayList<Pedido>   historial;
    private ArrayList<Producto> listaDeseos;
    private ArrayList<Opinion>  opiniones;
 
    public Usuario(int id, String nombre, String email,
                   String contrasena, PerfilUsuario perfil, Rol rol) {
        this.id          = id;
        this.nombre      = nombre;
        this.email       = email;
        this.contrasena  = contrasena;
        this.perfil      = perfil;
        this.rol         = rol;
        this.historial   = new ArrayList<>();
        this.listaDeseos = new ArrayList<>();
        this.opiniones   = new ArrayList<>();
    }
 
    public void agregarADeseos(Producto p) {
        for (Producto existente : listaDeseos) {
            if (existente.getId() == p.getId()) {
                System.out.println("El producto ya está en lista de deseos: " + p.getNombre());
                return;
            }
        }
        listaDeseos.add(p);
    }
 
    public boolean quitarDeDeseos(int idProducto) {
        for (int i = 0; i < listaDeseos.size(); i++) {
            if (listaDeseos.get(i).getId() == idProducto) {
                listaDeseos.remove(i);
                return true;
            }
        }
        return false;
    }
 
    public void registrarOpinion(Opinion op) {
        if (!op.esValida()) return;
        if (op.getIdUsuario() != this.id) return;
        for (Opinion existente : opiniones) {
            if (existente.getIdProducto() == op.getIdProducto()) return;
        }
        opiniones.add(op);
    }
 
    public void agregarAlHistorial(Pedido pedido) {
        for (Pedido p : historial) {
            if (p.getId() == pedido.getId()) return;
        }
        historial.add(pedido);
    }
 
    public int                  getId()          { return id; }
    public String               getNombre()      { return nombre; }
    public String               getEmail()       { return email; }
    public String               getContrasena()  { return contrasena; }
    public PerfilUsuario        getPerfil()      { return perfil; }
    public Rol                  getRol()         { return rol; }
    public ArrayList<Pedido>    getHistorial()   { return historial; }
    public ArrayList<Producto>  getListaDeseos() { return listaDeseos; }
    public ArrayList<Opinion>   getOpiniones()   { return opiniones; }
 
    @Override
    public String toString() {
        return "Usuario[" + id + "] " + nombre
             + " | " + email
             + " | " + rol
             + " | " + perfil.getTipo();
    }
}
