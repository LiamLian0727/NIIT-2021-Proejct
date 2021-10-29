import java.io.BufferedReader;
import java.io.FileReader;


/**
 * @author 连仕杰
 */
public class FromCsv {

    static int countMax =50;
    public void dataFromCsv() {
        try {
            BufferedReader reader = new BufferedReader(
                                    new FileReader("src\\main\\dataset\\IMDb_movies.csv"));
            String line;
            int count=0;
            while ((line = reader.readLine()) != null && count < countMax) {
                count++;
                String[] item = line.split(",");
                //CSV格式文件为逗号分隔符文件，这里根据逗号切分
                for (String s : item) {
                    System.out.print(s+"  ");
                }
                System.out.println("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
            }
            System.out.println("----------sum:"+ count +"--------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        FromCsv fromCsv = new FromCsv();
        fromCsv.dataFromCsv();
    }
}
