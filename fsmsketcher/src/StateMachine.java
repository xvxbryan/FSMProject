/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.*;

/**
 * 
 * @author Leonardo Gutierrez
 * 
 * An interface for a state machine that can test a list of inputs all at once, or 
 * the machine can set a set of current states one symbol at a time.
 *
 */
public interface StateMachine {
	/**
	 * Returns the number of states in the machine.
	 * 
	 * @return The number of states in the machine.
	 */
	public int getNumberOfStates();
	
	/**
	 * Converts the state given to a accept state if it wasn't an accepts state, or
	 * converts it to a reject state if it was an accept state.
	 * @param state The state to be toggled.
	 */
	public void toggleStateAcceptance(int state);
        
	/**
	 * Tests whether a given state is an accept state.
	 * 
	 * @param state The state to be tested.
	 * 
	 * @return Whether the given state an accept state.
	 */
	public boolean isAcceptState(int state);
	
	/**
	 * Makes a given state the initials state.
	 * 
	 * @param state The state to be made the initial state.
	 */
	public void setInitialState(int state);
        
	/**
	 * Returns the state that is the initial state.
	 * @return The state that is the initial state.
	 */
	public int getInitialState();
	
	/**
	 * Creates a new state.
	 * 
	 * @return The index of the state created.
	 */
	public int addState();
        
	/**
	 * Removes a state from the machine.
	 * 
	 * @param state The state to be removed.
	 */
	public void removeState(int state);
	
	/**
	 * Creates a transition, with a certain symbol, between two states.
	 * 
	 * @param fromState The state the transition is moving from.
	 * @param toState The state the transition leads to.
	 * @param symbol The symbol the transition requires.
	 */
	public void addTransition(int fromState, int toState, char symbol);
        
	/**
	 * Removes a transition from two states.
	 * 
	 * @param fromState The state the transition is moving from.
	 * @param toState The state the transition leads to.
	 * @param symbol The symbol the transition requires.
	 */
	public void removeTransition(int fromState, int toState, char symbol);
        
	/**
	 * Returns all the symbols of transitions between two states.
	 * 
	 * @param fromState The state the transition is moving from.
	 * @param toState The state the transition leads to.
	 * @return The symbols of each transitions.
	 */
	public ArrayList<Character> getTransitionsCharactersBetween(int fromState, int toState);
	
	/**
	 * Steps all the current states to the next current states given a symbol.
	 * 
	 * @param symbol The symbol to step on.
         * @param state blahblah
         * 
         * @return blahblah
	 */
	public HashSet<Integer> nextStates(HashSet<Integer> states, char symbol);
	
	
	/**
	 * See's if the machine accepts a list of symbols. Bypasses the current states and just
	 * uses the initial state.
	 * 
	 * @param symbols The symbols to feed the machine
	 * 
	 * @return Whether the machine accepts the symbols.
	 */
	public boolean acceptsWord(char[] symbols);
        
        /**
         * Sets a given state to be final.
         * 
         * @param state 
         */
        public void setFinalState(int state);
        
        /**
         * 
         * @param word
         * @return 
         */
        public boolean validateWord(char[] word);
        
        /**
         * 
         * @param word
         * @return 
         */
        public boolean validateRE(char[] word);
        
        
        /**
         * 
         * @param size
         * @return 
         */
        public ArrayList<Character> getAlphabet(int size);
}