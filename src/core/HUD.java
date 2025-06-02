package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class HUD {

    public static void drawHUD(worldGenerator wg, int mouseX, int mouseY, int worldWidth, int worldHeight, Game game) {
        // background bar
        StdDraw.setPenColor(new Color(0, 0, 0, 180));
        StdDraw.filledRectangle(worldWidth / 2.0, worldHeight + 2.5, worldWidth / 2.0, 2.5);

        // text color
        StdDraw.setPenColor(Color.WHITE);

        // character icon
        StdDraw.picture(2, worldHeight + 4, game.getAvatarTile().getFilepath(), 2, 2);


        // food icon
        StdDraw.picture(6, worldHeight + 4, wg.getFoodTile().getFilepath(), 1.5, 1.5);

        // food count text
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 16));
        StdDraw.text(8.5, worldHeight + 4, game.getFoodCollected() + " / " + game.getTotalFood());


        // tile description under mouse
        if (wg.inBorders(mouseX, mouseY)) {
            TETile tile = wg.getTile(mouseX, mouseY);
            if (tile != null) {
                StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 12));
                StdDraw.text(worldWidth / 2.0, worldHeight + 1, tile.description());
            }
        }

        // "[P] Show/Hide Paths" hint
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 14));
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.textLeft(2, worldHeight + 1, "[P] Show/Hide Paths");


        // "+1" when food collected this tick
        if (game.isFoodCollectedThisTick()) {
            // color based on selected character
            switch (game.getSelectedCharacter()) {
                case 'k' -> StdDraw.setPenColor(new Color(248, 225, 183));
                case 'm' -> StdDraw.setPenColor(new Color(100, 74, 7));
                case 'c' -> StdDraw.setPenColor(new Color(33, 52, 72));
                default -> StdDraw.setPenColor(Color.WHITE);
            }

            StdDraw.setFont(new Font("SansSerif", Font.BOLD, 18));
            StdDraw.text(game.getAvatar().getX() + 0.5, game.getAvatar().getY() + 1.0, "+1");
        }


    }


}
