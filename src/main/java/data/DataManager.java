package data;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DataManager {
    private final int recordNum;
    private static final int PRINT_LIMIT = 10;

    public DataManager(int recordNum) {
        this.recordNum = recordNum;
    }

    public void generateData(String fileName) {
        File file = new File("src/main/java/data/" + fileName + ".txt");
        float[] options = {2, 3, 3.5F, 4, 4.5F, 5};
        float v1, v2, v3;
        Random random = new Random();

        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < recordNum; i++) {
                v1 = options[random.nextInt(options.length)];
                v2 = options[random.nextInt(options.length)];
                v3 = options[random.nextInt(options.length)];

                writer.write(String.format("%.1f %.1f %.1f%n", v1, v2, v3));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        File defaultDirectory = new File("src/main/java/data");
        fileChooser.setCurrentDirectory(defaultDirectory);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("File selected: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } else {
            System.out.println("File selection canceled. Choosing deafult file...");
            return "src/main/java/data/data.txt";
        }
    }


    public void readFromKeyboard(String fileName) {
        File file = new File("src/main/java/data/" + fileName + ".txt");
        float v1, v2, v3;
        System.out.println("Enter the number of records: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int recordNum;
        try {
            recordNum = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Grades to choose from are: {2, 3, 3.5, 4, 4.5, 5} \nEnter the grades: ");
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < recordNum; i++) {
                String[] grades = reader.readLine().split("\\s+");
                v1 = Float.parseFloat(grades[0]);
                v2 = Float.parseFloat(grades[1]);
                v3 = Float.parseFloat(grades[2]);

                writer.write(String.format("%.1f %.1f %.1f%n", v1, v2, v3));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getData(String[] args, String fileName){
        String mode;
        if (args.length < 1) {
            mode = "generate";
        }
        else {
            mode = args[0];
        }
        switch (mode.toLowerCase()) {
            case "generate":
                System.out.println("Generating data...");
                generateData(fileName);
                return "src/main/java/data/" + fileName + ".txt";
            case "file":
                System.out.println("Choose file...");
                return chooseFile();
            case "keyboard":
                System.out.println("Reading data from keyboard...");
                readFromKeyboard(fileName);
                return "src/main/java/data/" + fileName + ".txt";
            default:
                System.out.println("Invalid mode");
        }
        return "src/main/java/data/data.txt";
    }

    public void printRuns(String args) {
        int limit = 0;
        File dir = new File("src/main/java/memory/runs");
        for(File file: Objects.requireNonNull(dir.listFiles()))
        {
            if(args == null)
                return;
            else if(args.equals("short"))
                limit = PRINT_LIMIT;
            else if(args.equals("full")) //if args[1] is "full" then limit is set to run size
                limit = runSize(file.getAbsolutePath());
            System.out.println("--------------------------------------------");
            System.out.println(file.getName() + " " + runSize(file.getAbsolutePath()) + " records:\n");
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null && limit > 0) {
                    String[] parts = line.split("\\s+");
                    List<Float> values = new ArrayList<>();
                    for (String part : parts) {
                        values.add(Float.parseFloat(part));
                    }
                    Record record = new Record(values);
                    System.out.println(record.toString());
                    limit--;
                    if(limit == 0 && args.equals("short"))
                        System.out.println("And " + (runSize(file.getAbsolutePath()) - PRINT_LIMIT) + " more...\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}
