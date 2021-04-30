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
    ArrayList<int[]> material2;

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
        this.material2 = original.material2;;
    }

    private void fillMaterial(){
        material = new int[problem.getMaterialHeight()][problem.getMaterialLength()];
        material2 = new ArrayList<>();
        material2.add(new int[problem.getMaterialHeight()]);
        for(int i = 0; i < genome.length; i++){
            for(int j = 0 ; j < problem.getMaterialHeight()*problem.getMaterialLength(); j++){
                int x = j % problem.getMaterialHeight();
                int y = j / problem.getMaterialHeight();
                if(checkValidPlacement(problem.getItems().get(genome[i]),x,y)){
                    //fazer um if e adicionar novas casas caso seja ncessesario
                    Item item = problem.getItems().get(genome[i]);
                    int[][]itemArray = item.getMatrix();
                    for(int h = 0;h<item.getLines();h++){
                        for(int l = 0;l<item.getColumns();l++){
                            if(material2.size() == y+l)
                                material2.add(new int[problem.getMaterialHeight()]);
                            if(itemArray[h][l] != 0 && material2.get(l+y)[h+x] == 0){
                                material[h+x][l+y] = item.getRepresentation();
                                material2.get(l+y)[h+x] = item.getRepresentation();
                            }
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
        nrCortes = 0;
        for (int i = 0; i < material.length; i++) {
            for (int j = 1; j < material[0].length; j++) {
                if (material[i][j] != material[i][j-1]) {
                    nrCortes++;
                }
            }
        }
        for (int j = 0; j < material[0].length; j++) {
            for (int i = 1; i < material.length; i++) {
                if (material[i][j] != material[i-1][j]) {
                    nrCortes++;
                }
            }
        }
        fitness = nrCortes + material2.size();
        System.out.println("nrCortes= " + nrCortes + "   tamax = " + material2.size());
        return fitness;
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

    private boolean checkValidPlacement2(Item item, int lineIndex, int columnIndex) {
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

        sb.append("\nmaterial2: ");
        for (int i = 0; i<problem.getMaterialHeight();i++) {
            String linha = "\n";
            for (int j = 0; j<material2.size();j++) {
                if(material2.get(j)[i] == 0){
                    linha += " 0 ";
                }else{
                    linha += " "+(char)material2.get(j)[i] + " ";
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