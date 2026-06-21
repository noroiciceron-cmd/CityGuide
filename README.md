# Гид по Хабаровску / CityGuide

**CityGuide** — учебное Android-приложение на Kotlin, представляющее собой мобильный гид по достопримечательностям Хабаровска. Приложение содержит список мест, карточки с описанием, карту, заметки, избранное, фото пользователя, аудио/видео, виджет «Место дня» и экран облачной рекомендации через Firebase Realtime Database.

## Основные возможности

- Просмотр достопримечательностей Хабаровска.
- Поиск мест по названию или категории.
- Карточка места с изображением, описанием и категорией.
- Добавление места в избранное.
- Создание, изменение и удаление заметок к конкретному месту.
- Съёмка фотографий через камеру устройства.
- Сохранение нескольких фотографий для каждого места.
- Переключение между фотографиями пользователя.
- Открытие сохранённой фотографии во внешнем просмотрщике.
- Аудиогид.
- Видеоэкран с настройкой автоматического запуска видео.
- Виджет «Место дня» со случайным местом и переходом в карточку места.
- Экран «Облако» с рекомендацией из Firebase Realtime Database.
- Пользовательская иконка приложения.

## Технологии

- Kotlin
- Android SDK
- XML layouts
- SQLite
- SharedPreferences
- FileProvider
- AppWidgetProvider
- Firebase Realtime Database
- osmdroid
- Gradle Kotlin DSL

## Требования для запуска

Перед запуском проекта на новом компьютере нужно установить:

- Android Studio
- Git
- Android SDK через Android Studio
- JDK, который поставляется вместе с Android Studio
- Android-устройство с включённой USB-отладкой или Android Emulator

## Как скачать проект

Открой терминал и выполни:

```bash
git clone https://github.com/noroiciceron-cmd/CityGuide.git
```

Перейди в папку проекта:

```bash
cd CityGuide/CityGuide
```

Внутри этой папки должны находиться файлы Gradle-проекта, например:

```text
build.gradle.kts
settings.gradle.kts
gradlew.bat
app/
```

Если открываешь проект через Android Studio, лучше открывать именно внутреннюю папку:

```text
CityGuide/CityGuide
```

## Как открыть проект в Android Studio

1. Запусти Android Studio.
2. Нажми **Open**.
3. Выбери папку:

   ```text
   CityGuide/CityGuide
   ```

4. Дождись Gradle Sync.
5. Если Android Studio предложит установить недостающие SDK-компоненты, согласись.
6. После завершения синхронизации выбери устройство или эмулятор.
7. Нажми **Run**.

## Как собрать проект через терминал

На Windows из папки `CityGuide/CityGuide` выполни:

```bash
.\gradlew.bat assembleDebug
```

На macOS или Linux:

```bash
./gradlew assembleDebug
```

Если сборка успешна, APK будет создан примерно по пути:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Как запустить на физическом Android-устройстве

1. Подключи телефон к компьютеру по USB.
2. На телефоне включи **Developer options**.
3. Включи **USB debugging**.
4. Подтверди RSA-запрос на телефоне, если он появится.
5. В Android Studio выбери телефон в списке устройств.
6. Нажми **Run**.

При первом использовании приложение может запросить разрешения, например для камеры или уведомлений. Их нужно разрешить, чтобы соответствующие функции работали корректно.

## Как запустить на эмуляторе

1. Открой Android Studio.
2. Перейди в **Device Manager**.
3. Создай виртуальное устройство.
4. Выбери образ Android.
5. Запусти эмулятор.
6. Нажми **Run** в Android Studio.

Для проверки Firebase и карты у эмулятора должен работать интернет.

## Firebase Realtime Database

Приложение использует Firebase Realtime Database для экрана **«Облако»**.

Ожидаемая структура данных в базе:

```json
{
  "recommendation": {
    "title": "Набережная Амура",
    "text": "Сегодня стоит прогуляться по набережной Амура."
  }
}
```

Приложение читает данные по путям:

```text
/recommendation/title
/recommendation/text
```

Если Firebase уже настроен и файл `google-services.json` находится в папке:

```text
app/google-services.json
```

то дополнительных действий обычно не требуется.

Если Firebase не работает на новом компьютере или в новом аккаунте, открой файл:

```text
FIREBASE_SETUP.md
```

и выполни настройку по инструкции.

Также проверь файл:

```text
app/src/main/java/com/example/cityguide/CloudActivity.kt
```

В нём URL базы должен совпадать с URL во вкладке **Realtime Database -> Data** в Firebase Console.

## Рекомендуемые Firebase Rules для учебной проверки

Для учебного проекта можно оставить публичное чтение только ветки `recommendation`, а запись закрыть:

```json
{
  "rules": {
    ".read": false,
    ".write": false,

    "recommendation": {
      ".read": true,
      ".write": false
    }
  }
}
```

С такими правилами приложение сможет читать облачную рекомендацию, но пользователи приложения не смогут изменять данные в базе.

Изменять `title` и `text` вручную всё равно можно через Firebase Console.

## Где находятся основные файлы

```text
app/src/main/java/com/example/cityguide/MainActivity.kt
```

Главный экран приложения, список мест и поиск.

```text
app/src/main/java/com/example/cityguide/PlaceDetailsActivity.kt
```

Карточка места, избранное, заметки, камера, фото, аудио, видео, уведомления и переход к карте.

```text
app/src/main/java/com/example/cityguide/SavedPlaceDbHelper.kt
```

SQLite-база для избранного, заметок и фотографий.

```text
app/src/main/java/com/example/cityguide/SettingsActivity.kt
```

Настройки приложения, включая автоматический запуск видео.

```text
app/src/main/java/com/example/cityguide/VideoActivity.kt
```

Экран видео и логика автозапуска.

```text
app/src/main/java/com/example/cityguide/CloudActivity.kt
```

Экран облачной рекомендации из Firebase Realtime Database.

```text
app/src/main/java/com/example/cityguide/CityPlaceWidgetProvider.kt
```

Виджет «Место дня».

```text
app/src/main/res/values/strings.xml
```

Текстовые ресурсы приложения.

```text
app/src/main/res/layout/
```

XML-разметка экранов.

```text
app/src/main/res/mipmap-*/
```

Иконки приложения.

## Частые проблемы

### Кнопка Run неактивна

Дождись окончания Gradle Sync. Если синхронизация не началась автоматически, нажми **Sync Project with Gradle Files**.

### Приложение не собирается

Выполни:

```bash
.\gradlew.bat assembleDebug
```

и смотри ошибку в терминале. Ошибки из вкладки Layout Preview не всегда означают проблему сборки.

### Layout Preview показывает Render problem

Если `assembleDebug` проходит успешно, то Render problem в Android Studio можно временно игнорировать. Это ошибка предпросмотра, а не обязательно ошибка приложения.

### Firebase показывает ошибку

Проверь:

- есть ли `app/google-services.json`;
- совпадает ли package name `com.example.cityguide`;
- создана ли Realtime Database;
- есть ли объект `/recommendation`;
- разрешено ли чтение в Firebase Rules;
- совпадает ли `DATABASE_URL` в `CloudActivity.kt` с URL твоей базы;
- работает ли интернет на устройстве или эмуляторе.

### Новая иконка не отображается на телефоне

Лаунчер телефона может кэшировать старую иконку. Удали приложение с устройства и установи заново через Android Studio.

## Документация проекта

В репозитории также есть дополнительные файлы:

```text
FIREBASE_SETUP.md
```

Подробная настройка Firebase Realtime Database.

```text
LABS_COVERAGE.md
```

Описание того, какие лабораторные требования покрываются текущей логикой приложения.

## Примечание

Этот проект является учебным Android-приложением. Он предназначен для демонстрации работы с Kotlin, Android UI, локальным хранением данных, камерой, виджетами и Firebase Realtime Database.
