# Ecommerce

Projeto Java em console para gestão de **clientes**, **categorias**, **produtos** e **pedidos**, usando JDBC com MySQL.

## Tecnologias

- Java
- JDBC
- MySQL
- IntelliJ IDEA

## Funcionalidades

- Cadastro, listagem, atualização e exclusão de clientes
- Cadastro, listagem, atualização e exclusão de categorias
- Cadastro, listagem, atualização e exclusão de produtos
- Cadastro e consulta de pedidos
- Cadastro de itens de pedido
- Controle básico de estoque ao criar pedidos
- Validação para impedir exclusões com relacionamentos vinculados

## Estrutura

- `src/model` - entidades do domínio
- `src/repository` - contratos de acesso a dados
- `src/repository/impl` - implementações JDBC
- `src/service` - regras de negócio
- `src/controller` - menus e interação com o usuário
- `src/config` - configuração de banco
- `src/exception` - exceções do projeto

## Banco de dados

O projeto usa o banco MySQL `ecommerce`, configurado em:

`src/config/DatabaseConfig.java`

Parâmetros atuais:

- URL: `jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC`
- Usuário: `root`
- Senha: `123456789`

## Como executar

1. Crie o banco `ecommerce` no MySQL.
2. Ajuste `DatabaseConfig.java` se necessário.
3. Compile o projeto.
4. Execute a classe `Main`.

## Menu principal

- `1` - Clientes
- `2` - Produtos
- `3` - Pedidos
- `0` - Sair

## Observação

Os deletes são protegidos para evitar quebra de relacionamento:

- cliente não pode ser excluído se tiver pedidos
- categoria não pode ser excluída se tiver produtos
- produto não pode ser excluído se já tiver sido usado em pedidos
