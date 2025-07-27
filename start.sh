#!/bin/bash

echo "🎲 D&D App Starter"
echo ""

# Default to empty files
CHARACTER_DATA_FILE="./db/empty-character-data.sql"
ITEM_DATA_FILE="./db/empty-item-data.sql"

# Parse arguments
LOAD_CHARACTERS=false
LOAD_ITEMS=false
SHOW_HELP=false

for arg in "$@"; do
    case $arg in
        --with-characters|-c)
            LOAD_CHARACTERS=true
            ;;
        --with-items|-i)
            LOAD_ITEMS=true
            ;;
        --with-all|-a)
            LOAD_CHARACTERS=true
            LOAD_ITEMS=true
            ;;
        --help|-h)
            SHOW_HELP=true
            ;;
        *)
            echo "❌ Unknown option: $arg"
            SHOW_HELP=true
            ;;
    esac
done

if [ "$SHOW_HELP" = true ]; then
    echo "Usage: ./start.sh [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  (none)              Start with empty database"
    echo "  --with-characters   Load sample characters, classes, races, backgrounds"
    echo "  --with-items        Load sample items, weapons, armor"
    echo "  --with-all          Load both characters and items"
    echo "  --help              Show this help"
    echo ""
    echo "You can combine options:"
    echo "  ./start.sh --with-characters --with-items"
    echo ""
    echo "Process:"
    echo "  1. Database starts"
    echo "  2. Backend starts and Hibernate creates tables"
    echo "  3. Frontend starts"
    echo "  4. Script waits for services to be ready"
    echo "  5. Script loads data (if specified)"
    exit 0
fi

# Set data files based on flags
if [ "$LOAD_CHARACTERS" = true ]; then
    CHARACTER_DATA_FILE="./db/character-data.sql"
    echo "✅ Loading character data (classes, races, backgrounds)"
fi

if [ "$LOAD_ITEMS" = true ]; then
    ITEM_DATA_FILE="./db/item-data.sql"
    echo "✅ Loading item data (weapons, armor, equipment)"
fi

if [ "$LOAD_CHARACTERS" = false ] && [ "$LOAD_ITEMS" = false ]; then
    echo "🚀 Starting with empty database..."
    echo "💡 Use './start.sh --help' to see data loading options"
else
    echo "🚀 Starting containers first, then loading data..."
fi

echo ""

# Start the containers first (without data loading)
echo "🐳 Starting Docker containers..."
docker compose up -d

echo "⏳ Waiting for services to be ready..."

# Wait for database to be ready
echo "   Waiting for database..."
DB_WAIT_COUNT=0
while ! docker compose exec db pg_isready -U dnd -d dnddb >/dev/null 2>&1; do
    echo "   Database not ready yet, waiting..."
    sleep 3
    DB_WAIT_COUNT=$((DB_WAIT_COUNT + 1))
    if [ $DB_WAIT_COUNT -gt 20 ]; then
        echo "❌ Database failed to start after 60 seconds"
        echo "🔍 Checking database logs:"
        docker compose logs db
        exit 1
    fi
done

# Wait for backend to be ready
echo "   Waiting for backend..."
echo "🔍 Checking initial backend status:"
docker compose logs --tail=5 backend

BACKEND_WAIT_COUNT=0
while true; do
    # Check if backend container is running and port is responding (don't care about HTTP status)
    if docker compose exec backend nc -z localhost 8080 >/dev/null 2>&1; then
        echo "   Backend port 8080 is responding!"
        break
    fi
    
    # Also check if we can see the "Started" message in logs
    if docker compose logs backend | grep -q "Started.*DndappApplication"; then
        echo "   Backend application has started successfully!"
        break
    fi
    
    echo "   Backend not ready yet, waiting... (attempt $((BACKEND_WAIT_COUNT + 1)))"
    sleep 5
    BACKEND_WAIT_COUNT=$((BACKEND_WAIT_COUNT + 1))
    
    # Show backend logs every 3 attempts (15 seconds)
    if [ $((BACKEND_WAIT_COUNT % 3)) -eq 0 ]; then
        echo "🔍 Backend logs (last 10 lines):"
        docker compose logs --tail=10 backend
        echo ""
    fi
    
    # Timeout after 2 minutes
    if [ $BACKEND_WAIT_COUNT -gt 24 ]; then
        echo "❌ Backend failed to start after 2 minutes"
        echo "🔍 Full backend logs:"
        docker compose logs backend
        exit 1
    fi
done

echo "✅ All services are ready!"

# Now load data if requested
if [ "$LOAD_CHARACTERS" = true ] || [ "$LOAD_ITEMS" = true ]; then
    echo ""
    echo "📊 Loading data into database..."
    
    if [ "$LOAD_CHARACTERS" = true ]; then
        echo "📚 Loading character data..."
        if [ -f "$CHARACTER_DATA_FILE" ] && [ -s "$CHARACTER_DATA_FILE" ]; then
            docker compose exec -T db psql -U dnd -d dnddb < "$CHARACTER_DATA_FILE"
            if [ $? -eq 0 ]; then
                echo "✅ Character data loaded successfully"
            else
                echo "❌ Failed to load character data"
            fi
        else
            echo "⚠️  Character data file not found or empty: $CHARACTER_DATA_FILE"
        fi
    fi
    
    if [ "$LOAD_ITEMS" = true ]; then
        echo "⚔️  Loading item data..."
        if [ -f "$ITEM_DATA_FILE" ] && [ -s "$ITEM_DATA_FILE" ]; then
            docker compose exec -T db psql -U dnd -d dnddb < "$ITEM_DATA_FILE"
            if [ $? -eq 0 ]; then
                echo "✅ Item data loaded successfully"
            else
                echo "❌ Failed to load item data"
            fi
        else
            echo "⚠️  Item data file not found or empty: $ITEM_DATA_FILE"
        fi
    fi
    
    # Show data summary
    echo ""
    echo "📈 Data loading summary:"
    CLASS_COUNT=$(docker compose exec -T db psql -U dnd -d dnddb -t -c "SELECT COUNT(*) FROM class;" | tr -d ' ')
    RACE_COUNT=$(docker compose exec -T db psql -U dnd -d dnddb -t -c "SELECT COUNT(*) FROM race;" | tr -d ' ')
    BACKGROUND_COUNT=$(docker compose exec -T db psql -U dnd -d dnddb -t -c "SELECT COUNT(*) FROM background;" | tr -d ' ')
    ITEM_COUNT=$(docker compose exec -T db psql -U dnd -d dnddb -t -c "SELECT COUNT(*) FROM item_catalog;" | tr -d ' ')
    
    echo "   📚 Classes: $CLASS_COUNT"
    echo "   👤 Races: $RACE_COUNT"  
    echo "   🎭 Backgrounds: $BACKGROUND_COUNT"
    echo "   ⚔️  Items: $ITEM_COUNT"
fi

echo ""
echo "🎉 D&D App is ready!"
echo "🌐 Frontend: http://localhost"
echo "🔧 Backend API: http://localhost/api"
echo ""
echo "📋 To view logs: docker compose logs -f"
echo "🛑 To stop: docker compose down"