package taojava.util;

import java.util.Iterator;
import java.util.Random;
// Citation : http://michaelnaper.com/samplework/programming/skiplist_java/SkipList.java
// Albert helped us understand the add function. 

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Ameer Shujjah
 * @author Yazan Kittaneh
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The Data field
   */
  T data;

  /**
   * Head of the SkipList
   */
  Node head;

  /**
   * The maximum levels in the list
   */
  int maxLevel;

  /**
   * The probability that defines the distribution of nodes.
   */
  double probability;

  /**
   * The size of the skip list.
   */
  int size;

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node<T>
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    T val;

    /**
     * The levels of the node.
     */
    Node<T>[] next;

    /**
     * Constructs a Node
     */
    @SuppressWarnings("unchecked")
    public Node(T value, int setLevel)
    {
      this.val = value;
      next = new Node[setLevel + 1];

      for (int i = 0; i <= setLevel; i++)
        {
          next[i] = null;
        }// for each level, set to null
    }
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * SkipList default constructor 
   */
  public SkipList()
  {
    this.probability = .5;
    this.maxLevel = 20;
    this.head = new Node(null, 20);
    this.size = 0;
  } //SkipList()

  /**
   * SkipList constructor with maxLevels parameter
   */
  public SkipList(int level)
  {
    this.probability = .5;
    this.maxLevel = level;
    this.head = new Node(null, maxLevel);
    this.size = 0;
  }// SkipList(int)

  /**
   * SkipList Constructor with maxLevel and probability as parameters
   */
  public SkipList(int level, double prob)
  {
    this.probability = prob;
    this.maxLevel = level;
    this.head = new Node(null, maxLevel);
    this.size = 0;
  }// SkipList(int, double)

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+
  /**
   * Generates a random level
   * @return an int, the level
   */
  public int randomLevel()
  {
    int newLevel = 1;
    Random value = new Random();
    while (value.nextDouble() < this.probability)
      {
        newLevel++;
      }// while random is less than the probability
    return Math.min(newLevel, this.maxLevel);
  } // randomLevel()

  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  /**
   * Iterators for SkipList
   */
  @SuppressWarnings("rawtypes")
  public class SkipListIterator
      implements Iterator
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The node iterator points to.
     */
    Node current;

    // +--------------+----------------------------------------------------
    // | Constructors |
    // +--------------+
    /**
     * Construct an iterator
     */
    public SkipListIterator()
    {
      current = head;
    } // SkipListIterator()

    // +--------+--------------------------------------------------------
    // | Methods|
    // +--------+

    /**
     * Check if the iterator has a next element.
     */
    public boolean hasNext()
    {
      return current.next[0] != null;
    } // hasNext()

    /**
     * Return the next element.
     */
    public T next()
    {
      current = current.next[0];
      return (T) current.val;
    } // next()

    /**
     * Remove the current element.
     */
    @SuppressWarnings("unchecked")
    public void remove()
    {
      SkipList.this.remove((T) this.current.val);
    } // remove()
  } // class SkipListIterator

  /**
   * Return a SkipListIterator
   */
  public Iterator<T> iterator()
  {
    return new SkipListIterator();
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  @SuppressWarnings("unchecked")
  public void add(T val)
  {
    if (val == null)
      {
        return;
      } // if value is null, don't add anything

    Node current = this.head;
    Node[] update = new Node[this.maxLevel + 1];

    for (int level = this.maxLevel; level >= 0; level--)
      {
        while ((current.next[level] != null)
               && val.compareTo((T) current.next[level].val) > 0)
          {
            current = current.next[level];
          } // while, search for the preceding node
        update[level] = current;
      } // for, all the levels

    current = current.next[0];

    if (current != null && val.compareTo((T) current.val) == 0)
      {
        return;
      } // if, the val already exists, then don't add anything
    else
      {
        //create a new node with a random level
        int newLevel = randomLevel();

        if (newLevel > this.maxLevel)
          {
            for (int level = this.maxLevel + 1; level <= newLevel; level--)
              {
                update[level] = this.head;
              } // for
            this.maxLevel = newLevel;
          } // if, new level is greater than maxlevel

        current = new Node(val, newLevel);

        for (int level = 0; level <= newLevel; level++)
          {
            current.next[level] = update[level].next[level];
            update[level].next[level] = current;
          } // for, update the pointers
      } // else
    this.size++;
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  @SuppressWarnings("unchecked")
  public boolean contains(T val)
  {
    Node<T> current = this.head;

    if (val == null)
      {
        return false;
      } // if searching for null return false
    else
      {
        for (int level = this.maxLevel; level >= 0; level--)
          {
            while ((current.next[level] != null)
                   && val.compareTo((T) current.next[level].val) > 0)
              {
                current = current.next[level];
              } // while
          } // for, all the levels
        current = current.next[0];
        // return val.equals(current.val);
      } // else if val is not null 

    if (current == null) // if, not found
      return false;
    else
      return val.equals(current.val);
  } // contains(T)

  /**
   * Remove an element from the set.
   *(
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    if (val == null)
      {
        return;
      } // if value is null, don't add anything

    Node current = this.head;
    Node[] update = new Node[this.maxLevel + 1];

    for (int level = this.maxLevel; level >= 0; level--)
      {
        while ((current.next[level] != null)
               && val.compareTo((T) current.next[level].val) > 0)
          {
            current = current.next[level];
          } // while, finding the preceding nodes
        update[level] = current;
      } // for, all the levels

    current = current.next[0];

    if (current != null && val.compareTo((T) current.val) == 0)
      {
        for (int i = 0; i <= this.maxLevel; i++)
          {
            if (update[i].next[i] != current)
              break;
            update[i].next[i] = current.next[i];
          } //for, all the levels update the pointers 
        current = null;
        while ((this.maxLevel > 1) && (this.head.next[this.maxLevel] == null))
          {
            this.maxLevel--;
          } // while

        this.size--;
        return;
      } // if, the val exists, then remove it 
    else
      {
        return;
      } // else, if not found
  }// remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   * @return null, if the index is out of range (index < 0 || index >= length)
   *   otherwise it return the val at index i
   */
  public T get(int i)
  {
    if ((i < 0) || (i > this.size))
      {
        return null;
      } //if index out of bounds

    Node current = this.head.next[0];
    if (current == null)
      {
        return null;
      } // if, list is empty

    for (int pos = 0; pos < i; pos++)
      {
        current = current.next[0];
      } // for, loop through to the index
    return (T) current.val;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    return this.size;
  } // length()

} // class SkipList<T>
