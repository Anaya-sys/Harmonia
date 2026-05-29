/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author CARLOS ANAYA
 */
public class Carritostack {
      private final Deque<ItemCarrito> pila = new ArrayDeque<>();
 
    
    public void agregar(ItemCarrito item) {
        
        for (ItemCarrito existente : pila) {
            boolean mismoProducto  = existente.getProducto().getId() == item.getProducto().getId();
            boolean mismaVariante  = (existente.getVariante() == null && item.getVariante() == null)
                || (existente.getVariante() != null && item.getVariante() != null
                    && existente.getVariante().getId() == item.getVariante().getId());
            if (mismoProducto && mismaVariante) {
                existente.actualizarCantidad(existente.getCantidad() + item.getCantidad());
                return;
            }
        }
        pila.push(item);
    }
 
   
    public ItemCarrito deshacer() {
        return pila.isEmpty() ? null : pila.pop();
    }
 
    
    public ItemCarrito verUltimo() {
        return pila.isEmpty() ? null : pila.peek();
    }
 
    
   
    public boolean quitar(int idProducto, int idVariante) {
        Deque<ItemCarrito> auxiliar = new ArrayDeque<>();
        boolean eliminado = false;
 
        while (!pila.isEmpty()) {
            ItemCarrito item = pila.pop();
            boolean mismoProducto = item.getProducto().getId() == idProducto;
            boolean mismaVariante = (item.getVariante() == null && idVariante == -1)
                || (item.getVariante() != null && item.getVariante().getId() == idVariante);
 
            if (mismoProducto && mismaVariante && !eliminado) {
                eliminado = true;          
            } else {
                auxiliar.push(item);       
            }
            System.out.println();
        }
 
      
        while (!auxiliar.isEmpty()) {
            pila.push(auxiliar.pop());
        }
        return eliminado;
    }
 
    
    public double calcularTotal() {
        double total = 0.0;
        for (ItemCarrito item : pila) {
            total += item.calcularSubtotal();
        }
        return total;
    }
 
    
    public boolean isEmpty()        { return pila.isEmpty(); }
    public int     size()           { return pila.size(); }
    public void    vaciar()         { pila.clear(); }
 
    
    public List<ItemCarrito> getItems() {
        return new ArrayList<>(pila);
    }
 
    @Override
    public String toString() {
        return "CarritoStack[" + pila.size() + " ítems | total=$"
             + String.format("%.2f", calcularTotal()) + "]";
    }
}
