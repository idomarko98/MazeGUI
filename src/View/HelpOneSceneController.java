package View;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpOneSceneController implements Initializable {

    public javafx.scene.image.ImageView image_view_help_sign;
    public javafx.scene.control.Button btn_help_screen_two;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Image image = new Image(new FileInputStream("resources/images/help sign.png"));
            image_view_help_sign.setImage(image);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void openHelpScreenTwo(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("HelpTwoScene.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.show();
            Stage currentStage = (Stage) btn_help_screen_two.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
