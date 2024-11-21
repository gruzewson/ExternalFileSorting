package memory;

import data.Record;

public class HeapNode {
    Record record;
    int bufferIndex;  // Index of the buffer this element comes from
    int position;     // Position of the element within the buffer

    public HeapNode(Record record, int bufferIndex, int position) {
        this.record = record;
        this.bufferIndex = bufferIndex;
        this.position = position;
    }

    public double getAverage() {
        return record.getAverage();
    }

    public String toString() {
        return record.toString() + " " + bufferIndex + " " + position;
    }
}