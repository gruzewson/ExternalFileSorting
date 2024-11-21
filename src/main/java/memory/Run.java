package memory;

import data.Record;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Run {
    private final int bufferSize;
    private final int bufferNum;
    private final String filePath;
    private final List<Record> records;
    private int readBuffers = 0;
    private int savedBuffers = 0;

    public Run(int bufferSize, int bufferNum, String filePath) {
        this.bufferSize = bufferSize;
        this.bufferNum = bufferNum;
        this.filePath = filePath;
        this.records = new ArrayList<>();
    }

    public void fillRun(int currentLine)
    {
        Buffer buffer = new Buffer(bufferSize);
        for(int i = 0; i < bufferNum; i ++)
        {
            buffer.readRecords(filePath, currentLine);
            if(buffer.isNull())
                break;
            readBuffers++;
            records.addAll(Arrays.asList(buffer.getRecords()));
            currentLine += bufferSize;
            buffer.clearBuffer();
        }
    }

    public void sortRun()
    {
        records.sort((r1, r2) -> {
            return Double.compare(r1.getAverage(), r2.getAverage());
        });
    }

    public int getReadBuffers()
    {
        return readBuffers;
    }

    public int getSavedBuffers()
    {
        return savedBuffers;
    }

    public int getRunSize()
    {
        return records.size();
    }

    public void saveRun(String filePath) {
        Buffer buffer = new Buffer(bufferSize);

        for (Record record : records) {
            buffer.addRecord(record);
            if (buffer.getCurrentSize() == bufferSize)
            {
                buffer.saveBuffer(filePath);
                savedBuffers++;
                buffer.clearBuffer();
            }

        }
        // Write any remaining records in the buffer.
        if (buffer.getCurrentSize() > 0) {
            buffer.saveBuffer(filePath);
            savedBuffers++;
        }

    }
}
