package memory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import data.Record;

public class Buffer {
    private final int maxSize;
    private Record[] buffer;
    private int currentSize = 0;

    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        this.buffer = new Record[maxSize];
    }

    public void readRecords(String filePath, int startLine) {
        File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            int currentLine = 0;

            while (currentLine < startLine && (line = reader.readLine()) != null) {
                currentLine++;
            }

            for (int i = 0; i < maxSize; i++) {
                line = reader.readLine();
                if (line == null) {
                    System.out.println("Reached the end of the file. Buffer will not be completely filled.");
                    for (int j = i; j < maxSize; j++) {
                        buffer[j] = null;
                    }
                    break;
                }
                List<Double> numbers = Arrays.stream(line.split(" "))
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                Record record = new Record(numbers);
                buffer[i] = record;
                currentSize++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return Arrays.stream(buffer)
                .map(record -> record == null ? "null" : record.toString())
                .collect(Collectors.joining("\n"));
    }

    public void clearBuffer() {
        buffer = new Record[maxSize];
        currentSize = 0;
    }

    public Record[] getRecords() {
        return buffer;
    }

    public boolean isNull() {
        for (Record record : buffer) {
            if (record == null) {
                return true;
            }
        }
        return false;
    }

    public Record getHead() {
        return buffer[0];
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public Record getRecord(int index) {
        return buffer[index];
    }

    public void addRecord(Record record) {
        if(currentSize == maxSize) {
            System.out.println("Buffer is full");
        }
        else {
            buffer[currentSize] = record;
            currentSize++;
        }
    }

    public void saveBuffer(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (Record record : buffer) {
                if (record == null) {
                    break;
                }
                writer.write(String.format("%.1f %.1f %.1f%n", record.getNumber(0), record.getNumber(1), record.getNumber(2)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
