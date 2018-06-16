package View;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutSceneController implements Initializable {
    public javafx.scene.image.ImageView image_view_about;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Image image = new Image(new FileInputStream("resources/images/about Image.jpg"));
            image_view_about.setImage(image);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
