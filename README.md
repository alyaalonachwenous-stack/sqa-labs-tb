# sqa-labs-tb

Репозиторій команди Т3 для лабораторних робіт з дисципліни "Технології забезпечення якості програмних засобів". Обраний продукт: ЕдуПортал.

Команда: **Матюшенко А.В.**, **Керничний С.С.**, **Черноус А.Д.**

---

## Лабораторна робота 4 — автотести: JUnit 5 і Selenium WebDriver

Репозиторій автотестів продукту «ЕдуПортал» (система дистанційного навчання, SRS v1.1). Складається з:

- **юніт-тестів JUnit 5** модуля бізнес-логіки v1.0.0 (пакет `ua.kpi.tzyapz.eduportal`, вимоги BL-10…BL-80);
- **UI-тестів Selenium WebDriver 4** (вхід і каталог/пошук курсів) на демо https://sandbox.moodledemo.net;
- **проєкту Selenium IDE** (`selenium-ide/eduportal.side`) та чернеток експорту в Java.

### Вимоги до середовища

| Що | Версія | Перевірка |
|---|---|---|
| JDK | 17 (Temurin) | `java -version` |
| Maven | 3.9.x | `mvn -v` (у виводі має бути Java version: 17) |
| Chrome | актуальний (для UI-тестів) | — |
| Інтернет | потрібен | Maven завантажує залежності; Selenium Manager під час першого UI-запуску завантажує chromedriver, що відповідає версії Chrome (вручну драйвер ставити не треба) |

### Збірка і запуск

Усі команди — з кореня репозиторію (там, де `pom.xml`). **У PowerShell параметри `-D…` беріть у лапки** (інакше крапка в імені ламає розбір аргументів).

| Мета | Команда |
|---|---|
| Усі юніт-тести (UI за замовчуванням виключено) | `mvn clean test` |
| Один тест-клас | `mvn test "-Dtest=GradeCalculatorTest"` |
| Лише UI-тести (Chrome + інтернет) | `mvn test "-Dgroups=ui" "-DexcludedGroups=none"` |
| UI-тести з видимим вікном браузера | `mvn test "-Dgroups=ui" "-DexcludedGroups=none" "-Dui.headless=false"` |
| Усі тести разом (юніт + UI) | `mvn test "-DexcludedGroups=none"` |

**Очікуваний результат `mvn clean test` на модулі v1:** `Tests run: 122, Failures: 22, Errors: 0, Skipped: 0` → `BUILD FAILURE`.
Це не помилка збірки: 22 запуски червоні, бо в модулі v1 закладено 6 дефектів (див. нижче). Червоні тести навмисно не виправляють і не вимикають —
у спринті 5 цей самий набір буде запущено на модулі v2, і кожен тест покаже, чи виправлено дефект.
UI-набір має пройти повністю: `Tests run: 5, Failures: 0` → `BUILD SUCCESS`.

### Звіти

- Консольний підсумок і `target/surefire-reports/` (`*.txt` — підсумок і трасування по класу; `TEST-*.xml` — усі тести з назвами `@DisplayName`) створює Surefire.
- Протокол запуску (дата, команда, підсумок, список червоних тестів із ID дефектів): [docs/sprint4/surefire-summary.md](docs/sprint4/surefire-summary.md).

### Структура

```
.
├── README.md                       ← цей файл
├── pom.xml                         ← JDK 17, JUnit 5.10.2, Selenium 4.21.0, Surefire 3.2.5 (тег ui виключено за замовчуванням)
├── .gitignore
├── selenium-ide/
│   ├── eduportal.side              ← проєкт Selenium IDE (2 сценарії: вхід, пошук курсу)
│   └── export/                     ← Java-чернетки експорту (не збираються, довідково)
├── docs/
│   └── sprint4/                    ← матриця покриття, UI-сценарії, протокол Surefire, звіти про дефекти, review, ретро, досьє
└── src/
    ├── main/java/ua/kpi/tzyapz/eduportal/       ← модуль бізнес-логіки v1.0.0 (GradeCalculator, QuizScorer, EnrollmentValidator + записи)
    └── test/java/ua/kpi/tzyapz/eduportal/
        ├── GradeCalculatorTest.java             ← BL-10, BL-20, BL-30      (Матюшенко А.В.)
        ├── QuizScorerTest.java                  ← BL-40, BL-50, BL-60      (Керничний С.С.)
        ├── QuizQuestionTest.java                ← ключ питання             (Керничний С.С.)
        ├── EnrollmentValidatorTest.java         ← BL-70, BL-80             (Черноус А.Д.)
        └── ui/                                  ← WebDriver-тести, @Tag("ui") (Черноус А.Д.)
            ├── UiTestBase.java                  ← браузер на кожен тест, headless за замовчуванням
            ├── LoginTest.java
            ├── CourseCatalogTest.java
            └── pages/ (LoginPage, CourseSearchPage)   ← Page Object
```

### Хто що написав

| Автор | Роль у спринті 4 | Тести |
|---|---|---|
| Матюшенко А.В. | Керівник тестування + Скрам-майстер/рецензент | `GradeCalculatorTest` (11) |
| Керничний С.С. | Тест-аналітик | `QuizScorerTest` (12), `QuizQuestionTest` (3), матриця покриття, UI-сценарії |
| Черноус А.Д. | Інженер з тестування (SDET) | `EnrollmentValidatorTest` (15), UI-тести (5), Selenium IDE, інфраструктура |

Авторство підтверджується історією комітів (`git log --author=...`).

### Знайдені дефекти модуля v1 (звіти — GitHub Issues, мітка `module`)

| ID | Issue | Вимога | Дефект | Тест |
|---|:---:|---|---|---|
| BUG-M01 | #22 | BL-20 | `ectsLetter(95.0)` повертає B замість A | `GradeCalculatorTest.ectsLetterAtBoundaries` |
| BUG-M02 | #23 | BL-30 | штраф за запізнення — складний відсоток замість −10 %/день | `GradeCalculatorTest.latePenaltyByDays` |
| BUG-M03 | #24 | BL-50 | від'ємний бал за питання з кількома правильними | `QuizScorerTest.multiScoreIsNeverNegative` |
| BUG-M04 | #25 | BL-60 | підсумок спроби округлено до цілого, а не до 1 знака | `QuizScorerTest.attemptScoreIsRoundedToOneDecimal` |
| BUG-M05 | #26 | BL-70(2) | не перевіряється кінець періоду зарахування | `EnrollmentValidatorTest.withinPeriodAtEndBoundary`, `cannotEnrollAfterPeriodEnd` |
| BUG-M06 | #27 | BL-70(3) | `enrolled = capacity` вважається наявністю вільних місць | `EnrollmentValidatorTest.hasCapacityAtBoundary`, `cannotEnrollWhenCourseIsFull` |

Повні звіти: [docs/sprint4/bug-reports.md](docs/sprint4/bug-reports.md).

### Типові проблеми

| Симптом | Що робити |
|---|---|
| `SessionNotCreatedException` / драйвер не завантажується | Перевірте інтернет і версію Chrome; очистіть `~/.cache/selenium`; оновіть `selenium.version` у `pom.xml` до останньої 4.x |
| `Unknown lifecycle phase ".test.skip=…"` у PowerShell | Візьміть `-D…` параметр у подвійні лапки |
| UI-тест не знаходить елемент | Запустіть з `"-Dui.headless=false"`, перевірте локатор у DevTools (`Ctrl+F` → «1 of 1») і виправте його в `ui/pages/*.java` |
| `mvn -v` показує не JDK 17 | Встановіть JDK 17 (Temurin) і задайте `JAVA_HOME` на нього |
| Лишилися процеси Chrome після збою | Тести викликають `driver.quit()` у `@AfterEach`; закрийте процеси вручну і повторіть запуск |

### Інші матеріали

Документація спринта 4: [docs/sprint4/README.md](docs/sprint4/README.md) · Матриця покриття: [docs/sprint4/coverage-matrix.md](docs/sprint4/coverage-matrix.md) ·
UI-сценарії: [docs/sprint4/ui-scenarios.md](docs/sprint4/ui-scenarios.md) · Досьє, розділ «Автоматизація»: [docs/sprint4/dossier-automation.md](docs/sprint4/dossier-automation.md).

