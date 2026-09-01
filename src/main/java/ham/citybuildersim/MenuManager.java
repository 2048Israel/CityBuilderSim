package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Jerus
 */
public class MenuManager {

    public void showGameMenu() {
        System.out.println("--- City Simulator ---");
        System.out.println("1. Start New Game");
        System.out.println("2. Resume Game");
        System.out.println("3. Load Game");
        System.out.println("4. Save Game");
        System.out.println("5. Settings");
        System.out.println("6. Quit Game");
        System.out.print("Choose: ");
    }
    
    public void showSettingsMenu() {
        System.out.println("--- Settings ---");
        System.out.println("1. Graphs");
        System.out.println("2. Reports");
        System.out.println("3. Back");
        System.out.print("Choose: ");
    }


    public void showMainMenu(int month, double cash, double income) {
        printMenuHeader(month, cash);
        System.out.println("1. Buildings");
        System.out.println("2. Economy");
        System.out.println("3. Population");
        System.out.print("4. Next Month: $");
        System.out.println(formatter.format(income));
        System.out.println("5. Simulate Multiple Months");
        System.out.println("6. Main Menu");
        System.out.print("Choose: ");
    }

    public void showBuildingsMenu(double cash, int constructionMaterials) {
        System.out.println("\n--- Buildings Menu ---");
        System.out.print("Cash: ");
        System.out.println(formatter.format(cash));
        System.out.println("Construction Materials: " + constructionMaterials);
        System.out.println("1. Residential");
        System.out.println("2. Commerical");
        System.out.println("3. Industrial");
        System.out.println("4. Other");
        System.out.println("5. Return to menu");
        System.out.print("Choose: ");
    }

    public void showEconomyMenu(int month, double cash) {
        printMenuHeader(month, cash);
        System.out.println("1. Finance");
        System.out.println("2. Restructure");
        System.out.println("3. Debt Info");
        System.out.println("4. Sector Info");
        System.out.println("5. Back");
        System.out.print("Choose: ");
    }

    public void showFinanceMenu(int month, double cash, double rate) {
        printMenuHeader(month, cash);
        System.out.println("Interest Rate: %" + formatter.format(rate*100)+"%");
        System.out.println("1. Short Term T-Bills");
        System.out.println("2. Medium Term Bonds");
        System.out.println("3. Long Term Bonds");
        System.out.println("4. Back");
        System.out.print("Choose: ");
    }

    private void printMenuHeader(int month, double cash) {
        System.out.println("\n========================================");
        System.out.println("Month: " + month + " | Cash: $" + formatter.format(cash));
    }

    public void showSectorMenu(int month, double cash) {
        printMenuHeader(month, cash);
        System.out.println("1. Demographics & Labor Pool");
        System.out.println("2. Private Enterprise Sector");
        System.out.println("3. Municipal Utility Services");
        System.out.println("4. [System Operations]");
        System.out.println("5. Return to Main Menu");
        System.out.print("Select Department: ");
    }

    public void showPrivateSectorMenu(int month, double cash) {
        printMenuHeader(month, cash);
        System.out.println("1. Retail & Consumer Services (Commercial)");
        System.out.println("2. Resource Production (Industrial)");
        System.out.println("3. [Future Expansion]");
        System.out.println("4. [Future Expansion]");
        System.out.println("5. Back to Overview");
        System.out.print("Select Industry: ");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

}
