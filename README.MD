# Data Parser

## Опис проекту

Багатопоточна консольна утиліта для аналізу JSON-даних із автоматичним формуванням статистики за атрибутами предметної
області.

### Ключові особливості

- **Потокова обробка JSON** — Jackson Streaming API дозволяє ефективно працювати з великими файлами без завантаження всього в пам’ять.
- **Багатопотокова обробка** — використання thread pool для паралельної обробки файлів у директорії.
- **Гнучкий формат вхідних даних** — підтримка як масивів JSON-об’єктів, так і одиночних JSON-записів.
- **Підтримка складних атрибутів** — значення атрибутів можуть містити списки (розділені комами), кожне з яких обробляється окремо.
- **Генерація звітів** — автоматичне формування XML-файлів зі статистикою.


## Предметна область

### Основна сутність: **Order**

Відношення: **багато-до-одного** (багато Order → один Client)

#### Атрибути:

| Атрибут         | Тип           | Опис                                  | Приклад                                                |
|-----------------|---------------|---------------------------------------|--------------------------------------------------------|
| id              | Long          | Унікальний ідентифікатор замовлення   | 4                                                      |
| orderNumber     | String        | Номер замовлення                      | "ORD-1004"                                             |
| orderDate       | LocalDateTime | Дата та час створення замовлення      | "2025-01-12T16:45:00"                                  |
| status          | String        | Статус замовлення                     | "DELIVERED"                                            |
| totalAmount     | Double        | Загальна сума замовлення              | 310.75                                                 |
| deliveryAddress | Address       | Адреса доставки                       | "city": "Kyiv","street": "Street 43","houseNumber": 69 |
| paymentMethod   | String        | Метод оплати                          | "paypal"                                               |
| clientId        | Long          | Ідентифікатор клієнта                 | 101                                                    |
| createdAt       | LocalDateTime | Дата та час створення запису          | "2025-01-12T16:20:00"                                  |
| updatedAt       | LocalDateTime | Дата та час останнього оновлення      | "2025-01-12T16:45:00"                                  |
| tags            | String        | Теги/категорії замовлення, через кому | "electronics, gadgets"                                 |

### Другорядна сутність: **Client**

#### Атрибути:

| Атрибут          | Тип       | Опис                             | Приклад               |
|------------------|-----------|----------------------------------|-----------------------|
| id               | Long      | Унікальний ідентифікатор клієнта | 101                   |
| firstName        | String    | Ім'я клієнта                     | "Yurii"               |
| lastName         | String    | Прізвище клієнта                 | "Doe"                 |
| email            | String    | Електронна пошта клієнта         | "yurii.doe@email.com" |
| phone            | String    | Номер телефону клієнта           | "+380501234567"       |
| registrationDate | LocalDate | Дата реєстрації клієнта          | "2025-10-10"          |

## Структура проекту

```
DataParser/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── edu/
│   │   │       ├── internship/
│   │   │           ├── config/
│   │   │           │   └── AppConfig.java
│   │   │           ├── model/
│   │   │           │   ├── Address.java
│   │   │           │   ├── Client.java
│   │   │           │   └── Order.java
│   │   │           ├── parser/
│   │   │           │   ├── JsonFileParser.java
│   │   │           │   └── JsonFileProcessor.java
│   │   │           ├── report/
│   │   │           │   └── XMLReportGenerator.java
│   │   │           ├── testutils/
│   │   │           │   └── JsonOrderGenerator.java
│   │   │           └── Main.java
│   │   ├── resources/
│   │       ├── data/
│   │           └── orders-test.json
│   ├── test/
│       ├── java/
│           ├── internship/
│               ├── parser/
│               │   ├── JsonFileParserTest.java
│               │   └── JsonFileProcessorTest.java
│               ├── report/
│                   └── XMLReportGeneratorTest.java
├── README.MD
├── dependency-reduced-pom.xml
└── pom.xml
```

## Приклади файлів

### Вхідний файл: `orders-test.json`

```json
[
  {
    "id": 1,
    "orderNumber": "ORD-1001",
    "orderDate": "2025-10-02T00:00:00.837989",
    "status": "DELIVERED",
    "totalAmount": 1217.03,
    "deliveryAddress": {
      "city": "Kyiv",
      "street": "Street 43",
      "houseNumber": 69
    },
    "paymentMethod": "paypal",
    "comment": "",
    "clientId": 187,
    "createdAt": "2025-10-08T20:20:00.839218",
    "updatedAt": "2025-10-26T20:47:00.839245",
    "tags": "furniture,fashion"
  },
  {
    "id": 2,
    "orderNumber": "ORD-1002",
    "orderDate": "2025-11-02T20:41:00.839281",
    "status": "CANCELLED",
    "totalAmount": 1058.38,
    "deliveryAddress": {
      "city": "Odesa",
      "street": "Street 95",
      "houseNumber": 30
    },
    "paymentMethod": "paypal",
    "comment": "Autogenerated",
    "clientId": 131,
    "createdAt": "2025-10-16T14:53:00.839335",
    "updatedAt": "2025-10-14T00:51:00.839353",
    "tags": "furniture,home,education"
  },
  {
    "id": 3,
    "orderNumber": "ORD-1003",
    "orderDate": "2025-11-11T18:24:00.839379",
    "status": "DELIVERED",
    "totalAmount": 1224.28,
    "deliveryAddress": {
      "city": "Lviv",
      "street": "Street 7",
      "houseNumber": 24
    },
    "paymentMethod": "credit_card",
    "comment": "",
    "clientId": 262,
    "createdAt": "2025-10-26T23:11:00.839425",
    "updatedAt": "2025-10-19T15:11:00.839440",
    "tags": "education,education"
  }
]
```

### Вихідний файл: `statistics_by_status.xml`

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<statistics attribute="status" totalCount="100" totalItems="4">
    <item>
        <value>DELIVERED</value>
        <count>30</count>
    </item>
    <item>
        <value>PROCESSING</value>
        <count>28</count>
    </item>
    <item>
        <value>CANCELLED</value>
        <count>22</count>
    </item>
    <item>
        <value>PENDING</value>
        <count>20</count>
    </item>
</statistics>
```

## Використання

### Компіляція та створення JAR

```bash
mvn clean package
```

### Запуск

#### Формат команди

```
java -jar data-parser-1.0-SNAPSHOT-shaded.jar <папка> <атрибут> [потоки]
```

**Параметри:**

- `<папка>` - шлях до папки з JSON-файлами (обов'язковий)
- `<атрибут>` - назва атрибута для статистики (обов'язковий)
- `[потоки]` - кількість потоків (опціонально, за замовчуванням = к-сть CPU ядер)

```bash
# Базовий запуск (автоматичний вибір кількості потоків)
java -jar data-parser-1.0-SNAPSHOT-shaded.jar ./data status

# З вказаною кількістю потоків
java -jar tdata-parser-1.0-SNAPSHOT-shaded.jar ./data  status  8

```

### Приклади виводу

```
================================================================================
  JSON STATISTICS PARSER
================================================================================
✓ Successfully completed!

File saved:  /DataParser/statistics/statistics_by_tags.xml

Threads: 8, execution time: 648 ms
```

## Експерименти з багатопоточністю

### Тестове середовище

| Компонент          | Характеристика       |
|--------------------|----------------------|
| Процесор           | Apple M2             |
| Оперативна пам’ять | 8 GB LPDDR5          |
| JDK                | OpenJDK 21.0.9       |
| Операційна система | macOS 15.6 (Sequoia) |

### Набір даних

- Файлів: 8
- Записів на файл: 50_000
- Загальна кількість: 400_000
- Розмір: ~138.4 MB



### Результати:

### За атрибутом `status`

| Потоків | Час (мс) | Прискорення |
|---------|----------|-------------|
| 1       | 538 ms   | 1.00x       |
| 2       | 400 ms   | 1.345x      |
| 4       | 358 ms   | 1.5x        |
| 8       | 326 ms   | 1.65x       |

---

### За атрибутом `tags`

тестовий атрибут, що містить кілька значень, розділених комою

| Потоків | Час (мс) | Прискорення |
|---------|----------|-------------|
| 1       | 671 ms   | 1.00x       |
| 2       | 495 ms   | 1.355x      |
| 4       | 483 ms   | 1.389x      |
| 8       | 462 ms   | 1.452x      |


## JsonOrderGenerator

JsonOrderGenerator — утилітний клас для генерації випадкових тестових даних для об’єктів Order у форматі JSON. 

Основні можливості:

- Генерує випадкові замовлення з полями id, orderNumber, orderDate, status, totalAmount, deliveryAddress, paymentMethod, clientId.
- Підтримує вкладені об’єкти, наприклад Address з city, street, houseNumber.
- Зберігає дані у JSON-файли в обрану папку.





