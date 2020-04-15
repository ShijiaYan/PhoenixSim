package Simulations.timeDomain.switches;

import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import PhotonicElements.DirectionalCoupler.CompactCoupler;
import PhotonicElements.EffectiveIndexMethod.Structures.StripWg;
import PhotonicElements.EffectiveIndexMethod.ThermoOpticEffect.StripWg.ThermoOpticStripWgTE;
import PhotonicElements.Heater.SimpleHeater;
import PhotonicElements.Heater.Model.HeaterModel;
import PhotonicElements.Heater.Model.ImpulseResponseModel.ImpulseResponse1D_Modified_FFT;
import PhotonicElements.Heater.Model.Structure.SelfHeating;
import PhotonicElements.Heater.Model.VoltageFunc.AbstractVoltage;
import PhotonicElements.Heater.Model.VoltageFunc.StepVoltage;
import PhotonicElements.Switches.Switch2x2.Switch2x2MZITO;
import PhotonicElements.Utilities.Wavelength;
import PhotonicElements.Waveguides.CurvedWaveguide.BendLossMode.ConstantBendLossModel;
import PhotonicElements.Waveguides.WaveguideProperties.WgProperties;

public class Switch2x2MZITOSim {
	
	public static void main(String[] args){
		double length_um = 300 ;
		Wavelength inputLambda = new Wavelength(1550) ;
		WgProperties wgProp = new WgProperties(1, 2, 1, new ConstantBendLossModel(2)) ;
		AbstractVoltage voltage = new StepVoltage(3, 0, 3) ;
//		AbstractVoltage voltage = new PulseTrainVoltage(0.5, 10, 100, 0, 3) ;
		HeaterModel heater = new HeaterModel(new ImpulseResponse1D_Modified_FFT(30, 10), 
											 new SelfHeating(1e-3, 0.6, 850), 
											 voltage) ;
		double[] time = heater.transResponse.time_usec ;
		double[] wgTemp = heater.transResponse.transResponse ;
		
		MatlabChart fig0 = new MatlabChart() ;
		fig0.plot(time, wgTemp);
		fig0.RenderPlot();
		fig0.run(true);
		
		double[] transAmp = new double[time.length] ;
		double[] transPhase = new double[time.length] ;
		double[] phaseShiftDegree = new double[time.length] ;
		for(int i=0; i<time.length; i++){
			StripWg stripWg = new StripWg(inputLambda, wgProp.getWidthNm(), wgProp.getHeightNm()) ;
			ThermoOpticStripWgTE thermo = new ThermoOpticStripWgTE(stripWg, wgTemp[i]+27) ;
			phaseShiftDegree[i] = thermo.getDNeff() * inputLambda.getK0() * length_um*1e-6 ;
			SimpleHeater H = new SimpleHeater(phaseShiftDegree[i]) ;
			Switch2x2MZITO mzi = new Switch2x2MZITO(inputLambda, wgProp, length_um, new CompactCoupler(0, true), new CompactCoupler(0, true), H) ;
			transAmp[i] = mzi.S21.absSquared() ;
			transPhase[i] = mzi.S21.phase() ;
		}
		MatlabChart fig1 = new MatlabChart() ;
		fig1.plot(time, transAmp, "r");
		fig1.RenderPlot();
		fig1.run(true);
		
		MatlabChart fig3 = new MatlabChart() ;
		fig3.plot(wgTemp, transAmp, "g");
		fig3.RenderPlot();
		fig3.run(true);
		
		MatlabChart fig2 = new MatlabChart() ;
		fig2.plot(time, transPhase, "k");
		fig2.RenderPlot();
		fig2.run(true);
		
		MatlabChart fig4 = new MatlabChart() ;
		fig4.plot(time, phaseShiftDegree, "m");
		fig4.RenderPlot();
		fig4.run(true);
		
	}

}
