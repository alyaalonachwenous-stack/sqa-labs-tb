package ua.kpi.tzyapz.eduportal.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Спільна інфраструктура UI-тестів «ЕдуПортал» (демонстраційний застосунок Moodle Sandbox).
 * <p>
 * Кожен тест отримує власний браузер ({@code @BeforeEach}) і закриває його ({@code @AfterEach},
 * виконується і після падіння тесту), тому тести незалежні й не залежать від порядку виконання.
 * <p>
 * Параметри запуску (передаються як {@code -D...}):
 * <ul>
 *   <li>{@code ui.headless=false} — показати вікно браузера (для демо); за замовчуванням headless;</li>
 *   <li>{@code ui.baseUrl=...} — інша адреса застосунку (за замовчуванням https://sandbox.moodledemo.net).</li>
 * </ul>
 * Селеніум Manager (Selenium 4.6+) сам завантажує драйвер, що відповідає встановленому Chrome, —
 * chromedriver вручну встановлювати не потрібно (потрібен інтернет під час першого запуску).
 */
abstract class UiTestBase {

    protected static final String BASE_URL =
        System.getProperty("ui.baseUrl", "https://sandbox.moodledemo.net");

    /** Облікові дані студента демо — з головної сторінки Moodle Sandbox (скидається щогодини). */
    protected static final String STUDENT_LOGIN = "student";
    protected static final String STUDENT_PASSWORD = "sandbox24";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("ui.headless", "true"))) {
            options.addArguments("--headless=new");
        }
        // Розмір вікна задаємо явно: у вузькому вікні адаптивна верстка ховає елементи навігації.
        // --lang=en-US + параметр ?lang=en в адресах дають англійський інтерфейс, тож тексти у перевірках стабільні.
        options.addArguments("--window-size=1280,900", "--lang=en-US");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
