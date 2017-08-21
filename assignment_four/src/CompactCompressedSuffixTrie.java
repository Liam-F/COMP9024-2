import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/************************************************
 * COMP9024 Assignment 4
 * 
 * @author Chunnan Sheng
 *
 ************************************************/

public class CompactCompressedSuffixTrie
{
    /**
     * Node of the suffix trie containing the start index and
     * end index of the original string.
     * Since there are at most 5 children for each trie node:
     * A, T, C, G and $, there is an array size of 5 to record
     * references of these children.
     * @author Chunnan Sheng
     *
     */
    private class TrieNode
    {
        private int m_start_index;
        private int m_end_index;

        private TrieNode[] m_children;

        public TrieNode(int start, int end)
        {
            this.m_start_index = start;
            this.m_end_index = end;

            this.m_children = new TrieNode[5];
        }

        public TrieNode()
        {
            this.m_start_index = -1;
            this.m_end_index = -1;

            this.m_children = new TrieNode[5];
        }

        public int getStart()
        {
            return this.m_start_index;
        }

        public int getEnd()
        {
            return this.m_end_index;
        }

        public void setEnd(int end)
        {
            this.m_end_index = end;
        }

        /**
         * Get a child via the beginning char of this child
         * A is index 0 of the array.
         * T is index 1 of the array.
         * G is index 2 of the array.
         * C is index 3 of the array.
         * $ is index 4 of the array.
         * @param head
         * @return
         */
        public TrieNode getChild(char head)
        {
            TrieNode child = null;
            switch (head)
            {
            case 'A':
                child = this.m_children[0];
                break;
            case 'T':
                child = this.m_children[1];
                break;
            case 'G':
                child = this.m_children[2];
                break;
            case 'C':
                child = this.m_children[3];
                break;
            case '$':
                child = this.m_children[4];
                break;
            default:
            }

            return child;
        }

        /**
         * Assign a new trie node as a child to this trie node
         * @param head
         * @param node
         */
        public void newChild(char head, TrieNode node)
        {
            switch (head)
            {
            case 'A':
                this.m_children[0] = node;
                break;
            case 'T':
                this.m_children[1] = node;
                break;
            case 'G':
                this.m_children[2] = node;
                break;
            case 'C':
                this.m_children[3] = node;
                break;
            case '$':
                this.m_children[4] = node;
                break;
            default:
            }
        }

        public TrieNode[] allChildren()
        {
            return this.m_children;
        }

        public void setAllChildren(TrieNode[] children)
        {
            this.m_children = children;
        }

        public void clearAllChildren()
        {
            this.m_children = new TrieNode[5];
        }
    }

    private TrieNode m_trie_root;
    private char[] m_entire_sequence;

    /**
     * Try to add a new suffix into the trie.
     * <p>
     * In this implementation, the worst time complexity to add a new suffix is O(n), when
     * all characters of the new suffix fit the longest route from the root to the leaf in
     * the trie except the last character of this suffix.
     * <p>
     * In contrast, the best time complexity to add a new suffix is O(1) if the entire
     * suffix is a new child of the root.
     * <p>
     * I cannot rigorously demonstrate the accurate average time complexity of this trie
     * implementation. However, it is apparent that how balanced the trie is will
     * significantly affect the time efficiency to build this trie.
     * <p>
     * Generally, if this trie is roughly balanced, the time complexity to add a new
     * suffix to this trie is between O(1) and O(n). So the whole time complexity to
     * build this trie is between O(n) and O(n * n). 
     * 
     * @param f_array
     * @param start
     * @param end
     */
    private void addSuffixToTrie(char[] f_array, int start, int end)
    {
        // Start from the route node of the trie
        TrieNode current_node = this.m_trie_root;
        for (int i = start; i <= end;)
        {
            // We should compare the characters between the trie node and the
            // suffix, if the trie node is not a root
            if (this.m_trie_root != current_node)
            {
                int j = current_node.getStart();
                int k = i;
                boolean mismatch = false;
                for (; j <= current_node.getEnd(); j++, k++)
                {
                    // Since each trie node always ends with '$'.
                    // So, if this loop meets the end of this trie node,
                    // there is always mismatch.
                    if (f_array[j] != f_array[k])
                    {
                        mismatch = true;
                        // If there is mismatch, truncate the trie node into two
                        // nodes
                        // at the location of mismatch.
                        // Children of the original trie node will become
                        // children
                        // of the latter one (child 1).
                        // The new suffix will also create a new node (child 2).
                        int old_node_end = current_node.getEnd();
                        current_node.setEnd(j - 1);

                        TrieNode child_1 = new TrieNode(j, old_node_end);
                        child_1.setAllChildren(current_node.allChildren());
                        current_node.clearAllChildren();
                        current_node.newChild(f_array[j], child_1);

                        TrieNode child_2 = new TrieNode(k, end);
                        current_node.newChild(f_array[k], child_2);

                        break;
                    }
                }

                if (mismatch) // Mismatch has happened and a new trie node has to
                              // be created for the new suffix. Finish the search
                              // and insertion.
                {
                    break;
                }
                else // No mismatch is found, and search in the current trie node has
                     // been finished
                {
                    // Move the index to the place right after the finish point of current
                    // node and continue the search into children of current node
                    i = k;
                }
            }

            // If the trie node does not have a child starting with this
            // character, Create a new child for this suffix
            TrieNode child = current_node.getChild(f_array[i]);
            if (null == child)
            {
                child = new TrieNode(i, end);
                current_node.newChild(f_array[i], child);
                break;
            }
            // If the trie has a child starting with this character
            else
            {
                current_node = child;
            }
        }
    }

    /**
     * This method is to traverse and print the trie to check programming errors.
     * @param current_node
     * @param from_root_to_leaf
     * @return
     */
    public StringBuilder toStringBuilder(TrieNode current_node, ArrayList<TrieNode> from_root_to_leaf)
    {
        StringBuilder sb = new StringBuilder();

        if (null != current_node)
        {
            boolean has_children = false;

            if (current_node.getChild('A') != null)
            {
                has_children = true;
                from_root_to_leaf.add(current_node.getChild('A'));
                sb.append(toStringBuilder(current_node.getChild('A'), from_root_to_leaf));
                from_root_to_leaf.remove(from_root_to_leaf.size() - 1);
            }

            if (current_node.getChild('T') != null)
            {
                has_children = true;
                from_root_to_leaf.add(current_node.getChild('T'));
                sb.append(toStringBuilder(current_node.getChild('T'), from_root_to_leaf));
                from_root_to_leaf.remove(from_root_to_leaf.size() - 1);
            }

            if (current_node.getChild('G') != null)
            {
                has_children = true;
                from_root_to_leaf.add(current_node.getChild('G'));
                sb.append(toStringBuilder(current_node.getChild('G'), from_root_to_leaf));
                from_root_to_leaf.remove(from_root_to_leaf.size() - 1);
            }

            if (current_node.getChild('C') != null)
            {
                has_children = true;
                from_root_to_leaf.add(current_node.getChild('C'));
                sb.append(toStringBuilder(current_node.getChild('C'), from_root_to_leaf));
                from_root_to_leaf.remove(from_root_to_leaf.size() - 1);
            }

            if (current_node.getChild('$') != null)
            {
                has_children = true;
                from_root_to_leaf.add(current_node.getChild('$'));
                sb.append(toStringBuilder(current_node.getChild('$'), from_root_to_leaf));
                from_root_to_leaf.remove(from_root_to_leaf.size() - 1);
            }

            if (!has_children)
            {
                for (TrieNode node : from_root_to_leaf)
                {
                    sb.append('-');
                    if (node.getStart() >= 0)
                    {
                        for (int i = node.getStart(); i <= node.getEnd(); i++)
                        {
                            sb.append(this.m_entire_sequence[i]);
                        }
                    }
                    sb.append('-');
                }
                sb.append('\n');
            }
        }

        return sb;
    }

    /**
     * Override the original toString()
     */
    public String toString()
    {
        ArrayList<TrieNode> from_root_to_leaf = new ArrayList<TrieNode>();
        from_root_to_leaf.add(m_trie_root);
        StringBuilder sb = this.toStringBuilder(m_trie_root, from_root_to_leaf);
        return sb.toString();
    }

    /**
     * Method for finding the first occurrence of a pattern in the DNA sequence using the trie.
     * The worst time complexity of this search is length of the entire sequence (O(n)) if the
     * trie is totally unbalanced, and the mismatch is found right at the end of the search.
     * <p>
     * If the trie is roughly balanced, the time complexity is less than O(n) but is till larger
     * than O(log(n)) since each character in each node of a certain route in the tree has to be
     * checked until the pattern is confirmed existing in the sequence, or there is a mismatch found.
     * <p>
     * This piece of code is derived from the pseudo-code of Lecture Week 9
     * 
     */
    public int findString(String s)
    {
        char[] pattern = s.toCharArray();
        int pat_len = s.length();
        TrieNode cur_node = this.m_trie_root;
        boolean head_match = false;

        for (int j = 0; j < s.length();)
        {
            TrieNode[] children = cur_node.allChildren();
            for (int k = 0; k < children.length; k++)
            {
                if (null == children[k])
                {
                    continue;
                }

                int i = children[k].getStart();

                if (pattern[j] == this.m_entire_sequence[i])
                {
                    head_match = true;
                    int node_len = children[k].getEnd() - i + 1;

                    if (pat_len <= node_len)
                    {
                        boolean mismatch = false;
                        for (int m = j, n = i; m < j + pat_len; m++, n++)
                        {
                            if (pattern[m] != this.m_entire_sequence[n])
                            {
                                mismatch = true;
                                break;
                            }
                        }

                        if (!mismatch)
                        {
                            return i - j;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                    else
                    {
                        boolean mismatch = false;
                        for (int m = j, n = i; m < j + node_len; m++, n++)
                        {
                            if (pattern[m] != this.m_entire_sequence[n])
                            {
                                mismatch = true;
                                break;
                            }
                        }

                        if (!mismatch)
                        {
                            pat_len = pat_len - node_len;
                            j = j + node_len;
                            cur_node = children[k];
                            break;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                }
            }

            if (head_match == false)
            {
                break;
            }
        }
        return -1;
    }
    
    
    /**
     * Coordinate of a matrix item
     * @author Chunnan Sheng
     *
     */
    private class Coordinate
    {
        private int m_i;
        private int m_j;

        public Coordinate(int i, int j)
        {
            this.m_i = i;
            this.m_j = j;
        }

        public int getI()
        {
            return this.m_i;
        }

        public int getJ()
        {
            return this.m_j;
        }
        
        /**
         * Hashing of this class should be overridden.
         */
        public int hashCode()
        {
            return Objects.hash(this.m_i, this.m_j);
        }
        
        /**
         * This method should be overridden for hash table too.
         */
        public boolean equals(Object obj)
        {
            if (null == obj || !(obj instanceof Coordinate))
            {
                return false;
            }
            
            Coordinate cd = (Coordinate)obj;
            if (cd.m_i != this.m_i || cd.m_j != this.m_j)
            {
                return false;
            }
            
            return true;
        }
    }

    /**
     * The CommonSubString is exactly NOT a real string because it
     * has only start and end coordinates of the substring in the matrix.
     * @author Chunnan Sheng
     *
     */
    private class CommonSubString
    {
        private Coordinate m_start;
        private Coordinate m_end;

        public CommonSubString(Coordinate start, Coordinate end)
        {
            this.m_start = start;
            this.m_end = end;
        }

        public Coordinate getStart()
        {
            return this.m_start;
        }

        public Coordinate getEnd()
        {
            return this.m_end;
        }
        
        public boolean equals(Object obj)
        {
            if (null == obj || !(obj instanceof CommonSubString))
            {
                return false;
            }
            
            CommonSubString css = (CommonSubString)obj;
            if (!this.m_start.equals(css.m_start))
            {
                return false;
            }
            
            return true;
        }
    }
    
    /**
     * This is the 2D matrix used for dynamic programming.
     * @author Chunnan Sheng
     *
     */
    private class CompareMatrix
    {
        private class MatrixNode
        {
            private int m_value;
            private Coordinate m_pos;

            public MatrixNode(Coordinate pos, int value)
            {
                this.m_pos = pos;
                this.m_value = value;
            }

            public int getValue()
            {
                return this.m_value;
            }

            public Coordinate getPos()
            {
                return this.m_pos;
            }
        }
        
        /**
         * A node represents a row or a column of the matrix.
         * If a matched sub string is found, corresponding row or column
         * would be marked as occupied. This trick is helpful for dealing
         * with the overlapping problem of common substrings.
         * @author Chunnan Sheng
         *
         */
        private class StringNode
        {
            private char m_value;
            private boolean m_is_occupied;
            
            public StringNode(char v)
            {
                this.m_value = v;
                this.m_is_occupied = false;
            }
            
            public void setOccupied(boolean occupied)
            {
                this.m_is_occupied = occupied;
            }
            
            public boolean isOccupied()
            {
                return this.m_is_occupied;
            }
            
            public char getValue()
            {
                return this.m_value;
            }
        }

        /**
         * The the data stored in the matrix.
         * A hash map is applied here to save memory
         */
        private Map<Coordinate, MatrixNode> m_data;
        
        /**
         * A list of common substrings starting from the longest.
         * Overlapped substrings are already removed
         */
        private ArrayList<CommonSubString> m_longest_com_sub_Strings;
        
        private ArrayList<StringNode> m_rows;
        private ArrayList<StringNode> m_columns;

        /**
         * Get the longest common substring from the matrix.
         * Corresponding rows and columns of the matrix will be marked as occupied
         * after this substring is found, so that none of the next largest common
         * substrings will overlap with this one.
         * <p>
         * Hash table is applied here to store matrix items to save memory. There is
         * only one item containing string length for each common substring in the matrix.
         * During the search process, length of the common substring may grow up.
         * <p>
         * Time complexity of each search is O(m * n)
         * @return
         */
        private CommonSubString getLongestCommonSubString()
        {
            int longest = 0;
            CommonSubString longest_css = null;
            for (int i = 0; i < this.m_rows.size(); i++)
            {
                for (int j = 0; j < this.m_columns.size(); j++)
                {   
                    if (this.m_rows.get(i).getValue() != this.m_columns.get(j).getValue())
                    {
                        continue;
                    }
                    
                    if (this.m_rows.get(i).isOccupied() || this.m_columns.get(j).isOccupied())
                    {
                        continue;
                    }

                    MatrixNode prev_node = this.m_data.get(new Coordinate(i - 1, j - 1));
                    MatrixNode node = null;
                    if (null == prev_node)
                    {
                        node = new MatrixNode(new Coordinate(i, j), 1);
                        this.m_data.put(new Coordinate(i, j), node);                        
                    }
                    else
                    {
                        node = new MatrixNode(new Coordinate(i, j), prev_node.getValue() + 1);
                        this.m_data.put(new Coordinate(i, j), node);

                        // Remove previous node to save memory.
                        // It seems only one node with the largest flag value is needed in the matrix
                        // to represent a common substring.
                        this.m_data.remove(new Coordinate(i - 1, j - 1));
                    }
                    
                    // If there is a longer common sub string than the "longest",
                    // get rid of the old longest one
                    if (node.getValue() > longest)
                    {
                        longest = node.getValue();
                        longest_css = null;
                    }
                    
                    // Skip if the longest_css != null, we only pick up the first longest common sub string
                    // if they have the same length
                    if (node.getValue() == longest && longest_css == null)
                    {
                        int start_i = node.getPos().getI() - node.getValue() + 1;
                        int start_j = node.getPos().getJ() - node.getValue() + 1;
                        Coordinate start = new Coordinate(start_i, start_j);
                        longest_css = new CommonSubString(start, node.getPos());
                    }
                }                
            }
            
            // Do not forget to mark rows and columns of this substring as occupied.
            if (null != longest_css)
            {
                int start_i = longest_css.getStart().getI();
                int start_j = longest_css.getStart().getJ();
                int end_i = longest_css.getEnd().getI();
                int end_j = longest_css.getEnd().getJ();
                
                for (int i = start_i; i <= end_i; i++)
                {
                    this.m_rows.get(i).setOccupied(true);
                }
                
                for (int j = start_j; j <= end_j; j++)
                {
                    this.m_columns.get(j).setOccupied(true);
                }
            }
            
            return longest_css;
        }
        
        /**
         * Construct a 2D matrix to search for k largest common substrings.
         * Time complexity of each search is O(m * n), then the entire time
         * complexity is O(k * m * n)
         * @param f1_array
         * @param f2_array
         * @param k
         */
        public CompareMatrix(char[] f1_array, char[] f2_array, int k)
        {
            this.m_rows = new ArrayList<StringNode>();
            this.m_columns = new ArrayList<StringNode>();
            
            this.m_data = new HashMap<Coordinate, MatrixNode>();
            this.m_longest_com_sub_Strings = new ArrayList<CommonSubString> ();
            
            
            for (int i = 0; i < f1_array.length; i++)
            {
                this.m_rows.add(new StringNode(f1_array[i]));
            }
            
            for (int j = 0; j < f2_array.length; j++)
            {
                this.m_columns.add(new StringNode(f2_array[j]));
            }
            
            // There are K rounds to get largest common substrings
            for (int m = 0; m < k; m++)
            {
                CommonSubString css = getLongestCommonSubString();
                if (null != css)
                {
                    this.m_longest_com_sub_Strings.add(css);
                }
            }
        }
        
        /**
         * Get characters of common sub strings because there are only start and end
         * coordinates in the instances of CommonSubString
         * @return
         */
        public String[] getLongestCommonSubStrings()
        {
            String[] str_array = new String[this.m_longest_com_sub_Strings.size()];
            
            for (int i = 0; i < this.m_longest_com_sub_Strings.size(); i++)
            {
                CommonSubString css = this.m_longest_com_sub_Strings.get(i);
                StringBuilder sb = new StringBuilder();
                int start_i = css.getStart().getI();
                int end_i = css.getEnd().getI();
                
                for (int j = start_i; j <= end_i; j++)
                {
                    sb.append(this.m_rows.get(j).getValue());
                }
                
                str_array[i] = sb.toString();
            }
            
            return str_array;
        }
    }
    
    /**
     * Building a trie from a file of DNA sequence.
     * The time complexity to build this trie is between O(n) and O(n * n).
     * <p>
     * For more details of the documentation, please see the declaration of
     * method addSuffixToTrie(char[] f_array, int start, int end)
     * @param f
     */
    public CompactCompressedSuffixTrie(String f) // Create a compact compressed
                                                 // suffix trie from file f
    {
        StringBuilder sb = readFromFile(f);
        sb.append('$');
        this.m_entire_sequence = sb.toString().toCharArray();
        this.m_trie_root = new TrieNode();

        for (int i = 0; i < this.m_entire_sequence.length; i++)
        {
            /**
             * Time complexity of this method is between O(1) and O(n).
             * <p>
             * See declaration of this method for more details of documentation.
             */
            this.addSuffixToTrie(this.m_entire_sequence, i, this.m_entire_sequence.length - 1);
        }
    }
    
    public CompactCompressedSuffixTrie()
    {
    }
    
    /**
     * Build a 2D matrix to deal with the search problem of common substrings.
     * The time complexity to search for k largest common substrings is O(k * m * n).
     * <p>
     * See class definitions and method declarations of CompareMatrix for more descriptions of
     * this algorithm.
     * @param f1
     * @param f2
     * @param f3
     * @param k
     */
    public void kLongest(String f1, String f2, String f3, int k)
    {
        StringBuilder sb1, sb2;
        sb1 = readFromFile(f1);
        sb2 = readFromFile(f2);
        char[] str1 = sb1.toString().toCharArray();
        char[] str2 = sb2.toString().toCharArray();
              
        CompareMatrix matrix = new CompareMatrix(str1, str2, k);
        String [] longest_com_sub_strings = matrix.getLongestCommonSubStrings();
       
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longest_com_sub_strings.length; i++)
        {
            sb.append((i + 1) + ": ");
            sb.append(longest_com_sub_strings[i]);
            sb.append('\n');
        }
        
        // System.out.println(sb.toString());
        
        writeToFile(sb.toString(), f3);
    }

    /**
     * Method for finding k longest common substrings of two DNA sequences
     * stored in the text files f1 and f2
     */
    public static void kLongestSubstrings(String f1, String f2, String f3, int k)
    {
        new CompactCompressedSuffixTrie().kLongest(f1, f2, f3, k);
    }
    
    
    public static StringBuilder readFromFile(String f)
    {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            // Read all the text from the file
            br = new BufferedReader(new FileReader(f));
            int chr = -1;
            while ((chr = br.read()) != -1)
            {
                if ((char) chr == 'G' || (char) chr == 'T' || (char) chr == 'A' || (char) chr == 'C')
                {
                    sb.append((char) chr);
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.out.println("input error when reading the attribute of the task X");
        }
        finally
        {
            if (null != br)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return sb;
    }
    
    public static void writeToFile(String data, String file_name)
    {
        try
        {
            File out_file = new File(file_name);
            out_file.createNewFile(); // if file already exists will do nothing
            FileOutputStream fos = new FileOutputStream(out_file, false);
            PrintWriter pw = null;

            pw = new PrintWriter(fos, true);
            pw.print(data);
            pw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception
    {
        /**
         * Construct a compact compressed suffix trie named trie1
         */
        CompactCompressedSuffixTrie trie1 = new CompactCompressedSuffixTrie("file1.txt");
        // System.out.println(trie1.toString());

        System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

        System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));
        
        System.out.println("GTAAGTATAAAACAACTTCGTAAGTA is at: " + trie1.findString("GTAAGTATAAAACAACTTCGTAAGTA"));
        
        System.out.println("TAAAACAACTTCGTAAGTATAAAACAACTTCGTAAGTATAAAACAACTTCGTAAGTA is at: " + trie1.findString("TAAAACAACTTCGTAAGTATAAAACAACTTCGTAAGTATAAAACAACTTCGTAAGTA"));

        System.out.println("ACTTCGTAAGGTT is at: " + trie1.findString("ACTTCGTAAGGTT"));
        
        CompactCompressedSuffixTrie.kLongestSubstrings("file2.txt", "file3.txt", "file4.txt", 6);
    }
}