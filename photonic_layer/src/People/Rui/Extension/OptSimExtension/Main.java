package People.Rui.Extension.OptSimExtension;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Main {

	public static void main(String[] args) throws IOException {
		/*
		 * // Test globalParameters
		 * int a=3;
		 * 
		 * GlobalParameters globalTest = new GlobalParameters("test");
		 * globalTest.setParameters();
		 * globalTest.setWriteFilePropertyString();
		 * 
		 * System.out.println(globalTest.propertyString);
		 * System.out.println(globalTest.rootProperty.property.get(4).flag);
		 * 
		 * 
		 * 
		 * // test CW Lasers
		 * CWLaserComponent testCWLaser = new CWLaserComponent("cw laser 1");
		 * testCWLaser.setWriteFilePropertyString();
		 * 
		 * System.out.println(testCWLaser.propertyString);
		 * 
		 * // test OptMux
		 * OptMuxComponent testMux = new OptMuxComponent("Mux 1", 4);
		 * testMux.setWriteFilePropertyString();
		 * 
		 * 
		 * // test SignalGenerator
		 * SignalGeneratorComponent testSignalGenerator = new SignalGeneratorComponent("wavelength 1");
		 * testSignalGenerator.setWriteFilePropertyString();
		 * 
		 * // test driver
		 * DriverComponent testDriver = new DriverComponent("Driver 1");
		 * testDriver.setWriteFilePropertyString();
		 * 
		 * // test Ring Modulator
		 * RingModulatorComponent testRingModulator = new RingModulatorComponent("RingModulator 1");
		 * testRingModulator.setWriteFilePropertyString();
		 * 
		 * // test Ring Resonator
		 * RingResonatorComponent testRingResonator = new RingResonatorComponent("Ring Resonator 1");
		 * testRingResonator.setWriteFilePropertyString();
		 * 
		 * // test Spectrum Analyzer
		 * SpectrumAnalyzerComponent testSpectrumAnalyzer = new SpectrumAnalyzerComponent("Analyzer 1");
		 * testSpectrumAnalyzer.setWriteFilePropertyString();
		 * 
		 * // test Nonlinear Fiber
		 * NonlinearFiberComponent testNLFiberComponent = new NonlinearFiberComponent("fiber");
		 * testNLFiberComponent.setWriteFilePropertyString();
		 * 
		 * // test Link System
		 * OptSimLinkSystem testLink = new OptSimLinkSystem(4);
		 * 
		 * // test pdk component
		 * CustomPDKExample testPdk = new CustomPDKExample("testPdk", "testWaveguidePDK","L","50",1,1, 520, 320 );
		 * testPdk.setWriteFilePropertyString();
		 * System.out.println(testPdk.propertyString);
		 * 
		 * try(PrintWriter out = new PrintWriter(new BufferedWriter(new
		 * FileWriter("C:\\\\Users\\Charey\\Desktop\\test.moml", false)))) {
		 * out.println(testLink.writeFileString);
		 * }catch (IOException e) {
		 * System.err.println(e);
		 * }
		 * 
		 * System.out.println(a/2);
		 */

		/******************* test demofab *********************/
		/*
		 * DemofabTransmitter testDemofab = new DemofabTransmitter(4);
		 * 
		 * try(PrintWriter out = new PrintWriter(new BufferedWriter(new
		 * FileWriter("C:\\Users\\Charey\\Desktop\\test\\testDemofab.moml")))) {
		 * out.println(testDemofab.getWriteString());
		 * }catch (IOException e) {
		 * System.err.println(e);
		 * }
		 */

		// ****************** test read string from file *******************
		String content = readFile(
				"D:\\javanco_env\\PhotonicLayer\\src\\People\\Rui\\Extension\\OptSimExtension\\optsim_pathway.txt",
				StandardCharsets.UTF_8);
		System.out.println(content);

		// Add two new properties nesting in rootProperty
		/*
		 * OptSimProperty newProperty2 = new OptSimProperty("peakPower",
		 * "ptolemy.data.expr.Parameter","1.0E-3");
		 * OptSimProperty childProperty = new OptSimProperty("wavelength","ptolemy.data.expr.Parameter"
		 * ,"1.55E-6");
		 * 
		 * OptSimProperty newChildProperty = new OptSimProperty("mode","ptolemy.data.expr.Parameter",
		 * "&quot;Single&quot;");
		 * testCW.rootProperty.property.add(newProperty1);
		 * testCW.rootProperty.property.add(newProperty2);
		 * 
		 * testCW.rootProperty.property.get(1).property.add(childProperty);
		 * testCW.rootProperty.property.get(1).property.get(0).property.add(newChildProperty);
		 */
		// try to see the tree structure

		// Note: remember you should always setCWLaser before you use it.

		// Now we have test a single component, let's think about the whole system
		// You might want to have access to all the parameters in the system, let's try if it's possible for that

		// Runtime rt = Runtime.getRuntime();
		// File file = new File("C:\\Users\\Charey\\Desktop\\SingleRing.moml");
		// Desktop.getDesktop().open(file);

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
