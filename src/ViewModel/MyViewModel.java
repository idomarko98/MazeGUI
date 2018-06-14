package ViewModel;


import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer{

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public StringProperty characterPositionRow; //For Binding
    public StringProperty characterPositionColumn; //For Binding

    public MyViewModel(IModel model){
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
                characterPositionRowIndex = model.getCharacterPositionRow();
                characterPositionRow.set(characterPositionRowIndex + "");
                characterPositionColumnIndex = model.getCharacterPositionColumn();
                characterPositionColumn.set(characterPositionColumnIndex + "");
            }
            if(arg instanceof Solution){

            }
            if(arg instanceof KeyCode){
                //startSound();
            }
            setChanged();
            notifyObservers(arg);
        }
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

    public static void setConfigurations(int numberOfThreads, String generatingAlgorithmName, String solvingAlgorithmName){
        IModel.setConfigurations(numberOfThreads, generatingAlgorithmName, solvingAlgorithmName);
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

