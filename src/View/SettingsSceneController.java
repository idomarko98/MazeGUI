package View;

import Model.MyModel;
import Server.Server;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.SimpleMazeGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
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
            //Image image = new Image(new FileInputStream("resources/images/mario settings image.jpg"));
            Image image = new Image(this.getClass().getResourceAsStream("/images/mario settings image.jpg"));

            imageViewMario.setImage(image);
            imageViewMario.fitHeightProperty().bind(anchorPane.heightProperty().divide(1.8));
            imageViewMario.fitWidthProperty().bind(anchorPane.widthProperty().divide(1.5));
            imageViewMario.xProperty().bind(anchorPane.widthProperty().divide(20));
            imageViewMario.yProperty().bind(anchorPane.heightProperty().divide(20));

            RadioButtonBestAlgorithm.setToggleGroup(generatingAlgorithmGroup);
            RadioButtonBestAlgorithm.setUserData("MyMazeGenerator");
            RadioButtonSimpleAlgorithm.setToggleGroup(generatingAlgorithmGroup);
            RadioButtonSimpleAlgorithm.setUserData("SimpleMazeGenerator");
            IMazeGenerator currGenerateAlgorithm = MyViewModel.getConfigurationGeneratingAlgorithm();
            if(currGenerateAlgorithm instanceof MyMazeGenerator)
                RadioButtonBestAlgorithm.setSelected(true);
            else if(currGenerateAlgorithm instanceof SimpleMazeGenerator)
                RadioButtonSimpleAlgorithm.setSelected(true);

            RadioButtonBreadthFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonBreadthFirstSearch.setUserData("BreadthFirstSearch");
            RadioButtonDepthFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonDepthFirstSearch.setUserData("DepthFirstSearch");
            RadioButtonBestFirstSearch.setToggleGroup(solvingAlgorithmGroup);
            RadioButtonBestFirstSearch.setUserData("BestFirstSearch");
            String currSearchingAlgorithm = MyViewModel.getConfigurationSolvingAlgorithmName();
            for (Toggle rb: solvingAlgorithmGroup.getToggles()) {
                if(rb.getUserData().toString().equals(currSearchingAlgorithm))
                    rb.setSelected(true);
            }

            int currAmountOfThreads = MyViewModel.getConfigurationNumberOfThreads();
            textFieldNumOfThreads.setText(currAmountOfThreads + "");
            //RadioButtonBreadthFirstSearch.setSelected(true);

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
        } catch (Exception e) {
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
            MyViewModel.setConfigurations(numOfThreads, selectedGeneratingAlgorithm, selectedSolvingAlgorithm);
            //Stage currentStage = (Stage) ButtonSaveSettings.getScene().getWindow();
            //currentStage.close();
            //openStartPage();
            showAlert("Settings have been changed successfully. Going back to main menu");
        }
    }

    private void openStartPage() {
        try {
            Stage primaryStage = new Stage();
            MyModel model = new MyModel();
            //model.startServers();
            MyViewModel viewModel = new MyViewModel(model);
            //model.addObserver(viewModel);
            //--------------
            primaryStage.setTitle("A-maze-ing");
            FXMLLoader fxmlLoader = new FXMLLoader();
            //Parent root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
            Parent root = fxmlLoader.load(getClass().getResource("StartScene.fxml").openStream());
            Scene scene = new Scene(root, 407, 400);
            /////
            //scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            primaryStage.setScene(scene);
            //--------------
            //MyViewController view = fxmlLoader.getController();
            StartSceneController view = fxmlLoader.getController();
            view.setResizeEvent(scene);
            //view.setViewModel(viewModel);
            //viewModel.addObserver(view);
            //--------------
            Stage currentStage = (Stage) ButtonSaveSettings.getScene().getWindow();
            currentStage.close();
            SetStageCloseEvent(primaryStage);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    System.exit(0);
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.setOnCloseRequest(event -> openStartPage());
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
        //Stage currentStage = (Stage) ButtonSaveSettings.getScene().getWindow();
        //currentStage.close();
        //openStartPage();
        showAlert("Settings not saved. Going back to main menu");
    }
}
