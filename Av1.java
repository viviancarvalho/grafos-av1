import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.swing_viewer.SwingViewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import java.util.*;

public class Av1 {
    static Graph graph;
    static View view;

    // ====================== STYLESHEET ESTÁTICO ======================
    static String style =
            "node {" +
                    " size: 20px;" +
                    " fill-color: lightgray;" +
                    " stroke-mode: plain;" +
                    " stroke-color: black;" +
                    " text-size: 14;" +
                    " text-alignment: above;" +
                    "}" +
                    "node.visitado {" +
                    " fill-color: orange;" +
                    "}" +
                    "node.origem {" +
                    " fill-color: green;" +
                    "}" +
                    "node.destino {" +
                    " fill-color: blue;" +
                    "}" +
                    "node.caminho {" +
                    " fill-color: red;" +
                    "}" +
                    "edge {" +
                    " arrow-size: 10px, 5px;" +
                    "}";

    // ====================== BFS ANIMADO ======================
    public static void bfsAnimado(Graph graph, Map<String, List<String>> G, String origem, String destino) throws Exception {

        final int INF = Integer.MAX_VALUE;

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> fila = new ArrayDeque<>();

        for (String u : G.keySet()) {
            dist.put(u, INF);
            pred.put(u, null);
        }

        visited.add(origem);
        dist.put(origem, 0);
        fila.add(origem);

        while (!fila.isEmpty()) {

            String u = fila.remove();
            Node node = graph.getNode(u);

            if (node != null) {
                node.setAttribute("ui.style", "fill-color: orange;");
            }

            Thread.sleep(800);

            if (u.equals(destino)) {
                Node dest = graph.getNode(u);
                if (dest != null) {
                    dest.setAttribute("ui.style", "fill-color: blue;");
                }
                return;
            }

            for (String v : G.getOrDefault(u, Collections.emptyList())) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    dist.put(v, dist.get(u) + 1);
                    pred.put(v, u);
                    fila.add(v);
                }
            }
        }
    }

    // ====================== BFS ANALITICO ======================
    static class ResultadoBFS {
        List<String> ordem;
        Map<String, Integer> dist;
        Map<String, String> pred;

        ResultadoBFS(List<String> o, Map<String, Integer> d, Map<String, String> p) {
            ordem = o;
            dist = d;
            pred = p;
        }
    }

    static ResultadoBFS bfsAnalitico(Map<String, List<String>> G, String origem) {
        final int INF = Integer.MAX_VALUE;

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> fila = new ArrayDeque<>();
        List<String> ordem = new ArrayList<>();

        for (String atual : G.keySet()) {
            dist.put(atual, INF);
            pred.put(atual, null);
        }

        visitados.add(origem);
        dist.put(origem, 0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            String atual = fila.remove();
            ordem.add(atual);

            for (String vizinho : G.get(atual)) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    dist.put(vizinho, dist.get(atual) + 1);
                    pred.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }
        return new ResultadoBFS(ordem, dist, pred);
    }

    static List<String> reconstruirCaminho(Map<String, String> pred, String destino) {
        List<String> path = new ArrayList<>();

        if (pred.get(destino) == null) return path;

        String atual = destino;

        while (atual != null) {
            path.add(atual);
            atual = pred.get(atual);
        }

        Collections.reverse(path);
        return path;
    }

    // ====================== DFS ANIMADO ======================
    private static int timer;

    public static void dfsAnimado(Graph graph, Map<String, List<String>> G, String origem, String destino, Set<String> visitados) throws Exception {
        timer = 0;

        Map<String, Integer> tin = new HashMap<>();
        Map<String, Integer> tout = new HashMap<>();
        Map<String, String> pred = new HashMap<>();

        pred.put(origem, null);
        dfsVisitAnimado(graph, G, origem, destino, visitados, tin, tout, pred);
    }

    private static void dfsVisitAnimado(Graph graph, Map<String, List<String>> G, String u, String destino,
                                        Set<String> visitados, Map<String, Integer> tin,
                                        Map<String, Integer> tout, Map<String, String> pred) throws Exception {

        visitados.add(u);
        tin.put(u, ++timer);

        Node node = graph.getNode(u);
        if (node != null) {
            node.setAttribute("ui.style", "fill-color: orange;");
        }

        Thread.sleep(1000);

        if (u.equals(destino)) {
            Node dest = graph.getNode(u);
            if (dest != null) {
                dest.setAttribute("ui.style", "fill-color: blue;");
            }
            return;
        }

        for (String v : G.getOrDefault(u, Collections.emptyList())) {
            if (!visitados.contains(v)) {
                pred.put(v, u);
                dfsVisitAnimado(graph, G, v, destino, visitados, tin, tout, pred);
            }
        }

        tout.put(u, ++timer);
    }

    // ====================== DFS ANALITICO ======================
    static class ResultadoDFS {
        List<String> ordem;
        Map<String, String> pred;
        boolean temCiclo;

        ResultadoDFS(List<String> o, Map<String, String> p, boolean c) {
            ordem = o;
            pred = p;
            temCiclo = c;
        }
    }

    static ResultadoDFS dfsAnalitico(Map<String, List<String>> G, String origem, String destino) {
        Set<String> visitados = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> ordem = new ArrayList<>();
        Map<String, String> pred = new HashMap<>();

        boolean[] ciclo = new boolean[1];

        dfsVisit(G, origem, visitados, recStack, ordem, pred, ciclo, destino);

        for (String u : G.keySet()) {
            if (!visitados.contains(u)) {
                dfsVisit(G, u, visitados, recStack, ordem, pred, ciclo, destino);
            }
        }
        return new ResultadoDFS(ordem, pred, ciclo[0]);
    }

    static void dfsVisit(Map<String, List<String>> G, String u, Set<String> visitados, Set<String> recStack, List<String> ordem,
                         Map<String, String> pred, boolean[] ciclo, String destino) {

        visitados.add(u);
        recStack.add(u);
        ordem.add(u);

        for (String v : G.get(u)) {
            if (!visitados.contains(v)) {
                pred.put(v, u);
                dfsVisit(G, v, visitados, recStack, ordem, pred, ciclo, destino);
            } else if (recStack.contains(v)) {
                ciclo[0] = true;
            }
        }
        recStack.remove(u);
    }

    // ====================== PROPRIEDADES FUNDAMENTAIS — retorna String ======================
    static String gerarInfoGrafo(Map<String, List<String>> G, boolean direcionado, String origem) {
        StringBuilder sb = new StringBuilder();

        int vertices = G.size();
        int arestas = 0;

        for (String u : G.keySet()) {
            arestas += G.get(u).size();
        }

        if (!direcionado) {
            arestas /= 2;
        }

        sb.append("=== PROPRIEDADES FUNDAMENTAIS ===\n");
        sb.append("Grafo ").append(direcionado ? "Direcionado" : "Não-direcionado").append("\n");
        sb.append("|V| = ").append(Math.abs(vertices)).append(", |E| = ").append(Math.abs(arestas)).append("\n\n");

        sb.append("--- Graus ---\n");

        for (String u : G.keySet()) {
            if (direcionado) {
                int grauSaida = G.get(u).size();
                int grauEntrada = 0;

                for (String v : G.keySet()) {
                    if (G.get(v).contains(u)) grauEntrada++;
                }

                sb.append(u).append(": entrada=").append(grauEntrada).append(", saída=").append(grauSaida).append("\n");
            } else {
                sb.append(u).append(": grau=").append(G.get(u).size()).append("\n");
            }
        }

        double densidade;

        if (direcionado) {
            densidade = (double) Math.abs(arestas) / (Math.abs(vertices) * Math.abs(vertices - 1));
        } else {
            densidade = (double) Math.abs(arestas * 2) / (Math.abs(vertices) * Math.abs(vertices - 1));
        }

        sb.append("\nDensidade = ").append(String.format("%.4f", densidade)).append("\n");

        // Conectividade
        sb.append("\n--- Conectividade ---\n");

        if (direcionado) {
            ResultadoBFS resultadoBFS = bfsAnalitico(G, origem);
            List<String> alcancaveis = new ArrayList<>();

            for (String vizinho : G.keySet()) {
                if (resultadoBFS.dist.get(vizinho) != Integer.MAX_VALUE && !vizinho.equals(origem)) {
                    alcancaveis.add(vizinho);
                }
            }

            sb.append("Alcançáveis a partir de ").append(origem).append(": ").append(alcancaveis).append("\n");
        } else {
            Set<String> visitados = new HashSet<>();
            int comp = 0;

            for (String atual : G.keySet()) {
                if (!visitados.contains(atual)) {
                    comp++;
                    Queue<String> fila = new ArrayDeque<>();
                    fila.add(atual);
                    visitados.add(atual);
                    List<String> membros = new ArrayList<>();

                    while (!fila.isEmpty()) {
                        String u = fila.remove();
                        membros.add(u);

                        for (String vizinho : G.get(u)) {
                            if (!visitados.contains(vizinho)) {
                                visitados.add(vizinho);
                                fila.add(vizinho);
                            }
                        }
                    }
                    sb.append("Componente ").append(comp).append(": ").append(membros).append("\n");
                }
            }
            sb.append(comp == 1 ? "O grafo é conexo.\n" : "O grafo é NÃO conexo.\n");
        }

        // Altura
        ResultadoBFS resultadoBFS = bfsAnalitico(G, origem);
        int altura = 0;

        for (int d : resultadoBFS.dist.values()) {
            if (d != Integer.MAX_VALUE && d > altura) {
                altura = d;
            }
        }
        sb.append("\nAltura a partir de ").append(origem).append(": ").append(altura).append("\n");
        sb.append("=================================\n");

        return sb.toString();
    }

    // ====================== PARTE INVESTIGATIVA — retorna String ======================
    static String gerarHubs(Map<String, List<String>> G) {

        List<String> vertices = new ArrayList<>(G.keySet());
        vertices.sort((a, b) -> G.get(b).size() - G.get(a).size());

        StringBuilder sb = new StringBuilder();
        sb.append("=== TOP-3 HUBS ===\n");

        List<String> top3 = vertices.subList(0, Math.min(3, vertices.size()));

        for (int i = 0; i < top3.size(); i++) {
            String v = top3.get(i);
            sb.append((i + 1)).append("º ").append(v).append(" — grau ").append(G.get(v).size()).append("\n");
        }

        sb.append("==================\n");
        return sb.toString();
    }

    static String gerarAmigos2Passos(Map<String, List<String>> G, String user) {
        if (!G.containsKey(user)) {
            return "Vértice \"" + user + "\" não encontrado no grafo.\n";
        }

        Set<String> amigos2 = new HashSet<>();

        for (String u : G.get(user)) {
            if (!G.containsKey(u))  {
                continue;
            }
            for (String v : G.get(u)) {
                if (!v.equals(user) && !G.get(user).contains(v)) {
                    amigos2.add(v);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== AMIGOS A 2 PASSOS DE ").append(user.toUpperCase()).append(" ===\n");

        if (amigos2.isEmpty()) {
            sb.append("Nenhum.\n");
        } else {
            for (String a : amigos2) {
                sb.append("• ").append(a).append("\n");
            }
        }
        sb.append("=====================================\n");
        return sb.toString();
    }

    // ====================== MAIN ======================
    public static void main(String[] args) throws Exception {

        System.setProperty("org.graphstream.ui", "swing");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("Visualizador de Grafos");
        frame.setSize(1200, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ===== PAINEL CONTROLE =====
        JPanel controle = new JPanel();
        controle.setLayout(new BorderLayout());
        controle.setPreferredSize(new Dimension(380, 650));

        JPanel inputs = new JPanel(new GridLayout(6, 1));

        JTextField origem = new JTextField("ana");
        JTextField destino = new JTextField("julia");
        JTextField userInvestigativo = new JTextField("ana");

        inputs.add(new JLabel("Origem"));
        inputs.add(origem);
        inputs.add(new JLabel("Destino"));
        inputs.add(destino);
        inputs.add(new JLabel("Usuário (investigativo)"));
        inputs.add(userInvestigativo);

        controle.add(inputs, BorderLayout.NORTH);

        // ===== INPUT ARESTAS =====
        JTextArea entrada = new JTextArea();
        entrada.setText(
                "ana bruno\n" +
                        "ana carla\n" +
                        "bruno diego\n" +
                        "bruno edu\n" +
                        "carla fernanda\n" +
                        "diego gabriel\n" +
                        "edu helena\n" +
                        "fernanda igor\n" +
                        "gabriel julia\n" +
                        "helena julia\n" +
                        "igor julia\n" +
                        "carla diego"
        );

        JScrollPane scrollEntrada = new JScrollPane(entrada);
        scrollEntrada.setBorder(BorderFactory.createTitledBorder("Lista de Arestas"));

        // ===== BOTÕES =====
        JPanel botoes = new JPanel(new GridLayout(2, 2, 4, 4));

        JButton btnBfs = new JButton("Rodar BFS");
        JButton btnDfs = new JButton("Rodar DFS");
        JButton btnProps = new JButton("Propriedades");
        JButton btnInvestigativo = new JButton("Investigativo");

        botoes.add(btnBfs);
        botoes.add(btnDfs);
        botoes.add(btnProps);
        botoes.add(btnInvestigativo);

        controle.add(botoes, BorderLayout.SOUTH);

        // ===== RESULTADOS =====
        JTextArea resultados = new JTextArea();
        resultados.setEditable(false);
        resultados.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scrollResultados = new JScrollPane(resultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel meio = new JPanel(new GridLayout(2, 1));
        meio.add(scrollEntrada);
        meio.add(scrollResultados);

        controle.add(meio, BorderLayout.CENTER);
        frame.add(controle, BorderLayout.WEST);

        // ===== AREA DO GRAFO =====
        graph = new SingleGraph("Grafo");
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", style);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        view = viewer.addDefaultView(false);
        frame.add((Component) view, BorderLayout.CENTER);
        frame.setVisible(true);

        // ===== EVENTO BFS =====
        btnBfs.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(entrada.getText());
            construirGraphStream(grafo);
            resultados.setText("BFS em execução...\n");

            Node nOrigem = graph.getNode(origem.getText());
            Node nDestino = graph.getNode(destino.getText());

            if (nOrigem != null) {
                nOrigem.setAttribute("ui.style", "fill-color: green;");
            }
            if (nDestino != null) {
                nDestino.setAttribute("ui.style", "fill-color: blue;");
            }

            new Thread(() -> {
                try {
                    bfsAnimado(graph, grafo, origem.getText(), destino.getText());
                    SwingUtilities.invokeLater(() -> resultados.append("BFS concluído.\n"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        // ===== EVENTO DFS =====
        btnDfs.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(entrada.getText());
            construirGraphStream(grafo);
            resultados.setText("DFS em execução...\n");

            Node nOrigem = graph.getNode(origem.getText());
            Node nDestino = graph.getNode(destino.getText());

            if (nOrigem != null) {
                nOrigem.setAttribute("ui.style", "fill-color: green;");
            }
            if (nDestino != null) {
                nDestino.setAttribute("ui.style", "fill-color: blue;");
            }

            new Thread(() -> {
                try {
                    dfsAnimado(graph, grafo, origem.getText(), destino.getText(), new HashSet<>());
                    SwingUtilities.invokeLater(() -> resultados.append("DFS concluído.\n"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        // ===== EVENTO PROPRIEDADES =====
        btnProps.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(entrada.getText());
            construirGraphStream(grafo);
            String info = gerarInfoGrafo(grafo, true, origem.getText());
            resultados.setText(info);
        });

        // ===== EVENTO INVESTIGATIVO =====
        btnInvestigativo.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(entrada.getText());
            construirGraphStream(grafo);

            StringBuilder sb = new StringBuilder();
            sb.append(gerarHubs(grafo));
            sb.append("\n");
            sb.append(gerarAmigos2Passos(grafo, userInvestigativo.getText()));
            resultados.setText(sb.toString());
        });
    }

    // ===== LEITURA DO GRAFO =====
    static Map<String, List<String>> lerGrafo(String texto) {
        Map<String, List<String>> grafo = new HashMap<>();

        for (String linha : texto.split("\n")) {
            String[] p = linha.trim().split("\\s+");
            
            if (p.length < 2) {
                continue;
            }

            String u = p[0];
            String v = p[1];

            grafo.putIfAbsent(u, new ArrayList<>());
            grafo.putIfAbsent(v, new ArrayList<>());
            grafo.get(u).add(v);
        }
        return grafo;
    }

    // ===== CONSTRÓI GRAPHSTREAM =====
    static void construirGraphStream(Map<String, List<String>> grafo) {
        graph.clear();
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", style);

        for (String u : grafo.keySet()) {
            if (graph.getNode(u) == null) {
                Node n = graph.addNode(u);
                n.setAttribute("ui.label", u);
            }
        }

        int id = 0;
        for (String u : grafo.keySet()) {
            for (String v : grafo.get(u)) {
                if (graph.getNode(v) == null) {
                    Node n = graph.addNode(v);
                    n.setAttribute("ui.label", v);
                }
                if (graph.getEdge("E" + id) == null) {
                    graph.addEdge("E" + id++, u, v, true);
                }
            }
        }
    }
}
