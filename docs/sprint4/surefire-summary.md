# Протокол запуску автотестів (Surefire) — спринт 4

**Продукт:** П6 «ЕдуПортал» · **Об'єкт:** модуль бізнес-логіки `v1.0.0` (`ua.kpi.tzyapz.eduportal`)  
**Дата запуску:** 16.11.2026 · **Виконала:** Матюшенко А.В. (Керівник тестування)  
**Середовище:** Windows 10/11, JDK 17 (Eclipse Temurin), Maven 3.9, JUnit 5.10.2 (Jupiter), Selenium 4.21.0

---

## 1. Запуск набору юніт-тестів (`mvn clean test`)

За замовчуванням у `pom.xml` тести з тегом `@Tag("ui")` виключено, тому команда виконує лише модульні тести бізнес-логіки (`BL-10`…`BL-80`).

### Підсумок консолі Surefire

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest
[ERROR] Tests run: 37, Failures: 6, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest
[INFO] Running ua.kpi.tzyapz.eduportal.GradeCalculatorTest
[ERROR] Tests run: 50, Failures: 7, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.GradeCalculatorTest
[INFO] Running ua.kpi.tzyapz.eduportal.QuizQuestionTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.QuizQuestionTest
[INFO] Running ua.kpi.tzyapz.eduportal.QuizScorerTest
[ERROR] Tests run: 32, Failures: 9, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.QuizScorerTest
[INFO]
[INFO] Results:
[INFO]
[ERROR] Tests run: 122, Failures: 22, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

### Деталізація за тест-класами

| Клас | Тест-методів | Запусків Surefire | Failures | Errors | Skipped | Статус |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| `GradeCalculatorTest` | 11 | 50 | 7 | 0 | 0 | Виявлено `BUG-M01`, `BUG-M02` |
| `QuizScorerTest` | 12 | 32 | 9 | 0 | 0 | Виявлено `BUG-M03`, `BUG-M04` |
| `QuizQuestionTest` | 3 | 3 | 0 | 0 | 0 | Усі пройшли |
| `EnrollmentValidatorTest` | 15 | 37 | 6 | 0 | 0 | Виявлено `BUG-M05`, `BUG-M06` |
| **Разом** | **41** | **122** | **22** | **0** | **0** | **Очікуваний `BUILD FAILURE` на `v1`** |

### Аналіз 22 «червоних» запусків

Статус `BUILD FAILURE` спричинений виключно розбіжностями між реалізацією модуля `v1.0.0` та вимогами розділу 5 SRS v1.1. Технічних помилок (`Errors: 0`) або пропущених тестів (`Skipped: 0`) немає. Усі 22 падіння задокументовані у GitHub Issues з міткою `module`:

| Тест (`клас.метод[набір]`) | Фактично → Очікувано | Дефект | Issue |
| :--- | :--- | :--- | :---: |
| `GradeCalculatorTest.ectsLetterAtBoundaries[3]` (`95.0`) | `"B"` → `"A"` | `BUG-M01` (BL-20) | #22 |
| `GradeCalculatorTest.latePenaltyByDays[3]…[8]` (`2, 3, 4, 5, 6, 30` дн.) | `64.8 / 58.32 / 52.49 / 47.24` → `64.0 / 56.0 / 48.0 / 40.0` | `BUG-M02` (BL-30) | #23 |
| `QuizScorerTest.multiScoreIsNeverNegative[1]…[3]` | `-1.0 / -0.5 / -0.5` → `0.0` | `BUG-M03` (BL-50) | #24 |
| `QuizScorerTest.attemptScoreIsRoundedToOneDecimal[2,3,5,6,7,8]` | `33.0 / 67.0 / 17.0 / 14.0 / 13.0 / 6.0` → `33.3 / 66.7 / 16.7 / 14.3 / 12.5 / 6.3` | `BUG-M04` (BL-60) | #25 |
| `EnrollmentValidatorTest.withinPeriodAtEndBoundary[3],[4]`, `cannotEnrollAfterPeriodEnd` | `true` → `false` (дати після `30.09.2026`) | `BUG-M05` (BL-70) | #26 |
| `EnrollmentValidatorTest.hasCapacityAtBoundary[3],[5]`, `cannotEnrollWhenCourseIsFull` | `true` → `false` (`25/25` та `0/0`) | `BUG-M06` (BL-70) | #27 |

---

## 2. Запуск UI-набору Selenium WebDriver (`mvn test "-Dgroups=ui" "-DexcludedGroups=none"`)

Окремий запуск браузерних тестів (`LoginTest` — 2 тести, `CourseCatalogTest` — 3 тести) у headless-режимі Chrome на стенді `https://sandbox.moodledemo.net`:

```text
[INFO] Running ua.kpi.tzyapz.eduportal.ui.CourseCatalogTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.ui.CourseCatalogTest
[INFO] Running ua.kpi.tzyapz.eduportal.ui.LoginTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.eduportal.ui.LoginTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Повні текстові та XML-звіти генеруються локально у директорії `target/surefire-reports/` під час кожного прогону.