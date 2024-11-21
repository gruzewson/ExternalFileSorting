package memory;

import data.DataManager;
import java.io.*;
import java.util.Objects;
import java.util.PriorityQueue;

public class Merger {
    private final int recordsNum;
    private final int bufferSize;
    private final int bufferNum;
    private final String filePath;
    private int readBuffers = 0;
    private int savedBuffers = 0;
    private int mergeCycles = 0;

    public Merger(int bufferSize, int bufferNum, String filePath, int recordsNum) {
        this.bufferSize = bufferSize;
        this.bufferNum = bufferNum;
        this.filePath = filePath;
        this.recordsNum = recordsNum;
    }

    public static int runSize(String filePath) {
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        return lineCount;
    }

    public int howManyRuns() {
        int runs = 0;
        File dir = new File("src/main/java/memory/runs");
        for(File file: Objects.requireNonNull(dir.listFiles()))
        {
            if (!file.isDirectory())
                runs++;
        }
        return runs;
    }

    public void deleteOldRuns(int cycle) {
        File dir = new File("src/main/java/memory/runs");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                String fileName = file.getName();
                // Extract the cycle number from the filename
                if (fileName.startsWith("run" + cycle)) {
                    file.delete();
                }
            }
        }
    }

    public void deleteAllRuns() {
        File dir = new File("src/main/java/memory/runs");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    public void initialize(){
        deleteAllRuns();

        int runNum = 0;
        for(int i = 0; i < recordsNum; i += bufferNum * bufferSize)
        {
            Run run = new Run(bufferSize, bufferNum, filePath);
            run.fillRun(i);
            run.sortRun();
            //saving run to file
            run.saveRun("src/main/java/memory/runs/run" + mergeCycles + runNum + ".txt");
            System.out.println("run " + runNum + " saved");
            readBuffers += run.getReadBuffers();
            savedBuffers += run.getSavedBuffers();
            runNum++;
        }
    }

    public void mergeRuns(int runNum, int runName) {

        String path;
        Buffer mergingBuffer = new Buffer(bufferSize);
        Buffer[] buffers = new Buffer[bufferNum];
        int[] runLine = new int[bufferNum];
        int[] runSizes = new int[bufferNum];
        boolean[] isRunFinished = new boolean[bufferNum];
        int newBufferNum = bufferNum - 1;

        for (int i = 0; i < bufferNum; i++) {
            buffers[i] = new Buffer(bufferSize);
            runLine[i] = 0;
            isRunFinished[i] = false;
        }
        for (int i = runNum; i < bufferNum + runNum - 1; i++) {
            path = "src/main/java/memory/runs/run" +  (mergeCycles-1) + i + ".txt";
            File file = new File(path);
            if (!file.exists()) {
                newBufferNum = i - runNum;
                break;
            }
            buffers[i - runNum].readRecords(path, runLine[i - runNum]);
            runLine[i - runNum] += bufferSize;
            runSizes[i - runNum] = runSize(path);
        }

        PriorityQueue<HeapNode> heap = new PriorityQueue<>((r1, r2) -> {
            return Double.compare(r1.getAverage(), r2.getAverage());
        });

        for (int i = 0; i < newBufferNum; i++) {
            HeapNode heapNode = new HeapNode(buffers[i].getHead(), i, 0);
            heap.add(heapNode);
        }
        while (!IsRunFinished(isRunFinished)) {
            // Remove the smallest element from the heap
            HeapNode heapNode = heap.poll();
            if (heapNode == null) {
                break;
            }
            mergingBuffer.addRecord(heapNode.record);
            if(heapNode.position == bufferSize - 1)
            {
                path = "src/main/java/memory/runs/run" + (mergeCycles-1) +(heapNode.bufferIndex+runNum) + ".txt";
                if (runLine[heapNode.bufferIndex] < runSizes[heapNode.bufferIndex])
                { // Check if more records exist in the file
                    buffers[heapNode.bufferIndex].clearBuffer();
                    buffers[heapNode.bufferIndex].readRecords(path, runLine[heapNode.bufferIndex]);
                    readBuffers++;
                    runLine[heapNode.bufferIndex] += bufferSize;
                    heap.add(new HeapNode(buffers[heapNode.bufferIndex].getRecord(0), heapNode.bufferIndex, 0));
                } else {

                    isRunFinished[heapNode.bufferIndex] = true;
                    System.out.println("run " + (heapNode.bufferIndex+runNum) + " finished");
                }
            }
            else{
                heap.add(new HeapNode(buffers[heapNode.bufferIndex].getRecord(heapNode.position + 1), heapNode.bufferIndex, heapNode.position + 1));
            }

            if (mergingBuffer.getCurrentSize() == bufferSize) {
                mergingBuffer.saveBuffer("src/main/java/memory/runs/run" + mergeCycles + runName + ".txt");
                savedBuffers++;
                mergingBuffer.clearBuffer();
            }
        }
        if(mergingBuffer.getCurrentSize() > 0) {
            mergingBuffer.saveBuffer("src/main/java/memory/runs/run" + mergeCycles+ runName + ".txt");
            savedBuffers++;
        }

    }

    public int getReadBuffers() {
        return readBuffers;
    }

    public int getSavedBuffers() {
        return savedBuffers;
    }

    public int getMergeCycles() {
        return mergeCycles;
    }

    public boolean IsRunFinished(boolean[] isRunFinished) {
        for(boolean b : isRunFinished) {
            if(!b) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        DataManager dataManager = new DataManager(10000000);
        dataManager.generateData("data");
        Merger merger = new Merger(10, 1001, "src/main/java/data/data.txt", 10000000);
        merger.initialize();
        merger.mergeCycles++;

        while(merger.howManyRuns() > 1) {
            int starterRun = 0;
            int howManyNewRuns;
            if(merger.howManyRuns()%(merger.bufferNum-1) == 0) {
                howManyNewRuns = merger.howManyRuns()/(merger.bufferNum-1);
            }
            else {
                howManyNewRuns = merger.howManyRuns()/(merger.bufferNum-1) + 1;
            }
            for(int i = 0; i < howManyNewRuns; i++) {
                merger.mergeRuns(starterRun, i);
                starterRun+=merger.bufferNum-1;
            }
            merger.mergeCycles++;
            merger.deleteOldRuns(merger.mergeCycles-2);
        }
        System.out.println("Cycles: " + merger.getMergeCycles());
        System.out.println("Read buffers: " + merger.getReadBuffers());
        System.out.println("Saved buffers: " + merger.getSavedBuffers());
        System.out.println("Merge cycles: " + merger.getMergeCycles());
        System.out.println((merger.savedBuffers+merger.readBuffers));
    }
}
