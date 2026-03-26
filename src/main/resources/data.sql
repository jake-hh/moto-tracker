INSERT INTO "APP_USER" (ID, VERSION, USERNAME, FIRST_NAME, LAST_NAME, PASSWORD_HASH) VALUES
(101, 1, 'admin', 'Mark', 'Robers', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),
(102, 1, 'user',  'John', 'Deere',  '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),
(103, 1, 'guest', 'Alex', 'Jonson', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW');

INSERT INTO "VEHICLE" (ID, VERSION, OWNER_ID, TYPE, MAKE, MODEL, ENGINE, COLOUR, MILEAGE, TRACKING_DATE) VALUES
(301, 1, 102, 'Car',        'Opel',   'Astra', '1.6 CDTI', 'Brown', 100_000, DATE '2026-01-01'),
(302, 1, 102, 'Car',        'Skoda',  'Fabia', '1.4 TDI',  'Red',   600_000, DATE '2026-01-01'),
(303, 1, 102, 'Motorcycle', 'Yamaha', 'R125',  '125cc',    'Black',  53_000, DATE '2023-06-21'),
(304, 1, 102, 'Moped',      'Romet',  'ZXT50', '50cc',     'Yellow', 15_000, DATE '2023-01-01');

INSERT INTO "APP_USER_SETTINGS" (ID, VERSION, USER_ID, SELECTED_VEHICLE_ID) VALUES
(201, 1, 102, 303);

INSERT INTO "EVENT" (ID, VERSION, VEHICLE_ID, DATE_VALUE, MILEAGE) VALUES
(401, 1, 303, DATE '2023-06-21', 48000),
(402, 1, 303, DATE '2024-03-21', 50000),
(403, 1, 303, DATE '2024-09-21', 50600),
(404, 1, 303, DATE '2025-08-26', 51500);

INSERT INTO "TRACKER" (ID, VERSION, VEHICLE_ID, NAME, INTERV_AMOUNT, INTERV_UNIT, RANGE) VALUES
(501, 1, 303, 'Oil change',         1, 3, 10000),
(502, 1, 303, 'Brake fluid change', 2, 3, 20000),
(503, 1, 303, 'Coolant',            3, 3, 30000),
(504, 1, 303, 'Chain cleaning',     1, 2, 700);
INSERT INTO "TRACKER" (ID, VERSION, VEHICLE_ID, NAME) VALUES
(505, 1, 303, 'Brake pads'),
(506, 1, 303, 'Chain & sprockets'),
(507, 1, 303, 'Timing belt'),
(508, 1, 303, 'Spark plugs'),
(509, 1, 303, 'Air filter'),
(510, 1, 303, 'Tires'),
(511, 1, 303, 'Clutch'),
(512, 1, 303, 'Clutch cable');

INSERT INTO "DEFAULT_TRACKER" (ID, VERSION, NAME, INTERV_AMOUNT, INTERV_UNIT, RANGE) VALUES
(701, 1, 'Oil change',         1, 3, 10000),
(702, 1, 'Brake fluid change', 2, 3, 20000),
(703, 1, 'Coolant',            3, 3, 30000),
(704, 1, 'Chain cleaning',     1, 2,   700);
INSERT INTO "DEFAULT_TRACKER" (ID, VERSION, NAME) VALUES
(705, 1, 'Brake pads'),
(706, 1, 'Chain & sprockets'),
(707, 1, 'Spark plugs'),
(708, 1, 'Air filter'),
(709, 1, 'Tires'),
(710, 1, 'Timing belt'),
(711, 1, 'Clutch'),
(712, 1, 'Wiper blades'),
(713, 1, 'Transmission fluid'),
(714, 1, 'Power steering fluid'),
(715, 1, 'Clutch cable'),
(716, 1, 'Clutch fluid'),
(717, 1, 'Gearbox oil'),
(718, 1, 'Differential oil'),
(719, 1, 'Wheel bearings'),
(720, 1, 'CV joints'),
(721, 1, 'Shock absorbers'),
(722, 1, 'Serpentine belt'),
(723, 1, 'Thermostat'),
(724, 1, 'Fuel filter'),
(725, 1, 'Battery'),
(726, 1, 'Headlight bulbs'),
(727, 1, 'Cabin air filter');

INSERT INTO "OPERATION" (ID, VERSION, EVENT_ID, TRACKER_ID) VALUES
(601, 1, 401, 503),
(602, 1, 402, 502),
(603, 1, 403, 501),
(604, 1, 403, 507),
(605, 1, 404, 505),
(606, 1, 404, 512),
(607, 1, 404, 506);
