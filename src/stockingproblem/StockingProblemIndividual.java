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
    ArrayList<ArrayList<Integer>> material2;
    int biggestSize;

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
        this.biggestSize = original.biggestSize;
    }

    private void fillMaterial(){
        material = new int[problem.getMaterialHeight()][problem.getMaterialLength()];
        material2 = new ArrayList<>();
        for(int i = 0; i < problem.getMaterialHeight();i++){
            material2.add(new ArrayList<Integer>());
        }
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
                            while(material2.get(h+x).size() <= l+y)
                                material2.get(h+x).add(0);
                            if(itemArray[h][l] != 0){
                                material[h+x][l+y] = item.getRepresentation();
                                material2.get(h+x).set(l+y,item.getId());
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
        biggestSize = 0;
        for (int i = 0; i < problem.getMaterialHeight(); i++) {
            for (int j = 1; j < material2.get(i).size(); j++) {
                if(material2.get(i).size() > j)
                    if (material2.get(i).get(j) != material2.get(i).get(j - 1)) {
                        nrCortes++;
                    }
            }
            if(material2.get(i).size() > biggestSize)
                biggestSize = material2.get(i).size();
        }
        nrCortes+=problem.getMaterialHeight();
        for (int j = 0; j < biggestSize; j++) {
            for (int i = 1; i < problem.getMaterialHeight(); i++) {
                if(material2.get(i).size() > j && material2.get(i-1).size() > j){
                    if (material2.get(i).get(j) != material2.get(i-1).get(j)) {
                        nrCortes++;
                    }
                }
            }
        }
        for(int i = 1; i< material2.size(); i++){
            if(material2.get(i-1).size() > material2.get(i).size()){
                nrCortes += material2.get(i-1).size() - material2.get(i).size();
            }else{
                if(material2.get(i-1).size() < material2.get(i).size()){
                    nrCortes += material2.get(i).size() - material2.get(i-1).size();
                }
            }
        }
        fitness = nrCortes + material2.size();
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
        for (int i = 0; i<material2.size();i++) {
            String linha = "\n";
            for (int j = 0; j<material2.get(i).size();j++) {
                if(material2.get(i).get(j) == 0){
                    linha += " 0 ";
                }else{
                    linha += " "+ (material2.get(i).get(j) < 26 ? (char) ('A' + material2.get(i).get(j)) : (char) ('A' + material2.get(i).get(j) + 6)) + " ";
                }

            }
            sb.append(linha);
        }
        sb.append("\nNumber of cuts: ").append(nrCortes);
        sb.append("\nSize: ").append(material2.size());
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