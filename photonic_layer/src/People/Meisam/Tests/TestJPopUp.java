package People.Meisam.Tests;

import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class TestJPopUp {
	
	public static void main(String[] args){
//		JOptionPane.showMessageDialog(null, "No input in input kappa");
//		JOptionPane.showConfirmDialog(null, "Exit without saving?") ;
//		Db.noYes("Exit without saving?") ;
//		String st = Db.readLine("Insert the expression:") ;
//		System.out.println(MoreMath.evaluate(st));

		JOptionPane.showMessageDialog(
                ((Supplier<JDialog>) () -> {final JDialog dialog = new JDialog(); 
                							dialog.setAlwaysOnTop(true);
                							return dialog;}).get()
                , " FSR (nm) = 10 \n Radius (um) = 5 \n kappa_in = 0.2 \n kappa_out = 0.3");
		// it does not exit the jvm
		System.exit(1);
	}

}
