package de.unikassel.se.swq.carrental.ui;

import com.microsoft.playwright.*;
import de.unikassel.se.swq.carrental.model.CarCategory;
import de.unikassel.se.swq.carrental.model.CustomerStatus;
import de.unikassel.se.swq.carrental.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Paths;
import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestFormPageTest {

    @LocalServerPort
    int port;
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    String baseUrl;

    int duration = 5;
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(duration);
    CustomerStatus status = CustomerStatus.Standard;
    CarCategory carCategory = CarCategory.Compact_Car;
    Location location = Location.Kassel;


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
    }

    @AfterEach
    void teardown() {
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));

        browser.close();
        playwright.close();
    }

    @Test
    void requestACar() {
        page.navigate(baseUrl + "/");
        assertThat(page.locator("#page-title")).hasText("Request a rental offer");

        page.locator("#pickupLocation").selectOption(location.name());
        page.locator("#returnLocation").selectOption(location.name());
        page.locator("#category").selectOption(carCategory.name());
        page.locator("#customerStatus").selectOption(status.name());
        page.locator("#startDate").fill(startDate.toString());
        page.locator("#endDate").fill(endDate.toString());
        page.locator("#submit-offer-request").click();

        assertThat(page.locator("#page-title")).hasText("Your offer");
        page.locator("#back-to-start-link").click();
        assertThat(page.locator("#page-title")).hasText("Request a rental offer");
    }

}
