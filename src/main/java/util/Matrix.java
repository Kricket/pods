package util;

import java.util.Set;

/**
 * What a crackhead; who implements their own Matrix class?
 * I actually started with JAMA, but JProfiler showed me that a lot of time
 * was being wasted creating double[] arrays - because JAMA matrices store
 * their data as double[][], every temporary matrix had to initialize all
 * those arrays (java's not the most efficient language when it comes to
 * multi-dim arrays). Since my needs are pretty simple, I rolled my own...
 */
public class Matrix {

	/**
	 * (r,c) = data[cols*r + c]<br>
	 * i.e., data is ordered like:<pre>
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8</pre>
	 */
	public final double[] data;
	/**
	 * Number of rows and columns in this Matrix.
	 */
	public final int rows, cols;
	
	/**
	 * Create an empty Matrix of the given size.
	 * @param r Number of rows
	 * @param c Number of columns
	 */
	public Matrix(int r, int c) {
		this(r,c,new double[r*c]);
	}
	
	/**
	 * Create a column vector from the given data.
	 * @param col The column vector.
	 */
	public Matrix(double ...col) {
		this(col.length, 1, col);
	}
	
	/**
	 * Create a new Matrix of the given size, using the given data.
	 * @param r
	 * @param c
	 * @param data
	 */
	public Matrix(int r, int c, double... data) {
		rows = r;
		cols = c;
		this.data = data;
	}
	
	/**
	 * Initialize this with random values in (-scale/2, scale/2).
	 * @param r
	 * @param c
	 * @param scale The random values will vary from (-scale/2, scale/2)
	 * @return
	 */
	public static Matrix random(int r, int c, double scale) {
		Matrix m = new Matrix(r, c);
		for(int i=0; i<m.data.length; i++) {
			m.data[i] = (Math.random() - Math.random())*scale;
		}
		return m;
	}
	
	/**
	 * Get the value at row r, column c.
	 * @param r
	 * @param c
	 * @return
	 */
	public double at(int r, int c) {
		return data[r*cols + c];
	}
	
	/**
	 * Set the value at row r, column c.
	 * @param r
	 * @param c
	 * @param value
	 */
	public void set(int r, int c, double value) {
		data[r*cols+c] = value;
	}
	
	public Matrix plusEquals(Matrix m) {
		/*
		if(data.length != m.data.length)
			throw new IllegalArgumentException("I have " + data.length + " elements, but m has " + m.data.length);
		*/
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			set(r, c, at(r,c) + m.at(r,c));
		return this;
	}

	public Matrix times(Matrix m) {
		/*
		if(cols != m.rows)
			throw new IllegalArgumentException("Incompatible dimensions: I have " + cols + " cols, but m has " + m.rows + " rows");
		*/
		Matrix p = new Matrix(rows, m.cols);
		for(int r=0; r<p.rows; r++) {
			for(int c=0; c<p.cols; c++) {
				// this.row r . m.col c
				double rc = 0;
				for(int i=0; i<cols; i++) {
					//rc += data[r*cols + i] * m.data[i*m.cols + c];
					rc += at(r,i) * m.at(i,c);
				}
				//p.data[p.cols*r + c] = rc;
				p.set(r, c, rc);
			}
		}
		return p;
	}
	
	/**
	 * Multiply the transpose of this times m
	 * @param m
	 * @return
	 */
	public Matrix transposeTimes(Matrix m) {
		/*
		if(rows != m.rows)
			throw new IllegalArgumentException("Incompatible dimensions: my transpose has " + rows + " cols, but m has " + m.rows + " rows");
		*/
		Matrix p = new Matrix(cols, m.cols);
		for(int r=0; r<p.rows; r++) {
			for(int c=0; c<p.cols; c++) {
				// this.col r . m.col c
				double rc = 0;
				for(int i=0; i<rows; i++) {
					//rc += data[i*cols + r] * m.data[i*m.cols + c];
					rc += at(i,r) * m.at(i,c);
				}
				//p.data[p.cols*r + c] = rc;
				p.set(r, c, rc);
			}
		}
		return p;
	}
	
	/**
	 * Multiply this times the transpose of m.
	 * @param m
	 * @return
	 */
	public Matrix timesTranspose(Matrix m) {
		/*
		if(cols != m.cols)
			throw new IllegalArgumentException("Incompatible dimensions: I have " + cols + " cols, but mT has " + m.cols + " rows");
		*/
		Matrix p = new Matrix(rows, m.rows);
		for(int r=0; r<p.rows; r++) {
			for(int c=0; c<p.cols; c++) {
				// this.row r . m.row c
				double rc = 0;
				for(int i=0; i<cols; i++) {
					//rc += data[r*cols + i] * m.data[c*m.cols + i];
					rc += at(r,i) * m.at(c,i);
				}
				//p.data[p.cols*r + c] = rc;
				p.set(r, c, rc);
			}
		}
		return p;
	}
	
	public Matrix copy() {
		Matrix m = new Matrix(rows, cols);
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			m.set(r, c, at(r,c));
		return m;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int r=0; r<rows; r++) {
			if(r > 0)
				sb.append("\n");
			for(int c=0; c<cols; c++) {
				sb.append(" ");
				sb.append(String.format("%.3f", at(r,c)));
			}
		}
		
		return sb.toString();
	}

	/**
	 * Multiply this*d.
	 * @param d
	 * @return this
	 */
	public Matrix timesEquals(double d) {
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			set(r,c, at(r,c) * d);
		return this;
	}
	
	/**
	 * Elementwise multiplication with the given Matrix.
	 * @param m
	 * @return this
	 */
	public Matrix dotTimesEquals(Matrix m) {
		/*
		if(cols != m.cols || rows != m.rows)
			throw new IllegalArgumentException("Incompatible dimensions: I am (" + rows + "," + cols + "), m is (" + m.rows + "," + m.cols + ")");
		*/
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			set(r, c, at(r,c) * m.at(r,c));
		
		return this;
	}

	public Matrix minus(Matrix m) {
		/*
		if(cols != m.cols || rows != m.rows)
			throw new IllegalArgumentException("Incompatible dimensions: I am (" + rows + "," + cols + "), m is (" + m.rows + "," + m.cols + ")");
		*/
		Matrix result = new Matrix(rows, cols);
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			result.set(r, c, at(r,c) - m.at(r,c));
		return result;
	}

	/**
	 * Return a copy of this Matrix, WITHOUT the given rows.
	 * @param rowsToRemove Indices of the rows to remove.
	 */
	public Matrix withoutRows(Set<Integer> rowsToRemove) {
		Matrix m = new Matrix(rows - rowsToRemove.size(), cols);
		int mr = 0;
		for(int r=0; r<rows; r++) {
			if(rowsToRemove.contains(r))
				continue;
			for(int c=0; c<cols; c++) {
				m.set(mr, c, at(r,c));
			}
			mr++;
		}
		
		return m;
	}

	/**
	 * Return a copy of this Matrix, WITHOUT the given columns.
	 * @param colsToRemove Indices of the columns to remove.
	 */
	public Matrix withoutColumns(Set<Integer> colsToRemove) {
		Matrix m = new Matrix(rows, cols - colsToRemove.size());
		for(int r=0; r<rows; r++) {
			int mc = 0;
			for(int c=0; c<cols; c++) {
				if(colsToRemove.contains(c))
					continue;
				m.set(r,mc,at(r,c));
				mc++;
			}
		}
		return m;
	}

	/**
	 * Inverse of {@link #withoutRows(Set)}: assume that this Matrix had previously had
	 * removeRows() called. The resulting Matrix was modified, and now we want to re-
	 * integrate the missing rows.
	 * @param m A Matrix containing only the rows that were previously removed.
	 * @param rowsToKeep The indices of the rows that were NOT removed (and will thus
	 * be left as-is).
	 */
	public void restoreRows(Matrix m, Set<Integer> rowsToKeep) {
		int mr = 0;
		for(int r=0; r<rows; r++) {
			if(rowsToKeep.contains(r))
				continue;
			
			for(int c=0; c<cols; c++) {
				set(r,c, m.at(mr, c));
			}
			
			mr++;
		}
	}

	public void restoreColumns(Matrix m, Set<Integer> removedCols) {
		for(int r=0; r<rows; r++) {
			int mc=0;
			for(int c=0; c<cols; c++) {
				if(removedCols.contains(c))
					continue;
				set(r,c,m.at(r,mc));
				mc++;
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Matrix))
			return false;
		
		Matrix m = (Matrix) other;
		if(m.rows != rows || m.cols != cols)
			return false;
		
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			if(at(r,c) != m.at(r,c))
				return false;
		
		return true;
	}
	/**
	 * Sum of the elementwise products of this and the given Matrix.
	 * @param m
	 * @return
	 */
	public double dot(Matrix m) {
		/*
		if(m.rows != rows || m.cols != cols)
			throw new IllegalArgumentException("I am " + rows + "x" + cols + " and m is " + m.rows + "x" + m.cols);
		*/
		double d = 0;
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++)
			d += at(r,c) * m.at(r,c);
		return d;
	}
	
	public String draw() {
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++) {
			double d = at(r,c);
			if(d < min)
				min = d;
			if(d > max)
				max = d;
		}

		double diff = (max-min)/4;
		double x = min + diff;
		double y = x + diff;
		double z = y + diff;
		
		StringBuilder sb = new StringBuilder();
		for(int r=0; r<rows; r++) {
			for(int c=0; c<cols; c++) {
				double value = at(r,c);
				if(value > z)
					sb.append("@");
				else if(value > y)
					sb.append("o");
				else if(value > x)
					sb.append(".");
				else
					sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public double norm() {
		double d = 0;
		for(int r=0; r<rows; r++) for(int c=0; c<cols; c++) {
			double e = at(r,c);
			d += e*e;
		}
		return Math.sqrt(d);
	}
}
