CREATE TABLE orgchart(
  name VARCHAR(24) PRIMARY KEY,
  reportsTo VARCHAR(24) REFERENCES orgchart);

INSERT INTO orgchart VALUES('Carol',  NULL);

INSERT INTO orgchart VALUES('Alice', 'Carol');
INSERT INTO orgchart VALUES('Bob',   'Carol');
INSERT INTO orgchart VALUES('Ted',   'Carol');

INSERT INTO orgchart VALUES('Edna',  'Alice');
INSERT INTO orgchart VALUES('Joyce', 'Alice');
INSERT INTO orgchart VALUES('Edgar', 'Alice');

INSERT INTO orgchart VALUES('Al',    'Bob');
INSERT INTO orgchart VALUES('Maria', 'Bob');
INSERT INTO orgchart VALUES('Nick',  'Bob');

INSERT INTO orgchart VALUES('Ruth', 'Ted');
INSERT INTO orgchart VALUES('Ky',   'Ted');
INSERT INTO orgchart VALUES('Nina', 'Ted');

INSERT INTO orgchart VALUES('Joe', 'Edna');

INSERT INTO orgchart VALUES('Terry', 'Edgar');

INSERT INTO orgchart VALUES('Petra', 'Ky');
