import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.awt.Color;

/**
 * Simple Checker game application. It demonstrates the alpha-beta 
 * search algorithm for move selection and pruning.
 *
 * @author A Part of GUI comes from: http://math.hws.edu/eck/cs124/javanotes3/source/Checkers.java
 * 		   - Modified by Shang Da
 * 		   Architecture of CheckerGame, CheckerState, and Alpha-Beta Search comes from 
 * 	 	   the online code repository of the AIMA textbook
 * 		   url: http://aima.cs.berkeley.edu/code.html
 * 		   - Implemented by Shang Da
 */
public class CheckerApp extends Applet {
	/* The main applet class only lays out the applet.  The work of
    the game is all done in the CheckersCanvas object.   Note that
    the Buttons and Label used in the applet are defined as 
    instance variables in the CheckersCanvas class.  The applet
    class gives them their visual appearance and sets their
    size and positions.*/

 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() {
	 
	    setLayout(null);  // I will do the layout myself.
	 
	    setBackground(new Color(0,150,0));  // Dark green background.
	    
	    /* Create the components and add them to the applet. */
	
	    CheckersCanvas board = new CheckersCanvas();
	        // Note: The constructor creates the buttons board.resignButton
	        // and board.newGameButton and the Label board.message.
	    add(board);
	
	    board.newGameButton.setBackground(Color.lightGray);
	    add(board.newGameButton);
	
	    board.resignButton.setBackground(Color.lightGray);
	    add(board.resignButton);
	
	    board.message.setForeground(Color.green);
	    board.message.setFont(new Font("Serif", Font.BOLD, 16));
	    add(board.message);
	    
	    /* Set the position and size of each component by calling
	       its setBounds() method. */
	
	    board.setBounds(20,20,800,800); // Note:  size MUST be 800-by-800 !
	    board.newGameButton.setBounds(840, 640, 250, 80);
	    board.resignButton.setBounds(840, 740, 250, 80);
	    board.message.setBounds(800, 200, 330, 50);
	    setSize(1110,840);
	 }
	 
	} // end class Checkers
	

class CheckersCanvas extends Canvas implements ActionListener, MouseListener {
	
	   // This canvas displays a 160-by-160 checkerboard pattern with
	   // a 2-pixel black border.  It is assumed that the size of the
	   // canvas is set to exactly 800-by-800 pixels.  This class does
	   // the work of letting the users play checkers, and it displays
	   // the checkerboard.
	
	 /**
		 * 
		 */
		 private static final long serialVersionUID = 1L;
		 Button resignButton;   // Current player can resign by clicking this button.
		 Button newGameButton;  // This button starts a new game.  It is enabled only
		                        //     when the current game has ended.
		 
		 Label message;   // A label for displaying messages to the user.
		 
		 CheckerGame game;
		 CheckerState currentState;
		 int currentPlayer;
		
		 boolean gameInProgress; // Is a game currently in progress?
		 
		 /* The next three variables are valid only when the game is in progress. */
		    
		 
		 int selectedRow, selectedCol;  // If the current player has selected a piece to
		                                //     move, these give the row and column
		                                //     containing that piece.  If no piece is
		                                //     yet selected, then selectedRow is -1.
		 
		 int aiPlayer = CheckerState.BLACK;//
		 
		 CheckersMove[] legalMoves;

		 public CheckersCanvas() {
		        // Constructor.  Create the buttons and lable.  Listen for mouse
		        // clicks and for clicks on the buttons.  Create the board and
		        // start the first game.
		    setBackground(Color.black);
		    addMouseListener(this);
		    setFont(new  Font("Serif", Font.BOLD, 14));
		    resignButton = new Button("Resign");
		    resignButton.setFont(new Font("Serif", Font.BOLD, 28));
		    resignButton.addActionListener(this);
		    newGameButton = new Button("New Game");
		    newGameButton.addActionListener(this);
		    newGameButton.setFont(new Font("Serif", Font.BOLD, 28));
		    message = new Label("",Label.CENTER);
	    
		    doNewGame();
	 }
	 
	
	 public void actionPerformed(ActionEvent evt) {
	       // Respond to user's click on one of the two buttons.
	    Object src = evt.getSource();
	    if (src == newGameButton)
	       doNewGame();
	    else if (src == resignButton)
	       doResign();
	 }
	 
	
	 void doNewGame() {
		 		    
	    game = new CheckerGame();
	    currentState = game.getInitialState();
	    currentPlayer = CheckerState.RED;
	    legalMoves = currentState.getLegalMoves();
		 
	    if(aiPlayer == CheckerState.RED) {
	    	currentState.setAI(CheckerState.RED);
	        CheckersMove newMove = getAIMove(currentState);
	        doMakeMove(newMove);
	    }
	       // Begin a new game.
	    if (gameInProgress == true) {
	           // This should not be possible, but it doens't 
	           // hurt to check.
	       message.setText("Finish the current game first!");
	       return;
	    }
	    	    
	    selectedRow = -1;   // RED has not yet selected a piece to move.
	    message.setText("Red:  Make your move.");
	    gameInProgress = true;
	    newGameButton.setEnabled(false);
	    resignButton.setEnabled(true);
	    repaint();
	 }
	 
	
	 void doResign() {
	        // Current player resigns.  Game ends.  Opponent wins.
	     if (gameInProgress == false) {
	        message.setText("There is no game in progress!");
	        return;
	     }
	     if (game.getPlayer(currentState) == CheckerState.RED)
	        gameOver("RED resigns.  BLACK wins.");
	     else
	        gameOver("BLACK resigns.  RED winds.");
	 }
	 
	
	 void gameOver(String str) {
	        // The game ends.  The parameter, str, is displayed as a message
	        // to the user.  The states of the buttons are adjusted so playes
	        // can start a new game.
	    message.setText(str);
	    newGameButton.setEnabled(true);
	    resignButton.setEnabled(false);
	    gameInProgress = false;
	 }
	    
	
	 void doClickSquare(int row, int col) {
	       // This is called by mousePressed() when a player clicks on the
	       // square in the specified row and col.  It has already been checked
	       // that a game is, in fact, in progress.
	       
	    /* If the player clicked on one of the pieces that the player
	       can move, mark this row and col as selected and return.  (This
	       might change a previous selection.)  Reset the message, in
	       case it was previously displaying an error message. */
	
	    for (int i = 0; i < legalMoves.length; i++)
	       if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
	          selectedRow = row;
	          selectedCol = col;
	          
	          String playerInfo = (currentPlayer == CheckerState.RED? "RED" : "BLACK");
              message.setText(playerInfo + ":  Make your move.");
              
	          repaint();
	          return;
	       }
	
	    /* If no piece has been selected to be moved, the user must first
	       select a piece.  Show an error message and return. */
	
	    if (selectedRow < 0) {
	        message.setText("Click the piece you want to move.");
	        return;
	    }
	    
	    /* If the user clicked on a squre where the selected piece can be
	       legally moved, then make the move and return. */
	
	    for (int i = 0; i < legalMoves.length; i++)
	       if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
	               && legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
	          doMakeMove(legalMoves[i]);
	          return;
	       }
	       
	    /* If we get to this point, there is a piece selected, and the square where
	       the user just clicked is not one where that piece can be legally moved.
	       Show an error message. */
	
	    message.setText("Click the square you want to move to.");
	
	 }  // end doClickSquare()
	 
	
	 void doMakeMove(CheckersMove move) {
	        // This is called when the current player has chosen the specified
	        // move.  Make the move, and then either end or continue the game
	        // appropriately.

		currentState = game.getResult(currentState, move);
	    currentPlayer = currentState.getPlayerToMove();
    	String playerInfo = (currentPlayer == CheckerState.RED? "RED" : "BLACK");
    	String opInfo = (currentPlayer == CheckerState.RED? "BLACK" : "RED");
	    legalMoves = currentState.getLegalMoves();
	    
	    /* Check if a player can keep jumping. If yes, the player must jump.  The same
	       player continues moving.
	    */
	    
	    if(currentState.keepJump()) {
	    	if (currentPlayer != aiPlayer) {
	        	  message.setText("RED:  You must continue jumping.");
		          selectedRow = move.toRow;  // Since only one piece can be moved, select it.
		          selectedCol = move.toCol;
	          }
	          else{
	        	  CheckersMove newMove = getAIMove(currentState);
		       	  doMakeMove(newMove);
		      }
	          repaint();
	          return;
	    }
	    
	    /* The current player's turn is ended, so change to the other player.
	       Get that player's legal moves.  If the player has no legal moves,
	       then the game ends. */
	    
		if(game.isTerminal(currentState)) { 
			gameOver(playerInfo + " has no moves. " + opInfo + "  wins.");
			repaint();
			return;
		}

	    if (currentPlayer == aiPlayer) {
		   message.setText("BLACK:  AI's turn.");
	       CheckersMove newMove = getAIMove(currentState);
	       doMakeMove(newMove);
	    }
	    else {
	       if (currentState.canJump())
	          message.setText("RED:  Make your move.  You must jump.");
	       else
	          message.setText("RED:  Make your move.");
	    }
	    
	    /* Set selectedRow = -1 to record that the player has not yet selected
	        a piece to move. */
	    
	    selectedRow = -1;
	    
	    /* As a courtesy to the user, if all legal moves use the same piece, then
	       select that piece automatically so the use won't have to click on it
	       to select it. */
	    

	    if (legalMoves != null) {
	       boolean sameStartSquare = true;
	       for (int i = 1; i < legalMoves.length; i++)
	          if (legalMoves[i].fromRow != legalMoves[0].fromRow
	                               || legalMoves[i].fromCol != legalMoves[0].fromCol) {
	              sameStartSquare = false;
	              break;
	          }
	       if (sameStartSquare) {
	          selectedRow = legalMoves[0].fromRow;
	          selectedCol = legalMoves[0].fromCol;
	       }
	    }
	    
	    /* Make sure the board is redrawn in its new state. */
	    
	    repaint();
	    
	 }  // end doMakeMove();
	 
	
	 private CheckersMove getAIMove(CheckerState currState) {
		 AdversarialSearch<CheckerState, CheckersMove> search;
		 search = AlphaBetaSearch.createFor(game);
		 CheckersMove action = search.makeDecision(currState);
		 System.out.println(action.toString());
         return action;
	}


	public void update(Graphics g) {
	      // The paint method completely redraws the canvas, so don't erase
	      // before calling paint().
	    paint(g);
	 }
	 
	
	 public void paint(Graphics g) {
	      // Draw  checkerboard pattern in gray and lightGray.  Draw the
	      // checkers.  If a game is in progress, hilite the legal moves.
	    
	    /* Draw a two-pixel black border around the edges of the canvas. */
	    
		  Font myfont = new Font(null,Font.PLAIN,80);
		  g.setFont(myfont);
		   
	    g.setColor(Color.black);
	    g.drawRect(0,0,getSize().width-1,getSize().height-1);
	    g.drawRect(1,1,getSize().width-3,getSize().height-3);
	    
	    /* Draw the squares of the checkerboard and the checkers. */
	    
	    for (int row = 0; row < 8; row++) {
	       for (int col = 0; col < 8; col++) {
	           if ( row % 2 == col % 2 )
	              g.setColor(Color.lightGray);
	           else
	              g.setColor(Color.gray);
	           g.fillRect(2 + col*100, 2 + row*100, 100, 100);
	           switch (currentState.pieceAt(row,col)) {
	              case CheckerState.RED:
	                 g.setColor(Color.red);
	                 g.fillOval(12 + col*100, 12 + row*100, 80, 80);
	                 break;
	              case CheckerState.BLACK:
	                 g.setColor(Color.black);
	                 g.fillOval(12 + col*100, 12 + row*100, 80, 80);
	                 break;
	              case CheckerState.RED_KING:
	                 g.setColor(Color.red);
	                 g.fillOval(12 + col*100, 12 + row*100, 80, 80);
	                 g.setColor(Color.white);
	                 g.drawString("K", 25 + col*100, 80 + row*100);
	                 break;
	              case CheckerState.BLACK_KING:
	                 g.setColor(Color.black);
	                 g.fillOval(12 + col*100, 12 + row*100, 80, 80);
	                 g.setColor(Color.white);
	                 g.drawString("K", 25 + col*100, 80 + row*100);
	                 break;
	           }
	       }
	    }
	  
	    /* If a game is in progress, highlite the legal moves.   Note that legalMoves
	       is never null while a game is in progress. */      
	    
	    if (gameInProgress) {
	          // First, draw a cyan border around the pieces that can be moved.
	       g.setColor(Color.cyan);

	       for (int i = 0; i < legalMoves.length; i++) {
	          g.drawRect(2 + legalMoves[i].fromCol*100, 2 + legalMoves[i].fromRow*100, 98, 98);
	       }
	          // If a piece is selected for moving (i.e. if selectedRow >= 0), then
	          // draw a 2-pixel white border around that piece and draw green borders 
	          // around eacj square that that piece can be moved to.
	       if (selectedRow >= 0) {
	          g.setColor(Color.white);
	          g.drawRect(2 + selectedCol*100, 2 + selectedRow*100, 98, 98);
	          g.drawRect(3 + selectedCol*100, 4 + selectedRow*100, 96, 96);
	          g.setColor(Color.green);
	          for (int i = 0; i < legalMoves.length; i++) {
	             if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
	                g.drawRect(2 + legalMoves[i].toCol*100, 2 + legalMoves[i].toRow*100, 98, 98);
	          }
	       }
	    }
	 }  // end paint()
	 
	 
	 public Dimension getPreferredSize() {
	       // Specify desired size for this component.  Note:
	       // the size MUST be 800 by 800.
	    return new Dimension(800, 800);
	 }
	
	
	 public Dimension getMinimumSize() {
	    return new Dimension(800, 800);
	 }
	 
	
	 public void mousePressed(MouseEvent evt) {
	       // Respond to a user click on the board.  If no game is
	       // in progress, show an error message.  Otherwise, find
	       // the row and column that the user clicked and call
	       // doClickSquare() to handle it.
	    if (gameInProgress == false)
	       message.setText("Click \"New Game\" to start a new game.");
	    else {
	       int col = (evt.getX() - 2) / 100;
	       int row = (evt.getY() - 2) / 100;
	       if (col >= 0 && col < 8 && row >= 0 && row < 8)
	          doClickSquare(row,col);
	    }
	 }
	 
	
	 public void mouseReleased(MouseEvent evt) { }
	 public void mouseClicked(MouseEvent evt) { }
	 public void mouseEntered(MouseEvent evt) { }
	 public void mouseExited(MouseEvent evt) { }
	
	
	}  // end class SimpleCheckerboardCanvas

