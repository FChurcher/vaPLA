package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.Reader;
import model.Scoring;
import model.Sequence;

/**
 * This Class is containing static values used or changed by other methods/classes 
 * @author Falco, inspired by Alignments/src/container/generalContainer.java written by Nancy
 */
public class Settings {
  // static values an methods
	/** the scoring matrix (e.g. BLOSUM, PAM) */
	public static Scoring scoring;
	/** further coeds (e.g. 'N' for (A or G or T or C)) */
	public static HashMap<Character, ArrayList<Character>> codes;
	/** the name of this alignment */
	public static String name;
	
	
	/** reads all the given files and extracts sequences, locality and scoring */
	public static Sequence[] init(String sequencesFilePath, String localityPath, String scoringFilePath, String codeFilePath) {
		if (scoringFilePath == null) {
			Settings.scoring = null;
		} else {
			Settings.scoring = Reader.readScoreMatrix(scoringFilePath);
		}
		Settings.codes = Reader.readCodes(codeFilePath);
		Sequence[] sequences = Reader.readLocality(localityPath,  Reader.readSequences(sequencesFilePath));
		if (sequencesFilePath.contains(File.separator)) {
			Settings.name = sequencesFilePath.substring(sequencesFilePath.lastIndexOf(File.separator)+1, sequencesFilePath.lastIndexOf("."));	
		}
		return sequences;
	}
	
	/** reads all the given files and extracts sequences, locality and scoring */
	public static Sequence[] init(String sequencesLocalitiesName, String scoringFilePath, String codeFilePath) {
		return init(sequencesLocalitiesName + ".fasta", sequencesLocalitiesName + ".loc", scoringFilePath, codeFilePath);
	}
	
	/** reads all the given files and extracts sequences, locality and scoring */
	public static Sequence[] init(String sequencesLocalitiesName, String scoringFilePath) {
		return init(sequencesLocalitiesName, scoringFilePath, "data" + File.separator + "default_codes.cod");
	}
	
	/** reads all the given files and extracts sequences, locality and scoring */
	public static Sequence[] init(String sequencesLocalitiesName) {
		return init(sequencesLocalitiesName, "data" + File.separator + "default_scores.sco");
	}
	
	/** reads all the given files and extracts sequences, locality and scoring */
	public static Sequence[] init() {
		return init("data" + File.separator + "example");
	}
	
	public static String printToString() { 
		String s = "##########################################################\n";
		s += "Settings:\n\n";
		if (scoring != null) {
			s += scoring.toString();
		}
		s += "\n";
		s += "==========================================================\n";
		s += "Codes:\n";
		s += "----------------------------------------------------------\n";
		for (Character codeSymbol : codes.keySet()) {
			s += codeSymbol + ":\t";
			for (Character nucleotideSymbol : codes.get(codeSymbol)) {
				s += nucleotideSymbol + " ";
			}
			s += "\n";
		}
		s += "----------------------------------------------------------\n";
		s += "==========================================================\n";
		s += "##########################################################\n";
		return s;
	}

}
