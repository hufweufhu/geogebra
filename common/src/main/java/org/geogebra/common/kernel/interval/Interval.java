package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.empty;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class to implement interval arithmetic
 *
 */
public class Interval {
	private final IntervalEvaluate evaluate = new IntervalEvaluate(this);
	private double low;
	private double high;

	/**
	 * Creates a singleton interval [value, value]
	 *
	 * @param value for the singleton interval.
	 */
	public Interval(double value) {
		this(value, value);
	}

	/**
	 * Creates an interval with bounds [low, high]
	 *
	 * @param low lower bound of the interval
	 * @param high higher bound of the interval
	 */
	public Interval(double low, double high) {
		if (high < low) {
			setEmpty();
		} else {
			set(low, high);
		}
	}

	/**
	 * Creates an empty interval.
	 */
	public Interval() {
		setEmpty();
	}

	/**
	 * Copy constructor
	 *
	 * @param other to copy.
	 */
	public Interval(Interval other) {
		this(other.low, other.high);
	}

	/**
	 *
	 * @param interval interval.
	 * @param other interval.
	 * @return the max of interval and other.
	 */
	public static Interval max(Interval interval, Interval other) {
		if (interval.isEmpty() && other.isEmpty()) {
			return empty();
		} else if (interval.isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return interval;
		}

		return new Interval(Math.max(interval.low, other.low),
				Math.max(interval.high, other.high));
	}

	/**
	 *
	 * @param interval interval.
	 * @param other interval.
	 * @return the min of interval and other.
	 */
	public static Interval min(Interval interval, Interval other) {
		if (interval.isEmpty() && other.isEmpty()) {
			return empty();
		} else if (interval.isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return interval;
		}

		return new Interval(Math.min(interval.low, other.low),
				Math.min(interval.high, other.high));
	}

	/** Empty interval is represented by [∞, -∞]
	 * as in the original lib. */
	public void setEmpty() {
		set(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
	}

	/**
	 * Interval addition
	 *
	 * @param other interval to add
	 * @return this as result
	 */
	public Interval add(Interval other) {
		low += other.low;
		high += other.high;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Interval) {
			return almostEqual((Interval) o);
		}
		return false;

	}

	@Override
	public String toString() {
		String result = "Interval [";
		if (!isEmpty()) {
			result += low;
			if (!isSingleton()) {
				result += ", " + high;
			}
		}

		result += "]";
		return result;
	}

	/**
	 * Interval subtraction
	 *
	 * @param other to subtract from this interval
	 * @return this as result.
	 */
	public Interval subtract(Interval other) {
		if (isUndefined() || other.isUndefined()) {
			setUndefined();
		} else {
			low -= other.high;
			high -= other.low;
		}
		return this;
	}

	/**
	 *
	 * @return if interval has zero in it.
	 */
	public boolean hasZero() {
		return low <= 0 && high >= 0;
	}

	/**
	 *
	 * @return if interval represents all the real numbers R.
	 */
	public boolean isWhole() {
		return low == Double.NEGATIVE_INFINITY && high == Double.POSITIVE_INFINITY;
	}

	/**
	 *
	 * @return if interval is in the form [n, n] where n is finite.
	 */
	public boolean isSingleton() {
		return MyDouble.isFinite(low) && DoubleUtil.isEqual(high, low, 1E-7);
	}

	@Override
	public int hashCode() {
		return Objects.hash(low, high);
	}

	/**
	 *
	 * @return if interval is empty.
	 */
	public boolean isEmpty() {
		return  low > high;
	}

	/**
	 *
	 * @return lower bound.
	 */
	public double getLow() {
		return low;
	}

	/**
	 *
	 * @return upper bound.
	 */
	public double getHigh() {
		return high;
	}

	/**
	 *
	 * @param other interval to check.
	 * @return if intervals are overlapping
	 */
	public boolean isOverlap(Interval other) {
		if (isEmpty() || other.isEmpty()) {
			return false;
		}
		return (low <= other.low && other.low <= high)
				|| (other.low <= low && low <= other.high);
	}

	/**
	 *
	 * @param other to compare
	 * @return if the other interval is equal with a precision
	 */
	public boolean almostEqual(Interval other) {
		if (isUndefined() && other.isUndefined()) {
			return true;
		}

		return DoubleUtil.isEqual(low, other.low, 1E-7)
			&& DoubleUtil.isEqual(high, other.high, 1E-7);
	}

	/**
	 *  Computes "1 / x"
	 * @return this as result.
	 */
	public Interval multiplicativeInverse() {
		if (isEmpty()) {
			return empty();
		}

		if (isUndefined()) {
			return this;
		}

		if (hasZero()) {
			if (low != 0) {
				if (high != 0) {
					// [negative, positive]
					setUndefined();
				} else {
					// [negative, zero]
					double d = low;
					low = Double.NEGATIVE_INFINITY;
					high = RMath.divHigh(1.0, d);
				}
			} else {
				if (high != 0) {
					// [zero, positive]
					low = RMath.divLow(1, high);
					high = Double.POSITIVE_INFINITY;
				} else {
					// [zero, zero]
					setUndefined();
				}
			}
		} else {
			// [positive, positive]
			return new Interval(RMath.divLow(1, high), RMath.divHigh(1, low));
		}
		return this;
	}

	void setWhole() {
		set(IntervalConstants.whole());
	}

	/**
	 * Sets the closed interval bounds as the other interval
	 * @param other interval to set bounds as the same.
	 */
	public void set(Interval other) {
		set(other.low, other.high);
	}

	/**
	 * Sets the closed interval bounds.
	 *
	 * @param low lower bound.
	 * @param high higher bound.
	 */
	public void set(double low, double high) {
		this.low = low;
		this.high = high;
	}

	/**
	 * [a, b] -> (a, b]
	 * @return this as result
	 */
	public Interval halfOpenLeft() {
		low = RMath.next(low);
		return this;
	}

	/**
	 * [a, b] -> (a, b]
	 *
	 * @param a low limit
	 * @param b high limit
	 * @return this as result
	 */
	public Interval halfOpenLeft(double a, double b) {
		set(RMath.next(a), b);
		return this;
	}

	/**
	 * [a, b] -> [a, b)
	 * @return this as result
	 */
	public Interval halfOpenRight() {
		high = RMath.prev(high);
		return this;
	}

	/**
	 * [a, b] -> [a, b)
	 *
	 * @param a low limit
	 * @param b high limit
	 * @return this as result
	 */
	public Interval halfOpenRight(double a, double b) {
		 set(a, RMath.prev(b));
		return this;
	}

	/**
	 * Checks if the interval is
	 * either [-∞, -∞] or [∞, ∞].
	 *
	 * @return true if infinite.
	 */
	public boolean isOnlyInfinity() {
		return (isLowInfinite() || isHighInfinite()) && DoubleUtil.isEqual(high, low);
	}

	/**
	 *
	 * @return width of the interval.
	 */
	public double getWidth() {
		if (isEmpty()) {
			return 0;
		}
		return RMath.subHigh(high, low);
	}

	/**
	 * "Invert" the interval
	 * @return this as result.
	 */
	public Interval negative() {
		set(-high, -low);
		return this;
	}

	/**
	 *
	 * @return the bounds as array
	 */
	public double[] toArray() {
		return new double[] {low, high};
	}

	public void setZero() {
		set(IntervalConstants.zero());
	}

	public boolean isZero() {
		return low == 0 && high == 0;
	}

	public boolean contains(Interval interval) {
		return interval.low > low && interval.high < high;
	}

	/**
	 *
	 * @param other to compare with.
	 * @return if this interval is greater than the other.
	 */
	public boolean isGreaterThan(Interval other) {
		if (isEmpty() || other.isEmpty()) {
			return false;
		}
		return high > other.high;
	}

	public Interval evaluate(Operation operation,
			Interval other) throws Exception {
		return evaluate.evaluate(operation, other);
	}

	public Interval evaluate(Operation operation) throws Exception {
		return evaluate.evaluate(operation);
	}

	/**
	 * Shift the interval (both low and high) by a given value.
	 * @param deltaX to shift by.
	 * @return the result inteval;
	 */
	public Interval shiftBy(double deltaX) {
		set(low + deltaX, high + deltaX);
		return this;
	}

	/**
	 *
	 * @return shorten string form to compare and debug
	 */
	public String toShortString() {
		String result = "[";
		if (!isEmpty()) {
			result += low;
			if (!isSingleton()) {
				result += ", " + high;
			}
		}

		result += "]";
		return result;
	}

	/**
	 *
	 * @return the length of the interval
	 */
	public double getLength() {
		if (isEmpty()) {
			return 0;
		}
		return Math.abs(high - low);
	}

	/**
	 *
	 * @param low to set.
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 *
	 * @param high to set.
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	public boolean isHalfPositiveInfinity() {
		return DoubleUtil.isEqual(0, low, 1E-5) && high == Double.POSITIVE_INFINITY;
	}

	public boolean isHalfNegativeInfinity() {
		return low == Double.NEGATIVE_INFINITY && DoubleUtil.isEqual(high, 0, 1E-5);
	}

	/**
	 *
	 * @return true if interval one or both border is infinite.
	 */
	public boolean hasInfinity() {
		return !isEmpty() && isLowInfinite() || isHighInfinite();
	}

	/**
	 *
	 * @return true if high is infinite
	 */
	public boolean isHighInfinite() {
		return high == Double.POSITIVE_INFINITY;
	}

	/**
	 *
	 * @return true if low is infinite
	 */
	public boolean isLowInfinite() {
		return low == Double.NEGATIVE_INFINITY;
	}

	/**
	 *
	 * @return if the interval is the unit one.
	 */
	public boolean isOne() {
		return low == 1 && high == 1;
	}

	public boolean isFinite() {
		return MyDouble.isFinite(low) && MyDouble.isFinite(high);
	}

	/**
	 *
	 * @return true if any of the bounds is infinite but not both.
	 */
	public boolean isSemiInfinite() {
		return (isLowInfinite() && !isHighInfinite())
				|| (!isLowInfinite() && isHighInfinite());
	}

	/**
	 *
	 * @return true if interval is undefined (division by zero).
	 */
	public boolean isUndefined() {
		return Double.isNaN(low) && Double.isNaN(high);
	}

	/**
	 * Sets interval undefined (result of division by zero).
	 */
	public void setUndefined() {
		set(Double.NaN, Double.NaN);
	}

	/**
	 *
	 * @param other interval to check
	 * @return true if the bounds of the intervals has the same sign
	 * respectively, ie low (or high) < 0 then other.low (or other.high)< 0 or reverse.
	 */
	public boolean isSignEquals(Interval other) {
		return DoubleUtil.isEqual(Math.abs(low), other.low)
				&& DoubleUtil.isEqual(Math.abs(high), other.high);
	}

	/**
	 *
	 * @return if both bounds are positive.
	 */
	public boolean isPositive() {
		return !isEmpty() && low > 0;
	}

	/**
	 *
	 * @return if both bounds are negative.
	 */
	public boolean isNegative() {
		return !isEmpty() && high < 0;
	}

	/**
	 *
	 * @return if interval is [n, n] where n is an integer .
	 */
	public boolean isSingletonInteger() {
		return isSingleton() && DoubleUtil.isEqual(low, Math.round(low));
	}

	public IntervalEvaluate getEvaluate() {
		return evaluate;
	}
}