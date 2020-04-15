package People.Meisam.Tests.experimental.pdfviewer;

import People.Meisam.GUI.Builders.PDFLoader;

public class TestPDFViewer {

	public static void main(String[] args){

//		Runnable runnable = new Runnable(){
//			@Override
//			public void run() {
//				String path = "/People/Meisam/GUI/PhoenixSim/documentations/ExtractionOfCouplingCoefficients.pdf" ;
//				File pdfFile;
//				try {
//					pdfFile = new File(Object.class.getClass().getResource(path).toURI());
//					System.out.println(pdfFile.exists());
//					PDFViewer pdf = new PDFViewer(true) ;
//					try {
//						pdf.openFile(pdfFile);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} catch (URISyntaxException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//
//			}
//
//		} ;
//
//		EventQueue.invokeLater(runnable);
		PDFLoader.loadPDF_useInternalPDFLoader("/People/Meisam/GUI/PhoenixSim/documentations/ExtractionOfCouplingCoefficients.pdf");

	}

}
