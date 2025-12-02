# SAP Integration Event Dashboard

A real-time monitoring dashboard for SAP Cloud Integration (SCI) events using Spring Boot, Kafka, and Thymeleaf. Track integration flow executions, monitor success/failure rates, and manage event lifecycle in a modern, responsive web UI.

---

## 📊 Features

✅ **Real-time Event Monitoring** - Track SAP Cloud Integration events as they happen  
✅ **Dashboard Analytics** - View success rates, failed events, pending tasks at a glance  
✅ **Search & Filter** - Find events by Order ID and filter by status (SUCCESS, FAILED, PENDING)  
✅ **Event Management** - Retry failed events with one click  
✅ **Kafka Integration** - Publish/consume events from Apache Kafka topics  
✅ **Responsive UI** - Dark mode support, mobile-friendly design  
✅ **RESTful API** - Receive events from external systems via REST endpoints  
✅ **Sample Data Generator** - Auto-populate dashboard with demo data on startup  

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    SAP Cloud Integration                        │
│                  (Production Integration Flows)                 │
└────────────────┬────────────────────────────────────────────────┘
                 │ REST API / Kafka Events
                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                        │
│  ┌────────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │  Controllers   │  │   Services   │  │  Repositories    │   │
│  │  (REST APIs)   │  │  (Business   │  │  (JPA, Database) │   │
│  │                │  │   Logic)     │  │                  │   │
│  └────────────────┘  └──────────────┘  └──────────────────┘   │
│         │                   │                   │              │
│         └───────────────────┼───────────────────┘              │
│                             ▼                                  │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │              Kafka Producer/Consumer                    │ │
│  │      (Event Publishing & Subscription)                  │ │
│  └──────────────────────────────────────────────────────────┘ │
│                             │                                  │
└────────────────┬────────────┼──────────────────────────────────┘
                 │            │
                 ▼            ▼
         ┌──────────────┬─────────────┐
         │  H2 Database │   Kafka     │
         │  (Events)    │   Broker    │
         └──────────────┴─────────────┘

         ▲
         │ Thymeleaf + HTML/CSS/JS
         │
┌────────┴──────────────────────────────┐
│     Web Browser (Dashboard UI)        │
│  - Statistics Cards                   │
│  - Event Table with Search/Filter     │
│  - Status Badge & Retry Buttons       │
│  - Event Chart & Analytics            │
└───────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.0 |
| **Web** | Thymeleaf, Spring MVC | Latest |
| **Database** | H2 (Development), any SQL for Production | 2.3.232 |
| **Messaging** | Apache Kafka | Latest |
| **ORM** | Spring Data JPA, Hibernate | Latest |
| **Build** | Maven | 3.x |
| **Java** | OpenJDK | 11+ |
| **UI Framework** | Vanilla HTML/CSS/JS, Chart.js | Latest |

---

## 📋 Prerequisites

- **Java 11+** - `java -version`
- **Maven 3.6+** - `mvn -version`
- **Kafka** - Local or Docker (optional, for real-time events)
- **Linux/Mac/Windows** with terminal access

### Setup Check

```bash
# Verify Java
java -version

# Verify Maven
mvn -version

# Optional: Verify Kafka (if you have it installed)
cd ~/kafka_setup
ls bin/kafka-server-start.sh
```

---

## 🚀 Installation & Setup

### Step 1: Clone/Download Project

```bash
git clone https://github.com/yourusername/sap-dashboard.git
cd sap-dashboard
```

Or create from scratch using Spring Boot Initializr:
- Go to https://start.spring.io/
- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.2.0
- **Dependencies**: Web, Thymeleaf, Data JPA, Kafka, H2
- Download and extract

### Step 2: Project Structure

```
sap-dashboard/
├── src/main/java/com/example/sapdashboard/
│   ├── SapDashboardApplication.java          (Main entry point)
│   ├── model/
│   │   └── IntegrationEvent.java             (Entity)
│   ├── repository/
│   │   └── IntegrationEventRepository.java   (JPA Repository)
│   ├── service/
│   │   └── EventService.java                 (Business logic)
│   ├── controller/
│   │   └── DashboardController.java          (REST endpoints)
│   ├── kafka/
│   │   ├── KafkaConfig.java                  (Topic config)
│   │   ├── KafkaProducer.java                (Send events)
│   │   └── KafkaConsumer.java                (Receive events)
│   ├── config/
│   │   └── KafkaConfig.java                  (Kafka setup)
│   └── util/
│       └── SampleDataGenerator.java          (Demo data)
├── src/main/resources/
│   ├── application.properties                (Config)
│   ├── templates/
│   │   └── dashboard.html                    (UI template)
│   └── static/
│       ├── css/style.css                     (Styling)
│       └── js/dashboard.js                   (Interactivity)
├── pom.xml                                    (Dependencies)
└── README.md                                  (This file)
```

### Step 3: Configure Application

Edit: `src/main/resources/application.properties`

```properties
# Server
server.port=8080
server.servlet.context-path=/

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML

# Database (H2 - Development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=dashboard-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*

# Logging
logging.level.root=INFO
logging.level.com.example.sapdashboard=DEBUG
```

### Step 4: Update pom.xml

Ensure your `pom.xml` has correct dependencies and Lombok configuration. See the project setup guide for complete pom.xml.

---

## 💻 Running the Application

### Development Mode (Simple - No Kafka)

```bash
cd sap-dashboard

# Clean build
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

Expected output:
```
✅ Initialized JPA EntityManagerFactory
🔄 Generating sample data...
✓ Created: PO-00001 - SUCCESS
...
✅ Sample data generated successfully!
🚀 SAP Integration Event Dashboard is running!
📊 Open browser: http://localhost:8080
```

Visit: **http://localhost:8080**

---

### Production Mode (With Kafka)

#### Terminal 1: Start Zookeeper

```bash
cd ~/kafka_setup
bin/zookeeper-server-start.sh config/zookeeper.properties
```

#### Terminal 2: Start Kafka Broker

```bash
cd ~/kafka_setup
bin/kafka-server-start.sh config/server.properties
```

#### Terminal 3: Start Spring Boot Application

```bash
cd ~/Documents/sap-dashboard
mvn spring-boot:run
```

The application will:
1. Connect to Kafka on `localhost:9092`
2. Create/subscribe to `sap-integration-events` topic
3. Generate 20 sample events
4. Start listening on http://localhost:8080

---

## 📊 Adding Sample Data

### Method 1: Auto-Generate on Startup (RECOMMENDED)

The application includes `SampleDataGenerator.java` that automatically creates 20 events on startup.

**File:** `src/main/java/com/example/sapdashboard/util/SampleDataGenerator.java`

```java
@Component
@RequiredArgsConstructor
public class SampleDataGenerator {

    private final IntegrationEventRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void generateSampleData() {
        System.out.println("🔄 Generating sample data...");

        for (int i = 1; i <= 20; i++) {
            IntegrationEvent event = new IntegrationEvent();
            event.setOrderId("PO-" + String.format("%05d", i));
            event.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
            event.setIntegrationName(INTEGRATIONS[random.nextInt(INTEGRATIONS.length)]);
            event.setMessage(MESSAGES[random.nextInt(MESSAGES.length)]);
            event.setTimestamp(LocalDateTime.now().minusMinutes(random.nextInt(60) + 1));
            
            if (event.getStatus().equals("FAILED")) {
                event.setErrorDetails(ERROR_MESSAGES[random.nextInt(ERROR_MESSAGES.length)]);
            }

            repository.save(event);
        }

        System.out.println("✅ Sample data generated successfully!");
    }
}
```

**Result:** 20 events appear in dashboard on startup! ✅

---

### Method 2: Send Events via REST API

Use **curl** or **Postman** to manually send events.

#### Success Event

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "PO-12345",
    "status": "SUCCESS",
    "message": "Order placed successfully",
    "integrationName": "Order-to-SAP"
  }'
```

#### Failed Event with Error Details

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "PO-12346",
    "status": "FAILED",
    "message": "Network timeout",
    "integrationName": "Customer-Sync",
    "errorDetails": "Connection refused to SAP backend"
  }'
```

#### Pending Event

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "PO-12347",
    "status": "PENDING",
    "message": "Processing started",
    "integrationName": "Inventory-Update"
  }'
```

**Response:**
```json
{
  "status": "success",
  "message": "Event received and published to Kafka",
  "orderId": "PO-12345"
}
```

Then refresh dashboard: **http://localhost:8080** to see new events! ✅

---

### Method 3: Send Events via Kafka

If Kafka is running, publish events to the `sap-integration-events` topic:

```bash
cd ~/kafka_setup

# Start Kafka producer
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic sap-integration-events

# Paste this JSON and press Enter:
{"orderId":"PO-99999","status":"SUCCESS","message":"Kafka test event","integrationName":"Test-Integration","timestamp":"2025-12-01T20:00:00","errorDetails":""}
```

The dashboard will automatically receive and display the event! ✅

---

## 🔗 Integration with SAP Cloud Integration (Production)

### Step 1: Configure SAP BTP REST API

Your SAP Cloud Integration flows should send events to your dashboard.

**Endpoint:** `http://your-dashboard-server/api/events`

**Method:** POST

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "orderId": "SO-123456",
  "status": "SUCCESS",
  "message": "Order processing completed",
  "integrationName": "SalesOrder-to-SAP",
  "errorDetails": ""
}
```

---

### Step 2: Configure in SAP Integration Suite

#### Option A: Using Request Reply Pattern

In your iFlow:
1. Add **Request Reply** step
2. Configure HTTP adapter:
   - **Method:** POST
   - **Address:** `http://your-dashboard:8080/api/events`
   - **Headers:** Content-Type: application/json
3. Map your integration payload to event format

#### Option B: Using Kafka Adapter (Recommended)

1. Add **Apache Kafka** adapter
2. Configure:
   - **Kafka Broker URL:** `kafka-broker.example.com:9092`
   - **Topic:** `sap-integration-events`
   - **Message Format:** JSON
3. Map your iFlow data to IntegrationEvent format

---

### Step 3: Sample Integration Payload

From your SAP S/4HANA order process:

```json
{
  "orderId": "{{ Order ID from S/4HANA }}",
  "status": "{{ Integration Status: SUCCESS/FAILED/PENDING }}",
  "message": "{{ Business process message }}",
  "integrationName": "{{ Your iFlow name }}",
  "errorDetails": "{{ Error message if failed }}"
}
```

---

## 📋 API Reference

### POST /api/events

Create a new integration event.

**Request:**
```json
{
  "orderId": "PO-12345",
  "status": "SUCCESS",
  "message": "Event description",
  "integrationName": "Integration-Name",
  "errorDetails": ""
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Event received and published to Kafka",
  "orderId": "PO-12345"
}
```

---

### GET /

Get the dashboard UI.

**Response:** HTML dashboard page

---

### GET /health

Check application health.

**Response:**
```json
{
  "status": "UP",
  "message": "Dashboard is running",
  "timestamp": 1701532800000
}
```

---

## 🎯 Dashboard Features

| Feature | Description |
|---------|-------------|
| **Statistics Cards** | Total events, Success rate %, Failed count, Pending count |
| **Event Table** | Search, filter, view details, retry failed events |
| **Status Filter** | Filter by SUCCESS, FAILED, or PENDING |
| **Search** | Find events by Order ID |
| **Retry Button** | Mark failed events for reprocessing |
| **Chart** | Pie chart showing event status breakdown |
| **Dark Mode** | Toggle dark/light theme |
| **Time Display** | Shows "X minutes ago" format |

---

## 🧪 Testing

### Unit Test Example

```java
@SpringBootTest
class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private IntegrationEventRepository repository;

    @Test
    void testGetRecentEvents() {
        IntegrationEvent event = new IntegrationEvent(
            "PO-123", "SUCCESS", "Test", "Test-Integration"
        );
        repository.save(event);

        List<IntegrationEvent> events = eventService.getRecentEvents();
        
        assertThat(events).isNotEmpty();
        assertThat(events.get(0).getOrderId()).isEqualTo("PO-123");
    }
}
```

---

## 🐳 Docker Deployment (Optional)

### Dockerfile

```dockerfile
FROM openjdk:11-jre-slim
COPY target/sap-dashboard-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
```

### Build & Run

```bash
# Build Docker image
docker build -t sap-dashboard:1.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  sap-dashboard:1.0
```

---

## 🔧 Troubleshooting

### Dashboard shows all zeros

**Solution:** Add `SampleDataGenerator.java` or send events via REST API

```bash
mvn spring-boot:run
# Wait for "Sample data generated successfully!" message
# Refresh http://localhost:8080
```

---

### Events not appearing after REST call

**Check 1:** Verify Kafka is running (if using Kafka)

```bash
# Check Kafka topic
cd ~/kafka_setup
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic sap-integration-events --from-beginning
```

**Check 2:** Check application logs

```bash
# Look for error messages in terminal where app is running
```

---

### Port 8080 already in use

**Solution:** Change port in `application.properties`

```properties
server.port=8081
```

---

### Kafka connection errors

**Solution:** Verify Kafka is running and accessible

```bash
# Check Kafka status
ps aux | grep kafka

# If not running, start it:
cd ~/kafka_setup
bin/zookeeper-server-start.sh config/zookeeper.properties
# In another terminal:
bin/kafka-server-start.sh config/server.properties
```

---

## 📚 Documentation Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Apache Kafka](https://kafka.apache.org/)
- [SAP Cloud Integration](https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/en-US)
- [Thymeleaf Template Engine](https://www.thymeleaf.org/)

---

## 📝 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 👨‍💼 Author

Created by: SAP Integration Consultant

**Contact:** [Your Email]  
**LinkedIn:** [Your Profile]  
**GitHub:** [Your Repository]

---

## 🎯 Future Enhancements

- [ ] Real-time WebSocket updates (remove manual refresh)
- [ ] Database persistence (PostgreSQL/Oracle)
- [ ] Authentication & Authorization (OAuth2)
- [ ] Event archival & historical reports
- [ ] Alerting & Notifications (Email, Slack)
- [ ] Performance metrics & SLA tracking
- [ ] Integration with SAP Analytics Cloud
- [ ] Multi-tenant support
- [ ] Mobile app (React Native/Flutter)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/NewFeature`)
3. Commit changes (`git commit -m 'Add NewFeature'`)
4. Push to branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

---

## 📞 Support

For issues, questions, or suggestions:
- Open a GitHub Issue
- Contact the development team
- Check the Wiki for FAQs

---

**Happy Monitoring! 🚀**
