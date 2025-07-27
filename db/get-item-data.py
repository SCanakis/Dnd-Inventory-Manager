import requests
import json
import time
import os
from openai import OpenAI


client = OpenAI(api_key=os.getenv('OPENAI_API_KEY'))

url_equipment = "https://www.dnd5eapi.co/api/2014/equipment"
url_magic_item = "https://www.dnd5eapi.co/api/2014/magic-items"


def get_item_list():
    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    response_equipment = requests.request("GET", url_equipment, headers=headers, data=payload)
    response_magic_items= requests.request("GET", url_magic_item, headers=headers, data=payload)

    equipment_json = json.loads(response_equipment.text);   
    magic_item_json = json.loads(response_magic_items.text);   

    return equipment_json, magic_item_json


def get_item(index):
    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    try:
        response = requests.request("GET", url_equipment + "/"+index , headers=headers, data=payload)        
        response.raise_for_status()  # Raises an exception for bad status codes
        
        # Check if response has content
        if not response.text.strip():
            print(f"   ⚠️  Empty response for equipment {index}")
            return None
            
        return json.loads(response.text)
        
    except requests.exceptions.RequestException as e:
        print(f"   ❌ Request failed for equipment {index}: {e}")
        return None
    except json.JSONDecodeError as e:
        print(f"   ❌ JSON decode error for equipment {index}: {e}")
        print(f"   Response: {response.text[:100]}...")
        return None


def get_magic_item(index):
    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    try:
        response = requests.request("GET", url_magic_item + "/"+index , headers=headers, data=payload)        
        response.raise_for_status()  # Raises an exception for bad status codes
        
        # Check if response has content
        if not response.text.strip():
            print(f"   ⚠️  Empty response for magic item {index}")
            return None
            
        return json.loads(response.text)
        
    except requests.exceptions.RequestException as e:
        print(f"   ❌ Request failed for magic item {index}: {e}")
        return None
    except json.JSONDecodeError as e:
        print(f"   ❌ JSON decode error for magic item {index}: {e}")
        print(f"   Response: {response.text[:100]}...")
        return None


def ask_chatGPT(item_json):
    prompt = f"""You are converting D&D 5e items to a PostgreSQL database. Analyze the item data and return ONLY valid JSON.

ITEM DATA:
{json.dumps(item_json, indent=2)}

Return this EXACT JSON schema:

{{
  "item_name": "string max 50 chars",
  "item_description": "full description text",
  "item_weight": 0.0,
  "item_value": 0,
  "attackable": false,
  "ac_bonus": null,
  "add_as_to_ac": null,
  "equippable": false,
  "attunable": false,
  "item_equippable_type": null,
  "ability_requirment": null,
  "skill_altered_roll_type": null,
  "skill_altered_bonus": null,
  "item_rarity": "common",
  "is_container": false,
  "capacity": null
}}

CONVERSION RULES:

📝 BASIC INFO:
- item_name: Use "name" field, truncate to 50 chars if needed
- item_description: Join "desc" array with spaces, or empty string if missing
- item_weight: Convert "weight" field to float, default 0.0
- item_value: Convert "cost" to gold pieces using these rates:
  * cp (copper) = quantity x 0.01
  * sp (silver) = quantity x 0.1  
  * ep (electrum) = quantity x 0.5
  * gp (gold) = quantity x 1.0
  * pp (platinum) = quantity x 10.0
  * Default: 0 if no cost

⚔️ COMBAT PROPERTIES:
- attackable: true if equipment_category contains "weapon" OR desc mentions "attack", "damage", "hit"
- ac_bonus: Extract from "armor_class.base" if present, otherwise null

🎒 EQUIPMENT SLOTS:
- equippable: true if this can be worn/wielded (weapons, armor, accessories, tools)
- item_equippable_type: Choose from these EXACT values only:
  * WEAPONS:
    - ["mainhand"] - one-handed weapons (swords, maces, wands)
    - ["offhand"] - shields, light weapons that can be dual-wielded
    - ["twohand"] - two-handed weapons (greatswords, bows, staves)
  * ARMOR & ACCESSORIES:
    - ["armor"] - body armor (leather, chain mail, plate)
    - ["cloak"] - cloaks, capes
    - ["bracers"] - bracers, arm guards
    - ["head"] - helmets, hats, circlets
    - ["belt"] - belts, sashes
    - ["hands"] - gloves, gauntlets
    - ["ringl"] - left ring slot
    - ["ringr"] - right ring slot  
    - ["feet"] - boots, shoes
    - ["back"] - backpacks, cloaks worn on back
    - ["spellfocus"] - magical focuses (orbs, wands, instruments, holy symbols)
    - ["custom"] - unusual items that don't fit standard slots
  * Set to null if not equippable

✨ MAGIC PROPERTIES:
- attunable: true if item mentions "attunement", "requires attunement", or is clearly magical
- item_rarity: Extract from "rarity.name" or infer: "common", "uncommon", "rare", "very_rare", "legendary"
- ability_requirment: Extract strength/dex requirements as JSON like {{"strength": 13}}
- skill_altered_bonus: If item gives skill bonuses, format as {{"skill_name": bonus_number}}

📦 CONTAINERS:
- is_container: true if name contains "bag", "pack", "pouch", "chest", "box", "case", "quiver", "sheath"
- capacity: Estimate reasonable capacity in pounds:
  * Small pouch = 5-10 lbs
  * Belt pouch = 10-15 lbs  
  * Backpack = 30-40 lbs
  * Large bag = 50-60 lbs
  * Chest = 100+ lbs

🚨 CRITICAL: Return ONLY the JSON object. No explanations, no markdown, no extra text.

JSON:"""

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a precise JSON formatter for D&D items. Always return valid JSON only."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.1,  # Low temperature for consistent formatting
            max_tokens=1000
        )
            
        response_text = response.choices[0].message.content.strip()
        
        # Find JSON in the response
        start = response_text.find('{')
        end = response_text.rfind('}') + 1
        
        if start != -1 and end > start:
            json_text = response_text[start:end]
            return json.loads(json_text)
        
        print(f"   ❌ No valid JSON found in ChatGPT response")
        return None
        
    except Exception as e:
        print(f"   ❌ ChatGPT error: {e}")
        return None


def write_query(item_data):
    name = item_data.get('item_name', '').replace("'", "''")
    desc = item_data.get('item_description', '').replace("'", "''")
    
    # Handle all fields with .get() to avoid KeyError
    weight = item_data.get('item_weight', 0.0)
    value = item_data.get('item_value', 0)
    attackable = str(item_data.get('attackable', False)).lower()
    equippable = str(item_data.get('equippable', False)).lower()
    attunable = str(item_data.get('attunable', False)).lower()
    rarity = str(item_data.get('item_rarity', 'common')).lower()
    is_container = str(item_data.get('is_container', False)).lower()
    
    # Handle NULL values safely
    ac_bonus = item_data.get('ac_bonus') if item_data.get('ac_bonus') is not None else 'NULL'
    capacity = item_data.get('capacity') if item_data.get('capacity') is not None else 'NULL'
    
    # Handle JSON/Array fields safely
    add_as_to_ac = f"'{json.dumps(item_data['add_as_to_ac'])}'" if item_data.get('add_as_to_ac') is not None else 'NULL'
    
    # Handle array fields
    equippable_type = item_data.get('item_equippable_type')
    item_equippable_type = f"'{{{','.join(equippable_type)}}}'" if equippable_type is not None else 'NULL'
    
    # Handle JSON fields
    ability_req = item_data.get('ability_requirment')
    ability_requirment = f"'{json.dumps(ability_req)}'" if ability_req is not None else 'NULL'
    
    skill_roll_type = item_data.get('skill_altered_roll_type')
    skill_altered_roll_type = f"'{json.dumps(skill_roll_type)}'" if skill_roll_type is not None else 'NULL'
    
    skill_bonus = item_data.get('skill_altered_bonus')
    skill_altered_bonus = f"'{json.dumps(skill_bonus)}'" if skill_bonus is not None else 'NULL'
    
    return f"""INSERT INTO item_catalog (
    item_uuid, item_name, item_description, item_weight, item_value, attackable, 
    ac_bonus, add_as_to_ac, equippable, attunable, item_equippable_type,
    ability_requirment, skill_altered_roll_type, skill_altered_bonus,
    item_rarity, is_container, capacity
) VALUES (
    gen_random_uuid(), '{name}', '{desc}', {weight}, {value}, 
    {attackable}, {ac_bonus}, {add_as_to_ac}, {equippable}, 
    {attunable}, {item_equippable_type}, {ability_requirment},
    {skill_altered_roll_type}, {skill_altered_bonus}, '{rarity}',
    {is_container}, {capacity}
);"""


def save_progress(processed_names, current_count):
    """Save current progress to a file"""
    with open("./progress.json", "w") as f:
        json.dump({
            "processed_names": list(processed_names),
            "current_count": current_count
        }, f)


def load_progress():
    """Load previous progress if it exists"""
    try:
        with open("./progress.json", "r") as f:
            data = json.load(f)
            return set(data["processed_names"]), data["current_count"]
    except FileNotFoundError:
        return set(), 0


def main():
    equipment_json, magic_item_json = get_item_list()
    
    # Find the starting point - Ring of Regeneration
    start_item_name = "Ring of Regeneration"
    start_found = False
    equipment_start_index = len(equipment_json['results'])  # Skip all equipment
    magic_start_index = 0
    
    # Find Ring of Regeneration in magic items
    for i, item_ref in enumerate(magic_item_json['results']):
        if item_ref['name'] == start_item_name:
            magic_start_index = i
            start_found = True
            break
    
    if not start_found:
        print(f"❌ Could not find '{start_item_name}' in the item list!")
        return
    
    processed_names = set()
    remaining_items = len(magic_item_json['results']) - magic_start_index
    processed_count = 0
    success_count = 0
    failed_count = 0
    skipped_count = 0

    print(f"📦 Starting from '{start_item_name}'...")
    print(f"📦 Processing {remaining_items} remaining magic items...")

    # Always append since we're continuing from a specific point
    with open("./item-data.sql", "a") as file:
        file.write(f"\n-- Continuing from {start_item_name}\n\n")
        
        # Skip all equipment since we're starting from magic items
        
        # Process magic items starting from Ring of Regeneration
        for i, item_ref in enumerate(magic_item_json['results']):
            if i < magic_start_index:
                continue  # Skip items before our starting point
                    
            processed_count += 1
            
            try:
                item_json = get_magic_item(item_ref['index'])
                
                if item_json is None:
                    print(f"[{processed_count}/{remaining_items}] ✨ {item_ref['name']} - ❌ Failed to fetch")
                    failed_count += 1
                    time.sleep(0.2)  # Longer delay on error
                    continue

                if item_json['name'] in processed_names:
                    print(f"[{processed_count}/{remaining_items}] ✨ {item_json['name']} - ⏭️  Already processed")
                    skipped_count += 1
                    continue
                
                processed_names.add(item_json['name'])
                print(f"[{processed_count}/{remaining_items}] ✨ Processing: {item_json['name']}")

                item_data = ask_chatGPT(item_json)
                
                if item_data is None or not isinstance(item_data, dict) or 'item_name' not in item_data:
                    print("   ❌ Invalid ChatGPT response")
                    failed_count += 1
                else:
                    sql_insert = write_query(item_data)
                    file.write(sql_insert + '\n\n')
                    file.flush()  # Force write to disk
                    print("   ✅ Success")
                    success_count += 1
                    
            except Exception as e:
                print(f"[{processed_count}/{remaining_items}] ✨ {item_ref.get('name', 'Unknown')} - ❌ Error: {e}")
                failed_count += 1
            
            time.sleep(0.1)

    print(f"\n🎉 Completed from '{start_item_name}' to end!")
    print(f"   ✅ Successful: {success_count}")
    print(f"   ❌ Failed: {failed_count}")
    print(f"   ⏭️  Skipped: {skipped_count}")
    print(f"   📁 Total processed: {processed_count}")
    print(f"💾 SQL appended to: item-data.sql")


if __name__  == '__main__':
    main()