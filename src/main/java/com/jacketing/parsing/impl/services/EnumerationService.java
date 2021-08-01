package com.jacketing.parsing.impl.services;

import com.google.common.collect.BiMap;
import com.jacketing.parsing.interfaces.structures.services.EnumeratedNodeMap;
import com.jacketing.parsing.interfaces.structures.services.GraphEnumerationService;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import java.util.Collection;

public class EnumerationService implements GraphEnumerationService {

  private final BiMap<Integer, String> numeralToIdBiMap;
  private final BiMap<String, Integer> idToNumeralBiMap;

  public EnumerationService(BiMap<Integer, String> numeralToIdBiMap) {
    this.numeralToIdBiMap = numeralToIdBiMap;
    this.idToNumeralBiMap = numeralToIdBiMap.inverse();
  }

  public EnumeratedNodeMap enumerateFromGraph(GraphParser graph) {
    Collection<GraphNode> nodes = graph.getNodes().values();
    int incrementer = 0;
    for (GraphNode node : nodes) {
      numeralToIdBiMap.put(incrementer++, node.getId());
    }

    return new EnumeratedNodeMap() {
      @Override
      public String getIdFromNumeral(int numeral) {
        return numeralToIdBiMap.get(numeral);
      }

      @Override
      public int getEnumerated(String id) {
        return idToNumeralBiMap.get(id);
      }
    };
  }
}