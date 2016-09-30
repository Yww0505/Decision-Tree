
public abstract class Tree {
 // Use 'abstract' because we'll never create a TREE instance but will instead only create instances of INTERIOR_NODE and LEAF.
        abstract int computeTreeSize();  // The subclasses of TREE need to define these methods.
        abstract int countTreeNodes();
        abstract int countLeafNodes();  // Note that computeTreeSize() = countInteriorNodes() + countLeafNodes(), but doing it that way would require TWO tree traversals.
        abstract int computeMaxDepth();
   }

