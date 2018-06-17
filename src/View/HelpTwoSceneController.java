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

public class HelpTwoSceneController implements Initializable {

    public javafx.scene.image.ImageView img_mushroom;
    public javafx.scene.image.ImageView img_gomba;
    public javafx.scene.image.ImageView img_tortuga;
    public javafx.scene.image.ImageView img_flag;
    public javafx.scene.image.ImageView img_mario_help;
    public javafx.scene.control.Button btn_help_screen_one;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            //Image imageMushroom = new Image(new FileInputStream("resources/images/Displayed On Maze/mushroom.png"));
            Image imageMushroom = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/mushroom.png"));
            img_mushroom.setImage(imageMushroom);

            //Image imageGomba = new Image(new FileInputStream("resources/images/Enemy Characters/gomba_left_01.png"));
            Image imageGomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_left_01.png"));
            img_gomba.setImage(imageGomba);

            //Image imageTortuga = new Image(new FileInputStream("resources/images/Enemy Characters/tortuga_right_01.png"));
            Image imageTortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_right_01.png"));
            img_tortuga.setImage(imageTortuga);

            //Image imageFlag = new Image(new FileInputStream("resources/images/Displayed On Maze/flag.png"));
            Image imageFlag = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/flag.png"));
            img_flag.setImage(imageFlag);

            //Image imageMarioHelp = new Image(new FileInputStream("resources/images/mario question mark help.png"));
            Image imageMarioHelp = new Image(this.getClass().getResourceAsStream("/images/mario question mark help.png"));
            img_mario_help.setImage(imageMarioHelp);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void openHelpScreenOne(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("HelpOneScene.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.show();
            Stage currentStage = (Stage) btn_help_screen_one.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
