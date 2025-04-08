INSERT INTO tb_restaurant
    (id, name, address, kitchen_type, opening_time, closing_time, available_capacity)
VALUES
    (gen_random_uuid(), 'Paris 6', 'Alameda Tietê, 279 - Jardim Paulista', 'francesa', '18:00:00', '23:00:00', 200),
    (gen_random_uuid(), 'Pinocchio Cucina', 'Alameda Tietê, 140 - Jardim Paulista', 'italiana', '12:00:00', '05:00:010', 100);
    (gen_random_uuid(), 'Barletta Ristorante', 'Alameda Tietê, 360 - Jardim Paulista', 'vegetariana', '18:30:00', '23:00:00', 150),
    (gen_random_uuid(), 'Restaurante Eze', 'Alameda Tietê, 513 - Jardim Paulista', 'mediterranea', '19:00:00', '23:00:00', 90),
    (gen_random_uuid(), 'Cantina do Italiano', 'Rua Itapura, 1524 - Vila Gomes Cardim', 'italiana', '17:00:00', '23:00:00', 150),
    (gen_random_uuid(), 'Don Carlini Ristorante', 'Rua Dona Ana Neri, 265 - Mooca', 'italiana', '17:00:00', '23:00:00', 250);