package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    private Thread musicThread;
    private MediaPlayer themeMediaPlayer;
    private volatile boolean stopThemeSong = false;
    private double startX; // for mouse
    private double startY; //for mouse

    private volatile double dragAndCtrlPreviousX = 0;
    private volatile double dragAndCtrlPreviousY = 0;
    private volatile boolean startedDragging = false;
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
            /*
            else if(arg instanceof Solution){
                updateSolution((Solution)arg);
            }
            */
            else if(arg instanceof Position){
                updatePosition((Position)arg);
            }
            else if(arg instanceof KeyCode){
                //updateNotAbleToMove((KeyCode)arg);
            }
            else if(arg instanceof ArrayList){
                updateSolutionPath((ArrayList<AState>) arg);
            }
            else if(arg instanceof Boolean){
                mazeIsSolved();
            }
            //displayMaze(viewModel.getMaze());
            //btn_generateMaze.setDisable(false);
        }
    }

    private void updateSolutionPath(ArrayList<AState> arg) {
        playCoinCollectinSound();
        mazeDisplayer.changeSolutionPath(arg);
        btn_solveMaze.setDisable(false);
    }

    private void playCoinCollectinSound() {
        //String musicFile = "resources/Sounds/coin collecting.mp3";
        //Media sound = new Media(new File(musicFile).toURI().toString());
        Media sound = new Media(this.getClass().getResource("/Sounds/coin collecting.mp3").toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void updatePosition(Position arg) {
        characterPositionColumn.set(arg.getColumnIndex() + "");
        characterPositionRow.set(arg.getRowIndex() + "");
        mazeDisplayer.setCharacterPosition(arg.getRowIndex(), arg.getColumnIndex());
    }

    /*
    private void updateSolution(Solution arg) {
        mazeDisplayer.setSolution(arg);
        btn_solveMaze.setDisable(false);

    }
    */

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
        int heigth = -1;
        int width = -1;
        try {
            heigth = Integer.valueOf(txtfld_rowsNum.getText());
            width = Integer.valueOf(txtfld_columnsNum.getText());
        }
        catch (Exception e){
            showAlert("Values must be numeric");
            return;
        }
        if(heigth < 3 || width < 3 ){
            showAlert("Height and Width must be larger than 3");
            return;
        }
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
                    //String musicFile = "resources/Sounds/theme.mp3";
                    Media sound = new Media(this.getClass().getResource("/Sounds/theme.mp3").toString());
                    //Media sound = new Media(new File(musicFile).toURI().toString());
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
        btn_solveMaze.setDisable(true);
        viewModel.solveMaze();
    }

    private void playClickedOnSolveMusic() {
        //String musicFile = "resources/Sounds/solve clicked sound.mp3";
        //Media sound = new Media(new File(musicFile).toURI().toString());
        Media sound = new Media(this.getClass().getResource("/Sounds/solve clicked sound.mp3").toString());
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

    public void MouseReleased(MouseEvent mouseEvent) {
        viewModel.moveCharacter(mouseEvent, startX, startY);

        DragDone(mouseEvent);
        mouseEvent.consume();


    }

    public void MousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();
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
            Scene scene = new Scene(root, 614,400);
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

        viewModel.stopServers();
        System.exit(0);
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

    public void openAboutScene(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("AboutScene.fxml").openStream());
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void openHelpScene(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("HelpOneScene.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void MouseScrolled(ScrollEvent scrollEvent) {
        //Check if need to zoom
        if(scrollEvent.isControlDown()){
            if(scrollEvent.getDeltaY() < 0)
                mazeDisplayer.zoomOut();
            else
                mazeDisplayer.zoomIn();
        }
    }

    public void MouseDragged(MouseEvent mouseEvent) {
        if(mouseEvent.isControlDown()){
            if(startedDragging)
                mazeDisplayer.changeCursorsPlace((mouseEvent.getX() - dragAndCtrlPreviousX) / dragAndCtrlPreviousX, (mouseEvent.getY() - dragAndCtrlPreviousY) / dragAndCtrlPreviousY);
        }
    }

    public void DragDetected(MouseEvent mouseEvent) {
        if(mouseEvent.isControlDown()){
            dragAndCtrlPreviousX = mouseEvent.getX();
            dragAndCtrlPreviousY = mouseEvent.getY();
            startedDragging = true;
            //System.out.println("Drag Detected");
        }
    }

    public void DragDone(MouseEvent mouseEvent) {
        startedDragging = false;
        dragAndCtrlPreviousX = mouseEvent.getX();
        dragAndCtrlPreviousY = mouseEvent.getY();
    }

    public void mazeIsSolved(){
        playSolveMusic();
        openSolvedScene();
    }

    private void playSolveMusic() {
        stopThemeSong = true;
        themeMediaPlayer.stop();
        Media sound = new Media(this.getClass().getResource("/Sounds/win sound.mp3").toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.play();
    }

    public void openSolvedScene(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Solved!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("SolvedScene.fxml").openStream());
            Scene scene = new Scene(root, 424, 234);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void save(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Progress File", "*.mzprg"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                File saveFile = new File(file.getPath());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));
                Object[] objects = new Object[2];
                objects[0] = viewModel.getMaze();
                objects[1] = new Position(viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn());
                objectOutputStream.writeObject(objects);
                objectOutputStream.flush();
                objectOutputStream.close();
            }
            else{
                showAlert("Error Saving");
            }
        }
        catch (Exception e){
            showAlert("Error Saving");
        }
    }

    public void load(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Progress File", "*.mzprg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                File loadFile = new File(file.getPath());
                FileInputStream inputStream = new FileInputStream(loadFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object[] objects = (Object[])objectInputStream.readObject();
                Maze loadedMaze = (Maze)objects[0];
                Position loadedPoistion = (Position)objects[1];
                viewModel.load(loadedMaze, loadedPoistion);
                //displayMaze(loadedMaze);
                objectInputStream.close();
                inputStream.close();
            }
        }
        catch (Exception e){
            showAlert("Could not load file");
        }
    }
    //endregion

}
