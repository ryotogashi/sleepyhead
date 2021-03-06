package alarm.Controller;

import alarm.IMediaPlayerController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class FirstScreenController implements Initializable, OnCompleteSettingAlarm {

    @FXML
    private Label timeLabel;
    @FXML
    private Button setAlarmButton, quitButton;
    @FXML
    private TextField hoursField, minutesField, secondsField;
    @FXML
    private Label colon1, colon2;

    private boolean timerValidation = true;

    private Timer timer;
    private boolean timerOn = false;
    private boolean alarmPlayed = false;

    private MediaPlayer mediaPlayer;


    @Override
    public void onComplete(int totalTime) {

        setAlarmButton.setDisable(true);
        hoursField.setVisible(false);
        minutesField.setVisible(false);
        secondsField.setVisible(false);
        colon1.setVisible(false);
        colon2.setVisible(false);

        TimerTask task = new TimerTask() {
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            private int alarm_time = totalTime;


            @Override
            public void run() {

                if (0 < alarm_time) {
                    alarm_time--;
                } else {
                    if (timerOn) {
                        mediaPlayer.play();
                        alarmPlayed = true;
                        Platform.runLater(() -> {
                            openGameSelector();
                        });
                    }

                    timerOn = false;
                    cancel();
                }

                hours = alarm_time / 3600;
                minutes = (alarm_time % 3600) / 60;
                seconds = (alarm_time % 3600) % 60;

                Platform.runLater(() -> {
                    String tl;
                    tl = hours + " : " + minutes + " : " + seconds;
                    timeLabel.setText(tl);
                });
            }
        };

        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(task, 0, 1000);
        this.timerOn = true;

    }


    @FXML
    public void quitTheApp() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        hoursField.setPromptText("hr");
        hoursField.setFocusTraversable(false);
        minutesField.setPromptText("min");
        minutesField.setFocusTraversable(false);
        secondsField.setPromptText("sec");
        secondsField.setFocusTraversable(false);

        Media media = new Media(new File("src/alarm/sounds/alarm.mp3").toURI().toASCIIString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
    }


    @FXML
    private void setAlarm() {

        this.timerValidation = true;

        String hoursString = this.hoursField.getText();
        String minutesString = this.minutesField.getText();
        String secondsString = this.secondsField.getText();

        if (hoursString.equals("")) {
            hoursString = "0";
        }

        if (minutesString.equals("")) {
            minutesString = "0";
        }

        if (secondsString.equals("")) {
            secondsString = "0";
        }

        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        try {
            hours = Integer.parseInt(hoursString);
            minutes = Integer.parseInt(minutesString);
            seconds = Integer.parseInt(secondsString);
        } catch (Exception ex) {
            this.timerValidation = false;
            this.showWarning("Please enter a number only for the times!", "Invalid input received");
        }

        int totalTime = seconds + (minutes * 60) + (hours * 3600);

        if (totalTime == 0) {
            this.timerValidation = false;
            this.showWarning("Please set a time for the alarm before starting!", "Time not found!");
        }

        if (this.timerValidation) {
            onComplete(totalTime);
        }

    }


    private void showWarning(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void openGameSelector() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/gameSelector.fxml"));
            Parent parent = loader.load();
            IMediaPlayerController controller = loader.getController();
            controller.setMediaPlayer(mediaPlayer);
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

