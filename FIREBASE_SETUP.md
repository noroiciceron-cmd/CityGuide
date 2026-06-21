# Настройка Firebase Realtime Database

Этот файл описывает настройку Firebase для Android-приложения **«Гид по Хабаровску»**.

В приложении используется **Firebase Realtime Database** для экрана **«Облако»**. Экран читает облачную рекомендацию из базы данных по пути:

```text
/recommendation/title
/recommendation/text
```

Приложение не записывает данные в Firebase. Значения `title` и `text` редактируются вручную через **Firebase Console**.

---

## 1. Создать проект Firebase

1. Откройте Firebase Console:

   ```text
   https://console.firebase.google.com
   ```

2. Нажмите **Create a project** или **Add project**.

3. Название проекта можно указать, например:

   ```text
   CityGuide
   ```

4. Google Analytics для этой лабораторной работы можно отключить.

5. Дождитесь создания проекта.

---

## 2. Добавить Android-приложение

1. На главной странице Firebase-проекта нажмите значок Android.

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

---

## 3. Скачать и добавить `google-services.json`

После регистрации Android-приложения Firebase предложит скачать файл:

```text
google-services.json
```

Его нужно положить в папку Android-модуля `app`.

Если Android Studio открыт из внутренней папки проекта, путь будет таким:

```text
app/google-services.json
```

Если открыт весь репозиторий GitHub, путь будет таким:

```text
CityGuide/app/google-services.json
```

После добавления файла нажмите **Sync Now** в Android Studio или пересоберите проект.

---

## 4. Проверить Gradle-настройки

В проекте уже предусмотрено подключение Firebase Gradle Plugin только при наличии файла `google-services.json`.

В файле:

```text
CityGuide/app/build.gradle.kts
```

используется логика:

```kotlin
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
```

Также в зависимостях проекта должна быть библиотека Realtime Database:

```kotlin
implementation(libs.firebase.database)
```

Обычно ничего дополнительно копировать из Firebase Console в Gradle не нужно.

---

## 5. Создать Realtime Database

1. В Firebase Console откройте свой проект.

2. В левом меню выберите:

   ```text
   Build -> Realtime Database
   ```

3. Нажмите **Create Database**.

4. Выберите регион базы данных.

5. Для первичной проверки можно выбрать **Start in test mode**.

После создания базы откроется вкладка **Data**.

---

## 6. Добавить данные рекомендации

В корне Realtime Database нужно создать объект:

```json
{
  "recommendation": {
    "title": "Набережная Амура",
    "text": "Сегодня стоит прогуляться по набережной Амура."
  }
}
```

Итоговая структура базы должна быть такой:

```text
recommendation
├── title: "Набережная Амура"
└── text: "Сегодня стоит прогуляться по набережной Амура."
```

Приложение читает именно эти поля:

```text
/recommendation/title
/recommendation/text
```

### Как добавить данные через Firebase Console

1. Откройте **Realtime Database -> Data**.
2. Нажмите на три точки в правой части панели данных.
3. Выберите **Import JSON**.
4. Импортируйте JSON-файл со структурой выше.

Можно также создать узел `recommendation` вручную и добавить в него два поля: `title` и `text`.

---

## 7. Проверить URL базы

В `CloudActivity.kt` используется явный URL Realtime Database.

Файл:

```text
CityGuide/app/src/main/java/com/example/cityguide/CloudActivity.kt
```

Внизу файла находится константа:

```kotlin
private const val DATABASE_URL = "https://your-project-id-default-rtdb.firebaseio.com"
```

URL нужно взять в Firebase Console во вкладке:

```text
Realtime Database -> Data
```

Он отображается сверху над деревом данных.

Пример:

```text
https://cityguide-xxxxx-default-rtdb.firebaseio.com
```

Если URL в `CloudActivity.kt` не совпадает с URL вашей базы, приложение будет обращаться не к той базе.

---

## 8. Настроить правила доступа

Для быстрой проверки Firebase можно временно использовать тестовые правила:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

Но оставлять такие правила не рекомендуется, потому что любой человек, знающий URL базы, сможет читать, изменять и удалять данные.

После проверки лучше использовать более безопасный вариант:

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

Эти правила означают:

- вся база по умолчанию закрыта;
- приложение может читать только `/recommendation`;
- приложение не может записывать данные;
- `title` и `text` можно по-прежнему менять вручную через Firebase Console.

После изменения правил нажмите **Publish**.

---

## 9. Проверить работу в приложении

1. Убедитесь, что устройство или эмулятор подключены к интернету.
2. Запустите приложение.
3. На главном экране откройте меню с тремя точками.
4. Выберите пункт **Облако**.

Если всё настроено правильно, приложение покажет:

```text
Набережная Амура
Сегодня стоит прогуляться по набережной Амура.
```

Если изменить `title` или `text` в Firebase Console, приложение получит новые данные при повторном открытии экрана **«Облако»** или при нажатии кнопки обновления.

---

## 10. Что проверить при ошибке

Если экран **«Облако»** показывает сообщение об ошибке, проверьте:

- файл `google-services.json` лежит в папке `app`;
- package name в Firebase равен `com.example.cityguide`;
- Realtime Database создана;
- данные лежат по пути `/recommendation`;
- внутри `recommendation` есть поля `title` и `text`;
- правила Firebase разрешают чтение `/recommendation`;
- URL в `CloudActivity.kt` совпадает с URL базы;
- на устройстве или эмуляторе работает интернет.

---

## 11. VPN и Firebase

Firebase работает через интернет и не требует, чтобы устройства находились в одной Wi-Fi сети.

Если одно устройство подключено через VPN, а другое без VPN, это нормально. Главное, чтобы VPN не блокировал доступ к сервисам Google/Firebase.

Если без VPN экран **«Облако»** работает, а с VPN не работает, причина обычно в VPN, DNS-фильтрации или блокировке доменов Firebase.
