/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author CARLOS ANAYA
 */
public class Pedido {
     private int                   id;
    private int                   idUsuario;
    private ArrayList<ItemCarrito> items;
    private EstadoPedido           estado;
    private LocalDate              fechaCreacion;
    private double                 total;
    private String                 direccionEnvio;

    public Pedido(int id, int idUsuario, ArrayList<ItemCarrito> items,
                  String direccionEnvio) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Un pedido debe tener al menos un item.");
        this.id             = id;
        this.idUsuario      = idUsuario;
        this.items          = new ArrayList<>(items); // copia defensiva
        this.estado         = EstadoPedido.PENDIENTE;
        this.fechaCreacion  = LocalDate.now();
        this.direccionEnvio = direccionEnvio;
        this.total          = calcularTotal();
    }

  
    public double calcularTotal() {
        double suma = 0.0;
        for (ItemCarrito item : items) {
            suma += item.calcularSubtotal();
        }
        this.total = suma;
        return this.total;
    }

  
    public void actualizarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado.ordinal() <= this.estado.ordinal()) {
            System.out.println("No se puede retroceder el estado. Actual: "
                             + this.estado + " | Solicitado: " + nuevoEstado);
            return;
        }
        this.estado = nuevoEstado;
        System.out.println("Pedido [" + id + "] actualizado a: " + nuevoEstado);
    }

  
    public String generarConfirmacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("========= CONFIRMACIÓN DE PEDIDO =========\n");
        sb.append("Pedido #").append(id).append("\n");
        sb.append("Fecha   : ").append(fechaCreacion).append("\n");
        sb.append("Envío a : ").append(direccionEnvio).append("\n");
        sb.append("Estado  : ").append(estado).append("\n");
        sb.append("------------------------------------------\n");
        for (ItemCarrito item : items) {
            sb.append("  ").append(item.toString()).append("\n");
        }
        sb.append("------------------------------------------\n");
        sb.append("TOTAL   : $").append(String.format("%.2f", total)).append("\n");
        sb.append("==========================================");
        return sb.toString();
    }


    public int                    getId()             { return id; }
    public int                    getIdUsuario()      { return idUsuario; }
    public ArrayList<ItemCarrito> getItems()          { return items; }
    public EstadoPedido           getEstado()         { return estado; }
    public LocalDate              getFechaCreacion()  { return fechaCreacion; }
    public double                 getTotal()          { return total; }
    public String                 getDireccionEnvio() { return direccionEnvio; }

    @Override
    public String toString() {
        return "Pedido[" + id + "] usuario:" + idUsuario
             + " | " + estado
             + " | $" + String.format("%.2f", total)
             + " | " + fechaCreacion;
    }
}
