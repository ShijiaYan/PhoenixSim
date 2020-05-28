package ch.epfl.javanco.pluggings;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTextField;

import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.path.Path;
import ch.epfl.general_libraries.utils.NodePair;
import ch.epfl.general_libraries.utils.TypeParser;
import ch.epfl.javanco.algorithms.BFS;
import ch.epfl.javanco.base.AbstractGraphHandler;
import ch.epfl.javanco.network.LinkContainer;

public class DistanceMatrixFromRange extends JavancoTool {

	@Override
	public void internalFrameClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(final AbstractGraphHandler agh, Frame f) {
		JDialog dia = new JDialog();
		dia.setSize(700,400);
		dia.setLayout(new FlowLayout());
		final JTextField tf1 = new JTextField(40);
		final JTextField tf2 = new JTextField(40);
		dia.add(tf1);
		dia.add(tf2);
		final JButton b = new JButton("go");
		b.addActionListener(e -> {
            int from = Integer.parseInt(tf1.getText());
            int to = Integer.parseInt(tf2.getText())+1;
            int[][] dist = new int[to-from][to-from];
            for (int i = from ; i < to ; i++) {
                int[] dista = BFS.getDistancesFromUndirected(agh, i);
                for (int j = 0 ; j < to-from ; j++) {
                    dist[i-from][j] = dista[from + j];
                }
            }
            PcolorGUI.show(dist);
        });
		dia.add(b);
		dia.setVisible(true);
	}

}
