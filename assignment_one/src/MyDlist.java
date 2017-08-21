
/////////////////////////////////////
// 
//    COMP9024 Assignment 1
//
//    Programmed by Chunnan Sheng
//    Student Code: 5100764
//
/////////////////////////////////////

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 
 * @author Chunnan Sheng
 *
 */
public class MyDlist extends DList
{
    private static boolean debug = false;

    /**
     * Since advanced JAVA data structures are NOT allowed in this assignment, I
     * need to create a hash table class by myself.
     * 
     * @author Chunnan Sheng
     *
     */
    private class HashSet
    {
        /**
         * Hash node definition. The Hash node is a single linked list in case
         * there are hash collisions.
         * 
         * @author Chunnan Sheng
         */
        private class HashNode
        {
            private String myKey;
            private HashNode myNext;

            public HashNode(String key)
            {
                this.myKey = key;
                this.myNext = null;
            }

            public String getKey()
            {
                return this.myKey;
            }

            public void setKey(String key)
            {
                this.myKey = key;
            }

            public void setNext(HashNode next)
            {
                this.myNext = next;
            }

            public HashNode getNext()
            {
                return this.myNext;
            }
        }

        // The hash array will be stored here
        // Hash code will be index of this array
        private HashNode[] all_hash_data;

        // Size of hash array
        // Therefore the index of hash array will be
        // from 0 to (myHashSize - 1)
        private int myHashSize;

        // Number of keys stored in this hash table
        private int mySize;

        /**
         * This is a simple hash algorithm. The hash code returned by this
         * function will be used as an index of a hash node in the hash array.
         * 
         * @param key
         * @return hash code
         */
        private int generateHashCode(String key)
        {
            int beginner = 0;
            int result;

            for (char c : key.toCharArray())
            {
                beginner *= 31;
                beginner += (int) c;
            }

            result = beginner % this.myHashSize;
            if (result < 0)
            {
                result += this.myHashSize;
            }

            return result;
        }

        /**
         * Constructor of the hash table Hash size should not be less than 1
         * 
         * @param hash_size
         */
        public HashSet(int hash_size)
        {
            this.myHashSize = hash_size < 1 ? 1 : hash_size;
            this.all_hash_data = new HashNode[this.myHashSize];
            this.mySize = 0;
        }

        /**
         * Get number of keys of this hash table
         * 
         * @return
         */
        public int size()
        {
            return this.mySize;
        }

        /**
         * Print this hash table for debug and test
         */
        public void print()
        {
            if (!debug)
            {
                return;
            }

            for (int i = 0; i < this.myHashSize; i++)
            {
                HashNode the_node = this.all_hash_data[i];

                if (the_node != null)
                {
                    System.out.print(i + " ");
                    HashNode next_node = the_node;
                    while (next_node != null)
                    {
                        System.out.print(next_node.getKey() + " ");
                        next_node = next_node.getNext();
                    }
                    System.out.println();
                }
                else
                {
                    System.out.println(i);
                }
            }

            System.out.println();
        }

        /**
         * Add a new key to the hash table
         * 
         * @param key
         */
        public void put(String key)
        {
            // Generate the hash code of the key
            int index = generateHashCode(key);

            // Add a new hash node to hash array via hash code
            if (this.all_hash_data[index] == null)
            {
                this.all_hash_data[index] = new HashNode(key);
                this.mySize++;
            }
            // If there is hash node already existing with this index,
            // check all keys in this single linked list
            else
            {
                HashNode next_node = this.all_hash_data[index];
                while (true)
                {
                    // If there is a duplicate key (key already exists),
                    // ignore it!!!
                    if (next_node.getKey().equals(key))
                    {
                        break;
                    }
                    // If there is no duplicate key,
                    // try to find the last node of this linked list,
                    // and append a new node to the end of this linked list
                    else if (next_node.getNext() == null)
                    {
                        next_node.setNext(new HashNode(key));
                        this.mySize++;
                        break;
                    }
                    else
                    {
                        next_node = next_node.getNext();
                    }
                }
            }
        }

        /**
         * Search in the hash table to check if this key exits in this hash
         * table
         * 
         * @param key
         * @return true or false
         */
        public boolean containsKey(String key)
        {
            int index = generateHashCode(key);

            // If the corresponding hash node to this index is NULL,
            // it is obvious that this key does not exist in this hash table
            if (this.all_hash_data[index] == null)
            {
                return false;
            }
            else
            {
                // If there exists a hash node of this index,
                // traverse the linked list to search for the key.
                HashNode next_node = this.all_hash_data[index];
                while (true)
                {
                    // If the key is found, return true
                    if (next_node.getKey().equals(key))
                    {
                        return true;
                    }
                    // If the key is not found, and you are already at the last
                    // node of this list,
                    // the key does not exist.
                    else if (next_node.getNext() == null)
                    {
                        return false;
                    }
                    // If the key is not found, but there are next nodes to
                    // visit,
                    // continue the loop.
                    else
                    {
                        next_node = next_node.getNext();
                    }
                }
            }
        }

        /**
         * Traverse the entire hash table to find out all keys.
         * 
         * @return a list of all keys
         */
        public String[] allKeys()
        {
            String[] all_keys = new String[this.mySize];
            int index = 0;
            for (int i = 0; i < this.myHashSize; i++)
            {
                HashNode the_node = this.all_hash_data[i];

                if (the_node != null)
                {
                    HashNode next_node = the_node;
                    while (next_node != null)
                    {
                        all_keys[index] = next_node.getKey();
                        index++;
                        next_node = next_node.getNext();
                    }
                }
            }

            return all_keys;
        }

    }

    /***
     * Private function of reading text data from text file or console input
     * 
     * @param file_name
     * @return all text (\r\n replaced by spaces)
     * @throws IOException
     */
    private String readFromFile(String file_name) throws IOException
    {
        String text;
        Reader the_reader;

        if (file_name == "stdin")
        {
            // Read from keyboard input
            the_reader = new InputStreamReader(System.in);
        }
        else
        {
            // Read from file
            the_reader = new FileReader(file_name);
        }

        // Keyboard input and file input share the same logic,
        // while keyboard input would terminate after double returns
        try (BufferedReader br = new BufferedReader(the_reader))
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null && (line.length() > 0 || file_name != "stdin"))
            {
                sb.append(line);
                sb.append(" ");
                line = br.readLine();
            }
            text = sb.toString();
        }

        return text;
    }

    /**
     * Default constructor is the same as super class
     */
    public MyDlist()
    {
        super();
    }

    /**
     * Constructor with text file name as input. If the name is "stdin", then
     * get input from keyboard. Insert all words of this text file or keyboard
     * input into this list
     * 
     * @param f
     */
    public MyDlist(String f)
    {
        super();

        String text = "";
        try
        {
            text = readFromFile(f);
        }
        catch (IOException ex)
        {
            // do nothing
        }

        String[] words = text.split("[ \t\r\n]+");
        HashSet pool = new HashSet(words.length);

        // Remove duplicate elements using hash table
        for (String word : words)
        {
            pool.put(word);
        }
        // Add all elements from hash table to new list
        for (String key : pool.allKeys())
        {
            DNode new_node = new DNode(key, null, null);
            super.addLast(new_node);
        }

        pool.print();
    }

    /***
     * Print this list
     */
    public void print()
    {
        if (this.size() > 0)
        {
            DNode node = this.getFirst();
            while (node.element != null)
            {
                System.out.println(node.element);
                node = node.getNext();
            }
        }

        System.out.println();
    }

    /***
     * Copy all element of this list to a new list
     * 
     * @return The new list
     */
    public MyDlist clone()
    {
        // Create a new empty list
        MyDlist new_list = new MyDlist();

        // Copy all nodes to the new list
        DNode node = this.getFirst();
        while (node.element != null)
        {
            new_list.addLast(new DNode(node.getElement(), null, null));
            node = node.getNext();
        }
        return new_list;
    }

    /**
     * Combine two lists. Elements of both these list are inserted into a hash
     * table as keys. Duplicate keys will be merged, so all elements are unique
     * in this hash table. And then copy all elements of this hash table back to
     * a new list.
     * 
     * Time complexity: We assume that lengths of list_A and list_B are m and n.
     * Then the size of this hash table should be no more than m + n. Time
     * complexities of hash table search and insertion are both O(1). Therefore
     * the entire time complexity would be O(m + n), or simply O(N)
     * 
     * @param other
     * @return the new union list
     */
    public MyDlist union(MyDlist other)
    {
        // Create a hash table.
        HashSet pool = new HashSet(this.size + other.size);

        if (this.size > 0)
        {
            // Put all elements of this list into the hash table.
            DNode node = this.getFirst();
            while (node.element != null)
            {
                pool.put(node.getElement());
                node = node.getNext();
            }
        }

        if (other.size > 0)
        {
            // Put all elements of the other list into the hash table.
            // Duplicate elements will be merged.
            // Therefore all elements in this hash table are unique.
            DNode node = other.getFirst();
            while (node.element != null)
            {
                pool.put(node.getElement());
                node = node.getNext();
            }
        }

        pool.print();

        // Put all elements of this hash table into a new list
        MyDlist new_list = new MyDlist();

        for (String key : pool.allKeys())
        {
            new_list.addLast(new DNode(key, null, null));
        }

        return new_list;
    }

    /***
     * Intersection of two lists. Elements of each list are inserted into a
     * corresponding hash table as keys. Duplicate keys will be merged, so all
     * elements are unique in these hash tables. And then copy elements which
     * exist in both hash tables back to a new list.
     * 
     * Time complexity: We assume that lengths of list_A and list_B are m and n.
     * Then the sizes of hash table A and hash table B are at most m and n. So
     * size of the new list is at most min(m, n). Time complexities of hash
     * table search and insertion are both O(1). Therefore the entire time
     * complexity would be O(m + n), or simply O(N)
     * 
     * @param other
     * @return the new list of intersection
     */
    public MyDlist intersection(MyDlist other)
    {
        // Create two hash tables
        HashSet pool_this = new HashSet(this.size);
        HashSet pool_other = new HashSet(other.size);

        // Put elements of this list into the first hash table.
        if (this.size > 0)
        {
            DNode node = this.getFirst();
            while (node.element != null)
            {
                pool_this.put(node.getElement());
                node = node.getNext();
            }
        }

        // Put elements of the other list into the second hash table.
        if (other.size > 0)
        {
            DNode node = other.getFirst();
            while (node.element != null)
            {
                pool_other.put(node.getElement());
                node = node.getNext();
            }
        }

        pool_this.print();
        pool_other.print();

        // Put elements existing in both hash tables into the new list.
        MyDlist new_list = new MyDlist();

        for (String key : pool_this.allKeys())
        {
            if (pool_other.containsKey(key))
            {
                new_list.addLast(new DNode(key, null, null));
            }
        }

        return new_list;
    }

    /***
     * Print the linked list
     * 
     * @param u
     */
    public static void printList(MyDlist u)
    {
        u.print();
    }

    /***
     * Copy the Double linked list into a new list
     *
     * @param u
     * @return
     */
    public static MyDlist cloneList(MyDlist u)
    {
        return u.clone();
    }

    /**
     * Combine two lists.
     * 
     * @param u
     * @param v
     * @return
     */
    public static MyDlist union(MyDlist u, MyDlist v)
    {
        return u.union(v);
    }

    /**
     * Intersection of two lists.
     * 
     * @param u
     * @param v
     * @return
     */
    public static MyDlist intersection(MyDlist u, MyDlist v)
    {
        return u.intersection(v);
    }

    /**
     * Main function of this program
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println("please type some strings, one string each line and an empty line for the end of input:");
        /**
         * Create the first doubly linked list by reading all the strings from
         * the standard input.
         */
        MyDlist firstList = new MyDlist("stdin");

        /** Print all elememts in firstList */
        printList(firstList);

        /**
         * Create the second doubly linked list by reading all the strings from
         * the file myfile that contains some strings.
         */

        /** Replace the argument by the full path name of the text file */
        MyDlist secondList = new MyDlist("myfile.txt");

        /** Print all elements in secondList */
        printList(secondList);

        /** Clone firstList */
        MyDlist thirdList = cloneList(firstList);

        /** Print all elements in thirdList. */
        printList(thirdList);

        /** Clone secondList */
        MyDlist fourthList = cloneList(secondList);

        /** Print all elements in fourthList. */
        printList(fourthList);

        /** Compute the union of firstList and secondList */
        MyDlist fifthList = union(firstList, secondList);

        /** Print all elements in thirdList. */
        printList(fifthList);

        /** Compute the intersection of thirdList and fourthList */
        MyDlist sixthList = intersection(thirdList, fourthList);

        /** Print all elements in fourthList. */
        printList(sixthList);
    }
}
