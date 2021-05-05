package ga.geneticoperators;

import algorithms.IntVectorIndividual;
import algorithms.Problem;

import java.util.ArrayList;
import java.util.Arrays;

public class Recombination2<I extends IntVectorIndividual, P extends Problem<I>> extends Recombination<I, P> {

    public Recombination2(double probability) {
        super(probability);
    }

    private int[] child1, child2;

    @Override
    public void recombine(I ind1, I ind2) {
        //TODO
        child1 = new int[ind1.getNumGenes()];
        child2 = new int[ind2.getNumGenes()];

        int value1 = ind1.getGene(0);
        child1[0] = value1;
        int value2 = ind2.getGene(0);
        child2[0] = value2;
        if(ind1.getGene(0) == 18 && ind1.getFitness() == 284 && ind2.getFitness() == 296 && ind2.getGene(26)==18)
            System.out.println("aa");

        do {
            child1[ind1.getIndexof(value2)] = value2;
            child2[ind2.getIndexof(value1)] = value1;
            value1 = ind1.getGene(ind2.getIndexof(value1));
            value2 = ind2.getGene(ind1.getIndexof(value2));
        } while (child1[ind1.getIndexof(value2)] != value2);


        for(int i = 0;i<child1.length;i++){
            if(child1[i] == 0)
                child1[i] = ind2.getGene(i);
            if(child2[i] == 0)
                child2[i] = ind1.getGene(i);
        }

        if(child1[0] == 18 && child1[26]==18)
            System.out.println("aa");
        for (int i = 0; i < ind1.getNumGenes(); i++) {
            ind1.setGene(i, child1[i]);
            ind2.setGene(i, child2[i]);
        }

        System.out.println(Arrays.toString(child1));
        System.out.println(Arrays.toString(child2));
        System.out.println(Arrays.toString(ind1.getGenome()));
    }

    @Override
    public String toString(){
        //TODO
        return "CX";
    }
}