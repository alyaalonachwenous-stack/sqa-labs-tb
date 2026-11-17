# Матриця покриття BL-вимог юніт-тестами — спринт 4

**Продукт:** П6 «ЕдуПортал» · **Об'єкт:** модуль бізнес-логіки `v1.0.0` · **Джерело вимог:** SRS v1.1 (виправлена), розділ 5.  
**Склав:** Керничний С.С. (Тест-аналітик). Позначка рецензента (`✔`) ставиться після схваленого Pull Request за чек-листом 5.3 (див. [code-review.md](code-review.md)).

**Умовні позначки:**  
🔴 — тест «червоний» на `v1` і відповідає зареєстрованому дефекту модуля (див. [bug-reports.md](bug-reports.md));  
🟢 — проходить на `v1`.  
«Межі» — значення, що перевіряються з обох боків межі. Параметризований метод у нормативі рахується як один тест.

| BL | Правило (SRS) | Метод модуля | Тести (`клас.метод`) | Межі / негативні випадки | Автор | Рецензент | Статус на v1 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **BL-10** | $\Sigma(\text{бал} \cdot \text{вага}) / 100$, сума ваг = 100, бал 0..100, округлення до 2 знаків `HALF_UP` | `GradeCalculator.weightedTotal` | `GradeCalculatorTest.weightedTotalOfThreeItems`, `weightedTotalIsRoundedHalfUp`, `weightedTotalAtScoreBoundaries`, `weightsNotSummingTo100Throw`, `invalidItemsThrow` | бал 0 / 0,01 / 99,99 / 100; сума ваг 99,99 / 100,01 / 40 / 140; бал −0,01 і 100,01; від'ємна вага; `null` і порожній перелік; `HALF_UP` на 0,125 і 2,375 | Матюшенко | Черноус | 🟢 |
| **BL-20** | Шкала ECTS: A $\ge 95$; B 85–94; C 75–84; D 65–74; E 60–64; Fx 35–59; F $< 35$; поза 0..100 — виняток | `GradeCalculator.ectsLetter` | `GradeCalculatorTest.ectsLetterAtBoundaries`, `ectsLetterOutOfRangeThrows` | усі 6 меж (95, 85, 75, 65, 60, 35) — значення на межі і $\pm 0,01$; 0 і 100; −0,01 і 100,01 | Матюшенко | Черноус | 🔴 `BUG-M01` (95 → B) |
| **BL-30** | Штраф −10 % від балу за повний день, не більше −50 %; 0 днів — без змін; округлення `HALF_UP` | `GradeCalculator.latePenalty` | `GradeCalculatorTest.latePenaltyByDays`, `latePenaltyKeepsScoreWhenOnTime`, `latePenaltyIsRoundedHalfUp`, `latePenaltyInvalidArgumentsThrow` | 0, 1, 2, 3, 4, 5 днів, 6 і 30 (ліміт); бал 0 / 100; округлення 0,225 → 0,23; бал −0,01 / 100,01; днів −1 | Матюшенко | Черноус | 🔴 `BUG-M02` (дні 2…30) |
| **BL-40** | Одна правильна: 1 бал, якщо обрано саме її і лише її; без відповіді — 0; невідомий варіант — виняток | `QuizScorer.questionScore` (`isSingle`) | `QuizScorerTest.singleCorrectOptionGivesOnePoint`, `singleWrongOrExtraOptionsGiveZero`, `noAnswerGivesZero`, `unknownOptionThrows` | правильний / неправильний / правильний+зайвий / усі варіанти; `null` і порожня множина; варіант `"z"` | Керничний | Матюшенко | 🟢 |
| **BL-50** | Кілька правильних: (обрані прав./усі прав.) − (обрані неправ./усі неправ.), не менше 0; якщо неправильних немає — друга частка 0 | `QuizScorer.questionScore` | `QuizScorerTest.multiPartialScore`, `multiScoreIsNeverNegative`, `multiWithoutWrongOptionsHasNoPenalty`, `noAnswerGivesZero`, `unknownOptionThrows` | 1,0 / 0,5 / 0,0 (рівно нуль без обмеження) / від'ємна різниця (`{c, d}`, `{c}`, `{a, c, d}`); питання без неправильних варіантів | Керничний | Матюшенко | 🔴 `BUG-M03` (від'ємний бал) |
| **BL-60** | Підсумок = $\Sigma$ балів / к-сть питань **у ключі** $\cdot 100$, 1 знак `HALF_UP`; порожній ключ — виняток | `QuizScorer.score` | `QuizScorerTest.attemptScoreIsRoundedToOneDecimal`, `attemptScoreUsesKeySizeNotAnswersCount`, `attemptScoreSumsPartialCredit`, `emptyKeyThrows`, `missingAnswersGiveZeroPercent` | 0 / 33,3 / 66,7 / 100 %; 16,7; 14,3; 12,5; 6,25 → 6,3 (`HALF_UP`); відповіли на 2 з 4; змішаний ключ; `null`/порожній ключ; `null`-відповіді | Керничний | Матюшенко | 🔴 `BUG-M04` (ціле замість 1 знака) |
| **BL-40/50** (ключ) | Питання має `id`, непорожні варіанти й правильні, правильні $\subseteq$ варіантів; `isSingle` $\leftrightarrow$ рівно одна правильна | `QuizQuestion` (конструктор, `isSingle`) | `QuizQuestionTest.validQuestionKeepsItsData`, `isSingleDependsOnNumberOfCorrectOptions`, `invalidQuestionThrows` | 1, 2 і всі правильні; `null`/порожній `id`; `null`/порожні варіанти і правильні; правильний поза варіантами | Керничний | Матюшенко | 🟢 |
| **BL-70 (1)** | Кодове слово збігається точно, з урахуванням регістру | `EnrollmentValidator.keyMatches` | `EnrollmentValidatorTest.keyMatchesExactKey`, `keyIsCaseSensitiveAndExact` | інший регістр, пробіл на початку/в кінці, префікс, порожній рядок, інше слово | Черноус | Керничний | 🟢 |
| **BL-70 (2)** | `today` у межах періоду включно з початком і кінцем | `EnrollmentValidator.withinPeriod` | `EnrollmentValidatorTest.withinPeriodAtStartBoundary`, `withinPeriodAtEndBoundary`, `cannotEnrollAfterPeriodEnd` | 31.08 / 01.09 / 02.09 та 29.09 / 30.09 / 01.10 / 01.01.2027 | Черноус | Керничний | 🔴 `BUG-M05` (після кінця) |
| **BL-70 (3)** | Вільні місця: зараховано $<$ місткість | `EnrollmentValidator.hasCapacity` | `EnrollmentValidatorTest.hasCapacityAtBoundary`, `cannotEnrollWhenCourseIsFull` | 0/25, 24/25, 25/25, 26/25, 0/0 | Черноус | Керничний | 🔴 `BUG-M06` (`enrolled = capacity`) |
| **BL-70 (4)** | Передумова (якщо є) складена з підсумком $\ge 60$ | `EnrollmentValidator.prerequisiteMet` | `EnrollmentValidatorTest.prerequisiteMetAtPassThreshold`, `prerequisiteMetWhenNoPrerequisiteOrNotCompleted` | 0 / 59,99 / 60 / 60,01 / 100; передумови немає; не складено; складено інший курс; `null`-мапа оцінок | Черноус | Керничний | 🟢 |
| **BL-70** (рішення) | `canEnroll` = `true` лише коли виконано всі чотири умови одночасно | `EnrollmentValidator.canEnroll` | `EnrollmentValidatorTest.canEnrollWhenEveryConditionHoldsAtItsBoundary`, `cannotEnrollWithWrongKeyBeforeStartOrWithoutPrerequisite`, `cannotEnrollAfterPeriodEnd`, `cannotEnrollWhenCourseIsFull` | усі умови на межі (останній день, останнє місце, рівно 60); кожна з умов порушена окремо | Черноус | Керничний | 🔴 `BUG-M05`, `BUG-M06` |
| **BL-80** | Перелік порушених умов у порядку перевірки: `KEY`, `PERIOD`, `CAPACITY`, `PREREQUISITE` | `EnrollmentValidator.violations` | `EnrollmentValidatorTest.violationsEmptyWhenAllConditionsMet`, `violationsReportsSingleViolationCode`, `violationsKeepCheckOrder`, `violationsRejectNullArguments` | порожній перелік; по одному коду на кожну умову; усі чотири разом; пари (`KEY`+`CAPACITY`, `PERIOD`+`PREREQUISITE`); `null` у кожному з 4 аргументів | Черноус | Керничний | 🟢 |

## Підсумок

| Показник | Значення |
| :--- | :--- |
| Класів модуля з нетривіальною логікою | 4 (`GradeCalculator`, `QuizScorer`, `EnrollmentValidator`, `QuizQuestion`) — усі покрито |
| Усіх BL-вимог (`BL-10`…`BL-80`) | 8 — усі мають тести |
| Тест-методів (параметризований = 1) | 41 (`GradeCalculatorTest` — 11, `QuizScorerTest` — 12, `QuizQuestionTest` — 3, `EnrollmentValidatorTest` — 15) |
| Запусків у звіті Surefire | 122 (кожен набір даних параметризованого тесту — окремий запуск) |
| Червоних запусків на `v1` | 22 — усі пояснюються шістьма звітами про дефекти (`BUG-M01`…`BUG-M06`) |

## Примітки аналітика

1. **BL-80 у SRS нечітка.** Формулювання «перелік порушених умов у зручному для користувача вигляді» не визначає ні формату, ні кодів, ні порядку, тобто вимога неперевірювана як написана. Очікування для `violations` взято з `README.md` модуля («коди порушень у порядку перевірки: `KEY`, `PERIOD`, `CAPACITY`, `PREREQUISITE`») та Javadoc методу. Рекомендація команди для наступної версії SRS: уточнити BL-80 (формат — список кодів, порядок, порожній список = умови виконано) та зареєструвати це як дефект вимоги.
2. **Допоміжні тести.** `QuizQuestionTest` перевіряє валідацію запису-ключа з Javadoc; формально вона не винесена в окрему BL-вимогу, але від `isSingle()` залежить вибір формули BL-40 чи BL-50, тому тести простежуються до BL-40/BL-50.
3. **Ізоляція дефектів.** Тести `violations*` навмисно використовують «безпечні» значення (дата до початку періоду, зараховано більше за місткість), щоб їхній результат не залежав від `BUG-M05` і `BUG-M06`; дефекти фіксуються тестами `withinPeriodAtEndBoundary`, `hasCapacityAtBoundary`, `cannotEnroll*`.
4. **Не покрито навмисно:** гетери/конструктори без логіки записів `GradeItem`, `Student` (його конструктор лише замінює `null` на порожню мапу — перевірено у `prerequisiteMetWhenNoPrerequisiteOrNotCompleted`) і `Course`.