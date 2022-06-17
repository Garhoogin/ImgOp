# ImgOp
ImgOp is a command line program that performs mathematical operations on images. The supported operations are addition, subtraction, multiplication, division, exponentiation, and linear transformation. Operations are carried out in a stack-like fashion, with commands either popping elements off the stack or pushing them. 

## Commands
```
Command   Description
i         Push an image to the top of the stack by its file path
n         Push a scalar quantity to the top of the stack
v         Push a 4-dimensional vector to the top of the stack
x         Push a 4x4 matrix to the top of the stack
u         Duplicate the element on the top of the stack
w         Swap the position of the two elements on the top of the stack
o         Pop the top element off the stack and write it to a file as an image
a         Add the top two stack elements
s         Subtract the top two stack elements
m         Multiply the top two stack elements
d         Divide the top two stack elements
p         Raise the first element on the top of the stack to the power of the second
c         Clamp the minimum value of the element on the top of the stack
C         Clamp the maximum value of the element on the top of the stack
```

## Basic Stack Operations
To push items to the stack, use the `i` command for images, `n` command for scalars, `v` command for 4-dimensional vectors, and `x` command for 4x4 matries. With the `i` command, follow it in the command line with a path to the image to push. With the `n` command, follow it with the scalar value to push. With the `v` and `x` commands, follow them with 4 and 16 values respectively.

To duplicate the top stack element, use the `u` command. To swap the top two stack items around, use the `w` command. To write out the top element of the stack as an image, use the `o` command followed by the path to the output file. When writing out the image output, color values are converted back to the range of [0, 255] and rounded to the nearest integer value.

## Arithmetic
For addition, subtraction, multiplication, division, and exponentiation, the item pushed to the stack first comes first, and the item pushed to the stack second comes second in the operation. When scalars are the elements on top of the stack, scalar operations are done on them. When vectors are on top of the stack, they are treated componentwise. When images are on top of the stack, they, too, are treated componentwise. Based on the first type of element on the stack, the following is a list of valid element types that may follow it:

* **Scalar** scalar, image, vector (multiplication and division), matrix (multiplication and division)
* **Vector** scalar (multiplication), image, vector
* **Image**  scalar, image, vector
* **Matrix** scalar (multiplication and division), image (multiplication), matrix (addition, subtraction, multiplication)

## Clamping
Currently only images can be clamped. To clamp an image's color channels, first push the vector containing either the lower bounds or the upper bounds, then push the image (alternatively, push in the other order and use a `u` command). Then use the `c` command (for lower bound) or `C` command (for upper bound), and the image will be clamped.

## Example Commands
Invert image colors:
```
java -jar ImgOp.jar i image.png v -1 -1 -1 1 m v 1 1 1 0 a o inverted.png
#multiply all channels (except alpha) by -1, then add 1 to each channel (except alpha), then write
```
Swap the red and green color channels:
```bash
java -jar ImgOp.jar i image.png x 0 1 0 0 1 0 0 0 0 0 1 0 0 0 0 1 w m o swapped.png
#multiply the image by a matrix that swaps the red and blue color channels, then write
```
Difference of two images:
```bash
java -jar ImgOp.jar i image1.png i image2.png s n 2 p n 0.5 p v 0 0 0 1 a o diff.png
#subtract two images, take the absolute value by squaring and square rooting, then make opaque before writing
```
