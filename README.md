# Visualizador e Analisador de Grafos com BFS e DFS

Este projeto implementa um **visualizador interativo de grafos em Java**, utilizando a biblioteca **GraphStream** para renderização gráfica e **Swing** para interface gráfica.

A aplicação permite:

- Criar grafos a partir de uma **lista de arestas**
- Visualizar o grafo dinamicamente
- Executar **BFS (Breadth-First Search)** e **DFS (Depth-First Search)** com animação
- Gerar um **relatório analítico completo do grafo**
- Realizar uma **análise investigativa da rede** (identificação de hubs e amigos a dois passos)

O sistema foi desenvolvido como parte de um trabalho acadêmico de **Teoria dos Grafos / Estruturas de Dados**.

---

# Funcionalidades

## 1️⃣ Visualização do Grafo

O grafo é exibido utilizando **GraphStream**, com layout automático.

Cada vértice possui cores diferentes conforme seu estado:

| Cor | Significado |
|----|----|
| 🟢 Verde | vértice de origem |
| 🔵 Azul | vértice de destino |
| 🟠 Laranja | vértice visitado |
| 🔴 Vermelho | vértices pertencentes ao caminho |

---

# Algoritmos Implementados

## Breadth-First Search (BFS)

O BFS explora o grafo **por níveis**, visitando primeiro todos os vizinhos de um vértice antes de avançar.

### Implementações no projeto

**BFS Animado**
- Mostra a exploração do grafo passo a passo
- Cada vértice visitado muda de cor
- Permite acompanhar visualmente o algoritmo

**BFS Analítico**
- Calcula:
  - ordem de visita
  - distância mínima
  - predecessores
- Permite reconstruir o **menor caminho entre dois vértices**

---

## Depth-First Search (DFS)

O DFS explora o grafo **em profundidade**, avançando o máximo possível antes de retroceder.

### Implementações no projeto

**DFS Animado**
- Mostra a exploração recursiva do grafo
- Cada vértice visitado é destacado visualmente

**DFS Analítico**
- Calcula:
  - ordem de descoberta
  - predecessores
- Detecta **ciclos em grafos direcionados**

---

# Análise Estrutural do Grafo

O sistema gera automaticamente um **relatório completo** contendo:

## Propriedades Fundamentais

- Tipo do grafo
- Ordem |V|
- Tamanho |E|
- Graus dos vértices
- Densidade do grafo

## Conectividade

Para grafos direcionados:

- vértices alcançáveis a partir da origem

Para grafos não direcionados:

- componentes conexas

## Altura do grafo

Calculada como:

```
altura(s) = maior distância a partir do vértice origem via BFS
```

---

# Análise Investigativa da Rede

O projeto inclui uma funcionalidade inspirada em **análise de redes sociais**.

## 🔝 Top-3 Hubs

Identifica os vértices com maior grau (mais conexões).

Exemplo:

```
1º bruno (grau 3)
2º carla (grau 2)
3º julia (grau 2)
```

---

## 👥 Amigos a 2 Passos

Dado um usuário `u`, o sistema encontra:

- amigos de amigos
- que **não são amigos diretos**

Isso simula **recomendações de conexão em redes sociais**.

---

# 🖥 Interface Gráfica

A interface foi construída com **Java Swing**.

## Painel de entrada

Campos disponíveis:

- Origem
- Destino
- Usuário investigativo

---

## Lista de arestas

Entrada textual no formato:

```
verticeA verticeB
```

Exemplo:

```
ana bruno
ana carla
bruno diego
carla fernanda
```

---

## Botões disponíveis

| Botão | Função |
|------|------|
| ▶ BFS animado | executa BFS visual |
| ▶ DFS animado | executa DFS visual |
| 📊 Propriedades + BFS/DFS | gera relatório completo |
| 🔍 Investigativo | hubs + amigos a 2 passos |

---

# 🗂 Estrutura do Projeto

```
Av1.java
README.md
```

Principais métodos do código:

```
bfsAnimado()
bfsAnalitico()

dfsAnimado()
dfsAnalitico()

gerarRelatorioCompleto()

gerarHubs()
gerarAmigos2Passos()

lerGrafo()
construirGraphStream()
```

---

# ⚙️ Tecnologias Utilizadas

- **Java**
- **GraphStream**
- **Java Swing**
- **Java Collections Framework**

---

# 📦 Dependência

Biblioteca necessária:

```
GraphStream
```

Site oficial:

https://graphstream-project.org/

---

# ▶️ Como Executar

## 1️⃣ Clonar o repositório

```
git clone https://github.com/seu-usuario/seu-repositorio.git
```

---

## 2️⃣ Adicionar GraphStream ao projeto

Baixe os JARs:

```
gs-core
gs-ui-swing
```

Adicione-os ao **classpath** do projeto.

---

## 3️⃣ Compilar

```
javac Av1.java
```

---

## 4️⃣ Executar

```
java Av1
```

---

# 📷 Exemplo de Uso

Entrada de arestas:

```
ana bruno
ana carla
bruno diego
carla fernanda
diego gabriel
```

Origem:

```
ana
```

Destino:

```
julia
```

O programa:

- desenha o grafo
- executa BFS ou DFS
- mostra os resultados analíticos no painel

---

# 🎓 Objetivos Educacionais

Este projeto demonstra na prática:

- representação de grafos
- exploração com BFS e DFS
- análise estrutural de grafos
- visualização de algoritmos
- aplicação em redes sociais

---

#  Autor
Vivian Carvalho de Abreu Matos
Projeto desenvolvido para disciplina de **Grafos**.
