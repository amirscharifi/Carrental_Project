package de.unikassel.se.swq.carrental.usecase;

import com.pholser.junit.quickcheck.From;
import de.unikassel.se.swq.carrental.model.Car;
import de.unikassel.se.swq.carrental.model.OfferResult;
import de.unikassel.se.swq.carrental.service.AvailabilityChecker;
import de.unikassel.se.swq.carrental.service.DistanceMatrix;
import de.unikassel.se.swq.carrental.service.PricingCalculator;
import de.unikassel.se.swq.carrental.service.RelocationFeeCalculator;
import de.unikassel.se.swq.carrental.usecase.testsupport.FleetTestCase;
import de.unikassel.se.swq.carrental.usecase.testsupport.FleetTestCaseGenerator;
import de.unikassel.se.swq.carrental.usecase.testsupport.StubCarRepository;
import de.unikassel.se.swq.carrental.usecase.testsupport.StubReservationRepository;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(JQF.class)
public class RequestOfferUseCaseFuzzTest {

    @Fuzz
    public void fuzzEvaluateRentalRequest(@From(FleetTestCaseGenerator.class) FleetTestCase testCase) {

        System.out.println("Cars:" + testCase.cars());
        System.out.println("Reservations: " +testCase.reservations());
        System.out.println("Requests: "+testCase.request());

        StubCarRepository carRepo = new StubCarRepository(testCase.cars());
        StubReservationRepository reservationRepo = new StubReservationRepository(testCase.reservations());
        AvailabilityChecker availabilityChecker = new AvailabilityChecker(reservationRepo, carRepo, new DistanceMatrix());
        RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(new DistanceMatrix());
        PricingCalculator pricingCalculator = new PricingCalculator(relocationFeeCalculator, new DistanceMatrix());
        RequestOfferUseCase useCase = new RequestOfferUseCase(availabilityChecker, pricingCalculator);

        OfferResult result = useCase.evaluateRentalRequest(testCase.request());


        switch (result) {
            case OfferResult.Success available -> {
                Car offeredCar = available.offer().car();

                //Soundness: the offered car should match the requested category
                assertEquals(testCase.request().category(), offeredCar.category());

                //Soundness: the offered car has no overlapping reservation
                boolean overlaps = testCase.reservations().stream()
                        .filter(r -> r.car().id().equals(offeredCar.id()))
                        .anyMatch(r -> r.period().overlapsWith(testCase.request().period()));
                assertFalse(overlaps, "Offered car must not have an overlapping reservation");

                //Soundness: relocationRequired flag matches reality
                boolean carIsAtRequestedLocation =
                        offeredCar.location().equals(testCase.request().pickupLocation());
                assertEquals(!carIsAtRequestedLocation, available.offer().relocationRequired());
            }
            case OfferResult.NoCarAvailable noCarAvailable -> {
                assertFalse(existsAvailableCarAnywhere(testCase), "Oracle found an available car, but use case returned NoCarAvailable");
            }

            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
    }

    private boolean existsAvailableCarAnywhere(FleetTestCase testCase) {
        for (Car car : testCase.cars()) {
            if (car.category() != testCase.request().category()) {
                continue;
            }
            boolean free = testCase.reservations().stream()
                    .filter(r -> r.car().id().equals(car.id()))
                    .noneMatch(r -> r.period().overlapsWith(testCase.request().period()));

            if (free) {
                return true;
            }
        }
        return false;
    }


}
