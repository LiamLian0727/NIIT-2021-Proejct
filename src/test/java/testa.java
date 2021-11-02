import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class testa{
    static String csvSplitBy =",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static void dataFromCsvToHbase() throws IOException {

        String[] title,a;
        int count=0;

        BufferedReader reader = new BufferedReader(
                new FileReader("src\\main\\dataset\\IMDb_movies.csv"));
        String line;
        line= reader.readLine();
        title = line.split(csvSplitBy);
        line= reader.readLine();
        a = line.split(csvSplitBy);
        for (int i = 0; i < 22; i++) {
            if(!a[i].equals("")){
                System.out.println(title[i] + " : "+a[i]);
            }else{
                System.out.println(title[i] + " : null");
            }
        }
        /*
        while ((line = reader.readLine()) != null&&count<5) {
            String[] item = line.split(",");
            System.out.println(item.length);
            for (String s : item) {
                System.out.print(s+":");
            }
            System.out.println("\n");
            count++;
        }
        */
    }

    public static void main(String[] args) throws IOException {
        dataFromCsvToHbase();
    }


}
