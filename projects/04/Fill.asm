// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

(LOOP)
	@24576
	D=M
	
	@SETBLACK
	D;JGT

	@SETWHITE
	0;JMP

(SETWHITE)
	@SCREEN
	D=A
	@address1
	M=D	
	@8192
	D=D+A
	@total
	M=D

(SETWHITELOOP)
	@address1
	D=M

	@total
	D=M-D

	@LOOP
	D;JEQ

	@address1
	A=M
	M=0

	@address1
	M=M+1

	@SETWHITELOOP
	0;JMP

	
(SETBLACK)
	@SCREEN
	D=A
	@address2
	M=D	
	@8192
	D=D+A
	@total
	M=D

(SETBLACKLOOP)
	@address2
	D=M

	@total
	D=M-D

	@LOOP
	D;JEQ


	@address2
	A=M
	M=-1

	@address2
	M=M+1

	@SETBLACKLOOP
	0;JMP
