package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", 0);
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", 1);
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.white, "floor", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);

    // My Melody Theme
    public static final TETile MELODY_AVATAR = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "my melody", "core/images/my_melody.png", 13);
    public static final TETile MELODY_FLOOR = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "floor", "core/images/melody_floor.png", 14);
    public static final TETile MELODY_WALL = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "wall", "core/images/melody_wall.png", 15);
    public static final TETile MELODY_NOTHING = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "nothing", "core/images/melody_nothing.png", 16);
    public static final TETile MELODY_SPIRIT = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "rabbit", "core/images/melody_rabbit.png", 17);
    public static final TETile MELODY_GATE = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "dream gate", "core/images/melody_house.png", 18);
    public static final TETile MELODY_FOOD = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "apple", "core/images/melody_food.png", 19);


    // Kuromi Theme
    public static final TETile KUROMI_AVATAR = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "kuromi", "core/images/kuromi.png", 20);
    public static final TETile KUROMI_FLOOR = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "floor", "core/images/kuromi_floor.png", 21);
    public static final TETile KUROMI_WALL = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "wall", "core/images/kuromi_wall.png", 22);
    public static final TETile KUROMI_NOTHING = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "nothing", "core/images/kuromi_nothing.png", 23);
    public static final TETile KUROMI_SPIRIT = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "ghost", "core/images/kuromi_ghost.png", 24);
    public static final TETile KUROMI_GATE = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "dream gate", "core/images/kuromi_dreamgate.png", 25);
    public static final TETile KUROMI_FOOD = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "cupcake", "core/images/kuromi_food.png", 26);


    // Cinnamoroll Theme
    public static final TETile CINNAMOROLL_AVATAR = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "cinnamoroll", "core/images/cinnamoroll.png", 27);
    public static final TETile CINNAMOROLL_FLOOR  = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "floor", "core/images/cinnamoroll_floor.png", 28);
    public static final TETile CINNAMOROLL_WALL = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "wall", "core/images/cinnamoroll_wall.png", 29);
    public static final TETile CINNAMOROLL_NOTHING = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "nothing", "core/images/cinnamoroll_nothing1.png", 30);
    public static final TETile CINNAMOROLL_SPIRIT = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "tornado", "core/images/cinnamoroll_tornado.png", 31);
    public static final TETile CINNAMOROLL_GATE = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "dream gate", "core/images/cinnamoroll_skycoffee.png", 32);
    public static final TETile CINNAMOROLL_FOOD = new TETile(' ', new Color(255, 255, 255, 0), new Color(255, 255, 255, 0), "cinnamon", "core/images/cinnamoroll_food.png", 33);
}


