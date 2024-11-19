import data.DataManager;
import memory.Buffer;
import memory.Run;

public class Main {
    public static void main(String[] args) {
        int recordsRead = 0;
        System.out.println("Hello World!");
        //DataManager dataManager = new DataManager();
        //dataManager.chooseFile();
        //dataManager.printRecords();
        /*Buffer buffer1 = new Buffer(5);
        buffer1.readRecords("src/main/java/data/data.txt", 0);
        System.out.println(buffer1.toString());
        recordsRead += buffer1.getRecordsRead();
        Buffer buffer2 = new Buffer(5);
        buffer2.readRecords("src/main/java/data/data.txt", 6);
        System.out.println(buffer2.toString());
        recordsRead += buffer2.getRecordsRead();*/
        Run run = new Run(2, 11, "src/main/java/data/data.txt");
        run.fillRun();
        System.out.println(run.toString());
        run.sortRun();
        System.out.println(run.toString());
        System.out.println("Times buffers were read: " + run.getReadBuffers());
        System.out.println("Times records were read: " + run.getReadRecords());

    }
}
