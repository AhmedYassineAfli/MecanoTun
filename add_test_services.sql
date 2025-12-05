-- Script SQL pour ajouter des services de test dans la base de données
-- Exécutez ce script dans votre base de données H2/PostgreSQL

-- Assurez-vous d'abord qu'il existe au moins un mécanicien
-- Si vous n'avez pas de mécanicien, créez-en un d'abord

-- Insérer des services de base (ajustez les IDs selon votre base de données)
-- Remplacez 'mechanic_id' par l'ID d'un mécanicien existant

INSERT INTO service (service_type, price, mechanic_id) VALUES 
('OIL_CHANGE', 50, 1),
('TIRE_ROTATION', 40, 1),
('BRAKE_INSPECTION', 60, 1),
('ENGINE_DIAGNOSTIC', 100, 1),
('TRANSMISSION_SERVICE', 150, 1),
('AIR_FILTER_REPLACEMENT', 30, 1),
('BATTERY_REPLACEMENT', 80, 1),
('WHEEL_ALIGNMENT', 70, 1);

-- Si la table utilise des noms différents, ajustez la requête
-- Par exemple, si c'est 'type' au lieu de 'service_type':
-- INSERT INTO service (type, price, mechanic_id) VALUES ('OIL_CHANGE', 50, 1);
