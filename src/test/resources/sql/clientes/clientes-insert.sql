insert into USUARIOS (id, username, password, role) values (100, 'ana@gmail.com', '$2a$12$BwNyksXV40ESORM6/O/dhuAggfrYZT73ZGaOSEbZnll1a7VvpcOFK', 'ROLE_ADMIN');
insert into USUARIOS (id, username, password, role) values (101, 'jonas@gmail.com', '$2a$12$BwNyksXV40ESORM6/O/dhuAggfrYZT73ZGaOSEbZnll1a7VvpcOFK', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (102, 'renato@gmail.com', '$2a$12$BwNyksXV40ESORM6/O/dhuAggfrYZT73ZGaOSEbZnll1a7VvpcOFK', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (103, 'maria@gmail.com', '$2a$12$BwNyksXV40ESORM6/O/dhuAggfrYZT73ZGaOSEbZnll1a7VvpcOFK', 'ROLE_CLIENTE');

insert into CLIENTES (id, nome, cpf, usuario_id) values (10, 'Jonas Nemer', '59073353009', 101);
insert into CLIENTES (id, nome, cpf, usuario_id) values (20, 'Renato Carlos', '06362896050', 102);