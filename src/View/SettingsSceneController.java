package View;

import Server.Server;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsSceneController implements Initializable {
    public javafx.scene.control.Button ButtonSaveSettings;
    public javafx.scene.control.Button ButtonCancel;
    public javafx.scene.image.ImageView imageViewMario;
    public javafx.scene.control.TextField textFieldNumOfThreads;
    public javafx.scene.control.Label labelAmountOfThreadsInServer;
    public javafx.scene.control.Label labelSolvingAlgorithm;
    public javafx.scene.control.Label labelGeneratingAlgorithm;
    public javafx.scene.control.Label labelSettings;
    public javafx.scene.control.RadioButton RadioButtonBestAlgorithm;
    public javafx.scene.control.RadioButton RadioButtonSimpleAlgorithm;
    public javafx.scene.control.RadioButton RadioButtonBreadthFirstSearch;
    public javafx.scene.control.RadioButton RadioButtonDepthFirstSearch;
    public javafx.scene.control.RadioButton RadioButtonBestFirstSearch;
    private ToggleGroup generatingAlgorithmGroup;
    private ToggleGroup solvingAlgorithmGroup;
    public javafx.scene.layout.AnchorPane anchorPane;

    public SettingsSceneController(){
        generatingAlgorithmGroup = new ToggleGroup();
        solvingAlgorithmGroup = new ToggleGroup();
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("Width: " + newSceneWidth);
                //btn_start.setLayoutX(anchorPane.getWidth()/3);

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //System.out.println("Height: " + newSceneHeight);
                //btn_start.setLayoutY(anchorPane.getHeight()/10);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image image = new Image(new FileInputStream("resources/images/mario settings image.jpg"));

            imageViewMario.setImage(image);
            imageViewMario.fitHeightProperty().bind(anchorPane.heightProperty().divide(1.8));
            imageViewMario.fitWidthProperty().bind(anchorPane.widthProperty().divide(1.5));
            imageViewMario.xProperty().bind(anchorPane.widthProperty().divide(20));
            imageViewMario.yProperty().bind(anchorPane.heightProperty().divide(20));

            RadioButtonBestAlgorithm.setToggleGroup(generatingAlgorithmGroup);
            RadioButtonBestAlgorithm.setUserData("MyMazeGenerator");
            RadioButtonSimpleAlgorithm.setToggleGroup(generatingAlgorithmGroup);
            RadioButtonSimpleAlgorithm.setUserData("SimpleMazeGenerator");
            RadioButtonBestAlgorithm.setSelected(true);

            RadioButtonBreadthFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonBreadthFirstSearch.setUserData("BreadthFirstSearch");
            RadioButtonDepthFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonDepthFirstSearch.setUserData("DepthFirstSearch");
            RadioButtonBestFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonBestFirstSearch.setUserData("BestFirstSearch");
            RadioButtonBreadthFirstSearch.setSelected(true);

            //imageViewMario.layoutXProperty().bind(anchorPane.widthProperty().divide(2));
            //imageViewMario.layoutYProperty().bind(anchorPane.heightProperty().divide(8));

            //BackgroundImage background = new BackgroundImage(backgroundImage,BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            //BackgroundSize.DEFAULT);
            //anchorPane.setBackground(new Background(background));
            /*
            imageView_settings.setImage(settings);
            imageView_background.fitHeightProperty().bind(anchorPane.heightProperty());
            imageView_background.fitWidthProperty().bind(anchorPane.widthProperty());
            imageView_settings.fitWidthProperty().bind(anchorPane.widthProperty().divide(8));
            imageView_settings.fitHeightProperty().bind(anchorPane.heightProperty().divide(8));
            btn_start.prefHeightProperty().bind(anchorPane.heightProperty().divide(10));
            btn_start.prefWidthProperty().bind(anchorPane.widthProperty().divide(3));
            //btn_start.layoutXProperty().bind(anchorPane.layoutXProperty().divide(2));
            //btn_start.layoutYProperty().bind(anchorPane.layoutYProperty().divide(5));
            //btn_start.setLayoutX(anchorPane.getWidth()/2);
            //btn_start.setLayoutY(anchorPane.getHeight()/10);

            textSize.bind(btn_start.heightProperty().divide(2));
            */
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void SaveSettingsClicked(ActionEvent actionEvent) {
        String selectedGeneratingAlgorithm = generatingAlgorithmGroup.getSelectedToggle().getUserData().toString();
        String selectedSolvingAlgorithm = solvingAlgorithmGroup.getSelectedToggle().getUserData().toString();
        String amountOfThreadsWanted = textFieldNumOfThreads.getText();
        boolean validText = true;
        int numOfThreads = 4;
        try {
            numOfThreads = Integer.parseInt(amountOfThreadsWanted);
            if(numOfThreads < 1 || numOfThreads > 20) {
                validText = false;
                showAlert("Amount of threads must be minimum 1 and maximum 20");
            }
        }
        catch (Exception e){
            validText = false;
            showAlert("Amount of threads must be an Integer");
        }

        if(validText){
            Server.Configurations.setProp(numOfThreads, selectedGeneratingAlgorithm, selectedSolvingAlgorithm);
            showAlert("Settings has been changed successfully :)");
        }
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void EnterPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            SaveSettingsClicked(new ActionEvent());
        }
        else if(keyEvent.getCode().equals(KeyCode.ESCAPE)){
            CancelSettingsClicked(new ActionEvent());
        }
    }

    public void CancelSettingsClicked(ActionEvent actionEvent) {

    }
}
