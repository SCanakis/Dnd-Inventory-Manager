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

    response = requests.request("GET", url_equipment + "/"+index , headers=headers, data=payload)        
    
    return json.loads(response.text)


def get_magic_item(index):

    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    response = requests.request("GET", url_magic_item + "/"+index , headers=headers, data=payload)        
    
    return json.loads(response.text)

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
  * cp (copper) = quantity × 0.01
  * sp (silver) = quantity × 0.1  
  * ep (electrum) = quantity × 0.5
  * gp (gold) = quantity × 1.0
  * pp (platinum) = quantity × 10.0
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
    
    return None


def write_query(item_data):
    name = item_data['item_name'].replace("'", "''")
    desc = item_data['item_description'].replace("'", "''")
    
    # Handle NULL values
    ac_bonus = item_data['ac_bonus'] if item_data['ac_bonus'] is not None else 'NULL'
    capacity = item_data['capacity'] if item_data['capacity'] is not None else 'NULL'
    
    # Handle JSON/Array fields
    add_as_to_ac = f"'{json.dumps(item_data['add_as_to_ac'])}'" if item_data['add_as_to_ac'] is not None else 'NULL'
    item_equippable_type = f"'{{{','.join(item_data['item_equippable_type'])}}}'" if item_data['item_equippable_type'] is not None else 'NULL'
    ability_requirment = f"'{json.dumps(item_data['ability_requirment'])}'" if item_data['ability_requirment'] is not None else 'NULL'
    skill_altered_roll_type = f"'{json.dumps(item_data['skill_altered_roll_type'])}'" if item_data['skill_altered_roll_type'] is not None else 'NULL'
    skill_altered_bonus = f"'{json.dumps(item_data['skill_altered_bonus'])}'" if item_data['skill_altered_bonus'] is not None else 'NULL'
    
    return f"""INSERT INTO item_catalog (
    item_name, item_description, item_weight, item_value, attackable, 
    ac_bonus, add_as_to_ac, equippable, attunable, item_equippable_type,
    ability_requirment, skill_altered_roll_type, skill_altered_bonus,
    item_rarity, is_container, capacity
) VALUES (
    '{name}', '{desc}', {item_data['item_weight']}, {item_data['item_value']}, 
    {item_data['attackable']}, {ac_bonus}, {add_as_to_ac}, {item_data['equippable']}, 
    {item_data['attunable']}, {item_equippable_type}, {ability_requirment},
    {skill_altered_roll_type}, {skill_altered_bonus}, '{item_data['item_rarity']}',
    {item_data['is_container']}, {capacity}
);"""

def main():
    equipment_json, magic_item_json = get_item_list()
    
    total_item_num = len(equipment_json['results'] + magic_item_json['results'])
    count = 0


    processed_names = set()

    with open("./sql_data.sql", "w") as file:
        # Process all equipment
        for item_ref in equipment_json['results']:
            item_json = get_item(item_ref['index'])
            count += 1

            if item_json['name'] in processed_names:
                print("Already processed" + item_json['name'])
                continue
            processed_names.add(item_json['name'])

            item_data = ask_chatGPT(item_json)
            sql_insert = write_query(item_data)
            file.write(sql_insert + '\n\n')
            time.sleep(0.1)
            print(f"[{count}/{total_item_num}] 🛡️  Processing: {item_json['name']}")

        for item_ref in magic_item_json['results']:
            item_json = get_magic_item(item_ref['index'])
            count += 1

            if item_json['name'] in processed_names:
                print("Already processed" + item_json['name'])
                continue
            processed_names.add(item_json['name'])

            item_data = ask_chatGPT(item_json)
            sql_insert = write_query(item_data)
            file.write(sql_insert + '\n\n')
            time.sleep(0.1)
            print(f"[{count}/{total_item_num}] 🛡️  Processing: {item_json['name']}")


if __name__  == '__main__':
    main()