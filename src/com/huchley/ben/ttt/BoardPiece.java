package com.huchley.ben.ttt;

import android.widget.ImageView;

public final class BoardPiece {
	public static final int BLANK = 0;
	public static final int X = 1;
	public static final int O = 2;
	public static final int XTORNADO = 3;
	public static final int OTORNADO = 4;
	public static final int CANCELED = 5;
	public static final int XGHOST = 6;
	public static final int OGHOST = 7;
	public int type;
	private ImageView img;
	public boolean xIsValid;
	public boolean oIsValid;
	public BoardPiece(ImageView theImg) //for visible board pieces
	{
		type = BLANK;
		img = theImg;
		xIsValid = true;
		oIsValid = true;
	}
	public BoardPiece(int type, boolean xValid, boolean oValid) //for temporary (used to be abstract) board pieces
	{
		this.type = type;
		img = null;
		xIsValid = xValid;
		oIsValid = oValid;
	}
	public void updateImage() //call this only on visible board pieces whenever they change
	{
		if (type == BLANK)
		{
			img.setImageResource(R.drawable.blank);
		}
		if (type == X)
		{
			img.setImageResource(R.drawable.x);
		}
		if (type == O)
		{
			img.setImageResource(R.drawable.o);
		}
		if (type == XTORNADO)
		{
			img.setImageResource(R.drawable.xtornado);
		}
		if (type == OTORNADO)
		{
			img.setImageResource(R.drawable.otornado);
		}
		if (type == CANCELED)
		{
			img.setImageResource(R.drawable.canceled);
		}
		if (type == XGHOST)
		{
			img.setImageResource(R.drawable.xmini);
		}
		if (type == OGHOST)
		{
			img.setImageResource(R.drawable.omini);
		}
	}
	public boolean isEmpty()
	{
		return (type == BLANK || type == XGHOST || type == OGHOST);
	}
	public void deGhost()
	{
		xIsValid = true;
		oIsValid = true;
		if (type == XGHOST)
		{
			type = X;
		}
		else if (type == OGHOST)
		{
			type = O;
		}
	}
	/**
	 * Only call this if you already know that this and other are in the same row or column.
	 */
//	public boolean wouldClear(BoardPiece other)
//	{
//		return (type == XTORNADO && other.type == O) || (type == OTORNADO && other.type == X);
//	}
	/**
	 * Only call this if you already know that this and other are in the same row or column.
	 */
//	public boolean wouldCancel(BoardPiece other)
//	{
//		return (type == XTORNADO && other.type == OTORNADO) || (type == OTORNADO && other.type == XTORNADO);
//	}
	
}
