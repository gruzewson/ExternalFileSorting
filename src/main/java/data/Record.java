package data;

import java.util.List;

public class Record {
    private final List<Double> numbers;
    private final double average;

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

    public double getAverage() {
        return average;
    }

    public double getNumber(int index) {
        return numbers.get(index);
    }

    @Override
    public String toString() {
        return numbers + " " + average;
    }
}
