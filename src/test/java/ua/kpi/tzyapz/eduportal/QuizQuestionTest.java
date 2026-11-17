package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести {@link QuizQuestion}: ключ питання, на якому тримаються BL-40 і BL-50.
 * Записи get/set окремих тестів не потребують, але конструктор цього запису має валідацію
 * (контракт описано у Javadoc класу), а {@code isSingle()} вирішує, за якою формулою — BL-40 чи BL-50 —
 * рахується бал, тож вони перевіряються тут.
 *
 * @author Керничний С.С.
 */
@Tag("unit")
@DisplayName("QuizQuestion — ключ питання для BL-40/BL-50")
class QuizQuestionTest {

    @Test
    @DisplayName("BL-40/BL-50: коректне питання зберігає ідентифікатор, варіанти та правильні відповіді")
    void validQuestionKeepsItsData() {
        // arrange + act
        QuizQuestion question = new QuizQuestion("q1", Set.of("a", "b", "c"), Set.of("a", "c"));
        // assert
        assertAll("поля питання",
            () -> assertEquals("q1", question.id()),
            () -> assertEquals(Set.of("a", "b", "c"), question.options()),
            () -> assertEquals(Set.of("a", "c"), question.correct()));
    }

    @Test
    @DisplayName("BL-40/BL-50: isSingle() — true лише для рівно однієї правильної відповіді")
    void isSingleDependsOnNumberOfCorrectOptions() {
        // arrange
        QuizQuestion oneCorrect = new QuizQuestion("q1", Set.of("a", "b"), Set.of("a"));
        QuizQuestion twoCorrect = new QuizQuestion("q2", Set.of("a", "b", "c"), Set.of("a", "b"));
        QuizQuestion allCorrect = new QuizQuestion("q3", Set.of("a", "b"), Set.of("a", "b"));
        // assert
        assertAll("isSingle",
            () -> assertTrue(oneCorrect.isSingle()),
            () -> assertFalse(twoCorrect.isSingle()),
            () -> assertFalse(allCorrect.isSingle()));
    }

    @Test
    @DisplayName("BL-40/BL-50: порожній id, порожні варіанти/правильні або правильні поза варіантами → IllegalArgumentException")
    void invalidQuestionThrows() {
        assertAll("некоректне питання",
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion(null, Set.of("a", "b"), Set.of("a"))),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("   ", Set.of("a", "b"), Set.of("a"))),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("q1", null, Set.of("a"))),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("q1", Set.of(), Set.of("a"))),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("q1", Set.of("a", "b"), null)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("q1", Set.of("a", "b"), Set.of())),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new QuizQuestion("q1", Set.of("a", "b"), Set.of("z"))));
    }
}
