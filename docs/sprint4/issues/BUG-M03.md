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
