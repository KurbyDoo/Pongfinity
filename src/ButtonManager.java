import csta.ibm.pong.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The ButtonManager class manages the buttons and controls within the Pong game environment.
 * It extends the GameObject class and provides methods to handle button interactions and selection.
 * */
public class ButtonManager extends GameObject {
    private ArrayList<BufferedImage> controls = new ArrayList<>();
    private ArrayList<BufferedImage> icons = new ArrayList<>();
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int totalUnscaledWidth, selectedButton = 1, controlsWidth = 0, time = 0, screenWidth, screenHeight;
    private Pong mainGame;

    /**
     * Constructs a ButtonManager for the Pong game.
     *
     * @param screenWidth The width of the screen.
     * @param screenHeight The height of the screen.
     * @param game The Pong game instance.
     */
    public ButtonManager(int screenWidth, int screenHeight, Pong game) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        mainGame = game;
        game.add(this);


        // Add buttons to the manager
        buttons.add(new Button(screenWidth, screenHeight, "play_button.png") {
            @Override
            public void activate(Pong game) {
                super.activate(game);
                // Activate the play button and reset the game state
                game.setStatePlaying();
                game.resetGame();
            }
        });
//        buttons.add(new Button(screenWidth, screenHeight, "controls_button.png"));
//        buttons.add(new Button(screenWidth, screenHeight, "settings_button.png"));
        buttons.add(new Button(screenWidth, screenHeight, "help_button.png"));
        buttons.add(new Button(screenWidth, screenHeight, "exit_button.png") {
            @Override
            public void activate(Pong game) {
                // Exit the game when the exit button is activated
                System.exit(0);
            }
        });

        // Calculate total unscaled width of buttons
        for (Button b : buttons) {
            totalUnscaledWidth += b.getImageWidth();
        }

        // Add spacing between buttons
        totalUnscaledWidth += buttons.size() * 20;

        // Position buttons horizontally centered at the bottom of the screen
        int currentX = (screenWidth - totalUnscaledWidth) / 2;
        int currentY = screenHeight * 8 / 10;
        for (Button b : buttons) {
            b.setLocation(currentX, currentY);
            currentX += b.getImageWidth() + 20;
            game.add(b);
        }

        // Load control images
        try {
            controls.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("z_key.png")));
            controls.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("x_key.png")));
            controls.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("controls_button.png")));
            controls.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("n_key.png")));
            controls.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("m_key.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("red_arrow_up.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("red_arrow_down.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("blue_arrow_up.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("blue_arrow_down.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("left_arrow.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("right_arrow.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("cancel_button.png")));
            icons.add(ImageIO.read(getClass().getClassLoader().getResourceAsStream("select_button.png")));
        } catch (IOException e) {
            System.out.println(e);
        }

        // Calculate total width of control images
        controlsWidth += (controls.get(0).getWidth()) * 4;
        controlsWidth += (controls.get(2).getWidth());
    }

    /**
     * Changes the currently selected button by the specified shift amount.
     *
     * @param shift The amount by which to shift the selection.
     */
    public void changeSelection(int shift) {
        // Update the selected button index, ensuring it stays within the bounds of the buttons list
        selectedButton = (selectedButton + shift + buttons.size()) % buttons.size();
    }

    /**
     * Activates the currently selected button.
     *
     * @param game The Pong game instance.
     */
    public void selectButton(Pong game) {
        buttons.get(selectedButton).activate(game);
    }

    /**
     * Deactivates the currently selected button.
     *
     * @param game The Pong game instance.
     */
    public void deselectButton(Pong game) {
        buttons.get(selectedButton).deactivate();
    }

    /**
     * Updates the visibility of all buttons.
     *
     * @param newVisibility The new visibility value.
     */
    public void updateVisibility(boolean newVisibility) {
        for (Button b : buttons) {
            b.updateVisibility(newVisibility);
        }
    }

    /**
     * Paints the buttons and controls.
     *
     * @param g The graphics context.
     */
    public void paint(Graphics g) {
        // Update the location and size of buttons
        int currentX = (screenWidth - totalUnscaledWidth) / 2;
        int currentY = screenHeight * 8 / 10;
        for (Button b : buttons) {
            b.setLocation(currentX, currentY);
            b.setSize(screenWidth, screenHeight);
            currentX += b.getImageWidth() + 20;
        }

        // Draw control images if the controls button is active
//        if (buttons.get(1).getActive()) {
        if (selectedButton == 1) {
            int xPos = (screenWidth - controlsWidth - 20 * (controls.size() - 1)) / 2;
            for (int i = 0; i < controls.size(); i++) {
                g.drawImage(controls.get(i), xPos, (int) (screenHeight / 2 - 5 * Math.sin((time + i * 20) / 4.0)), null);
                if (i > 2) {
                    g.drawImage(icons.get(i - 1), xPos, (int) (screenHeight / 2 - 5 * Math.sin((time + i * 20) / 4.0)) - 100, null);
                    g.drawImage(icons.get(i - 1 + 4), xPos, (int) (screenHeight / 2 - 5 * Math.sin((time + i * 20) / 4.0)) + 80, null);

                } else if (i < 2) {
                    g.drawImage(icons.get(i), xPos, (int) (screenHeight / 2 - 5 * Math.sin((time + (i) * 20) / 4.0)) - 100, null);
                    g.drawImage(icons.get(i + 4), xPos, (int) (screenHeight / 2 - 5 * Math.sin((time + (i) * 20) / 4.0)) + 80, null);

                }
                xPos += controls.get(i).getWidth() + 20;
            }
        }
    }

    /**
     * Updates the state of the button manager.
     */
    @Override
    public void act() {
        time += 1;
        // Update screen dimensions
        screenHeight = mainGame.getFieldHeight();
        screenWidth = mainGame.getFieldWidth();

        // Update floating state of buttons and deactivate non-selected buttons
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setFloating(i == selectedButton);
            if (i != selectedButton) {
                buttons.get(i).deactivate();
            }
        }
    }

}
