package BrickDestroy.Gameplay_Model.Brick;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Random;

public class Crack {

    /**Defines the depth of the crack when making a new crack object
     *
     */
    public static final int DEF_CRACK_DEPTH = 5;
    /**Defines how zig-zag is the crack.
     *
     */
    public static final int DEF_STEPS = 35;

    /**Defines the stroke color of the crack.
     *
     */
    private static final Color CRACK_COLOR = new Color(218.0/256, 200.0/256, 176.0/256,1);

    /**Probability of the crack jumping up or down when forming the crack
     *
     */
    private static final double JUMP_PROBABILITY = 0.7;

    /**Constant to signal the ball has hit the left part of the brick.
     */
    public static final int LEFT = 10;
    /**Constant to signal the ball has hit the right part of the brick.
     */
    public static final int RIGHT = 20;
    /**Constant to signal the ball has hit the upper part of the brick.
     */
    public static final int UP = 30;
    /**Constant to signal the ball has hit the lower part of the brick.
     */
    public static final int DOWN = 40;
    /**Constant to signal the Crack object to form a vertical positioned crack.
     */
    public static final int VERTICAL = 100;
    /**Constant to signal the Crack object to form a horizontal positioned crack.
     */
    public static final int HORIZONTAL = 200;

    private static Random rnd;

    /**Contains the detail of the crack to be drawn onto the pane.
     *
     */
    private Path crack;

    /**The depth of the crack when jumps happen during the formation of the crack.
     *
     */
    private int crackDepth;
    /**How zig-zag the crack should be
     *
     */
    private int steps;


    /**Intialises the Crack object with an empty Path and color.
     *
     */
    public Crack() {
        rnd=new Random();
        crack = new Path();
        crack.setStroke(CRACK_COLOR);
        this.crackDepth = DEF_CRACK_DEPTH;
        this.steps = DEF_STEPS;

    }


    /**Makes the crack from the point of impact to the side of the brick that is parallel to side where the ball made contact.
     * <p>
     * Based on the point of impact, a random point is chosen on the other side of the brick to create a crack with.
     * <p>
     * Example: If the ball hits the bottom side of the brick, a random point is selected on the upper side of the brick..
     * Then, a crack is made from the point of impact to the chosen random point on the upper side of the brick.
     * @param point point of contact
     * @param direction direction of the crack to be made (vertical or horizontal)
     * @param brickFace the Shape object of the brick
     */
    protected void makeCrackPath(Point2D point, int direction, Rectangle brickFace) {
        Rectangle bounds = brickFace;

        Point2D impact = new Point2D( point.getX(),  point.getY());
        Point2D start = new Point2D(0,0);
        Point2D end = new Point2D(0,0);


        switch (direction) {
            case LEFT:
                start= new Point2D(bounds.getX() + bounds.getWidth(), bounds.getY());
                end = new Point2D(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
                Point2D tmp = makeRandomPoint(start, end, VERTICAL);
                makeCrackPath(impact, tmp);
                break;
            case RIGHT:
                start = new Point2D(bounds.getX(),bounds.getY());
                end = new Point2D(bounds.getX(), bounds.getY() + bounds.getHeight());
                tmp = makeRandomPoint(start, end, VERTICAL);
                makeCrackPath(impact, tmp);
                break;
            case UP:
                start = new Point2D(bounds.getX(), bounds.getY() + bounds.getHeight());
                end = new Point2D(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
                tmp = makeRandomPoint(start, end, HORIZONTAL);
                makeCrackPath(impact, tmp);
                break;
            case DOWN:
                start = new Point2D(bounds.getX(),bounds.getY());
                end = new Point2D(bounds.getX() + bounds.getWidth(), bounds.getY());
                tmp = makeRandomPoint(start, end, HORIZONTAL);
                makeCrackPath(impact, tmp);

                break;

        }
    }

    /**Chooses points inside the brick and then link them together to finally create the crack.
     * <p>
     * Each of the point chosen has a probability to jump by an amount, making the crack seemingly random.
     * @param start the starting point of the crack
     * @param end the ending point of the crack
     */
    private void makeCrackPath(Point2D start, Point2D end) {

        Path path = crack;
        path.getElements().add(new MoveTo(start.getX(), start.getY()));

        double w = (end.getX() - start.getX()) / (double) steps;
        double h = (end.getY() - start.getY()) / (double) steps;

        int jump = crackDepth;

        double x, y;
        for (int i = 1; i < steps; i++) {

            x = (i * w) + start.getX();
            y = (i * h) + start.getY();

            y += jumps(jump, JUMP_PROBABILITY);

            y=checkIfOutOfBrickBound(y,start,end);
            path.getElements().add(new LineTo(x, y));

        }
        path.getElements().add(new LineTo(end.getX(), end.getY()));
    }

    /**It checks whether, when a point "jumps", it becomes out of bound from the brick or not.
     * <p>
     * If it does becomes out of bound, it is immediately adjusted back into the brick.
     * @param y the y-axis of the point in question (only the y-axis of the point has to jump out of bound)
     * @param start starting point of the crack
     * @param end ending point of the crack.
     * @return returns the value itself back if it does not fall out of bound. Otherwise, return the adjusted value.
     */
    private double checkIfOutOfBrickBound(double y,Point2D start, Point2D end){
        if ( ( start.getY()<=y && y<=end.getY() ) || ( start.getY()>=y && y>=end.getY() ) )
            return y;
        else{
            double yStartDifference = start.getY() - y;
            double yEndDifference = end.getY() - y;
            if (Math.abs(yStartDifference)<Math.abs(yEndDifference))
                return start.getY();
            else return end.getY();
        }

    }

    /**Returns a random number between the range of -ve of the input number and +ve of the input number.
     * @param bound the range of the number in question
     * @return a random number between the range of the -ve of bound to +ve of bound
     */
    private int randomInBounds(int bound) {
        int n = (bound * 2);
        return rnd.nextInt(n) - bound;
    }

    /**Roll dice to whether allow a jump or not
     * @param bound the range of the number to return if a jump does happen
     * @param probability probability of jumping
     * @return theamount  deviation of the point on the y-axis
     */
    private int jumps(int bound, double probability) {

        if (rnd.nextDouble() > probability)
            return randomInBounds(bound);
        return 0;

    }

    /**It chooses the random point on the other side of the brick from the side where the ball made contact.
     * <p>
     * The corner of the sides is used to find the random point between them.
     * @param from the first point on the edge of the side
     * @param to the last point on the edge of the side
     * @param direction direction of the crack
     * @return the random point that is to be used as the end point of the crack
     */
    private Point2D makeRandomPoint(Point2D from, Point2D to, int direction) {

        Point2D out = new Point2D(0,0);
        double pos;

        switch (direction) {
            case HORIZONTAL:
                pos = rnd.nextInt((int) (to.getX() - from.getX())) +  from.getX();
                out = new Point2D(pos, to.getY());
                break;
            case VERTICAL:
                pos = rnd.nextInt((int) (to.getY() - from.getY())) + (int)from.getY();
                out = new Point2D(to.getX(), pos);
                break;
        }
        return out;
    }

    /**Gets the Path object used to describe the crack on the brick.
     * @return the Path object of the crack
     */
    public Path getCrackPath() {
        return crack;
    }
}
