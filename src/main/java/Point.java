import java.util.Objects;

public class Point {
    public int floor, row, col;

    public Point(int x, int y, int z) {
        this.floor = x;
        this.row = y;
        this.col = z;
    }

    public double distanceSquared(Point other) {
        return (this.floor - other.floor) * (this.floor - other.floor) +
                (this.row - other.row) * (this.row - other.row) +
                (this.col - other.col) * (this.col - other.col);
    }

    public Point copy() {
        return new Point(this.floor, this.row, this.col);
    }

    public Point copyWithX(int x) {
        return new Point(x, this.row, this.col);
    }

    public Point copyWithY(int y) {
        return new Point(this.floor, y, this.col);
    }

    public Point copyWithZ(int z) {
        return new Point(this.floor, this.row, z);
    }

    public byte get(byte[][][] space) {
        return space[floor][row][col];
    }

    public boolean within(byte[][][] space) {
        return floor >= 0 && row >= 0 && col >= 0 &&
                floor < space.length &&
                row < space[floor].length &&
                col < space[floor][row].length;
    }

    @Override
    public String toString() {
        return "{" +
                "floor=" + floor +
                ", row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return floor == point.floor && row == point.row && col == point.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, row, col);
    }
}
