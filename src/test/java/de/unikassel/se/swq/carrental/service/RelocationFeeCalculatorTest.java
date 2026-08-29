package de.unikassel.se.swq.carrental.service;

import de.unikassel.se.swq.carrental.model.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RelocationFeeCalculatorTest {

    DistanceMatrix distanceMatrix = new DistanceMatrix();
    RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(distanceMatrix);

    @Test
    public void feeForDistance0km_returns0() {
        assertEquals(0, relocationFeeCalculator.calculateRelocationFee(Location.Kassel, Location.Kassel));

        //------------------------------------boundary-tests------------------------------------//
        assertEquals(0, relocationFeeCalculator.calculateRelocationFee(0));
        assertNotEquals(0, relocationFeeCalculator.calculateRelocationFee(1));
    }

    @Test
    public void feeForDistanceUpTo60km_returns30() {
        assertEquals(30, relocationFeeCalculator.calculateRelocationFee(Location.Kassel, Location.Goettingen));

        //------------------------------------boundary-tests------------------------------------//
        assertEquals(30, relocationFeeCalculator.calculateRelocationFee(60));
        assertNotEquals(30, relocationFeeCalculator.calculateRelocationFee(61));
    }

    @Test
    public void feeForDistanceUpTo120km_returns55() {
        assertEquals(55, relocationFeeCalculator.calculateRelocationFee(Location.Kassel, Location.Giessen));

        //------------------------------------boundary-tests------------------------------------//
        assertEquals(55, relocationFeeCalculator.calculateRelocationFee(120));
        assertNotEquals(55, relocationFeeCalculator.calculateRelocationFee(121));
    }

    @Test
    public void feeForDistanceOver120km_returns90() {
        assertEquals(90, relocationFeeCalculator.calculateRelocationFee(Location.Goettingen, Location.Paderborn));

        //------------------------------------boundary-tests------------------------------------//
        assertNotEquals(90, relocationFeeCalculator.calculateRelocationFee(120));
        assertEquals(90, relocationFeeCalculator.calculateRelocationFee(121));
    }

}
