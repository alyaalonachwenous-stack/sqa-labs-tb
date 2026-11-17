package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести {@link QuizScorer}: BL-40 (питання з однією правильною відповіддю),
 * BL-50 (кілька правильних, частковий бал), BL-60 (підсумок спроби у відсотках).
 * <p>
 * Очікувані значення обчислено вручну зі специфікації SRS v1.1 (розділ 5), а не з поведінки коду.
 * Тест, що падає на модулі v1, лишається «червоним» і відповідає звіту про дефект
 * (див. docs/sprint4/bug-reports.md).
 *
 * @author Керничний С.С.
 */
@Tag("unit")
@DisplayName("QuizScorer — оцінювання питань і спроби тесту (BL-40…BL-60)")
class QuizScorerTest {

    private static final double EPS = 0.001;

    /** Одна правильна відповідь: варіанти a–d, правильний — b (BL-40). */
    private static final QuizQuestion SINGLE =
        new QuizQuestion("q1", Set.of("a", "b", "c", "d"), Set.of("b"));
    /** Кілька правильних: правильні a, b (2 шт.), неправильні c, d (2 шт.) (BL-50). */
    private static final QuizQuestion MULTI =
        new QuizQuestion("q2", Set.of("a", "b", "c", "d"), Set.of("a", "b"));
    /** Усі варіанти правильні — неправильних немає, штраф не діє (BL-50). */
    private static final QuizQuestion MULTI_ALL_CORRECT =
        new QuizQuestion("q3", Set.of("a", "b"), Set.of("a", "b"));

    private QuizScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new QuizScorer();
    }

    /** "a|b" → {a, b}: зручний запис набору обраних варіантів у @CsvSource. */
    private static Set<String> set(String options) {
        return Set.of(options.split("\\|"));
    }

    /** Ключ із n питань: варіанти a/b, правильний — a. */
    private static List<QuizQuestion> singleChoiceKey(int n) {
        List<QuizQuestion> key = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            key.add(new QuizQuestion("q" + i, Set.of("a", "b"), Set.of("a")));
        }
        return key;
    }

    /** Відповіді на n питань: перші {@code correct} — правильно ("a"), решта — неправильно ("b"). */
    private static Map<String, Set<String>> answers(int total, int correct) {
        Map<String, Set<String>> given = new HashMap<>();
        for (int i = 1; i <= total; i++) {
            given.put("q" + i, Set.of(i <= correct ? "a" : "b"));
        }
        return given;
    }

    // ============================================================ BL-40 (одна правильна)

    @Test
    @DisplayName("BL-40: обрано правильний варіант — 1 бал")
    void singleCorrectOptionGivesOnePoint() {
        // act
        double score = scorer.questionScore(SINGLE, Set.of("b"));
        // assert
        assertEquals(1.0, score, EPS);
    }

    @ParameterizedTest(name = "BL-40: обрано {0} (не лише правильний) → 0 балів")
    @CsvSource({"a", "d", "b|c", "a|b|c|d"})
    void singleWrongOrExtraOptionsGiveZero(String selected) {
        // arrange: «саме правильний варіант і лише його» — зайвий варіант поруч із правильним теж дає 0
        // act + assert
        assertEquals(0.0, scorer.questionScore(SINGLE, set(selected)), EPS);
    }

    @ParameterizedTest(name = "BL-40/BL-50: відповіді немає ({0}) → 0 балів")
    @NullAndEmptySource
    void noAnswerGivesZero(Set<String> selected) {
        assertAll("питання без відповіді",
            () -> assertEquals(0.0, scorer.questionScore(SINGLE, selected), EPS),
            () -> assertEquals(0.0, scorer.questionScore(MULTI, selected), EPS));
    }

    @Test
    @DisplayName("BL-40/BL-50: обрано варіант, якого немає серед варіантів питання → IllegalArgumentException")
    void unknownOptionThrows() {
        assertAll("невідомий варіант",
            () -> assertThrows(IllegalArgumentException.class, () -> scorer.questionScore(SINGLE, Set.of("z"))),
            () -> assertThrows(IllegalArgumentException.class, () -> scorer.questionScore(MULTI, Set.of("a", "z"))));
    }

    // ============================================================ BL-50 (кілька правильних)

    @ParameterizedTest(name = "BL-50: обрано {0} → {1} бала")
    @CsvSource({
        "a|b, 1.0",        // обидва правильні, жодного неправильного: 2/2 − 0 = 1
        "a, 0.5",          // 1/2 − 0 = 0,5
        "b, 0.5",
        "a|b|c, 0.5",      // 2/2 − 1/2 = 0,5
        "a|b|d, 0.5",
        "a|c, 0.0",        // 1/2 − 1/2 = 0
        "a|b|c|d, 0.0"     // 2/2 − 2/2 = 0
    })
    void multiPartialScore(String selected, double expected) {
        assertEquals(expected, scorer.questionScore(MULTI, set(selected)), EPS);
    }

    // ДЕФЕКТ BUG-M03: у v1 різниця часток не обмежується знизу нулем, тож повертається від'ємний бал
    // (c|d → −1,0; c → −0,5; a|c|d → −0,5), а за BL-50 бал «не менше 0».
    @ParameterizedTest(name = "BL-50: обрано {0} → бал не менше 0 (очікуємо {1})")
    @CsvSource({
        "c|d, 0.0",        // 0/2 − 2/2 = −1 → 0
        "c, 0.0",          // 0/2 − 1/2 = −0,5 → 0
        "a|c|d, 0.0"       // 1/2 − 2/2 = −0,5 → 0
    })
    void multiScoreIsNeverNegative(String selected, double expected) {
        assertEquals(expected, scorer.questionScore(MULTI, set(selected)), EPS);
    }

    @ParameterizedTest(name = "BL-50: неправильних варіантів немає, обрано {0} → {1} (друга частка = 0)")
    @CsvSource({"a|b, 1.0", "a, 0.5"})
    void multiWithoutWrongOptionsHasNoPenalty(String selected, double expected) {
        assertEquals(expected, scorer.questionScore(MULTI_ALL_CORRECT, set(selected)), EPS);
    }

    // ============================================================ BL-60 (підсумок спроби)

    // ДЕФЕКТ BUG-M04: у v1 підсумок округлюється до цілого (Math.round), а за BL-60 — до 1 знака після коми.
    // Падають усі рядки, де відсоток не є цілим або має .x5 (33,3; 66,7; 16,7; 14,3; 12,5; 6,3).
    @ParameterizedTest(name = "BL-60: {1} з {0} питань правильно → {2} %")
    @CsvSource({
        "3, 0, 0.0", "3, 1, 33.3", "3, 2, 66.7", "3, 3, 100.0",
        "6, 1, 16.7", "7, 1, 14.3",
        "8, 1, 12.5",      // 12,5 вже має один знак
        "16, 1, 6.3"       // 6,25 → HALF_UP → 6,3 (HALF_EVEN дав би 6,2)
    })
    void attemptScoreIsRoundedToOneDecimal(int totalQuestions, int correctAnswers, double expected) {
        // arrange
        List<QuizQuestion> key = singleChoiceKey(totalQuestions);
        Map<String, Set<String>> given = answers(totalQuestions, correctAnswers);
        // act
        double percent = scorer.score(given, key);
        // assert
        assertEquals(expected, percent, EPS);
    }

    @Test
    @DisplayName("BL-60: кількість питань береться з ключа, а не з кількості наданих відповідей")
    void attemptScoreUsesKeySizeNotAnswersCount() {
        // arrange: у ключі 4 питання, студент відповів лише на 2 (обидві правильно) → 2/4 · 100 = 50
        List<QuizQuestion> key = singleChoiceKey(4);
        Map<String, Set<String>> given = answers(2, 2);
        // act
        double percent = scorer.score(given, key);
        // assert
        assertEquals(50.0, percent, EPS);
    }

    @Test
    @DisplayName("BL-60: підсумок складається з балів за питання, зокрема часткових (1 + 0,5) / 2 = 75 %")
    void attemptScoreSumsPartialCredit() {
        // arrange: SINGLE → правильно (1 бал); MULTI → обрано лише a (0,5 бала); (1 + 0,5) / 2 · 100 = 75
        List<QuizQuestion> key = List.of(SINGLE, MULTI);
        Map<String, Set<String>> given = Map.of("q1", Set.of("b"), "q2", Set.of("a"));
        // act
        double percent = scorer.score(given, key);
        // assert
        assertEquals(75.0, percent, EPS);
    }

    @Test
    @DisplayName("BL-60: порожній або відсутній ключ тесту → IllegalArgumentException")
    void emptyKeyThrows() {
        assertAll("ключ тесту",
            () -> assertThrows(IllegalArgumentException.class, () -> scorer.score(answers(1, 1), List.of())),
            () -> assertThrows(IllegalArgumentException.class, () -> scorer.score(answers(1, 1), null)));
    }

    @Test
    @DisplayName("BL-60: відповідей немає (null або порожня мапа) → 0 % за непорожнім ключем")
    void missingAnswersGiveZeroPercent() {
        // arrange
        List<QuizQuestion> key = singleChoiceKey(3);
        // act + assert
        assertAll("без відповідей",
            () -> assertEquals(0.0, scorer.score(null, key), EPS),
            () -> assertEquals(0.0, scorer.score(Map.of(), key), EPS));
    }
}
