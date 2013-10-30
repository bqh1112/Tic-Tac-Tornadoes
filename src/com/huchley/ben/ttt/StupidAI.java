package com.huchley.ben.ttt;

//import android.os.Message;

/**
 * Write a description of class TicTacTornadoesAI here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StupidAI
{
	private boolean aiIsX;

	private boolean searchForTornado(String[][] board)
	{
		for(int i=0;i<7;i++)
		{
			for(int j=0;j<7;j++)
			{
				if(aiIsX)
				{
					if(board[i][j].equals("@"))
					{
						return true;
					}
				}
				else
				{
					if(board[i][j].equals("#"))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean[][] tornadoes(String[][] board4)
	{
		boolean[][] tornado=new boolean[7][7];
		for(int i=0;i<7;i++)
		{
			for(int j=0;j<7;j++)
			{
				if(aiIsX)
				{
					if(board4[i][j].equals("@"))
					{
						tornado[i][j]=true;;
					}
					else
						tornado[i][j]=false;
				}
				else
				{
					if(board4[i][j].equals("#"))
					{
						tornado[i][j]=true;
					}
					else
						tornado[i][j]=false;
				}
			}
		}
		return tornado;
	}

	private boolean[][] unoccupied(String[][] board3)
	{
		boolean[][] unoccupied=new boolean[7][7];
		for(int i=0;i<7;i++)
		{
			for(int j=0;j<7;j++)
			{
				unoccupied[i][j]=board3[i][j].equals("x")||board3[i][j].equals("o")||board3[i][j].equals(" ");
			}
		}
		return unoccupied;
	}

	private int[] findTornadoCancelLoc(String[][] board4)
	{
		int xCoord=(int)(7*Math.random());
		int yCoord=(int)(7*Math.random());
		while(! tornadoes(board4)[xCoord][yCoord])
		{
			xCoord=(int)(7*Math.random());
			yCoord=(int)(7*Math.random());
		}
		int[] loc = {xCoord,yCoord};
		return loc;
	}

	private int[] findRegularMoveLoc(String[][] board5)
	{
		int xCoord = (int) (Math.random() * 7);
		int yCoord = (int) (Math.random() * 7);
		while (! unoccupied(board5)[xCoord][yCoord])
		{
			xCoord = (int) (Math.random() * 7);
			yCoord = (int) (Math.random() * 7);
		}
		int[] loc = {xCoord,yCoord};
		return loc;
	}

	public int[] playRandomly(Board board)
	{
		aiIsX = board.isXTurn;
		int tornados = aiIsX?board.XTornados:board.YTornados;
		int tornadoCancels = aiIsX?board.XTCancels:board.YTCancels;
		String[][] boardStrings = boardToString(board);
		int type = (int)(3*Math.random());
		int[] loc = null;
		while(loc == null)
		{
			if (type == 0)//piece
			{
				loc = findRegularMoveLoc(boardStrings);
			}
			else if (type == 1 && tornados > 0)
			{
				loc = findRegularMoveLoc(boardStrings);
			}
			else if (type == 2 && searchForTornado(boardStrings) && tornadoCancels > 0)
			{
				loc = findTornadoCancelLoc(boardStrings);
			}
			else
			{
				type = (int) (3*Math.random());
			}
		}
		int[] move = {type, loc[0], loc[1]};
		return move;
	}
	private String[][] boardToString(Board b)
	{
		String[][] board = new String[7][7];
		for (int i = 0; i < 7; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				board[i][j] = boardPiecetoString(b.boardPieces[i][j]);
			}
		}
		return board;
	}
	private String boardPiecetoString(BoardPiece b)
	{
		if (b.type == BoardPiece.X) return "X";
		else if (b.type == BoardPiece.O) return "O";
		else if (b.type == BoardPiece.BLANK) return " ";
		else if (b.type == BoardPiece.XGHOST) return "x";
		else if (b.type == BoardPiece.OGHOST) return "o";
		else if (b.type == BoardPiece.XTORNADO) return "#";
		else if (b.type == BoardPiece.OTORNADO) return "@";
		else if (b.type == BoardPiece.CANCELED) return "%";
		return "";
	}
}
