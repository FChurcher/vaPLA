
import controller.Aligner;
import controller.Settings;
import io.Writer;
import model.Alignment;
import model.Sequence;
import ui.ArgsParser;
import ui.TimeStampMaganer;

/**
 * the optimiced version
 * @author falco
 */
public class Main_GLOCAL {

	public static void main(String[] args) {
		TimeStampMaganer.getInstance().printGuide();
		TimeStampMaganer.getInstance().printTimeStamp("reading input files...");
//		Sequence[] sequences = Settings.init();
		Sequence[] sequences = ArgsParser.getInstance().initWithArgs(args);
		for (Sequence sequence : sequences) {
			System.out.println(sequence);
		}
		
		TimeStampMaganer.getInstance().printTimeStamp("aligning...");
		Alignment a = Aligner.getInstance().align(sequences);
		
		TimeStampMaganer.getInstance().printTimeStamp("writing files...");
		String writername = Settings.name;
		Writer.registerWriter(Writer.DIR_NAME_OUTPUT, writername);
		Writer.write(writername, "took " + TimeStampMaganer.getInstance().getActualRunTime() + " ms\n\n");
		Writer.write(writername, Settings.printToString() + "\n\n");
		Writer.write(writername, a.toString() + "\n");
//		Writer.write(writername, a.getHasseGraph().toLongString());
		
		// print graph
//		String graphWriterName = Settings.name + "_graph";
//		Writer.registerWriter(Writer.DIR_NAME_OUTPUT, graphWriterName);
//		Writer.write(graphWriterName, a.getHasseGraph());
		
		Writer.closeAll();
		TimeStampMaganer.getInstance().printTimeStamp("done");
		
		System.out.println(a);
	}
}
