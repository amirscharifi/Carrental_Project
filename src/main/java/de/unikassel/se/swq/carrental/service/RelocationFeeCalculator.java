package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Location;

public class RelocationFeeCalculator {

    private final DistanceMatrix distanceMatrix;

    public RelocationFeeCalculator(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public int calculateRelocationFee(int distance){
        //a separate method for boundry-testing
        if (distance == 0) {
            return 0;
        } else if (distance <= 60) {
            return 30;
        } else if (distance <= 120) {
            return 55;
        } else {
            return 90;
        }
    }

    public int calculateRelocationFee(Location from, Location to) {
        //calculate the exact distance in km and then the relocation-fee
        int distance = distanceMatrix.getDistance(from, to);
        return calculateRelocationFee(distance);

    }
}
