# Досьє якості v4 — розділ «Автоматизація»

**Продукт:** П6 «ЕдуПортал» · **Команда:** Т3 · **Спринт:** 4 · **Модуль бізнес-логіки:** `v1.0.0` · **Специфікація:** SRS v1.1  
**Інкремент:** «Репозиторій автотестів + протокол запуску (звіт Surefire) + звіти про дефекти модуля»  
**Репозиторій:** https://github.com/alyaalonachwenous-stack/sqa-labs-tb · **Трекер:** GitHub Issues (мітка `module`)

---

## 1. Виконання нормативів спринта (команда з 3 осіб)

| Показник | Норматив | Факт | Підтвердження |
| :--- | :---: | :---: | :--- |
| Юніт-тестів JUnit 5 (параметризований метод = 1) | $\ge 12$ | **41** (`GradeCalculatorTest` — 11, `QuizScorerTest` — 12, `QuizQuestionTest` — 3, `EnrollmentValidatorTest` — 15) | `src/test/java/ua/kpi/tzyapz/eduportal/*Test.java` |
| UI-сценаріїв WebDriver (1 сценарій = 1 тест-метод) | $\ge 4$ | **5** (`LoginTest` — 2, `CourseCatalogTest` — 3) | [ui-scenarios.md](ui-scenarios.md) |
| Знайдених дефектів модуля із 6 закладених | $\ge 3$ | **6 з 6** (`BUG-M01`…`BUG-M06`, Issues `#22`–`#27`) | [bug-reports.md](bug-reports.md) |
| Юніт-тестів на кожного члена команди | $\ge 3$ | Матюшенко А.В. — 11 · Керничний С.С. — 15 · Черноус А.Д. — 15 | Історія комітів `git log --author` |
| Сценаріїв у Selenium IDE | 1–2 | **2** (`selenium-ide/eduportal.side`) | Експорт у `selenium-ide/export/` |
| Code review кожного тест-класу | усі | 4 групи класів перевірено перехресно за чек-листом 5.3 | [code-review.md](code-review.md) |

---

## 2. Матриця покриття BL-вимог

Усі 8 вимог бізнес-логіки (`BL-10`…`BL-80`) покрито юніт-тестами: для кожного правила перевірено нормальні сценарії, граничні значення з обох боків межі та обробку некоректних вхідних даних (`IllegalArgumentException`). Детальна матриця з прив'язкою до методів і рецензентів наведена у файлі [coverage-matrix.md](coverage-matrix.md).

---

## 3. Перелік автоматизованих UI-сценаріїв

| ID кейсу (Спринт 3) | Вимога | Назва сценарію | Тест-метод WebDriver |
| :--- | :--- | :--- | :--- |
| `TC-001` | F-10, GUI-10, GUI-75 | Успішний вхід студента (`student`) | `LoginTest.validLoginOpensDashboard` |
| `TC-003` | F-10, GUI-20 | Спроба входу з невірним паролем та збереження логіна | `LoginTest.invalidPasswordShowsErrorAndKeepsUsername` |
| `TC-008` | F-30 | Відображення списку доступних курсів і викладача без авторизації | `CourseCatalogTest.homeListsCoursesWithTeacherWithoutLogin` |
| `TC-009` | F-140 | Пошук курсу за фрагментом назви (`first`) | `CourseCatalogTest.searchFindsCourseByNameFragment` |
| `TC-011` | F-30 | Пошук курсу без збігів (`zzqqxx`) та повідомлення про порожній результат | `CourseCatalogTest.searchWithoutMatchesShowsNotFoundMessage` |

Детальний опис кроків, CSS-локаторів і явних очікувань наведено у [ui-scenarios.md](ui-scenarios.md).

---

## 4. Підсумок виконання (Surefire)

* **Юніт-набір (`mvn clean test`):** `Tests run: 122, Failures: 22, Errors: 0, Skipped: 0` (100 успішних перевірок; 22 падіння точно відповідають 6 дефектам модуля `v1`).
* **UI-набір (`mvn test "-Dgroups=ui" "-DexcludedGroups=none"`):** `Tests run: 5, Failures: 0, Errors: 0, Skipped: 0` (`BUILD SUCCESS`).
* Повний протокол запуску зафіксовано у [surefire-summary.md](surefire-summary.md).

---

## 5. Реєстр дефектів модуля `v1.0.0`

| ID | Issue | Вимога | Тест, що фіксує дефект | Короткий опис розбіжності | Severity / Priority |
| :--- | :---: | :--- | :--- | :--- | :--- |
| `BUG-M01` | #22 | `BL-20` | `GradeCalculatorTest.ectsLetterAtBoundaries[3]` | `ectsLetter(95.0)` повертає `"B"` замість `"A"` (`>` замість `>=`) | Major / Medium |
| `BUG-M02` | #23 | `BL-30` | `GradeCalculatorTest.latePenaltyByDays[3…8]` | Застосовано складний відсоток `0.9^days` замість лінійного −10 % за день | Major / High |
| `BUG-M03` | #24 | `BL-50` | `QuizScorerTest.multiScoreIsNeverNegative[1…3]` | Бал за питання з кількома відповідями може бути від'ємним замість обмеження знизу `0.0` | Major / High |
| `BUG-M04` | #25 | `BL-60` | `QuizScorerTest.attemptScoreIsRoundedToOneDecimal` | Підсумок спроби округлюється до цілого (`Math.round`), а не до 1 знака `HALF_UP` | Major / Medium |
| `BUG-M05` | #26 | `BL-70(2)` | `EnrollmentValidatorTest.withinPeriodAtEndBoundary`, `cannotEnrollAfterPeriodEnd` | Ігнорується дата закриття зарахування `enrolEnd` | Major / High |
| `BUG-M06` | #27 | `BL-70(3)` | `EnrollmentValidatorTest.hasCapacityAtBoundary`, `cannotEnrollWhenCourseIsFull` | Дозволяється зарахування при `enrolled == capacity` (`<=` замість `<`) | Major / High |

---

## 6. Висновок про стан модуля `v1`

Модуль бізнес-логіки `v1.0.0` **не відповідає** вимогам розділу 5 SRS v1.1: виявлено критичні логічні помилки у 5 із 8 вимог (`BL-20`, `BL-30`, `BL-50`, `BL-60`, `BL-70`). Найвищий ризик для експлуатації несуть дефекти `BUG-M05` та `BUG-M06` (порушення обмежень місткості груп і строків запису на курс), а також `BUG-M02` і `BUG-M03` (спотворення підсумкових оцінок студентів). Окремо зафіксовано нечіткість формулювання вимоги `BL-80` у SRS (відсутній опис формату кодів порушень — виправлення заплановано на Спринт 5).

**Рекомендація:** реліз модуля `v1.0.0` не приймати; передати дефекти `#22`–`#27` на виправлення та виконати повторний регресійний прогін у Спринті 5 на версії `v2`.

---

## 7. Перевірка відтворюваності збірки «з нуля»

| Параметр перевірки | Результат |
| :--- | :--- |
| Дата та виконавець | 16.11.2026 · Матюшенко А.В. (КТ) / Керничний С.С. (ТА) |
| Тестове середовище | Windows 10/11, IntelliJ IDEA Community, JDK 17 (Temurin), Maven 3.9 |
| Виконана послідовність | Відкриття проєкту за `pom.xml` → підключення Project SDK 17 → запуск `mvn clean test` → запуск UI-тестів |
| Що спрацювало без підказок | Усі 41 юніт-тести та бібліотеки JUnit 5 / Selenium 4 підтягнулися автоматично; звіти Surefire згенерувалися у `target/surefire-reports/` |
| Особливості середовища | У терміналі Windows PowerShell параметри виду `-Dgroups=ui` необхідно брати в лапки (`"-Dgroups=ui"`), що задокументовано в `README.md` |
