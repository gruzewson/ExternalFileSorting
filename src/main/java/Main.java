import data.DataManager;
import memory.RAM;

public class Main {
    public static void main(String[] args) {
        int bufferSize = 10;
        int bufferNum = 10;
        int recordNum = 1000;
        System.out.println("\nRecord Number: " + recordNum);

        DataManager dataManager = new DataManager(recordNum);
        String filePath = dataManager.getData(args, "data");
        RAM ram = new RAM(bufferSize, bufferNum, filePath, recordNum);
        if (args.length < 2) {
            ram.externalSort(null);
        } else {
            ram.externalSort(args[1]);
        }
    }
}
