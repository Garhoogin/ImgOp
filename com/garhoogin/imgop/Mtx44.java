package com.garhoogin.imgop;

/**
 * This class represents a 4x4 matrix.
 *
 * @author Declan Moore
 */
public class Mtx44 {

	/**
	 * The elements of this matrix.
	 */
	public float m[];

	/**
	 * Create a 4x4 matrix from an initial array containing the matrix's
	 * elements
	 *
	 * @param f the array of initial elements
	 */
	public Mtx44(float[] f) {
		this.m = new float[16];
		for(int i = 0; i < 16; i++) this.m[i] = f[i];
	}

	/**
	 * Add two matrices together.
	 *
	 * @param m2 the second matrix
	 * @return   the result of the matrix addition
	 */
	public Mtx44 add(Mtx44 m2) {
		float f[] = new float[16];
		for(int i = 0; i < 16; i++) f[i] = this.m[i] + m2.m[i];
		return new Mtx44(f);
	}

	/**
	 * Subtract to matrices.
	 *
	 * @param m2 the second matrix
	 * @return   the result of the matrix subtraction
	 */
	public Mtx44 sub(Mtx44 m2) {
		float f[] = new float[16];
		for(int i = 0; i < 16; i++) f[i] = this.m[i] - m2.m[i];
		return new Mtx44(f);
	}

	/**
	 * Scale a matrix by soem scalar quantity.
	 *
	 * @param s the scalar
	 * @return  the scaled matrix
	 */
	public Mtx44 mul(float s) {
		float f[] = new float[16];
		for(int i = 0; i < 16; i++) f[i] = this.m[i] * s;
		return new Mtx44(f);
	}

	/**
	 * Multiply a vector by this matrix.
	 *
	 * @param v the vector to transform
	 * @return  the transformed vector
	 */
	public Vec4 mul(Vec4 v) {
		float s[] = new float[4];
		for(int i = 0; i < 4; i++) {
			s[i] = 0;
			s[i] += this.m[i * 4 + 0] * v.v[0];
			s[i] += this.m[i * 4 + 1] * v.v[1];
			s[i] += this.m[i * 4 + 2] * v.v[2];
			s[i] += this.m[i * 4 + 3] * v.v[3];
		}
		return new Vec4(s[0], s[1], s[2], s[3]);
	}

	/**
	 * Multiply two matrices.
	 *
	 * @param m2 the second matrix
	 * @return   the result of the matrix multiplication
	 */
	public Mtx44 mul(Mtx44 m2) {
		float f[] = new float[16];
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 4; x++) {
				float t = 0.0f;
				//row y of this, corresponding to column x of m2
				t += this.m[4 * y + 0] * m2.m[0 * 4 + x];
				t += this.m[4 * y + 1] * m2.m[1 * 4 + x];
				t += this.m[4 * y + 2] * m2.m[2 * 4 + x];
				t += this.m[4 * y + 3] * m2.m[3 * 4 + x];
				f[x + y * 4] = t;
			}
		}
		return new Mtx44(f);
	}
}

