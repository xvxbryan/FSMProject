import java.awt.Color;
import java.awt.Graphics2D;

/**
 * 
 * @author Leonardo Gutierrez
 * 
 * This class represents the UI representation of a theoretical state. 
 * This works with the SketchFrame class to display state data to the user.
 *
 */
public class StateDisplay {
	
	/**
	 * Radius of circles to be drawn.
	 */
	public static final int RADIUS = 20;
	
	private static final Color SELECTED_STATE_COLOR = Color.RED.brighter();
	private static final Color CURRENT_STATE_COLOR = Color.YELLOW.brighter();
	private static final Color DEFAULT_COLOR = Color.WHITE;
	
	/**
	 * The string that will be displayed on the state. Will be used by the user to identify the state.
	 */
	private String label;
	/**
	 * The x coordinate of the state's center.
	 */
	private int x;
	/**
	 * The y coordinate of the state's center.
	 */
	private int y;
	/**
	 * The corresponding id of the state in the FSM class.
	 */
	private int stateID;
	/**
	 * Marks whether the state is an initial state.
	 */
	private boolean isInitialState;
	/**
	 * Marks whether the state is a final state.
	 */
	private boolean isFinalState;
	/**
	 * Marks whether the current state is being "stepped" through in the simulation.
	 */
	private boolean isCurrentState;
	/**
	 * Marks whether the user has marked the state.
	 */
	private boolean isSelected;
	
	
	/**
	 * Creates a state display with a label, location, radius, and stateID.
	 * 
	 * @param label The string that will be displayed on the state. If it is null, the stateID will be used.
	 * @param x The x coordinate of the state's center.
	 * @param y The y coordinate of the state's center.
	 * @param r The radius of the circle representing the state.
	 * @param stateID The id of the corresponding state in the FSM class.
	 */
	public StateDisplay(String label, int x, int y, int stateID) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.stateID = stateID;
	}
	
	/**
	 * Fills are draws a circle from it's center. Used to aid the 'void draw(Graphics2D)' method.
	 * 
	 * @param g The graphics used to draw the circle.
	 * @param x The x coordinate of the center.
	 * @param y The y coordinate of the center.
	 * @param radius The radius of the circle to draw.
	 * @param fill If it's true, the circle is filled. Otherwise, only the outer rim is drawn.
	 */
	private void paintCenterCircle(Graphics2D g, int x, int y, int r, boolean fill) {
		if (fill) {
			g.fillOval(x - r, y - r, 2 * r, 2 * r);
		} else {
			g.drawOval(x - r, y - r, 2 * r, 2 * r);
		}
	}
	
	/**
	 * Draws the state onto the screen. 
	 * 
	 * <teamate documentation here>
	 * 
	 * @param g The graphics used to draw the state.
	 */
	public void draw(Graphics2D g) {
		g.setColor(DEFAULT_COLOR);
		paintCenterCircle(g, x, y, RADIUS, true);
		if (isSelected) {
			g.setColor(SELECTED_STATE_COLOR);
			paintCenterCircle(g, x, y, RADIUS, true);
		}
		g.setColor(Color.BLACK);
		paintCenterCircle(g, x, y, RADIUS, false);
		if (isFinalState) {
			paintCenterCircle(g, x, y, 3 * RADIUS / 4, false);
		}
		if (isInitialState) {
			//Draw arrow
			g.drawLine(x - 2 * RADIUS, y, x - RADIUS, y);
			g.drawLine(x - 5 * RADIUS / 4, y + 5, x - RADIUS, y);
			g.drawLine(x - 5 * RADIUS / 4, y - 5, x - RADIUS, y);
		}
		if (isCurrentState) {
			g.setColor(CURRENT_STATE_COLOR);
			paintCenterCircle(g, x, y, RADIUS / 3, true);
		}
		
		
		g.setColor(Color.BLACK);
		paintCenterCircle(g, x, y, RADIUS, false);
		g.drawString(label, x, y);
	}
	
	/**
	 * Returns the string that is drawn on the state.
	 * 
	 * @return The string drawn on the state.
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * Returns the x coordinate of the state's circle's center.
	 * @return The x coordinate of the center.
	 */
	public int getX() {
		return x;
	}
	/**
	 * Returns the y coordinate of the state's circle's center.
	 * @return The y coordinate of the center.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the stateID of the corresponding state in the FSM class.
	 * @return The corresponding stateID.
	 */
	public int getStateID() {
		return stateID;
	}
	/**
	 * Returns whether the state represented is an initial state.
	 * @return Whether the state is an initial state.
	 */
	public boolean isInitialState() {
		return isInitialState;
	}
	/**
	 * Returns whether the state represented is an final state.
	 * @return Whether the state is an final state.
	 */
	public boolean isFinalState() {
		return isFinalState;
	}
	/**
	 * Returns whether the state represented is an current state.
	 * @return Whether the state is an current state.
	 */
	public boolean isCurrentState() {
		return isCurrentState;
	}
	/**
	 * Returns whether the state represented is selected.
	 * @return Whether the state is selected.
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Set's the string to be drawn over the state. This will be used to identify the state.
	 * If the label is set to null, the stateID will be used.
	 * 
	 * @param label The string to be drawn over the state.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * Set's the x coordinate of the circle's center.
	 * @param x The new x coordinate of the circles center.
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * Set's the y coordinate of the circle's center.
	 * @param y The new y coordinate of the circles center.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Marks whether the state should be represented as an initial state.
	 * @param isInitialState true if it should be initial, false otherwise
	 */
	public void setIsInitialState(boolean isInitialState) {
		this.isInitialState = isInitialState;
	}
	/**
	 * Marks whether the state should be represented as a final state.
	 * @param isInitialState true if it should be final, false otherwise
	 */
	public void setIsFinalState(boolean isFinalState) {
		this.isFinalState = isFinalState;
	}
	/**
	 * Marks whether the state should be represented as a current state.
	 * @param isInitialState true if it should be current, false otherwise
	 */
	public void setIsCurrentState(boolean isCurrentState) {
		this.isCurrentState = isCurrentState;
	}
	/**
	 * Marks whether the state should be represented as a selected state.
	 * @param isInitialState true if it should be selected, false otherwise
	 */
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}