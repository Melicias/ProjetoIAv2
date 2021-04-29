package stockingproblem;

import algorithms.IntVectorIndividual;
import ga.GeneticAlgorithm;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class StockingProblemIndividual extends IntVectorIndividual<StockingProblem, StockingProblemIndividual> {
    //TODO this class might require the definition of additional methods and/or attributes
    private int nrCortes;
    int[][] material;
    ArrayList<int[]> dbs;

    public StockingProblemIndividual(StockingProblem problem, int size) {
        //TODO
        super(problem, size);
        genome = new int[size];
        for (int i = 0; i < genome.length; i++) {
            genome[i] = i;
        }

        for(int i = 0; i< genome.length;i++){
            int save = genome[i];
            int ant = GeneticAlgorithm.random.nextInt(size);
            genome[i] = genome[ant];
            genome[ant] = save;
        }
    }

    private void fillMaterial(){
        material = new int[problem.getMaterialHeight()][problem.getMaterialLength()];
        for(int i = 0;i<genome.length;i++){
            for(int j = 0 ; j < problem.getMaterialHeight()*problem.getMaterialLength(); j++){
                //x = resto de j/nºlinhas
                int x = j % problem.getMaterialHeight();
                //y = j/nºcolunas
                int y = j/problem.getMaterialLength();
                if(checkValidPlacement(problem.getItems().get(genome[i]),x,y)){
                    Item item = problem.getItems().get(genome[i]);
                    for(int h = 0;h<item.getLines();h++){
                        for(int l = 0;l<item.getColumns();l++){
                            material[h+x][l+y] = item.getRepresentation();
                        }
                    }
                    break;
                }
            }
        }
    }

    public StockingProblemIndividual(StockingProblemIndividual original) {
        super(original);
        this.nrCortes = original.nrCortes;
    }

    @Override
    public double computeFitness() {
        //TODO
        //fitness calculado com o nr cortes e o tamanho ate onde tem valores do array
        fillMaterial();
        return 1;
    }

    private boolean checkValidPlacement(Item item, int lineIndex, int columnIndex) {
        int[][] itemMatrix = item.getMatrix();
        for (int i = 0; i < itemMatrix.length; i++) {
            for (int j = 0; j < itemMatrix[i].length; j++) {
                if (itemMatrix[i][j] != 0) {
                    if ((lineIndex + i) >= material.length
                            || (columnIndex + j) >= material[0].length
                            || material[lineIndex + i][columnIndex + j] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fitness: ").append(fitness);
        sb.append("\nmaterial: ");
        for (int i = 0; i<material.length;i++) {
            String linha = "\n";
            for (int j = 0; j<material[i].length;j++) {
                if(material[i][j] == 0){
                    linha += "0";
                }else{
                    linha += (char)material[i][j] + "-";
                }

            }
            sb.append(linha);
        }
        sb.append("\nNumber of cuts: ").append(nrCortes);
        //TODO
        return sb.toString();
    }

    /**
     * @param i
     * @return 1 if this object is BETTER than i, -1 if it is WORST than I and
     * 0, otherwise.
     */
    @Override
    public int compareTo(StockingProblemIndividual i) {
        return (this.fitness == i.getFitness()) ? 0 : (this.fitness < i.getFitness()) ? 1 : -1;
    }

    @Override
    public StockingProblemIndividual clone() {
        return new StockingProblemIndividual(this);

    }
}