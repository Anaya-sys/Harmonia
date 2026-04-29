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
public class Categoria {
     private int                id;
    private String             nombre;
    private ArrayList<Categoria> subcategorias;

    public Categoria(int id, String nombre) {
        this.id            = id;
        this.nombre        = nombre;
        this.subcategorias = new ArrayList<>();
    }

  
    public void agregarSubcategoria(Categoria c) {
        for (Categoria sub : subcategorias) {
            if (sub.getId() == c.getId()) {
                System.out.println("Subcategoría ya existe: " + c.getNombre());
                return;
            }
        }
        subcategorias.add(c);
    }

   
    public Categoria buscarPorNombre(String nombre) {
        if (this.nombre.equalsIgnoreCase(nombre)) return this;

        for (Categoria sub : subcategorias) {
            Categoria resultado = sub.buscarPorNombre(nombre);
            if (resultado != null) return resultado;
        }
        return null;
    }

   
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public ArrayList<Categoria>  getSubcategorias() { return subcategorias; }

    @Override
    public String toString() {
        return "Categoria[" + id + "] " + nombre
             + " | subcategorias: " + subcategorias.size();
    }
}
