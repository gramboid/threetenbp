/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendrical;

import javax.time.DateTimes;
import javax.time.LocalTime;
import javax.time.ZoneOffset;
import javax.time.chrono.Chronology;
import javax.time.chrono.ISOChronology;

/**
 * A local date-time without time-zone suitable for use with all calendar systems.
 * <p>
 * This class performs a role similar to {@code LocalDateTime} but permits the
 * calendar system, represented by {@link Chronology}, to be controlled.
 * The model used restricts calendar system changes to the date, not the time.
 * As such, the date part of this class is generified by the relevent date class.
 * <p>
 * When using this class, bear in mind that two instances may represent dates
 * in two different calendar systems. Thus, application logic needs to handle
 * calendar systems, for example allowing for additional months, different years
 * and alternate leap systems.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 * 
 * @param <D> the date class
 */
public final class ChronoLocalDateTime<D extends ChronoDate<?>>
        implements DateTime<ChronoLocalDateTime<D>>, DateTimeAdjuster, Comparable<ChronoLocalDateTime<?>> {

    /**
     * The date.
     */
    private final D date;
    /**
     * The time.
     */
    private final LocalTime time;

    public static <R extends ChronoDate<?>> ChronoLocalDateTime<R> of(R date, LocalTime time) {
        DateTimes.checkNotNull(date, "Date must not be null");
        DateTimes.checkNotNull(time, "Time must not be null");
        return new ChronoLocalDateTime<R>(date, time);
    }

    private ChronoLocalDateTime(D date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date part of the date-time.
     *
     * @return the date, not null
     */
    public D getDate() {
        return date;
    }

    /**
     * Gets the time part of the date-time.
     *
     * @return the time, not null
     */
    public LocalTime getTime() {
        return time;
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(DateTimeField field) {
        return date.range(field);
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isTimeField()) {
                return time.get(field);
            } else {
                return date.get(field);
            }
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDateTime<D> with(DateTimeAdjuster adjuster) {
        return adjuster.doAdjustment(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDateTime<D> with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isTimeField()) {
                LocalTime updated = time.with(field, newValue);
                return new ChronoLocalDateTime<D>(date, updated);
            } else {
                D updated = (D) date.with(field, newValue);
                return new ChronoLocalDateTime<D>(updated, time);
            }
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDateTime<D> plus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doAdd(this);
    }

    @Override
    public ChronoLocalDateTime<D> plus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDateTime<D> minus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doSubtract(this);
    }

    @Override
    public ChronoLocalDateTime<D> minus(long periodAmount, PeriodUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology in use for this date-time.
     * <p>
     * The {@code Chronology} represents the calendar system, where the world civil calendar
     * system is referred to as the {@link ISOChronology ISO calendar system}.
     * All fields are expressed in terms of the calendar system.
     *
     * @return the calendar system chronology used by the date-time, not null
     */
    public Chronology getChronology() {
        return date.getChronology();
    }

//    /**
//     * Returns a local date-time representing the same date as this date-time
//     * but expressed using a different chronology.
//     *
//     * @param chrono  the calendar system to use, not null
//     * @return the date-time based on this date-time with the chronology changed, not null
//     */
//    public <R extends ChronoDate<R>> ChronoLocalDateTime<R> withChronology(Chronology chrono) {
//        return chrono.dateTime(date, time);
//    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset date-time formed from this date-time and the specified offset.
     * <p>
     * This merges the two objects - {@code this} and the specified offset -
     * to form an instance of {@code ChronoOffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date-time formed from this date-time and the specified offset, not null
     */
    public ChronoOffsetDateTime<D> atOffset(ZoneOffset offset) {
        return ChronoOffsetDateTime.of(date, time, offset);
    }

//    /**
//     * Returns a zoned date-time formed from this date-time and the specified time-zone.
//     * <p>
//     * Time-zone rules, such as daylight savings, mean that not every time on the
//     * local time-line exists. If the local date-time is in a gap or overlap according to
//     * the rules then a resolver is used to determine the resultant local time and offset.
//     * This method uses the {@link ZoneResolvers#postGapPreOverlap() post-gap pre-overlap} resolver.
//     * This selects the date-time immediately after a gap and the earlier offset in overlaps.
//     * <p>
//     * Finer control over gaps and overlaps is available in two ways.
//     * If you simply want to use the later offset at overlaps then call
//     * {@link ZonedDateTime#withLaterOffsetAtOverlap()} immediately after this method.
//     * Alternately, pass a specific resolver to {@link #atZone(ZoneId, ZoneResolver)}.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param zone  the time-zone to use, not null
//     * @return the zoned date-time formed from this date-time, not null
//     */
//    public ChronoZonedDateTime<D> atZone(ZoneId zone) {
//        return ChronoZonedDateTime.of(this, zone, ZoneResolvers.postGapPreOverlap());
//    }
//
//    /**
//     * Returns a zoned date-time formed from this date-time and the specified time-zone
//     * taking control of what occurs in time-line gaps and overlaps.
//     * <p>
//     * Time-zone rules, such as daylight savings, mean that not every time on the
//     * local time-line exists. If the local date-time is in a gap or overlap according to
//     * the rules then the resolver is used to determine the resultant local time and offset.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param zone  the time-zone to use, not null
//     * @param resolver  the zone resolver to use for gaps and overlaps, not null
//     * @return the zoned date-time formed from this date-time, not null
//     * @throws DateTimeException if the date-time cannot be resolved
//     */
//    public ChronoZonedDateTime<D> atZone(ZoneId zone, ZoneResolver resolver) {
//        return ChronoZonedDateTime.of(this, zone, resolver);
//    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == Chronology.class) {
            return (R) getChronology();
        }
        return null;
    }

    @Override
    public <R extends DateTime<R>> R doAdjustment(R dateTime) {
        // TODO Auto-generated method stub
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time.
     * <p>
     * The comparison is based on the date and time within the calendar system.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int compareTo(ChronoLocalDateTime<?> other) {
        int cmp = ((ChronoDate) date).compareTo((ChronoDate) other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
        }
        return cmp;
    }

    // TODO equals
}
