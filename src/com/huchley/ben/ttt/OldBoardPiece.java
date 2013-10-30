package com.huchley.ben.ttt;

import android.widget.ImageView;

public class OldBoardPiece {
	public static final int BLANK = 0;
	public static final int X = 1;
	public static final int O = 2;
	public static final int XTORNADO = 3;
	public static final int OTORNADO = 4;
	public static final int CANCELED = 5;
	public static final int XGHOST = 6;
	public static final int OGHOST = 7;
	private int type;
	private ImageView img;
	public OldBoardPiece(ImageView theImg)
	{
		type = BLANK;
		img = theImg;
	}
	public void changeType(int newType)
	{
		type = newType;
		if (newType == BLANK)
		{
			img.setImageResource(R.drawable.blank);
		}
		if (newType == X)
		{
			img.setImageResource(R.drawable.x);
		}
		if (newType == O)
		{
			img.setImageResource(R.drawable.o);
		}
		if (newType == XTORNADO)
		{
			img.setImageResource(R.drawable.xtornado);
		}
		if (newType == OTORNADO)
		{
			img.setImageResource(R.drawable.otornado);
		}
		if (newType == CANCELED)
		{
			img.setImageResource(R.drawable.canceled);
		}
		if (newType == XGHOST)
		{
			img.setImageResource(R.drawable.xmini);
		}
		if (newType == OGHOST)
		{
			img.setImageResource(R.drawable.omini);
		}
	}
	public int getType()
	{
		return type;
	}
	public boolean isEmpty()
	{
		return (type == BLANK || type == XGHOST || type == OGHOST);
	}
	public void deGhost()
	{
		if (type == XGHOST)
		{
			changeType(X);
		}
		else if (type == OGHOST)
		{
			changeType(O);
		}
	}
}
