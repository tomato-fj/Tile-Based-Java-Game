package core;

import tileengine.TETile;
import utils.RandomUtils;
import java.util.Random;


public class Room {

    TETile[][] world;
    Random RANDOM;
    Position startPoint; // bottom-left of that room
    int roomWidth;
    int roomHeight;



    public Room(TETile[][] world, Random RANDOM) {
        this.world = world;
        this.RANDOM = RANDOM;
        startPoint = randomStartPoint();
        roomWidth = RandomUtils.uniform(RANDOM, 3, 8);
        roomHeight = RandomUtils.uniform(RANDOM, 3, 8);
    }

    Position randomStartPoint() {
        int startX = RandomUtils.uniform(RANDOM,1, world.length);
        int startY = RandomUtils.uniform(RANDOM,1, world[0].length);
        return new Position(startX, startY);
    }



    public Position getCenter() {
        int centerX = (Left() + Right()) / 2;
        int centerY = (Down() + Up()) / 2;
        return new Position(centerX, centerY);
    }



    int Left() {
        return startPoint.getX();
    }

    int Right() {
        return startPoint.getX() + roomWidth - 1;
    }

    int Up() {
        return startPoint.getY() + roomHeight - 1;
    }

    int Down() {
        return startPoint.getY();
    }


    public boolean contains(int x, int y) {

        return x >= Left() && x <= Right() && y >= Down() && y <= Up();
    }



}
