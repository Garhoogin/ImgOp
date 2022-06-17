package com.garhoogin.imgop;

/**
 * This class represents a 4-dimensional vector.
 *
 * @author Declan Moore
 */
class Vec4 {

	/**
	 * Array of this vector's elements
	 */
	public float v[];

	/**
	 * Create a 4-dimensional vector given four coordinates
	 *
	 * @param a coordinate 1
	 * @param b coordinate 2
	 * @param c coordinate 3
	 * @param d coordinate 4
	 */
	public Vec4(float a, float b, float c, float d) {
		this.v = new float[4];
		this.v[0] = a;
		this.v[1] = b;
		this.v[2] = c;
		this.v[3] = d;
	}

	/**
	 * Add two vectors together.
	 *
	 * @param v2 the second vector
	 * @return   the result of the vector addition
	 */
	public Vec4 add(Vec4 v2) {
		return new Vec4(v[0] + v2.v[0], v[1] + v2.v[1], v[2] + v2.v[2], v[3] + v2.v[3]);
	}

	/**
	 * Subtract two vectors.
	 *
	 * @param v2 the second vector
	 * @return   the result of the vector subtraction
	 */
	public Vec4 sub(Vec4 v2) {
		return new Vec4(v[0] - v2.v[0], v[1] - v2.v[1], v[2] - v2.v[2], v[3] - v2.v[3]);
	}

	/**
	 * Multiply two vectors together componentwise.
	 *
	 * @param v2 the second vector
	 * @return   the result of the componentwise multiplication
	 */
	public Vec4 mul(Vec4 v2) {
		return new Vec4(v[0] * v2.v[0], v[1] * v2.v[1], v[2] * v2.v[2], v[3] * v2.v[3]);
	}

	/**
	 * Scale a vector by some scalar
	 *
	 * @param s the scalar
	 * @return  the scaled vector
	 */
	public Vec4 mul(float s) {
		return new Vec4(v[0] * s, v[1] * s, v[2] * s, v[3] * s);
	}

	/**
	 * Divide two vectors componentwise.
	 *
	 * @param v2 the second vector
	 * @return   the result of the componentwise division
	 */
	public Vec4 div(Vec4 v2) {
		return new Vec4(v[0] / v2.v[0], v[1] / v2.v[1], v[2] / v2.v[2], v[3] / v2.v[3]);
	}

}

