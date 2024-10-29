# Spring Integration Demo Project

This is a demo application showcasing basic functionalities of Spring Integration, specifically focusing on JMS integration with ActiveMQ and file handling capabilities.

## Prerequisites

- Java
- Maven
- Docker and Docker Compose
- ActiveMQ broker

## Project Structure

```
project-root/
├── src/
│   └── ...
└── monitoring/
    └── ...
    ├── docker-compose.yml
    └── grafana-dashboard.json
```

## Setup Instructions

### 1. ActiveMQ Setup
Ensure you have ActiveMQ broker running locally on your machine. The application depends on this message broker for JMS integration.

### 2. Monitoring Stack Setup

1. Navigate to the monitoring directory:
```bash
cd monitoring 
```
2. Start the monitoring stack using Docker Compose:
```bash
docker-compose up
```

### 3. Grafana Dashboard Setup

1. Access Grafana at http://localhost:3000 (default credentials are usually admin/admin)
2. Go to Dashboards → Import
3. Import the dashboard using the monitoring/grafana-dashboard.json file

### 4. Running the Application
From the project root directory:
```bash
./mvnw spring-boot:run
```

## Components

- **Spring Integration JMS**: Demonstrates message handling using ActiveMQ
- **Spring Integration File**: Shows file processing capabilities
- **Monitoring**: Includes Prometheus and Grafana setup for metrics visualization

## Monitoring

The application exports various metrics that can be visualized in Grafana, including:
- System and Process CPU usage
- Requests per second, messages per second
- Other application-specific metrics