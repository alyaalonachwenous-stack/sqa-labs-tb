package ua.kpi.tzyapz.eduportal.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ua.kpi.tzyapz.eduportal.ui.pages.CourseSearchPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тести каталогу курсів «ЕдуПортал» (F-30, F-140) на демо Moodle Sandbox: перелік курсів і пошук без входу.
 *
 * @author Черноус А.Д.
 */
@Tag("ui")
@DisplayName("ЕдуПортал (Moodle Sandbox): каталог курсів і пошук — F-30, F-140")
class CourseCatalogTest extends UiTestBase {

    @Test
    @DisplayName("F-30 · TC-008: гість бачить перелік курсів із назвою та викладачем без входу")
    void homeListsCoursesWithTeacherWithoutLogin() {
        // arrange + act
        CourseSearchPage catalog = new CourseSearchPage(driver, wait).openHome(BASE_URL);
        List<String> names = catalog.courseNames();
        String pageText = catalog.pageText();
        // assert
        assertAll("F-30: перелік курсів",
            () -> assertTrue(names.contains("My first course"), "Немає «My first course» серед: " + names),
            () -> assertTrue(names.contains("My second course"), "Немає «My second course» серед: " + names),
            () -> assertTrue(pageText.contains("Terri Teacher"), "Не показано викладача курсу"));
    }

    @Test
    @DisplayName("F-140 · TC-009: пошук за фрагментом назви повертає лише відповідний курс")
    void searchFindsCourseByNameFragment() {
        // arrange
        CourseSearchPage catalog = new CourseSearchPage(driver, wait).openSearch(BASE_URL);
        // act
        catalog.search("first");
        List<String> names = catalog.courseNames();
        // assert
        assertAll("F-140: результати пошуку «first»",
            () -> assertTrue(names.contains("My first course"), "Немає «My first course» серед: " + names),
            () -> assertFalse(names.contains("My second course"), "«My second course» не має збігатися з «first»: " + names));
    }

    @Test
    @DisplayName("F-30 · TC-011: пошук без збігів показує повідомлення, що жодного курсу не знайдено")
    void searchWithoutMatchesShowsNotFoundMessage() {
        // arrange
        CourseSearchPage catalog = new CourseSearchPage(driver, wait).openSearch(BASE_URL);
        // act
        catalog.search("zzqqxx");
        String pageText = catalog.waitForPageText("No courses were found");
        // assert
        assertAll("F-30: порожній результат пошуку",
            () -> assertTrue(pageText.contains("No courses were found"), "Немає повідомлення про відсутність курсів"),
            () -> assertEquals(0, catalog.courseLinkCount(), "Серед результатів не має бути курсів"));
    }
}
