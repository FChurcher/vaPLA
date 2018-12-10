package controller;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The Scorer is a singleton class containing methods to compute scores for matches and miss-matches as well as gaps.
 * @author Falco, inspired by Alignments/src/container/Algebra.java written by Nancy 
 */
public class Scorer {
  // static finals values
	/** char representing a gap */
	public static final char GAP_CHAR = '-';

  // static values an methods
	/** default score */
	public static float scoreGap = -9;
	/** default score */
	public static float scoreMatch = 4;
	/** default score */
	public static float scoreMissMatch = -2;
	
	/** sets all the static values */
	public static void init(float scoreGap, float scoreMatch, float scoreMissMatch){
		Scorer.scoreGap = scoreGap;
		Scorer.scoreMatch = scoreMatch;
		Scorer.scoreMissMatch = scoreMissMatch;
	}
	
  // singleton intrinsic
	/** the singleton instance */
	private static Scorer instance;
	
	/**
	 * singleton get instance method
	 * @return an instance of this class
	 */
	public static Scorer getInstance(){
		if (instance == null) {
			instance = new Scorer();
		}
		return instance;
	}

  // object definition
	/**
	 * computes the score for two characters
	 * @return the score
	 */
	public float getScore(char c1, char c2){
		if (Settings.scoring == null) {									// if no scoring matrix was given -> default scoring
			return Scorer.getInstance().getDefaultScore(c1, c2);
		}
		boolean c1InScoring = Settings.scoring.contains(c1);
		boolean c2InScoring = Settings.scoring.contains(c2);
		boolean c1InCodes = Settings.codes.containsKey(c1);
		boolean c2InCodes = Settings.codes.containsKey(c2);
		
		if (c1InScoring && c2InScoring) {								// at first (!) look if chars are in scoring matrix
			return Settings.scoring.getScore(c1, c2);
		}
		
		if ((!c1InScoring && !c1InCodes) || (!c2InScoring && !c2InCodes)) {	// if char is not known -> default scoring
			return Scorer.getInstance().getDefaultScore(c1, c2);
		}
		
		ArrayList<Character> chars1, chars2 = new ArrayList<>();
		if (!c1InScoring && c1InCodes) {								// if in codes, get nucleotide symbols
			chars1 = Settings.codes.get(c1);
		} else {
			chars1 = new ArrayList<>();									// put chars in (if not in codes but in score matrix)
			chars1.add(c1);
		}
		if (!c2InScoring && c2InCodes) {
			chars2 = Settings.codes.get(c2);
		} else {
			chars2 = new ArrayList<>();
			chars2.add(c2);
		}
			
		ArrayList<Float> scores = new ArrayList<>();
		for (Character char1 : chars1) {
			for (Character char2 : chars2) {
				scores.add(Settings.scoring.getScore(char1, char2));
			}
		}
		float maxScore = Collections.max(scores);
		return maxScore;
	}
	
	public float getDefaultScore(char c1, char c2){
		if (c1 == c2) {
			if (c1 == GAP_CHAR) 
				return 0;
			return scoreMatch;
		}
		if (c1 == GAP_CHAR || c2 == GAP_CHAR ) 
			return scoreGap;
		return scoreMissMatch;
	}
	
	/**
	 * computes the Sum of Pairs Score for all possible  pairs of n characters
	 * @param chars - the chars to score
	 * @return the score
	 */
	public float getScoreSumOfPairs(char... chars){
		float score = 0;
		for (int i = 0; i < chars.length-1; i++) {
			for (int j = i+1; j < chars.length; j++) {
				score += getScore(chars[i], chars[j]);
			}
		}
		return score;
	}
	
}
