package ga.geneticoperators;

import algorithms.IntVectorIndividual;
import algorithms.Problem;
import ga.GeneticAlgorithm;

public class Recombination3<I extends IntVectorIndividual, P extends Problem<I>> extends Recombination<I, P> {

    public Recombination3(double probability) {
        super(probability);
    }

    private int[] child1, child2;


    @Override
    public void recombine(I ind1, I ind2) {

        child1 = new int[ind1.getNumGenes()];
        child2 = new int[ind2.getNumGenes()];

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
        System.out.println("Entrou");
        child1 = fillChilds(child1,ind2,i1,i2+1);
        child2 = fillChilds(child2,ind1,i1,i2+1);
        System.out.println("saiu");
        for (int i = 0; i < ind1.getNumGenes(); i++) {
            ind1.setGene(i, child1[i]);
            ind2.setGene(i, child2[i]);
        }

    }


    private int[] fillChilds(int[] child,I ind,int i1, int i2){
        int indAux = i2;
        while (i2 != i1){
            if(i2 == ind.getNumGenes()) {
                i2 = 0;
            }
            do{
                indAux++;
                if(indAux >= ind.getNumGenes()) {
                    indAux = 0;
                }
            }while(check_forDuplicates(child, ind.getGene(indAux)));
            child[i2] = ind.getGene(indAux);
            i2++;
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
        return "crossover";
    }
}