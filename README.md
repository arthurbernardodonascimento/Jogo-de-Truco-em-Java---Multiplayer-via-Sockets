# Truco Java

Jogo de Truco para dois jogadores, com comunicação por sockets TCP e clientes de terminal e interface gráfica Swing.

## Estrutura

```text
TrucoJava/
├── README.md
└── src/
    └── truco/
        ├── modelo/
        │   ├── Carta.java
        │   ├── Baralho.java
        │   └── Jogador.java
        ├── servidor/
        │   ├── Main.java
        │   └── Jogo.java
        └── cliente/
            ├── Cliente.java
            └── ClienteGUI.java
```

## Requisitos

- JDK 8 ou superior (com `javac` e `java` no PATH).
- Porta TCP 12345 disponível no computador que executará o servidor.
- Não há dependências externas.

## Compilar

Abra o terminal na pasta `TrucoJava`.

Windows (PowerShell):

```powershell
$fontes = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $fontes
```

Linux/macOS:

```bash
javac -encoding UTF-8 -d out $(find src -name '*.java')
```

## Configurar o endereço do servidor

O IP original dos clientes é `192.168.0.109`. Se os jogadores estiverem no mesmo computador do servidor, altere esse endereço para `127.0.0.1` em `src/truco/cliente/Cliente.java` e `src/truco/cliente/ClienteGUI.java` antes de compilar. Se estiverem em computadores diferentes na mesma rede, use o IP local do computador do servidor e permita a conexão TCP na porta 12345 no firewall, se necessário.

A mensagem exibida no console do servidor contém um IP fixo do código original; ela não detecta automaticamente o IP real. O servidor escuta na porta 12345.

## Executar

1. Inicie o servidor em um terminal:

```sh
java -cp out truco.servidor.Main
```

2. Em outros dois terminais, inicie um cliente para cada jogador. Escolha a interface gráfica:

```sh
java -cp out truco.cliente.ClienteGUI
```

Ou use o cliente de terminal:

```sh
java -cp out truco.cliente.Cliente
```

O servidor aguarda dois clientes antes de iniciar a partida. Ambos os tipos de cliente podem ser usados juntos.

## IDE

No IntelliJ IDEA, Eclipse ou VS Code, abra a pasta do projeto, configure `src` como raiz dos fontes se necessário e execute a classe `truco.servidor.Main`, seguida de duas instâncias de um dos clientes.

## Alterações realizadas

Os sete arquivos originais foram renomeados para corresponder às classes públicas, separados em pacotes por responsabilidade e tiveram imports e espaçamento ajustados. As regras de Truco, a interface Swing, o protocolo de mensagens e os endereços originais foram preservados. Não foram realizadas correções funcionais na lógica do jogo.
