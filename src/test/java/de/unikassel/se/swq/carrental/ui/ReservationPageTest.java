package de.unikassel.se.swq.carrental.ui;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import de.unikassel.se.swq.carrental.model.*;
import de.unikassel.se.swq.carrental.service.DistanceMatrix;
import de.unikassel.se.swq.carrental.service.PricingCalculator;
import de.unikassel.se.swq.carrental.service.RelocationFeeCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Paths;
import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationPageTest {
    @LocalServerPort
    int port;
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    String baseUrl;
    PricingCalculator pricingCalculator;

    int duration = 5;
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(duration);
    CustomerStatus status = CustomerStatus.Standard;
    CarCategory carCategory = CarCategory.Compact_Car;
    Location location = Location.Kassel;
    PriceBreakdown priceBreakdown;
    DistanceMatrix distanceMatrix = new DistanceMatrix();
    RelocationFeeCalculator relocationFeeCalculator = new RelocationFeeCalculator(distanceMatrix);
    Car car = new Car("car-1", location, carCategory);

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
        baseUrl = "http://localhost:" + port;

        pricingCalculator = new PricingCalculator(relocationFeeCalculator, distanceMatrix);
        Offer offer = pricingCalculator.calculateTotalPrice(car, new RentalRequest(location, location, new RentalPeriod(startDate,endDate), carCategory, status));
        priceBreakdown = offer.priceBreakdown();
    }

    @AfterEach
    void teardown() {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));

        browser.close();
        playwright.close();
    }

    @Test
    void checkShownReservation(){
        goDirectlyToReservationPage();

        assertThat(page.locator("#page-title")).hasText("Reservation confirmed");
        assertThat(page.locator("#reservation-car-category")).hasText(carCategory.toString());
        assertThat(page.locator("#reservation-start-date")).hasText(startDate.toString());
        assertThat(page.locator("#reservation-end-date")).hasText(endDate.toString());

        assertThat(page.locator("#reservation-price-base")).hasText(String.valueOf(priceBreakdown.basePrice()));
        assertThat(page.locator("#reservation-price-duration")).hasText(String.valueOf(priceBreakdown.durationDiscount()));
        assertThat(page.locator("#reservation-price-status-discount")).hasText(String.valueOf(priceBreakdown.statusDiscount()));
        assertThat(page.locator("#reservation-price-relocation-fee")).hasText(String.valueOf(priceBreakdown.relocationFee()));
        assertThat(page.locator("#reservation-price-total")).hasText(String.valueOf(priceBreakdown.total()));
    }

    private void goDirectlyToReservationPage() {
        page.navigate(baseUrl + "/");
        APIResponse response = context.request().post(baseUrl + "/request",
                RequestOptions.create().setForm(FormData.create()
                        .set("pickupLocation", location.toString())
                        .set("returnLocation", location.toString())
                        .set("category", carCategory.toString())
                        .set("customerStatus", status.toString())
                        .set("startDate", startDate.toString())
                        .set("endDate", endDate.toString())));

        APIResponse confirmResponse = context.request().post(baseUrl + "/reservations/confirm");
        page.setContent(confirmResponse.text());
    }

}
