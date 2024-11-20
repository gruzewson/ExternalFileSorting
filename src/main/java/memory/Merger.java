package memory;

import data.DataManager;

public class Merger {

    private Run run1;
    private Run run2;
    private int recordsNum;
    private int bufferSize;
    private int bufferNum;
    private String filePath;
    private int readRecords = 0;
    private int readBuffers = 0;
    private int mergeCycles = 0;

    public Merger(int bufferSize, int bufferNum, String filePath, int recordsNum) {
        this.bufferSize = bufferSize;
        this.bufferNum = bufferNum;
        this.filePath = filePath;
        this.recordsNum = recordsNum;
    }

    public void initialize(){
        int runNum = 0;
        for(int i = 0; i < recordsNum; i += bufferNum * bufferSize)
        {
            Run run = new Run(bufferSize, bufferNum, filePath);
            run.fillRun(i);
            System.out.println("run " + (runNum) + " before sorting\n");
            System.out.println(run.toString());
            run.sortRun();
            System.out.println("\nrun " + (runNum) + " [" + run.getRunSize() + "] after sorting\n");
            System.out.println(run.toString());
            readRecords += run.getReadRecords();
            readBuffers = run.getReadBuffers();
            //saving run to file
            run.saveRun("src/main/java/memory/runs/run" + (runNum) + ".txt");
            runNum++;
        }
    }

    public void mergeRuns() {
        int currentLine = 0;
        for(int i = 0; i < 2; i++)
        {
            Run run = new Run(bufferSize, bufferNum, filePath);
            run.fillRun(currentLine);
            currentLine += run.getRunSize();
            System.out.println("run " + (i+1) + " before sorting\n");
            System.out.println(run.toString());
            run.sortRun();
            System.out.println("\nrun " + (i+1) + " after sorting\n");
            System.out.println(run.toString());
            readRecords += run.getReadRecords();
            readBuffers = run.getReadBuffers();
        }
    }

    public int getReadRecords() {
        return readRecords;
    }

    public int getReadBuffers() {
        return readBuffers;
    }

    public int getMergePasses() {
        return mergeCycles;
    }

    @Override
    public String toString() {
        return "Times buffers were read: " + readBuffers + "\nTimes records were read: " + readRecords + "\nMerge cycles: " + mergeCycles;
    }

    public static void main(String[] args) {
        Merger merger = new Merger(5, 2, "src/main/java/data/data.txt", 30);
        merger.initialize();
    }
}
