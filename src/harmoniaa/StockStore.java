/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CARLOS ANAYA
 */
public class StockStore {
    /** Umbral por defecto: ≤ 5 unidades → alerta de stock bajo. */
    public static final int UMBRAL = 5;
 
    // id → nombre del producto
    private static final Map<Integer, String>  nombres = new LinkedHashMap<>();
    // id → stock actual (suma de todas las variantes)
    private static final Map<Integer, Integer> stocks  = new LinkedHashMap<>();
    // id → idCategoria (para mostrar emoji en la alerta)
    private static final Map<Integer, Integer> cats    = new LinkedHashMap<>();
 
    private StockStore() {}
 
    // ── API pública ───────────────────────────────────────────────────────────
 
    /**
     * Registra o actualiza un producto en el store.
     * Llamar desde CatalogoStore.agregar() para productos admin,
     * y desde StockStore.inicializarBase() para el catálogo base.
     */
    public static synchronized void registrar(int id, String nombre,
                                               int stock, int idCategoria) {
        nombres.put(id, nombre);
        stocks.put(id, Math.max(0, stock));
        cats.put(id, idCategoria);
    }
 
    /**
     * Inicializa el store con el catálogo base del usuario.
     * Solo tiene efecto la primera vez (si ya existen esos IDs, no los sobreescribe
     * para no perder decrementos ya aplicados en la sesión).
     *
     * @param catalogo lista de Producto del catálogo base de DashboardController
     */
    public static synchronized void inicializarBase(List<Producto> catalogo) {
        for (Producto p : catalogo) {
            if (stocks.containsKey(p.getId())) continue; // ya registrado, no tocar
            int total = p.getVariantes().isEmpty()
                ? (p.estaDisponible() ? 999 : 0)
                : p.getVariantes().stream().mapToInt(Variante::getStock).sum();
            registrar(p.getId(), p.getNombre(), total, p.getIdCategoria());
        }
    }
 
    private static final String ARCHIVO_STOCK = "harmonia_stock.txt";

    // 1. Método para guardar el stock en disco
    public static synchronized void guardarStock() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(ARCHIVO_STOCK))) {
            for (Map.Entry<Integer, Integer> entry : stocks.entrySet()) {
                bw.write(entry.getKey() + "|" + entry.getValue());
                bw.newLine();
            }
        } catch (java.io.IOException ex) {
            System.err.println("⚠ StockStore: error guardando stock — " + ex.getMessage());
        }
    }

    // 2. Método para cargar el stock al abrir la app
    public static synchronized void cargarStock() {
        java.nio.file.Path ruta = java.nio.file.Paths.get(ARCHIVO_STOCK);
        if (!java.nio.file.Files.exists(ruta)) return;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(ARCHIVO_STOCK))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    stocks.put(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                }
            }
        } catch (Exception ex) {
            System.err.println("⚠ StockStore: error leyendo stock — " + ex.getMessage());
        }
    }
    
    /**
     * Decrementa el stock de un producto al confirmar un pedido.
     * El stock no baja de 0.
     */
    public static synchronized void decrementar(int idProducto, int cantidad) {
        int actual = stocks.getOrDefault(idProducto, 0);
        stocks.put(idProducto, Math.max(0, actual - cantidad));
        guardarStock();
        System.out.printf("[StockStore] Producto %d: stock %d → %d%n",
            idProducto, actual, stocks.get(idProducto));
    }
 
    /** Devuelve el stock actual de un producto, o -1 si no está registrado. */
    public static synchronized int getStock(int idProducto) {
        return stocks.getOrDefault(idProducto, -1);
    }
 
    /**
     * Devuelve la lista de productos con stock ≤ umbral, ordenados por stock ascendente.
     * Si un producto tiene stock -1 (no registrado) no aparece en la alerta.
     */
    public static synchronized List<ProductoStock> getBajoStock(int umbral) {
        List<ProductoStock> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : stocks.entrySet()) {
            int id    = e.getKey();
            int stock = e.getValue();
            if (stock >= 0 && stock <= umbral) {
                resultado.add(new ProductoStock(
                    id,
                    nombres.getOrDefault(id, "Producto #" + id),
                    stock,
                    cats.getOrDefault(id, 5)
                ));
            }
        }
        resultado.sort(Comparator.comparingInt(ps -> ps.stock));
        return resultado;
    }
 
    /** Usa el umbral por defecto (UMBRAL). */
    public static synchronized List<ProductoStock> getBajoStock() {
        return getBajoStock(UMBRAL);
    }
 
    /** Total de SKUs registrados. */
    public static synchronized int getTotalRegistrados() {
        return stocks.size();
    }
 
    // ── DTO ───────────────────────────────────────────────────────────────────
 
    /**
     * Snapshot de un producto con stock bajo.
     * Inmutable; se crea en getBajoStock() y se usa solo para renderizar.
     */
    public static final class ProductoStock {
        public final int    id;
        public final String nombre;
        public final int    stock;
        public final int    idCategoria;
 
        ProductoStock(int id, String nombre, int stock, int idCategoria) {
            this.id          = id;
            this.nombre      = nombre;
            this.stock       = stock;
            this.idCategoria = idCategoria;
        }
    }
}
