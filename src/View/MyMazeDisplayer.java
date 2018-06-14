package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class MyMazeDisplayer extends Canvas {

    private Maze maze;
    private Solution solution;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private boolean showSolution = false;

    public void setMaze(Maze maze) {
        this.maze = maze;
        redraw();
    }

    public void setSolution(Solution solution){
        this.solution = solution;
        showSolution = true;
        redraw();
    }

    public void removeSolution(){
        this.solution = null;
        showSolution = false;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }
    /*
    public void redrawOrDrawSolution(){
        if(!showSolution)
            redraw();
        else
            redrawSolution();
    }
    */

    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / /*maze.length*/maze.getRowSize();
            double cellWidth = canvasWidth / /*maze[0].length*/maze.getColumnSize();

            try {
                //Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                //Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image hi = new Image(new FileInputStream("C:\\Users\\Ido\\Desktop\\assets\\mario_small01.png"));
                Image ch = new Image(new FileInputStream("C:\\Users\\Ido\\Desktop\\rot.png"));
                Image shmelen = new Image(new FileInputStream("C:\\Users\\Ido\\Desktop\\shmelen.png"));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < /*maze.length*/maze.getRowSize(); i++) {
                    for (int j = 0; j < /*maze[i].length*/maze.getColumnSize(); j++) {
                        if (maze.getAtIndex(i,j) == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            //gc.drawImage(wallImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(hi, j * cellWidth, i * cellHeight, cellHeight, cellWidth);
                        }
                    }
                }

                if(showSolution) {
                    //Draw Solution
                    for (AState state : solution.getSolutionPath()) {
                        MazeState mazeState = (MazeState) state;
                        Position position = mazeState.getPositionOfMazeState();
                        gc.drawImage(shmelen, position.getColumnIndex() * cellWidth, position.getRowIndex() * cellHeight, cellHeight, cellWidth );
                    }
                }

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);

                gc.drawImage(/*characterImage*/ch, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }
    //endregion

}