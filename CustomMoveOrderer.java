package src.pa2.moveorder;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;


// JAVA PROJECT IMPORTS
import src.pa2.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer
    extends Object
{

    /**
     * TODO: implement me!
     * This method should perform move ordering. Remember, move ordering is how alpha-beta pruning gets part of its power from.
     * You want to see nodes which are beneficial FIRST so you can prune as much as possible during the search (i.e. be faster)
     * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
     * @return The ordered nodes.
     */
    public static List<DFSTreeNode> order(List<DFSTreeNode> nodes)
    {
        Map<DFSTreeNode, Integer> nodeValues = new HashMap<>();

        for (DFSTreeNode node: nodes)
        {
            int val = evaluateNode(node);
            nodeValues.put(node, val);
            //map each node wit its value
            //sort the map then return then return the list of nodes in order from highest to lowest value
        }


        List<DFSTreeNode> sortedNodes = nodeValues.entrySet()
        .stream()
        .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // Sorting using lambda expression
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

        return sortedNodes;

    }

    // Evaluate a node and return a score. Higher score means higher priority.
    private static int evaluateNode(DFSTreeNode node) {
        if (node.getMove() == null) {
            return 0;
        }

        // evaluation logic
        switch (node.getMove().getType()) {
            case CAPTUREMOVE:
                return 10; // High priority for capture moves
            case CASTLEMOVE:
                return 8; // Next priority for putting the opponent in check
            case MOVEMENTMOVE:
                return 5; // Encourage development moves
            case PROMOTEPAWNMOVE:
                return 3; // Pawn moves might be less critical
            default:
                return 1; // Low priority for other moves
        }
    }
    
}

        // please replace this!
        ///return DefaultMoveOrderer.order(nodes);
    
