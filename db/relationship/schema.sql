create table engine (
    id serial primary key,
    name varchar(100) not null,
    serial_number varchar(100) not null unique
);

create table driver (
    id serial primary key,
    name varchar(255) not null
);

create table car (
    id serial primary key,
    name varchar(255) not null,
    engine_id int not null unique references engine(id)
);

create table history_owner (
    driver_id int not null references driver(id),
    car_id int not null references car(id)
);

