package ViewModel;


import Model.IModel;
import Model.MyModel;
import View.MyMazeDisplayer;
import View.MyViewController;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer{

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    private ArrayList<AState> solutionPath;
    private Object solutionPathLock;

    public StringProperty characterPositionRow; //For Binding
    public StringProperty characterPositionColumn; //For Binding

    private int gombaPositionRowIndex;
    private int gombaPositionColumnIndex;

    private int tortugaPositionRowIndex;
    private int tortugaPositionColumnIndex;

    private int mushroomPositionRowIndex;
    private int mushroomPositionColumnIndex;


    public MyViewModel(IModel model){
        solutionPathLock = new Object();
        this.model = model;
        characterPositionRow = new SimpleStringProperty("1");
        characterPositionColumn = new SimpleStringProperty("1");

        gombaPositionColumnIndex = 1;
        gombaPositionRowIndex = 1;

        tortugaPositionColumnIndex = 1;
        tortugaPositionRowIndex = 1;

        mushroomPositionColumnIndex = 1;
        mushroomPositionRowIndex = 1;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            if(arg instanceof Maze){
                solutionPath = null;
            }
            else if(arg instanceof Position){
                Position pos = (Position)arg;
                if(pos.getColumnIndex() > getCharacterPositionColumn())
                    MyMazeDisplayer.movingRight = true;
                else if(pos.getColumnIndex() < getCharacterPositionColumn())
                    MyMazeDisplayer.movingRight = false;
                characterPositionRowIndex = model.getCharacterPositionRow();
                characterPositionRow.set(characterPositionRowIndex + "");
                characterPositionColumnIndex = model.getCharacterPositionColumn();
                characterPositionColumn.set(characterPositionColumnIndex + "");

                synchronized (solutionPathLock) {
                    if(solutionPath != null)
                        if (solutionPath.remove(new MazeState(new Position(pos))))
                            notifySolutionPathChanged();
                }

            }
            else if(arg instanceof Solution){
                solutionPath = ((Solution) arg).getSolutionPath();
                arg = solutionPath;
                ((ArrayList) arg).remove(new MazeState(new Position(characterPositionRowIndex, characterPositionColumnIndex)));
            }
            else if(arg instanceof KeyCode || arg instanceof MouseEvent){
                //MyMazeDisplayer.shrink = true;

                //startSound();
            }
            else if(arg instanceof Boolean){
                //
            }
            else if(arg instanceof String){
                if(((String)arg).length() >= 10 && ((String)arg).substring(0,10).equals("GombaMoved")) {
                    gombaPositionRowIndex = model.getGombaPositionRowIndex();
                    gombaPositionColumnIndex = model.getGombaPositionColumnIndex();
                    String side = ((String)arg).substring(10);
                    if(side.equals("Left"))
                        MyMazeDisplayer.gombaMovingRight = false;
                    else if(side.equals("Right"))
                        MyMazeDisplayer.gombaMovingRight = true;

                }
                else if(((String)arg).length() >= 10 && ((String)arg).substring(0,12).equals("TortugaMoved")) {
                    tortugaPositionRowIndex = model.getTortugaPositionRowIndex();
                    tortugaPositionColumnIndex = model.getTortugaPositionColumnIndex();
                    String side = ((String)arg).substring(12);
                    if(side.equals("Left"))
                        MyMazeDisplayer.tortugaMovingRight = false;
                    else if(side.equals("Right"))
                        MyMazeDisplayer.tortugaMovingRight = true;
                }
                else if(((String)arg).equals("MushroomMoved")){
                    mushroomPositionRowIndex = model.getMushroomPositionRowIndex();
                    mushroomPositionColumnIndex = model.getMushroomPositionColumnIndex();
                }
                else if(((String)arg).equals("collide")){
                    MyMazeDisplayer.shrink = true;
                    //
                }
                else if(((String)arg).equals("collideWithMushroom"))
                    MyMazeDisplayer.shrink = false;
            }
            setChanged();
            notifyObservers(arg);
        }
    }

    private void notifySolutionPathChanged(){
        setChanged();
        notifyObservers(solutionPath);
    }

    public ArrayList<AState> getSolutionPath(){
        return solutionPath;
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public void moveCharacter(MouseEvent movement, double startX, double startY){
        model.moveCharacter(movement,startX, startY);
    }

    public static void setConfigurations(int numberOfThreads, String generatingAlgorithmName, String solvingAlgorithmName){
        IModel.setConfigurations(numberOfThreads, generatingAlgorithmName, solvingAlgorithmName);
    }

    public static int getConfigurationNumberOfThreads(){
        return IModel.getConfigurationNumberOfThreads();
    }

    public static IMazeGenerator getConfigurationGeneratingAlgorithm(){
        return IModel.getConfigurationGeneratingAlgorithm();
    }

    public static String getConfigurationSolvingAlgorithmName(){
        return IModel.getConfigurationSolvingAlgorithmName();
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public int getGombaPositionRowIndex(){
        return gombaPositionRowIndex;
    }

    public int getGombaPositionColumnIndex(){
        return gombaPositionColumnIndex;
    }

    public int getTortugaPositionRowIndex(){
        return tortugaPositionRowIndex;
    }

    public int getTortugaPositionColumnIndex(){
        return tortugaPositionColumnIndex;
    }

    public int getMushroomPositionRowIndex(){
        return mushroomPositionRowIndex;
    }

    public int getMushroomPositionColumnIndex(){
        return mushroomPositionColumnIndex;
    }

    public void stopServers() {
        model.stopServers();
    }


    public void load(Maze loadedMaze, Position loadedPoistion) {
        model.load(loadedMaze, loadedPoistion);
    }
}

