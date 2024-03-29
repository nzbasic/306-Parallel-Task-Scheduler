package com.jacketing.view.innercontrollers;

import com.jacketing.algorithm.structures.ScheduleV1;
import com.jacketing.common.analysis.AlgorithmEvent;
import com.jacketing.common.analysis.AlgorithmObserver;
import com.jacketing.parsing.impl.structures.EnumeratedAdjacencyList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

public class SearchSpaceController {

  private AlgorithmObserver observer;
  private Graph graphData;
  private ScheduleV1 schedule;
  private List<String> nodes = new ArrayList<>();
  private List<String> edges = new ArrayList<>();
  private Thread pollingThread;

  public SearchSpaceController(AlgorithmObserver observer, StackPane pane) {
    System.setProperty("org.graphstream.ui", "javafx");
    Graph graph = new SingleGraph("Input Display");
    this.graphData = graph;

    observer.on(AlgorithmEvent.GRAPH_LOADED, (event) -> {
      Platform.runLater(() -> {
        EnumeratedAdjacencyList adjacencyList = observer
          .getGraph()
          .getAdjacencyList();

        for (int nodeId : adjacencyList.getNodeIds()) {
          String id = nodeId + "";
          nodes.add(id);
          graph.addNode(id);
        }

        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList
          .getInAdjacencyList()
          .entrySet()) {
          String key = Integer.toString(entry.getKey());
          for (int value : entry.getValue()) {
            String node = Integer.toString(value);
            String id = key + node;
            edges.add(id);
            graph.addEdge(id, key, node);
          }
        }

        for (Node node : graph) {
          node.setAttribute("ui.label", "  " + node.getId());
          node.setAttribute("size", "big");
        }

        graph.setAttribute(
          "ui.stylesheet",
          "graph { fill-color: #2f2d2e; } node { fill-color: #8b5fbf; size: 30px; stroke-mode: plain; } node.visited { fill-color: #00aeef; } edge { size: 2px; } edge.done { fill-color: green; }"
        );
        FxViewer view = new FxViewer(
          graph,
          FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD
        );
        view.enableAutoLayout();

        FxViewPanel panel = (FxViewPanel) view.addView(
          FxViewer.DEFAULT_VIEW_ID,
          new FxGraphRenderer()
        );
        pane.getChildren().addAll(panel); // prevent UI shift issues
        this.observer = observer;

        Thread pollingThread = new Thread(
          () -> {
            while (true) {
              pollGraph();
              try {
                Thread.sleep(200);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        );
        this.pollingThread = pollingThread;
        pollingThread.start();
      });

    });
  }

  public void stop() {
    pollingThread.suspend();
  }

  public void resume() {
    pollingThread.resume();
  }

  private void pollGraph() {
    ScheduleV1 partial = observer.getPartialSchedule();
    if (partial != null && !observer.isFinished()) {
      List<Integer> visited = observer.getVisited();

      for (String node : nodes) {
        graphData.getNode(node).removeAttribute("ui.class");
      }

      for (String edge : edges) {
        graphData.getEdge(edge).removeAttribute("ui.class");
      }

      EnumeratedAdjacencyList adjacencyList = observer
        .getGraph()
        .getAdjacencyList();

      for (int node1 : visited) {
        List<Integer> entry = adjacencyList.getInAdjacencyList().get(node1);
        if (entry != null) {
          for (int node2 : visited) {
            if (entry.contains(node2)) {
              graphData
                .getEdge(node1 + "" + node2)
                .setAttribute("ui.class", "done");
            }
          }
        }

        graphData.getNode(node1 + "").setAttribute("ui.class", "visited");
      }
    }

    if (observer.isFinished()) {
      for (String edge : edges) {
        graphData.getEdge(edge).setAttribute("ui.class", "done");
      }

      for (String node : nodes) {
        graphData.getNode(node).setAttribute("ui.class", "visited");
      }
    }
  }
}
