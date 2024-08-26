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

    public void createQR() {
        labelImage.setVisible(false);

        String firstName = FirstNameQR.getText();
        String lastName = LastNameQR.getText();
        String bacYear = BacYearQR.getText();
        String speciality = SpecialityQR.getText();

        // Combine all input fields into a single string
        String qrContent = String.format("FirstName: %s\nLastName: %s\nBacYear: %s\nSpeciality: %s",
                firstName, lastName, bacYear, speciality);

        try {
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 256, 256);

            /* Save QR code to file
            String fileName = String.format("QR_%d.png", i);
            Path outputPath = Paths.get("src/main/resources/app/Pic/" + fileName);
            i++;*/
            // Create a unique file name using timestamp
            String fileName = String.format("QR_%d.png", System.currentTimeMillis());
            Path outputPath = Paths.get("src/main/resources/app/Pic/" + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);

            // Load the saved QR code into ImageView
            InputStream qrInputStream = Files.newInputStream(outputPath);
            Image qrImage = new Image(qrInputStream);
            ImageQR.setImage(qrImage);

        } catch (Exception ex) {
            Logger.getLogger(ThirdSeen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public void addQR() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources/app/Pic"));
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
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        // Mettre à jour les champs en fonction des clés
                        switch (key) {
                            case "FirstName":
                                FirstNameQR.setText(value);
                                break;
                            case "LastName":
                                LastNameQR.setText(value);
                                break;
                            case "BacYear":
                                BacYearQR.setText(value);
                                break;
                            case "Speciality":
                                SpecialityQR.setText(value);
                                break;
                        }
                    }
                }

            } catch (IOException | ReaderException e) {
                e.printStackTrace();
            }
        }
    }




    public static void main(String[] args) {
        launch();
    }

}
