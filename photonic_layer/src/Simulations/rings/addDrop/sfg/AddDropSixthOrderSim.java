package Simulations.rings.addDrop.sfg;

import static java.lang.Math.PI;
import java.io.File;
import PhotonicElements.EffectiveIndexMethod.ModeSolver.StripWg.ModeStripWgTE;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.RingStructures.AddDrop.Graph.AddDropSixthOrder;
import PhotonicElements.Utilities.MathLibraries.MoreMath.Conversions;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import flanagan.io.FileOutput;


public class AddDropSixthOrderSim implements Experiment {

	AddDropSixthOrder adr;
	StripWg stripWg;
	double radius_um, loss_dBperCm, neff, lambda_nm;
	double kin, kout, k12, k23, k34, k45, k56;
	boolean print;
	public static boolean filePrint = true;
	public static String path = System.getProperty("user.home") + File.separator + "Desktop";

	public AddDropSixthOrderSim(@ParamName(name = "Strip Wg") StripWg stripWg,
			@ParamName(name = "radius (um)") double radius_um,
			@ParamName(name = "ring loss (dB/cm)") double loss_dBperCm, @ParamName(name = "input kappa") double kin,
			@ParamName(name = "kappa_12") double k12, @ParamName(name = "kappa_23") double k23,
			@ParamName(name = "kappa_34") double k34, @ParamName(name = "kappa_45") double k45,
			@ParamName(name = "kappa_56") double k56, @ParamName(name = "output kappa") double kout,
			@ParamName(name = "Print SFG to file?", default_ = "false") boolean print) {
		this.stripWg = stripWg;
		this.radius_um = radius_um;
		this.loss_dBperCm = loss_dBperCm;
		this.kin = kin;
		this.k12 = k12;
		this.k23 = k23;
		this.k34 = k34;
		this.k45 = k45;
		this.k56 = k56;
		this.kout = kout;
		ModeStripWgTE modeTE = new ModeStripWgTE(stripWg, 0, 0);
		neff = modeTE.getEffectiveIndex();
		lambda_nm = stripWg.getWavelengthNm();
		this.print = print;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		double phi_rad = 2 * PI / (lambda_nm * 1e-9) * neff * (2 * PI * radius_um * 1e-6);
		double loss_dB = loss_dBperCm * (radius_um * 1e-4);
		double loss = Conversions.fromdB(loss_dB);
		adr = new AddDropSixthOrder(phi_rad, loss, kin, kout, k12, k23, k34, k45, k56);

		DataPoint dp = new DataPoint();
		dp.addProperty("Wavelength (nm)", lambda_nm);
		dp.addProperty("kappa_in", kin);
		dp.addProperty("kappa_12", k12);
		dp.addProperty("kappa_23", k23);
		dp.addProperty("kappa_out", kout);
		dp.addProperty("loss (dB/cm)", loss_dBperCm);
		dp.addProperty("radius (um)", radius_um);
		dp.addResultProperty("Drop (dB)", Conversions.todB(adr.getS31().absSquared()));
		dp.addResultProperty("Thru (dB)", Conversions.todB(adr.getS21().absSquared()));
		man.addDataPoint(dp);

		if (filePrint && print) {
			FileOutput fout = new FileOutput(path + File.separator + "adr_sixth_order_drop_thru.txt");
			String st1 = "==============DROP PORT============";
			String st2 = "==============THRU PORT============";
			String st3 = "==============ALL LOOPS============";
			fout.println(st1);
			adr.buildDrop();
			fout.println(adr.sfg.printForwardPaths());
			fout.println(st2);
			adr.buildThru();
			fout.println(adr.sfg.printForwardPaths());
			fout.println(st3);
			fout.println(adr.sfg.printForwardPaths());
			fout.close();
			System.gc();
			filePrint = false;
		}

	}

}