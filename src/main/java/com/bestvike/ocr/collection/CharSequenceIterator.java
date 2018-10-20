package com.bestvike.ocr.collection;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Created by 许崇雷 on 2016/8/2.
 */
public final class CharSequenceIterator implements Iterator<Character> {
    /**
     * The charSequence to iterate over
     */
    private final CharSequence charSequence;
    /**
     * The start index to loop from
     */
    private final int startIndex;
    /**
     * The end index to loop to
     */
    private final int endIndex;
    /**
     * The current iterator index
     */
    private int index = 0;


    /**
     * Constructs an CharSequenceIterator that will iterate over the values in the
     * specified charSequence.
     *
     * @param charSequence the charSequence to iterate over.
     * @throws NullPointerException if <code>charSequence</code> is <code>null</code>
     */
    public CharSequenceIterator(final CharSequence charSequence) {
        this(charSequence, 0);
    }

    /**
     * Constructs an CharSequenceIterator that will iterate over the values in the
     * specified charSequence from a specific start index.
     *
     * @param charSequence the charSequence to iterate over.
     * @param startIndex   the index to start iterating at.
     * @throws NullPointerException      if <code>charSequence</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public CharSequenceIterator(final CharSequence charSequence, final int startIndex) {
        this(charSequence, startIndex, Array.getLength(charSequence));
    }

    /**
     * Construct an CharSequenceIterator that will iterate over a range of values
     * in the specified charSequence.
     *
     * @param charSequence the charSequence to iterate over.
     * @param startIndex   the index to start iterating at.
     * @param endIndex     the index to finish iterating at.
     * @throws NullPointerException      if <code>charSequence</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if either index is invalid
     */
    public CharSequenceIterator(final CharSequence charSequence, final int startIndex, final int endIndex) {
        super();

        this.charSequence = charSequence;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;

        final int len = charSequence.length();
        this.checkBound(startIndex, len, "start");
        this.checkBound(endIndex, len, "end");
        if (endIndex < startIndex)
            throw new IllegalArgumentException("End index must not be less than start index.");
    }

    /**
     * Gets the charSequence that this iterator is iterating over.
     *
     * @return the charSequence this iterator iterates over.
     */
    public CharSequence getCharSequence() {
        return this.charSequence;
    }

    /**
     * Gets the start index to loop from.
     *
     * @return the start index
     * @since 4.0
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * Gets the end index to loop to.
     *
     * @return the end index
     * @since 4.0
     */
    public int getEndIndex() {
        return this.endIndex;
    }

    /**
     * Checks whether the index is valid or not.
     *
     * @param bound the index to check
     * @param len   the length of the charSequence
     * @param type  the index type (for error messages)
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    protected void checkBound(final int bound, final int len, final String type) {
        if (bound > len)
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s beyond the end of the charSequence. ");
        if (bound < 0)
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s before the start of the charSequence. ");
    }

    /**
     * Returns true if there are more elements to return from the charSequence.
     *
     * @return true if there is a next element to return
     */
    @Override
    public boolean hasNext() {
        return this.index < this.endIndex;
    }

    /**
     * Returns the next element in the charSequence.
     *
     * @return the next element in the charSequence
     * @throws IndexOutOfBoundsException if all the elements in the charSequence
     *                                   have already been returned
     */
    @Override
    public Character next() {
        return this.charSequence.charAt(this.index++);
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }
}
