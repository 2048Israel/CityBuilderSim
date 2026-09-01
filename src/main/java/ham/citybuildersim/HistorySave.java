/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jerus
 */
public class HistorySave {
    
transient String userHome = System.getProperty("user.home");
    transient Path path = Path.of(userHome, "YourGame", "history.json");

    
    
    private List<Double> cash = new ArrayList<>();
    private List<Double> gdp = new ArrayList<>();
    private List<Integer> month = new ArrayList<>();
    private List<Double> debt = new ArrayList<>();
    private List<Double> interestRate = new ArrayList<>();
    private List<Integer> jobs = new ArrayList<>();
    private List<Integer> workforce = new ArrayList<>();
    private List<Integer> population = new ArrayList<>();
            
    public void recordMonth(
            int month,
            double cash,
            double gdp,
            double debt,
            double interestRate,
            int jobs,
            int workforce,
            int population) {

        this.month.add(month);
        this.cash.add(cash);
        this.gdp.add(gdp);
        this.debt.add(debt);
        this.interestRate.add(interestRate);
        this.jobs.add(jobs);
        this.workforce.add(workforce);
        this.population.add(population);
    }
 

    

    public void saveHistory() {
        try {
            Files.createDirectories(path.getParent());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(this);

            Files.writeString(path, json);

            System.out.println(path.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error saving.");
        }
    }
    
    //getters
    public List<Double> getCash() {
        return cash;
    }

    public List<Double> getGdp() {
        return gdp;
    }

    public List<Integer> getMonth() {
        return month;
    }

    public List<Double> getDebt() {
        return debt;
    }

    public List<Double> getInterestRate() {
        return interestRate;
    }

    public List<Integer> getJobs() {
        return jobs;
    }

    public List<Integer> getWorkforce() {
        return workforce;
    }

    //save variables
    public List<Integer> getPopulation() {
        return population;
    }
    
    //setters
    public void setCash(List<Double> cash) {
        this.cash = cash;
    }

    public void setGdp(List<Double> gdp) {
        this.gdp = gdp;
    }

    public void setMonth(List<Integer> month) {
        this.month = month;
    }

    public void setDebt(List<Double> debt) {
        this.debt = debt;
    }

    public void setInterestRate(List<Double> interestRate) {
        this.interestRate = interestRate;
    }

    public void setJobs(List<Integer> jobs) {
        this.jobs = jobs;
    }

    public void setWorkforce(List<Integer> workforce) {
        this.workforce = workforce;
    }

    public void setPopulation(List<Integer> population) {
        this.population = population;
    }



  
    
}
