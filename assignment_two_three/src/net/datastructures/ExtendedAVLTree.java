/*********************************************
 * 
 *          COMP9024 Assignment 2
 *          Programmed by Chunnan Sheng
 *          Student Code z5100764
 * 
 *********************************************/

package net.datastructures;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * The extended AVL tree
 * @author Chunnan Sheng
 *
 * @param <K>
 * @param <V>
 */
public class ExtendedAVLTree<K, V> extends AVLTree<K, V>
{
    // This variable defines diameter of internal tree nodes on the window
    private static int node_size = 25;
    
    // This variable mark identifications of windows because
    // multiple windows will be open in order to show AVL trees in
    // chronological order
    private static int window_number = 0;
    
    /**
     * The window to show an AVL tree graphically
     * @author Chunnan Sheng
     *
     */
    private class ShowWin extends JFrame
    {
        //
        private static final long serialVersionUID = 1L;
        
        // The AVL tree which needs to be showed on this window
        private ExtendedAVLTree<K, V> m_avl_tree;
        
        /**
         * Constructor of the window
         * @param title
         */
        public ShowWin(String title)
        {
            super(title + " " + window_number);
            
            window_number ++;

            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            getContentPane().add(panel);
        }
        
        /**
         * Assign the tree to the window so that this window can
         * display the tree graphically
         * @param tree
         */
        public void setAVLTree(ExtendedAVLTree<K, V> tree)
        {
            this.m_avl_tree = tree;
            
            int window_width = (int) (tree.width() * node_size * 0.3) + node_size;
            int window_height = (int) ((tree.height() + 2) * node_size * 2);
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(window_width, window_height);
            this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        }

        /**
         * This function will be called any time the window is repainted
         */
        public void paint(Graphics g)
        {
            super.paint(g); // fixes the immediate problem.
            this.m_avl_tree.print(g);
        }
    }
    
    /**
     * Definition of AVL tree clone method which calls copy constructor of ExtendedAVLTree
     * @param tree
     * @return
     */
    public static <K, V> AVLTree<K, V> clone(AVLTree<K, V> tree)
    {
        // Create a new AVLTree which has the same comparator as
        // the original tree
        ExtendedAVLTree<K, V> new_tree = new ExtendedAVLTree<K, V>(tree);

        return new_tree;
    }

    /**
     * Definition of AVLTree merge method which calls the merge constructor of ExtendedAVLTree
     * @param tree1
     * @param tree2
     * @return
     */
    public static <K, V> AVLTree<K, V> merge(AVLTree<K, V> tree1, AVLTree<K, V> tree2)
    {
        // Construct a new constructor of ExtendedAVLTree
        // to implement this solution
        ExtendedAVLTree<K, V> new_tree = new ExtendedAVLTree<K, V>(tree1, tree2);
        
        // Destroy tree1
        tree1.root = null;
        tree1.numEntries = 0;
        tree1.addRoot(null);
        
        // Destroy tree2
        tree2.root = null;
        tree2.numEntries = 0;
        tree2.addRoot(null);
        
        return new_tree;
    }

    /**
     * Copy an AVLTree to an ExtendedAVLTree.
     * Assign the ExtendedAVLTree instance to the window to display the tree.
     * @param tree
     */
    public static <K, V> void print(AVLTree<K, V> tree)
    {
        ExtendedAVLTree<K, V>.ShowWin win = (new ExtendedAVLTree<K, V>()).new ShowWin("AVLTree");

        ExtendedAVLTree<K, V> copy = new ExtendedAVLTree<K, V>(tree);
        win.setAVLTree(copy);
        win.setVisible(true);
    }
    
    /**
     * Inherited constructor from AVLTree
     * @param c
     */
    public ExtendedAVLTree(Comparator<K> c)
    {
        super(c);
    }

    /**
     * Inherited constructor from AVLTree
     */
    public ExtendedAVLTree()
    {
        super();
    }

    /**
     * Copy constructor of ExtendedAVLTree
     * Depth first traverse the old AVLTree in Pre-order to construct a new ExtendedAVLTree.
     * Time complexity of this job is O(n) (n is amount of elements of the original AVLTree)
     * @param old_tree
     */
    public ExtendedAVLTree(AVLTree<K, V> old_tree)
    {
        super(old_tree.C);

        Position<Entry<K, V>> old_root_node;

        // We do not need to do anything for an empty tree
        if (old_tree.isEmpty())
        {
            return;
        }

        // Try to get the root node of the original tree
        old_root_node = old_tree.root();

        // Traverse the original tree to construct the new tree
        traverseAndCopy(root(), old_root_node, old_tree);
    }
    
    public int width()
    {
        return (int) (Math.pow(2, this.height() + 1));
    }
    
    public int height()
    {
        return ((AVLNode<K, V>)this.root()).getHeight();
    }

    /**
     * Merge constructor of ExtendedAVLTree.
     * There are three steps to merge two AVLTrees into one.
     * Step 1:
     * Depth first traverse each AVLTree in In-order to add all entries (elements) into an ArrayList.
     * Time complexity: O(m + n)
     * Step 2:
     * Combine two ArrayLists into one. All elements in the combined ArrayList should be ordered by K (key).
     * Time complexity: O(m + n)
     * Step 3:
     * Build a new ExtendedAVLTree from the combined ArrayList with the method of binary search (Recursive).
     * Time complexity: O(m + n)
     * @param tree1
     * @param tree2
     */
    public ExtendedAVLTree(AVLTree<K, V> tree1, AVLTree<K, V> tree2)
    {
        // Step 1
        
        ArrayList<Entry<K, V>> list1 = new ArrayList<Entry<K, V>>();
        this.traverseAndAppend(list1, tree1.root());
        
        ArrayList<Entry<K, V>> list2 = new ArrayList<Entry<K, V>>();
        this.traverseAndAppend(list2, tree2.root());

        ArrayList<Entry<K, V>> union = new ArrayList<Entry<K, V>>();

        // Step 2
        
        int index1 = 0, index2 = 0;
        while (true)
        {
            if (index1 < list1.size() && index2 < list2.size())
            {
                K key1 = list1.get(index1).getKey();
                K key2 = list2.get(index2).getKey();
                int comp = this.C.compare(key1, key2);
                if (comp < 0)
                {
                    union.add(list1.get(index1));
                    index1++;
                }
                else if (comp > 0)
                {
                    union.add(list2.get(index2));
                    index2++;
                }
                else
                {
                    union.add(list1.get(index1));
                    union.add(list2.get(index2));
                    index1++;
                    index2++;
                }
            }
            else if (index1 < list1.size() && index2 == list2.size())
            {
                union.add(list1.get(index1));
                index1 ++;
            }
            else if (index1 == list1.size() && index2 < list2.size())
            {
                union.add(list2.get(index2));
                index2 ++;
            }
            else
            {
                break;
            }
        }

        // Step 3
        
        buildAVLTreeFromSortedArrayList(this.root(), union, 0, union.size() - 1);
    }

    /**
     * Depth first traverse the ExtendedAVLTree in Pre-order to draw the tree.
     * @param g
     */
    public void print(Graphics g)
    {   
        int start_pos;
        
        // Estimate graphical width of the tree by calculating tree depth to
        // figure out position of the root node.
        int canvas_width = (int)(this.width() * node_size * 0.3);
        // This is position of the root node
        start_pos = canvas_width / 2;
        
        // Pre-order depth traverse and draw the tree
        printNodes(this.root(), g, start_pos, node_size * 2, this.height());
    }

    /**
     * This is an internal recursive function trying to do depth first traverse (Pre-order)
     * to copy all nodes of the original tree to the new tree.
     * Time complexity: O(n)
     * @param new_node
     * @param old_node
     */
    private void traverseAndCopy(Position<Entry<K, V>> new_node, Position<Entry<K, V>> old_node, AVLTree<K, V> old_tree)
    {
        // External position indicates that the recursion terminates
        if (old_tree.isExternal(old_node))
        {
            return;
        }

        K key = old_node.element().getKey();
        V value = old_node.element().getValue();
        this.insertAtExternal(new_node, new BSTEntry<K, V>(key, value, new_node));

        traverseAndCopy(this.left(new_node), old_tree.left(old_node), old_tree);

        traverseAndCopy(this.right(new_node), old_tree.right(old_node), old_tree);
        
        this.setHeight(new_node);
    }
    
    /**
     * In-order depth first traverse the tree to add all elements into an ArrayList.
     * Time complexity: O(n)
     * @param list
     * @param node
     */
    private void traverseAndAppend(ArrayList<Entry<K, V>> list, Position<Entry<K, V>> node)
    {
        if (this.isExternal(node))
        {
            return;
        }

        traverseAndAppend(list, this.left(node));

        list.add(node.element());

        traverseAndAppend(list, this.right(node));
    }

    /**
     * We assume that the list for input is sorted.
     * This is a recursive function using the method of binary search.
     * Step 1:
     * Pick up the entry which is in the middle of the ArrayList as the root of the new tree.
     * Step 2:
     * Pick up left half of the array (except the middle element) and do depth first
     * traverse (Pre-order) to build left subtree
     * Step 3:
     * Pick up right half of the array (except the middle element) and do depth first
     * traverse (Pre-order) to build right subtree
     * Time complexity: O(n)
     * @param list
     * @return
     */
    private void buildAVLTreeFromSortedArrayList(Position<Entry<K, V>> new_node, ArrayList<Entry<K, V>> list, int start,
            int end)
    {
        if (start < end - 1)
        {
            int mid = (start + end) / 2;
            Entry<K, V> mid_entry = list.get(mid);
            this.insertAtExternal(new_node, mid_entry);

            buildAVLTreeFromSortedArrayList(left(new_node), list, start, mid - 1);
            buildAVLTreeFromSortedArrayList(right(new_node), list, mid + 1, end);
            // Set height of the parent after all of its offsprings have heights 
            this.setHeight(new_node);
        }
        // If there are only two entries left from the array
        else if (start == end - 1)
        {
            this.insertAtExternal(new_node, list.get(end));
            this.insertAtExternal(left(new_node), list.get(start));
            // Set height of the parent after its child has height 
            this.setHeight(new_node);
        }
        // If there is only one entry left from the array
        else if (start == end)
        {
            this.insertAtExternal(new_node, list.get(start));
            // Set height of the new node
            this.setHeight(new_node);
        }
    }
    
    /**
     * Recursive function of drawing a tree.
     * It is Pre-order depth first search.
     * Time Complexity: O(n)
     * @param pos
     * @param g
     * @param x
     * @param y
     */
    private void printNodes(Position<Entry<K, V>> pos, Graphics g, int x, int y, int height)
    {
        if (this.isExternal(pos))
        {
            g.drawRect(x + node_size / 4, y, node_size * 1 / 2, node_size * 1 / 2);
            return;
        }
        
        g.drawOval(x, y, node_size, node_size);
        
        K key = pos.element().getKey();
        V value = pos.element().getValue();

        String str_key = Integer.toString((Integer)key);
        int str_length = str_key.length();
        
        g.drawString(str_key, x + node_size / 2 - str_length * 3, y + node_size / 2 + 6);
        
        int move = (int)(Math.pow(2, height - 1) * node_size * 0.3);
        
        int x_left = x - move;
        int x_right = x + move;
        
        int y_left = y + node_size * 2;
        int y_right = y_left;
        
        g.drawLine(x + node_size / 2, y + node_size, x_left + node_size / 2, y_left);
        g.drawLine(x + node_size / 2, y + node_size, x_right + node_size / 2, y_right);
        
        printNodes(this.left(pos), g, x_left, y_left, height - 1);
        printNodes(this.right(pos), g, x_right, y_right, height - 1);
    }


    public static void main(String[] args)
    {
        Random rd = new Random();
        rd.setSeed(9);

        /*
         * Create the first AVL tree with an external node as the root and the
         * default comparator
         */
        AVLTree<Integer, String> tree1 = new AVLTree<Integer, String>();

        for (int i = 0; i < 15; i++)
            tree1.insert(rd.nextInt(100), "");

        /*
         * Create the second AVL tree with an external node as the root and the
         * default comparator
         */
        AVLTree<Integer, String> tree2 = new AVLTree<Integer, String>();

        for (int i = 0; i < 20; i++)
            tree2.insert(rd.nextInt(100), "");

        ExtendedAVLTree.print(tree1);
        ExtendedAVLTree.print(tree2);
        
        AVLTree <Integer, String> ex_tree1 = ExtendedAVLTree.clone(tree1);
        AVLTree <Integer, String> ex_tree2 = ExtendedAVLTree.clone(tree2);
        
        ExtendedAVLTree.print(ex_tree1);
        ExtendedAVLTree.print(ex_tree2);
        rd.setSeed(99);
        for (int i = 0; i < 14; i++)
            tree1.insert(rd.nextInt(100), "");
        
        for (int i = 0; i < 12; i++)
            tree2.insert(rd.nextInt(100), "");
        
        ExtendedAVLTree.print(tree1);
        ExtendedAVLTree.print(tree2);
        rd.setSeed(99);
        for (int i = 0; i < 14; i++)
            ex_tree1.insert(rd.nextInt(100), "");               
        
        for (int i = 0; i < 12; i++)
            ex_tree2.insert(rd.nextInt(100), "");
        
        ExtendedAVLTree.print(ex_tree1);
        ExtendedAVLTree.print(ex_tree2);
        
        ExtendedAVLTree.print(ExtendedAVLTree.merge(ex_tree1, ex_tree2));
        
        AVLTree<Integer, String> tree3 = new AVLTree<Integer, String>();
        ExtendedAVLTree.print(tree3);
        
        tree3.insert(3, "");
        tree3.insert(5,  "");
        ExtendedAVLTree.print(tree3);
        
        AVLTree<Integer, String> tree4 = new AVLTree<Integer, String>();
        AVLTree<Integer, String> tree5 = new AVLTree<Integer, String>();
        for (int i = 0; i < 20; i++)
        {
            if (i % 2 == 0)
            {
                tree5.insert(i, "");
            }
            else
            {
                tree4.insert(i, "");
            }
        }
        
        AVLTree<Integer, String> tree6 = new ExtendedAVLTree<Integer, String>(tree4, tree5);
        
        ExtendedAVLTree.print(tree4);
        ExtendedAVLTree.print(tree5);
        ExtendedAVLTree.print(tree6);
    }
}
