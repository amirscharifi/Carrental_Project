package de.unikassel.se.swq.carrental.config;

import de.unikassel.se.swq.carrental.adapter.InMemoryCarRepository;
import de.unikassel.se.swq.carrental.adapter.InMemoryReservationRepository;
import de.unikassel.se.swq.carrental.port.CarRepository;
import de.unikassel.se.swq.carrental.port.ReservationRepository;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;
import de.unikassel.se.swq.carrental.service.DistanceMatrix;
import de.unikassel.se.swq.carrental.service.PricingCalculator;
import de.unikassel.se.swq.carrental.service.RelocationFeeCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CarRepository carRepository() {
        return new InMemoryCarRepository();
    }

    @Bean
    public ReservationRepository reservationRepository() {
        return new InMemoryReservationRepository();
    }

    @Bean
    public DistanceMatrix distanceMatrix() {
        return new DistanceMatrix();
    }

    @Bean
    public RelocationFeeCalculator relocationFeeCalculator(DistanceMatrix distanceMatrix) {
        return new RelocationFeeCalculator(distanceMatrix);
    }

    @Bean
    public AvailabilityChecker availabilityChecker(ReservationRepository reservationRepository, CarRepository carRepository, DistanceMatrix distanceMatrix) {
        return new AvailabilityChecker(reservationRepository, carRepository, distanceMatrix);
    }

    @Bean
    public PricingCalculator pricingCalculator(RelocationFeeCalculator relocationFeeCalculator, DistanceMatrix distanceMatrix) {
        return new PricingCalculator(relocationFeeCalculator, distanceMatrix);
    }

}
