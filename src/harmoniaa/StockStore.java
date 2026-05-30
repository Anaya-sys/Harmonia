/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import static harmoniaa.CatalogoStore.guardar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
    private static boolean cargado = false;
    private static List<Producto> catalogoBase = new ArrayList<>();
    // id → nombre del producto
    private static final Map<Integer, String>  nombres = new LinkedHashMap<>();
    // id → stock actual (suma de todas las variantes)
    private static final Map<Integer, Integer> stocks  = new LinkedHashMap<>();
    // id → idCategoria (para mostrar emoji en la alerta)
    private static final Map<Integer, Integer> cats    = new LinkedHashMap<>();
    private static final java.util.List<Runnable> listeners = new java.util.ArrayList<>();
    
    private static final List<Producto>      extras       = new ArrayList<>();
    private static final Map<Integer,String> imagenesAdmin = new HashMap<>();
    private static final String ARCHIVO = "harmonia_catalogo_extra.txt";
    private static final String SEP     = "|";
    
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
    
    private static void notificarCambios() {
    for(Runnable listener : listeners) {
        listener.run();
    }
    }
    
    
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
 
                    // Registrar en StockStore (puede ya existir si el usuario lo agregó
                    // antes en esta misma sesión — registrar() no sobreescribe si el
                    // stock ya fue decrementado, pero aquí sí queremos el valor del disco)
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
 
    public static synchronized boolean eliminar(int idProducto) {
        if (!cargado) {cargar(); cargado = true; }

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
