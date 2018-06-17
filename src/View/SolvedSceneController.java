package View;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class SolvedSceneController implements Initializable {

    public javafx.scene.image.ImageView img_mario_and_peach;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Image image = new Image(this.getClass().getResourceAsStream("/images/mario and peach.png"));
            img_mario_and_peach.setImage(image);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
