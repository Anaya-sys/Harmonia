package harmoniaa;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Controller {
    @FXML private VBox      cardlogin;
    @FXML private Button    btnIniciarTab;
    @FXML private Button    btnCrearTab;
    @FXML private Label     lblNombre;
    @FXML private TextField txtNombre;
    @FXML private Label     lblId;
    @FXML private TextField txtId;
    @FXML private Label     lblEmail;
    @FXML private TextField emailField;
    @FXML private Label     lblPassword;
    @FXML private TextField txtPassword;
    @FXML private Button    authActionButton;
    @FXML private Label     label;
 
    private enum Tab { LOGIN, REGISTER }
    private Tab tabActual = Tab.LOGIN;
 
    private static final String TAB_ACTIVO =
            "-fx-background-color: #564AB5;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(86,74,181,0.35), 10, 0, 0, 4);";
 
    private static final String TAB_INACTIVO =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #8F8AA8;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;";
 
    private static final String FEEDBACK_ERROR =
            "-fx-text-fill: #FF6B6B; -fx-font-size: 12px; -fx-text-alignment: center;";
 
    private static final String FEEDBACK_OK =
            "-fx-text-fill: #6BFF9E; -fx-font-size: 12px; -fx-text-alignment: center;";
 
    private static final String FEEDBACK_INFO =
            "-fx-text-fill: #FFD86B; -fx-font-size: 12px; -fx-text-alignment: center;";
 

 
    @FXML
    public void initialize() {
        aplicarEstiloTab(btnIniciarTab, TAB_ACTIVO);
        aplicarEstiloTab(btnCrearTab,   TAB_INACTIVO);
        limpiarFeedback();
    }
 
   
 
    @FXML
    private void switchToLogin() {
        if (tabActual == Tab.LOGIN) return;
        tabActual = Tab.LOGIN;
        animarCambioTabs(btnIniciarTab, btnCrearTab);
        mostrarCamposRegistro(false);
        authActionButton.setText("Iniciar Sesión");
        limpiarCampos();
        limpiarFeedback();
    }
 
    @FXML
    private void switchToRegister() {
        if (tabActual == Tab.REGISTER) return;
        tabActual = Tab.REGISTER;
        animarCambioTabs(btnCrearTab, btnIniciarTab);
        mostrarCamposRegistro(true);
        authActionButton.setText("Crear Cuenta");
        limpiarCampos();
        limpiarFeedback();
    }
 
    @FXML
    private void handleAuthAction() {
        if (tabActual == Tab.LOGIN) manejarLogin();
        else                        manejarRegistro();
    }
 
   
 
    private void manejarLogin() {
        String correo   = emailField.getText().trim();
        String password = txtPassword.getText().trim();
 
        if (correo.isEmpty() || password.isEmpty()) {
            mostrarFeedback("⚠  Completa todos los campos.", FEEDBACK_ERROR);
            return;
        }
        if (!correo.contains("@")) {
            mostrarFeedback("⚠  Ingresa un correo válido.", FEEDBACK_ERROR);
            return;
        }
 
        UserStore.LoginResultado resultado = UserStore.intentarLogin(correo, password);
 
        switch (resultado) {
            case OK -> navegarAlDashboard();
            case EMAIL_NO_EXISTE ->
                mostrarFeedback("⚠  Correo no registrado. ¿Quieres crear una cuenta?", FEEDBACK_INFO);
            case CONTRASENA_INCORRECTA ->
                mostrarFeedback("⚠  Contraseña incorrecta.", FEEDBACK_ERROR);
        }
    }
 
  
    private void manejarRegistro() {
        String nombre   = txtNombre.getText().trim();
        String idStr    = txtId.getText().trim();
        String correo   = emailField.getText().trim();
        String password = txtPassword.getText().trim();
 
        if (nombre.isEmpty() || idStr.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            mostrarFeedback("⚠  Completa todos los campos.", FEEDBACK_ERROR);
            return;
        }
        if (!correo.contains("@")) {
            mostrarFeedback("⚠  Ingresa un correo válido.", FEEDBACK_ERROR);
            return;
        }
        if (password.length() < 6) {
            mostrarFeedback("⚠  La contraseña debe tener mínimo 6 caracteres.", FEEDBACK_ERROR);
            return;
        }
        if (UserStore.buscarPorEmail(correo) != null) {
            mostrarFeedback("⚠  Ese correo ya está registrado.", FEEDBACK_INFO);
            return;
        }
 
        int nuevoId = UserStore.siguienteId();
        PerfilUsuario perfil = new PerfilUsuario(TipoPerfil.ESTUDIANTE, "Sin definir");
        Usuario nuevo = new Usuario(nuevoId, nombre, correo, password, perfil, Rol.COMPRADOR);
 
        if (UserStore.registrar(nuevo)) {
            mostrarFeedback("✓  Cuenta creada. Ingresando...", FEEDBACK_OK);
             PauseTransition pause = new PauseTransition(Duration.millis(700));
             pause.setOnFinished(e -> navegarAlDashboard());
             pause.play();
        } else {
            mostrarFeedback("⚠  No se pudo crear la cuenta.", FEEDBACK_ERROR);
        }
    }
 

 
    private void navegarAlDashboard() {
       try {
        Usuario activo = UserStore.getUsuarioActivo();
        boolean esAdmin = activo != null && activo.getRol() == Rol.ADMIN;

        String fxmlArchivo = esAdmin ? "dashboardAdmin.fxml" : "dashboard.fxml";
        String titulo      = esAdmin ? "Harmonia – Administración" : "Harmonia – Dashboard";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlArchivo));
        Parent root = loader.load();
        Stage stage = (Stage) authActionButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
    } catch (Exception e) {
        mostrarFeedback("⚠  Error al cargar el dashboard.", FEEDBACK_ERROR);
        e.printStackTrace();
    }
    }
 
    
 
    private void animarCambioTabs(Button btnActivar, Button btnDesactivar) {
        aplicarEstiloTab(btnActivar, TAB_ACTIVO);
 
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(180), btnActivar);
        scaleIn.setFromX(0.92); scaleIn.setFromY(0.92);
        scaleIn.setToX(1.0);   scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);
 
        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), btnActivar);
        fadeIn.setFromValue(0.5); fadeIn.setToValue(1.0);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);
 
        new ParallelTransition(scaleIn, fadeIn).play();
 
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(130), btnDesactivar);
        scaleOut.setFromX(1.0); scaleOut.setFromY(1.0);
        scaleOut.setToX(0.95);  scaleOut.setToY(0.95);
        scaleOut.setAutoReverse(true); scaleOut.setCycleCount(2);
        scaleOut.setInterpolator(Interpolator.EASE_BOTH);
 
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), btnDesactivar);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.7);
        fadeOut.setAutoReverse(true); fadeOut.setCycleCount(2);
 
        ParallelTransition desactivar = new ParallelTransition(scaleOut, fadeOut);
        desactivar.setOnFinished(e -> aplicarEstiloTab(btnDesactivar, TAB_INACTIVO));
        desactivar.play();
    }
 
    private void mostrarCamposRegistro(boolean visible) {
        lblNombre.setVisible(visible); lblNombre.setManaged(visible);
        txtNombre.setVisible(visible); txtNombre.setManaged(visible);
        lblId.setVisible(visible);     lblId.setManaged(visible);
        txtId.setVisible(visible);     txtId.setManaged(visible);
    }
 
    private void mostrarFeedback(String msg, String estilo) {
        label.setText(msg);
        label.setStyle(estilo);
    }
 
    private void limpiarFeedback() {
        label.setText("");
        label.setStyle("");
    }
 
    private void limpiarCampos() {
        txtNombre.clear(); txtId.clear();
        emailField.clear(); txtPassword.clear();
    }
 
    private void aplicarEstiloTab(Button btn, String estilo) {
        btn.setStyle(estilo);
    }
}