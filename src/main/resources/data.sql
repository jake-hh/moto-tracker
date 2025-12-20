INSERT INTO "EVENT" (ID, VERSION, DATE_VALUE, MILEAGE) VALUES
(101, 1, DATE '2023-06-21', 48000),
(102, 1, DATE '2024-03-21', 50000),
(103, 1, DATE '2024-09-21', 50600),
(104, 1, DATE '2025-08-26', 51500);

INSERT INTO "TRACKER" (ID, VERSION, NAME, INTERV_AMOUNT, INTERV_UNIT, RANGE) VALUES
(201, 1, 'Oil change',         1, 3, 10000),
(202, 1, 'Brake fluid change', 2, 3, 20000),
(203, 1, 'Coolant',            3, 3, 30000),
(204, 1, 'Chain cleaning',     1, 2, 700);
INSERT INTO "TRACKER" (ID, VERSION, NAME) VALUES
(205, 1, 'Brake pads'),
(206, 1, 'Chain & sprockets'),
(207, 1, 'Timing belt'),
(208, 1, 'Spark plugs'),
(209, 1, 'Air filter'),
(210, 1, 'Tires'),
(211, 1, 'Clutch'),
(212, 1, 'Clutch cable');

INSERT INTO "OPERATION" (ID, VERSION, EVENT_ID, TRACKER_ID) VALUES
(301, 1, 101, 203),
(302, 1, 102, 202),
(303, 1, 103, 201),
(304, 1, 103, 207),
(305, 1, 104, 205),
(306, 1, 104, 212),
(307, 1, 104, 206);
