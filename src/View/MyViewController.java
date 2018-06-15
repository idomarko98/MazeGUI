package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.EventObject;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    private Thread musicThread;
    private MediaPlayer themeMediaPlayer;
    private volatile boolean stopThemeSong = false;
    //private volatile boolean stopLostSound = false;

    public MyMazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.MenuItem menu_item_save;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if(arg instanceof Maze){
                updateMaze((Maze)arg);
            }
            else if(arg instanceof Solution){
                updateSolution((Solution)arg);
            }
            else if(arg instanceof Position){
                updatePosition((Position)arg);
            }
            else if(arg instanceof KeyCode){
                //updateNotAbleToMove((KeyCode)arg);
            }
            //displayMaze(viewModel.getMaze());
            //btn_generateMaze.setDisable(false);
        }
    }

    private void updatePosition(Position arg) {
        characterPositionColumn.set(arg.getColumnIndex() + "");
        characterPositionRow.set(arg.getRowIndex() + "");
        mazeDisplayer.setCharacterPosition(arg.getRowIndex(), arg.getColumnIndex());
    }

    private void updateSolution(Solution arg) {
        mazeDisplayer.setSolution(arg);
        btn_solveMaze.setDisable(false);

    }

    private void updateMaze(Maze arg) {
        displayMaze(arg);
        btn_generateMaze.setDisable(false);
        btn_solveMaze.setDisable(false);
    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void generateMaze() {
        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        mazeDisplayer.removeSolution();
        viewModel.generateMaze(width, heigth);
        if(musicThread != null && musicThread.isAlive()) {
            themeMediaPlayer.stop();
            stopThemeSong = true;
        }
        menu_item_save.setDisable(false);
        playTheme();
    }

    private void playTheme() {
        stopThemeSong = false;
        musicThread = new Thread(()->{
            try {
                while(!stopThemeSong) {
                    String musicFile = "resources/Sounds/theme.mp3";
                    Media sound = new Media(new File(musicFile).toURI().toString());
                    themeMediaPlayer = new MediaPlayer(sound);
                    themeMediaPlayer.play();

                    int time = 219000;
                    Thread.sleep(time);
                }
            }
            catch (Exception e) {System.out.println(e); }
        });
        musicThread.start();
    }

    public void solveMaze(ActionEvent actionEvent) {
        //showAlert("Solving maze..");
        stopThemeSong = true;
        themeMediaPlayer.stop();
        playClickedOnSolveMusic();
        btn_solveMaze.setDisable(false);
        viewModel.solveMaze();
    }

    private void playClickedOnSolveMusic() {
        String musicFile = "resources/Sounds/solve clicked sound.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        /*stopThemeSong = false;
        musicThread = new Thread(()->{
            try {
                while(!stopThemeSong) {
                    String musicFile = "resources/Sounds/solve clicked sound.mp3";
                    Media sound = new Media(new File(musicFile).toURI().toString());
                    themeMediaPlayer = new MediaPlayer(sound);
                    themeMediaPlayer.play();

                    int time = 219000;
                    Thread.sleep(time);
                }
            }
            catch (Exception e) {System.out.println(e); }
        });
        musicThread.start();*/
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }


    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void openSettingsScene(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Settings");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("SettingsScene.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeScene(ActionEvent actionEvent) {
        Stage currentStage = (Stage) btn_solveMaze.getScene().getWindow();
        currentStage.close();
    }

    public void openStartScene(ActionEvent actionEvent) {
        Stage currentStage = (Stage) btn_solveMaze.getScene().getWindow();
        currentStage.close();

        try {
            Stage stage = new Stage();
            stage.setTitle("A-maze-ing");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("StartScene.fxml").openStream());
            StartSceneController startSceneController = fxmlLoader.getController();
            Scene scene = new Scene(root, 407, 400);
            startSceneController.setResizeEvent(scene);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //endregion

}
