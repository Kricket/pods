package util;

public final class Vec {
	/** The 0 vector */
	public static final Vec ORIGIN = new Vec(0,0);
	/** Unit vector (1,0) */
	public static final Vec UNIT = new Vec(1,0);
	/** Big vector on positive X axis */
	public static final Vec BIGUNIT = new Vec(1000000., 0);
	
	final public double x, y;
	
	public Vec(double _x, double _y) {
		x = _x;
		y = _y;
	}
	
	public Vec minus(Vec other) {
		return new Vec(x-other.x, y-other.y);
	}

	/**
	 * Square of the length of this vector.
	 */
	public double norm2() {
		return dot(this);
	}

	/**
	 * Inner (dot) product.
	 */
	public double dot(Vec v) {
		return v.x*x + v.y*y;
	}

	/**
	 * Get the angle (rotation from 1,0) in radians.
	 * @return
	 */
	public double getAngle() {
		if(x == 0) {
			return (y > 0 ? Math.PI / 2 : Math.PI * 1.5);
		}
		
		if(y == 0) {
			return (x > 0 ? 0 : Math.PI);
		}
		
		double atan = Math.atan(y / x);
		if(x < 0)
			atan += Math.PI;
		
		if(atan < 0)
			atan += (2*Math.PI);
		
		return atan;
	}

	/**
	 * Get this vector rotated by the given angle.
	 * @param radians
	 * @return
	 */
	public Vec rotate(double radians) {
		double cos = Math.cos(radians), sin = Math.sin(radians);
		return new Vec(x*cos - y*sin, x*sin + y*cos);
	}

	public Vec times(double d) {
		return new Vec(d*x, d*y);
	}

	public Vec plus(Vec v) {
		return new Vec(x+v.x, y+v.y);
	}

	public Vec truncate() {
		return new Vec((int)x, (int)y);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public double norm() {
		return Math.sqrt(norm2());
	}

	@Override
	public int hashCode() {
		return (int) (10000*(x+y));
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vec) {
			Vec v = (Vec) obj;
			return x == v.x && y == v.y;
		}
		return false;
	}

	public Vec scale(double d, double e) {
		return new Vec(x * d, y * e);
	}
}
