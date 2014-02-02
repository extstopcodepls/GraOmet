package com.game.extras;

public class LevelScoreCollector
{
	public static int allScore = 0;
	public static int allStars = 0;
	
	public static int getAllScore()
	{
		return allScore;
	}

	public static int getAllStars()
	{
		return allStars;
	}

	public static void addToScore(int score)
	{
		allScore += score;
	}
	
	public static void addToStars(int star)
	{
		allStars += star;
	}
}