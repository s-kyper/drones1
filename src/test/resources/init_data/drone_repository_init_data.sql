-- clear table
delete from medications;
delete from drones;

--Init drones

INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (0, '0', 'LIGHTWEIGHT', 24, 100, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (1, '1', 'LIGHTWEIGHT', 25, 100, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (2, '2', 'LIGHTWEIGHT', 75, 100, 'LOADED', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (3, '3', 'MIDDLEWEIGHT', 40, 200, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (4, '4', 'MIDDLEWEIGHT', 100, 200, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (5, '5', 'MIDDLEWEIGHT', 55, 200, 'DELIVERING', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (6, '6', 'CRUISERWEIGHT', 85, 300, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (7, '7', 'CRUISERWEIGHT', 35, 300, 'DELIVERED', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (8, '8', 'HEAVYWEIGHT', 75, 400, 'IDLE', CURRENT_TIMESTAMP);
INSERT INTO drones (id, serial_number, drone_model, battery_capacity, weight_limit, drone_state, created_at) VALUES (9, '9', 'HEAVYWEIGHT', 85, 400, 'RETURNING', CURRENT_TIMESTAMP);
