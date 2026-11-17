package ua.kpi.tzyapz.eduportal.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object сторінки входу «ЕдуПортал» (F-10, GUI-10, GUI-20).
 */
public class LoginPage {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("loginbtn");
    /** Блок помилки на сторінці входу в Moodle 5.3 (Boost). */
    private static final By ERROR = By.cssSelector(".alert-danger, .loginerrors .alert-danger, [role='alert']");
    /** Меню користувача у верхній панелі: з'являється лише після входу (GUI-75). */
    private static final By USER_MENU = By.cssSelector("#user-menu-toggle, .usermenu");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl + "/login/index.php?lang=en");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        return this;
    }

    public void loginAs(String user, String password) {
        driver.findElement(USERNAME).sendKeys(user);
        driver.findElement(PASSWORD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();
    }

    /** Чекає появи меню користувача — ознаки того, що сеанс створено. */
    public void waitForUserMenu() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(USER_MENU));
    }

    /** Чи відображається меню авторизованого користувача у верхній панелі. */
    public boolean isUserMenuDisplayed() {
        return !driver.findElements(USER_MENU).isEmpty()
            && driver.findElement(USER_MENU).isDisplayed();
    }

    /** Чекає і повертає текст повідомлення про помилку входу. */
    public String errorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR)).getText();
    }

    /** Значення поля «Ім'я входу» після відправлення форми (GUI-20: має зберігатися). */
    public String enteredUsername() {
        return driver.findElement(USERNAME).getAttribute("value");
    }

    /** Значення поля «Пароль» після відправлення форми (GUI-20: має очищуватися). */
    public String enteredPassword() {
        return driver.findElement(PASSWORD).getAttribute("value");
    }

    /** Чи є на сторінці форма входу (кнопка «Log in»). */
    public boolean isLoginFormPresent() {
        return !driver.findElements(LOGIN_BUTTON).isEmpty();
    }
}