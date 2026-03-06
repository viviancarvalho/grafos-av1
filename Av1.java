import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.swing_viewer.SwingViewer;

import javax.swing.*;
import java.util.*;

public class Av1 {

    // ====================== BFS ANIMADO ======================
    public static void bfsAnimado(Graph graph, Map<String, List<String>> G,
                                  String origem, String destino) throws Exception {
        final int INF = Integer.MAX_VALUE;
        Map<String,Integer> dist = new HashMap<>();
        Map<String,String> pred = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();

        for (String u : G.keySet()) { dist.put(u, INF); pred.put(u, null); }

        visited.add(origem);
        dist.put(origem, 0);
        q.add(origem);

        while(!q.isEmpty()){
            String u = q.remove();
            Node node = graph.getNode(u);
            node.setAttribute("ui.class", "visitado");
            Thread.sleep(800);

            if (u.equals(destino)) return;

            for(String v : G.getOrDefault(u, Collections.emptyList())){
                if(!visited.contains(v)){
                    visited.add(v);
                    dist.put(v, dist.get(u) + 1);
                    pred.put(v, u);
                    q.add(v);
                }
            }
        }
    }

    // ====================== DFS ANIMADO ======================
    public static class Times {
        public final Map<String,Integer> tin;
        public final Map<String,Integer> tout;
        public final Map<String,String> pred;

        public Times(Map<String,Integer> tin, Map<String,Integer> tout, Map<String,String> pred){
            this.tin = tin;
            this.tout = tout;
            this.pred = pred;
        }
    }

    private static int timer;
    static boolean destinoEncontrado = false;

    public static void dfsAnimado(Graph graph, Map<String,List<String>> G,
                                  String origem, String destino,
                                  Set<String> visitados) throws Exception {

        timer = 0;
        destinoEncontrado = false;

        Map<String,Integer> tin = new HashMap<>();
        Map<String,Integer> tout = new HashMap<>();
        Map<String,String> pred = new HashMap<>();

        pred.put(origem, null);
        dfsVisitAnimado(graph, G, origem, destino, visitados, tin, tout, pred);
    }

    private static void dfsVisitAnimado(Graph graph, Map<String,List<String>> G, String u,
                                        String destino, Set<String> visitados,
                                        Map<String,Integer> tin, Map<String,Integer> tout,
                                        Map<String,String> pred) throws Exception {
        if (destinoEncontrado) return;

        visitados.add(u);
        tin.put(u, ++timer);

        Node node = graph.getNode(u);
        node.setAttribute("ui.class", "visitado");
        Thread.sleep(800);

        if (u.equals(destino)) {
            destinoEncontrado = true;
            return;
        }

        for(String v : G.getOrDefault(u, Collections.emptyList())){
            if(!visitados.contains(v)){
                pred.put(v, u);
                dfsVisitAnimado(graph, G, v, destino, visitados, tin, tout, pred);
            }
        }
        tout.put(u, ++timer);
    }

    // ====================== PROPRIEDADES FUNDAMENTAIS ======================
    static void imprimirInfoGrafo(Map<String,List<String>> G, boolean direcionado) {
        int n = G.size();
        int m = 0;
        for(String u : G.keySet()) m += G.get(u).size();
        if(!direcionado) m /= 2;

        System.out.println("=== PROPRIEDADES FUNDAMENTAIS ===");
        System.out.println("Grafo " + (direcionado ? "Direcionado" : "Não-direcionado"));
        System.out.println("|V| = " + n + ", |E| = " + m);

        // Graus
        for(String u : G.keySet()){
            if(direcionado){
                int grauSaida = G.get(u).size();
                int grauEntrada = 0;
                for(String v : G.keySet()){
                    if(G.get(v).contains(u)) grauEntrada++;
                }
                System.out.println(u + ": grau entrada=" + grauEntrada + ", grau saída=" + grauSaida);
            } else {
                System.out.println(u + ": grau=" + G.get(u).size());
            }
        }

        // Densidade
        double densidade;
        if(direcionado) densidade = (double)m / (n*(n-1));
        else densidade = (double)(2*m) / (n*(n-1));
        System.out.println("Densidade = " + densidade);
        System.out.println("================================\n");
    }

    // ====================== BFS ANALITICO ======================
    static class ResultadoBFS {
        List<String> ordem;
        Map<String,Integer> dist;
        Map<String,String> pred;

        ResultadoBFS(List<String> o, Map<String,Integer> d, Map<String,String> p){
            ordem=o; dist=d; pred=p;
        }
    }

    static ResultadoBFS bfsAnalitico(Map<String,List<String>> G, String origem){
        final int INF = Integer.MAX_VALUE;
        Map<String,Integer> dist = new HashMap<>();
        Map<String,String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        List<String> ordem = new ArrayList<>();

        for(String u : G.keySet()){ dist.put(u, INF); pred.put(u, null); }

        visitados.add(origem); dist.put(origem,0); q.add(origem);

        while(!q.isEmpty()){
            String u = q.remove();
            ordem.add(u);
            for(String v : G.get(u)){
                if(!visitados.contains(v)){
                    visitados.add(v);
                    dist.put(v, dist.get(u)+1);
                    pred.put(v,u);
                    q.add(v);
                }
            }
        }

        return new ResultadoBFS(ordem, dist, pred);
    }

    static List<String> reconstruirCaminho(Map<String,String> pred, String destino){
        List<String> path = new ArrayList<>();
        if(pred.get(destino) == null) return path;
        String atual = destino;
        while(atual != null){
            path.add(atual);
            atual = pred.get(atual);
        }
        Collections.reverse(path);
        return path;
    }

    // ====================== DFS ANALITICO ======================
    static class ResultadoDFS {
        List<String> ordem;
        Map<String,String> pred;
        boolean temCiclo;

        ResultadoDFS(List<String> o, Map<String,String> p, boolean c){
            ordem=o; pred=p; temCiclo=c;
        }
    }

    static ResultadoDFS dfsAnalitico(Map<String,List<String>> G, String origem, String destino){
        Set<String> visitados = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> ordem = new ArrayList<>();
        Map<String,String> pred = new HashMap<>();
        boolean[] destinoEncontrado = new boolean[1];

        boolean[] ciclo = new boolean[1]; // mutable

        dfsVisit(G, origem, visitados, recStack, ordem, pred, ciclo, destino, destinoEncontrado);

        for(String u : G.keySet()){
            if(!visitados.contains(u)) dfsVisit(G,u,visitados,recStack,ordem,pred,ciclo, destino, destinoEncontrado);
        }

        return new ResultadoDFS(ordem,pred,ciclo[0]);
    }

    static void dfsVisit(Map<String,List<String>> G, String u, Set<String> visitados,
                         Set<String> recStack, List<String> ordem, Map<String,String> pred, boolean[] ciclo, String destino, boolean[] destinoEncontrado){
        if (destinoEncontrado[0]) return;

        visitados.add(u);
        recStack.add(u);
        ordem.add(u);

        if (u.equals(destino)) {
            destinoEncontrado[0] = true;
            return;
        }

        for(String v : G.get(u)){
            if (destinoEncontrado[0]) break;
            if(!visitados.contains(v)){
                pred.put(v, u);
                dfsVisit(G, v, visitados, recStack, ordem, pred, ciclo, destino, destinoEncontrado);
            } else if(recStack.contains(v)) {
                ciclo[0] = true;
            }
        }
        recStack.remove(u);
    }

    // ====================== PARTE INVESTIGATIVA ======================
    static void imprimirHubs(Map<String,List<String>> G){
        List<String> vertices = new ArrayList<>(G.keySet());
        vertices.sort((a,b)-> G.get(b).size() - G.get(a).size());
        System.out.println("Top-3 hubs: " + vertices.subList(0, Math.min(3, vertices.size())));
    }

    static void amigos2Passos(Map<String,List<String>> G, String s){
        Set<String> amigos2 = new HashSet<>();
        for(String u : G.get(s)){
            for(String v : G.get(u)){
                if(!v.equals(s) && !G.get(s).contains(v)) amigos2.add(v);
            }
        }
        System.out.println("Amigos a 2 passos de " + s + ": " + amigos2);
    }

    // ====================== MAIN ======================
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // LEITURA DO FORMATO A
        int n = sc.nextInt();
        int m = sc.nextInt();
        String tipo = sc.next(); // "D" ou "U"
        boolean direcionado = tipo.equalsIgnoreCase("D");
        sc.nextLine(); // consumir resto da linha

        Map<String, List<String>> grafo = new HashMap<>();
        Map<String, Map<String,Integer>> pesos = new HashMap<>();

        System.out.println("Lendo arestas:");
        for (int i = 0; i < m; i++) {
            String linha = sc.nextLine();
            String[] parts = linha.split("\\s+");
            String u = parts[0];
            String v = parts[1];
            int w = parts.length >= 3 ? Integer.parseInt(parts[2]) : 1;

            grafo.putIfAbsent(u, new ArrayList<>());
            grafo.putIfAbsent(v, new ArrayList<>());

            grafo.get(u).add(v);
            pesos.putIfAbsent(u, new HashMap<>());
            pesos.get(u).put(v, w);

            if (!direcionado) {
                grafo.get(v).add(u);
                pesos.putIfAbsent(v, new HashMap<>());
                pesos.get(v).put(u, w);
            }

            System.out.println(u + " -> " + v + " (peso=" + w + ")");
        }

        sc.close();

        // ESCOLHA DE ORIGEM E DESTINO
        String origem = "ana";
        String destino = "fernanda";

        // IMPRIMIR PROPRIEDADES FUNDAMENTAIS
        imprimirInfoGrafo(grafo, direcionado);

        // BFS e DFS analíticos
        ResultadoBFS resBFS = bfsAnalitico(grafo, origem);
        System.out.println("BFS ordem: " + resBFS.ordem);
        System.out.println("BFS distâncias: " + resBFS.dist);
        System.out.println("BFS caminho " + origem + "->" + destino + ": " + reconstruirCaminho(resBFS.pred, destino));

        ResultadoDFS resDFS = dfsAnalitico(grafo, origem, destino);
        System.out.println("DFS ordem: " + resDFS.ordem);
        System.out.println("DFS tem ciclo? " + resDFS.temCiclo);
        System.out.println("DFS exemplo de caminho " + origem + "->" + destino + ": " + reconstruirCaminho(resDFS.pred, destino));

        // PARTE INVESTIGATIVA
        imprimirHubs(grafo);
        amigos2Passos(grafo, origem);

        // ====================== CRIAR GRAFOS NO GRAPHSTREAM ======================
        Graph graphBFS = new SingleGraph("BFS");
        Graph graphDFS = new SingleGraph("DFS");
        System.setProperty("org.graphstream.ui", "swing");

        String style =
                "node { size: 20px; fill-color: lightgray; text-size: 14; }" +
                        "node.visitado { fill-color: orange; }" +
                        "node.origem { fill-color: green; }" +
                        "node.destino { fill-color: blue; }" +
                        "edge { arrow-size: 10px,5px; }";

        graphBFS.setAttribute("ui.stylesheet", style);
        graphDFS.setAttribute("ui.stylesheet", style);

        for (String u : grafo.keySet()) {
            if (graphBFS.getNode(u) == null) graphBFS.addNode(u).setAttribute("ui.label", u);
            if (graphDFS.getNode(u) == null) graphDFS.addNode(u).setAttribute("ui.label", u);
        }

        int edgeId = 0;
        for (String u : grafo.keySet()) {
            for (String v : grafo.get(u)) {
                String id = "E" + edgeId;
                boolean existe = graphBFS.getEdge(id) != null;
                if (!existe) {
                    graphBFS.addEdge(id, u, v, direcionado);
                    graphDFS.addEdge(id, u, v, direcionado);
                    edgeId++;
                }
            }
        }

        graphBFS.getNode(origem).setAttribute("ui.class", "origem");
        graphBFS.getNode(destino).setAttribute("ui.class", "destino");
        graphDFS.getNode(origem).setAttribute("ui.class", "origem");
        graphDFS.getNode(destino).setAttribute("ui.class", "destino");

        SwingViewer viewerBFS = new SwingViewer(graphBFS, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        SwingViewer viewerDFS = new SwingViewer(graphDFS, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        viewerBFS.enableAutoLayout();
        viewerDFS.enableAutoLayout();

        View viewBFS = viewerBFS.addDefaultView(false);
        View viewDFS = viewerDFS.addDefaultView(false);

        JFrame frameBFS = new JFrame("Busca em Largura (BFS)");
        frameBFS.setLayout(new java.awt.BorderLayout());
        frameBFS.add((java.awt.Component)viewBFS, java.awt.BorderLayout.CENTER);
        frameBFS.setSize(600, 500);
        frameBFS.setLocation(100, 100);
        frameBFS.setVisible(true);

        JFrame frameDFS = new JFrame("Busca em Profundidade (DFS)");
        frameDFS.setLayout(new java.awt.BorderLayout());
        frameDFS.add((java.awt.Component)viewDFS, java.awt.BorderLayout.CENTER);
        frameDFS.setSize(600, 500);
        frameDFS.setLocation(750, 100);
        frameDFS.setVisible(true);

        // EXECUTAR BFS E DFS ANIMADOS
        new Thread(() -> {
            try { bfsAnimado(graphBFS, grafo, origem, destino); } catch (Exception ignored){}
        }).start();

        new Thread(() -> {
            try { dfsAnimado(graphDFS, grafo, origem, destino, new HashSet<>()); } catch (Exception ignored){}
        }).start();
    }
}