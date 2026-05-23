/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.util.List;

/**
 *
 * @author CARLOS ANAYA
 */
public class PedidoStore {
     private static final Gestorpedidos GESTOR = new Gestorpedidos();
    private static boolean demoInicializado = false;
 
    private PedidoStore() {}
 
    /** Devuelve la instancia global del GestorPedidos. */
    public static Gestorpedidos getGestor() {
        return GESTOR;
    }
 
    /**
     * Inicializa datos de demo con el catálogo de productos.
     * Llamar desde DashboardController.initialize() una sola vez.
     *
     * @param catalogo lista de productos ya cargados en el dashboard
     */
    public static void inicializarDemo(List<Producto> catalogo) {
        if (!demoInicializado) {
            GESTOR.cargarDatosDemo(catalogo);
            demoInicializado = true;
        }
    }
}
