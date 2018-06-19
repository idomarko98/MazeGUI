package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.application.Platform;
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
import javax.swing.text.View;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MyMazeDisplayer extends Canvas {

    private Maze maze;
    private int previousCharacterPositionRow;
    private int previousCharacterPositionColumn;
    private Solution solution;
    private ArrayList<AState> solutionPath;
    private int characterPositionRow;
    private int characterPositionColumn;
    private int gombaPositionRow;
    private int gombaPositionColumn;
    private int tortugaPositionRow;
    private int tortugaPositionColumn;
    private int mushroomPositionRow;
    private int mushroomPositionColumn;
    private boolean showSolution;
    public static boolean movingRight;
    public static boolean shrink;
    public static boolean gombaMovingRight;
    public static boolean tortugaMovingRight;
    private GraphicsContext gc;
    private double canvasHeight;
    private double canvasWidth;
    private double zoomFactor;
    private double zoomDelta;
    private double cellHeight;
    private double cellWidth;
    private double startX;
    private double startY;

    private Image wall;
    private Image path;
    private Image flag;
    private Image coin;
    private Image character;
    private Image gomba;
    private Image tortuga;
    private Image mushroom;
    private Image questionBrick;

    private volatile Object lock;
    private volatile Object lock2;
    private volatile Object cellHeightAndWidthLock;
    private volatile Object zoomFactorLock;
    private volatile Object startPositionLock;
    private volatile Object gombaLock;
    private volatile Object tortugaLock;
    private volatile Object mushroomLock;

    private ArrayList<Position> questionBricksList;


    public MyMazeDisplayer() {
        try {
            lock = new Object();
            lock2 = new Object();
            cellHeightAndWidthLock = new Object();
            zoomFactorLock = new Object();
            startPositionLock = new Object();
            gombaLock = new Object();
            tortugaLock = new Object();

            zoomFactor = 0;
            zoomDelta = 20;
            startX = 0;
            startY = 0;

            previousCharacterPositionRow = 1;
            previousCharacterPositionColumn = 1;
            characterPositionRow = 1;
            characterPositionColumn = 1;
            gombaPositionRow = 1;
            gombaPositionColumn = 1;
            tortugaPositionRow = 1;
            tortugaPositionColumn = 1;
            showSolution = false;
            movingRight = true;
            shrink = false;
            gombaMovingRight = true;
            tortugaMovingRight = true;
            questionBricksList = new ArrayList<>();
            /*
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gc = getGraphicsContext2D();
                }
            });
            */
            try {
                gc = getGraphicsContext2D();
            }
            catch (Exception e){}

            //wall = new Image(new FileInputStream("resources/images/Displayed On Maze/brick.png"));
            wall = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/brick.png"));
            //path = new Image(new FileInputStream("resources/images/Displayed On Maze/path.png"));
            path = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/path.png"));
            //flag = new Image(new FileInputStream("resources/images/Displayed On Maze/flag.png"));
            flag = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/flag.png"));
            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));
            character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_big_right01.png"));
            gomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_right_01.png"));
            tortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_right_01.png"));
            mushroom = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/mushroom.png"));
            questionBrick = new Image(this.getClass().getResourceAsStream("/images/Displayed On Maze/question.png"));

            ChangingCharactersImage();
            ChangingCoinImage();
            ChagingGombaImage();
            ChagingTortugaImage();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCanvasProperties() {
        synchronized (cellHeightAndWidthLock) {
            canvasHeight = getHeight();
            canvasWidth = getWidth();
        }
        drawMaze();
    }

    private void ChagingGombaImage() {
        Thread thread = new Thread(()->{
            boolean firstImage = true;
            while(true) {
                try {
                    if (gombaMovingRight) {
                        if (firstImage)
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));
                            gomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_right_01.png"));
                        else
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right02.png"));
                            gomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_right_02.png"));
                    } else {
                        if (firstImage)
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                            gomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_left_01.png"));
                        else
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left02.png"));
                            gomba = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/gomba_left_02.png"));
                    }

                    firstImage = !firstImage;
                    synchronized (gombaLock) {
                        drawGomba();
                    }
                    Thread.sleep(200);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        thread.start();
    }

    private void ChagingTortugaImage() {
        Thread thread = new Thread(()->{
            boolean firstImage = true;
            while(true) {
                try {
                    if (tortugaMovingRight) {
                        if (firstImage)
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));
                            tortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_right_01.png"));
                        else
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right02.png"));
                            tortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_right_02.png"));
                    } else {
                        if (firstImage)
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                            tortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_left_01.png"));
                        else
                            //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left02.png"));
                            tortuga = new Image(this.getClass().getResourceAsStream("/images/Enemy Characters/tortuga_left_02.png"));
                    }

                    firstImage = !firstImage;
                    synchronized (tortugaLock) {
                        drawTortuga();
                    }
                    Thread.sleep(200);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        thread.start();
    }

    private void ChangingCharactersImage(){
        Thread thread = new Thread(()->{
            boolean firstImage = true;
            while(true){
                try{
                    if(!shrink) {
                        if(movingRight) {
                            if (firstImage)
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_big_right01.png"));
                            else
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right02.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_big_right02.png"));
                        }
                        else{
                            if (firstImage)
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_big_left01.png"));
                            else
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left02.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_big_left02.png"));
                        }
                    }
                    else{
                        if(movingRight) {
                            if (firstImage)
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_small_right01.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_small_right01.png"));
                            else
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_small_right02.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_small_right02.png"));
                        }
                        else{
                            if (firstImage)
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_small_left01.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_small_left01.png"));
                            else
                                //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_small_left02.png"));
                                character = new Image(this.getClass().getResourceAsStream("/images/Mario Characters/mario_small_left02.png"));
                        }
                    }

                    firstImage = !firstImage;
                    synchronized (lock) {
                        drawCharacter();
                    }
                    Thread.sleep(200);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        thread.start();
    }

    private void ChangingCoinImage(){
        Thread thread = new Thread(()->{
            int imageNumber = 0;
            while(true){
                try{
                    if(imageNumber == 0)
                        //coin = new Image(new FileInputStream("resources/images/Coins/coin01.png"));
                        coin = new Image(this.getClass().getResourceAsStream("/images/Coins/coin01.png"));
                    else if(imageNumber == 1)
                        //coin = new Image(new FileInputStream("resources/images/Coins/coin02.png"));
                        coin = new Image(this.getClass().getResourceAsStream("/images/Coins/coin02.png"));
                    else if(imageNumber == 2)
                        //coin = new Image(new FileInputStream("resources/images/Coins/coin03.png"));
                        coin = new Image(this.getClass().getResourceAsStream("/images/Coins/coin03.png"));
                    else
                        //coin = new Image(new FileInputStream("resources/images/Coins/coin04.png"));
                        coin = new Image(this.getClass().getResourceAsStream("/images/Coins/coin04.png"));

                    imageNumber = (imageNumber + 1) % 4;

                    synchronized (lock2) {
                        drawCoins();
                    }
                    Thread.sleep(100);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        thread.start();
    }

    private void drawCoins() {
        if(maze != null && showSolution){
            //Draw Solution
            for (AState state : solutionPath) {
                //changeCoin();
                MazeState mazeState = (MazeState) state;
                Position position = mazeState.getPositionOfMazeState();
                //changeCoin(position.getColumnIndex(), position.getRowIndex(), cellWidth, cellHeight, gc);
                //gc.drawImage(coin, position.getColumnIndex() * cellWidth, position.getRowIndex() * cellHeight, cellHeight, cellWidth );
                /*
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cellHeightAndWidthLock) {
                            if(position.getColumnIndex() != maze.getGoalPosition().getColumnIndex() || position.getRowIndex() != maze.getGoalPosition().getRowIndex())
                                gc.drawImage(coin, startX + position.getColumnIndex() * cellWidth, startY + position.getRowIndex() * cellHeight, cellWidth, cellHeight);
                        }
                    }
                });
                */try {
                    synchronized (cellHeightAndWidthLock) {
                        if (position.getColumnIndex() != maze.getGoalPosition().getColumnIndex() || position.getRowIndex() != maze.getGoalPosition().getRowIndex())
                            gc.drawImage(coin, startX + position.getColumnIndex() * cellWidth, startY + position.getRowIndex() * cellHeight, cellWidth, cellHeight);
                    }
                }
                catch (Exception e){}
            }
        }
    }

    public void changeSolutionPath(ArrayList<AState> solutionPath){
        synchronized (lock2) {
            this.solutionPath = solutionPath;
            showSolution = true;
        }
    }

    public void restartMazeView(){
        if(maze != null) {
            synchronized (zoomFactorLock) {
                zoomDelta = (maze.getColumnSize() + maze.getRowSize()) / 2;
                zoomFactor = 0;
                startX = 0;
                startY = 0;
            }
            drawMaze();
        }
    }

    public void setMaze(Maze maze) {
        synchronized (cellHeightAndWidthLock) {
            if (canvasHeight == 0 && canvasWidth == 0) {
                canvasHeight = getHeight();
                canvasWidth = getWidth();
            }
            for(int i = 0; i < maze.getRowSize(); i++)
                for(int j = 0; j < maze.getColumnSize(); j++)
                {
           //         if()
                }
        }

        this.maze = maze;
        shrink = false;
        movingRight = true;
        gombaMovingRight = true;
        tortugaMovingRight = true;
        //redraw();
        restartMazeView();
    }

    /*

    public void setSolution(Solution solution){
        synchronized (lock2) {
            this.solution = solution;
            this.solutionPath = this.solution.getSolutionPath();
            showSolution = true;
            System.out.println("Oops, how did I get here?");
        }
        //redraw();
        //drawSolution();
    }
    */

    public void removeSolution(){
        this.solution = null;
        showSolution = false;
    }

    public void setCharacterPosition(int row, int column) {
        synchronized (lock) {
            previousCharacterPositionColumn = characterPositionColumn;
            previousCharacterPositionRow = characterPositionRow;

            characterPositionRow = row;
            characterPositionColumn = column;

            //redraw();
            removePreviousCharacter();
            drawCharacter();
        }
    }

    public void zoomIn(){
        synchronized (zoomFactorLock){
            zoomFactor += zoomDelta;
        }
        drawMaze();
        //setCellHeightAndWidth();
    }

    public void zoomOut(){
        synchronized (zoomFactorLock){
            if((canvasHeight + zoomFactor) / maze.getRowSize() >= 1 && (canvasWidth + zoomFactor) / maze.getColumnSize() >=1)
                zoomFactor -= zoomDelta;
        }
        drawMaze();
        //setCellHeightAndWidth();
    }

    public void changeCursorsPlace(double xTo, double yTo){
        if(maze != null) {
            double extraMoveX = (cellWidth * maze.getColumnSize()) / canvasWidth;
            double extraMoveY = (cellHeight * maze.getRowSize()) / canvasHeight;
            if (xTo < 0)
                extraMoveX = -extraMoveX;
            if (yTo < 0)
                extraMoveY = -extraMoveY;
            startX += xTo + extraMoveX;
            startY += yTo + extraMoveY;
            drawMaze();
        }
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

    private void setCellHeightAndWidth(){
        synchronized (cellHeightAndWidthLock) {
            if (maze != null) {
                synchronized (zoomFactorLock) {
                    cellHeight = (canvasHeight + zoomFactor) / /*maze.length*/ maze.getRowSize();
                    cellWidth = (canvasWidth + zoomFactor) / /*maze[0].length*/ maze.getColumnSize();
                }
            }
        }
    }

    private void drawMaze(){
        if (maze != null) {
            /*
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getRowSize();
            double cellWidth = canvasWidth / maze.getColumnSize();
            */

            setCellHeightAndWidth();

            try {
                //Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                //Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                /*
                Image wall = new Image(new FileInputStream("resources/images/Displayed On Maze/brick.png"));
                Image path = new Image(new FileInputStream("resources/images/Displayed On Maze/path.png"));
                Image flag = new Image(new FileInputStream("resources/images/Displayed On Maze/flag.png"));
                */

                //GraphicsContext gc = getGraphicsContext2D();
                /*
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        gc.clearRect(0, 0, getWidth(), getHeight());
                    }
                });
                */
                try {
                    gc.clearRect(0, 0, getWidth(), getHeight());
                }
                catch(Exception e){}


                //Draw Maze
                for (int i = 0; i < /*maze.length*/maze.getRowSize(); i++) {
                    for (int j = 0; j < /*maze[i].length*/maze.getColumnSize(); j++) {
                        drawSpot(i, j);
                    }
                }
                drawMushroom();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void drawCharacter() {
        if (maze != null) {
            /*
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getRowSize();
            double cellWidth = canvasWidth / maze.getColumnSize();
            */
            try {
                //Image character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_right01.png"));

                //gc.clearRect(0, 0, getWidth(), getHeight());
                //gc.clearRect(previousCharacterPositionColumn, previousCharacterPositionRow, cellWidth, cellHeight);


                //removePreviousCharacter();


                //GraphicsContext gc = getGraphicsContext2D();
                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                /*
                if (!movingRight && !shrink) {
                    //character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                }
                if (!movingRight && shrink) {
                    //shrinkAnimtionLeft(character, gc, cellHeight, cellWidth);
                }
                if (movingRight && shrink) {
                }
                */
                //shrinkAnimationRight(character);
                /*
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (cellHeightAndWidthLock) {
                            gc.drawImage(character, startX + characterPositionColumn * cellWidth, startY + characterPositionRow * cellHeight, cellWidth, cellHeight);
                        }
                    }
                });
                */
                try{
                    synchronized (cellHeightAndWidthLock) {
                        gc.drawImage(character, startX + characterPositionColumn * cellWidth, startY + characterPositionRow * cellHeight, cellWidth, cellHeight);
                    }
                }
                catch (Exception e){}

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void drawGomba() {
        if (maze != null) {
            try {
                try{
                    synchronized (cellHeightAndWidthLock) {
                        gc.drawImage(gomba, startX + gombaPositionColumn * cellWidth, startY + gombaPositionRow * cellHeight, cellWidth, cellHeight);
                    }
                }
                catch (Exception e){}

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void drawTortuga() {
        if (maze != null) {
            try {
                try{
                    synchronized (cellHeightAndWidthLock) {
                        gc.drawImage(tortuga, startX + tortugaPositionColumn * cellWidth, startY + tortugaPositionRow * cellHeight, cellWidth, cellHeight);
                    }
                }
                catch (Exception e){}

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void drawMushroom() {
        if (maze != null) {
            try {
                try{
                    synchronized (cellHeightAndWidthLock) {
                        gc.drawImage(mushroom, startX + mushroomPositionColumn * cellWidth, startY + mushroomPositionRow * cellHeight, cellWidth, cellHeight);
                    }
                }
                catch (Exception e){}

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void removePreviousCharacter() {
        drawSpot(previousCharacterPositionRow, previousCharacterPositionColumn);
    }

    private void drawSpot(int row, int column) {
        if (maze != null) {

            /*
            Image wall = new Image(new FileInputStream("resources/images/Displayed On Maze/brick.png"));
            Image path = new Image(new FileInputStream("resources/images/Displayed On Maze/path.png"));
            Image flag = new Image(new FileInputStream("resources/images/Displayed On Maze/flag.png"));
            */
            /*
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (cellHeightAndWidthLock) {
                        if (maze.getAtIndex(row, column) == 1) {
                            gc.drawImage(wall, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                        } else if (row == maze.getGoalPosition().getRowIndex() && column == maze.getGoalPosition().getColumnIndex()) {
                            gc.drawImage(flag, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                        } else {
                            gc.drawImage(path, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
            });
            */
            try{
                synchronized (cellHeightAndWidthLock) {
                    if (maze.getAtIndex(row, column) == 1) {
                        Random r = new Random();
                        int result = r.nextInt(10);
                        if(result == 5)
                            gc.drawImage(questionBrick, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                        else
                            gc.drawImage(wall, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                    } else if (row == maze.getGoalPosition().getRowIndex() && column == maze.getGoalPosition().getColumnIndex()) {
                        gc.drawImage(flag, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                    } else {
                        gc.drawImage(path, startX + column * cellWidth, startY + row * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
            catch (Exception e){}

        }
    }

    /*
    private void drawSolution() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getRowSize();
            double cellWidth = canvasWidth / maze.getColumnSize();
            try {
                coin = new Image(new FileInputStream("resources/images/coins/try.gif"));

                if (showSolution) {

                    //Draw Solution
                    for (AState state : solution.getSolutionPath()) {
                        //changeCoin();
                        MazeState mazeState = (MazeState) state;
                        Position position = mazeState.getPositionOfMazeState();
                        changeCoin(position.getColumnIndex(), position.getRowIndex(), cellWidth, cellHeight, gc);
                        //gc.drawImage(coin, position.getColumnIndex() * cellWidth, position.getRowIndex() * cellHeight, cellHeight, cellWidth );
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    */

    /*
    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getRowSize();
            double cellWidth = canvasWidth / maze.getColumnSize();

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
                for (int i = 0; i < maze.getRowSize(); i++) {
                    for (int j = 0; j < maze.getColumnSize(); j++) {
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
                        changeCoin(position.getColumnIndex(),position.getRowIndex(), cellWidth, cellHeight, gc);
                        //gc.drawImage(coin, position.getColumnIndex() * cellWidth, position.getRowIndex() * cellHeight, cellHeight, cellWidth );
                    }
                }

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                if(!movingRight && !shrink)
                    character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                if(!movingRight && shrink){
                    //shrinkAnimtionLeft(character, gc, cellHeight, cellWidth);
                }
                if(movingRight && shrink){}
                    //shrinkAnimationRight(character);
                gc.drawImage(character, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    */
    /*

    private void shrinkAnimtionLeft(Image character, GraphicsContext gc, double cellHeight, double cellWidth) {
        for(int i =0; i < 8; i++){
            try {
                character = new Image(new FileInputStream("resources/images/Mario Characters/mario_big_left01.png"));
                gc.drawImage(character, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                Thread.sleep(100);
                character = new Image(new FileInputStream("resources/images/Mario Characters/mario_small_left01.png"));
                gc.drawImage(character, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    */
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

    public void setGombaPosition(int gombaPositionRowIndex, int gombaPositionColumnIndex) {
        synchronized (gombaLock) {
            //redraw();
            drawSpot(gombaPositionRow, gombaPositionColumn);
            this.gombaPositionRow = gombaPositionRowIndex;
            this.gombaPositionColumn = gombaPositionColumnIndex;
            drawGomba();
        }
    }

    public void setTortugaPosition(int tortugaPositionRowIndex, int tortugaPositionColumnIndex) {
        synchronized (tortugaLock) {
            //redraw();
            drawSpot(tortugaPositionRow, tortugaPositionColumn);
            this.tortugaPositionRow = tortugaPositionRowIndex;
            this.tortugaPositionColumn = tortugaPositionColumnIndex;
            drawTortuga();
        }
    }

    public void setMushroomPosition(int mushroomPositionRowIndex, int mushroomPositionColumnIndex) {
        synchronized (tortugaLock) {
            //redraw();
            drawSpot(mushroomPositionRow, mushroomPositionColumn);
            this.mushroomPositionRow = mushroomPositionRowIndex;
            this.mushroomPositionColumn = mushroomPositionColumnIndex;
            drawMushroom();
        }
    }

    public double getCellHeight() {
        return cellHeight;
    }

    public double getCellWidth() {
        return cellWidth;
    }

    /*
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
        try {
            threadcoin.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */
    //endregion

}