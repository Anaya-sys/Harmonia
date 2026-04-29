/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

/**
 *
 * @author CARLOS ANAYA
 */
public class Variante {
      private int    id;
    private String descripcion;
    private double precioExtra;
    private int    stock;

    public Variante(int id, String descripcion, double precioExtra, int stock) {
        this.id          = id;
        this.descripcion = descripcion;
        this.precioExtra = precioExtra;
        this.stock       = stock;
    }

    public void reducirStock(int n) {
        if (n <= 0) throw new IllegalArgumentException("Cantidad debe ser mayor a 0.");
        if (n > this.stock) throw new IllegalStateException("Stock insuficiente para la variante: " + descripcion);
        this.stock -= n;
    }

    
    public boolean hayStock() {
        return this.stock > 0;
    }

 
    public int getId(){ return id; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioExtra() { return precioExtra; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        return "Variante[" + id + "] " + descripcion
             + " | +$" + precioExtra + " | stock: " + stock;
    }
}
