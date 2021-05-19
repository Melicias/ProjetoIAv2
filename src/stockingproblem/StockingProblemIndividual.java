package stockingproblem;

import algorithms.IntVectorIndividual;
import ga.GeneticAlgorithm;

import java.util.ArrayList;

public class StockingProblemIndividual extends IntVectorIndividual<StockingProblem, StockingProblemIndividual> {
    //TODO this class might require the definition of additional methods and/or attributes
    private int nrCortes;
    public ArrayList<int[]> material;

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

    public StockingProblemIndividual(StockingProblemIndividual original) {
        super(original);
        this.nrCortes = original.nrCortes;
        this.material = original.material;
    }

    private void fillMaterial(){
        material = new ArrayList<>();
        for(int i = 0; i < genome.length; i++){
            for(int j = 0 ; j < problem.getMaterialHeight()*problem.getMaterialLength(); j++){
                int x = j % problem.getMaterialHeight();
                int y = j / problem.getMaterialHeight();
                if(checkValidPlacement(problem.getItems().get(genome[i]),x,y)){
                    Item item = problem.getItems().get(genome[i]);
                    for(int l = 0;l<item.getColumns();l++){
                        if(material.size() == y+l)
                            material.add(new int[problem.getMaterialHeight()]);
                        for(int h = 0;h<item.getLines();h++){
                            if(item.getMatrix()[h][l] != 0)
                                material.get(l+y)[h+x] = item.getRepresentation();
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public double computeFitness() {
        //TODO
        //fitness calculado com o nr cortes e o tamanho ate onde tem valores do array
        fillMaterial();
        material.add(new int[problem.getMaterialHeight()]);
        nrCortes = 0;
        for (int i = 0; i < problem.getMaterialHeight(); i++) {
            for (int j = 1; j < material.size(); j++) {
                if (material.get(j)[i] != material.get(j-1)[i]) {
                    nrCortes++;
                }
            }
        }
        for (int j = 0; j < material.size(); j++) {
            for (int i = 1; i < problem.getMaterialHeight(); i++) {
                if (material.get(j)[i] != material.get(j)[i-1]) {
                    nrCortes++;
                }
            }
        }
        fitness = nrCortes + material.size()-1;
        return fitness;
    }

    public ArrayList<int[]> getMaterial(){
        return material;
    }

    private boolean checkValidPlacement(Item item, int lineIndex, int columnIndex) {
        int[][] itemMatrix = item.getMatrix();
        for (int i = 0; i < itemMatrix.length; i++) {
            for (int j = 0; j < itemMatrix[i].length; j++) {
                if (itemMatrix[i][j] != 0) {
                    if((lineIndex+i) >= problem.getMaterialHeight())
                        return false;
                    if((columnIndex + j) < material.size())
                        if ( material.get(columnIndex+j)[lineIndex+i] != 0)
                            return false;
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
        for (int i = 0; i<problem.getMaterialHeight();i++) {
            String linha = "\n";
            for (int j = 0; j< material.size()-1; j++) {
                if(material.get(j)[i] == 0){
                    linha += " 0 ";
                }else{
                    linha += " "+(char) material.get(j)[i] + " ";
                }

            }
            sb.append(linha);
        }
        sb.append("\nNumber of cuts: ").append(nrCortes);
        sb.append("\nSize: ").append(material.size()-1);
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