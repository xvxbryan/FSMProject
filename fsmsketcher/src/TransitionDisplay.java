import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Leonardo Gutierrez
 * 
 * This class represents the UI representation of a theoretical transition from one state to another. 
 * This works with the SketchFrame class to display state data to the user.
 *
 */
public class TransitionDisplay {

	/**
	 * The state the transition starts from.
	 */
	private StateDisplay fromState;
	/**
	 * The state the transition leads to.
	 */
	private StateDisplay toState;
	/**
	 * The symbols on which the transitions allows travel.
	 */
	private List<String> labels;
	
	/**
	 * The crates a transition display from one state to another.
	 * @param fromState The state the transition starts from.
	 * @param toState The state the transition leads to.
	 */
	public TransitionDisplay(StateDisplay fromState, StateDisplay toState) {
		this.fromState = fromState;
		this.toState = toState;
		this.labels = new ArrayList<>();
	}
	
	/**
	 * Draws the transition to the screen.
	 * 
	 * <teammate documentation>
	 * 
	 * @param g The graphics used to draw.
	 */
	public void draw(Graphics2D g) {
		//If the destination is the source, it is a self loop.
		if (fromState == toState) {
			int peakX = fromState.getX() - 2 * StateDisplay.RADIUS;
			int peakY = fromState.getY() - 2 * StateDisplay.RADIUS;
			QuadCurve2D q = new QuadCurve2D.Float();
			q.setCurve(
					fromState.getX() - StateDisplay.RADIUS, 
					fromState.getY(), 
					peakX, 
					peakY, 
					fromState.getX(), 
					fromState.getY() - StateDisplay.RADIUS);
			g.draw(q);
			for (int i = 0; i < labels.size(); i++) {
				g.drawString(labels.get(i), peakX + StateDisplay.RADIUS + 10 * i, peakY + StateDisplay.RADIUS - 15);	
			}
			g.drawOval(fromState.getX() - 5, fromState.getY() - StateDisplay.RADIUS - 5, 10, 10);
			return;
		}
		
		int startX = fromState.getX();
		int startY = fromState.getY();
		int endX = toState.getX();
		int endY = toState.getY();
		
		int dx = endX - startX;
		int dy = endY - startY;
		
		if (dx < 0) {
			startX -= StateDisplay.RADIUS;
			endX += StateDisplay.RADIUS;
		} else {
			startX += StateDisplay.RADIUS;
			endX -= StateDisplay.RADIUS;
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(startX, startY, endX, endY);
		for (int i = 0; i < labels.size(); i++) {
			String prefix = fromState.getLabel() + " -> " + toState.getLabel() + ": ";
			if (startX > endX) {
				g.drawString(prefix + labels.get(i), (startX + endX) / 2 + 10 * i - 15, (startY + endY) / 2 + 20);
			} else {
				g.drawString(prefix + labels.get(i), (startX + endX) / 2 + 10 * i - 15, (startY + endY) / 2 - 20);
			}
				
		}
		g.fillOval(endX - 5, endY - 5, 10, 10);
	}
	
	/**
	 * Returns the state the transition starts from.
	 * 
	 * @return The start state.
	 */
	public StateDisplay getFromState() {
		return fromState;
	}
	/**
	 * Returns the state the transition leads to.
	 * @return The end state.
	 */
	public StateDisplay getToState() {
		return toState;
	}
	/**
	 * Returns a list of transition symbols.
	 * @return The labels of the transition.
	 */
	public List<String> getLabels() {
		return labels;
	}
}