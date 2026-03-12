import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

/**
 * The selectworld is the world where the user will choose which main prisoners the user wants in the world.
 * 
 * @author Ainson Z and Bryan Y 
 * @version April 2024
 */
public class SelectWorld extends AllWorld
{
    private int numSelectedPrisoners;
    private List<SavedPrisoner> selectedPrisoners = new ArrayList<>();
    private List<String> serializedPrisonersState;
    
    
    // Colors
    private Color bgColor = new Color(119, 136, 153, 240);
    private Color borderColor = new Color(192, 192, 192);
    private Color transparentColor = new Color(0, 0, 0, 0);
    private Color textColor = new Color(250, 249, 246);
    
    // Info
    private SuperTextBox bruteInfo = new SuperTextBox("Buck: Brute", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    private SuperTextBox thiefInfo = new SuperTextBox("Wyatt: Thief", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    private SuperTextBox weaponsDealerInfo = new SuperTextBox("Leon: Weapons Dealer", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    private SuperTextBox scientistInfo = new SuperTextBox("Waldo: Scientist", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    private SuperTextBox explosiveExpertInfo = new SuperTextBox("Aron: Explosive Expert", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    private SuperTextBox builderInfo = new SuperTextBox("Reuben: Builder", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 28), true, 300, 5, borderColor);
    
    private GreenfootImage bgImage;

    /**
     * Constructor for objects of class SelectWorld.
     * Display each possible MC and their respective character information.
     */
    public SelectWorld()
    {
        super(AllWorld.WORLD_WIDTH, AllWorld.WORLD_HEIGHT, 1);
        setPaintOrder(Fade.class);
        numSelectedPrisoners = 0;

        SavedPrisoner mc1 = new SavedPrisoner("Buck", "None", 15, 1.8, 40, "Brute");
        SavedPrisoner mc2 = new SavedPrisoner("Wyatt", "Librarian", 8, 3.8, 40, "Thief");
        SavedPrisoner mc3 = new SavedPrisoner("Leon", "Metalworker", 8, 1.8, 40, "Weapons Dealer");
        SavedPrisoner mc4 = new SavedPrisoner("Waldo", "Cook", 8, 1.8, 40, "Scientist");
        SavedPrisoner mc5 = new SavedPrisoner("Aron", "Janitor", 8, 1.8, 40, "Explosive Expert");
        SavedPrisoner mc6 = new SavedPrisoner("Reuben", "Woodworker", 5, 1.8, 40, "Builder");

        addObject(mc1, 150, 250);
        addObject(mc2, 150, 550);
        addObject(mc3, 500, 250);
        addObject(mc4, 500, 550);
        addObject(mc5, 850, 250);
        addObject(mc6, 850, 550);
        
        addObject(bruteInfo, 250, 340);
        addObject(thiefInfo, 250, 640);
        addObject(weaponsDealerInfo, 600, 340);
        addObject(scientistInfo, 600, 640);
        addObject(explosiveExpertInfo, 950, 340);
        addObject(builderInfo, 950, 640);
        

        SelectButton button1 = new SelectButton(this, mc1);
        addObject(button1, 150 + 150, 250);
        SelectButton button2 = new SelectButton(this, mc2);
        addObject(button2, 150 + 150, 550);
        SelectButton button3 = new SelectButton(this, mc3);
        addObject(button3, 500 + 150, 250);
        SelectButton button4 = new SelectButton(this, mc4);
        addObject(button4, 500 + 150, 550);
        SelectButton button5 = new SelectButton(this, mc5);
        addObject(button5, 850 + 150, 250);
        SelectButton button6 = new SelectButton(this, mc6);
        addObject(button6, 850 + 150, 550);

        NextButton next = new NextButton("NextButton.png");
        addObject(next, 1000, 800);

        bgImage = new GreenfootImage("PrisonBg2.png");
        setBackground(bgImage);
    }

    /**
     * Return a list of the prisoners that were selected.
     * 
     * @return selectedPrisoners    The prisoners that were selected.
     */
    public List<SavedPrisoner> getSelectedPrisoners() {
        return selectedPrisoners;
    }
    
    /**
     * Return the amount of selected prisoners. (Max 4)
     * 
     * @return numSelectedPrisoners The number of prisoners that were selected.
     */
    public int getNumSelectedPrisoners() {
        return numSelectedPrisoners;
    }
    
    /**
     * Increment the number of selected prisoners by 1.
     */
    public void incrementNumSelectedPrisoners() {
        numSelectedPrisoners++;
    }
    
    /**
     * Decrement the number of selected prisoners by 1.
     */
    public void decrementNumSelectedPrisoners() {
        numSelectedPrisoners--;
    }
    
    /**
     * Add a prisoner to the selected prisoners.
     * 
     * @param prisoner  The prisoner to be added.
     */
    public void addSelectedPrisoner(SavedPrisoner prisoner) {
        selectedPrisoners.add(prisoner);
    }
    
    /**
     * Remove a prisoner from the selected prisoners.
     * 
     * @param prisoner The prisoner to be removed.
     */
    public void removeSelectedPrisoner(SavedPrisoner prisoner) {
        selectedPrisoners.remove(prisoner);
    }
    
    /**
     * Switch to the StatWorld and give it stats and values relating to the chosen MCs
     */
    public void transitionWorld() {
        serializedPrisonersState = saveSelectedPrisonersState();
        //System.out.println(serializedPrisonersState);
        Greenfoot.setWorld(new StatWorld(serializedPrisonersState));
    }

    /**
     * Used to save all the stats of each prisoner
     */
    public List<String> saveSelectedPrisonersState() {
        List<String> serializedDataList = new ArrayList<>();
        for (SavedPrisoner prisoner: selectedPrisoners) {
            String serializedData = prisoner.serializeState();
            serializedDataList.add(serializedData);
        }
        return serializedDataList;
    }
    
    /**
     * The act method of SelectWorld.
     * Once the number of selected prisoners equals 4 and the user wants to switch worlds, switch.
     */
    public void act() {
        if (numSelectedPrisoners == 4) {
            if(NextButton.getSwitchWorld()) {
                //System.out.print(getSelectedPrisoners());
                NextButton.resetSwitchWorld();
                transitionWorld();
            }

        }
    }
}
