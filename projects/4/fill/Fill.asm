// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

    (MAIN_LOOP)
        @KBD 
        D=M 
        @BLACK 
        D;JNE        //jump to black if kbd detected
        @WHITE
        0;JMP        //white if 0 kbd detected 

    (BLACK)
        @color
        M=-1
        @FILL        //if black, start filling
        0;JMP 

    (WHITE)
        @color 
        M=0          //if no key pressed (white), keep going

    (FILL)
        @i
        M=0

    (FILL_LOOP)       //while i<8192, total 8192 words
        @i 
        D=M
        @8192        
        D=D-A        
        @MAIN_LOOP
        D;JGE         //i-8192>0

        @i
        D=M
        @SCREEN 
        D=D+A    //D = i + 16384(screen starting address)
        @addr
        M=D      //Pointer used to store i+16384

        @color 
        D=M 
        @addr 
        A=M      //A=i+16384
        M=D      //i+16384 = SCREEN[i] = color

        @i
        M=M+1

        @FILL_LOOP
        0;JMP  





