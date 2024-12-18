import csta.ibm.pong.GameObject;

import java.awt.*;

/**
 * The PowerUp class represents a power-up within the game environment.
 * It extends the GameObject class and provides methods to control the power-up's appearance,
 * behavior, and effects on paddles and balls within game.
 */
public class PowerUp extends GameObject {
    private enum Type {SIZE_INCREASE, SPEED_INCREASE, EXTRA_BALL, SIZE_DEBUFF, SPEED_DEBUFF};
    private final Type powerUpType;
    private Color currentColour;
    /**
     * Constructs a new PowerUp object with a random type and position.
     *
     * @param screenWidth The width of the game screen.
     * @param screenHeight The height of the game screen.
     */
    public PowerUp(int screenWidth, int screenHeight) {
        // Set initial size and color for the PowerUp
        setSize(20, 20);
        setColor(Color.YELLOW);

        // Select a random type for the PowerUp
        powerUpType = Type.values()[(int) (Math.random() * Type.values().length)];

        // Set size for the PowerUp based on its type
        setSize(30, 30);

        // Set random position for the PowerUp within the game screen boundaries
        setX((int) ((((screenWidth - getWidth()) * 8.0 / 10) * Math.random()) + ((screenWidth - getWidth()) * 1.0/10)));
        setY((int) ((screenHeight - getHeight()) * Math.random()));

        // Determine color based on PowerUp type
//        currentColour = switch (powerUpType) { // Commented out for 1.8 jar release
//            case SIZE_INCREASE, SPEED_INCREASE ->  new Color(180, 255, 0);
//            case SIZE_DEBUFF, SPEED_DEBUFF -> new Color(255, 165, 0);
//            case EXTRA_BALL -> new Color(255, 223, 12);
//        };
         switch (powerUpType) {
             case SIZE_INCREASE:
             case SPEED_INCREASE: currentColour = new Color(180, 255, 0); break;
             case SIZE_DEBUFF:
             case SPEED_DEBUFF: currentColour = new Color(255, 165, 0); break;
             case EXTRA_BALL: currentColour = new Color(255, 223, 12); break;
        };

        // Set color for the PowerUp
        setColor(currentColour);
    }



    @Override
    public void act() {

    }

    /**
     * Returns the color of the PowerUp.
     *
     * @return The color of the PowerUp.
     */
    public Color getColor() {
        return currentColour;
    }

    /**
     * Checks if the PowerUp collides with a ball and activates its effect if applicable.
     *
     * @param currentBall The ball to check collision with.
     * @param leftPaddle The left paddle.
     * @param rightPaddle The right paddle.
     * @param mainGame The main Pong game instance.
     * @return True if the PowerUp collides with the ball and its effect is activated, otherwise false.
     */
    public boolean checkCollides(Ball currentBall, Paddle leftPaddle, Paddle rightPaddle, Pong mainGame) {
        // Check if the PowerUp collides with the ball
        if (!currentBall.collides(this)) {
            return false;
        }
        // Activate the PowerUp effect based on the last paddle that touched the ball
        if (currentBall.lastTouchedLeft()) {
            activateEffect(leftPaddle, rightPaddle, mainGame);
        } else if (currentBall.lastTouchedRight()) {
            activateEffect(rightPaddle, leftPaddle, mainGame);
        }
        return true;
    }

    /**
     * Activates the effect of the PowerUp on the specified paddle and main game instance.
     *
     * @param friendly The friendly paddle to apply the effect.
     * @param opponent The opponent paddle.
     * @param mainGame The main Pong game instance.
     */
    private void activateEffect(Paddle friendly, Paddle opponent, Pong mainGame) {
        // Switch statement to determine the effect based on the PowerUp type
//        switch (powerUpType) { // Commented out for 1.8 jar release
//            case SIZE_INCREASE -> friendly.changePaddleHeight(1.2);
//            case SIZE_DEBUFF -> opponent.changePaddleHeight(0.8);
//            case SPEED_INCREASE -> friendly.changePaddleSpeed(0.1);
//            case SPEED_DEBUFF -> opponent.changePaddleSpeed(-0.2);
//            case EXTRA_BALL -> mainGame.addNewBall();
//        }
        switch (powerUpType) { // Commented out for 1.8 jar release
            case SIZE_INCREASE: friendly.changePaddleHeight(1.2); break;
            case SIZE_DEBUFF: opponent.changePaddleHeight(0.8); break;
            case SPEED_INCREASE: friendly.changePaddleSpeed(0.1); break;
            case SPEED_DEBUFF: opponent.changePaddleSpeed(-0.2); break;
            case EXTRA_BALL: mainGame.addNewBall(); break;
        }
    }

}
