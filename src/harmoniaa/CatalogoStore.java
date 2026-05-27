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
import java.util.List;
import java.util.Locale;

/**
 *
 * @author CARLOS ANAYA
 */
public class CatalogoStore {
     private static final String ARCHIVO = "harmonia_catalogo_extra.txt";
    private static final String SEP     = "|";
 
    private static final List<Producto> extras  = new ArrayList<>();
    private static boolean cargado = false;
 
    private CatalogoStore() {}
 
    // ── API pública ───────────────────────────────────────────────────────────
 
    /** Devuelve la lista inmutable de productos agregados por el admin. */
    public static synchronized List<Producto> getExtras() {
        if (!cargado) { cargar(); cargado = true; }
        return Collections.unmodifiableList(extras);
    }
 
    /** Agrega un nuevo producto y lo persiste en el archivo. */
    public static synchronized void agregar(Producto p) {
        if (!cargado) { cargar(); cargado = true; }
        extras.add(p);
        guardar();
    }
 
    /** Devuelve el próximo ID disponible para productos admin (≥ 100). */
    public static synchronized int siguienteId() {
        if (!cargado) { cargar(); cargado = true; }
        int max = 99;
        for (Producto p : extras) if (p.getId() > max) max = p.getId();
        return max + 1;
    }
 
    // ── Persistencia ─────────────────────────────────────────────────────────
 
    /**
     * Lee harmonia_catalogo_extra.txt.
     * Formato de línea: id|nombre|descripcion|precio|idCategoria|stock
     */
    private static void cargar() {
        extras.clear();
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
                    int    id     = Integer.parseInt(c[0].trim());
                    String nom    = c[1];
                    String desc   = c[2];
                    double precio = Double.parseDouble(c[3].trim());
                    int    idCat  = Integer.parseInt(c[4].trim());
                    int    stock  = c.length > 5 ? Integer.parseInt(c[5].trim()) : 10;
 
                    Producto prod = new Producto(id, nom, desc, precio, 0, idCat);
                    prod.agregarVariante(new Variante(id * 100, "Estándar", 0.0, stock));
                    extras.add(prod);
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
    private static void guardar() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            bw.write("# id|nombre|descripcion|precio|idCategoria|stock");
            bw.newLine();
            for (Producto p : extras) {
                int stock = p.getVariantes().isEmpty()
                    ? 0 : p.getVariantes().get(0).getStock();
                bw.write(String.join(SEP,
                    String.valueOf(p.getId()),
                    p.getNombre(),
                    p.getDescripcion(),
                    String.format(Locale.US, "%.2f", p.getPrecio()),
                    String.valueOf(p.getIdCategoria()),
                    String.valueOf(stock)
                ));
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("⚠ CatalogoStore: error guardando — " + ex.getMessage());
        }
    }
}
