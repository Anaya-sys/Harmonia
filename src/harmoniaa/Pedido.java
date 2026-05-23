/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author CARLOS ANAYA
 */
public class Pedido implements Serializable {
 
    private static final long serialVersionUID = 2L;
 
    private int                    id;
    private int                    idUsuario;
    private ArrayList<ItemCarrito> items;
    private EstadoPedido           estado;
    private LocalDate              fechaCreacion;
    private double                 total;
    private String                 direccionEnvio;
 
    // ══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR — para pedidos NUEVOS
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Crea un pedido nuevo con estado PENDIENTE y fecha de hoy.
     *
     * @param id             identificador único asignado por GestorPedidos
     * @param idUsuario      ID del comprador
     * @param items          lista de productos del carrito (no vacía)
     * @param direccionEnvio dirección de entrega
     */
    public Pedido(int id, int idUsuario, ArrayList<ItemCarrito> items, String direccionEnvio) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Un pedido debe tener al menos un ítem.");
        if (direccionEnvio == null || direccionEnvio.isBlank())
            throw new IllegalArgumentException("La dirección de envío no puede estar vacía.");
 
        this.id             = id;
        this.idUsuario      = idUsuario;
        this.items          = new ArrayList<>(items); // copia defensiva
        this.estado         = EstadoPedido.PENDIENTE;
        this.fechaCreacion  = LocalDate.now();
        this.direccionEnvio = direccionEnvio.trim();
        this.total          = calcularTotal();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  FACTORY — para reconstruir desde archivo (no dispara lógica de negocio)
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Reconstruye un {@code Pedido} a partir de datos leídos del archivo de texto.
     * <p>
     * A diferencia del constructor normal, este método restaura el estado, la fecha
     * y el total tal como estaban guardados, sin reasignarlos.
     * <p>
     * <b>Uso exclusivo de {@code GestorPedidos#cargarDesdeArchivo()}.</b>
     *
     * @param id        ID original del pedido
     * @param idUsuario ID del usuario comprador
     * @param items     ítems reconstruidos desde harmonia_items.txt
     * @param direccion dirección de envío guardada
     * @param estado    estado guardado en harmonia_pedidos.txt
     * @param fecha     fecha de creación original
     * @param total     total guardado (evita recalcular con precios que podrían haber cambiado)
     * @return          instancia lista para ser gestionada en memoria
     */
    public static Pedido reconstruir(int id, int idUsuario, ArrayList<ItemCarrito> items,
                                     String direccion, EstadoPedido estado,
                                     LocalDate fecha, double total) {
        // Usamos el constructor privado de reconstrucción para no disparar
        // la lógica de negocio de un pedido nuevo.
        Pedido p = new Pedido(id, idUsuario, items, direccion);
        p.estado        = estado;        // sobreescribir PENDIENTE por defecto
        p.fechaCreacion = fecha;         // sobreescribir LocalDate.now()
        p.total         = total;         // preservar total histórico
        return p;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  LÓGICA DE NEGOCIO
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Recalcula el total sumando los subtotales de todos los ítems.
     *
     * @return total actualizado
     */
    public double calcularTotal() {
        double suma = 0.0;
        for (ItemCarrito item : items) suma += item.calcularSubtotal();
        this.total = suma;
        return this.total;
    }
 
    /**
     * Avanza el estado del pedido.
     * Los retrocesos de estado son ignorados con un mensaje en consola.
     *
     * @param nuevoEstado estado de destino
     */
    public void actualizarEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado.ordinal() <= this.estado.ordinal()) {
            System.out.println("[Pedido #" + id + "] No se puede retroceder: "
                + this.estado + " → " + nuevoEstado);
            return;
        }
        this.estado = nuevoEstado;
        System.out.println("[Pedido #" + id + "] Estado actualizado → " + nuevoEstado);
    }
 
    /**
     * Genera un comprobante de texto con todos los detalles del pedido.
     *
     * @return cadena multilínea lista para mostrar o guardar
     */
    public String generarConfirmacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════╗\n");
        sb.append("  PEDIDO #").append(id).append("\n");
        sb.append("╠═══════════════════════════════════════╣\n");
        sb.append("  Fecha    : ").append(fechaCreacion).append("\n");
        sb.append("  Usuario  : #").append(idUsuario).append("\n");
        sb.append("  Envío a  : ").append(direccionEnvio).append("\n");
        sb.append("  Estado   : ").append(estado).append("\n");
        sb.append("╠═══════════════════════════════════════╣\n");
        for (ItemCarrito item : items) {
            sb.append("  • ").append(item.toString()).append("\n");
        }
        sb.append("╠═══════════════════════════════════════╣\n");
        sb.append(String.format("  TOTAL    : $%,.2f%n", total));
        sb.append("╚═══════════════════════════════════════╝");
        return sb.toString();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  GETTERS
    // ══════════════════════════════════════════════════════════════════════════
 
    public int                    getId()             { return id; }
    public int                    getIdUsuario()      { return idUsuario; }
    public ArrayList<ItemCarrito> getItems()          { return new ArrayList<>(items); }
    public EstadoPedido           getEstado()         { return estado; }
    public LocalDate              getFechaCreacion()  { return fechaCreacion; }
    public double                 getTotal()          { return total; }
    public String                 getDireccionEnvio() { return direccionEnvio; }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  SETTERS CONTROLADOS
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Permite al administrador corregir la dirección de envío.
     * La validación de que el pedido esté en estado PENDIENTE la realiza
     * {@code GestorPedidos#actualizarDireccion()}.
     *
     * @param nuevaDireccion nueva dirección de entrega (no nula ni vacía)
     */
    public void setDireccionEnvio(String nuevaDireccion) {
        if (nuevaDireccion == null || nuevaDireccion.isBlank())
            throw new IllegalArgumentException("La dirección no puede estar vacía.");
        this.direccionEnvio = nuevaDireccion.trim();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  OBJECT OVERRIDES
    // ══════════════════════════════════════════════════════════════════════════
 
    @Override
    public String toString() {
        return "Pedido[" + id + "]"
            + " usuario:#" + idUsuario
            + " | " + estado
            + " | $" + String.format("%,.2f", total)
            + " | " + fechaCreacion
            + " | " + items.size() + " ítems";
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido other)) return false;
        return this.id == other.id;
    }
 
    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
