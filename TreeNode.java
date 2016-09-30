
public class TreeNode extends Tree{
	String feature;
	Tree left, right;
	int index;
	String leftValue, rightValue;
	public TreeNode(String feature, int index, String leftV, String rightV) {
		this.feature = feature;
		this.index = index;
		this.leftValue = leftV;
		this.rightValue = rightV;
		this.left = null;
		this.right = null;
	}
	
	int computeTreeSize(){
		return 1 + left.computeTreeSize() + right.computeTreeSize(); 
	}
    int countTreeNodes() {
    	return 1 + left.countTreeNodes() + right.countTreeNodes();
    }
    int countLeafNodes() {
    	return 0;
    }
    int computeMaxDepth() {
    	return 1 + Math.max(left.computeMaxDepth(), right.computeMaxDepth());
    }
}
