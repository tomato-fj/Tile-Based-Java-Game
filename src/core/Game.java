package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;




/*
Your seeds are:
4505030412791415517
5104927075106644929
6179074809671138579
2603456407050626888
907628386347235441
 */

public class Game {
    // private static final long SEED = 907628386347235441L;
    private static final int WORLD_WIDTH = 50;
    private static final int WORLD_HEIGHT = 30;

    private final String filename = "saved.txt";
    private final File file = new File(filename);
    private long seed;
    private String inputHistory = "";
    private TERenderer ter;
    private worldGenerator wg;
    private Avatar avatar;
    private char selectedCharacter = '\0';
    private int foodCollected = 0;
    private final int TOTAL_FOOD = 5;
    private TETile tileUnderAvatar;
    private List<Spirit> spirits = new ArrayList<>();
    private boolean showPaths = false;
    private int playerMoveCount = 0;
    private boolean portalPlaced;
    private boolean foodCollectedThisTick = false;





    public Game() {
        StdDraw.setCanvasSize(WORLD_WIDTH * 16, WORLD_HEIGHT * 16);
        StdDraw.setXscale(0, WORLD_WIDTH);
        StdDraw.setYscale(0, WORLD_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        char firstKey = InitialScreen.drawInitialScreen();
        playWithKeyboard(firstKey);


    }

    // start the game!
    private void drawStoryPage() {
        StdDraw.setXscale(0, WORLD_WIDTH);
        StdDraw.setYscale(0, WORLD_HEIGHT);
        StdDraw.enableDoubleBuffering();

        while (true) {
            StdDraw.clear(new Color(255, 240, 250)); // bg

            // title
            StdDraw.setPenColor(new Color(253, 120, 139));
            StdDraw.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 4, "🌸 Sanrio Sweet Escape 🌸");

            // story lines
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 8, "In a dreamy world filled with sparkling skies,");
            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 10, "My Melody, Cinnamoroll, and Kuromi are lost in a magical maze!");

            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 14, "To find their way home, they must gather five enchanted sweets. 🍬");
            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 16, "But beware: after three sweets, spirits awaken — chasing them every four steps! 👻");

            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 20, "Stay quick, stay clever — and reach the Dream Portal before it's too late! 🌟");

            // blinking "Press ENTER" at the bottom
            double tick = System.currentTimeMillis() / 300.0;
            double bounce = Math.sin(tick) * 0.3;

            StdDraw.setPenColor(new Color(253, 120, 139));
            StdDraw.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
            StdDraw.text(WORLD_WIDTH / 2.0, 5 + bounce, "✨ Press ENTER to Begin Your Adventure ✨");

            StdDraw.show();
            StdDraw.pause(16);

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == '\n' || c == '\r') {
                    break; // player pressed Enter
                }
            }
        }
    }




    public void playWithKeyboard(char key) {
        switch (key) {
            case 'n' -> {
                drawStoryPage();

                selectedCharacter = askCharacter();
                long SEED = askSeed();
                inputHistory = "n" + selectedCharacter + SEED + "s";
                startGameWithSeed(SEED);
            }
            case 'q' -> System.exit(0);
            case 'l' -> loadState();

        }
    }


    private long askSeed() {
        String inputSeed = "";
        drawSeedPrompt(inputSeed);

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 's') {
                    if (inputSeed.isEmpty()) {
                        drawSeedPrompt("Seed can't be empty!");
                        continue; // ask again
                    }
                    break;
                }
                if (Character.isDigit(c)) {
                    inputSeed += c;
                    drawSeedPrompt(inputSeed);
                }
            }
        }
        return Long.parseLong(inputSeed);
    }

    private void drawSeedPrompt(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0 + 2, "Enter Seed:");
        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, seed + "_");
        StdDraw.show();
    }


    public void startGameWithSeed(long seed) {
        initializeGame(seed);
        ter.renderFrame(wg.getWorld());
        runGameLoop();
    }

    private void initializeGame(long seed) {
        this.seed = seed;
        ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT + 5);
        wg = new worldGenerator(WORLD_WIDTH, WORLD_HEIGHT, seed, selectedCharacter);
        wg.generateWorld();
        avatar = spawnAvatar();
        spirits = spawnSpiritsFromRooms();
        tileUnderAvatar = wg.getFloorTile();
        portalPlaced = false;
    }



    // save and load
    private void saveState() {
        Out out = new Out(filename);
        out.print(inputHistory);
        out.close();
        System.out.println("Game saved with input history: " + inputHistory);
    }

    private void loadState() {
        if (!file.exists()) {
            System.out.println("Save file not found.");
            return;
        }
        In in = new In(file);
        if (!in.hasNextLine()) {
            System.out.println("Save file is empty.");
            return;
        }
        inputHistory = in.readLine();
        System.out.println("Loaded with input history: " + inputHistory);
        replayGame();
    }


    private void replayGame() {
        if (inputHistory.length() < 3 || inputHistory.charAt(0) != 'n') {
            System.out.println("Invalid save format.");
            return;
        }
        int i = 2;
        selectedCharacter = inputHistory.charAt(1);
        String seedStr = "";
        while (i < inputHistory.length() && Character.isDigit(inputHistory.charAt(i))) {
            seedStr += inputHistory.charAt(i);
            i++;
        }


        seed = Long.parseLong(seedStr);
        String moves = inputHistory.substring(i + 1);
        initializeGame(seed);


        for (char action : moves.toCharArray()) {
            if (isMoveKey(action)) {
                Position p = getDelta(action);
                moveAvatar(p.getX(), p.getY());
            } else if (action == 'p') {
                showPaths = !showPaths;
            }
        }
        ter.renderFrame(wg.getWorld());
        runGameLoop();
    }


    // spawn avatar and spirits
    private Avatar spawnAvatar() {
        Position spawn = wg.getRandomFloorInRoom(wg.getAvatarRoom());
        Avatar a = new Avatar(spawn.getX(), spawn.getY());
        TETile avatarTile;

        switch (selectedCharacter) {
            case 'c' -> avatarTile = Tileset.CINNAMOROLL_AVATAR;
            case 'k' -> avatarTile = Tileset.KUROMI_AVATAR;
            default -> avatarTile = Tileset.MELODY_AVATAR;
        }


        wg.setTile(spawn.getX(), spawn.getY(), avatarTile);
        return a;
    }


    private List<Spirit> spawnSpiritsFromRooms() {
        List<Spirit> result = new ArrayList<>();
        for (Room r : wg.getSpiritRooms()) {
            Position pos = wg.getRandomFloorInRoom(r);
            if (pos != null) {
                result.add(new Spirit(pos.getX(), pos.getY(), selectedCharacter, wg));
            }
        }
        return result;
    }



    // game loop and move avatar
    private void runGameLoop() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == ':') {
                    while (!StdDraw.hasNextKeyTyped()) StdDraw.pause(10);
                    char next = Character.toLowerCase(StdDraw.nextKeyTyped());
                    if (next == 'q') {
                        saveState();
                        System.exit(0);
                    }
                } else if (isMoveKey(c)) {
                    inputHistory += c;
                    Position delta = getDelta(c);
                    moveAvatar(delta.getX(), delta.getY());
                } else {
                    if (c == 'p') {
                        showPaths = !showPaths;
                        System.out.println("showPaths toggled: " + showPaths);
                        inputHistory += 'p';
                    }

                }
            }


            // ter.renderFrame(wg.getWorld());
            StdDraw.clear(Color.BLACK);
            ter.drawTiles(wg.getWorld());


            if (showPaths) {
                StdDraw.setPenColor(new Color(186, 31, 105, 160));
                int avatarX = avatar.getX();
                int avatarY = avatar.getY();
                for (Spirit s : spirits) {

                    for (Position p : s.getCurrentPath(avatarX, avatarY)) {
                        StdDraw.filledCircle(p.getX() + 0.5, p.getY() + 0.5, 0.15);
                    }
                }
            }


            HUD.drawHUD(wg, (int) StdDraw.mouseX(), (int) StdDraw.mouseY(), WORLD_WIDTH, WORLD_HEIGHT, this);

            StdDraw.show();
            StdDraw.pause(20);
            foodCollectedThisTick = false;

        }
    }


    private void moveAvatar(int dx, int dy) {

        int oldX = avatar.getX();
        int oldY = avatar.getY();
        int newX = oldX + dx;
        int newY = oldY + dy;

        TETile destinationTile = wg.getTile(newX, newY);


        // handle food collection first
        if (isFoodTile(destinationTile)) {
            foodCollected++;
            foodCollectedThisTick = true;

            wg.setTile(newX, newY, wg.getFloorTile());
            destinationTile = wg.getFloorTile();
            System.out.println("Yum! Collected: " + foodCollected + "/" + TOTAL_FOOD);
        }


        // activate spirits after collecting 3 food
        if (foodCollected == 3) {
            for (Spirit s : spirits) {
                s.setAggressive(true);
            }
        }


        // spawn portal after collecting all food
        if (foodCollected == TOTAL_FOOD && !portalPlaced) {
            Position portalPos = wg.getAnyFloorAwayFrom(avatar.getPos(), 2);
            wg.setTile(portalPos.getX(), portalPos.getY(), wg.getPortalTile());
            portalPlaced = true;

        }



        // save the new tile
        if (wg.isWalkable(newX, newY)) {
            wg.setTile(oldX, oldY, tileUnderAvatar);
            tileUnderAvatar = destinationTile;
            avatar.move(dx, dy);
            wg.setTile(newX, newY, getAvatarTile());
        }

        playerMoveCount++;
        //System.out.println(playerMoveCount);


        // check losing condition
        for (Spirit s : spirits) {
            s.update(avatar.getX(), avatar.getY());
            if (s.getPosition().equals(new Position(avatar.getX(), avatar.getY()))) {
                showLoseScreen();
            }
        }


        // check winning condition
        if (foodCollected == TOTAL_FOOD && isPortalTile(tileUnderAvatar)) {
            showWinScreen();
        }
    }


    // win lose screens
    private void showWinScreen() {
        // background color depending on character
        switch (selectedCharacter) {
            case 'm' -> StdDraw.clear(new Color(255, 204, 213));
            case 'k' -> StdDraw.clear(new Color(205, 193, 255));
            case 'c' -> StdDraw.clear(new Color(191, 236, 255));
            default -> StdDraw.clear(Color.PINK);
        }

        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 24));
        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0 + 2, "🎉 You Win! 🎉");

        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 16));
        switch (selectedCharacter) {
            case 'm' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "My Melody is proud of you!");
            case 'c' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "Cinnamoroll flies you home!");
            case 'k' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "Kuromi tosses you a skull and winks!");
        }

        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0 - 2, "Thanks for playing!");
        StdDraw.show();
        StdDraw.pause(5000);
        System.exit(0);
    }




    private void showLoseScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.RED);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 24));
        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0 + 2, "💀 Game Over 💀");
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 16));

        switch (selectedCharacter) {
            case 'm' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "My Melody cries softly...");
            case 'c' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "Cinnamoroll is very sad now.");
            case 'k' -> StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0, "Kuromi is not having it!");
        }

        StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT / 2.0 - 2, "Better luck next time.");
        StdDraw.show();
        StdDraw.pause(5000);
        System.exit(0);
    }


    // tile check
    private boolean isPortalTile(TETile tile) {
        return tile == getPortalTile();
    }


    private boolean isFoodTile(TETile tile) {
        return switch (selectedCharacter) {
            case 'c' -> tile == Tileset.CINNAMOROLL_FOOD;
            case 'k' -> tile == Tileset.KUROMI_FOOD;
            default -> tile == Tileset.MELODY_FOOD;
        };
    }



    private TETile getPortalTile() {
        return switch (selectedCharacter) {
            case 'c' -> Tileset.CINNAMOROLL_GATE;
            case 'k' -> Tileset.KUROMI_GATE;
            default -> Tileset.MELODY_GATE;
        };
    }



    TETile getAvatarTile() {
        return switch (selectedCharacter) {
            case 'c' -> Tileset.CINNAMOROLL_AVATAR;
            case 'k' -> Tileset.KUROMI_AVATAR;
            default -> Tileset.MELODY_AVATAR;
        };
    }
    public char getSelectedCharacter() {
        return selectedCharacter;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public boolean isFoodCollectedThisTick() {
        return foodCollectedThisTick;
    }

    public int getFoodCollected() {
        return foodCollected;
    }

    public int getTotalFood() {
        return TOTAL_FOOD;
    }




    // moving helpers
    private static boolean isMoveKey(char c) {
        return c == 'w' || c == 'a' || c == 's' || c == 'd';
    }

    private static Position getDelta(char c) {
        return switch (c) {
            case 'w' -> new Position(0, 1);
            case 'a' -> new Position(-1, 0);
            case 's' -> new Position(0, -1);
            case 'd' -> new Position(1, 0);
            default -> new Position(0, 0);
        };
    }




    // character menu
    private char askCharacter() {
        double tick = 0;

        while (true) {
            StdDraw.clear(Color.WHITE);
            // pink top ribbon
            double startX = 5;
            double decorY = 31;
            double decorWidth = 10;
            double decorHeight = 7;

            for (double x = startX; x <= 45; x += decorWidth) {
                StdDraw.picture(x, decorY, "core/images/top_ribbon.png", decorWidth, decorHeight);
            }



            // floating animation
            double bounce = Math.sin(tick) * 0.3;

            // title
            StdDraw.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
            StdDraw.setPenColor(new Color(253, 120, 139));
            StdDraw.text(WORLD_WIDTH / 2.0, WORLD_HEIGHT - 5, "✨ Choose Your Character! ✨");

            // draw characters
            StdDraw.picture(12, 15 + bounce, "core/images/my_melody_original.png", 6, 6);
            StdDraw.picture(25, 15 - bounce, "core/images/cinnamoroll_original.png", 6, 6);
            StdDraw.picture(38, 15 + bounce, "core/images/kuromi_original.png", 6, 6);

            // character labels
            StdDraw.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(12, 8, "(M) Melody");
            StdDraw.text(25, 8, "(C) Cinnamoroll");
            StdDraw.text(38, 8, "(K) Kuromi");

            StdDraw.show();
            StdDraw.pause(16);
            tick += 0.05;

            // check for input
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'm' || c == 'c' || c == 'k') {
                    return c;
                }
            }
        }
    }









}
