package com.example.demotrafik.Model;

public class TrafficLight extends Led {
    private static final int TOTAL_CYCLE_DURATION = 27000;
    private static final int DEFAULT_YELLOW_DURATION = 3000;
    private static final int MIN_GREEN_DURATION = 5000;
    private static final int MAX_GREEN_DURATION = TOTAL_CYCLE_DURATION - DEFAULT_YELLOW_DURATION;

    private int vehicleCount;
    private TrafficLight[] otherLights;

    public TrafficLight(Led.Color color, int vehicleCount, TrafficLight[] otherLights) {
        super(color, calculateDuration(color, vehicleCount));
        this.vehicleCount = vehicleCount;
        this.otherLights = otherLights != null ? otherLights : new TrafficLight[0];
    }

    private static int calculateDuration(Led.Color color, int vehicleCount) {
        if (color == Led.Color.RED) {
            return TOTAL_CYCLE_DURATION - calculateGreenDuration(vehicleCount) - DEFAULT_YELLOW_DURATION;
        } else if (color == Led.Color.GREEN) {
            return calculateGreenDuration(vehicleCount);
        } else {
            return DEFAULT_YELLOW_DURATION;
        }
    }

    public static int calculateGreenDuration(int vehicleCount) {
        if (vehicleCount < 0) {
            throw new IllegalArgumentException("vehicleCount negatif olamaz");
        }
        return Math.min(MIN_GREEN_DURATION + vehicleCount * 1000, MAX_GREEN_DURATION);
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
        setDuration(calculateDuration(getColor(), vehicleCount));
    }

    @Override
    public void changeLight() {
        // Durum geçişleri TrafficLightController'da yönetiliyor
    }

    public void setOtherLights(TrafficLight[] otherLights) {
        this.otherLights = otherLights;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }
}