// ЧЕРНЕТКА експорту Selenium IDE → Java JUnit (формат експортера: JUnit 4, Hamcrest, без headless).
// Довідковий файл: поза src/, Maven його НЕ збирає. Здається адаптований WebDriver-тест
// (src/test/java/.../ui/LoginTest.java), а не цей файл.
//
// ЯК ОНОВИТИ ДО СПРАВЖНЬОГО ЕКСПОРТУ: імпортуйте ../eduportal.side у Selenium IDE, відтворіть тест,
// потім ПКМ на тесті → Export → Java JUnit і замініть вміст цього файлу експортом зі свого IDE.
//
// Що саме треба змінити під JUnit 5 (таблиця у README стартового проєкту, розділ 6):
//   org.junit.Test → org.junit.jupiter.api.Test; @Before/@After → @BeforeEach/@AfterEach;
//   Hamcrest assertThat(...) → Assertions.assertEquals/assertTrue; додати @Tag("ui") і headless;
//   замінити pause/Thread.sleep на WebDriverWait + ExpectedConditions.
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class F10VkhidStudentaTest {
  private WebDriver driver;

  @Before
  public void setUp() {
    driver = new ChromeDriver();
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void f10VkhidStudenta() {
    driver.get("https://sandbox.moodledemo.net/login/index.php?lang=en");
    driver.manage().window().setSize(new Dimension(1280, 900));
    {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }
    driver.findElement(By.id("username")).sendKeys("student");
    driver.findElement(By.id("password")).sendKeys("sandbox24");
    driver.findElement(By.id("loginbtn")).click();
    {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#user-menu-toggle, .usermenu")));
    }
    // assertElementPresent — жорстка перевірка
    {
      List<WebElement> elements = driver.findElements(By.cssSelector("#user-menu-toggle, .usermenu"));
      assert(elements.size() > 0);
    }
    // verifyElementNotPresent / verifyElementPresent — м'які перевірки (в експорті IDE це звичайні assert-и)
    {
      List<WebElement> elements = driver.findElements(By.id("loginbtn"));
      assert(elements.size() == 0);
    }
    {
      List<WebElement> elements = driver.findElements(By.cssSelector("body#page-my-index"));
      assert(elements.size() > 0);
    }
  }
}
