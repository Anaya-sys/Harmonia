package harmoniaa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

    // ── SIDEBAR ────────────────────────────────────────────────────────────────
    @FXML private Label  lblAvatarInitial;
    @FXML private Label  lblSidebarNombre;
    @FXML private Label  lblSidebarRol;
    @FXML private Button btnLogout;

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
    @FXML private VBox       panePedidos;
    @FXML private ScrollPane paneDeseos;

    // ── INICIO ─────────────────────────────────────────────────────────────────
    @FXML private Label    lblGreeting;
    @FXML private TextField txtBuscar;
    @FXML private FlowPane productosGrid;

    // ── CATÁLOGO ───────────────────────────────────────────────────────────────
    @FXML private Label             lblResultados;
    @FXML private TextField         txtCatalogoBuscar;
    @FXML private ComboBox<String>  cmbOrdenar;
    @FXML private HBox              chipsActivos;

    @FXML private Button catTodas;
    @FXML private Button catGuitarras;
    @FXML private Button catTeclados;
    @FXML private Button catPercusion;
    @FXML private Button catVientos;
    @FXML private Button catAccesorios;

    @FXML private Button precioTodos;
    @FXML private Button precio0a500;
    @FXML private Button precio500a2M;
    @FXML private Button precio2Ma5M;
    @FXML private Button precioMas5M;

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

    // ── PERFIL ─────────────────────────────────────────────────────────────────
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

    private String filtroCat    = "Todas";
    private String filtroPrecio = "Todos";
    private String filtroDisp   = "Todos";
    private String busqueda     = "";
    private boolean modoEdicion = false;

    // ── ESTILOS ────────────────────────────────────────────────────────────────
    private static final String ESTILO_NAV_ACTIVO =
        "-fx-background-color: #564AB5; -fx-background-radius: 10; " +
        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 0 12 0 14;";
    private static final String ESTILO_NAV_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 10; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 13px; " +
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
    private static final String ESTILO_PRECIO_ACTIVO =
        "-fx-background-color: rgba(86,74,181,0.15); -fx-background-radius: 8; " +
        "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 8; " +
        "-fx-text-fill: #A99CF0; -fx-font-size: 11.5px; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String ESTILO_PRECIO_INACTIVO =
        "-fx-background-color: transparent; -fx-background-radius: 8; " +
        "-fx-text-fill: #8F8AA8; -fx-font-size: 11.5px; " +
        "-fx-cursor: hand; -fx-padding: 0 10 0 12;";
    private static final String FB_OK =
        "-fx-text-fill: #6BFF9E; -fx-font-size: 12px; -fx-text-alignment: center;";
    private static final String FB_ERROR =
        "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-text-alignment: center;";

    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        cargarCatalogoDatos();
        cargarDatosUsuario();
        iniciarComboOrdenar();
        renderCatalogo();
        cargarProductosDestacadosInicio();
        actualizarVistaCarrito();
        actualizarVistaDeseos();
        navBtnInicio.setStyle(ESTILO_NAV_ACTIVO);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DATOS CATÁLOGO
    // ══════════════════════════════════════════════════════════════════════════
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
            "Sintetizador portátil con 61 teclas y batería", 3799000, 4, 2);

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

    // ══════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN
    // ══════════════════════════════════════════════════════════════════════════
    private void mostrarSeccion(Node pane, Button navBtn) {
        List<Node> todos = List.of(paneInicio, panePerfil,
                                   paneCatalogo, paneCarrito, paneDeseos, panePedidos);
        for (Node n : todos) { n.setVisible(false); n.setManaged(false); }

        pane.setVisible(true);
        pane.setManaged(true);
        pane.setOpacity(0.0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), pane);
        ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();

        List<Button> navBtns = List.of(navBtnInicio, navBtnCatalogo, navBtnCarrito,
                                        navBtnDeseos, navBtnPedidos, navBtnPerfil);
        for (Button b : navBtns) b.setStyle(ESTILO_NAV_INACTIVO);

        navBtn.setStyle(ESTILO_NAV_ACTIVO);
        ScaleTransition sc = new ScaleTransition(Duration.millis(150), navBtn);
        sc.setFromX(0.93); sc.setFromY(0.93); sc.setToX(1.0); sc.setToY(1.0); sc.play();
    }

    @FXML private void navegarInicio()   { mostrarSeccion(paneInicio,   navBtnInicio); }
    @FXML private void navegarCatalogo() { mostrarSeccion(paneCatalogo, navBtnCatalogo); renderCatalogo(); }
    @FXML private void navegarCarrito()  { actualizarVistaCarrito(); mostrarSeccion(paneCarrito, navBtnCarrito); }
    @FXML private void navegarDeseos()   { actualizarVistaDeseos();  mostrarSeccion(paneDeseos,  navBtnDeseos); }
    @FXML private void navegarPedidos()  { mostrarSeccion(panePedidos,  navBtnPedidos); }
    @FXML private void navegarPerfil()   { mostrarSeccion(panePerfil,   navBtnPerfil); }

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
    private void filtrarPrecio(javafx.event.ActionEvent e) {
        Button src = (Button) e.getSource();
        if      (src == precioTodos)  filtroPrecio = "Todos";
        else if (src == precio0a500)  filtroPrecio = "0-500000";
        else if (src == precio500a2M) filtroPrecio = "500000-2000000";
        else if (src == precio2Ma5M)  filtroPrecio = "2000000-5000000";
        else if (src == precioMas5M)  filtroPrecio = "5000000+";
        for (Button b : List.of(precioTodos, precio0a500, precio500a2M, precio2Ma5M, precioMas5M))
            b.setStyle(b == src ? ESTILO_PRECIO_ACTIVO : ESTILO_PRECIO_INACTIVO);
        renderCatalogo();
    }

    @FXML
    private void filtrarDisponibilidad(javafx.event.ActionEvent e) {
        Button src = (Button) e.getSource();
        filtroDisp = (src == dispDisponible) ? "Disponible" : "Todos";
        for (Button b : List.of(dispTodos, dispDisponible))
            b.setStyle(b == src ? ESTILO_PRECIO_ACTIVO : ESTILO_PRECIO_INACTIVO);
        renderCatalogo();
    }

    @FXML
    private void handleCatalogoBuscar() {
        busqueda = txtCatalogoBuscar.getText().trim().toLowerCase();
        renderCatalogo();
    }

    @FXML
    private void handleOrdenar() { renderCatalogo(); }

    @FXML
    private void limpiarFiltros() {
        filtroCat = "Todas"; filtroPrecio = "Todos"; filtroDisp = "Todos"; busqueda = "";
        txtCatalogoBuscar.clear();
        cmbOrdenar.setValue("Relevancia");
        catTodas.setStyle(ESTILO_FILTRO_ACTIVO);
        for (Button b : List.of(catGuitarras, catTeclados, catPercusion, catVientos, catAccesorios))
            b.setStyle(ESTILO_FILTRO_INACTIVO);
        precioTodos.setStyle(ESTILO_PRECIO_ACTIVO);
        for (Button b : List.of(precio0a500, precio500a2M, precio2Ma5M, precioMas5M))
            b.setStyle(ESTILO_PRECIO_INACTIVO);
        dispTodos.setStyle(ESTILO_PRECIO_ACTIVO);
        dispDisponible.setStyle(ESTILO_PRECIO_INACTIVO);
        renderCatalogo();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CATÁLOGO – RENDER
    // ══════════════════════════════════════════════════════════════════════════
    private void renderCatalogo() {
        List<Producto> filtrados = catalogo.stream().filter(p -> {
            if (!filtroCat.equals("Todas")) {
                Categoria cat = getCategoriaById(p.getIdCategoria());
                if (cat == null || !cat.getNombre().equals(filtroCat)) return false;
            }
            if (!filtroPrecio.equals("Todos")) {
                double pr = p.getPrecio();
                switch (filtroPrecio) {
                    case "0-500000"        -> { if (pr > 500000)                   return false; }
                    case "500000-2000000"  -> { if (pr < 500000  || pr > 2000000)  return false; }
                    case "2000000-5000000" -> { if (pr < 2000000 || pr > 5000000)  return false; }
                    case "5000000+"        -> { if (pr < 5000000)                  return false; }
                }
            }
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
        catalogoVacio.setVisible(sinRes);
        catalogoVacio.setManaged(sinRes);
        for (Producto p : filtrados) catalogoGrid.getChildren().add(crearTarjetaProducto(p));
        actualizarChips();
    }

    private VBox crearTarjetaProducto(Producto p) {
        VBox card = new VBox(0);
        card.setPrefWidth(200); card.setMaxWidth(200);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 12; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);");

        // Zona imagen / emoji
        StackPane imgZone = new StackPane();
        imgZone.setPrefHeight(130);
        imgZone.setStyle("-fx-background-color: #13111e; -fx-background-radius: 12 12 0 0;");
        Label lblEmoji = new Label(getEmojiCategoria(p.getIdCategoria()));
        lblEmoji.setStyle("-fx-font-size: 52px;");
        imgZone.getChildren().add(lblEmoji);

        // Badge stock
        if (p.estaDisponible()) {
            Label badge = new Label("✓ Stock");
            badge.setStyle(
                "-fx-background-color: rgba(16,185,129,0.18); -fx-background-radius: 6; " +
                "-fx-border-color: rgba(16,185,129,0.35); -fx-border-radius: 6; " +
                "-fx-text-fill: #10b981; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 3 8 3 8;");
            VBox bw = new VBox(badge);
            bw.setStyle("-fx-padding: 8 0 0 8;");
            StackPane.setAlignment(bw, Pos.TOP_LEFT);
            imgZone.getChildren().add(bw);
        }

        // Botón deseo ♡ / ♥
        Button btnDeseo = new Button(deseos.contiene(p.getId()) ? "♥" : "♡");
        btnDeseo.setStyle(
            "-fx-background-color: rgba(13,13,20,0.7); -fx-background-radius: 50%; " +
            "-fx-text-fill: " + (deseos.contiene(p.getId()) ? "#ef4444" : "#8F8AA8") + "; " +
            "-fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 7 4 7; " +
            "-fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 50%;");
        btnDeseo.setOnAction(e -> {
            if (deseos.contiene(p.getId())) deseos.quitar(p.getId());
            else deseos.agregar(p);
            renderCatalogo();
        });
        VBox dw = new VBox(btnDeseo);
        dw.setStyle("-fx-padding: 8 8 0 0;");
        StackPane.setAlignment(dw, Pos.TOP_RIGHT);
        imgZone.getChildren().add(dw);

        // Cuerpo info
        VBox body = new VBox(6);
        body.setStyle("-fx-padding: 12 14 12 14;");
        Categoria cat = getCategoriaById(p.getIdCategoria());
        Label lblCat = new Label(cat != null ? cat.getNombre().toUpperCase() : "PRODUCTO");
        lblCat.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #8b7cf8; -fx-letter-spacing: 1px;");
        Label lblNombre = new Label(p.getNombre());
        lblNombre.setWrapText(true); lblNombre.setMaxWidth(172);
        lblNombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label lblDesc = new Label(p.getDescripcion());
        lblDesc.setWrapText(true); lblDesc.setMaxWidth(172);
        lblDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
        Label lblPrecio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        int nv = p.getVariantes().size();
        Label lblVar = new Label(nv > 0 ? nv + " variante" + (nv > 1 ? "s" : "") : "Producto único");
        lblVar.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b6890;");
        body.getChildren().addAll(lblCat, lblNombre, lblDesc, lblPrecio, lblVar);

        // Footer con botón agregar
        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
            "-fx-padding: 10 14 12 14; " +
            "-fx-border-color: rgba(86,74,181,0.15); -fx-border-width: 1 0 0 0;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        boolean yaEnCarrito = carrito.getItems().stream()
            .anyMatch(it -> it.getProducto().getId() == p.getId());
        Button btnAgregar = new Button(yaEnCarrito ? "✓ En carrito" : "🛒  Agregar");
        btnAgregar.setStyle(
            (yaEnCarrito
                ? "-fx-background-color: rgba(16,185,129,0.2); -fx-border-color: rgba(16,185,129,0.4); -fx-text-fill: #10b981;"
                : "-fx-background-color: #564AB5; -fx-border-color: transparent; -fx-text-fill: white;") +
            " -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 7 14 7 14;");
        btnAgregar.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            mostrarToast("🛒 \"" + p.getNombre() + "\" añadido al carrito.");
            renderCatalogo();
        });
        footer.getChildren().addAll(spacer, btnAgregar);

        card.getChildren().addAll(imgZone, body, footer);

        // Hover
        String estiloNormal = "-fx-background-color: #1E1A2E; -fx-background-radius: 12; " +
            "-fx-border-color: rgba(86,74,181,0.2); -fx-border-radius: 12; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);";
        String estiloHover  = "-fx-background-color: #252540; -fx-background-radius: 12; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 12; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.35),20,0,0,6);";
        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e  -> card.setStyle(estiloNormal));
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // INICIO – DESTACADOS
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarProductosDestacadosInicio() {
        productosGrid.getChildren().clear();
        List<Producto> dest = catalogo.subList(0, Math.min(6, catalogo.size()));
        for (Producto p : dest) productosGrid.getChildren().add(crearMiniCard(p));
    }

    private VBox crearMiniCard(Producto p) {
        VBox card = new VBox(8);
        card.setPrefWidth(170); card.setMaxWidth(170);
        card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10; " +
            "-fx-padding: 14; -fx-cursor: hand;");
        Label emoji  = new Label(getEmojiCategoria(p.getIdCategoria()));
        emoji.setStyle("-fx-font-size: 30px;");
        Label nombre = new Label(p.getNombre());
        nombre.setWrapText(true); nombre.setMaxWidth(142);
        nombre.setStyle("-fx-font-size: 11.5px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        card.getChildren().addAll(emoji, nombre, precio);
        card.setOnMouseClicked(e -> navegarCatalogo());
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #252540; -fx-background-radius: 10; " +
            "-fx-border-color: #564AB5; -fx-border-radius: 10; -fx-padding: 14; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.2),12,0,0,4);"));
        card.setOnMouseExited(e  -> card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10; -fx-padding: 14; -fx-cursor: hand;"));
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARRITO
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarVistaCarrito() {
        carritoItemsContainer.getChildren().clear();
        boolean vacio = carrito.isEmpty();
        lblCarritoVacio.setVisible(vacio);
        lblCarritoVacio.setManaged(vacio);
        for (ItemCarrito item : carrito.getItems())
            carritoItemsContainer.getChildren().add(crearFilaCarrito(item));
        lblCarritoTotal.setText("Total: $" + String.format("%,.2f", carrito.calcularTotal()));
    }

    private HBox crearFilaCarrito(ItemCarrito item) {
        HBox fila = new HBox(14);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 16, 14, 16));
        fila.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(86,74,181,0.18); -fx-border-radius: 10;");
        Label emoji = new Label(getEmojiCategoria(item.getProducto().getIdCategoria()));
        emoji.setStyle("-fx-font-size: 28px;");
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
        btnQ.setStyle(
            "-fx-background-color: rgba(255,107,107,0.1); -fx-background-radius: 6; " +
            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        int idVar = item.getVariante() != null ? item.getVariante().getId() : -1;
        btnQ.setOnAction(e -> { carrito.quitar(item.getProducto().getId(), idVar); actualizarVistaCarrito(); });
        fila.getChildren().addAll(emoji, info, subtotal, btnQ);
        return fila;
    }

    @FXML
    private void handleDeshacerCarrito() {
        ItemCarrito ultimo = carrito.deshacer();
        if (ultimo != null)
            mostrarToast("↩ \"" + ultimo.getProducto().getNombre() + "\" eliminado del carrito.");
        actualizarVistaCarrito();
    }

    @FXML
    private void handleConfirmarPedido() {
        if (carrito.isEmpty()) return;
        ArrayList<ItemCarrito> items = new ArrayList<>(carrito.getItems());
        Pedido pedido = new Pedido(
            (int)(System.currentTimeMillis() % 100000),
            UserStore.getUsuarioActivo() != null ? UserStore.getUsuarioActivo().getId() : 0,
            items, "Dirección por definir");
        if (UserStore.getUsuarioActivo() != null)
            UserStore.getUsuarioActivo().agregarAlHistorial(pedido);
        carrito.vaciar();
        actualizarVistaCarrito();
        lblCarritoVacio.setText("✅  ¡Pedido #" + pedido.getId() + " confirmado! ¡Gracias!");
        lblCarritoVacio.setStyle(
            "-fx-font-size: 13px; -fx-text-fill: #10b981; " +
            "-fx-text-alignment: center; -fx-padding: 40 0 40 0; " +
            "-fx-background-color: rgba(16,185,129,0.06); -fx-background-radius: 12;");
        lblCarritoVacio.setVisible(true);
        lblCarritoVacio.setManaged(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DESEOS
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarVistaDeseos() {
        deseosItemsContainer.getChildren().clear();
        boolean vacio = deseos.isEmpty();
        lblDeseosVacio.setVisible(vacio);
        lblDeseosVacio.setManaged(vacio);
        for (Producto p : deseos.getProductos())
            deseosItemsContainer.getChildren().add(crearFilaDeseo(p));
    }

    private HBox crearFilaDeseo(Producto p) {
        HBox fila = new HBox(14);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(14, 16, 14, 16));
        fila.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 10; " +
            "-fx-border-color: rgba(188,127,21,0.2); -fx-border-radius: 10;");
        Label emoji = new Label(getEmojiCategoria(p.getIdCategoria()));
        emoji.setStyle("-fx-font-size: 28px;");
        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label nombre = new Label(p.getNombre());
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label desc = new Label(p.getDescripcion());
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nombre, desc);
        Label precio = new Label("$" + String.format("%,.0f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
        Button btnMover = new Button("🛒");
        btnMover.setStyle(
            "-fx-background-color: rgba(86,74,181,0.2); -fx-background-radius: 6; " +
            "-fx-text-fill: #A99CF0; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        btnMover.setOnAction(e -> {
            Variante v = p.getVariantes().isEmpty() ? null : p.getVariantes().get(0);
            carrito.agregar(new ItemCarrito(p, v, 1));
            deseos.quitar(p.getId());
            actualizarVistaDeseos();
            actualizarVistaCarrito();
        });
        Button btnQ = new Button("✕");
        btnQ.setStyle(
            "-fx-background-color: rgba(255,107,107,0.1); -fx-background-radius: 6; " +
            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 4 8 4 8;");
        btnQ.setOnAction(e -> { deseos.quitar(p.getId()); actualizarVistaDeseos(); });
        fila.getChildren().addAll(emoji, info, precio, btnMover, btnQ);
        return fila;
    }

    @FXML private void handleDeshacerDeseos() {
        Producto ultimo = deseos.deshacer();
        if (ultimo != null)
            mostrarToast("↩ \"" + ultimo.getNombre() + "\" removido de deseos.");
        actualizarVistaDeseos();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PERFIL
    // ══════════════════════════════════════════════════════════════════════════
    @FXML
    private void handleEditarPerfil() {
        modoEdicion = !modoEdicion;
        seccionEditar.setVisible(modoEdicion);
        seccionEditar.setManaged(modoEdicion);
        if (btnEditarPerfil != null)
            btnEditarPerfil.setText(modoEdicion ? "✕  Cancelar" : "✏   Editar");
        if (!modoEdicion) mostrarFeedbackPerfil("", "");
    }

    @FXML
    private void handleGuardarPerfil() {
        String nombre     = txtEditNombre.getText().trim();
        String instrumento = txtEditInstrumento.getText().trim();
        String presStr    = txtEditPresupuesto.getText().trim();
        if (nombre.isEmpty()) {
            mostrarFeedbackPerfil("⚠ El nombre no puede estar vacío.", FB_ERROR); return;
        }
        double presup;
        try {
            presup = Double.parseDouble(presStr);
            if (presup < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarFeedbackPerfil("⚠ Presupuesto inválido.", FB_ERROR); return;
        }
        lblPerfilNombre.setText(nombre);
        lblSidebarNombre.setText(primerNombre(nombre));
        lblGreeting.setText("Hola, " + primerNombre(nombre) + " 👋");
        lblInstrumento.setText(instrumento.isBlank() ? "Sin definir" : instrumento);
        lblPresupuesto.setText(String.format("$%.2f", presup));
        mostrarFeedbackPerfil("✓ Perfil actualizado.", FB_OK);
        modoEdicion = false;
        seccionEditar.setVisible(false);
        seccionEditar.setManaged(false);
    }

    @FXML
    private void handleCambiarFoto() {
        mostrarFeedbackPerfil("📷 Funcionalidad de foto próximamente.", FB_OK);
    }

    private void mostrarFeedbackPerfil(String msg, String estilo) {
        lblPerfilFeedback.setText(msg);
        lblPerfilFeedback.setStyle(estilo);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LOGOUT
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CHIPS DE FILTROS ACTIVOS
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarChips() {
        chipsActivos.getChildren().clear();
        boolean hay = false;
        if (!filtroCat.equals("Todas")) {
            chipsActivos.getChildren().add(crearChip("🎵 " + filtroCat, () -> {
                filtroCat = "Todas";
                catTodas.setStyle(ESTILO_FILTRO_ACTIVO);
                for (Button b : List.of(catGuitarras, catTeclados, catPercusion, catVientos, catAccesorios))
                    b.setStyle(ESTILO_FILTRO_INACTIVO);
                renderCatalogo();
            })); hay = true;
        }
        if (!filtroPrecio.equals("Todos")) {
            String lbl = switch (filtroPrecio) {
                case "0-500000"        -> "Hasta $500k";
                case "500000-2000000"  -> "$500k–$2M";
                case "2000000-5000000" -> "$2M–$5M";
                case "5000000+"        -> "Más de $5M";
                default -> filtroPrecio;
            };
            chipsActivos.getChildren().add(crearChip("💰 " + lbl, () -> {
                filtroPrecio = "Todos";
                precioTodos.setStyle(ESTILO_PRECIO_ACTIVO);
                for (Button b : List.of(precio0a500, precio500a2M, precio2Ma5M, precioMas5M))
                    b.setStyle(ESTILO_PRECIO_INACTIVO);
                renderCatalogo();
            })); hay = true;
        }
        if (!busqueda.isEmpty()) {
            chipsActivos.getChildren().add(crearChip("🔍 \"" + busqueda + "\"", () -> {
                busqueda = ""; txtCatalogoBuscar.clear(); renderCatalogo();
            })); hay = true;
        }
        chipsActivos.setVisible(hay);
        chipsActivos.setManaged(hay);
    }

    private HBox crearChip(String texto, Runnable onQuitar) {
        HBox chip = new HBox(6); chip.setAlignment(Pos.CENTER_LEFT);
        chip.setStyle(
            "-fx-background-color: rgba(86,74,181,0.18); -fx-background-radius: 20; " +
            "-fx-border-color: rgba(86,74,181,0.4); -fx-border-radius: 20; -fx-padding: 4 10 4 12;");
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #A99CF0;");
        Button x = new Button("✕");
        x.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #A99CF0; " +
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
            new javafx.animation.PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> lblGreeting.setText(original));
        pause.play();
    }

    private static String primerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "Usuario";
        return nombreCompleto.trim().split("\\s+")[0];
    }
}
