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

**Attachments:** `target/surefire-reports/ua.kpi.tzyapz.eduportal.EnrollmentValidatorTest.txt`.
