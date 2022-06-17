package com.garhoogin.imgop;

import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

public class ImgOp {

	public static void main(String[] args) throws Exception {
		/*
		a	add
		s  subtract
		m  multiply
		d  divide
		p  power
		c  clamp min
		C  clamp max
		v  push 4-dimensional vector
		x  push 4x4 matrix
		n  push constant
		i  push image
		u  duplicate top of stack
		o  pop stack and write to location
		w  swap top two stack items
		*/

		Stack<Object> stack = new Stack<>();
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			char cmd = arg.charAt(0);

			switch(cmd) {
				case 'a':
				case '+':
					stackAdd(stack);
					break;
				case 's':
				case '-':
					stackSubtract(stack);
					break;
				case 'm':
				case '*':
					stackMultiply(stack);
					break;
				case 'd':
				case '/':
					stackDivide(stack);
					break;
				case 'p':
				case '^':
					stackPower(stack);
					break;
				case 'c':
					stackClampMin(stack);
					break;
				case 'C':
					stackClampMax(stack);
					break;
				case 'v':
				{
					i++;
					float v1 = Float.parseFloat(args[i]);
					i++;
					float v2 = Float.parseFloat(args[i]);
					i++;
					float v3 = Float.parseFloat(args[i]);
					i++;
					float v4 = Float.parseFloat(args[i]);
					stack.push(new Vec4(v1, v2, v3, v4));
					break;
				}
				case 'x':
				{
					float mtx[] = new float[16];
					i++;
					for(int j = i; j < i + 16; j++) {
						mtx[j - i] = (float) Float.parseFloat(args[j]);
					}
					i += 15;
					stack.push(new Mtx44(mtx));
					break;
				}
				case 'n':
				{
					i++;
					float n = Float.parseFloat(args[i]);
					stack.push((Float) n);
					break;
				}
				case 'i':
				{
					i++;
					String path = args[i];
					stack.push(new FloatImage(ImageIO.read(new File(path))));
					break;
				}
				case 'u':
					stackDuplicate(stack);
					break;
				case 'o':
				{
					i++;
					String path = args[i];
					Object o1 = stack.pop();
					FloatImage f1 = (FloatImage) o1;
					BufferedImage bf = f1.getImage();
					ImageIO.write(bf, "png", new File(path));
					break;
				}
				case 'w':
					stackSwap(stack);
					break;
				default:
					System.err.println("Unknown command " + cmd + ".");
					System.exit(stack.size());
					break;
			}
		}
	}

	/**
	 * Duplicate the top element of a stack.
	 *
	 * @param stack the stack to duplicate the top element of
	 */
	public static void stackDuplicate(Stack<Object> stack) {
		Object o1 = stack.pop();
		stack.push(o1);
		stack.push(o1);
	}

	/**
	 * Swap the top two elements of a stack.
	 *
	 * @param stack the stack to swap the top elements of
	 */
	public static void stackSwap(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		stack.push(o2);
		stack.push(o1);
	}

	/**
	 * Add together the top two elements of the stack and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackAdd(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		if(o1 instanceof FloatImage) { //add to image
			FloatImage i1 = (FloatImage) o1;
			if(o2 instanceof FloatImage) {
				stack.push(i1.add((FloatImage) o2));
			} else if(o2 instanceof Float) {
				float f = (float) (Float) o2;
				stack.push(i1.add(f, f, f, f));
			} else if(o2 instanceof Vec4) {
				Vec4 v = (Vec4) o2;
				stack.push(i1.add(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o2 instanceof FloatImage) { //add to image
			FloatImage i2 = (FloatImage) o2;
			if(o1 instanceof Float) {
				float f = (float) (Float) o1;
				stack.push(i2.add(f, f, f, f));
			} else if(o1 instanceof Vec4) {
				Vec4 v = (Vec4) o1;
				stack.push(i2.add(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o1 instanceof Float) { //floating point add
			float f1 = (float) (Float) o1;
			if(o2 instanceof Float) {
				stack.push(f1 + (float) (Float) o2);
			}
		} else if(o1 instanceof Vec4) { //vector add
			Vec4 v = (Vec4) o1;
			if(o2 instanceof Vec4) {
				stack.push(v.add((Vec4) o2));
			}
		} else if(o1 instanceof Mtx44) { //matrix add
			Mtx44 m = (Mtx44) o1;
			if(o2 instanceof Mtx44) {
				stack.push(m.add((Mtx44) o2));
			}
		}
	}

	/**
	 * Subtract the top two elements of the stack and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackSubtract(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		if(o1 instanceof FloatImage) { //image subtract
			FloatImage i1 = (FloatImage) o1;
			if(o2 instanceof FloatImage) {
				stack.push(i1.sub((FloatImage) o2));
			} else if(o2 instanceof Float) {
				float f = (float) (Float) o2;
				stack.push(i1.sub(f, f, f, f));
			} else if(o2 instanceof Vec4) {
				Vec4 v = (Vec4) o2;
				stack.push(i1.sub(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o2 instanceof FloatImage) { //image subtract
			FloatImage i2 = (FloatImage) o2;
			if(o1 instanceof Float) {
				float f = (float) (Float) o1;
				stack.push(i2.sub(f, f, f, f));
			} else if(o1 instanceof Vec4) {
				Vec4 v = (Vec4) o1;
				stack.push(i2.sub(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o1 instanceof Float) { //floating point subtract
			float f1 = (float) (Float) o1;
			if(o2 instanceof Float) {
				stack.push(f1 - (float) (Float) o2);
			}
		} else if(o1 instanceof Vec4) { //vector subtract
			Vec4 v = (Vec4) o1;
			if(o2 instanceof Vec4) {
				stack.push(v.sub((Vec4) o2));
			}
		} else if(o1 instanceof Mtx44) { //matrix subtract
			Mtx44 m1 = (Mtx44) o1;
			if(o2 instanceof Mtx44) {
				stack.push(m1.sub((Mtx44) o2));
			}
		}
	}

	/**
	 * Multiply the top two elements of the stack and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackMultiply(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		if(o1 instanceof FloatImage) { //multiply image
			FloatImage i1 = (FloatImage) o1;
			if(o2 instanceof FloatImage) {
				stack.push(i1.mul((FloatImage) o2));
			} else if(o2 instanceof Float) {
				float f = (float) (Float) o2;
				stack.push(i1.mul(f, f, f, f));
			} else if(o2 instanceof Vec4) {
				Vec4 v = (Vec4) o2;
				stack.push(i1.mul(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o2 instanceof FloatImage) { //multiply image
			FloatImage i2 = (FloatImage) o2;
			if(o1 instanceof Float) {
				float f = (float) (Float) o1;
				stack.push(i2.mul(f, f, f, f));
			} else if(o1 instanceof Vec4) {
				Vec4 v = (Vec4) o1;
				stack.push(i2.mul(v.v[0], v.v[1], v.v[2], v.v[3]));
			} else if(o1 instanceof Mtx44) {
				Mtx44 m1 = (Mtx44) o1;
				stack.push(i2.colorTransform(
					m1.m[0], m1.m[1], m1.m[2], m1.m[3],
					m1.m[4], m1.m[5], m1.m[6], m1.m[7],
					m1.m[8], m1.m[9], m1.m[10], m1.m[11],
					m1.m[12], m1.m[13], m1.m[14], m1.m[15]
				));
			}
		} else if(o1 instanceof Float) { //scalar multiplication
			float f1 = (float) (Float) o1;
			if(o2 instanceof Float) {
				stack.push(f1 * (float) (Float) o2);
			}
		} else if(o1 instanceof Vec4) { //vector multiplication
			Vec4 v = (Vec4) o1;
			if(o2 instanceof Vec4) {
				stack.push(v.mul((Vec4) o2));
			}
		} else if(o1 instanceof Mtx44) { //matrix multiplication
			Mtx44 m1 = (Mtx44) o1;
			if(o2 instanceof Mtx44) {
				stack.push(m1.mul((Mtx44) o2));
			} else if(o2 instanceof Vec4) {
				stack.push(m1.mul((Vec4) o2));
			} else if(o2 instanceof Float) {
				stack.push(m1.mul((float) (Float) o2));
			}
		}
	}

	/**
	 * Divide the top two elements of the stack and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackDivide(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		if(o1 instanceof FloatImage) { //image componentwise division
			FloatImage i1 = (FloatImage) o1;
			if(o2 instanceof FloatImage) {
				stack.push(i1.div((FloatImage) o2));
			} else if(o2 instanceof Float) {
				float f = (float) (Float) o2;
				stack.push(i1.div(f, f, f, f));
			} else if(o2 instanceof Vec4) {
				Vec4 v = (Vec4) o2;
				stack.push(i1.div(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o2 instanceof FloatImage) { //image componentwise division
			FloatImage i2 = (FloatImage) o2;
			if(o1 instanceof Float) {
				float f = (float) (Float) o1;
				stack.push(i2.div(f, f, f, f));
			} else if(o1 instanceof Vec4) {
				Vec4 v = (Vec4) o1;
				stack.push(i2.div(v.v[0], v.v[1], v.v[2], v.v[3]));
			}
		} else if(o1 instanceof Float) { //scalar division
			float f1 = (float) (Float) o1;
			if(o2 instanceof Float) {
				stack.push(f1 / (float) (Float) o2);
			}
		} else if(o1 instanceof Vec4) { //vector componentwise division
			Vec4 v = (Vec4) o1;
			if(o2 instanceof Vec4) {
				stack.push(v.div((Vec4) o2));
			} else if(o2 instanceof Float) {
				stack.push(v.mul(1.0f / (float) (Float) o2));
			}
		} else if(o1 instanceof Mtx44) { //matrix componentwise division
			Mtx44 m1 = (Mtx44) o1;
			if(o2 instanceof Float) {
				stack.push(m1.mul(1.0f / (float) (Float) o2));
			}
		}
	}

	/**
	 * Raise the second to last stack item to the power of the last stack item
	 * and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackPower(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		if(o1 instanceof FloatImage) {
			FloatImage f1 = (FloatImage) o1;
			if(o2 instanceof Float) {
				float f = (float) (Float) o2;
				stack.push(f1.pow(f, f, f, f));
			} else if(o2 instanceof Vec4) {
				Vec4 v = (Vec4) o2;
				stack.push(f1.pow(v.v[0], v.v[1], v.v[2], v.v[3]));
			} else if(o2 instanceof FloatImage) {
				stack.push(f1.pow((FloatImage) o2));
			}
		} else if(o1 instanceof Float) {
			float f1 = (float) (Float) o1;
			if(o2 instanceof Float) {
				stack.push((Float) (float) Math.pow(f1, (float) (Float) o2));
			}
		}
	}

	/**
	 * Clamp the top element of the stack underneath and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackClampMin(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		FloatImage f1 = (FloatImage) o1;
		Vec4 bound = (Vec4) o2;
		stack.push(f1.clamp(bound.v[0], Float.MAX_VALUE, bound.v[1],
			Float.MAX_VALUE, bound.v[2], Float.MAX_VALUE, bound.v[3], Float.MAX_VALUE));
	}

	/**
	 * Clamp the top element of the stack above and push the result.
	 *
	 * @param stack the stack
	 */
	public static void stackClampMax(Stack<Object> stack) {
		Object o2 = stack.pop();
		Object o1 = stack.pop();
		FloatImage f1 = (FloatImage) o1;
		Vec4 bound = (Vec4) o2;
		stack.push(f1.clamp(-Float.MAX_VALUE, bound.v[0], -Float.MAX_VALUE, bound.v[1],
			-Float.MAX_VALUE, bound.v[2], -Float.MAX_VALUE, bound.v[3]));
	}

}

