INSERT INTO users_roles (user_id, role_id)
VALUES (3, (SELECT id FROM roles WHERE role = 'ADMIN'));