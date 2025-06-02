package core;


public class Avatar {
    private int x, y;

    public Avatar(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public Position getPos() { return new Position(x, y); }

}
