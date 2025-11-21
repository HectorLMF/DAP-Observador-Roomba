package pathfinding;

import model.Position;
import model.Room;

import java.util.*;

/**
 * Implementacion simple de A* con heuristica Manhattan.
 * Devuelve lista de posiciones desde start(excluido) hasta goal(incluido).
 */
public class AStar {
    public static List<Position> findPath(Position start, Position goal, Room room) {
        if (start.equals(goal)) return Collections.emptyList();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<Position, Integer> gScore = new HashMap<>();
        Map<Position, Position> cameFrom = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal));
        open.add(startNode);
        gScore.put(start, 0);

        while (!open.isEmpty()) {
            Node node = open.poll();
            Position current = node.pos;
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }
            for (Position neighbor : room.getNeighbors(current)) {
                int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1 + (room.isCleaned(neighbor) ? 1 : 0);
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    int f = tentativeG + heuristic(neighbor, goal);
                    open.add(new Node(neighbor, tentativeG, f));
                }
            }
        }
        return null; // no path
    }

    private static List<Position> reconstructPath(Map<Position, Position> cameFrom, Position current) {
        List<Position> total = new ArrayList<>();
        Position cur = current;
        while (cameFrom.containsKey(cur)) {
            total.add(cur);
            cur = cameFrom.get(cur);
        }
        Collections.reverse(total);
        return total;
    }

    private static int heuristic(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static class Node {
        Position pos;
        int g;
        int f;

        Node(Position pos, int g, int f) { this.pos = pos; this.g = g; this.f = f; }
    }
}

