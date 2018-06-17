package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartSceneController implements Initializable{
    @FXML
    public javafx.scene.control.Button btn_start;
    public javafx.scene.image.ImageView imageView_settings;
    public javafx.scene.image.ImageView imageView_background;
    public javafx.scene.layout.AnchorPane anchorPane;
    private DoubleProperty textSize = new SimpleDoubleProperty();
    private int settingsImageNum = 1;
    private Thread threadSettings = null;
    private volatile boolean stopSettings = false;

    //private StringProperty imageView_settings_filename = new SimpleStringProperty();

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("Width: " + newSceneWidth);
                btn_start.setLayoutX(anchorPane.getWidth()/3);
                btn_start.setFont(new Font(btn_start.getFont().getName(),textSize.doubleValue()));
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //System.out.println("Height: " + newSceneHeight);
                btn_start.setLayoutY(anchorPane.getHeight()/10);
                btn_start.setFont(new Font(btn_start.getFont().getName(),textSize.doubleValue()));
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Image backgroundImage = new Image(new FileInputStream("resources/images/logo.jpg"));
            Image settings = new Image(new FileInputStream("resources/images/settings.jpg"));
            imageView_background.setImage(backgroundImage);
            //BackgroundImage background = new BackgroundImage(backgroundImage,BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    //BackgroundSize.DEFAULT);
            //anchorPane.setBackground(new Background(background));
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
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void mouseEnteredImage(){
        stopSettings = false;
        threadSettings = new Thread(()->{
            try {
                while(!stopSettings) {
                    Image settings = new Image(new FileInputStream("resources/images/settings.jpg"));
                    Image settings2 = new Image(new FileInputStream("resources/images/settings2.jpg"));
                    if(settingsImageNum == 1) {
                        imageView_settings.setImage(settings2);
                        settingsImageNum = 0;
                    }
                    else {
                        imageView_settings.setImage(settings);
                        settingsImageNum = 1;
                    }
                    int time = 100;
                    Thread.sleep(time);
                }
            }
            catch (Exception e) {System.out.println(e); }
        });
        threadSettings.start();
    }

    public void mouseExitImage(MouseEvent mouseEvent) {
        stopSettings = true;
    }

    public void openSettingsScene() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Settings");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("SettingsScene.fxml").openStream());
            SettingsSceneController settingsSceneController = fxmlLoader.getController();
            Scene scene = new Scene(root, 600, 400);
            settingsSceneController.setResizeEvent(scene);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void openViewScene(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Amaze maze");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            //stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            double setX = (primScreenBounds.getWidth() - stage.getWidth());
            double setY = (primScreenBounds.getHeight() - stage.getHeight());
            //stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
            //Scene scene = new Scene(root, 700, 400);
            Scene scene = new Scene(root, setX, setY);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);

            MyModel model = new MyModel();
            //model.startServers();
            MyViewModel viewModel = new MyViewModel(model);
            model.addObserver(viewModel);
            MyViewController view = fxmlLoader.getController();
            view.setViewModel(viewModel);
            viewModel.addObserver(view);

            stage.setResizable(false);
            stage.show();
            Stage currentStage = (Stage) btn_start.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
