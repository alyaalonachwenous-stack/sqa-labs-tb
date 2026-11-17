package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести {@link GradeCalculator}: BL-10 (зважений підсумок), BL-20 (шкала ECTS),
 * BL-30 (штраф за запізнення).
 * <p>
 * Очікувані значення обчислено вручну зі специфікації SRS v1.1 (розділ 5), а не з поведінки коду.
 * Тест, що падає на модулі v1, лишається «червоним»: він відповідає зареєстрованому звіту про дефект
 * (ID звіту — у коментарі над тестом; див. docs/sprint4/bug-reports.md).
 *
 * @author Матюшенко А.В.
 */
@Tag("unit")
@DisplayName("GradeCalculator — підсумок курсу, шкала ECTS, штраф за запізнення (BL-10…BL-30)")
class GradeCalculatorTest {

    /** Допуск порівняння double: результати модуля округлено до 0,01, тож 0,001 розрізняє сусідні значення. */
    private static final double EPS = 0.001;

    private GradeCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new GradeCalculator();
    }

    // ===================================================================== BL-10

    @Test
    @DisplayName("BL-10: підсумок 70×30% + 85×20% + 95×50% = 85,50")
    void weightedTotalOfThreeItems() {
        // arrange: 70·0,30 + 85·0,20 + 95·0,50 = 21 + 17 + 47,5 = 85,5
        List<GradeItem> items = List.of(
            new GradeItem("Лабораторні роботи", 70, 30),
            new GradeItem("Модульна контрольна", 85, 20),
            new GradeItem("Іспит", 95, 50));
        // act
        double total = calc.weightedTotal(items);
        // assert
        assertEquals(85.50, total, EPS);
    }

    @Test
    @DisplayName("BL-10: підсумок округлюється до 2 знаків за HALF_UP (0,125 → 0,13; 2,375 → 2,38)")
    void weightedTotalIsRoundedHalfUp() {
        // arrange: 12,5·1% = 0,125 і 95·2,5% = 2,375 — обидва числа точно подаються у double,
        // тож результат залежить лише від режиму округлення (HALF_EVEN дав би 0,12 і 2,38 → відрізнився б на першому)
        List<GradeItem> first = List.of(new GradeItem("A", 12.5, 1), new GradeItem("B", 0, 99));
        List<GradeItem> second = List.of(new GradeItem("A", 95, 2.5), new GradeItem("B", 0, 97.5));
        // act + assert (два значення одного правила — м'яка перевірка)
        assertAll("HALF_UP до 2 знаків",
            () -> assertEquals(0.13, calc.weightedTotal(first), EPS),
            () -> assertEquals(2.38, calc.weightedTotal(second), EPS));
    }

    @ParameterizedTest(name = "BL-10: один елемент: бал {0}, вага 100 → підсумок {1}")
    @CsvSource({"0, 0.00", "0.01, 0.01", "50, 50.00", "99.99, 99.99", "100, 100.00"})
    void weightedTotalAtScoreBoundaries(double score, double expected) {
        // arrange
        List<GradeItem> items = List.of(new GradeItem("Іспит", score, 100));
        // act
        double total = calc.weightedTotal(items);
        // assert
        assertEquals(expected, total, EPS);
    }

    @ParameterizedTest(name = "BL-10: ваги 40 + {0} ≠ 100 → IllegalArgumentException")
    @CsvSource({"0", "59.99", "60.01", "100"})
    void weightsNotSummingTo100Throw(double secondWeight) {
        // arrange: сума ваг = 40 + secondWeight: 40, 99,99, 100,01 та 140 — жодна не дорівнює 100
        List<GradeItem> items = List.of(
            new GradeItem("Лабораторні роботи", 80, 40),
            new GradeItem("Іспит", 90, secondWeight));
        // act + assert
        assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(items));
    }

    @Test
    @DisplayName("BL-10: null/порожній перелік, бал поза 0..100, від'ємна вага → IllegalArgumentException")
    void invalidItemsThrow() {
        // arrange
        List<GradeItem> scoreBelowZero = List.of(new GradeItem("Іспит", -0.01, 100));
        List<GradeItem> scoreAbove100 = List.of(new GradeItem("Іспит", 100.01, 100));
        // сума ваг тут дорівнює 100, тож виняток спричиняє саме від'ємна вага
        List<GradeItem> negativeWeight = List.of(new GradeItem("A", 80, -10), new GradeItem("B", 90, 110));
        // act + assert
        assertAll("BL-10: некоректні вхідні дані",
            () -> assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(List.of())),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(scoreBelowZero)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(scoreAbove100)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(negativeWeight)));
    }

    // ===================================================================== BL-20

    // ДЕФЕКТ BUG-M01: у v1 рядок «95, A» падає (повертається "B"): умова «total > 95» замість «total ≥ 95».
    @ParameterizedTest(name = "BL-20: ectsLetter({0}) = {1}")
    @CsvSource({
        "100, A", "95.01, A", "95, A", "94.99, B",
        "85.01, B", "85, B", "84.99, C",
        "75.01, C", "75, C", "74.99, D",
        "65.01, D", "65, D", "64.99, E",
        "60.01, E", "60, E", "59.99, Fx",
        "35.01, Fx", "35, Fx", "34.99, F",
        "0, F"
    })
    void ectsLetterAtBoundaries(double total, String expectedLetter) {
        // act
        String letter = calc.ectsLetter(total);
        // assert
        assertEquals(expectedLetter, letter);
    }

    @ParameterizedTest(name = "BL-20: ectsLetter({0}) → IllegalArgumentException")
    @ValueSource(doubles = {-0.01, 100.01, -100.0, 1000.0})
    void ectsLetterOutOfRangeThrows(double total) {
        assertThrows(IllegalArgumentException.class, () -> calc.ectsLetter(total));
    }

    // ===================================================================== BL-30

    // ДЕФЕКТ BUG-M02: у v1 штраф складний (score·0,9^днів), а не лінійний −10 % за день;
    // падають рядки для 2…5 днів, а також ліміт «не більше −50 %» (5 днів дає −40,95 %).
    @ParameterizedTest(name = "BL-30: бал {0}, запізнення {1} дн. → {2}")
    @CsvSource({
        "80, 0, 80.00", "80, 1, 72.00", "80, 2, 64.00", "80, 3, 56.00",
        "80, 4, 48.00", "80, 5, 40.00", "80, 6, 40.00", "80, 30, 40.00"
    })
    void latePenaltyByDays(double score, int daysLate, double expected) {
        // arrange: очікуване = score · (1 − 0,1 · min(днів, 5)); для 80 балів: 72, 64, 56, 48, 40, далі 40 (ліміт −50 %)
        // act
        double result = calc.latePenalty(score, daysLate);
        // assert
        assertEquals(expected, result, EPS);
    }

    @ParameterizedTest(name = "BL-30: здано вчасно (0 днів): бал {0} без змін")
    @CsvSource({"0", "55.55", "100"})
    void latePenaltyKeepsScoreWhenOnTime(double score) {
        assertEquals(score, calc.latePenalty(score, 0), EPS);
    }

    @ParameterizedTest(name = "BL-30: бал {0}, 1 день запізнення → {1} (округлення до 2 знаків HALF_UP)")
    @CsvSource({"0.25, 0.23", "77.77, 69.99"})
    void latePenaltyIsRoundedHalfUp(double score, double expected) {
        // arrange: 0,25·0,9 = 0,225 → 0,23 (HALF_UP); 77,77·0,9 = 69,993 → 69,99
        // act + assert
        assertEquals(expected, calc.latePenalty(score, 1), EPS);
    }

    @Test
    @DisplayName("BL-30: бал поза 0..100 або від'ємна кількість днів → IllegalArgumentException")
    void latePenaltyInvalidArgumentsThrow() {
        assertAll("BL-30: некоректні вхідні дані",
            () -> assertThrows(IllegalArgumentException.class, () -> calc.latePenalty(-0.01, 1)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.latePenalty(100.01, 1)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.latePenalty(80, -1)));
    }
}
