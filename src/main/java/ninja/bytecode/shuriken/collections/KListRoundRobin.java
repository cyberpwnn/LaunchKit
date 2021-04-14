package ninja.bytecode.shuriken.collections;

import java.util.concurrent.atomic.AtomicInteger;

public class KListRoundRobin<T> implements RoundRobin<T> {
    private final AtomicInteger cursor;
    private final KList<T> data;

    public KListRoundRobin(KList<T> data)
    {
        this.data = data;
        this.cursor = new AtomicInteger(0);
    }

    @Override
    public boolean hasNext() {
        return data.isNotEmpty();
    }

    @Override
    public boolean isEmpty() {
        return !hasNext();
    }

    @Override
    public T next() {
        if(isEmpty())
        {
            return null;
        }

        int index = cursor.getAndIncrement();

        if(!data.hasIndex(index))
        {
            cursor.set(0);
            index = 0;
        }

        return data.getOrNull(index);
    }
}
