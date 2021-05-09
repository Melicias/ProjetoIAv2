package ga.geneticoperators;

import algorithms.IntVectorIndividual;
import algorithms.Problem;
import ga.GeneticAlgorithm;

public class Mutation4<I extends IntVectorIndividual, P extends Problem<I>> extends Mutation<I, P> {

    public Mutation4(double probability) {
        super(probability);
    }

    //Inversion
    @Override
    public void mutate(I ind) {
        int i1 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1; //indice 1 para trocar
        int i2 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1; //indice 2 para trocar
        while(i1 >= i2){ //caso o indice 2 seja maior que o indice 1 random aoutra vez
            i1 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
            i2 = GeneticAlgorithm.random.nextInt(ind.getNumGenes()-1)+1;
        }
        for(int i = 0; i < i2-i1; i++){ // sacar de 2 numeros para dar swap entre o intervalo de i1 e i2 --- ser anecessario adicionar mais interacoes?
            int s1 = GeneticAlgorithm.random.nextInt(i1)+(i2-i1);
            int s2 = GeneticAlgorithm.random.nextInt(i1)+(i2-i1);
            int first = ind.getGene(s1);
            ind.setGene(s1,ind.getGene(s2));
            ind.setGene(s2,first);
        }
    }

    @Override
    public String toString() {
        //TODO
        return "Scramble mutation (" + probability + ")";
    }
}
