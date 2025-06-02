package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.*;



public class worldGenerator {
    static int WORLD_WIDTH;
    static int WORLD_HEIGHT;
    static Random RANDOM;

    int currRoomNum = 0;
    int roomNum;
    List<Room> existingRooms = new ArrayList<>();
    TETile[][] world;
    Set<Position> wallCandidates = new HashSet<>();
    private final char character;
    private TETile floorTile;
    private TETile wallTile;
    private TETile nothingTile;
    private TETile portalTile;
    private TETile foodTile;
    private List<Position> availableSpots = new ArrayList<>();
    private Set<TETile> walkableTiles;
    private Room spawnAvatarRoom;
    private List<Room> spiritSpawnRooms;
    Set<Position> roomFloors = new HashSet<>();






    public worldGenerator(int width, int height, long seed, char character) {
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
        this.character = character;
        RANDOM = new Random(seed);
        world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        roomNum = RandomUtils.uniform(RANDOM, 20, 25);

        switch (character) {
            case 'm' -> {
                floorTile = Tileset.MELODY_FLOOR;
                wallTile = Tileset.MELODY_WALL;
                nothingTile = Tileset.MELODY_NOTHING;
                portalTile = Tileset.MELODY_GATE;
                foodTile = Tileset.MELODY_FOOD;
            }
            case 'c' -> {
                floorTile = Tileset.CINNAMOROLL_FLOOR;
                wallTile = Tileset.CINNAMOROLL_WALL;
                nothingTile = Tileset.CINNAMOROLL_NOTHING;
                portalTile = Tileset.CINNAMOROLL_GATE;
                foodTile = Tileset.CINNAMOROLL_FOOD;
            }
            case 'k' -> {
                floorTile = Tileset.KUROMI_FLOOR;
                wallTile = Tileset.KUROMI_WALL;
                nothingTile = Tileset.KUROMI_NOTHING;
                portalTile = Tileset.KUROMI_GATE;
                foodTile = Tileset.KUROMI_FOOD;
            }
        }

        walkableTiles = new HashSet<>();
        walkableTiles.add(floorTile);
        walkableTiles.add(portalTile);


    }

    // high level world generation
    public void generateWorld() {
        switch (character) {
            case 'm' -> generateMelodyWorld();
            case 'c' -> generateCinnamorollWorld();
            case 'k' -> generateKuromiWorld();
        }
    }

    private void generateKuromiWorld() {
        fillWorld();
        generateWorldHelper();
        assignRolesToRooms();
    }

    private void generateCinnamorollWorld() {
        fillWorld();
        generateWorldHelper();
        assignRolesToRooms();
    }

    private void generateMelodyWorld() {
        fillWorld();
        generateWorldHelper();
        assignRolesToRooms();
    }


    // world generation helpers
    private void fillWorld() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = nothingTile;
            }
        }
    }

    private void generateWorldHelper() {
        addRooms();
        connectRoomsWithMST();
        placeWalls();

        // count % of covered area
        int filled = 0;
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                if (world[x][y] != nothingTile) {
                    filled++;
                }
            }
        }
        System.out.println("Filled: " + filled + " / " + (WORLD_WIDTH * WORLD_HEIGHT));
        System.out.println("Percentage filled: " + (100.0 * filled / (WORLD_WIDTH * WORLD_HEIGHT)) + "%");

    }

    // add rooms
    void addRooms() {
        int maxRooms = roomNum;
        int maxAttemptsPerRoom = 1000;
        int failedAttempts = 0;

        while (currRoomNum < maxRooms && failedAttempts < maxRooms) {
            boolean placed = false;

            // try up to 300 times to place a single room
            for (int i = 0; i < maxAttemptsPerRoom; i++) {
                Room r = new Room(world, RANDOM);
                if (isWithinBounds(r) && !isOverlap(r) && farEnough(r)) {
                    placeRoom(r);
                    existingRooms.add(r);
                    currRoomNum++;
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                // could not place a room after 300 attempts
                failedAttempts++;
            }
        }

        //System.out.println("Rooms placed:" + currRoomNum);
    }


    void placeRoom(Room r) {
        for (int x = r.Left(); x <= r.Right(); x++) {
            for (int y = r.Down(); y <= r.Up(); y++) {
                world[x][y] = floorTile;
                roomFloors.add(new Position(x, y));  // mark as a room floor
                storeWallCandidates(x, y);
            }
        }
    }

    boolean isWithinBounds(Room r) {
        return r.Left() > 0 && r.Right() < WORLD_WIDTH - 1 &&
                r.Down() > 0 && r.Up() < WORLD_HEIGHT - 1;
    }

    boolean isOverlap(Room r) {
        int buffer = 1;
        for (Room placed : existingRooms) {
            if (r.Right() + buffer >= placed.Left() - buffer &&
                    r.Left() - buffer <= placed.Right() + buffer &&
                    r.Up() + buffer >= placed.Down() - buffer &&
                    r.Down() - buffer <= placed.Up() + buffer) {
                return true;
            }
        }
        return false;
    }

    boolean farEnough(Room r) {
        int minDistance = 3;

        for (Room placed : existingRooms) {
            boolean tooCloseLeft   = r.Right() + minDistance >= placed.Left();
            boolean tooCloseRight  = r.Left() - minDistance <= placed.Right();
            boolean tooCloseBottom = r.Up() + minDistance >= placed.Down();
            boolean tooCloseTop    = r.Down() - minDistance <= placed.Up();

            // if all sides are too close, skip this room
            if (tooCloseLeft && tooCloseRight && tooCloseTop && tooCloseBottom) {
                return false;
            }
        }

        return true; // far enough in at least one direction
    }





    // add hallways
    void connectRoomsWithMST() {
        List<Edge> edges = buildAllEdges();
        Collections.sort(edges);
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(existingRooms.size());

        for (Edge e : edges) {
            Room a = e.roomA, b = e.roomB;
            int idA = existingRooms.indexOf(a), idB = existingRooms.indexOf(b);

            if (!uf.connected(idA, idB)) {
                uf.union(idA, idB);

                int maxTries = 1000, tries = 0;
                Position edgeA = getRandomEdge(a);
                Position edgeB = getRandomEdge(b);
                while (tries < maxTries) {
                    edgeA = getRandomEdge(a);
                    edgeB = getRandomEdge(b);

                    if (canTurn(edgeA, edgeB) && !hitAnotherRoom(edgeA, edgeB, a, b)) {
                        break;
                    }

                    tries++;
                }

                buildLHallway(edgeA, edgeB);


            }
        }
    }


    private boolean canTurn(Position posA, Position posB) {
        int dx = Math.abs(posA.getX() - posB.getX());
        int dy = Math.abs(posA.getY() - posB.getY());

        return (dx >= 3 && dy >= 2) || (dx >= 2 && dy >= 3);
    }


    private List<Position> computeZigzagPath(Position start, Position end) {
        List<Position> path = new ArrayList<>();

        int x = start.getX();
        int y = start.getY();
        int endX = end.getX();
        int endY = end.getY();

        boolean horizontal = RANDOM.nextBoolean(); // pick random start direction

        while (x != endX || y != endY) {
            if (horizontal) {
                int dx = Integer.compare(endX, x);
                int steps = Math.min(Math.abs(endX - x), RandomUtils.uniform(RANDOM, 2, 5));
                for (int i = 0; i < steps; i++) {
                    if (x == endX) break;
                    x += dx;
                    path.add(new Position(x, y));
                }
            } else {
                int dy = Integer.compare(endY, y);
                int steps = Math.min(Math.abs(endY - y), RandomUtils.uniform(RANDOM, 2, 5));
                for (int i = 0; i < steps; i++) {
                    if (y == endY) break;
                    y += dy;
                    path.add(new Position(x, y));
                }
            }
            horizontal = !horizontal; // switch direction
        }

        return path;
    }


    private List<Position> computeLPath(Position start, Position end) {
        List<Position> path = new ArrayList<>();

        int x = start.getX();
        int y = start.getY();
        int endX = end.getX();
        int endY = end.getY();

        boolean horizontalFirst = RANDOM.nextBoolean();

        if (horizontalFirst) {
            int stepX = Integer.compare(endX, x);
            while (x != endX) {
                x += stepX;
                path.add(new Position(x, y));
            }

            int stepY = Integer.compare(endY, y);
            while (y != endY) {
                y += stepY;
                path.add(new Position(x, y));
            }
        } else {
            int stepY = Integer.compare(endY, y);
            while (y != endY) {
                y += stepY;
                path.add(new Position(x, y));
            }

            int stepX = Integer.compare(endX, x);
            while (x != endX) {
                x += stepX;
                path.add(new Position(x, y));
            }
        }

        return path;
    }




    void buildLHallway(Position start, Position end) {
        List<Position> path = computeZigzagPath(start, end);
        for (Position p : path) {
            buildHallwayHelper(p.getX(), p.getY());
        }
    }


    private boolean hitAnotherRoom(Position start, Position end, Room roomA, Room roomB) {
        List<Position> path = computeZigzagPath(start, end);
        for (Position p : path) {
            int x = p.getX();
            int y = p.getY();
            for (Room r : existingRooms) {
                if (r != roomA && r != roomB && r.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }



    List<Edge> buildAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < existingRooms.size(); i++) {
            for (int j = i + 1; j < existingRooms.size(); j++) {
                Room a = existingRooms.get(i);
                Room b = existingRooms.get(j);
                double dist = distance(a.getCenter(), b.getCenter());
                edges.add(new Edge(a, b, dist));
            }
        }
        return edges;
    }

    double distance(Position a, Position b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }




    Position getRandomEdge(Room r) {
        List<Position> edges = new ArrayList<>();
        for (int x = r.Left(); x <= r.Right(); x++) {
            edges.add(new Position(x, r.Up()));
            edges.add(new Position(x, r.Down()));
        }
        for (int y = r.Down() + 1; y < r.Up(); y++) {
            edges.add(new Position(r.Left(), y));
            edges.add(new Position(r.Right(), y));
        }
        return edges.get(RandomUtils.uniform(RANDOM, 0, edges.size()));
    }



    void buildHallwayHelper(int x, int y) {
        Position pos = new Position(x, y);
        if (isInBounds(pos)) {
            world[x][y] = floorTile;
            storeWallCandidates(x, y);
        }
    }



    // build walls
    void placeWalls() {
        for (Position pos : wallCandidates) {
            if (world[pos.getX()][pos.getY()] == nothingTile) {
                world[pos.getX()][pos.getY()] = wallTile;
            }
        }
    }

    private void storeWallCandidates(int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                if (inBorders(nx, ny) && world[nx][ny] == nothingTile) {
                    wallCandidates.add(new Position(nx, ny));
                }
            }
        }
    }



    // assign roles to rooms
    private void assignRolesToRooms() {
        collectAvailableSpots();

        //Collections.shuffle(existingRooms, RANDOM);
        spawnAvatarRoom = existingRooms.get(0);
        spiritSpawnRooms = existingRooms.subList(1, 2);
        List<Room> foodRooms = existingRooms.subList(2, existingRooms.size());
        int placedFood = 0;

        // place food
        for (Room r : foodRooms) {
            if (placedFood == 5) {
                break;
            }
            Position pos = getRandomFloorInRoom(r);
            if (pos != null) {
                world[pos.getX()][pos.getY()] = foodTile;
                placedFood++;
            }
        }
    }



    private void collectAvailableSpots() {

        for (Room r : existingRooms) {
            for (int x = r.Left() + 1; x < r.Right(); x++) {
                for (int y = r.Down() + 1; y < r.Up(); y++) {
                    if (world[x][y] == floorTile) {
                        availableSpots.add(new Position(x, y));
                    }
                }
            }
        }
    }


    public Position getRandomFloorInRoom(Room room) {
        List<Position> floors = new ArrayList<>();
        for (int x = room.Left() + 1; x < room.Right(); x++) {
            for (int y = room.Down() + 1; y < room.Up(); y++) {
                if (world[x][y] == floorTile) {
                    floors.add(new Position(x, y));
                }
            }
        }
        if (floors.isEmpty()) return null;
        return floors.get(RandomUtils.uniform(RANDOM, floors.size()));
    }




    // for floors
    boolean isInBounds(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        return x > 0 && x < WORLD_WIDTH - 1 && y > 0 && y < WORLD_HEIGHT - 1;
    }








    // get tile helpers
    public TETile[][] getWorld() {
        return world;
    }


    public TETile getTile(int x, int y) {

        return world[x][y];
    }

    public void setTile(int x, int y, TETile tile) {

        world[x][y] = tile;
    }

    public boolean isWalkable(int x, int y) {
        TETile tile = world[x][y];
        return walkableTiles.contains(tile);
    }


    // for world borders
    public boolean inBorders(int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }

    public TETile getFloorTile() {
        return floorTile;
    }

    public TETile getPortalTile() { return portalTile; }

    public TETile getFoodTile() { return foodTile; }


    // get spawned room
    public Room getAvatarRoom() { return spawnAvatarRoom; }
    public List<Room> getSpiritRooms() { return spiritSpawnRooms; }

    public Position getAnyFloorAwayFrom(Position avatarPos, int Distance) {
        List<Position> candidates = new ArrayList<>();

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                if (world[x][y] == floorTile) {
                    Position p = new Position(x, y);
                    if (distance(p, avatarPos) == Distance) {
                        candidates.add(p);
                    }
                }
            }
        }

        if (candidates.isEmpty()) return null;
        return candidates.get(RandomUtils.uniform(RANDOM, candidates.size()));
    }

}