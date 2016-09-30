import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/* This class holds all of our examples from one dataset
   (train OR test, not BOTH).  It extends the ArrayList class.
   Be sure you're not confused.  We're using TWO types of ArrayLists.  
   An Example is an ArrayList of feature values, while a ListOfExamples is 
   an ArrayList of examples. Also, there is one ListOfExamples for the 
   TRAINING SET and one for the TESTING SET. 
*/
class ListOfExamples extends ArrayList<Example>
{
  // The name of the dataset.
  private String nameOfDataset = "";

  // The number of features per example in the dataset.
  private int numFeatures = -1;

  // An array of the parsed features in the data.
  private BinaryFeature[] features;

  // A binary feature representing the output label of the dataset.
  private BinaryFeature outputLabel;

  // The number of examples in the dataset.
  private int numExamples = -1;

  public ListOfExamples() {} 
  
  public ListOfExamples(ListOfExamples ex) {
	  this.features = ex.getFeatureList();
	  this.outputLabel = ex.getLabelInfo();
	  this.numFeatures = ex.getNumberOfFeatures();
  }
  public BinaryFeature[] getFeatureList() {
	  return features;
  }
  
  public BinaryFeature getLabelInfo() {
	  return outputLabel;
  }
  
 
  public void addExample(Example ex) {
	  this.add(ex);
  }
  
  // Print out a high-level description of the dataset including its features.
  public void DescribeDataset()
  {
    System.out.println("Dataset '" + nameOfDataset + "' contains "
                       + numExamples + " examples, each with "
                       + numFeatures + " features.");
    System.out.println("Valid category labels: "
                       + outputLabel.getFirstValue() + ", "
                       + outputLabel.getSecondValue());
    System.out.println("The feature names (with their possible values) are:");
    for (int i = 0; i < numFeatures; i++)
    {
      BinaryFeature f = features[i];
      System.out.println("   " + f.getName() + " (" + f.getFirstValue() +
			 " or " + f.getSecondValue() + ")");
    }
    System.out.println();
  }

  // Print out ALL the examples.
  public void PrintAllExamples()
  {
    System.out.println("List of Examples\n================");
    for (int i = 0; i < size(); i++)
    {
      Example thisExample = this.get(i);  
      thisExample.PrintFeatures();
    }
  }

  // Print out the SPECIFIED example.
  public void PrintThisExample(int i)
  {
    Example thisExample = this.get(i); 
    thisExample.PrintFeatures();
  }

  // Returns the number of features in the data.
  public int getNumberOfFeatures() {
    return numFeatures;
  }

  // Returns the name of the ith feature.
  public String getFeatureName(int i) {
    return features[i].getName();
  }

  // Takes the name of an input file and attempts to open it for parsing.
  // If it is successful, it reads the dataset into its internal structures.
  // Returns true if the read was successful.
  public boolean ReadInExamplesFromFile(String dataFile) {
    nameOfDataset = dataFile;

    // Try creating a scanner to read the input file.
    Scanner fileScanner = null;
    try {
      fileScanner = new Scanner(new File(dataFile));
    } catch(FileNotFoundException e) {
      return false;
    }

    // If the file was successfully opened, read the file
    this.parse(fileScanner);
    return true;
  }

  /**
   * Does the actual parsing work. We assume that the file is in proper format.
   *
   * @param fileScanner a Scanner which has been successfully opened to read
   * the dataset file
   */
  public void parse(Scanner fileScanner) {
    // Read the number of features per example.
    numFeatures = Integer.parseInt(parseSingleToken(fileScanner));

    // Parse the features from the file.
    parseFeatures(fileScanner);

    // Read the two possible output label values.
    String labelName = "output";
    String firstValue = parseSingleToken(fileScanner);
    String secondValue = parseSingleToken(fileScanner);
    outputLabel = new BinaryFeature(labelName, firstValue, secondValue);

    // Read the number of examples from the file.
    numExamples = Integer.parseInt(parseSingleToken(fileScanner));

    parseExamples(fileScanner);
  }

  /**
   * Returns the first token encountered on a significant line in the file.
   *
   * @param fileScanner a Scanner used to read the file.
   */
  private String parseSingleToken(Scanner fileScanner) {
    String line = findSignificantLine(fileScanner);

    // Once we find a significant line, parse the first token on the
    // line and return it.
    Scanner lineScanner = new Scanner(line);
    return lineScanner.next();
  }

  /**
   * Reads in the feature metadata from the file.
   * 
   * @param fileScanner a Scanner used to read the file.
   */
  private void parseFeatures(Scanner fileScanner) {
    // Initialize the array of features to fill.
    features = new BinaryFeature[numFeatures];

    for(int i = 0; i < numFeatures; i++) {
      String line = findSignificantLine(fileScanner);

      // Once we find a significant line, read the feature description
      // from it.
      Scanner lineScanner = new Scanner(line);
      String name = lineScanner.next();
      String dash = lineScanner.next();  // Skip the dash in the file.
      String firstValue = lineScanner.next();
      String secondValue = lineScanner.next();
      features[i] = new BinaryFeature(name, firstValue, secondValue);
    }
  }

  private void parseExamples(Scanner fileScanner) {
    // Parse the expected number of examples.
    for(int i = 0; i < numExamples; i++) {
      String line = findSignificantLine(fileScanner);
      Scanner lineScanner = new Scanner(line);

      // Parse a new example from the file.
      Example ex = new Example(this);

      String name = lineScanner.next();
      ex.setName(name);

      String label = lineScanner.next();
      ex.setLabel(label);
      
      // Iterate through the features and increment the count for any feature
      // that has the first possible value.
      for(int j = 0; j < numFeatures; j++) {
	String feature = lineScanner.next();
	ex.addFeatureValue(feature);
      }

      // Add this example to the list.
      this.add(ex);
    }
  }

  /**
   * Returns the next line in the file which is significant (i.e. is not
   * all whitespace or a comment.
   *
   * @param fileScanner a Scanner used to read the file
   */
  private String findSignificantLine(Scanner fileScanner) {
    // Keep scanning lines until we find a significant one.
    while(fileScanner.hasNextLine()) {
      String line = fileScanner.nextLine().trim();
      if (isLineSignificant(line)) {
	return line;
      }
    }

    // If the file is in proper format, this should never happen.
    System.err.println("Unexpected problem in findSignificantLine.");

    return null;
  }

  /**
   * Returns whether the given line is significant (i.e., not blank or a
   * comment). The line should be trimmed before calling this.
   *
   * @param line the line to check
   */
  private boolean isLineSignificant(String line) {
    // Blank lines are not significant.
    if(line.length() == 0) {
      return false;
    }

    // Lines which have consecutive forward slashes as their first two
    // characters are comments and are not significant.
    if(line.length() > 2 && line.substring(0,2).equals("//")) {
      return false;
    }

    return true;
  }
}
