/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

/**
 *
 * @author CARLOS ANAYA
 */
public class Marca {
    private int id;
    private String nombre;
    private String pais;

    public Marca(int id, String nombre, String pais) {
        this.id     = id;
        this.nombre = nombre;
        this.pais   = pais;
    }

    public int    getId()     { return id; }
    public String getNombre() { return nombre; }
    public String getPais()   { return pais; }

    @Override
    public String toString() {
        return "Marca[" + id + "] " + nombre + " (" + pais + ")";
    }
}
