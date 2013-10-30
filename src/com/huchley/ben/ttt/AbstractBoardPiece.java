package com.huchley.ben.ttt;
/**
 * like a normal board piece but linked to no image
 */
public class AbstractBoardPiece {
	private int type;
	public AbstractBoardPiece(int type)
	{
		this.type = type;
		if (0 == 0) throw new RuntimeException("Oh no, an abstract board piece appeared!");
	}
	public void changeType(int newType)
	{
		type = newType;
	}
	public int getType()
	{
		return type;
	}
	public boolean isEmpty()
	{
		return (type == BoardPiece.BLANK || type == BoardPiece.XGHOST || type == BoardPiece.OGHOST);
	}
	public void deGhost()
	{
		if (type == BoardPiece.XGHOST)
		{
			changeType(BoardPiece.X);
		}
		else if (type == BoardPiece.OGHOST)
		{
			changeType(BoardPiece.O);
		}
	}
}
