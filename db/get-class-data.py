import requests
import json
import time
import os
from openai import OpenAI

client = OpenAI(api_key=os.getenv('OPENAI_API_KEY'))

base_url = "https://www.dnd5eapi.co/api/2014"
url_class = "https://www.dnd5eapi.co/api/2014/classes"
url_subclass = "https://www.dnd5eapi.co/api/2014/subclasses"
url_race = "https://www.dnd5eapi.co/api/2014/races"
url_background = "https://www.dnd5eapi.co/api/2014/backgrounds"

def get_all_lists():
    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    response_class = requests.request("GET", url_class, headers=headers, data=payload)
    response_subclass = requests.request("GET", url_subclass, headers=headers, data=payload)
    response_race = requests.request("GET", url_race, headers=headers, data=payload)
    response_background = requests.request("GET", url_background, headers=headers, data=payload)
    
    class_json = json.loads(response_class.text)
    subclass_json = json.loads(response_subclass.text)
    race_json = json.loads(response_race.text)
    background_json = json.loads(response_background.text)

    return class_json, subclass_json, race_json, background_json

def get_individual(index, keyword):
    payload = {}    
    headers = {
        'Accept': 'application/json'
    }    

    response = requests.request("GET", base_url + "/" + keyword + "/" + index, headers=headers, data=payload)        
    
    return json.loads(response.text)

def ask_chatGPT_for_class(class_json):
    """Format class data for database"""
    prompt = f"""Convert this D&D class to PostgreSQL database format. Return ONLY valid JSON.

CLASS DATA:
{json.dumps(class_json, indent=2)}

Convert to this schema:
{{
  "name": "string max 50 chars",
  "description": "full description text", 
  "hit_dice_value": "D6|D8|D10|D12"
}}

RULES:
- name: Use "name" field, truncate to 50 chars if needed
- description: Join "desc" array or use name if no desc
- hit_dice_value: Extract from "hit_die" field (should be D6, D8, D10, or D12)

RESPOND WITH ONLY THE JSON, NO OTHER TEXT."""

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a precise JSON formatter for D&D classes. Always return valid JSON only."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.1,
            max_tokens=500
        )
        
        response_text = response.choices[0].message.content.strip()
        
        # Find JSON in the response
        start = response_text.find('{')
        end = response_text.rfind('}') + 1
        
        if start != -1 and end > start:
            json_text = response_text[start:end]
            return json.loads(json_text)
        
        return None
        
    except Exception as e:
        print(f"   ❌ OpenAI error: {e}")
        return None

def ask_chatGPT_for_race(race_json):
    """Format race data for database"""
    prompt = f"""Convert this D&D race to PostgreSQL database format. Return ONLY valid JSON.

RACE DATA:
{json.dumps(race_json, indent=2)}

Convert to this schema:
{{
  "name": "string max 50 chars",
  "stat_increases": "JSON object with ability score increases"
}}

RULES:
- name: Use "name" field, truncate to 50 chars if needed  
- stat_increases: Create JSON with ability_bonuses array converted to object format like {{"strength": 2, "dexterity": 0, "constitution": 1, "intelligence": 0, "wisdom": 0, "charisma": 0}}

RESPOND WITH ONLY THE JSON, NO OTHER TEXT."""

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a precise JSON formatter for D&D races. Always return valid JSON only."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.1,
            max_tokens=500
        )
        
        response_text = response.choices[0].message.content.strip()
        
        # Find JSON in the response
        start = response_text.find('{')
        end = response_text.rfind('}') + 1
        
        if start != -1 and end > start:
            json_text = response_text[start:end]
            return json.loads(json_text)
        
        return None
        
    except Exception as e:
        print(f"   ❌ OpenAI error: {e}")
        return None

def ask_chatGPT_for_background(background_json):
    """Format background data for database"""
    prompt = f"""Convert this D&D background to PostgreSQL database format. Return ONLY valid JSON.

BACKGROUND DATA:
{json.dumps(background_json, indent=2)}

Convert to this schema:
{{
  "name": "string max 50 chars",
  "description": "concise description text", 
  "starting_gold": "integer starting gold amount"
}}

RULES:
- name: Use "name" field, truncate to 50 chars if needed
- description: Create brief description from feature or summary
- starting_gold: Extract starting equipment value or use reasonable default (25-100)

RESPOND WITH ONLY THE JSON, NO OTHER TEXT."""

    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "You are a precise JSON formatter for D&D backgrounds. Always return valid JSON only."},
                {"role": "user", "content": prompt}
            ],
            temperature=0.1,
            max_tokens=500
        )
        
        response_text = response.choices[0].message.content.strip()
        
        # Find JSON in the response
        start = response_text.find('{')
        end = response_text.rfind('}') + 1
        
        if start != -1 and end > start:
            json_text = response_text[start:end]
            return json.loads(json_text)
        
        return None
        
    except Exception as e:
        print(f"   ❌ OpenAI error: {e}")
        return None

def escape_sql_string(text):
    """Properly escape strings for SQL"""
    if text is None:
        return ''
    return str(text).replace("'", "''").replace("\\", "\\\\")

def write_class_query(class_data):
    """Create SQL INSERT for class table"""
    name = escape_sql_string(class_data.get('name', ''))
    desc = escape_sql_string(class_data.get('description', ''))
    hit_dice = class_data.get('hit_dice_value', 'D8')
    
    return f"""INSERT INTO class (
    name, description, hit_dice_value
) VALUES (
    '{name}', '{desc}', '{hit_dice}'
);"""

def write_race_query(race_data):
    """Create SQL INSERT for race table"""
    name = escape_sql_string(race_data.get('name', ''))
    stat_increases = race_data.get('stat_increases', {})
    
    # Convert stat_increases to JSON string
    stat_json = json.dumps(stat_increases).replace("'", "''")
    
    return f"""INSERT INTO race (
    name, stat_increases
) VALUES (
    '{name}', '{stat_json}'
);"""

def write_background_query(background_data):
    """Create SQL INSERT for background table"""
    name = escape_sql_string(background_data.get('name', ''))
    desc = escape_sql_string(background_data.get('description', ''))
    starting_gold = background_data.get('starting_gold', 50)
    
    return f"""INSERT INTO background (
    name, description, starting_gold
) VALUES (
    '{name}', '{desc}', {starting_gold}
);"""

def write_subclass_query(subclass_data, parent_class_name):
    """Create SQL INSERT for subclass table using class name lookup"""
    name = escape_sql_string(subclass_data.get('name', ''))
    parent_class = escape_sql_string(parent_class_name)
    
    return f"""INSERT INTO subclass (name, class_source) 
SELECT '{name}', class_uuid FROM class WHERE name = '{parent_class}';"""

def main():
    print("🚀 Starting D&D data import for fresh database...")
    
    class_json, subclass_json, race_json, background_json = get_all_lists()
    
    total_items = len(class_json['results']) + len(race_json['results']) + len(subclass_json['results']) + len(background_json['results'])
    processed_count = 0
    success_count = 0
    failed_count = 0

    print(f"📦 Processing {total_items} total items...")

    with open("./character-data.sql", "w") as file:
        file.write("-- D&D Classes, Races, Backgrounds, and Subclasses for PostgreSQL\n")
        file.write("-- Generated by OpenAI GPT-3.5-Turbo\n")
        file.write("-- For fresh database instances\n\n")
        
        file.write("-- ===============================================\n")
        file.write("-- CLASSES\n") 
        file.write("-- ===============================================\n\n")
        
        # Process all classes first
        for class_ref in class_json['results']:
            class_data = get_individual(class_ref['index'], 'classes')
            processed_count += 1
            
            print(f"[{processed_count}/{total_items}] 🛡️  Processing class: {class_data['name']}")

            try:
                formatted_data = ask_chatGPT_for_class(class_data)
                
                if formatted_data is None:
                    print("   ❌ ChatGPT returned None")
                    failed_count += 1
                    continue
                    
                if not isinstance(formatted_data, dict) or 'name' not in formatted_data:
                    print("   ❌ Invalid response format")
                    failed_count += 1
                    continue
                
                sql_insert = write_class_query(formatted_data)
                file.write(sql_insert + '\n')
                print("   ✅ Success")
                success_count += 1
                
            except Exception as e:
                print(f"   ❌ Error: {e}")
                failed_count += 1
            
            time.sleep(0.1)
        
        file.write("\n-- ===============================================\n")
        file.write("-- RACES\n") 
        file.write("-- ===============================================\n\n")
        
        # Process all races
        for race_ref in race_json['results']:
            race_data = get_individual(race_ref['index'], 'races')
            processed_count += 1
            
            print(f"[{processed_count}/{total_items}] ✨ Processing race: {race_data['name']}")

            try:
                formatted_data = ask_chatGPT_for_race(race_data)
                
                if formatted_data is None:
                    print("   ❌ ChatGPT returned None")
                    failed_count += 1
                    continue
                    
                if not isinstance(formatted_data, dict) or 'name' not in formatted_data:
                    print("   ❌ Invalid response format")
                    failed_count += 1
                    continue
                
                sql_insert = write_race_query(formatted_data)
                file.write(sql_insert + '\n')
                print("   ✅ Success")
                success_count += 1
                
            except Exception as e:
                print(f"   ❌ Error: {e}")
                failed_count += 1
            
            time.sleep(0.1)

        file.write("\n-- ===============================================\n")
        file.write("-- BACKGROUNDS\n") 
        file.write("-- ===============================================\n\n")
        
        # Process all backgrounds
        for background_ref in background_json['results']:
            background_data = get_individual(background_ref['index'], 'backgrounds')
            processed_count += 1
            
            print(f"[{processed_count}/{total_items}] 📜 Processing background: {background_data['name']}")

            try:
                formatted_data = ask_chatGPT_for_background(background_data)
                
                if formatted_data is None:
                    print("   ❌ ChatGPT returned None")
                    failed_count += 1
                    continue
                    
                if not isinstance(formatted_data, dict) or 'name' not in formatted_data:
                    print("   ❌ Invalid response format")
                    failed_count += 1
                    continue
                
                sql_insert = write_background_query(formatted_data)
                file.write(sql_insert + '\n')
                print("   ✅ Success")
                success_count += 1
                
            except Exception as e:
                print(f"   ❌ Error: {e}")
                failed_count += 1
            
            time.sleep(0.1)

        file.write("\n-- ===============================================\n")
        file.write("-- SUBCLASSES\n") 
        file.write("-- ===============================================\n\n")
        
        # Process all subclasses
        for subclass_ref in subclass_json['results']:
            subclass_data = get_individual(subclass_ref['index'], 'subclasses')
            processed_count += 1
            
            print(f"[{processed_count}/{total_items}] ⚔️  Processing subclass: {subclass_data['name']}")

            try:
                # Get parent class name
                parent_class_name = subclass_data.get('class', {}).get('name', 'Unknown')
                
                # Create formatted data for subclass
                formatted_data = {
                    'name': subclass_data['name'][:50],  # Truncate to 50 chars
                    'parent_class_name': parent_class_name
                }
                
                sql_insert = write_subclass_query(formatted_data, parent_class_name)
                file.write(sql_insert + '\n')
                print("   ✅ Success")
                success_count += 1
                
            except Exception as e:
                print(f"   ❌ Error: {e}")
                failed_count += 1
            
            time.sleep(0.1)

    print(f"\n🎉 Completed!")
    print(f"   ✅ Successful: {success_count}")
    print(f"   ❌ Failed: {failed_count}")
    print(f"   📁 Total processed: {processed_count}")
    print(f"💾 SQL saved to: character-data.sql")
    print("\n📋 Ready to run on fresh database!")

if __name__ == '__main__':
    main()