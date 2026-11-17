package ua.kpi.tzyapz.eduportal.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ua.kpi.tzyapz.eduportal.ui.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тести входу в «ЕдуПортал» (F-10, GUI-10, GUI-20, GUI-75) на демо Moodle Sandbox.
 *
 * @author Черноус А.Д.
 */
@Tag("ui")
@DisplayName("ЕдуПортал (Moodle Sandbox): вхід користувача — F-10")
class LoginTest extends UiTestBase {

    @Test
    @DisplayName("F-10 · TC-001: правильні облікові дані студента виконують вхід у систему")
    void validLoginOpensDashboard() {
        // arrange
        LoginPage login = new LoginPage(driver, wait).open(BASE_URL);
        // act
        login.loginAs(STUDENT_LOGIN, STUDENT_PASSWORD);
        // умова-передумова (жорстка): сеанс створено — у верхній панелі з'явилось меню користувача
        login.waitForUserMenu();
        // assert (м'яка перевірка: ознаки активного сеансу)
        String url = driver.getCurrentUrl();
        assertAll("F-10: успішний вхід",
            () -> assertTrue(login.isUserMenuDisplayed(), "Меню користувача має бути видимим після входу"),
            () -> assertFalse(url.contains("/login/"), "Користувач лишився на сторінці входу: " + url),
            () -> assertFalse(login.isLoginFormPresent(), "Форма входу не має бути видимою після входу"));
    }

    @Test
    @DisplayName("F-10/GUI-20 · TC-003: неправильний пароль — повідомлення про помилку, вхід не виконано")
    void invalidPasswordShowsErrorAndKeepsUsername() {
        // arrange
        LoginPage login = new LoginPage(driver, wait).open(BASE_URL);
        // act
        login.loginAs(STUDENT_LOGIN, "wrong_password_1");
        String error = login.errorText();
        // assert (м'яка перевірка: повідомлення, URL і стан полів — властивості одного результату)
        String url = driver.getCurrentUrl();
        assertAll("F-10/GUI-20: невдалий вхід",
            () -> assertTrue(
                error.toLowerCase().contains("unable to log in") || error.toLowerCase().contains("invalid login"),
                "Очікували повідомлення про невірні дані входу, отримали: " + error),
            () -> assertTrue(url.contains("/login/"), "Користувач має лишитись на сторінці входу: " + url),
            () -> assertEquals("", login.enteredPassword(), "GUI-20: пароль має очищуватися"));
    }
}