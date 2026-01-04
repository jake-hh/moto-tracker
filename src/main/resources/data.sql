INSERT INTO "APP_USER" (ID, VERSION, USERNAME, PASSWORD_HASH) VALUES
(101, 1, 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),
(102, 1, 'user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW'),
(103, 1, 'guest', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW');

INSERT INTO "VEHICLE" (ID, VERSION, OWNER_ID, TYPE, MAKE, MODEL, ENGINE, COLOUR, TRACKING_DATE) VALUES
(301, 1, 102, 'Car',       'Opel',   'Astra', '1.6 CDTI', 'Brown',  DATE '2026-01-01'),
(302, 1, 102, 'Car',       'Skoda',  'Fabia', '1.4 TDI',  'Red',    DATE '2026-01-01'),
(303, 1, 102, 'Motorbike', 'Yamaha', 'R125',  '125cc',    'Black',  DATE '2023-06-21'),
(304, 1, 102, 'Moped',     'Romet',  'ZXT50', '50cc',     'Yellow', DATE '2023-01-01');

INSERT INTO "EVENT" (ID, VERSION, VEHICLE_ID, DATE_VALUE, MILEAGE) VALUES
(401, 1, 303, DATE '2023-06-21', 48000),
(402, 1, 303, DATE '2024-03-21', 50000),
(403, 1, 303, DATE '2024-09-21', 50600),
(404, 1, 303, DATE '2025-08-26', 51500);

INSERT INTO "TRACKER" (ID, VERSION, NAME, INTERV_AMOUNT, INTERV_UNIT, RANGE) VALUES
(501, 1, 'Oil change',         1, 3, 10000),
(502, 1, 'Brake fluid change', 2, 3, 20000),
(503, 1, 'Coolant',            3, 3, 30000),
(504, 1, 'Chain cleaning',     1, 2, 700);
INSERT INTO "TRACKER" (ID, VERSION, NAME) VALUES
(505, 1, 'Brake pads'),
(506, 1, 'Chain & sprockets'),
(507, 1, 'Timing belt'),
(508, 1, 'Spark plugs'),
(509, 1, 'Air filter'),
(510, 1, 'Tires'),
(511, 1, 'Clutch'),
(512, 1, 'Clutch cable');

INSERT INTO "OPERATION" (ID, VERSION, EVENT_ID, TRACKER_ID) VALUES
(601, 1, 401, 503),
(602, 1, 402, 502),
(603, 1, 403, 501),
(604, 1, 403, 507),
(605, 1, 404, 505),
(606, 1, 404, 512),
(607, 1, 404, 506);
