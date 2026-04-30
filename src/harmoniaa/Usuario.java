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
    private final Rol           rol = null;
    private ArrayList<Pedido>   historial;
    private ArrayList<Producto> listaDeseos;
    private ArrayList<Opinion>  opiniones;

    public Usuario(int id, String nombre, String email,
                   String contrasena, PerfilUsuario perfil) {
        this.id          = id;
        this.nombre      = nombre;
        this.email       = email;
        this.contrasena  = contrasena;
        this.perfil      = perfil;
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
        System.out.println("Agregado a deseos: " + p.getNombre());
    }

 
    public boolean quitarDeDeseos(int idProducto) {
        for (int i = 0; i < listaDeseos.size(); i++) {
            if (listaDeseos.get(i).getId() == idProducto) {
                System.out.println("Quitado de deseos: " + listaDeseos.get(i).getNombre());
                listaDeseos.remove(i);
                return true;
            }
        }
        System.out.println("Producto no encontrado en lista de deseos. Id: " + idProducto);
        return false;
    }


    public void registrarOpinion(Opinion op) {
        if (!op.esValida()) {
            System.out.println("Opinión inválida. Verifique calificación (1-5) y comentario.");
            return;
        }
        if (op.getIdUsuario() != this.id) {
            System.out.println("La opinión no pertenece a este usuario.");
            return;
        }
        for (Opinion existente : opiniones) {
            if (existente.getIdProducto() == op.getIdProducto()) {
                System.out.println("Ya existe una opinión para el producto id: " + op.getIdProducto());
                return;
            }
        }
        opiniones.add(op);
        System.out.println("Opinión registrada para el producto id: " + op.getIdProducto());
    }

    
    public void agregarAlHistorial(Pedido pedido) {
        for (Pedido p : historial) {
            if (p.getId() == pedido.getId()) {
                System.out.println("El pedido ya está en el historial. Id: " + pedido.getId());
                return;
            }
        }
        historial.add(pedido);
        System.out.println("Pedido agregado al historial. Id: " + pedido.getId());
    }

 
    public int                  getId()          { return id; }
    public String               getNombre()      { return nombre; }
    public String               getEmail()       { return email; }
    public String               getContrasena()  { return contrasena; }
    public PerfilUsuario        getPerfil()      { return perfil; }
    public ArrayList<Pedido>    getHistorial()   { return historial; }
    public ArrayList<Producto>  getListaDeseos() { return listaDeseos; }
    public ArrayList<Opinion>   getOpiniones()   { return opiniones; }

    @Override
    public String toString() {
        return "Usuario[" + id + "] " + nombre
             + " | " + email
             + " | " + perfil.getTipo()
             + " | deseos: "   + listaDeseos.size()
             + " | pedidos: "  + historial.size()
             + " | opiniones: " + opiniones.size();
    }
}
