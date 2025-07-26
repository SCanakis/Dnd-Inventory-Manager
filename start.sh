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
    echo "Or use Docker Compose directly:"
    echo "  docker-compose up"
    echo "  CHARACTER_DATA_FILE=./db/character-data.sql docker-compose up"
    echo "  ITEM_DATA_FILE=./db/item-data.sql docker-compose up"
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
    echo "🚀 Starting with selected sample data..."
fi

echo ""

# Export environment variables and start
export CHARACTER_DATA_FILE
export ITEM_DATA_FILE
docker compose up