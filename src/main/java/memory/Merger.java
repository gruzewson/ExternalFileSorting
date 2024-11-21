package memory;

import data.DataManager;
import data.Record;

import java.io.*;
import java.util.Objects;
import java.util.PriorityQueue;

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
        //clear runs
        File dir = new File("src/main/java/memory/runs");
        for(File file: Objects.requireNonNull(dir.listFiles()))
        {
            if (!file.isDirectory())
                file.delete();
        }

        int runNum = 0;
        for(int i = 0; i < recordsNum; i += bufferNum * bufferSize)
        {
            Run run = new Run(bufferSize, bufferNum, filePath);
            run.fillRun(i);
            //System.out.println("run " + (runNum) + " before sorting\n");
            //System.out.println(run.toString());
            run.sortRun();
            //System.out.println("\nrun " + (runNum) + " [" + run.getRunSize() + "] after sorting\n");
            //System.out.println(run.toString());
            readRecords += run.getReadRecords();
            readBuffers = run.getReadBuffers();
            //saving run to file
            run.saveRun("src/main/java/memory/runs/run" + (runNum) + ".txt");
            runNum++;
        }
    }

    public void mergeRuns() {
        String path;
        Buffer mergingBuffer = new Buffer(bufferSize);
        Buffer[] buffers = new Buffer[bufferNum];
        int[] runLine = new int[bufferNum];
        boolean[] runFinished = new boolean[bufferNum];
        for(int i = 0; i < bufferNum; i++) {
            buffers[i] = new Buffer(bufferSize);
            runLine[i] = 0;
            runFinished[i] = false;
        }
        for(int i = 0; i < bufferNum; i++) {
            path = "src/main/java/memory/runs/run" + i + ".txt";
            buffers[i].readRecords(path, runLine[i]);
            runLine[i] += bufferSize;
            System.out.println(i + "\n");
            System.out.println(buffers[i].toString());
        }

        PriorityQueue<HeapNode> heap = new PriorityQueue<>((r1, r2) -> {
            if(r1.getAverage() > r2.getAverage())
            {
                return 1;
            }
            else if(r1.getAverage() < r2.getAverage())
            {
                return -1;
            }
            else
            {
                return 0;
            }
        });
        for(int i = 0; i < bufferNum; i++) {
            HeapNode heapNode = new HeapNode(buffers[i].getHead(), i, 0);
            heap.add(heapNode);
            System.out.println(heap.toString());
        }
        while(!checkRunFinished(runFinished)) {
            //delete head of heap
            HeapNode heapNode = heap.poll();
            if(heapNode != null){
                mergingBuffer.addRecord(heapNode.record);
                System.out.println("merging buffer" + mergingBuffer.toString());
                System.out.println("heap" + heap.toString());
            }
            System.out.println("took record from run: " + heapNode.bufferIndex + " " + heapNode.position);
            heap.add(new HeapNode(buffers[heapNode.bufferIndex].getRecord(heapNode.position+1), heapNode.bufferIndex, heapNode.position + 1));
            if(heapNode.position + 1 == bufferSize * bufferNum) {
                runFinished[heapNode.bufferIndex] = true;
            }

            if(mergingBuffer.getTail() == bufferSize-1) {
                mergingBuffer.saveBuffer("src/main/java/memory/runs/run11111.txt");
                mergingBuffer.clearBuffer();
                break;
            }
            //System.out.println("deedw\n" + mergingBuffer.toString());
        }
        mergeCycles++;

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

    public void getRuns(int startingRun) throws IOException {
        String path;
        Buffer[] buffers = new Buffer[bufferNum];
        int[] runLine = new int[bufferNum];
        boolean[] runFinished = new boolean[bufferNum];
        for(int i = 0; i < bufferNum; i++) {
            buffers[i] = new Buffer(bufferSize);
            runLine[i] = 0;
            runFinished[i] = false;
        }
        for(int i = 0; i < bufferNum; i++) {
            path = "src/main/java/memory/runs/run" + i + ".txt";
            buffers[i].readRecords(path, runLine[i]);
            runLine[i] += bufferSize;
            System.out.println(i + "\n");
            System.out.println(buffers[i].toString());
        }
    }

    public boolean checkRunFinished(boolean[] runFinished) {
        for(boolean b : runFinished) {
            if(!b) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        Merger merger = new Merger(5, 2, "src/main/java/data/data.txt", 30);
        merger.initialize();
        merger.mergeRuns();
    }
}
