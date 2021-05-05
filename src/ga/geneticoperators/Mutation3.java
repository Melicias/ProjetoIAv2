package ga.geneticoperators;

import algorithms.IntVectorIndividual;
import algorithms.Problem;
import ga.GeneticAlgorithm;

public class Mutation3<I extends IntVectorIndividual, P extends Problem<I>> extends Mutation<I, P> {

    public Mutation3(double probability) {
        super(probability);
    }

    @Override
    public void mutate(I ind) {
        int i1 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
        int i2 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
        while(i1 >= i2){
            i1 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
            i2 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
        }
        int midGene = ((i2-i1)/2)+i1;

        for (; i1 <= midGene; i1++, i2--) {
            int aux = ind.getGene(i1);
            ind.setGene(i1, ind.getGene(i2));
            ind.setGene(i2, aux);
        }


    }

    @Override
    public String toString() {
        //TODO
        return "Inversion (" + probability + ")";
    }
}