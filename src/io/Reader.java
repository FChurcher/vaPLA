package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import model.Scoring;
import model.Sequence;

/**
 * a "static" Class to read input files
 * @author Falco
 */
public class Reader {
  // static values an methods
	/** the reader */
	private static BufferedReader reader;
	
	/**
	 * reads the Sequences froma n imput file
	 * @param path - the path of the file to read
	 * @return - the Sequences
	 */
	public static Sequence[] readSequences(String path){
		try {
			Reader.reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		ArrayList<Sequence> sequecnes = new  ArrayList<>();
		String sequenceName;
		String sequence;
		
		String line = readLine(";");
		while (line != null) {
			sequenceName = "unknown";
			sequence = "";
			if (line.startsWith(">")) {
				sequenceName = line.substring(1);
				while ((line = readLine(";")) != null && !line.startsWith(">")) {
					sequence += line;
				}
			} else {
				System.out.println("invalid format");
				return null;
			}
			sequecnes.add(new Sequence(sequence.toUpperCase().toCharArray(), true, true, sequenceName));
		}
		return sequecnes.toArray(new Sequence[sequecnes.size()]);
	}
	
	/**
	 * reads the Loclities froma n imput file
	 * @param path - the path of the file to read
	 * @return - the Loclities
	 */
	public static Sequence[] readLocality(String path, Sequence[] sequences) {
		try {
			Reader.reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		String line;
		while ((line = readLine("#")) != null) {
			if (line.startsWith(">")) {															// parse name
				for (Sequence sequence : sequences) {
					if (sequence.getName().equals(line.substring(1))) {							// compare names
						line = readLine("#");													// read next line containing locality
						sequence.setGlobalLeft(Boolean.parseBoolean(line.split(" ")[0]));
						sequence.setGlobalRight(Boolean.parseBoolean(line.split(" ")[1]));
					}
				}
			}
		}
		return sequences;
	}
	
	/**
	 * reads the ScoreMatrix froma n imput file
	 * @param path - the path of the file to read
	 * @return - the ScoreMatrix
	 */
	public static Scoring readScoreMatrix(String path) {
		try {
			Reader.reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		char[] chars;
		float[][] scores;
		String line = readLine("#");
		
		String[] letters = line.split(" +");					// read chars
		chars = new char[letters.length];
		for (int i = 0; i < letters.length; i++) {
			chars[i] = letters[i].charAt(0);
		}
		
		scores = new float[chars.length][chars.length];			// read values
		for (int i = 0; i < letters.length; i++) {
			line = readLine("#");
			String[] floats = line.split(" +");
			for (int j = 1; j < floats.length; j++) {			// start with 1 to skip letters at start
				scores[i][j-1] = Float.parseFloat(floats[j]);
			}
		}
		
		try {
			Reader.reader.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		return new Scoring(chars, scores);
	}
	
	/**
	 * reads the codes froma n imput file
	 * @param path - the path of the file to read
	 * @return - the codes
	 */
	public static HashMap<Character, ArrayList<Character>> readCodes(String path){
		try {
			Reader.reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		HashMap<Character, ArrayList<Character>> codes = new HashMap<>();
		String line;
		while ((line = readLine("#")) != null) {	
			String[] code = line.split("\t");
			char codeSymbol = code[0].charAt(0);
			codes.put(codeSymbol, new ArrayList<>());
			String[] nucleotideSymbols = code[1].split(" +");
			for (String nucleotideSymbol : nucleotideSymbols) {
				codes.get(codeSymbol).add(nucleotideSymbol.charAt(0));
			}
		}
		
		try {
			Reader.reader.close();
		} catch (IOException e) { e.printStackTrace(); }
		
		return codes;
	}
	
	/**
	 * reads a single line from the file
	 * @param commentsPrefix - the prefix of coments (to ignore them) 
	 * @return - a line of the file (empty lines and comments are skipped)
	 */
	private static String readLine(String commentsPrefix) {
		try {
			String line;
			while ((line = reader.readLine()) != null) {	
				line = line.trim();
				if (line.contains(commentsPrefix)) {
					line = line.substring(0, line.indexOf(commentsPrefix)).trim(); 	// remove comments
				}
				if (line.equals("")) { continue; }									// skip empty lines
				break;
			}
			return line;
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}

}
