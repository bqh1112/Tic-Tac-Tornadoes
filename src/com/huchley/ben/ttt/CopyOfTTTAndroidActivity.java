package com.huchley.ben.ttt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CopyOfTTTAndroidActivity extends Activity {
	// define the many variables used
	private Board board;
	private boolean inGame = true;
	private static final String[] pieceTypes = new String[] {"Piece", "Tornado", "Tornado Cancel"};
	private Handler aiHandler;
	private boolean aiInProgress;
	private static final float[][] squareValues = //Values of each square for hard ai.
		//Values are relative to a corner square.
	{
		{1.0f,1.1f,1.2f,1.3f,1.2f,1.1f,1.0f},
		{1.1f,1.3f,1.4f,1.5f,1.4f,1.3f,1.1f},
		{1.2f,1.4f,1.6f,1.7f,1.6f,1.4f,1.2f},
		{1.3f,1.5f,1.7f,1.9f,1.7f,1.5f,1.3f},
		{1.2f,1.4f,1.6f,1.7f,1.6f,1.4f,1.2f},
		{1.1f,1.3f,1.4f,1.5f,1.4f,1.3f,1.1f},
		{1.0f,1.1f,1.2f,1.3f,1.2f,1.1f,1.0f}
	};
	private static long playTimeSum;
	private static long countTimeSum;
	public static long initialTimeSum;
	public static long tornadoTimeSum;
	public static long winCheckTimeSum;
	public static long stalemateTimeSum;
	//these are i/o
	private Spinner type;
	private TextView info;
	private Button rules;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//initialize i/o and the button listener
		board = new Board();
		info = (TextView) findViewById(R.id.info);
		//display the starting info
		info.setText("X has " + board.XTornados + " tornados and " + board.XTCancels + " cancels\nO has " + board.YTornados + " tornados and " + board.YTCancels + " cancels\n");
		if (board.isXTurn)
		{
			info.append("It is X's turn.");
		}
		else
		{
			info.append("It is O's turn.");
		}
		rules = (Button) findViewById(R.id.rules);
		rules.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				Intent instr = new Intent(CopyOfTTTAndroidActivity.this, InstructionsActivity.class);
				startActivity(instr);
			}
		});
		aiInProgress = true;
		//initialize the board piece array and image listeners simultaneously
		// (so that I don't have to make 2 for loops going through the array and getting every image)
		TableLayout t = (TableLayout) findViewById(R.id.tableLayout1);
		for (int i = 0; i < 7; i++)
		{
			TableRow tr = (TableRow) t.getChildAt(i);
			for (int j = 0; j < 7; j++)
			{
				ImageView img = (ImageView) tr.getChildAt(j);
				board.boardPieces[i][j] = new BoardPiece(img);
				img.setClickable(true);
				final int x = i; //so I can pass them into the on click listener, which only accepts final variables
				final int y = j; //I can't make i and j final because then I could not modify them anymore
				img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v)
					{
						if (!inGame)
						{
							CopyOfTTTAndroidActivity.this.finish();
						}
						if (aiInProgress)
							return;
						//if the game is on, read the spinner and pass it and location to play

						int[] move = {type.getSelectedItemPosition(),x,y};		
						Message mes = aiHandler.obtainMessage(0, move);
						mes.sendToTarget();
						type.setSelection(0);
						//							else if (result != Board.INVALID)//if the player moved call the AI
						//							{
						//								aiInProgress = true;
						//								Thread thread = new Thread(new Runnable()
						//								{
						//									@Override
						//									public void run()
						//									{
						//										//run whichever AI i'm using, the first line is the
						//										//normal ai, the other is the stupid one
						//										//aiMove();
						//										stupidAiMove();
						//									}
						//								});
						//								thread.start();
						//								
						//							}


					}
				});
			}
		}
		aiHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg)
			{
				if (!inGame)
				{
					return true;
				}
				int[] move = (int[])msg.obj;
				if (move == null)
				{
					Log.wtf("TTT", "move is null");
				}
				boolean worked = playMove(move);
				if (worked)
				{
					aiInProgress = false;
					int nextTypeOfMover = board.isXTurn?GameStartActivity.xType:GameStartActivity.oType;
					allowPlay(nextTypeOfMover);
				}
				else
				{
					Log.wtf("TTT", "AI move " + move[0] + "," + move[1] + "," + move[2] + " was invalid");
				}
				return true;
			}
		});
		aiInProgress = false;
		int nextTypeOfMover = board.isXTurn?GameStartActivity.xType:GameStartActivity.oType;
		allowPlay(nextTypeOfMover);
		//Ai tester. Pits 2 ais against each other. Comment out image listeners
		//and uncomment this to use.
		//		Thread aiThread = new Thread(new Runnable()
		//		{
		//			@Override
		//			public void run()
		//			{
		//				while(inGame)
		//				{
		//					if (board.isXTurn) easyAiMove();
		//					else stupidAiMove();
		//					try {
		//						Thread.currentThread().sleep(1000);
		//					} catch (InterruptedException e)
		//					{
		//						// oh well
		//					}
		//				}
		//			}
		//		});
		//		aiThread.start();

	}
	//If someone won this will return the correct string name of the result. Otherwise it returns null.
	private String convertReturnToString(int playReturns)
	{
		if (playReturns == Board.X) return "X wins!";
		if (playReturns == Board.Y) return "O wins!";
		if (playReturns == Board.STALEMATE) return "Stalemate!";
		return null;
	}
	//the array will have length 3. the first value is 0 for piece, 1 for tornado, and 2 for tc.
	//the second value is the x, the third is the y.
	private int[] easyAi()
	{
		Board[] boards = new Board[3];
		boards[0] = board.copy();
		boards[1] = board.copy();
		boards[2] = board.copy();
		HashMap<Float, int[]> map = new HashMap<Float,int[]>(); //moves mapped to values
		for (int type = 0; type < 3; type++)
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 7; y++)
				{
					int[] move = {type,x,y};
					boards[0].set(board);
					map.put(easyTestMove(move,boards), move);
				}
		int[] max = max(map);
		return max;
	}
	//returns the likelihood of winning with a certain move, although it is not perfect
	//more is better. 0 is force loss, 1 is force win
	//move must be in same syntax as ai return.
	//at 2nd move if no forced win/loss it interprets likelihood based on # of pieces with an unused tornado or tc counting as 5.
	private Float easyTestMove(int[] move, Board[] boardSet)
	{
		return easyTestMoveHelper(move,0,boardSet);
	}
	//like testMove but it is given the depth searched up to, so it searches to the (4-depth)th move.
	//assumes that no one has won yet on b
	private Float easyTestMoveHelper(int[] move, int depth, Board[] boardSet)
	{
		Board b = boardSet[depth];
		boolean turn = b.isXTurn; //the original turn so that play does not make it play for the wrong side
		int result = b.play(move[0],move[1],move[2]);
		if (result == Board.X) return turn?1.f:0.f;
		if (result == Board.Y) return turn?0.f:1.f;
		if (result == Board.STALEMATE) return 0.5f;
		if (result == Board.INVALID) return -1.f;
		if (depth == 1)
		{
			int playerType = turn?BoardPiece.X:BoardPiece.O;
			int enemyType = (!turn)?BoardPiece.X:BoardPiece.O;
			float friendly = 0.f;
			float enemy = 0.f;
			for (int i = 0; i < 7; i++)
				for (int j = 0; j < 7; j++)
				{
					if (b.boardPieces[i][j].type == playerType)
					{
						friendly+=squareValues[i][j];
					}
					if (b.boardPieces[i][j].type == enemyType)
					{
						enemy+=squareValues[i][j];
					}
				}
			int friendlybonus = (turn?b.XTornados:b.YTornados) + (turn?b.XTCancels:b.YTCancels);
			friendly+=(5*friendlybonus);
			int enemybonus = ((!turn)?b.XTornados:b.YTornados) + ((!turn)?b.XTCancels:b.YTCancels);
			enemy+=(5*enemybonus);
			return (Float) (friendly/(enemy+friendly));
		}
		//otherwise
		ArrayList<Float> vals = new ArrayList<Float>();
		for (int type = 0; type < 3; type++)
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 7; y++)
				{
					int[] newmove = {type,x,y};
					boardSet[depth+1].set(b);
					vals.add(easyTestMoveHelper(newmove, depth+1, boardSet));
				}
		float worst = 0.f;
		for (Float val : vals)
		{
			worst = Math.max(worst, val); //The value of a move is 1 minus the worst case
			//scenario that can result. The enemy's move being high is bad so this finds worst case.
		}
		return 1.f-worst;

	}
	private int[] max(HashMap<Float,int[]> map)
	{
		Set<Float> values = map.keySet();
		float maxValue = -Float.MAX_VALUE;
		for (Float d : values)
		{
			maxValue = d>maxValue?d:maxValue;
		}
		if (maxValue < 0.0f)
		{
			throw new RuntimeException("tried to max a map with only invalid moves: " + map.toString());
		}
		int[] result = map.get(maxValue);
		Log.i("TTT", "max value is " + maxValue);
		return result;
	}
	/**
	 * This runs on a separate thread.
	 */
	private void easyAiMove()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//oh well
		}
		int[] move = easyAi();
		//thread stuff
		Message mes = aiHandler.obtainMessage(0, move);
		mes.sendToTarget();


	}
	private boolean playMove(int[] move)
	{
		int result = board.play(move[0], move[1], move[2]);
		if (result == Board.INVALID) return false;
		updateImages(); //if the move did anything refresh the board's images
		info.setText("X has " + board.XTornados + " tornados and " + board.XTCancels + " cancels\nO has " + board.YTornados + " tornados and " + board.YTCancels + " cancels\n");
		info.append(board.isXTurn?"It is X's turn.":"It is O's turn.");
		if (result != Board.NO_ONE) //don't need to worry about invalid
		{
			//end game
			info.setText("Last game's result: " + convertReturnToString(result) + "\nTap the board again to restart.");
			inGame = false;
			type.setVisibility(8);
			rules.setVisibility(8);

		}
		return true;
	}
	private void updateImages()
	{
		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				board.boardPieces[i][j].updateImage();
			}
		}
	}
	private void stupidAiMove()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			//oh well
		}
		StupidAI noob = new StupidAI();
		int[] move = noob.playRandomly(board);
		Message mes = aiHandler.obtainMessage(0, move);
		mes.sendToTarget();
	}
	/**
	 * assumes aiInProgress = false
	 * @param id 0 1 or 2 for player easy ai or stupid ai
	 */
	private void allowPlay(int id)
	{
		if (id == 0) return;
		aiInProgress = true;
		if (id == 1)
		{
			Thread aiThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					stupidAiMove();
				}
			});
			aiThread.start();
		}
		else if (id == 2)
		{
			Thread aiThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					easyAiMove();
				}
			});
			aiThread.start();
		}
		else if (id == 3)
		{
			Thread aiThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					hardAiMove();
				}
			});
			aiThread.start();
		}

	}
	private int[] hardAi(int maxDepth)
	{
		ArrayList<Board> boards = new ArrayList<Board>();
		boards.add(board.copy());
		boards.add(board.copy()); //add 2 boards at start so that I can't get accidental
		//"board does not exist" errors before the automatic board addition starts
		//HashMap<Float, int[]> map = new HashMap<Float,int[]>(); //moves mapped to values
		int[] move = new int[3];
		ArrayList<MoveValue> moveValues = new ArrayList<MoveValue>(147);
		for (int type = 0; type < 3; type++)
		{
			move[0] = type;
			for (int x = 0; x < 7; x++)
			{
				move[1] = x;
				for (int y = 0; y < 7; y++)
				{
					move[2] = y;
					Board b = boards.get(0);
					b.set(board);
					boolean isXTurn = b.isXTurn;
					float value = 0.f;
					int result = b.play(move[0],move[1],move[2]);
					if (result == Board.X) value = isXTurn?1.f:0.f;
					else if (result == Board.Y) value = isXTurn?0.f:1.f;
					else if (result == Board.STALEMATE) value = 0.5f;
					else if (result == Board.INVALID) value = -1.f;
					else
					{
						float friendly = 0.f;
						float enemy = 0.f;
						for (int i = 0; i < 7; i++)
							for (int j = 0; j < 7; j++)
							{
								if (b.boardPieces[i][j].type == (isXTurn?BoardPiece.X:BoardPiece.O))
								{
									//if (2<=i && i <= 4 && 2 <= j && j <= 4) friendly+=0.2; //center bonus
									friendly+=squareValues[i][j];
								}
								if (b.boardPieces[i][j].type == (isXTurn?BoardPiece.O:BoardPiece.X))
								{
									//if (2<=i && i <= 4 && 2 <= j && j <= 4) enemy+=0.2; //center bonus
									enemy+=squareValues[i][j];
								}
							}
						int friendlybonus = (isXTurn?b.XTornados:b.YTornados) + (isXTurn?b.XTCancels:b.YTCancels);
						friendly+=(5*friendlybonus);
						int enemybonus = ((!isXTurn)?b.XTornados:b.YTornados) + ((!isXTurn)?b.XTCancels:b.YTCancels);
						enemy+=(5*enemybonus);
						value = friendly / (friendly+enemy);
					}
					moveValues.add(new MoveValue(move, value));
				}
			}
		}
		//sort movevalues by value from high to low (compareTo is intentionally backwards)
		Collections.sort(moveValues);
		int[] bestMove = new int[3];
		float bestValue = -10.f;
		for (MoveValue m : moveValues)
		{
			move = m.move;
			boards.get(0).set(board);
			float value = alphabetaHardTestMove(move,boards, maxDepth, bestValue);
			//bestValue in that parameter list is used as the starting alpha value
			//for the alphabeta recursion, thus cutting off branches based on level 0
			if (value > bestValue)
			{
				bestValue = value;
				bestMove = move;
			}
		}
		Log.i("TTT", "bestValue is " + bestValue + ", bestMove is " + toString(bestMove));
		//int[] max = max(map);
		if (bestMove == null)
		{
			Log.w("TTT", "bestMove in max is null");
		}
		return bestMove;
	}
	//returns the likelihood of winning with a certain move, although it is not perfect
	//more is better. 0 is force loss, 1 is force win
	//move must be in same syntax as ai return.
	//at (maxDepth)th move if no forced win/loss it interprets likelihood based on # of pieces with an unused tornado or tc counting as 5.
	private Float hardTestMove(int[] move, ArrayList<Board> boardSet, int maxDepth)
	{
		return hardTestMoveHelper(move,0,boardSet, maxDepth);
	}
	//like testMove but it is given the depth searched up to, so it searches to the (4-depth)th move.
	//assumes that no one has won yet on b
	private Float hardTestMoveHelper(int[] move, int depth, ArrayList<Board> boardSet, int maxDepth)
	{
		if (boardSet.size() <= depth+1) boardSet.add(board.copy());
		Board b = boardSet.get(depth);
		boolean turn = b.isXTurn; //the original turn so that play does not make it play for the wrong side
		long beforePlay;
		if (Util.DEBUG) beforePlay = SystemClock.uptimeMillis();
		int result = b.play(move[0],move[1],move[2]);
		long afterPlay;
		if (Util.DEBUG)
		{
			afterPlay = SystemClock.uptimeMillis();
			playTimeSum+=(afterPlay-beforePlay);
		}
		if (result == Board.X) return turn?1.f:0.f;
		if (result == Board.Y) return turn?0.f:1.f;
		if (result == Board.STALEMATE) return 0.5f;
		if (result == Board.INVALID) return -1.f;
		if (depth == maxDepth)
		{
			int playerType = turn?BoardPiece.X:BoardPiece.O;
			int enemyType = (!turn)?BoardPiece.X:BoardPiece.O;
			float friendly = 0.f;
			float enemy = 0.f;
			for (int i = 0; i < 7; i++)
				for (int j = 0; j < 7; j++)
				{
					if (b.boardPieces[i][j].type == playerType)
					{
						//if (2<=i && i <= 4 && 2 <= j && j <= 4) friendly+=0.2; //center bonus
						friendly+=squareValues[i][j];
					}
					if (b.boardPieces[i][j].type == enemyType)
					{
						//if (2<=i && i <= 4 && 2 <= j && j <= 4) enemy+=0.2; //center bonus
						enemy+=squareValues[i][j];
					}
				}
			int friendlybonus = (turn?b.XTornados:b.YTornados) + (turn?b.XTCancels:b.YTCancels);
			friendly+=(5*friendlybonus);
			int enemybonus = ((!turn)?b.XTornados:b.YTornados) + ((!turn)?b.XTCancels:b.YTCancels);
			enemy+=(5*enemybonus);
			long afterCount;
			if (Util.DEBUG)
			{
				afterCount = SystemClock.uptimeMillis();
				countTimeSum+=(afterCount-afterPlay);
			}
			if (b.boardPieces[move[1]][move[2]].isEmpty())
			{
				return (Float) (0.1f * friendly/(enemy+friendly)); //don't play ghosts
			}
			return (Float) (friendly/(enemy+friendly));
		}
		//otherwise
		float worst = 0.f;
		for (int type = 0; type < 3; type++)
			for (int x = 0; x < 7; x++)
				for (int y = 0; y < 7; y++)
				{
					int[] newmove = {type,x,y};
					boardSet.get(depth+1).set(b);
					worst = Math.max(worst, hardTestMoveHelper(newmove, depth+1, boardSet, maxDepth));
					//The value of a move is 1 minus the worst case scenario that can result.
					//The enemy's move being high is bad so this finds worst case.
				}
		if (worst > 0.05f && b.boardPieces[move[1]][move[2]].isEmpty())
		{
			return .1f * (1.f-worst); //don't play ghosts (unless you win with them)
		}
		if (worst < 0.05f) return 0.999f-worst; //winning sooner is better
		return 1.f-worst;

	}
	//TODO make alphabeta work
	private Float alphabetaHardTestMove(int[] move, ArrayList<Board> boardSet, int maxDepth, float initialAlpha)
	{
		Float result = alphabetaHardTestMoveHelper(move,0,boardSet, maxDepth, -10.f, Float.MAX_VALUE, true);
		if (result == null)
		{
			Log.wtf("TTT", "alphabetaHardTestMoveHelper returned null");
		}
		return result;
	}
	private Float alphabetaHardTestMoveHelper(int[] move, int depth, ArrayList<Board> boardSet, int maxDepth, 
			float alpha, float beta, boolean isMaximizing)
	{
		if (boardSet.size() <= depth+1) boardSet.add(board.copy());
		Board b = boardSet.get(depth);
		boolean isXTurn = b.isXTurn; //the original turn so that play does not make it play for the wrong side
		boolean isMaximizingX = !(isMaximizing^isXTurn);
		long beforePlay;
		if (Util.DEBUG) beforePlay = SystemClock.uptimeMillis();
		int result = b.play(move[0],move[1],move[2]);
		long afterPlay;
		if (Util.DEBUG)
		{
			afterPlay = SystemClock.uptimeMillis();
			playTimeSum+=(afterPlay-beforePlay);
		}
		if (result == Board.X)
		{
			return isMaximizingX?1.f:0.f;
		}
		if (result == Board.Y) return isMaximizingX?0.f:1.f;
		if (result == Board.STALEMATE) return 0.5f;
		if (result == Board.INVALID) return -1.f;
		if (depth == maxDepth)
		{
			
			int maximizingType = isMaximizingX?BoardPiece.X:BoardPiece.O;
			int minimizingType = isMaximizingX?BoardPiece.O:BoardPiece.X;
			float friendly = 0.f;
			float enemy = 0.f;
			for (int i = 0; i < 7; i++)
				for (int j = 0; j < 7; j++)
				{
					if (b.boardPieces[i][j].type == maximizingType)
					{
						//if (2<=i && i <= 4 && 2 <= j && j <= 4) friendly+=0.2; //center bonus
						friendly+=squareValues[i][j];
					}
					if (b.boardPieces[i][j].type == minimizingType)
					{
						//if (2<=i && i <= 4 && 2 <= j && j <= 4) enemy+=0.2; //center bonus
						enemy+=squareValues[i][j];
					}
				}
			int friendlybonus = (isMaximizingX?b.XTornados:b.YTornados) + (isMaximizingX?b.XTCancels:b.YTCancels);
			friendly+=(5*friendlybonus);
			int enemybonus = ((!isMaximizingX)?b.XTornados:b.YTornados) + ((!isMaximizingX)?b.XTCancels:b.YTCancels);
			enemy+=(5*enemybonus);
			long afterCount;
			if (Util.DEBUG)
			{
				afterCount = SystemClock.uptimeMillis();
				countTimeSum+=(afterCount-afterPlay);
			}
//			if (b.boardPieces[move[1]][move[2]].isEmpty())
//			{
//				return (Float) (0.1f * friendly/(enemy+friendly)); //don't play ghosts
//			}
			return (Float) (friendly/(enemy+friendly));
		}
		//otherwise, go down to next depth
		//!isOriginal is because play reverses whose turn it is
		int[] newmove = new int[3];
		if (!isMaximizing)
		{
			for (int type = 0; type < 3; type++)
			{
				newmove[0] = type;
				for (int x = 0; x < 7; x++)
				{
					newmove[1] = x;
					for (int y = 0; y < 7; y++)
					{
						newmove[2] = y;
						boardSet.get(depth+1).set(b);
						float nextlevel = alphabetaHardTestMoveHelper(newmove, depth+1, boardSet, maxDepth, alpha, beta, !isMaximizing);
						if (nextlevel > -0.5) //if it's a valid move
						{
							alpha = Math.max(alpha, nextlevel);
						}
						if (beta <= alpha) 
						{
							break;
						}
						//The value of a move is 1 minus the worst case scenario that can result.
						//The enemy's move being high is bad so this finds worst case.
					}
					if (beta <= alpha) break;
				}
				if (beta <= alpha) break;
			}
//			if (alpha > 0.05f && b.boardPieces[move[1]][move[2]].isEmpty())
//			{
//				return .1f * (1.f-alpha); //don't play ghosts (unless you win with them)
//			}
//			if (alpha < 0.05f) return 0.999f-alpha; //winning sooner is better
			return alpha;
		}
		else
		{
			for (int type = 0; type < 3; type++)
			{
				newmove[0] = type;
				for (int x = 0; x < 7; x++)
				{
					newmove[1] = x;
					for (int y = 0; y < 7; y++)
					{
						newmove[2] = y;
						boardSet.get(depth+1).set(b);
						float nextlevel = alphabetaHardTestMoveHelper(newmove, depth+1, boardSet, maxDepth, alpha, beta, !isMaximizing);
						if (nextlevel > -0.5)
						{
							beta = Math.min(beta, nextlevel);
						}
						if (beta <= alpha) break;
						//The value of a move is 1 minus the worst case scenario that can result.
						//The enemy's move being high is bad so this finds worst case.
					}
					if (beta <= alpha)
					{
						break;
					}
				}
				if (beta <= alpha) break;
			}
//			if (beta > 0.05f && b.boardPieces[move[1]][move[2]].isEmpty())
//			{
//				return .1f * (1.f-beta); //don't play ghosts (unless you win with them)
//			}
//			if (beta < 0.05f) return 0.999f-beta; //winning sooner is better
			return beta;
		}
	}
	/**
	 * This runs on a separate thread.
	 */
	private void hardAiMove()
	{
		long startTime = SystemClock.uptimeMillis();
		int depth = 1;
		playTimeSum = 0;
		countTimeSum = 0;
		initialTimeSum = 0;
		tornadoTimeSum = 0;
		winCheckTimeSum = 0;
		stalemateTimeSum = 0;
		int[] move = new int[3];
		while ((SystemClock.uptimeMillis()-startTime) <= 3500)
		{
			move = hardAi(depth); //If the ai can run the level below in a certain time
			//run the ai to the next depth. Not optimized but memory-light.
			depth++;
		}
		Util.debug("TTT",""+depth);
		if (move == null)
		{
			Log.wtf("TTT", "move is null at hardAiMove");
		}
//		Util.debug("TTT", "Play took " + playTimeSum + " ms total");
//		Util.debug("TTT", "Count took " + countTimeSum + " ms total");
//		Util.debug("TTT", "Initial part took " + initialTimeSum + " ms total");
//		Util.debug("TTT", "Tornado took " + tornadoTimeSum + " ms total");
//		Util.debug("TTT", "Win check took " + winCheckTimeSum + " ms total");
//		Util.debug("TTT", "Stalemate took " + stalemateTimeSum + " ms total");
		//thread stuff
		Message mes = aiHandler.obtainMessage(0, move);
		mes.sendToTarget();
	}
	private static String toString(int[] a)
	{
		String str = "[";
		for (int i = 0; i < a.length-1; i++)
		{
			str = str + a[i] + ", ";
		}
		str = str + a[a.length-1] + "]";
		return str;
	}
}