package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;


import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/MyView.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        playImage(primaryStage);
        //playMusic1();
        //playMusicInLoop();
        playMusicInThread();
    }

    private void playMusic1(){
        String musicFile = "C:\\Users\\Ido\\Desktop\\theme.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();

        /* mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.play();
            }
        });*/

         /*String musicFile1 = "C:\\Users\\Ido\\Desktop\\resizing sound.mp3";
        Media sound1 = new Media(new File(musicFile1).toURI().toString());
        MediaPlayer mediaPlayer1 = new MediaPlayer(sound1);
        mediaPlayer1.play();*/
    }

    private void playMusicInLoop(){
        AudioPlayer AP = AudioPlayer.player;
        AudioStream AS;
        AudioData AD;
        ContinuousAudioDataStream loop = null;
        try{
            AS = new AudioStream(new FileInputStream("C:\\Users\\Ido\\Desktop\\resizing sound.wav"));
            AD = AS.getData();
            loop = new ContinuousAudioDataStream(AD);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        AP.start(loop);
    }

    private void playMusicLoop2(){
        try{
            File file = new File("C:\\Users\\Ido\\Desktop\\theme.mp3");
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            try{
                //Player p = new AdvancedPlayer(bis);
                //player.play();

            } catch(Exception ex) {}

        } catch(Exception e){}
    }

    private void playMusicInThread(){
        new Thread() {
            public void run() {
                try {
                    while(true) {
                        //String musicFile = "C:\\Users\\Ido\\Desktop\\resizing sound.mp3";
                        String musicFile = "C:\\Users\\Ido\\Desktop\\theme.mp3";
                        Media sound = new Media(new File(musicFile).toURI().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(sound);
                        //mediaPlayer.setAutoPlay(true);
                        mediaPlayer.play();
                        //long time = (long)sound.getDuration().toMillis();
                        //long time = 198000l;
                        int time = 219000;
                        //long time = 4000;
                        Thread.sleep(time);
                    }
                }
                catch (Exception e) {System.out.println(e); }
            }
        }.start();
    }

    private void playImage(Stage stage){
        try {
            Image icon = Image.impl_fromPlatformImage(new ImageIcon(String.valueOf(new FileInputStream("resources/images/Coins/try.jpg"))).getImage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
