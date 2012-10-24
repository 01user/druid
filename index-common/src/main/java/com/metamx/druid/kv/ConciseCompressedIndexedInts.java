package com.metamx.druid.kv;


import com.google.common.collect.Ordering;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 */
public class ConciseCompressedIndexedInts implements IndexedInts, Comparable<ConciseCompressedIndexedInts>
{
  public static ObjectStrategy<ImmutableConciseSet> objectStrategy =
      new ImmutableConciseSetObjectStrategy();

  private static Ordering<ImmutableConciseSet> comparator = new Ordering<ImmutableConciseSet>()
  {
    @Override
    public int compare(
        @Nullable ImmutableConciseSet conciseSet, @Nullable ImmutableConciseSet conciseSet1
    )
    {
      if (conciseSet.size() == 0 && conciseSet1.size() == 0) {
        return 0;
      }
      if (conciseSet.size() == 0) {
        return -1;
      }
      if (conciseSet1.size() == 0) {
        return 1;
      }
      return conciseSet.compareTo(conciseSet1);
    }
  }.nullsFirst();

  private final ImmutableConciseSet immutableConciseSet;

  public ConciseCompressedIndexedInts(ImmutableConciseSet conciseSet)
  {
    this.immutableConciseSet = conciseSet;
  }

  @Override
  public int compareTo(ConciseCompressedIndexedInts conciseCompressedIndexedInts)
  {
    return immutableConciseSet.compareTo(conciseCompressedIndexedInts.getImmutableConciseSet());
  }

  @Override
  public int size()
  {
    return immutableConciseSet.size();
  }

  @Override
  public int get(int index)
  {
    throw new UnsupportedOperationException("This is really slow, so it's just not supported.");
  }

  public ImmutableConciseSet getImmutableConciseSet()
  {
    return immutableConciseSet;
  }

  @Override
  public Iterator<Integer> iterator()
  {
    return new Iterator<Integer>()
    {
      IntSet.IntIterator baseIterator = immutableConciseSet.iterator();

      @Override
      public boolean hasNext()
      {
        return baseIterator.hasNext();
      }

      @Override
      public Integer next()
      {
        return baseIterator.next();
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  private static class ImmutableConciseSetObjectStrategy
      implements ObjectStrategy<ImmutableConciseSet>
  {
    @Override
    public Class<? extends ImmutableConciseSet> getClazz()
    {
      return ImmutableConciseSet.class;
    }

    @Override
    public ImmutableConciseSet fromByteBuffer(ByteBuffer buffer, int numBytes)
    {
      buffer.limit(buffer.position() + numBytes);
      return new ImmutableConciseSet(buffer.asReadOnlyBuffer());
    }

    @Override
    public byte[] toBytes(ImmutableConciseSet val)
    {
      if (val == null || val.size() == 0) {
        return new byte[]{};
      }
      return val.toBytes();
    }

    @Override
    public int compare(ImmutableConciseSet o1, ImmutableConciseSet o2)
    {
      return comparator.compare(o1, o2);
    }
  }
}
