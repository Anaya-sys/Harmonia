/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author CARLOS ANAYA
 */
public class Producto implements Serializable {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int  idMarca;
    private int  idCategoria;
    private ArrayList<Variante> variantes;
    private ArrayList<String>  demos;        

    public Producto(int id, String nombre, String descripcion,
                    double precio, int idMarca, int idCategoria) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.precio      = precio;
        this.idMarca     = idMarca;
        this.idCategoria = idCategoria;
        this.variantes   = new ArrayList<>();
        this.demos       = new ArrayList<>();
    }

 
    public void agregarVariante(Variante v) {
        for (Variante existente : variantes) {
            if (existente.getId() == v.getId()) {
                System.out.println("Variante ya registrada: " + v.getDescripcion());
                return;
            }
        }
        variantes.add(v);
    }

   
    public boolean eliminarVariante(int id) {
        for (int i = 0; i < variantes.size(); i++) {
            if (variantes.get(i).getId() == id) {
                variantes.remove(i);
                return true;
            }
        }
        System.out.println("Variante no encontrada con id: " + id);
        return false;
    }

    
    public boolean estaDisponible() {
        if (variantes.isEmpty()) return true;

        for (Variante v : variantes) {
            if (v.hayStock()) return true;
        }
        return false;
    }

  
    public Variante getVariante(int id) {
        for (Variante v : variantes) {
            if (v.getId() == id) return v;
        }
        return null;
    }

  
    public int                  getId()          { return id; }
    public String               getNombre()      { return nombre; }
    public String               getDescripcion() { return descripcion; }
    public double               getPrecio()      { return precio; }
    public int                  getIdMarca()     { return idMarca; }
    public int                  getIdCategoria() { return idCategoria; }
    public ArrayList<Variante>  getVariantes()   { return variantes; }
    public ArrayList<String>    getDemos()       { return demos; }

    public void agregarDemo(String demo) { demos.add(demo); }

    @Override
    public String toString() {
        return "Producto[" + id + "] " + nombre
             + " | $" + precio
             + " | disponible: " + estaDisponible()
             + " | variantes: " + variantes.size();
    }
}
