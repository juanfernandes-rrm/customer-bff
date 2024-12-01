# Gateway - Nota Social

Bem-vindo ao projeto Gateway do sistema NotaSocial. Este projeto faz parte do Trabalho de Conclusão de Curso (TCC) do Curso Superior de Tecnologia em Análise e Desenvolvimento de Sistemas (TADS) - UFPR. O Gateway é responsável por rotear as requisições para os serviços do sistema: receipt-scan, catalog, register, social e auth.

Este documento fornece instruções detalhadas para configurar e executar o Gateway, além de descrever o funcionamento e propósito de cada serviço integrado.

## Pré-requisitos

Certifique-se de ter instalado:

- Docker
- Docker Compose

## Configuração e Execução

### 1. Clonar os Repositórios:
Para que o docker-compose seja executado corretamente, clone os repositórios dos serviços em um mesmo repositório de forma que siga a seguinte estrutura:
- [receipt-scan](https://github.com/juanfernandes-rrm/receipt-scan)
- [catalog](https://github.com/juanfernandes-rrm/catalog)
- [register](https://github.com/juanfernandes-rrm/register)
- [social](https://github.com/juanfernandes-rrm/social)
- [auth](https://github.com/juanfernandes-rrm/auth-mvp)

```
nota-social
└─── gateway
└─── receipt-scan
└─── catalog
└─── register
└─── social
└─── auth
```

### 2. Configuração do Docker Compose

O arquivo docker-compose.yml já está configurado para construir a imagem dos serviços e subir os contêineres. Além dos serviços principais, também são levantados os contêineres do RabbitMQ, MySQL e Keycloak.

A única configuração necessária é definir os client-secrets do Keycloak para os serviços Auth e Register. Para isso:

1. Acesse o Keycloak com as credenciais configuradas no docker-compose.yml.
2. No cluster nota-social, navegue até a seção Clients.
3. Gere novos client-secrets para os serviços Auth e Register.
4. Configure cada client-secret na variável de ambiente correspondente:
        KEYCLOAK_CREDENTIALS_SECRET para cada serviço.

Caso necessite, ajuste o arquivo docker-compose.yml. Certifique-se de que todas as imagens e configurações estejam corretas, especialmente as variáveis de ambiente.

### 3.Construir e Iniciar os Contêineres

No diretório `docker/docker` do projeto, execute:

```bash
docker-compose up --build
```

Isso irá construir as imagens e iniciar os contêineres conforme configurado no docker-compose.yml.


### 4. Serviços Disponíveis

- receipt-scan: Responsável por extrair, processar e guardar informações das notas fiscais, como produtos adquiridos, preços, quantidade e identificação da loja. Utiliza técnicas de scraping para extrair os dados.

- catalog: Responsável por processar e armazenar as informações de produtos, como o cadastro dos produtos extraídos das notas fiscais e seus relacionamentos com os estabelecimentos e filiais, atualização e histórico de preços, e outras informações relacionadas. Fornece APIs para consulta destes dados.
    
- register: Responsável por gerenciar informações e operações relacionadas aos usuários, como cadastro e atualização de dados pessoais. Além de realizar o cadastro de estabelecimentos e filiais, associando usuários a estabelecimentos.
    
- social: Responsável por funcionalidades relacionadas a interações sociais entre os usuários. Por exemplo, postagens, comentários, compartilhamentos, conexões entre usuários, etc.
    
- auth: Responsável por gerenciar o processo de autenticação dos usuários no sistema. Ele atua como intermediário entre os clientes e o Keycloak (Gerenciador de identidade e acesso).

### 5. Recursos Adicionais

Uma collection Postman está disponível no projeto, contendo todos os endpoints desenvolvidos para facilitar os testes e a integração com os serviços do sistema.

    
