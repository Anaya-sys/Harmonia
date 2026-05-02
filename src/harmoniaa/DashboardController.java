/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

/**
 *
 * @author CARLOS ANAYA
 */


import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
 


public class DashboardController {
     @FXML private Label  lblAvatarInitial;
    @FXML private Label  lblSidebarNombre;
    @FXML private Label  lblSidebarRol;
    @FXML private Button btnLogout;
 
    // ─── Nav buttons ───
    @FXML private Button navBtnInicio;
    @FXML private Button navBtnCatalogo;
    @FXML private Button navBtnCarrito;
    @FXML private Button navBtnPedidos;
    @FXML private Button navBtnPerfil;
 
    // ─── Content panes ───
    @FXML private ScrollPane paneInicio;
    @FXML private ScrollPane panePerfil;
    @FXML private VBox       paneCatalogo;
    @FXML private VBox       paneCarrito;
    @FXML private VBox       panePedidos;
 
    // ─── Inicio ───
    @FXML private Label    lblGreeting;
    @FXML private TextField txtBuscar;
    @FXML private FlowPane productosGrid;
 
    // ─── Perfil: display ───
    @FXML private Label  lblPerfilInitial;
    @FXML private Label  lblPerfilNombre;
    @FXML private Label  lblPerfilEmail;
    @FXML private Label  lblRolBadge;
    @FXML private Label  lblTipoPerfil;
    @FXML private Label  lblInstrumento;
    @FXML private Label  lblPresupuesto;
 
    // ─── Perfil: edición ───
    @FXML private VBox      seccionEditar;
    @FXML private Button    btnEditarPerfil;
    @FXML private TextField txtEditNombre;
    @FXML private TextField txtEditInstrumento;
    @FXML private TextField txtEditPresupuesto;
    @FXML private Label     lblPerfilFeedback;
 
    // ─── Estilos nav ───
    private static final String ESTILO_NAV_ACTIVO =
        "-fx-background-color: #564AB5;" +
        "-fx-background-radius: 10;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-cursor: hand;" +
        "-fx-padding: 0 12 0 14;";
 
    private static final String ESTILO_NAV_INACTIVO =
        "-fx-background-color: transparent;" +
        "-fx-background-radius: 10;" +
        "-fx-text-fill: #8F8AA8;" +
        "-fx-font-size: 13px;" +
        "-fx-cursor: hand;" +
        "-fx-padding: 0 12 0 14;";
 
    // ─── Estilos feedback perfil ───
    private static final String FB_OK    =
        "-fx-text-fill: #6BFF9E; -fx-font-size: 12px; -fx-text-alignment: center;";
    private static final String FB_ERROR =
        "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-text-alignment: center;";
 
    private boolean modoEdicion = false;
 
    // ══════════════════════════════════════════════════════════
    // INICIALIZACIÓN
    // ══════════════════════════════════════════════════════════
 
    @FXML
    public void initialize() {
        cargarDatosUsuario();
        cargarProductosEjemplo();
        // Estado inicial: Inicio activo
        navBtnInicio.setStyle(ESTILO_NAV_ACTIVO);
    }
 
    /**
     * Popula todos los labels con datos del usuario activo.
     * Si UserStore.getUsuarioActivo() es null (sesión expirada), regresa a login.
     */
    private void cargarDatosUsuario() {
        Usuario u = UserStore.getUsuarioActivo();
        if (u == null) {
            handleLogout();
            return;
        }
 
        // Sidebar
        String inicial = u.getNombre().isEmpty() ? "?" :
                         String.valueOf(u.getNombre().charAt(0)).toUpperCase();
        lblAvatarInitial.setText(inicial);
        lblSidebarNombre.setText(primerNombre(u.getNombre()));
        lblSidebarRol.setText(u.getRol().name());
 
        // Inicio: saludo
        lblGreeting.setText("Hola, " + primerNombre(u.getNombre()) + " 👋");
 
        // Perfil: display
        lblPerfilInitial.setText(inicial);
        lblPerfilNombre.setText(u.getNombre());
        lblPerfilEmail.setText(u.getEmail());
        lblRolBadge.setText(u.getRol().name());
 
        PerfilUsuario p = u.getPerfil();
        if (p != null) {
            lblTipoPerfil.setText(p.getTipo().name());
            lblInstrumento.setText(
                p.getInstrumento() == null || p.getInstrumento().isBlank()
                ? "Sin definir" : p.getInstrumento());
            lblPresupuesto.setText(String.format("$%.2f", p.getPresupuesto()));
 
            // Pre-llenar campos de edición
            txtEditNombre.setText(u.getNombre());
            txtEditInstrumento.setText(p.getInstrumento());
            txtEditPresupuesto.setText(String.valueOf((int) p.getPresupuesto()));
        }
    }
 
    // ══════════════════════════════════════════════════════════
    // NAVEGACIÓN
    // ══════════════════════════════════════════════════════════
 
    @FXML private void navegarInicio()   { mostrarSeccion(paneInicio,   navBtnInicio); }
    @FXML private void navegarCatalogo() { mostrarSeccion(paneCatalogo, navBtnCatalogo); }
    @FXML private void navegarCarrito()  { mostrarSeccion(paneCarrito,  navBtnCarrito); }
    @FXML private void navegarPedidos()  { mostrarSeccion(panePedidos,  navBtnPedidos); }
    @FXML private void navegarPerfil()   { mostrarSeccion(panePerfil,   navBtnPerfil); }
 
    /**
     * Muestra el pane indicado con animación fade y actualiza el estilo de la nav.
     *
     * @param pane   Nodo a mostrar
     * @param navBtn Botón de nav correspondiente
     */
    private void mostrarSeccion(Node pane, Button navBtn) {
        // Ocultar todo
        List<Node> todos = List.of(paneInicio, panePerfil,
                                   paneCatalogo, paneCarrito, panePedidos);
        for (Node n : todos) {
            n.setVisible(false);
            n.setManaged(false);
        }
        // Mostrar seleccionado con fade in
        pane.setVisible(true);
        pane.setManaged(true);
        pane.setOpacity(0.0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), pane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
 
        // Actualizar estilos nav
        List<Button> navBtns = List.of(
            navBtnInicio, navBtnCatalogo, navBtnCarrito, navBtnPedidos, navBtnPerfil);
        for (Button b : navBtns) b.setStyle(ESTILO_NAV_INACTIVO);
        navBtn.setStyle(ESTILO_NAV_ACTIVO);
 
        // Micro-animation en el botón activo
        ScaleTransition sc = new ScaleTransition(Duration.millis(150), navBtn);
        sc.setFromX(0.93); sc.setFromY(0.93);
        sc.setToX(1.0);    sc.setToY(1.0);
        sc.play();
    }
 
    // ══════════════════════════════════════════════════════════
    // PERFIL
    // ══════════════════════════════════════════════════════════
 
    /** Alterna el formulario de edición visible/oculto. */
    @FXML
    private void handleEditarPerfil() {
        modoEdicion = !modoEdicion;
        seccionEditar.setVisible(modoEdicion);
        seccionEditar.setManaged(modoEdicion);
        btnEditarPerfil.setText(modoEdicion ? "✕  Cancelar" : "✏   Editar");
        lblPerfilFeedback.setText("");
 
        // Animación de apertura del formulario
        if (modoEdicion) {
            FadeTransition ft = new FadeTransition(Duration.millis(220), seccionEditar);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }
    }
 
    /**
     * Valida y aplica los cambios del formulario de edición.
     * TODO: persistir cambios en el objeto Usuario del UserStore
     *       cuando se agregue una capa de persistencia.
     */
    @FXML
    private void handleGuardarPerfil() {
        String nuevoNombre       = txtEditNombre.getText().trim();
        String nuevoInstrumento  = txtEditInstrumento.getText().trim();
        String presupuestoStr    = txtEditPresupuesto.getText().trim();
 
        // Validación básica
        if (nuevoNombre.isEmpty()) {
            mostrarFeedbackPerfil("⚠  El nombre no puede estar vacío.", FB_ERROR);
            return;
        }
        double presupuesto = 0.0;
        if (!presupuestoStr.isEmpty()) {
            try {
                presupuesto = Double.parseDouble(presupuestoStr);
                if (presupuesto < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarFeedbackPerfil("⚠  El presupuesto debe ser un número positivo.", FB_ERROR);
                return;
            }
        }
 
        // Aplicar cambios a labels de display
        lblPerfilNombre.setText(nuevoNombre);
        lblSidebarNombre.setText(primerNombre(nuevoNombre));
        String nueva = String.valueOf(nuevoNombre.charAt(0)).toUpperCase();
        lblAvatarInitial.setText(nueva);
        lblPerfilInitial.setText(nueva);
        lblGreeting.setText("Hola, " + primerNombre(nuevoNombre) + " 👋");
 
        if (!nuevoInstrumento.isEmpty()) {
            lblInstrumento.setText(nuevoInstrumento);
        }
        if (presupuesto > 0) {
            lblPresupuesto.setText(String.format("$%.2f", presupuesto));
        }
 
        // TODO: persistir en UserStore cuando se implemente la capa de guardado:
        //   Usuario u = UserStore.getUsuarioActivo();
        //   u.getPerfil().setInstrumento(nuevoInstrumento);
        //   u.getPerfil().setPresupuesto(presupuesto);
        //   UserStore.actualizar(u);
 
        mostrarFeedbackPerfil("✓  Cambios guardados correctamente.", FB_OK);
 
        // Cerrar formulario tras 1 segundo
        javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(Duration.millis(900));
        p.setOnFinished(e -> {
            if (modoEdicion) handleEditarPerfil();
        });
        p.play();
    }
 
    /**
     * Maneja el clic en el botón de cámara del avatar.
     * TODO: implementar FileChooser para seleccionar imagen y cargarla en
     *       un ImageView que reemplace el placeholder del avatar.
     *
     * Ejemplo de implementación futura:
     *   FileChooser chooser = new FileChooser();
     *   chooser.getExtensionFilters().add(
     *       new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
     *   File file = chooser.showOpenDialog(btnCambiarFoto.getScene().getWindow());
     *   if (file != null) {
     *       Image img = new Image(file.toURI().toString());
     *       // aplicar al ImageView del avatar
     *   }
     */
    @FXML
    private void handleCambiarFoto() {
        mostrarFeedbackPerfil("📷  Funcionalidad de foto próximamente.", FB_OK);
    }
 
    private void mostrarFeedbackPerfil(String msg, String estilo) {
        lblPerfilFeedback.setText(msg);
        lblPerfilFeedback.setStyle(estilo);
    }
 
    // ══════════════════════════════════════════════════════════
    // LOGOUT
    // ══════════════════════════════════════════════════════════
 
    @FXML
    private void handleLogout() {
        UserStore.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("harmoniaa.fxml"));
            Parent root = loader.load();
 
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Harmonia");
 
            // Fade in de la pantalla de login
            root.setOpacity(0.0);
            FadeTransition ft = new FadeTransition(Duration.millis(350), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // ══════════════════════════════════════════════════════════
    // PRODUCTOS  –  PLACEHOLDER
    // ══════════════════════════════════════════════════════════
 
    /**
     * Carga 4 productos de muestra en el grid de Inicio.
     *
     * Para conectar con datos reales:
     *   Reemplazar este método por uno que itere sobre
     *   una lista de Producto y llame agregarProductoCard() por cada uno.
     *
     *   Ejemplo:
     *     for (Producto p : catalogo.getProductosDestacados()) {
     *         agregarProductoCard(p);
     *     }
     *
     *   Luego ajusta la firma de agregarProductoCard para aceptar Producto.
     */
    private void cargarProductosEjemplo() {
        agregarProductoCard("Guitarra Clásica Pro", "Guitarras",  450.0,  "🎸");
        agregarProductoCard("Piano Digital 88",     "Pianos",    1200.0,  "🎹");
        agregarProductoCard("Batería Completa",     "Percusión",  890.0,  "🥁");
        agregarProductoCard("Violín 4/4 Estudio",   "Cuerdas",    320.0,  "🎻");
    }
 
    /**
     * Construye y agrega una tarjeta de producto al FlowPane.
     *
     * Estructura de la tarjeta:
     *   VBox (card)
     *   ├── StackPane (área de imagen / TODO: IMG)
     *   │   ├── Rectangle placeholder gris
     *   │   ├── Label emoji (reemplazar con ImageView)
     *   │   └── Button wishlist (♡)
     *   └── VBox (info)
     *       ├── Label nombre
     *       ├── Label categoría
     *       ├── Label precio
     *       └── Button añadir al carrito
     *
     * @param nombre    Nombre del producto
     * @param categoria Categoría del producto
     * @param precio    Precio base
     * @param emoji     Emoji representativo (usar hasta que haya imágenes reales)
     */
    private void agregarProductoCard(String nombre, String categoria,
                                     double precio, String emoji) {
        // ── CARD CONTAINER ──
        VBox card = new VBox();
        card.setPrefWidth(198);
        card.setMaxWidth(198);
        card.setStyle(
            "-fx-background-color: #1E1A2E;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(86,74,181,0.22);" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);"
        );
 
        // ── ÁREA DE IMAGEN  (TODO: IMG) ──────────────────────────────
        // Para agregar imagen real:
        //   1. Quita el emojiLbl
        //   2. Agrega: ImageView iv = new ImageView(new Image("ruta/imagen.jpg"));
        //              iv.setFitWidth(198); iv.setFitHeight(130); iv.setPreserveRatio(false);
        //   3. imgArea.getChildren().add(0, iv);  // agrega antes del wishlist btn
        // ─────────────────────────────────────────────────────────────
        StackPane imgArea = new StackPane();
        imgArea.setPrefHeight(130);
        imgArea.setPrefWidth(198);
        imgArea.setStyle(
            "-fx-background-color: #272239;" +
            "-fx-background-radius: 12 12 0 0;"
        );
 
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 46px;");
 
        // Botón de lista de deseos
        Button btnWish = new Button("♡");
        btnWish.setStyle(
            "-fx-background-color: rgba(30,26,46,0.75);" +
            "-fx-background-radius: 16;" +
            "-fx-text-fill: #8F8AA8;" +
            "-fx-font-size: 14px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 3 7 3 7;"
        );
        StackPane.setAlignment(btnWish, Pos.TOP_RIGHT);
        StackPane.setMargin(btnWish, new Insets(8, 8, 0, 0));
        // TODO: btnWish.setOnAction(e -> agregarADeseos(producto));
 
        imgArea.getChildren().addAll(emojiLbl, btnWish);
 
        // ── INFO ──
        VBox info = new VBox(6);
        info.setStyle("-fx-padding: 12 12 14 12;");
 
        Label lNombre = new Label(nombre);
        lNombre.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #E9E9ED;"
        );
        lNombre.setMaxWidth(174);
        lNombre.setWrapText(true);
 
        Label lCat = new Label(categoria);
        lCat.setStyle("-fx-font-size: 10px; -fx-text-fill: #8F8AA8;");
 
        Label lPrecio = new Label(String.format("$%.0f", precio));
        lPrecio.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #BC7F15;"
        );
 
        // Botón añadir al carrito
        Button btnAdd = new Button("🛒  Añadir");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnAdd, Priority.ALWAYS);
        btnAdd.setStyle(
            "-fx-background-color: #564AB5;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 7 10 7 10;" +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.25),8,0,0,3);"
        );
        // TODO: btnAdd.setOnAction(e -> agregarAlCarrito(producto, variante, 1));
 
        info.getChildren().addAll(lNombre, lCat, lPrecio, btnAdd);
        card.getChildren().addAll(imgArea, info);
 
        // Hover effect
        card.setOnMouseEntered(e ->
            card.setStyle(
                "-fx-background-color: #272239;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(86,74,181,0.5);" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.2),20,0,0,6);"
            ));
        card.setOnMouseExited(e ->
            card.setStyle(
                "-fx-background-color: #1E1A2E;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(86,74,181,0.22);" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);"
            ));
 
        productosGrid.getChildren().add(card);
    }
 
    // ══════════════════════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════════════════════
 
    /** Extrae el primer nombre de un string completo. */
    private static String primerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "Usuario";
        String[] partes = nombreCompleto.trim().split("\\s+");
        return partes[0];
    }
}
