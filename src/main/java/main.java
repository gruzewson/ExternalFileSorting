import data.DataManager;

public class main
{
    public static void simulation(String[] args)
    {
        System.out.println("Hello World!");
        DataManager dataManager = new DataManager();
        dataManager.generateData(10);
    }
}
