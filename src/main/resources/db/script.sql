/* inserir id integracao tabela controle */
ALTER TABLE controle ADD id_integracao varchar(30) NULL;

/* inserir id integracao tabela status boleto
ALTER TABLE statusboleto ADD id_integracao varchar(30) NULL;

/* inserir a situacao do boleto */
ALTER TABLE statusboleto ADD situacao INT NOT NULL COMMENT 
 '1- PENDENTE
  2- SALVO
  3- FALHA
  4- EMITIDO
  5- REJEITADO
  6- REGISTRADO
  7- LIQUIDADO
  8- BAIXADO';

/* mensagem status */
ALTER TABLE statusboleto ADD mensagem TEXT NULL;

/* Criar a pk para tabela statusboleto que deve ser a fk para a tabela boleto */
ALTER TABLE avinfoloja.statusboleto ADD CONSTRAINT statusboleto_PK PRIMARY KEY (Id);

/* criar a fk para a tabela statusboleto com referência para a tabela boleto */


/* Criar a pk para a tabela boleto */