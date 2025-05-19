package com.example.demotrafik.view;

import com.example.demotrafik.Controller.TrafficLightController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import java.util.Random;

public class traffic extends Application {
    private final int[] vehicleCounts = {5, 5, 5, 5}; // Kuzey, Doğu, Güney, Batı
    private final Label[] vehicleLabels = new Label[4];
    private final Label[] timerLabels = new Label[4];
    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        Circle[] redLights = new Circle[4];
        Circle[] yellowLights = new Circle[4];
        Circle[] greenLights = new Circle[4];
        for (int i = 0; i < 4; i++) {
            redLights[i] = new Circle(10, Color.RED);
            yellowLights[i] = new Circle(10, Color.DARKGOLDENROD);
            greenLights[i] = new Circle(10, Color.DARKGREEN);
            timerLabels[i] = new Label("0");
        }

        Label statusLabel = new Label("Simülasyon bekliyor, araç sayılarını girin");
        Button startButton = new Button("Başlat");
        Button stopButton = new Button("Durdur");
        Button randomButton = new Button("Rastgele Ata"); // Yeni buton

        for (int i = 0; i < 4; i++) {
            vehicleLabels[i] = new Label("Araç Sayısı (Yön " + getDirectionName(i) + "): " + vehicleCounts[i]);
        }

        TrafficLightController controller = new TrafficLightController(
                redLights, yellowLights, greenLights, timerLabels, statusLabel, startButton, stopButton, randomButton, vehicleCounts
        );

        // Rastgele araç sayısı atama
        randomButton.setOnAction(e -> {
            // Toplam araç sayısı: 20 ile 80 arasında
            int totalVehicles = random.nextInt(61) + 20; // 20 ile 80 arası
            int[] newCounts = distributeVehicles(totalVehicles);
            for (int i = 0; i < 4; i++) {
                vehicleCounts[i] = newCounts[i];
                vehicleLabels[i].setText("Araç Sayısı (Yön " + getDirectionName(i) + "): " + vehicleCounts[i]);
                controller.updateVehicleCount(i, vehicleCounts[i]);
            }
        });

        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #9999;");
        double paneWidth = 800;
        double paneHeight = 800;
        double centerX = paneWidth / 2;
        double centerY = paneHeight / 2;

        Line verticalRoad = new Line(centerX, centerY - 200, centerX, centerY + 200);
        verticalRoad.setStroke(Color.PINK);
        verticalRoad.setStrokeWidth(100);

        Line horizontalRoad = new Line(centerX - 200, centerY, centerX + 200, centerY);
        horizontalRoad.setStroke(Color.PINK);
        horizontalRoad.setStrokeWidth(100);

        Polygon topArrow = createArrow(centerX - 23, centerY - 150, 0);
        Polygon top1Arrow = createArrow(centerX - 23, centerY + 150, 0);
        Polygon rightArrow = createArrow(centerX + 150, centerY + 23, 90);
        Polygon right1Arrow = createArrow(centerX + 150, centerY - 23, -90);
        Polygon bottomArrow = createArrow(centerX + 23, centerY + 150, 180);
        Polygon bottom1Arrow = createArrow(centerX + 23, centerY - 150, 180);
        Polygon leftArrow = createArrow(centerX - 150, centerY - 23, -90);
        Polygon left1Arrow = createArrow(centerX - 150, centerY + 23, 90);

        VBox topLight = new VBox(5, redLights[0], yellowLights[0], greenLights[0], timerLabels[0]);
        topLight.setLayoutX(centerX - 100);
        topLight.setLayoutY(centerY - 150);
        topLight.setAlignment(Pos.CENTER);

        VBox rightLight = new VBox(5, redLights[1], yellowLights[1], greenLights[1], timerLabels[1]);
        rightLight.setLayoutX(centerX + 80);
        rightLight.setLayoutY(centerY - 150);
        rightLight.setAlignment(Pos.CENTER);

        VBox bottomLight = new VBox(5, redLights[2], yellowLights[2], greenLights[2], timerLabels[2]);
        bottomLight.setLayoutX(centerX + 80);
        bottomLight.setLayoutY(centerY + 50);
        bottomLight.setAlignment(Pos.CENTER);

        VBox leftLight = new VBox(5, redLights[3], yellowLights[3], greenLights[3], timerLabels[3]);
        leftLight.setLayoutX(centerX - 100);
        leftLight.setLayoutY(centerY + 50);
        leftLight.setAlignment(Pos.CENTER);

        VBox[] vehicleControls = new VBox[4];
        for (int i = 0; i < 4; i++) {
            int index = i;
            Button increaseBtn = new Button("Arttır");
            Button decreaseBtn = new Button("Azalt");
            increaseBtn.setOnAction(e -> {
                vehicleCounts[index]++;
                vehicleLabels[index].setText("Araç Sayısı (Yön " + getDirectionName(index) + "): " + vehicleCounts[index]);
                controller.updateVehicleCount(index, vehicleCounts[index]);
            });
            decreaseBtn.setOnAction(e -> {
                if (vehicleCounts[index] > 0) {
                    vehicleCounts[index]--;
                    vehicleLabels[index].setText("Araç Sayısı (Yön " + getDirectionName(index) + "): " + vehicleCounts[index]);
                    controller.updateVehicleCount(index, vehicleCounts[index]);
                }
            });
            vehicleControls[i] = new VBox(5, vehicleLabels[i], increaseBtn, decreaseBtn);
            vehicleControls[i].setAlignment(Pos.CENTER);
        }

        vehicleControls[0].setLayoutX(centerX - 100);
        vehicleControls[0].setLayoutY(centerY - 250);
        vehicleControls[1].setLayoutX(centerX + 150);
        vehicleControls[1].setLayoutY(centerY - 150);
        vehicleControls[2].setLayoutX(centerX + 150);
        vehicleControls[2].setLayoutY(centerY + 100);
        vehicleControls[3].setLayoutX(centerX - 250);
        vehicleControls[3].setLayoutY(centerY + 100);

        VBox mainControls = new VBox(10, statusLabel, startButton, stopButton, randomButton);
        mainControls.setLayoutX(centerX - 50);
        mainControls.setLayoutY(centerY + 180);
        mainControls.setAlignment(Pos.CENTER);

        pane.getChildren().addAll(
                verticalRoad, horizontalRoad,
                topArrow, rightArrow, bottomArrow, leftArrow, top1Arrow, bottom1Arrow, right1Arrow, left1Arrow,
                topLight, rightLight, bottomLight, leftLight,
                vehicleControls[0], vehicleControls[1], vehicleControls[2], vehicleControls[3],
                mainControls
        );

        Scene scene = new Scene(pane, paneWidth, paneHeight);
        primaryStage.setTitle("Trafik Işığı Kavşak");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int[] distributeVehicles(int totalVehicles) {
        int[] counts = new int[4];
        int remaining = totalVehicles - 4; // Her yöne en az 1 araç
        for (int i = 0; i < 4; i++) {
            counts[i] = 1; // Minimum 1 araç
        }
        // Kalan araçları rastgele dağıt
        for (int i = 0; i < remaining; i++) {
            counts[random.nextInt(4)]++;
        }
        return counts;
    }

    private Polygon createArrow(double x, double y, double angle) {
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(
                0.0, -10.0,
                -10.0, 10.0,
                10.0, 10.0
        );
        arrow.setFill(Color.WHITE);
        arrow.setLayoutX(x);
        arrow.setLayoutY(y);
        arrow.setRotate(angle);
        return arrow;
    }

    private String getDirectionName(int index) {
        switch (index) {
            case 0: return "Kuzey";
            case 1: return "Doğu";
            case 2: return "Güney";
            case 3: return "Batı";
            default: return "";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}