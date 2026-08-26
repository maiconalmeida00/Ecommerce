# Ecommerce

Aplicação Java em console para gerenciamento de clientes, categorias, produtos e pedidos. O acesso aos dados é feito com JDBC e MySQL.

## Tecnologias

- Java
- JDBC
- MySQL 8 ou superior
- IntelliJ IDEA ou outro ambiente compatível

## Funcionalidades

- Cadastro, consulta, atualização e inativação de clientes
- Cadastro, listagem, atualização e exclusão de categorias
- Cadastro, consulta, atualização e inativação de produtos
- Criação e consulta de pedidos
- Atualização de status e inativação de pedidos
- Cadastro de itens de pedido
- Cálculo do total do pedido
- Baixa de estoque ao criar um pedido
- Validação de relacionamentos antes das exclusões
- Tratamento de entradas inválidas no terminal

## Menus

### Menu principal

- `1` - Clientes
- `2` - Produtos e categorias
- `3` - Pedidos
- `0` - Sair

### Clientes

- `1` - Listar clientes
- `2` - Buscar clientes por nome
- `3` - Criar cliente
- `4` - Atualizar cliente
- `5` - Deletar cliente
- `0` - Voltar

### Produtos e categorias

Produtos:

- `1` - Listar produtos
- `2` - Listar produtos acima de um preço
- `3` - Criar produto
- `4` - Atualizar produto
- `5` - Deletar produto

Categorias:

- `6` - Criar categoria
- `7` - Atualizar categoria
- `8` - Deletar categoria
- `9` - Listar categorias
- `0` - Voltar

### Pedidos

- `1` - Listar pedidos
- `2` - Listar pedidos por cliente
- `3` - Criar pedido
- `4` - Atualizar status do pedido
- `5` - Deletar pedido
- `0` - Voltar

## Validações

### Clientes

- Nome, e-mail, senha, CPF e telefone são obrigatórios no cadastro.
- A senha é armazenada como hash SHA-256, nunca como texto puro.
- CPF deve possuir 11 dígitos e é salvo no formato `123.456.789-10`.
- Telefone deve possuir 11 dígitos e é salvo no formato `(11) 91111-1111`.
- E-mail e CPF duplicados são rejeitados antes da gravação.

### Produtos

- Nome e categoria são obrigatórios.
- Preço não pode ser negativo.
- Estoque não pode ser negativo.
- Gênero deve ser `M`, `F` ou `U`.
- Apenas produtos ativos são exibidos nas consultas.

### Pedidos

- O cliente e o endereço de entrega são obrigatórios.
- O pedido deve possuir pelo menos um item.
- A quantidade deve ser maior que zero.
- O mesmo produto não pode aparecer mais de uma vez no pedido.
- Os status aceitos são `PENDENTE`, `PAGO`, `ENVIADO`, `ENTREGUE` e `CANCELADO`.
- A entrada do status é normalizada para maiúsculo antes de ser salva.

## Banco de dados

O projeto utiliza o banco MySQL `ecommerce`. A configuração está em [src/config/DatabaseConfig.java](src/config/DatabaseConfig.java).

Configuração atual:

- URL: `jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC`
- Usuário: `root`
- Senha: definida diretamente em `DatabaseConfig.java`

O schema possui as tabelas:

- `customers`
- `categories`
- `products`
- `orders`
- `order_items`

O banco também possui chaves estrangeiras, índices, restrições de integridade, campos `active` e triggers para manter o total do pedido sincronizado com seus itens.

## Exclusões e inativação

- Clientes e pedidos são inativados com `active = 0`.
- Produtos são inativados com `active = 0`.
- Categorias são excluídas fisicamente somente quando não possuem produtos vinculados.
- Produtos não podem ser excluídos quando já foram usados em itens de pedido.
- Clientes não podem ser excluídos quando possuem pedidos vinculados.

## Estrutura do projeto

- `src/model` - entidades do domínio
- `src/repository` - interfaces de acesso a dados
- `src/repository/impl` - implementações JDBC
- `src/service` - regras de negócio e validações
- `src/controller` - menus e interação com o usuário
- `src/config` - configuração da conexão com o banco
- `src/exception` - exceções de negócio e banco de dados

## Como executar

1. Instale o Java e o MySQL.
2. Crie o banco `ecommerce` executando o script SQL do projeto.
3. Ajuste URL, usuário e senha em `src/config/DatabaseConfig.java`.
4. Garanta que o driver JDBC do MySQL esteja disponível no classpath do projeto.
5. Compile e execute a classe `Main` pela IDE ou pelo terminal.

Exemplo de compilação quando o driver JDBC está em `lib/mysql-connector-j.jar`:

```powershell
javac -cp "lib/mysql-connector-j.jar" -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "out;lib/mysql-connector-j.jar" Main
```

## IDs auto incrementais

As tabelas utilizam `AUTO_INCREMENT`. Apagar ou inativar registros não reinicia a sequência de IDs. Portanto, é esperado que o próximo registro receba um número maior mesmo quando não existem registros ativos.

Para reiniciar a sequência manualmente, somente quando isso for desejado e seguro:

```sql
ALTER TABLE customers AUTO_INCREMENT = 1;
```

Para apagar todos os registros e reiniciar a tabela, use `TRUNCATE TABLE` com cuidado, pois essa operação remove os dados.
