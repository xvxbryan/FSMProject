import java.util.*;

public class FSM implements StateMachine{
	
    public static class pair {
        char ch;
        int nS;
        
        public pair(char c, int ns) {
            ch = c;
            nS = ns;
        }
        
        boolean isEqual(pair a)
        {
            return (a.ch == this.ch || a.nS == this.nS);
        }
    }
    public static class AdjList extends ArrayList<ArrayList<pair>>{}
    
    private ArrayList<Integer> states;
    private int initialState;
    private ArrayList<Character> alphabet;
    private AdjList transitions;
    private TreeMap finalStates;
    
    public FSM()
    {
        states = new ArrayList<>();
        initialState = 0;
        alphabet = new ArrayList<>();
        transitions = new AdjList();
        finalStates = new TreeMap();
    }
    
    
    @Override
    public int getNumberOfStates() {
        return states.size();
    }

    @Override
    public void toggleStateAcceptance(int state) {
        if(finalStates.get(state).equals(1)) {
            finalStates.remove(state);
        }
        else
        {
            finalStates.put(state,1);
        }
            
    }

    @Override
    public boolean isAcceptState(int state) {
        return (finalStates.get(state).equals(1));
    }

    @Override
    public void setInitialState(int state) {
        initialState = state;
    }

    @Override
    public int getInitialState() {
        return initialState;
    }

    @Override
    public int addState() {
        states.add(states.size());
        ArrayList<pair> temp = new ArrayList<>();
        transitions.add(temp);
        return 0;
    }

    @Override
    public void removeState(int state) {
        states.remove(state);
    }

    @Override
    public void addTransition(int fromState, int toState, char symbol) {
        pair temp = new pair(symbol, toState);
        transitions.get(fromState).add(temp);
    }

    @Override
    public void removeTransition(int fromState, int toState, char symbol) {
        pair temp = new pair(symbol, toState);
        for(int i = 0; i < transitions.get(fromState).size(); i++)
        {
            if (transitions.get(fromState).get(i).isEqual(temp))
            {
                transitions.get(fromState).remove(i);
                break;
            }
        }
    }
    
    @Override
    public void setFinalState(int state)
    {
        finalStates.put(state,1);
    }

    @Override
    public ArrayList<Character> getTransitionsCharactersBetween(int fromState, int toState) 
    {
        ArrayList<Character> transitionChars = new ArrayList<>();
        for(pair trans : transitions.get(fromState))
        {
            transitionChars.add(trans.ch);
        }
        return transitionChars;
    }

    
    @Override
    public HashSet<Integer> nextStates(HashSet<Integer> states, char symbol) 
    {
        HashSet<Integer> next = new HashSet<>(alphabet.size());
        for (int state : states)
        {
            for (pair trans : transitions.get(state))
            {
                if (trans.ch == symbol)
                {
                    next.add(trans.nS);
                }
            }
        }
        return next;
    }
    
    @Override
    public boolean validateWord(char[] word)
    {
        boolean valid = true;
        for (char C : word)
        {
            boolean in = false;
            for (char ch : alphabet)
            {
                if (ch == C)
                {
                    in = true;
                    break;
                }
            }
            if (!in)
            {
                valid = false;
                break;
            }    
        }
        return valid;
    }
    
    @Override
    public boolean acceptsWord(char[] word) 
    {
        if (validateWord(word))
        {
            HashSet<Integer> current_states = new HashSet(alphabet.size());
            current_states.add(initialState);
            for(char C : word)
            {
                current_states = nextStates(current_states,C);
                if (current_states.isEmpty())
                {
                    return false;
                }
            }
            for(int state : current_states)
            {
                if (finalStates.containsKey(state))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public ArrayList<Character> getAlphabet(int size)
    {
        char letter = 'a';
        for(int i = 0; i < size; i++)
        {
            alphabet.add(letter);
            letter++;
        }
        return alphabet; 
    }
    
    @Override
    public boolean validateRE(char[] reg_exp)
    {
        HashSet<Character> non_bracket = new HashSet<>();
        for(char a : alphabet)
        {
            non_bracket.add(a);
        }
        non_bracket.add('+');
        non_bracket.add('*');
        
        HashMap<Character,Integer> open = new HashMap<>();
        HashMap<Character,Integer> close = new HashMap<>();
        
        open.put('(', 1);
        close.put(')', 1);
        open.put('[', 2);
        close.put(']', 2);
        open.put('{', 3);
        close.put('}', 3);
        
        Stack<Character> S = new Stack<>();
        
        for(char a : reg_exp)
        {
            if (!non_bracket.contains(a))
            {
                if (open.containsKey(a))
                {
                    S.add(a);
                }
                else if (close.containsKey(a))
                {
                    char b = S.pop();
                    if (open.get(b) != close.get(a))
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        
        return S.empty();
    }
    
	
}