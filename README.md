# AJUDA AÍ — Trabalho Prático 3 de AEDs III

Sistema de perguntas e respostas em interface textual, desenvolvido em Java para a disciplina de Algoritmos e Estruturas de Dados III (AEDs III). Este relatório documenta o TP3: a construção do **índice invertido** com as palavras-chave das perguntas e a **busca de perguntas por palavras-chave**, com os resultados classificados pelo modelo **TF×IDF**.

VIDEO DE APRESENTAÇÃO: https://youtu.be/n4wAUw11wJk
---

## 1. Participantes

| Participantes |
| Lucas José Souza Rodrigues |
| Luca Maciel |
| Matheus Mendes Senna |
| Saulo Peixoto|

---

## 2. Vídeo de demonstração

**Link do vídeo:**  

**Descrição:** o vídeo (até 5 minutos) mostra:

1. a busca de perguntas usando mais de uma palavra-chave;
2. a busca de uma pergunta antes e depois da alteração das suas palavras-chave;
3. o código responsável pelo ranking das perguntas (método `buscar()` da classe `busca.IndiceInvertido`), comprovando o uso do modelo TF×IDF.

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

O **AJUDA AÍ** é um fórum textual de perguntas e respostas. Depois de criar uma conta e fazer login, o usuário pode:

- **gerenciar os próprios dados** (nome, e-mail, senha e pergunta de recuperação de senha);
- **gerenciar as próprias perguntas**: listar, incluir, alterar (texto e palavras-chave) e arquivar;
- **consultar as perguntas mais recentes**, cinco por página, e abrir os detalhes de cada uma;
- **responder perguntas** e consultar as respostas ordenadas pela nota;
- **votar** (`+1` ou `-1`) em perguntas e respostas de outros usuários, sem voto duplicado e sem voto no próprio conteúdo;
- **buscar perguntas por palavras-chave** *(TP3)*.

### 4.1. Busca por palavras-chave (TP3)

Toda pergunta tem um campo de palavras-chave (por exemplo, `Java; árvore B+; índice`). Essas palavras são transformadas em termos e guardadas em um índice invertido. Cada termo aponta para a lista das perguntas que o contêm, junto com a frequência do termo em cada uma delas (TF).

No menu **Início > Buscar perguntas > (B) Busca por palavras-chave**, o usuário digita um ou mais termos. O sistema aplica aos termos digitados o mesmo tratamento dado às palavras-chave, consulta a lista de cada termo e calcula a relevância de cada pergunta pelo modelo TF×IDF. As perguntas são então apresentadas da mais relevante para a menos relevante, de 10 em 10, e qualquer uma delas pode ser aberta para ver detalhes, respostas e votar.

O índice é mantido sempre atualizado: ele muda quando uma pergunta é incluída, quando suas palavras-chave são alteradas e quando ela é arquivada ou excluída.

---

## 5. Organização do projeto

```text
TP3_AEDS3/
├── src/
│   ├── Principal.java              ponto de entrada
│   ├── aed3/                       estruturas-base fornecidas pelo professor
│   ├── busca/                      tratamento de termos e índice invertido (TP3)
│   ├── entidades/                  entidades gravadas em arquivo
│   ├── arquivos/                   CRUDs e índices associados
│   ├── indices/                    pares chave-valor usados nas Árvores B+ e Hash
│   └── menus/                      menus e interface textual
├── dados/                          arquivos de registros e índices
├── docs/img/                       capturas de tela
├── bin/                            bytecode (gerado localmente)
└── README.md                       este relatório
```

### 5.1. Classes criadas pelo grupo

As classes criadas ou alteradas no TP3 estão marcadas com **(TP3)**.

| Classe | Pacote | O que faz |
|---|---|---|
| `ProcessadorTermos` **(TP3)** | `busca` | Transforma um texto no vetor de termos: minúsculas, sem acentos, separado em palavras e sem stop words e numerais. É usada tanto na indexação quanto na consulta. |
| `IndiceInvertido` **(TP3)** | `busca` | Índice invertido construído sobre a `ListaInvertida`. Insere e remove perguntas (gravando o TF de cada termo) e faz a busca com ranking TF×IDF. |
| `ArquivoPergunta` **(TP3)** | `arquivos` | CRUD de perguntas, índice cronológico e, no TP3, a manutenção do índice invertido das palavras-chave em `create`, `update` e `delete`, além do método `buscarPorPalavrasChave()`. |
| `MenuUsuario` **(TP3)** | `menus` | Menus do usuário. No TP3 ganhou a tela de busca por palavras-chave, com paginação de 10 em 10 e exibição do valor TF×IDF. |
| `Principal` | *(default)* | Ponto de entrada da aplicação. |
| `Usuario` | `entidades` | Entidade que representa um usuário. |
| `Pergunta` | `entidades` | Entidade pergunta: autor, texto, palavras-chave, datas, nota e status. |
| `Resposta` | `entidades` | Entidade resposta vinculada a uma pergunta e a um usuário, com nota. |
| `Voto` | `entidades` | Entidade que registra o voto de um usuário em um conteúdo. |
| `ArquivoUsuario` | `arquivos` | CRUD de usuários e índice de e-mail. |
| `ArquivoResposta` | `arquivos` | CRUD de respostas e índice de relacionamento entre pergunta e respostas. |
| `ArquivoVoto` | `arquivos` | CRUD de votos e índice de relacionamento entre usuário e votos. |
| `ParEmailID` | `indices` | Par `(email; idUsuario)` utilizado no índice de e-mail. |
| `ParUsuarioPergunta` | `indices` | Par `(idUsuario; idPergunta)` do relacionamento entre usuários e perguntas. |
| `ParPerguntaResposta` | `indices` | Par `(idPergunta; idResposta)` do relacionamento entre perguntas e respostas. |
| `ParUsuarioVoto` | `indices` | Par `(idUsuario; idVoto)` do relacionamento entre usuários e votos. |
| `ParDataPergunta` | `indices` | Chave do índice cronológico das perguntas. |
| `MenuAcesso` | `menus` | Login, cadastro e recuperação de senha. |
| `MenuPerguntas` | `menus` | Gerenciamento das perguntas do usuário. |
| `MenuResposta` | `menus` | Exibição de respostas e fluxo de votação. |
| `Console` | `menus` | Entrada de teclado compartilhada pelos menus. |
| `Formato` | `menus` | Converte datas e horários para formato legível. |

### 5.2. Classes fornecidas pelo professor (pacote `aed3`)

| Classe | Uso no projeto |
|---|---|
| `ListaInvertida` e `ElementoLista` **(TP3)** | Base do índice invertido das palavras-chave. Foram incluídas sem alterações. |
| `Arquivo` | CRUD genérico com índice direto. |
| `HashExtensivel`, `RegistroHashExtensivel`, `ParIDEndereco` | Índice direto e índice de e-mail. |
| `ArvoreBMais`, `InterfaceArvoreBMais` | Índices de relacionamento e índice cronológico. |
| `Registro` | Interface das entidades. |

---

## 6. Estrutura dos arquivos e dos índices

| Arquivo | Índice direto | Índice indireto / relacionamento |
|---|---|---|
| `dados/usuarios/usuarios.db` | Hash Extensível: `idUsuario` → endereço | Hash Extensível: `email` → `idUsuario` |
| `dados/perguntas/perguntas.db` | Hash Extensível: `idPergunta` → endereço | Árvore B+: `(idUsuario; idPergunta)` e Árvore B+ cronológica `(criação; idPergunta)` |
| `dados/perguntas/indicePalavrasChave.dic.db` e `.blocos.db` **(TP3)** | — | Lista invertida: `termo` → `[(idPergunta; TF), ...]`, mais o total de perguntas indexadas |
| `dados/respostas/respostas.db` | Hash Extensível: `idResposta` → endereço | Árvore B+: `(idPergunta; idResposta)` |
| `dados/votos/votos.db` | Hash Extensível: `idVoto` → endereço | Árvore B+: `(idUsuario; idVoto)` |

No índice invertido, o arquivo de **dicionário** guarda, no cabeçalho, o número de perguntas indexadas (`numeroEntidades()`) e, em seguida, cada termo com o endereço do seu primeiro bloco. O arquivo de **blocos** guarda os pares `(idPergunta; TF)`, cinco por bloco, com encadeamento para blocos seguintes quando a lista cresce.

---

## 7. Telas do sistema

As telas abaixo são a saída real do programa em um teste com as quatro perguntas do exemplo do enunciado, cadastradas junto com outras seis perguntas já existentes (10 perguntas ativas no total).

### 7.1. Busca com mais de uma palavra-chave

```text
Busca por palavras-chave
------------------------
Palavras-chave (vazio para cancelar): Inteligência Artificial
Termos considerados: inteligencia, artificial

Resultados — página 1 de 1 (3 pergunta(s) encontrada(s))
----------------------------------------

(1) 08/11/2026 19:19 | Relevância (TF x IDF): 0,407
O que é IA?
Autor: Usuario Teste
Palavras-chave: Introdução à Inteligência Artificial

(2) 08/11/2026 19:19 | Relevância (TF x IDF): 0,349
IA no trabalho?
Autor: Usuario Teste
Palavras-chave: Inteligência no Trabalho por Meio da Inteligência Artificial

(3) 08/11/2026 19:19 | Relevância (TF x IDF): 0,174
Como controlar emoções?
Autor: Usuario Teste
Palavras-chave: Inteligência Emocional para Gestores

N - Próxima página | P - Página anterior | R - Voltar
Selecione o número da pergunta ou uma opção:
```

A pergunta "Como gerir equipes?" (palavras-chave `Introdução à Gestão de Equipes`) não aparece, porque não tem nenhum dos termos buscados.

### 7.2. Alteração das palavras-chave e nova busca

A pergunta "Como gerir equipes?" teve as palavras-chave alteradas para `Inteligência Artificial para Gestão`, e a pergunta "Como controlar emoções?" foi arquivada:

```text
Alterar pergunta
----------------
Digite o número da pergunta que deseja alterar (0 para cancelar): 4

Pergunta atual: Como gerir equipes?
Nova pergunta (deixe vazio para não alterar):
Palavras-chave atuais: Introdução à Gestão de Equipes
Novas palavras-chave (deixe vazio para não alterar): Inteligência Artificial para Gestão

Pergunta alterada com sucesso!
```

Repetindo a mesma busca, a pergunta alterada passa a ser encontrada e a pergunta arquivada sai dos resultados. Os valores mudam porque agora são 9 perguntas ativas e os termos aparecem em 3 delas:

```text
Resultados — página 1 de 1 (3 pergunta(s) encontrada(s))
----------------------------------------

(1) 08/11/2026 19:19 | Relevância (TF x IDF): 0,318
O que é IA?
Autor: Usuario Teste
Palavras-chave: Introdução à Inteligência Artificial

(2) 08/11/2026 19:19 | Relevância (TF x IDF): 0,318
Como gerir equipes?
Autor: Usuario Teste
Palavras-chave: Inteligência Artificial para Gestão

(3) 08/11/2026 19:19 | Relevância (TF x IDF): 0,286
IA no trabalho?
Autor: Usuario Teste
Palavras-chave: Inteligência no Trabalho por Meio da Inteligência Artificial
```

### 7.3. Busca apenas com stop words

```text
Palavras-chave (vazio para cancelar): de para com 2

Nenhum termo válido na busca (apenas stop words ou números).
```

---

## 8. Operações especiais implementadas

### 8.1. Tratamento dos termos (`busca.ProcessadorTermos`)

O mesmo tratamento é aplicado às palavras-chave cadastradas e aos termos digitados na busca:

1. o texto é passado para letras minúsculas;
2. acentos e cedilha são removidos com `java.text.Normalizer` (decomposição NFD e remoção das marcas diacríticas);
3. o texto é quebrado em palavras: qualquer caractere que não seja letra ou dígito separa termos, inclusive o `;` usado entre as palavras-chave;
4. as **stop words** e os **numerais** são descartados.

Exemplo: `"Inteligência no Trabalho por Meio da Inteligência Artificial"` → `[inteligencia, trabalho, meio, inteligencia, artificial]`.

A lista de stop words parte das palavras vazias usuais do português (artigos, preposições, contrações, pronomes, conjunções, advérbios e verbos auxiliares muito frequentes e numerais por extenso). Ela foi **adaptada ao domínio** do AJUDA AÍ com palavras comuns em fóruns de perguntas que não ajudam a diferenciar uma pergunta da outra, como `pergunta`, `duvida`, `ajuda`, `alguem`, `pessoal`, `obrigado` e `ola`. Números escritos com dígitos (`2`, `2024`) também são descartados, por serem numerais.

### 8.2. Construção do índice invertido (`busca.IndiceInvertido.inserir`)

Para cada pergunta, calcula-se o vetor de termos e cada ocorrência vale `1/n`, sendo `n` o total de termos válidos. Como a `ListaInvertida` não aceita o mesmo id duas vezes na lista de um termo, o código segue o que o enunciado pede:

```java
ElementoLista e = lista.read(termo, id);
if (e == null)
    lista.create(termo, new ElementoLista(id, frequencia));
else
    lista.update(termo, new ElementoLista(id, frequencia + e.getFrequencia()));
```

Ao fim da inserção, `incrementaEntidades()` registra mais uma pergunta, valor que é usado no cálculo do IDF. Com os quatro livros do enunciado, o índice gerado pelo sistema é exatamente o esperado (valores arredondados para três casas):

```text
introducao: (1;0.333) (4;0.333)
inteligencia: (1;0.333) (2;0.333) (3;0.4)
artificial: (1;0.333) (3;0.2)
emocional: (2;0.333)
gestores: (2;0.333)
trabalho: (3;0.2)
meio: (3;0.2)
gestao: (4;0.333)
equipes: (4;0.333)
```

### 8.3. Atualização do índice (`arquivos.ArquivoPergunta`)

O índice é mantido pelo próprio CRUD de perguntas, então nenhum menu precisa se preocupar com ele:

| Operação | Efeito no índice invertido |
|---|---|
| `create` | Insere os termos da nova pergunta e incrementa o total de perguntas. |
| `update` com palavras-chave alteradas | Remove o id da lista de cada termo antigo, decrementa o total e insere os termos novos, incrementando-o de novo. |
| `update` que arquiva a pergunta | Remove a pergunta do índice e decrementa o total. Perguntas arquivadas não aparecem na busca nem contam no IDF. |
| `update` sem mudança de palavras-chave ou de status (um voto, por exemplo) | Não altera o índice. |
| `delete` | Remove a pergunta do índice e decrementa o total. |

Se os arquivos do índice ainda não existirem, como na primeira execução do TP3 com a pasta `dados/` dos trabalhos anteriores, o índice é construído automaticamente a partir das perguntas ativas já gravadas.

### 8.4. Busca e ranking TF×IDF (`busca.IndiceInvertido.buscar`)

Este é o código responsável pelo ranking das perguntas:

```java
for (String termo : termos) {
    ElementoLista[] ocorrencias = lista.read(termo);
    if (ocorrencias.length == 0)
        continue;

    double idf = Math.log10((double) totalEntidades / ocorrencias.length);
    for (ElementoLista e : ocorrencias)
        pontuacao.merge(e.getId(), e.getFrequencia() * idf, Double::sum);
}
```

Para cada termo da consulta:

- **IDF** = `log10(N / df)`, sendo `N` o total de perguntas indexadas (`numeroEntidades()`) e `df` o tamanho da lista do termo;
- cada par `(id; TF)` da lista contribui com **TF × IDF** para a pergunta `id`;
- as contribuições de uma mesma pergunta são somadas, e o resultado é ordenado do maior para o menor valor.

Com os quatro livros do enunciado e a consulta `"Inteligência Artificial"`, o sistema produz `[(1; 0.142), (3; 0.110), (2; 0.042)]`, isto é, a ordem `[1, 3, 2]`, exatamente como no exemplo do enunciado.

Detalhes adotados:

- termos repetidos na consulta (`"java java"`) contam uma única vez;
- em caso de empate no valor TF×IDF, a pergunta de menor id aparece primeiro;
- uma pergunta que contém um termo presente em **todas** as perguntas aparece no resultado, mas com valor 0 para esse termo (`log10(1) = 0`).

### 8.5. Apresentação dos resultados (`menus.MenuUsuario.buscarPorPalavrasChave`)

A tela de busca mostra os termos que foram considerados após o tratamento, o total de perguntas encontradas e os resultados de **10 em 10**, com a posição no ranking e o valor TF×IDF de cada pergunta. É possível avançar e voltar páginas, sem ultrapassar os limites, e abrir qualquer pergunta para ver detalhes, respostas e votar. Ao sair dos detalhes, o usuário volta para a mesma página de resultados.

### 8.6. Funcionalidades das etapas anteriores

Continuam disponíveis a listagem paginada das perguntas mais recentes (índice cronológico em Árvore B+), as respostas ordenadas por nota e a votação positiva/negativa com bloqueio de voto duplicado e de voto no próprio conteúdo. A tela de detalhes da pergunta passou a ser compartilhada entre a listagem das recentes e a busca por palavras-chave.

---

## 9. Checklist

> **O índice invertido com palavras-chave foi criado usando a classe ListaInvertida?**

**Sim.** A classe `busca.IndiceInvertido` usa a `aed3.ListaInvertida` fornecida pelo professor, sem alterações, para guardar os pares `(idPergunta; TF)` de cada termo das palavras-chave. Ela também usa os métodos `incrementaEntidades()`, `decrementaEntidades()` e `numeroEntidades()` para manter o total de perguntas usado no IDF.

> **É possível buscar perguntas por palavras-chave no menu de buscar perguntas?**

**Sim.** Em **Início > Buscar perguntas > (B) Busca por palavras-chave**.

> **As perguntas retornadas estão corretamente classificadas pelo modelo TFxIDF?**

**Sim.** A relevância de cada pergunta é a soma de TF × log10(N/df) dos termos buscados, e os resultados são ordenados do maior para o menor valor. Reproduzindo o exemplo do enunciado, o sistema chegou às mesmas listas invertidas e ao mesmo ranking `[(1; 0.142), (3; 0.110), (2; 0.042)]`.

> **O trabalho compila corretamente?**

**Sim.** O projeto compila sem erros nem avisos com `javac -encoding UTF-8 -d bin $(find src -name "*.java")` (testado com o JDK 21).

> **O trabalho está completo e funcionando sem erros de execução?**

**Sim.** Todas as operações pedidas foram implementadas e testadas (ver a tabela abaixo), sem erros de execução.

> **O trabalho é original e não a cópia de um trabalho de outro grupo?**

**Sim.** O trabalho é original e foi desenvolvido pelo grupo; não é cópia do trabalho de outro grupo.

### 9.1. Resumo dos testes funcionais

| Teste | Resultado observado |
|---|---|
| Listas invertidas do exemplo do enunciado (4 livros) | Iguais às do enunciado |
| Ranking da consulta "Inteligência Artificial" no exemplo do enunciado | `[1, 3, 2]`, com os mesmos valores |
| Construção do índice a partir das perguntas já existentes em `dados/` | Funcionou (perguntas arquivadas ficam de fora) |
| Inclusão de pergunta e busca pelas suas palavras-chave | Funcionou |
| Busca com mais de uma palavra-chave | Funcionou |
| Alteração das palavras-chave: termos antigos deixam de encontrar a pergunta e os novos passam a encontrar | Funcionou |
| Arquivamento: pergunta deixa de aparecer na busca e o total de perguntas do IDF diminui | Funcionou |
| Voto em pergunta não altera o índice | Funcionou |
| Busca só com stop words / números | Mensagem de nenhum termo válido |
| Busca sem resultados | Mensagem de nenhuma pergunta encontrada |
| Paginação de 10 em 10 com 12 resultados, incluindo os limites da primeira e da última página | Funcionou |
| Abertura dos detalhes de uma pergunta a partir da busca | Funcionou |
| Persistência do índice após reiniciar a aplicação | Funcionou |
