# Instruções de Uso

## Comandos disponíveis

* cadastrar-cedente
* cadastrar-conta
* cadastrar-convenio
* incluir-boleto
* gerar-remessa

## cadastrar-cedente

Comando para cadastrar um cedente na API Plugboleto Tecnosped: [documentacao oficial API](https://atendimento.tecnospeed.com.br/hc/pt-br/articles/360006128214-Cadastrando-um-Cedente)

### Invocando o cadastrar-cedente

Inserir o registro abaixo na tabela boletocomando no banco de dados do Check:

| Id               | comando           | param1 | status | mensagem |
|------------------|-------------------|--------|--------|----------|
| [autoincremento] | cadastrar-cedente | [NULL] | 1      | [NULL]   |

O avboleto deverá pegar os dados do cedente da tabela controle do Check e logo após invoca a API do Plugboleto
e escreve a resposta da API na coluna mensagem da tabela boletocomando e atualiza o status de acordo com esse mesmo retorno.

## cadastrar-conta

Comando para cadastrar uma conta na API Plugboleto Tecnosped: [documentacao oficial API](https://atendimento.tecnospeed.com.br/hc/pt-br/articles/360006230413-Cadastrando-uma-Conta)

### Invocando o cadastrar-conta

Inserir o registro abaixo na tabela boletocomando no banco de dados do Check:

| Id               | comando           | param1                     | status | mensagem |
|------------------|-------------------|----------------------------|--------|----------|
| [autoincremento] | cadastrar-conta   | [sr_recno tabela conta30i] | 1      | [NULL]   |

O avboleto deverá pegar os dados da conta da tabela conta30i do Check e logo após invoca a API do Plugboleto
e escreve a resposta da API na coluna mensagem da tabela boletocomando e atualiza o status de acordo com esse mesmo retorno.

## cadastrar-convenio

Comando para cadastrar um convênio na API Plugboleto Tecnosped: [documentacao oficial API](https://atendimento.tecnospeed.com.br/hc/pt-br/articles/360006145374-Cadastrando-um-Conv%C3%AAnio)

### Invocando o cadastrar-convenio

Inserir o registro abaixo na tabela boletocomando no banco de dados do Check:

| Id               | comando              | param1                     | status | mensagem |
|------------------|----------------------|----------------------------|--------|----------|
| [autoincremento] | cadastrar-convenio   | [sr_recno tabela convenio] | 1      | [NULL]   |

O avboleto deverá pegar os dados da tabela convenio do Check e logo após invoca a API do Plugboleto
e escreve a resposta da API na coluna mensagem da tabela boletocomando e atualiza o status de acordo com esse mesmo retorno.

## incluir-boleto

Comando para incluir um boleto na API Plugboleto Tecnosped: [documentacao oficial API](https://atendimento.tecnospeed.com.br/hc/pt-br/articles/360006232893-Incluindo-um-Boleto)

### Invocando o incluir-boleto

Inserir o registro abaixo na tabela boletocomando no banco de dados do Check:

| Id               | comando              | param1                       | status | mensagem |
|------------------|----------------------|------------------------------|--------|----------|
| [autoincremento] | incluir-boleto       | [NumeroBoleto tabela boleto] | 1      | [NULL]   |

#### [DETALHES SERÃO INFORMADOS POSTERIORMENTE]

## Configuração do sistema

O sistema necessita da criação de um arquivo de configuração externo, para inputar parâmteros de conexão com banco de dados, entre outros:

Esse arquivo deve ser salvo na mesma pasta em que o jar da aplicação se encontra, com o nome **application.properties**

#### Seguem os parâmetros:

```Properties

#url de conexao com banco de dados
spring.datasource.url=jdbc:mysql://localhost/avinfoloja

#usuario banco de dados
spring.datasource.username=user

#senha banco de dados
spring.datasource.password=password

#host principal da api plugboleto (versão de testes)
# para produção deve-se utilizado o host de produção (plugboleto.com.br/api)
tecnosped.boleto.api.host=homologacao.plugboleto.com.br/api

#versao da api plugboleto
tecnosped.boleto.api.version=1

#header que contem o cnpj da softwarehouse
tecnosped.boleto.api.cnpjsh=01001001000113

#header que contem o token da softwarehouse
tecnosped.boleto.api.tokensh=f22b97c0c9a3d41ac0a3875aba69e5aa

#periodo de carredura do sistema avboleto, varredura na tabela boletocomando para disparo dos comandos
periodo.varredura.seg=5

#flag que indica qual protocolo usar (true=https, false=http), quando o sistema for utilizado em produção 
#deve-se utilizar esse parâmetro como true
habilitar.https=false
	
```

## Rodando a aplicação 

Selecione a mesma pasta onde contenham o arquivo jar principal avboleto-0.0.1-SNAPSHOT.jar e o application.properties e execute o comando:

```bash
    java -jar avboleto-0.0.1-SNAPSHOT.jar
```

## Download do Release

[versão beta](https://bitbucket.org/tominem/avboleto/downloads/avboleto-0.0.2-SNAPSHOT.jar.zip)



