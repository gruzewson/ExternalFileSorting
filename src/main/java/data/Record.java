package data;

import java.util.List;

public class Record {
    private List<Double> numbers;
    private double average;

    public Record(List<Double> numbers) {
        this.numbers = numbers;
        this.average = calculateAverage(numbers);
    }

    private double calculateAverage(List<Double> numbers) {
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        return Math.round((sum / numbers.size()) * 100.0) / 100.0;
    }

    public List<Double> getNumbers() {
        return numbers;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return numbers + " " + average;
    }
}
