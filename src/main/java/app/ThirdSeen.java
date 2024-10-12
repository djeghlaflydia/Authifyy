package app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;

import com.google.zxing.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;


public class ThirdSeen extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(MainSeen.class.getResource("ThirdSeen.fxml"));
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

    public void LogOut(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainSeen.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        String css = this.getClass().getResource("StyleSeen.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }




    // fonctionnemnt
    @FXML
    private Label labelImage;
    @FXML
    private ImageView ImageQR;
    @FXML
    private TextField FirstNameQR;
    @FXML
    private TextField LastNameQR;
    @FXML
    private TextField BacYearQR;
    @FXML
    private TextField SpecialityQR;
    private int i=0;
    @FXML
    private Label codeGenerator;



    public void createQR() {
        labelImage.setVisible(false);

        String firstName = FirstNameQR.getText();
        String lastName = LastNameQR.getText();
        String bacYear = BacYearQR.getText();
        String speciality = SpecialityQR.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || bacYear.isEmpty() || speciality.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please fill in all fields.");
            alert.show();
            return;
        }

        String qrContent = String.format("FirstName: %s\nLastName: %s\nBacYear: %s\nSpeciality: %s",
                firstName, lastName, bacYear, speciality);

        Path outputPath = null;
        try {
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 256, 256);

            String fileName = String.format("QR_%d.png", System.currentTimeMillis());
            outputPath = Paths.get("src/main/resources/app/Pic/" + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);

            // Load the saved QR code into ImageView
            InputStream qrInputStream = Files.newInputStream(outputPath);
            Image qrImage = new Image(qrInputStream);
            ImageQR.setImage(qrImage);

        } catch (Exception ex) {
            Logger.getLogger(ThirdSeen.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Code generation
        String lastNameCode = lastName.length() >= 2 ? lastName.substring(0, 2) : lastName;
        String bacYearCode = bacYear.length() >= 2 ? bacYear.substring(bacYear.length() - 2) : bacYear;
        String specialityCode;

        if (speciality.contains(" ")) {
            StringBuilder initials = new StringBuilder();
            for (String word : speciality.split(" ")) {
                if (!word.isEmpty()) {
                    initials.append(word.charAt(0));
                }
            }
            specialityCode = initials.toString();
        } else {
            specialityCode = speciality.length() >= 2 ? speciality.substring(0, 2) : speciality;
        }

        String generatedCode = lastNameCode + bacYearCode + specialityCode;
        codeGenerator.setText(generatedCode);

        // Database insertion
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx", "root", "");
             PreparedStatement psInsert = connection.prepareStatement(
                     "INSERT INTO info (LastName, FirstName, BacYear, Speciality, Code, QRCode) VALUES (?, ?, ?, ?, ?, ?)")) {

            // Set values for each column
            psInsert.setString(1, lastName);
            psInsert.setString(2, firstName);
            psInsert.setString(3, bacYear);
            psInsert.setString(4, speciality);
            // Store generated code
            psInsert.setString(5, generatedCode);
            // Read QR code image file as a binary stream
            InputStream qrFileStream = Files.newInputStream(outputPath);
            psInsert.setBinaryStream(6, qrFileStream, (int) Files.size(outputPath));

            // Execute the insert
            psInsert.executeUpdate();
            qrFileStream.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Information successfully saved!");
            alert.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }



    public void addQR() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources/app/Pic/"));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try (FileInputStream inputStream = new FileInputStream(selectedFile)) {
                Image image = new Image(inputStream);
                ImageQR.setImage(image);
                labelImage.setVisible(false);

                // Lire l'image et déchiffrer le QR code
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                MultiFormatReader reader = new MultiFormatReader();
                Result result = reader.decode(bitmap);

                // Extraire les informations du QR code
                String qrContent = result.getText();

                // Décomposer le contenu en lignes
                String[] lines = qrContent.split("\n");
                String firstName = "", lastName = "", bacYear = "", speciality = "";

                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        // Mettre à jour les champs en fonction des clés
                        switch (key) {
                            case "FirstName":
                                firstName = value;
                                FirstNameQR.setText(value);
                                break;
                            case "LastName":
                                lastName = value;
                                LastNameQR.setText(value);
                                break;
                            case "BacYear":
                                bacYear = value;
                                BacYearQR.setText(value);
                                break;
                            case "Speciality":
                                speciality = value;
                                SpecialityQR.setText(value);
                                break;
                        }
                    }
                }

                // Vérifier si l'information existe dans la base de données
                String generatedCode = checkCodeInDatabase(lastName, firstName, bacYear, speciality);
                if (generatedCode != null) {
                    // Afficher le code si trouvé
                    codeGenerator.setText(generatedCode);
                } else {
                    // Afficher un message d'information si aucune correspondance trouvée
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("No matching information found in the database.");
                    alert.show();
                }

            } catch (IOException | ReaderException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour vérifier le code dans la base de données
    private String checkCodeInDatabase(String lastName, String firstName, String bacYear, String speciality) {
        String generatedCode = null;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx", "root", "");
             PreparedStatement psSelect = connection.prepareStatement(
                     "SELECT Code FROM info WHERE LastName = ? AND FirstName = ? AND BacYear = ? AND Speciality = ?")) {

            // Set values for each parameter
            psSelect.setString(1, lastName);
            psSelect.setString(2, firstName);
            psSelect.setString(3, bacYear);
            psSelect.setString(4, speciality);

            ResultSet resultSet = psSelect.executeQuery();
            if (resultSet.next()) {
                generatedCode = resultSet.getString("Code");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedCode; 
    }

    public static void main(String[] args) {
        launch();
    }

}
