# gateway

Este projeto é o ***BFF*** mobile do sistema **NotaSocial**, desenvolvido para a disciplina de **Trabalho de Conclusão do Curso(TCC)**. Ele coordena as requisições aos serviços: ***receipt-scan, catalog, register, e social***.
Este readme descreve como configurar e executar o sistama a partir do ***customer-bff**.

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
project
└─── gateway
└─── receipt-scan
└─── catalog
└─── register
└─── social
└─── auth
```

### 2. Configuração do Docker Compose

O arquivo docker-compose.yml já está configurado para construir a imagem dos serviços e subir os contêiner. Além dos serviços, também é levantado os contêineres do RabbitMQ e Mysql.
Caso necessite, ajuste o arquivo docker-compose.yml. Certifique-se de que todas as imagens e configurações estejam corretas, especialmente as variáveis de ambiente.

### 3.Construir e Iniciar os Contêineres

No diretório raiz do projeto, execute:

```bash
docker-compose up --build
```

Isso irá construir as imagens e iniciar os contêineres conforme configurado no docker-compose.yml.


### 4. Serviços Disponíveis

    receipt-scan: Responsável por ...
    catalog: Responsável por ...
    register: Responsável por ...
    social: Responsável por ...
    auth: Responsável por ...

### 5. Documentação dos Endpoints

No projeto, está disponibilizado uma collection postman.


    receipt-scan: Documentação dos endpoints
    catalog: Documentação dos endpoints
    register: Documentação dos endpoints
    social: Documentação dos endpoints
    gateway: Documentação dos endpoints
    auth: Documentação dos endpoints

    
