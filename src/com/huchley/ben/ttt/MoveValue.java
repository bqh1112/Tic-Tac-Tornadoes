package com.huchley.ben.ttt;

public class MoveValue implements Comparable<MoveValue>
{
	public int[] move;
	public float value;
	public MoveValue(int[] move, float value)
	{
		this.move = new int[3];
		this.move[0] = move[0];
		this.move[1] = move[1];
		this.move[2] = move[2];
		this.value = value;
	}
	@Override
	/**
	 * This function actually compares them by value backwards, since high values are good moves. 
	 */
	public int compareTo(MoveValue another)
	{
		if (value > another.value) return -1;
		if (value < another.value) return 1;
		return 0;
	}
}
