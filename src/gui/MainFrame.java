package gui;

import algorithms.*;
import experiments.Experiment;
import experiments.ExperimentEvent;
import ga.GeneticAlgorithm;
import ga.geneticoperators.*;
import ga.selectionmethods.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;

import random.RandomAlgorithm;
import stockingproblem.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import random.*;

public class MainFrame extends JFrame implements AlgorithmListener {

    private static final long serialVersionUID = 1L;
    public static final int PANEL_SIZE = 250;
    private StockingProblem warehouse;
    private Algorithm<StockingProblemIndividual, StockingProblem> algorithm;
    private StockingProblemExperimentsFactory experimentsFactory;
    private PanelTextArea problemPanel;
    PanelTextArea bestIndividualPanel;
    private PanelParameters panelParameters = new PanelParameters();
    private JButton buttonDataSet = new JButton("Data set");
    private JButton buttonRun = new JButton("Run");
    private JButton buttonRun2 = new JButton("Run experiments Algorithm");
    private JButton buttonStop = new JButton("Stop");
    private JButton buttonExperiments = new JButton("Experiments");
    private JButton buttonRunExperiments = new JButton("Run experiments");
    private JTextField textFieldExperimentsStatus = new JTextField("", 10);
    private XYSeries seriesBestIndividual;
    private XYSeries seriesAverage;
    private Image image;
    private SwingWorker<Void, Void> worker;
    private JPanel simulationPanel = new JPanel();

    private long starttime;

    public MainFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void jbInit() throws Exception {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("IA Project");

        //North Left Panel
        JPanel panelNorthLeft = new JPanel(new BorderLayout());
        panelNorthLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        panelNorthLeft.add(panelParameters, java.awt.BorderLayout.WEST);
        JPanel panelButtons = new JPanel();
        panelButtons.add(buttonDataSet);
        buttonDataSet.addActionListener(new ButtonDataSet_actionAdapter(this));
        panelButtons.add(buttonRun);
        buttonRun.setEnabled(false);
        buttonRun.addActionListener(new ButtonRun_actionAdapter(this));
        panelButtons.add(buttonStop);
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new ButtonStop_actionAdapter(this));
        panelNorthLeft.add(panelButtons, java.awt.BorderLayout.SOUTH);

        //North Right Panel - Chart creation
        seriesBestIndividual = new XYSeries("Best");
        seriesAverage = new XYSeries("Average");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesBestIndividual);
        dataset.addSeries(seriesAverage);
        JFreeChart chart = ChartFactory.createXYLineChart("Evolution", // Title
                "iteration/generation", // x-axis Label
                "fitness", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 250));



        //North Panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(panelNorthLeft, java.awt.BorderLayout.WEST);
        northPanel.add(chartPanel, java.awt.BorderLayout.CENTER);

        //Center panel
        problemPanel = new PanelTextArea("Problem data: ", 20, 40);
        bestIndividualPanel = new PanelTextArea("Best solution: ", 20, 40);
        bestIndividualPanel.setFont(new Font("monospaced", Font.PLAIN, 12));
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(problemPanel, java.awt.BorderLayout.WEST);
        centerPanel.add(bestIndividualPanel, java.awt.BorderLayout.CENTER);

        //South Panel
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        southPanel.add(buttonRun2);
        buttonRun2.addActionListener(new ButtonRun2_actionAdapter(this));
        southPanel.add(buttonExperiments);
        buttonExperiments.addActionListener(new ButtonExperiments_actionAdapter(this));
        southPanel.add(buttonRunExperiments);
        buttonRunExperiments.setEnabled(false);
        buttonRunExperiments.addActionListener(new ButtonRunExperiments_actionAdapter(this));
        southPanel.add(new JLabel("Status: "));
        southPanel.add(textFieldExperimentsStatus);
        textFieldExperimentsStatus.setEditable(false);

        simulationPanel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        //North Panel
        simulationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        //Global structure
        JPanel globalPanel = new JPanel(new BorderLayout());
        globalPanel.add(northPanel, java.awt.BorderLayout.NORTH);
        globalPanel.add(centerPanel, java.awt.BorderLayout.CENTER);
        globalPanel.add(southPanel, java.awt.BorderLayout.SOUTH);
        globalPanel.add(simulationPanel, BorderLayout.SOUTH);

        this.getContentPane().add(globalPanel);

        pack();
    }

    public void buttonDataSet_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dataSet = fc.getSelectedFile();
                warehouse = StockingProblem.buildWarehouse(dataSet);
                problemPanel.textArea.setText(warehouse.toString());
                problemPanel.textArea.setCaretPosition(0);
                buttonRun.setEnabled(true);
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void jButtonRun_actionPerformed(ActionEvent e) {

        try {
            if (warehouse == null) {
                JOptionPane.showMessageDialog(this, "You must first choose a problem", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bestIndividualPanel.textArea.setText("");
            seriesBestIndividual.clear();
            seriesAverage.clear();
            switch (panelParameters.getAlgorithm()) {
                case 0:
                    algorithm = new GeneticAlgorithm<StockingProblemIndividual, StockingProblem>(
                            Integer.parseInt(panelParameters.jTextFieldN.getText()),
                            Integer.parseInt(panelParameters.jTextFieldGenerations.getText()),
                            panelParameters.getSelectionMethod(),
                            panelParameters.getRecombinationMethod(),
                            panelParameters.getMutationMethod(),
                            new Random(Integer.parseInt(panelParameters.jTextFieldSeed.getText())));

                    System.out.println(algorithm);
                    break;

                case 1:
                    algorithm = new RandomAlgorithm<StockingProblemIndividual, StockingProblem>(
                            Integer.parseInt(panelParameters.jTextFieldGenerations.getText()),
                            new Random(Integer.parseInt(panelParameters.jTextFieldSeed.getText())));
                    break;

            }
            algorithm.addListener(this);

            manageButtons(false, false, true, false, false);

            worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    try {
                        algorithm.run(warehouse);
                        bestIndividualPanel.textArea.append("\n\nTime elapsed: "+(System.currentTimeMillis()-starttime));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                    return null;
                }

                @Override
                public void done() {
                    manageButtons(true, true, false, true, experimentsFactory != null);
                }
            };
            starttime = System.currentTimeMillis();
            worker.execute();
        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void jButtonRun2_actionPerformed(ActionEvent e) {

        try {
            bestIndividualPanel.textArea.setText("");
            seriesBestIndividual.clear();
            seriesAverage.clear();
            JFileChooser fc = new JFileChooser(new java.io.File("."));
            int returnVal = fc.showOpenDialog(this);

            try {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    runAlg run = new runAlg(fc.getSelectedFile());
                    manageButtons(true, warehouse != null, false, true, true);
                }
            } catch (IOException e1) {
                e1.printStackTrace(System.err);
            } catch (java.util.NoSuchElementException e2) {
                JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public void iterationEnded(AlgorithmEvent e) {
        Algorithm<StockingProblemIndividual, StockingProblem> source = e.getSource();
        bestIndividualPanel.textArea.setText(source.getGlobalBest().toString());
        seriesBestIndividual.add(source.getIteration(), source.getGlobalBest().getFitness());
        if (source instanceof GeneticAlgorithm)
            seriesAverage.add(source.getIteration(), ((GeneticAlgorithm)source).getAverageFitness());
        if (worker.isCancelled()) {
            e.setStopped(true);
        }


    }

    @Override
    public void runEnded(AlgorithmEvent e) {
    }

    public void jButtonStop_actionPerformed(ActionEvent e) {
        worker.cancel(true);
    }

    public void buttonExperiments_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                experimentsFactory = new StockingProblemExperimentsFactory(fc.getSelectedFile());
                manageButtons(true, warehouse != null, false, true, true);
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buttonRunExperiments_actionPerformed(ActionEvent e) {

        manageButtons(false, false, false, false, false);
        textFieldExperimentsStatus.setText("Running");

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {
                    while (experimentsFactory.hasMoreExperiments()) {
                        try {

                            Experiment experiment = experimentsFactory.nextExperiment();
                            experiment.run();

                        } catch (IOException e1) {
                            e1.printStackTrace(System.err);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }

            @Override
            public void done() {
                manageButtons(true, warehouse != null, false, true, false);
                textFieldExperimentsStatus.setText("Finished");
            }
        };
        worker.execute();
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {
    }

    private void manageButtons(
            boolean dataSet,
            boolean run,
            boolean stopRun,
            boolean experiments,
            boolean runExperiments) {

        buttonDataSet.setEnabled(dataSet);
        buttonRun.setEnabled(run);
        buttonStop.setEnabled(stopRun);
        buttonExperiments.setEnabled(experiments);
        buttonRunExperiments.setEnabled(runExperiments);
    }

    public void environmentUpdated(Algorithm<StockingProblemIndividual, StockingProblem> source) {
        Color colors[] = {Color.YELLOW, Color.GREEN, Color.CYAN, Color.RED, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.LIGHT_GRAY, Color.BLACK, Color.BLUE};
        Graphics g = image.getGraphics();
        g.clearRect(0, 0, PANEL_SIZE, PANEL_SIZE);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, PANEL_SIZE, PANEL_SIZE);

        if (warehouse != null) {
            /*
            ArrayList<ArrayList<Integer>> ordersForTruck = source.getGlobalBest().getOrdersForTruck();

            for (int i = 0; i < ordersForTruck.size(); i++) {
                g.setColor(colors[i]);

                if (ordersForTruck.get(i).size() != 0) {
                    g.drawLine((int) distributionProblem.getWarehousePosition().getX(), (int) distributionProblem.getWarehousePosition().getY(), orders.get(ordersForTruck.get(i).get(0) - 1).getPosition().x, orders.get(ordersForTruck.get(i).get(0) - 1).getPosition().y);
                    g.drawLine((int) distributionProblem.getWarehousePosition().getX(), (int) distributionProblem.getWarehousePosition().getY(), orders.get(ordersForTruck.get(i).get(ordersForTruck.get(i).size() - 1) - 1).getPosition().x, orders.get(ordersForTruck.get(i).get(ordersForTruck.get(i).size() - 1) - 1).getPosition().y);
                }

                for (int j = 0; j < ordersForTruck.get(i).size() - 1; j++) {
                    g.drawLine(orders.get(ordersForTruck.get(i).get(j) - 1).getPosition().x, orders.get(ordersForTruck.get(i).get(j) - 1).getPosition().y, orders.get(ordersForTruck.get(i).get(j + 1) - 1).getPosition().x, orders.get(ordersForTruck.get(i).get(j + 1) - 1).getPosition().y);
                }

            }
            g.setColor(Color.RED);
            g.fillOval((int) distributionProblem.getWarehousePosition().getX() - 3, (int) distributionProblem.getWarehousePosition().getY() - 3, 6, 6);

            g = simulationPanel.getGraphics();
            g.drawImage(image, 0, 0, null);
             */
        }

    }

}

class ButtonDataSet_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonDataSet_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonDataSet_actionPerformed(e);
    }
}

class ButtonRun_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRun_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun_actionPerformed(e);
    }
}

class ButtonRun2_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRun2_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun2_actionPerformed(e);
    }
}

class ButtonStop_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonStop_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStop_actionPerformed(e);
    }
}

class ButtonExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonExperiments_actionPerformed(e);
    }
}

class ButtonRunExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRunExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonRunExperiments_actionPerformed(e);
    }
}

class PanelTextArea extends JPanel {

    JTextArea textArea;

    public PanelTextArea(String title, int rows, int columns) {
        textArea = new JTextArea(rows, columns);
        setLayout(new BorderLayout());
        add(new JLabel(title), java.awt.BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        add(scrollPane);
    }
}

class PanelAtributesValue extends JPanel {

    protected String title;
    protected List<JLabel> labels = new ArrayList<>();
    protected List<JComponent> valueComponents = new ArrayList<>();

    public PanelAtributesValue() {
    }

    protected void configure() {

        //for(JComponent textField : textFields)
        //textField.setHorizontalAlignment(SwingConstants.RIGHT);

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);

        //addLabelTextRows

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHEAST;
        Iterator<JLabel> itLabels = labels.iterator();
        Iterator<JComponent> itTextFields = valueComponents.iterator();

        while (itLabels.hasNext()) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            add(itLabels.next(), c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            add(itTextFields.next(), c);
        }
    }
}

class PanelParameters extends PanelAtributesValue {

    public static final int TEXT_FIELD_LENGHT = 7;
    public static final String SEED = "1";
    public static final String POPULATION_SIZE = "100";
    public static final String GENERATIONS = "100";
    public static final String TOURNAMENT_SIZE = "2";
    public static final String PROB_RECOMBINATION = "0.7";
    public static final String PROB_MUTATION = "0.1";
    JTextField jTextFieldSeed = new JTextField(SEED, TEXT_FIELD_LENGHT);
    JTextField jTextFieldN = new JTextField(POPULATION_SIZE, TEXT_FIELD_LENGHT);
    JTextField jTextFieldGenerations = new JTextField(GENERATIONS, TEXT_FIELD_LENGHT);
    String[] selectionMethods = {"Tournament", "Roulette wheel"};
    JComboBox jComboBoxSelectionMethods = new JComboBox(selectionMethods);
    JTextField jTextFieldTournamentSize = new JTextField(TOURNAMENT_SIZE, TEXT_FIELD_LENGHT);
    String[] recombinationMethods = {"PMX", "CX", "CROSSOVER"};
    JComboBox jComboBoxRecombinationMethods = new JComboBox(recombinationMethods);
    JTextField jTextFieldProbRecombination = new JTextField(PROB_RECOMBINATION, TEXT_FIELD_LENGHT);
    String[] mutationMethods = {"Insert", "Swap", "Inversion", "Scramble"};
    JComboBox jComboBoxMutationMethods = new JComboBox(mutationMethods);
    JTextField jTextFieldProbMutation = new JTextField(PROB_MUTATION, TEXT_FIELD_LENGHT);
    String[] algorithms = {"GA", "Random"};
    JComboBox jComboBoxAlgorithms = new JComboBox(algorithms);

    public PanelParameters() {
        title = "Genetic algorithm parameters";

        labels.add(new JLabel("Seed: "));
        valueComponents.add(jTextFieldSeed);
        jTextFieldSeed.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Population size: "));
        valueComponents.add(jTextFieldN);
        jTextFieldN.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("# of gen/iterations: "));
        valueComponents.add(jTextFieldGenerations);
        jTextFieldGenerations.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Selection method: "));
        valueComponents.add(jComboBoxSelectionMethods);
        jComboBoxSelectionMethods.addActionListener(new JComboBoxSelectionMethods_ActionAdapter(this));

        labels.add(new JLabel("Tournament size: "));
        valueComponents.add(jTextFieldTournamentSize);
        jTextFieldTournamentSize.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Recombination method: "));
        valueComponents.add(jComboBoxRecombinationMethods);

        labels.add(new JLabel("Recombination prob.: "));
        valueComponents.add(jTextFieldProbRecombination);

        labels.add(new JLabel("Mutation method: "));
        valueComponents.add(jComboBoxMutationMethods);

        labels.add(new JLabel("Mutation prob.: "));
        valueComponents.add(jTextFieldProbMutation);

        labels.add(new JLabel("Algorithms: "));
        valueComponents.add(jComboBoxAlgorithms);

        configure();
    }

    public void actionPerformedSelectionMethods(ActionEvent e) {
        jTextFieldTournamentSize.setEnabled(jComboBoxSelectionMethods.getSelectedIndex() == 0);
    }

    public SelectionMethod<StockingProblemIndividual, StockingProblem> getSelectionMethod() {
        switch (jComboBoxSelectionMethods.getSelectedIndex()) {
            case 0:
                return new Tournament<>(
                        Integer.parseInt(jTextFieldN.getText()),
                        Integer.parseInt(jTextFieldTournamentSize.getText()));
            case 1:
                return new RouletteWheel<>(
                        Integer.parseInt(jTextFieldN.getText()));
        }
        return null;
    }

    public int getAlgorithm() {
        return jComboBoxAlgorithms.getSelectedIndex();
    }

    public Recombination<StockingProblemIndividual, StockingProblem> getRecombinationMethod() {

        double recombinationProb = Double.parseDouble(jTextFieldProbRecombination.getText());

        switch (jComboBoxRecombinationMethods.getSelectedIndex()) {
            case 0:
                return new RecombinationPartialMapped<>(recombinationProb);
            case 1:
                return new RecombinationCycleCrossover<>(recombinationProb);
            case 2:
                return new RecombinationCrossover<>(recombinationProb);
        }
        return null;
    }


    public Mutation<StockingProblemIndividual, StockingProblem> getMutationMethod() {

        double mutationProb = Double.parseDouble(jTextFieldProbMutation.getText());

        switch (jComboBoxMutationMethods.getSelectedIndex()) {
            case 0:
                return new MutationInsert<>(mutationProb);
            case 1:
                return new MutationSwap<>(mutationProb);
            case 2:
                return new MutationInversion<>(mutationProb);
            case 3:
                return new MutationScramble<>(mutationProb);
        }
        return null;
    }
}

class JComboBoxSelectionMethods_ActionAdapter implements ActionListener {

    final private PanelParameters adaptee;

    JComboBoxSelectionMethods_ActionAdapter(PanelParameters adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.actionPerformedSelectionMethods(e);
    }
}


class IntegerTextField_KeyAdapter implements KeyListener {

    final private MainFrame adaptee;

    IntegerTextField_KeyAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            e.consume();
        }
    }
}
