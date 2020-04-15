package PhotonicElements.Modulators.InjectionMode.DC;

import ch.epfl.general_libraries.clazzes.ParamName;

public class IVmodel {

	double R_kOhm, Vbi_V, Vthermal_mV, Is_nA ;
	
	public IVmodel(
			@ParamName(name="R (kOhm)", default_="0.2") double R_kohm,
			@ParamName(name="Vbi (V)", default_="0.7") double Vbi_V,
			@ParamName(name="Vthermal (mV)", default_="25") double Vthermal_mV,
			@ParamName(name="Is (nA)") double Is_nA
			){
		this.R_kOhm = R_kohm ;
		this.Vbi_V = Vbi_V ;
		this.Vthermal_mV = Vthermal_mV ;
		this.Is_nA = Is_nA ;
	}
	
	
			
			
			
			
			
	
}
