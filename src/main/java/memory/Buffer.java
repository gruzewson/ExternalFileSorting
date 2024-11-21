package memory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import data.Record;

public class Buffer {
    private final int size;
    private Record[] buffer;
    private int tail = 0;
    private int recordsRead = 0;

    public Buffer(int size) {
        this.size = size;
        this.buffer = new Record[size];
    }

    public void readRecords(String filePath, int startLine) {
        File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            int currentLine = 0;

            while (currentLine < startLine && (line = reader.readLine()) != null) {
                currentLine++;
            }

            for (int i = 0; i < size; i++) {
                line = reader.readLine();
                if (line == null) {
                    System.out.println("Reached the end of the file. Buffer will not be completely filled.");
                    for (int j = i; j < size; j++) {
                        buffer[j] = null;
                    }
                    break;
                }
                //System.out.println("Reading line: " + line);
                List<Double> numbers = Arrays.stream(line.split(" "))
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                Record record = new Record(numbers);
                recordsRead++;
                buffer[i] = record;
                tail++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public Record[] getBuffer() {
        return buffer;
    }

    @Override
    public String toString() {
        return Arrays.stream(buffer)
                .map(record -> record == null ? "null" : record.toString())
                .collect(Collectors.joining("\n"));
    }

    public int getRecordsRead() {
        return recordsRead;
    }

    public void clearBuffer() {
        buffer = new Record[size];
        recordsRead = 0;
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

    public int getTail() {
        return tail;
    }

    public Record getRecord(int index) {
        return buffer[index];
    }

    public void addRecord(Record record) {
        buffer[tail] = record;
        if(tail == size - 1) {
            System.out.println("Buffer is full");
        }
        else
            tail++;
    }

    public void saveBuffer(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Record record : buffer) {
                if (record != null) {
                    writer.write(record.toString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
