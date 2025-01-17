package Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("serial")
public class WorldComponent extends JComponent {

    public static final int GRID_SIZE = 50;
    private static final int ANIMATION_DELAY = 20;
    private static final int WORLD_UPDATE_DELAY = 33;


    private BufferedImage fuelImage;
    private World world;
    private Timer timer;

    private int frame = 0;

    public WorldComponent() {
        super();
        world = new World();
        setPreferredSize(new Dimension(600, 600));

        try {
            fuelImage = ImageIO.read(new File("assets/fuel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RoboGame();
    }

    public void start() {
        timer = new Timer();
        timer.schedule(new AnimationTask(), 0, ANIMATION_DELAY);
        world.start();
    }

    public void reset() {
        if (timer != null)
            timer.cancel();
        world.reset();
        world = new World();
    }

    public void loadRobotProgram(int rob, File code) {
        world.loadRobotProgram(rob, code);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i <= World.SIZE; i++) {
            Line2D hline = new Line2D.Double(0, i * GRID_SIZE, 600, i * GRID_SIZE);
            Line2D vline = new Line2D.Double(i * GRID_SIZE, 0, i * GRID_SIZE, 600);
            g2d.draw(hline);
            g2d.draw(vline);
        }

        for (Point fuel : world.getAvailableFuel()) {
            int x = fuel.x * GRID_SIZE + GRID_SIZE / 2 - fuelImage.getWidth() / 2;
            int y = fuel.y * GRID_SIZE + GRID_SIZE / 2 - fuelImage.getHeight() / 2;
            g2d.drawImage(fuelImage, x, y, null);
        }

        for (int i = 1; i <= 2; i++) {
            Game.Robot rob;
            if ((rob = world.getRobot(i)) != null) rob.draw(g2d, getTimeRatio());
        }
    }

    private double getTimeRatio() {
        int base = frame / WORLD_UPDATE_DELAY;
        return (double) frame / WORLD_UPDATE_DELAY - base;
    }

    private class AnimationTask extends TimerTask {

        public AnimationTask() {
            frame = 0;
        }

        @Override
        public void run() {
            //test for any deaths
            boolean r1dead = world.getRobot(1).isDead();
            boolean r2dead = world.getRobot(2).isDead();
            if (r1dead || r2dead) {
                timer.cancel();
                timer = null;
                String msg = (r1dead && r2dead) ? "Both robots" : r1dead ? "Game.Robot 1 (red)" : "Game.Robot 2 (blue)";
                JOptionPane.showMessageDialog(null, msg + " ran out of fuel!");
                return;
            }

            //logic tick
            frame++;
            if (frame % WORLD_UPDATE_DELAY == 0)
                world.updateWorld();
            repaint();
        }
    }

}
