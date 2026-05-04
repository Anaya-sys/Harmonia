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
public class Listadeseosstack {
        private final Deque<Producto> pila = new ArrayDeque<>();
 

    public boolean agregar(Producto p) {
        for (Producto existente : pila) {
            if (existente.getId() == p.getId()) {
                return false;   
            }
        }
        pila.push(p);
        return true;
    }
 
   
    public Producto deshacer() {
        return pila.isEmpty() ? null : pila.pop();
    }
 
 
    public Producto verUltimo() {
        return pila.isEmpty() ? null : pila.peek();
    }
 
    
   
    public boolean quitar(int idProducto) {
        Deque<Producto> auxiliar = new ArrayDeque<>();
        boolean eliminado = false;
 
        while (!pila.isEmpty()) {
            Producto p = pila.pop();
            if (p.getId() == idProducto && !eliminado) {
                eliminado = true;
            } else {
                auxiliar.push(p);
            }
        }
 
        while (!auxiliar.isEmpty()) {
            pila.push(auxiliar.pop());
        }
        return eliminado;
    }
 
    
    public boolean contiene(int idProducto) {
        for (Producto p : pila) {
            if (p.getId() == idProducto) return true;
        }
        return false;
    }
 
    
    public boolean isEmpty()     { return pila.isEmpty(); }
    public int     size()        { return pila.size(); }
    public void    vaciar()      { pila.clear(); }
 
   
    public List<Producto> getProductos() {
        return new ArrayList<>(pila);
    }
 
    @Override
    public String toString() {
        return "ListaDeseosStack[" + pila.size() + " productos]";
    }
}
