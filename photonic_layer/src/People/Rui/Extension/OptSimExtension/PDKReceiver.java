package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;


public class PDKReceiver {
    int num;
    public String writeFileString;
    public String linkString;


    private static final String PDK_RING_NAME = "ScanRingPDK";
    private static final String PDK_WAVEGUIDE_NAME = "testWaveguidePDK";
    static private Double[] laserWavelengths = {1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9,
            1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9, 1520.5E-9, 1524.4E-9, 1510.0E-9,
            1534.1E-9, 1520.5E-9, 1524.4E-9, 1510.0E-9, 1534.1E-9};
    static private String[] pdkRingRadius = {"4.2", "4.22", "4.25", "4.27",
            "4.2", "4.22", "4.25", "4.27", "4.2", "4.22", "4.25", "4.27",
            "4.2", "4.22", "4.25", "4.27"};
    public GlobalParametersDefault globalParameters;
    public OptMuxComponent optMuxComponent;
    public ArrayList<CWLaserComponent> cwlasers = new ArrayList<>();
    public ArrayList<SpectrumAnalyzerComponent> spectrumAnalyzerComponents = new ArrayList<>();
    public ArrayList<CustomPDKExample> pdkRing = new ArrayList<>();
    public ArrayList<CustomPDKExample> pdkWaveguide = new ArrayList<>();


    //  Constructor, input numbers of rings, center wavelength, fsr,
    //  we give it a default setting, the center wavelength is 1.55
    public PDKReceiver(int num) {
        this.num = num;
        this.optMuxComponent = new OptMuxComponent("Mux", num, 320, 320);
        this.globalParameters = new GlobalParametersDefault("pdk Receiver", "9");
        for (int i = 0; i < num; i++) {
            cwlasers.add(new CWLaserComponent("CWLaser" + i,
                    Double.toString(laserWavelengths[i % 16]), 250.0, 240 + 80 * i));
            spectrumAnalyzerComponents.add(new SpectrumAnalyzerComponent("Spec" + i,
                    440 + 160 * i, 560));
            // set up pdk components
            pdkRing.add(new CustomPDKExample("pdkRing" + i, PDK_RING_NAME, "r", pdkRingRadius[i % 16],
                    2, 2, 440.0 + i * 160, 320.0));
        }
        for (int i = 0; i < num - 1; i++)
            pdkWaveguide.add(new CustomPDKExample("pdkWg" + i, PDK_WAVEGUIDE_NAME, "L", "20",
                    1, 1, 520.0 + i * 160, 320.0));
        setAllParameters();
        setLinkString();
        setWriteFileString();
    }

    public ArrayList<CWLaserComponent> getCWlasers() {
        return cwlasers;
    }

    // When set up a new system, remember to set up it here
    public void setAllParameters() {
        globalParameters.setWriteFilePropertyString();
        optMuxComponent.setWriteFilePropertyString();
        for (CWLaserComponent cwlaser : cwlasers)
            cwlaser.setWriteFilePropertyString();
        for (CustomPDKExample ring : pdkRing)
            ring.setWriteFilePropertyString();
        for (CustomPDKExample wg : pdkWaveguide)
            wg.setWriteFilePropertyString();
        for (SpectrumAnalyzerComponent spectrumAnalyzerComponent : spectrumAnalyzerComponents)
            spectrumAnalyzerComponent.setWriteFilePropertyString();
    }

    public void setWriteFileString() {
        setAllParameters();
        StringBuilder builder = new StringBuilder();
        builder.append(globalParameters.propertyString);
        builder.append(optMuxComponent.propertyString);
        // you need to update all the components before write
        for (CWLaserComponent cwlaser : cwlasers)
            builder.append(cwlaser.propertyString);
        for (CustomPDKExample ring : pdkRing)
            builder.append(ring.propertyString);
        for (CustomPDKExample wg : pdkWaveguide)
            builder.append(wg.propertyString);
        for (SpectrumAnalyzerComponent spectrumAnalyzerComponent : spectrumAnalyzerComponents)
            builder.append(spectrumAnalyzerComponent.propertyString);
        builder.append(linkString);
        writeFileString = builder.append("</entity>\n").toString();

    }

    public void setLinkString() {
        StringBuilder builder = new StringBuilder();
        String stringToAppend;
        for (int i = 0; i < 6 * num - 2; i++) {
            stringToAppend = "    <relation name=\"link" + i + "\" class=\"ptolemy.actor.TypedIORelation\">\r\n" +
                    "        <property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">\r\n" +
                    "        </property>\r\n";
            builder.append(stringToAppend);
            builder.append("    </relation>\n");
        }
        // it looks like for pdk one would have to link the system

        // link cw laser with mux -> num
        for (int i = 0; i < num; i++) {
            stringToAppend = "    <link port=\"CWLaser" + i + ".output#1\" relation=\"link" + i + "\"/>\r\n" +
                    "    <link port=\"Mux.input#" + (i + 1) + "\" relation=\"link" + i + "\"/>\n";
            builder.append(stringToAppend);
        }

        // link Mux with first ring ->num+1
        builder.append("    <link port=\"Mux.output\" relation=\"link").append(num).append("\"/>\r\n").append("    " +
                "<link port=\"pdkRing0.input#1\" relation=\"link").append(num).append("\"/>\n");

        // link ring -> waveguide, link num+2*i+1 and num+2*i+2
        for (int i = 0; i < num - 1; i++) {
            stringToAppend = "    <link port=\"pdkWg" + i + ".input#1\" relation=\"link" + (num + 2 * i + 1) +
                    "\"/>\r\n" +
                    "    <link port=\"pdkRing" + i + ".output#4\" relation=\"link" + (num + 2 * i + 1) +
                    "\"/>\n" +
                    "    <link port=\"pdkWg" + i + ".output#1\" relation=\"link" + (num + 2 * i + 2) +
                    "\"/>\r\n" +
                    "    <link port=\"pdkRing" + i + ".input#4\" relation=\"link" + (num + 2 * i + 2) +
                    "\"/>\n";
            builder.append(stringToAppend);
        }

        // link waveguide -> ring, link 3*num+2*i-1 and 3*num+2*i
        for (int i = 0; i < num - 1; i++) {
            stringToAppend =
                    "    <link port=\"pdkWg" + i + ".output#2\" relation=\"link" + (3 * num + 2 * i - 1) + "\"/>\r\n" +
                            "    <link port=\"pdkRing" + (i + 1) + ".input#1\" relation=\"link" + (3 * num + 2 * i - 1) +
                            "\"/>\n" +
                            "    <link port=\"pdkWg" + i + ".input#2\" relation=\"link" + (3 * num + 2 * i) + "\"/>\r" +
                            "\n" +
                            "    <link port=\"pdkRing" + (i + 1) + ".output#1\" relation=\"link" + (3 * num + 2 * i) +
                            "\"/>\n";
            builder.append(stringToAppend);
        }
        // link Spec with RingRes -> 5*num-2+i
        for (int i = 0; i < num; i++) {
            stringToAppend = "    <link port=\"Spec" + i + ".input#1\" relation=\"link" + (5 * num - 2 + i) +
                    "\"/>\r\n" +
                    "    <link port=\"pdkRing" + i + ".output#2\" relation=\"link" + (5 * num - 2 + i) +
                    "\"/>\n";
            builder.append(stringToAppend);
        }
        linkString = builder.toString();
    }


    public ArrayList<String> getComponentsName() {
        ArrayList<String> componentsName = new ArrayList<>();
        componentsName.add("global param");    // 0 single
        componentsName.add("Mux");                //1 single
        componentsName.add("CWLasers");            //2
        componentsName.add("pdk Ring");            //3
        componentsName.add("Spec Analyzer");    //4
        componentsName.add("pdk Waveguide");    //5
        return componentsName;
    }

    public void setEdittedPorperty(int component, int index, int propertyIndex, String newValue) {
        switch (component) {
            case 0:
                this.globalParameters.rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 1:
                this.optMuxComponent.rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 2:
                this.cwlasers.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 4:
                this.spectrumAnalyzerComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 3:
                this.pdkRing.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 5:
                this.pdkWaveguide.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            default:
                break;

        }
    }

}
