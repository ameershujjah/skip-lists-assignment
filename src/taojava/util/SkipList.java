package taojava.util;

import java.util.Iterator;
import java.util.Random;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Your Name Here
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  T data;
  Node head;
  int maxLevel;
  double probability;
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
    Node<T>[] levels;

    @SuppressWarnings("unchecked")
    public Node(T value, int setLevel)
    {
      this.val = value;

      levels = new Node[setLevel + 1];

      for (int i = 0; i < setLevel; i++)
        {
          levels[i] = null;
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
   * 
   */
  public SkipList(int level)
  {
    this.probability = .5;
    this.maxLevel = level;
    this.head = new Node(null, maxLevel);
    this.size = 0;
  }// SkipList(int level)

  public SkipList(int level, double prob)
  {
    this.probability = prob;
    this.maxLevel = level;
    this.head = new Node(null, maxLevel);
    this.size = 0;
  }

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

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
  public Iterator<T> iterator()
  {
    // S`
    return null;
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
    Node[] update = new Node[this.maxLevel];

    for (int level = this.maxLevel; level >= 1; level--)
      {
        while ((current.levels[level] != null)
               && val.compareTo((T) current.levels[level].val) > 0)
          {
            current = current.levels[level];
          } // while
        update[level] = current;
      } // for

    current = current.levels[1];

    if (current != null && val.compareTo((T) current.val) == 0)
      {
        return;
      } // if, the val already exists, then don't add anything
    else
      {
        int newLevel = randomLevel();
        if (newLevel > this.maxLevel)
          {

            for (int level = this.maxLevel + 1; level <= newLevel; level--)
              {
                update[level] = this.head;
              } // for
            this.maxLevel = newLevel;
          }

        current = new Node(val, newLevel);

        for (int level = 1; level <= newLevel; level++)
          {
            current.levels[level] = update[level].levels[level];
            update[level].levels[level] = current;
          } // for
      } // else
    this.size++;
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    // STUB
    return false;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    // STUB
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    // STUB
    return null;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    return this.size;
  } // length()

} // class SkipList<T>
