/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author CARLOS ANAYA
 */
public class CatalogoStore {
private static final String ARCHIVO = "harmonia_catalogo_extra.txt";
    private static final String SEP     = "|";
 
    private static final List<Producto>      extras       = new ArrayList<>();
    private static final Map<Integer,String> imagenesAdmin = new HashMap<>();
    private static boolean cargado = false;

    /**
     * Referencia al catálogo base (los 14 productos hardcodeados en DashboardController).
     * Se registra una sola vez al arrancar la aplicación.
     */
    private static List<Producto> catalogoBase = new ArrayList<>();
    private static final java.util.List<Runnable> listeners = new java.util.ArrayList<>();

    public static void addChangeListener(Runnable listener) {
    listeners.add(listener);
    }

    private static void notificarCambios() {
    for (Runnable listener : listeners) {
        listener.run();
    }
}
    private CatalogoStore() {}
 
    // ── API pública ───────────────────────────────────────────────────────────
 
    /** Devuelve la lista inmutable de productos agregados por el admin. */
    public static synchronized List<Producto> getExtras() {
        if (!cargado) { cargar(); cargado = true; }
        return Collections.unmodifiableList(extras);
    }

    /**
     * Registra la lista de productos base (los 14 hardcodeados).
     * Llamar una sola vez desde DashboardController.cargarCatalogoDatos().
     */
    public static synchronized void registrarBase(List<Producto> lista) {
        catalogoBase = lista; // referencia directa; las variantes se actualizan en vivo
    }

    /** Devuelve la lista inmutable de productos base. */
    public static synchronized List<Producto> getBase() {
        return Collections.unmodifiableList(catalogoBase);
    }

    /**
     * Devuelve TODOS los productos (base + extras).
     * Usado por el panel de administración para mostrar el inventario completo.
     */
    public static synchronized List<Producto> getTodos() {
        if (!cargado) { cargar(); cargado = true; }
        List<Producto> todos = new ArrayList<>(catalogoBase);
        for (Producto e : extras) {
            boolean yaExiste = todos.stream().anyMatch(p -> p.getId() == e.getId());
            if (!yaExiste) todos.add(e);
        }
        return Collections.unmodifiableList(todos);
    }

    /**
     * Busca un producto por ID en base + extras.
     * @return el Producto encontrado, o null si no existe.
     */
    public static synchronized Producto buscarPorId(int id) {
        for (Producto p : catalogoBase) if (p.getId() == id) return p;
        for (Producto p : extras)       if (p.getId() == id) return p;
        return null;
    }

    /**
     * Reabastecer un producto: añade {@code n} unidades a su primera variante.
     * Para productos extras persiste el cambio en disco inmediatamente.
     *
     * @param idProducto ID del producto a reabastecer
     * @param n          unidades a añadir
     * @return true si el producto fue encontrado y actualizado
     */
    public static synchronized boolean restockProducto(int idProducto, int n) {
        Producto p = buscarPorId(idProducto);
        if (p == null || p.getVariantes().isEmpty()) return false;
        try {
            p.getVariantes().get(0).agregarStock(n);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        
        int nuevoStockTotal = p.getVariantes().stream().mapToInt(Variante::getStock).sum();
        StockStore.registrar(idProducto, p.getNombre(), nuevoStockTotal, p.getIdCategoria());
        StockStore.guardarStock(); // <-- ¡NUEVO! Salva todo el inventario
        // Solo persiste extras; los base se gestionan en memoria
        boolean esExtra = extras.stream().anyMatch(e -> e.getId() == idProducto);
        if (esExtra) guardar();
        notificarCambios();
        return true;
    }

    /**
     * Reduce el stock de la variante del ítem comprado y persiste si es extra.
     * Llamar desde DashboardController.handleConfirmarPedido().
     */
    public static synchronized void reducirStockCompra(int idProducto, Variante variante, int cantidad) {
        if (variante == null) return;
        try {
            variante.reducirStock(cantidad);
        } catch (IllegalStateException ex) {
            // Stock insuficiente — se redujo a 0 como máximo
            System.err.println("⚠ CatalogoStore: " + ex.getMessage());
        }
        // Persistir en archivo solo si es un producto extra
        boolean esExtra = extras.stream().anyMatch(e -> e.getId() == idProducto);
        if (esExtra) guardar();
    }

    /** Persiste el estado actual al archivo. Llamable externamente. */
    public static synchronized void persistir() {
        guardar();
    }
 
    /**
     * Agrega un nuevo producto y lo persiste en el archivo.
     * También lo registra en StockStore para el control de stock.
     *
     * @param p         producto a agregar (ya con variante estándar)
     * @param imagePath ruta absoluta a la imagen en disco, o null/vacío si no hay
     */
    public static synchronized void agregar(Producto p, String imagePath) {
        if (!cargado) { cargar(); cargado = true; }
        extras.add(p);
        if (imagePath != null && !imagePath.isBlank()) {
            imagenesAdmin.put(p.getId(), imagePath.trim());
        }
        // Registrar en StockStore para el panel de alertas del admin
        // Usar la SUMA de todas las variantes (no sólo la primera)
        int stock = p.getVariantes().stream().mapToInt(Variante::getStock).sum();
        StockStore.registrar(p.getId(), p.getNombre(), stock, p.getIdCategoria());
        guardar();
        notificarCambios();
    }
 
    /**
     * Sobreescritura compatible hacia atrás — sin imagen.
     * Equivalente a llamar agregar(p, null).
     */
    public static synchronized void agregar(Producto p) {
        agregar(p, null);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ELIMINAR PRODUCTO  ← NUEVA FUNCIONALIDAD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Elimina un producto del catálogo según su tipo de origen:
     *
     *  • Productos EXTRAS (admin): se eliminan de {@code extras}, de
     *    {@code imagenesAdmin} y de {@code StockStore}, y el archivo
     *    harmonia_catalogo_extra.txt se reescribe sin ese registro.
     *
     *  • Productos BASE (hardcodeados): se eliminan de {@code catalogoBase} y
     *    de {@code StockStore} solo en memoria (sesión actual).
     *    No hay archivo que modificar para los base.
     *
     * @param idProducto ID del producto a eliminar
     * @return {@code true} si fue encontrado y eliminado; {@code false} si no existe.
     */
    public static synchronized boolean eliminar(int idProducto) {
        if (!cargado) { cargar(); cargado = true; }

        // ── 1. Buscar en extras ───────────────────────────────────────────────
        boolean eliminadoDeExtras = extras.removeIf(p -> p.getId() == idProducto);

        if (eliminadoDeExtras) {
            // Limpiar imagen asociada
            imagenesAdmin.remove(idProducto);
            // Quitar del StockStore (evita alertas de stock fantasma)
            StockStore.eliminar(idProducto);
            // Persistir el archivo sin ese producto
            guardar();
            notificarCambios();
            System.out.println("[CatalogoStore] Producto extra #" + idProducto + " eliminado y archivo actualizado.");
            return true;
        }

        // ── 2. Buscar en catalogoBase ─────────────────────────────────────────
        boolean eliminadoDeBase = catalogoBase.removeIf(p -> p.getId() == idProducto);

        if (eliminadoDeBase) {
            // Solo eliminación en memoria; los base no tienen archivo propio
            StockStore.eliminar(idProducto);
            notificarCambios();
            System.out.println("[CatalogoStore] Producto base #" + idProducto + " eliminado de memoria (sesión actual).");
            return true;
        }

        System.err.println("⚠ CatalogoStore: eliminar() — producto #" + idProducto + " no encontrado.");
        return false;
    }

    /**
     * Indica si un producto es de tipo "extra" (creado por el admin).
     * Útil para mostrar advertencia diferenciada en la UI antes de eliminar.
     *
     * @param idProducto ID a consultar
     * @return {@code true} si está en {@code extras}; {@code false} si es base o no existe.
     */
    public static synchronized boolean esExtra(int idProducto) {
        if (!cargado) { cargar(); cargado = true; }
        return extras.stream().anyMatch(p -> p.getId() == idProducto);
    }

    // ─────────────────────────────────────────────────────────────────────────
 
    /** Devuelve el próximo ID disponible para productos admin (≥ 100). */
    public static synchronized int siguienteId() {
        if (!cargado) { cargar(); cargado = true; }
        int max = 99;
        for (Producto p : extras) if (p.getId() > max) max = p.getId();
        return max + 1;
    }
 
    /**
     * Devuelve la ruta de imagen en disco para un producto admin,
     * o null si no tiene imagen registrada.
     * Llamado desde DashboardController.cargarImagenProducto().
     */
    public static synchronized String getImagenAdmin(int idProducto) {
        if (!cargado) { cargar(); cargado = true; }
        return imagenesAdmin.get(idProducto);
    }
 
    // ── Persistencia ─────────────────────────────────────────────────────────
 
    /**
     * Lee harmonia_catalogo_extra.txt.
     * Formato: id|nombre|descripcion|precio|idCategoria|stock[|imagePath]
     */
    private static void cargar() {
        extras.clear();
        imagenesAdmin.clear();
 
        Path ruta = Paths.get(ARCHIVO);
        if (!Files.exists(ruta)) return;
 
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] c = linea.split("\\" + SEP, -1);
                if (c.length < 5) continue;
                try {
                    int    id        = Integer.parseInt(c[0].trim());
                    String nom       = c[1];
                    String desc      = c[2];
                    double precio    = Double.parseDouble(c[3].trim());
                    int    idCat     = Integer.parseInt(c[4].trim());
                    int    stock     = c.length > 5 ? Integer.parseInt(c[5].trim()) : 10;
                    String imgPath   = c.length > 6 ? c[6].trim() : "";
 
                    Producto prod = new Producto(id, nom, desc, precio, 0, idCat);
                    prod.agregarVariante(new Variante(id * 100, "Estándar", 0.0, stock));
                    extras.add(prod);
 
                    if (!imgPath.isEmpty()) {
                        imagenesAdmin.put(id, imgPath);
                    }
 
                    StockStore.registrar(id, nom, stock, idCat);
 
                } catch (Exception ex) {
                    System.err.println("⚠ CatalogoStore: línea malformada — " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("⚠ CatalogoStore: error leyendo " + ARCHIVO + " — " + ex.getMessage());
        }
 
        System.out.println("[CatalogoStore] " + extras.size() + " productos extra cargados.");
    }
 
    /** Sobreescribe el archivo con el estado actual de la lista. */
    public static void guardar() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            bw.write("# id|nombre|descripcion|precio|idCategoria|stock|imagePath");
            bw.newLine();
            for (Producto p : extras) {
                // Persistir la SUMA de todas las variantes como stock total
                int stock = p.getVariantes().stream().mapToInt(Variante::getStock).sum();
                String imgPath = imagenesAdmin.getOrDefault(p.getId(), "");
                bw.write(String.join(SEP,
                    String.valueOf(p.getId()),
                    p.getNombre(),
                    p.getDescripcion(),
                    String.format(Locale.US, "%.2f", p.getPrecio()),
                    String.valueOf(p.getIdCategoria()),
                    String.valueOf(stock),
                    imgPath
                ));
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("⚠ CatalogoStore: error guardando — " + ex.getMessage());
        }
    }
    
    public static synchronized void inicializarBaseSiVacio() {
        if (!catalogoBase.isEmpty()) return;

        Producto p1 = new Producto(1, "Fender Stratocaster Player", "Guitarra eléctrica con pastillas Player Series", 2899000, 1, 1);
        p1.agregarVariante(new Variante(1, "Sunburst", 0, 10));
        p1.agregarVariante(new Variante(2, "Black", 0, 5));
        p1.agregarVariante(new Variante(3, "Polar White", 50000, 3));
        
        Producto p2 = new Producto(2, "Gibson Les Paul Standard '50s", "Guitarra eléctrica clásica con acabado vintage", 8499000, 2, 1);
        p2.agregarVariante(new Variante(4, "Gold Top", 0, 4));
        p2.agregarVariante(new Variante(5, "Heritage Cherry Sunburst", 0, 2));
        
        Producto p3 = new Producto(3, "Fender Telecaster American Pro II",
            "Telecaster de gama profesional, acción ultra baja", 7200000, 1, 1);
        p3.agregarVariante(new Variante(6, "3-Color Sunburst", 0, 3));
 
        Producto p4 = new Producto(4, "Fender Precision Bass Player",
            "Bajo eléctrico con tono potente y definido", 3299000, 1, 1);
        p4.agregarVariante(new Variante(10, "3-Color Sunburst", 0,     7));
        p4.agregarVariante(new Variante(11, "Black",            0,     4));
        p4.agregarVariante(new Variante(12, "Olympic White",    80000, 3));

        Producto p5 = new Producto(5, "Yamaha P-125 Piano Digital",
            "Piano digital ligero con teclado GH de 88 teclas", 2199000, 3, 2);
        p5.agregarVariante(new Variante(7, "Negro",  0,      8));
        p5.agregarVariante(new Variante(8, "Blanco", 150000, 3));
 
        Producto p6 = new Producto(6, "Roland FP-90X Piano Digital",
            "Piano digital de escenario con sonido PureAcoustic", 5499000, 4, 2);
        p6.agregarVariante(new Variante(9, "Negro", 0, 5));
 
        Producto p7 = new Producto(7, "Roland JUNO-DS61 Sintetizador",
            "Sintetizador portátil con 61 teclas y batería interna", 3799000, 4, 2);
        p7.agregarVariante(new Variante(13, "Negro",   0,      6));
        p7.agregarVariante(new Variante(14, "Blanco",  120000, 2));

        Producto p8 = new Producto(8, "Roland TD-17KVX Batería Electrónica",
            "Batería electrónica con platillos en V-Cymbal", 5899000, 4, 3);
        p8.agregarVariante(new Variante(15, "Kit Completo",       0,      5));
        p8.agregarVariante(new Variante(16, "Kit + Amplificador", 450000, 2));

        Producto p9 = new Producto(9, "Yamaha DTX452K Batería Electrónica",
            "Batería electrónica con módulo DTX502 y parches XP80", 3199000, 3, 3);
        p9.agregarVariante(new Variante(17, "Kit Completo",       0,      4));
        p9.agregarVariante(new Variante(18, "Kit + Pedal HH65",   200000, 3));

        Producto p10 = new Producto(10, "Saxofón Alto Yamaha YAS-280",
            "Saxofón alto ideal para estudiantes avanzados", 3499000, 3, 4);
        p10.agregarVariante(new Variante(19, "Lacado Dorado",  0,      5));
        p10.agregarVariante(new Variante(20, "Plateado",       100000, 2));

        Producto p11 = new Producto(11, "Yamaha YFL-222 Flauta Traversa",
            "Flauta traversa de plata niquelada para estudiantes", 1499000, 3, 4);
        p11.agregarVariante(new Variante(21, "Plata Niquelada", 0,     8));
        p11.agregarVariante(new Variante(22, "Cabeza Curva",    90000, 3));

        Producto p12 = new Producto(12, "Audio-Technica ATH-M50x",
            "Auriculares profesionales de estudio con respuesta plana", 899000, 5, 5);
        p12.agregarVariante(new Variante(23, "Negro",  0,     10));
        p12.agregarVariante(new Variante(24, "Blanco", 50000,  4));
        p12.agregarVariante(new Variante(25, "Rojo",   50000,  3));

        Producto p13 = new Producto(13, "Shure SM58 Micrófono Vocal",
            "Micrófono dinámico cardioide, el estándar de la industria", 489000, 6, 5);
        p13.agregarVariante(new Variante(26, "Solo micrófono",     0,      12));
        p13.agregarVariante(new Variante(27, "Con cable XLR 5m",   75000,   6));
        p13.agregarVariante(new Variante(28, "Con soporte de mesa", 120000,  4));

        Producto p14 = new Producto(14, "Audio-Technica AT2020 Condensador",
            "Micrófono de condensador de lado largo para grabación", 679000, 5, 5);
        p14.agregarVariante(new Variante(29, "Solo micrófono",         0,      8));
        p14.agregarVariante(new Variante(30, "Con araña y filtro pop", 180000,  4));

        catalogoBase.addAll(List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14));
        StockStore.inicializarBase(catalogoBase);
        
        if (!cargado) { cargar(); cargado = true; }
        
        // Cargar stock guardado en disco y aplicarlo a los productos base
        StockStore.cargarStock();
        for (Producto p : catalogoBase){
            int stockGuardado = StockStore.getStock(p.getId());
            if (stockGuardado >= 0 && !p.getVariantes().isEmpty()) {
                // Sincronizar la memoria RAM con el archivo de texto
                p.getVariantes().get(0).setStock(stockGuardado);
                for (int i = 1; i < p.getVariantes().size(); i++) {
                    p.getVariantes().get(i).setStock(0);
                }
            }
        }
    }
}