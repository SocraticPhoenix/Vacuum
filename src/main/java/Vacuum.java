import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Vacuum {
    public static final byte WALL = 0;
    public static final byte STAIR = 1;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner;
        boolean output;

        //Take input from args or from file, test.txt and test2.txt are examples
        if (args.length == 0) {
            scanner = new Scanner(System.in);
            output = true;
        } else {
            scanner = new Scanner(new File(args[0]));
            output = false;
        }

        if (output) System.out.print("How wide is the widest floor: ");
        int rows = Integer.parseInt(scanner.nextLine());
        if (rows <= 0) {
            if (output) System.out.println("Floors must be wider than 0");
            return;
        }

        if (output) System.out.print("How long is the longest floor: ");
        int cols = Integer.parseInt(scanner.nextLine());
        if (cols <= 0) {
            if (output) System.out.println("Floors must be longer than 0");
            return;
        }

        if (output) System.out.print("How many floors: ");
        int floors = Integer.parseInt(scanner.nextLine());
        if (floors <= 0) {
            if (output) System.out.println("There must at least 1 floor");
            return;
        }

        byte[][][] array = new byte[floors][rows][cols];
        Map<Character, Byte> idMap = new HashMap<>();
        Map<Byte, Character> charMap = new HashMap<>();
        byte id = 2;

        if (output) System.out.println("Input the floors. Use # for walls, % for stairs, and any letter for each room.");
        for (int i = 0; i < floors; i++) {
            if (output) System.out.println("Input floor " + (i + 1));
            for (int j = 0; j < rows; j++) {
                String line = scanner.nextLine();
                for (int k = 0; k < line.length() && k < cols; k++) {
                    char c = line.charAt(k);

                    byte val;
                    if (c == '#') {
                        val = WALL;
                    } else if (c == '%') {
                        val = STAIR;
                    } else if (idMap.containsKey(c)) {
                        val = idMap.get(c);
                    } else {
                        val = id;
                        idMap.put(c, id);
                        charMap.put(id, c);
                        id++;
                    }

                    array[i][j][k] = val;
                }
            }
        }

        if (output) System.out.print("Input dirty rooms: ");
        Set<Byte> dirtyBytes = scanner.nextLine().chars().mapToObj(i -> idMap.get((char) i)).collect(Collectors.toSet());
        Set<Point> dirt = new HashSet<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                for (int k = 0; k < array[i][j].length; k++) {
                    if (dirtyBytes.contains(array[i][j][k])) {
                        dirt.add(new Point(i, j, k));
                    }
                }
            }
        }
        if (output) System.out.print("Input starting floor: ");
        int startFloor = Integer.parseInt(scanner.nextLine());
        if (output) System.out.print("Input starting row: ");
        int startRow = Integer.parseInt(scanner.nextLine());
        if (output) System.out.print("Input starting column: ");
        int startCol = Integer.parseInt(scanner.nextLine());

        byte[][][] model = array;
        Point vacuum = new Point(startFloor, startRow, startCol);

        dirt.remove(vacuum);

        //At each step, pathfind to nearest dirty sqaure and clean it
        List<Point> path;
        while (!(path = astar(vacuum, dirt::contains, array)).isEmpty()) {
            for (int i = 1; i < path.size(); i++) {
                System.out.println("Move to " + path.get(i));
            }
            vacuum = path.get(path.size() - 1);
            dirt.remove(vacuum);
            System.out.println("Clean " + vacuum);
        }
        System.out.println("All done");
        if (!dirt.isEmpty()) {
            System.out.println("Couldn't reach:");
            dirt.forEach(System.out::println);
        }
    }

    private static List<Point> astar(Point start, Predicate<Point> end, byte[][][] space) {
        Set<Point> visited = new HashSet<>();
        Map<Point, Point> pathTracker = new HashMap<>();
        Map<Point, Double> scores = new HashMap<>();

        scores.put(start, 0.0);
        Queue<Point> queue = new PriorityQueue<>(Comparator.comparingDouble(scores::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            if (end.test(point)) {
                List<Point> path = new ArrayList<>();
                Point curr = point;
                while (curr != null) {
                    path.add(0, curr);
                    curr = pathTracker.get(curr);
                }
                return path;
            } else {
                visited.add(point);
                for (Point neighbor : neighbors(point, space)) {
                    double score = scores.get(point) + 1;
                    if (score < scores.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                        pathTracker.put(neighbor, point);
                        scores.put(neighbor, score);
                        if (!visited.contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<Point> neighbors(Point point, byte[][][] space) {
        List<Point> neighbors = new ArrayList<>();
        if (point.get(space) == STAIR) {
            neighbors.add(point.copyWithX(point.floor + 1));
            neighbors.add(point.copyWithX(point.floor - 1));
        }
        neighbors.add(new Point(point.floor, point.row + 1, point.col));
        neighbors.add(new Point(point.floor, point.row - 1, point.col));
        neighbors.add(new Point(point.floor, point.row, point.col + 1));
        neighbors.add(new Point(point.floor, point.row, point.col - 1));

        return neighbors.stream().filter(p -> p.within(space) && p.get(space) != WALL).collect(Collectors.toList());
    }

}
