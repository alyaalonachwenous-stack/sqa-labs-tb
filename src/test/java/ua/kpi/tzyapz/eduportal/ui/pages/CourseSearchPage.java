package ua.kpi.tzyapz.eduportal.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object каталогу курсів «ЕдуПортал» (F-30): головна сторінка з переліком курсів і сторінка пошуку курсів.
 */
public class CourseSearchPage {

    /** Поле «Пошук курсів» у основній області сторінки пошуку (Moodle 5.3 використовує name='q'). */
    private static final By SEARCH_BOX = By.cssSelector(
        "#region-main input[name='q'], #region-main input[name='search'], #coursesearchbox, input[placeholder*='Search courses']");
    /** Назви курсів — це посилання на сторінку курсу /course/view.php?id=N. */
    private static final By COURSE_LINKS = By.cssSelector("a[href*='/course/view.php?id=']");
    private static final By BODY = By.tagName("body");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CourseSearchPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /** Головна сторінка з блоком «Available courses». */
    public CourseSearchPage openHome(String baseUrl) {
        driver.get(baseUrl + "/?lang=en");
        wait.until(d -> !d.findElements(COURSE_LINKS).isEmpty());
        return this;
    }

    /** Сторінка пошуку курсів із порожнім запитом. */
    public CourseSearchPage openSearch(String baseUrl) {
        driver.get(baseUrl + "/course/search.php?lang=en");
        wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_BOX));
        return this;
    }

    /** Вводить запит у поле «Пошук курсів» і відправляє форму клавішею Enter. */
    public void search(String term) {
        WebElement box = driver.findElement(SEARCH_BOX);
        box.clear();
        box.sendKeys(term, Keys.ENTER);
        wait.until(d -> d.getCurrentUrl().contains("q=" + term) || d.getCurrentUrl().contains("search=" + term));
    }

    /** Назви курсів зі сторінки (чекає, доки з'явиться хоча б одна). */
    public List<String> courseNames() {
        return wait.until(d -> {
            List<String> names = d.findElements(COURSE_LINKS).stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
            return names.isEmpty() ? null : names;
        });
    }

    /** Чекає, доки на сторінці з'явиться повідомлення з вказаним фрагментом, і повертає весь текст сторінки. */
    public String waitForPageText(String fragment) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(BODY, fragment));
        return driver.findElement(BODY).getText();
    }

    /** Весь видимий текст сторінки. */
    public String pageText() {
        return driver.findElement(BODY).getText();
    }

    /** Кількість посилань на курси на поточній сторінці (без очікування). */
    public int courseLinkCount() {
        return driver.findElements(COURSE_LINKS).size();
    }
}