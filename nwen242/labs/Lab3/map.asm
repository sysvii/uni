.data 0x10000480
    Array_A:
.word 1,1,1,1,2,2,2,2
    Array_B:
.word 3,3,3,3,4,4,4,4

.text
.globl __start
__start:
    la $2, Array_A
    li $6, 0        #sum=0
    li $4, 8        #number of elements
loop:
    lw $5, 0($2)
    add $6, $6, $5  #sum=sum+Array_A[i]
    addi $2, $2, 4
    addi $4, $4, -1
    bgt $4, $0, loop
.end
