/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;
import java.io.Serializable;
/**
 *
 * @author CARLOS ANAYA
 */
public class ItemCarrito implements Serializable {
     private Producto producto;
    private Variante variante;      
    private int      cantidad;
    private double   precioUnitario;

    public ItemCarrito(Producto producto, Variante variante, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        this.producto       = producto;
        this.variante       = variante;
        this.cantidad       = cantidad;
        this.precioUnitario = producto.getPrecio()
                            + (variante != null ? variante.getPrecioExtra() : 0.0);
    }


    public double calcularSubtotal() {
        return precioUnitario * cantidad;
    }

    public void actualizarCantidad(int n) {
        if (n <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        this.cantidad = n;
    }

    public Producto getProducto()       { return producto; }
    public Variante getVariante()       { return variante; }
    public int      getCantidad()       { return cantidad; }
    public double   getPrecioUnitario() { return precioUnitario; }

    @Override
    public String toString() {
        String variante = this.variante != null ? " [" + this.variante.getDescripcion() + "]" : "";
        return producto.getNombre() + variante
             + " x" + cantidad
             + " | $" + precioUnitario + " c/u"
             + " | subtotal: $" + calcularSubtotal();
    }
    
}
