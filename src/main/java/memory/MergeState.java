package memory;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MergeState {
    public Buffer[] buffers;
    public int[] runLine;
    public int[] runSizes;
    public boolean[] isRunFinished;
    public PriorityQueue<HeapNode> heap;
    public int runNum;
    public int runName;

    public MergeState(int bufferNum, int runNum, int runName) {
        this.buffers = new Buffer[bufferNum];
        this.runLine = new int[bufferNum];
        this.runSizes = new int[bufferNum];
        this.isRunFinished = new boolean[bufferNum];
        this.heap = new PriorityQueue<>(bufferNum, Comparator.comparingDouble(HeapNode::getAverage));
        this.runNum = runNum;
        this.runName = runName;
    }
}
