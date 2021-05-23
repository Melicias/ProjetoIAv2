package ga.geneticoperators;

import algorithms.IntVectorIndividual;
import algorithms.Problem;
import ga.GeneticAlgorithm;

public class RecombinationCrossoverFirst<I extends IntVectorIndividual, P extends Problem<I>> extends Recombination<I, P> {

    public RecombinationCrossoverFirst(double probability) {
        super(probability);
    }

    private int[] child1, child2;


    @Override
    public void recombine(I ind1, I ind2) {

        child1 = new int[ind1.getNumGenes()];
        child2 = new int[ind2.getNumGenes()];

        for (int i = 0; i < ind1.getNumGenes(); i++) {
            child1[i]=-1;
            child2[i]=-1;
        }

        int i1 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes()-1)+1;
        int i2 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes()-1)+1;
        while(i1 >= i2){
            i1 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes()-1)+1;
            i2 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes()-1)+1;
        }

        for (int i = i1; i <= i2; i++) {
            child1[i] = ind1.getGene(i);
            child2[i] = ind2.getGene(i);
        }

        child1 = fillChilds(child1,ind2);
        child2 = fillChilds(child2,ind1);

        for (int i = 0; i < ind1.getNumGenes(); i++) {
            ind1.setGene(i, child1[i]);
            ind2.setGene(i, child2[i]);
        }

    }

    private int[] fillChilds(int[] child,I ind){
        int ind1 = 0;
        int indAux = 0;
        while (ind1 < child.length){
            if(child[ind1] == -1){
                while(check_forDuplicates(child, ind.getGene(indAux))){
                    indAux++;
                }
                child[ind1] = ind.getGene(indAux);
                indAux++;
            }
            ind1++;
        }
        return child;
    }

    private boolean check_forDuplicates(int[] offspring, int element) {
        for (int index = 0; index < offspring.length; index++) {
            if (offspring[index] == element) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        //TODO
        //crossover 2 random, corta ai e troca o meu para o oposto
        //depois troca comeca a inserir consoante o 1 elemento oposto.
        return "Order 1 crossover";
    }
}