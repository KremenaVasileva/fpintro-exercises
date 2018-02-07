package org.example.chapter2;

import static org.example.Util.error;

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
    }
}


