package People.Meisam.GUI.ThermalHeater.MainGUI;

import People.Meisam.GUI.Builders.AbstractController;
import People.Meisam.GUI.Builders.WindowBuilder;
import People.Meisam.GUI.Plotters.MainGUI.PlotterController;
import People.Meisam.GUI.Plotters.MatlabPlot.MatlabChart;
import People.Meisam.GUI.Utilities.DataBaseTable.DataBase_v1_0.DatabaseTableController;
import People.Meisam.GUI.Utilities.ExportPlot.ExportToMATLAB.ExportToMatlabController;
import People.Meisam.GUI.Utilities.SimulationDataBase;
import People.Meisam.GUI.Utilities.SimulationVariable;
import PhotonicElements.Heater.Model.VoltageFunc.AbstractVoltage;
import PhotonicElements.Heater.Model.VoltageFunc.PulseTrainVoltage;
import PhotonicElements.Heater.Model.VoltageFunc.SinVoltage;
import PhotonicElements.Heater.Model.VoltageFunc.StepVoltage;
import PhotonicElements.Heater.Model.ImpulseResponseModel.ImpulseResponse1D_Modified_FFT;
import PhotonicElements.Heater.Model.Structure.SelfHeating;
import PhotonicElements.Heater.Model.TransientResponseModel.TransientResponse;
import PhotonicElements.Utilities.MathLibraries.MoreMath;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Created by meisam on 7/1/17.
 */
public class HeaterController extends AbstractController {

    // photonic elements needed for the physical simulation
    SelfHeating selfHeating ;
    ImpulseResponse1D_Modified_FFT impulse ;
    AbstractVoltage voltage ;
    TransientResponse transResponse ;

    // defining simulation database and the figures in the simulation
    SimulationDataBase simDataBase = new SimulationDataBase() ;
    MatlabChart figDC, figFreq, figImpulse, figStep, figSin, figPWM ;

    public void setSimDataBase(SimulationDataBase simDataBase){
        this.simDataBase = simDataBase ;
    }

    public SimulationDataBase getSimDataBase(){
        return simDataBase ;
    }

	//******** Parameters for the menuBar ****************************************************************
	@FXML MenuItem closeApp ;
	@FXML MenuItem newApp ;
	@FXML MenuItem popupPlot ;
	@FXML MenuItem dataBase ;
	@FXML TabPane tabs ;
	@FXML Tab dcTab ;
	@FXML Tab freqTab ;
	@FXML Tab impulseTab ;
	@FXML Tab stepTab ;
	@FXML Tab sinTab ;
	@FXML Tab pwmTab ;
    //********* parameters related to DC characteristic tab ****************************************************************
    @FXML TextField KvTextField ;
    @FXML TextField RlinearTextField ;
    @FXML TextField aHTextField ;
    @FXML Pane dcMatlabPlotPane ;
    @FXML RadioButton viPlotRadioButton ;
    @FXML RadioButton tempVRadioButton ;
    @FXML RadioButton tempIRadioButton ;
    @FXML RadioButton ivPlotRadioButton ;
    @FXML ToggleGroup dcPlot ;
    @FXML Label kvLabel ;
    @FXML Label RlinearLabel ;
    @FXML Label aHLabel ;


    private void dcTab_initialize(){
        kvLabel.setText("");
        RlinearLabel.setText("");
        aHLabel.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figDC = fig ;
        showPlot(fig, dcMatlabPlotPane);
    }

    private boolean checkFilledTextsInDCTab(){
        if(!kvLabel.getText().isEmpty() && !RlinearLabel.getText().isEmpty() && !aHLabel.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean dcToggleIsSelected(){
        if(ivPlotRadioButton.isSelected() || viPlotRadioButton.isSelected() || tempIRadioButton.isSelected() || tempVRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    private void setKv(){
        double kv = Double.parseDouble(KvTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("kv", new double[]{kv}));
        kvLabel.setText("Kv is set to " + kv + " /Volt^2");
        if(dcToggleIsSelected()){
            dcPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void setRlinear(){
        double Rlinear = Double.parseDouble(RlinearTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("Rlinear", new double[]{Rlinear}));
        RlinearLabel.setText("R is set to " + Rlinear + " Ohms");
        if(dcToggleIsSelected()){
            dcPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void setaH(){
        double alphaH = Double.parseDouble(aHTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("alphaH", new double[]{alphaH}));
        aHLabel.setText("aH is set to " + alphaH + " /Kelvin");
        if(dcToggleIsSelected()){
            dcPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotDC(){
        if(checkFilledTextsInDCTab()){
            double[] VH_V = MoreMath.linspace(0, 5, 1000) ;
            double alphaH = simDataBase.getVariable("alphaH").getValue(0) ;
            double kv = simDataBase.getVariable("kv").getValue(0) ;
            double Rlinear = simDataBase.getVariable("Rlinear").getValue(0) ;
            selfHeating = new SelfHeating(alphaH, kv, Rlinear) ;
            double[] IH_mA = selfHeating.getCurrent_mA(VH_V) ;
            double[] deltaTH_K = selfHeating.getDeltaT(VH_V) ;
            simDataBase.addNewVariable(new SimulationVariable("VH_(V)", "Heater Voltage (V)", VH_V));
            simDataBase.addNewVariable(new SimulationVariable("IH_(mA)", "Heater Current (mA)", IH_mA));
            simDataBase.addNewVariable(new SimulationVariable("deltaTH_(K)", "Heater Temperature (K)", deltaTH_K));
        }

        if(dcPlot.getSelectedToggle().equals(viPlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("IH_(mA)"), simDataBase.getVariableValues("VH_(V)"));
            fig.RenderPlot();
            fig.xlabel("Heater Current (mA)");
            fig.ylabel("Heater Voltage (V)");
            figDC = fig ;
            showPlot(fig, dcMatlabPlotPane);
        }
        else if(dcPlot.getSelectedToggle().equals(tempVRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("VH_(V)"), simDataBase.getVariableValues("deltaTH_(K)"));
            fig.RenderPlot();
            fig.xlabel("Heater Voltage (V)");
            fig.ylabel("Heater Temperature (K)");
            figDC = fig ;
            showPlot(fig, dcMatlabPlotPane);
        }
        else if(dcPlot.getSelectedToggle().equals(tempIRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("IH_(mA)"), simDataBase.getVariableValues("deltaTH_(K)"));
            fig.RenderPlot();
            fig.xlabel("Heater Current (mA)");
            fig.ylabel("Heater Temperature (K)");
            figDC = fig ;
            showPlot(fig, dcMatlabPlotPane);
        }
        else if(dcPlot.getSelectedToggle().equals(ivPlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("VH_(V)"), simDataBase.getVariableValues("IH_(mA)"));
            fig.RenderPlot();
            fig.xlabel("Heater Voltage (V)");
            fig.ylabel("Heater Current (mA)");
            figDC = fig ;
            showPlot(fig, dcMatlabPlotPane);
        }

    }

  //********* parameters related to freq characteristic tab ****************************************************************
    @FXML Pane freqMatlabPlotPane ;
    @FXML TextField f0TextField ;
    @FXML TextField nuTextField ;
    @FXML Label f0Label ;
    @FXML Label nuLabel ;
    @FXML ToggleGroup freqPlot ;
    @FXML RadioButton ampdBPlotRadioButton ;
    @FXML RadioButton ampPlotRadioButton ;
    @FXML RadioButton phaseRadRadioButton ;
    @FXML RadioButton phaseDegreeRadioButton ;

    private void freqTab_initialize(){
        f0Label.setText("");
        nuLabel.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figFreq = fig ;
        showPlot(fig, freqMatlabPlotPane);
    }

    private boolean checkFilledTextsInFreqTab(){
        if(!f0Label.getText().isEmpty() && !nuLabel.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean freqToggleIsSelected(){
        if(ampdBPlotRadioButton.isSelected() || ampPlotRadioButton.isSelected() || phaseRadRadioButton.isSelected() || phaseDegreeRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    public void setf0(){
        double f0 = Double.parseDouble(f0TextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("f0", new double[]{f0}));
        f0Label.setText("f0 is set to " + f0 + " kHz");
        if(freqToggleIsSelected()){
            freqPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setNu(){
        double nu = Double.parseDouble(nuTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("nu", new double[]{nu}));
        nuLabel.setText("nu is set to " + nu + " ");
        if(freqToggleIsSelected()){
            freqPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotFreq(){
        if(checkFilledTextsInFreqTab()){
            double[] freq_kHz = MoreMath.linspace(0, 200, 400) ;
            double[] freq_Hz = MoreMath.Arrays.times(freq_kHz, 1e3) ;
            double f0 = simDataBase.getVariable("f0").getValue(0) ;
            double nu = simDataBase.getVariable("nu").getValue(0) ;
            impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
            double[] amp = impulse.getFreqResponse(freq_Hz) ;
            double[] amp_dB = impulse.getFreqResponsedB(freq_Hz) ;
            double[] phase_rad = impulse.getfreqResponsePhaseRad(freq_Hz) ;
            double[] phase_degree = impulse.getfreqResponsePhaseDegree(freq_Hz) ;
            simDataBase.addNewVariable(new SimulationVariable("freq_(kHz)", "Frequency (kHz)", freq_kHz));
            simDataBase.addNewVariable(new SimulationVariable("amp_(dB)", "Amplitude (dB)", amp_dB));
            simDataBase.addNewVariable(new SimulationVariable("amp", "Amplitude", amp));
            simDataBase.addNewVariable(new SimulationVariable("phase_(Rad)", "Phase (Rad)", phase_rad));
            simDataBase.addNewVariable(new SimulationVariable("phase_(Degree)", "Phase (Degree)", phase_degree));
        }

        if(freqPlot.getSelectedToggle().equals(ampdBPlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("freq_(kHz)"), simDataBase.getVariableValues("amp_(dB)"));
            fig.RenderPlot();
            fig.xlabel("Frequency (kHz)");
            fig.ylabel("Amplitude (dB)");
            figFreq = fig ;
            showPlot(fig, freqMatlabPlotPane);
        }
        else if(freqPlot.getSelectedToggle().equals(ampPlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("freq_(kHz)"), simDataBase.getVariableValues("amp"));
            fig.RenderPlot();
            fig.xlabel("Frequency (kHz)");
            fig.ylabel("Amplitude");
            figFreq = fig ;
            showPlot(fig, freqMatlabPlotPane);
        }
        else if(freqPlot.getSelectedToggle().equals(phaseRadRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("freq_(kHz)"), simDataBase.getVariableValues("phase_(Rad)"));
            fig.RenderPlot();
            fig.xlabel("Frequency (kHz)");
            fig.ylabel("Phase (Rad)");
            figFreq = fig ;
            showPlot(fig, freqMatlabPlotPane);
        }
        else if(freqPlot.getSelectedToggle().equals(phaseDegreeRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("freq_(kHz)"), simDataBase.getVariableValues("phase_(Degree)"));
            fig.RenderPlot();
            fig.xlabel("Frequency (kHz)");
            fig.ylabel("Phase (Degree)");
            figFreq = fig ;
            showPlot(fig, freqMatlabPlotPane);
        }

    }

  //********* parameters related to impulse response tab ****************************************************************
    @FXML Pane impulseMatlabPlotPane ;
    @FXML TextField impulseStartTimeTextField ;
    @FXML TextField impulseEndTimeTextField ;
    @FXML Label impulseStartTimeLabel ;
    @FXML Label impulseEndTimeLabel ;
    @FXML ToggleGroup impulsePlot ;
    @FXML RadioButton impulsePlotRadioButton ;
    @FXML RadioButton impulseNormalPlotRadioButton ;

    private void impulseTab_initialize(){
        impulseStartTimeLabel.setText("");
        impulseEndTimeLabel.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figImpulse = fig ;
        showPlot(fig, impulseMatlabPlotPane);
    }

    private boolean checkFilledTextsInImpulseTab(){
        if(!impulseStartTimeLabel.getText().isEmpty() && !impulseEndTimeLabel.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean impulseToggleIsSelected(){
        if(impulsePlotRadioButton.isSelected() || impulseNormalPlotRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    public void setImpulseStartTime(){
        double impulse_tStart_usec = Double.parseDouble(impulseStartTimeTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("impulse_tStart_(usec)", new double[]{impulse_tStart_usec}));
        impulseStartTimeLabel.setText("Start time is set to " + impulse_tStart_usec + " usec");
        if(impulseToggleIsSelected()){
            impulsePlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setImpulseEndTime(){
        double impulse_tEnd_usec = Double.parseDouble(impulseEndTimeTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("impulse_tEnd_(usec)", new double[]{impulse_tEnd_usec}));
        impulseEndTimeLabel.setText("End time is set to " + impulse_tEnd_usec + " usec");
        if(impulseToggleIsSelected()){
            impulsePlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotImpulse(){
        if(checkFilledTextsInImpulseTab() && checkFilledTextsInFreqTab()){
            double tStart_usec = simDataBase.getVariable("impulse_tStart_(usec)").getValue(0) ;
            double tEnd_usec = simDataBase.getVariable("impulse_tEnd_(usec)").getValue(0) ;
            double[] time_usec = MoreMath.linspace(tStart_usec, tEnd_usec, 400) ;
            if(checkFilledTextsInFreqTab()){
                double f0 = simDataBase.getVariable("f0").getValue(0) ;
                double nu = simDataBase.getVariable("nu").getValue(0) ;
                impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
                double[] Iwg = impulse.getTimeResponse(time_usec) ;
                double Iwg_max = MoreMath.Arrays.FindMaximum.getValue(Iwg) ;
                double[] Iwg_normalized = MoreMath.Arrays.times(Iwg, 1/Iwg_max) ;
                simDataBase.addNewVariable(new SimulationVariable("Iwg", "Impulse Response", Iwg));
                simDataBase.addNewVariable(new SimulationVariable("Iwg_normalized", "Normalized Impulse Response", Iwg_normalized));
                simDataBase.addNewVariable(new SimulationVariable("impulse_time_(usec)", "Time (usec)", time_usec));
            }
        }

        if(impulsePlot.getSelectedToggle().equals(impulsePlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("impulse_time_(usec)"), simDataBase.getVariableValues("Iwg"));
            fig.RenderPlot();
            fig.xlabel("Time (usec)");
            fig.ylabel("Impulse Response");
            figImpulse = fig ;
            showPlot(fig, impulseMatlabPlotPane);
        }
        else if(impulsePlot.getSelectedToggle().equals(impulseNormalPlotRadioButton)){
            MatlabChart fig = new MatlabChart() ;
            fig.plot(simDataBase.getVariableValues("impulse_time_(usec)"), simDataBase.getVariableValues("Iwg_normalized"));
            fig.RenderPlot();
            fig.xlabel("Time (usec)");
            fig.ylabel("Normalized Impulse Response");
            figImpulse = fig ;
            showPlot(fig, impulseMatlabPlotPane);
        }

    }

  //********* parameters related to step response tab ****************************************************************
    @FXML Pane stepMatlabPlotPane ;
    @FXML TextField stept0TextField ;
    @FXML TextField stepV0TextField ;
    @FXML Label stept0Label ;
    @FXML Label stepV0Label ;
    @FXML ToggleGroup stepPlot ;
    @FXML RadioButton stepWaveguidePlotRadioButton ;
    @FXML RadioButton stepHeaterPlotRadioButton ;

    private void stepTab_initialize(){
        stept0Label.setText("");
        stepV0Label.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figStep = fig ;
        showPlot(fig, stepMatlabPlotPane);
    }

    private boolean checkFilledTextsInStepTab(){
        if(!stept0Label.getText().isEmpty() && !stepV0Label.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean stepToggleIsSelected(){
        if(stepWaveguidePlotRadioButton.isSelected() || stepHeaterPlotRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    public void setT0(){
        double step_t0_usec = Double.parseDouble(stept0TextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("step_t0_(usec)", new double[]{step_t0_usec}));
        stept0Label.setText("t0 is set to " + step_t0_usec + " usec");
        if(stepToggleIsSelected()){
            stepPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setV0(){
        double step_V0_V = Double.parseDouble(stepV0TextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("step_V0_(V)", new double[]{step_V0_V}));
        stepV0Label.setText("V0 is set to " + step_V0_V + " Volt");
        if(stepToggleIsSelected()){
            stepPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotStep(){
    	if(checkFilledTextsInStepTab()){
	        double t0 = simDataBase.getVariable("step_t0_(usec)").getValue(0) ;
	        double V0 = simDataBase.getVariable("step_V0_(V)").getValue(0) ;
	        double tStart_usec = simDataBase.getVariable("impulse_tStart_(usec)").getValue(0) ;
	        double tEnd_usec = simDataBase.getVariable("impulse_tEnd_(usec)").getValue(0) ;
	        double[] time_usec = MoreMath.linspace(tStart_usec, tEnd_usec, 200) ;
	        int M = time_usec.length ;
	        double[] heaterStepVoltage_V = new double[M] ;
	        voltage = new StepVoltage(t0, 0, V0) ;
	        for(int i=0; i<M; i++){
	            heaterStepVoltage_V[i] = voltage.getVoltage(time_usec[i]) ;
	        }
	        double f0 = simDataBase.getVariable("f0").getValue(0) ;
	        double nu = simDataBase.getVariable("nu").getValue(0) ;
	        double[] deltaT_H = selfHeating.getDeltaT(heaterStepVoltage_V) ;
	        double[] step_dc_response = MoreMath.Arrays.times(deltaT_H, (nu-1)/nu) ;
	        simDataBase.addNewVariable(new SimulationVariable("heater_step_temperature_(K)", "Heater Temperature (K)", deltaT_H));
	        simDataBase.addNewVariable(new SimulationVariable("wg_step_dc_temperature_(K)", step_dc_response));
	        simDataBase.addNewVariable(new SimulationVariable("heater_step_voltage_(V)", heaterStepVoltage_V));
	        simDataBase.addNewVariable(new SimulationVariable("step_time_(usec)", "Time (usec)", time_usec));

	        if(stepPlot.getSelectedToggle().equals(stepWaveguidePlotRadioButton)){
	            impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
	            double[] t_usec = simDataBase.getVariableValues("impulse_time_(usec)") ;
	            double[] impulsevalues = simDataBase.getVariableValues("Iwg") ;
	            transResponse = new TransientResponse(selfHeating, impulse, voltage) ;
	            double[] step_response = transResponse.getTimeResponse(time_usec, t_usec, impulsevalues) ;
	            simDataBase.addNewVariable(new SimulationVariable("wg_step_temperature_(K)", "Waveguide Temperature (K)", step_response));
	            MatlabChart fig = new MatlabChart() ;
	            fig.plot(simDataBase.getVariableValues("step_time_(usec)"), simDataBase.getVariableValues("wg_step_temperature_(K)"));
	            fig.plot(simDataBase.getVariableValues("step_time_(usec)"), simDataBase.getVariableValues("wg_step_dc_temperature_(K)"), ":r", 1.0f, "");
	            fig.RenderPlot();
	            fig.xlabel("Time (usec)");
	            fig.ylabel("Waveguide Temperature (K)");
	            figStep = fig ;
	            showPlot(fig, stepMatlabPlotPane);
	        }
	        else if(stepPlot.getSelectedToggle().equals(stepHeaterPlotRadioButton)){
	            MatlabChart fig = new MatlabChart() ;
	            fig.plot(simDataBase.getVariableValues("step_time_(usec)"), simDataBase.getVariableValues("heater_step_temperature_(K)"));
	            fig.RenderPlot();
	            fig.xlabel("Time (usec)");
	            fig.ylabel("Heater Temperature (K)");
	            figStep = fig ;
	            showPlot(fig, stepMatlabPlotPane);
	        }
    	}


    }

    //********* parameters related to sin response tab ****************************************************************
    @FXML Pane sinMatlabPlotPane ;
    @FXML TextField sint0TextField ;
    @FXML TextField sinV0TextField ;
    @FXML TextField sinFreqTextField ;
    @FXML TextField sinVpTextField ;
    @FXML Label sint0Label ;
    @FXML Label sinV0Label ;
    @FXML Label sinFreqLabel ;
    @FXML Label sinVpLabel ;
    @FXML ToggleGroup sinPlot ;
    @FXML RadioButton sinWaveguidePlotRadioButton ;
    @FXML RadioButton sinHeaterPlotRadioButton ;

    private void sinTab_initialize(){
        sint0Label.setText("");
        sinV0Label.setText("");
        sinFreqLabel.setText("");
        sinVpLabel.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figSin = fig ;
        showPlot(fig, sinMatlabPlotPane);
    }

    private boolean checkFilledTextsInSinTab(){
        if(!sint0Label.getText().isEmpty() && !sinV0Label.getText().isEmpty() && !sinFreqLabel.getText().isEmpty() && !sinVpLabel.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean sinToggleIsSelected(){
        if(sinWaveguidePlotRadioButton.isSelected() || sinHeaterPlotRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    public void setV0Sin(){
        double sin_V0_V = Double.parseDouble(sinV0TextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("sin_V0_(V)", new double[]{sin_V0_V}));
        sinV0Label.setText("V0 is set to " + sin_V0_V + " Volt");
        if(sinToggleIsSelected()){
            sinPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setT0Sin(){
        double sin_t0_usec = Double.parseDouble(sint0TextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("sin_t0_(usec)", new double[]{sin_t0_usec}));
        sint0Label.setText("t0 is set to " + sin_t0_usec + " usec");
        if(sinToggleIsSelected()){
            sinPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setFreqSin(){
        double sin_freq_kHz = Double.parseDouble(sinFreqTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("sin_freq_(kHz)", new double[]{sin_freq_kHz}));
        sinFreqLabel.setText("freq is set to " + sin_freq_kHz + " kHz");
        if(sinToggleIsSelected()){
            sinPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setVpSin(){
        double sin_Vp_V = Double.parseDouble(sinVpTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("sin_Vp_(V)", new double[]{sin_Vp_V}));
        sinVpLabel.setText("Vp is set to " + sin_Vp_V + " Volt");
        if(sinToggleIsSelected()){
            sinPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotSin(){
    	if(checkFilledTextsInSinTab()){
            double t0 = simDataBase.getVariable("sin_t0_(usec)").getValue(0) ;
            double V0 = simDataBase.getVariable("sin_V0_(V)").getValue(0) ;
            double freq = simDataBase.getVariable("sin_freq_(kHz)").getValue(0) ;
            double Vp = simDataBase.getVariable("sin_Vp_(V)").getValue(0) ;
            double tStart_usec = simDataBase.getVariable("impulse_tStart_(usec)").getValue(0) ;
            double tEnd_usec = simDataBase.getVariable("impulse_tEnd_(usec)").getValue(0) ;
            double[] time_usec = MoreMath.linspace(tStart_usec, tEnd_usec, 500) ;
            int M = time_usec.length ;
            double[] heaterSinVoltage_V = new double[M] ;
            double[] v_dc = new double[M] ;
            voltage = new SinVoltage(t0, V0, Vp, freq) ;
            StepVoltage vDC = new StepVoltage(t0, 0, V0) ;
            for(int i=0; i<M; i++){
                heaterSinVoltage_V[i] = voltage.getVoltage(time_usec[i]) ;
                v_dc[i] = vDC.getVoltage(time_usec[i]) ;
            }
            double f0 = simDataBase.getVariable("f0").getValue(0) ;
            double nu = simDataBase.getVariable("nu").getValue(0) ;
            double[] deltaT_H = selfHeating.getDeltaT(heaterSinVoltage_V) ;
            double[] deltaT_H_dc = selfHeating.getDeltaT(v_dc) ;
            double[] sin_dc_response = MoreMath.Arrays.times(deltaT_H_dc, (nu-1)/nu) ;
            simDataBase.addNewVariable(new SimulationVariable("heater_sin_temperature_(K)", "Heater Temperature (K)", deltaT_H));
            simDataBase.addNewVariable(new SimulationVariable("wg_sin_dc_temperature_(K)", sin_dc_response));
            simDataBase.addNewVariable(new SimulationVariable("heater_sin_voltage_(V)", heaterSinVoltage_V));
            simDataBase.addNewVariable(new SimulationVariable("sin_time_(usec)", "Time (usec)", time_usec));

            if(sinPlot.getSelectedToggle().equals(sinWaveguidePlotRadioButton)){
                impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
                double[] t_usec = simDataBase.getVariableValues("impulse_time_(usec)") ;
                double[] impulsevalues = simDataBase.getVariableValues("Iwg") ;
                transResponse = new TransientResponse(selfHeating, impulse, voltage) ;
                double[] response = transResponse.getTimeResponse(time_usec, t_usec, impulsevalues) ;
                simDataBase.addNewVariable(new SimulationVariable("wg_sin_temperature_(K)", "Waveguide Temperature (K)", response));
                MatlabChart fig = new MatlabChart() ;
                fig.plot(simDataBase.getVariableValues("sin_time_(usec)"), simDataBase.getVariableValues("wg_sin_temperature_(K)"));
                fig.plot(simDataBase.getVariableValues("sin_time_(usec)"), simDataBase.getVariableValues("wg_sin_dc_temperature_(K)"), ":r", 1.0f, "");
                fig.RenderPlot();
                fig.xlabel("Time (usec)");
                fig.ylabel("Waveguide Temperature");
                figSin = fig ;
                showPlot(fig, sinMatlabPlotPane);
            }
            else if(sinPlot.getSelectedToggle().equals(sinHeaterPlotRadioButton)){
                MatlabChart fig = new MatlabChart() ;
                fig.plot(simDataBase.getVariableValues("sin_time_(usec)"), simDataBase.getVariableValues("heater_sin_temperature_(K)"));
                fig.RenderPlot();
                fig.xlabel("Time (usec)");
                fig.ylabel("Heater Temperature");
                figSin = fig ;
                showPlot(fig, sinMatlabPlotPane);
            }
    	}

    }

    //********* parameters related to pwm response tab ****************************************************************
    @FXML Pane pwmMatlabPlotPane ;
    @FXML TextField pwmVpTextField ;
    @FXML TextField pwmDTextField ;
    @FXML TextField pwmFreqTextField ;
    @FXML Label pwmVpLabel ;
    @FXML Label pwmDLabel ;
    @FXML Label pwmFreqLabel ;
    @FXML ToggleGroup pwmPlot ;
    @FXML RadioButton pwmWaveguidePlotRadioButton ;
    @FXML RadioButton pwmHeaterPlotRadioButton ;

    private void pwmTab_initialize(){
        pwmVpLabel.setText("");
        pwmDLabel.setText("");
        pwmFreqLabel.setText("");
        // initialize plot
        double[] x = {} ;
        double[] y = {} ;
        MatlabChart fig = new MatlabChart() ;
        fig.plot(x, y);
        fig.RenderPlot();
        fig.xlabel("");
        fig.ylabel("");
        figPWM = fig ;
        showPlot(fig, pwmMatlabPlotPane);
    }

    private boolean checkFilledTextsInPWMTab(){
        if(!pwmVpLabel.getText().isEmpty() && !pwmDLabel.getText().isEmpty() && !pwmFreqLabel.getText().isEmpty()){
            return true ;
        }
        else{
            return false ;
        }
    }

    private boolean pwmToggleIsSelected(){
        if(pwmWaveguidePlotRadioButton.isSelected() || pwmHeaterPlotRadioButton.isSelected()){
            return true ;
        }
        else{
            return false ;
        }
    }

    @FXML
    public void setVpPWM(){
        double PWM_Vp_V = Double.parseDouble(pwmVpTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("PWM_Vp_(V)", new double[]{PWM_Vp_V}));
        pwmVpLabel.setText("Vp is set to " + PWM_Vp_V + " Volt");
        if(pwmToggleIsSelected()){
            pwmPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setDPWM(){
        double PWM_D = Double.parseDouble(pwmDTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("PWM_D", new double[]{PWM_D}));
        pwmDLabel.setText("D is set to " + (100*PWM_D) + " %");
        if(pwmToggleIsSelected()){
            pwmPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    public void setFreqPWM(){
        double PWM_freq_kHz = Double.parseDouble(pwmFreqTextField.getText()) ;
        simDataBase.addNewVariable(new SimulationVariable("PWM_freq_(kHz)", new double[]{PWM_freq_kHz}));
        pwmFreqLabel.setText("freq is set to " + PWM_freq_kHz + " kHz");
        if(pwmToggleIsSelected()){
            pwmPlot.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    private void plotPWM(){
    	if(checkFilledTextsInPWMTab()){
            double D = simDataBase.getVariable("PWM_D").getValue(0) ;
            double freq = simDataBase.getVariable("PWM_freq_(kHz)").getValue(0) ;
            double Vp = simDataBase.getVariable("PWM_Vp_(V)").getValue(0) ;
            double tStart_usec = simDataBase.getVariable("impulse_tStart_(usec)").getValue(0) ;
            double tEnd_usec = simDataBase.getVariable("impulse_tEnd_(usec)").getValue(0) ;
            double tPeriod_usec = (1/(freq*1e3))*1e6 ;
            double[] time_usec = MoreMath.linspace(tStart_usec, tEnd_usec, 1000) ;
            int numPeriods = (int) (MoreMath.Arrays.FindMaximum.getValue(time_usec)/tPeriod_usec) ;
            int M = time_usec.length ;
            double[] heaterPWMVoltage_V = new double[M] ;
            voltage = new PulseTrainVoltage(D, tPeriod_usec, numPeriods, 0, Vp) ;
            for(int i=0; i<M; i++){
                heaterPWMVoltage_V[i] = voltage.getVoltage(time_usec[i]) ;
            }
            double f0 = simDataBase.getVariable("f0").getValue(0) ;
            double nu = simDataBase.getVariable("nu").getValue(0) ;
            double[] deltaT_H = selfHeating.getDeltaT(heaterPWMVoltage_V) ;
            simDataBase.addNewVariable(new SimulationVariable("heater_pwm_temperature_(K)", "Heater Temperature (K)", deltaT_H));
            simDataBase.addNewVariable(new SimulationVariable("heater_pwm_voltage_(V)", heaterPWMVoltage_V));
            simDataBase.addNewVariable(new SimulationVariable("pwm_time_(usec)", "Time (usec)", time_usec));

            if(pwmPlot.getSelectedToggle().equals(pwmWaveguidePlotRadioButton)){
                impulse = new ImpulseResponse1D_Modified_FFT(f0, nu) ;
                double[] t_usec = simDataBase.getVariableValues("impulse_time_(usec)") ;
                double[] impulsevalues = simDataBase.getVariableValues("Iwg") ;
                transResponse = new TransientResponse(selfHeating, impulse, voltage) ;
                double[] response = transResponse.getTimeResponse(time_usec, t_usec, impulsevalues) ;

                simDataBase.addNewVariable(new SimulationVariable("wg_pwm_temperature_(K)", "Waveguide Temperature (K)", response));
                MatlabChart fig = new MatlabChart() ;
                fig.plot(simDataBase.getVariableValues("pwm_time_(usec)"), simDataBase.getVariableValues("wg_pwm_temperature_(K)"));
                fig.RenderPlot();
                fig.xlabel("Time (usec)");
                fig.ylabel("Waveguide Temperature");
                figPWM = fig ;
                showPlot(fig, pwmMatlabPlotPane);
            }
            else if(pwmPlot.getSelectedToggle().equals(pwmHeaterPlotRadioButton)){
                MatlabChart fig = new MatlabChart() ;
                fig.plot(simDataBase.getVariableValues("pwm_time_(usec)"), simDataBase.getVariableValues("heater_pwm_temperature_(K)"));
                fig.RenderPlot();
                fig.xlabel("Time (usec)");
                fig.ylabel("Heater Temperature");
                figPWM = fig ;
                showPlot(fig, pwmMatlabPlotPane);
            }
    	}

    }

    //********* parameters related to general tab ****************************************************************

    @FXML
    public void initialize(){
        dcTab_initialize() ;
        freqTab_initialize() ;
        impulseTab_initialize() ;
        stepTab_initialize() ;
        sinTab_initialize() ;
        pwmTab_initialize() ;

    }

    private void showPlot(MatlabChart fig, Pane pane){
        int width = 500, height = 400 ;
        pane.getChildren().remove(fig.getChartSwingNode(width, height)) ;
        pane.getChildren().add(fig.getChartSwingNode(width, height)) ;
        pane.setPrefSize((double) width, (double) height);
    }

    // implementing event handlers for menu bar
    @FXML
    public void dataBasePressed() throws IOException{
    	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/Utilities/DataBaseTable/DataBase_v1_0/database_table.fxml")) ;
    	WindowBuilder builder = new WindowBuilder(loader) ;
    	builder.setIcon("/People/Meisam/GUI/Utilities/DataBaseTable/Extras/database.png");
    	builder.build("Simulation DataBase v1.0", true);
    	DatabaseTableController controller = (DatabaseTableController) loader.getController() ;
    	controller.setDataBase(simDataBase);
    	controller.updateParamTable();
    	controller.getCloseButton().setOnAction(e -> {
            try {
                builder.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    @FXML
    public void exportToMatlabPressed() throws IOException{
    	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/Utilities/ExportPlot/ExportToMATLAB/exportToMatlab.fxml")) ;
    	WindowBuilder builder = new WindowBuilder(loader) ;
    	builder.setIcon("/People/Meisam/GUI/Utilities/ExportPlot/ExportToMATLAB/Extras/MatlabIcons/Matlab_Logo.png");
    	builder.build("Configure Export To Matlab", false);
    	ExportToMatlabController controller = loader.getController() ;
    	controller.setColors(Color.BLUE, Color.RED);
    	if(dcTab.isSelected()){
            if(dcPlot.getSelectedToggle().equals(viPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("IH_(mA)"), simDataBase.getVariable("VH_(V)"));
            }
            else if(dcPlot.getSelectedToggle().equals(tempVRadioButton)){
            	controller.setVariables(simDataBase.getVariable("VH_(V)"), simDataBase.getVariable("deltaTH_(K)"));
            }
            else if(dcPlot.getSelectedToggle().equals(tempIRadioButton)){
            	controller.setVariables(simDataBase.getVariable("IH_(mA)"), simDataBase.getVariable("deltaTH_(K)"));
            }
            else if(dcPlot.getSelectedToggle().equals(ivPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("VH_(V)"), simDataBase.getVariable("IH_(mA)"));
            }
            controller.initialize();
    	}
    	else if(freqTab.isSelected()){
            if(freqPlot.getSelectedToggle().equals(ampdBPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("freq_(kHz)"), simDataBase.getVariable("amp_(dB)"));
            }
            else if(freqPlot.getSelectedToggle().equals(ampPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("freq_(kHz)"), simDataBase.getVariable("amp"));
            }
            else if(freqPlot.getSelectedToggle().equals(phaseRadRadioButton)){
            	controller.setVariables(simDataBase.getVariable("freq_(kHz)"), simDataBase.getVariable("phase_(Rad)"));
            }
            else if(freqPlot.getSelectedToggle().equals(phaseDegreeRadioButton)){
            	controller.setVariables(simDataBase.getVariable("freq_(kHz)"), simDataBase.getVariable("phase_(Degree)"));
            }
            controller.initialize();
    	}
    	else if(impulseTab.isSelected()){
            if(impulsePlot.getSelectedToggle().equals(impulsePlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("impulse_time_(usec)"), simDataBase.getVariable("Iwg"));
            }
            else if(impulsePlot.getSelectedToggle().equals(impulseNormalPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("impulse_time_(usec)"), simDataBase.getVariable("Iwg_normalized"));
            }
            controller.initialize();
    	}
    	else if(stepTab.isSelected()){
            if(stepPlot.getSelectedToggle().equals(stepWaveguidePlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("step_time_(usec)"), simDataBase.getVariable("wg_step_temperature_(K)"));
            }
            else if(stepPlot.getSelectedToggle().equals(stepHeaterPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("step_time_(usec)"), simDataBase.getVariable("heater_step_temperature_(K)"));
            }
            controller.initialize();
    	}
    	else if(sinTab.isSelected()){
            if(sinPlot.getSelectedToggle().equals(sinWaveguidePlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("sin_time_(usec)"), simDataBase.getVariable("wg_sin_temperature_(K)"));
            }
            else if(sinPlot.getSelectedToggle().equals(sinHeaterPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("sin_time_(usec)"), simDataBase.getVariable("heater_sin_temperature_(K)"));
            }
            controller.initialize();
    	}
    	else if(pwmTab.isSelected()){
            if(pwmPlot.getSelectedToggle().equals(pwmWaveguidePlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("pwm_time_(usec)"), simDataBase.getVariable("wg_pwm_temperature_(K)"));
            }
            else if(pwmPlot.getSelectedToggle().equals(pwmHeaterPlotRadioButton)){
            	controller.setVariables(simDataBase.getVariable("pwm_time_(usec)"), simDataBase.getVariable("heater_pwm_temperature_(K)"));
            }
            controller.initialize();
    	}

    }

    @FXML
    public void openInPlotterPressed() throws IOException {
        FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/Plotters/MainGUI/plotter.fxml")) ;
        WindowBuilder plotter = new WindowBuilder(loader) ;
        plotter.setIcon("/People/Meisam/GUI/Plotters/MainGUI/Extras/plotter.png");
        plotter.build("Plotter v0.5 Beta", true);
        PlotterController controller = (PlotterController) plotter.getController() ;
        controller.setDataBase(simDataBase);
        controller.initialize();
    }

    @FXML
    public void popupPlotPressed(){
    	if(dcTab.isSelected()){
    		figDC.run();
    	}
    	else if(freqTab.isSelected()){
    		figFreq.run();
    	}
    	else if(impulseTab.isSelected()){
    		figImpulse.run();
    	}
    	else if(stepTab.isSelected()){
    		figStep.run();
    	}
    	else if(sinTab.isSelected()){
    		figSin.run();
    	}
    	else if(pwmTab.isSelected()){
    		figPWM.run();
    	}

    }

    @FXML
    public void creditsPressed() throws IOException{
    	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/Credits/GeneralCredits/general_credits.fxml")) ;
    	WindowBuilder credits = new WindowBuilder(loader) ;
    	credits.setIcon("/People/Meisam/GUI/Credits/GeneralCredits/Extras/gift-box.png");
    	credits.build("Credits", false);

    }

    @FXML
    public void feedbackPressed() throws Exception {
    	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/Credits/FeedbackForm/feedback_form.fxml")) ;
    	WindowBuilder credits = new WindowBuilder(loader) ;
    	credits.setIcon("/People/Meisam/GUI/Credits/FeedbackForm/Extras/feedback.png");
    	credits.build("Feedback Form", false);

    }

    @FXML
    public void newMenuItemPressed() throws Exception{
    	FXMLLoader loader = new FXMLLoader(Object.class.getClass().getResource("/People/Meisam/GUI/ThermalHeater/MainGUI/heater.fxml")) ;
    	WindowBuilder newSim = new WindowBuilder(loader) ;
    	newSim.setIcon("/People/Meisam/GUI/ThermalHeater/Extras/PhoenixSim_logo.png");
    	newSim.build_NoModality("Thermo-Optic Analyzer v1.0", false);
    	HeaterController controller = (HeaterController) newSim.getController() ;
    	controller.closeApp.setOnAction(e ->{
    		try {
				newSim.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	});

    }




}
