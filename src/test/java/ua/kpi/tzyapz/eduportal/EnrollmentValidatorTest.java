package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести {@link EnrollmentValidator}: BL-70 (чотири умови самозарахування та рішення canEnroll)
 * і BL-80 (перелік порушених умов).
 * <p>
 * Очікувані значення взято зі SRS v1.1 (розділ 5). Для BL-80 формулювання SRS нечітке («у зручному для
 * користувача вигляді»), тому конкретні коди й порядок (KEY, PERIOD, CAPACITY, PREREQUISITE) взято з README
 * модуля та Javadoc методу violations — див. примітку в docs/sprint4/coverage-matrix.md.
 * <p>
 * Тести violations навмисно використовують «безпечні» значення (дата до початку періоду, enrolled &gt; capacity),
 * щоб їхній результат не залежав від дефектів BUG-M05/BUG-M06 і червоніли лише тести, що фіксують ці дефекти.
 *
 * @author Черноус А.Д.
 */
@Tag("unit")
@DisplayName("EnrollmentValidator — умови самозарахування і перелік порушень (BL-70, BL-80)")
class EnrollmentValidatorTest {

    private static final LocalDate START = LocalDate.of(2026, 9, 1);
    private static final LocalDate END = LocalDate.of(2026, 9, 30);
    private static final String KEY = "edu2026";
    private static final String PREREQUISITE = "prog-basics";
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 8);

    private EnrollmentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EnrollmentValidator();
    }

    /** Курс із періодом 01.09.2026–30.09.2026 і кодовим словом "edu2026". */
    private static Course course(int capacity, int enrolled, String prerequisiteId) {
        return new Course("tzyapz", KEY, START, END, capacity, enrolled, prerequisiteId);
    }

    /** Студент, що склав передумовний курс "prog-basics" із заданим балом. */
    private static Student studentWithPrerequisiteGrade(double grade) {
        return new Student("s001", Map.of(PREREQUISITE, grade));
    }

    // ================================================== BL-70 (1): кодове слово

    @Test
    @DisplayName("BL-70(1): кодове слово збігається точно — keyMatches = true")
    void keyMatchesExactKey() {
        assertTrue(validator.keyMatches(course(25, 10, null), "edu2026"));
    }

    @ParameterizedTest(name = "BL-70(1): кодове слово \"{0}\" ≠ \"edu2026\" → false")
    @ValueSource(strings = {"EDU2026", "Edu2026", "edu2025", "edu2026 ", " edu2026", "edu", ""})
    void keyIsCaseSensitiveAndExact(String wrongKey) {
        // arrange: збіг має бути точним, з урахуванням регістру; пробіл на краю — вже інший рядок
        // act + assert
        assertFalse(validator.keyMatches(course(25, 10, null), wrongKey));
    }

    // ================================================== BL-70 (2): період зарахування

    @ParameterizedTest(name = "BL-70(2): {0} відносно початку періоду 01.09.2026 → {1}")
    @CsvSource({"2026-08-31, false", "2026-09-01, true", "2026-09-02, true"})
    void withinPeriodAtStartBoundary(LocalDate today, boolean expected) {
        assertEquals(expected, validator.withinPeriod(course(25, 10, null), today));
    }

    // ДЕФЕКТ BUG-M05: у v1 withinPeriod не перевіряє кінець періоду — рядки 2026-10-01 та 2027-01-01 падають
    // (повертається true), бо є лише умова «не раніше початку».
    @ParameterizedTest(name = "BL-70(2): {0} відносно кінця періоду 30.09.2026 → {1}")
    @CsvSource({"2026-09-29, true", "2026-09-30, true", "2026-10-01, false", "2027-01-01, false"})
    void withinPeriodAtEndBoundary(LocalDate today, boolean expected) {
        assertEquals(expected, validator.withinPeriod(course(25, 10, null), today));
    }

    // ================================================== BL-70 (3): вільні місця

    // ДЕФЕКТ BUG-M06: у v1 hasCapacity порівнює «enrolled <= capacity» замість «enrolled < capacity» —
    // падають рядки «25, 25» (курс заповнений) та «0, 0» (місткість нуль).
    @ParameterizedTest(name = "BL-70(3): місткість {0}, зараховано {1} → вільні місця: {2}")
    @CsvSource({"25, 0, true", "25, 24, true", "25, 25, false", "25, 26, false", "0, 0, false"})
    void hasCapacityAtBoundary(int capacity, int enrolled, boolean expected) {
        assertEquals(expected, validator.hasCapacity(course(capacity, enrolled, null)));
    }

    // ================================================== BL-70 (4): передумовний курс

    @ParameterizedTest(name = "BL-70(4): підсумок передумови {0} (поріг 60) → {1}")
    @CsvSource({"0.0, false", "59.99, false", "60.0, true", "60.01, true", "100.0, true"})
    void prerequisiteMetAtPassThreshold(double grade, boolean expected) {
        // arrange
        Course course = course(25, 10, PREREQUISITE);
        Student student = studentWithPrerequisiteGrade(grade);
        // act + assert
        assertEquals(expected, validator.prerequisiteMet(student, course));
    }

    @Test
    @DisplayName("BL-70(4): передумови немає — true; передумову не складено або складено інший курс — false")
    void prerequisiteMetWhenNoPrerequisiteOrNotCompleted() {
        // arrange
        Course withoutPrerequisite = course(25, 10, null);
        Course withPrerequisite = course(25, 10, PREREQUISITE);
        Student noCourses = new Student("s002", Map.of());
        Student otherCourseOnly = new Student("s003", Map.of("math", 95.0));
        Student nullCourses = new Student("s004", null);
        // act + assert
        assertAll("передумовний курс",
            () -> assertTrue(validator.prerequisiteMet(noCourses, withoutPrerequisite), "передумови немає"),
            () -> assertFalse(validator.prerequisiteMet(noCourses, withPrerequisite), "курсів немає"),
            () -> assertFalse(validator.prerequisiteMet(otherCourseOnly, withPrerequisite), "складено інший курс"),
            () -> assertFalse(validator.prerequisiteMet(nullCourses, withPrerequisite), "null-мапа оцінок"));
    }

    // ================================================== BL-80: violations

    @Test
    @DisplayName("BL-80: усі умови виконано — перелік порушень порожній")
    void violationsEmptyWhenAllConditionsMet() {
        // act
        List<String> result = validator.violations(
            studentWithPrerequisiteGrade(75.0), course(25, 10, PREREQUISITE), TODAY, KEY);
        // assert
        assertEquals(List.of(), result);
    }

    @ParameterizedTest(name = "BL-80: ключ={0}, дата={1}, зараховано={2}, оцінка передумови={3} → [{4}]")
    @CsvSource({
        "wrong,   2026-09-08, 10, 75.0, KEY",
        "edu2026, 2026-08-31, 10, 75.0, PERIOD",
        "edu2026, 2026-09-08, 30, 75.0, CAPACITY",
        "edu2026, 2026-09-08, 10, 59.0, PREREQUISITE"
    })
    void violationsReportsSingleViolationCode(String key, LocalDate today, int enrolled, double grade,
                                              String expectedCode) {
        // arrange: порушено рівно одну умову
        Course course = course(25, enrolled, PREREQUISITE);
        Student student = studentWithPrerequisiteGrade(grade);
        // act
        List<String> result = validator.violations(student, course, today, key);
        // assert
        assertEquals(List.of(expectedCode), result);
    }

    @Test
    @DisplayName("BL-80: кілька порушень повертаються у порядку перевірки: KEY, PERIOD, CAPACITY, PREREQUISITE")
    void violationsKeepCheckOrder() {
        // arrange
        Student weak = studentWithPrerequisiteGrade(10.0);
        Course full = course(25, 30, PREREQUISITE);
        Student good = studentWithPrerequisiteGrade(75.0);
        // act + assert (порядок важливий, тому assertIterableEquals)
        assertAll("порядок кодів порушень",
            () -> assertIterableEquals(List.of("KEY", "PERIOD", "CAPACITY", "PREREQUISITE"),
                validator.violations(weak, full, LocalDate.of(2026, 8, 31), "wrong")),
            () -> assertIterableEquals(List.of("KEY", "CAPACITY"),
                validator.violations(good, full, TODAY, "wrong")),
            () -> assertIterableEquals(List.of("PERIOD", "PREREQUISITE"),
                validator.violations(weak, course(25, 10, PREREQUISITE), LocalDate.of(2026, 8, 31), KEY)));
    }

    @Test
    @DisplayName("BL-80: null у будь-якому з чотирьох аргументів → IllegalArgumentException")
    void violationsRejectNullArguments() {
        // arrange
        Student student = studentWithPrerequisiteGrade(75.0);
        Course course = course(25, 10, PREREQUISITE);
        // act + assert
        assertAll("null-аргументи",
            () -> assertThrows(IllegalArgumentException.class, () -> validator.violations(null, course, TODAY, KEY)),
            () -> assertThrows(IllegalArgumentException.class, () -> validator.violations(student, null, TODAY, KEY)),
            () -> assertThrows(IllegalArgumentException.class, () -> validator.violations(student, course, null, KEY)),
            () -> assertThrows(IllegalArgumentException.class, () -> validator.violations(student, course, TODAY, null)));
    }

    // ================================================== BL-70: canEnroll

    @Test
    @DisplayName("BL-70: усі умови на межі виконано (останній день, останнє місце, рівно 60 балів) — зарахування дозволено")
    void canEnrollWhenEveryConditionHoldsAtItsBoundary() {
        // arrange: останній день періоду, одне вільне місце (24 з 25), передумова рівно на порозі 60
        Course course = course(25, 24, PREREQUISITE);
        Student student = studentWithPrerequisiteGrade(60.0);
        Course noPrerequisite = course(25, 24, null);
        // act + assert
        assertAll("межові випадки дозволеного зарахування",
            () -> assertTrue(validator.canEnroll(student, course, END, KEY)),
            () -> assertTrue(validator.canEnroll(new Student("s005", Map.of()), noPrerequisite, START, KEY)));
    }

    @Test
    @DisplayName("BL-70: хибне кодове слово, дата до початку або непройдена передумова — зарахування заборонено")
    void cannotEnrollWithWrongKeyBeforeStartOrWithoutPrerequisite() {
        // arrange
        Course course = course(25, 10, PREREQUISITE);
        Student good = studentWithPrerequisiteGrade(75.0);
        Student weak = studentWithPrerequisiteGrade(59.99);
        // act + assert
        assertAll("заборонені випадки",
            () -> assertFalse(validator.canEnroll(good, course, TODAY, "EDU2026"), "ключ з іншим регістром"),
            () -> assertFalse(validator.canEnroll(good, course, LocalDate.of(2026, 8, 31), KEY), "до початку періоду"),
            () -> assertFalse(validator.canEnroll(weak, course, TODAY, KEY), "передумова 59,99 < 60"));
    }

    // ДЕФЕКТ BUG-M05 (той самий, що й у withinPeriodAtEndBoundary): після останнього дня періоду v1 дозволяє зарахування.
    @Test
    @DisplayName("BL-70(2): наступного дня після завершення періоду зарахування заборонено")
    void cannotEnrollAfterPeriodEnd() {
        // arrange: усі інші умови виконано, дата — 01.10.2026, на день пізніше за enrolEnd
        Course course = course(25, 10, PREREQUISITE);
        Student student = studentWithPrerequisiteGrade(75.0);
        // act
        boolean allowed = validator.canEnroll(student, course, END.plusDays(1), KEY);
        // assert
        assertFalse(allowed);
    }

    // ДЕФЕКТ BUG-M06 (той самий, що й у hasCapacityAtBoundary): коли зараховано рівно capacity, v1 ще впускає студента.
    @Test
    @DisplayName("BL-70(3): курс заповнений (зараховано = місткість) — зарахування заборонено")
    void cannotEnrollWhenCourseIsFull() {
        // arrange: 25 зараховано з 25, усі інші умови виконано
        Course fullCourse = course(25, 25, PREREQUISITE);
        Student student = studentWithPrerequisiteGrade(75.0);
        // act
        boolean allowed = validator.canEnroll(student, fullCourse, TODAY, KEY);
        // assert
        assertFalse(allowed);
    }
}
