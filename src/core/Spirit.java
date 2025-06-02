package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.*;


public class Spirit {
    private int x, y;
    private final char character;
    private final worldGenerator wg;
    private final TETile spiritTile;
    private final int cooldown = 4;
    private int tickCounter = 0;
    private TETile tileUnderSpirit;
    private boolean aggressive = false;
    //private Position lastPlayerPosition = null;

    private List<Position> currentPath = new ArrayList<>();

    public Spirit(int x, int y, char character, worldGenerator wg) {
        this.x = x;
        this.y = y;
        this.character = character;
        this.wg = wg;

        this.spiritTile = switch (character) {
            case 'c' -> Tileset.CINNAMOROLL_SPIRIT;
            case 'k' -> Tileset.KUROMI_SPIRIT;
            default -> Tileset.MELODY_SPIRIT;
        };


        this.tileUnderSpirit = wg.getFloorTile();
        wg.setTile(x, y, spiritTile);

    }


    // spirit movement dependent on player movement count
    // moves only every N player moves
    public void update(int playerX, int playerY) {
        tickCounter++;
        if (tickCounter % cooldown != 0) return;

        if (!aggressive) return;


        /*
        Position playerPos = new Position(playerX, playerY);
        if (!playerPos.equals(lastPlayerPosition)) {
            currentPath = bfsTo(playerX, playerY);
            lastPlayerPosition = playerPos;
        }
         */

        currentPath = bfsTo(playerX, playerY);


        if (currentPath.size() > 1) {
            Position nextPos = currentPath.get(1);

            /*
            if (nextPos.equals(new Position(playerX, playerY))) {
                return; // don't move onto player
            }
             */

            // restore tile under spirit
            wg.setTile(x, y, tileUnderSpirit);

            // move
            x = nextPos.getX();
            y = nextPos.getY();

            // save new tile under spirit
            tileUnderSpirit = wg.getTile(x, y);

            // overwrite with spirit tile
            wg.setTile(x, y, spiritTile);

        }


    }


    private List<Position> bfsTo(int targetX, int targetY) {
        Map<Position, Position> cameFrom = new HashMap<>();

        Position start = new Position(x, y);  // spirit
        Position goal = new Position(targetX, targetY);  // avatar

        Queue<Position> fringe = new LinkedList<>();
        fringe.add(start);
        cameFrom.put(start, null);

        while (!fringe.isEmpty()) {
            Position curr = fringe.remove();

            if (curr.equals(goal)) {
                break;
            }


            // visit neighbor if not already visited
            // also allow stepping onto the goal tile, even if it's not normally walkable
            for (Position neighbor : neighbors(curr)) {
                if (!cameFrom.containsKey(neighbor) && isPathable(neighbor.getX(), neighbor.getY(), goal)) {
                    fringe.add(neighbor);
                    cameFrom.put(neighbor, curr);
                }
            }
        }

        // path from spirit position to avatar position
        List<Position> path = new ArrayList<>();
        Position curr = goal;
        while (curr != null && cameFrom.containsKey(curr)) {
            path.add(0, curr);  // insert at the front to reverse the path
            curr = cameFrom.get(curr);
        }



        return path;
    }


    private List<Position> neighbors(Position p) {
        return List.of(
                new Position(p.getX() + 1, p.getY()),
                new Position(p.getX() - 1, p.getY()),
                new Position(p.getX(), p.getY() + 1),
                new Position(p.getX(), p.getY() - 1)
        );
    }




    public void setAggressive(boolean on) {
        this.aggressive = on;
    }


    public List<Position> getCurrentPath(int playerX, int playerY) {
        currentPath = bfsTo(playerX, playerY);
        return currentPath;
    }

    public Position getPosition() {
        return new Position(x, y);
    }




    private boolean isPathable(int x, int y, Position goal) {
        TETile tile = wg.getTile(x, y);
        return wg.isWalkable(x, y) || tile == wg.getFoodTile() || new Position(x, y).equals(goal);
    }





}
