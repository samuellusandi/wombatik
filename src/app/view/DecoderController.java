package app.view;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import app.model.ImageDecoder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class DecoderController {
    
    @FXML
    private Label fileSourceLabel;
    
    @FXML
    private MenuButton algorithm;
    
    @FXML
    private TextField hash;
    
    private Stage decoderStage;
    private File source;
    
    @FXML
    private void initialize() {
        this.fileSourceLabel.setText("");
        MenuItem md2 = new MenuItem("MD2");
        MenuItem md5 = new MenuItem("MD5");
        MenuItem sha1 = new MenuItem("SHA-1");
        MenuItem sha256 = new MenuItem("SHA-256");
        MenuItem sha384 = new MenuItem("SHA-384");
        MenuItem sha512 = new MenuItem("SHA-512");
        this.algorithm.getItems().addAll(md2, md5, sha1, sha256, sha384, sha512);
        for (MenuItem item : this.algorithm.getItems()) {
            item.setOnAction(a -> {
                this.algorithm.setText(item.getText());
            });
        }
    }
    
    @FXML
    private void chooseSource() {
        FileChooser source = new FileChooser();
        source.setTitle("Select source...");
        source.setInitialDirectory(new File(System.getProperty("user.home")));
        
        ExtensionFilter bmp = new FileChooser.ExtensionFilter("BMP Images (*.bmp)", "*.bmp");
        ExtensionFilter png = new FileChooser.ExtensionFilter("PNG Images (*.png)", "*.png");
        
        source.getExtensionFilters().addAll(bmp, png);
        
        File selected = source.showOpenDialog(new Stage());
        if (selected != null) {
            this.fileSourceLabel.setText(selected.getName());
            this.source = selected;
        }
    }
    
    @FXML
    private void handleOK() {
        if (this.source == null || this.algorithm.getText().equals("Select Algorithm...") || this.hash.getText().isEmpty()) {
            this.showError("Please select source file, hash algorithm, and destination file correctly (do not leave any of them empty)");
        } else {
            try {
                ImageDecoder decoder = new ImageDecoder(ImageIO.read(this.source));
                boolean res = decoder.decode_v2(this.hash.getText(), this.algorithm.getText());
                if (!res) 
                    this.notAuthorized();
                else                
                    this.authorized();
            } catch (IOException e) {
                this.showError("Failed to read source image, either the image is non-existent or you don't have permission to read it");
            }
            
        }
    }
    
    @FXML
    private void handleCancel() {
        this.decoderStage.close();
    }
    
    public void setStage(Stage stage) {
        this.decoderStage = stage;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("warning.png"));
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.showAndWait();
    }
    
    private void notAuthorized() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("warning.png"));
        alert.setTitle("Error");
        alert.setHeaderText("You don't have the creative rights of this content");
        String res = "";
        res += "There are several reasons for this : " + "\n\n";
        res += "1. The image is tampered," + "\n";
        res += "2. Hash is mistyped," + "\n";
        res += "3. Indeed, you don't have any rights on this content.";
        alert.setContentText(res);
        
        alert.showAndWait();
    }
    
    private void authorized() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("info.png"));
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Success! The image is not tampered and you have creative right(s) on this content!");
        
        alert.showAndWait();
    }
}
