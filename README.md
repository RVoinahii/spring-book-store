![Logo](🍃Spring_Book_Store_📚.png)
## 📝Introduction

The Online Book Store is a web application that allows users to browse, search,
and purchase books online. The project aims to simulate the functionalities of an e-commerce book store,
enabling both **shoppers (`USER`)** and **managers (`ADMIN`)** to interact with the platform efficiently.

This project was developed as part of a learning module to gain hands-on experience with **Spring Boot**,
**REST APIs**, **authentication**, and **database management**.

## ✨Features
### 🛒Shopper (USER)
- **Authentication & Authorization** - Register and log in.

- **Book Browsing** - View all books, search by parameters, and explore categories.

- **Shopping Cart** - Add, remove, and view books in the cart.

- **Order Management** - Place orders, view past receipts, and examine order details.

### 🛠️ Manager (ADMIN)
- **Book Management** - Add, update, and remove books from the store.
- **Category Management** - Create, modify, and delete book categories.
- **Order Management** - Update order statuses (*e.g., "SHIPPED", "DELIVERED"*).

---

# 🌟Technologies Used
### 🖥️Programing Language

- **Java 21** – Primary programming language.

### ⚙️Frameworks & Libraries

- **Spring Boot** – Framework for backend development.
- **Spring Security** – Authentication and authorization.
- **Spring Data JPA** - Database interaction using **Hibernate**.
- **Lombok** - Reduces boilerplate code (*e.g., getters, setters*).
- **MapStruck** - Mapping between DTOs and entities.

### 🗄️Database & Migrations

- **MySQL** – Relational database.
- **Liquibase** — Database schema migration tool.

### 🧪Testing
- **Testcontainers** - Containerized testing environments.
- **JUnit 5** – Unit testing framework.
- **Mockito** - Mocking frameworks for unit tests.

### 🛠️Build & Dependency Management
- **Maven** – Dependency management and build tool.

### 🐳Containerization
- **Docker** - Containerization tool.
- **Docker Compose** — Orchestrates multi-container applications.

### 📜API Documentation
- **Swagger** - API documentation.

---

# 📍Endpoints
> **💡 Note**: To test the API endpoints, you can import the provided [**Postman collection**](Spring%20Book%20Store%20API%20Collection.postman_collection.json) into your Postman.
### 🛡️Authentication Сontroller
| Method | Endpoint          | Description                   | Required Role |
|--------|-------------------|-------------------------------|---------------|
| POST   | /auth/registration | Create a new user             | (*No role*)   |
| POST   | /auth/login        | Authenticate an existing user | (*No role*)   |

### 📚Book Сontroller
| Method | Endpoint             | Description                | Required Role   |
|--------|----------------------|----------------------------|-----------------|
| GET    | /books                | Get list of all books      | USER           |
| GET    | /books/{id}           | Get a book by ID           | USER           |
| GET    | /books/search         | Search books by parameters | USER           |
| POST   | /books                | Create a new book          | ADMIN          |
| PUT    | /books/{id}           | Update a book by ID        | ADMIN          |
| DELETE | /books/{id}           | Delete a book              | ️ ADMIN         |


### 🗂️Category Сontroller
| Method | Endpoint               | Description                | Required Role |
|--------|------------------------|----------------------------|---------------|
| GET    | /categories             | Get list of all categories | USER         |
| GET    | /categories/{id}        | Get category by ID         | USER         |
| GET    | /categories/{id}/books  | Get books by category ID   | USER         |
| POST   | /categories             | Create a new category      | ADMIN        |


### 📦Order Сontroller
| Method | Endpoint                | Description                   | Required Role |
|--------|-------------------------|-------------------------------|---------------|
| GET    | /orders                  | Get user's orders             | USER         |
| GET    | /orders/{orderId}/items  | Get items in an order         | USER         |
| GET    | /orders/{orderId}/items/{itemId} | Get item details in an order  | USER |
| POST   | /orders                  | Create a new order            | USER         |
| PATCH  | /orders/{orderId}        | Change order status           | ADMIN        |

### 🛒Shopping Cart Сontroller
| Method | Endpoint                   | Description                | Required Role |
|--------|----------------------------|----------------------------|---------------|
| GET    | /cart                       | Get items in the cart      | USER         |
| POST   | /cart                       | Add item to the cart       | USER         |
| PUT    | /cart/item/{id}             | Update item in the cart    | USER         |
| DELETE | /cart/item/{id}             | Delete item from the cart  | USER         |

---

# 🚀Installation & Setup

## 🐳Using Docker:

### 📌Prerequisites
Ensure the following are installed:
- **[Docker & Docker Compose](https://www.docker.com/)**
- **[Maven](https://maven.apache.org/download.cgi)**
> **💡 Note**: Java 21 and MySQL do not need to be installed manually - they will be used inside Docker containers.

### 📥Cloning the Project
```
git clone https://github.com/RVoinahii/spring-book-store.git
cd spring-book-store
```

### 🐳Running with Docker
1. ### **📝Create an `.env` file and update the necessary values:**
``` 
cp .env.sample .env 
```
Example:
```
MYSQLDB_USER=root
MYSQLDB_USER_PASSWORD=password
MYSQLDB_USER_DATABASE=spring_book_store
MYSQLDB_USER_LOCAL_PORT=3308
MYSQLDB_USER_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8088
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005
```
2. ### **🔧Build the project using Maven:**
```
mvn clean install
```
3. ### **🚀Build the Docker images and start the containers:**
```
docker-compose up --build
```

---

## 💻 Running Without Docker
### 📌Prerequisites
Ensure the following are installed:
- **[Java 21](https://www.oracle.com/java/technologies/downloads/#java21)**
- **[Maven](https://maven.apache.org/download.cgi)**
- **[MySQL 8.0+](https://www.mysql.com/downloads/)**
> **💡 Note**: Ensure MySQL is running and the database is created before proceeding.

### 📥Cloning the Project
```
git clone https://github.com/RVoinahii/spring-book-store.git
cd spring-book-store
```

### ⚙️Configure `application.properties` file
```
 spring.datasource.url=jdbc:mysql://mysqldb:<MYSQL_PORT>/<MYSQL_DATABASE>"
 spring.datasource.username=root
 spring.datasource.password=password
 spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
 spring.jpa.hibernate.ddl-auto=validate
 
 jwt.expiration=300000
 jwt.secret="someRandomSecretKeyThatIsAtLeast32BytesLong"
```

### 🚀Running the Application
1. ### **🔧Build the project using Maven:**
```
mvn clean install
```
2. ### **🚀Start the application:**
```
mvn spring-boot:run
```
---

## 🌍Accessing the Application
- **Aplication Demo** is available [HERE](http://ec2-13-60-94-14.eu-north-1.compute.amazonaws.com/swagger-ui/index.html#/).  
This demo version allows anyone to explore how the application works.
> **💡 Note**: **Demo** only supports functionalities available for the `USER` role. Additionally, **15 books** and **3 categories** are preloaded in the demo for you to explore.
- **Swagger UI** for local usage is accessible at: `http://localhost:<YOUR_LOCAL_PORT>/swagger-ui/index.html#/`.  
Here, you can explore all the available API endpoints and test them using a user-friendly interface.
