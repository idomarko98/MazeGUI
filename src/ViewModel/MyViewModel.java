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

    public MyViewModel(IModel model){
        solutionPathLock = new Object();
        this.model = model;
        characterPositionRow = new SimpleStringProperty("1");
        characterPositionColumn = new SimpleStringProperty("1");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            if(arg instanceof Maze){

            }
            if(arg instanceof Position){
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
            if(arg instanceof Solution){
                solutionPath = ((Solution) arg).getSolutionPath();
                arg = solutionPath;
                ((ArrayList) arg).remove(new MazeState(new Position(characterPositionRowIndex, characterPositionColumnIndex)));
            }
            if(arg instanceof KeyCode || arg instanceof MouseEvent){
                MyMazeDisplayer.shrink = true;
                //startSound();
            }
            if(arg instanceof Boolean){
                //
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
}

