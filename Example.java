import java.util.ArrayList;

// This class, an extension of ArrayList, holds an individual example.
// The new method PrintFeatures() can be used to
// display the contents of the example. 
// The items in the ArrayList are the feature values.
public class Example extends ArrayList<String>
{
  // The name of this example.
  private String name;  

  // The output label of this example.
  private String label;

  // The data set in which this is one example.
  private ListOfExamples parent;  

  // Constructor which stores the dataset which the example belongs to.
  public Example(ListOfExamples parent) {
    this.parent = parent;
  }

  // Print out this example in human-readable form.
  public void PrintFeatures()
  {
    System.out.print("Example " + name + ",  label = " + label + "\n");
    for (int i = 0; i < parent.getNumberOfFeatures(); i++)
    {
      System.out.print("     " + parent.getFeatureName(i)
                       + " = " +  this.get(i) + "\n");
    }
  }

  // Adds a feature value to the example.
  public void addFeatureValue(String value) {
    this.add(value);
  }

  // Accessor methods.
  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }

  // Mutator methods.
  public void setName(String name) {
    this.name = name;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
