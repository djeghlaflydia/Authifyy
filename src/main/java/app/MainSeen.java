package app;

import com.almasb.fxgl.net.Connection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class MainSeen extends Application {
    private Connection connection;
    private Statement statement;


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(MainSeen.class.getResource("MainSeen.fxml"));
        Parent root = loader.load();
        stage.setTitle("Authify");

        // Set the application icon
        Image icon = new Image(getClass().getResourceAsStream("/app/Pic/icon.png"));
        stage.getIcons().add(icon);

        Scene scene = new Scene(root);
        String css = this.getClass().getResource("StyleSeen.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    private Stage stage;
    private Scene scene;
    private Parent root;

    public void SignUpPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("SecondSeen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = this.getClass().getResource("StyleSeen.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void enterPage(Stage stage) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ThirdSeen.fxml"));
        scene = new Scene(root);
        String css = this.getClass().getResource("StyleSeen.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }




    @FXML
    private TextField userNameLogIn;
    @FXML
    private PasswordField PassWordLogIn;

    @FXML
    public void LogIn(ActionEvent event) {
        String name = userNameLogIn.getText();
        String pass = PassWordLogIn.getText();

        java.sql.Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx", "root", "12012005");
            preparedStatement = connection.prepareStatement("SELECT pass FROM client WHERE name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Utilisateur trouvé, vérifier le mot de passe
                String retrievedPass = resultSet.getString("pass");

                if (retrievedPass.equals(pass)) {
                    System.out.println("Username found in the database (you are logged in)");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Successfully logged in!");
                    alert.show();

                    userNameLogIn.clear();
                    PassWordLogIn.clear();

                    // Appelle enterPage avec l'instance du stage actuel
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    enterPage(currentStage);
                }
                else {
                    System.out.println("Wrong password");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Wrong password");
                    alert.show();

                    userNameLogIn.clear();
                    PassWordLogIn.clear();
                }
            } else {
                // Utilisateur non trouvé
                System.out.println("Username not found in the database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();

                userNameLogIn.clear();
                PassWordLogIn.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    public static void main(String[] args) {
        launch();
    }
}
