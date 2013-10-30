package com.huchley.ben.ttt;

import android.os.SystemClock;

public class AbstractBoard {
	public AbstractBoardPiece[][] boardPieces;
	public boolean isXTurn;
	public int XTornados;
	public int XTCancels;
	public int YTornados;
	public int YTCancels;
	/**
	 * Makes a new abstract board with all the squares blank.
	 */
	public AbstractBoard()
	{
		
		
		boardPieces = new AbstractBoardPiece[7][7];
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
				boardPieces[i][j] = new AbstractBoardPiece(BoardPiece.BLANK);
		isXTurn = true;
		XTornados = 3;
		XTCancels = 2;
		YTornados = 3;
		YTCancels = 2;
		if (0 == 0)
		{
			throw new RuntimeException("Oh no, an abstract board appeared!");
		}
	}
	/**
	 * Creates a new abstract board from a given state.
	 * boardPieces must be fully initialized and 7 by 7.
	 * @param boardPieces the pieces to make a board from
	 */
	public AbstractBoard(AbstractBoardPiece[][] boardPieces, int XTornados, int XTCancels, int YTornados, int YTCancels, boolean isXTurn)
	{
		this.boardPieces = boardPieces;
		this.isXTurn = isXTurn;
		this.XTornados = XTornados;
		this.XTCancels = XTCancels;
		this.YTornados = YTornados;
		this.YTCancels = YTCancels;
		if (0 == 0)
		{
			throw new RuntimeException("Oh no, an abstract board appeared!");
		}
	}
	public void set(AbstractBoard b)
	{
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
				boardPieces[i][j].changeType(b.boardPieces[i][j].getType());
		isXTurn = b.isXTurn;
		XTornados = b.XTornados;
		XTCancels = b.XTCancels;
		YTornados = b.YTornados;
		YTCancels = b.YTCancels;
	}
	public void set(Board b)
	{
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
				boardPieces[i][j].changeType(b.boardPieces[i][j].type);
		isXTurn = b.isXTurn;
		XTornados = b.XTornados;
		XTCancels = b.XTCancels;
		YTornados = b.YTornados;
		YTCancels = b.YTCancels;
	}
	/**
	 * @return a new abstract copy of the board
	 */
	public AbstractBoard clone()
	{
		AbstractBoardPiece[][] copyPieces = new AbstractBoardPiece[7][7];
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
				copyPieces[i][j] = new AbstractBoardPiece(boardPieces[i][j].getType());
		AbstractBoard copy = new AbstractBoard(copyPieces,XTornados,XTCancels,YTornados,YTCancels,isXTurn);
		return copy;
	}
	/**
	 * receive the player's input and change the board accordingly, end the game if necessary
	 * @param pieceType is the type of piece the player wants to place
	 * @param x is the x-coordinate, in string form, of where the player wants to play
	 * @param y is the y-coordinate, in string form, of where the player wants to play
	 */
	public int play(int pieceType,int x,int y) {
		//place the pieces, and decrement x tornadoes and x tornado cancels if necessary
		long startInitialTime = SystemClock.uptimeMillis();
		if (pieceType == 1)//tornado
		{
			if(isXTurn)
			{
				if (boardPieces[x][y].isEmpty() && XTornados > 0)
				{
					boardPieces[x][y].changeType(BoardPiece.XTORNADO);
					XTornados--;
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
			else
			{
				if (boardPieces[x][y].isEmpty() && YTornados > 0)
				{
					boardPieces[x][y].changeType(BoardPiece.OTORNADO);
					YTornados--;
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
		}
		else if (pieceType == 2)//cancel
		{
			if(isXTurn)
			{
				if (boardPieces[x][y].getType() == BoardPiece.OTORNADO && XTCancels > 0)
				{
					boardPieces[x][y].changeType(BoardPiece.CANCELED);
					XTCancels--;
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
			else
			{
				if (boardPieces[x][y].getType() == BoardPiece.XTORNADO && YTCancels > 0)
				{
					boardPieces[x][y].changeType(BoardPiece.CANCELED);
					YTCancels--;
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
		}
		else
		{
			if(isXTurn)
			{
				if (boardPieces[x][y].isEmpty())
				{
					boardPieces[x][y].changeType(BoardPiece.X);
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
			else
			{
				if (boardPieces[x][y].isEmpty())
				{
					boardPieces[x][y].changeType(BoardPiece.O);
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
		}
		long startTornadoTime = SystemClock.uptimeMillis();
		TTTAndroidActivity.initialTimeSum += startTornadoTime - startInitialTime;
		// manage tornadoes
		//reset ghosts to normal (easiest way to rectify ghosts that should regenerate)
		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 7; j++){
				boardPieces[i][j].deGhost();
			}
		}

		boolean tornadoUp = false;
		boolean tornadoDown = false;
		boolean tornadoLeft = false;
		boolean tornadoRight = false;
		//apply tornado effects to board
		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 7; j++){
				if (boardPieces[i][j].getType() == BoardPiece.XTORNADO){
					for (int k = 0; k < i; k++){
						if (boardPieces[k][j].getType() == BoardPiece.OTORNADO ){
							tornadoUp = true;
						}
					}
					for (int k = 0; k < j; k++){
						if (boardPieces[i][k].getType() == BoardPiece.OTORNADO ){
							tornadoLeft = true;
						}
					}
					for (int k = i; k < 7; k++){
						if (boardPieces[k][j].getType() == BoardPiece.OTORNADO ){
							tornadoDown = true;
						}
					}
					for (int k = j; k < 7; k++){
						if (boardPieces[i][k].getType() == BoardPiece.OTORNADO ){
							tornadoRight = true;
						}
					}
					if(!tornadoUp){
						for(int l = 0; l < i; l++){
							if (boardPieces[l][j].getType() == BoardPiece.O){
								boardPieces[l][j].changeType(BoardPiece.OGHOST);
							}
						}
					}
					if(!tornadoLeft){
						for(int l = 0; l < j; l++){
							if (boardPieces[i][l].getType() == BoardPiece.O){
								boardPieces[i][l].changeType(BoardPiece.OGHOST);
							}
						}
					}
					if(!tornadoDown){
						for(int l = i; l < 7; l++){
							if (boardPieces[l][j].getType() == BoardPiece.O){
								boardPieces[l][j].changeType(BoardPiece.OGHOST);
							}
						}
					}
					if(!tornadoRight){
						for(int l = j; l < 7; l++){
							if (boardPieces[i][l].getType() == BoardPiece.O){
								boardPieces[i][l].changeType(BoardPiece.OGHOST);
							}
						}
					}
					tornadoUp = false;
					tornadoDown = false;
					tornadoLeft = false;
					tornadoRight = false;
				}
				if (boardPieces[i][j].getType() == BoardPiece.OTORNADO){
					for (int k = 0; k < i; k++){
						if (boardPieces[k][j].getType() == BoardPiece.XTORNADO){
							tornadoUp = true;
						}
					}
					for (int k = 0; k < j; k++){
						if (boardPieces[i][k].getType() == BoardPiece.XTORNADO){
							tornadoLeft = true;
						}
					}
					for (int k = i; k < 7; k++){
						if (boardPieces[k][j].getType() == BoardPiece.XTORNADO){
							tornadoDown = true;
						}
					}
					for (int k = j; k < 7; k++){
						if (boardPieces[i][k].getType() == BoardPiece.XTORNADO){
							tornadoRight = true;
						}
					}
					if(!tornadoUp){
						for(int l = 0; l < i; l++){
							if (boardPieces[l][j].getType() == BoardPiece.X){
								boardPieces[l][j].changeType(BoardPiece.XGHOST);
							}
						}
					}
					if(!tornadoLeft){
						for(int l = 0; l < j; l++){
							if (boardPieces[i][l].getType() == BoardPiece.X){
								boardPieces[i][l].changeType(BoardPiece.XGHOST);
							}
						}
					}
					if(!tornadoDown){
						for(int l = i; l < 7; l++){
							if (boardPieces[l][j].getType() == BoardPiece.X){
								boardPieces[l][j].changeType(BoardPiece.XGHOST);
							}
						}
					}
					if(!tornadoRight){
						for(int l = j; l < 7; l++){
							if (boardPieces[i][l].getType() == BoardPiece.X){
								boardPieces[i][l].changeType(BoardPiece.XGHOST);
							}
						}
					}
					tornadoUp = false;
					tornadoDown = false;
					tornadoLeft = false;
					tornadoRight = false;
				}
			}
		}
		long startWinTime = SystemClock.uptimeMillis();
		TTTAndroidActivity.tornadoTimeSum += startWinTime - startTornadoTime;
		isXTurn = !isXTurn;
		//check to see if anyone wins. if so, return the identity of the winner
		for (int m = 0; m < 7; m++){
			for (int n = 0; n < 3; n++){
				if (boardPieces[m][n].getType() == BoardPiece.X && boardPieces[m][n+1].getType() == BoardPiece.X && boardPieces[m][n+2].getType() == BoardPiece.X && boardPieces[m][n+3].getType() == BoardPiece.X && boardPieces[m][n+4].getType() == BoardPiece.X){
					return Board.X;
					
				}
				if (boardPieces[n][m].getType() == BoardPiece.X && boardPieces[n+1][m].getType() == BoardPiece.X && boardPieces[n+2][m].getType() == BoardPiece.X && boardPieces[n+3][m].getType() == BoardPiece.X && boardPieces[n+4][m].getType() == BoardPiece.X){
					return Board.X;
				}
				if (boardPieces[m][n].getType() == BoardPiece.O && boardPieces[m][n+1].getType() == BoardPiece.O && boardPieces[m][n+2].getType() == BoardPiece.O && boardPieces[m][n+3].getType() == BoardPiece.O && boardPieces[m][n+4].getType() == BoardPiece.O){
					return Board.Y;
				}
				if (boardPieces[n][m].getType() == BoardPiece.O && boardPieces[n+1][m].getType() == BoardPiece.O && boardPieces[n+2][m].getType() == BoardPiece.O && boardPieces[n+3][m].getType() == BoardPiece.O && boardPieces[n+4][m].getType() == BoardPiece.O){
					return Board.Y;
				}
			}
		}
		for (int o = 0; o < 3; o++){
			for (int p = 0; p < 3; p++){
				if (boardPieces[o][p].getType() == BoardPiece.X && boardPieces[o+1][p+1].getType() == BoardPiece.X && boardPieces[o+2][p+2].getType() == BoardPiece.X && boardPieces[o+3][p+3].getType() == BoardPiece.X && boardPieces[o+4][p+4].getType() == BoardPiece.X){
					return Board.X;
				}
				if (boardPieces[o][p].getType() == BoardPiece.O && boardPieces[o+1][p+1].getType() == BoardPiece.O && boardPieces[o+2][p+2].getType() == BoardPiece.O && boardPieces[o+3][p+3].getType() == BoardPiece.O && boardPieces[o+4][p+4].getType() == BoardPiece.O){
					return Board.Y;
				}
			}
			for (int q = 4; q < 7; q++){
				if (boardPieces[o][q].getType() == BoardPiece.X && boardPieces[o+1][q-1].getType() == BoardPiece.X && boardPieces[o+2][q-2].getType() == BoardPiece.X && boardPieces[o+3][q-3].getType() == BoardPiece.X && boardPieces[o+4][q-4].getType() == BoardPiece.X){
					return Board.X;
				}
				if (boardPieces[o][q].getType() == BoardPiece.O && boardPieces[o+1][q-1].getType() == BoardPiece.O && boardPieces[o+2][q-2].getType() == BoardPiece.O && boardPieces[o+3][q-3].getType() == BoardPiece.O && boardPieces[o+4][q-4].getType() == BoardPiece.O){
					return Board.Y;
				}
			}
		}

		//I know that it might return in the middle of this area and not increment winchecktimesum.
		//That's OK, since it'll only happen when the game ends. Same applies for stalematetimesum.
		long startStalemateTime = SystemClock.uptimeMillis();
		TTTAndroidActivity.winCheckTimeSum += startStalemateTime - startWinTime;
		//stalemate checker, it checks for every empty space to be covered by both sides' tornadoes
		if (XTornados == 0 && YTornados == 0 && XTCancels == 0 && YTCancels == 0)
		{
			for (int i = 0; i < 7; i++)
			{
				for (int j = 0; j < 7; j++)
				{
					if (boardPieces[i][j].isEmpty())
					{
						//for each empty space
						boolean xtornado = false;
						boolean otornado = false;
						boolean xfound = false;
						boolean yfound = false;
						for (int k = 0; k < 7; k++)
						{
							if (boardPieces[i][k].getType() == BoardPiece.XTORNADO && !xfound)
							{
								xtornado = true;
								xfound = true;
							}
							if (boardPieces[k][j].getType() == BoardPiece.XTORNADO && !yfound)
							{
								xtornado = true;
								yfound = true;
							}
							if (boardPieces[i][k].getType() == BoardPiece.OTORNADO && !xfound)
							{
								otornado = true;
								xfound = true;
							}
							if (boardPieces[k][j].getType() == BoardPiece.OTORNADO && !yfound)
							{
								otornado = true;
								yfound = true;
							}
						}
						if (!xtornado || !otornado)
						{
							return Board.NO_ONE;
						}
					}
				}
			}
			return Board.STALEMATE;
		}
		TTTAndroidActivity.stalemateTimeSum += SystemClock.uptimeMillis() - startStalemateTime;
		return Board.NO_ONE; //if there is no stalemate and no win the game continues
	}

}
