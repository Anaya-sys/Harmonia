package harmoniaa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

    // ── SIDEBAR ────────────────────────────────────────────────────────────────
    @FXML private VBox  sidebar;
    @FXML private HBox  logoBox;
    @FXML private VBox  userTextBox;
    @FXML private javafx.scene.image.ImageView logoIcono;
    @FXML private javafx.scene.image.ImageView logoCompleto;
    @FXML private Label  lblAvatarInitial;
    @FXML private Label  lblSidebarNombre;
    @FXML private Label  lblSidebarRol;
    @FXML private Button btnLogout;
@FXML private HBox      categoriasBar;

    @FXML private Button navBtnInicio;
    @FXML private Button navBtnCatalogo;
    @FXML private Button navBtnCarrito;
    @FXML private Button navBtnDeseos;
    @FXML private Button navBtnPedidos;
    @FXML private Button navBtnPerfil;

    // ── PANES ──────────────────────────────────────────────────────────────────
    @FXML private ScrollPane paneInicio;
    @FXML private BorderPane paneCatalogo;
    @FXML private ScrollPane panePerfil;
    @FXML private ScrollPane paneCarrito;
    @FXML private ScrollPane panePedidos;
    @FXML private ScrollPane paneDeseos;

    // ── INICIO ─────────────────────────────────────────────────────────────────
    @FXML private StackPane heroBanner;
    @FXML private Label     lblGreeting;
    @FXML private TextField txtBuscar;
    @FXML private FlowPane  productosGrid;

    // ── CATÁLOGO ───────────────────────────────────────────────────────────────
    @FXML private Label            lblResultados;
    @FXML private TextField        txtCatalogoBuscar;
    @FXML private ComboBox<String> cmbOrdenar;
    @FXML private HBox             chipsActivos;

    @FXML private Button catTodas;
    @FXML private Button catGuitarras;
    @FXML private Button catTeclados;
    @FXML private Button catPercusion;
    @FXML private Button catVientos;
    @FXML private Button catAccesorios;

    @FXML private Slider sliderMin;
    @FXML private Slider sliderMax;
    @FXML private Label  lblRangoPrecio;

    @FXML private Button dispTodos;
    @FXML private Button dispDisponible;

    @FXML private FlowPane catalogoGrid;
    @FXML private VBox     catalogoVacio;

    // ── CARRITO ────────────────────────────────────────────────────────────────
    @FXML private Label lblCarritoVacio;
    @FXML private VBox  carritoItemsContainer;
    @FXML private Label lblCarritoTotal;

    // ── DESEOS ─────────────────────────────────────────────────────────────────
    @FXML private Label lblDeseosVacio;
    @FXML private VBox  deseosItemsContainer;

    // ── PEDIDOS ────────────────────────────────────────────────────────────────
    @FXML private VBox   lblPedidosVacio;       // VBox estado vacío
    @FXML private VBox   pedidosItemsContainer;
    @FXML private Label  lblContadorPedidos;
    @FXML private Label  lblTotalGastado;
    @FXML private Label  lblNumeroPedidos;
    @FXML private Label  lblProductosComprados;
    @FXML private Button tabPedTodos;
    @FXML private Button tabPedConfirmados;
    @FXML private Button tabPedEnCamino;
    @FXML private Button tabPedEntregados;

   
    @FXML private Label     lblPerfilInitial;
    @FXML private Label     lblPerfilNombre;
    @FXML private Label     lblPerfilEmail;
    @FXML private Label     lblRolBadge;
    @FXML private Label     lblTipoPerfil;
    @FXML private Label     lblInstrumento;
    @FXML private Label     lblPresupuesto;
    @FXML private VBox      seccionEditar;
    @FXML private Button    btnEditarPerfil;
    @FXML private TextField txtEditNombre;
    @FXML private TextField txtEditInstrumento;
    @FXML private TextField txtEditPresupuesto;
    @FXML private Label     lblPerfilFeedback;

    // ── ESTADO INTERNO ─────────────────────────────────────────────────────────
    private final Carritostack     carrito  = new Carritostack();
    private final Listadeseosstack deseos   = new Listadeseosstack();
    private final List<Producto>   catalogo = new ArrayList<>();

    private String  filtroCat   = "Todas";
    private String  filtroDisp  = "Todos";
    private String  busqueda    = "";
    private String  filtroPedidos = "Todos";
    private boolean modoEdicion = false;

   
    private StackPane rootStackPane;   
    private ScrollPane paneDetalle;    
    private Producto   productoActual; 

    // ── MAPA DE IMÁGENES ───────────────────────────────────────────────────────
    private static final java.util.Map<Integer, String> IMG_PRODUCTO = new java.util.HashMap<>();
    static {
        IMG_PRODUCTO.put(1,  "prodimg/p1_fender_strat.png");
        IMG_PRODUCTO.put(2,  "prodimg/p2_gibson_lp.png");
        IMG_PRODUCTO.put(3,  "prodimg/p3_fender_tele.png");
        IMG_PRODUCTO.put(4,  "prodimg/p4_fender_bass.png");
        IMG_PRODUCTO.put(5,  "prodimg/p5_yamaha_p125.png");
        IMG_PRODUCTO.put(6,  "prodimg/p6_roland_fp90x.png");
        IMG_PRODUCTO.put(7,  "prodimg/p7_roland_juno.png");
        IMG_PRODUCTO.put(8,  "prodimg/p8_roland_td17.png");
        IMG_PRODUCTO.put(9,  "prodimg/p9_yamaha_dtx.png");
        IMG_PRODUCTO.put(10, "prodimg/p10_yamaha_sax.png");
        IMG_PRODUCTO.put(11, "prodimg/p11_yamaha_flaut.png");
        IMG_PRODUCTO.put(12, "prodimg/p12_ath_m50x.png");
        IMG_PRODUCTO.put(13, "prodimg/p13_shure_sm58.png");
        IMG_PRODUCTO.put(14, "prodimg/p14_at2020.png");
    }

    
    private static final double CARD_WIDTH    = 290;
    private static final double IMG_HEIGHT    = 220;
    private static final double NOMBRE_HEIGHT = 46;
    private static final double DESC_HEIGHT   = 38;

    
    private static final String ESTILO_NAV_ACTIVO =
        "-fx-background-color: #564AB5; -fx-background-radius: 12; " +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 12 0 14;";
    private static final String ESTILO_NAV_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 12; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 15px; " +
        "-fx-cursor: hand; -fx-padding: 0 12 0 14;";
    private static final String ESTILO_FILTRO_ACTIVO =
        "-fx-background-color: rgba(86,74,181,0.2); -fx-background-radius: 8; " +
        "-fx-border-color: #564AB5; -fx-border-radius: 8; " +
        "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String ESTILO_FILTRO_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 8; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 12px; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String ESTILO_DISP_ACTIVO =
        "-fx-background-color: rgba(86,74,181,0.15); -fx-background-radius: 8; " +
        "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
        "-fx-text-fill: #A99CF0; -fx-font-size: 11.5px; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String ESTILO_DISP_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 8; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 11.5px; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String FB_OK =
        "-fx-text-fill: #6BFF9E; -fx-font-size: 12px; -fx-text-alignment: center;";
    private static final String FB_ERROR =
        "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-text-alignment: center;";

    
    private boolean  sidebarExpanded = false;
    private Timeline sidebarAnim;

    private static final double   SIDEBAR_COLLAPSED = 68.0;
    private static final double   SIDEBAR_EXPANDED  = 230.0;
    private static final Duration ANIM_DURATION     = Duration.millis(200);

    // Icono + etiqueta de cada botón de navegación
    private static final String[][] NAV_TEXTOS = {
        { "🏠",  "  Inicio"   },
        { "🎸",  "  Catálogo" },
        { "🛒",  "  Carrito"  },
        { "♡",   "  Deseos"   },
        { "📦",  "  Pedidos"  },
        { "👤",  "  Perfil"   }
    };

    // ══════════════════════════════════════════════════════════════════════════
    // INITIALIZE
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        cargarCatalogoDatos();
        cargarDatosUsuario();
        iniciarComboOrdenar();
        iniciarSliders();
        catTodas.setStyle(ESTILO_FILTRO_ACTIVO);
        for (Button b : List.of(catGuitarras, catTeclados, catPercusion, catVientos, catAccesorios))
            b.setStyle(ESTILO_FILTRO_INACTIVO);
        dispTodos.setStyle(ESTILO_DISP_ACTIVO);
        dispDisponible.setStyle(ESTILO_DISP_INACTIVO);
        renderCatalogo();
        cargarProductosDestacadosInicio();
        actualizarVistaCarrito();
        actualizarVistaDeseos();
        navBtnInicio.setStyle(ESTILO_NAV_ACTIVO);
        inicializarSidebar();
        construirHeroBanner();
        construirCategoriasBar();
    }


    private void inicializarSidebar() {
        sidebar.setPrefWidth(SIDEBAR_COLLAPSED);
        sidebar.setMinWidth(SIDEBAR_COLLAPSED);
        sidebar.setMaxWidth(SIDEBAR_COLLAPSED);
        userTextBox.setVisible(false);
        userTextBox.setManaged(false);
        btnLogout.setVisible(false);
        btnLogout.setManaged(false);
        actualizarTextosBotones(false);
    }

    @FXML
    private void handleSidebarEntered() {
        if (sidebarExpanded) return;
        sidebarExpanded = true;
        // Swap logo: icono → completo
        logoIcono.setVisible(false);   logoIcono.setManaged(false);
        logoCompleto.setVisible(true); logoCompleto.setManaged(true);
        logoBox.setStyle("-fx-padding: 16 12 16 12; -fx-background-color: #0D0B17 ; -fx-background-radius: 0;");
       sidebar.setStyle("-fx-background-color: #0D0B17  ; -fx-border-color: rgba(86,74,181,0.25); -fx-border-width: 0 1 0 0; -fx-effect: null;");
        actualizarTextosBotones(true);
        userTextBox.setVisible(true);  userTextBox.setManaged(true);
        btnLogout.setVisible(true);    btnLogout.setManaged(true);
        animarSidebar(SIDEBAR_EXPANDED);
    }

    @FXML
    private void handleSidebarExited() {
        if (!sidebarExpanded) return;
        sidebarExpanded = false;
        // Swap logo: completo → icono
        logoCompleto.setVisible(false); logoCompleto.setManaged(false);
        logoIcono.setVisible(true);     logoIcono.setManaged(true);
        logoBox.setStyle("-fx-padding: 16 12 16 12; -fx-background-color: #120F1E; -fx-background-radius: 0;");
        sidebar.setStyle("-fx-background-color: #1E1A2E; -fx-border-color: rgba(86,74,181,0.25); -fx-border-width: 0 1 0 0;");
        animarSidebar(SIDEBAR_COLLAPSED);
    }

    private void animarSidebar(double targetWidth) {
        if (sidebarAnim != null && sidebarAnim.getStatus() == Animation.Status.RUNNING)
            sidebarAnim.stop();

        DoubleProperty widthProp = new SimpleDoubleProperty(sidebar.getPrefWidth());
        widthProp.addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            sidebar.setPrefWidth(w);
            sidebar.setMinWidth(w);
            sidebar.setMaxWidth(w);
        });

        KeyValue kv = new KeyValue(widthProp, targetWidth,
                javafx.animation.Interpolator.EASE_BOTH);
        sidebarAnim = new Timeline(new KeyFrame(ANIM_DURATION, kv));
        sidebarAnim.setOnFinished(e -> {
            if (!sidebarExpanded) {
                actualizarTextosBotones(false);
                userTextBox.setVisible(false);
                userTextBox.setManaged(false);
                btnLogout.setVisible(false);
                btnLogout.setManaged(false);
            }
        });
        sidebarAnim.play();
    }

    private void actualizarTextosBotones(boolean conTexto) {
        Button[] btns = { navBtnInicio, navBtnCatalogo, navBtnCarrito,
                          navBtnDeseos, navBtnPedidos, navBtnPerfil };
        for (int i = 0; i < btns.length; i++) {
            if (btns[i] == null) continue;
            String icono = NAV_TEXTOS[i][0];
            String label = NAV_TEXTOS[i][1];
            btns[i].setText(conTexto ? icono + label : icono);
            btns[i].setAlignment(conTexto
                    ? javafx.geometry.Pos.CENTER_LEFT
                    : javafx.geometry.Pos.CENTER);
        }
    }

  
    private void cargarCatalogoDatos() {
        Producto p1 = new Producto(1, "Fender Stratocaster Player",
            "Guitarra eléctrica con pastillas Player Series", 2899000, 1, 1);
        p1.agregarVariante(new Variante(1, "Sunburst",    0,     10));
        p1.agregarVariante(new Variante(2, "Black",       0,      5));
        p1.agregarVariante(new Variante(3, "Polar White", 50000,  3));

        Producto p2 = new Producto(2, "Gibson Les Paul Standard '50s",
            "Guitarra eléctrica clásica con acabado vintage", 8499000, 2, 1);
        p2.agregarVariante(new Variante(4, "Gold Top", 0, 4));
        p2.agregarVariante(new Variante(5, "Heritage Cherry Sunburst", 0, 2));

        Producto p3 = new Producto(3, "Fender Telecaster American Pro II",
            "Telecaster de gama profesional, acción ultra baja", 7200000, 1, 1);
        p3.agregarVariante(new Variante(6, "3-Color Sunburst", 0, 3));

        Producto p4 = new Producto(4, "Fender Precision Bass Player",
            "Bajo eléctrico con tono potente y definido", 3299000, 1, 1);

        Producto p5 = new Producto(5, "Yamaha P-125 Piano Digital",
            "Piano digital ligero con teclado GH de 88 teclas", 2199000, 3, 2);
        p5.agregarVariante(new Variante(7, "Negro",  0,      8));
        p5.agregarVariante(new Variante(8, "Blanco", 150000, 3));

        Producto p6 = new Producto(6, "Roland FP-90X Piano Digital",
            "Piano digital de escenario con sonido PureAcoustic", 5499000, 4, 2);
        p6.agregarVariante(new Variante(9, "Negro", 0, 5));

        Producto p7 = new Producto(7, "Roland JUNO-DS61 Sintetizador",
            "Sintetizador portátil con 61 teclas y batería interna", 3799000, 4, 2);

        Producto p8 = new Producto(8, "Roland TD-17KVX Batería Electrónica",
            "Batería electrónica con platillos en V-Cymbal", 5899000, 4, 3);

        Producto p9 = new Producto(9, "Yamaha DTX452K Batería Electrónica",
            "Batería electrónica con módulo DTX502 y parches XP80", 3199000, 3, 3);

        Producto p10 = new Producto(10, "Saxofón Alto Yamaha YAS-280",
            "Saxofón alto ideal para estudiantes avanzados", 3499000, 3, 4);

        Producto p11 = new Producto(11, "Yamaha YFL-222 Flauta Traversa",
            "Flauta traversa de plata niquelada para estudiantes", 1499000, 3, 4);

        Producto p12 = new Producto(12, "Audio-Technica ATH-M50x",
            "Auriculares profesionales de estudio con respuesta plana", 899000, 5, 5);

        Producto p13 = new Producto(13, "Shure SM58 Micrófono Vocal",
            "Micrófono dinámico cardioide, el estándar de la industria", 489000, 6, 5);

        Producto p14 = new Producto(14, "Audio-Technica AT2020 Condensador",
            "Micrófono de condensador de lado largo para grabación", 679000, 5, 5);

        catalogo.addAll(List.of(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPER: imagen con fallback null
    // ══════════════════════════════════════════════════════════════════════════
    private ImageView cargarImagenProducto(Producto p, double ancho, double alto) {
        String ruta = IMG_PRODUCTO.get(p.getId());
        if (ruta == null) return null;
        try {
            var stream = getClass().getResourceAsStream(ruta);
            if (stream == null) return null;
            Image img = new Image(stream, ancho, alto, true, true);
            if (img.isError()) return null;
            ImageView iv = new ImageView(img);
            iv.setFitWidth(ancho);
            iv.setFitHeight(alto);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            return iv;
        } catch (Exception ex) { return null; }
    }

    private void cargarDatosUsuario() {
        Usuario u = UserStore.getUsuarioActivo();
        if (u == null) { handleLogout(); return; }
        String inicial = u.getNombre().isEmpty() ? "?"
                       : String.valueOf(u.getNombre().charAt(0)).toUpperCase();
        lblAvatarInitial.setText(inicial);
        lblSidebarNombre.setText(primerNombre(u.getNombre()));
        lblSidebarRol.setText(u.getRol().name());
        lblGreeting.setText("Hola, " + primerNombre(u.getNombre()) + " 👋");
        lblPerfilInitial.setText(inicial);
        lblPerfilNombre.setText(u.getNombre());
        lblPerfilEmail.setText(u.getEmail());
        lblRolBadge.setText(u.getRol().name());
        PerfilUsuario p = u.getPerfil();
        if (p != null) {
            lblTipoPerfil.setText(p.getTipo().name());
            lblInstrumento.setText(p.getInstrumento() == null || p.getInstrumento().isBlank()
                ? "Sin definir" : p.getInstrumento());
            lblPresupuesto.setText(String.format("$%.2f", p.getPresupuesto()));
            txtEditNombre.setText(u.getNombre());
            txtEditInstrumento.setText(p.getInstrumento());
            txtEditPresupuesto.setText(String.valueOf((int) p.getPresupuesto()));
        }
    }

    private void iniciarComboOrdenar() {
        cmbOrdenar.getItems().addAll("Relevancia", "Menor precio", "Mayor precio", "Nombre A–Z");
        cmbOrdenar.setValue("Relevancia");
        cmbOrdenar.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #E9E9ED; -fx-font-size: 12px;");
    }

    private void iniciarSliders() {
        sliderMin.setBlockIncrement(500_000);
        sliderMax.setBlockIncrement(500_000);
        actualizarLabelRango();
        sliderMin.valueProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() > sliderMax.getValue())
                sliderMin.setValue(sliderMax.getValue());
            actualizarLabelRango();
        });
        sliderMax.valueProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() < sliderMin.getValue())
                sliderMax.setValue(sliderMin.getValue());
            actualizarLabelRango();
        });
    }

    private void actualizarLabelRango() {
        long min = (long) sliderMin.getValue(), max = (long) sliderMax.getValue();
        lblRangoPrecio.setText(String.format("$%,d – $%,d", min, max));
    }

    @FXML private void resetearRangoPrecio() {
        sliderMin.setValue(0); sliderMax.setValue(10_000_000);
        actualizarLabelRango(); renderCatalogo();
    }

    @FXML private void handleAplicarRangoPrecio() {
        renderCatalogo();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN
    // ══════════════════════════════════════════════════════════════════════════
    private void mostrarSeccion(Node pane, Button navBtn) {
        cerrarDetalle(); // siempre cierra el detalle al cambiar de sección
        for (Node n : List.of(paneInicio, panePerfil,
                               paneCatalogo, paneCarrito, paneDeseos, panePedidos)) {
            n.setVisible(false); n.setManaged(false);
        }
        pane.setVisible(true); pane.setManaged(true);
        pane.setOpacity(0.0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), pane);
        ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
        for (Button b : List.of(navBtnInicio, navBtnCatalogo, navBtnCarrito,
                                 navBtnDeseos, navBtnPedidos, navBtnPerfil))
            b.setStyle(ESTILO_NAV_INACTIVO);
        navBtn.setStyle(ESTILO_NAV_ACTIVO);
        ScaleTransition sc = new ScaleTransition(Duration.millis(150), navBtn);
        sc.setFromX(0.93); sc.setFromY(0.93); sc.setToX(1.0); sc.setToY(1.0); sc.play();
    }

    @FXML private void navegarInicio()   { mostrarSeccion(paneInicio,    navBtnInicio); }
    @FXML private void navegarCatalogo() { mostrarSeccion(paneCatalogo,  navBtnCatalogo); renderCatalogo(); }
    @FXML private void navegarCarrito()  { actualizarVistaCarrito(); mostrarSeccion(paneCarrito,  navBtnCarrito); }
    @FXML private void navegarDeseos()   { actualizarVistaDeseos();  mostrarSeccion(paneDeseos,   navBtnDeseos); }
    @FXML private void navegarPedidos()  { actualizarVistaPedidos(); mostrarSeccion(panePedidos,  navBtnPedidos); }
    @FXML private void navegarPerfil()   { mostrarSeccion(panePerfil,    navBtnPerfil); }

    // ══════════════════════════════════════════════════════════════════════════
    // VISTA DETALLE DE PRODUCTO — overlay estilo Mercado Libre
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Obtiene el StackPane raíz del FXML (que envuelve center + sidebar).
     * El FXML tiene un BorderPane como raíz; lo envolvemos dinámicamente la
     * primera vez para poder añadir el overlay encima de todo.
     */
    private StackPane obtenerRootStackPane() {
        if (rootStackPane != null) return rootStackPane;
        // El BorderPane es el padre directo del center; su padre es la Scene root
        // En nuestro FXML el StackPane ya existe en center, así que buscamos hacia arriba
        Node n = paneCatalogo;
        while (n.getParent() != null) {
            if (n.getParent() instanceof StackPane sp) {
                rootStackPane = sp;
                return rootStackPane;
            }
            n = n.getParent();
        }
        return null;
    }

    /** Abre la vista detalle de un producto en un panel overlay. */
    public void abrirDetalle(Producto p) {

    cerrarDetalle();

    productoActual = p;

    paneDetalle = construirPanelDetalle(p);

    paneDetalle.setPickOnBounds(true);

    StackPane center = (StackPane) paneCatalogo.getParent();

    center.getChildren().add(paneDetalle);

    paneDetalle.toFront();

    paneDetalle.setOpacity(0.0);

    FadeTransition ft =
        new FadeTransition(Duration.millis(220), paneDetalle);

    ft.setFromValue(0.0);
    ft.setToValue(1.0);

    ft.play();
}

private void cerrarDetalle() {

    if (paneDetalle == null) {
        return;
    }

    ScrollPane detalleActual = paneDetalle;

    detalleActual.setMouseTransparent(true);

    Node parent = detalleActual.getParent();

    if (parent instanceof StackPane sp) {

        FadeTransition ft =
            new FadeTransition(Duration.millis(180), detalleActual);

        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.setOnFinished(e -> {
            sp.getChildren().remove(detalleActual);
        });

        ft.play();
    }

    paneDetalle = null;
}

    /** Construye el ScrollPane con toda la vista de detalle del producto. */
    private ScrollPane construirPanelDetalle(Producto p) {

        // Contenedor raíz del detalle (fondo sólido sobre el catálogo)
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #151221;");

        // ── BARRA SUPERIOR (breadcrumb + botón cerrar) ───────────────────────
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-padding: 14 24 14 24; " +
            "-fx-background-color: #1E1A2E; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-width: 0 0 1 0;");

        Button btnVolver = new Button("← Volver al catálogo");
        btnVolver.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;");
        btnVolver.setOnAction(e -> cerrarDetalle());
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle(
            "-fx-background-color: rgba(86,74,181,0.15); -fx-background-radius: 8; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 8; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));

        Label breadcrumb = new Label("Tienda  ›  "
            + (getCategoriaById(p.getIdCategoria()) != null
               ? getCategoriaById(p.getIdCategoria()).getNombre() : "")
            + "  ›  " + p.getNombre());
        breadcrumb.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8;");

        Region spacerTop = new Region(); HBox.setHgrow(spacerTop, Priority.ALWAYS);
        topBar.getChildren().addAll(btnVolver, spacerTop, breadcrumb);

        // ── ZONA PRINCIPAL (imagen izquierda + info derecha) ─────────────────
        HBox cuerpo = new HBox(40);
        cuerpo.setStyle("-fx-padding: 36 40 36 40; -fx-background-color: #151221;");

        // ---- Columna izquierda: imagen grande + miniaturas variantes ----------
        VBox colImg = new VBox(14);
        colImg.setAlignment(Pos.TOP_CENTER);
        colImg.setMinWidth(500); colImg.setPrefWidth(500); colImg.setMaxWidth(500);

        // Imagen principal
        StackPane imgGrande = new StackPane();
        imgGrande.setPrefSize(480, 480);
        imgGrande.setMinSize(480, 480);
        imgGrande.setMaxSize(480, 480);
        imgGrande.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 16; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 16;");

        ImageView ivGrande = cargarImagenProducto(p, 480, 480);
        if (ivGrande != null) {
            ivGrande.setPreserveRatio(true);
            Rectangle clipG = new Rectangle(480, 480);
            clipG.setArcWidth(32); clipG.setArcHeight(32);
            imgGrande.setClip(clipG);
            imgGrande.getChildren().add(ivGrande);
        } else {
            Label emo = new Label(getEmojiCategoria(p.getIdCategoria()));
            emo.setStyle("-fx-font-size: 80px;");
            imgGrande.getChildren().add(emo);
        }

        // Miniaturas de variantes (si las hay) — actúan como "fotos adicionales"
        HBox miniaturas = new HBox(10);
        miniaturas.setAlignment(Pos.CENTER);
        if (!p.getVariantes().isEmpty()) {
            // Miniatura "principal" siempre
            StackPane m0 = crearMiniatura(p, 60, 60, true);
            miniaturas.getChildren().add(m0);
            // Una miniatura por variante (máx 4)
            int max = Math.min(p.getVariantes().size(), 4);
            for (int i = 0; i < max; i++) {
                Variante v = p.getVariantes().get(i);
                StackPane mv = new StackPane();
                mv.setPrefSize(60, 60); mv.setMinSize(60, 60); mv.setMaxSize(60, 60);
                mv.setStyle(
                    "-fx-background-color: #272239; -fx-background-radius: 8; " +
                    "-fx-border-color: rgba(86,74,181,0.25); -fx-border-radius: 8; -fx-cursor: hand;");
                Label lv = new Label(v.getDescripcion().length() > 8
                    ? v.getDescripcion().substring(0, 7) + "…" : v.getDescripcion());
                lv.setStyle("-fx-font-size: 9px; -fx-text-fill: #A99CF0; -fx-text-alignment: center;");
                lv.setWrapText(true); lv.setMaxWidth(54);
                mv.getChildren().add(lv);
                miniaturas.getChildren().add(mv);
            }
        }

        colImg.getChildren().addAll(imgGrande, miniaturas);

        // ---- Columna derecha: toda la información del producto ---------------
        VBox colInfo = new VBox(16);
        HBox.setHgrow(colInfo, Priority.ALWAYS);
        colInfo.setStyle("-fx-padding: 4 0 0 0;");

        // Marca / categoría
        Categoria cat = getCategoriaById(p.getIdCategoria());
        Label lblMarca = new Label((cat != null ? cat.getNombre().toUpperCase() : "HARMONIA")
            + "   ·   Producto #" + p.getId());
        lblMarca.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #8b7cf8;");

        // Nombre del producto
        Label lblNombreDet = new Label(p.getNombre());
        lblNombreDet.setWrapText(true);
        lblNombreDet.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        // Estrellas simuladas + disponibilidad
        HBox starsRow = new HBox(10);
        starsRow.setAlignment(Pos.CENTER_LEFT);
        Label estrellas = new Label("★★★★☆  4.2  ·  (124 reseñas)");
        estrellas.setStyle("-fx-font-size: 13px; -fx-text-fill: #f59e0b;");
        Label dispBadge = new Label(p.estaDisponible() ? "✓  En stock" : "✗  Sin stock");
        dispBadge.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; " +
            (p.estaDisponible()
                ? "-fx-text-fill: #10b981; -fx-background-color: rgba(16,185,129,0.12); "
                : "-fx-text-fill: #ef4444; -fx-background-color: rgba(239,68,68,0.12); ") +
            "-fx-background-radius: 6; -fx-padding: 3 10 3 10;");
        starsRow.getChildren().addAll(estrellas, dispBadge);

        // Separador
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: rgba(86,74,181,0.2);");

        // Precio (con precio "tachado" simulado como referencia)
        HBox precioZona = new HBox(14);
        precioZona.setAlignment(Pos.BASELINE_LEFT);
        double precioBase = p.getPrecio();
        double precioOriginal = precioBase * 1.12; // +12% como precio "antes"
        Label lblPrecioAntes = new Label("$" + String.format("%,.0f", precioOriginal));
        lblPrecioAntes.setStyle(
            "-fx-font-size: 14px; -fx-text-fill: #6b6890; " +
            "-fx-strikethrough: true;");
        Label lblPrecioActual = new Label("$" + String.format("%,.0f", precioBase));
        lblPrecioActual.setStyle(
            "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label lblDescuento = new Label("-12%");
        lblDescuento.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #ef4444; " +
            "-fx-background-color: rgba(239,68,68,0.15); -fx-background-radius: 6; " +
            "-fx-padding: 3 8 3 8;");
        precioZona.getChildren().addAll(lblPrecioActual, lblPrecioAntes, lblDescuento);

        // Texto de cuotas
        Label lblCuotas = new Label("3 cuotas de $"
            + String.format("%,.0f", precioBase / 3) + " sin interés");
        lblCuotas.setStyle("-fx-font-size: 13px; -fx-text-fill: #10b981;");

        // Separador
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: rgba(86,74,181,0.2);");

        // Variantes (selector de variante)
        VBox variantesZona = new VBox(10);
        if (!p.getVariantes().isEmpty()) {
            Label lblVarTitulo = new Label("Variante:");
            lblVarTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
            HBox varBtns = new HBox(8);
            varBtns.setAlignment(Pos.CENTER_LEFT);
            final Variante[] varSeleccionada = {p.getVariantes().get(0)};
            for (Variante v : p.getVariantes()) {
                Button bv = new Button(v.getDescripcion());
                boolean selected = v == varSeleccionada[0];
                bv.setStyle(selected
                    ? "-fx-background-color: #564AB5; -fx-background-radius: 8; " +
                      "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 14 6 14;"
                    : "-fx-background-color: #272239; -fx-background-radius: 8; " +
                      "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
                      "-fx-text-fill: #8F8AA8; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 14 6 14;");
                bv.setOnAction(e -> {
                    varSeleccionada[0] = v;
                    for (Node nb : varBtns.getChildren()) {
                        if (nb instanceof Button bb) {
                            bb.setStyle(bb == bv
                                ? "-fx-background-color: #564AB5; -fx-background-radius: 8; " +
                                  "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 14 6 14;"
                                : "-fx-background-color: #272239; -fx-background-radius: 8; " +
                                  "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
                                  "-fx-text-fill: #8F8AA8; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 14 6 14;");
                        }
                    }
                });
                varBtns.getChildren().add(bv);
            }
            variantesZona.getChildren().addAll(lblVarTitulo, varBtns);
        }

        // Cantidad + botones acción
        HBox cantidadRow = new HBox(12);
        cantidadRow.setAlignment(Pos.CENTER_LEFT);
        Label lblCantTitulo = new Label("Cantidad:");
        lblCantTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        final int[] cantidad = {1};
        Button btnMenos = new Button("−");
        btnMenos.setPrefSize(32, 32); btnMenos.setMinSize(32, 32);
        btnMenos.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #E9E9ED; -fx-font-size: 16px; -fx-cursor: hand;");
        Label lblCantidad = new Label("1");
        lblCantidad.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
            "-fx-text-fill: #E9E9ED; -fx-min-width: 28; -fx-alignment: center;");
        Button btnMas = new Button("+");
        btnMas.setPrefSize(32, 32); btnMas.setMinSize(32, 32);
        btnMas.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #E9E9ED; -fx-font-size: 16px; -fx-cursor: hand;");
        btnMenos.setOnAction(e -> {
            if (cantidad[0] > 1) { cantidad[0]--; lblCantidad.setText(String.valueOf(cantidad[0])); }
        });
        btnMas.setOnAction(e -> {
            cantidad[0]++; lblCantidad.setText(String.valueOf(cantidad[0]));
        });
        cantidadRow.getChildren().addAll(lblCantTitulo, btnMenos, lblCantidad, btnMas);

        // Botones principales
        HBox botonesAccion = new HBox(12);
        botonesAccion.setAlignment(Pos.CENTER_LEFT);

        Button btnAgregarDet = new Button("🛒   Agregar al carrito");
        btnAgregarDet.setPrefHeight(46);
        btnAgregarDet.setPrefWidth(220);
        btnAgregarDet.setStyle(
            "-fx-background-color: #564AB5; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.4),14,0,0,5);");
        btnAgregarDet.setOnMouseEntered(e -> btnAgregarDet.setStyle(
            "-fx-background-color: #6c5ce7; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(86,74,181,0.6),18,0,0,6);"));
        btnAgregarDet.setOnMouseExited(e -> btnAgregarDet.setStyle(
            "-fx-background-color: #564AB5; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(86,74,181,0.4),14,0,0,5);"));
        btnAgregarDet.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            for (int i = 0; i < cantidad[0]; i++)
                carrito.agregar(new ItemCarrito(p, v, 1));
            mostrarToast("🛒 " + cantidad[0] + "x \"" + p.getNombre() + "\" añadido al carrito.");
        });

        Button btnDeseosDet = new Button(deseos.contiene(p.getId()) ? "♥  En deseos" : "♡  Guardar");
        btnDeseosDet.setPrefHeight(46);
        btnDeseosDet.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.45); -fx-border-radius: 10; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 18 0 18;");
        btnDeseosDet.setOnAction(e -> {
            if (deseos.contiene(p.getId())) {
                deseos.quitar(p.getId());
                btnDeseosDet.setText("♡  Guardar");
            } else {
                deseos.agregar(p);
                btnDeseosDet.setText("♥  En deseos");
                btnDeseosDet.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.1); -fx-background-radius: 10; " +
                    "-fx-border-color: rgba(239,68,68,0.4); -fx-border-radius: 10; " +
                    "-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 18 0 18;");
            }
        });

        botonesAccion.getChildren().addAll(btnAgregarDet, btnDeseosDet);

        // ── Características del producto ─────────────────────────────────────
        Separator sep3 = new Separator();
        sep3.setStyle("-fx-background-color: rgba(86,74,181,0.2);");

        VBox caracteristicas = new VBox(10);
        Label lblCaracTitulo = new Label("Características");
        lblCaracTitulo.setStyle(
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        VBox listaCarac = new VBox(8);
        String[] caracs = getCaracteristicas(p);
        for (String c : caracs) {
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            Label punto = new Label("◆");
            punto.setStyle("-fx-font-size: 8px; -fx-text-fill: #564AB5;");
            Label texto = new Label(c);
            texto.setStyle("-fx-font-size: 13px; -fx-text-fill: #C5C3D4;");
            item.getChildren().addAll(punto, texto);
            listaCarac.getChildren().add(item);
        }
        caracteristicas.getChildren().addAll(lblCaracTitulo, listaCarac);

        colInfo.getChildren().addAll(
            lblMarca, lblNombreDet, starsRow, sep1,
            precioZona, lblCuotas, sep2,
            variantesZona, cantidadRow, botonesAccion,
            sep3, caracteristicas
        );

        cuerpo.getChildren().addAll(colImg, colInfo);

        // ── SECCIÓN PRODUCTOS RELACIONADOS ───────────────────────────────────
        VBox relacionadosZona = new VBox(18);
        relacionadosZona.setStyle("-fx-padding: 0 40 40 40; -fx-background-color: #151221;");

        Separator sepRel = new Separator();
        sepRel.setStyle("-fx-background-color: rgba(86,74,181,0.2);");

        Label lblRelTitulo = new Label("Productos relacionados");
        lblRelTitulo.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        FlowPane relGrid = new FlowPane(16, 16);
        List<Producto> relacionados = catalogo.stream()
            .filter(pr -> pr.getIdCategoria() == p.getIdCategoria() && pr.getId() != p.getId())
            .limit(4)
            .collect(Collectors.toList());
        // Si hay pocos de la misma categoría, completar con otros
        if (relacionados.size() < 4) {
            catalogo.stream()
                .filter(pr -> pr.getId() != p.getId() && !relacionados.contains(pr))
                .limit(4 - relacionados.size())
                .forEach(relacionados::add);
        }
        for (Producto rel : relacionados) {
            VBox miniRel = crearMiniCardRelacionado(rel);
            relGrid.getChildren().add(miniRel);
        }

        relacionadosZona.getChildren().addAll(sepRel, lblRelTitulo, relGrid);

        root.getChildren().addAll(topBar, cuerpo, relacionadosZona);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle(
            "-fx-background-color: #151221; -fx-background: #151221; -fx-border-color: transparent;");
        return sp;
    }

    /** Mini-tarjeta para la sección "Productos relacionados" */
    private VBox crearMiniCardRelacionado(Producto p) {
        VBox card = new VBox(0);
        card.setPrefWidth(200); card.setMaxWidth(200); card.setMinWidth(200);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 12; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,3);");

        StackPane imgZone = new StackPane();
        imgZone.setPrefHeight(140); imgZone.setMinHeight(140); imgZone.setMaxHeight(140);
        imgZone.setStyle("-fx-background-color: #13111e; -fx-background-radius: 12 12 0 0;");

        ImageView iv = cargarImagenProducto(p, 200, 140);
        if (iv != null) {
            Rectangle clip = new Rectangle(200, 140);
            clip.setArcWidth(24); clip.setArcHeight(24);
            imgZone.setClip(clip);
            imgZone.getChildren().add(iv);
        } else {
            Label emoji = new Label(getEmojiCategoria(p.getIdCategoria()));
            emoji.setStyle("-fx-font-size: 36px;");
            imgZone.getChildren().add(emoji);
        }

        // Ícono carrito circular sobre la imagen (esquina inferior derecha)
        boolean yaEnCarrito = carrito.getItems().stream()
            .anyMatch(it -> it.getProducto().getId() == p.getId());
        Button btnC = new Button(yaEnCarrito ? "✓" : "🛒");
        btnC.setPrefSize(34, 34); btnC.setMinSize(34, 34); btnC.setMaxSize(34, 34);
        btnC.setStyle(
            (yaEnCarrito
                ? "-fx-background-color: rgba(16,185,129,0.25); -fx-text-fill: #10b981;"
                : "-fx-background-color: #564AB5; -fx-text-fill: white;") +
            " -fx-background-radius: 50%; -fx-border-color: transparent; " +
            "-fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0;");
        btnC.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            mostrarToast("🛒 \"" + p.getNombre() + "\" añadido al carrito.");
        });
        VBox cw = new VBox(btnC); cw.setStyle("-fx-padding: 0 8 8 0;");
        StackPane.setAlignment(cw, Pos.BOTTOM_RIGHT);
        imgZone.getChildren().add(cw);

        VBox body = new VBox(5);
        body.setStyle("-fx-padding: 10 12 12 12;");

        Categoria cat = getCategoriaById(p.getIdCategoria());
        Label lCat = new Label(cat != null ? cat.getNombre().toUpperCase() : "");
        lCat.setStyle("-fx-font-size: 9.5px; -fx-font-weight: bold; -fx-text-fill: #8b7cf8;");

        Label lNom = new Label(p.getNombre());
        lNom.setPrefHeight(32); lNom.setMinHeight(32); lNom.setMaxHeight(32);
        lNom.setPrefWidth(176); lNom.setMaxWidth(176);
        lNom.setWrapText(true);
        lNom.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        Label lPre = new Label("$" + String.format("%,.0f", p.getPrecio()));
        lPre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");

        body.getChildren().addAll(lCat, lNom, lPre);
        card.getChildren().addAll(imgZone, body);

        // Click abre el detalle de ese producto relacionado
        card.setOnMouseClicked(e -> abrirDetalle(p));
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #252540; -fx-background-radius: 12; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 12; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.3),14,0,0,5);"));
        card.setOnMouseExited(e  -> card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 12; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,3);"));
        return card;
    }

    /** Miniatura cuadrada para la galería de la vista detalle */
    private StackPane crearMiniatura(Producto p, double w, double h, boolean selected) {
        StackPane sp = new StackPane();
        sp.setPrefSize(w, h); sp.setMinSize(w, h); sp.setMaxSize(w, h);
        sp.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            (selected ? "-fx-border-color: #564AB5;" : "-fx-border-color: rgba(86,74,181,0.25);") +
            " -fx-border-radius: 8; -fx-cursor: hand;");
        ImageView iv = cargarImagenProducto(p, w, h);
        if (iv != null) {
            Rectangle clip = new Rectangle(w, h);
            clip.setArcWidth(16); clip.setArcHeight(16);
            sp.setClip(clip);
            sp.getChildren().add(iv);
        } else {
            Label emo = new Label(getEmojiCategoria(p.getIdCategoria()));
            emo.setStyle("-fx-font-size: 20px;");
            sp.getChildren().add(emo);
        }
        return sp;
    }

    /** Retorna características simuladas según la categoría del producto */
    private String[] getCaracteristicas(Producto p) {
        return switch (p.getIdCategoria()) {
            case 1 -> new String[]{
                "Cuerpo de aliso",
                "Mástil de arce moderno C",
                "Pastillas Player Series",
                "Puente sincronizado de 2 puntos",
                "Incluye funda y correa"};
            case 2 -> new String[]{
                "88 teclas con acción martillo graduado",
                "Polifonía de 192 voces",
                "10 sonidos preset de alta calidad",
                "Salida de auriculares integrada",
                "Diseño ultraligero y portable"};
            case 3 -> new String[]{
                "Parche de malla tensor ajustable",
                "Módulo de sonido con 50 kits",
                "Compatible con pedal de bombo",
                "Salida USB MIDI y audio",
                "Diseño plegable para fácil transporte"};
            case 4 -> new String[]{
                "Cuerpo de latón de alta pureza",
                "Sistema de llaves plateadas",
                "Octavario afinado de precisión",
                "Incluye estuche rígido",
                "Recomendado para nivel intermedio-avanzado"};
            default -> new String[]{
                "Calidad de estudio profesional",
                "Respuesta de frecuencia 20Hz–20kHz",
                "Compatibilidad universal",
                "Cable de conexión incluido",
                "Garantía oficial del fabricante"};
        };
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CATÁLOGO – FILTROS
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    private void filtrarCategoria(javafx.event.ActionEvent e) {
        Button src = (Button) e.getSource();
        if      (src == catTodas)      filtroCat = "Todas";
        else if (src == catGuitarras)  filtroCat = "Guitarras";
        else if (src == catTeclados)   filtroCat = "Teclados";
        else if (src == catPercusion)  filtroCat = "Percusión";
        else if (src == catVientos)    filtroCat = "Vientos";
        else if (src == catAccesorios) filtroCat = "Accesorios";
        for (Button b : List.of(catTodas, catGuitarras, catTeclados,
                                 catPercusion, catVientos, catAccesorios))
            b.setStyle(b == src ? ESTILO_FILTRO_ACTIVO : ESTILO_FILTRO_INACTIVO);
        renderCatalogo();
    }

    @FXML
    private void filtrarDisponibilidad(javafx.event.ActionEvent e) {
        Button src = (Button) e.getSource();
        filtroDisp = (src == dispDisponible) ? "Disponible" : "Todos";
        dispTodos.setStyle(src == dispTodos ? ESTILO_DISP_ACTIVO : ESTILO_DISP_INACTIVO);
        dispDisponible.setStyle(src == dispDisponible ? ESTILO_DISP_ACTIVO : ESTILO_DISP_INACTIVO);
        renderCatalogo();
    }

    @FXML private void handleCatalogoBuscar() {
        busqueda = txtCatalogoBuscar.getText().trim().toLowerCase();
        renderCatalogo();
    }

    @FXML private void handleOrdenar() { renderCatalogo(); }

    @FXML
    private void limpiarFiltros() {
        filtroCat = "Todas"; filtroDisp = "Todos"; busqueda = "";
        txtCatalogoBuscar.clear(); cmbOrdenar.setValue("Relevancia");
        sliderMin.setValue(0); sliderMax.setValue(10_000_000);
        actualizarLabelRango();
        catTodas.setStyle(ESTILO_FILTRO_ACTIVO);
        for (Button b : List.of(catGuitarras, catTeclados, catPercusion, catVientos, catAccesorios))
            b.setStyle(ESTILO_FILTRO_INACTIVO);
        dispTodos.setStyle(ESTILO_DISP_ACTIVO);
        dispDisponible.setStyle(ESTILO_DISP_INACTIVO);
        renderCatalogo();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CATÁLOGO – RENDER
    // ══════════════════════════════════════════════════════════════════════════
    private void renderCatalogo() {
        double precioMin = sliderMin.getValue(), precioMax = sliderMax.getValue();
        List<Producto> filtrados = catalogo.stream().filter(p -> {
            if (!filtroCat.equals("Todas")) {
                Categoria cat = getCategoriaById(p.getIdCategoria());
                if (cat == null || !cat.getNombre().equals(filtroCat)) return false;
            }
            if (p.getPrecio() < precioMin || p.getPrecio() > precioMax) return false;
            if (filtroDisp.equals("Disponible") && !p.estaDisponible()) return false;
            if (!busqueda.isEmpty()) {
                boolean ok = p.getNombre().toLowerCase().contains(busqueda)
                          || p.getDescripcion().toLowerCase().contains(busqueda);
                if (!ok) return false;
            }
            return true;
        }).collect(Collectors.toList());

        String orden = cmbOrdenar.getValue();
        if (orden != null) switch (orden) {
            case "Menor precio" -> filtrados.sort(Comparator.comparingDouble(Producto::getPrecio));
            case "Mayor precio" -> filtrados.sort(Comparator.comparingDouble(Producto::getPrecio).reversed());
            case "Nombre A–Z"   -> filtrados.sort(Comparator.comparing(Producto::getNombre));
        }

        int total = filtrados.size();
        lblResultados.setText(total + " producto" + (total != 1 ? "s" : "") +
                              " encontrado" + (total != 1 ? "s" : ""));
        catalogoGrid.getChildren().clear();
        boolean sinRes = filtrados.isEmpty();
        catalogoVacio.setVisible(sinRes); catalogoVacio.setManaged(sinRes);
        for (Producto p : filtrados) catalogoGrid.getChildren().add(crearTarjetaProducto(p));
        actualizarChips();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TARJETA CATÁLOGO — click abre la vista detalle
    // ══════════════════════════════════════════════════════════════════════════
    private VBox crearTarjetaProducto(Producto p) {
        VBox card = new VBox(0);
        card.setPrefWidth(CARD_WIDTH); card.setMaxWidth(CARD_WIDTH); card.setMinWidth(CARD_WIDTH);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 12; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);");

        // Imagen
        StackPane imgZone = new StackPane();
        imgZone.setPrefHeight(IMG_HEIGHT); imgZone.setMinHeight(IMG_HEIGHT); imgZone.setMaxHeight(IMG_HEIGHT);
        imgZone.setStyle("-fx-background-color: #13111e; -fx-background-radius: 12 12 0 0;");

        ImageView iv = cargarImagenProducto(p, CARD_WIDTH, IMG_HEIGHT);
        if (iv != null) {
            Rectangle clip = new Rectangle(CARD_WIDTH, IMG_HEIGHT);
            clip.setArcWidth(24); clip.setArcHeight(24);
            imgZone.setClip(clip);
            imgZone.getChildren().add(iv);
        } else {
            Label lblEmoji = new Label(getEmojiCategoria(p.getIdCategoria()));
            lblEmoji.setStyle("-fx-font-size: 48px;");
            imgZone.getChildren().add(lblEmoji);
        }

        // Badge stock
        if (p.estaDisponible()) {
            Label badge = new Label("✓ Stock");
            badge.setStyle(
                "-fx-background-color: rgba(16,185,129,0.18); -fx-background-radius: 6; " +
                "-fx-border-color: rgba(16,185,129,0.35); -fx-border-radius: 6; " +
                "-fx-text-fill: #10b981; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8 3 8;");
            VBox bw = new VBox(badge); bw.setStyle("-fx-padding: 8 0 0 8;");
            StackPane.setAlignment(bw, Pos.TOP_LEFT);
            imgZone.getChildren().add(bw);
        }

        // Botón deseo
        Button btnDeseo = new Button(deseos.contiene(p.getId()) ? "♥" : "♡");
        btnDeseo.setStyle(
            "-fx-background-color: rgba(13,13,20,0.7); -fx-background-radius: 50%; " +
            "-fx-text-fill: " + (deseos.contiene(p.getId()) ? "#ef4444" : "#8F8AA8") + "; " +
            "-fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 4 6 4 6; " +
            "-fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 50%;");
        btnDeseo.setOnAction(e -> {
            if (deseos.contiene(p.getId())) deseos.quitar(p.getId());
            else deseos.agregar(p);
            e.consume(); // no propagar al click del card
            renderCatalogo();
        });
        VBox dw = new VBox(btnDeseo); dw.setStyle("-fx-padding: 8 8 0 0;");
        StackPane.setAlignment(dw, Pos.TOP_RIGHT);
        imgZone.getChildren().add(dw);

        // Cuerpo texto
        VBox body = new VBox(5);
        body.setStyle("-fx-padding: 12 14 14 14;");

        Categoria cat = getCategoriaById(p.getIdCategoria());
        Label lblCat = new Label(cat != null ? cat.getNombre().toUpperCase() : "PRODUCTO");
        lblCat.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: #8b7cf8;");
        lblCat.setPrefHeight(18); lblCat.setMinHeight(18); lblCat.setMaxHeight(18);

        Label lblNombre = new Label(p.getNombre());
        lblNombre.setPrefHeight(NOMBRE_HEIGHT); lblNombre.setMinHeight(NOMBRE_HEIGHT); lblNombre.setMaxHeight(NOMBRE_HEIGHT);
        lblNombre.setPrefWidth(CARD_WIDTH - 28); lblNombre.setMaxWidth(CARD_WIDTH - 28);
        lblNombre.setWrapText(true);
        lblNombre.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        Label lblDesc = new Label(p.getDescripcion());
        lblDesc.setPrefHeight(DESC_HEIGHT); lblDesc.setMinHeight(DESC_HEIGHT); lblDesc.setMaxHeight(DESC_HEIGHT);
        lblDesc.setPrefWidth(CARD_WIDTH - 28); lblDesc.setMaxWidth(CARD_WIDTH - 28);
        lblDesc.setWrapText(true);
        lblDesc.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #6b6890;");

        Region sep = new Region(); sep.setPrefHeight(6);

        // Precio + ícono carrito
        HBox precioRow = new HBox(8); precioRow.setAlignment(Pos.CENTER_LEFT);
        Label lblPrecio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(lblPrecio, Priority.ALWAYS);

        boolean yaEnCarrito = carrito.getItems().stream()
            .anyMatch(it -> it.getProducto().getId() == p.getId());
        Button btnCarrito = new Button(yaEnCarrito ? "✓" : "🛒");
        btnCarrito.setPrefSize(36, 36); btnCarrito.setMinSize(36, 36); btnCarrito.setMaxSize(36, 36);
        String estiloC = (yaEnCarrito
            ? "-fx-background-color: rgba(16,185,129,0.25); -fx-border-color: rgba(16,185,129,0.5); -fx-text-fill: #10b981;"
            : "-fx-background-color: #564AB5; -fx-border-color: transparent; -fx-text-fill: white;") +
            " -fx-background-radius: 50%; -fx-border-radius: 50%; " +
            "-fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0;";
        btnCarrito.setStyle(estiloC);
        btnCarrito.setOnMouseEntered(e ->
            btnCarrito.setStyle("-fx-background-color: #7c6fe0; -fx-border-color: transparent; -fx-text-fill: white; " +
                "-fx-background-radius: 50%; -fx-border-radius: 50%; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0; " +
                "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.55),10,0,0,3);"));
        btnCarrito.setOnMouseExited(e -> btnCarrito.setStyle(estiloC));
        btnCarrito.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            mostrarToast("🛒 \"" + p.getNombre() + "\" añadido al carrito.");
            e.consume();
            renderCatalogo();
        });

        precioRow.getChildren().addAll(lblPrecio, btnCarrito);
        int nv = p.getVariantes().size();
        Label lblVar = new Label(nv > 0 ? nv + " variante" + (nv > 1 ? "s" : "") : "Producto único");
        lblVar.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b6890;");
        lblVar.setPrefHeight(16); lblVar.setMinHeight(16); lblVar.setMaxHeight(16);

        body.getChildren().addAll(lblCat, lblNombre, lblDesc, sep, precioRow, lblVar);
        card.getChildren().addAll(imgZone, body);

        // Hover
        String cardNormal = "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 12; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);";
        String cardHover  = "-fx-background-color: #252540; -fx-background-radius: 12; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 12; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.35),20,0,0,6);";
        card.setOnMouseEntered(e -> card.setStyle(cardHover));
        card.setOnMouseExited(e  -> card.setStyle(cardNormal));

        // Click en la tarjeta → abre el detalle
        card.setOnMouseClicked(e -> {
            // Solo si no se clickeó un botón interno
            if (!(e.getTarget() instanceof Button)) {
                abrirDetalle(p);
            }
        });

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INICIO – HERO BANNER
    // ══════════════════════════════════════════════════════════════════════════
    private void construirHeroBanner() {
        // Imagen de fondo del banner
        ImageView bannerImg = null;
        try {
            java.io.InputStream is = getClass().getResourceAsStream("banner.png");
            if (is != null) {
                Image img = new Image(is);
                bannerImg = new ImageView(img);
                bannerImg.setPreserveRatio(false);
                // El ImageView se estirará con el StackPane via bindings
            }
        } catch (Exception ex) { /* si no existe, usamos gradiente */ }

        // Overlay oscuro encima de la imagen para que el texto sea legible
        javafx.scene.shape.Rectangle overlay = new javafx.scene.shape.Rectangle();
        overlay.widthProperty().bind(heroBanner.widthProperty());
        overlay.heightProperty().bind(heroBanner.heightProperty());
        overlay.setFill(javafx.scene.paint.Color.web("#0D0B17", 0.62));

        if (bannerImg != null) {
            // La imagen ocupa todo el banner
            bannerImg.fitWidthProperty().bind(heroBanner.widthProperty());
            bannerImg.fitHeightProperty().bind(heroBanner.heightProperty());
            heroBanner.getChildren().addAll(bannerImg, overlay);
        } else {
            // Fallback: gradiente si no hay imagen
            heroBanner.setStyle(
                "-fx-background-color: linear-gradient(to right, #0D0B17 0%, #1A1035 45%, #2A1A4A 100%);");
        }

        // Contenido del hero (igual que antes, ahora encima del fondo)
        HBox contenido = new HBox(40);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setPadding(new Insets(0, 56, 0, 56));

        VBox textoHero = new VBox(14);
        textoHero.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textoHero, Priority.ALWAYS);

        HBox badge = new HBox(6);
        badge.setAlignment(Pos.CENTER_LEFT);
        Label badgeDot = new Label("●");
        badgeDot.setStyle("-fx-font-size: 8px; -fx-text-fill: #BC7F15;");
        Label badgeText = new Label("NUEVA COLECCIÓN");
        badgeText.setStyle(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #BC7F15; " +
            "-fx-letter-spacing: 2;");
        badge.getChildren().addAll(badgeDot, badgeText);

        Label titulo = new Label("Tu tienda de música,\nafinada al detalle.");
        titulo.setStyle(
            "-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; " +
            "-fx-line-spacing: 4;");
        titulo.setWrapText(true);

        Label subtitulo = new Label(
            "Descubre instrumentos, equipos y accesorios para músicos\nde todos los niveles.");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #D0CAFF; -fx-line-spacing: 3;");

        HBox botones = new HBox(12);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button btnExplorar = new Button("Explorar tienda  →");
        btnExplorar.setStyle(
            "-fx-background-color: #BC7F15; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
            "-fx-cursor: hand; -fx-padding: 11 24 11 24; " +
            "-fx-effect: dropshadow(gaussian,rgba(188,127,21,0.45),16,0,0,5);");
        btnExplorar.setOnMouseEntered(e -> btnExplorar.setStyle(
            "-fx-background-color: #D4941A; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
            "-fx-cursor: hand; -fx-padding: 11 24 11 24; " +
            "-fx-effect: dropshadow(gaussian,rgba(188,127,21,0.6),20,0,0,6);"));
        btnExplorar.setOnMouseExited(e -> btnExplorar.setStyle(
            "-fx-background-color: #BC7F15; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
            "-fx-cursor: hand; -fx-padding: 11 24 11 24; " +
            "-fx-effect: dropshadow(gaussian,rgba(188,127,21,0.45),16,0,0,5);"));
        btnExplorar.setOnAction(e -> navegarCatalogo());

        Button btnOfertas = new Button("Ver ofertas");
        btnOfertas.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(255,255,255,0.55); -fx-border-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-cursor: hand; -fx-padding: 11 24 11 24;");
        btnOfertas.setOnAction(e -> navegarCatalogo());

        botones.getChildren().addAll(btnExplorar, btnOfertas);

        HBox trust = new HBox(20);
        trust.setAlignment(Pos.CENTER_LEFT);
        for (String t : new String[]{"✓  Envío gratis +$200k", "✓  Garantía 2 años", "✓  Soporte especializado"}) {
            Label lbl = new Label(t);
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(220,215,255,0.85);");
            trust.getChildren().add(lbl);
        }

        textoHero.getChildren().addAll(badge, titulo, subtitulo, botones, trust);

        contenido.getChildren().add(textoHero);
        heroBanner.getChildren().add(contenido);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INICIO – CATEGORÍAS
    // ══════════════════════════════════════════════════════════════════════════
    private void construirCategoriasBar() {
        categoriasBar.getChildren().clear();
        String[][] cats = {
            {"🎸", "Guitarras"}, {"🎹", "Pianos"}, {"🥁", "Percusión"},
            {"🎻", "Cuerdas"},   {"🎤", "Voz"},    {"🎧", "Accesorios"}
        };
        boolean primera = true;
        for (String[] cat : cats) {
            VBox chip = new VBox(8);
            chip.setAlignment(Pos.CENTER);
            chip.setPadding(new Insets(16, 12, 16, 12));
            HBox.setHgrow(chip, Priority.ALWAYS);  // cada chip ocupa el espacio disponible
            String base = primera
                ? "-fx-background-color: rgba(86,74,181,0.22); -fx-border-color: #564AB5;"
                : "-fx-background-color: #1E1A2E; -fx-border-color: rgba(86,74,181,0.2);";
            chip.setStyle(base +
                " -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand;");

            Label emojiLbl = new Label(cat[0]);
            emojiLbl.setStyle("-fx-font-size: 30px;");
            Label textoLbl = new Label(cat[1]);
            textoLbl.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; " +
                "-fx-text-fill: " + (primera ? "#A99CF0" : "#8F8AA8") + ";");

            chip.getChildren().addAll(emojiLbl, textoLbl);
            chip.setOnMouseEntered(e -> chip.setStyle(
                "-fx-background-color: rgba(86,74,181,0.22); -fx-border-color: #564AB5; " +
                "-fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand;"));
            chip.setOnMouseExited(e -> chip.setStyle(base +
                " -fx-background-radius: 14; -fx-border-radius: 14; -fx-cursor: hand;"));
            chip.setOnMouseClicked(e -> navegarCatalogo());

            categoriasBar.getChildren().add(chip);
            primera = false;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INICIO – MINI CARDS (rediseñadas)
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarProductosDestacadosInicio() {
        productosGrid.getChildren().clear();
        List<Producto> dest = catalogo.subList(0, Math.min(6, catalogo.size()));
        for (Producto p : dest) productosGrid.getChildren().add(crearMiniCard(p));
    }

    private VBox crearMiniCard(Producto p) {
        VBox card = new VBox(0);
        card.setPrefWidth(270); card.setMaxWidth(270); card.setMinWidth(270);
        String cardNormal =
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 14; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),14,0,0,5);";
        String cardHover =
            "-fx-background-color: #252540; -fx-background-radius: 14; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 14; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.4),22,0,0,7);";
        card.setStyle(cardNormal);

        // Zona imagen
        StackPane miniImg = new StackPane();
        miniImg.setPrefHeight(175); miniImg.setMinHeight(175); miniImg.setMaxHeight(175);
        miniImg.setStyle("-fx-background-color: #13111e; -fx-background-radius: 14 14 0 0;");

        ImageView iv = cargarImagenProducto(p, 270, 175);
        if (iv != null) {
            Rectangle clip = new Rectangle(270, 175);
            clip.setArcWidth(28); clip.setArcHeight(28);
            miniImg.setClip(clip);
            miniImg.getChildren().add(iv);
        } else {
            Label emoji = new Label(getEmojiCategoria(p.getIdCategoria()));
            emoji.setStyle("-fx-font-size: 44px;");
            miniImg.getChildren().add(emoji);
        }

        // Badge de categoría sobre la imagen
        Label badgeCat = new Label(getEmojiCategoria(p.getIdCategoria()) + "  " +
            getCategoriaById(p.getIdCategoria()) != null
                ? (getCategoriaById(p.getIdCategoria()) != null
                    ? getCategoriaById(p.getIdCategoria()).getNombre() : "")
                : "");
        badgeCat.setStyle(
            "-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 6; " +
            "-fx-text-fill: #E9E9ED; -fx-font-size: 9px; -fx-padding: 3 8 3 8;");
        StackPane.setAlignment(badgeCat, Pos.TOP_LEFT);
        badgeCat.setTranslateX(10); badgeCat.setTranslateY(10);

        // Botón carrito circular sobre imagen
        Button btnC = new Button("🛒");
        btnC.setPrefSize(36, 36); btnC.setMinSize(36, 36); btnC.setMaxSize(36, 36);
        btnC.setStyle(
            "-fx-background-color: #564AB5; -fx-background-radius: 50%; " +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.5),10,0,0,3);");
        btnC.setOnMouseEntered(e -> btnC.setStyle(
            "-fx-background-color: #6c5ce7; -fx-background-radius: 50%; " +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0;"));
        btnC.setOnMouseExited(e -> btnC.setStyle(
            "-fx-background-color: #564AB5; -fx-background-radius: 50%; " +
            "-fx-text-fill: white; -fx-font-size: 15px; -fx-cursor: hand; -fx-padding: 0;"));
        btnC.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            mostrarToast("🛒 \"" + p.getNombre() + "\" añadido al carrito.");
            e.consume();
        });
        VBox cw = new VBox(btnC); cw.setPadding(new Insets(0, 10, 10, 0));
        StackPane.setAlignment(cw, Pos.BOTTOM_RIGHT);

        miniImg.getChildren().addAll(badgeCat, cw);

        // Body
        VBox body = new VBox(6); body.setPadding(new Insets(12, 14, 14, 14));

        Label nombre = new Label(p.getNombre());
        nombre.setPrefHeight(38); nombre.setMinHeight(38); nombre.setMaxHeight(38);
        nombre.setPrefWidth(242); nombre.setMaxWidth(242);
        nombre.setWrapText(true);
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        Label desc = new Label(p.getDescripcion());
        desc.setPrefWidth(242); desc.setMaxWidth(242);
        desc.setWrapText(true);
        desc.setPrefHeight(30); desc.setMaxHeight(30);
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890;");

        HBox precioRow = new HBox();
        precioRow.setAlignment(Pos.CENTER_LEFT);
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        HBox.setHgrow(precio, Priority.ALWAYS);

        // Badge disponibilidad
        Label dispBadge = new Label(p.estaDisponible() ? "En stock" : "Sin stock");
        dispBadge.setStyle(p.estaDisponible()
            ? "-fx-font-size: 9px; -fx-text-fill: #10b981; -fx-background-color: rgba(16,185,129,0.12); " +
              "-fx-background-radius: 4; -fx-padding: 2 6 2 6;"
            : "-fx-font-size: 9px; -fx-text-fill: #FF6B6B; -fx-background-color: rgba(255,107,107,0.12); " +
              "-fx-background-radius: 4; -fx-padding: 2 6 2 6;");

        precioRow.getChildren().addAll(precio, dispBadge);
        body.getChildren().addAll(nombre, desc, precioRow);
        card.getChildren().addAll(miniImg, body);

        card.setOnMouseClicked(e -> { navegarCatalogo(); abrirDetalle(p); });
        card.setOnMouseEntered(e -> card.setStyle(cardHover));
        card.setOnMouseExited(e  -> card.setStyle(cardNormal));
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARRITO
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarVistaCarrito() {
        carritoItemsContainer.getChildren().clear();
        boolean vacio = carrito.isEmpty();
        lblCarritoVacio.setVisible(vacio); lblCarritoVacio.setManaged(vacio);
        for (ItemCarrito item : carrito.getItems())
            carritoItemsContainer.getChildren().add(crearFilaCarrito(item));
        lblCarritoTotal.setText("Total: $" + String.format("%,.2f", carrito.calcularTotal()));
    }

    private HBox crearFilaCarrito(ItemCarrito item) {
        HBox fila = new HBox(14); fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 16, 14, 16));
        fila.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10;");
        // Imagen del producto (fallback: emoji de categoría)
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(52, 52); imgBox.setMinSize(52, 52); imgBox.setMaxSize(52, 52);
        imgBox.setStyle("-fx-background-color: #13111e; -fx-background-radius: 8;");
        ImageView iv = cargarImagenProducto(item.getProducto(), 52, 52);
        if (iv != null) {
            Rectangle clip = new Rectangle(52, 52);
            clip.setArcWidth(16); clip.setArcHeight(16);
            imgBox.setClip(clip);
            imgBox.getChildren().add(iv);
        } else {
            Label emoji = new Label(getEmojiCategoria(item.getProducto().getIdCategoria()));
            emoji.setStyle("-fx-font-size: 24px;");
            imgBox.getChildren().add(emoji);
        }
        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label nombre = new Label(item.getProducto().getNombre());
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        String vd = item.getVariante() != null ? item.getVariante().getDescripcion() : "Estándar";
        Label variante = new Label("Variante: " + vd + "  ·  Cant: " + item.getCantidad());
        variante.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nombre, variante);
        Label subtotal = new Label("$" + String.format("%,.2f", item.calcularSubtotal()));
        subtotal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        Button btnQ = new Button("✕");
        btnQ.setStyle("-fx-background-color: rgba(255,107,107,0.1); -fx-background-radius: 6; " +
            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        int idVar = item.getVariante() != null ? item.getVariante().getId() : -1;
        btnQ.setOnAction(e -> { carrito.quitar(item.getProducto().getId(), idVar); actualizarVistaCarrito(); });
        fila.getChildren().addAll(imgBox, info, subtotal, btnQ);
        return fila;
    }

    @FXML private void handleDeshacerCarrito() {
        ItemCarrito ultimo = carrito.deshacer();
        if (ultimo != null) mostrarToast("↩ \"" + ultimo.getProducto().getNombre() + "\" eliminado del carrito.");
        actualizarVistaCarrito();
    }

    @FXML private void handleConfirmarPedido() {
         if (carrito.isEmpty()) return;

    Usuario u = UserStore.getUsuarioActivo();
    int idUsuario = (u != null) ? u.getId() : 0;

    ArrayList<ItemCarrito> items = new ArrayList<>(carrito.getItems());

    // ── CAMBIO CLAVE: pasar por Gestorpedidos ──────────────────────────────
    // crearPedido() asigna el ID correcto (contadorId++), encola en FIFO,
    // y llama a guardarEnArchivo() → escribe en harmonia_pedidos.txt e items.txt
    Pedido pedido = PedidoStore.getGestor()
                               .crearPedido(idUsuario, items, "Dirección por definir");

    // Mantener el historial local del usuario para la vista del comprador
    if (u != null) u.agregarAlHistorial(pedido);

    carrito.vaciar();
    actualizarVistaCarrito();
    lblCarritoVacio.setText("✅  ¡Pedido #" + pedido.getId() + " confirmado! Revisa tu historial en 📦 Pedidos.");
    lblCarritoVacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #10b981; " +
        "-fx-text-alignment: center; -fx-padding: 40 0 40 0; " +
        "-fx-background-color: rgba(16,185,129,0.06); -fx-background-radius: 12;");
    lblCarritoVacio.setVisible(true);
    lblCarritoVacio.setManaged(true);

    javafx.animation.PauseTransition pausa =
        new javafx.animation.PauseTransition(Duration.seconds(1.8));
    pausa.setOnFinished(e -> navegarPedidos());
    pausa.play();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PEDIDOS — HISTORIAL
    // ══════════════════════════════════════════════════════════════════════════

    private static final String TAB_PED_ACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 0; " +
        "-fx-border-color: transparent transparent #564AB5 transparent; " +
        "-fx-border-width: 0 0 2 0; " +
        "-fx-text-fill: #A99CF0; -fx-font-size: 13px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 18 0 18;";
    private static final String TAB_PED_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 0; " +
        "-fx-border-color: transparent; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 13px; " +
        "-fx-cursor: hand; -fx-padding: 0 18 0 18;";

    /** Actualiza toda la vista del historial de pedidos. */
    private void actualizarVistaPedidos() {
           Usuario u = UserStore.getUsuarioActivo();
    if (u == null) return;

    // ── CAMBIO CLAVE: leer del gestor (archivo) en vez del historial en memoria ──
    // Así los cambios de estado del admin se reflejan automáticamente
    List<Pedido> historial = PedidoStore.getGestor().getPorUsuario(u.getId());

    // ── Estadísticas ──────────────────────────────────────────────────────
    int    totalPedidos = historial.size();
    double totalGasto   = historial.stream().mapToDouble(Pedido::getTotal).sum();
    long   totalProds   = historial.stream()
                            .flatMap(p -> p.getItems().stream())
                            .mapToLong(ItemCarrito::getCantidad).sum();

    lblContadorPedidos.setText(totalPedidos + (totalPedidos == 1 ? " pedido" : " pedidos"));
    lblNumeroPedidos.setText(String.valueOf(totalPedidos));
    lblTotalGastado.setText("$" + String.format("%,.0f", totalGasto));
    lblProductosComprados.setText(String.valueOf(totalProds));

    // ── Filtrado por tab activo ───────────────────────────────────────────
    List<Pedido> filtrados = filtrarPorTab(historial, filtroPedidos);

    // ── Mostrar / ocultar estado vacío ────────────────────────────────────
    boolean vacio = filtrados.isEmpty();
    lblPedidosVacio.setVisible(vacio);
    lblPedidosVacio.setManaged(vacio);

    // ── Renderizar tarjetas (más recientes primero) ───────────────────────
    pedidosItemsContainer.getChildren().clear();
    List<Pedido> ordenados = new java.util.ArrayList<>(filtrados);
    java.util.Collections.reverse(ordenados);
    for (Pedido p : ordenados)
        pedidosItemsContainer.getChildren().add(crearTarjetaPedido(p));
    }

    /** Filtra la lista según el tab activo. */
    private List<Pedido> filtrarPorTab(List<Pedido> historial, String tab) {
        return switch (tab) {
        case "Confirmados" -> historial.stream()
            .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE
                      || p.getEstado() == EstadoPedido.EN_PROCESO)
            .collect(Collectors.toList());
        case "En camino" -> historial.stream()
            .filter(p -> p.getEstado() == EstadoPedido.ENVIADO)
            .collect(Collectors.toList());
        case "Entregados" -> historial.stream()
            .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
            .collect(Collectors.toList());
        default -> new java.util.ArrayList<>(historial);
    };
    }

    /** Verifica si el estado de un pedido coincide con alguno de los valores dados. */
    private boolean estadoPedidoEs(Pedido pedido, String... estados) {
        EstadoPedido estadoActual = pedido.getEstado();
        for (String e : estados)
            if (estadoActual.name().equalsIgnoreCase(e)) return true;
        return false;
    }

    /** Obtiene el texto del estado del pedido de forma segura. */
 

    /** Obtiene los ítems de un pedido de forma segura. */
    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerItemsPedido(Pedido pedido) {
        try {
            Object items = pedido.getClass().getMethod("getItems").invoke(pedido);
            if (items instanceof List<?>) return (List<ItemCarrito>) items;
        } catch (Exception ignored) {}
        return java.util.Collections.emptyList();
    }

    /** Calcula el total de un pedido sumando sus ítems. */
    private double calcularTotalPedido(Pedido pedido) {
        return obtenerItemsPedido(pedido).stream()
            .mapToDouble(ItemCarrito::calcularSubtotal)
            .sum();
    }

    /** Handler para los tabs de filtro de pedidos. */
    @FXML private void filtrarPedidos(javafx.event.ActionEvent e) {
        Button src = (Button) e.getSource();
        if      (src == tabPedTodos)       filtroPedidos = "Todos";
        else if (src == tabPedConfirmados) filtroPedidos = "Confirmados";
        else if (src == tabPedEnCamino)    filtroPedidos = "En camino";
        else if (src == tabPedEntregados)  filtroPedidos = "Entregados";
        // Actualizar estilos de tabs
        for (Button b : List.of(tabPedTodos, tabPedConfirmados, tabPedEnCamino, tabPedEntregados))
            b.setStyle(b == src ? TAB_PED_ACTIVO : TAB_PED_INACTIVO);
        actualizarVistaPedidos();
    }

    /** Construye la tarjeta visual de un pedido. */
    private VBox crearTarjetaPedido(Pedido pedido) {
        List<ItemCarrito> items = obtenerItemsPedido(pedido);
        double total = calcularTotalPedido(pedido);
        EstadoPedido estadoTexto = pedido.getEstado();

        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),10,0,0,4);");

        // ── CABECERA DE LA TARJETA ────────────────────────────────────────────
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 18, 14, 18));
        header.setStyle("-fx-border-color: rgba(86,74,181,0.12); -fx-border-width: 0 0 1 0;");

        Label lblNum = new Label("Pedido  #" + pedido.getId());
        lblNum.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");

        Region spacerH = new Region(); HBox.setHgrow(spacerH, Priority.ALWAYS);

        // Badge de estado
        Label estadoBadge = crearBadgeEstado(estadoTexto);

        // Cantidad de ítems
        int totalItems = items.stream().mapToInt(ItemCarrito::getCantidad).sum();
        Label lblItems = new Label(totalItems + " producto" + (totalItems != 1 ? "s" : ""));
        lblItems.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890;");

        header.getChildren().addAll(lblNum, spacerH, lblItems, estadoBadge);

        // ── TIMELINE DE ESTADO ────────────────────────────────────────────────
        HBox timeline = crearTimeline(estadoTexto);
        timeline.setPadding(new Insets(12, 18, 12, 18));
        timeline.setStyle("-fx-border-color: rgba(86,74,181,0.08); -fx-border-width: 0 0 1 0;");

        // ── LISTA DE ÍTEMS (máx 2 visibles + "y X más") ───────────────────────
        VBox listaItems = new VBox(8);
        listaItems.setPadding(new Insets(14, 18, 12, 18));

        int maxVisible = Math.min(2, items.size());
        for (int i = 0; i < maxVisible; i++) {
            ItemCarrito it = items.get(i);
            HBox fila = new HBox(10);
            fila.setAlignment(Pos.CENTER_LEFT);

            // Imagen del producto (fallback: emoji de categoría)
            StackPane imgBox = new StackPane();
            imgBox.setPrefSize(40, 40); imgBox.setMinSize(40, 40); imgBox.setMaxSize(40, 40);
            imgBox.setStyle("-fx-background-color: #13111e; -fx-background-radius: 6;");
            ImageView ivItem = cargarImagenProducto(it.getProducto(), 40, 40);
            if (ivItem != null) {
                Rectangle clip = new Rectangle(40, 40);
                clip.setArcWidth(12); clip.setArcHeight(12);
                imgBox.setClip(clip);
                imgBox.getChildren().add(ivItem);
            } else {
                Label emojiProd = new Label(getEmojiCategoria(it.getProducto().getIdCategoria()));
                emojiProd.setStyle("-fx-font-size: 18px; -fx-min-width: 28;");
                imgBox.getChildren().add(emojiProd);
            }

            VBox infoItem = new VBox(1); HBox.setHgrow(infoItem, Priority.ALWAYS);
            Label nomItem = new Label(it.getProducto().getNombre());
            nomItem.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #D8D6E8;");
            String vDesc = it.getVariante() != null ? "  ·  " + it.getVariante().getDescripcion() : "";
            Label detItem = new Label("Cant: " + it.getCantidad() + vDesc);
            detItem.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890;");
            infoItem.getChildren().addAll(nomItem, detItem);

            Label subItem = new Label("$" + String.format("%,.0f", it.calcularSubtotal()));
            subItem.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #A99CF0;");

            fila.getChildren().addAll(imgBox, infoItem, subItem);
            listaItems.getChildren().add(fila);
        }
        if (items.size() > 2) {
            Label masItems = new Label("+" + (items.size() - 2) + " producto" + (items.size() - 2 != 1 ? "s" : "") + " más");
            masItems.setStyle("-fx-font-size: 11px; -fx-text-fill: #564AB5; -fx-padding: 2 0 0 28;");
            listaItems.getChildren().add(masItems);
        }
        if (items.isEmpty()) {
            Label sinItems = new Label("Sin productos registrados");
            sinItems.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b6890;");
            listaItems.getChildren().add(sinItems);
        }

        // ── FOOTER: total + botones ───────────────────────────────────────────
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(12, 18, 16, 18));
        footer.setStyle("-fx-border-color: rgba(86,74,181,0.12); -fx-border-width: 1 0 0 0;");

        Label lblTotal = new Label("Total:  $" + String.format("%,.0f", total));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        HBox.setHgrow(lblTotal, Priority.ALWAYS);

        // Botón Reordenar
        Button btnReordenar = new Button("🔄  Reordenar");
        btnReordenar.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;");
        btnReordenar.setOnMouseEntered(ev -> btnReordenar.setStyle(
            "-fx-background-color: rgba(86,74,181,0.15); -fx-background-radius: 8; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 8; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));
        btnReordenar.setOnMouseExited(ev -> btnReordenar.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));
        btnReordenar.setOnAction(ev -> reordenarPedido(items));

        // Botón Ver dirección
        Button btnInfo = new Button("📍  " + obtenerDireccionPedido(pedido));
        btnInfo.setStyle(
            "-fx-background-color: transparent; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(188,127,21,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #BC7F15; -fx-font-size: 11px; -fx-cursor: default; -fx-padding: 7 12 7 12;");
        btnInfo.setMouseTransparent(true);

        footer.getChildren().addAll(lblTotal, btnInfo, btnReordenar);
        card.getChildren().addAll(header, timeline, listaItems, footer);

        // Hover sutil en la tarjeta completa
        card.setOnMouseEntered(ev -> card.setStyle(
            "-fx-background-color: #22203a; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.38); -fx-border-radius: 14; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.18),14,0,0,5);"));
        card.setOnMouseExited(ev -> card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),10,0,0,4);"));
        return card;
    }

    /** Crea el badge de color según el estado del pedido. */
    private Label crearBadgeEstado(EstadoPedido estadoTexto) {
         String color, bg, texto;
    switch (estadoTexto) {
        case PENDIENTE  -> { color = "#A99CF0"; bg = "rgba(86,74,181,0.14)";   texto = "📋  Pendiente";  }
        case EN_PROCESO -> { color = "#8b7cf8"; bg = "rgba(139,124,248,0.14)"; texto = "⚙  En proceso"; }
        case ENVIADO    -> { color = "#f59e0b"; bg = "rgba(245,158,11,0.14)";  texto = "🚚  Enviado";    }
        case ENTREGADO  -> { color = "#10b981"; bg = "rgba(16,185,129,0.14)";  texto = "✓  Entregado";  }
        default         -> { color = "#A99CF0"; bg = "rgba(86,74,181,0.14)";   texto = "📋  Pendiente";  }
    }
    Label badge = new Label(texto);
    badge.setStyle(
        "-fx-background-color: " + bg + "; -fx-background-radius: 20; " +
        "-fx-border-color: " + color + "44; -fx-border-radius: 20; " +
        "-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-font-weight: bold; " +
        "-fx-padding: 4 12 4 12;");
    return badge;
    }

    /**
     * Construye el timeline visual de 4 pasos:
     * Confirmado → En preparación → En camino → Entregado
     */
    private HBox crearTimeline(EstadoPedido estadoTexto) {
        // Índice numérico del estado actual (0–3)
    int pasoActual = switch (estadoTexto) {
        case PENDIENTE  -> 0;
        case EN_PROCESO -> 1;
        case ENVIADO    -> 2;
        case ENTREGADO  -> 3;
    };

    String[] pasos  = { "Pendiente", "En proceso", "Enviado", "Entregado" };
    String[] iconos = { "📋",         "⚙",          "🚚",       "✓"        };

    HBox timeline = new HBox(0);
    timeline.setAlignment(Pos.CENTER_LEFT);

    for (int i = 0; i < pasos.length; i++) {
        boolean hecho  = i <= pasoActual;
        boolean actual = i == pasoActual;

        // ── Círculo ──────────────────────────────────────────────────────
        StackPane circulo = new StackPane();
        circulo.setPrefSize(28, 28); circulo.setMinSize(28, 28);

        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(14);
        bg.setFill(javafx.scene.paint.Color.web(
            hecho ? "rgba(86,74,181,0.30)" : "rgba(60,56,80,0.4)"));
        bg.setStroke(javafx.scene.paint.Color.web(
            hecho ? "#564AB5" : "#3a3757"));
        bg.setStrokeWidth(actual ? 2.2 : 1.5);

        Label iconoLbl = new Label(hecho ? iconos[i] : String.valueOf(i + 1));
        iconoLbl.setStyle(
            "-fx-font-size: " + (hecho ? "12" : "10") + "px; " +
            "-fx-text-fill: " + (hecho ? "#A99CF0" : "#5a5775") + ";");
        circulo.getChildren().addAll(bg, iconoLbl);

        // ── Etiqueta ─────────────────────────────────────────────────────
        Label txtPaso = new Label(pasos[i]);
        txtPaso.setStyle(
            "-fx-font-size: 10px; " +
            "-fx-text-fill: " + (hecho ? "#A99CF0" : "#4a4760") + "; " +
            "-fx-font-weight: " + (actual ? "bold" : "normal") + ";");

        VBox paso = new VBox(4);
        paso.setAlignment(Pos.CENTER);
        paso.setMinWidth(70);
        paso.getChildren().addAll(circulo, txtPaso);
        timeline.getChildren().add(paso);

        // ── Línea conectora ───────────────────────────────────────────────
        if (i < pasos.length - 1) {
            Region linea = new Region();
            linea.setPrefHeight(2);
            linea.setPrefWidth(Double.MAX_VALUE);
            HBox.setHgrow(linea, Priority.ALWAYS);
            // La línea se llena solo si el siguiente paso ya fue alcanzado
            linea.setStyle("-fx-background-color: " +
                (i < pasoActual ? "#564AB5" : "#2e2c42") +
                "; -fx-background-radius: 2;");
            linea.setTranslateY(-10);
            timeline.getChildren().add(linea);
        }
    }
    return timeline;
    }

    /** Obtiene la dirección del pedido de forma segura. */
    private String obtenerDireccionPedido(Pedido pedido) {
        try {
            Object dir = pedido.getClass().getMethod("getDireccion").invoke(pedido);
            if (dir != null && !dir.toString().isBlank()) return dir.toString();
        } catch (Exception ignored) {}
        return "Dirección por definir";
    }

    /** Agrega los ítems de un pedido anterior al carrito (reordenar). */
    private void reordenarPedido(List<ItemCarrito> items) {
        if (items == null || items.isEmpty()) {
            mostrarToast("⚠ Este pedido no tiene productos para reordenar.");
            return;
        }
        for (ItemCarrito it : items)
            carrito.agregar(new ItemCarrito(it.getProducto(), it.getVariante(), it.getCantidad()));
        actualizarVistaCarrito();
        mostrarToast("🛒 " + items.size() + " producto(s) añadidos al carrito.");
        navegarCarrito();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DESEOS
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarVistaDeseos() {
        deseosItemsContainer.getChildren().clear();
        boolean vacio = deseos.isEmpty();
        lblDeseosVacio.setVisible(vacio); lblDeseosVacio.setManaged(vacio);
        for (Producto p : deseos.getProductos())
            deseosItemsContainer.getChildren().add(crearFilaDeseo(p));
    }

    private HBox crearFilaDeseo(Producto p) {
        HBox fila = new HBox(14); fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 16, 14, 16));
        fila.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(188,127,21,0.2); -fx-border-radius: 10;");
        // Imagen del producto (fallback: emoji de categoría)
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(52, 52); imgBox.setMinSize(52, 52); imgBox.setMaxSize(52, 52);
        imgBox.setStyle("-fx-background-color: #13111e; -fx-background-radius: 8;");
        ImageView iv = cargarImagenProducto(p, 52, 52);
        if (iv != null) {
            Rectangle clip = new Rectangle(52, 52);
            clip.setArcWidth(16); clip.setArcHeight(16);
            imgBox.setClip(clip);
            imgBox.getChildren().add(iv);
        } else {
            Label emoji = new Label(getEmojiCategoria(p.getIdCategoria()));
            emoji.setStyle("-fx-font-size: 24px;");
            imgBox.getChildren().add(emoji);
        }
        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label nombre = new Label(p.getNombre());
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label desc = new Label(p.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nombre, desc);
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        Button btnMover = new Button("🛒");
        btnMover.setStyle("-fx-background-color: rgba(86,74,181,0.2); -fx-background-radius: 6; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        btnMover.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            deseos.quitar(p.getId()); actualizarVistaDeseos(); actualizarVistaCarrito();
        });
        Button btnQ = new Button("✕");
        btnQ.setStyle("-fx-background-color: rgba(255,107,107,0.1); -fx-background-radius: 6; " +
            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        btnQ.setOnAction(e -> { deseos.quitar(p.getId()); actualizarVistaDeseos(); });
        fila.getChildren().addAll(imgBox, info, precio, btnMover, btnQ);
        return fila;
    }

    @FXML private void handleDeshacerDeseos() {
        Producto ultimo = deseos.deshacer();
        if (ultimo != null) mostrarToast("↩ \"" + ultimo.getNombre() + "\" removido de deseos.");
        actualizarVistaDeseos();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIL
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private void handleEditarPerfil() {
        modoEdicion = !modoEdicion;
        seccionEditar.setVisible(modoEdicion); seccionEditar.setManaged(modoEdicion);
        if (btnEditarPerfil != null) btnEditarPerfil.setText(modoEdicion ? "✕  Cancelar" : "✏   Editar");
        if (!modoEdicion) mostrarFeedbackPerfil("", "");
    }

    @FXML private void handleGuardarPerfil() {
        String nombre = txtEditNombre.getText().trim();
        String instrumento = txtEditInstrumento.getText().trim();
        String presStr = txtEditPresupuesto.getText().trim();
        if (nombre.isEmpty()) { mostrarFeedbackPerfil("⚠ El nombre no puede estar vacío.", FB_ERROR); return; }
        double presup;
        try {
            presup = Double.parseDouble(presStr);
            if (presup < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) { mostrarFeedbackPerfil("⚠ Presupuesto inválido.", FB_ERROR); return; }
        lblPerfilNombre.setText(nombre); lblSidebarNombre.setText(primerNombre(nombre));
        lblGreeting.setText("Hola, " + primerNombre(nombre) + " 👋");
        lblInstrumento.setText(instrumento.isBlank() ? "Sin definir" : instrumento);
        lblPresupuesto.setText(String.format("$%.2f", presup));
        mostrarFeedbackPerfil("✓ Perfil actualizado.", FB_OK);
        modoEdicion = false;
        seccionEditar.setVisible(false); seccionEditar.setManaged(false);
    }

    @FXML private void handleCambiarFoto() { mostrarFeedbackPerfil("📷 Funcionalidad de foto próximamente.", FB_OK); }

    private void mostrarFeedbackPerfil(String msg, String estilo) {
        lblPerfilFeedback.setText(msg); lblPerfilFeedback.setStyle(estilo);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LOGOUT
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private void handleLogout() {
        UserStore.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("harmoniaa.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root)); stage.setTitle("Harmonia");
            root.setOpacity(0.0);
            FadeTransition ft = new FadeTransition(Duration.millis(350), root);
            ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CHIPS
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarChips() {
        chipsActivos.getChildren().clear();
        boolean hay = false;
        if (!filtroCat.equals("Todas")) {
            chipsActivos.getChildren().add(crearChip("🎵 " + filtroCat, () -> {
                filtroCat = "Todas"; catTodas.setStyle(ESTILO_FILTRO_ACTIVO);
                for (Button b : List.of(catGuitarras, catTeclados, catPercusion, catVientos, catAccesorios))
                    b.setStyle(ESTILO_FILTRO_INACTIVO);
                renderCatalogo();
            })); hay = true;
        }
        double min = sliderMin.getValue(), max = sliderMax.getValue();
        if (min > 0 || max < 10_000_000) {
            chipsActivos.getChildren().add(crearChip(String.format("💰 $%,.0f – $%,.0f", min, max), () -> {
                sliderMin.setValue(0); sliderMax.setValue(10_000_000);
                actualizarLabelRango(); renderCatalogo();
            })); hay = true;
        }
        if (!busqueda.isEmpty()) {
            chipsActivos.getChildren().add(crearChip("🔍 \"" + busqueda + "\"", () -> {
                busqueda = ""; txtCatalogoBuscar.clear(); renderCatalogo();
            })); hay = true;
        }
        chipsActivos.setVisible(hay); chipsActivos.setManaged(hay);
    }

    private HBox crearChip(String texto, Runnable onQuitar) {
        HBox chip = new HBox(6); chip.setAlignment(Pos.CENTER_LEFT);
        chip.setStyle("-fx-background-color: rgba(86,74,181,0.18); -fx-background-radius: 20; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 20; -fx-padding: 4 10 4 12;");
        Label lbl = new Label(texto); lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #A99CF0;");
        Button x = new Button("✕");
        x.setStyle("-fx-background-color: transparent; -fx-text-fill: #A99CF0; " +
            "-fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 0 0 0 2;");
        x.setOnAction(e -> onQuitar.run());
        chip.getChildren().addAll(lbl, x);
        return chip;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════════════════════════════════════
    private Categoria getCategoriaById(int id) {
        return switch (id) {
            case 1 -> new Categoria(1, "Guitarras");
            case 2 -> new Categoria(2, "Teclados");
            case 3 -> new Categoria(3, "Percusión");
            case 4 -> new Categoria(4, "Vientos");
            case 5 -> new Categoria(5, "Accesorios");
            default -> null;
        };
    }

    private String getEmojiCategoria(int idCat) {
        return switch (idCat) {
            case 1 -> "🎸"; case 2 -> "🎹"; case 3 -> "🥁";
            case 4 -> "🎺"; case 5 -> "🎧"; default -> "🎵";
        };
    }

    private void mostrarToast(String msg) {
        String original = lblGreeting.getText();
        lblGreeting.setText(msg);
        javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> lblGreeting.setText(original));
        pause.play();
    }

    private static String primerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "Usuario";
        return nombreCompleto.trim().split("\\s+")[0];
    }
}