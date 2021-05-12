package random;

import algorithms.Algorithm;
import algorithms.AlgorithmEvent;
import algorithms.Individual;
import algorithms.Problem;
import ga.GeneticAlgorithm;
import ga.Population;

import java.util.Random;

public class RandomAlgorithm<I extends Individual, P extends Problem<I>> extends Algorithm<I, P> {
    //TODO this class might require the definition of additional methods and/or attributes
    public RandomAlgorithm(int maxIterations, Random rand) {
        super(maxIterations, rand);
    }

    @Override
    public I run(P problem) {

        t = 0;
        globalBest = problem.getNewIndividual();
        globalBest.computeFitness();
        fireIterationEnded(new AlgorithmEvent(this));

        while (t < maxIterations && !stopped) {
            I bestInGen = problem.getNewIndividual();
            bestInGen.computeFitness();
            computeBestInRun(bestInGen);
            t++;
            fireIterationEnded(new AlgorithmEvent(this));
        }
        fireRunEnded(new AlgorithmEvent(this));
        return globalBest;
    }

    private void computeBestInRun(I bestInGen) {
        if (bestInGen.compareTo(globalBest) > 0) {
            globalBest = (I) bestInGen.clone();
        }
    }
}
