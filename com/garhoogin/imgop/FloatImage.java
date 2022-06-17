package com.garhoogin.imgop;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;

/**
 * This class represents an image as separate red, green, blue, and alpha images
 * where each color channel is stored as a floating point, where valid color
 * values are in the range [0, 1] rather than [0, 255].
 *
 * @author Declan Moore
 */
public class FloatImage {

	private static BufferedImage createImage(int[] pixels, int width, int height){
		int[] newBuffer = new int[pixels.length << 2];
		for(int i = 0; i < pixels.length; i++){
			int index = i << 2;
			newBuffer[index + 0] = pixels[i] & 0xFF;
			newBuffer[index + 1] = (pixels[i] >>> 8) & 0xFF;
			newBuffer[index + 2] = (pixels[i] >>> 16) & 0xFF;
			newBuffer[index + 3] = (pixels[i] >>> 24) & 0xFF;
		}
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel colorModel = new ComponentColorModel(colorSpace, new int[]{8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		//BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_CUSTOM);
		BufferedImage bi = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width, height), colorModel.isAlphaPremultiplied(), null);
		WritableRaster raster = (WritableRaster) bi.getRaster();
		raster.setPixels(0, 0, width, height, newBuffer);
		return bi;
	}

	/**
	 * The width of this image.
	 */
	int width;

	/**
	 * The height of this image.
	 */
	int height;

	/**
	 * The red image.
	 */
	float pxR[];

	/**
	 * The green image.
	 */
	float pxG[];

	/**
	 * The blue image.
	 */
	float pxB[];

	/**
	 * The alpha image.
	 */
	float pxA[];

	/**
	 * Create a FloatImage from a BufferedImage.
	 *
	 * @param im the input BufferedImage
	 */
	public FloatImage(BufferedImage im) {
		this.width = im.getWidth();
		this.height = im.getHeight();
		this.pxR = new float[this.width * this.height];
		this.pxG = new float[this.width * this.height];
		this.pxB = new float[this.width * this.height];
		this.pxA = new float[this.width * this.height];

		int[] rgba = im.getRGB(0, 0, this.width, this.height, null, 0, this.width);
		for(int i = 0; i < rgba.length; i++) {
			int c = rgba[i];
			this.pxR[i] = ((c >>> 16) & 0xFF) / 255.0f;
			this.pxG[i] = ((c >>> 8) & 0xFF) / 255.0f;
			this.pxB[i] = ((c >>> 0) & 0xFF) / 255.0f;
			this.pxA[i] = ((c >>> 24) & 0xFF) / 255.0f;
		}
	}

	/**
	 * Create a FloatImage from dimensions and raw floating point pixel values.
	 *
	 * @param width  the image width
	 * @param height the image height
	 * @param pxR    the red image
	 * @param pxR    the green image
	 * @param pxB    the blue image
	 * @param pxA    the alpha image
	 */
	private FloatImage(int width, int height, float pxR[], float pxG[], float pxB[], float pxA[]) {
		this.width = width;
		this.height = height;
		this.pxR = pxR;
		this.pxG = pxG;
		this.pxB = pxB;
		this.pxA = pxA;
	}

	/**
	 * Gets the width of the FloatImage.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gets the height of the FloatImage.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Normalizes a pixel value from the range [0, 1] to the range [0, 255] and
	 * rounds to the nearest integer value, clamping the result to the range
	 * [0, 255].
	 *
	 * @param f the number to normalize
	 * @return  the input number normalized and clamped.
	 */
	private static int denormalize(float f) {
		float f2 = f * 255.0f;
		if(f2 < 0.0f) f2 = -f2;
		else if(f2 > 255.0f) f2 = 255.0f;
		return (int) Math.floor(f2 + 0.5f);
	}

	/**
	 * Convert the FloatImage back into a BufferedImage.
	 *
	 * @return this FloatImage approximated as a BufferedImage
	 */
	public BufferedImage getImage() {
		int out[] = new int[this.width * this.height];
		for(int i = 0; i < this.pxR.length; i++) {
			float r = this.pxR[i];
			float g = this.pxG[i];
			float b = this.pxB[i];
			float a = this.pxA[i];
			int cc = denormalize(r) | (denormalize(g) << 8) | (denormalize(b) << 16) | (denormalize(a) << 24);
			out[i] = cc;
		}
		return createImage(out, this.width, this.height);
	}

	/**
	 * Creates a copy of this FloatImage.
	 *
	 * @return a copy of this FloatImage
	 */
	public FloatImage copy() {
		float pxR2[] = new float[this.pxR.length];
		float pxG2[] = new float[this.pxG.length];
		float pxB2[] = new float[this.pxB.length];
		float pxA2[] = new float[this.pxA.length];
		System.arraycopy(this.pxR, 0, pxR2, 0, this.pxR.length);
		System.arraycopy(this.pxG, 0, pxG2, 0, this.pxG.length);
		System.arraycopy(this.pxB, 0, pxB2, 0, this.pxB.length);
		System.arraycopy(this.pxA, 0, pxA2, 0, this.pxA.length);
		return new FloatImage(this.width, this.height, pxR2, pxG2, pxB2, pxA2);
	}

	/**
	 * Adds another FloatImage's color values to this image's color values.
	 *
	 * @param i2 the image whose color values should be added to this one's
	 * @return   the result of the color addition
	 */
	public FloatImage add(FloatImage i2) {
		FloatImage i1 = this.copy();
		int dw = Math.min(i1.width, i2.width);
		int dh = Math.min(i1.height, i2.height);
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] += i2.pxR[i];
				i1.pxG[i] += i2.pxG[i];
				i1.pxB[i] += i2.pxB[i];
				i1.pxA[i] += i2.pxA[i];
			}
		}
		return i1;
	}

	/**
	 * Adds RGBA values to every pixel of a copy of this image.
	 *
	 * @param r the red value added to this image's pixels
	 * @param g the green value added to this image's pixels
	 * @param b the blue value to add to this iamge's pixels
	 * @param a the alpha value to add to this image's pixels
	 * @return  a copy of this image with the specified values added to the
	 *          color values of all its pixels.
	 */
	public FloatImage add(float r, float g, float b, float a) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] += r;
				i1.pxG[i] += g;
				i1.pxB[i] += b;
				i1.pxA[i] += a;
			}
		}
		return i1;
	}

	/**
	 * Subtracts an image's color values from a copf of this image's color
	 * values.
	 *
	 * @param i2 the image whose color values are to be subtracted
	 * @return   a copy of this image with the input image's color values
	 *           subtracted from it
	 */
	public FloatImage sub(FloatImage i2) {
		FloatImage i1 = this.copy();
		int dw = Math.min(i1.width, i2.width);
		int dh = Math.min(i1.height, i2.height);
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] -= i2.pxR[i];
				i1.pxG[i] -= i2.pxG[i];
				i1.pxB[i] -= i2.pxB[i];
				i1.pxA[i] -= i2.pxA[i];
			}
		}
		return i1;
	}

	/**
	 * Subtract an RGBA value from every pixel of a copy of this image.
	 *
	 * @param r the red value to subtract from this image
	 * @param g the green value to subtract from this image
	 * @param b the blue value to subtract from this image
	 * @param a the alpha value to subtract from this image
	 * @return  a copy of this image with the specified color values subtracted
	 *          from every pixel of this image
	 */
	public FloatImage sub(float r, float g, float b, float a) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] -= r;
				i1.pxG[i] -= g;
				i1.pxB[i] -= b;
				i1.pxA[i] -= a;
			}
		}
		return i1;
	}

	/**
	 * Multiply this image's colors values componentwise with another image's
	 * color values.
	 *
	 * @param i2 the image by which to multiply
	 * @return   a copy of this image with color values multiplied by those from
	 *           the specified image.
	 */
	public FloatImage mul(FloatImage i2) {
		FloatImage i1 = this.copy();
		int dw = Math.min(i1.width, i2.width);
		int dh = Math.min(i1.height, i2.height);
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] *= i2.pxR[i];
				i1.pxG[i] *= i2.pxG[i];
				i1.pxB[i] *= i2.pxB[i];
				i1.pxA[i] *= i2.pxA[i];
			}
		}
		return i1;
	}

	/**
	 * Multiply the color values of this image's pixels by a constant RGBA
	 * vector componentwise.
	 *
	 * @param r the red value to multiply by
	 * @param g the green value to multiply by
	 * @param b the blue value to multiply by
	 * @param a the alpha value to multiply by
	 * @return  a copy of this image whose pixels' color values are multiplied
	 *          by those from the input color vector
	 */
	public FloatImage mul(float r, float g, float b, float a) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] *= r;
				i1.pxG[i] *= g;
				i1.pxB[i] *= b;
				i1.pxA[i] *= a;
			}
		}
		return i1;
	}

	/**
	 * Divides this image's color values by those from another image's.
	 *
	 * @param i2 the image whose color channels this one's should be divided by
	 * @return   a copy of this image with color values divided by the ones from
	 *           the specified image.
	 */
	public FloatImage div(FloatImage i2) {
		FloatImage i1 = this.copy();
		int dw = Math.min(i1.width, i2.width);
		int dh = Math.min(i1.height, i2.height);
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] /= i2.pxR[i];
				i1.pxG[i] /= i2.pxG[i];
				i1.pxB[i] /= i2.pxB[i];
				i1.pxA[i] /= i2.pxA[i];
			}
		}
		return i1;
	}

	/**
	 * Divides the image's color channels by constant RGBA values.
	 *
	 * @param r the red value to divided by
	 * @param g the green value to divide by
	 * @param b the blue value to divide by
	 * @param a the alpha value to divide by
	 * @return  a copy of this image with color channels divided by the
	 *          specified values.
	 */
	public FloatImage div(float r, float g, float b, float a) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] /= r;
				i1.pxG[i] /= g;
				i1.pxB[i] /= b;
				i1.pxA[i] /= a;
			}
		}
		return i1;
	}

	/**
	 * Raise all this image's color values by another image's respective color
	 * values.
	 *
	 * @param i2 the image that will serve as the exponent in the calculations
	 * @return   a copy of this image with color values raised to the power of
	 *           the color values of another image.
	 */
	public FloatImage pow(FloatImage i2) {
		FloatImage i1 = this.copy();
		int dw = Math.min(i1.width, i2.width);
		int dh = Math.min(i1.height, i2.height);
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] = (float) Math.pow(i1.pxR[i], i2.pxR[i]);
				i1.pxG[i] = (float) Math.pow(i1.pxG[i], i2.pxG[i]);
				i1.pxB[i] = (float) Math.pow(i1.pxB[i], i2.pxB[i]);
				i1.pxA[i] = (float) Math.pow(i1.pxA[i], i2.pxA[i]);
			}
		}
		return i1;
	}

	/**
	 * Raise all this image's color values to a specified power for each of the
	 * red, green, blue, and alpha channels.
	 *
	 * @param r the exponent for the red channel
	 * @param g the exponent for the green channel
	 * @param b the exponent for the blue channel
	 * @param a the exponent for the alpha channel
	 * @return  a copy of this image with color channels raised to the power of
	 *          the given values.
	 */
	public FloatImage pow(float r, float g, float b, float a) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] = (float) Math.pow(i1.pxR[i], r);
				i1.pxG[i] = (float) Math.pow(i1.pxG[i], g);
				i1.pxB[i] = (float) Math.pow(i1.pxB[i], b);
				i1.pxA[i] = (float) Math.pow(i1.pxA[i], a);
			}
		}
		return i1;
	}

	/**
	 * Performs a linear transform on this image's RGBA values by a 4x4 matrix.
	 *
	 * @param a element 1,1 of the matrix
	 * @param b element 1,2 of the matrix
	 * @param c element 1,3 of the matrix
	 * @param d element 1,4 of the matrix
	 * @param e element 2,1 of the matrix
	 * @param f element 2,2 of the matrix
	 * @param g element 2,3 of the matrix
	 * @param h element 2,4 of the matrix
	 * @param i element 3,1 of the matrix
	 * @param j element 3,2 of the matrix
	 * @param k element 3,3 of the matrix
	 * @param l element 3,4 of the matrix
	 * @param m element 4,1 of the matrix
	 * @param n element 4,2 of the matrix
	 * @param o element 4,3 of the matrix
	 * @param p element 4,4 of the matrix
	 * return   a copy of this image whose color values are linearly transformed
	 *          by the specified matrix transformation.
	 */
	public FloatImage colorTransform(float a, float b, float c, float d,
			float e, float f, float g, float h,
			float i, float j, float k, float l,
			float m, float n, float o, float p) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int si = y * i1.width + x;
				float cr = i1.pxR[si];
				float cg = i1.pxG[si];
				float cb = i1.pxB[si];
				float ca = i1.pxA[si];

				//apply linear transform
				i1.pxR[si] = cr * a + cg * b + cb * c + ca * d;
				i1.pxG[si] = cr * e + cg * f + cb * g + ca * h;
				i1.pxB[si] = cr * i + cg * j + cb * k + ca * l;
				i1.pxA[si] = cr * m + cg * n + cb * o + ca * p;
			}
		}
		return i1;
	}

	/**
	 * Clamps this image's color values to the specified ranges in red, green,
	 * blue, and alpha.
	 *
	 * @param minR the minimum red value
	 * @param maxR the maximum red value
	 * @param minG the minimum green value
	 * @param maxG the maximum green value
	 * @param minB the minimum blue value
	 * @param maxB the maximum blue value
	 * @param minA the minimum alpha value
	 * @param maxA the maximum alpha value
	 * @return     a copy of this image with color values clamped to the given
	 *             ranges.
	 */
	public FloatImage clamp(float minR, float maxR, float minG, float maxG, float minB, float maxB, float minA, float maxA) {
		FloatImage i1 = this.copy();
		int dw = i1.width;
		int dh = i1.height;
		for(int y = 0; y < dh; y++) {
			for(int x = 0; x < dw; x++) {
				int i = y * i1.width + x;
				i1.pxR[i] = (float) Math.min(Math.max(i1.pxR[i], minR), maxR);
				i1.pxG[i] = (float) Math.min(Math.max(i1.pxG[i], minG), maxG);
				i1.pxB[i] = (float) Math.min(Math.max(i1.pxB[i], minB), maxB);
				i1.pxA[i] = (float) Math.min(Math.max(i1.pxA[i], minA), maxA);
			}
		}
		return i1;
	}

}
