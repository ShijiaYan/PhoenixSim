package archives.lisa;

import java.util.Map;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import ch.epfl.general_libraries.results.Execution;
import ch.epfl.general_libraries.utils.SimpleMap;
import ch.epfl.general_libraries.random.PRNStream;


public class PreferentialAttachmentPropertyAnalyzer implements Experiment {
	private double pwrFactor;
	private int vertices;
	private int seed;
	
	private transient int[] degrees;
	private transient double[] weights;
	private transient int totalLinks;
	private transient int[] children;
	
	public PreferentialAttachmentPropertyAnalyzer(@ParamName(name="Power Factor", default_="-5:0.5:5") double powerfactor,
			@ParamName(name="Number of Desired Vertices", default_="10,50,100,1000")int nSwitches,
			@ParamName(name="Seed", default_="1:20")int seed){
		this.pwrFactor = powerfactor;
		this.vertices=nSwitches;
		this.seed = seed;
	}
	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) {
		DataPoint dp = new DataPoint();
		dp.addProperty("Power factor", pwrFactor);
		dp.addProperty("Number of Vertices", vertices);
		dp.addProperty("type", getType());	
		dp.addProperties(getAllParameters());
		
		generatePseudoRandomTopology();
		
		/*for(int i=0; i<degrees.length; i++){
			System.out.println("children["+i+"] ="+children[i]);
		}*/
		double p0,p1=0,dsim=0,LstoTheor,dstoTheor,percentErrL,percentErrD;
		int numLeaves=0;
        for (int child : children) {
            if (child == 0)
                numLeaves++;
            if (child == 1)
                p1++;
            if (child >= 2)
                dsim++; //delegating nodes
        }

		p0=(double)numLeaves/(double)vertices;
		p1=p1/vertices;
		LstoTheor = getAverageNumberOfLeaves();
		dstoTheor = getAverageHubNumber();
		percentErrL = Math.abs(LstoTheor-numLeaves)/LstoTheor*100;
		percentErrD = Math.abs(dstoTheor- dsim)/ dstoTheor *100;
		
		dp.addResultProperty("Number Leaves", numLeaves);
		dp.addResultProperty("Theoretical number leaves", LstoTheor);
		dp.addResultProperty("Probability degree 0", p0);
		dp.addResultProperty("Probability degree 1", p1);
		dp.addResultProperty("Delegating nodes, simulated", dsim);
		dp.addResultProperty("Delegating nodes, theoretical", dstoTheor);

		dp.addResultProperty("Percent error leaves theoretical vs simulated",percentErrL);
		dp.addResultProperty("Percent error d theoretical vs simulated",percentErrD);

		Execution ex = new Execution();
		
		ex.addDataPoint(dp);
		man.addExecution(ex);
	}
	
	public double getAverageNumberOfLeaves() {
		//throw new IllegalStateException("Yet to implement using a table"); 
		double averageNumLeaves=0, p0;
/*		if(pwrFactor<=1)
			p0 = 0.5069*Math.exp(0.2525*pwrFactor); //calculated this via simulation, R^2 value of 0.9995
		else
			p0 = pwrFactor*(-0.0021*pwrFactor*pwrFactor*pwrFactor +0.0436*pwrFactor*pwrFactor-0.3167*pwrFactor+0.9551);//gives R^2 value of 0.9963 w/simulation
*/
		if(pwrFactor<=0){
			if(vertices<6.5)
				p0=Math.pow(pwrFactor,3)*0.0003+Math.pow(pwrFactor,2)*0.0062+0.0479*pwrFactor+0.5097;
			else if(vertices<11.5)
				p0=Math.pow(pwrFactor,3)*0.0006+Math.pow(pwrFactor,2)*0.0113+0.0776*pwrFactor+0.4627;
			else if(vertices<20)
				p0=Math.pow(pwrFactor,3)*0.0005+Math.pow(pwrFactor,2)*0.0122+0.1071*pwrFactor+0.4911;
			else if(vertices<37)
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0108+0.1065*pwrFactor+0.4775;
			else if(vertices<75)
				p0=Math.pow(pwrFactor,3)*0.0002+Math.pow(pwrFactor,2)*0.0066+0.0934*pwrFactor+0.4786;
			else if(vertices <300)
				p0=Math.pow(pwrFactor,3)*0.0002+Math.pow(pwrFactor,2)*0.0084+0.1034*pwrFactor+0.4842;
			else if(vertices < 750)
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0109+0.1149*pwrFactor+0.4989;
			else
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0111+0.1156*pwrFactor+0.4951;
		}else{
			if(vertices<6.5)
				p0=-Math.pow(pwrFactor,3)*0.0006+Math.pow(pwrFactor,2)*0.0038+0.0290*pwrFactor+0.5205;
			else if(vertices<11.5)
				p0=Math.pow(pwrFactor,3)*0.001-Math.pow(pwrFactor,2)*0.0215+0.1550*pwrFactor+0.4455;
			else if(vertices<20)
				p0=Math.pow(pwrFactor,3)*0.0021-Math.pow(pwrFactor,2)*0.0387+0.2363*pwrFactor+0.4263;
			else if(vertices<37)
				p0=Math.pow(pwrFactor,3)*0.0032-Math.pow(pwrFactor,2)*0.0539+0.2955*pwrFactor+0.4132;
			else if(vertices<75)
				p0=Math.pow(pwrFactor,3)*0.0044-Math.pow(pwrFactor,2)*0.0689+0.3493*pwrFactor+0.4063;
			else if(vertices <300)
				p0=Math.pow(pwrFactor,3)*0.0052-Math.pow(pwrFactor,2)*0.0796+0.3874*pwrFactor+0.3948;
			else if(vertices < 750)
				p0=-Math.pow(pwrFactor,4)*0.0008+Math.pow(pwrFactor,3)*0.0193-Math.pow(pwrFactor,2)*0.158+0.5433*pwrFactor+0.3433;
			else
				p0=-Math.pow(pwrFactor,4)*0.001+Math.pow(pwrFactor,3)*0.0218-Math.pow(pwrFactor,2)*0.1727+0.5763*pwrFactor+0.3263;
		}
		averageNumLeaves=p0*vertices;
		return averageNumLeaves; 
	}

	public double getAverageHubNumber() { //'d' in paper
		double averageHub=0,p0,p1;
		/*if(pwrFactor<=1){
			double p0 = 0.5069*Math.exp(0.2525*pwrFactor); //calculated this via simulation, R^2 value of 0.9995
			double p1 = -0.0956*pwrFactor+ 0.2538; //also calculated via simulation; R^2 value of 0.9988	
		}else{ //NEED TO FILL THIS IN
			throw new IllegalStateException("Yet to implement using a table"); 
		}*/
		if(pwrFactor<=0){
			if(vertices<6.5)
				p0=Math.pow(pwrFactor,3)*0.0003+Math.pow(pwrFactor,2)*0.0062+0.0479*pwrFactor+0.5097;
			else if(vertices<11.5)
				p0=Math.pow(pwrFactor,3)*0.0006+Math.pow(pwrFactor,2)*0.0113+0.0776*pwrFactor+0.4627;
			else if(vertices<20)
				p0=Math.pow(pwrFactor,3)*0.0005+Math.pow(pwrFactor,2)*0.0122+0.1071*pwrFactor+0.4911;
			else if(vertices<37)
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0108+0.1065*pwrFactor+0.4775;
			else if(vertices<75)
				p0=Math.pow(pwrFactor,3)*0.0002+Math.pow(pwrFactor,2)*0.0066+0.0934*pwrFactor+0.4786;
			else if(vertices <300)
				p0=Math.pow(pwrFactor,3)*0.0002+Math.pow(pwrFactor,2)*0.0084+0.1034*pwrFactor+0.4842;
			else if(vertices < 750)
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0109+0.1149*pwrFactor+0.4989;
			else
				p0=Math.pow(pwrFactor,3)*0.0004+Math.pow(pwrFactor,2)*0.0111+0.1156*pwrFactor+0.4951;
		}else{
			if(vertices<6.5)
				p0=-Math.pow(pwrFactor,3)*0.0006+Math.pow(pwrFactor,2)*0.0038+0.0290*pwrFactor+0.5205;
			else if(vertices<11.5)
				p0=Math.pow(pwrFactor,3)*0.001-Math.pow(pwrFactor,2)*0.0215+0.1550*pwrFactor+0.4455;
			else if(vertices<20)
				p0=Math.pow(pwrFactor,3)*0.0021-Math.pow(pwrFactor,2)*0.0387+0.2363*pwrFactor+0.4263;
			else if(vertices<37)
				p0=Math.pow(pwrFactor,3)*0.0032-Math.pow(pwrFactor,2)*0.0539+0.2955*pwrFactor+0.4132;
			else if(vertices<75)
				p0=Math.pow(pwrFactor,3)*0.0044-Math.pow(pwrFactor,2)*0.0689+0.3493*pwrFactor+0.4063;
			else if(vertices <300)
				p0=Math.pow(pwrFactor,3)*0.0052-Math.pow(pwrFactor,2)*0.0796+0.3874*pwrFactor+0.3948;
			else if(vertices < 750)
				p0=-Math.pow(pwrFactor,4)*0.0008+Math.pow(pwrFactor,3)*0.0193-Math.pow(pwrFactor,2)*0.158+0.5433*pwrFactor+0.3433;
			else
				p0=-Math.pow(pwrFactor,4)*0.001+Math.pow(pwrFactor,3)*0.0218-Math.pow(pwrFactor,2)*0.1727+0.5763*pwrFactor+0.3263;
		}
		if(pwrFactor<=1){
			if(vertices<6.5)
				p1=Math.pow(pwrFactor,3)*0.0005+Math.pow(pwrFactor,2)*0.0022-0.0277*pwrFactor+0.2954;
			else if(vertices<11.5)
				p1=Math.pow(pwrFactor,3)*0.0007+Math.pow(pwrFactor,2)*0.0032-0.0465*pwrFactor+0.3541;
			else if(vertices<20)
				p1=Math.pow(pwrFactor,3)*0.0013+Math.pow(pwrFactor,2)*0.0092-0.0686*pwrFactor+0.2991;
			else if(vertices<37)
				p1=Math.pow(pwrFactor,3)*0.0012+Math.pow(pwrFactor,2)*0.0085-0.076*pwrFactor+0.2928;
			else if(vertices<75)
				p1=Math.pow(pwrFactor,3)*0.001+Math.pow(pwrFactor,2)*0.0081-0.0737*pwrFactor+0.2686;
			else if(vertices <300)
				p1=Math.pow(pwrFactor,3)*0.0011+Math.pow(pwrFactor,2)*0.0084-0.0764*pwrFactor+0.2715;
			else if(vertices < 750)
				p1=Math.pow(pwrFactor,3)*0.001+Math.pow(pwrFactor,2)*0.0075-0.0808*pwrFactor+0.2569;
			else
				p1=Math.pow(pwrFactor,3)*0.001+Math.pow(pwrFactor,2)*0.0078-0.0805*pwrFactor+0.2618;
		}else{
			if(vertices<6.5)
				p1=Math.pow(pwrFactor,2)*0.0046-0.0616*pwrFactor+0.3063;
			else if(vertices<11.5)
				p1=-Math.pow(pwrFactor,3)*0.0002+Math.pow(pwrFactor,2)*0.0069-0.0628*pwrFactor+0.2291;
			else if(vertices<20)
				p1=-Math.pow(pwrFactor,3)*0.0019+Math.pow(pwrFactor,2)*0.0324-0.1775*pwrFactor+0.3503;
			else if(vertices<37)
				p1=0.0004*Math.pow(pwrFactor,4)-Math.pow(pwrFactor,3)*0.0095+Math.pow(pwrFactor,2)*0.0787-0.2854*pwrFactor+0.405;
			else if(vertices<75)
				p1=0.0007*Math.pow(pwrFactor,4)-Math.pow(pwrFactor,3)*0.015+Math.pow(pwrFactor,2)*0.1136-0.3715*pwrFactor+0.454;
			else if(vertices <300)
				p1=0.001*Math.pow(pwrFactor,4)-Math.pow(pwrFactor,3)*0.02+Math.pow(pwrFactor,2)*0.1456-0.4521*pwrFactor+0.5086;
			else if(vertices < 750)
				p1=0.0002*Math.pow(pwrFactor,6)-0.0054*Math.pow(pwrFactor,5)+Math.pow(pwrFactor,4)*0.0629-Math.pow(pwrFactor,3)*0.3798+Math.pow(pwrFactor,2)*1.2417-2.0795*pwrFactor+1.3931;
			else
				p1=0.0002*Math.pow(pwrFactor,6)-0.0069*Math.pow(pwrFactor,5)+Math.pow(pwrFactor,4)*0.0793-Math.pow(pwrFactor,3)*0.4705+Math.pow(pwrFactor,2)*1.5063-2.457*pwrFactor+1.5915;
			}
		averageHub=1-p1 +(vertices-1)*(1-p0-p1);
		return averageHub;
		
	}	
	
	private void addPseudoLink(int a, int b) {
		degrees[a] += 1;
		weights[a] = Math.pow(degrees[a]*2, pwrFactor);
		degrees[b] += 1;
		weights[b] = Math.pow(degrees[b]*2, pwrFactor);
		children[b]+=1;
		totalLinks = totalLinks+2;
	}

	public void generatePseudoRandomTopology() {
		PRNStream generator = PRNStream.getDefaultStream(seed);
		
		degrees = new int[vertices];
		weights = new double[vertices];
		children = new int[vertices];

		totalLinks = 0;
		for(int k = 0 ; k < vertices ; k++){
			degrees[k] = 0;
			weights[k] = 0;
			children[k]=0;
		}
		
		addPseudoLink(0,1);
		
		for (int i = 2 ; i < vertices ; i++) {
			int alt = generator.getRandomIndex(weights);
			addPseudoLink(i,alt);
		}
	}
	
	public String getType() {
		return "simulation";
	}	
	
	public Map<String, String> getAllParameters() {
		return SimpleMap.getMap("Power Factor", pwrFactor+"");
	}


}
