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
