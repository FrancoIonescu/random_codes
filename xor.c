#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dos.h>

/* Constants */
#define BUFFER_SIZE 512
#define KEY "SECRET123"
#define KEY_LENGTH 9

/* Function prototypes */
int openInputFile(const char *filename, FILE **file);
int createOutputFile(const char *filename, FILE **file);
int processFile(FILE *input, FILE *output);
void xorBuffer(unsigned char *buffer, int size);
void closeFiles(FILE *input, FILE *output);
void printMessage(const char *message);

/* Global variables */
const char *inputFileName = "input.txt";
const char *outputFileName = "output.txt";

/*
 * Main function
 */
int main(void)
{
    FILE *inputFile = NULL;
    FILE *outputFile = NULL;
    int result = 0;
    
    printMessage("XOR Encryption/Decryption Started...\n");
    
    /* Open input file */
    if (openInputFile(inputFileName, &inputFile) != 0) {
        printMessage("Error: Cannot open input file!\n");
        return 1;
    }
    
    /* Create output file */
    if (createOutputFile(outputFileName, &outputFile) != 0) {
        printMessage("Error: Cannot create output file!\n");
        fclose(inputFile);
        return 1;
    }
    
    /* Process file (read, encrypt/decrypt, write) */
    result = processFile(inputFile, outputFile);
    
    /* Close files */
    closeFiles(inputFile, outputFile);
    
    if (result == 0) {
        printMessage("Operation completed successfully!\n");
    } else {
        printMessage("Error: Operation failed!\n");
    }
    
    return result;
}

/*
 * Function: openInputFile
 * Opens the input file for reading
 * Parameters:
 *   filename - name of file to open
 *   file - pointer to FILE pointer
 * Returns: 0 on success, -1 on error
 */
int openInputFile(const char *filename, FILE **file)
{
    *file = fopen(filename, "rb");
    
    if (*file == NULL) {
        return -1;
    }
    
    return 0;
}

/*
 * Function: createOutputFile
 * Creates the output file for writing
 * Parameters:
 *   filename - name of file to create
 *   file - pointer to FILE pointer
 * Returns: 0 on success, -1 on error
 */
int createOutputFile(const char *filename, FILE **file)
{
    *file = fopen(filename, "wb");
    
    if (*file == NULL) {
        return -1;
    }
    
    return 0;
}

/*
 * Function: processFile
 * Reads from input file, encrypts/decrypts, writes to output file
 * Parameters:
 *   input - input file pointer
 *   output - output file pointer
 * Returns: 0 on success, -1 on error
 */
int processFile(FILE *input, FILE *output)
{
    unsigned char buffer[BUFFER_SIZE];
    size_t bytesRead;
    size_t bytesWritten;
    
    /* Process file in chunks */
    while ((bytesRead = fread(buffer, 1, BUFFER_SIZE, input)) > 0) {
        /* Check for read error */
        if (ferror(input)) {
            printMessage("Error: Cannot read from input file!\n");
            return -1;
        }
        
        /* XOR encrypt/decrypt the buffer */
        xorBuffer(buffer, (int)bytesRead);
        
        /* Write to output file */
        bytesWritten = fwrite(buffer, 1, bytesRead, output);
        
        if (bytesWritten != bytesRead) {
            printMessage("Error: Cannot write to output file!\n");
            return -1;
        }
    }
    
    return 0;
}

/*
 * Function: xorBuffer
 * XORs the buffer with the encryption key
 * Parameters:
 *   buffer - buffer to encrypt/decrypt
 *   size - number of bytes to process
 */
void xorBuffer(unsigned char *buffer, int size)
{
    int i;
    int keyIndex = 0;
    
    for (i = 0; i < size; i++) {
        /* XOR current byte with current key byte */
        buffer[i] ^= KEY[keyIndex];
        
        /* Move to next key byte (circular) */
        keyIndex++;
        if (keyIndex >= KEY_LENGTH) {
            keyIndex = 0;
        }
    }
}

/*
 * Function: closeFiles
 * Closes both input and output files
 * Parameters:
 *   input - input file pointer
 *   output - output file pointer
 */
void closeFiles(FILE *input, FILE *output)
{
    if (input != NULL) {
        fclose(input);
    }
    
    if (output != NULL) {
        fclose(output);
    }
}

/*
 * Function: printMessage
 * Prints a message to console
 * Parameters:
 *   message - string to print
 */
void printMessage(const char *message)
{
    printf("%s", message);
}