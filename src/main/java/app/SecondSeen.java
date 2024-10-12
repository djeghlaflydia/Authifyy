package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecondSeen extends Application {

    @FXML
    private TextField userNameSignUp;
    @FXML
    private TextField EmailSignUp;
    @FXML
    private PasswordField PassWordSignUp;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainSeen.class.getResource("SecondSeen.fxml"));
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

    public void logInPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainSeen.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }



    @FXML
    public void SignUp(ActionEvent event) {
        String name = userNameSignUp.getText().trim();
        String email = EmailSignUp.getText().trim();
        String pass = PassWordSignUp.getText().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please fill in all fields.");
            alert.show();
            return;
        }

        if (!isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please enter a valid email address.");
            alert.show();
            return;
        }

        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx", "root", "");
            psCheckExists = connection.prepareStatement("SELECT * FROM client WHERE name = ?");
            psCheckExists.setString(1, name);
            resultSet = psCheckExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("Username already exists");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username!");
                alert.show();
                userNameSignUp.clear();
                EmailSignUp.clear();
                PassWordSignUp.clear();
            } else {
                String hashedPass = hashPassword(pass);
                psInsert = connection.prepareStatement("INSERT INTO client (name, email, pass) VALUES (?, ?, ?)");
                psInsert.setString(1, name);
                psInsert.setString(2, email);
                psInsert.setString(3, hashedPass);
                psInsert.executeUpdate();

                System.out.println("Username has been inserted");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Successfully signed up!");
                alert.show();

                userNameSignUp.clear();
                EmailSignUp.clear();
                PassWordSignUp.clear();

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                enterPage(currentStage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (psCheckExists != null) psCheckExists.close();
                if (psInsert != null) psInsert.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // Méthode utilitaire pour vérifier le format de l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public static void main(String[] args) {
        launch();
    }
}
