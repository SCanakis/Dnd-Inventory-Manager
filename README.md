<!-- ────────────────────────────────────────────────────────────────────── -->
<!-- DndApp – Root README                                                 -->
<!-- ────────────────────────────────────────────────────────────────────── -->

<h1 align="center">
  🐲 D&D Inventory Manager
</h1>

<p align="center">
  <em>A full‑stack D&D 5e companion: create characters, track loot, and manage coin
  purses – Spring Boot + PostgreSQL in the back, Angular in the front.</em>
</p>

<p align="center">
  <!-- CI badge – update workflow file name if needed -->
  <a href="https://github.com/SCanakis/DndApp/actions">
    <img src="https://img.shields.io/github/actions/workflow/status/SCanakis/DndApp/ci.yml?label=build">
  </a>
  <img src="https://img.shields.io/badge/Java-17+-brightgreen">
  <img src="https://img.shields.io/badge/Angular-17-red">
  <img src="https://img.shields.io/badge/PostgreSQL-15-blue">
  <img src="https://img.shields.io/github/license/SCanakis/DndApp">
  <!-- swap once coverage is wired -->
  <img src="https://img.shields.io/badge/coverage‑pending-lightgrey">
</p>

---

## ✨ Key Features

| Module        | What works today                                             |
|---------------|--------------------------------------------------------------|
| **Characters**| Full CRUD: race, class, subclass, background                 | 
| **Inventory** | Automatic weight + attunement calculations                   | 
| **Coin Purse**| Manage GP / PP / EP / SP / CP (currencies)                    |
| **Auth**      | Spring Security form login + HTTP Basic (session cookies)    | 

> GitHub's language mix: **≈59% Java, 16% SCSS, 16% TypeScript, 9% HTML**.

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

---

## 🛠️ Development

### Manual Docker Compose
```bash
# Empty database
docker-compose up

# With specific data
CHARACTER_DATA_FILE=./db/character-data.sql docker-compose up
ITEM_DATA_FILE=./db/item-data.sql docker-compose up

# With all data
CHARACTER_DATA_FILE=./db/character-data.sql ITEM_DATA_FILE=./db/item-data.sql docker-compose up
```

### Stop & Clean
```bash
# Stop containers
docker-compose down

# Remove all data (fresh start)
docker-compose down --volumes
```

---

## 📁 Project Structure

```
DndApp/
├── frontend/dnd-app/          # Angular frontend
├── backend/dndapp/            # Spring Boot backend  
├── db/                        # Database initialization
│   ├── init-extensions.sql    # PostgreSQL extensions
│   ├── init-data.sql         # Core schema
│   ├── character-data.sql    # Sample D&D classes, races, etc.
│   └── item-data.sql        # Sample weapons, armor, equipment
├── docker-compose.yml        # Container orchestration
└── start.sh                 # Easy startup script
```

---

## 🎯 API Endpoints (Key Examples)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/characters` | List all characters |
| POST   | `/api/characters` | Create new character |
| GET    | `/api/items` | List all items |
| GET    | `/api/classes` | List available D&D classes |
| GET    | `/api/races` | List available D&D races |

**[View complete API documentation ->] (backend/README.md)**
---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feat/amazing-feature`)
5. Open a Pull Request

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🎲 About

Built for D&D enthusiasts who want a digital companion for character management and inventory tracking. Perfect for both players and DMs!