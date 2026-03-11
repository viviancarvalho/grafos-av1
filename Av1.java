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

    static String style =
            "node {" +
                    " size: 20px;" +
                    " fill-color: lightgray;" +
                    " stroke-mode: plain;" +
                    " stroke-color: black;" +
                    " text-size: 14;" +
                    " text-alignment: above;" +
                    "}" +
                    "node.visitado { fill-color: orange; }" +
                    "node.origem   { fill-color: green;  }" +
                    "node.destino  { fill-color: blue;   }" +
                    "node.caminho  { fill-color: red;    }" +
                    "edge { arrow-size: 10px, 5px; }";

    // ====================== BFS ANIMADO ======================
    public static void bfsAnimado(Graph graph, Map<String, List<String>> G,
                                  String origem, String destino) throws Exception {
        final int INF = Integer.MAX_VALUE;
        Map<String, Integer> dist = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> fila = new ArrayDeque<>();

        for (String u : G.keySet()) dist.put(u, INF);

        visited.add(origem);
        dist.put(origem, 0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            String u = fila.remove();
            Node node = graph.getNode(u);
            if (node != null) node.setAttribute("ui.style", "fill-color: orange;");
            Thread.sleep(800);

            if (u.equals(destino)) {
                if (node != null) node.setAttribute("ui.style", "fill-color: blue;");
                return;
            }

            for (String v : G.getOrDefault(u, Collections.emptyList())) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    dist.put(v, dist.get(u) + 1);
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
            ordem = o; dist = d; pred = p;
        }
    }

    static ResultadoBFS bfsAnalitico(Map<String, List<String>> G, String origem) {
        final int INF = Integer.MAX_VALUE;
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> fila = new ArrayDeque<>();
        List<String> ordem = new ArrayList<>();

        for (String u : G.keySet()) { dist.put(u, INF); pred.put(u, null); }

        visitados.add(origem);
        dist.put(origem, 0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            String atual = fila.remove();
            ordem.add(atual);
            for (String viz : G.getOrDefault(atual, Collections.emptyList())) {
                if (!visitados.contains(viz)) {
                    visitados.add(viz);
                    dist.put(viz, dist.get(atual) + 1);
                    pred.put(viz, atual);
                    fila.add(viz);
                }
            }
        }
        return new ResultadoBFS(ordem, dist, pred);
    }

    static List<String> reconstruirCaminho(Map<String, String> pred, String origem, String destino) {
        List<String> path = new ArrayList<>();
        if (!pred.containsKey(destino) || (pred.get(destino) == null && !destino.equals(origem))) return path;
        String atual = destino;
        while (atual != null) { path.add(atual); atual = pred.get(atual); }
        Collections.reverse(path);
        if (path.isEmpty() || !path.get(0).equals(origem)) return new ArrayList<>();
        return path;
    }

    // ====================== DFS ANIMADO ======================
    private static int timer;

    public static void dfsAnimado(Graph graph, Map<String, List<String>> G,
                                  String origem, String destino, Set<String> visitados) throws Exception {
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
        if (node != null) node.setAttribute("ui.style", "fill-color: orange;");
        Thread.sleep(800);

        if (u.equals(destino)) {
            if (node != null) node.setAttribute("ui.style", "fill-color: blue;");
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
        ResultadoDFS(List<String> o, Map<String, String> p, boolean c) { ordem = o; pred = p; temCiclo = c; }
    }

    static ResultadoDFS dfsAnalitico(Map<String, List<String>> G, String origem, String destino) {
        Set<String> visitados = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> ordem = new ArrayList<>();
        Map<String, String> pred = new HashMap<>();
        boolean[] destinoEncontrado = {false};
        boolean[] ciclo = {false};

        dfsVisit(G, origem, visitados, recStack, ordem, pred, ciclo, destino, destinoEncontrado);
        for (String u : G.keySet()) {
            if (!visitados.contains(u))
                dfsVisit(G, u, visitados, recStack, ordem, pred, ciclo, destino, destinoEncontrado);
        }
        return new ResultadoDFS(ordem, pred, ciclo[0]);
    }

    static void dfsVisit(Map<String, List<String>> G, String u, Set<String> visitados, Set<String> recStack,
                         List<String> ordem, Map<String, String> pred, boolean[] ciclo,
                         String destino, boolean[] destinoEncontrado) {
        if (destinoEncontrado[0]) return;
        visitados.add(u); recStack.add(u); ordem.add(u);
        if (u.equals(destino)) { destinoEncontrado[0] = true; return; }

        for (String v : G.getOrDefault(u, Collections.emptyList())) {
            if (destinoEncontrado[0]) break;
            if (!visitados.contains(v)) {
                pred.put(v, u);
                dfsVisit(G, v, visitados, recStack, ordem, pred, ciclo, destino, destinoEncontrado);
            } else if (recStack.contains(v)) {
                ciclo[0] = true;
            }
        }
        recStack.remove(u);
    }

    // ====================== GERADOR DE RELATÓRIO COMPLETO ======================
    static String gerarRelatorioCompleto(Map<String, List<String>> G, boolean direcionado, String origem, String destino) {
        StringBuilder sb = new StringBuilder();

        int V = G.size();
        int E = 0;
        for (String u : G.keySet()) E += G.get(u).size();
        if (!direcionado) E /= 2;

        // ── 1. Tipo do grafo ──────────────────────────────────────────────────
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║     PROPRIEDADES FUNDAMENTAIS        ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");

        sb.append("1. TIPO DO GRAFO\n");
        sb.append("   Direcionamento : ").append(direcionado ? "Direcionado" : "Não-direcionado").append("\n");
        sb.append("   Ponderação     : Não-ponderado\n\n");

        // ── 2. Ordem e tamanho ───────────────────────────────────────────────
        sb.append("2. ORDEM E TAMANHO\n");
        sb.append("   |V| = ").append(V).append(" vértices\n");
        sb.append("   |E| = ").append(E).append(direcionado ? " arcos\n\n" : " arestas\n\n");

        // ── 3. Graus ─────────────────────────────────────────────────────────
        sb.append("3. GRAUS DOS VÉRTICES\n");
        for (String u : G.keySet()) {
            if (direcionado) {
                int saida = G.get(u).size();
                int entrada = 0;
                for (String v : G.keySet()) if (G.get(v).contains(u)) entrada++;
                sb.append("   ").append(u)
                        .append(" → entrada=").append(entrada)
                        .append(", saída=").append(saida).append("\n");
            } else {
                sb.append("   ").append(u).append(" → grau=").append(G.get(u).size()).append("\n");
            }
        }
        sb.append("\n");

        // ── 4. Densidade ─────────────────────────────────────────────────────
        double densidade;
        if (direcionado) {
            densidade = (V <= 1) ? 0 : (double) E / (V * (V - 1));
        } else {
            densidade = (V <= 1) ? 0 : (double) (E * 2) / (V * (V - 1));
        }
        sb.append("4. DENSIDADE\n");
        sb.append("   δ = ").append(String.format("%.4f", densidade)).append("\n\n");

        // ── 5. Conectividade ─────────────────────────────────────────────────
        sb.append("5. CONECTIVIDADE\n");
        if (direcionado) {
            ResultadoBFS bfs = bfsAnalitico(G, origem);
            List<String> alcancaveis = new ArrayList<>();
            for (String v : G.keySet())
                if (!v.equals(origem) && bfs.dist.getOrDefault(v, Integer.MAX_VALUE) != Integer.MAX_VALUE)
                    alcancaveis.add(v);
            sb.append("   Alcançáveis a partir de \"").append(origem).append("\": ");
            sb.append(alcancaveis.isEmpty() ? "(nenhum)" : alcancaveis.toString()).append("\n\n");
        } else {
            Set<String> visitados = new HashSet<>();
            int comp = 0;
            for (String atual : G.keySet()) {
                if (!visitados.contains(atual)) {
                    comp++;
                    Queue<String> fila = new ArrayDeque<>();
                    fila.add(atual); visitados.add(atual);
                    List<String> membros = new ArrayList<>();
                    while (!fila.isEmpty()) {
                        String u = fila.remove(); membros.add(u);
                        for (String viz : G.getOrDefault(u, Collections.emptyList()))
                            if (!visitados.contains(viz)) { visitados.add(viz); fila.add(viz); }
                    }
                    sb.append("   Componente ").append(comp).append(": ").append(membros).append("\n");
                }
            }
            sb.append("   → ").append(comp == 1 ? "Grafo CONEXO." : "Grafo NÃO conexo (" + comp + " componentes).").append("\n\n");
        }

        // ── 6. Altura ────────────────────────────────────────────────────────
        ResultadoBFS bfsAltura = bfsAnalitico(G, origem);
        int altura = 0;
        for (int d : bfsAltura.dist.values())
            if (d != Integer.MAX_VALUE && d > altura) altura = d;
        sb.append("6. ALTURA\n");
        sb.append("   altura(").append(origem).append(") = ").append(altura)
                .append("  (maior nível/distância a partir de \"").append(origem).append("\" via BFS)\n\n");

        // ── BFS aplicado ─────────────────────────────────────────────────────
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║         BFS — APLICAÇÃO              ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");

        ResultadoBFS bfsResult = bfsAnalitico(G, origem);

        sb.append("Ordem de visita:\n   ").append(bfsResult.ordem).append("\n\n");

        sb.append("Distâncias a partir de \"").append(origem).append("\":\n");
        for (String v : bfsResult.ordem) {
            int d = bfsResult.dist.getOrDefault(v, Integer.MAX_VALUE);
            sb.append("   ").append(v).append(" → ").append(d == Integer.MAX_VALUE ? "∞" : d).append("\n");
        }
        sb.append("\n");

        List<String> caminhoBFS = reconstruirCaminho(bfsResult.pred, origem, destino);
        sb.append("Caminho de \"").append(origem).append("\" até \"").append(destino).append("\":\n   ");
        if (caminhoBFS.isEmpty()) {
            sb.append("Caminho não encontrado.\n\n");
        } else {
            sb.append(String.join(" → ", caminhoBFS)).append("\n");
            sb.append("   (").append(caminhoBFS.size() - 1).append(" aresta(s))\n\n");
        }

        // ── DFS aplicado ─────────────────────────────────────────────────────
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║         DFS — APLICAÇÃO              ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");

        ResultadoDFS dfsResult = dfsAnalitico(G, origem, destino);

        sb.append("Ordem de descoberta:\n   ").append(dfsResult.ordem).append("\n\n");

        sb.append("Detecção de ciclo:\n");
        if (direcionado) {
            sb.append("   Regra: em grafos direcionados, um ciclo existe quando\n");
            sb.append("   a DFS encontra uma aresta de retorno (u → v) onde v já\n");
            sb.append("   está na pilha de recursão (recStack).\n");
            sb.append("   Resultado: ").append(dfsResult.temCiclo ? "CICLO DETECTADO." : "Nenhum ciclo detectado.").append("\n\n");
        } else {
            sb.append("   Regra: em grafos não-direcionados, um ciclo existe quando\n");
            sb.append("   a DFS encontra um vizinho já visitado que não é o predecessor.\n");
            sb.append("   Resultado: ").append(dfsResult.temCiclo ? "CICLO DETECTADO." : "Nenhum ciclo detectado.").append("\n\n");
        }

        List<String> caminhoDFS = reconstruirCaminho(dfsResult.pred, origem, destino);
        sb.append("Caminho DFS de \"").append(origem).append("\" até \"").append(destino).append("\":\n   ");
        if (caminhoDFS.isEmpty()) {
            sb.append("Caminho não encontrado.\n");
        } else {
            sb.append(String.join(" → ", caminhoDFS)).append("\n");
            sb.append("   (caminho encontrado pela DFS — não necessariamente mínimo)\n");
        }

        return sb.toString();
    }

    // ====================== HUBS E AMIGOS A 2 PASSOS ======================
    static String gerarHubs(Map<String, List<String>> G) {
        List<String> vertices = new ArrayList<>(G.keySet());
        vertices.sort((a, b) -> G.get(b).size() - G.get(a).size());
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║           TOP-3 HUBS                 ║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        List<String> top3 = vertices.subList(0, Math.min(3, vertices.size()));
        for (int i = 0; i < top3.size(); i++) {
            String v = top3.get(i);
            sb.append((i + 1)).append("º  ").append(v).append("  (grau ").append(G.get(v).size()).append(")\n");
        }
        return sb.toString();
    }

    static String gerarAmigos2Passos(Map<String, List<String>> G, String user) {
        if (!G.containsKey(user)) return "Vértice \"" + user + "\" não encontrado.\n";
        Set<String> amigos2 = new HashSet<>();
        for (String u : G.get(user)) {
            if (!G.containsKey(u)) continue;
            for (String v : G.get(u))
                if (!v.equals(user) && !G.get(user).contains(v)) amigos2.add(v);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║    AMIGOS A 2 PASSOS DE: ").append(user.toUpperCase());
        // padding
        int pad = 12 - user.length();
        for (int i = 0; i < pad; i++) sb.append(" ");
        sb.append("║\n");
        sb.append("╚══════════════════════════════════════╝\n");
        if (amigos2.isEmpty()) {
            sb.append("Nenhum encontrado.\n");
        } else {
            for (String a : amigos2) sb.append("• ").append(a).append("\n");
        }
        return sb.toString();
    }

    // ====================== MAIN ======================
    public static void main(String[] args) throws Exception {

        System.setProperty("org.graphstream.ui", "swing");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frame = new JFrame("Visualizador de Grafos");
        frame.setSize(1280, 680);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ── Painel de controle ────────────────────────────────────────────────
        JPanel controle = new JPanel(new BorderLayout());
        controle.setPreferredSize(new Dimension(400, 680));

        JPanel inputs = new JPanel(new GridLayout(8, 1));
        JTextField tfOrigem = new JTextField("ana");
        JTextField tfDestino = new JTextField("julia");
        JTextField tfUserInv = new JTextField("ana");

        inputs.add(new JLabel("Origem (s)"));
        inputs.add(tfOrigem);
        inputs.add(new JLabel("Destino (t)"));
        inputs.add(tfDestino);
        inputs.add(new JLabel("Usuário (investigativo)"));
        inputs.add(tfUserInv);
        controle.add(inputs, BorderLayout.NORTH);

        // ── Área de arestas ───────────────────────────────────────────────────
        JTextArea taEntrada = new JTextArea();
        taEntrada.setText(
                "ana bruno\nana carla\nbruno diego\nbruno edu\ncarla fernanda\n" +
                        "diego gabriel\nedu helena\nfernanda igor\ngabriel julia\n" +
                        "helena julia\nigor julia\ncarla diego"
        );
        JScrollPane spEntrada = new JScrollPane(taEntrada);
        spEntrada.setBorder(BorderFactory.createTitledBorder("Lista de Arestas"));

        // ── Resultados ────────────────────────────────────────────────────────
        JTextArea taResultados = new JTextArea();
        taResultados.setEditable(false);
        taResultados.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));
        JScrollPane spResultados = new JScrollPane(taResultados);
        spResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JPanel meio = new JPanel(new GridLayout(2, 1));
        meio.add(spEntrada);
        meio.add(spResultados);
        controle.add(meio, BorderLayout.CENTER);

        // ── Botões ────────────────────────────────────────────────────────────
        JPanel botoes = new JPanel(new GridLayout(2, 2, 4, 4));
        JButton btnBfs        = new JButton("▶ BFS animado");
        JButton btnDfs        = new JButton("▶ DFS animado");
        JButton btnProps      = new JButton("📊 Propriedades + BFS/DFS");
        JButton btnInv        = new JButton("🔍 Investigativo");

        botoes.add(btnBfs);
        botoes.add(btnDfs);
        botoes.add(btnProps);
        botoes.add(btnInv);
        controle.add(botoes, BorderLayout.SOUTH);

        frame.add(controle, BorderLayout.WEST);

        // ── Grafo ─────────────────────────────────────────────────────────────
        graph = new SingleGraph("Grafo");
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", style);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        view = viewer.addDefaultView(false);
        frame.add((Component) view, BorderLayout.CENTER);
        frame.setVisible(true);

        // ── Evento BFS animado ────────────────────────────────────────────────
        btnBfs.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(taEntrada.getText());
            construirGraphStream(grafo);
            taResultados.setText("BFS em execução...\n");

            Node nO = graph.getNode(tfOrigem.getText());
            Node nD = graph.getNode(tfDestino.getText());
            if (nO != null) nO.setAttribute("ui.style", "fill-color: green;");
            if (nD != null) nD.setAttribute("ui.style", "fill-color: blue;");

            new Thread(() -> {
                try {
                    bfsAnimado(graph, grafo, tfOrigem.getText(), tfDestino.getText());
                    SwingUtilities.invokeLater(() -> taResultados.append("BFS concluído.\n"));
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        });

        // ── Evento DFS animado ────────────────────────────────────────────────
        btnDfs.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(taEntrada.getText());
            construirGraphStream(grafo);
            taResultados.setText("DFS em execução...\n");

            Node nO = graph.getNode(tfOrigem.getText());
            Node nD = graph.getNode(tfDestino.getText());
            if (nO != null) nO.setAttribute("ui.style", "fill-color: green;");
            if (nD != null) nD.setAttribute("ui.style", "fill-color: blue;");

            new Thread(() -> {
                try {
                    dfsAnimado(graph, grafo, tfOrigem.getText(), tfDestino.getText(), new HashSet<>());
                    SwingUtilities.invokeLater(() -> taResultados.append("DFS concluído.\n"));
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        });

        // ── Evento Propriedades ───────────────────────────────────────────────
        btnProps.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(taEntrada.getText());
            construirGraphStream(grafo);
            String relatorio = gerarRelatorioCompleto(grafo, true, tfOrigem.getText(), tfDestino.getText());
            taResultados.setText(relatorio);
            taResultados.setCaretPosition(0);
        });

        // ── Evento Investigativo ──────────────────────────────────────────────
        btnInv.addActionListener(e -> {
            Map<String, List<String>> grafo = lerGrafo(taEntrada.getText());
            construirGraphStream(grafo);
            taResultados.setText(gerarHubs(grafo) + gerarAmigos2Passos(grafo, tfUserInv.getText()));
            taResultados.setCaretPosition(0);
        });
    }

    // ====================== LEITURA DO GRAFO ======================
    static Map<String, List<String>> lerGrafo(String texto) {
        Map<String, List<String>> grafo = new HashMap<>();
        for (String linha : texto.split("\n")) {
            String[] p = linha.trim().split("\\s+");
            if (p.length < 2) continue;
            grafo.putIfAbsent(p[0], new ArrayList<>());
            grafo.putIfAbsent(p[1], new ArrayList<>());
            grafo.get(p[0]).add(p[1]);
        }
        return grafo;
    }

    // ====================== CONSTRÓI GRAPHSTREAM ======================
    static void construirGraphStream(Map<String, List<String>> grafo) {
        graph.clear();
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.stylesheet", style);

        for (String u : grafo.keySet()) {
            if (graph.getNode(u) == null) graph.addNode(u).setAttribute("ui.label", u);
        }

        int id = 0;
        for (String u : grafo.keySet()) {
            for (String v : grafo.get(u)) {
                if (graph.getNode(v) == null) graph.addNode(v).setAttribute("ui.label", v);
                graph.addEdge("E" + id++, u, v, true);
            }
        }
    }
}
