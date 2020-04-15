package People.Meisam.GUI.Plotters.MatlabPlot.old_versions;

import java.awt.datatransfer.*;
import org.jfree.chart.JFreeChart;
import java.awt.event.ActionEvent;
import org.jfree.chart.ChartPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import javax.swing.JMenuItem;
import java.io.IOException;


public class CopyChartPanel extends ChartPanel{

   private static final long serialVersionUID = 1L;
   private ImageSelection imgsel;
   private int cwidth;
   private int cheight;
   public CopyChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips){
      super(chart,width,height,minimumDrawWidth,minimumDrawHeight,maximumDrawWidth,maximumDrawHeight,useBuffer,true,true,true,true,true);
      this.cwidth = width;
      this.cheight = height;
      // for copying to clipboard
      JMenuItem copyAsBitmap = new JMenuItem("Copy as bitmap");
      copyAsBitmap.setActionCommand("copyAsBitmap");
      copyAsBitmap.addActionListener(this);
      getPopupMenu().add(copyAsBitmap);
      imgsel = new ImageSelection();
   }
   public void actionPerformed(ActionEvent ae){
      if(ae.getActionCommand().equals("copyAsBitmap")){
         System.out.println("Copying chart to clipboard");
         setClipboard(getChart().createBufferedImage(
         cwidth,cheight, BufferedImage.TYPE_INT_ARGB,null));
         System.out.println("Done");
      }
      else super.actionPerformed(ae);
   }
   private void setClipboard(Image image){
      imgsel.setImage(image);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgsel, null);
    }
    private class ImageSelection implements Transferable{
       private Image image;
       public void setImage(Image image){
          this.image = image;
       }
       public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }



}
