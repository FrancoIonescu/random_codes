.MODEL SMALL
.STACK 100h

.DATA
    inputFile   DB 'input.txt', 0
    outputFile  DB 'output.txt', 0
    
    xorKey      DB 'parolakey', 0
    keyLen      DW 9
    
    inputHandle DW ?
    outputHandle DW ?
    
    buffer      DB 512 DUP(?)
    bytesRead   DW ?
    
    msgStart    DB 'Criptare/Decriptare XOR...', 13, 10, '$'
    msgSuccess  DB 'Succes!', 13, 10, '$'
    msgError    DB 'Eroare!', 13, 10, '$'
    msgOpenErr  DB 'Eroare la deschiderea fisierului!', 13, 10, '$'
    msgReadErr  DB 'Eroare la citirea fisierului!', 13, 10, '$'
    msgWriteErr DB 'Eroare la scrierea fisierului!', 13, 10, '$'

.CODE
MAIN PROC
    MOV AX, @DATA
    MOV DS, AX
    
    LEA DX, msgStart
    CALL PrintString
    
    CALL OpenInputFile
    JC ErrorHandler
    
    CALL CreateOutputFile
    JC ErrorHandler
    
    CALL ProcessFile
    JC ErrorHandler
    
    CALL CloseFiles
    
    LEA DX, msgSuccess
    CALL PrintString
    
    JMP ExitProgram
    
ErrorHandler:
    LEA DX, msgError
    CALL PrintString
    
ExitProgram:
    MOV AL, 00h
    MOV AH, 4Ch
    INT 21h
MAIN ENDP

OpenInputFile PROC
    PUSH AX
    PUSH DX

    MOV AH, 3Dh
    MOV AL, 0          
    LEA DX, inputFile
    INT 21h
    
    JC OpenInputError
    
    MOV inputHandle, AX 
    CLC               
    JMP OpenInputDone
    
OpenInputError:
    PUSH DX
    LEA DX, msgOpenErr
    CALL PrintString
    POP DX
    STC      
    
OpenInputDone:
    POP DX
    POP AX
    RET
OpenInputFile ENDP

CreateOutputFile PROC
    PUSH AX
    PUSH CX
    PUSH DX
    
    MOV AH, 3Ch
    MOV CX, 0          
    LEA DX, outputFile
    INT 21h
    
    JC CreateOutputError
    
    MOV outputHandle, AX 
    CLC                
    JMP CreateOutputDone
    
CreateOutputError:
    PUSH DX
    LEA DX, msgOpenErr
    CALL PrintString
    POP DX
    STC            
    
CreateOutputDone:
    POP DX
    POP CX
    POP AX
    RET
CreateOutputFile ENDP

ProcessFile PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH DX
    
ProcessLoop:
    MOV AH, 3Fh
    MOV BX, inputHandle
    MOV CX, 512        
    LEA DX, buffer
    INT 21h
    
    JC ReadError
    
    CMP AX, 0
    JE ProcessDone
    
    MOV bytesRead, AX  
    
    MOV CX, AX       
    CALL XorBuffer
    
    MOV AH, 40h
    MOV BX, outputHandle
    MOV CX, bytesRead
    LEA DX, buffer
    INT 21h
    
    JC WriteError
    
    JMP ProcessLoop
    
ReadError:
    PUSH DX
    LEA DX, msgReadErr
    CALL PrintString
    POP DX
    STC
    JMP ProcessExit
    
WriteError:
    PUSH DX
    LEA DX, msgWriteErr
    CALL PrintString
    POP DX
    STC
    JMP ProcessExit
    
ProcessDone:
    CLC             
    
ProcessExit:
    POP DX
    POP CX
    POP BX
    POP AX
    RET
ProcessFile ENDP

XorBuffer PROC
    PUSH AX
    PUSH BX
    PUSH CX
    PUSH SI
    PUSH DI
    
    LEA SI, buffer      
    LEA DI, xorKey    
    MOV BX, 0         
    
XorLoop:
    MOV AL, [SI]
    
    PUSH SI
    MOV SI, DI
    ADD SI, BX
    MOV AH, [SI]
    POP SI
    
    XOR AL, AH
    
    MOV [SI], AL
    
    INC SI
    
    INC BX
    CMP BX, keyLen
    JL NoKeyReset
    MOV BX, 0      
    
NoKeyReset:
    LOOP XorLoop
    
    POP DI
    POP SI
    POP CX
    POP BX
    POP AX
    RET
XorBuffer ENDP

CloseFiles PROC
    PUSH AX
    PUSH BX
    
    MOV AH, 3Eh
    MOV BX, inputHandle
    INT 21h
    
    MOV AH, 3Eh
    MOV BX, outputHandle
    INT 21h
    
    POP BX
    POP AX
    RET
CloseFiles ENDP

PrintString PROC
    PUSH AX
    
    MOV AH, 09h
    INT 21h
    
    POP AX
    RET
PrintString ENDP

END MAIN