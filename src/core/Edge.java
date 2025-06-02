package core;

public class Edge implements Comparable<Edge> {

    Room roomA, roomB;
    double distance;

    Edge(Room a, Room b, double d) {
        roomA = a;
        roomB = b;
        distance = d;
    }

    public double getDistance() {
        return distance;
    }

    public int compareTo(Edge other) {
        return Double.compare(this.distance, other.distance);
    }

}
