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

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.QuizScorerTest.txt`.
