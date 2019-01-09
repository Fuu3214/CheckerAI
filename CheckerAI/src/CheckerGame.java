

import java.util.List;


/**
 * Provides an implementation of the checker game which can be used for
 * experiments with the alpha-beta search.
 * 
 * @author Shang Da
 * 
 */
public class CheckerGame implements Game<CheckerState, CheckersMove, Integer> {

	private  CheckerState initialState = new CheckerState();

	@Override
	public CheckerState getInitialState() {
		return initialState;
	}

	@Override
	public Integer[] getPlayers() {
		return new Integer[] { CheckerState.RED, CheckerState.BLACK };
	}

	@Override
	public Integer getPlayer(CheckerState state) {
		return state.getPlayerToMove();
	}

	@Override
	public List<CheckersMove> getActions(CheckerState state) {	
		return state.getLegalActions();
	}

	@Override
	public CheckerState getResult(CheckerState state, CheckersMove action) {
		CheckerState result = state.clone();
		result.makeMove(action);
		return result;
	}

	@Override
	public boolean isTerminal(CheckerState state) {
		if(state == null) return true;
		return (getActions(state) == null);
	}

	@Override
	public double getUtility(CheckerState state, Integer player) {
		return state.getUtility();
	}
	
	
	
}
