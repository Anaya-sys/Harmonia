package harmoniaa;


import java.io.*; 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class Gestorpedidos {
    private static final String ARCHIVO_PEDIDOS = "harmonia_pedidos.txt";
    private static final String ARCHIVO_ITEMS   = "harmonia_items.txt";
    private static final String SEP             = "|";
 
    // ── ESTRUCTURAS EN MEMORIA ────────────────────────────────────────────────
    private final ArrayList<Pedido>  registroPedidos = new ArrayList<>();
    private final LinkedList<Pedido> colaPendientes  = new LinkedList<>();
    private int contadorId = 1;
 
    // ══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════════════
 
    public Gestorpedidos() {
        cargarDesdeArchivo();
        if (!new File(ARCHIVO_PEDIDOS).exists() || !new File(ARCHIVO_ITEMS).exists()) {
        guardarEnArchivo();
    }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  PERSISTENCIA — GUARDAR  (simula COMMIT / FLUSH)
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Sobreescribe ambos archivos con el estado actual de la memoria.
     * Equivale a TRUNCATE + INSERT ALL en una base de datos relacional.
     */
    private void guardarEnArchivo() {
        guardarTablaPedidos();
        guardarTablaItems();
        System.out.println("[DB] Commit → " + registroPedidos.size() + " pedidos persistidos.");
    }
 
    private void guardarTablaPedidos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            bw.write("# id|idUsuario|estado|fechaCreacion|total|direccionEnvio");
            bw.newLine();
            for (Pedido p : registroPedidos) {
                bw.write(String.join(SEP,
                    String.valueOf(p.getId()),
                    String.valueOf(p.getIdUsuario()),
                    p.getEstado().name(),
                    p.getFechaCreacion().toString(),
                    String.format(Locale.US, "%.2f", p.getTotal()),
                    escapar(p.getDireccionEnvio())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("⚠ [DB] Error guardando tabla PEDIDOS: " + e.getMessage());
        }
    }
 
    private void guardarTablaItems() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ITEMS))) {
            bw.write("# idPedido|idProducto|nombreProducto|precioBase|idVariante|descVariante|precioExtra|cantidad");
            bw.newLine();
            for (Pedido p : registroPedidos) {
                for (ItemCarrito item : p.getItems()) {
                    int    idVar   = item.getVariante() != null ? item.getVariante().getId()          : -1;
                    String descVar = item.getVariante() != null ? item.getVariante().getDescripcion() : "";
                    double extVar  = item.getVariante() != null ? item.getVariante().getPrecioExtra() : 0.0;
 
                    bw.write(String.join(SEP,
                        String.valueOf(p.getId()),
                        String.valueOf(item.getProducto().getId()),
                        escapar(item.getProducto().getNombre()),
                        String.format(Locale.US, "%.2f", item.getProducto().getPrecio()),
                        String.valueOf(idVar),
                        escapar(descVar),
                        String.format(Locale.US, "%.2f", extVar),
                        String.valueOf(item.getCantidad())
                    ));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("⚠ [DB] Error guardando tabla ITEMS: " + e.getMessage());
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  PERSISTENCIA — CARGAR  (simula SELECT * FROM + JOIN)
    // ══════════════════════════════════════════════════════════════════════════
 
    private void cargarDesdeArchivo() {
        Map<Integer, PedidoCabecera> cabeceras = leerTablaPedidos();
        Map<Integer, ArrayList<ItemCarrito>> itemsMap = leerTablaItems();
 
        registroPedidos.clear();
        colaPendientes.clear();
 
        for (PedidoCabecera cab : cabeceras.values()) {
            ArrayList<ItemCarrito> items = itemsMap.getOrDefault(cab.id, new ArrayList<>());
            if (items.isEmpty()) {
                System.err.println("⚠ [DB] Pedido #" + cab.id + " sin ítems — se omite al cargar.");
                continue;
            }
            Pedido p = Pedido.reconstruir(
                cab.id, cab.idUsuario, items,
                cab.direccion, cab.estado, cab.fecha, cab.total
            );
            registroPedidos.add(p);
            if (p.getEstado() == EstadoPedido.PENDIENTE) colaPendientes.offer(p);
            if (cab.id >= contadorId) contadorId = cab.id + 1;
        }
 
        System.out.println("[DB] SELECT * → " + registroPedidos.size() + " pedidos cargados.");
    }
 
    /** Lee harmonia_pedidos.txt → Map<id, cabecera> */
    private Map<Integer, PedidoCabecera> leerTablaPedidos() {
        Map<Integer, PedidoCabecera> mapa = new LinkedHashMap<>();
        Path ruta = Paths.get(ARCHIVO_PEDIDOS);
        if (!Files.exists(ruta)) return mapa;
 
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_PEDIDOS))) {
            String linea;
            int nLinea = 0;
            while ((linea = br.readLine()) != null) {
                nLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
 
                String[] c = linea.split("\\" + SEP, -1);
                if (c.length < 6) {
                    System.err.println("⚠ [DB] Línea " + nLinea + " malformada en PEDIDOS, se omite.");
                    continue;
                }
                try {
                    int          id        = Integer.parseInt(c[0].trim());
                    int          idUsuario = Integer.parseInt(c[1].trim());
                    EstadoPedido estado    = EstadoPedido.valueOf(c[2].trim());
                    LocalDate    fecha     = LocalDate.parse(c[3].trim());
                    double       total     = Double.parseDouble(c[4].trim());
                    String       direccion = desescapar(c[5]);
                    mapa.put(id, new PedidoCabecera(id, idUsuario, estado, fecha, total, direccion));
                } catch (Exception ex) {
                    System.err.println("⚠ [DB] Error parseando línea " + nLinea + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("⚠ [DB] No se pudo abrir " + ARCHIVO_PEDIDOS + ": " + e.getMessage());
        }
        return mapa;
    }
 
    /** Lee harmonia_items.txt → Map<idPedido, List<ItemCarrito>> */
    private Map<Integer, ArrayList<ItemCarrito>> leerTablaItems() {
        Map<Integer, ArrayList<ItemCarrito>> mapa = new HashMap<>();
        Path ruta = Paths.get(ARCHIVO_ITEMS);
        if (!Files.exists(ruta)) return mapa;
 
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_ITEMS))) {
            String linea;
            int nLinea = 0;
            while ((linea = br.readLine()) != null) {
                nLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
 
                String[] c = linea.split("\\" + SEP, -1);
                if (c.length < 8) {
                    System.err.println("⚠ [DB] Línea " + nLinea + " malformada en ITEMS, se omite.");
                    continue;
                }
                try {
                    int    idPedido    = Integer.parseInt(c[0].trim());
                    int    idProducto  = Integer.parseInt(c[1].trim());
                    String nomProd     = desescapar(c[2]);
                    double precioProd  = Double.parseDouble(c[3].trim());
                    int    idVariante  = Integer.parseInt(c[4].trim());
                    String descVar     = desescapar(c[5]);
                    double precioExtra = Double.parseDouble(c[6].trim());
                    int    cantidad    = Integer.parseInt(c[7].trim());
 
                    // Reconstruir objetos de dominio a partir de los datos almacenados
                    Producto prod = new Producto(idProducto, nomProd, "", precioProd, 0, 0);
                    Variante var  = (idVariante == -1) ? null
                                  : new Variante(idVariante, descVar, precioExtra, 0);
                    ItemCarrito item = new ItemCarrito(prod, var, cantidad);
 
                    mapa.computeIfAbsent(idPedido, k -> new ArrayList<>()).add(item);
                } catch (Exception ex) {
                    System.err.println("⚠ [DB] Error parseando ítem línea " + nLinea + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("⚠ [DB] No se pudo abrir " + ARCHIVO_ITEMS + ": " + e.getMessage());
        }
        return mapa;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  OPERACIONES CRUD
    // ══════════════════════════════════════════════════════════════════════════
 
    // ── INSERT ─────────────────────────────────────────────────────────────────
 
    /**
     * Crea un nuevo pedido, lo encola en la FIFO de pendientes y lo persiste.
     * Equivale a: INSERT INTO pedidos + INSERT INTO items_pedido
     */
    public Pedido crearPedido(int idUsuario, ArrayList<ItemCarrito> items, String direccionEnvio) {
        Pedido p = new Pedido(contadorId++, idUsuario, items, direccionEnvio);
        registroPedidos.add(p);
        colaPendientes.offer(p);
        guardarEnArchivo();
        System.out.println("[DB] INSERT pedido #" + p.getId() + " para usuario #" + idUsuario);
        return p;
    }
 
    // ── UPDATE ─────────────────────────────────────────────────────────────────
 
    /**
     * Avanza el estado de un pedido.
     * Equivale a: UPDATE pedidos SET estado = ? WHERE id = ?
     */
    
    public void recargarDatos() {
        cargarDesdeArchivo();
    }
    
    public boolean avanzarEstado(int idPedido, EstadoPedido nuevoEstado) {
        Pedido p = buscarPorId(idPedido);
        if (p == null) return false;
 
        EstadoPedido estadoAntes = p.getEstado();
        p.actualizarEstado(nuevoEstado);
 
        // actualizarEstado() bloquea retrocesos; si no cambió, no persistir
        if (p.getEstado() == estadoAntes) return false;
 
        // CORRECCIÓN: Si el pedido ya no es PENDIENTE (sin importar a qué estado pasó),
        // debemos sacarlo obligatoriamente de la cola FIFO.
        if (estadoAntes == EstadoPedido.PENDIENTE && nuevoEstado != EstadoPedido.PENDIENTE) {
            colaPendientes.remove(p);
        }
 
        guardarEnArchivo();
        System.out.println("[DB] UPDATE pedido #" + idPedido + " → " + nuevoEstado);
        return true;
    }
 
    /**
     * Cancela un pedido si aún es PENDIENTE o EN_PROCESO.
     * Equivale a: DELETE FROM pedidos WHERE id = ? (borrado lógico)
     */
    public boolean cancelarPedido(int idPedido) {
        Pedido p = buscarPorId(idPedido);
        if (p == null) return false;
 
        EstadoPedido estado = p.getEstado();
        if (estado == EstadoPedido.ENVIADO || estado == EstadoPedido.ENTREGADO) {
            System.out.println("[DB] CANCEL rechazado — pedido #" + idPedido + " ya está " + estado);
            return false;
        }
 
        registroPedidos.remove(p);
        colaPendientes.remove(p);
        guardarEnArchivo();
        System.out.println("[DB] DELETE pedido #" + idPedido + " (estado anterior: " + estado + ")");
        return true;
    }
 
    /**
     * Actualiza la dirección de envío de un pedido PENDIENTE.
     * Equivale a: UPDATE pedidos SET direccionEnvio = ? WHERE id = ?
     */
    public boolean actualizarDireccion(int idPedido, String nuevaDireccion) {
        if (nuevaDireccion == null || nuevaDireccion.isBlank()) return false;
        Pedido p = buscarPorId(idPedido);
        if (p == null || p.getEstado() != EstadoPedido.PENDIENTE) return false;
 
        p.setDireccionEnvio(nuevaDireccion.trim());
        guardarEnArchivo();
        System.out.println("[DB] UPDATE pedido #" + idPedido + " dirección actualizada.");
        return true;
    }
 
    // ── QUEUE — FIFO ───────────────────────────────────────────────────────────
 
    /**
     * Procesa el siguiente pedido pendiente de la cola FIFO.
     * Equivale a: UPDATE pedidos SET estado='EN_PROCESO' WHERE id = (head de la cola)
     */
    public Pedido procesarSiguiente() {
        Pedido p = colaPendientes.poll();
        if (p != null) {
            p.actualizarEstado(EstadoPedido.EN_PROCESO);
            guardarEnArchivo();
            System.out.println("[DB] FIFO → procesado pedido #" + p.getId());
        }
        return p;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  CONSULTAS — SELECT
    // ══════════════════════════════════════════════════════════════════════════
 
    public ArrayList<Pedido> getTodos() {
        return new ArrayList<>(registroPedidos);
    }
 
    public Pedido buscarPorId(int id) {
        return registroPedidos.stream()
            .filter(p -> p.getId() == id)
            .findFirst().orElse(null);
    }
 
    public ArrayList<Pedido> getPorUsuario(int idUsuario) {
        return registroPedidos.stream()
            .filter(p -> p.getIdUsuario() == idUsuario)
            .collect(Collectors.toCollection(ArrayList::new));
    }
 
    public ArrayList<Pedido> getPorEstado(EstadoPedido estado) {
        return registroPedidos.stream()
            .filter(p -> p.getEstado() == estado)
            .collect(Collectors.toCollection(ArrayList::new));
    }
 
    /**
     * Búsqueda de texto libre — busca en: id, idUsuario, dirección, fecha.
     * Equivale a: SELECT * WHERE ... LIKE '%texto%'
     */
    public ArrayList<Pedido> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) return getTodos();
        String q = texto.toLowerCase(Locale.ROOT).trim();
        return registroPedidos.stream().filter(p ->
            String.valueOf(p.getId()).contains(q)
            || String.valueOf(p.getIdUsuario()).contains(q)
            || p.getDireccionEnvio().toLowerCase(Locale.ROOT).contains(q)
            || p.getFechaCreacion().toString().contains(q)
        ).collect(Collectors.toCollection(ArrayList::new));
    }
 
    /**
     * Devuelve los N pedidos más recientes del registro.
     * Equivale a: SELECT * ORDER BY id DESC LIMIT n
     */
    public ArrayList<Pedido> getPedidosMasRecientes(int n) {
        int desde = Math.max(0, registroPedidos.size() - n);
        ArrayList<Pedido> recientes = new ArrayList<>(
            registroPedidos.subList(desde, registroPedidos.size())
        );
        Collections.reverse(recientes);
        return recientes;
    }
 
    // ── Métricas ───────────────────────────────────────────────────────────────
 
    public int    getTotalPedidos()    { return registroPedidos.size(); }
    public int    getCantidadEnCola()  { return colaPendientes.size(); }
    public long   getTotalEntregados() {
        return registroPedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO).count();
    }
 
    public double getIngresoTotal() {
        return registroPedidos.stream().mapToDouble(Pedido::getTotal).sum();
    }
 
    /**
     * Conteo de pedidos por cada estado.
     * Equivale a: SELECT estado, COUNT(*) FROM pedidos GROUP BY estado
     */
    public Map<EstadoPedido, Long> getConteosPorEstado() {
        Map<EstadoPedido, Long> mapa = new LinkedHashMap<>();
        for (EstadoPedido e : EstadoPedido.values()) {
            mapa.put(e, registroPedidos.stream()
                .filter(p -> p.getEstado() == e).count());
        }
        return mapa;
    }
 
    /**
     * Ingresos agrupados por estado.
     * Equivale a: SELECT estado, SUM(total) FROM pedidos GROUP BY estado
     */
    public Map<EstadoPedido, Double> getIngresosPorEstado() {
        Map<EstadoPedido, Double> mapa = new LinkedHashMap<>();
        for (EstadoPedido e : EstadoPedido.values()) {
            mapa.put(e, registroPedidos.stream()
                .filter(p -> p.getEstado() == e)
                .mapToDouble(Pedido::getTotal).sum());
        }
        return mapa;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  EXPORTAR REPORTE
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Genera un archivo de texto legible con todos los pedidos y métricas.
     * Ideal para adjuntar como reporte al cliente.
     *
     * @param rutaArchivo ruta destino, ej. "reporte_pedidos.txt"
     */
    public void exportarReporte(String rutaArchivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo), true)) {
            pw.println("══════════════════════════════════════════════════════");
            pw.println("  REPORTE DE PEDIDOS — Harmonia Music Store");
            pw.println("  Generado: " + LocalDate.now());
            pw.println("══════════════════════════════════════════════════════");
            pw.println();
            pw.printf("  Total pedidos   : %d%n",     getTotalPedidos());
            pw.printf("  Ingreso total   : $%,.2f%n", getIngresoTotal());
            pw.printf("  En cola (FIFO)  : %d%n",     getCantidadEnCola());
            pw.printf("  Entregados      : %d%n",     getTotalEntregados());
            pw.println();
            pw.println("  Desglose por estado:");
            getConteosPorEstado().forEach((estado, cant) ->
                pw.printf("    %-12s → %d pedidos  ($%,.2f)%n",
                    estado, cant, getIngresosPorEstado().getOrDefault(estado, 0.0))
            );
            pw.println();
            pw.println("──────────────────────────────────────────────────────");
 
            for (Pedido p : registroPedidos) {
                pw.println(p.generarConfirmacion());
                pw.println();
            }
 
            pw.println("══════════════════════════════════════════════════════");
            System.out.println("[DB] Reporte exportado → " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("⚠ [DB] Error al exportar reporte: " + e.getMessage());
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ══════════════════════════════════════════════════════════════════════════
 
    /** Solo carga demos si la BD está vacía (primera ejecución). */
    public void cargarDatosDemo(List<Producto> catalogoProductos) {
        if (!registroPedidos.isEmpty()) return;
        System.out.println("[DB] BD vacía — puedes agregar pedidos demo aquí.");
        guardarEnArchivo();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  UTILIDADES INTERNAS
    // ══════════════════════════════════════════════════════════════════════════
 
    /** Escapa el separador '|' y saltos de línea dentro de un valor de campo. */
    private String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace(SEP, "\\|")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
 
    /** Restaura un valor de campo que fue escapado previamente. */
    private String desescapar(String s) {
        if (s == null) return "";
        return s.replace("\\\\", "\\")
                .replace("\\|", SEP)
                .replace("\\n", "\n");
    }
 
    // ── DTO interno: cabecera de pedido sin ítems (para la carga por fases) ────
    private static final class PedidoCabecera {
        final int          id, idUsuario;
        final EstadoPedido estado;
        final LocalDate    fecha;
        final double       total;
        final String       direccion;
 
        PedidoCabecera(int id, int idUsuario, EstadoPedido estado,
                       LocalDate fecha, double total, String direccion) {
            this.id        = id;
            this.idUsuario = idUsuario;
            this.estado    = estado;
            this.fecha     = fecha;
            this.total     = total;
            this.direccion = direccion;
        }
    }
}