package ui;

import controller.Settings;
import model.Sequence;

public class ArgsParser {
  // singleton intrinsic
	/** the singleton instance */
	protected static ArgsParser instance;
	
	/**
	 * singleton get instance method
	 * @return an instance of this class
	 */
	public static ArgsParser getInstance(){
			if (instance == null) {
		instance = new ArgsParser();
		}
		return instance;
	}
	
  // object defenition
	public Sequence[] initWithArgs(String[] args) {
		if (args.length == 1) {
			return Settings.init(args[0]);
		} else if (args.length == 2) {
			return Settings.init(args[0], args[1]);
		} else if (args.length == 3) {
			return Settings.init(args[0], args[1], args[2]);
		} else if (args.length == 4) {
			return Settings.init(args[0], args[1], args[2], args[3]);
		} else {
			System.out.println("none or wrong input was given.");
			System.out.println("usage:\nvaPLA.jar [sequencesFilePath] [localityPath] [scoringFilePath] [codeFilePath]\nvaPLA.jar [sequencesLocalitiesName] [scoringFilePath] [codeFilePath]\nvaPLA.jar [sequencesLocalitiesName] [scoringFilePath]\nvaPLA.jar [sequencesLocalitiesName]\n  EXAMPLE:\n  java -jar vaPLA.jar data/example");
			System.exit(0);
			return null;
		}
	}

}
