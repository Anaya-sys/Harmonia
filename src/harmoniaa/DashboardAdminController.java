package harmoniaa;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


/**
 * ══════════════════════════════════════════════════════════════════════════════
 *  DashboardAdminController  —  Panel de Administración Harmonia
 * ══════════════════════════════════════════════════════════════════════════════
 *
 *  Responsabilidades:
 *  ─────────────────
 *  • Sidebar colapsable (68 ↔ 230 px animado)
 *  • Overview: stat-cards, cola FIFO, pedidos recientes
 *  • Gestión de pedidos: filtro por estado, búsqueda, avance de estado
 *  • Perfil del administrador
 *  • Logout → pantalla de login
 *
 *  Solo este controlador llama a GestorPedidos#avanzarEstado() y
 *  GestorPedidos#procesarSiguiente().  El panel de usuario (comprador)
 *  es de solo lectura.
 * ══════════════════════════════════════════════════════════════════════════════
 */
public class DashboardAdminController {
   // ── SIDEBAR ───────────────────────────────────────────────────────────────
    @FXML private VBox  sidebar;
    @FXML private HBox  logoBox;
    @FXML private HBox  adminBadgeBox;
    @FXML private javafx.scene.image.ImageView logoIcono;
    @FXML private javafx.scene.image.ImageView logoCompleto;
    @FXML private HBox  userInfoBox;
    @FXML private VBox  userTextBox;
    @FXML private Label lblAvatarInitial;
    @FXML private Label lblSidebarNombre;
    @FXML private Label lblSidebarRol;
    @FXML private Button btnLogout;
 
    // ── NAV BUTTONS ───────────────────────────────────────────────────────────
    @FXML private Button navBtnDashboard;
    @FXML private Button navBtnPedidos;
    @FXML private Button navBtnCatalogo;
    @FXML private Button navBtnUsuarios;
    @FXML private Button navBtnPerfil;
 
    // ── PANES ─────────────────────────────────────────────────────────────────
    @FXML private ScrollPane paneOverview;
    @FXML private ScrollPane panePedidosAdmin;
    @FXML private ScrollPane paneCatalogoAdmin;
    @FXML private ScrollPane paneUsuariosAdmin;
    @FXML private ScrollPane panePerfilAdmin;
 
    // ── OVERVIEW ──────────────────────────────────────────────────────────────
    @FXML private Label  lblToastAdmin;
    @FXML private Label  lblSubtituloDashboard;
    @FXML private Label  lblTotalPedidos;
    @FXML private Label  lblTotalPedidosHint;
    @FXML private Label  lblEnCola;
    @FXML private Label  lblEnColaHint;
    @FXML private Label  lblIngresos;
    @FXML private Label  lblIngresosHint;
    @FXML private Label  lblEntregados;
    @FXML private Label  lblEntregadosHint;
    @FXML private Label  lblColaBadge;
    @FXML private VBox   colaContainer;
    @FXML private VBox   recentContainer;
 
    // ── PEDIDOS ADMIN ─────────────────────────────────────────────────────────
    @FXML private Label     lblPedidosSubtitulo;
    @FXML private TextField txtBuscarPedido;
    @FXML private Button    tabAdminTodos;
    @FXML private Button    tabAdminPendiente;
    @FXML private Button    tabAdminEnProceso;
    @FXML private Button    tabAdminEnviado;
    @FXML private Button    tabAdminEntregado;
    @FXML private Label     lblContadorFiltro;
    @FXML private VBox      pedidosVacioAdmin;
    @FXML private VBox      pedidosContainer;
 
    // ── PERFIL ADMIN ──────────────────────────────────────────────────────────
    @FXML private Label lblPerfilInitialAdmin;
    @FXML private Label lblPerfilNombreAdmin;
    @FXML private Label lblPerfilEmailAdmin;
    @FXML private Label lblResumenTotal;
    @FXML private Label lblResumenCola;
    @FXML private Label lblResumenEntregados;
 
    // ── ESTADO INTERNO ────────────────────────────────────────────────────────
    private final Gestorpedidos gestor = PedidoStore.getGestor();
 
    /** Filtro activo en el panel de pedidos ("Todos", "PENDIENTE", …). */
    private String filtroAdmin = "Todos";
 
    /** Texto de búsqueda activo. */
    private String busquedaAdmin = "";
 
    private static final DateTimeFormatter FMT_FECHA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
 
    // ── SIDEBAR ANIMATION ─────────────────────────────────────────────────────
    private boolean  sidebarExpanded = false;
    private Timeline sidebarAnim;
 
    private static final double   SIDEBAR_COLLAPSED = 68.0;
    private static final double   SIDEBAR_EXPANDED  = 230.0;
    private static final Duration ANIM_DURATION     = Duration.millis(200);
 
    private static final String[][] NAV_TEXTOS = {
        { "📊", "  Dashboard" },
        { "📦", "  Pedidos"   },
        { "🏷️", "  Catálogo"  },
        { "👥", "  Usuarios"  },
        { "👤", "  Perfil"    }
    };
 
    // ── ESTILOS ───────────────────────────────────────────────────────────────
    private static final String ESTILO_NAV_ACTIVO =
        "-fx-background-color: #564AB5; -fx-background-radius: 12; " +
        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 12 0 14;";
 
    private static final String ESTILO_NAV_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 12; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 15px; " +
        "-fx-cursor: hand; -fx-padding: 0 12 0 14;";
 
    private static final String ESTILO_TAB_ACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 0; " +
        "-fx-border-color: transparent transparent #564AB5 transparent; " +
        "-fx-border-width: 0 0 2 0; -fx-text-fill: #A99CF0; " +
        "-fx-font-size: 12px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 20 0 20;";
 
    private static final String ESTILO_TAB_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 0; " +
        "-fx-border-color: transparent; -fx-text-fill: #8F8AA8; " +
        "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 20 0 20;";
 
    // ══════════════════════════════════════════════════════════════════════════
    //  INITIALIZE
    // ══════════════════════════════════════════════════════════════════════════
 
    @FXML
    public void initialize() {
        inicializarSidebar();
        cargarDatosAdmin();
        CatalogoStore.inicializarBaseSiVacio();
        actualizarOverview();
        actualizarTabsEstilo(tabAdminTodos);
        mostrarSeccion(paneOverview, navBtnDashboard);
        
        CatalogoStore.addChangeListener(() -> {
        // Platform.runLater asegura que los cambios visuales se hagan de forma segura en el hilo de JavaFX
        javafx.application.Platform.runLater(() -> {
            renderCatalogoAdmin(); // Refresca el inventario/catálogo del admin con las nuevas imágenes o stock
            actualizarOverview();  // Refresca las tarjetas de alertas y bajo stock inmediatamente
        });
    });
        
        
    }
 
    // ── Datos de sesión del admin en sidebar/perfil ───────────────────────────
    private void cargarDatosAdmin() {
        Usuario u = UserStore.getUsuarioActivo();
        if (u == null) return;
 
        String inicial = (u.getNombre() != null && !u.getNombre().isBlank())
            ? String.valueOf(u.getNombre().trim().toUpperCase().charAt(0))
            : "A";
 
        lblAvatarInitial.setText(inicial);
        lblSidebarNombre.setText(primerNombre(u.getNombre()));
        lblSidebarRol.setText("ADMINISTRADOR");
 
        if (lblPerfilInitialAdmin != null) lblPerfilInitialAdmin.setText(inicial);
        if (lblPerfilNombreAdmin  != null) lblPerfilNombreAdmin.setText(u.getNombre());
        if (lblPerfilEmailAdmin   != null) lblPerfilEmailAdmin.setText(u.getEmail());
    }
 
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
    
    
     private ImageView cargarImagenProducto(Producto p, double ancho, double alto) {
        // 1. Imágenes del catálogo base (classpath: prodimg/pN_xxx.png)
        String ruta = IMG_PRODUCTO.get(p.getId());
        if (ruta != null) {
            try {
                var stream = getClass().getResourceAsStream(ruta);
                if (stream != null) {
                    Image img = new Image(stream, ancho, alto, true, true);
                    if (!img.isError()) {
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(ancho); iv.setFitHeight(alto);
                        iv.setPreserveRatio(true); iv.setSmooth(true);
                        return iv;
                    }
                }
            } catch (Exception ignored) {}
        }
        // 2. Imágenes de productos admin (archivo en disco: prodimg/)
        String adminPath = CatalogoStore.getImagenAdmin(p.getId());
        if (adminPath != null && !adminPath.isBlank()) {
            try {
                java.io.File f = new java.io.File(adminPath);
                if (f.exists()) {
                    Image img = new Image(f.toURI().toString(), ancho, alto, true, true);
                    if (!img.isError()) {
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(ancho); iv.setFitHeight(alto);
                        iv.setPreserveRatio(true); iv.setSmooth(true);
                        return iv;
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
    
     
       
    
    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR COLAPSABLE
    // ══════════════════════════════════════════════════════════════════════════
 
    private void inicializarSidebar() {
        sidebar.setPrefWidth(SIDEBAR_COLLAPSED);
        sidebar.setMinWidth(SIDEBAR_COLLAPSED);
        sidebar.setMaxWidth(SIDEBAR_COLLAPSED);
        userTextBox.setVisible(false);   userTextBox.setManaged(false);
        btnLogout.setVisible(false);     btnLogout.setManaged(false);
        adminBadgeBox.setVisible(false); adminBadgeBox.setManaged(false);
        actualizarTextosBotones(false);
    }
 
    @FXML
    private void handleSidebarEntered() {
        if (sidebarExpanded) return;
        sidebarExpanded = true;
 
        logoIcono.setVisible(false);    logoIcono.setManaged(false);
        logoCompleto.setVisible(true);  logoCompleto.setManaged(true);
        logoBox.setStyle("-fx-padding: 16 12 16 12; -fx-background-color: #0D0B17;");
        sidebar.setStyle("-fx-background-color: #0D0B17; " +
                         "-fx-border-color: rgba(86,74,181,0.25); " +
                         "-fx-border-width: 0 1 0 0; -fx-effect: null;");
 
        adminBadgeBox.setVisible(true); adminBadgeBox.setManaged(true);
        actualizarTextosBotones(true);
        userTextBox.setVisible(true);   userTextBox.setManaged(true);
        btnLogout.setVisible(true);     btnLogout.setManaged(true);
        animarSidebar(SIDEBAR_EXPANDED);
    }
 
    @FXML
    private void handleSidebarExited() {
        if (!sidebarExpanded) return;
        sidebarExpanded = false;
 
        logoCompleto.setVisible(false); logoCompleto.setManaged(false);
        logoIcono.setVisible(true);     logoIcono.setManaged(true);
        logoBox.setStyle("-fx-padding: 16 12 16 12; -fx-background-color: #120F1E;");
        sidebar.setStyle("-fx-background-color: #1E1A2E; " +
                         "-fx-border-color: rgba(86,74,181,0.25); " +
                         "-fx-border-width: 0 1 0 0;");
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
 
        sidebarAnim = new Timeline(
            new KeyFrame(ANIM_DURATION,
                new KeyValue(widthProp, targetWidth, javafx.animation.Interpolator.EASE_BOTH))
        );
        sidebarAnim.setOnFinished(e -> {
            if (!sidebarExpanded) {
                actualizarTextosBotones(false);
                adminBadgeBox.setVisible(false); adminBadgeBox.setManaged(false);
                userTextBox.setVisible(false);   userTextBox.setManaged(false);
                btnLogout.setVisible(false);     btnLogout.setManaged(false);
            }
        });
        sidebarAnim.play();
    }
 
    private void actualizarTextosBotones(boolean conTexto) {
        Button[] btns = { navBtnDashboard, navBtnPedidos, navBtnCatalogo,
                          navBtnUsuarios, navBtnPerfil };
        for (int i = 0; i < btns.length; i++) {
            if (btns[i] == null) continue;
            btns[i].setText(conTexto ? NAV_TEXTOS[i][0] + NAV_TEXTOS[i][1] : NAV_TEXTOS[i][0]);
            btns[i].setAlignment(conTexto ? Pos.CENTER_LEFT : Pos.CENTER);
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN
    // ══════════════════════════════════════════════════════════════════════════
 
    private void mostrarSeccion(Node pane, Button navBtn) {
        List<Node> panes = List.of(paneOverview, panePedidosAdmin,
                                   paneCatalogoAdmin, paneUsuariosAdmin, panePerfilAdmin);
        panes.forEach(n -> { n.setVisible(false); n.setManaged(false); });
 
        pane.setVisible(true); pane.setManaged(true);
        pane.setOpacity(0.0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), pane);
        ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
 
        List<Button> navBtns = List.of(navBtnDashboard, navBtnPedidos,
                                       navBtnCatalogo, navBtnUsuarios, navBtnPerfil);
        navBtns.forEach(b -> b.setStyle(ESTILO_NAV_INACTIVO));
        navBtn.setStyle(ESTILO_NAV_ACTIVO);
 
        ScaleTransition sc = new ScaleTransition(Duration.millis(140), navBtn);
        sc.setFromX(0.93); sc.setFromY(0.93); sc.setToX(1.0); sc.setToY(1.0);
        sc.play();
    }
 
    @FXML
    private void navegarDashboard() {
        gestor.recargarDatos(); // ← Recarga los datos al entrar
        actualizarOverview();
        mostrarSeccion(paneOverview, navBtnDashboard);
    }
 
    @FXML
    private void navegarPedidosAdmin() {
        gestor.recargarDatos(); 
        filtroAdmin   = "Todos";
        busquedaAdmin = "";
        if (txtBuscarPedido != null) txtBuscarPedido.clear();
        actualizarTabsEstilo(tabAdminTodos);
        renderPedidosAdmin();
        mostrarSeccion(panePedidosAdmin, navBtnPedidos);
    }
 
    @FXML
    private void navegarCatalogoAdmin() {
        renderCatalogoAdmin();
        mostrarSeccion(paneCatalogoAdmin, navBtnCatalogo);
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  CATÁLOGO ADMIN — Gestión de productos
    // ══════════════════════════════════════════════════════════════════════════
 private String rutaImagenSeleccionada = null;
    private void renderCatalogoAdmin() {
    VBox contenedor;
        if (paneCatalogoAdmin.getContent() instanceof VBox vb) {
            contenedor = vb;
        } else {
            contenedor = new VBox(20);
            contenedor.setStyle("-fx-padding: 28 32 40 32; -fx-background-color: #151221;");
            paneCatalogoAdmin.setContent(contenedor);
            paneCatalogoAdmin.setFitToWidth(true);
        }
        contenedor.getChildren().clear();
        rutaImagenSeleccionada = null; // reset
 
        // ── Encabezado ────────────────────────────────────────────────────────
        HBox encabezado = new HBox(14);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("🏷️  Gestión de Catálogo");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(titulo, Priority.ALWAYS);
        int totalProds = CatalogoStore.getTodos().size();
        Label contador = new Label(totalProds + " producto" +
            (totalProds != 1 ? "s" : "") + " en inventario");
        contador.setStyle("-fx-font-size: 12px; -fx-text-fill: #A99CF0; " +
            "-fx-background-color: rgba(86,74,181,0.15); -fx-background-radius: 20; " +
            "-fx-padding: 4 14 4 14;");
        encabezado.getChildren().addAll(titulo, contador);
        contenedor.getChildren().add(encabezado);
 
        // ── Formulario ────────────────────────────────────────────────────────
        VBox formCard = new VBox(16);
        formCard.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 14; -fx-padding: 22 24 22 24;");
 
        Label formTitulo = new Label("➕  Agregar nuevo producto al catálogo");
        formTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
 
        javafx.scene.control.Separator sepForm = new javafx.scene.control.Separator();
        sepForm.setStyle("-fx-background-color: rgba(86,74,181,0.15);");
 
        String tfStyle = "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #E9E9ED; -fx-prompt-text-fill: #8F8AA8; -fx-font-size: 13px;";
 
        // Fila 1: nombre + descripción
        HBox fila1 = new HBox(14);
        javafx.scene.control.TextField txtNomProd = new javafx.scene.control.TextField();
        txtNomProd.setPromptText("Nombre del producto"); txtNomProd.setStyle(tfStyle);
        HBox.setHgrow(txtNomProd, Priority.ALWAYS);
        javafx.scene.control.TextField txtDescProd = new javafx.scene.control.TextField();
        txtDescProd.setPromptText("Descripción breve"); txtDescProd.setStyle(tfStyle);
        HBox.setHgrow(txtDescProd, Priority.ALWAYS);
        fila1.getChildren().addAll(txtNomProd, txtDescProd);
 
        // Fila 2: precio + stock + categoría
        HBox fila2 = new HBox(14);
        javafx.scene.control.TextField txtPrecioProd = new javafx.scene.control.TextField();
        txtPrecioProd.setPromptText("Precio (ej: 850000)"); txtPrecioProd.setStyle(tfStyle);
        HBox.setHgrow(txtPrecioProd, Priority.ALWAYS);
        javafx.scene.control.TextField txtStockProd = new javafx.scene.control.TextField();
        txtStockProd.setPromptText("Stock inicial"); txtStockProd.setStyle(tfStyle);
        HBox.setHgrow(txtStockProd, Priority.ALWAYS);
        javafx.scene.control.ComboBox<String> cmbCat = new javafx.scene.control.ComboBox<>();
        cmbCat.getItems().addAll("Guitarras", "Teclados", "Percusión", "Vientos", "Accesorios");
        cmbCat.setPromptText("Categoría");
        cmbCat.setStyle("-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.3); -fx-border-radius: 8; " +
            "-fx-text-fill: #E9E9ED; -fx-font-size: 12px;");
        cmbCat.setPrefWidth(160);
        fila2.getChildren().addAll(txtPrecioProd, txtStockProd, cmbCat);

        // Fila 3: VARIANTES ──────────────────────────────────────────────────
        // Lista interna donde se acumulan las variantes creadas antes de guardar.
        // Cada elemento es String[3]: [nombre, precioExtra, stock]
        java.util.List<String[]> variantesTemp = new java.util.ArrayList<>();

        VBox variantesZona = new VBox(10);
        variantesZona.setStyle(
            "-fx-background-color: rgba(86,74,181,0.06); -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10; -fx-padding: 14 16 14 16;");

        Label lblVarTitulo = new Label("🎨  Variantes (opcional)");
        lblVarTitulo.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #A99CF0;");

        Label lblVarHint = new Label("Si no agregas variantes, se crea automáticamente una variante Estándar con el stock de arriba.");
        lblVarHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890;");
        lblVarHint.setWrapText(true);

        // VBox donde aparecen las filas de variantes ya añadidas
        VBox variantesLista = new VBox(6);

        // Fila de entrada: nombre + precio extra + stock + botón +
        HBox filaEntradaVar = new HBox(10);
        filaEntradaVar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        javafx.scene.control.TextField txtVarNombre = new javafx.scene.control.TextField();
        txtVarNombre.setPromptText("Nombre variante (ej: Rojo)");
        txtVarNombre.setStyle(tfStyle);
        HBox.setHgrow(txtVarNombre, Priority.ALWAYS);

        javafx.scene.control.TextField txtVarPrecioExtra = new javafx.scene.control.TextField();
        txtVarPrecioExtra.setPromptText("Precio extra (0)");
        txtVarPrecioExtra.setPrefWidth(120);
        txtVarPrecioExtra.setStyle(tfStyle);

        javafx.scene.control.TextField txtVarStock = new javafx.scene.control.TextField();
        txtVarStock.setPromptText("Stock");
        txtVarStock.setPrefWidth(80);
        txtVarStock.setStyle(tfStyle);

        Button btnAddVar = new Button("+ Añadir");
        btnAddVar.setStyle(
            "-fx-background-color: rgba(86,74,181,0.18); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 7; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 12 6 12;");

        // Label de feedback inline para variantes
        Label lblVarError = new Label("");
        lblVarError.setStyle("-fx-font-size: 11px; -fx-text-fill: #ef4444;");

        // Label stock total (se actualiza cada vez que se añade/elimina una variante)
        Label lblStockTotal = new Label("");
        lblStockTotal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        lblStockTotal.setVisible(false);
        lblStockTotal.setManaged(false);

        // Recalcula el total y actualiza visibilidad del campo "Stock inicial"
        Runnable actualizarTotal = () -> {
            int total = variantesTemp.stream()
                .mapToInt(vd -> { try { return Integer.parseInt(vd[2]); } catch (Exception ex) { return 0; } })
                .sum();
            boolean hayVariantes = !variantesTemp.isEmpty();
            lblStockTotal.setText("Stock total: " + total + " unidades");
            lblStockTotal.setVisible(hayVariantes);
            lblStockTotal.setManaged(hayVariantes);
            // Ocultar/mostrar el campo "Stock inicial" según haya variantes
            txtStockProd.setDisable(hayVariantes);
            txtStockProd.setOpacity(hayVariantes ? 0.35 : 1.0);
            if (hayVariantes) {
                txtStockProd.setPromptText("Definido por variantes");
                txtStockProd.clear();
            } else {
                txtStockProd.setPromptText("Stock inicial");
            }
        };

        // Conectar actualizarTotal al botón añadir y al eliminar
        btnAddVar.setOnAction(ev -> {
            String vNom   = txtVarNombre.getText().trim();
            String vPreEx = txtVarPrecioExtra.getText().trim().isEmpty() ? "0" : txtVarPrecioExtra.getText().trim();
            String vStock = txtVarStock.getText().trim();
            if (vNom.isEmpty() || vStock.isEmpty()) {
                lblVarError.setText("⚠ Nombre y stock son obligatorios.");
                return;
            }
            try {
                double pe = Double.parseDouble(vPreEx.replace(",","").replace(".",""));
                int    vs = Integer.parseInt(vStock);
                if (vs < 0 || pe < 0) throw new NumberFormatException();
                lblVarError.setText("");

                HBox filaVar = new HBox(8);
                filaVar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                filaVar.setStyle(
                    "-fx-background-color: rgba(86,74,181,0.1); -fx-background-radius: 6; " +
                    "-fx-padding: 5 10 5 10;");

                Label lNom = new Label(vNom);
                lNom.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
                HBox.setHgrow(lNom, Priority.ALWAYS);

                Label lPe = new Label(pe == 0 ? "sin recargo" : "+ $" + String.format("%,.0f", pe));
                lPe.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (pe == 0 ? "#6b6890" : "#BC7F15") + ";");

                Label lSt = new Label("Stock: " + vs);
                lSt.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (vs <= 5 ? "#ef4444" : "#10b981") + ";");

                String[] dataRef = {vNom, vPreEx, vStock};
                Button btnElim = new Button("✕");
                btnElim.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.1); -fx-background-radius: 5; " +
                    "-fx-text-fill: #ef4444; -fx-font-size: 10px; -fx-cursor: hand; -fx-padding: 3 7 3 7;");
                btnElim.setOnAction(evv -> {
                    variantesTemp.remove(dataRef);
                    variantesLista.getChildren().remove(filaVar);
                    actualizarTotal.run();
                });

                filaVar.getChildren().addAll(lNom, lPe, lSt, btnElim);
                variantesTemp.add(dataRef);
                variantesLista.getChildren().add(filaVar);

                txtVarNombre.clear();
                txtVarPrecioExtra.clear();
                txtVarStock.clear();
                txtVarNombre.requestFocus();
                actualizarTotal.run();

            } catch (NumberFormatException ex) {
                lblVarError.setText("⚠ Precio extra y stock deben ser números válidos.");
            }
        });

        filaEntradaVar.getChildren().addAll(txtVarNombre, txtVarPrecioExtra, txtVarStock, btnAddVar);
        variantesZona.getChildren().addAll(lblVarTitulo, lblVarHint, variantesLista, filaEntradaVar, lblVarError, lblStockTotal);

        // Fila 4: IMAGEN — FileChooser + preview de nombre seleccionado ────────
        HBox filaImagen = new HBox(12);
        filaImagen.setAlignment(Pos.CENTER_LEFT);
 
        Label lblImgTitulo = new Label("📷  Imagen del producto:");
        lblImgTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8;");
 
        Label lblImgSeleccionada = new Label("Sin imagen seleccionada");
        lblImgSeleccionada.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b6890; " +
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-padding: 6 14 6 14;");
        HBox.setHgrow(lblImgSeleccionada, Priority.ALWAYS);
 
        Button btnSeleccionarImg = new Button("🖼  Seleccionar imagen");
        btnSeleccionarImg.setStyle(
            "-fx-background-color: rgba(86,74,181,0.2); -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;");
        btnSeleccionarImg.setOnMouseEntered(e -> btnSeleccionarImg.setStyle(
            "-fx-background-color: rgba(86,74,181,0.35); -fx-background-radius: 8; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 8; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));
        btnSeleccionarImg.setOnMouseExited(e -> btnSeleccionarImg.setStyle(
            "-fx-background-color: rgba(86,74,181,0.2); -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;"));
 
        btnSeleccionarImg.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Seleccionar imagen del producto");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
            );
            Stage stage = (Stage) btnSeleccionarImg.getScene().getWindow();
            File archivo = chooser.showOpenDialog(stage);
            if (archivo != null) {
                // Copiar imagen a prodimg/ (junto al .jar o en el directorio de trabajo)
                String destPath = copiarImagenAProductImg(archivo);
                if (destPath != null) {
                    rutaImagenSeleccionada = destPath;
                    lblImgSeleccionada.setText("✓  " + archivo.getName());
                    lblImgSeleccionada.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981; " +
                        "-fx-background-color: rgba(16,185,129,0.1); -fx-background-radius: 8; " +
                        "-fx-padding: 6 14 6 14;");
                } else {
                    lblImgSeleccionada.setText("⚠  No se pudo copiar la imagen");
                    lblImgSeleccionada.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444; " +
                        "-fx-background-color: rgba(239,68,68,0.1); -fx-background-radius: 8; " +
                        "-fx-padding: 6 14 6 14;");
                }
            }
        });
 
        // Botón limpiar imagen
        Button btnLimpiarImg = new Button("✕");
        btnLimpiarImg.setStyle("-fx-background-color: rgba(239,68,68,0.12); -fx-background-radius: 6; " +
            "-fx-text-fill: #ef4444; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 6 10 6 10;");
        btnLimpiarImg.setOnAction(e -> {
            rutaImagenSeleccionada = null;
            lblImgSeleccionada.setText("Sin imagen seleccionada");
            lblImgSeleccionada.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b6890; " +
                "-fx-background-color: #272239; -fx-background-radius: 8; -fx-padding: 6 14 6 14;");
        });
 
        filaImagen.getChildren().addAll(lblImgTitulo, lblImgSeleccionada, btnSeleccionarImg, btnLimpiarImg);
 
        Label lblFeedbackCat = new Label("");
        lblFeedbackCat.setStyle("-fx-font-size: 12px;");
 
        Button btnAgregarProd = new Button("✓  Agregar producto");
        btnAgregarProd.setStyle("-fx-background-color: #564AB5; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; -fx-padding: 10 22 10 22;");
        btnAgregarProd.setOnMouseEntered(e -> btnAgregarProd.setStyle(
            "-fx-background-color: #6c5ce7; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; -fx-padding: 10 22 10 22;"));
        btnAgregarProd.setOnMouseExited(e -> btnAgregarProd.setStyle(
            "-fx-background-color: #564AB5; -fx-background-radius: 10; " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-cursor: hand; -fx-padding: 10 22 10 22;"));
        btnAgregarProd.setOnAction(e -> {
            String nom    = txtNomProd.getText().trim();
            String desc   = txtDescProd.getText().trim();
            String preStr = txtPrecioProd.getText().trim();
            String stStr  = txtStockProd.getText().trim();
            String cat    = cmbCat.getValue();
            // Stock global del campo principal es obligatorio solo si no hay variantes personalizadas
            boolean tieneVariantesPersonalizadas = !variantesTemp.isEmpty();
            if (nom.isEmpty() || desc.isEmpty() || preStr.isEmpty() || cat == null
                    || (!tieneVariantesPersonalizadas && stStr.isEmpty())) {
                lblFeedbackCat.setText("⚠  Completa todos los campos obligatorios.");
                lblFeedbackCat.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444;");
                return;
            }
            try {
                double precio = Double.parseDouble(preStr.replace(",", "").replace(".", "").trim());
                if (precio <= 0) throw new NumberFormatException();
                int idCat = switch (cat) {
                    case "Guitarras" -> 1; case "Teclados" -> 2; case "Percusión" -> 3;
                    case "Vientos" -> 4; default -> 5;
                };
                int nuevoId = CatalogoStore.siguienteId();
                Producto nuevo = new Producto(nuevoId, nom, desc, precio, 0, idCat);

                if (tieneVariantesPersonalizadas) {
                    // Usar las variantes definidas por el admin
                    int varIdx = 0;
                    for (String[] vd : variantesTemp) {
                        double pe  = Double.parseDouble(vd[1].replace(",","").replace(".",""));
                        int    vst = Integer.parseInt(vd[2]);
                        nuevo.agregarVariante(new Variante(nuevoId * 100 + varIdx, vd[0], pe, vst));
                        varIdx++;
                    }
                } else {
                    // Fallback: una sola variante "Estándar" — comportamiento original
                    int stock = Integer.parseInt(stStr);
                    if (stock < 0) throw new NumberFormatException();
                    nuevo.agregarVariante(new Variante(nuevoId * 100, "Estándar", 0, stock));
                }

                CatalogoStore.agregar(nuevo, rutaImagenSeleccionada);
                int varCount = nuevo.getVariantes().size();
                mostrarToast("✓ Producto \"" + nom + "\" agregado"
                    + (varCount > 1 ? " con " + varCount + " variantes." : " (variante estándar).")
                    + (rutaImagenSeleccionada != null ? " 📷" : ""));

                // Limpiar formulario completo
                txtNomProd.clear(); txtDescProd.clear();
                txtPrecioProd.clear(); txtStockProd.clear(); cmbCat.setValue(null);
                rutaImagenSeleccionada = null;
                variantesTemp.clear();
                variantesLista.getChildren().clear();
                lblFeedbackCat.setText("✓  Producto agregado exitosamente.");
                lblFeedbackCat.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");
                renderCatalogoAdmin();
            } catch (NumberFormatException ex) {
                lblFeedbackCat.setText("⚠  Precio y stock deben ser números válidos.");
                lblFeedbackCat.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444;");
            }
        });
 
        formCard.getChildren().addAll(formTitulo, sepForm, fila1, fila2, variantesZona, filaImagen,
                                       btnAgregarProd, lblFeedbackCat);
        contenedor.getChildren().add(formCard);
 
        // ── Inventario completo (base + admin) ────────────────────────────────
        javafx.scene.control.Separator sepInv = new javafx.scene.control.Separator();
        sepInv.setStyle("-fx-background-color: rgba(86,74,181,0.2);");
        contenedor.getChildren().add(sepInv);
 
        Label lblInventarioTitulo = new Label("📋  Inventario completo — todos los productos");
        lblInventarioTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        contenedor.getChildren().add(lblInventarioTitulo);
 
        Label lblLeyenda = new Label(
            "🔵 Producto base  ·  ✨ Agregado por admin  ·  🔴 Stock bajo (≤ 5 unidades)");
        lblLeyenda.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890; -fx-padding: 0 0 4 0;");
        contenedor.getChildren().add(lblLeyenda);
 
        java.util.List<Producto> todosProductos = new java.util.ArrayList<>(CatalogoStore.getTodos());
        if (todosProductos.isEmpty()) {
            Label vacio = new Label("No hay productos en el inventario.");
            vacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #8F8AA8; -fx-padding: 8 0 0 0;");
            contenedor.getChildren().add(vacio);
        } else {
            // Separar base de extras para el badge visual
            java.util.Set<Integer> idExtras = CatalogoStore.getExtras()
                .stream().map(Producto::getId)
                .collect(java.util.stream.Collectors.toSet());
            for (Producto prod : todosProductos) {
                boolean esExtra = idExtras.contains(prod.getId());
                contenedor.getChildren().add(crearFilaInventario(prod, esExtra, contenedor));
            }
        }
 
        // ── Separador ─────────────────────────────────────────────────────────
        //javafx.scene.control.Separator sepProveedores = new javafx.scene.control.Separator();
        //sepProveedores.setStyle("-fx-background-color: rgba(86,74,181,0.2); -fx-padding: 10 0 10 0;");
        //contenedor.getChildren().add(sepProveedores);
 
        // ── Catálogo de proveedores (con restock real) ────────────────────────
        //contenedor.getChildren().add(crearPanelProveedores());
    }
 
    
      private String copiarImagenAProductImg(File origen) {
        try {
            Path dirProdImg = Paths.get("prodimg");
            if (!Files.exists(dirProdImg)) {
                Files.createDirectories(dirProdImg);
            }
            // Nombre de destino: original con timestamp para evitar colisiones
            String ext  = origen.getName().contains(".")
                ? origen.getName().substring(origen.getName().lastIndexOf('.')) : ".png";
            String base = origen.getName().contains(".")
                ? origen.getName().substring(0, origen.getName().lastIndexOf('.')) : origen.getName();
            String nombreDest = base + "_" + System.currentTimeMillis() + ext;
            Path destino = dirProdImg.resolve(nombreDest);
            Files.copy(origen.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Admin] Imagen copiada → " + destino.toAbsolutePath());
            return destino.toAbsolutePath().toString();
        } catch (Exception ex) {
            System.err.println("⚠ [Admin] Error copiando imagen: " + ex.getMessage());
            return null;
        }
    }
 
    /**
     * Construye el panel de "Catálogo de Proveedores" (simulación).
     * Muestra productos disponibles de supuestos proveedores con precio mayorista
     * y la opción de "generar una orden de compra" (simulada, solo visual).
     */
    private VBox crearPanelProveedores() {
 
        // productId = ID del producto en CatalogoStore al que corresponde la orden.
        // 0 = sin producto equivalente en catálogo actual (orden solo referencial).
        record ItemProveedor(String proveedor, String producto, String categoria,
                             double precioMayorista, int minPedido, String pais, int productId) {}
 
        java.util.List<ItemProveedor> catalogo = java.util.List.of(
            new ItemProveedor("Fender Musical Instruments",
                "Stratocaster Player Series (lote)", "Guitarras", 1_650_000, 3, "🇺🇸 EE.UU.", 1),
            new ItemProveedor("Gibson Brands Inc.",
                "Les Paul Standard '50s (lote)", "Guitarras", 4_800_000, 2, "🇺🇸 EE.UU.", 2),
            new ItemProveedor("Yamaha Corporation",
                "P-125 Piano Digital (lote)", "Teclados", 1_120_000, 5, "🇯🇵 Japón", 5),
            new ItemProveedor("Roland Corporation",
                "FP-90X Stage Piano", "Teclados", 3_200_000, 2, "🇯🇵 Japón", 6),
            new ItemProveedor("Roland Corporation",
                "TD-17 Electronic Drum Kit", "Percusión", 2_450_000, 2, "🇯🇵 Japón", 8),
            new ItemProveedor("Yamaha Corporation",
                "DTX432K Batería Electrónica", "Percusión", 1_890_000, 3, "🇯🇵 Japón", 9),
            new ItemProveedor("Selmer Paris",
                "Serie III Alto Saxofón", "Vientos", 8_200_000, 1, "🇫🇷 Francia", 10),
            new ItemProveedor("Audio-Technica Corp.",
                "ATH-M50x Monitor Headphones (x10)", "Accesorios", 280_000, 10, "🇯🇵 Japón", 12),
            new ItemProveedor("Shure Incorporated",
                "SM58 Vocal Microphone (x5)", "Accesorios", 310_000, 5, "🇺🇸 EE.UU.", 13),
            new ItemProveedor("Ibanez (Hoshino Gakki)",
                "RG Series Starter Pack", "Guitarras", 780_000, 4, "🇯🇵 Japón", 3)
        );
 
        VBox panel = new VBox(16);
        panel.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14; " +
            "-fx-padding: 22 24 22 24;");
 
        // Encabezado del panel
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label tituloP = new Label("🚢  Catálogo de Proveedores  —  Reabastecimiento");
        tituloP.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(tituloP, Priority.ALWAYS);
        Label badgeSim = new Label("📋  Genera orden → actualiza stock real");
        badgeSim.setStyle(
            "-fx-font-size: 10px; -fx-text-fill: #10b981; " +
            "-fx-background-color: rgba(16,185,129,0.1); -fx-background-radius: 20; " +
            "-fx-padding: 3 12 3 12;");
        header.getChildren().addAll(tituloP, badgeSim);
 
        javafx.scene.control.Separator sepP = new javafx.scene.control.Separator();
        sepP.setStyle("-fx-background-color: rgba(86,74,181,0.15);");
 
        // Tabla de cabeceras
        HBox cabecera = new HBox(0);
        cabecera.setStyle("-fx-padding: 6 0 6 0;");
        String[] cols = {"Proveedor", "Producto", "Categ.", "P.Mayorista", "Min.Pedido", "Origen", ""};
        double[] anchos = {160, 0, 90, 100, 90, 90, 110}; // 0 = flexible
        for (int i = 0; i < cols.length; i++) {
            Label h = new Label(cols[i]);
            h.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #6b6890;");
            if (anchos[i] > 0) { h.setMinWidth(anchos[i]); h.setPrefWidth(anchos[i]); }
            else { HBox.setHgrow(h, Priority.ALWAYS); }
            cabecera.getChildren().add(h);
        }
 
        VBox filas = new VBox(6);
        for (ItemProveedor item : catalogo) {
            filas.getChildren().add(crearFilaProveedor(item.proveedor(), item.producto(),
                item.categoria(), item.precioMayorista(), item.minPedido(), item.pais(),
                item.productId()));
        }
 
        panel.getChildren().addAll(header, sepP, cabecera, filas);
        return panel;
    }
 
    private HBox crearFilaProveedor(String proveedor, String producto, String categoria,
                                     double precioMayorista, int minPedido, String pais,
                                     int productId) {
        HBox fila = new HBox(0);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.12); -fx-border-radius: 8; " +
            "-fx-padding: 10 14 10 14;");
 
        // Proveedor
        Label lblProv = new Label(proveedor);
        lblProv.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8; -fx-min-width: 160; -fx-pref-width: 160;");
 
        // Producto
        Label lblProd = new Label(producto);
        lblProd.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        lblProd.setWrapText(true);
        HBox.setHgrow(lblProd, Priority.ALWAYS);
 
        // Categoría
        String emoji = switch (categoria) {
            case "Guitarras" -> "🎸"; case "Teclados" -> "🎹";
            case "Percusión" -> "🥁"; case "Vientos" -> "🎺"; default -> "🎧";
        };
        Label lblCat = new Label(emoji + " " + categoria);
        lblCat.setStyle("-fx-font-size: 10px; -fx-text-fill: #A99CF0; " +
            "-fx-min-width: 90; -fx-pref-width: 90;");
 
        // Precio mayorista
        Label lblPrecio = new Label("$" + String.format("%,.0f", precioMayorista));
        lblPrecio.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #BC7F15; " +
            "-fx-min-width: 100; -fx-pref-width: 100;");
 
        // Pedido mínimo
        Label lblMin = new Label("≥ " + minPedido + " und.");
        lblMin.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8; " +
            "-fx-min-width: 90; -fx-pref-width: 90;");
 
        // Origen
        Label lblPais = new Label(pais);
        lblPais.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8; " +
            "-fx-min-width: 90; -fx-pref-width: 90;");
 
        // Botón "Generar orden" — aplica restock real si hay productId vinculado
        Button btnOrden = new Button("📦  Generar orden");
        btnOrden.setStyle(
            "-fx-background-color: rgba(188,127,21,0.15); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(188,127,21,0.35); -fx-border-radius: 7; " +
            "-fx-text-fill: #BC7F15; -fx-font-size: 11px; -fx-cursor: hand; " +
            "-fx-padding: 5 12 5 12; -fx-min-width: 110;");
        btnOrden.setOnMouseEntered(e -> btnOrden.setStyle(
            "-fx-background-color: rgba(188,127,21,0.30); -fx-background-radius: 7; " +
            "-fx-border-color: #BC7F15; -fx-border-radius: 7; " +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; " +
            "-fx-padding: 5 12 5 12; -fx-min-width: 110;"));
        btnOrden.setOnMouseExited(e -> btnOrden.setStyle(
            "-fx-background-color: rgba(188,127,21,0.15); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(188,127,21,0.35); -fx-border-radius: 7; " +
            "-fx-text-fill: #BC7F15; -fx-font-size: 11px; -fx-cursor: hand; " +
            "-fx-padding: 5 12 5 12; -fx-min-width: 110;"));
        btnOrden.setOnAction(e -> {
            if (productId > 0) {
                // Restock real: añade minPedido unidades al producto vinculado
                boolean ok = CatalogoStore.restockProducto(productId, minPedido);
                if (ok) {
                    mostrarToast("✅ Orden ejecutada: +" + minPedido
                        + " unidades → \"" + producto + "\"  |  Costo: $"
                        + String.format("%,.0f", precioMayorista * minPedido));
                    renderCatalogoAdmin(); // refresca inventario con nuevo stock
                } else {
                    mostrarToast("⚠ No se encontró el producto vinculado en el catálogo.");
                }
            } else {
                mostrarToast("📋 Orden registrada: " + minPedido + "x \"" + producto
                    + "\" → " + proveedor + "  |  Total: $"
                    + String.format("%,.0f", precioMayorista * minPedido));
            }
        });
 
        fila.getChildren().addAll(lblProv, lblProd, lblCat, lblPrecio, lblMin, lblPais, btnOrden);
 
        fila.setOnMouseEntered(ev -> fila.setStyle(
            "-fx-background-color: #2e2a44; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.25); -fx-border-radius: 8; " +
            "-fx-padding: 10 14 10 14;"));
        fila.setOnMouseExited(ev -> fila.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 8; " +
            "-fx-border-color: rgba(86,74,181,0.12); -fx-border-radius: 8; " +
            "-fx-padding: 10 14 10 14;"));
 
        return fila;
    }
    
    
    private HBox crearFilaProductoAdmin(Producto p) {
        HBox fila = new HBox(16);
        fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10; -fx-padding: 12 16 12 16;");
 
        Label emoji = new Label(switch (p.getIdCategoria()) {
            case 1 -> "🎸"; case 2 -> "🎹"; case 3 -> "🥁"; case 4 -> "🎺"; default -> "🎧";
        });
        emoji.setStyle("-fx-font-size: 22px;");
 
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label nom = new Label(p.getNombre());
        nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label desc = new Label(p.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nom, desc);
 
        int stock = p.getVariantes().isEmpty() ? 0 : p.getVariantes().get(0).getStock();
        Label lblStock = new Label("Stock: " + stock);
        lblStock.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (stock <= 6 ? "#ef4444" : "#10b981") + ";");
 
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        Label idLbl = new Label("ID #" + p.getId());
        idLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        fila.getChildren().addAll(emoji, info, lblStock, precio, idLbl);
        return fila;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  INVENTARIO — Fila con restock (sustituye crearFilaProductoAdmin en UI)
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Crea una fila de inventario para un producto (base o extra).
     * Incluye badge de origen, stock en tiempo real y botón "Reabastecer".
     *
     * @param p          producto a mostrar
     * @param esExtra    true si fue añadido por el admin (extras), false si es base
     * @param contenedor VBox padre (para refresh parcial tras restock)
     */
private VBox crearFilaInventario(Producto p, boolean esExtra, VBox contenedor) {
        VBox wrapper = new VBox(0);
 
        HBox fila = new HBox(14);
        fila.setAlignment(Pos.CENTER_LEFT);
        int stock = p.getVariantes().isEmpty() ? 0 : p.getVariantes().get(0).getStock();
        boolean stockBajo = stock > 0 && stock <= 5;
        boolean sinStock  = stock <= 0;
 
        String borderColor = sinStock  ? "rgba(239,68,68,0.45)"
                           : stockBajo ? "rgba(251,191,36,0.4)"
                           : "rgba(86,74,181,0.18)";
        fila.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: " + borderColor + "; -fx-border-radius: 10; -fx-padding: 12 16 12 16;");
 
        // ── Imagen / Emoji categoría (Reemplazo aplicado) ──────────────
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(42, 42);
        imgBox.setMinSize(42, 42);
        imgBox.setMaxSize(42, 42);
        imgBox.setStyle("-fx-background-color: #13111e; -fx-background-radius: 8;");

        javafx.scene.image.ImageView iv = cargarImagenProducto(p, 42, 42);
        if (iv != null) {
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(42, 42);
            clip.setArcWidth(12); 
            clip.setArcHeight(12);
            imgBox.setClip(clip);
            imgBox.getChildren().add(iv);
        } else {
            // Fallback al emoji si no hay imagen
            Label emojiLabel = new Label(switch (p.getIdCategoria()) {
                case 1 -> "🎸"; case 2 -> "🎹"; case 3 -> "🥁"; case 4 -> "🎺"; default -> "🎧";
            });
            emojiLabel.setStyle("-fx-font-size: 20px;");
            imgBox.getChildren().add(emojiLabel);
        }
 
        // Info + badge origen
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);
        HBox nombreFila = new HBox(8);
        nombreFila.setAlignment(Pos.CENTER_LEFT);
        Label nom = new Label(p.getNombre());
        nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label badgeOrigen = new Label(esExtra ? "✨ Admin" : "🔵 Base");
        badgeOrigen.setStyle("-fx-font-size: 9px; -fx-padding: 2 7 2 7; " +
            "-fx-background-radius: 10; -fx-border-radius: 10; " +
            (esExtra
                ? "-fx-background-color: rgba(16,185,129,0.12); -fx-text-fill: #10b981; -fx-border-color: rgba(16,185,129,0.3);"
                : "-fx-background-color: rgba(86,74,181,0.12); -fx-text-fill: #A99CF0; -fx-border-color: rgba(86,74,181,0.3);"));
        nombreFila.getChildren().addAll(nom, badgeOrigen);
        Label desc = new Label(p.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nombreFila, desc);
 
        // Stock badge
        String stockColor = sinStock ? "#ef4444" : stockBajo ? "#f59e0b" : "#10b981";
        String stockIcono = sinStock ? "✗" : stockBajo ? "⚠" : "✓";
        Label lblStock = new Label(stockIcono + " Stock: " + stock);
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + stockColor + ";" +
            "-fx-background-color: " + stockColor + "22; -fx-background-radius: 6; -fx-padding: 3 10 3 10;");
        lblStock.setMinWidth(100);
 
        // Variantes count
        Label lblVars = new Label(p.getVariantes().size() + " var.");
        lblVars.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        // Precio
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        // ID
        Label idLbl = new Label("#" + p.getId());
        idLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890; -fx-min-width: 36;");
 
        // ── Zona de restock (TextField oculto + botón confirmar) ──────────────
        javafx.scene.control.TextField txtCantidad = new javafx.scene.control.TextField();
        txtCantidad.setPromptText("Unidades");
        txtCantidad.setPrefWidth(80); txtCantidad.setMaxWidth(80);
        txtCantidad.setStyle("-fx-background-color: #272239; -fx-background-radius: 6; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 6; " +
            "-fx-text-fill: #E9E9ED; -fx-prompt-text-fill: #6b6890; -fx-font-size: 12px;");
        txtCantidad.setVisible(false); txtCantidad.setManaged(false);
 
        Button btnConfirmarRestock = new Button("✓");
        btnConfirmarRestock.setStyle("-fx-background-color: #10b981; -fx-background-radius: 6; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 5 10 5 10;");
        btnConfirmarRestock.setVisible(false); btnConfirmarRestock.setManaged(false);
 
        Button btnRestock = new Button("↑ Reabastecer");
        btnRestock.setStyle("-fx-background-color: rgba(16,185,129,0.14); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(16,185,129,0.4); -fx-border-radius: 7; " +
            "-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 12 5 12;");
        btnRestock.setOnMouseEntered(e -> btnRestock.setStyle(
            "-fx-background-color: rgba(16,185,129,0.28); -fx-background-radius: 7; " +
            "-fx-border-color: #10b981; -fx-border-radius: 7; " +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 12 5 12;"));
        btnRestock.setOnMouseExited(e -> btnRestock.setStyle(
            "-fx-background-color: rgba(16,185,129,0.14); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(16,185,129,0.4); -fx-border-radius: 7; " +
            "-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 12 5 12;"));
 
        // Toggle panel de entrada al hacer clic en "Reabastecer"
        btnRestock.setOnAction(e -> {
            boolean visible = txtCantidad.isVisible();
            txtCantidad.setVisible(!visible);    txtCantidad.setManaged(!visible);
            btnConfirmarRestock.setVisible(!visible); btnConfirmarRestock.setManaged(!visible);
            if (!visible) { txtCantidad.clear(); txtCantidad.requestFocus(); }
        });
 
        // Confirmar restock
        btnConfirmarRestock.setOnAction(e -> {
            String txt = txtCantidad.getText().trim();
            try {
                int n = Integer.parseInt(txt);
                if (n <= 0) throw new NumberFormatException();
                boolean ok = CatalogoStore.restockProducto(p.getId(), n);
                if (ok) {
                    CatalogoStore.guardar(); 
                    StockStore.guardarStock();
                    mostrarToast("✓ Reabastecido: +" + n + " unidades → \"" + p.getNombre() + "\"");
                    renderCatalogoAdmin(); // refresh completo para que stock se actualice
                    actualizarOverview();
                } else {
                    mostrarToast("⚠ No se pudo reabastecer el producto #" + p.getId());
                }
            } catch (NumberFormatException ex) {
                mostrarToast("⚠ Ingresa un número entero positivo.");
            }
        });
 
        // ── Botón Eliminar producto ───────────────────────────────────────────
        Button btnEliminar = new Button("🗑");
        btnEliminar.setTooltip(new javafx.scene.control.Tooltip("Eliminar producto"));
        btnEliminar.setStyle(
            "-fx-background-color: rgba(239,68,68,0.10); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(239,68,68,0.30); -fx-border-radius: 7; " +
            "-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-cursor: hand; " +
            "-fx-padding: 5 10 5 10;");
        btnEliminar.setOnMouseEntered(ev -> btnEliminar.setStyle(
            "-fx-background-color: rgba(239,68,68,0.26); -fx-background-radius: 7; " +
            "-fx-border-color: #ef4444; -fx-border-radius: 7; " +
            "-fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; " +
            "-fx-padding: 5 10 5 10;"));
        btnEliminar.setOnMouseExited(ev -> btnEliminar.setStyle(
            "-fx-background-color: rgba(239,68,68,0.10); -fx-background-radius: 7; " +
            "-fx-border-color: rgba(239,68,68,0.30); -fx-border-radius: 7; " +
            "-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-cursor: hand; " +
            "-fx-padding: 5 10 5 10;"));

        btnEliminar.setOnAction(ev -> handleEliminarProducto(p, esExtra));

        // ── Agregando imgBox al HBox principal en lugar de emoji ──────────────
        fila.getChildren().addAll(imgBox, info, lblVars, lblStock, precio, idLbl,
                                  txtCantidad, btnConfirmarRestock, btnRestock, btnEliminar);
        wrapper.getChildren().add(fila);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ELIMINAR PRODUCTO — flujo completo con confirmación
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Gestiona el flujo completo de eliminación de un producto:
     *  1. Muestra un Alert de confirmación con advertencia según tipo (base/extra).
     *  2. Si el usuario confirma, llama a {@link CatalogoStore#eliminar(int)}.
     *  3. Refresca el inventario y el overview.
     *  4. Muestra un toast con el resultado.
     *
     * @param p       Producto a eliminar
     * @param esExtra {@code true} si es un producto creado por el admin
     */
    private void handleEliminarProducto(Producto p, boolean esExtra) {
        // ── Construir diálogo de confirmación ─────────────────────────────────
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar producto");
        alerta.setHeaderText("¿Eliminar \"" + p.getNombre() + "\" (#" + p.getId() + ")?");

        String advertencia = esExtra
            ? "Este producto fue agregado por el administrador.\n" +
              "Se eliminará del catálogo y del archivo en disco.\n" +
              "Esta acción no se puede deshacer."
            : "⚠ Este es un producto BASE del catálogo original.\n" +
              "Se eliminará solo en memoria durante esta sesión.\n" +
              "Al reiniciar la aplicación volverá a aparecer.";

        alerta.setContentText(advertencia);

        // Personalizar los botones del diálogo
        ButtonType btnConfirmar = new ButtonType("Eliminar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar  = new ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnConfirmar, btnCancelar);

        // Estilizar el diálogo para que combine con el tema oscuro de la app
        alerta.getDialogPane().setStyle(
            "-fx-background-color: #1E1A2E; -fx-text-fill: #E9E9ED;");

        // Aplicar color rojo al botón "Eliminar"
        alerta.getDialogPane().lookupButton(btnConfirmar).setStyle(
            "-fx-background-color: #ef4444; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

        // ── Procesar la respuesta del usuario ─────────────────────────────────
        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnConfirmar) {
                boolean ok = CatalogoStore.eliminar(p.getId());
                if (ok) {
                    String tipo = esExtra ? "extra" : "base (sesión)";
                    mostrarToast("🗑 Producto \"" + p.getNombre() + "\" eliminado del catálogo [" + tipo + "].");
                    renderCatalogoAdmin();   // refresca el inventario completo
                    actualizarOverview();    // actualiza stat-cards y alertas de stock
                } else {
                    mostrarToast("⚠ No se pudo eliminar el producto #" + p.getId() + ". Intenta de nuevo.");
                }
            }
        });
    }
 
    @FXML private void navegarUsuariosAdmin() {
        renderUsuariosAdmin();
        mostrarSeccion(paneUsuariosAdmin, navBtnUsuarios);
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  CATÁLOGO DE USUARIOS
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Rellena paneUsuariosAdmin con una tarjeta por cada usuario registrado.
     * Muestra: avatar inicial, nombre, email, rol y tipo de perfil.
     * El admin también puede ver cuántos pedidos tiene cada usuario.
     */
    private void renderUsuariosAdmin() {
        // Obtener el ScrollPane → su contenido es un VBox (definido en FXML)
        // Si el FXML ya tiene un VBox dentro del ScrollPane, lo buscamos;
        // de lo contrario lo creamos y lo asignamos como contenido.
        VBox contenedor;
        if (paneUsuariosAdmin.getContent() instanceof VBox vb) {
            contenedor = vb;
        } else {
            contenedor = new VBox(14);
            contenedor.setStyle("-fx-padding: 24;");
            paneUsuariosAdmin.setContent(contenedor);
            paneUsuariosAdmin.setFitToWidth(true);
        }
        contenedor.getChildren().clear();
 
        java.util.List<Usuario> usuarios = UserStore.getTodos();
 
        // ── Encabezado ────────────────────────────────────────────────────────
        HBox encabezado = new HBox(12);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.setStyle("-fx-padding: 0 0 8 0;");
 
        Label titulo = new Label("👥  Usuarios registrados");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(titulo, Priority.ALWAYS);
 
        Label contador = new Label(usuarios.size() + " usuario" + (usuarios.size() != 1 ? "s" : ""));
        contador.setStyle(
            "-fx-font-size: 12px; -fx-text-fill: #A99CF0; " +
            "-fx-background-color: rgba(86,74,181,0.18); -fx-background-radius: 20; " +
            "-fx-padding: 4 12 4 12;");
 
        encabezado.getChildren().addAll(titulo, contador);
        contenedor.getChildren().add(encabezado);
 
        if (usuarios.isEmpty()) {
            Label vacio = new Label("No hay usuarios registrados.");
            vacio.setStyle("-fx-font-size: 13px; -fx-text-fill: #8F8AA8; -fx-padding: 20 0 0 0;");
            contenedor.getChildren().add(vacio);
            return;
        }
 
        for (Usuario u : usuarios) {
            contenedor.getChildren().add(crearTarjetaUsuario(u));
        }
    }
 
    /** Construye la tarjeta visual de un usuario para el panel de administración. */
    private HBox crearTarjetaUsuario(Usuario u) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14; " +
            "-fx-padding: 16 20 16 20; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,3);");
 
        // ── Avatar con inicial ──────────────────────────────────────────────────
        javafx.scene.shape.Circle avatarBg = new javafx.scene.shape.Circle(22);
        boolean esAdmin = u.getRol() == Rol.ADMIN;
        avatarBg.setFill(javafx.scene.paint.Color.web(
            esAdmin ? "rgba(188,127,21,0.25)" : "rgba(86,74,181,0.25)"));
        avatarBg.setStroke(javafx.scene.paint.Color.web(
            esAdmin ? "#BC7F15" : "#564AB5"));
        avatarBg.setStrokeWidth(1.5);
 
        String inicial = (u.getNombre() != null && !u.getNombre().isBlank())
            ? String.valueOf(u.getNombre().trim().toUpperCase().charAt(0)) : "?";
        Label lblInicial = new Label(inicial);
        lblInicial.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; " +
            "-fx-text-fill: " + (esAdmin ? "#BC7F15" : "#A99CF0") + ";");
 
        javafx.scene.layout.StackPane avatar = new javafx.scene.layout.StackPane(avatarBg, lblInicial);
        avatar.setMinSize(44, 44); avatar.setPrefSize(44, 44); avatar.setMaxSize(44, 44);
 
        // ── Info principal ────────────────────────────────────────────────────
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
 
        Label lblNombre = new Label(u.getNombre());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
 
        Label lblEmail = new Label("✉  " + u.getEmail());
        lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8;");
 
        HBox chips = new HBox(8);
        chips.setAlignment(Pos.CENTER_LEFT);
 
        // Badge rol
        Label rolBadge = new Label(esAdmin ? "⭐  ADMIN" : "🛒  COMPRADOR");
        rolBadge.setStyle(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 10 3 10; " +
            "-fx-background-radius: 20; -fx-border-radius: 20; " +
            (esAdmin
                ? "-fx-background-color: rgba(188,127,21,0.15); -fx-text-fill: #BC7F15; -fx-border-color: rgba(188,127,21,0.4);"
                : "-fx-background-color: rgba(86,74,181,0.15); -fx-text-fill: #A99CF0; -fx-border-color: rgba(86,74,181,0.4);"));
 
        // Badge tipo perfil
        PerfilUsuario perfil = u.getPerfil();
        if (perfil != null) {
            Label tipoBadge = new Label("🎵  " + perfil.getTipo().name());
            tipoBadge.setStyle(
                "-fx-font-size: 10px; -fx-padding: 3 10 3 10; " +
                "-fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-background-color: rgba(16,185,129,0.1); " +
                "-fx-text-fill: #10b981; -fx-border-color: rgba(16,185,129,0.3);");
            chips.getChildren().addAll(rolBadge, tipoBadge);
 
            // Instrumento
            String instr = perfil.getInstrumento();
            if (instr != null && !instr.isBlank()) {
                Label instrLbl = new Label("🎸  " + instr);
                instrLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
                chips.getChildren().add(instrLbl);
            }
        } else {
            chips.getChildren().add(rolBadge);
        }
 
        info.getChildren().addAll(lblNombre, lblEmail, chips);
 
        // ── Estadísticas de pedidos ───────────────────────────────────────────
        long nPedidos = gestor.getTodos().stream()
            .filter(p -> p.getIdUsuario() == u.getId()).count();
        double totalGasto = gestor.getTodos().stream()
            .filter(p -> p.getIdUsuario() == u.getId())
            .mapToDouble(p -> p.getTotal()).sum();
 
        VBox stats = new VBox(4);
        stats.setAlignment(Pos.CENTER_RIGHT);
        stats.setStyle("-fx-min-width: 120;");
 
        Label lblPedidos = new Label(nPedidos + " pedido" + (nPedidos != 1 ? "s" : ""));
        lblPedidos.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #A99CF0;");
 
        Label lblGasto = new Label(String.format("$%,.0f", totalGasto));
        lblGasto.setStyle("-fx-font-size: 12px; -fx-text-fill: #BC7F15;");
 
        Label lblId = new Label("ID: " + u.getId());
        lblId.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        stats.getChildren().addAll(lblPedidos, lblGasto, lblId);
 
        card.getChildren().addAll(avatar, info, stats);
 
        // Hover
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #252540; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.45); -fx-border-radius: 14; " +
            "-fx-padding: 16 20 16 20; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.2),14,0,0,5);"));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14; " +
            "-fx-padding: 16 20 16 20; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,3);"));
 
        return card;
    }
 
    @FXML
    private void navegarPerfilAdmin() {
        actualizarPerfilResumen();
        mostrarSeccion(panePerfilAdmin, navBtnPerfil);
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  OVERVIEW  —  stat-cards, desglose, cola, recientes
    // ══════════════════════════════════════════════════════════════════════════
 
    private void actualizarOverview() {
      int    total      = gestor.getTotalPedidos();
        int    enCola     = gestor.getCantidadEnCola();
        double ingresos   = gestor.getIngresoTotal();
        long   entregados = gestor.getTotalEntregados();
 
        lblTotalPedidos.setText(String.valueOf(total));
        lblTotalPedidosHint.setText(total == 1 ? "pedido registrado" : "pedidos registrados");
 
        lblEnCola.setText(String.valueOf(enCola));
        lblEnColaHint.setText(enCola == 0 ? "cola vacía"
            : enCola == 1 ? "1 pendiente de procesar"
            : enCola + " pendientes de procesar");
 
        lblIngresos.setText(String.format("$%,.0f", ingresos));
        lblIngresosHint.setText("suma de todos los pedidos");
 
        lblEntregados.setText(String.valueOf(entregados));
        lblEntregadosHint.setText(entregados == 1 ? "pedido completado" : "pedidos completados");
 
        renderDesgloseEstados();
        renderColaPendientes();
        renderPedidosRecientes();
        renderStockAlerta();       // ← LÍNEA NUEVA
    }
 
    @FXML
    private void handleRefrescar() {
        gestor.recargarDatos(); 
        actualizarOverview();
        renderPedidosAdmin();
        actualizarPerfilResumen();
        mostrarToast("✓ Datos actualizados desde el archivo");
    }
 
    // ── Desglose de ingresos por estado (se añade al inicio de recentContainer) ─
    private void renderDesgloseEstados() {
        // Reutilizamos recentContainer — se limpia también en renderPedidosRecientes
        // Para separar, usamos un VBox independiente que insertamos antes del título
        // No necesita FXML nuevo: construimos todo programáticamente.
    }
 
    // ── Cola FIFO de pendientes ────────────────────────────────────────────────
    private void renderColaPendientes() {
        colaContainer.getChildren().clear();
        ArrayList<Pedido> pendientes = gestor.getPorEstado(EstadoPedido.PENDIENTE);
 
        if (lblColaBadge != null) lblColaBadge.setText(String.valueOf(pendientes.size()));
 
        if (pendientes.isEmpty()) {
            Label vacio = new Label("✓  Cola vacía — sin pendientes");
            vacio.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981; " +
                           "-fx-padding: 8 0 0 0;");
            colaContainer.getChildren().add(vacio);
            return;
        }
 
        for (Pedido p : pendientes) {
            colaContainer.getChildren().add(crearFilaCola(p));
        }
    }
 
    private HBox crearFilaCola(Pedido p) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: rgba(188,127,21,0.08); " +
                      "-fx-background-radius: 10; -fx-border-color: rgba(188,127,21,0.2); " +
                      "-fx-border-radius: 10; -fx-padding: 10 14 10 14;");
 
        Label pos  = new Label("#" + p.getId());
        pos.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED; -fx-min-width: 45;");
 
        String nomCola = UserStore.getTodos().stream()
            .filter(u -> u.getId() == p.getIdUsuario())
            .map(Usuario::getNombre)
            .findFirst().orElse("#" + p.getIdUsuario());
        Label usr  = new Label("👤 " + nomCola);
        usr.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        HBox.setHgrow(usr, Priority.ALWAYS);
 
        Label tot  = new Label(String.format("$%,.0f", p.getTotal()));
        tot.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        Label fecha = new Label(p.getFechaCreacion().format(FMT_FECHA));
        fecha.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        // Botón rápido: procesar este pedido directamente
        Button btnProcesar = new Button("▶");
        btnProcesar.setStyle("-fx-background-color: rgba(86,74,181,0.25); " +
                             "-fx-background-radius: 6; -fx-text-fill: #A99CF0; " +
                             "-fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        btnProcesar.setOnAction(e -> {
            boolean ok = gestor.avanzarEstado(p.getId(), EstadoPedido.EN_PROCESO);
            if (ok) {
                mostrarToast("⚡ Pedido #" + p.getId() + " → EN PROCESO");
                actualizarOverview();
                renderPedidosAdmin();
            }
        });
 
        fila.getChildren().addAll(pos, usr, tot, fecha, btnProcesar);
        return fila;
    }
 
      private VBox stockAlertaContainer = null;
    
    private void renderStockAlerta() {
        // Obtener el VBox raíz del overview (contenido del ScrollPane)
        if (!(paneOverview.getContent() instanceof VBox rootVBox)) return;
 
        // Remover panel anterior si existe
        if (stockAlertaContainer != null) {
            rootVBox.getChildren().remove(stockAlertaContainer);
        }
 
        java.util.List<StockStore.ProductoStock> bajoStock = StockStore.getBajoStock();
 
        if (bajoStock.isEmpty()) {
            // Mostrar panel de "todo OK" si había alertas antes
            stockAlertaContainer = null;
            return;
        }
 
        // ── Construir panel ──────────────────────────────────────────────────
        VBox panel = new VBox(14);
        panel.setStyle(
            "-fx-background-color: rgba(239,68,68,0.07); " +
            "-fx-background-radius: 14; " +
            "-fx-border-color: rgba(239,68,68,0.3); -fx-border-radius: 14; " +
            "-fx-padding: 18 20 18 20; " +
            "-fx-effect: dropshadow(gaussian,rgba(239,68,68,0.08),8,0,0,2);");
 
        // Encabezado
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titulo = new Label("⚠  Stock bajo — " + bajoStock.size()
            + " producto" + (bajoStock.size() != 1 ? "s" : "") + " por agotar");
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
        HBox.setHgrow(titulo, Priority.ALWAYS);
 
        Label umbralLbl = new Label("Umbral: ≤ " + StockStore.UMBRAL + " und.");
        umbralLbl.setStyle(
            "-fx-font-size: 11px; -fx-text-fill: #ef4444; -fx-opacity: 0.7; " +
            "-fx-background-color: rgba(239,68,68,0.12); -fx-background-radius: 20; " +
            "-fx-padding: 3 10 3 10;");
        header.getChildren().addAll(titulo, umbralLbl);
 
        // Separador
        javafx.scene.control.Separator sep =
            new javafx.scene.control.Separator();
        sep.setStyle("-fx-background-color: rgba(239,68,68,0.2);");
 
        // Filas de productos
        VBox filas = new VBox(8);
        for (StockStore.ProductoStock ps : bajoStock) {
            filas.getChildren().add(crearFilaStockAlerta(ps));
        }
 
        // Botón ir a catálogo
        Button btnIr = new Button("📦  Gestionar catálogo →");
        btnIr.setStyle(
            "-fx-background-color: rgba(239,68,68,0.15); -fx-background-radius: 8; " +
            "-fx-border-color: rgba(239,68,68,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-cursor: hand; " +
            "-fx-padding: 8 16 8 16;");
        btnIr.setOnMouseEntered(e -> btnIr.setStyle(
            "-fx-background-color: rgba(239,68,68,0.25); -fx-background-radius: 8; " +
            "-fx-border-color: #ef4444; -fx-border-radius: 8; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; " +
            "-fx-padding: 8 16 8 16;"));
        btnIr.setOnMouseExited(e -> btnIr.setStyle(
            "-fx-background-color: rgba(239,68,68,0.15); -fx-background-radius: 8; " +
            "-fx-border-color: rgba(239,68,68,0.4); -fx-border-radius: 8; " +
            "-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-cursor: hand; " +
            "-fx-padding: 8 16 8 16;"));
        btnIr.setOnAction(e -> navegarCatalogoAdmin());
 
        panel.getChildren().addAll(header, sep, filas, btnIr);
        stockAlertaContainer = panel;
        rootVBox.getChildren().add(panel);
    }
 
    private HBox crearFilaStockAlerta(StockStore.ProductoStock ps) {
        HBox fila = new HBox(12);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle(
            "-fx-background-color: rgba(239,68,68,0.05); -fx-background-radius: 8; " +
            "-fx-border-color: rgba(239,68,68,0.15); -fx-border-radius: 8; " +
            "-fx-padding: 8 12 8 12;");
 
        // Emoji categoría
        Label emoji = new Label(switch (ps.idCategoria) {
            case 1 -> "🎸"; case 2 -> "🎹"; case 3 -> "🥁"; case 4 -> "🎺"; default -> "🎧";
        });
        emoji.setStyle("-fx-font-size: 18px;");
 
        // Nombre
        Label nombre = new Label(ps.nombre);
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(nombre, Priority.ALWAYS);
 
        // Indicador de stock con barra de calor
        String color  = ps.stock == 0 ? "#ef4444" : ps.stock <= 2 ? "#f97316" : "#f59e0b";
        String bg     = ps.stock == 0 ? "rgba(239,68,68,0.18)"
                      : ps.stock <= 2 ? "rgba(249,115,22,0.18)"
                      : "rgba(245,158,11,0.18)";
        Label stockLbl = new Label(ps.stock == 0 ? "⛔  SIN STOCK" : "⚠  " + ps.stock + " und.");
        stockLbl.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + "; " +
            "-fx-background-color: " + bg + "; -fx-background-radius: 20; " +
            "-fx-padding: 3 12 3 12;");
 
        // ID del producto
        Label idLbl = new Label("ID #" + ps.id);
        idLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        fila.getChildren().addAll(emoji, nombre, idLbl, stockLbl);
        return fila;
    }
    
    // ── Pedidos recientes (últimos 5) ─────────────────────────────────────────
    private void renderPedidosRecientes() {
        recentContainer.getChildren().clear();
 
        // ── Desglose por estado (minimétricas) ────────────────────────────
        HBox desglose = crearDesgloseEstados();
        recentContainer.getChildren().add(desglose);
 
        // ── Últimos pedidos ───────────────────────────────────────────────
        ArrayList<Pedido> recientes = gestor.getPedidosMasRecientes(5);
        if (recientes.isEmpty()) {
            Label lbl = new Label("Sin pedidos registrados aún.");
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8; -fx-padding: 12 0 0 0;");
            recentContainer.getChildren().add(lbl);
            return;
        }
 
        for (Pedido p : recientes) {
            recentContainer.getChildren().add(crearFilaReciente(p));
        }
    }
 
    /** Mini-barra con conteo de pedidos por estado. */
    private HBox crearDesgloseEstados() {
        HBox barra = new HBox(8);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle("-fx-padding: 0 0 14 0;");
 
        Map<EstadoPedido, Long> conteos = gestor.getConteosPorEstado();
        Map<EstadoPedido, Double> ingresos = gestor.getIngresosPorEstado();
 
        for (EstadoPedido e : EstadoPedido.values()) {
            long   cant  = conteos.getOrDefault(e, 0L);
            double total = ingresos.getOrDefault(e, 0.0);
 
            VBox chip = new VBox(2);
            chip.setAlignment(Pos.CENTER);
            chip.setStyle("-fx-background-color: " + colorFondoEstado(e) + "; " +
                          "-fx-background-radius: 10; -fx-padding: 7 12 7 12;");
 
            Label lblCant = new Label(iconoEstado(e) + "  " + cant);
            lblCant.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                             "-fx-text-fill: " + colorTextoEstado(e) + ";");
 
            Label lblNom = new Label(etiquetaEstado(e));
            lblNom.setStyle("-fx-font-size: 9px; -fx-text-fill: " + colorTextoEstado(e) + "; " +
                            "-fx-opacity: 0.8;");
 
            Label lblTotal = new Label(String.format("$%,.0f", total));
            lblTotal.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTextoEstado(e) + "; " +
                              "-fx-opacity: 0.7;");
 
            chip.getChildren().addAll(lblCant, lblNom, lblTotal);
            HBox.setHgrow(chip, Priority.ALWAYS);
            barra.getChildren().add(chip);
        }
        return barra;
    }
 
    private HBox crearFilaReciente(Pedido p) {
        HBox fila = new HBox(12);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
                      "-fx-border-color: rgba(86,74,181,0.15); -fx-border-radius: 10; " +
                      "-fx-padding: 12 16 12 16;");
 
        Label badgeEstado = new Label(textoEstado(p.getEstado()));
        badgeEstado.setStyle("-fx-background-color: " + colorFondoEstado(p.getEstado()) + "; " +
                             "-fx-background-radius: 8; " +
                             "-fx-text-fill: " + colorTextoEstado(p.getEstado()) + "; " +
                             "-fx-font-size: 10px; -fx-font-weight: bold; " +
                             "-fx-padding: 3 10 3 10; -fx-min-width: 82;");
 
        Label id = new Label("Pedido #" + p.getId());
        id.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        HBox.setHgrow(id, Priority.ALWAYS);
 
        String nomReciente = UserStore.getTodos().stream()
            .filter(u -> u.getId() == p.getIdUsuario())
            .map(Usuario::getNombre)
            .findFirst().orElse("#" + p.getIdUsuario());
        Label usuario = new Label("👤 " + nomReciente + " (#" + p.getIdUsuario() + ")");
        usuario.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
 
        Label total = new Label(String.format("$%,.0f", p.getTotal()));
        total.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        Label fecha = new Label(p.getFechaCreacion().format(FMT_FECHA));
        fecha.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
 
        fila.getChildren().addAll(badgeEstado, id, usuario, total, fecha);
        return fila;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  PROCESAR SIGUIENTE (FIFO)
    // ══════════════════════════════════════════════════════════════════════════
 
    @FXML
    private void handleProcesarSiguiente() {
       Pedido procesado = gestor.procesarSiguiente();
        if (procesado == null) {
            mostrarToast("⚠ La cola de pendientes está vacía.");
        } else {
            mostrarToast("⚡ Pedido #" + procesado.getId() + " movido a EN_PROCESO y guardado.");
        }
        
        // Sincronizamos todas las vistas después de procesar
        actualizarOverview();
        renderPedidosAdmin();
        actualizarPerfilResumen();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  EXPORTAR REPORTE  (persiste en archivo de texto legible)
    // ══════════════════════════════════════════════════════════════════════════
 
    /**
     * Genera "reporte_pedidos.txt" en el directorio de la app.
     * Vincular con un Button onAction="#handleExportarReporte" en el FXML.
     */
    @FXML
    private void handleExportarReporte() {
        String archivo = "reporte_pedidos_" + java.time.LocalDate.now() + ".txt";
        gestor.exportarReporte(archivo);
        mostrarToast("📄 Reporte exportado → " + archivo);
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  GESTIÓN DE PEDIDOS — FILTROS, BÚSQUEDA Y RENDER
    // ══════════════════════════════════════════════════════════════════════════
 
    @FXML
    private void filtrarPedidosAdmin(javafx.event.ActionEvent event) {
        Button origen = (Button) event.getSource();
 
        // Se lee el valor semántico fijo asignado via userData en el FXML,
        // en lugar del texto visible que puede cambiar con la animación del sidebar.
        Object ud = origen.getUserData();
        if (ud != null) {
            filtroAdmin = ud.toString();
        } else {
            // Fallback defensivo: inferir desde el texto si userData no está asignado
            String texto = origen.getText().trim();
            if      (texto.equals("Todos"))         filtroAdmin = "Todos";
            else if (texto.contains("Pendiente"))   filtroAdmin = "PENDIENTE";
            else if (texto.contains("En Proceso"))  filtroAdmin = "EN_PROCESO";
            else if (texto.contains("Enviado"))     filtroAdmin = "ENVIADO";
            else if (texto.contains("Entregado"))   filtroAdmin = "ENTREGADO";
        }
 
        actualizarTabsEstilo(origen);
        renderPedidosAdmin();
    }
 
    /**
     * Búsqueda enriquecida en tiempo real.
     * Ahora busca en: ID, ID usuario, dirección y fecha.
     */
    @FXML
    private void handleBuscarPedido() {
        busquedaAdmin = txtBuscarPedido.getText().trim().toLowerCase();
        renderPedidosAdmin();
    }
 
    @FXML
    private void irAPedidosPendientes() {
        filtroAdmin   = "PENDIENTE";
        busquedaAdmin = "";
        if (txtBuscarPedido != null) txtBuscarPedido.clear();
        actualizarTabsEstilo(tabAdminPendiente);
        renderPedidosAdmin();
        mostrarSeccion(panePedidosAdmin, navBtnPedidos);
    }
 
    // ── Construye la lista filtrada combinando búsqueda + tab de estado ────────
    private ArrayList<Pedido> obtenerPedidosFiltrados() {
        ArrayList<Pedido> fuente;
 
        if (!busquedaAdmin.isEmpty()) {
            // Búsqueda enriquecida sobre el gestor (ID, usuario, dirección, fecha)
            fuente = gestor.buscarPorTexto(busquedaAdmin);
            // Aplicar filtro de estado encima
            if (!filtroAdmin.equals("Todos")) {
                EstadoPedido estadoFiltro = EstadoPedido.valueOf(filtroAdmin);
                fuente.removeIf(p -> p.getEstado() != estadoFiltro);
            }
        } else if (filtroAdmin.equals("Todos")) {
            fuente = gestor.getTodos();
        } else {
            fuente = gestor.getPorEstado(EstadoPedido.valueOf(filtroAdmin));
        }
 
        return fuente;
    }
 
    private void renderPedidosAdmin() {
        pedidosContainer.getChildren().clear();
 
        ArrayList<Pedido> fuente = obtenerPedidosFiltrados();
 
        // Actualizar contador y subtítulo
        int n = fuente.size();
        if (lblContadorFiltro != null)
            lblContadorFiltro.setText(n + (n == 1 ? " pedido" : " pedidos"));
        if (lblPedidosSubtitulo != null)
            lblPedidosSubtitulo.setText("Administra y actualiza el estado de cada pedido" +
                (filtroAdmin.equals("Todos") ? ""
                 : " · Filtro: " + etiquetaEstado(EstadoPedido.valueOf(filtroAdmin))));
 
        boolean hayResultados = !fuente.isEmpty();
        if (pedidosVacioAdmin != null) {
            pedidosVacioAdmin.setVisible(!hayResultados);
            pedidosVacioAdmin.setManaged(!hayResultados);
        }
 
        // Botón exportar (encabezado del listado)
        if (hayResultados) {
            pedidosContainer.getChildren().add(crearBarraAccionesGlobal(fuente.size()));
        }
 
        // Iterar en orden inverso: más reciente primero
        for (int i = fuente.size() - 1; i >= 0; i--) {
            pedidosContainer.getChildren().add(crearTarjetaPedido(fuente.get(i)));
        }
    }
 
    /** Barra superior con contador y botón de exportar */
    private HBox crearBarraAccionesGlobal(int cantidad) {
        HBox barra = new HBox(10);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle("-fx-padding: 0 0 12 0;");
 
        Label info = new Label("Mostrando " + cantidad + " pedido" + (cantidad == 1 ? "" : "s"));
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8;");
        HBox.setHgrow(info, Priority.ALWAYS);
 
        Button btnExportar = new Button("📄  Exportar reporte");
        btnExportar.setStyle("-fx-background-color: rgba(86,74,181,0.2); " +
                             "-fx-background-radius: 8; -fx-text-fill: #A99CF0; " +
                             "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 14 7 14;");
        btnExportar.setOnAction(e -> handleExportarReporte());
 
        barra.getChildren().addAll(info, btnExportar);
        return barra;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  TARJETA DE PEDIDO — expandible con ítems, cancelar, avanzar estado
    // ══════════════════════════════════════════════════════════════════════════
 
    private VBox crearTarjetaPedido(Pedido p) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: #1E1A2E; -fx-background-radius: 14; " +
                      "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 14;");
 
        // ── Cabecera ──────────────────────────────────────────────────────
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-padding: 14 18 14 18; " +
                        "-fx-cursor: hand; " +
                        "-fx-border-color: rgba(86,74,181,0.1); -fx-border-width: 0 0 1 0;");
 
        Label lblId = new Label("Pedido  #" + p.getId());
        lblId.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
 
        Label badgeEstado = new Label(textoEstado(p.getEstado()));
        badgeEstado.setStyle("-fx-background-color: " + colorFondoEstado(p.getEstado()) + "; " +
                             "-fx-background-radius: 20; " +
                             "-fx-text-fill: " + colorTextoEstado(p.getEstado()) + "; " +
                             "-fx-font-size: 11px; -fx-font-weight: bold; " +
                             "-fx-padding: 4 14 4 14;");
 
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
 
        Label lblFecha = new Label("📅 " + p.getFechaCreacion().format(FMT_FECHA));
        lblFecha.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b6890;");
 
        // Toggle expandir/colapsar
        Label toggleBtn = new Label("▾");
        toggleBtn.setStyle("-fx-font-size: 14px; -fx-text-fill: #8F8AA8; -fx-cursor: hand;");
 
        header.getChildren().addAll(lblId, badgeEstado, spacer, lblFecha, toggleBtn);
 
        // ── Sección expandible: lista de ítems ────────────────────────────
        VBox seccionItems = crearSeccionItems(p);
        seccionItems.setVisible(false);
        seccionItems.setManaged(false);
 
        // Click en cabecera togglea la sección de ítems
        header.setOnMouseClicked(e -> {
            boolean visible = !seccionItems.isVisible();
            seccionItems.setVisible(visible);
            seccionItems.setManaged(visible);
            toggleBtn.setText(visible ? "▴" : "▾");
        });
 
        // ── Cuerpo: info principal + acciones ─────────────────────────────
        HBox body = new HBox(32);
        body.setStyle("-fx-padding: 14 18 14 18;");
 
        // Columna izquierda
        VBox infoCol = new VBox(6);
        HBox.setHgrow(infoCol, Priority.ALWAYS);
 
        // Resolver nombre del comprador para mostrarlo junto al ID
        String nombreComprador = UserStore.getTodos().stream()
            .filter(u -> u.getId() == p.getIdUsuario())
            .map(Usuario::getNombre)
            .findFirst().orElse("Usuario #" + p.getIdUsuario());
        Label lblUsuario  = new Label("👤  " + nombreComprador + "  (ID #" + p.getIdUsuario() + ")");
        Label lblDireccion = new Label("📍  " + p.getDireccionEnvio());
        Label lblItems    = new Label("🛒  " + p.getItems().size() +
                                      (p.getItems().size() == 1 ? " ítem" : " ítems") +
                                      "  (clic para ver)");
        Label lblTotal    = new Label(String.format("💰  Total: $%,.2f", p.getTotal()));
 
        List.of(lblUsuario, lblDireccion, lblItems)
            .forEach(l -> l.setStyle("-fx-font-size: 12px; -fx-text-fill: #8F8AA8;"));
        lblDireccion.setWrapText(true);
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        infoCol.getChildren().addAll(lblUsuario, lblDireccion, lblItems, lblTotal);
 
        // Columna derecha: acciones
        VBox accionCol = new VBox(8);
        accionCol.setAlignment(Pos.TOP_RIGHT);
        accionCol.setStyle("-fx-min-width: 200;");
 
        Label lblAccion = new Label("Cambiar estado:");
        lblAccion.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        accionCol.getChildren().add(lblAccion);
 
        // Botones de avance de estado
        for (EstadoPedido e : EstadoPedido.values()) {
            if (e.ordinal() > p.getEstado().ordinal()) {
                accionCol.getChildren().add(crearBotonEstado(p, e));
            }
        }
 
        // Pedido completado
        if (p.getEstado() == EstadoPedido.ENTREGADO) {
            Label fin = new Label("✓ Pedido completado");
            fin.setStyle("-fx-font-size: 11px; -fx-text-fill: #10b981; -fx-font-weight: bold;");
            accionCol.getChildren().add(fin);
        }
 
        // Botón cancelar (solo para PENDIENTE o EN_PROCESO)
        if (p.getEstado() == EstadoPedido.PENDIENTE
                || p.getEstado() == EstadoPedido.EN_PROCESO) {
            Region sep = new Region();
            sep.setPrefHeight(4);
            accionCol.getChildren().add(sep);
            accionCol.getChildren().add(crearBotonCancelar(p));
        }
 
        body.getChildren().addAll(infoCol, accionCol);
        card.getChildren().addAll(header, body, seccionItems);
        return card;
    }
 
    /** Panel colapsable con el detalle línea a línea de los ítems del pedido. */
    private VBox crearSeccionItems(Pedido p) {
        VBox seccion = new VBox(4);
        seccion.setStyle("-fx-padding: 0 18 14 18; " +
                         "-fx-border-color: rgba(86,74,181,0.08); " +
                         "-fx-border-width: 1 0 0 0;");
 
        Label titulo = new Label("  Detalle de ítems");
        titulo.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8; " +
                        "-fx-font-weight: bold; -fx-padding: 10 0 6 0;");
        seccion.getChildren().add(titulo);
 
        for (ItemCarrito item : p.getItems()) {
            HBox fila = new HBox(8);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setStyle("-fx-background-color: rgba(86,74,181,0.06); " +
                          "-fx-background-radius: 8; -fx-padding: 7 12 7 12;");
 
            String varDesc = item.getVariante() != null
                ? " [" + item.getVariante().getDescripcion() + "]" : "";
 
            Label nombre = new Label(item.getProducto().getNombre() + varDesc);
            nombre.setStyle("-fx-font-size: 12px; -fx-text-fill: #C8C4E0;");
            HBox.setHgrow(nombre, Priority.ALWAYS);
 
            Label cantidad = new Label("×" + item.getCantidad());
            cantidad.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8; -fx-min-width: 30;");
 
            Label precioUnit = new Label(String.format("$%,.2f c/u", item.getPrecioUnitario()));
            precioUnit.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
 
            Label subtotal = new Label(String.format("$%,.2f", item.calcularSubtotal()));
            subtotal.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #A99CF0;");
 
            fila.getChildren().addAll(nombre, cantidad, precioUnit, subtotal);
            seccion.getChildren().add(fila);
        }
 
        // Totalizador al pie
        HBox totalFila = new HBox();
        totalFila.setAlignment(Pos.CENTER_RIGHT);
        totalFila.setStyle("-fx-padding: 6 0 0 0;");
        Label totalLbl = new Label(String.format("Total del pedido:  $%,.2f", p.getTotal()));
        totalLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        totalFila.getChildren().add(totalLbl);
        seccion.getChildren().add(totalFila);
 
        return seccion;
    }
 
    /** Botón de avance de estado con color según destino. */
    private Button crearBotonEstado(Pedido p, EstadoPedido estadoDestino) {
        Button btn = new Button(iconoEstado(estadoDestino) + "  → " + etiquetaEstado(estadoDestino));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: " + colorFondoEstado(estadoDestino) + "; " +
                     "-fx-background-radius: 8; " +
                     "-fx-text-fill: " + colorTextoEstado(estadoDestino) + "; " +
                     "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 12 7 12;");
        btn.setOnAction(e -> {
            boolean ok = gestor.avanzarEstado(p.getId(), estadoDestino);
            if (ok) {
                mostrarToast("✓ Pedido #" + p.getId() + " → " + etiquetaEstado(estadoDestino) +
                             " (guardado en archivo)");
                actualizarOverview();
                renderPedidosAdmin();
                actualizarPerfilResumen();
            } else {
                mostrarToast("⚠ No se pudo actualizar el pedido #" + p.getId());
            }
        });
        return btn;
    }
 
    /**
     * Botón para cancelar (eliminar) un pedido PENDIENTE o EN_PROCESO.
     * Solicita confirmación antes de proceder.
     */
    private Button crearBotonCancelar(Pedido p) {
        Button btn = new Button("🗑  Cancelar pedido");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: rgba(239,68,68,0.12); " +
                     "-fx-background-radius: 8; -fx-text-fill: #ef4444; " +
                     "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 7 12 7 12;");
        btn.setOnAction(e -> {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Cancelar pedido");
            alerta.setHeaderText("¿Eliminar el pedido #" + p.getId() + "?");
            alerta.setContentText("Esta acción no se puede deshacer. " +
                                  "El pedido será eliminado del registro permanentemente.");
 
            // Estilizar el diálogo
            alerta.getDialogPane().setStyle("-fx-background-color: #1E1A2E; " +
                                            "-fx-text-fill: #E9E9ED;");
 
            alerta.showAndWait().ifPresent(tipo -> {
                if (tipo == ButtonType.OK) {
                    boolean ok = gestor.cancelarPedido(p.getId());
                    if (ok) {
                        mostrarToast("🗑 Pedido #" + p.getId() + " cancelado y eliminado del archivo.");
                        actualizarOverview();
                        renderPedidosAdmin();
                        actualizarPerfilResumen();
                    } else {
                        mostrarToast("⚠ No se pudo cancelar el pedido #" + p.getId());
                    }
                }
            });
        });
        return btn;
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  PERFIL ADMIN
    // ══════════════════════════════════════════════════════════════════════════
 
    private void actualizarPerfilResumen() {
        if (lblResumenTotal     != null) lblResumenTotal.setText(String.valueOf(gestor.getTotalPedidos()));
        if (lblResumenCola      != null) lblResumenCola.setText(String.valueOf(gestor.getCantidadEnCola()));
        if (lblResumenEntregados!= null) lblResumenEntregados.setText(String.valueOf(gestor.getTotalEntregados()));
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════════════════════════════
 
    @FXML
    private void handleLogout() {
        UserStore.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("harmoniaa.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Harmonia");
            root.setOpacity(0.0);
            FadeTransition ft = new FadeTransition(Duration.millis(350), root);
            ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  TOAST
    // ══════════════════════════════════════════════════════════════════════════
 
    private void mostrarToast(String msg) {
        if (lblToastAdmin == null) return;
        lblToastAdmin.setText(msg);
        lblToastAdmin.setVisible(true);
        lblToastAdmin.setManaged(true);
 
        PauseTransition pause = new PauseTransition(Duration.seconds(2.8));
        pause.setOnFinished(e -> {
            lblToastAdmin.setVisible(false);
            lblToastAdmin.setManaged(false);
        });
        pause.play();
    }
 
    // ══════════════════════════════════════════════════════════════════════════
    //  UTILIDADES — ESTILOS DE ESTADO
    // ══════════════════════════════════════════════════════════════════════════
 
    private void actualizarTabsEstilo(Button activo) {
        List.of(tabAdminTodos, tabAdminPendiente, tabAdminEnProceso,
                tabAdminEnviado, tabAdminEntregado)
            .stream()
            .filter(t -> t != null)
            .forEach(t -> t.setStyle(t == activo ? ESTILO_TAB_ACTIVO : ESTILO_TAB_INACTIVO));
    }
 
    private String textoEstado(EstadoPedido e) {
        return switch (e) {
            case PENDIENTE  -> "⏳  Pendiente";
            case EN_PROCESO -> "⚙  En Proceso";
            case ENVIADO    -> "🚚  Enviado";
            case ENTREGADO  -> "✅  Entregado";
        };
    }
 
    private String etiquetaEstado(EstadoPedido e) {
        return switch (e) {
            case PENDIENTE  -> "Pendiente";
            case EN_PROCESO -> "En Proceso";
            case ENVIADO    -> "Enviado";
            case ENTREGADO  -> "Entregado";
        };
    }
 
    private String iconoEstado(EstadoPedido e) {
        return switch (e) {
            case PENDIENTE  -> "⏳";
            case EN_PROCESO -> "⚙";
            case ENVIADO    -> "🚚";
            case ENTREGADO  -> "✅";
        };
    }
 
    private String colorFondoEstado(EstadoPedido e) {
        return switch (e) {
            case PENDIENTE  -> "rgba(188,127,21,0.18)";
            case EN_PROCESO -> "rgba(86,74,181,0.22)";
            case ENVIADO    -> "rgba(59,130,246,0.18)";
            case ENTREGADO  -> "rgba(16,185,129,0.18)";
        };
    }
 
    private String colorTextoEstado(EstadoPedido e) {
        return switch (e) {
            case PENDIENTE  -> "#BC7F15";
            case EN_PROCESO -> "#A99CF0";
            case ENVIADO    -> "#3b82f6";
            case ENTREGADO  -> "#10b981";
        };
    }
 
    private static String primerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "Admin";
        return nombreCompleto.trim().split("\\s+")[0];
    }
}