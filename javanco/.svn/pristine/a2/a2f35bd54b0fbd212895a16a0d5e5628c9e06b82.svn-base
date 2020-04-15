package ch.epfl.javancox.execdeamon.server;

public class ExperimentRunner extends AbstractObjectAttender {
	
	Object db;

	@Override
	public Object attendObject(Object o) {
		try {
			if (db == null) {
				db = getObjectFromClass("ch.epfl.javancox.results_manager.RawExecutionCollector");
			}
			if (Flags.logging)
				System.out.println(o);
			
			Object experiment = runMethodOnObject(o, "build", new Object[]{special});
			
			// this line is equivalent to have experiment.run(db, null)
			runMethodOnObject(experiment, "run", new Object[]{db, null});
			
		/*	runStaticMethodOnClass(
					"ch.epfl.javancox.results_manager.gui.DefaultResultDisplayingGUI", 
					"displayDefault_", 
					new Object[]{db});*/
			return db;
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			return "Hello exception";
		}
		return null;

	}

}
