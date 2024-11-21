package data;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataManager {
    private static final int GRADES_NUM = 3;
    private List<Record> records = new ArrayList<>();

    public void generateData(int record_num) {
        File file = new File("src/main/java/data/data.txt");
        double[] options = {2, 3, 3.5, 4, 4.5, 5};
        double v1, v2, v3;
        Random random = new Random();

        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < record_num; i++) {
                v1 = options[random.nextInt(options.length)];
                v2 = options[random.nextInt(options.length)];
                v3 = options[random.nextInt(options.length)];

                writer.write(String.format("%.1f %.1f %.1f%n", v1, v2, v3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        File defaultDirectory = new File("src/main/java/data");
        fileChooser.setCurrentDirectory(defaultDirectory);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\s+"); // Split the line by whitespace
                    List<Double> numbers = new ArrayList<>();

                    for (int i = 0; i < GRADES_NUM; i++) {
                        numbers.add(Double.parseDouble(parts[i])); // Parse first 3 numbers
                    }

                    // Create a Record object and add it to the records list
                    records.add(new Record(numbers));
                }

                System.out.println("File processed successfully! Records added.");
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in file: " + e.getMessage());
            }
        } else {
            System.out.println("File selection canceled.");
        }
    }

    public void readFromKeyboard() {
        File file = new File("src/main/java/data/data.txt");
        double v1, v2, v3;
        System.out.println("Enter the number of records: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int record_num = 0;
        try {
            record_num = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Grades to choose from are: {2, 3, 3.5, 4, 4.5, 5} \nEnter the grades: ");
        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < record_num; i++) {
                String[] grades = reader.readLine().split("\\s+");
                v1 = Double.parseDouble(grades[0]);
                v2 = Double.parseDouble(grades[1]);
                v3 = Double.parseDouble(grades[2]);

                writer.write(String.format("%.1f %.1f %.1f%n", v1, v2, v3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRecords() {
        for (Record record : records) {
            System.out.println(record);
        }
    }

    public static void main(String[] args) {
        String mode;
        DataManager dataGenerator = new DataManager();
        if (args.length < 1) {
            mode = "generate";
        }
        else {
            mode = args[0];
        }
        switch (mode.toLowerCase()) {
            case "generate":
                System.out.println("Generating data...");
                dataGenerator.generateData(30);
                dataGenerator.printRecords();
                break;
            case "file":
                System.out.println("Choose file...");
                dataGenerator.chooseFile();
                dataGenerator.printRecords();
                break;
            case "keyboard":
                System.out.println("Reading data from keyboard...");
                dataGenerator.readFromKeyboard();
                dataGenerator.printRecords();
                break;
            default:
                System.out.println("Invalid mode");
        }
    }
}
