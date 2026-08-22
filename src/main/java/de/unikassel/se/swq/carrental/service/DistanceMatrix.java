package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Location;

import java.util.Map;

public class DistanceMatrix {

    private static final Map<Location, Map<Location, Integer>> distances = Map.of(
            Location.KASSEL, Map.of(
                    Location.GOETTINGEN, 55,
                    Location.PADERBORN, 85,
                    Location.GIESSEN, 115
            ),
            Location.GOETTINGEN, Map.of(
                    Location.KASSEL, 55,
                    Location.PADERBORN, 145,
                    Location.GIESSEN, 170
            ),
            Location.GIESSEN, Map.of(
                    Location.KASSEL, 115,
                    Location.GOETTINGEN, 170,
                    Location.PADERBORN, 195
            ),
            Location.PADERBORN, Map.of(
                    Location.KASSEL, 85,
                    Location.GOETTINGEN, 145,
                    Location.GIESSEN, 195
            )
    );

    public int getDistance(Location from, Location to) {
        return distances.get(from).get(to);
    }

}
