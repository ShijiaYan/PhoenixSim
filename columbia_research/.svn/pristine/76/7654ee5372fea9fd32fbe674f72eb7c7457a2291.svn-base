package edu.columbia.sebastien.autobooksim;

import java.io.File;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;

public class AutoBookSimExperiment implements Experiment {
	
	private String booksimDirectory;
	private BooksimConfig config;
	
	private boolean displayOutput = true;
	
	public AutoBookSimExperiment(
			@ParamName(name="Booksim directory (relative to autobooksim home)") String booksimDirectory,
			BooksimConfig config) {
		this.booksimDirectory = booksimDirectory;
		this.config = config;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis)
			throws WrongExperimentException {
		
		ensureDirectory();
		
		config.makeAndwrite(booksimDirectory);
		
		Process proc = null;
		
		try {
			ProcessBuilder pb;
			if (System.getProperty("os.name").startsWith("Windows")) {
				String winBooksimDirectory = booksimDirectory.replaceAll("/", "\\\\");
				
				pb = new ProcessBuilder("CMD", "/C", 
						 "booksim.exe ",  config.getConfigFileNameAndId());
				pb.directory(new File(booksimDirectory));
			} else {	
				pb = 
					new ProcessBuilder(booksimDirectory + "booksim.exe", config.getConfigFileNameAndId());
			}

			
		//	if (displayOutput) {
		//		pb.inheritIO();
		//	} else {
				pb.redirectOutput(new File("sst.out"));
				pb.redirectError(new File("sst.err"));
		//	}
			
			proc = pb.start();
			proc.waitFor();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		catch (ThreadDeath td) {
			System.out.println("You killed me");
		}
		finally {
			if (proc != null) {
				proc.destroy();
				proc = null;
				synchronized(this) {
					this.notifyAll();
				}
			}
		}
	}

	private void ensureDirectory() {
		if (!booksimDirectory.endsWith("/")) {
			booksimDirectory = booksimDirectory + "/";
		}
		
		File f = new File(booksimDirectory);
		f.mkdirs();
	}
	

}
