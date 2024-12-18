import csta.ibm.pong.GameObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The Menu class represents a menu within the Pong game environment.
 * It extends the GameObject class and provides methods to manage menu components,
 * such as buttons and controls.
 */
public class Menu extends GameObject {
    private final int FONT_SIZE = 50;
    private BufferedImage titleImage;
    private int titleX = 0, titleY = 0, time = 0, textTime = 0, screenWidth, screenHeight;
    private boolean titleVisible = true, buttonsPressed = false, gameOver = false;
    private String upperTextRow = "ABCDEFG", lowerTextRow = "BCDEF";
    private JLabel upperLabel = new JLabel("ABCDEFG"), lowerLabel = new JLabel("BCDEF");
    private ButtonManager buttonManager;

    /**
     * Constructs the main menu of the Pong game.
     *
     * @param screenWidth  The width of the screen.
     * @param screenHeight The height of the screen.
     * @param game         The Pong game instance.
     */
    public Menu(int screenWidth, int screenHeight, Pong game) {
        try {
            titleImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("pong_title.png"));
        } catch (IOException e) {
            System.out.println(e);
        }

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        setSize(screenWidth, screenHeight);
        titleX = (screenWidth - titleImage.getWidth()) / 2;

        buttonManager = new ButtonManager(screenWidth, screenHeight, game);
        game.add(upperLabel);
        game.add(lowerLabel);
        upperLabel.setForeground(Color.WHITE);
        lowerLabel.setForeground(Color.WHITE);
        upperLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, FONT_SIZE));
        lowerLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, FONT_SIZE));

        buttonManager.selectButton(game);

    }

    /**
     * Paints the menu components.
     *
     * @param g The graphics context.
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        titleX = (screenWidth - titleImage.getWidth()) / 2;

        // Paint buttons
        buttonManager.paint(g);

        // Paint title image if visible
        if (titleVisible && !gameOver) {
            g.drawImage(titleImage, titleX, titleY, null);
        }
    }


    /**
     * Updates the state of the menu.
     */
    @Override
    public void act() {

        time += 1;
        updateY();
        repaint();
        // Update the visibility of buttons based on title visibility
        buttonManager.updateVisibility(titleVisible && !gameOver);


        if (gameOver) {
            upperLabel.setVisible(true);
            lowerLabel.setVisible(true);
            upperLabel.setBounds(0, 0, screenWidth, screenHeight / 2);
            lowerLabel.setBounds(0, 0, screenWidth, screenHeight / 2);
            upperLabel.setLocation((screenWidth - upperTextRow.length() * (int) (FONT_SIZE * 0.5)) / 2, 0);
            lowerLabel.setLocation((screenWidth - lowerTextRow.length() * (int) (FONT_SIZE * 0.5)) / 2, (int) (FONT_SIZE * 1.2));
            upperLabel.setText(upperTextRow.substring(0, Math.min(upperTextRow.length(), Math.max(0, (time - textTime) / 3))));
            lowerLabel.setText(lowerTextRow.substring(0, Math.min(lowerTextRow.length(), Math.max(0, (time - textTime - lowerTextRow.length() * 3) / 3))));
        } else {
            upperLabel.setVisible(false);
            lowerLabel.setVisible(false);
        }
    }

    /**
     * Updates the y-coordinate of the title image.
     */
    private void updateY() {
        titleY = (int) (20 * Math.sin(time / 15.0)) + 100;
    }


    /**
     * Processes key presses for navigating and interacting with menu buttons.
     *
     * @param zPressed    Whether the Z key is pressed.
     * @param xPressed    Whether the X key is pressed.
     * @param nPressed    Whether the N key is pressed.
     * @param mPressed    Whether the M key is pressed.
     * @param game        The Pong game instance.
     */
    public void processPressed(boolean zPressed, boolean xPressed, boolean nPressed, boolean mPressed, Pong game) {
        if (!buttonsPressed) {
            // Change selection based on key presses
            if (zPressed) {
                buttonManager.changeSelection(-1);
            } else if (xPressed) {
                buttonManager.changeSelection(1);
            } else if (mPressed) {
                buttonManager.selectButton(game);
            } else if (nPressed) {
                buttonManager.deselectButton(game);
            }
        }

        // Update buttonsPressed flag
        buttonsPressed = zPressed || xPressed || nPressed || mPressed;

        // Update screen dimensions
        screenHeight = game.getFieldHeight();
        screenWidth = game.getFieldWidth();
    }

    /**
     * Updates the visibility of the menu title.
     *
     * @param newVisibility The new visibility value.
     */
    public void updateVisibility(boolean newVisibility, boolean isGameOver) {
        titleVisible = newVisibility;
        if (isGameOver != gameOver) {
            gameOver = isGameOver;
            textTime = time + 10;
        }
    }

    /**
     * Sets the winner of the game and updates the upper and lower text rows accordingly.
     *
     * @param leftWin True if the left player wins, false otherwise.
     */
    public void setWinner(boolean leftWin) {
        if (leftWin) {
            upperTextRow = "Left Player Wins!";
        } else {
            upperTextRow = "Right Player Wins!";
        }

        lowerTextRow = "Press Space To Restart";
    }
}