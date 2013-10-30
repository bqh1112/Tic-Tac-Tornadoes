package com.huchley.ben.ttt;

import android.os.SystemClock;
//import android.util.Log;

public final class Board {
	public final BoardPiece[][] boardPieces;
	public boolean isXTurn;
	public int XTornados;
	public int XTCancels;
	public int YTornados;
	public int YTCancels;
	public static final int X = 0;
	public static final int Y = 1;
	public static final int NO_ONE = 2;
	public static final int STALEMATE = 3;
	public static final int INVALID = 4;
	public Board()
	{
		boardPieces = new BoardPiece[7][7]; //can't initialize it b/c board can't initialize listeners
		isXTurn = true;
		XTornados = 3;
		XTCancels = 2;
		YTornados = 3;
		YTCancels = 2;
	}
	public Board(BoardPiece[][] boardPieces, int XTornados, int XTCancels, int YTornados, int YTCancels, boolean isXTurn)
	{
		this.boardPieces = boardPieces;
		this.isXTurn = isXTurn;
		this.XTornados = XTornados;
		this.XTCancels = XTCancels;
		this.YTornados = YTornados;
		this.YTCancels = YTCancels;
	}
	/**
	 * @return a new temporary copy of the board
	 */
	public Board copy()
	{
		//long t0 = SystemClock.uptimeMillis();
		BoardPiece[][] copyPieces = new BoardPiece[7][7];
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
			{
				BoardPiece piece = boardPieces[i][j];
				copyPieces[i][j] = new BoardPiece(piece.type, piece.xIsValid, piece.oIsValid);
			}
		Board copy = new Board(copyPieces,XTornados,XTCancels,YTornados,YTCancels,isXTurn);
		//long t1 = SystemClock.uptimeMillis();
		//Log.d("ai", "copy took " + (t1-t0));
		return copy;
	}
	public void set(Board b)
	{
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 7; j++)
			{
				boardPieces[i][j].type = b.boardPieces[i][j].type;
				boardPieces[i][j].xIsValid = b.boardPieces[i][j].xIsValid;
				boardPieces[i][j].oIsValid = b.boardPieces[i][j].oIsValid;
			}
		isXTurn = b.isXTurn;
		XTornados = b.XTornados;
		XTCancels = b.XTCancels;
		YTornados = b.YTornados;
		YTCancels = b.YTCancels;
	}

	/**
	 * receive the player's input and change the board accordingly, end the game if necessary
	 * @param pieceType is the type of piece the player wants to place
	 * @param x is the x-coordinate, in string form, of where the player wants to play
	 * @param y is the y-coordinate, in string form, of where the player wants to play
	 */
	public int play(int pieceType,int x,int y) {
		//place the pieces, and decrement x tornadoes and x tornado cancels if necessary
		long startInitialTime = 0;
		if (Util.DEBUG) startInitialTime = SystemClock.uptimeMillis();
		if (pieceType == 1)//tornado
		{
			if(isXTurn)
			{
				if (boardPieces[x][y].isEmpty() && XTornados > 0)
				{
					boardPieces[x][y].type = BoardPiece.XTORNADO;
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
					boardPieces[x][y].type = BoardPiece.OTORNADO;
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
				if (boardPieces[x][y].type == BoardPiece.OTORNADO && XTCancels > 0)
				{
					boardPieces[x][y].type = BoardPiece.CANCELED;
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
				if (boardPieces[x][y].type == BoardPiece.XTORNADO && YTCancels > 0)
				{
					boardPieces[x][y].type = BoardPiece.CANCELED;
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
			//Util.debug("TTT", "a piece");
			if(isXTurn)
			{
				if (boardPieces[x][y].isEmpty())
				{
					boardPieces[x][y].type = BoardPiece.X;
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
					boardPieces[x][y].type = BoardPiece.O;
				}
				else
				{
					TTTAndroidActivity.initialTimeSum += SystemClock.uptimeMillis() - startInitialTime;
					return Board.INVALID;
				}
			}
		}
		long startTornadoTime = 0;
		if (Util.DEBUG)
		{
			startTornadoTime = SystemClock.uptimeMillis();
			TTTAndroidActivity.initialTimeSum += startTornadoTime - startInitialTime;
		}
		// manage tornadoes
		//if it is a tornado or tc brute force tornado check
		if (pieceType == 1 || pieceType == 2)
		{
			//Util.warn("TTT","brute force checking, pieceType is " + pieceType);
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
					if (boardPieces[i][j].type == BoardPiece.XTORNADO){
						for (int k = 0; k < i; k++){
							if (boardPieces[k][j].type == BoardPiece.OTORNADO ){
								tornadoUp = true;
							}
						}
						for (int k = 0; k < j; k++){
							if (boardPieces[i][k].type == BoardPiece.OTORNADO ){
								tornadoLeft = true;
							}
						}
						for (int k = i; k < 7; k++){
							if (boardPieces[k][j].type == BoardPiece.OTORNADO ){
								tornadoDown = true;
							}
						}
						for (int k = j; k < 7; k++){
							if (boardPieces[i][k].type == BoardPiece.OTORNADO ){
								tornadoRight = true;
							}
						}
						if(!tornadoUp){
							for(int l = 0; l < i; l++){
								boardPieces[l][j].oIsValid = false;
								if (boardPieces[l][j].type == BoardPiece.O){
									boardPieces[l][j].type = BoardPiece.OGHOST;
								}
							}
						}
						if(!tornadoLeft){
							for(int l = 0; l < j; l++){
								boardPieces[i][l].oIsValid = false;
								if (boardPieces[i][l].type == BoardPiece.O){
									boardPieces[i][l].type = BoardPiece.OGHOST;
								}
							}
						}
						if(!tornadoDown){
							for(int l = i; l < 7; l++){
								boardPieces[l][j].oIsValid = false;
								if (boardPieces[l][j].type == BoardPiece.O){
									boardPieces[l][j].type = BoardPiece.OGHOST;
								}
							}
						}
						if(!tornadoRight){
							for(int l = j; l < 7; l++){
								boardPieces[i][l].oIsValid = false;
								if (boardPieces[i][l].type == BoardPiece.O){
									boardPieces[i][l].type = BoardPiece.OGHOST;
								}
							}
						}
						tornadoUp = false;
						tornadoDown = false;
						tornadoLeft = false;
						tornadoRight = false;
					}
					if (boardPieces[i][j].type == BoardPiece.OTORNADO){
						for (int k = 0; k < i; k++){
							if (boardPieces[k][j].type == BoardPiece.XTORNADO){
								tornadoUp = true;
							}
						}
						for (int k = 0; k < j; k++){
							if (boardPieces[i][k].type == BoardPiece.XTORNADO){
								tornadoLeft = true;
							}
						}
						for (int k = i; k < 7; k++){
							if (boardPieces[k][j].type == BoardPiece.XTORNADO){
								tornadoDown = true;
							}
						}
						for (int k = j; k < 7; k++){
							if (boardPieces[i][k].type == BoardPiece.XTORNADO){
								tornadoRight = true;
							}
						}
						if(!tornadoUp){
							for(int l = 0; l < i; l++){
								boardPieces[l][j].xIsValid = false;
								if (boardPieces[l][j].type == BoardPiece.X){
									boardPieces[l][j].type = BoardPiece.XGHOST;
								}
							}
						}
						if(!tornadoLeft){
							for(int l = 0; l < j; l++){
								boardPieces[i][l].xIsValid = false;
								if (boardPieces[i][l].type == BoardPiece.X){
									boardPieces[i][l].type = BoardPiece.XGHOST;
								}
							}
						}
						if(!tornadoDown){
							for(int l = i; l < 7; l++){
								boardPieces[l][j].xIsValid = false;
								if (boardPieces[l][j].type == BoardPiece.X){
									boardPieces[l][j].type = BoardPiece.XGHOST;
								}
							}
						}
						if(!tornadoRight){
							for(int l = j; l < 7; l++){
								boardPieces[i][l].xIsValid = false;
								if (boardPieces[i][l].type == BoardPiece.X){
									boardPieces[i][l].type = BoardPiece.XGHOST;
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
		}
		else //otherwise only check piece that was played
		{
			BoardPiece piece = boardPieces[x][y];
			if (isXTurn && (!piece.xIsValid))
			{
				piece.type = BoardPiece.XGHOST;
			}
			if ((!isXTurn) && (!piece.oIsValid))
			{
				piece.type = BoardPiece.OGHOST;
			}
		}
		long startWinTime = 0;
		if (Util.DEBUG)
		{
			startWinTime = SystemClock.uptimeMillis();
			TTTAndroidActivity.tornadoTimeSum += startWinTime - startTornadoTime;
		}
		isXTurn = !isXTurn;
		//check to see if anyone wins. if so, return the identity of the winner
		for (int m = 0; m < 7; m++){
			final BoardPiece[] boardRowM = boardPieces[m];
			for (int n = 0; n < 3; n++){
				if (boardRowM[n].type == BoardPiece.X && boardRowM[n+1].type == BoardPiece.X && boardRowM[n+2].type == BoardPiece.X && boardRowM[n+3].type == BoardPiece.X && boardRowM[n+4].type == BoardPiece.X){
					return Board.X;

				}
				if (boardPieces[n][m].type == BoardPiece.X && boardPieces[n+1][m].type == BoardPiece.X && boardPieces[n+2][m].type == BoardPiece.X && boardPieces[n+3][m].type == BoardPiece.X && boardPieces[n+4][m].type == BoardPiece.X){
					return Board.X;
				}
				if (boardRowM[n].type == BoardPiece.O && boardRowM[n+1].type == BoardPiece.O && boardRowM[n+2].type == BoardPiece.O && boardRowM[n+3].type == BoardPiece.O && boardRowM[n+4].type == BoardPiece.O){
					return Board.Y;
				}
				if (boardPieces[n][m].type == BoardPiece.O && boardPieces[n+1][m].type == BoardPiece.O && boardPieces[n+2][m].type == BoardPiece.O && boardPieces[n+3][m].type == BoardPiece.O && boardPieces[n+4][m].type == BoardPiece.O){
					return Board.Y;
				}
			}
		}
		for (int o = 0; o < 3; o++){
			for (int p = 0; p < 3; p++){
				if (boardPieces[o][p].type == BoardPiece.X && boardPieces[o+1][p+1].type == BoardPiece.X && boardPieces[o+2][p+2].type == BoardPiece.X && boardPieces[o+3][p+3].type == BoardPiece.X && boardPieces[o+4][p+4].type == BoardPiece.X){
					return Board.X;
				}
				if (boardPieces[o][p].type == BoardPiece.O && boardPieces[o+1][p+1].type == BoardPiece.O && boardPieces[o+2][p+2].type == BoardPiece.O && boardPieces[o+3][p+3].type == BoardPiece.O && boardPieces[o+4][p+4].type == BoardPiece.O){
					return Board.Y;
				}
			}
			for (int q = 4; q < 7; q++){
				if (boardPieces[o][q].type == BoardPiece.X && boardPieces[o+1][q-1].type == BoardPiece.X && boardPieces[o+2][q-2].type == BoardPiece.X && boardPieces[o+3][q-3].type == BoardPiece.X && boardPieces[o+4][q-4].type == BoardPiece.X){
					return Board.X;
				}
				if (boardPieces[o][q].type == BoardPiece.O && boardPieces[o+1][q-1].type == BoardPiece.O && boardPieces[o+2][q-2].type == BoardPiece.O && boardPieces[o+3][q-3].type == BoardPiece.O && boardPieces[o+4][q-4].type == BoardPiece.O){
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
							if (boardPieces[i][k].type == BoardPiece.XTORNADO && !xfound)
							{
								xtornado = true;
								xfound = true;
							}
							if (boardPieces[k][j].type == BoardPiece.XTORNADO && !yfound)
							{
								xtornado = true;
								yfound = true;
							}
							if (boardPieces[i][k].type == BoardPiece.OTORNADO && !xfound)
							{
								otornado = true;
								xfound = true;
							}
							if (boardPieces[k][j].type == BoardPiece.OTORNADO && !yfound)
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
		if (Util.DEBUG) TTTAndroidActivity.stalemateTimeSum += SystemClock.uptimeMillis() - startStalemateTime;
		return Board.NO_ONE; //if there is no stalemate and no win the game continues
	}

}
//boolean clearable = false;
//for (int i = 0; i < 7 && !clearable; i++)
//{
//	BoardPiece vertcheck = boardPieces[x][i];
//	if (vertcheck.wouldClear(boardPieces[x][y]))
//	{
//		if (i < y)
//		{
//			for (int j = y+1; j < 7 && !clearable; j++)
//			{
//				clearable = true;
//				if (boardPieces[x][j].wouldCancel(vertcheck))
//				{
//					clearable = false;
//				}
//			}
//		}
//		else
//		{
//			for (int j = y-1; j >= 0 && !clearable; j--)
//			{
//				clearable = true;
//				if (boardPieces[x][j].wouldCancel(vertcheck))
//				{
//					clearable = false;
//				}
//			}
//		}
//	}
//	if (!clearable)
//	{
//		BoardPiece horizcheck = boardPieces[i][y];
//		if (horizcheck.wouldClear(boardPieces[x][y]))
//		{
//			if (i < x)
//			{
//				for (int j = x+1; j < 7 && !clearable; j++)
//				{
//					clearable = true;
//					if (boardPieces[j][y].wouldCancel(horizcheck))
//					{
//						clearable = false;
//					}
//				}
//			}
//			else
//			{
//				for (int j = x-1; j >= 0 && !clearable; j--)
//				{
//					clearable = true;
//					if (boardPieces[j][y].wouldCancel(horizcheck))
//					{
//						clearable = false;
//					}
//				}
//			}
//		}
//	}
//}