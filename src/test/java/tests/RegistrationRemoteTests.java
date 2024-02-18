package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.ProjectConfig;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Tag("properties_task")

public class RegistrationRemoteTests {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("environment", System.getProperty("environment", "prod"));

        Configuration.baseUrl = "https://demoqa.com";
        Configuration.pageLoadStrategy = "eager";
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserVersion = System.getProperty("browserVersion", "100.0");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.remote = System.getProperty("browserRemoteUrl");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                "enableVNC", true,
                "enableVideo", true
        ));
        Configuration.browserCapabilities = capabilities;

    }

    @BeforeEach
    void beforeEach() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    void addAttachments() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        Attach.addVideo();

    }

    @Test
    @Tag("demoqa")
    void fillPracticeFormTest() {
        ProjectConfig projectConfig = ConfigFactory.create(ProjectConfig.class);

        step("Open form", () -> {
            open("/automation-practice-form");
            $(".fc-button-label").click();
           executeJavaScript("$('#fixedban').remove()");
           executeJavaScript("$('footer').remove()");
        });
        step("Fill form", () -> {
            $("#firstName").setValue(projectConfig.firstName());
            $("#lastName").setValue(projectConfig.lastName());
            $("#userEmail").setValue("ivanov88@mail.ru");
            $("#genterWrapper").$(byText("Male")).click();
            $("#userNumber").setValue("9119991919");
            $("#dateOfBirthInput").click();
            $(".react-datepicker__month-select").selectOption(7);
            $(".react-datepicker__year-select").selectOption(88);
            $(".react-datepicker__day--023").click();
            $("#subjectsInput").setValue("Arts").pressEnter();
            $("#hobbiesWrapper").$(byText("Music")).click();
            $("#uploadPicture").uploadFromClasspath("screen.jpg");
            $("#currentAddress").setValue("Test");
            $("#state").click();
            $("#stateCity-wrapper").$(byText("Haryana")).click();
            $("#city").click();
            $("#stateCity-wrapper").$(byText("Karnal")).click();
            $("#submit").click();
        });
        step("Verify results", () -> {
            $(".modal-header").shouldHave(text("Thanks for submitting the form"));
            $(".table-responsive").shouldHave(text(projectConfig.firstName()));
            $(".table-responsive").shouldHave(text(projectConfig.lastName()));
            $(".table-responsive").shouldHave(text("ivanov88@mail.ru"));
            $(".table-responsive").shouldHave(text("Male"));
            $(".table-responsive").shouldHave(text("9119991919"));
            $(".table-responsive").shouldHave(text("23 August,1988"));
            $(".table-responsive").shouldHave(text("Test"));
            $(".table-responsive").shouldHave(text("screen.jpg"));
            $(".table-responsive").shouldHave(text("Test"));
            $(".table-responsive").shouldHave(text("Haryana Karnal"));
        });
    }
}
