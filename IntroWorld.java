import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * The IntroWorld displays a cutscene with 4 chosen MCs with a reasoning of why they want to escape.
 * 
 * @author Jamison H
 * @version April 2024
 */
public class IntroWorld extends AllWorld
{    
    // Background
    private GreenfootImage introBackground = new GreenfootImage("prison_cell.jpg");

    //Font
    private greenfoot.Font PixelFont;
    
    // Color 
    private Color bgColor = new Color(119, 136, 153);
    private Color borderColor = new Color(192, 192, 192);
    private Color transparentColor = new Color(0, 0, 0, 0);
    private Color textColor = new Color(250, 249, 246);
    
    // Info - only at start
    private SuperTextBox bruteInfo = new SuperTextBox("Brute", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private SuperTextBox thiefInfo = new SuperTextBox("Thief", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private SuperTextBox weaponsDealerInfo = new SuperTextBox("Weapons Dealer", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private SuperTextBox scientistInfo = new SuperTextBox("Scientist", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private SuperTextBox explosiveExpertInfo = new SuperTextBox("Explosive Expert", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private SuperTextBox builderInfo = new SuperTextBox("Builder", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 20), true, 150, 5, borderColor);
    private ArrayList<SuperTextBox> chosenInfo = new ArrayList<SuperTextBox>();
    
    // Dialogue, Speaker, General Text
    // 9 lines + 4 lines (one special line per chosen MC)
    private SuperTextBox[] dialogues = new SuperTextBox[13];
    private String[] splitName;
    // Guard + 4 chosen MCs
    private SuperTextBox[] speakers = new SuperTextBox[5];
    // Start-up message
    private SuperTextBox guideMessage;
    // Boxes
    private TempBox dialogueBox;
    private TempBox speakerBox;
    
    // Dialogue Control
    private int dialogueCounter = 0;
    private boolean switchedSpeaker = false;
    
    // Coordinates
    int[] xCoords = {150, 275, 400, 525}; 
    int[] yCoords = {615, 615, 615, 615}; 
    
    // Guard
    private Guard guard;
    private int actsLeft;
    Accessory tempAccessory;
    
    // Indicators
    Indicator[] bubbles = {new Indicator(50, 50), new Indicator(50, 50), new Indicator(50, 50), new Indicator(50, 50)};

    // InnerIcon
    InnerIcon[] exclamationMarks = {new InnerIcon(0, 30, 30), new InnerIcon(0, 30, 30), new InnerIcon(0, 30, 30), new InnerIcon(0, 30, 30)}; 
    InnerIcon[] questionMarks = {new InnerIcon(1, 30, 30), new InnerIcon(1, 30, 30), new InnerIcon(1, 30, 30), new InnerIcon(1, 30, 30)}; 

    private List<String> serializedPrisonersState;
    private ArrayList<String> savedMCs = new ArrayList<String>();
    
    private SavedPrisoner[] savedPrisoners;
    private List<String> MCs;
    /**
     * Constructor for IntroWorld that creates a start dialogue screen.
     * 
     * @param selectedPrisoners A list of values from StatWorld that give indicators of which MCs were chosen.
     */
    public IntroWorld(List<String> selectedPrisoners)
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        // The world will have prisoners in cells, no dialogue box or dialogue yet
        super(AllWorld.WORLD_WIDTH, AllWorld.WORLD_HEIGHT, 1); 
        introBackground.scale(1200, 700); // purposely scaled to lower than world height to add dialogue box in a logical place, will cause double background
        setBackground(introBackground);
        
        setPaintOrder(Fade.class);
        
        // Covering the double background
        SuperTextBox cover = new SuperTextBox("A", Color.BLACK, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf",80), true, 1200, 0, Color.BLACK);
        addObject(cover, 600, 780); 
        
        // Initalize font so the text isn't displayed as default font
        SimulationFont.initalizeFont();
        
        MCs = selectedPrisoners;
        
        fillSpeakersAndDialogue();
        displayCharacters();
        
        // Info
        int[] xCoords = {150, 275, 400, 525};
        int[] yCoords = {500, 450, 500, 450};
        
        // Top left, top right, bottom left, bottom right
        for(int i = 0; i < 4; i++){
            String name = selectedPrisoners.get(i);
            String[] splitName = name.split(",");
            if(splitName[5].equals("Thief")){ // Thief
                addObject(thiefInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(thiefInfo);
            } else if(splitName[5].equals("Brute")){ // Brute
                addObject(bruteInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(bruteInfo);
            } else if(splitName[5].equals("Scientist")){ // Scientist
                addObject(scientistInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(scientistInfo);
            } else if(splitName[5].equals("Weapons Dealer")){ // Weapons Dealer
                addObject(weaponsDealerInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(weaponsDealerInfo);
            } else if(splitName[5].equals("Explosive Expert")){ // Expolsive Expert
                addObject(explosiveExpertInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(explosiveExpertInfo);
            } else { // Builder
                addObject(builderInfo, xCoords[i], yCoords[i]);
                chosenInfo.add(builderInfo);
            }
        }
        
        
        savedPrisoners = new SavedPrisoner[4];
        int index = 0;
        for(String serializedData : selectedPrisoners) {
            savedPrisoners[index] = new SavedPrisoner(serializedData);
            index++;
        }
        
        guideMessage = new SuperTextBox("Click to start & advance dialogue", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 54), true, 768, 5, borderColor);
        addObject(guideMessage, 600, 775);
    }

    /**
     * The act method of IntroWorld.
     * New dialogue will show up upon every click registered on the screen
     * Note: Dialogue moves in one way, no going back to previous dialogue
     */
    public void act(){
        super.act();
        // before this it should be just prisoners in the cell
        if(Greenfoot.mouseClicked(null) && dialogueCounter == 0){
            sm.playSound("click");
            removeObject(guideMessage);
            // remove info
            for(SuperTextBox box : chosenInfo){
                removeObject(box);
            }
            // add new boxes for dialogue
            dialogueBox = new TempBox(1200, 150, bgColor, borderColor, 5);
            speakerBox = new TempBox(400, 50, bgColor, borderColor, 5);
            addObject(dialogueBox, 600, 780);
            addObject(speakerBox, 350, 700);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[0], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 1){
            sm.playSound("click");
            for(int i = 0; i < 4; i++){ // add question marks
                addObject(bubbles[i], xCoords[i], yCoords[i] - 100);
                addObject(questionMarks[i], xCoords[i], yCoords[i] - 105);
            }
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[0]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[1], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 2){
            sm.playSound("click");
            for(int i = 0; i < 4; i++){ // remove question marks
                removeObject(bubbles[i]);
                removeObject(questionMarks[i]);
            }
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[1]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[0], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 3){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[0]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[2], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 4){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[2]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[0], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 5){
            sm.playSound("click");
            actsLeft = 60; // fade out the guard
            for(int i = 0; i < 4; i++){ // add exclamation marks
                addObject(bubbles[i], xCoords[i], yCoords[i] - 100);
                addObject(exclamationMarks[i], xCoords[i], yCoords[i] - 105);
            }
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[0]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[3], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 6){
            sm.playSound("click");
            for(int i = 0; i < 4; i++){ //remove exclamation marks
                removeObject(bubbles[i]);
                removeObject(exclamationMarks[i]);
            }
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[3]);
            addObject(dialogues[dialogueCounter], 600, 775);
            if(splitName[0].equals("Brute")){ // so doesn't go against personality
                addObject(speakers[3], 350, 695);
                switchedSpeaker = true;
            } else {
                addObject(speakers[4], 350, 695);
            }
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 7){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            addObject(dialogues[dialogueCounter], 600, 775);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 8){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            addObject(dialogues[dialogueCounter], 600, 775);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 9){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            if(switchedSpeaker){
                removeObject(speakers[3]);
                switchedSpeaker = false;
            } else {
                removeObject(speakers[4]);
            }
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[1], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 10){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[1]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[2], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 11){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[2]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[3], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 12){ // end of dialogue
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[3]);
            addObject(dialogues[dialogueCounter], 600, 775);
            addObject(speakers[4], 350, 695);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 13){
            sm.playSound("click");
            removeObject(dialogues[dialogueCounter-1]);
            removeObject(speakers[4]);
            removeObject(dialogueBox);
            removeObject(speakerBox);
            SuperTextBox enterSimulation = new SuperTextBox("Click to enter main simulation", bgColor, textColor, SimulationFont.loadCustomFont("VT323-Regular.ttf", 54), true, 768, 5, borderColor);
            addObject(enterSimulation, 600, 775);
            dialogueCounter++;
        } else if (Greenfoot.mouseClicked(null) && dialogueCounter == 14){ //once world clicked, proceed to main simulation
            sm.playSound("click");
            Person.setIntro(false);
            switchWorld();
        }
        if(actsLeft <= 60 && actsLeft > 0){
            actsLeft--;
            guard.fade(actsLeft, 60);
            Accessory guardHat = guard.getAccessory();
            guardHat.setActsLeft(actsLeft);
            if(actsLeft == 0){
                removeObject(guardHat);
            }
        }
    }
    
    /**
     * Display the chosen characters along with a guard from the previous SelectWorld.
     */
    private void displayCharacters(){
        System.out.println("-----");
        System.out.println(savedMCs.get(0));
        System.out.println(savedMCs.get(1));
        System.out.println(savedMCs.get(2));
        System.out.println(savedMCs.get(3));
        
        MC[] displayMCs = {new MC(0, true, savedMCs.get(0)),new MC(1, true, savedMCs.get(1)), new MC(2, true, savedMCs.get(2)), new MC(3, true, savedMCs.get(3))};
        for(int i = 0; i < 4; i++){
            addObject(displayMCs[i], xCoords[i], yCoords[i]);
        }
        
        guard = new Guard(0, true);
        addObject(guard, 900, 615);
    }
    
    /**
     * Fill my arrays with text with possible speakers and dialogue.
     */
    private void fillSpeakersAndDialogue(){
        int index = 0;
        // Fill dialogues array with preset dialogue
        // Manadatory dialogue
        dialogues[0] = new SuperTextBox("Enjoy your last day here at the prison.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[1] = new SuperTextBox("Are we being released?", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[2] = new SuperTextBox("In your dreams, you all will be executed at dawn tomorrow.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[3] = new SuperTextBox("That isn't fair, why didn't we get an earlier notice?", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[4] = new SuperTextBox("Because I felt like it.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[5] = new SuperTextBox("HEY!!! COME BACK!!!", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[6] = new SuperTextBox("Look, surely we all don't want to die tomorrow.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[7] = new SuperTextBox("We should make a plan to escape.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        dialogues[8] = new SuperTextBox("I say we can gather materials by day and escape at night.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
        // Special lines for each character, will be chosen depending on which characters were selected as main 4
        for(int i = 9; i < 13; i++){
            String name = MCs.get(index);
            splitName = name.split(",");
            // Only add these values once
            savedMCs.add(splitName[5]);
            if(splitName[5].equals("Thief")){ // Thief
                dialogues[i] = new SuperTextBox("Sure, I'll steal something useful.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            } else if(splitName[5].equals("Brute")){ // Brute
                dialogues[i] = new SuperTextBox("Screw you all, I'm doing my own thing.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            } else if(splitName[5].equals("Scientist")){ // Scientist
                dialogues[i] = new SuperTextBox("Ok, I'll cook up some poisonous food.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            } else if(splitName[5].equals("Weapons Dealer")){ // Weapons Dealer
                dialogues[i] = new SuperTextBox("Very well, I'll create some makeshift weapons.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            } else if(splitName[5].equals("Explosive Expert")){ // Expolsive Expert
                dialogues[i] = new SuperTextBox("Its time to blow this place up!", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            } else { // Builder
                dialogues[i] = new SuperTextBox("Time to dig out of this place.", transparentColor, Color.BLACK, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), false, 1024, 0, transparentColor);
            }
            index ++;
        }
        index = 0;
        
        // Fill speakers array with preset speaker
        speakers[0] = new SuperTextBox("Guard", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);
        // Fill speakers array with chosen MCs
        for(int i = 1; i < 5; i++){
            String name = MCs.get(index);
            splitName = name.split(",");
            System.out.println("-----");
            System.out.println(splitName[5] + " " + index);
            if(splitName[5].equals("Thief")){
                speakers[i] = new SuperTextBox("Thief", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            } else if(splitName[5].equals("Brute")){
                speakers[i] = new SuperTextBox("Brute", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            } else if(splitName[5].equals("Scientist")){
                speakers[i] = new SuperTextBox("Scientist", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            } else if(splitName[5].equals("Weapons Dealer")){
                speakers[i] = new SuperTextBox("Weapons Dealer", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            } else if(splitName[5].equals("Explosive Expert")){
                speakers[i] = new SuperTextBox("Expolsive Expert", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            } else { // must be builder
                speakers[i] = new SuperTextBox("Builder", transparentColor, Color.WHITE, SimulationFont.loadCustomFont("VT323-Regular.ttf", 36), true, 256, 0, transparentColor);        
            }
            index++;
        }
        index = 0;
    }
    
    /**
     * Switch to the next world (MyWorld, main simulation).
     * Pass on a list of strings for the next world to know MCs selected and respective stats
     */
    public void switchWorld() {
        serializedPrisonersState = savePrisonersState();
        System.out.println(serializedPrisonersState);
        Greenfoot.setWorld(new MyWorld(serializedPrisonersState));
    }
    
    /**
     * Used to save all the stats of each prisoner
     */
    public List<String> savePrisonersState() {
        List<String> serializedDataList = new ArrayList<>();
        for (SavedPrisoner prisoner : savedPrisoners) {
            String serializedData = prisoner.serializeState();
            serializedDataList.add(serializedData);
        }
        
        return serializedDataList;
    }
}