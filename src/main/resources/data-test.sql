INSERT INTO role (id, type) VALUES (1, 'CUSTOMER');
INSERT INTO role (id, type) VALUES (2, 'SELLER');

INSERT INTO user_entity (id, username, password, role_id) VALUES (1, 'emi123', '$2a$10$3MqlE56NbZRMPDduskqlV.j5UepDJn/NJcvzgaLvaUO76u/.ar65e', 1);
INSERT INTO user_entity (id, username, password, role_id) VALUES (2, 'celi123', '$2a$10$Qf3m9/VjtJmIrG2Or5039.cJmVlfYUUs9fBcIRoiBfYuLdZFWzW3S', 1);
INSERT INTO user_entity (id, username, password, role_id) VALUES (3, 'test', '$2a$10$Qf3m9/VjtJmIrG2Or5039.cJmVlfYUUs9fBcIRoiBfYuLdZFWzW3S', 2);
