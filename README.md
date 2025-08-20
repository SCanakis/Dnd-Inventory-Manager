<h1 align="center">
  🐲 D&D Inventory Manager
</h1>

<p align="center">
  <em>A full‑stack D&D 5e companion: create characters, track loot, and manage coin
  purses – Spring Boot + PostgreSQL in the back, Angular in the front.</em>
</p>

<p align="center">
  <!-- Deployment status badge -->
  <a href="https://github.com/SCanakis/DndApp/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/SCanakis/DndApp/deploy.yml?label=build">
  </a>
  <img src="https://img.shields.io/badge/Java-17+-brightgreen">
  <img src="https://img.shields.io/badge/Angular-17-red">
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue">
  <img src="https://img.shields.io/github/license/SCanakis/DndApp">
</p>

---

## ✨ Key Features

| Module        | What works today                                             |
|---------------|--------------------------------------------------------------|
| **Characters**| Full CRUD operations: race, class, subclass, background     | 
| **Inventory** | Automated weight + attunement calculations                  | 
| **Coin Purse**| Multi-currency management (GP / PP / EP / SP / CP)          |
| **Real-time** | WebSocket support for live inventory and character updates |
| **Containers** | Advanced inventory organization with weight management |
| **Item Search** | Fuzzy search across character inventories and item catalog |
| **Auth**      | Spring Security with form login + HTTP Basic authentication | 

> **Tech Stack**: **≈59% Java, 16% SCSS, 16% TypeScript, 9% HTML** - Enterprise-grade full-stack architecture.

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Git

### Start Options

```bash
# Clone the repository
git clone https://github.com/SCanakis/DndApp.git
cd DndApp

# Make the start script executable (Linux/Mac only)
chmod +x start.sh

# Choose your startup option:

# 🎯 Start with ALL sample data (recommended for demo)
./start.sh --with-all

# 🏗️ Start with character data only (classes, races, backgrounds)
./start.sh --with-characters

# ⚔️ Start with item data only (weapons, armor, equipment)
./start.sh --with-items

# 🗃️ Start with empty database (clean slate)
./start.sh
```

### Windows Users
```bash
# Use bash directly instead of chmod
bash start.sh --with-all
```

### Access the App
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432 (user: `dnd`, password: `dndpass`)

**🌐 Live Demo**: [View on AWS EC2](http://54.80.122.64) *(Production deployment)*

---

## 🏗️ Architecture Overview

### System Design
This application follows a microservices-inspired architecture with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular SPA   │    │  Spring Boot    │    │   PostgreSQL    │
│   (Frontend)    │◄──►│   (Backend)     │◄──►│   (Database)    │
│                 │    │                 │    │                 │
│ • Components    │    │ • REST APIs     │    │ • Relational    │
│ • Services      │    │ • WebSockets    │    │ • JSON Support  │
│ • Guards        │    │ • Security      │    │ • Full-text     │
│ • Interceptors  │    │ • JPA/Hibernate │    │   Search        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Key Technical Features

**Backend (Spring Boot)**
* RESTful API Design: Clean endpoints following REST principles
* WebSocket Integration: Real-time updates for inventory and character changes
* Spring Security: Form-based and HTTP Basic authentication
* JPA/Hibernate: Object-relational mapping with PostgreSQL
* Service Layer Pattern: Clear separation between controllers and business logic
* DTO Pattern: Data transfer objects for API boundaries
* Comprehensive Testing: Unit and integration tests with TestContainers

**Database (PostgreSQL)**
* Advanced Features: JSON column support for complex D&D mechanics
* Full-text Search: PostgreSQL's pg_trgm extension for fuzzy item searching
* Referential Integrity: Foreign key constraints maintaining data consistency
* Custom Domains: Type-safe ability score constraints (0-30)
* Optimized Indexes: Performance-tuned queries for inventory operations

**Real-time Features**
* WebSocket Endpoints: Live character stats, inventory, and coin purse updates
* STOMP Protocol: Message routing for targeted updates
* Event Broadcasting: Multi-user session support for shared campaigns

---

## 🛠️ Development & Architecture

### Containerized Deployment
```bash
# Empty database
docker-compose up

# With specific data sets
CHARACTER_DATA_FILE=./db/character-data.sql docker-compose up
ITEM_DATA_FILE=./db/item-data.sql docker-compose up

# Full production-like environment
CHARACTER_DATA_FILE=./db/character-data.sql ITEM_DATA_FILE=./db/item-data.sql docker-compose up
```

### Clean Development Environment
```bash
# Stop containers
docker-compose down

# Remove all data for fresh start
docker-compose down --volumes
```

---

## 📁 Project Structure

```
DndApp/
├── frontend/dnd-app/          # Angular 17 frontend with TypeScript
├── backend/dndapp/            # Spring Boot backend with Java 17+
├── db/                        # PostgreSQL database layer
│   ├── init-extensions.sql    # Database extensions setup
│   ├── init-data.sql         # Core schema & migrations
│   ├── character-data.sql    # D&D classes, races, backgrounds
│   └── item-data.sql        # Weapons, armor, equipment catalog
├── docker-compose.yml        # Multi-container orchestration
└── start.sh                 # Automated deployment script
```

---

## 🎯 RESTful API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/characters` | Retrieve all characters |
| POST   | `/api/characters` | Create new character |
| GET    | `/api/items` | Browse item catalog |
| GET    | `/api/classes` | List D&D character classes |
| GET    | `/api/races` | List available races |

**[Complete API Documentation →](backend/README.md)** - Comprehensive endpoint reference with examples

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feat/amazing-feature`)
5. Open a Pull Request

**Development Standards**: Clean code principles, comprehensive testing, and documented APIs

---

## 📖 D&D Content Attribution

This application uses game content from the D&D 5th Edition System Reference Document (SRD), which is available under the Open Game License (OGL). The SRD content includes character races, classes, spells, monsters, and equipment that form the mechanical foundation of this application.

**System Reference Document (SRD)**
- Character classes, races, and backgrounds
- Equipment, weapons, and armor statistics
- Game mechanics and rules references

This product is compliant with the Open Game License (OGL) and contains Open Game Content, as defined in the Open Game License version 1.0a Section 1(d). No material which is Product Identity under the OGL is reproduced herein.

**Dungeons & Dragons** and **D&D** are trademarks of Wizards of the Coast LLC, which does not license or endorse this product.

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🎲 About

Enterprise-grade web application built for D&D enthusiasts who want a digital companion for character management and inventory tracking. Demonstrates modern full-stack development practices with Spring Boot microservices, Angular frontend, containerized deployment, and comprehensive testing strategies.

**Perfect for**: Players, Dungeon Masters, and developers interested in game mechanics implementation and modern web application architecture.

**Live Application**: Deployed on AWS EC2 with Docker containerization for scalable, production-ready hosting.

**Built With Love For**
* 🎭 Players who want to focus on roleplaying instead of bookkeeping
* 🧙‍♂️ Dungeon Masters who need efficient campaign management tools
* 💻 Developers interested in full-stack development and game mechanics
* 🏢 Teams looking for examples of enterprise-grade application architecture