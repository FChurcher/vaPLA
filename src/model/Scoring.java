package model;

import java.util.HashMap;

import controller.Scorer;

/**
 * An object of this class contains a scoring matrix to score the Alignments
 * @author falco
 */
public class Scoring {
  // object defenition
	/** the used characters */
	char[] chars;
	/** the scoring matrix */
	HashMap<Character, HashMap<Character, Float>> scoring;
	
	public Scoring(char[] chars, float[][] scores) {
		this.chars = chars;
		this.scoring = new HashMap<>();
		for (int i = 0; i < chars.length; i++) {
			this.scoring.put(chars[i], new HashMap<>());
			for (int j = 0; j < chars.length; j++) {
				this.scoring.get(chars[i]).put(chars[j], scores[i][j]);
			}
		}
		Scorer.getInstance();
		if (!this.contains(Scorer.GAP_CHAR)) {
			this.scoring.put(Scorer.GAP_CHAR, new HashMap<>());
			this.scoring.get(Scorer.GAP_CHAR).put(Scorer.GAP_CHAR, 0f);		// gap-gap is bad
			for (int i = 0; i < chars.length; i++) {
				this.scoring.get(Scorer.GAP_CHAR).put(chars[i], Scorer.scoreGap);
				this.scoring.get(chars[i]).put(Scorer.GAP_CHAR, Scorer.scoreGap);
			}
			this.chars = new char[chars.length+1];
			this.chars[chars.length] = Scorer.GAP_CHAR;
			for (int i = 0; i < chars.length; i++) {
				this.chars[i] = chars[i];
			}
		}
	}
	
	public boolean contains(char c) {
		for (char containedC : chars) {
			if (c == containedC) {
				return true;
			}
		}
		return false;
	}
	
	public float getScore(char c1, char c2) {
		return this.scoring.get(c1).get(c2);
	}
	
	@Override
	public String toString() {
		String s = "==========================================================\n";
		s += "Scoring Matrix: \n";
		s += "----------------------------------------------------------\n";
		for (int i = 0; i < chars.length; i++) {
			s += "\t" + chars[i];
		}
		s += "\n";
		for (int i = 0; i < this.chars.length; i++) {
			s += chars[i] + "\t";
			for (int j = 0; j < this.chars.length; j++) {
				s += this.scoring.get(this.chars[i]).get(this.chars[j]) + "\t";
			}
			s += "\n";
		}
		s += "----------------------------------------------------------\n";
		s += "==========================================================\n";
		return s;
	}

}
