package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jerus
 */
public class DataSave {

    transient String userHome = System.getProperty("user.home");
    transient Path path = Path.of(userHome, "YourGame", "save.json");

    //save variables
    private double cash;
    private int[] buildings;
    private int month = 1;
    private JsonArray debts;

    /**
     * Private-sector loans. Kept in its own array rather than mixed into debts,
     * because the two hierarchies are separate and the load switch would
     * otherwise have to disambiguate government bonds from business loans by
     * type string alone.
     */
    private JsonArray businessDebts;
    private double[] progress;
    private int[] underConstruction;
    private int constructionMaterials;
    private int storeInventory;
    private int industryFoodInventory;
    private int population;
    
    //settings
    private boolean reports = true;
    private boolean graphs = true;
    
    
    //business stuff
    //cash
    private double industrialCash;
    private double commercialCash;
    private double realEstateCash;
            

 

    public void setUnderConstruction(int[] underConstruction) {
        this.underConstruction = underConstruction;
    }

    //setters
    public void setCash(double money) {
        cash = money;
    }
    
    public void setMonth(int month){
        this.month = month;
    }

    public void setBuildingNum(int i) {
        buildings = new int[i];
    }

    public void setBuildingQuantity(int index, int quantity) {
        if (index < 0 || index >= buildings.length) {
            throw new IllegalArgumentException("Invalid building index: " + index);
        }
        buildings[index] = quantity;
    }
    
    public void setDebt(List<Debt> debts) {
        Gson gson = new Gson();
        this.debts = gson.toJsonTree(debts).getAsJsonArray();
    }
    
    public void setProgress(double[] progress){
        this.progress = progress;
    }
    
    public void setConstructionMaterials(int constructionMaterials){
        this.constructionMaterials = constructionMaterials;
    }
    
    public void setStoreInventory(int storeInventory){
        this.storeInventory = storeInventory;
    }
    public void setIndustryFoodInventory(int foodInventory){
        this.industryFoodInventory = foodInventory;
    }
    public void setPopulation(int population){
        this.population = population;
    }
    public void setReports(boolean reports){
        this.reports = reports;
    }
    public void setGraphs(boolean graphs){
        this.graphs = graphs;
    }

    public void setIndustrialCash(double industrialCash) {
        this.industrialCash = industrialCash;
    }

    public void setCommercialCash(double commercialCash) {
        this.commercialCash = commercialCash;
    }

    public void setRealEstateCash(double realEstateCash) {
        this.realEstateCash = realEstateCash;
    }
    

    public void saveGame() {
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
    public int getUnderConstructionLength(){
        return underConstruction.length;
    }
       public int getUnderConstruction(int index) {
        return underConstruction[index];
    }
    public double getCash() {
        return cash;
    }
    
    public int getMonth(){
        return month;
    }
    
    public int getBuildingQuantity(int index){
        return buildings[index];
    }
    
    public int getBuildingsLength(){
        return buildings.length;
    }
    
    public JsonArray getDebt(){
        return debts;
    }

    public void setBusinessDebt(List<BusinessDebt> loans) {
        Gson gson = new Gson();
        this.businessDebts = gson.toJsonTree(loans).getAsJsonArray();
    }

    public JsonArray getBusinessDebt(){
        return businessDebts;
    }

    public int getProgressLength(){
        return progress.length;
    }
    
    public double getProgress(int index){
        return progress[index];
    }
    
    public int getConstructionMaterials(){
        return constructionMaterials;
    }
    
    public int getStoreInventory(){
        return storeInventory;
    }
    
    public int getIndustryFoodInventory(){
        return industryFoodInventory;
    }
    public int getPopulation(){
        return population;
    }
    public boolean getReports(){
        return reports;
    }
    public boolean getGraphs(){
        return graphs;
    }
    
    public double getIndustrialCash() {
        return industrialCash;
    }

    public double getCommercialCash() {
        return commercialCash;
    }

    
    public double getRealEstateCash() {
        return realEstateCash;
    }
}
