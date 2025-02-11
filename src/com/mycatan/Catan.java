package com.mycatan;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;
import java.util.StringTokenizer;
//~50 methods
//CONTROLLER
public class Catan extends JFrame {//Controller

    //STATIC FIELDS
    public static final int STANDARD_BOARD = 0;
    public static final int SEAFARERS_BOARD = 1;
    public static final int FOGISLAND_BOARD = 2;
    private static final String[] gameModeStrings = {"standard" , "seafarers", "fogisland"};

    //FIELDS
    //Model
    private Board board;
    //View
    private BoardView bv;

    //CONTROL PANELS
    private JPanel np;
    private JPanel sp;
    private JPanel ep;
    private JPanel wp;

    //GUI BUTTONS
    private ButtonGroup sizeBG;
    private ButtonGroup typeBG;
    private JCheckBox debugBox;
    private JCheckBox randomBox;
    private JToggleButton flipPortButton;
    private JButton unflippedButton;

    //CONSTRUCTORS
    public Catan() {
        System.out.println("CATAN");
        setLayout(new BorderLayout());
        initControlPanels();
        newBoard();

        setVisible(true);
        setPreferredSize(setMinimumScreenSize());
        this.addComponentListener(new ResizeListener(this));
    }
    public Catan(String boardTypeString, String boardSizeString, String boardModifiersString) {
        int boardType=parseBoardType(boardTypeString);
        int boardSize=parseBoardSize(boardSizeString);
        int boardModifiers=parseBoardModifiers(boardModifiersString);

        System.out.println("CATAN");
        setLayout(new BorderLayout());
        initControlPanels();
        initGUIControls(boardType,boardSize,boardModifiers);
        newBoard(boardType,boardSize,boardModifiers);

        setVisible(true);
        setPreferredSize(setMinimumScreenSize());
        this.addComponentListener(new ResizeListener(this));
    }
    public Catan(String type) {
        StringTokenizer st = new StringTokenizer(type);
        int boardType=0, boardSize=0, boardFlags=0;
        if(st.hasMoreTokens()) {
            boardType = parseBoardType(st.nextToken());
        }
        if(st.hasMoreTokens()) {
            boardSize = parseBoardSize(st.nextToken());
        }
        if(st.hasMoreTokens()) {
            boardFlags = parseBoardModifiers(st.nextToken());
        }

        System.out.println("CATAN");
        setLayout(new BorderLayout());
        initControlPanels();
        initGUIControls(boardType,boardSize,boardFlags);
        newBoard(boardType,boardSize,boardFlags);

        setVisible(true);
        setPreferredSize(setMinimumScreenSize());
        this.addComponentListener(new ResizeListener(this));
    }
    public Catan(int boardType, int boardSize, int boardFlags) {
        System.out.println("CATAN");
        setLayout(new BorderLayout());
        initControlPanels();
        initGUIControls(boardType,boardSize,boardFlags);
        newBoard(boardType,boardSize,boardFlags);

        setVisible(true);
        setPreferredSize(setMinimumScreenSize());
        this.addComponentListener(new ResizeListener(this));
    }


    /**
     * <p>Initializes all 4 Control Panels.</p>
     */
    private void initControlPanels() {
        initEastPanel();
        initWestPanel();
        initNorthPanel();
        initSouthPanel();
    }


    /**
     * <p>Sets GUI's control State to match the initially loaded game.</p>
     * @param boardType use the value returned by {@code parseBoardType()}
     *               <p>or constants {@code STANDARD_BOARD}, {@code SEAFARERS_BOARD}, {@code FOGISLAND_BOARD}</p>
     * @param boardSize use the value returned by {@code parseBoardSize()}
     *               <p>or constants {@code Board.SMALL}, {@code Board.Large}</p>
     * @param boardModifiers use the value returned by {@code parseBoardModifiers()}
     *                    <p>or flags {@code Board.DEBUG} | {@code Board.RANDOM}</p>
     */
    private void initGUIControls(int boardType, int boardSize, int boardModifiers) {
        chooseButton(boardType,typeBG.getElements());
        chooseButton(boardSize,sizeBG.getElements());
        debugBox.setSelected((boardModifiers & Board.DEBUG) != 0);
        randomBox.setSelected((boardModifiers & Board.RANDOM) != 0);
    }


    /**
     * <p>Selects the button from {@code buttons} to match value.</p>
     * @param value the order of the button in the ButtonGroup
     * @param buttons an enumeration of the ButtonGroup
     */
    private void chooseButton(int value, Enumeration<AbstractButton> buttons){
        int i = 0;
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton)buttons.nextElement();
            if(i==value){
                button.setSelected(true);
                break;
            }
            i++;
        }
    }


    //CONTROL PANELS
    /**
     * <p>Initializes "Play", "Back", and "New" buttons.</p>
     */
    private void initNorthPanel() {
        np = new JPanel();
        np.setLayout(new FlowLayout(FlowLayout.RIGHT));
        np.setBackground(Tile.getBiomeColor(Tile.OCEAN));

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        panel.setLayout(new FlowLayout());
        addPlayButton(panel);
        addBackButton(panel);
        addNewButton(panel);
        np.add(panel);

        add(np, BorderLayout.NORTH);
    }
    /**
     * <p>Initializes "GameMode", "Modifiers", "Port" and "Hex" panels and their aggregates.</p>
     */
    private void initEastPanel() {
        ep = new JPanel();
        ep.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        ep.setBackground(Tile.getBiomeColor(Tile.OCEAN));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel control = new JPanel();
        control.setOpaque(false);
        control.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));

        addGameModePanel(control);
        addModBoxes(control);
        addHexButtons(control);
        addPortButtons(control);

        top.add(control);

        ep.add(top);
        add(ep, BorderLayout.EAST);
    }
    /**
     * <p>Initializes...</p>
     */
    private void initWestPanel() {
        wp = new JPanel();
        wp.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        wp.setBackground(Tile.getBiomeColor(Tile.OCEAN));
        add(wp, BorderLayout.WEST);
    }
    /**
     * <p>Initializes...</p>
     */
    private void initSouthPanel() {
        sp = new JPanel();
        wp.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        sp.setBackground(Tile.getBiomeColor(Tile.OCEAN));
        add(sp, BorderLayout.SOUTH);
    }


    //NORTH PANEL BUTTONS
    /**
     * <p>Adds "Play" button to the {@code parent} container and initializes its {@code ActionListener}.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addPlayButton(JPanel parent){
        JToggleButton toggleButton = new JToggleButton("Play");
        Dimension dim = new Dimension(180/3 - 10,22);
        toggleButton.setPreferredSize(dim);
        toggleButton.addActionListener(e -> {
            ep.setVisible(!toggleButton.isSelected());
            setMinimumScreenSize();
        });
        parent.add(toggleButton);
    }


    /**
     * <p>Adds "Back" button to the {@code parent} container and initializes its {@code ActionListener}</p>
     * @param parent the parent container the button is to be added to
     */
    private void addBackButton(JPanel parent){
        JButton button = new JButton("Back");
        button.setBackground(Tile.getBiomeColor(Tile.GOLD));
        Dimension dim = new Dimension(180/3 - 10,22);
        button.setPreferredSize(dim);
        parent.add(button);
    }


    /**
     * <p>Adds "New" button to the {@code parent} container and initializes its {@code ActionListener}.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addNewButton(JPanel parent){
        JButton button = new JButton("New");
        Dimension dim = new Dimension(180/3 - 10,22);
        button.setPreferredSize(dim);
        button.addActionListener(e -> newGame());
        parent.add(button);
    }


    //EAST PANEL BUTTONS
    //GAME MODE BUTTONS
    /**
     * <p>Adds game size and type panels to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addGameModePanel(JPanel parent){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Tile.getBiomeColor(Tile.WOOD));

        Label label = new Label("\tGame Mode:");
        panel.add(label, BorderLayout.NORTH);
        addSizeButtons(panel);
        addGameTypeButtons(panel);

        parent.add(panel);
    }


    /**
     * <p>Adds all size buttons to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addSizeButtons(JPanel parent){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        sizeBG = new ButtonGroup();
        panel.setLayout(new GridLayout(2,1));
        addSmallButton(panel,sizeBG);
        addLargeButton(panel,sizeBG);
        //panel.setBackground(Tile.getBiomeColor(Tile.SHEEP));
        parent.add(panel, BorderLayout.WEST);
    }


    /**
     * <p>Adds "Small" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     * @param group the button group the button is to be added to
     */
    private void addSmallButton(JPanel parent, ButtonGroup group){
        String actionCommand = "Small";
        JRadioButton rb = new JRadioButton(actionCommand);
        group.add(rb);
        rb.setActionCommand(actionCommand);
        parent.add(rb);
    }


    /**
     * <p>Adds "Large" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     * @param group the button group the button is to be added to
     */
    private void addLargeButton(JPanel parent, ButtonGroup group){
        String actionCommand = "Large";
        JRadioButton rb = new JRadioButton(actionCommand,true);
        group.add(rb);
        rb.setActionCommand(actionCommand);
        parent.add(rb);
    }


    /**
     * <p>Adds all game type buttons to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addGameTypeButtons(JPanel parent){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridLayout(3,1));
        typeBG = new ButtonGroup();
        addRadioButton(panel,typeBG,"Standard",false);
        addRadioButton(panel,typeBG,"Seafarers",true);
        addRadioButton(panel,typeBG,"FogIsland",false);
        parent.add(panel, BorderLayout.EAST);
    }


    /**
     * <p>Adds an arbitrary button to the {@code parent} container and set its {@code ActionCommand}.</p>
     * @param parent the parent container the button is to be added to
     * @param group the button group that the button is to be added to
     * @param actionCommand the button's text
     * @param selected true if the button is to be selected
     */
    private void addRadioButton(JPanel parent, ButtonGroup group, String actionCommand, boolean selected){
        JRadioButton rb = new JRadioButton(actionCommand, selected);
        group.add(rb);
        rb.setActionCommand(actionCommand);
        rb.addActionListener(e -> newGame());
        parent.add(rb);
    }


    //MODIFIER BUTTONS
    /**
     * <p>Adds all modifier check boxes to the {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addModBoxes(JPanel parent){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,2));

        Label label = new Label("\tModifiers:");
        panel.add(label);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer);

        addDebugBox(panel);
        addRandomBox(panel);

        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBackground(Tile.getBiomeColor(Tile.BRICK));
        parent.add(panel,BorderLayout.SOUTH);
    }


    /**
     * <p>Adds {@code debugBox} to the {@code parent} container and initializes its {@code ActionListener} and Hotkey.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addDebugBox(JPanel parent){
        debugBox = new JCheckBox("Debug");
        debugBox.addActionListener(e -> {
            if(debugBox.isSelected()){
                board.addFlags(Board.DEBUG);
            }
            else {
                board.removeFlags(Board.DEBUG);
            }
            board.updateTileDebugFlags();
        });
        addBoxHotKey("D", debugBox);
        parent.add(debugBox);
    }


    /**
     * <p>Adds {@code randomBox} to the {@code parent} container and initializes its Hotkey.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addRandomBox(JPanel parent){
        randomBox = new JCheckBox("Random");
        parent.add(randomBox);
        addBoxHotKey("R", randomBox);
    }


    /**
     * <p>Adds a hotkey shortcut to {@code box}.</p>
     * @param key Shortcut string.
     * @param box Box to link too.
     */
    public void addBoxHotKey(String key, JCheckBox box){
        KeyStroke ksD = KeyStroke.getKeyStroke(key);
        // Get the InputMap and ActionMap of the checkbox
        InputMap imD = box.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap amD = box.getActionMap();
        // Define the action that will be triggered by the hotkey
        Action HotKey = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                box.setSelected(!box.isSelected());
                // Fire the action listener manually
                for (ActionListener listener : box.getActionListeners()) {
                    listener.actionPerformed(new ActionEvent(box, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        };
        // Map the keystroke to the action
        imD.put(ksD, "toggle");
        amD.put("toggle", HotKey);
    }


    //PORT MANIPULATION BUTTONS
    /**
     * <p>Adds all port manipulation buttons to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addPortButtons(JPanel parent){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5,1));
        panel.setBackground(Tile.getBiomeColor(Tile.ROCK));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //panel.setOpaque(false);

        Label label = new Label("\tPort:");
        panel.add(label);
        addPortReshuffleButton(panel);
        addPortReplaceButton(panel);
        addFixPortButton(panel);
        addFlipPortButton(panel);

        parent.add(panel, BorderLayout.EAST);
    }


    /**
     * <p>Adds "Reshuffle Ports" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addPortReshuffleButton(JPanel parent) {
        JButton button = new JButton("Reshuffle");
        button.addActionListener(e -> {
            reshufflePorts();
            revalidate();
            repaint();
        });
        parent.add(button);
    }


    /**
     * <p>Adds "Replace Ports button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addPortReplaceButton(JPanel parent) {
        JButton button = new JButton("Replace");
        button.addActionListener(e -> replacePorts());
        parent.add(button);
    }


    /**
     * <p>Adds "Fix Ports" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addFixPortButton(JPanel parent) {
        JButton fixPortButton = new JButton("Fix");
        fixPortButton.addActionListener(e -> fixPorts());
        parent.add(fixPortButton);
    }


    /**
     * <p>Adds "Flip Ports" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addFlipPortButton(JPanel parent) {
        flipPortButton = new JToggleButton("Show");
        flipPortButton.addActionListener(e -> {
            boolean show = flipPortButton.isSelected();
            updateFlipPortButtonText(show);
            flipPorts(show);
        });
        parent.add(flipPortButton);
    }


    /**
     * <p>Updates {@code flipPortButton}'s text to match the state of the button.</p>
     */
    private void updateFlipPortButtonText(boolean show){
        flipPortButton.setText(show ? "Hide" : "Show");
    }


    //HEX RESHUFFLE BUTTONS
    /**
     * <p>Adds all hex reshuffle buttons to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addHexButtons(JPanel parent) {
        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(5,1));
        panel.setBackground(Tile.getBiomeColor(Tile.HAY));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //panel.setOpaque(false);

        Label label = new Label("\tHex:");
        panel.add(label);

        addResourceReshuffleButton(panel);
        addTokenReshuffleButton(panel);
        addHexReshuffleButton(panel);
        addUnflippedReshuffleButton(panel);

        parent.add(panel, BorderLayout.EAST);
    }


    /**
     * <p>Adds "Reshuffle Resources" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addResourceReshuffleButton(JPanel parent) {
        JButton button = new JButton("Reshuffle Resources");
        button.addActionListener(e -> reshuffleResources());
        parent.add(button);
    }


    /**
     * <p>Adds "Reshuffle Numbers" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addTokenReshuffleButton(JPanel parent) {
        JButton button = new JButton("Reshuffle Numbers");
        button.addActionListener(e -> reshuffleTokens());
        parent.add(button);
    }


    /**
     * <p>Adds "Reshuffle Both" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addHexReshuffleButton(JPanel parent) {
        JButton button = new JButton("Reshuffle Both");
        button.addActionListener(e -> reshuffleHexes());
        parent.add(button);
    }


    /**
     * <p>Adds "Reshuffle Unflipped" button to {@code parent} container.</p>
     * @param parent the parent container the button is to be added to
     */
    private void addUnflippedReshuffleButton(JPanel parent) {
        unflippedButton = new JButton("Reshuffle Unflipped");
        unflippedButton.addActionListener(e -> reshuffleUnflipped());
        parent.add(unflippedButton);
    }


    /**
     * <p>Enable {@code unflippedButton} only if the current board implements {@code IUnflippable}
     * and the "Random" modifier is off.</p>
     */
    private void setUnflippedButtonState(){
        boolean isUnflippable = board instanceof IFlippable;
        unflippedButton.setEnabled(isUnflippable && !board.getRandomFlag());
    }


    //BOARD CONTROL METHODS
    //Board Creation
    /**
     * <p>Initializes and displays a new board with size, type, and modifiers specified by GUI settings</p>
     */
    public void newBoard() {
        int boardType = parseBoardType(typeBG.getSelection().getActionCommand());
        int boardSize = parseBoardSize(sizeBG.getSelection().getActionCommand());
        int boardModifiers = (debugBox.isSelected()?Board.DEBUG:0)
                | (randomBox.isSelected()?Board.RANDOM:0);
        newBoard(boardType,boardSize,boardModifiers);
    }


    /**
     * <p>Initializes and displays a new board with size, type, and modifiers specified by function arguments.</p>
     * @param boardType use the value returned by {@code parseBoardType()}
     *                  <p>or constants {@code STANDARD_BOARD}, {@code SEAFARERS_BOARD}, {@code FOGISLAND_BOARD}</p>
     * @param boardSize use the value returned by {@code parseBoardSize()}
     *                  <p>or constants {@code Board.SMALL}, {@code Board.Large}</p>
     * @param boardModifiers use the value returned by {@code parseBoardModifiers()}
     *                  <p>or flags {@code Board.DEBUG} | {@code Board.RANDOM}</p>
     */
    public void newBoard(int boardType, int boardSize, int boardModifiers){
        switch (boardType) {
            case STANDARD_BOARD:
                board = new StandardBoard(boardSize, boardModifiers);
                break;
            case SEAFARERS_BOARD:
                board = new SeafarersBoard(boardSize, boardModifiers);
                break;
            case FOGISLAND_BOARD:
                board = new FogIslandBoard(boardSize, boardModifiers);
        }
        if(board.getDebugFlag()) {
            printBoardData();
        }
        initBoardView();
        resizeWindow();
        Dimension minimumSize = setMinimumScreenSize();
        setPreferredSize(minimumSize);
        setUnflippedButtonState();
    }


    /**
     * <p>Initializes and displays a new {@code BoardView} object in the {@code JFrame}.</p>
     */
    private void initBoardView() {
        bv = new BoardView(board);
        bv.displayBoard();
        add(bv, BorderLayout.CENTER);
        pack();
    }


    /**
     * <p>Removes the current board from visibility, initializes/displays a new board with {@code size}, {@code type}, and {@code modifiers} specified by GUI settings</p>
     * <p>Resets/Sets any necessary GUI controls.(i.e. "PortFlip", "Shuffle Unflipped")</p>
     */
    private void newGame(){
        remove(bv);
        flipPortButton.setSelected(false);
        updateFlipPortButtonText(flipPortButton.isSelected());
        newBoard();
        //Enable button only if Unflippable board with RANDOM disabled
        setUnflippedButtonState();
    }


    //Hex
    /**
     * <p>Reshuffles each shuffleable hex's resource biome and number token.</p>
     */
    public void reshuffleHexes() {
        board.initHexSpiral(true);
        board.shuffleHexes();
    }


    /**
     * <p>Reshuffles each shuffleable hex's resource biome.</p>
     */
    public void reshuffleResources(){
        board.shuffleHexes(new BiomeCommand());
    }


    /**
     * <p>Reshuffles each shuffleable hex's number token.</p>
     */
    public void reshuffleTokens(){
        if(board.getRandomFlag()){
            board.shuffleTokens();
        } else{
            board.initHexSpiral(true);
        }
        board.shuffleHexes(null);
    }


    /**
     * <p>Reshuffles each unflipped hex's resource biome and number token.</p>
     */
    public void reshuffleUnflipped(){
        if(board instanceof IFlippable){
            ((IFlippable)board).shuffleUnflippedHexes();
        }
    }


    //Port
    /**
     * <p>Reshuffles each port's resource biome.</p>
     */
    public void reshufflePorts() {
        board.shufflePorts();
    }


    /**
     * <p>Replaces the location of ports.</p>
     */
    public void replacePorts() {
        if (board.getRandomFlag()) {
            fixPorts();
            return;
        }
        flipPortButton.setSelected(false);
        updateFlipPortButtonText(flipPortButton.isSelected());
        board.clearPorts();
        board.initHexSpiral(true);
        board.findValidPorts();
        board.shufflePorts();
        bv.revalidate();
        bv.repaint();
    }


    /**
     * <p>Reruns the port finding algorithm to relocate all valid port locations.</p>
     * <p>Useful when moving ocean hexes.</p>
     */
    public void fixPorts(){
        flipPortButton.setSelected(false);
        bv.removePorts();

        board.initHexSpiral(true);
        board.findAllPorts();
        bv.placePorts();
        resizeWindow();

        board.findValidPorts();
        board.shufflePorts();
    }


    /**
     * <p>Sets each port's flipped status to {@code isShown}.</p>
     * @param isShown the new flipped status
     */
    public void flipPorts(boolean isShown){
        board.flipPorts(isShown);
    }


    //STRING PARSING METHODS
    /**
     * <p>Longest matching {@code type} string.</p>
     * @param typeString Standard, Seafarers, Fogisland
     * @return <p>Standard -> 0</p>
     *         <p>Seafarers -> 1</p>
     *         <p>Fogisland -> 2</p>
     */
    private int parseBoardType(String typeString){
        typeString = typeString.toLowerCase();
        int[] counts = new int[gameModeStrings.length];
        int min = Math.min(typeString.length(), gameModeStrings[0].length());
        for(int i = 0; i < min; i++) {
            char val = typeString.charAt(i);
            for (int j = 0; j < gameModeStrings.length; j++) {
                if (val == gameModeStrings[j].charAt(i)) {
                    counts[j]++;
                }
            }
        }
        int max = Math.max(Math.max(counts[0],counts[1]),counts[2]);
        int type = 0;
        while(type < counts.length && counts[type] != max){
            type++;
        }
        return type;
    }


    /**
     * <p>Longest matching {@code size} string.</p>
     * @param size Small, Large
     * @return Small -> 0, Large -> 1
     */
    private int parseBoardSize(String size){
        size = size.toLowerCase();
        return switch (size.charAt(0)) {
            case 's' -> Board.SMALL_BOARD;
            case 'l' -> Board.LARGE_BOARD;
            default -> 0;
        };
    }


    /**
     * <p>Longest matching {@code modifiers} string.</p>
     * @param modifiers "-(R)(D)"
     * @return R -> random, D -> debug
     */
    private int parseBoardModifiers(String modifiers){
        modifiers = modifiers.toLowerCase();
        int flag = 0;
        if(modifiers.charAt(0) != '-'){
            return flag;
        }
        if(modifiers.contains("d")){
            flag |= Board.DEBUG;
        }
        if(modifiers.contains("r")){
            flag |= Board.RANDOM;
        }
        return flag;
    }

    //TEST METHODS
    /**
     * <p>Console based test method for early stage development</p>
     */
    public void printBoardData(){
        System.out.print(board.toString());
        System.out.print(board.getSpiralString(new BiomeCommand()));
        System.out.println(board.getHexGridString(new TokenCommand()));
        System.out.println(board.getHexGridString(new IdCommand()));
        System.out.println(board.getHexGridString(new BiomeCommand()));
        System.out.println(board.getHexGridString(new TypeCommand()));

        System.out.println();
    }

    //RESIZE METHODS
    /**
     * <p>Calculates the minimum screen size using control Panels and board view sizes.</p>
     * @return The Minimum screen size
     */
    private Dimension setMinimumScreenSize(){
        Dimension screenSize = new Dimension(
                bv.getWorldSize().width + ((ep.isVisible())?ep.getWidth():0) + wp.getWidth(),
                bv.getWorldSize().height + np.getHeight() + sp.getHeight()
        );

        setMinimumSize(screenSize);
        return screenSize;
    }
    /**
     * <p>Defines window/world size ratio for X and Y direction
     * and passes minimum value to the {@code BoardView} object for resizing</p>
     */
    public void resizeWindow(){
        Dimension worldSize = bv.getWorldSize();
        Dimension windowSize = bv.getSize();
        double x = (double)windowSize.width / worldSize.width;
        double y = (double)windowSize.height / (worldSize.height - getInsets().top);
        double min = Math.min(x,y);
        bv.resize(min);
        revalidate();
        repaint();
    }
    record ResizeListener(Catan catan) implements ComponentListener{

        @Override
        public void componentResized(ComponentEvent e) {
            catan.resizeWindow();
        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }
}
