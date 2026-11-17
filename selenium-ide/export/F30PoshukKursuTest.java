// ЧЕРНЕТКА експорту Selenium IDE → Java JUnit (формат експортера: JUnit 4, Hamcrest, без headless).
// Довідковий файл: поза src/, Maven його НЕ збирає. Здається адаптований WebDriver-тест
// (src/test/java/.../ui/CourseCatalogTest.java), а не цей файл.
// Оновіть цей файл справжнім експортом зі свого Selenium IDE (див. F10VkhidStudentaTest.java).
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class F30PoshukKursuTest {
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
  public void f30PoshukKursu() {
    driver.get("https://sandbox.moodledemo.net/course/search.php?lang=en");
    driver.manage().window().setSize(new Dimension(1280, 900));
    {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.cssSelector("#coursesearchbox, #region-main input[name='search']")));
    }
    driver.findElement(By.cssSelector("#coursesearchbox, #region-main input[name='search']")).sendKeys("first");
    driver.findElement(By.cssSelector("#coursesearchbox, #region-main input[name='search']")).sendKeys(Keys.ENTER);
    {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("My first course")));
    }
    // assertText — жорстка перевірка
    assertThat(driver.findElement(By.linkText("My first course")).getText(), is("My first course"));
    // verifyElementNotPresent / verifyElementPresent — м'які перевірки
    {
      List<WebElement> elements = driver.findElements(By.linkText("My second course"));
      assert(elements.size() == 0);
    }
    {
      List<WebElement> elements = driver.findElements(By.cssSelector("a[href*='/course/view.php?id=']"));
      assert(elements.size() > 0);
    }
  }
}
