import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="123456",
        database="jobhunter_db"
    )
    cursor = conn.cursor(dictionary=True)
    
    print("--- User Subscriptions ---")
    cursor.execute("SELECT us.id, u.email, s.name as package, us.active, us.start_date, us.end_date FROM user_subscriptions us JOIN users u ON us.user_id = u.id JOIN subscriptions s ON us.subscription_id = s.id;")
    rows = cursor.fetchall()
    if not rows:
        print("No user subscriptions found.")
    for row in rows:
        print(row)
        
    print("\n--- Payment History ---")
    cursor.execute("SELECT p.id, u.email, s.name as package, p.amount, p.status, p.payment_method FROM payment_history p JOIN users u ON p.user_id = u.id JOIN subscriptions s ON p.subscription_id = s.id;")
    rows = cursor.fetchall()
    if not rows:
        print("No payment history found.")
    for row in rows:
        print(row)
        
    cursor.close()
    conn.close()
except Exception as e:
    print("Error:", e)
