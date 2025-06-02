package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InitialScreen {

    static class Ribbon {
        double x, y;
        double speed;
        String image;

        public Ribbon(double x, double y, double speed, String image) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.image = image;
        }

        public void update() {
            y += speed;
            if (y > 40) y = 0; // reset when offscreen
        }

        public void draw() {
            StdDraw.picture(x, y, image, 1.5, 1.5);
        }
    }

    public static char drawInitialScreen() {
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, 50);
        StdDraw.setYscale(0, 35);
        StdDraw.enableDoubleBuffering();


        List<Ribbon> ribbons = new ArrayList<>();
        String[] ribbonPaths = {
                "core/images/blue_ribbon.png",
                "core/images/pink_ribbon.png",
                "core/images/purple_ribbon.png"
        };

        Random rand = new Random(123);
        for (int i = 0; i < 15; i++) {
            double x = rand.nextDouble() * 50;
            double y = rand.nextDouble() * 35;
            double speed = 0.03 + rand.nextDouble() * 0.05;
            String image = ribbonPaths[rand.nextInt(ribbonPaths.length)];
            ribbons.add(new Ribbon(x, y, speed, image));
        }

        while (true) {
            StdDraw.clear(Color.WHITE);

            // draw floating ribbons
            for (Ribbon r : ribbons) {
                r.update();
                r.draw();
            }

            // draw characters
            StdDraw.picture(10, 9, "core/images/my_melody_original.png", 6, 6);
            StdDraw.picture(25, 9, "core/images/cinnamoroll_original.png", 6, 6);
            StdDraw.picture(40, 9, "core/images/kuromi_original.png", 6, 6);

            // draw bouncing title
            double tick = System.currentTimeMillis() / 300.0;
            double bounce = Math.sin(tick) * 0.3;

            StdDraw.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            StdDraw.setPenColor(new Color(253, 120, 139));
            StdDraw.text(25, 22 + bounce, "Sanrio Sweet Escape");

            // draw instruction text
            StdDraw.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
            StdDraw.setPenColor(Color.DARK_GRAY);
            StdDraw.text(25, 4, "(N) New Game     (L) Load Game     (Q) Quit");

            StdDraw.show();
            StdDraw.pause(16);

            // break on key typed
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'n' || c == 'l' || c == 'q') {
                    return c;
                }
            }
        }
    }
}
