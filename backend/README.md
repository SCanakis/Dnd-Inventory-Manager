# 🎲 DndApp Backend API

Complete API documentation for the D&D Inventory Manager backend.

## 🔐 Authentication
All destructive endpoints require authentication via session cookies or HTTP Basic.

## 🔗 Base URLs

**REST API:** `http://localhost:8080/api`  
**WebSockets:** `ws://localhost:8080/ws/character/websocket`

---

## 📋 Quick Reference

### REST Endpoints
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/create-user` | POST | Create user |
| `/characters` | GET | List all characters |
| `/character/{uuid}` | GET | Get character information |
| `/character/{uuid}` | PUT | Update character information |
| `/character/{uuid}` | DELETE | Delete character information |
| `/character/{uuid}` | POST | Create character |
| `/background` | GET | Get all backgrounds |
| `/classes` | GET | Get all classes |
| `/race` | GET | Get all races |
| `/subclasses` | GET | Get all subclasses |
| `/itemCatalog` | GET | Get all items |
| `/itemCatalog/id={uuid}` | GET | Get specific item |
| `/itemCatalog/searchTerm={searchTerm}` | GET | Get items using search term |
| `/itemCatalog/id={itemUuid}/charId={charUuid}` | POST | Add item to character |
| `/itemCatalog` | POST | Create item |
| `/containers/{charUuid}` | GET | Get container |
| `/containers/{charUuid}` | POST | Create container |
| `/containers/{charUuid}/containerId={containerId}` | DELETE | Delete container |
| `/containers/{charUuid}/containerId={containerId}` | PUT | Update container max capacity |
| `/containers/inventory/{uuid}` | GET | Get character inventory |
| `/containers/inventory/{uuid}/searchTerm={searchTerm}` | GET | Get character inventory using fuzzy search |
| `/containers/inventory/{uuid}/id={itemUuid}` | GET | Get item from inventory |
| `/containers/inventory/{uuid}/id={itemUuid}/containerId={containerUuid}` | DELETE | Delete item from inventory |
| `/containers/inventory/{uuid}/id={itemUuid}/containerId={containerUuid}` | PUT | Update character item |

### WebSocket Topics
| Topic | Type | Description |
|-------|------|-------------|
| `/character-stats/subscribe` | Subscribe | Subscribe to character stats updates |
| `/character-stats/update` | Publish | Send character stats updates |
| `/coin-purse/subscribe` | Subscribe | Subscribe to coin purse updates |
| `/coin-purse/update` | Publish | Send coin purse updates |
| `/container/subscribe` | Subscribe | Subscribe to container updates |
| `/container/delete` | Publish | Send container deletion |
| `/inventory/subscribe` | Subscribe | Subscribe to inventory updates |
| `/inventory/update` | Publish | Send inventory updates |
| `/inventory/add` | Publish | Send item addition to inventory |
| `/inventory/delete` | Publish | Send item deletion from inventory |
| `/itemCatalog/subscribe` | Subscribe | Subscribe to item catalog updates |

---

## 🔌 WebSocket Connection

### JavaScript Example
```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws/character/websocket');
const stompClient = Stomp.over(socket);

// Subscribe to character stats
stompClient.subscribe('/character-stats/subscribe', function(message) {
    const data = JSON.parse(message.body);
    console.log('Character stats updated:', data);
});

// Send character stats update
stompClient.send('/character-stats/update', {}, JSON.stringify({
    characterId: 'uuid-here',
    hitPoints: 45,
    level: 5
}));