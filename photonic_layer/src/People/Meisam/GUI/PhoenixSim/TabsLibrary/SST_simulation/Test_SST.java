package People.Meisam.GUI.PhoenixSim.TabsLibrary.SST_simulation;

import java.io.*;

/**
 * @author kristoff
 */
public class Test_SST {
    public static void main(String[] args) throws IOException {
        String toWrite = "# Automatically generated SST Python input\n" +
                "import sst\n" +
                "\n" +
                "# Define SST core options\n" +
                "sst.setProgramOption(\"timebase\", \"1 ps\")\n" +
                "sst.setProgramOption(\"stopAtCycle\", \"10000s\")\n" +
                "\n" +
                "# Define the simulation components\n" +
                "comp_clocker0 = sst.Component(\"clocker0\", \"simpleElementExample.simpleRNGComponent\")\n" +
                "comp_clocker0.addParams({\n" +
                "      \"count\" : \"\"\"100000\"\"\",\n" +
                "      \"seed\" : \"\"\"1447\"\"\",\n" +
                "      \"verbose\" : \"1\",\n" +
                "      \"rng\" : \"\"\"mersenne\"\"\"\n" +
                "})\n" +
                "\n" +
                "\n" +
                "# Define the simulation links\n" +
                "# End of generated output.";

        BufferedWriter writer = new BufferedWriter(new FileWriter("test1.py"));
        writer.write(toWrite);

        writer.close();
        try {
            Process p = Runtime.getRuntime().exec("sst " + System.getProperty("user.dir") +
                    "/test1.py");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s;

            System.out.println("SST simulation output:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Error message(s):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);
        } catch (
                IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
