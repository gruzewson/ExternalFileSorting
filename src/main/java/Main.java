import data.DataManager;
import memory.Buffer;
import memory.Run;

public class Main {
    public static void main(String[] args) {
        int recordsRead = 0;
        int buffersRead = 0;
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
        int currentLine = 0;
        for(int i = 0; i < 2; i++)
        {
            Run run = new Run(2, 10, "src/main/java/data/data.txt");
            run.fillRun(currentLine);
            currentLine += run.getRunSize();
            System.out.println("run " + (i+1) + " before sorting\n");
            System.out.println(run.toString());
            run.sortRun();
            System.out.println("\nrun " + (i+1) + " after sorting\n");
            System.out.println(run.toString());
            buffersRead = run.getReadBuffers();
        }

        System.out.println("Times buffers were read: " + buffersRead);
        System.out.println("Times records were read: " + recordsRead);

    }
}
