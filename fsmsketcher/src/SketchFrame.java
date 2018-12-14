import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

/**
 * 
 * @author Leonardo Gutierrez
 * 
 * The window that is used by the user. Responsible for displaying the FSM information
 * correctly and handling input.
 *
 */
public class SketchFrame extends JFrame implements MouseInputListener, MouseWheelListener, KeyListener {
	private static final long serialVersionUID = 1L; //Boiler Code
	
	/**
	 * The states currenlly displayed.
	 */
	private List<StateDisplay> stateDisplays;
	/**
	 * The transitions currently displayed.
	 */
	private List<TransitionDisplay> transitionDisplays;
	/**
	 * The states currently selected.
	 */
	private Stack<StateDisplay> selectedStates;
	/**
	 * Text used to notify the user of the program's status.
	 */
	private JTextField notificationTextField;
	/**
	 * The state machine logic.
	 */
	private FSM stateMachine;
	/**
	 * The alphabet size.
	 */
	private int alphabetSize;
	
	double moveX, moveY;
	
	private AffineTransform transform;
	private SketchPanel sketchPanel;
	
	/**
	 * Creates a SketchFrame. Upon calling this, the program is started.
	 * The screen starts off in a 800x600 resolution, but it can be resized.
	 */
	public SketchFrame() {
		//Setup JFrame
		super("FSM Sketcher");
		sketchPanel = new SketchPanel();
		sketchPanel.addMouseListener(this);
		sketchPanel.addMouseWheelListener(this);
		sketchPanel.setFocusable(true);
		setFocusable(true);
		sketchPanel.addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLayout(new BorderLayout());
		//setResizable(false);
		setLocationRelativeTo(null);
		add(sketchPanel);
		
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(6, 1));
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new GridLayout(1, 1));
		
		//Initialize members
		stateDisplays = new ArrayList<>();
		transform = new AffineTransform();
		transitionDisplays = new ArrayList<>();
		selectedStates = new Stack<>();
		notificationTextField = new JTextField();
		stateMachine = null;
		
		//Hook up the listener and the text field.
		notificationTextField.setText("Let's get started!");
		notificationTextField.setEditable(false);
		notificationTextField.setHorizontalAlignment(JTextField.CENTER);
		add(notificationTextField, BorderLayout.SOUTH);
		
		//Create and hook up the Add State button.
		JButton addStateButton = new JButton();
		addStateButton.setText("Add State");
		addStateButton.addActionListener(new ActionListener() {
			Random random = new Random();
			@Override
			public void actionPerformed(ActionEvent e) {
				String label = JOptionPane.showInputDialog("Enter State Label");
				if (label == null) {
					updateJFrame("State label not entered.");
					return;
				}
				int x = StateDisplay.RADIUS + random.nextInt(sketchPanel.getWidth() - StateDisplay.RADIUS);
				int y = StateDisplay.RADIUS + random.nextInt(sketchPanel.getHeight() - StateDisplay.RADIUS);
				stateDisplays.add(new StateDisplay(label, x, y, stateDisplays.size()));
				if (stateDisplays.size() == 1) {
					stateDisplays.get(0).setIsInitialState(true);
				}
				validateStateMachine();
				updateJFrame(null);
			}
		});
		westPanel.add(addStateButton);
		
		//Create and hook up the Remove State button.
		JButton removeStateButton = new JButton("Remove State");
		removeStateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!selectedStates.isEmpty()) {
					stateDisplays.removeAll(selectedStates);
					ArrayList<TransitionDisplay> toRemove = new ArrayList<>();
					
					for (TransitionDisplay t : transitionDisplays) {
						if (selectedStates.contains(t.getFromState()) || selectedStates.contains(t.getToState())) {
							toRemove.add(t);
						}
					}
					
					transitionDisplays.removeAll(toRemove);
					
					deselectAllStates();
					validateStateMachine();
					updateJFrame(null);
				} else {
					updateJFrame("No states were selected.");
				}
			}
		});
		westPanel.add(removeStateButton);
		
		//Create and hook up the Add Transition button.
		JButton addTransitionButton = new JButton("Add Transition");
		addTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedStates.size() > 2) {
					updateJFrame("Too many states selected.");
					return;
				}
				if (selectedStates.isEmpty()) {
					updateJFrame("No states selected");
					return;
				}
				
				String transitionLabel = JOptionPane.showInputDialog("Enter Transition Label");
				if (transitionLabel == null) {
					updateJFrame("Transition label was not given.");
					return;
				}
				
				//StateD
				TransitionDisplay transition;
				if (selectedStates.size() == 1) {
					transition = getTransitionDisplay(selectedStates.get(0), selectedStates.get(0));
				} else {
					transition = getTransitionDisplay(selectedStates.get(0), selectedStates.get(1));
				}
				if (transition == null) {
					TransitionDisplay newTransition;
					if (selectedStates.size() == 1) {
						newTransition = new TransitionDisplay(selectedStates.get(0), selectedStates.get(0));
					} else {
						newTransition = new TransitionDisplay(selectedStates.get(0), selectedStates.get(1));
					}
					newTransition.getLabels().add(transitionLabel);
					transitionDisplays.add(newTransition);
					validateStateMachine();
					updateJFrame(null);
				} else {
					if (transition.getLabels().contains(transitionLabel)) {
						updateJFrame("That transition already exists.");
					} else {
						transition.getLabels().add(transitionLabel);
						validateStateMachine();
						updateJFrame(null);
					}
				}
				deselectAllStates();
			}
		});
		westPanel.add(addTransitionButton, BorderLayout.WEST);
		
		//Create and hook up the Remove Transition button
		JButton removeTransitionButton = new JButton("Remove Transition");
		removeTransitionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedStates.size() < 2) {
					updateJFrame("Too few states selected.");
					return;
				}
				if (selectedStates.size() > 2) {
					updateJFrame("Too many states selected.");
					return;
				}
				
				String transitionLabel = JOptionPane.showInputDialog("Enter Transition Label");
				if (transitionLabel == null) {
					updateJFrame("Transition label was not given.");
					return;
				}
				

				TransitionDisplay transition = getTransitionDisplay(selectedStates.get(0), selectedStates.get(1));
				if (transition == null) {
					updateJFrame("No such transition.");
					return;
				} else {
					if (transition.getLabels().contains(transitionLabel)) {
						transition.getLabels().remove(transitionLabel);
						if (transition.getLabels().isEmpty()) {
							transitionDisplays.remove(transition);
						}
						validateStateMachine();
						updateJFrame(null);
					} else {
						updateJFrame("No such transition symbol.");
					}
				}
				deselectAllStates();
			}
		});
		westPanel.add(removeTransitionButton);
		
		//Create and hook up Select Initial State Button.
		JButton selectInitialButton = new JButton("Select Initial State");
		selectInitialButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedStates.size() != 1) {
					updateJFrame("Invalid number of selected states.");
					return;
				}
				for (StateDisplay sd : stateDisplays) {
					sd.setIsInitialState(false);
				}
				selectedStates.get(0).setIsInitialState(true);
				validateStateMachine();
				updateJFrame(null);
			}
		});
		westPanel.add(selectInitialButton);
		
		//Create and hook up Toggle Final State Button.
		JButton toggleFinalStatesButton = new JButton("Toggle Finale States");
		toggleFinalStatesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (StateDisplay sd : selectedStates) {
					sd.setIsFinalState(!sd.isFinalState());
				}
				deselectAllStates();
				validateStateMachine();
				updateJFrame(null);
			}
		});
		westPanel.add(toggleFinalStatesButton);
		
		//Create and hook up and Test String Button
		JButton testStringButton = new JButton("Test String");
		testStringButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String subject = JOptionPane.showInputDialog("Enter string to test");
				if (!stateMachine.validateWord(subject.toCharArray())) {
					updateJFrame("Not a valid string!");
					return;
				} 
				if (stateMachine.acceptsWord(subject.toCharArray())) {
					updateJFrame("Accept");
				} else {
					updateJFrame("Reject");
				}
			}
		});
		eastPanel.add(testStringButton);
		
		
		add(westPanel, BorderLayout.WEST);
		add(eastPanel, BorderLayout.EAST);
		
		//Finish creating the JFrame
		setVisible(true);
		
		while(true) {
			try {
				alphabetSize = Integer.parseInt(JOptionPane.showInputDialog("Enter the size of the alphabet"));
				break;
			}
			catch(Exception e) {
				
			}
			
		}
		
	}
	
	/**
	 * Redraws the JFrame and sets the notification text.
	 * 
	 * @param notificationText The notification text. If null, the text field will be invisible.
	 */
	public void updateJFrame(String notificationText) {
		if (notificationText != null) {
			notificationTextField.setText(notificationText);
			notificationTextField.setVisible(true);
		} else {
			notificationTextField.setVisible(false);
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Updates the FSM class to match what the visuals show.
	 */
	public void validateStateMachine() {
		stateMachine = new FSM();
		for (int i = 0; i < stateDisplays.size(); i++) stateMachine.addState();
		for (StateDisplay sd : stateDisplays) {
			if (sd.isInitialState()) {
				stateMachine.setInitialState(sd.getStateID());
			}
			if (sd.isFinalState()) {
				stateMachine.setFinalState(sd.getStateID());
			}
		}
		stateMachine.getAlphabet(alphabetSize);
		for (TransitionDisplay td : transitionDisplays) {
			for (String label : td.getLabels()) {
				stateMachine.addTransition(td.getFromState().getStateID(), 
										   td.getToState().getStateID(), 
										   label.charAt(0));
			}
		}
	}
	
	/**
	 * Returns a state display with the given label.
	 * 
	 * @param label The label used to select a StateDisplay.
	 * 
	 * @return The StateDisplay with the givel label.
	 */
	public StateDisplay getStateDisplay(String label) {
		if (label == null) return null;
		for (StateDisplay display : stateDisplays) {
			if (display.getLabel().equals(label)) {
				return display;
			}
		}
		return null;
	}
	
	/**
	 * Returns a state display with the given stateID.
	 * 
	 * @param stateID The id used to select a StateDisplay.
	 * 
	 * @return The StateDisplay with the givel label.
	 */
	public StateDisplay getStateDisplay(int stateID) {
		for (StateDisplay display : stateDisplays) {
			if (display.getStateID() == stateID) { 
				return display;
			}
		}
		return null;
	}
	
	/**
	 * De-selects all the states.
	 */
	public void deselectAllStates() {
		for (StateDisplay sd : stateDisplays) {
			sd.setIsSelected(false);
		}
		selectedStates.clear();
	}
	
	/**
	 * Returns a TransitionDisplay with a given fromState and toState.
	 * 
	 * @param fromState The fromState used to select the transition.
	 * @param toState The toState used to select the transition.
	 * 
	 * @return The transition with the given to and from states.
	 */
	public TransitionDisplay getTransitionDisplay(StateDisplay fromState, StateDisplay toState) {
		if (fromState == null || toState == null) return null;
		for (TransitionDisplay td : transitionDisplays) {
			if (td.getFromState() == fromState && td.getToState() == toState) return td;
		}
		return null;
	}
	
	/**
	 * 
	 * @author Leonardo Gutierrez
	 *
	 * The panel used to draw. It coveres the entire JFrame
	 *
	 */
	public class SketchPanel extends JPanel {
		private static final long serialVersionUID = 1L; //Boiler code
		
		/**
		 * Draws all the state displays and transition diplays onto the screen.
		 */
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		    
		    
		    
		    //Draw background
		    g2d.setColor(Color.GRAY.brighter());
			g2d.fillRect(0, 0, super.getWidth(), super.getHeight());
			g2d.setColor(Color.GRAY);
			g2d.transform(transform);
			//int sideLength = 40;
		    //for (int i = 0; i < 1 + getWidth() / sideLength; i++) g2d.drawLine(i * sideLength, 0, i * sideLength, getHeight());
		    //for (int i = 0; i < 1 + getHeight() / sideLength; i++) g2d.drawLine(0, i * sideLength, getWidth(), i * sideLength);
		    
			
			for (StateDisplay sd : stateDisplays) {
				sd.draw(g2d);
			}
			for (TransitionDisplay td : transitionDisplays) {
				td.draw(g2d);
			}
			
			g.dispose();
		}
	}
	
	
	//---MOUSE LISTENER METHODS---

	/**
	 * Selects states when clicked on. If there is a single state selected, and they 
	 * click empty space, the state will be moved.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D p = new Point2D.Double();
		try {
			p = transform.inverseTransform(new Point2D.Double(e.getX(), e.getY()), new Point2D.Double());
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		
		boolean stateSelected = false;
		for (StateDisplay sd : stateDisplays) {
			if (Math.hypot(p.getX() - sd.getX(), p.getY() - sd.getY()) <  StateDisplay.RADIUS) {
				if (selectedStates.contains(sd)) {
					selectedStates.remove(sd);
				} else {
					selectedStates.push(sd);
				}
				sd.setIsSelected(!sd.isSelected());
				stateSelected = true;
				updateJFrame(null);
			}
		}
		if (!stateSelected) {
			if (selectedStates.size() == 1) {
				int ix = (int) ((e.getX() - transform.getTranslateX()) / transform.getScaleX());
				int iy = (int) ((e.getY() - transform.getTranslateY()) / transform.getScaleX());
				selectedStates.get(0).setX(ix);
				selectedStates.get(0).setY(iy);
				deselectAllStates();
				updateJFrame(null);	
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double factor = 1 - 0.1 * e.getPreciseWheelRotation() ;
		System.out.println(factor);
		transform.scale(factor, factor);
		updateJFrame(null);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("reach");
		if (e.getKeyCode() == KeyEvent.VK_KP_LEFT) {
			transform.translate(-20, 0);
		}
		if (e.getKeyCode() == KeyEvent.VK_KP_RIGHT) {
			transform.translate(20, 0);
		}
		if (e.getKeyCode() == KeyEvent.VK_KP_UP) {
			transform.translate(0, 20);
		}
		if (e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
			transform.translate(0, -20);
		}
		updateJFrame(null);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}