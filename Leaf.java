
public class Leaf extends Tree{
	 String label;
	 
	 public Leaf(String label) {
		 this.label = label;
	 }
	 int computeTreeSize() {
		 return 1; 
	 }
     int countTreeNodes() {
    	 return 0;
     }
     int countLeafNodes() {
    	 return 1;
     }
    int computeMaxDepth() {
    	return 1;
    }
}
