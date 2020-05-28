package edu.columbia.sebastien;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.epfl.general_libraries.graphics.pcolor.GridPainter;
import ch.epfl.general_libraries.graphics.pcolor.PcolorGUI;
import ch.epfl.general_libraries.utils.Matrix;

class ClusterGridPainter extends GridPainter {
	
	int clusterSize;
	
	public ClusterGridPainter(int clusterSize) {
		this.clusterSize = clusterSize;
	}
	
	@Override
	public void paintToGraphics(Graphics g, int pixelWidth, int pixelHeight, int size) {
		
		
		g.setColor(Color.DARK_GRAY);
		for (int i = 1 ; i < size ; i++) {
			g.fillRect(i*pixelWidth, 0, 3, size*pixelHeight);
			g.fillRect(0, i*pixelHeight, size*pixelWidth, 3);
		}
		
		g.setColor(Color.WHITE);
		for (int i = 1 ; i < size ; i++) {
			g.fillRect(i*pixelWidth*clusterSize, (i-1)*clusterSize*pixelHeight, 3, pixelHeight*clusterSize*2);
			g.fillRect((i-1)*clusterSize*pixelWidth, i*pixelHeight*clusterSize, clusterSize*2*pixelWidth, 3);
		}
		
		
	}	
}

public class VisualizeAndPlayWithTrafficMatrix {
	
	private int size;
	private int clusterSize;
	
	public static void main(String[] args) {
		VisualizeAndPlayWithTrafficMatrix m = new VisualizeAndPlayWithTrafficMatrix(16,  4);
		final double[][] mat = m.getMatrix();
		final PcolorGUI gui = new PcolorGUI("", mat, new ClusterGridPainter(4));	
		gui.showInFrame();
		
		JDialog dia = new JDialog();
		dia.setSize(100, 100);
		
		dia.setLayout(new FlowLayout());
		
		JButton shiftRight = new JButton("Shift right");
		dia.add(shiftRight);
		shiftRight.addActionListener(e -> {
            Matrix.shiftRight(mat, 1);
            gui.repaint();
        });
		
		JButton shiftDown = new JButton("Shift down");
		dia.add(shiftDown);
		shiftDown.addActionListener(e -> {
            Matrix.shiftDown(mat, 1);
            gui.repaint();
        });
		
		JPanel perm = new JPanel();
		perm.setLayout(new FlowLayout());
		dia.add(perm);
		
		final JTextField field1 = new JTextField(4);
		final JTextField field2 = new JTextField(4);
		//field1.setSize(30, 1);
		//field2.setSize(30, 1);
		perm.add(field1);
		perm.add(field2);
		JButton permute = new JButton("Permute");
		perm.add(permute);
		permute.addActionListener(e -> {
            Matrix.permute(mat, Integer.parseInt(field1.getText()), Integer.parseInt(field2.getText()));
            gui.repaint();
        });
		
		
		
		dia.setVisible(true);
		
		/*mat = Matrix.shiftRight(mat, 4);
		
		mat = Matrix.permute(mat, 2, 6);
		mat = Matrix.permute(mat, 3, 7);	
		mat = Matrix.permute(mat, 6, 10);
		mat = Matrix.permute(mat, 7, 11);
		mat = Matrix.permute(mat, 10, 14);
		mat = Matrix.permute(mat, 11, 15);	*/	

		
		
	}

	public VisualizeAndPlayWithTrafficMatrix(int size, int clusterSize) {
		this.size = size;
		this.clusterSize = clusterSize;
	}
	
	public double[][] getMatrix() {
		double[][] mat = new double[size][size];
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
			//	int d = Math.max(0,  i - j);
				if (i / clusterSize == j / clusterSize) {
					mat[i][j] = 1;
				}
			}
		}
		return mat;
	}

}
