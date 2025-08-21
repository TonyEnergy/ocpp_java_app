# ⚡ OCPP Java Platform

An **OCPP (Open Charge Point Protocol)** platform built with **Java + Spring Boot**, supporting real-time communication between EV chargers and a central system. This project is backend, you can combine with your frontend, backend manages **WebSocket** connections and processes OCPP messages.

------

## ✨ Features

- ✅ Supports OCPP 1.6 core message exchange
- ✅ WebSocket server for persistent charger ↔ backend communication
- ✅ Heartbeat & status monitoring with configurable intervals
- ✅ JSON-based logging stored in database

------

## 🏗 Architecture

```
┌───────────────────────────────────────┐
│       Backend (Java + Spring Boot)    │
│  - WebSocket server (OCPP)            │
│  - Message parsing & routing           │
│  - JSON payload mapping                │
│  - REST API (extensions)              │
└───────────────────────────────────────┘
                │ websocket
                ▼
┌───────────────────────────────────────┐
│            EV Chargers                 │
│  - BootNotification                   │
│  - Heartbeat                          │
│  - StatusNotification                 │
│  - MeterValues                        │
└───────────────────────────────────────┘
```

------

## 🚀 Getting Started

### 1. Clone the repository

```git
git clone https://github.com/TonyEnergy/ocpp_java_app.git
```

### 2. Run backend (Java 8 / Spring Boot)

Clone this project to IntelliJ idea, I suggest you set parameters directly in application.yml file, if you have OSS / Server Chan / OKTA demand, you can also config it, if no need, you only need to config database configuration and keep other configuration like below.

```yml
ALIYUN_OSS_ENDPOINT="029f024e012a48a8bd583508169e4a19"
ALIYUN_OSS_ACCESS_KEY_ID="5b5b944ac074d1eaa1f79e791611f821"
ALIYUN_OSS_ACCESS_KEY_SECRET="c4f0385617ab401cb55924a8aeeeae26"
ALIYUN_OSS_BUCKET_NAME="45f35883f99748ceb2d5d6c6feda7b22"

SERVER_CHAN_KEY="fc93c5611a2a4177ae42e5d9186120a5"

OKTA_CLIENT_ID="558df264ae5e42758fb45f88b8bc2b01"
OKTA_CLIENT_SECRET="2b8975de45d643fc9ed9d8334e32095c"
OKTA_REDIRECT_URI="660a92724c0e41dbbc4649065c751aba"
OKTA_DOMAIN="311012bc93764b8985b9aa0b047be46e"
OKTA_AUTH_SERVER="e1f9e72b37f74d4b88a2a118e769df6e"

RAILWAY_MYSQL_URL:"jdbc:mysql://yourmysqluri:port/database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
RAILWAY_MYSQL_USERNAME:"yourusername"
RAILWAY_MYSQL_PASSWORD:"yourpassword"
RAILWAY_MYSQL_DRIVER_CLASSNAME:"com.mysql.cj.jdbc.Driver"
```

Backend swagger runs at:

```
http://localhost/swagger-ui/index.html
```

Backend websocket server runs at:

```
ws://LOCAL_AREA_NETWORK_IP:80/ocpp/ws
```

You can config this websocket url to your charger, then it can connect to your backend. Don't forget turn off the firewall, it may refuse connect.

If you don't know your local area network ip, you can enter 'ipconfig' in cmd, then find correct ip.

------

## ⚙️ Configuration

### WebSocket Server

Config your charger via swagger

------

## 📂 Project Structure

```
ocpp-java-app
├── src/main/java/github/tonyenergy
│   ├── config                # Config
│   ├── controller            # REST API
│   ├── entity                # OCPP message entities
│   ├── interceptors          # Use for ip track
│   ├── mapper                # Mybatis Plus mapper
│   ├── schedule              # Schedule task
│   ├── service               # Business logic
│   ├── utils                 # Ip track utils
│   └── websocket             # WebSocket handlers
├── src/main/resources
│   ├── application.yml       # Config
│   ├── application-dev.yml   # Dev config
│   ├── application-prod.yml  # Prod config
│   └── logback-spring.xml    # Use for log
├── Dockerfile                # Use docker file push to render server
└── pom.xml                   # Maven package
```

------

## 📜 License

This project is licensed under the MIT License.