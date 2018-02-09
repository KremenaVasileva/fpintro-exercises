package org.example.chapter3;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.example.Util.error;
import static org.example.Util.notImplemented;

public abstract class ConsList<A>
{

    public static <A> ConsList<A> empty()
    {
        return new Nil<>();
    }

    public static <A> ConsList<A> cons(A head, ConsList<A> tail)
    {
        return new Cons<>(head, tail);
    }

    public abstract boolean isEmpty();

    public abstract A head();

    public abstract ConsList<A> tail();

    public int length()
    {
        return foldLeft((x, y) -> x + 1, 0);
    }

    public ConsList<A> append(A a)
    {
        return foldRight((x, y) -> cons(x, y), cons(a, empty()));
    }

    public ConsList<A> reverse()
    {
        return foldLeft((x, y) -> cons(y, x), empty());
    }


    public ConsList<A> takeWhile(Predicate<A> predicate)
    {
        return notImplemented();
    }

    public ConsList<A> dropWhile(Predicate<A> predicate)
    {
        return notImplemented();
    }

    public ConsList<A> filter(Predicate<A> predicate)
    {
        return notImplemented();
    }

    public <B> ConsList<B> map(Function<A, B> function)
    {
        return notImplemented();
    }

    public <B> B foldLeft(BiFunction<B, A, B> f, B z)
    {
        B res = z;
        ConsList<A> xs = this;
        while (!xs.isEmpty())
        {
            res = f.apply(res, xs.head());
            xs = xs.tail();
        }

        return res;
    }

    public <B> B foldRight(BiFunction<A, B, B> f, B z)
    {
        return isEmpty() ? z : f.apply(head(), tail().foldRight(f, z));

    }

    private static final class Cons<A> extends ConsList<A>
    {
        public final A head;
        public final ConsList<A> tail;

        public Cons(A head, ConsList<A> tail)
        {
            this.head = head;
            this.tail = tail;
        }

        @Override public boolean isEmpty()
        {
            return false;
        }

        @Override public A head()
        {
            return head;
        }

        @Override public ConsList<A> tail()
        {
            return tail;
        }

        @Override public String toString()
        {
            return "Cons{" + "head=" + head + ", tail=" + tail + '}';
        }
    }


    private static final class Nil<A> extends ConsList<A>
    {

        public Nil()
        {
        }

        @Override public boolean isEmpty()
        {
            return true;
        }

        @Override public A head()
        {
            throw error("head() on Nil");
        }

        @Override public ConsList<A> tail()
        {
            throw error("tail() on Nil");
        }

        @Override public String toString()
        {
            return "Nil()";
        }
    }
}


