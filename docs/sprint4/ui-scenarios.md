# Звіти про дефекти модуля «ЕдуПортал» v1 — спринт 4

Дефекти знайдено юніт-тестами JUnit 5 на модулі бізнес-логіки v1.0.0 (пакет `ua.kpi.tzyapz.eduportal`).
Кожен звіт оформлено за шаблоном спринта 3 (ID, Title, Severity, Priority, Description, Steps to reproduce,
Actual result, Expected result, Environment, Attachments) з додатковими полями для модуля: вимога BL, тест-клас і метод.
Перед створенням звіту очікуване значення звірено з вимогою BL у SRS v1.1 і перераховано вручну (див. п. 4.9 методички);
тест не «підганяли» під фактичний результат і не вимикали.


| ID | Вимога | Клас.метод | Severity / Priority | Падаючих запусків | Issue |
|---|---|---|---|---|---|
| BUG-M01 | BL-20 | GradeCalculator.ectsLetter | Major / Medium | 1 | #22 |
| BUG-M02 | BL-30 (F-60) | GradeCalculator.latePenalty | Major / High | 6 | #23 |
| BUG-M03 | BL-50 | QuizScorer.questionScore | Major / High | 3 | #24 |
| BUG-M04 | BL-60 (F-80) | QuizScorer.score | Major / Medium | 6 | #25 |
| BUG-M05 | BL-70 (умова 2), F-40 | EnrollmentValidator.withinPeriod | Major / High | 3 | #26 |
| BUG-M06 | BL-70 (умова 3), F-40 | EnrollmentValidator.hasCapacity | Major / High | 3 | #27 |
| **Разом** | | | | **22** | |

---

**ID:** BUG-M01
**Title:** [module] GradeCalculator.ectsLetter(95.0) повертає «B» замість «A» (BL-20)
**Severity:** Major
**Priority:** Medium
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
За BL-20 літера **A** виставляється, коли `total ≥ 95`. Метод використовує строге порівняння `total > 95`, тому значення рівно 95,00 потрапляє в діапазон B. Помилка стосується лише верхньої межі діапазону A; інші межі (85, 75, 65, 60, 35) метод обробляє правильно. Підсумок курсу округлюється до 2 знаків (BL-10), тож значення рівно 95,00 цілком досяжне на практиці.

**Вимога (простежуваність):** BL-20 (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `GradeCalculatorTest.ectsLetterAtBoundaries` — рядок [3] «BL-20: ectsLetter(95.0) = A». Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `GradeCalculator.ectsLetter` умова `if (total > 95)` замість `if (total >= 95)`.

**Steps to reproduce:**
1. Створити `GradeCalculator calc = new GradeCalculator();`
2. Викликати `calc.ectsLetter(95.0)`.
3. Для порівняння викликати `calc.ectsLetter(95.01)` та `calc.ectsLetter(94.99)`.
Або запустити тест: `mvn test -Dtest=GradeCalculatorTest`.

**Actual result:** `ectsLetter(95.0)` повертає `"B"` (`ectsLetter(95.01)` — `"A"`, `ectsLetter(94.99)` — `"B"`).

**Expected result:** `ectsLetter(95.0)` повертає `"A"` (BL-20: A — `total ≥ 95`).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=GradeCalculatorTest` (у PowerShell: `mvn test "-Dtest=GradeCalculatorTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.GradeCalculatorTest.txt`.


---

**ID:** BUG-M02
**Title:** [module] GradeCalculator.latePenalty: штраф обчислюється складним відсотком (0,9^днів), а не −10 % за день (BL-30)
**Severity:** Major
**Priority:** High
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
BL-30 і F-60: штраф становить −10 % **від балу** за кожен повний день запізнення, але не більше −50 % загалом (тобто множник `1 − 0,1·min(днів, 5)`). У модулі бал множиться на `0,9` у степені кількості днів (складний відсоток), тож з 2-го дня штраф менший за вимогу, а максимальний штраф дорівнює −40,95 % замість −50 %. Для 0 і 1 дня результат збігається з вимогою, тому помилка непомітна на простих прикладах.

**Вимога (простежуваність):** BL-30 (F-60) (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `GradeCalculatorTest.latePenaltyByDays` — рядки [3]–[8] (запізнення 2, 3, 4, 5, 6 і 30 днів). Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `GradeCalculator.latePenalty` застосовано `score * Math.pow(0.9, cappedDays)` замість `score * (1 - 0.1 * cappedDays)`.

**Steps to reproduce:**
1. Створити `GradeCalculator calc = new GradeCalculator();`
2. Викликати `calc.latePenalty(80, 2)`, `calc.latePenalty(80, 3)`, `calc.latePenalty(80, 5)`, `calc.latePenalty(80, 30)`.
Або запустити тест: `mvn test -Dtest=GradeCalculatorTest`.

**Actual result:** `latePenalty(80, 2)` = 64.8; `(80, 3)` = 58.32; `(80, 4)` = 52.49; `(80, 5)` = 47.24; `(80, 6)` = 47.24; `(80, 30)` = 47.24.

**Expected result:** `latePenalty(80, 2)` = 64.00; `(80, 3)` = 56.00; `(80, 4)` = 48.00; `(80, 5)` = 40.00; `(80, 6)` = 40.00; `(80, 30)` = 40.00 (ліміт −50 %).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=GradeCalculatorTest` (у PowerShell: `mvn test "-Dtest=GradeCalculatorTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.GradeCalculatorTest.txt`.


---

**ID:** BUG-M03
**Title:** [module] QuizScorer.questionScore повертає від'ємний бал для питання з кількома правильними відповідями (BL-50)
**Severity:** Major
**Priority:** High
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
BL-50: бал за питання з кількома правильними = (обрані правильні / усі правильні) − (обрані неправильні / усі неправильні), **але не менше 0**. Метод повертає різницю часток без обмеження знизу, тому за переважно неправильних відповідей бал стає від'ємним і зменшує підсумок спроби (BL-60) нижче суми балів за інші питання.

**Вимога (простежуваність):** BL-50 (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `QuizScorerTest.multiScoreIsNeverNegative` — рядки [1]–[3] (обрано c|d, c, a|c|d). Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `QuizScorer.questionScore` останній рядок `return correctShare - wrongShare;` без `Math.max(0.0, …)`.

**Steps to reproduce:**
1. Створити питання `new QuizQuestion("q2", Set.of("a","b","c","d"), Set.of("a","b"))` (правильні — a, b).
2. Викликати `new QuizScorer().questionScore(question, Set.of("c", "d"))`.
3. Повторити для наборів `Set.of("c")` та `Set.of("a", "c", "d")`.
Або запустити тест: `mvn test -Dtest=QuizScorerTest`.

**Actual result:** Обрано c, d → **−1.0**; обрано c → **−0.5**; обрано a, c, d → **−0.5**.

**Expected result:** У всіх трьох випадках бал **0.0** (бал не менше 0).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=QuizScorerTest` (у PowerShell: `mvn test "-Dtest=QuizScorerTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.QuizScorerTest.txt`.


---

**ID:** BUG-M04
**Title:** [module] QuizScorer.score округлює підсумок спроби до цілого, а не до 1 знака після коми (BL-60)
**Severity:** Major
**Priority:** Medium
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
BL-60: підсумок спроби у відсотках округлюється до **1 знака після коми** за HALF_UP. Метод повертає `Math.round(percent)`, тобто ціле число; усі відсотки з дробовою частиною втрачають точність (до 0,5 процентного пункту), а значення виду x,x5 округлюються не за правилом BL-60. Випадки, де відсоток цілий (0 %, 100 %), помилку не показують.

**Вимога (простежуваність):** BL-60 (F-80) (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `QuizScorerTest.attemptScoreIsRoundedToOneDecimal` — рядки [2], [3], [5], [6], [7], [8] (1/3, 2/3, 1/6, 1/7, 1/8, 1/16 правильних). Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `QuizScorer.score` використано `(double) Math.round(percent)` замість округлення `BigDecimal` до 1 знака (HALF_UP).

**Steps to reproduce:**
1. Створити ключ із 3 питань з однією правильною відповіддю (варіанти a/b, правильний — a).
2. Передати відповіді, де правильно відповіли на 1 питання з 3, і викликати `new QuizScorer().score(answers, key)`.
3. Повторити для 2 з 3, 1 з 8 (12,5 %) та 1 з 16 (6,25 %).
Або запустити тест: `mvn test -Dtest=QuizScorerTest`.

**Actual result:** 1 з 3 → 33.0; 2 з 3 → 67.0; 1 з 6 → 17.0; 1 з 7 → 14.0; 1 з 8 → 13.0; 1 з 16 → 6.0.

**Expected result:** 1 з 3 → 33.3; 2 з 3 → 66.7; 1 з 6 → 16.7; 1 з 7 → 14.3; 1 з 8 → 12.5; 1 з 16 → 6.3 (HALF_UP).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=QuizScorerTest` (у PowerShell: `mvn test "-Dtest=QuizScorerTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.QuizScorerTest.txt` .


---

**ID:** BUG-M05
**Title:** [module] EnrollmentValidator.withinPeriod не перевіряє кінець періоду зарахування — зарахування можливе після закриття (BL-70)
**Severity:** Major
**Priority:** High
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
BL-70(2): `today` має бути у межах періоду зарахування **включно з датами початку і кінця**. Метод перевіряє лише, що дата не раніша за `enrolStart`, а верхню межу `enrolEnd` ігнорує. У результаті студент може зарахуватися на курс після завершення періоду, а `violations(...)` не повертає код `PERIOD`.

**Вимога (простежуваність):** BL-70 (умова 2), F-40 (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `EnrollmentValidatorTest.withinPeriodAtEndBoundary` — рядки [3] (2026-10-01) і [4] (2027-01-01); `EnrollmentValidatorTest.cannotEnrollAfterPeriodEnd`. Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `EnrollmentValidator.withinPeriod` лише `!today.isBefore(course.enrolStart())`, немає `&& !today.isAfter(course.enrolEnd())`.

**Steps to reproduce:**
1. Створити курс `new Course("tzyapz", "edu2026", LocalDate.of(2026,9,1), LocalDate.of(2026,9,30), 25, 10, null)`.
2. Викликати `new EnrollmentValidator().withinPeriod(course, LocalDate.of(2026,10,1))`.
3. Викликати `canEnroll(student, course, LocalDate.of(2026,10,1), "edu2026")` для студента без вимог до передумови.
Або запустити тест: `mvn test -Dtest=EnrollmentValidatorTest`.

**Actual result:** `withinPeriod(course, 2026-10-01)` = `true`; `withinPeriod(course, 2027-01-01)` = `true`; `canEnroll(...)` = `true`.

**Expected result:** `withinPeriod` = `false` для будь-якої дати після 2026-09-30; `canEnroll` = `false` (порушення `PERIOD`).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=EnrollmentValidatorTest` (у PowerShell: `mvn test "-Dtest=EnrollmentValidatorTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest.txt` .


---

**ID:** BUG-M06
**Title:** [module] EnrollmentValidator.hasCapacity дозволяє зарахування на заповнений курс (enrolled = capacity) (BL-70)
**Severity:** Major
**Priority:** High
**Статус:** Open (спринт 4, модуль v1) · **Мітка:** `module`

**Description:**
BL-70(3): вільні місця є, якщо кількість зарахованих **менша** за місткість (`enrolled < capacity`). Метод порівнює `enrolled <= capacity`, тому коли курс уже заповнений (зараховано рівно `capacity`), він усе ще повідомляє про вільні місця. Кожен курс може бути переповнений на одного студента; курс із нульовою місткістю приймає студентів.

**Вимога (простежуваність):** BL-70 (умова 3), F-40 (SRS v1.1, розділ 5)
**Тест, що фіксує дефект:** `EnrollmentValidatorTest.hasCapacityAtBoundary` — рядки [3] (місткість 25, зараховано 25) і [5] (місткість 0, зараховано 0); `EnrollmentValidatorTest.cannotEnrollWhenCourseIsFull`. Тест лишається «червоним» на v1; у спринті 5 він перевірить виправлення у v2 (closed / reopened).
**Імовірна причина в коді:** У `EnrollmentValidator.hasCapacity` оператор `<=` замість `<`.

**Steps to reproduce:**
1. Створити курс із `capacity = 25` і `enrolled = 25` (період і передумова не обмежують).
2. Викликати `new EnrollmentValidator().hasCapacity(course)`.
3. Викликати `canEnroll(student, course, LocalDate.of(2026,9,8), "edu2026")` для студента, що виконує решту умов.
Або запустити тест: `mvn test -Dtest=EnrollmentValidatorTest`.

**Actual result:** `hasCapacity(capacity 25, enrolled 25)` = `true`; `hasCapacity(capacity 0, enrolled 0)` = `true`; `canEnroll(...)` = `true`.

**Expected result:** `hasCapacity` = `false` у обох випадках; `canEnroll` = `false` (порушення `CAPACITY`).

**Environment:** Модуль eduportal-module v1.0.0 (пакет ua.kpi.tzyapz.eduportal); JDK 17 (Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter); ОС: Windows 10/11. Команда запуску: `mvn test -Dtest=EnrollmentValidatorTest` (у PowerShell: `mvn test "-Dtest=EnrollmentValidatorTest"`)

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest.txt` .
