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

import javax.swing.*;
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

    private Image coin;

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
                Image wall = new Image(new FileInputStream("resources/images/Displayed On Maze/brick.png"));
                Image character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));
                coin = new Image(new FileInputStream("resources/images/coins/try.gif"));
                //coin = new ImageIcon(new URL("http://i.stack.imgur.com/KSnus.gif")).getImage();
                Image path = new Image(new FileInputStream("resources/images/Displayed On Maze/path.png"));
                Image flag = new Image(new FileInputStream("resources/images/Displayed On Maze/flag.png"));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < /*maze.length*/maze.getRowSize(); i++) {
                    for (int j = 0; j < /*maze[i].length*/maze.getColumnSize(); j++) {
                        if (maze.getAtIndex(i,j) == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            //gc.drawImage(wallImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wall, j * cellWidth, i * cellHeight, cellHeight, cellWidth);
                        }
                        else if(i == maze.getGoalPosition().getRowIndex() && j == maze.getGoalPosition().getColumnIndex()){
                            gc.drawImage(flag, j * cellWidth, i * cellHeight, cellHeight, cellWidth);
                        }
                        else{
                            gc.drawImage(path, j * cellWidth, i * cellHeight, cellHeight, cellWidth);
                        }
                    }
                }

                if(showSolution) {
                    //Draw Solution
                    for (AState state : solution.getSolutionPath()) {
                        //changeCoin();
                        MazeState mazeState = (MazeState) state;
                        Position position = mazeState.getPositionOfMazeState();
                        //changeCoin(position.getColumnIndex(),position.getRowIndex(), cellWidth, cellHeight, gc);
                        gc.drawImage(coin, position.getColumnIndex() * cellWidth, position.getRowIndex() * cellHeight, cellHeight, cellWidth );
                    }
                }

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);

                gc.drawImage(/*characterImage*/character, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
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

    public void changeCoin(int col, int row, double cellWidth, double cellHeight, GraphicsContext gc){
        Thread threadcoin = new Thread(()->{
            int count = 0;
            while(true){
                try {
                    int num = count % 4;
                    switch (num) {
                        case 0:
                            gc.drawImage(new Image(new FileInputStream("resources/images/coins/coin01.png")), col * cellWidth,row * cellHeight, cellHeight, cellWidth );
                            //coin = new Image(new FileInputStream("resources/images/coins/coin01.png"));
                            break;
                        case 1:
                            gc.drawImage(new Image(new FileInputStream("resources/images/coins/coin02.png")), col * cellWidth,row * cellHeight, cellHeight, cellWidth );
                            //coin = new Image(new FileInputStream("resources/images/coins/coin02.png"));
                            break;
                        case 2:
                            gc.drawImage(new Image(new FileInputStream("resources/images/coins/coin03.png")), col * cellWidth,row * cellHeight, cellHeight, cellWidth );
                            //coin = new Image(new FileInputStream("resources/images/coins/coin03.png"));
                            break;
                        case 3:
                            gc.drawImage(new Image(new FileInputStream("resources/images/coins/coin04.png")), col * cellWidth,row * cellHeight, cellHeight, cellWidth );
                            //coin = new Image(new FileInputStream("resources/images/coins/coin04.png"));
                            break;
                    }
                    count++;
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        threadcoin.start();
    }
    //endregion

}