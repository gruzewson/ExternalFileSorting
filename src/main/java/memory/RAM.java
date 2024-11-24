package memory;

import data.DataManager;
import java.io.*;

public class RAM {
    private final String defaultRunPath = "src/main/java/memory/runs/run";
    private final int recordsNum;
    private final int bufferSize;
    private final int bufferNum;
    private final String filePath;
    private int readBuffers = 0;
    private int savedBuffers = 0;
    private int mergeCycles = 0;
    private final DataManager dataManager;

    public RAM(int bufferSize, int bufferNum, String filePath, int recordsNum) {
        this.bufferSize = bufferSize;
        this.bufferNum = bufferNum;
        this.filePath = filePath;
        this.recordsNum = recordsNum;
        this.dataManager = new DataManager(recordsNum);
    }

    public void initializeMerging(){
        dataManager.deleteAllRuns();

        int runNum = 0;
        for(int i = 0; i < recordsNum; i += bufferNum * bufferSize)
        {
            Run run = new Run(bufferSize, bufferNum, filePath);
            run.fillRun(i);
            run.sortRun();
            //saving run to file
            //in every path like that mergeCycles and runNum are used to make file name format "runXX.txt"
            //where first X is cycle of merging and second X is number of a run in that cycle
            run.saveRun(defaultRunPath + mergeCycles + runNum + ".txt");
            //System.out.println("run " + runNum + " saved");
            readBuffers += run.getReadBuffers();
            savedBuffers += run.getSavedBuffers();
            runNum++;
        }
    }

    public void mergeRuns(int runNum, int runName) {
        MergeState state = new MergeState(bufferNum, runNum, runName);

        initializeBuffers(state);
        int activeBufferCount = loadInitialBuffers(state);

        for (int i = 0; i < activeBufferCount; i++) {
            addBufferToHeap(state, i, 0);
        }

        mergeBuffers(state);
    }

    private void initializeBuffers(MergeState state) {
        for (int i = 0; i < state.buffers.length; i++) {
            state.buffers[i] = new Buffer(bufferSize);
            state.runLine[i] = 0;
            state.isRunFinished[i] = false;
        }
    }

    private int loadInitialBuffers(MergeState state) {
        int activeBufferCount = bufferNum - 1;

        for (int i = state.runNum; i < bufferNum + state.runNum - 1; i++) {
            String path = defaultRunPath + (mergeCycles - 1) + i + ".txt";
            File file = new File(path);

            if (!file.exists()) {
                activeBufferCount = i - state.runNum;
                break;
            }

            state.buffers[i - state.runNum].readRecords(path, state.runLine[i - state.runNum]);
            state.runLine[i - state.runNum] += bufferSize;
            state.runSizes[i - state.runNum] = DataManager.runSize(path);
        }

        return activeBufferCount;
    }

    private void addBufferToHeap(MergeState state, int bufferIndex, int position) {
        HeapNode heapNode = new HeapNode(state.buffers[bufferIndex].getRecord(position), bufferIndex, position);
        state.heap.add(heapNode);
    }

    private void mergeBuffers(MergeState state) {
        Buffer mergingBuffer = new Buffer(bufferSize);

        while (!allRunsFinished(state.isRunFinished)) {
            HeapNode smallestNode = state.heap.poll();

            if (smallestNode == null) break;

            mergingBuffer.addRecord(smallestNode.record);

            if (smallestNode.position == bufferSize - 1) {
                loadNextBuffer(state, smallestNode.bufferIndex);
            } else {
                addBufferToHeap(state, smallestNode.bufferIndex, smallestNode.position + 1);
            }

            if (mergingBuffer.getCurrentSize() == bufferSize) {
                saveMergingBuffer(mergingBuffer, state.runName);
            }
        }

        if (mergingBuffer.getCurrentSize() > 0) {
            saveMergingBuffer(mergingBuffer, state.runName);
        }
    }

    public void externalSort(String args) {
        initializeMerging();
        dataManager.printRuns(args);

        while(dataManager.howManyRuns() > 1) {
            int starterRun = 0;
            int howManyRuns = dataManager.howManyRuns();
            for(int i = 0; i < (int) Math.ceil(howManyRuns/(bufferNum-1)); i++) {
                mergeRuns(starterRun, i);
                starterRun+=bufferNum-1;
            }
            mergeCycles++;
            dataManager.deleteOldRuns(mergeCycles-2);
            dataManager.printRuns(args);
        }
        System.out.println("Read buffers: " + getReadBuffers());
        System.out.println("Saved buffers: " + getSavedBuffers());
        System.out.println("Merge cycles: " + getMergeCycles());
        System.out.println("I/O operations: " + (savedBuffers+readBuffers));
    }

    private void loadNextBuffer(MergeState state, int bufferIndex) {
        String path = defaultRunPath + (mergeCycles - 1) + (bufferIndex + state.runNum) + ".txt";

        if (state.runLine[bufferIndex] < state.runSizes[bufferIndex]) {
            state.buffers[bufferIndex].clearBuffer();
            state.buffers[bufferIndex].readRecords(path, state.runLine[bufferIndex]);
            readBuffers++;
            state.runLine[bufferIndex] += bufferSize;

            addBufferToHeap(state, bufferIndex, 0);
        } else {
            state.isRunFinished[bufferIndex] = true;
            //System.out.println("Run " + (bufferIndex + state.runNum) + " finished.");
        }
    }

    private void saveMergingBuffer(Buffer buffer, int runName) {
        buffer.saveBuffer(defaultRunPath + mergeCycles + runName + ".txt");
        savedBuffers++;
        buffer.clearBuffer();
    }

    private boolean allRunsFinished(boolean[] isRunFinished) {
        for (boolean finished : isRunFinished) {
            if (!finished) return false;
        }
        return true;
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


}
