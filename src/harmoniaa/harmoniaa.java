/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package harmoniaa;

/**
 *
 * @author CARLOS ANAYA
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class harmoniaa extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("harmoniaa.fxml"));
        primaryStage.setTitle("Harmonia");

        // Tamaño inicial de la ventana
        primaryStage.setScene(new Scene(root, 980, 660));

        // Tamaño mínimo para que no se deforme
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(520);

        // Ícono en la barra superior y taskbar
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logoicono.png")));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
