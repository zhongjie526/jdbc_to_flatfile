create table transactions (
                            ProductID VARCHAR2(8),
                            ProductName VARCHAR2(8),
                            Price NUMBER(8,4),
                            ProductDescription VARCHAR2(8)
                            );

insert into transactions values ( 'pid', 'pname', 2.3, 'pdesc');
insert into transactions values ( 'pid1', 'pname1', 3.4, 'pdesc1');