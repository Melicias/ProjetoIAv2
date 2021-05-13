package random;

import algorithms.Algorithm;
import experiments.Parameter;
import stockingproblem.StockingProblem;
import stockingproblem.StockingProblemIndividual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class runAlg {
    protected int numRuns;
    protected HashMap<String, Parameter> parameters;
    protected Parameter[] orderedParametersVector;
    private StockingProblem problem;

    public runAlg(File configFile) throws IOException {
        readParametersFile(configFile);

        runTheAlgorithms();
    }

    private void readParametersFile(File file) throws IOException {
        java.util.Scanner scanner = new java.util.Scanner(file);

        List<String> lines = new ArrayList<>(100);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (!s.equals("") && !s.startsWith("//") && !s.startsWith("Statistic")) {
                lines.add(s);
            }
        }

        parameters = new HashMap<>(lines.size() * 2);
        orderedParametersVector = new Parameter[lines.size()];
        int i = 0;
        for (String line : lines) {
            String[] tokens = line.split(":|,");
            String[] parameterValues = new String[tokens.length - 1];
            String parameterName = tokens[0];
            for (int j = 1; j < tokens.length; j++) {
                parameterValues[j - 1] = tokens[j].trim();
            }

            Parameter parameter = new Parameter(parameterName, parameterValues);
            parameters.put(parameterName, parameter);
            orderedParametersVector[i++] = parameter;
        }

//        //DEBUG
//        for (i = 0; i < orderedParametersVector.length; i++) {
//            System.out.print(orderedParametersVector[i].name + ": ");
//            for (int j = 0; j < orderedParametersVector[i].getNumberOfValues(); j++) {
//                System.out.print(orderedParametersVector[i].values[j]);
//                if(j < orderedParametersVector[i].getNumberOfValues() - 1){
//                    System.out.print(", ");
//                }
//            }
//            System.out.println();
//        }
    }




    private void runTheAlgorithms() throws IOException {
        numRuns = Integer.parseInt(getParameterValue("Runs"));
        int maxGenerations = Integer.parseInt(getParameterValue("Max_generations"));
        File f = new File(getParameterValue("Problem_file"));
        problem = StockingProblem.buildWarehouse(f);
        Algorithm<StockingProblemIndividual, StockingProblem> algorithm;
        for(int i = 1; i <= numRuns;i++){
            algorithm = new RandomAlgorithm<StockingProblemIndividual, StockingProblem>(
                    maxGenerations,new Random(i));
            algorithm.run(problem);
            utils.FileOperations.appendToTextFile("statistic_best_per_experiment.txt", "\r\n\r\n" + algorithm.getGlobalBest().toString() +"\r\n");
            utils.FileOperations.appendToTextFile("statistic_best_per_experiment_fitness.xls", "\t" + i + "\t" + algorithm.getGlobalBest().getFitness() + "\r\n");
        }
    }


    protected String getParameterValue(String name) {
        return parameters.get(name).getActiveValue();
    }

}
