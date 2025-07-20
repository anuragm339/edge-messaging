# Edge Messaging Platform

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.java.com/)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

Edge Messaging Platform is a modular, high-performance event bus and event storage system designed for Point of Sale (POS), IoT, and distributed retail edge applications.

It features a pluggable Java client SDK, annotation-based publish/subscribe, persistent event log (SQLite), and a future-proof architecture supporting pipe integration, clustering/sync, and priority dispatch.

---

## Features

* 🚀 **In-Memory Event Bus:** Topic-based publish/subscribe.
* 💿 **Durable Persistence:** SQLite/JDBC-based event log for reliable messaging and recovery.
* ☕️ **Java SDK:** Zero-boilerplate event emission using `@MessagePublisher`.
* 🔍 **Dynamic Listener Discovery:** Auto-registered event handlers via `@EventListener`.
* 📦 **Modular Monorepo:** Clean separation into common, platform, and SDK projects.
* 🔌 **Pluggable Architecture:** Ready for outbound “pipes”, multi-queue priorities, and distributed sync.
* 🌐 **REST API:** External event publishing. *(Future: gRPC & WebSocket endpoints.)*
* 🛠️ **Cloud/Edge Ready:** Deployable in both environments.

---

## Monorepo Structure

```plaintext
edge-messaging/
├── messaging-common/         # Shared annotations, models, interfaces
├── pos-client-sdk/           # Java SDK for POS and services (publishers)
├── pos-messaging-platform/   # Main event bus engine & persistence
├── settings.gradle
└── build.gradle
```

---

## Quick Start

### 1️⃣ Clone & Build

```bash
git clone https://github.com/anuragm339/edge-messaging.git
cd edge-messaging
./gradlew clean build
```

### 2️⃣ Run Messaging Platform

```bash
cd pos-messaging-platform
./gradlew run
```

By default, the API will be available at:
[http://localhost:8080](http://localhost:8080)

---

## How It Works

### Shared Contracts

All shared models and annotations are under `messaging-common`:

* **`Event.java`**: Defines the event payload.
* **`@MessagePublisher`**: Marks publisher methods in external apps/SDK.
* **`@EventListener`**: Marks consumer/handler methods for event handling.
* **`EventPublisherService`**: Interface implemented by both SDK and platform.

---

### Event Flow Overview

**Publishing Events:**

```java
@MessagePublisher(topic = "sales")
public void publishSale(String json) {
    // Implementation handled by proxy
}
```

* SDK (from `pos-client-sdk`) sends events to the platform via REST.
* Events are persisted in SQLite and queued in-memory for fast dispatch.

**Consuming Events:**

```java
@Singleton
public class InventoryEventHandler {

    @EventListener(topic = "inventory")
    public void onInventoryUpdate(Event event) {
        // Business logic here
    }
}
```

**Transport Layer:**

* REST (current).
* *Future*: gRPC and WebSocket support.

**Persistence:**

* SQLite-backed event log.

**Processing:**

* Dynamic listener dispatch.

**Pluggable Pipes:**

* Future: Route events to external services via plugins (REST/gRPC/message buses).

---

## Example Usage

### Create Publisher (POS App)

```java
import com.example.common.annotation.MessagePublisher;

public interface SaleEventPublisher {

    @MessagePublisher(topic = "sales")
    void publishSale(String json);

}

// Usage:
saleEventPublisher.publishSale("{\"id\":123,\"amount\":42.00}");
```

---

### Create Event Listener (Platform Side)

```java
import com.example.common.annotation.EventListener;
import com.example.common.model.Event;

@Singleton
public class InventoryEventHandler {

    @EventListener(topic = "inventory")
    public void onInventoryUpdate(Event event) {
        // Process inventory update
    }
}
```

---

### REST API Example

**Publish an Event:**

```http
POST /events
Content-Type: application/json

{
  "topic": "sales",
  "type": "SALE_CREATED",
  "payload": "{\"amount\": 100.50}"
}
```

---

## Roadmap / TODO

* [ ] Outbound Pipe Integration (HTTP/gRPC/message bus).
* [ ] Priority Queues with in-memory & persistent fallback.
* [ ] Distributed Cluster Tree Manager.
* [ ] Admin/Monitoring Endpoints (Metrics, Queue Stats).
* [ ] End-to-End POS → Platform → Subscriber Demo.
* [ ] Configurable Retention & Deduplication.
* [ ] CI/CD Pipelines and Full Developer Documentation.

---

## Contributing

PRs, feedback, and feature suggestions are welcome!
Please open an [issue](https://github.com/anuragm339/edge-messaging/issues) or submit a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Credits

Built with ❤️ using **Micronaut**, **Java 21+**, and the power of open-source collaboration.

---

For support, advanced use cases, or collaboration, please [contact the maintainer](mailto:your.email@example.com).
