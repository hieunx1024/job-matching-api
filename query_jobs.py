import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="123456",
        database="jobhunter_db"
    )
    cursor = conn.cursor(dictionary=True)
    
    # Check all active jobs and their skills
    print("--- Active Jobs with Skills ---")
    cursor.execute("""
        SELECT j.id as job_id, j.name as job_name, j.location, s.name as skill_name, j.active 
        FROM jobs j 
        LEFT JOIN job_skill js ON j.id = js.job_id 
        LEFT JOIN skills s ON js.skill_id = s.id
    """)
    rows = cursor.fetchall()
    
    for row in rows:
        print(f"[{'ACTIVE' if row['active'] else 'INACTIVE'}] Job: {row['job_name']} | Location: {row['location']} | Skill: {row['skill_name']}")
        
    print("\n--- All Skills in Database ---")
    cursor.execute("SELECT id, name FROM skills")
    skills = cursor.fetchall()
    print([s['name'] for s in skills])

    cursor.close()
    conn.close()
except Exception as e:
    print("Error:", e)
