# 🌾 CropDeal

CropDeal is a microservice-based crop trading platform that bridges the gap between farmers and dealers, ensuring fair trade, transparency, and efficiency. The platform allows farmers to sell their crops directly to dealers, while admins manage and monitor all platform operations.

---

1️⃣ Scope of Application

The CropDeal application addresses the challenges faced by farmers in selling their crops at fair prices by providing a digital marketplace that directly connects Farmers and Dealers — eliminating middlemen, commissions, and delays.
The system ensures:

Transparent crop pricing

Easy crop posting and searching

Secure authentication

Seamless payment management

---

2️⃣ Case Study Overview

| Parameter             | Details                 |
| --------------------- | ----------------------- |
| **Project Name**      | CropDeal                |
| **Architecture**      | Microservices           |
| **Database**          | MySQL                   |
| **Service Discovery** | Eureka Server           |
| **API Gateway**       | Spring Cloud Gateway    |
| **Security**          | JWT (Role-Based Access) |
| **Roles Supported**   | Farmer, Dealer, Admin   |


---

3️⃣ Stakeholders

| Stakeholder | Responsibilities                                                    |
| ----------- | ------------------------------------------------------------------- |
| **Farmer**  | Posts crop details, manages profile, adds bank info, views receipts |
| **Dealer**  | Subscribes to crops, purchases crops, pays farmers, views invoices  |
| **Admin**   | Manages users, generates reports, activates/deactivates users       |


4️⃣ Microservices and Responsibilities

| Service Name        | Port | Responsibilities                                    |
| ------------------- | ---- | --------------------------------------------------- |
| **Auth Service**    | 8080 | Handle authentication and JWT token creation        |
| **API Gateway**     | 8000 | Central entry point for all requests                |
| **Eureka Server**   | 8761 | Service registry for dynamic discovery              |
| **Farmer Service**  | 8081 | Farmer profile, crops, bank details, receipts       |
| **Dealer Service**  | 8083 | Dealer subscriptions, purchases, invoices           |
| **Admin Service**   | 8084 | Manage users, generate reports, activate/deactivate |
| **Payment Service** | 8091 | Handle payments between farmers and dealers         |


5️⃣ Architecture Diagram
       
             ┌───────────────────────────┐
             │     Angular Frontend      │
             └──────────────┬────────────┘
                            │
                            ▼
             ┌───────────────────────────┐
             │     API Gateway (8000)     │
             └──────────────┬────────────┘
                            │
                            ▼
             ┌───────────────────────────┐
             │     Eureka Server (8761)   │
             │ (Service Discovery Server) │
             └──────────────┬────────────┘
                            │
                            ▼
    ┌──────────────────────────────────────────────────────────┐
    │                  Microservices Layer                     │
    │                                                          │
    │  ┌──────────────────────────┐    ┌────────────────────┐  │
    │  │ Auth Service (8080)      │    │ Payment Service(8091)│ │
    │  │ JWT Authentication       │    │ Handles Payments     │ │
    │  └──────────────────────────┘    └────────────────────┘  │
    │                                                          │
    │  ┌──────────────────────────┐    ┌────────────────────┐  │
    │  │ Farmer Service (8081)    │    │ Dealer Service(8083)│ │
    │  │ Manage Crops & Profiles  │    │ Manage Purchases    │ │
    │  └──────────────────────────┘    └────────────────────┘  │
    │                                                          │
    │  ┌──────────────────────────┐                            │
    │  │ Admin Service (8084)     │                            │
    │  │ Manage Users & Reports   │                            │
    │  └──────────────────────────┘                            │
    └──────────────────────────────────────────────────────────┘
                            │
                            ▼
             ┌───────────────────────────┐
             │       MySQL Database       │
             └───────────────────────────┘

6️⃣ System Workflow

🔐 Authentication Flow

User selects role (Farmer / Dealer / Admin)

Registers or logs in using email

JWT token generated and returned to frontend

👨‍🌾 Farmer Flow

Edit profile and bank details

Post crops (type, quantity, price)

View posted crops

Receive notifications and payments

🧑‍💼 Dealer Flow

Subscribe to crop types

Receive notifications via RabbitMQ

Purchase crops and complete payment

View invoices

🛠️ Admin Flow

Manage farmers and dealers

Generate reports

Activate or deactivate users

7️⃣ Access Control Matrix

| Feature / Endpoint          | Farmer | Dealer |    Admin   |
| --------------------------- | :----: | :----: | :--------: |
| Register / Login            |    ✅   |    ✅   |      ✅     |
| View Profile                | ✅ Self | ✅ Self |   ✅ Self   |
| Update Details              | ✅ Self | ✅ Self | ✅ Any User |
| Delete User                 |    ❌   |    ❌   |      ✅     |
| Post Crop                   |    ✅   |    ❌   |      ✅     |
| View Crops                  |    ✅   |    ✅   |      ✅     |
| Subscribe to Notifications  |    ❌   |    ✅   |      ✅     |
| Initiate Purchase           |    ❌   |    ✅   |      ✅     |
| Generate Invoice            |    ❌   |    ✅   |      ✅     |
| Add Bank Details            |    ✅   |    ✅   |      ✅     |
| View All Users              |    ❌   |    ❌   |      ✅     |
| Activate / Deactivate Users |    ❌   |    ❌   |      ✅     |


8️⃣ Inter-Service Communication

| Communication                    | Purpose                |
| -------------------------------- | ---------------------- |
| Admin ↔ Auth                     | Authentication         |
| Admin ↔ Dealer                   | Get/update dealer data |
| Admin ↔ Farmer                   | Get/update farmer data |
| Dealer ↔ Auth                    | Authentication         |
| Dealer ↔ Farmer (CropController) | Crop subscriptions     |
| Dealer ↔ Payment                 | Crop purchases         |
| Farmer ↔ Auth                    | Authentication         |
| Payment ↔ Farmer                 | Generate receipt       |
| Payment ↔ Dealer                 | Generate invoice       |


9️⃣ Technology Stack

Spring Boot (3.x) — Microservice development

Spring Cloud Gateway — API Gateway

Spring Cloud Eureka — Service Discovery

Spring Security + JWT — Secure access

Spring Data JPA + MySQL — Database layer

Feign Client — Inter-service communication

RabbitMQ — Message broker

Postman — API testing

Angular — Frontend

🌐 Deployment

| Component | Platform     | Status        |
| --------- | ------------ | ------------- |
| Frontend  | GitHub Pages | ✅ Live        |
| Backend   | Render       | ⏳ In Progress |

👩‍💻 Author

Sonam Ravindra Kadam
