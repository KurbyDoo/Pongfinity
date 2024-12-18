import csta.ibm.pong.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A class to represent a selectable button within the game environment allowing
 * for the triggering of specific actions or events when selected by the player.
 */
public class Button extends GameObject{
    private BufferedImage image;
    private int restingY, restingX, dx, dy, time = 0;
    private boolean visible = true, floating = false, active = false;
    /**
     * Constructs a button with an image.
     *
     * @param screenWidth The width of the screen.
     * @param screenHeight The height of the screen.
     * @param imagePath The file path of the image.
     */
    public Button(int screenWidth, int screenHeight, String imagePath) {
        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
        } catch (IOException e) {
            System.out.println(e);
        }
        setSize(screenWidth, screenHeight);
    }

    /**
     * Paints the button component.
     *
     * @param g The graphics context.
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        if (visible) g.drawImage(image, restingX, restingY + dy, null);
    }

    /**
     * Updates the button's state.
     */
    @Override
    public void act() {
        time += 1;
        if (floating) {
            dy = (int) (-10 * Math.sin(time / 4.0));
            if (active) dy -= 20;
        } else {
            dy = 0;
        }
    }
    /**
     * Sets the location of the button.
     *
     * @param x The x-coordinate of the location.
     * @param y The y-coordinate of the location.
     */
    public void setLocation(int x, int y) {
        restingY = y;
        restingX = x;
    }

    /**
     * Gets the width of the image.
     *
     * @return The width of the image.
     */
    public int getImageWidth() {
        return image.getWidth(null);
    }

    /**
     * Sets whether the button is floating or not.
     *
     * @param newValue The new value of the floating flag.
     */
    public void setFloating(boolean newValue) {
        if (newValue != floating) {
            time = 0;
        }
        floating = newValue;
    }

    /**
     * Updates the visibility of the button.
     *
     * @param newVisibility The new visibility value.
     */
    public void updateVisibility(boolean newVisibility) {
        visible = newVisibility;
    }

    /**
     * Activates the button.
     *
     * @param game The Pong game instance.
     */
    public void activate(Pong game) {
        active = true;
    }

    /**
     * Deactivates the button.
     */
    public void deactivate() {
        active = false;
    }

    /**
     * Checks if the button is active.
     *
     * @return True if the button is active, otherwise false.
     */
    public boolean getActive() {
        return active;
    }

}
