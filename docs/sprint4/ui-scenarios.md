# UI-сценарії для автоматизації (Selenium WebDriver) — спринт 4

**Продукт:** П6 «ЕдуПортал» · **Демонстраційний застосунок:** https://sandbox.moodledemo.net (Moodle 5.3 Sandbox, Boost) ·  
**Облікові дані студента:** `student` / `sandbox24` (з головної сторінки демо). Демо скидається щогодини, тому кожен тест незалежний, створює власний браузер і не покладається на дані, створені іншими тестами.  
**Склав:** Керничний С.С. (Тест-аналітик); **реалізація:** Черноус А.Д. (Інженер з тестування / SDET).

## Вибір сценаріїв (обґрунтування)

Автоматизуємо те, що **справді проходить через інтерфейс** і повторюється при кожному випуску: вхід (High, F-10, GUI-10, GUI-20) та каталог/пошук курсів (High, F-30, F-140). Правила бізнес-логіки (підсумки, шкала ECTS, штрафи, умови зарахування) через браузер не перевіряються — для них є юніт-тести JUnit 5. Сценарії зарахування з кодовим словом (F-40) і здачі завдань (F-60) потребують попереднього налаштування курсу викладачем після кожного щогодинного скидання стенда, тому до поточного спринта не включені (кандидати для спринта 5).

## Перелік сценаріїв (5 сценаріїв, норматив для команди з 3 осіб — ≥ 4)

| № | Тест-кейс (спринт 3) | Вимога | Сценарій і кроки (дія → локатор) | Очікування перед перевіркою | Перевірка | Тест-метод |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | **TC-001** | F-10, GUI-10, GUI-75 | `get(BASE/login/index.php?lang=en)` → `By.id("username")` ввести `student` → `By.id("password")` ввести `sandbox24` → клік `By.id("loginbtn")` | `visibilityOfElementLocated(#user-menu-toggle, .usermenu)` | **Жорстка:** меню користувача видно. **М'яка (`assertAll`):** URL не містить `/login/`, меню користувача активне, форми входу немає | `LoginTest.validLoginOpensDashboard` |
| 2 | **TC-003** | F-10, GUI-20 | Та сама форма, пароль `wrong_password_1` | `visibilityOfElementLocated(.alert-danger, .loginerrors .alert-danger, [role='alert'])` | **М'яка (`assertAll`):** текст містить «Unable to log in»; URL містить `/login/`; поле `password` порожнє | `LoginTest.invalidPasswordShowsErrorAndKeepsUsername` |
| 3 | **TC-008** | F-30 | `get(BASE/?lang=en)` без входу | умова-лямбда: на сторінці є посилання `a[href*='/course/view.php?id=']` з непорожнім текстом | **М'яка (`assertAll`):** є «My first course» і «My second course»; у тексті сторінки є «Terri Teacher» | `CourseCatalogTest.homeListsCoursesWithTeacherWithoutLogin` |
| 4 | **TC-009** | F-140 | `get(BASE/course/search.php?lang=en)` → поле `#region-main input[name='q'], #region-main input[name='search']` ввести `first` → Enter | умова-лямбда «URL містить `q=first` або `search=first`», потім умова-лямбда «список посилань на курси непорожній» | **М'яка (`assertAll`):** є «My first course»; **немає** «My second course» | `CourseCatalogTest.searchFindsCourseByNameFragment` |
| 5 | **TC-011** | F-30 | Те саме з пошуковим рядком `zzqqxx` | `textToBePresentInElementLocated(body, "No courses were found")` | **М'яка (`assertAll`):** є повідомлення про відсутність курсів; кількість посилань на курси = 0 | `CourseCatalogTest.searchWithoutMatchesShowsNotFoundMessage` |

## Стійкість локаторів

| Локатор | Тип | Чому обрано | Ризик |
| :--- | :--- | :--- | :--- |
| `id=username`, `id=password`, `id=loginbtn` | `id` | Стандартні унікальні `id` форми входу Moodle; не залежать від верстки | Низький |
| `.alert-danger, .loginerrors .alert-danger, [role='alert']` | `css` (список) | Універсальний блок помилки входу в темі Boost (Moodle 5.3) | Середній — клас теми |
| `#user-menu-toggle, .usermenu` | `css` (список) | Ознака авторизованого сеансу; список страхує від різниці версій | Середній |
| `a[href*='/course/view.php?id=']` | `css` (атрибут) | Назва курсу — посилання на `/course/view.php?id=N`; не залежить від мови інтерфейсу й класів | Низький |
| `#region-main input[name='q'], #region-main input[name='search'], #coursesearchbox` | `css` (список) | Поле пошуку курсів у основній області для різних версій Moodle (`name='q'` у 5.3) | Середній |
| `linkText=My first course` (лише Selenium IDE) | `linkText` | Для швидкого запису; у WebDriver-тестах не використовується | Низька стійкість (залежить від мови) |

## Передумови та правила запуску

- Інтерфейс примусово англійський (`--lang=en-US` у Chrome та параметр `?lang=en` в адресах), тому тексти у перевірках стабільні.
- Для першого запуску потрібен доступ до інтернету (Selenium Manager автоматично завантажує сумісний `chromedriver`).
- **Запуск лише UI-тестів:** `mvn test "-Dgroups=ui" "-DexcludedGroups=none"`
- **Запуск усіх тестів (юніт + UI):** `mvn test "-DexcludedGroups=none"`
- **Запуск із видимим вікном браузера (для демо):** додати параметр `"-Dui.headless=false"`.
