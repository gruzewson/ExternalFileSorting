package memory;

import data.Record;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Run {
    private int bufferSize;
    private int bufferNum;
    private String filePath;
    private List<Record> records;
    private int readRecords = 0;
    private int readBuffers = 0;

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
            //System.out.println(buffer.toString());
            records.addAll(Arrays.asList(getRecordsFromBuffer(buffer)));
            currentLine += bufferSize;
            readRecords += buffer.getRecordsRead();
            buffer.clearBuffer();
        }
    }

    public void sortRun()
    {
        records.sort((r1, r2) -> {
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
    }

    public Record[] getRecordsFromBuffer(Buffer buffer)
    {
        return buffer.getRecords();
    }

    public String toString()
    {
        return records.stream()
                .map(Record::toString)
                .collect(Collectors.joining("\n"));
    }

    public int getReadRecords()
    {
        return readRecords;
    }

    public int getReadBuffers()
    {
        return readBuffers;
    }

    public int getRunSize()
    {
        return records.size();
    }

    public void saveRun(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (Record record : records) {
                writer.write(String.format("%.1f %.1f %.1f%n", record.getNumber(0), record.getNumber(1), record.getNumber(2)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }}
