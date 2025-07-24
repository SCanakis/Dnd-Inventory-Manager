<!-- ────────────────────────────────────────────────────────────────────── -->
<!-- DndApp – Root README                                                 -->
<!-- ────────────────────────────────────────────────────────────────────── -->

<h1 align="center">
  🐲 Dnd Inventory Manager
</h1>

<p align="center">
  <em>A full‑stack 5e companion: create characters, track loot, and manage coin
  purses – Spring Boot + PostgreSQL in the back, Angular in the front.</em>
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

## ✨ Key Features

| Module        | What works today                                             |
|---------------|--------------------------------------------------------------|
| **Characters**| Full CRUD: race, class, subclass, background                 | 
| **Inventory** | Automatic weight + attunement calculations                   | 
| **Coin Purse**| Manage GP / PP / EP / SP / CP (currencies)                    |
| **Auth**      | Spring Security form login + HTTP Basic (session cookies)    | 

> GitHub’s language mix: **≈59 % Java, 16 % SCSS, 16 % TypeScript, 9 % HTML**.

---

## 🏗️ Quick Start (Local Dev)

```bash
# 1 Clone
git clone https://github.com/SCanakis/DndApp.git
cd DndApp

# 2 Start PostgreSQL (tune ports/credentials in docker-compose.yml if needed)
docker compose up -d db

# 3 Backend – Spring Boot
cd backend
./mvnw spring-boot:run            # Swagger UI → http://localhost:8080/swagger-ui.html

# 4 Frontend – Angular
cd ../frontend/dnd-app
npm ci
ng serve --open                   # http://localhost:4200
