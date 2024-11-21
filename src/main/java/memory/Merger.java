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

    public void mergeRuns(int runNum) {
        String path;
        Buffer mergingBuffer = new Buffer(bufferSize);
        Buffer[] buffers = new Buffer[bufferNum];
        int[] runLine = new int[bufferNum];
        boolean[] runFinished = new boolean[bufferNum];
        int newBufferNum = bufferNum;
        for (int i = 0; i < bufferNum; i++) {
            buffers[i] = new Buffer(bufferSize);
            runLine[i] = 0;
            runFinished[i] = false;
        }
        for (int i = runNum; i < bufferNum + runNum; i++) {
            path = "src/main/java/memory/runs/run" + i + ".txt";
            File file = new File(path);
            if (!file.exists()) {
                newBufferNum = i - runNum;
                break;
            }
            buffers[i - runNum].readRecords(path, runLine[i - runNum]);
            runLine[i - runNum] += bufferSize;
        }

        for(int i = 0; i < newBufferNum; i++) {
            System.out.println(i + "\n");
            System.out.println(buffers[i].toString());
        }


        PriorityQueue<HeapNode> heap = new PriorityQueue<>((r1, r2) -> {
            if (r1.getAverage() > r2.getAverage()) {
                return 1;
            } else if (r1.getAverage() < r2.getAverage()) {
                return -1;
            } else {
                return 0;
            }
        });

        for (int i = 0; i < newBufferNum; i++) {
            HeapNode heapNode = new HeapNode(buffers[i].getHead(), i, 0);
            heap.add(heapNode);
            System.out.println(heap.toString());
        }
        while (!checkRunFinished(runFinished)) {
            // Remove the smallest element from the heap
            HeapNode heapNode = heap.poll();
            if (heapNode == null) {
                break;
            }
            // Add the record to the merging buffer
            mergingBuffer.addRecord(heapNode.record);
            System.out.println("merging buffer " + mergingBuffer.toString());
            System.out.println("\nheap" + heap.toString());
            System.out.println("took record from run: " + (heapNode.bufferIndex+runNum) + " " + heapNode.position);
            if(heapNode.position == bufferSize - 1) {
                System.out.println("buffer " + heapNode.bufferIndex + " is empty");
                path = "src/main/java/memory/runs/run" + (heapNode.bufferIndex+runNum) + ".txt";
                if (runLine[heapNode.bufferIndex] < bufferNum * bufferSize) { // Check if more records exist in the file
                    buffers[heapNode.bufferIndex].clearBuffer();
                    buffers[heapNode.bufferIndex].readRecords(path, runLine[heapNode.bufferIndex]);
                    runLine[heapNode.bufferIndex] += bufferSize;
                    System.out.println("buffer " + heapNode.bufferIndex + " filled");
                    heap.add(new HeapNode(buffers[heapNode.bufferIndex].getRecord(0), heapNode.bufferIndex, 0));
                } else {

                    runFinished[heapNode.bufferIndex] = true; // Mark this run as finished

                    System.out.println("run " + (heapNode.bufferIndex+runNum) + " finished");
                    //break;
                }
            }
            else{
                heap.add(new HeapNode(buffers[heapNode.bufferIndex].getRecord(heapNode.position + 1), heapNode.bufferIndex, heapNode.position + 1));
            }

            System.out.println("merging buffer " + mergingBuffer.getTail());
            if (mergingBuffer.getTail() == bufferSize - 1) {
                mergingBuffer.saveBuffer("src/main/java/memory/runs/run" + mergeCycles + ".txt");
                mergingBuffer.clearBuffer();
                System.out.println("merging buffer " + mergingBuffer.getTail());
                System.out.println("merging buffer cleared");
            }

            // Check if there are more records in the current buffer
            /*int bufferIndex = heapNode.bufferIndex;
            int nextPosition = heapNode.position + 1;

            if (nextPosition < buffers[bufferIndex].getTail()) {
                // Add the next record from the same buffer to the heap
                heap.add(new HeapNode(buffers[bufferIndex].getRecord(nextPosition), bufferIndex, nextPosition));
            } else {
                // If buffer is empty, check if more records are available in the run file
                path = "src/main/java/memory/runs/run" + bufferIndex + ".txt";
                if (runLine[bufferIndex] < recordsNum) { // Check if more records exist in the file
                    buffers[bufferIndex].clearBuffer();
                    buffers[bufferIndex].readRecords(path, runLine[bufferIndex]);
                    runLine[bufferIndex] += bufferSize;

                } else {
                    runFinished[bufferIndex] = true; // Mark this run as finished
                }
            }
            System.out.println("merging buffer " + mergingBuffer.getTail());
            if (mergingBuffer.getTail() == bufferSize - 1) {
                mergingBuffer.saveBuffer("src/main/java/memory/runs/run" + mergeCycles + runNum + ".txt");
                mergingBuffer.clearBuffer();
                System.out.println("merging buffer " + mergingBuffer.getTail());
                System.out.println("merging buffer cleared");
                for(int i = 0; i < bufferNum; i++) {
                    System.out.println(i + ", " + buffers[i].toString());
                }

            //System.out.println("deedw\n" + mergingBuffer.toString());
        }*/
        }
        if(mergingBuffer.getTail() > 0) {
            mergingBuffer.saveBuffer("src/main/java/memory/runs/run" + mergeCycles + ".txt");
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
        merger.mergeRuns(0);
        //merger.mergeRuns(2);
    }
}
