package com.example.demotrafik.Controller;

import com.example.demotrafik.Model.Led;
import com.example.demotrafik.Model.TrafficLight;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class TrafficLightController {
    private final TrafficLight[] trafficLights;
    private final Circle[] redLights;
    private final Circle[] yellowLights;
    private final Circle[] greenLights;
    private final Label[] timerLabels;
    private final Label statusLabel;
    private final Button startButton;
    private final Button stopButton;
    private final Button randomButton; // Yeni buton
    private int currentDirection;
    private Timeline cycleTimeline;
    private final Timeline[] countdownTimelines;

    private static final int TOTAL_CYCLE_DURATION = 27000;
    private static final int DEFAULT_YELLOW_DURATION = 3000;

    public TrafficLightController(Circle[] redLights, Circle[] yellowLights, Circle[] greenLights,
                                  Label[] timerLabels, Label statusLabel, Button startButton, Button stopButton,
                                  Button randomButton, int[] vehicleCounts) {
        this.redLights = redLights;
        this.yellowLights = yellowLights;
        this.greenLights = greenLights;
        this.timerLabels = timerLabels;
        this.statusLabel = statusLabel;
        this.startButton = startButton;
        this.stopButton = stopButton;
        this.randomButton = randomButton;
        this.currentDirection = -1;
        this.countdownTimelines = new Timeline[4];

        for (int i = 0; i < 4; i++) {
            countdownTimelines[i] = new Timeline();
        }

        trafficLights = new TrafficLight[4];
        for (int i = 0; i < 4; i++) {
            trafficLights[i] = new TrafficLight(Led.Color.RED, vehicleCounts[i], null);
        }

        for (int i = 0; i < 4; i++) {
            TrafficLight[] otherLights = new TrafficLight[3];
            int index = 0;
            for (int j = 0; j < 4; j++) {
                if (j != i) {
                    otherLights[index++] = trafficLights[j];
                }
            }
            trafficLights[i].setOtherLights(otherLights);
        }

        setupButtons();
        updateLights();
        statusLabel.setText("Simülasyon bekliyor");
    }

    private void setupButtons() {
        startButton.setOnAction(e -> {
            if (currentDirection != -1) return;
            currentDirection = 0;
            startCycle();
            statusLabel.setText("Simülasyon başlatıldı, Yön: " + getDirectionName(currentDirection));
        });

        stopButton.setOnAction(e -> {
            currentDirection = -1;
            if (cycleTimeline != null) {
                cycleTimeline.stop();
            }
            for (Timeline timeline : countdownTimelines) {
                timeline.stop();
            }
            for (int i = 0; i < 4; i++) {
                trafficLights[i].setColor(Led.Color.RED);
                trafficLights[i].setDuration(TOTAL_CYCLE_DURATION - TrafficLight.calculateGreenDuration(trafficLights[i].getVehicleCount()) - DEFAULT_YELLOW_DURATION);
                timerLabels[i].setText("0");
            }
            updateLights();
            statusLabel.setText("Simülasyon durduruldu");
        });
    }

    private void startCycle() {
        if (cycleTimeline != null) {
            cycleTimeline.stop();
        }
        for (Timeline timeline : countdownTimelines) {
            timeline.stop();
        }

        trafficLights[currentDirection].setColor(Led.Color.GREEN);
        trafficLights[currentDirection].setDuration(TrafficLight.calculateGreenDuration(trafficLights[currentDirection].getVehicleCount()));
        for (int i = 0; i < 4; i++) {
            if (i != currentDirection) {
                trafficLights[i].setColor(Led.Color.RED);
                trafficLights[i].setDuration(TOTAL_CYCLE_DURATION - trafficLights[currentDirection].getDuration() - DEFAULT_YELLOW_DURATION);
            }
        }
        updateLights();

        cycleTimeline = new Timeline();
        cycleTimeline.setCycleCount(Timeline.INDEFINITE);
        cycleTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(trafficLights[currentDirection].getDuration()), e -> {
                    trafficLights[currentDirection].setColor(Led.Color.YELLOW);
                    trafficLights[currentDirection].setDuration(DEFAULT_YELLOW_DURATION);
                    updateLights();
                })
        );
        cycleTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(trafficLights[currentDirection].getDuration() + DEFAULT_YELLOW_DURATION), e -> {
                    trafficLights[currentDirection].setColor(Led.Color.RED);
                    trafficLights[currentDirection].setDuration(TOTAL_CYCLE_DURATION - TrafficLight.calculateGreenDuration(trafficLights[currentDirection].getVehicleCount()) - DEFAULT_YELLOW_DURATION);
                    currentDirection = (currentDirection + 1) % 4;
                    trafficLights[currentDirection].setColor(Led.Color.GREEN);
                    trafficLights[currentDirection].setDuration(TrafficLight.calculateGreenDuration(trafficLights[currentDirection].getVehicleCount()));
                    for (int i = 0; i < 4; i++) {
                        if (i != currentDirection) {
                            trafficLights[i].setColor(Led.Color.RED);
                            trafficLights[i].setDuration(TOTAL_CYCLE_DURATION - trafficLights[currentDirection].getDuration() - DEFAULT_YELLOW_DURATION);
                        }
                    }
                    updateLights();
                })
        );
        cycleTimeline.play();
    }

    public void updateVehicleCount(int directionIndex, int newCount) {
        trafficLights[directionIndex].setVehicleCount(newCount);
        if (currentDirection == directionIndex && trafficLights[directionIndex].getColor() == Led.Color.GREEN) {
            trafficLights[directionIndex].setDuration(TrafficLight.calculateGreenDuration(newCount));
            if (cycleTimeline != null) {
                cycleTimeline.stop();
                for (Timeline timeline : countdownTimelines) {
                    timeline.stop();
                }
                startCycle();
            }
        }
        updateLights();
        statusLabel.setText("Yön " + getDirectionName(directionIndex) + " araç sayısı: " + newCount);
    }

    private void updateLights() {
        Platform.runLater(() -> {
            for (int i = 0; i < 4; i++) {
                setLightState(i, trafficLights[i].getColor().toString());
                updateTimer(i, trafficLights[i].getDuration());
            }
        });
    }

    private void setLightState(int index, String state) {
        Circle red = redLights[index];
        Circle yellow = yellowLights[index];
        Circle green = greenLights[index];

        switch (state) {
            case "RED":
                red.setFill(Color.RED);
                yellow.setFill(Color.DARKGOLDENROD);
                green.setFill(Color.DARKGREEN);
                break;
            case "YELLOW":
                red.setFill(Color.DARKRED);
                yellow.setFill(Color.YELLOW);
                green.setFill(Color.DARKGREEN);
                break;
            case "GREEN":
                red.setFill(Color.DARKRED);
                yellow.setFill(Color.DARKGOLDENROD);
                green.setFill(Color.LIMEGREEN);
                statusLabel.setText("Yön: " + getDirectionName(index) + ", Yeşil Süre: " + trafficLights[index].getDuration() + "ms");
                break;
        }
    }

    private void updateTimer(int index, int duration) {
        Label timerLabel = timerLabels[index];
        timerLabel.setText(String.valueOf(duration / 1000));

        countdownTimelines[index].stop();
        countdownTimelines[index].getKeyFrames().clear();

        for (int i = duration / 1000 - 1; i >= 0; i--) {
            final int count = i;
            countdownTimelines[index].getKeyFrames().add(
                    new KeyFrame(Duration.seconds(duration / 1000 - i), e -> timerLabel.setText(String.valueOf(count)))
            );
        }
        countdownTimelines[index].play();
    }

    private String getDirectionName(int index) {
        switch (index) {
            case 0: return "Kuzey";
            case 1: return "Doğu";
            case 2: return "Güney";
            case 3: return "Batı";
            default: return "Bekliyor";
        }
    }
}