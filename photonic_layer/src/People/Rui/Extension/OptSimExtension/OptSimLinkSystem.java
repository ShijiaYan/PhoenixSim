package People.Rui.Extension.OptSimExtension;

import java.util.ArrayList;


public class OptSimLinkSystem {
    // now let's assume we only have one CW Laser in our system, and what we want to do is
    //// TO-DO
    //  * 1 show the property to the table
    //  * 2 Generate a file which only content one component
    //  * 3 You also need a tree to put in global settings

    int num;
    double smallestWavelength;
    double fsr;
    double fiberLength;
    public String writeFileString;
    public String linkString;


    public GlobalParameters globalParameters;
    public OptMuxComponent optMuxComponent;
    public NonlinearFiberComponent nonlinearFiberComponent;
    public ArrayList<CWLaserComponent> cwlasers = new ArrayList<>();
    public ArrayList<SignalGeneratorComponent> signalGeneratorComponents = new ArrayList<>();
    public ArrayList<DriverComponent> driverComponents = new ArrayList<>();
    public ArrayList<RingModulatorComponent> ringModulatorComponents = new ArrayList<>();
    public ArrayList<RingResonatorComponent> ringResonatorComponents = new ArrayList<>();
    public ArrayList<SpectrumAnalyzerComponent> spectrumAnalyzerComponents = new ArrayList<>();


    //  Constructor, input numbers of rings, center wavelength, fsr, 
    //  we give it a default setting, the center wavelength is 1.55
    public OptSimLinkSystem(int num) {
        this.num = num;
        setFiberLength(100);
        setFsr(3.2E-9);
        setWavelength(1.55E-6);
        this.optMuxComponent = new OptMuxComponent("Mux", num);
        this.nonlinearFiberComponent = new NonlinearFiberComponent("fiber");
        this.globalParameters = new GlobalParameters("Link");
        for (int i = 0; i < num; i++) {
            cwlasers.add(new CWLaserComponent("CWLaser" + i,
                    Double.toString(smallestWavelength + fsr * i), -2800.0, -240 - 80 * i));
            signalGeneratorComponents.add(new SignalGeneratorComponent("PRBS" + i,
                    -2640.0 + 80 * i, -460));
            driverComponents.add(new DriverComponent("Driver" + i,
                    -2640.0 + 80 * i, -380));
            ringModulatorComponents.add(new RingModulatorComponent("RingMod" + i,
                    smallestWavelength + fsr * i, -2640 + 80 * i, -300));
            ringResonatorComponents.add(new RingResonatorComponent("RingRes" + i,
                    smallestWavelength + fsr * i, -2640 + 80 * i, -100));
            spectrumAnalyzerComponents.add(new SpectrumAnalyzerComponent("Spec" + i,
                    -2640 + 80 * i, 0));
        }
        setAllParameters();
        setLinkString();
        setWriteFileString();

    }

    public ArrayList<CWLaserComponent> getCWlasers() {
        return cwlasers;
    }


    public void setWavelength(double wavelength) {
        this.smallestWavelength = wavelength;
    }

    public void setFsr(double fsr) {
        this.fsr = fsr;
    }

    public void setFiberLength(double length) {
        this.fiberLength = length;
    }


    public void setAllParameters() {
        globalParameters.setWriteFilePropertyString();
        optMuxComponent.setWriteFilePropertyString();
        nonlinearFiberComponent.setWriteFilePropertyString();
        for (CWLaserComponent cwlaser : cwlasers)
            cwlaser.setWriteFilePropertyString();
        for (SignalGeneratorComponent signalGene : signalGeneratorComponents)
            signalGene.setWriteFilePropertyString();
        for (DriverComponent driver : driverComponents)
            driver.setWriteFilePropertyString();
        for (RingModulatorComponent ringModulatorComponent : ringModulatorComponents)
            ringModulatorComponent.setWriteFilePropertyString();
        for (RingResonatorComponent ringResonatorComponent : ringResonatorComponents)
            ringResonatorComponent.setWriteFilePropertyString();
        for (SpectrumAnalyzerComponent spectrumAnalyzerComponent : spectrumAnalyzerComponents)
            spectrumAnalyzerComponent.setWriteFilePropertyString();

    }

    public void setWriteFileString() {
        setAllParameters();
        writeFileString = "";
        writeFileString = writeFileString + globalParameters.propertyString;

        writeFileString = writeFileString + optMuxComponent.propertyString;
        writeFileString = writeFileString + nonlinearFiberComponent.propertyString;
        // you need to update all the components before write
        for (CWLaserComponent cwlaser : cwlasers)
            writeFileString = writeFileString + cwlaser.propertyString;
        for (SignalGeneratorComponent signalGene : signalGeneratorComponents)
            writeFileString = writeFileString + signalGene.propertyString;
        for (DriverComponent driver : driverComponents)
            writeFileString = writeFileString + driver.propertyString;
        for (RingModulatorComponent ringModulatorComponent : ringModulatorComponents)
            writeFileString = writeFileString + ringModulatorComponent.propertyString;
        for (RingResonatorComponent ringResonatorComponent : ringResonatorComponents)
            writeFileString = writeFileString + ringResonatorComponent.propertyString;
        for (SpectrumAnalyzerComponent spectrumAnalyzerComponent : spectrumAnalyzerComponents)
            writeFileString = writeFileString + spectrumAnalyzerComponent.propertyString;
        writeFileString = writeFileString + linkString;
        writeFileString = writeFileString + "</entity>\n";

    }

    public void setLinkString() {
        linkString = "";
        for (int i = 0; i < 6 * num + 1; i++) {
            linkString = linkString +
                    "    <relation name=\"link" + i + "\" class=\"ptolemy.actor.TypedIORelation\">\r\n" +
                    "        <property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">\r\n" +
                    "        </property>\r\n";
            linkString = linkString + "    </relation>\n";
        }

        // link cw laser with mux
        for (int i = 0; i < num; i++) {
            linkString = linkString +
                    "    <link port=\"CWLaser" + i + ".output#1\" relation=\"link" + i + "\"/>\r\n" +
                    "    <link port=\"Mux.input#" + (i + 1) + "\" relation=\"link" + i + "\"/>\n";

        }

        // link PRBS with driver
        for (int i = 0; i < num; i++) {
            linkString = linkString +
                    "    <link port=\"PRBS" + i + ".output#1\" relation=\"link" + (i + num) + "\"/>\r\n" +
                    "    <link port=\"Driver" + i + ".input#1\" relation=\"link" + (i + num) + "\"/>\n";
        }

        // link driver with RingModulater
        for (int i = 0; i < num; i++) {
            linkString = linkString +
                    "    <link port=\"RingMod" + i + ".input#2\" relation=\"link" + (i + 2 * num) + "\"/>\r\n" +
                    "    <link port=\"Driver" + i + ".output#1\" relation=\"link" + (i + 2 * num) + "\"/>\n";
        }
        // link RingMod with Mux
        linkString = linkString +
                "    <link port=\"RingMod0.input#1\" relation=\"link" + 3 * num + "\"/>\r\n" +
                "    <link port=\"Mux.output\" relation=\"link" + 3 * num + "\"/>\n";

        // link RingMod together
        for (int i = 0; i < num - 1; i++) {
            linkString = linkString +
                    "    <link port=\"RingMod" + (i + 1) + ".input#1\" relation=\"link" + (1 + i + 3 * num) + "\"/>\r" +
                    "\n" +
                    "    <link port=\"RingMod" + i + ".output#3\" relation=\"link" + (1 + i + 3 * num) + "\"/>\n";
        }

        // link RingRes together
        for (int i = 0; i < num - 1; i++) {
            linkString = linkString +
                    "    <link port=\"RingRes" + (i + 1) + ".output#1\" relation=\"link" + (4 * num + i) + "\"/>\r\n" +
                    "    <link port=\"RingRes" + i + ".input#3\" relation=\"link" + (i + 4 * num) + "\"/>\n";
        }

        // link RingMod with fiber
        linkString = linkString +
                "    <link port=\"fiber.input#1\" relation=\"link" + (5 * num - 1) + "\"/>\r\n" +
                "    <link port=\"RingMod" + (num - 1) + ".output#3\" relation=\"link" + (5 * num - 1) + "\"/>\n";

        // link Spec with RingRes
        for (int i = 0; i < num; i++) {
            linkString = linkString + "    <link port=\"Spec" + i +
                    ".input#1\" relation=\"link" + (5 * num + i) + "\"/>\r\n" + "    <link " +
                    "port=\"RingRes" +
                    i + ".output#4\" relation=\"link" + (i + 5 * num) +
                    "\"/>\n";
        }

        // link fIBER with RingRes
        linkString = linkString +
                "    <link port=\"fiber.output\" relation=\"link" + 6 * num + "\"/>\r\n" +
                "    <link port=\"RingRes" + (num - 1) + ".input#3\" relation=\"link" + 6 * num + "\"/>\n";
    }

    public ArrayList<String> getComponentsName() {
        ArrayList<String> componentsName = new ArrayList<>();
        componentsName.add("global param"); // 0 single
        componentsName.add("Mux");            //1 single
        componentsName.add("CWLasers");        //2
        componentsName.add("Ring Mods");    //3
        componentsName.add("Fiber");        //4 single
        componentsName.add("Ring Res");        //5
        componentsName.add("Signal Generator");//6
        componentsName.add("Drivers");        //7
        componentsName.add("Spec Analyzer");    //8
        return componentsName;
    }

    public void setEditedProperty(int component, int index, int propertyIndex, String newValue) {
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
            case 3:
                this.ringModulatorComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 4:
                this.nonlinearFiberComponent.rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 5:
                this.ringResonatorComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 6:
                this.signalGeneratorComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 7:
                this.driverComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            case 8:
                this.spectrumAnalyzerComponents.get(index).rootProperty.property.get(propertyIndex).setValue(newValue);
                break;
            default:
                break;

        }
    }

}
