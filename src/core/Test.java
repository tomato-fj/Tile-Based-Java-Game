package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.Tileset;

import java.awt.*;

public class Test {

    public static void main(String[] args) {
        StdDraw.setCanvasSize(512, 512);
        StdDraw.setXscale(0, 10);
        StdDraw.setYscale(0, 10);
        StdDraw.clear(Color.BLACK);

        Tileset.CINNAMOROLL_AVATAR.draw(4, 4);




        StdDraw.show();

    }

}
