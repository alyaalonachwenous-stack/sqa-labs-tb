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

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.GradeCalculatorTest.txt` .
