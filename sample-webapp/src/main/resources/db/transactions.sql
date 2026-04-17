----------------------------------------------------------------------------------------------------
--initialization

create table products(
    title text,
    qty int,
    price double precision
);

start transaction isolation level read committed;
show transaction isolation level;
insert into products values ('a', 10, 12), ('b', 1, 5);
select * from products;
commit;
----------------------------------------------------------------------------------------------------
--mvcc

select ctid, * from products;
select txid_current();
select * from pg_stat_activity;

--1st terminal
start transaction isolation level read committed;
update products set qty=9 where title='a';

--2nd terminal
start transaction isolation level read committed;
--is blocked until another transaction is committed or aborted
update products set qty=8 where title='a';
----------------------------------------------------------------------------------------------------
--anomalies: read uncommitted, non-repeatable read, lost update, write skew

--1st terminal
start transaction isolation level repeatable read;
update products set qty=2 where title='a';
--2nd terminal
start transaction isolation level repeatable read;
update products set qty=1 where title='a';
--1st terminal: commit
--2nd terminal: SQL Error [40001]: ERROR: could not serialize access due to concurrent update
----------------------------------------------------------------------------------------------------
--anomaly: write skew

create table workers(
    id int,
    type text
);

insert into workers values
    (1, 'A'),
    (2, 'A'),
    (1, 'B');
select * from workers;

with missing_workers as (
    select g.num as num, type
    from workers
    join generate_series(1, 2) g(num) on g.num != id
    where type='B'
)
insert into workers
select * from missing_workers;

--repeat insert from missing_workers in two terminals with serializable isolation level
start transaction isolation level serializable;
--SQL Error [40001]: ERROR: could not serialize access due to read/write dependencies among transactions
--  Detail: Reason code: Canceled on identification as a pivot, during commit attempt.
--  Hint: The transaction might succeed if retried.
----------------------------------------------------------------------------------------------------
