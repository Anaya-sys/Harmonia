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
        actualizarOverview();
        actualizarTabsEstilo(tabAdminTodos);
        mostrarSeccion(paneOverview, navBtnDashboard);
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
 
    @FXML private void navegarCatalogoAdmin() { mostrarSeccion(paneCatalogoAdmin, navBtnCatalogo); }
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
 
        // Stat-cards
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
 
        Label usr  = new Label("👤 " + p.getIdUsuario());
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
 
        Label usuario = new Label("👤 " + p.getIdUsuario());
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
 
        Label lblUsuario  = new Label("👤  Usuario: #" + p.getIdUsuario());
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