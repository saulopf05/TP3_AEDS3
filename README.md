# AJUDA AÍ — Trabalho Prático 2 de AEDs III

Sistema de perguntas e respostas em interface textual, desenvolvido em Java para a disciplina de Algoritmos e Estruturas de Dados III (AEDs III). Este relatório documenta as funcionalidades trabalhadas para o TP2: listagem paginada das perguntas mais recentes, consulta de respostas ordenadas por avaliação e votação.

---

## 1. Participantes

| Participante |
|---|
| Lucas José Souza Rodrigues |
| Luca Maciel |
| Matheus Mendes Senna |
| Saulo |

---

## 2. Vídeo de demonstração

**Link do vídeo:**  

**Descrição:**  

---

## 3. Como compilar e executar

O projeto utiliza Java e não exige bibliotecas externas nem ferramenta de build. É necessário ter um JDK instalado.

**Linux / macOS**

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin Principal
```

**Windows (PowerShell)**

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp bin Principal
```

Os arquivos de dados e índices são criados na pasta `dados/`, relativa ao diretório de onde o programa for executado. Preserve essa pasta caso queira manter os dados locais de teste.

---

## 4. O que o sistema faz

O **AJUDA AÍ** é um sistema textual de perguntas e respostas. Nesta etapa do trabalho, o foco está na consulta das perguntas mais recentes, na visualização das respostas e na avaliação desses conteúdos por meio de votos.

O fluxo implementado para o TP2 é:

1. **Perguntas mais recentes** — apresenta as perguntas ativas em ordem decrescente de data de criação.
2. **Paginação** — exibe cinco perguntas por página e permite avançar ou voltar respeitando os limites da listagem.
3. **Detalhes da pergunta** — permite abrir a pergunta selecionada e consultar suas informações.
4. **Respostas** — apresenta as respostas vinculadas à pergunta, com autor, data, nota e quantidade de avaliações, ordenadas da maior nota para a menor.
5. **Votação** — permite atribuir voto positivo (`+1`) ou negativo (`-1`), atualizando a nota e a quantidade de avaliações. O sistema bloqueia voto no próprio conteúdo e voto duplicado do mesmo usuário no mesmo conteúdo.

---

## 5. Organização do projeto

```text
TP2_AEDS3/
├── src/
│   ├── Principal.java              ponto de entrada
│   ├── aed3/                       estruturas-base fornecidas para o projeto
│   ├── entidades/                  entidades gravadas em arquivo
│   ├── arquivos/                   CRUDs e índices associados
│   ├── indices/                    pares chave-valor usados nos índices
│   └── menus/                      menus e interface textual
├── dados/                          arquivos de registros e índices
├── docs/img/                       capturas de tela
├── bin/                            bytecode (gerado localmente)
└── README.md                       este relatório
```

### 5.1. Classes criadas pelo grupo

| Classe | Pacote | O que faz |
|---|---|---|
| `Principal` | *(default)* | Ponto de entrada da aplicação. |
| `Usuario` | `entidades` | Entidade que representa um usuário. |
| `Pergunta` | `entidades` | Entidade pergunta, incluindo autor, conteúdo, datas, nota e status. |
| `Resposta` | `entidades` | Entidade resposta vinculada a uma pergunta e a um usuário, com nota. |
| `Voto` | `entidades` | Entidade que registra o voto de um usuário em um conteúdo. |
| `ArquivoUsuario` | `arquivos` | CRUD de usuários e índices associados. |
| `ArquivoPergunta` | `arquivos` | CRUD de perguntas e índice cronológico utilizado para listar as perguntas mais recentes. |
| `ArquivoResposta` | `arquivos` | CRUD de respostas e índice de relacionamento entre pergunta e respostas. |
| `ArquivoVoto` | `arquivos` | CRUD de votos e índice de relacionamento entre usuário e votos. |
| `ParEmailID` | `indices` | Par `(email; idUsuario)` utilizado no índice de e-mail. |
| `ParUsuarioPergunta` | `indices` | Par `(idUsuario; idPergunta)` utilizado no relacionamento entre usuários e perguntas. |
| `ParPerguntaResposta` | `indices` | Par `(idPergunta; idResposta)` utilizado no relacionamento entre perguntas e respostas. |
| `ParUsuarioVoto` | `indices` | Par `(idUsuario; idVoto)` utilizado no relacionamento entre usuários e votos. |
| `ParDataPergunta` | `indices` | Chave do índice cronológico usado para consultar perguntas por data de criação. |
| `MenuAcesso` | `menus` | Interface textual de acesso ao sistema. |
| `MenuUsuario` | `menus` | Menus e operações relacionadas ao usuário, incluindo a consulta de perguntas recentes. |
| `MenuPerguntas` | `menus` | Operações de gerenciamento de perguntas. |
| `MenuResposta` | `menus` | Exibição de respostas e fluxo de votação. |
| `Console` | `menus` | Centraliza a leitura da entrada de teclado, compartilhada pelos menus. |
| `Formato` | `menus` | Converte datas e horários das entidades para formato legível. |

### 5.2. Classes fornecidas pelo professor (pacote `aed3`)

O projeto utiliza as estruturas-base do pacote `aed3`, incluindo o CRUD genérico, a Tabela Hash Extensível e a Árvore B+. Essas estruturas dão suporte ao armazenamento dos registros e à manutenção dos índices utilizados pela aplicação.

---

## 6. Estrutura dos arquivos e dos índices

Os registros são armazenados em arquivos próprios, utilizando a estrutura do CRUD genérico. Os índices permitem localizar registros e percorrer os relacionamentos necessários para as consultas.

| Arquivo | Índice direto | Índice indireto / relacionamento |
|---|---|---|
| `dados/usuarios/usuarios.db` | Tabela Hash Extensível: `idUsuario` → endereço | Hash Extensível: `email` → `idUsuario` |
| `dados/perguntas/perguntas.db` | Tabela Hash Extensível: `idPergunta` → endereço | Árvore B+: `(idUsuario; idPergunta)` |
| `dados/perguntas/` | — | Índice cronológico em Árvore B+ para ordenar perguntas pela data de criação |
| `dados/respostas/respostas.db` | Tabela Hash Extensível: `idResposta` → endereço | Árvore B+: `(idPergunta; idResposta)` |
| `dados/votos/votos.db` | Tabela Hash Extensível: `idVoto` → endereço | Árvore B+: `(idUsuario; idVoto)` |

O índice cronológico permite consultar as perguntas da mais recente para a mais antiga sem depender da ordem física em que os registros estão gravados no arquivo. A listagem também considera a situação ativa da pergunta.

---

## 7. Telas do sistema

As capturas de tela serão inseridas posteriormente. Os espaços abaixo foram mantidos para preservar a estrutura do relatório.

### 7.1. Listagem das perguntas mais recentes

**Captura de tela:**  

**Descrição:**  

### 7.2. Navegação entre páginas

**Captura de tela:**  

**Descrição:**  

### 7.3. Limites da paginação

**Captura de tela:**  

**Descrição:**  

### 7.4. Detalhes de uma pergunta selecionada

**Captura de tela:**  

**Descrição:**  

### 7.5. Listagem das respostas de uma pergunta

**Captura de tela:**  

**Descrição:**  

### 7.6. Respostas ordenadas por nota

**Captura de tela:**  

**Descrição:**  

### 7.7. Voto positivo

**Captura de tela:**  

**Descrição:**  

### 7.8. Voto negativo

**Captura de tela:**  

**Descrição:**  

### 7.9. Bloqueio de voto duplicado

**Captura de tela:**  

**Descrição:**  

### 7.10. Bloqueio de voto no próprio conteúdo

**Captura de tela:**  

**Descrição:**  

### 7.11. Persistência após reiniciar o sistema

**Captura de tela:**  

**Descrição:**  

---

## 8. Operações especiais implementadas

### 8.1. Índice cronológico das perguntas

Foi acrescentado um índice baseado na data de criação das perguntas, permitindo percorrer os registros da data mais recente para a mais antiga. A chave do índice é representada pela classe `ParDataPergunta`, utilizada pela `ArquivoPergunta`.

O índice é atualizado quando novas perguntas são criadas. Para permitir a consulta de registros que já existiam antes da criação do índice, a implementação também contempla o preenchimento do índice a partir das perguntas armazenadas.

### 8.2. Paginação das perguntas mais recentes

A listagem apresenta cinco perguntas por página. A navegação permite avançar para a página seguinte ou retornar à anterior, sem ultrapassar o início ou o fim dos resultados disponíveis.

A ordem da listagem é decrescente por data de criação. Ao selecionar uma pergunta, o sistema abre os detalhes correspondentes.

### 8.3. Exibição dos detalhes da pergunta

A tela de detalhes apresenta as informações disponíveis da pergunta selecionada, incluindo autor, conteúdo, palavras-chave, nota, data de criação, data de alteração e status.

### 8.4. Listagem de respostas por avaliação

As respostas associadas à pergunta selecionada são apresentadas em ordem decrescente de nota. A listagem exibe informações como autor, data, nota acumulada e quantidade de avaliações.

### 8.5. Votação positiva e negativa

O sistema registra votos positivos (`+1`) e negativos (`-1`). O voto é armazenado como um registro próprio e a nota acumulada do conteúdo, assim como a quantidade de avaliações, é atualizada.

### 8.6. Validações de votação

Antes de aceitar um voto, o sistema verifica as regras de votação implementadas:

- um usuário não pode votar no próprio conteúdo;
- o mesmo usuário não pode votar mais de uma vez no mesmo conteúdo.

A validação utiliza os registros e índices de votos para identificar se já existe um voto daquele usuário no conteúdo avaliado.

### 8.7. Persistência dos registros e índices

Os dados e índices são armazenados em arquivos na pasta `dados/`. Após encerrar e iniciar novamente a aplicação, os registros persistidos continuam disponíveis. A pasta `dados/` deve ser preservada quando se deseja manter os dados de teste.

---

## 9. Checklist

> **A listagem de perguntas mais recentes está implementada?**

**Sim.** As perguntas ativas são apresentadas em ordem decrescente de data de criação, com cinco registros por página. A navegação entre páginas respeita os limites da listagem.

> **É possível navegar entre as páginas sem ultrapassar os limites?**

**Sim.** A aplicação permite avançar e voltar entre as páginas disponíveis e impede a navegação para antes da primeira página ou depois da última.

> **É possível abrir os detalhes de uma pergunta selecionada?**

**Sim.** A seleção de uma pergunta abre a tela de detalhes com as informações disponíveis do registro.

> **As respostas são apresentadas em ordem decrescente de nota?**

**Sim.** A listagem de respostas exibe primeiro as respostas com maior nota, mostrando autor, data, nota e quantidade de avaliações.

> **A votação positiva e negativa atualiza a nota e a quantidade de avaliações?**

**Sim.** Os votos positivos e negativos são registrados, e a nota e a quantidade de avaliações são atualizadas conforme o voto realizado.

> **O sistema impede voto duplicado e voto no próprio conteúdo?**

**Sim.** As validações bloqueiam um segundo voto do mesmo usuário no mesmo conteúdo e impedem que o autor vote no próprio conteúdo.

> **Os dados continuam disponíveis depois de reiniciar a aplicação?**

**Sim.** Nos testes funcionais realizados, os registros e índices permaneceram disponíveis após reiniciar o sistema. A pasta `dados/` contém os arquivos persistidos.

> **O trabalho compila e executa?**

**Sim.** A implementação foi compilada e executada durante os testes funcionais realizados pelo grupo.

### 9.1. Resumo dos testes funcionais

| Teste | Resultado observado |
|---|---|
| Listagem com cinco perguntas por página | Funcionou |
| Ordenação das perguntas da mais recente para a mais antiga | Funcionou |
| Navegação para a página seguinte e retorno à anterior | Funcionou |
| Bloqueio de navegação antes da primeira e depois da última página | Funcionou |
| Abertura dos detalhes da pergunta selecionada | Funcionou |
| Exibição de autor, data, nota e quantidade de avaliações das respostas | Funcionou |
| Ordenação das respostas por nota decrescente | Funcionou |
| Voto positivo e atualização da nota/quantidade | Funcionou |
| Voto negativo e atualização da nota/quantidade | Funcionou |
| Bloqueio de voto duplicado | Funcionou |
| Persistência após reiniciar a aplicação | Funcionou |
| Inclusão de nova pergunta e exibição na listagem de recentes | Funcionou |
