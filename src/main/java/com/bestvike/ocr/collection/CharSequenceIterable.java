package com.bestvike.ocr.collection;

import java.util.Iterator;

/**
 * Created by 许崇雷 on 2016/8/2.
 */
public final class CharSequenceIterable implements Iterable<Character> {
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
     * Constructs an CharSequenceIterable that will iterate over the values in the
     * specified charSequence.
     *
     * @param charSequence the charSequence to iterate over.
     */
    public CharSequenceIterable(final CharSequence charSequence) {
        this(charSequence, 0);
    }

    /**
     * Constructs an CharSequenceIterable that will iterate over the values in the
     * specified charSequence from a specific start index.
     *
     * @param charSequence the charSequence to iterate over.
     * @param startIndex   the index to start iterating at.
     */
    public CharSequenceIterable(final CharSequence charSequence, final int startIndex) {
        this(charSequence, startIndex, charSequence.length());
    }

    /**
     * Construct an CharSequenceIterable that will iterate over a range of values
     * in the specified charSequence.
     *
     * @param charSequence the charSequence to iterate over.
     * @param startIndex   the index to start iterating at.
     * @param endIndex     the index to finish iterating at.
     */
    public CharSequenceIterable(final CharSequence charSequence, final int startIndex, final int endIndex) {
        super();

        this.charSequence = charSequence;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * return an Iterator that will iterate over the values in the specified charSequence.
     *
     * @return the Iterator.
     * @throws NullPointerException      if <code>charSequence</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if either index is invalid
     */
    @Override
    public Iterator<Character> iterator() {
        return new CharSequenceIterator(this.charSequence, this.startIndex, this.endIndex);
    }
}
