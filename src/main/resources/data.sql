INSERT IGNORE INTO subscriptions (id, name, price, duration_days, limit_posts, active, created_at, updated_at) VALUES 
(1, 'Free', 0, 30, 3, 1, NOW(), NOW()), 
(2, 'Professional', 500000, 30, 20, 1, NOW(), NOW()), 
(3, 'Enterprise', 2000000, 30, -1, 1, NOW(), NOW());
