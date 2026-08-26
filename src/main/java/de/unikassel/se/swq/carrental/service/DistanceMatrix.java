package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Location;

import java.util.Map;

public class DistanceMatrix {

    private static final Map<Location, Map<Location, Integer>> distances = Map.of(
            Location.Kassel, Map.of(
                    Location.Goettingen, 55,
                    Location.Paderborn, 85,
                    Location.Giessen, 115,
                    Location.Kassel, 0
            ),
            Location.Goettingen, Map.of(
                    Location.Kassel, 55,
                    Location.Paderborn, 145,
                    Location.Giessen, 170,
                    Location.Goettingen, 0
            ),
            Location.Giessen, Map.of(
                    Location.Kassel, 115,
                    Location.Goettingen, 170,
                    Location.Paderborn, 195,
                    Location.Giessen, 0
            ),
            Location.Paderborn, Map.of(
                    Location.Kassel, 85,
                    Location.Goettingen, 145,
                    Location.Giessen, 195,
                    Location.Paderborn, 0
            )
    );

    public int getDistance(Location from, Location to) {
        return distances.get(from).get(to);
    }

}
