INSERT INTO products (name, price)
SELECT 'Keyboard', 39.99
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Keyboard');

INSERT INTO products (name, price)
SELECT 'Mouse', 19.50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mouse');
