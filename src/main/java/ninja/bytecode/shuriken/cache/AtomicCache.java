package ninja.bytecode.shuriken.cache;

import ninja.bytecode.shuriken.math.M;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class AtomicCache<T>
{
	private transient volatile T t;
	private transient volatile long a;
	private transient volatile int validations;
	private final ReentrantLock check;
	private final ReentrantLock time;
	private final ReentrantLock write;
	private final boolean nullSupport;

	public AtomicCache()
	{
		this(false);
	}

	public AtomicCache(boolean nullSupport)
	{
		this.nullSupport = nullSupport;
		check = new ReentrantLock();
		write = new ReentrantLock();
		time = new ReentrantLock();
		validations = 0;
		a = -1;
		t = null;
	}

	public void reset()
	{
		check.lock();
		write.lock();
		time.lock();
		a = -1;
		t = null;
		time.unlock();
		write.unlock();
		check.unlock();
	}

	public T aquire(Supplier<T> t)
	{
		if(nullSupport)
		{
			return aquireNull(t);
		}

		if(this.t != null && validations > 1000)
		{
			return this.t;
		}

		if(this.t != null && M.ms() - a > 1000)
		{
			if(this.t != null)
			{
				validations++;
			}

			return this.t;
		}

		check.lock();

		if(this.t == null)
		{
			write.lock();
			this.t = t.get();

			time.lock();

			if(a == -1)
			{
				a = M.ms();
			}

			time.unlock();
			write.unlock();
		}

		check.unlock();
		return this.t;
	}

	public T aquireNull(Supplier<T> t)
	{
		if(validations > 1000)
		{
			return this.t;
		}

		if(M.ms() - a > 1000)
		{
			validations++;
			return this.t;
		}

		check.lock();
		write.lock();
		this.t = t.get();

		time.lock();

		if(a == -1)
		{
			a = M.ms();
		}

		time.unlock();
		write.unlock();
		check.unlock();
		return this.t;
	}
}
