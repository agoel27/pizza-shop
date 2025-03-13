/* Replace the location to where you saved the data files*/
COPY Users
FROM '/home/csmajs/agoel006/pizza-shop/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM '/home/csmajs/agoel006/pizza-shop/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM '/home/csmajs/agoel006/pizza-shop/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM '/home/csmajs/agoel006/pizza-shop/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM '/home/csmajs/agoel006/pizza-shop/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
