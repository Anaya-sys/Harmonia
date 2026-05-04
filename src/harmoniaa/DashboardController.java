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
 
    
    @FXML private Button navBtnInicio;
    @FXML private Button navBtnCatalogo;
    @FXML private Button navBtnCarrito;
    @FXML private Button navBtnDeseos;
    @FXML private Button navBtnPedidos;
    @FXML private Button navBtnPerfil;
 
    
    @FXML private ScrollPane paneInicio;
    @FXML private ScrollPane panePerfil;
    @FXML private VBox       paneCatalogo;
    @FXML private ScrollPane paneCarrito;
    @FXML private ScrollPane paneDeseos;
    @FXML private VBox       panePedidos;
 
    
    @FXML private Label    lblGreeting;
    @FXML private TextField txtBuscar;
    @FXML private FlowPane productosGrid;
 
    
    @FXML private VBox  carritoItemsContainer;
    @FXML private Label lblCarritoTotal;
    @FXML private Label lblCarritoVacio;
    @FXML private Label navBadgeCarrito;   
    @FXML private Label navBadgeDeseos;    
 
    
    @FXML private VBox  deseosItemsContainer;
    @FXML private Label lblDeseosVacio;
 
    
    @FXML private Label  lblPerfilInitial;
    @FXML private Label  lblPerfilNombre;
    @FXML private Label  lblPerfilEmail;
    @FXML private Label  lblRolBadge;
    @FXML private Label  lblTipoPerfil;
    @FXML private Label  lblInstrumento;
    @FXML private Label  lblPresupuesto;
 
    
    @FXML private VBox      seccionEditar;
    @FXML private Button    btnEditarPerfil;
    @FXML private TextField txtEditNombre;
    @FXML private TextField txtEditInstrumento;
    @FXML private TextField txtEditPresupuesto;
    @FXML private Label     lblPerfilFeedback;
 
    
    private final Carritostack     carrito = new Carritostack();
    private final Listadeseosstack deseos  = new Listadeseosstack();
 
    
    private List<Producto> catalogoDemo;
 
    
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
 
    private static final String FB_OK    =
        "-fx-text-fill: #6BFF9E; -fx-font-size: 12px; -fx-text-alignment: center;";
    private static final String FB_ERROR =
        "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-text-alignment: center;";
 
    private boolean modoEdicion = false;
 
   
    @FXML
    public void initialize() {
        cargarCatalogoDemo();
        cargarDatosUsuario();
        cargarProductosEjemplo();
        navBtnInicio.setStyle(ESTILO_NAV_ACTIVO);
        actualizarVistaCarrito();
        actualizarVistaDeseos();
    }
 
    /** Crea los productos de demostración con IDs únicos. */
    private void cargarCatalogoDemo() {
        Marca fender   = new Marca(1, "Fender",   "USA");
        Marca yamaha   = new Marca(2, "Yamaha",   "Japón");
        Marca pearl    = new Marca(3, "Pearl",    "Japón");
        Marca cremona  = new Marca(4, "Cremona",  "Italia");
 
        Categoria guitars  = new Categoria(1, "Guitarras");
        Categoria pianos   = new Categoria(2, "Pianos");
        Categoria percus   = new Categoria(3, "Percusión");
        Categoria cuerdas  = new Categoria(4, "Cuerdas");
 
        catalogoDemo = List.of(
            new Producto(101, "Guitarra Clásica Pro", "Ideal para principiantes y estudio", 450.0, 1, 1),
            new Producto(102, "Piano Digital 88",     "88 teclas contrapesadas, 3 pedales",1200.0, 2, 2),
            new Producto(103, "Batería Completa",     "Set profesional de 5 piezas",        890.0, 3, 3),
            new Producto(104, "Violín 4/4 Estudio",  "Tapa de abeto macizo",               320.0, 4, 4)
        );
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
 
   
 
    @FXML private void navegarInicio()   { mostrarSeccion(paneInicio,   navBtnInicio); }
    @FXML private void navegarCatalogo() { mostrarSeccion(paneCatalogo, navBtnCatalogo); }
    @FXML private void navegarCarrito()  {
        actualizarVistaCarrito();
        mostrarSeccion(paneCarrito,  navBtnCarrito);
    }
    @FXML private void navegarDeseos()   {
        actualizarVistaDeseos();
        mostrarSeccion(paneDeseos,   navBtnDeseos);
    }
    @FXML private void navegarPedidos()  { mostrarSeccion(panePedidos,  navBtnPedidos); }
    @FXML private void navegarPerfil()   { mostrarSeccion(panePerfil,   navBtnPerfil); }
 
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
 
    
    private void agregarAlCarrito(Producto p) {
        ItemCarrito item = new ItemCarrito(p, null, 1);
        carrito.agregar(item);                          // PUSH
        actualizarBadgeCarrito();
        mostrarToast("🛒 \"" + p.getNombre() + "\" añadido al carrito.");
    }
 
  
    @FXML
    private void handleDeshacerCarrito() {
        ItemCarrito ultimo = carrito.deshacer();        // POP
        if (ultimo != null) {
            mostrarToast("↩ \"" + ultimo.getProducto().getNombre() + "\" eliminado del carrito.");
        }
        actualizarVistaCarrito();
    }
 
    
    @FXML
    private void handleConfirmarPedido() {
        if (carrito.isEmpty()) return;
        mostrarToast("✓ Pedido confirmado por $" + String.format("%.2f", carrito.calcularTotal()) + ". ¡Gracias!");
        carrito.vaciar();
        actualizarVistaCarrito();
        actualizarBadgeCarrito();
    }
 
    
    private void actualizarVistaCarrito() {
        carritoItemsContainer.getChildren().clear();
 
        boolean vacio = carrito.isEmpty();
        lblCarritoVacio.setVisible(vacio);
        lblCarritoVacio.setManaged(vacio);
 
        for (ItemCarrito item : carrito.getItems()) {       // iteración LIFO
            carritoItemsContainer.getChildren().add(crearFilaCarrito(item));
        }
 
        lblCarritoTotal.setText("Total: $" + String.format("%.2f", carrito.calcularTotal()));
        actualizarBadgeCarrito();
    }
 
    
    private HBox crearFilaCarrito(ItemCarrito item) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(10, 12, 10, 12));
        fila.setStyle(
            "-fx-background-color: #1E1A2E;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(86,74,181,0.18);" +
            "-fx-border-radius: 10;"
        );
 
        
        Label emoji = new Label(emojiParaProducto(item.getProducto().getId()));
        emoji.setStyle("-fx-font-size: 26px;");
 
        
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label nombre = new Label(item.getProducto().getNombre());
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label detalles = new Label("x" + item.getCantidad() + "  ·  $" +
            String.format("%.2f", item.getPrecioUnitario()) + " c/u");
        detalles.setStyle("-fx-font-size: 10px; -fx-text-fill: #8F8AA8;");
        info.getChildren().addAll(nombre, detalles);
 
       
        Label subtotal = new Label("$" + String.format("%.2f", item.calcularSubtotal()));
        subtotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
        
        Button btnQuitar = new Button("✕");
        btnQuitar.setStyle(
            "-fx-background-color: rgba(255,107,107,0.15);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: #FF6B6B;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 4 8 4 8;" +
            "-fx-border-color: rgba(255,107,107,0.25);" +
            "-fx-border-radius: 8;"
        );
        int idProducto = item.getProducto().getId();
        int idVariante = item.getVariante() != null ? item.getVariante().getId() : -1;
        btnQuitar.setOnAction(e -> {
            carrito.quitar(idProducto, idVariante);
            actualizarVistaCarrito();
        });
 
        fila.getChildren().addAll(emoji, info, subtotal, btnQuitar);
        return fila;
    }
 
    private void actualizarBadgeCarrito() {
        int n = carrito.size();
        if (navBadgeCarrito != null) {
            navBadgeCarrito.setText(n > 0 ? String.valueOf(n) : "");
            navBadgeCarrito.setVisible(n > 0);
            navBadgeCarrito.setManaged(n > 0);
        }
        
        navBtnCarrito.setText(n > 0 ? "  🛒   Carrito (" + n + ")" : "  🛒   Carrito");
    }
 
   
    private void agregarADeseos(Producto p, Button btnWish) {
        if (deseos.contiene(p.getId())) {
            deseos.quitar(p.getId());                   
            btnWish.setText("♡");
            btnWish.setStyle(btnWish.getStyle()
                .replace("#BC7F15", "#8F8AA8").replace("white", "#8F8AA8"));
            mostrarToast("💔 \"" + p.getNombre() + "\" removido de deseos.");
        } else {
            deseos.agregar(p);                          
            btnWish.setText("♥");
            btnWish.setStyle(
                "-fx-background-color: rgba(188,127,21,0.2);" +
                "-fx-background-radius: 16;" +
                "-fx-text-fill: #BC7F15;" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 3 7 3 7;"
            );
            mostrarToast("♥ \"" + p.getNombre() + "\" añadido a deseos.");
        }
        actualizarBadgeDeseos();
    }
 
    
    @FXML
    private void handleDeshacerDeseos() {
        Producto ultimo = deseos.deshacer();            
        if (ultimo != null) {
            mostrarToast("↩ \"" + ultimo.getNombre() + "\" removido de deseos.");
        }
        actualizarVistaDeseos();
        actualizarBadgeDeseos();
    }
 
   
    private void actualizarVistaDeseos() {
        deseosItemsContainer.getChildren().clear();
 
        boolean vacio = deseos.isEmpty();
        lblDeseosVacio.setVisible(vacio);
        lblDeseosVacio.setManaged(vacio);
 
        for (Producto p : deseos.getProductos()) {      // iteración LIFO
            deseosItemsContainer.getChildren().add(crearFilaDeseo(p));
        }
        actualizarBadgeDeseos();
    }
 
  
    private HBox crearFilaDeseo(Producto p) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(10, 12, 10, 12));
        fila.setStyle(
            "-fx-background-color: #1E1A2E;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(188,127,21,0.18);" +
            "-fx-border-radius: 10;"
        );
 
        Label emoji = new Label(emojiParaProducto(p.getId()));
        emoji.setStyle("-fx-font-size: 26px;");
 
        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label nombre = new Label(p.getNombre());
        nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #E9E9ED;");
        Label precio = new Label("$" + String.format("%.2f", p.getPrecio()));
        precio.setStyle("-fx-font-size: 12px; -fx-text-fill: #BC7F15; -fx-font-weight: bold;");
        info.getChildren().addAll(nombre, precio);
 
        // Botón "Al Carrito"
        Button btnCart = new Button("🛒 Al Carrito");
        btnCart.setStyle(
            "-fx-background-color: #564AB5;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 10 6 10;"
        );
        btnCart.setOnAction(e -> {
            agregarAlCarrito(p);
            deseos.quitar(p.getId());
            actualizarVistaDeseos();
            actualizarVistaCarrito();
        });
 
       
        Button btnQuitar = new Button("✕");
        btnQuitar.setStyle(
            "-fx-background-color: rgba(255,107,107,0.12);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: #FF6B6B;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 9 5 9;" +
            "-fx-border-color: rgba(255,107,107,0.22);" +
            "-fx-border-radius: 8;"
        );
        btnQuitar.setOnAction(e -> {
            deseos.quitar(p.getId());
            actualizarVistaDeseos();
        });
 
        fila.getChildren().addAll(emoji, info, btnCart, btnQuitar);
        return fila;
    }
 
    private void actualizarBadgeDeseos() {
        int n = deseos.size();
        if (navBadgeDeseos != null) {
            navBadgeDeseos.setText(n > 0 ? String.valueOf(n) : "");
            navBadgeDeseos.setVisible(n > 0);
            navBadgeDeseos.setManaged(n > 0);
        }
        navBtnDeseos.setText(n > 0 ? "  ♡   Deseos (" + n + ")" : "  ♡   Deseos");
    }
 
    
    @FXML
    private void handleEditarPerfil() {
        modoEdicion = !modoEdicion;
        seccionEditar.setVisible(modoEdicion);
        seccionEditar.setManaged(modoEdicion);
        btnEditarPerfil.setText(modoEdicion ? "✕  Cancelar" : "✏   Editar");
        if (!modoEdicion) mostrarFeedbackPerfil("", "");
    }
 
    @FXML
    private void handleGuardarPerfil() {
        String nombre      = txtEditNombre.getText().trim();
        String instrumento = txtEditInstrumento.getText().trim();
        String presupStr   = txtEditPresupuesto.getText().trim();
 
        if (nombre.isEmpty()) {
            mostrarFeedbackPerfil("⚠ El nombre no puede estar vacío.", FB_ERROR); return;
        }
        double presup;
        try {
            presup = Double.parseDouble(presupStr);
            if (presup < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarFeedbackPerfil("⚠ Presupuesto inválido.", FB_ERROR); return;
        }
 
        Usuario u = UserStore.getUsuarioActivo();
        // Actualización refleja en labels de display (UserStore no persiste nombre aún)
        lblPerfilNombre.setText(nombre);
        lblSidebarNombre.setText(primerNombre(nombre));
        lblGreeting.setText("Hola, " + primerNombre(nombre) + " 👋");
        if (u.getPerfil() != null) {
            lblInstrumento.setText(instrumento.isBlank() ? "Sin definir" : instrumento);
            lblPresupuesto.setText(String.format("$%.2f", presup));
        }
        mostrarFeedbackPerfil("✓ Perfil actualizado.", FB_OK);
    }
 
    @FXML
    private void handleCambiarFoto() {
        mostrarFeedbackPerfil("📷 Funcionalidad de foto próximamente.", FB_OK);
    }
 
    private void mostrarFeedbackPerfil(String msg, String estilo) {
        lblPerfilFeedback.setText(msg);
        lblPerfilFeedback.setStyle(estilo);
    }
 
    
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
 
   
    private void cargarProductosEjemplo() {
        for (Producto p : catalogoDemo) {
            agregarProductoCard(p);
        }
    }
 
    
    private void agregarProductoCard(Producto producto) {
        String emoji = emojiParaProducto(producto.getId());
 
        
        VBox card = new VBox();
        card.setPrefWidth(198); card.setMaxWidth(198);
        card.setStyle(
            "-fx-background-color: #1E1A2E;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: rgba(86,74,181,0.22);" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);"
        );
 
        
        StackPane imgArea = new StackPane();
        imgArea.setPrefHeight(130); imgArea.setPrefWidth(198);
        imgArea.setStyle("-fx-background-color: #272239; -fx-background-radius: 12 12 0 0;");
 
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 46px;");
 
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
 
        
        btnWish.setOnAction(e -> agregarADeseos(producto, btnWish));
 
        imgArea.getChildren().addAll(emojiLbl, btnWish);
 
        
        VBox info = new VBox(6);
        info.setStyle("-fx-padding: 12 12 14 12;");
 
        Label lNombre = new Label(producto.getNombre());
        lNombre.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-text-fill: #E9E9ED;");
        lNombre.setMaxWidth(174); lNombre.setWrapText(true);
 
        Label lCat = new Label(producto.getDescripcion().length() > 30
            ? producto.getDescripcion().substring(0, 30) + "…"
            : producto.getDescripcion());
        lCat.setStyle("-fx-font-size: 10px; -fx-text-fill: #8F8AA8;");
 
        Label lPrecio = new Label(String.format("$%.0f", producto.getPrecio()));
        lPrecio.setStyle(
            "-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #BC7F15;");
 
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
 
        
        btnAdd.setOnAction(e -> agregarAlCarrito(producto));
 
        info.getChildren().addAll(lNombre, lCat, lPrecio, btnAdd);
        card.getChildren().addAll(imgArea, info);
 
        
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #272239; -fx-background-radius: 12;" +
            "-fx-border-color: rgba(86,74,181,0.5); -fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(86,74,181,0.2),20,0,0,6);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: #1E1A2E; -fx-background-radius: 12;" +
            "-fx-border-color: rgba(86,74,181,0.22); -fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),12,0,0,4);"
        ));
 
        productosGrid.getChildren().add(card);
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
 
  
    private static String emojiParaProducto(int id) {
        return switch (id) {
            case 101 -> "🎸";
            case 102 -> "🎹";
            case 103 -> "🥁";
            case 104 -> "🎻";
            default  -> "🎵";
        };
    }
}
