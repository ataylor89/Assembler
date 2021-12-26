	global start
	section .text
start:	
	mov rax, 0x02000004
	mov rdi, 1
	mov rsi, message
	mov rdx, len
	syscall
        xor rsi, rsi
        xor rdx, rdx
        mov rax, 0x02000004
	mov rdi, 1
	mov rsi, message2
	mov rdx, len2
	syscall
	mov rax, 0x02000001
	xor rdi, rdi
	syscall
	section .data
message:	db		"hello world",10,0
len:			equ	$-message
message2:	db		"hello earth",10,0
len2:			equ	$-message2