INSERT INTO order_state (id, state) VALUES (1, 'ACCEPTED');

INSERT INTO material (id, description, price) VALUES (1, 'Brick', 5.5);

INSERT INTO construction (id, description) VALUES (1, 'Test description');

INSERT INTO order_entity (id, order_date, state_id, construction_id) VALUES (1, '2024-01-22T19:31:32.283Z', 1, 1);

INSERT INTO order_detail (id, quantity, price, order_id, material_id) VALUES (1, 20, 5.5, 1, 1);
