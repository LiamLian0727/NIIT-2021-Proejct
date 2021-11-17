import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class testa {
    static String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void dataFromCsvToHbase() throws IOException {

        String[] title, a;
        int count = 0;

        BufferedReader reader = new BufferedReader(
                new FileReader("src/main/java/MapReduce/UseOfHadoopMR/tmp/part-r-00000"));
        String line;
        line = reader.readLine();
        String[] s = line.split("    ");
        for (String s1 : s) {
            System.out.println(s1 + " : ");
        }
    }

    public static void main(String[] args) throws IOException {
        dataFromCsvToHbase();
    }


}
