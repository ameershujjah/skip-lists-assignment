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
    Node<T>[] next;

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

  @SuppressWarnings("rawtypes")
  public class SkipListIterator
      implements Iterator
  {
    Node current;

    public SkipListIterator()
    {
      current = head;
    } // SkipListIterator()

    public boolean hasNext()
    {
      return current.next[0] != null;
    } // hasNext()

    public T next()
    {
      current = current.next[0];
      return (T) current.val;
    } // next()

    public void remove()
    {
      Node delete = current.next[0];
      for (int i = 0; i <= maxLevel; i++)
        {
          if (current.next[i] != delete)
            break;
          delete.next[i] = current.next[i];
        } //for

      delete = null;
      while ((maxLevel > 1) && (head.next[maxLevel] == null))
        {
          maxLevel--;
        } // while
      size--;
    } // remove()

  } // class SkipListIterator

  public Iterator<T> iterator()
  {
    return new SkipListIterator();
  } // iterator()

  /*
  public Node search(T val)
  {
    Node current = this.head;
    Node[] update = new Node[this.maxLevel];

    for (int level = this.maxLevel; level >= 1; level--)
      {
        while ((current.next[level] != null)
               && val.compareTo((T) current.next[level].val) > 0)
          {
            current = current.next[level];
          } // while
        update[level] = current;
      } // for

    current = current.next[1];
    
    return current;
  }
  */
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
    Node[] update = new Node[this.maxLevel+1];

    for (int level = this.maxLevel; level >= 0; level--)
      {
        while ((current.next[level] != null)
               && val.compareTo((T) current.next[level].val) > 0)
          {
            current = current.next[level];
          } // while
        update[level] = current;
      } // for

    current = current.next[0];

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
          } // if

        current = new Node(val, newLevel);

        for (int level = 0; level <= newLevel; level++)
          {
            current.next[level] = update[level].next[level];
            update[level].next[level] = current;
          } // for
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
      }
    else
      {
        for (int level = this.maxLevel; level >= 0; level--)
          {
            while ((current.next[level] != null)
                   && val.compareTo((T) current.next[level].val) > 0)
              {
                current = current.next[level];
              } // while

          } // for
        current = current.next[0];
       // return val.equals(current.val);
      } 
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
          } // while
        update[level] = current;
      } // for

    current = current.next[0];

    if (current != null && val.compareTo((T) current.val) == 0)
      {
        for (int i = 0; i <= this.maxLevel; i++)
          {
            if (update[i].next[i] != current)
              break;
            update[i].next[i] = current.next[i];

          } //for
        current = null;
        while ((this.maxLevel > 1) && (this.head.next[this.maxLevel] == null))
          {
            this.maxLevel--;
          } // while

        this.size--;
        return;
      } // if, the val already exists, then don't add anything
    else
      {
        return;
      } // else
  }// remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    if ((i < 0) || (i > this.size))
      {
        return null;
      }

    Node current = this.head.next[1];
    if (current == null)
      {
        return null;
      } // if

    for (int pos = 0; pos < i; pos++)
      {
        current = current.next[0];
      } // for

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
