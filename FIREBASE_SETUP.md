# Настройка Firebase Realtime Database

Этот файл описывает, как студенту настроить Firebase для приложения **«Гид по Хабаровску»** в своём аккаунте.

## 1. Создать проект Firebase

1. Откройте Firebase Console:

   <https://console.firebase.google.com>

2. Нажмите **Create a project** или **Add project**.

3. Название проекта можно указать, например:

   ```text
   CityGuide
   ```

4. Google Analytics можно отключить. Для Realtime Database он не нужен.

5. Дождитесь создания проекта.

## 2. Добавить Android-приложение

1. На главной странице проекта Firebase нажмите значок Android.

2. В поле **Android package name** укажите:

   ```text
   com.example.cityguide
   ```

3. В поле **App nickname** можно указать:

   ```text
   CityGuide
   ```

4. SHA-1 для этой лабораторной работы не нужен. Поле можно оставить пустым.

5. Нажмите **Register app**.

## 3. Скачать google-services.json

После регистрации Android-приложения Firebase предложит скачать файл:

```text
google-services.json
```

Его нужно положить в папку:

```text
CityGuide/app/google-services.json
```

Полный путь в этом проекте:

```text
/Users/aleksandr/src/AndroidLabs/CityGuide/app/google-services.json
```

После добавления файла нужно синхронизировать Gradle в Android Studio или заново собрать проект.

## 4. Проверить Gradle-настройки

В проекте уже добавлена зависимость Firebase Realtime Database.

В `app/build.gradle.kts` есть логика:

```kotlin
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
```

Это значит, что Firebase Gradle plugin применяется только после добавления файла `google-services.json`.

Также в зависимостях уже есть Firebase Database:

```kotlin
implementation(libs.firebase.database)
```

Обычно ничего дополнительно копировать из Firebase Console в Gradle не нужно.

## 5. Создать Realtime Database

1. В Firebase Console откройте свой проект.

2. В левом меню выберите:

   ```text
   Build -> Realtime Database
   ```

3. Если база ещё не создана, нажмите **Create Database**.

4. Выберите регион.

5. Для лабораторной работы можно выбрать **Start in test mode**.

После создания базы должна открыться вкладка **Data**.

## 6. Добавить данные рекомендации

В Realtime Database нужно создать структуру:

```json
{
  "recommendation": {
    "title": "Набережная Амура",
    "text": "Сегодня стоит прогуляться по набережной Амура."
  }
}
```

То есть в корне базы должен быть объект:

```text
recommendation
```

Внутри него два поля:

```text
title
text
```

Приложение читает именно этот путь:

```text
/recommendation/title
/recommendation/text
```

## 7. Проверить URL базы

В этом проекте `CloudActivity` использует явный URL базы:

```kotlin
https://cityguide-76e7e-default-rtdb.europe-west1.firebasedatabase.app
```

Если студент создаёт Firebase-проект в своём аккаунте, URL будет другим. Его нужно посмотреть вверху вкладки **Realtime Database -> Data**.

Пример URL:

```text
https://your-project-id-default-rtdb.europe-west1.firebasedatabase.app
```

Затем нужно заменить константу `DATABASE_URL` в файле:

```text
app/src/main/java/com/example/cityguide/CloudActivity.kt
```

Пример:

```kotlin
private const val DATABASE_URL = "https://your-project-id-default-rtdb.europe-west1.firebasedatabase.app"
```

## 8. Настроить правила доступа

Для учебного проекта можно временно открыть чтение и запись.

Во вкладке **Rules** можно указать:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

Затем нажмите **Publish**.

Важно: такие правила подходят только для лабораторной работы. Для настоящего приложения они небезопасны.

## 9. Проверить работу в приложении

1. Убедитесь, что эмулятор или устройство подключены к интернету.

2. Запустите приложение.

3. На главном экране откройте меню с тремя точками.

4. Выберите **Облако**.

Если всё настроено правильно, приложение покажет:

```text
Набережная Амура
Сегодня стоит прогуляться по набережной Амура.
```

Если отображается ошибка, проверьте:

- файл `google-services.json` лежит в `CityGuide/app/`;
- package name в Firebase равен `com.example.cityguide`;
- Realtime Database создана;
- данные лежат по пути `/recommendation`;
- правила Firebase разрешают чтение;
- URL в `CloudActivity.kt` совпадает с URL вашей базы;
- на эмуляторе работает интернет.

