INSERT INTO order_state (id, state) VALUES (1, 'NEW');
INSERT INTO order_state (id, state) VALUES (2, 'CONFIRMED');
INSERT INTO order_state (id, state) VALUES (3, 'PENDING');
INSERT INTO order_state (id, state) VALUES (4, 'CANCELLED');
INSERT INTO order_state (id, state) VALUES (5, 'ACCEPTED');
INSERT INTO order_state (id, state) VALUES (6, 'REFUSED');
INSERT INTO order_state (id, state) VALUES (7, 'IN PREPARATION');
INSERT INTO order_state (id, state) VALUES (8, 'DELIVERED');

INSERT INTO material (id, description, price, current_stock) VALUES (1, 'Brick', 5.5, 155);
INSERT INTO material (id, description, price, current_stock) VALUES (2, 'Brick 2', 8.5, 130);

INSERT INTO construction (id, description) VALUES (1, 'Test description');
INSERT INTO construction (id, description) VALUES (2, 'Test description 2');

INSERT INTO order_entity (id, order_date, state_id, construction_id) VALUES (1, '2024-01-22T19:31:32.283Z', 1, 1);

INSERT INTO order_detail (id, quantity, price, order_id, material_id) VALUES (1, 20, 5.5, 1, 1);
INSERT INTO order_detail (id, quantity, price, order_id, material_id) VALUES (2, 50, 8.5, 1, 2);
