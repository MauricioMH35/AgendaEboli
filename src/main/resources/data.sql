-- SHOW COLUMNS FROM TBL_CUSTOMERS;
INSERT INTO TBL_CUSTOMERS (FULLNAME, CNPJ, FOUNDATION, REGISTERED) VALUES
 ('Bruna e Calebe Alimentos Ltda', '31449057000104', '1990-01-08', '2017-06-23 15:32:13'),
 ('Kamilly e Marlene Casa Noturna ME', '86678081000107', '1989-12-03', '2021-09-09 09:45:01'),
 ('Filipe e Alexandre Publicidade e Propaganda Ltda', '65203917000139', '2001-05-21','2021-09-18 13:01:23'),
 ('Fabiana e Jennifer Eletrônica ME', '46989866000178', '1960-06-12', '2022-10-08 08:12:32'),
 ('Giovanni e Leandro Adega Ltda', '48847337000165', '1978-11-07', '2022-12-03 13:32:21');

INSERT INTO TBL_ADDRESSES (PUBLIC_PLACE, NUMBER, COMPLEMENT, NEIGHBORHOOD, CITY, STATE, ZIP_CODE, CUSTOMER_ID) VALUES
 ('Rua Duque de Caxias', 444, 'A123', 'Serra das Lagoas', 'Campinas', 'SP', '11608545', 1),
 ('Rua Antônio Machado', 12, 'BL 1 APT 23', 'Laranjeiras', 'Serra', 'ES', '29175665', 2),
 ('Travessa Tessália', 1209, 'C27', 'Quintino Cunha', 'Fortaleza', 'CE', '60351440', 3),
 ('Rua Projetada', 32, 'BL 4 APT 12', 'Catavento', 'Picos', 'PI', '57075555', 4),
 ('Rua Aguiar de Barros', 305, 'BL 2 APT 21', 'Bela Vista', 'São Paulo', 'SP', '01316020', 5);

INSERT INTO TBL_CONTACTS (TYPE, CONTACT, CUSTOMER_ID) VALUES
 ('WEB_SITE', 'www.brunaecalebealimentosltda.com.br', 1),
 ('PHONE', '21 37162285', 2),
 ('EMAIL', 'producao@fapublicidade.com.br', 3),
 ('CELLPHONE', '22 985764503', 4),
 ('PHONE', '11 35268409', 5);

INSERT INTO TBL_AGENDA (TITLE, DESCRIPTION, MARKED_TO, CONCLUDED, CUSTOMER_ID) VALUES
 ('Reuniao Giovanni e Leandro Adega Ltda', 'Controle de Estoque', '2023-01-25 15:30:00', FALSE, 5),
 ('Reuniao Fabiana e Jennifer Eletrônica ME', 'Levantamento de Requisito Emisao de Nota Fiscal', '2023-01-12 09:30:00', FALSE, 4);