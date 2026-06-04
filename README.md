# Ierussi Flowers 

### Floriculture Management and Flower Retail Platform
<img width="1854" height="943" alt="Screenshot 2026-06-01 210507" src="https://github.com/user-attachments/assets/906c802e-4dbd-41f9-bf85-55a828a02181" />

Ierussi Flowers is a desktop application that connects customers, operators and administrators through a unified platform dedicated to flower sales and order management.

The system allows customers to browse the catalog, create personalized bouquets and place orders, while providing operators and administrators with the tools needed to manage products, inventory and order processing.

Developed for the course of **Software Engineering and Web Design (Academic Year 2025–2026)**.

---

## Overview

The system supports three user roles: Customer, Operator and Administrator, each with dedicated functionalities.

### Product Catalog

Customers can browse and filter the flower catalog according to color, variety, price and availability. Detailed information about each flower product is provided.

### Custom Bouquet Creation

Customers can create personalized bouquets by selecting flowers, bouquet size, packaging options, greeting cards and decorative accessories.

### Order Management

Customers can place orders through a complete checkout process supporting delivery and in-store pickup options.

### Inventory Management

Administrators can add, modify and remove flower products while monitoring stock availability.

### Operator Support

Operators can manage customer orders and support bouquet preparation activities.

### Address Validation

Delivery addresses are validated through an external geocoding service before confirming an order.

### Session Tracking

User sessions are managed through a centralized session mechanism shared across graphical and console interfaces.

---

## Architecture

Java desktop application developed according to the Model-View-Controller (MVC) architectural pattern.

| Pattern | Where Applied |
|----------|----------|
| Singleton | Session |
| Factory | DAOFactory |
| Builder | CustomBouquetBuilder, Order.Builder |
| DAO | Persistence Layer |
| MVC | Overall Application Architecture |

---

## Requirements

### Software

- JDK 17+
- Maven 3.8+
- JavaFX SDK 21
- MySQL 8.0+
- Docker Desktop
- Git

Cross-platform support:

- Windows 10/11
- Linux
- macOS

### Hardware

- Multi-core CPU (Intel i5 / Ryzen 5 or equivalent)
- 8 GB RAM minimum
- 500 MB free disk space
- 1366×768 minimum screen resolution

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/zachele/ierussiflowers_ispw.git
cd ierussiflowers_ispw
```

### 2. Start the database

```bash
docker-compose up -d
```

This creates the database:

```text
flowershop_db
```

Available database accounts:

```text
root / root
flowershop_user / flowershop_pass
```

### 3. Configure environment variables

Windows PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/flowershop_db"
$env:DB_USER="root"
$env:DB_PASSWORD="root"
```

Linux / macOS:

```bash
export DB_URL=jdbc:mysql://localhost:3306/flowershop_db
export DB_USER=root
export DB_PASSWORD=root
```

### 4. Select persistence mode

Inside `ShopFlowersApplication.java`:

```java
AppConfig.setMode(AppMode.DEMO);
```

Available modes:

| Mode | Description |
|----------|----------|
| DEMO | In-memory persistence |
| FILE | CSV persistence |
| FULL | MySQL persistence |

Examples:

```java
AppConfig.setMode(AppMode.DEMO);
```

```java
AppConfig.setMode(AppMode.FILE);
```

```java
AppConfig.setMode(AppMode.FULL);
```

### 5. Build and Run

```bash
mvn clean install
mvn javafx:run
```

Or using Maven Wrapper:

```powershell
.\mvnw.cmd javafx:run
```

---

## Demo Accounts

| Username | Role | Password |
|----------|----------|----------|
| admin | Administrator | admin123 |
| operatore | Operator | operatore123 |
| mario_rossi | Customer | cliente123 |

---

## Testing

Tests are organized into controller-level and domain-level suites.

Testing activities include:

- Order validation
- Checkout workflow verification
- DAO operations
- Recommendation engine validation
- Custom bouquet generation
- Inventory management
- Session management

Run tests using:

```bash
mvn test
```

---

## Code Quality

Code quality is monitored through SonarQube analysis and software engineering best practices.

The project emphasizes:

- Layer separation
- Object-Oriented Design
- DAO abstraction
- Reduced code duplication
- Maintainability
- Modularity
- Scalability

---

## Documentation

The complete project documentation includes:

- Software Requirements Specification (SRS)
- Use Case Diagrams
- Class Diagrams
- Sequence Diagrams
- Activity Diagrams
- Architectural Design
- Design Pattern Documentation

---

## Database Structure

Main tables:

- users
- flower_product
- orders
- order_item
- operator_details
- custom_bouquet_order

The schema supports:

- User management
- Product catalog management
- Order management
- Inventory management
- Operator management
- Custom bouquet management

---

## Technologies

### Core Technologies

- Java 17
- JavaFX 21.0.6
- Maven
- MySQL 8.0
- Docker

### Testing & Quality

- Code Quality is monitored through **[SonarQube](https://sonarcloud.io/summary/new_code?id=zachele_ierussiflorand)**.

### External Services

- OpenStreetMap Nominatim Geocoding API

---

## Author

**[Rocco Zachele Ierussi](https://github.com/zachele)**

Software Engineering and Web Design (ISPW)

Academic Year 2025–2026
