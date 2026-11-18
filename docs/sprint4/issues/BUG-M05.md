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

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest.txt`.
