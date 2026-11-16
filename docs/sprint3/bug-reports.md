# Звіти про дефекти — спринт 3, Run 1

**Продукт:** П6 «ЕдуПортал» (`eduportal`) · **Об'єкт:** demo https://sandbox.moodledemo.net (Moodle 5.3) · **Еталон:** SRS 1.1
**Reporter:** Матюшенко А.В. (ІТ) · **Рецензент:** Керничний С.С. (КТ+СМ)
**Формат:** шаблон 4.11 методички; трекер — цей файл (п. 4.15: «Допускається таблиця bug-reports.md з тими самими полями»).
**Статуси (4.14):** у спринті 3 — New → Open (після рецензії); перевірка виправлень — спринт 5 на версії 2.

Середовище для всіх звітів: Chrome 156.0.8078.80; Windows 11. Час у Moodle показано в часовому поясі профілю (UTC+8).

## Зведення

| Bug ID | Назва | Severity | Priority | TC | Вимога | Status |
| :-- | :-- | :-- | :-- | :-- | :-- | :-- |
| [BUG-001](#bug-001) | Після успішного входу відкривається головна сторінка сайту, а не інформаційна панель (F-10) | Minor | P3 | TC-001, TC-004 | F-10 | Open |
| [BUG-002](#bug-002) | Після невдалого входу фокус залишається в полі «Ім'я входу», а не в полі «Пароль» (GUI-20) | Minor | P3 | TC-003 | GUI-20 | Open |
| [BUG-003](#bug-003) | Обліковий запис не блокується після 3 невдалих спроб входу поспіль (F-10, S-20) | Major | P3 | TC-005 | F-10, S-20 | Open |
| [BUG-004](#bug-004) | Незарахований студент бачить вміст курсу замість сторінки «Параметри зарахування» (F-30, F-40) | Major | P3 | TC-012, TC-013 | F-30, F-40 | Open |
| [BUG-005](#bug-005) | Сторінка самозарахування не відповідає GUI-50: немає поля «Кодове слово», кнопки показати/сховати, інший напис кнопки | Trivial | P3 | TC-015 | GUI-50 | Open |
| [BUG-006](#bug-006) | Після неправильного кодового слова поле не очищується (GUI-60) | Minor | P3 | TC-014 | GUI-60 | Open |
| [BUG-007](#bug-007) | Форма входу з порожніми полями надсилається без позначки обов'язкових полів (GUI-10) | Minor | P3 | TC-006 | GUI-10 | Open |
| [BUG-008](#bug-008) | У верхній панелі для зареєстрованого користувача немає перемикача мови (GUI-75) | Trivial | P3 | TC-033 | GUI-75 | Open |
| [BUG-009](#bug-009) | Форма здачі завдання приймає файл .png, хоча дозволено лише .pdf, .docx, .zip (GUI-30) | Normal | P3 | TC-019 | GUI-30 | Open |
| [BUG-010](#bug-010) | Максимальний розмір файлу роботи — 1 МБ замість 50 МБ (GUI-30) | Normal | P3 | TC-019 | GUI-30 | Open |
| [BUG-011](#bug-011) | Журнал оцінок студента не показує літеру ECTS для підсумку курсу (F-100, BL-20) | Major | P3 | TC-026, TC-027 | F-100, BL-20 | Open |
| [BUG-012](#bug-012) | Оцінка запізнілої роботи не зменшується на 10 % за кожен день запізнення (F-60, BL-30) | Major | P3 | TC-020, TC-021 | F-60, BL-30 | Open |
| [BUG-013](#bug-013) | Профіль зберігає ім'я з цифрою «Ivan2» (F-110) | Minor | P3 | TC-030 | F-110 | Open |
| [BUG-014](#bug-014) | На ширині 375 px плаваючі кнопки бічних панелей перекривають вміст сторінки курсу (GUI-90) | Trivial | P3 | TC-035 | GUI-90 | Open |

**Разом:** 14 звітів — Major 4, Normal 2, Minor 5, Trivial 3; усі P3 (жоден дефект не блокує виконання кейсів).

---

## BUG-001

**Title:** Після успішного входу відкривається головна сторінка сайту, а не інформаційна панель (F-10)
**Severity:** Minor · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-001, TC-004; requirement F-10 (SRS 1.1)

### Description
F-10 (SRS 1.1): за правильної пари облікових даних Система «відкриває інформаційну панель із посиланням «Мої курси»». Фактично після входу відкривається головна сторінка сайту (Home, «Hi, Sam!»), а не Dashboard (`/my/`). Відтворюється для `student` і для створеного `t3lock1`.

**Severity Minor:** вхід працює, «My courses» доступні з верхньої панелі — порушено лише цільову сторінку після входу.
**Priority P3:** не блокує виконання інших тест-кейсів.

### Steps to reproduce
1. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
2. У поле «Ім'я входу» ввести `student`, у поле «Пароль» — `sandbox24`.
3. Натиснути «Увійти».

### Actual result
Після кроку 3 відкрито головну сторінку сайту `https://sandbox.moodledemo.net/` з привітанням «Hi, Sam!»; у верхній панелі активний пункт «Home».

### Expected result
Після кроку 3 відкрито інформаційну панель (Dashboard, адреса містить `/my/`) з посиланням «Мої курси» — F-10.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:43 (обліковий запис `student`, роль Студент); Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-001-1.png`](attachments/BUG-001-1.png) — сторінка, відкрита після кроку 3

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-002

**Title:** Після невдалого входу фокус залишається в полі «Ім'я входу», а не в полі «Пароль» (GUI-20)
**Severity:** Minor · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-003; requirement GUI-20 (SRS 1.1)

### Description
GUI-20: після невдалого входу «фокус — у полі «Пароль»». Фактично курсор у полі «Ім'я входу», і символи, набрані з клавіатури, дописуються до логіна. Інші вимоги GUI-20 виконуються: червоний блок над полями, логін збережено, пароль очищено.

**Severity Minor:** проблема зручності інтерфейсу; вхід можливий.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
2. У поле «Ім'я входу» ввести `student`, у поле «Пароль» — `Wrong-111`.
3. Натиснути «Увійти».
4. Не клацаючи мишею, набрати на клавіатурі `abc`.

### Actual result
Після кроку 3 — червоний блок «Unable to log in…» над полями, у полі логіна `student`, пароль порожній. Після кроку 4 `abc` дописано в поле «Ім'я входу» (`abcstudent`); поле «Пароль» порожнє.

### Expected result
Після кроку 3 фокус у полі «Пароль»; після кроку 4 символи `abc` з'являються (масковано) в полі «Пароль» — GUI-20.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:44 (без входу); Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-002-1.png`](attachments/BUG-002-1.png) — форма після кроку 4: `abcstudent` у полі логіна

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-003

**Title:** Обліковий запис не блокується після 3 невдалих спроб входу поспіль (F-10, S-20)
**Severity:** Major · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-005; requirement F-10, S-20 (SRS 1.1)

### Description
F-10 і S-20 (SRS 1.1): після 3 невдалих спроб входу поспіль обліковий запис блокується на 15 хвилин, користувач бачить повідомлення про блокування. Фактично після трьох невдалих спроб вхід з правильним паролем виконується одразу, повідомлення про блокування немає. Перевірено на окремому обліковому записі `t3lock2`, щоб не блокувати спільні акаунти demo.

**Severity Major:** захист від підбору пароля не працює — важлива функція захищеності відсутня; втрати даних немає.
**Priority P3:** не блокує виконання інших тест-кейсів.

### Steps to reproduce
1. Передумова: Під `admin` / `sandbox24` створено користувачів `t3student1`, `t3lock1`, `t3lock2` (Адміністрування сайту → Користувачі → Додати нового користувача; пароль `T3-Test-2026!`, e-mail `@example.com`).
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. У поле «Ім'я входу» ввести `t3lock2`, у поле «Пароль» — `Wrong-111`; натиснути «Увійти».
4. Повторити крок 3 ще двічі (разом 3 невдалі спроби поспіль).
5. У поле «Ім'я входу» ввести `t3lock2`, у поле «Пароль» — `T3-Test-2026!`; натиснути «Увійти».

### Actual result
Після кроків 3–4 щоразу показано «Unable to log in. Your login info may be incorrect…» (у полі логіна `t3lock2`), повідомлення про блокування немає. Після кроку 5 вхід виконано: відкрито головну сторінку, в меню користувача аватар «TL».

### Expected result
Після третьої невдалої спроби — повідомлення про блокування облікового запису; на кроці 5 вхід НЕ виконано, показано повідомлення про блокування (діє 15 хв) — F-10, S-20.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 07.11.2026 ~23:47, обліковий запис `t3lock2`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-003-1.png`](attachments/BUG-003-1.png) — форма після невдалої спроби: логін `t3lock2`
- [`BUG-003-2.png`](attachments/BUG-003-2.png) — успішний вхід після кроку 5

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-004

**Title:** Незарахований студент бачить вміст курсу замість сторінки «Параметри зарахування» (F-30, F-40)
**Severity:** Major · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-012, TC-013; requirement F-30, F-40 (SRS 1.1)

### Description
F-30: «Вміст курсу відкривається лише зарахованим користувачам після входу». F-40: студент, відкриваючи курс із самозарахуванням, потрапляє на сторінку «Параметри зарахування». Фактично незарахованому `t3student1` відкривається сторінка курсу з усіма розділами й елементами; зарахуватися можна лише через вкладку «Enrol me in this course». Імовірна причина — у курсі за замовчуванням увімкнено метод «Guest access».

**Severity Major:** порушено розмежування доступу до навчальних матеріалів.
**Priority P3:** не блокує інших кейсів.

### Steps to reproduce
1. Передумова: Під `admin` / `sandbox24` створено користувачів `t3student1`, `t3lock1`, `t3lock2` (Адміністрування сайту → Користувачі → Додати нового користувача; пароль `T3-Test-2026!`, e-mail `@example.com`).
2. Передумова: під `teacher` у «My first course» → Participants → Enrolment methods увімкнено «Self enrolment (Student)» з кодовим словом `T3Key2026`; `t3student1` не зарахований.
3. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
4. Увійти як `t3student1` / `T3-Test-2026!`.
5. Відкрити https://sandbox.moodledemo.net/course/index.php і натиснути «My first course».

### Actual result
Після кроку 5 відкрито сторінку курсу `course/view.php?id=2` з розділами General, Topic 1…5 і елементами (News forum тощо); угорі вкладки Course, Grades, Competencies, «Enrol me in this course».

### Expected result
Після кроку 5 вміст курсу не показано; відкрито сторінку «Параметри зарахування» з полем «Кодове слово» — F-30, F-40.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 07.11.2026 ~23:37, обліковий запис `t3student1`, роль — не зарахований; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-004-1.jpg`](attachments/BUG-004-1.jpg) — вміст курсу, відкритий незарахованому студенту

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-005

**Title:** Сторінка самозарахування не відповідає GUI-50: немає поля «Кодове слово», кнопки показати/сховати, інший напис кнопки
**Severity:** Trivial · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-015; requirement GUI-50 (SRS 1.1)

### Description
GUI-50: сторінка зарахування містить назву методу «Самореєстрація (Студент)», поле «Кодове слово» (символи приховані, кнопка показати/сховати) і кнопку «Приєднатися до курсу». Фактично на сторінці лише текст «An enrolment key will be required.» і кнопка «Enrol me»; поле з'являється у спливному вікні після натискання кнопки і не має кнопки показати/сховати. Примітка: SRS 1.1 сама суперечлива — F-40 називає кнопку «Зарахувати мене».

**Severity Trivial:** зарахування працює; розбіжності стосуються складу й написів елементів.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: як у BUG-004 (користувач `t3student1`, самозарахування з кодовим словом увімкнено).
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `t3student1` / `T3-Test-2026!`.
4. Відкрити https://sandbox.moodledemo.net/enrol/index.php?id=2.
5. Натиснути «Enrol me».

### Actual result
Після кроку 4: «Enrolment options», блок «Self enrolment as 'Student'», текст «An enrolment key will be required.», кнопка «Enrol me»; поля для кодового слова немає. Після кроку 5 — спливне вікно з полем «Enrolment key» (символи приховані), кнопками «Cancel» і «Enrol me»; кнопки показати/сховати немає.

### Expected result
Після кроку 4 на сторінці: назва методу «Самореєстрація (Студент)», поле «Кодове слово» з кнопкою показати/сховати, кнопка «Приєднатися до курсу» — GUI-50.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 07.11.2026 ~23:38, `t3student1` (мова профілю — англійська); Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-005-1.jpg`](attachments/BUG-005-1.jpg) — сторінка зарахування після кроку 4
- [`BUG-005-2.jpg`](attachments/BUG-005-2.jpg) — спливне вікно після кроку 5

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-006

**Title:** Після неправильного кодового слова поле не очищується (GUI-60)
**Severity:** Minor · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-014; requirement GUI-60 (SRS 1.1)

### Description
GUI-60: якщо кодове слово введено неправильно, Система показує червоне повідомлення під полем, «очищує поле і залишає користувача на сторінці зарахування». Повідомлення і місце відповідають вимозі, але введене значення залишається в полі.

**Severity Minor:** незначна проблема інтерфейсу.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: як у BUG-004.
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `t3student1` / `T3-Test-2026!`.
4. Відкрити https://sandbox.moodledemo.net/enrol/index.php?id=2 і натиснути «Enrol me».
5. У поле «Enrolment key» ввести `WrongKey`, натиснути «Enrol me» у спливному вікні.

### Actual result
Після кроку 5 під полем червоне «Incorrect enrolment key, please try again»; у полі залишились 8 прихованих символів (`WrongKey`); вікно зарахування відкрите.

### Expected result
Після кроку 5 — червоне повідомлення під полем, поле «Кодове слово» порожнє, користувач на сторінці зарахування — GUI-60.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 07.11.2026 ~23:39, `t3student1`; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-006-1.jpg`](attachments/BUG-006-1.jpg) — поле з неочищеним значенням після кроку 5

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-007

**Title:** Форма входу з порожніми полями надсилається без позначки обов'язкових полів (GUI-10)
**Severity:** Minor · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-006; requirement GUI-10 (SRS 1.1)

### Description
GUI-10: «Обидва поля обов'язкові». Фактично форма з порожніми полями відправляється на сервер без позначки обов'язковості; показано загальне «Unable to log in…», а в поле логіна підставляється попередній логін.

**Severity Minor:** вхід не виконується, але користувач не бачить, що саме треба заповнити.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
2. Переконатися, що поля «Ім'я входу» і «Пароль» порожні (за потреби очистити).
3. Натиснути «Увійти».

### Actual result
Після кроку 3 сторінка перезавантажується з блоком «Unable to log in. Your login info may be incorrect. Please try again.»; біля полів позначок «обов'язкове поле» немає; у полі логіна з'являється попередньо введений логін (`t3lock2`).

### Expected result
Після кроку 3 форма не надсилається; біля порожніх полів показано, що вони обов'язкові — GUI-10.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 07.11.2026 ~23:50 (без входу); Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-007-1.jpg`](attachments/BUG-007-1.jpg) — форма після кроку 3

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-008

**Title:** У верхній панелі для зареєстрованого користувача немає перемикача мови (GUI-75)
**Severity:** Trivial · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-033; requirement GUI-75 (SRS 1.1)

### Description
GUI-75: верхня панель на всіх сторінках містить перемикач мови. Для гостя перемикач є, а для зареєстрованого користувача у верхній панелі його немає — мову можна змінити лише через меню користувача → Language. Інші елементи панелі й меню відповідають вимозі.

**Severity Trivial:** функція доступна іншим шляхом; розбіжність у складі панелі.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
2. Увійти як `student` / `sandbox24`.
3. Переглянути верхню панель.
4. Відкрити меню користувача (аватар праворуч).

### Actual result
Після кроку 3 у панелі: Sandbox 5.3, Home, Dashboard, My courses, пошук, сповіщення, повідомлення, меню користувача; перемикача мови немає. Після кроку 4 меню: Profile, Grades, Calendar, Messages, Private files, Reports, Preferences, Language, Log out.

### Expected result
Після кроку 3 у верхній панелі є перемикач мови — GUI-75.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:24, `student`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-008-1.jpg`](attachments/BUG-008-1.jpg) — верхня панель і меню користувача

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-009

**Title:** Форма здачі завдання приймає файл .png, хоча дозволено лише .pdf, .docx, .zip (GUI-30)
**Severity:** Normal · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-019; requirement GUI-30 (SRS 1.1)

### Description
GUI-30 (SRS 1.1): «Приймаються файли форматів .pdf, .docx, .zip розміром до 50 МБ». Фактично файл .png додається в область «Files» без жодного повідомлення.

**Severity Normal:** здача працює, але контроль типів файлів відсутній — викладач може отримати роботу в неприйнятному форматі.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: під `teacher` у «My first course» створено завдання «Т3 Завдання» (Online text + File submissions, налаштування типів файлів за замовчуванням); `student` зарахований.
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `student` / `sandbox24`.
4. Відкрити «My first course» → «Т3 Завдання» → «Add submission» / «Edit submission».
5. Перетягнути будь-який файл .png (до 1 МБ) в область «Files».

### Actual result
Після кроку 5 файл .png («Знімок екрана…») з'явився в області «Files»; повідомлення про недопустимий тип немає.

### Expected result
Після кроку 5 файл .png не додано; показано повідомлення, що дозволено лише .pdf, .docx, .zip — GUI-30.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:28, `student`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-009-1.png`](attachments/BUG-009-1.png) — область «Files» з прийнятим .png і написом «Maximum file size: 1 MB»

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-010

**Title:** Максимальний розмір файлу роботи — 1 МБ замість 50 МБ (GUI-30)
**Severity:** Normal · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-019; requirement GUI-30 (SRS 1.1)

### Description
GUI-30 (SRS 1.1): приймаються файли розміром до 50 МБ. У формі здачі зазначено «Maximum file size: 1 MB». Фактичне завантаження файлів понад 1 МБ на публічному demo не виконувалось (етика роботи зі спільним стендом), розбіжність видно з напису у формі.

**Severity Normal:** роботи розміром 1–50 МБ здати неможливо, але більшість робіт менших розмірів здаються.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: як у BUG-009.
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `student` / `sandbox24`.
4. Відкрити «My first course» → «Т3 Завдання» → «Add submission» / «Edit submission».
5. Подивитися на підпис над областю «File submissions».

### Actual result
Після кроку 5 над областю файлів показано «Maximum file size: 1 MB, maximum number of files: 20».

### Expected result
Максимальний розмір файлу — 50 МБ — GUI-30.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:28, `student`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-010-1.png`](attachments/BUG-010-1.png) — напис «Maximum file size: 1 MB» над областю файлів

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-011

**Title:** Журнал оцінок студента не показує літеру ECTS для підсумку курсу (F-100, BL-20)
**Severity:** Major · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-026, TC-027; requirement F-100, BL-20 (SRS 1.1)

### Description
F-100: на сторінці «Оцінки» студент бачить підсумкову оцінку та літеру ECTS (BL-20: A — total ≥ 95; B — 85 ≤ total < 95). Фактично у звіті користувача для підсумків 95.00 і 94.99 літери немає ні в рядку «Course total», ні в окремому стовпці.

**Severity Major:** не працює ключова частина нової функції «Журнал оцінок» — переведення в шкалу ECTS.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: під `teacher` у «My second course» зарахувати Sam Student (`student`), створити завдання «Т3 ECTS» (макс. бал 100) — єдиний оцінюваний елемент курсу; «View all submissions» → «Grade» для Sam Student: `95` → Save changes.
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `student` / `sandbox24`.
4. Відкрити https://sandbox.moodledemo.net/grade/report/user/index.php?id=3 (Grades → My second course).
5. (Для межі B) під `teacher` змінити оцінку на `94.99`, повторити кроки 2–4.

### Actual result
Після кроку 4: «Т3 ECTS» 95.00, «Course total» 95.00 (0–100, 95.00 %); літери ECTS немає. Після кроку 5: «Course total» 94.99 (94.99 %); літери немає.

### Expected result
Після кроку 4 для підсумку 95.00 показано літеру «A»; після кроку 5 для 94.99 — літеру «B» — F-100, BL-20.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:35 і ~00:39, `student`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-011-1.jpg`](attachments/BUG-011-1.jpg) — звіт оцінок, підсумок 95.00
- [`BUG-011-2.jpg`](attachments/BUG-011-2.jpg) — звіт оцінок, підсумок 94.99

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-012

**Title:** Оцінка запізнілої роботи не зменшується на 10 % за кожен день запізнення (F-60, BL-30)
**Severity:** Major · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-020, TC-021; requirement F-60, BL-30 (SRS 1.1)

### Description
F-60, BL-30 (SRS 1.1): роботу, здану після терміну, Система позначає як запізнілу і зменшує оцінку на 10 % за кожен повний день запізнення, але не більше ніж на 50 %. Фактично запізнення позначається («submitted 1 day 6 hours late», «6 days 6 hours late»), але оцінка 80 залишається 80.00 і для 1 дня (очікувано 72.00), і для 6 днів (очікувано 40.00); у журналі оцінок викладача теж 80.00.

**Severity Major:** правило оцінювання з вимоги High не працює — підсумки студентів обчислюються неправильно.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: під `teacher` у «My first course» створено завдання «Т3 Завдання −1» (Due date — вчора 00:00, Cut-off date вимкнено, Online text, макс. бал 100) і «Т3 Завдання −6» (Due date — 6 днів тому 00:00); `student` зарахований.
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `student` / `sandbox24`; відкрити «Т3 Завдання −1» → «Add submission», ввести `Т3 запізніла робота`, «Save changes». Те саме для «Т3 Завдання −6». Вийти.
4. Увійти як `teacher` / `sandbox24`; «Т3 Завдання −1» → «View all submissions» → «Grade» для Sam Student: `80` → «Save changes». Те саме для «Т3 Завдання −6». Вийти.
5. Увійти як `student`; відкрити «Т3 Завдання −1», потім «Т3 Завдання −6».

### Actual result
Після кроку 3 статуси «Assignment was submitted 1 day 6 hours late» і «… 6 days 6 hours late». Після кроку 4 у викладача «Current grade in gradebook» 80.00 для обох робіт. Після кроку 5 студент бачить «Grade 80.00 / 100.00» в обох завданнях.

### Expected result
Після кроку 5: «Т3 Завдання −1» — 72.00 / 100.00 (−10 % × 1 день); «Т3 Завдання −6» — 40.00 / 100.00 (штраф обмежено 50 %) — F-60, BL-30.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:38–00:42, `student` і `teacher`; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-012-1.jpg`](attachments/BUG-012-1.jpg) — «Т3 Завдання −1» у студента: 1 день запізнення, 80.00
- [`BUG-012-2.jpg`](attachments/BUG-012-2.jpg) — «Т3 Завдання −6» у студента: 6 днів запізнення, 80.00
- [`BUG-012-3.jpg`](attachments/BUG-012-3.jpg) — вікно оцінювання викладача: Current grade in gradebook 80.00

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-013

**Title:** Профіль зберігає ім'я з цифрою «Ivan2» (F-110)
**Severity:** Minor · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-030; requirement F-110 (SRS 1.1)

### Description
F-110 (SRS 1.1): введені ім'я та прізвище перевіряються на відповідність текстовому формату (лише літери, дефіс, апостроф). Фактично ім'я «Ivan2» зберігається без повідомлення.

**Severity Minor:** незначна втрата функції перевірки даних; профіль редагується.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Передумова: Під `admin` / `sandbox24` створено користувачів `t3student1`, `t3lock1`, `t3lock2` (Адміністрування сайту → Користувачі → Додати нового користувача; пароль `T3-Test-2026!`, e-mail `@example.com`). (перевіряємо на власному акаунті, щоб не змінювати спільний).
2. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
3. Увійти як `t3student1` / `T3-Test-2026!`.
4. Відкрити https://sandbox.moodledemo.net/user/edit.php (Profile → Edit profile).
5. У поле «First name» ввести `Ivan2`, натиснути «Update profile».

### Actual result
Після кроку 5 — «Changes saved», у заголовку профілю «Ivan2 Студент», у меню користувача «Ivan2».

### Expected result
Після кроку 5 профіль не збережено; біля поля «Ім'я» — повідомлення про недопустимі символи (дозволено лише літери, дефіс, апостроф) — F-110.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:45, `t3student1`, роль Студент; Chrome 156.0.8078.80; Windows 11

### Attachments
- [`BUG-013-1.jpg`](attachments/BUG-013-1.jpg) — профіль після кроку 5

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026

---

## BUG-014

**Title:** На ширині 375 px плаваючі кнопки бічних панелей перекривають вміст сторінки курсу (GUI-90)
**Severity:** Trivial · **Priority:** P3 · **Status:** Open
**Linked:** test case TC-035; requirement GUI-90 (SRS 1.1)

### Description
GUI-90 (SRS 1.1): інтерфейс коректно відображається в Chrome і Firefox на екранах шириною від 375 px. На ширині 375 px дві плаваючі кнопки внизу екрана («Open course index» ліворуч і кнопка бічної панелі праворуч) лежать поверх вмісту сторінки курсу. У Chrome вони закривають кінець назви елемента курсу («Welzijnsbevraging klas 4D - scan» — слово «scan» під кнопкою), у Firefox — лівий і правий край картки елемента «News forum». Горизонтальної прокрутки немає; вміст, що опинився під кнопками, видно після прокручування.

**Severity Trivial:** косметична проблема відображення; вміст доступний після прокручування, функції не порушено.
**Priority P3:** кейсів не блокує.

### Steps to reproduce
1. Відкрити https://sandbox.moodledemo.net/login/index.php у Chrome (нове вікно інкогніто).
2. Увійти як `student` / `sandbox24`.
3. Відкрити «My courses» → «My first course».
4. Натиснути F12, увімкнути режим пристрою (Ctrl+Shift+M), задати ширину `375`, натиснути F5.
5. Прокрутити сторінку так, щоб блок «General» з елементом «Welzijnsbevraging klas 4D - scan» опинився на рівні плаваючих кнопок унизу екрана.
6. Повторити кроки 1–5 у Firefox (приватне вікно; адаптивний режим — Ctrl+Shift+M, ширина `375`).

### Actual result
Після кроку 5 (Chrome, 375 × 642): кнопка бічної панелі праворуч закриває кінець назви «Welzijnsbevraging klas 4D - scan», ліва кнопка «Open course index» лежить поверх картки елемента. Після кроку 6 (Firefox, 375 px): обидві кнопки лежать поверх лівого і правого краю картки «News forum».

### Expected result
На ширині 375 px елементи інтерфейсу не перекривають вміст сторінки курсу, назви елементів видно повністю — GUI-90.

### Environment
demo https://sandbox.moodledemo.net (Moodle 5.3), 08.11.2026 ~00:51 (Chrome) і ~14:10 (Firefox), `student`, роль Студент; Chrome 156.0.8078.80, Firefox 159.0.1; Windows 11

### Attachments
- [`BUG-014-1.png`](attachments/BUG-014-1.png) — Chrome, 375 px: кнопка праворуч закриває кінець назви «Welzijnsbevraging klas 4D - scan» (крок 5)
- [`BUG-014-2.png`](attachments/BUG-014-2.png) — Firefox, 375 px: кнопки поверх країв картки «News forum» (крок 6)

**Reporter:** Матюшенко А.В. · **Date:** 08.11.2026 · **Reviewer:** Керничний С.С., 08.11.2026
