import java.util.*;
import java.io.*;
import java.text.DecimalFormat;



public class BuildAndTestDecisionTree
{
  // "Main" reads in the names of the files we want to use, then reads 
  // in their examples.
  public static void main(String[] args)
  {   
    if (args.length != 2)
    {
      System.err.println("You must call BuildAndTestDecisionTree as " + 
			 "follows:\n\njava BuildAndTestDecisionTree " + 
			 "<trainsetFilename> <testsetFilename>\n");
      System.exit(1);
    }    

    // Read in the file names.
    String trainset = args[0];
    String testset  = args[1];

    // Read in the examples from the files.
    ListOfExamples trainExamples = new ListOfExamples();
    ListOfExamples testExamples  = new ListOfExamples();
    if (!trainExamples.ReadInExamplesFromFile(trainset) ||
        !testExamples.ReadInExamplesFromFile(testset))
    {
      System.err.println("Something went wrong reading the datasets ... " +
			   "giving up.");
      System.exit(1);
    }
    else
    { 
      
      Tree root = buildTree(trainExamples);
      System.out.println("The Decision Tree is : ");
      printTree(root, 0);
      ListOfExamples report = new ListOfExamples(testExamples);
      testTree(root, testExamples, report);
      System.out.println();
      printResult(report, testExamples);
     // trainExamples.DescribeDataset();
//      testExamples.DescribeDataset();
//      trainExamples.PrintThisExample(0);  // Print out an example
      //trainExamples.PrintAllExamples(); // Don't waste paper printing all 
                                          // of this out!
      //testExamples.PrintAllExamples();  // Instead, just view it on the screen
    }

    Utilities.waitHere("Hit <enter> when ready to exit.");
  }
  private static Tree buildTree(ListOfExamples examples) {
	  if(examples.size() == 0) {
		  return new Leaf("");
	  }
	  String leafValue = isLeaf(examples);
	  if(leafValue.length() != 0) {
		  Tree newNode = new Leaf(leafValue);
		  return newNode;
	  }
	  ArrayList<Integer> validFeature = getAttributes(examples);
	  double remainder = 10000.0;
	  ListOfExamples[] subList = null;
	  String featureName = null;
	  int featureIndex = 0;
	  for(int i = 0; i<validFeature.size(); i++) {
		  int index = validFeature.get(i);
		  ListOfExamples[] newList = classify(examples, index);
		  
		  int[] distribution1 = getDistribution(newList[0]);
		  int[] distribution2 = getDistribution(newList[1]);
		  double entropy1 = getEntropy(distribution1[0], distribution1[1]);
		  double entropy2 = getEntropy(distribution2[0], distribution2[1]);
		  double curr = getRemainder(newList[0].size(), newList[1].size(),
				  entropy1,entropy2);
		 
		  if(curr < remainder) {
			  remainder = curr;
			  subList = newList;
			  featureName = examples.getFeatureName(index);
		      featureIndex = index;
		  }
	  }
	  String leftValue = subList[0].get(0).get(featureIndex);
	  String rightValue = subList[1].get(0).get(featureIndex);
	  Tree newNode = new TreeNode(featureName, featureIndex, leftValue, rightValue);
	  Tree left = buildTree(subList[0]);
	  Tree right = buildTree(subList[1]);
	  if(left instanceof Leaf && ((Leaf) left).label.length() == 0) {
		  setParentValue(examples, (Leaf)left);
	  }
	  if(right instanceof Leaf && ((Leaf) right).label.length() == 0) {
		  setParentValue(examples, (Leaf) right);
	  }
	  
	  ((TreeNode)newNode).left = left;
	  ((TreeNode)newNode).right = right;
	  return newNode;
  }
  private static void printTree(Tree root, int depth) {
	  if(root == null || depth == 5) {
		  return;
	  }
	  if(root instanceof Leaf) {
		  System.out.println(((Leaf)root).label);
		  return;
	  }
	  TreeNode curr = (TreeNode) root;
	  for(int i = 0; i<depth; i++) {
		  System.out.print("**");
	  }
	  System.out.print(curr.feature + " " + curr.leftValue + ": ");
	  if(curr.left instanceof Leaf) {
		  System.out.println(((Leaf)curr.left).label);
	  }
	  else {
		  System.out.println();
		  int nextDepth = depth + 1;
		  printTree(curr.left, nextDepth);
	  }
	  for(int i = 0; i<depth; i++) {
		  System.out.print("**");
	  }
	  System.out.print(curr.feature + " " + curr.rightValue + ": ");
	  if(curr.right instanceof Leaf) {
		  System.out.println(((Leaf)curr.right).label);
	  }
	  else {
		  System.out.println();
		  int nextDepth = depth + 1;
		  printTree(curr.right, nextDepth);
	  }
  }
  private static void testTree(Tree root, ListOfExamples testSet, ListOfExamples report) {
	  for(int i = 0; i < testSet.size(); i++) {
		  Example curr = testSet.get(i);
		  Example result = testOneExample(root, curr);
		  if(result != null) {
			  report.add(result);
		  }
	  }
  }
  private static Example testOneExample(Tree root, Example curr) {
	  if(root instanceof Leaf) {
		  Leaf leaf = (Leaf) root;
		  if(leaf.label.equals(curr.getLabel())) {
			  return null;
		  }
		  else {
			  return curr;
		  }
	  }
	  TreeNode node = (TreeNode) root;
	  int index = node.index;
	  if(curr.get(index).equals(node.leftValue)) {
		  return testOneExample(node.left, curr);
	  }
	  else {
		  return testOneExample(node.right,curr);
	  }
	 
  }
  private static void printResult(ListOfExamples report, ListOfExamples test) {
	  double accuracy = 1 - (report.size() * 1.0f)/test.size();
	  System.out.println("The accuracy for the testset is " +
			  new DecimalFormat("##.###").format(accuracy));
	  System.out.println("Incorrect predict examples are ");
	  for(Example tmp : report) {
		  System.out.println(tmp.getName());
	  }
  }
  private static ArrayList<Integer> getAttributes(ListOfExamples examples) {
	  ArrayList<Integer> list = new ArrayList<>();
	  for(int i = 0; i < examples.getNumberOfFeatures(); i++) {
		  boolean valid = false;
		  String value = examples.get(0).get(i);
		  for(int j =1; j<examples.size(); j++) {
			 
			  String currValue = examples.get(j).get(i);
			  if(!currValue.equals(value)){
				  valid = true;
				  break;
			  }
		  }
		  if(valid) {
			  list.add(i);
		  }
	  }
	  return list;
  }
  private static double getRemainder(int numValue1, int numValue2, double Entropy1, double Entropy2) {
	  
	  double percent1 = (numValue1 *1.0f) / (numValue1 + numValue2);
	  double percent2 = 1 - percent1;
	  double result = percent1 * Entropy1 + percent2 * Entropy2;
	 
	  return result;
  }
  private static double getEntropy(int num1, int num2) {
	  if(num1 == 0 || num2 == 0) {
		  return 0;
	  }
	 
	  double percent1 = (num1 *1.0f )/ (num1 + num2);
	  double percent2 = 1.0 - percent1;
	  
	  double result = -(percent1 * Math.log(percent1)/Math.log(2)) - (percent2 * Math.log(percent2) / Math.log(2));
	
	  return result;
  }
  private static ListOfExamples[] classify(ListOfExamples examples, int index) {
	  ListOfExamples[] result = new ListOfExamples[2];
	  result[0] = new ListOfExamples(examples);
	  result[1] = new ListOfExamples(examples);
	 
	  String value1 = (examples.getFeatureList())[index].getFirstValue();
	  for(int i = 0; i < examples.size(); i++) {
		  Example curr = examples.get(i);
		  if(curr.get(index).equals(value1)) {
			  result[0].add(curr);
		  }
		  else{
			  result[1].add(curr);
		  }
	  }
	  return result;
  }
  private static int[] getDistribution(ListOfExamples examples) {
	  int[] count = new int[2];
	  String value1 = examples.getLabelInfo().getFirstValue();
	  for(int i = 0; i < examples.size(); i++) {
		  if(examples.get(i).getLabel().equals(value1)) {
			  count[0] ++;
		  }
		  else {
			  count[1] ++;
		  }
	  }
	  return count;
  }
  private static String isLeaf(ListOfExamples ex) {
	  
	  if(getAttributes(ex).size() == 0) {
		  String label_1 = ex.getLabelInfo().getFirstValue();
		  String label_2 = ex.getLabelInfo().getSecondValue();
		  int count_1 = 0, count_2 = 0;
		  for(int i = 0; i<ex.size(); i++) {
			  if(label_1.equals(ex.get(i).getLabel())) {
				  count_1 ++;
			  }
			  else {
				  count_2 ++;
			  }
		  }
		 return (count_1 > count_2) ? label_1 : label_2;
	  }
	  String str = "";
	  String label = ex.get(0).getLabel();
	  for(int i = 0; i<ex.size(); i++) {
		  if(!label.equals(ex.get(i).getLabel())) {
			  return str;
		  }
	  }
	  return ex.get(0).getLabel();
  }
  private static void setParentValue(ListOfExamples ex, Leaf leaf) {
	  int[] distri = getDistribution(ex);
	  int greater = (distri[0] > distri[1]) ? 0 : 1;
	  String majority; 
	  if(greater == 0) {
		  majority = ex.getLabelInfo().getFirstValue();
	  }
	  else {
		  majority = ex.getLabelInfo().getSecondValue();
	  }
	  ((Leaf) leaf).label = majority;
  }
}





/**
 * Represents a single binary feature with two String values.
 */
class BinaryFeature {
  private String name;
  private String firstValue;
  private String secondValue;

  public BinaryFeature(String name, String first, String second) {
    this.name = name;
    firstValue = first;
    secondValue = second;
  }

  public String getName() {
    return name;
  }

  public String getFirstValue() {
    return firstValue;
  }

  public String getSecondValue() {
    return secondValue;
  }
}

class Utilities
{
  // This method can be used to wait until you're ready to proceed.
  public static void waitHere(String msg)
  {
    System.out.print("\n" + msg);
    try { System.in.read(); }
    catch(Exception e) {} // Ignore any errors while reading.
  }
}
