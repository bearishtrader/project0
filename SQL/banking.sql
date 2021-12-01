CREATE TABLE clients(
	client_id serial PRIMARY KEY,
	first_name varchar(50),
	last_name varchar(50));

CREATE TABLE accounts(
	account_id integer,
	client_id integer REFERENCES clients(client_id) ON DELETE CASCADE ON UPDATE NO ACTION,
	balance double PRECISION DEFAULT 0.0,
	PRIMARY KEY (client_id, account_id)
	);
