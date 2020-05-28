package People.Natalie.tutorials.demos;

import People.Natalie.tutorials.devices.SwitchEO2x2MZI;
import PhotonicElements.PNJunction.PlasmaDispersionModel;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;

public class Switch2x2MZIDemo implements Experiment {

    SwitchEO2x2MZI mziSwitch;

    /**
     * in this demo, we set the wavelength to 1550nm and sweep the plasma effect to find the BAR and CROSS states.
     * Waveguide loss is set to 1 dB/cm.
     *
     * @param mziSwitch
     */

    public Switch2x2MZIDemo(
            @ParamName(name = "Length of Arm of Mach-Zehnder (um)", default_ = "300") double lengthOfArmMicron,
            PlasmaDispersionModel plasmaEffect
    ) {
        Wavelength inputLambda = new Wavelength(1550);
        WgProperties wgProp = new WgProperties(10, 1, 1, null);
        mziSwitch = new SwitchEO2x2MZI(inputLambda, wgProp, lengthOfArmMicron, 0, plasmaEffect);
    }

    @Override
    public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
        DataPoint dp = new DataPoint();

        // adding the input parameters
        dp.addProperty("wavelength (nm)", mziSwitch.lambdaNm);
        dp.addProperty("Length of Arm (um)", mziSwitch.getUpperWg().getWgLengthMicron());
        dp.addProperty("Delta Alpha (1/cm)", mziSwitch.getPlasmaEffect().getDalphaPerCm());

        // also checking the phase shift of the EO phase shifter (it's in the lower arm)
        dp.addResultProperty("EO phase shift (degree)",
				mziSwitch.getLowerWg().getS21().phaseDegree() - mziSwitch.getUpperWg().getS21().phaseDegree());

        // adding the optical power transmission
        dp.addResultProperty("Cross Power Transmission (dB)",
				MoreMath.Conversions.todB(mziSwitch.getS31().absSquared()));
        dp.addResultProperty("Thru Power Transmission (dB)", MoreMath.Conversions.todB(mziSwitch.getS21().absSquared()));

        man.addDataPoint(dp);
    }

}
