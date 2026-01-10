#define _CRT_SECURE_NO_WARNINGS
#include <openssl/sha.h>
#include <openssl/aes.h>
#include <openssl/evp.h>
#include <stdio.h>
#include <string.h>

int main()
{
    // SHA1 (160)

    const char* data = "Acesta este mesajul meu secret";
    size_t length = strlen(data);

    unsigned char hash256[SHA256_DIGEST_LENGTH];

    SHA256((unsigned char*)data, length, hash256);

    printf("SHA-256 Hash: ");
    for (int i = 0; i < SHA256_DIGEST_LENGTH; i++) {
        printf("%02X", hash256[i]);
    }

    printf("\n");

    // AES-256

    unsigned char* key = (unsigned char*) "01234567890123456789012345678901";
    unsigned char* iv = (unsigned char*) "0123456789012345";

    unsigned char* plaintext = (unsigned char*) "Mesajul meu secret de criptat";
    int plaintext_len = strlen((char*) plaintext);
    
    unsigned char ciphertext[128];
    int ciphertext_len;

    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();

    EVP_EncryptInit_ex(ctx, EVP_aes_256_cbc(), NULL, key, iv);

    EVP_EncryptUpdate(ctx, ciphertext, &ciphertext_len, plaintext, plaintext_len);
    int temp_len;

    EVP_EncryptFinal_ex(ctx, ciphertext + ciphertext_len, &temp_len);
    ciphertext_len += temp_len;

    EVP_CIPHER_CTX_free(ctx);

    printf("Ciphertext: ");
    for (int i = 0; i < ciphertext_len; i++) {
        printf("%02X", ciphertext[i]);
    }

    printf("\n");

    // PRINT FROM FILE

    FILE* file = NULL;
    file = fopen("input.txt", "r");

    if (file == NULL) {
        return 1;
    }

    int nr = 0;

    fseek(file, 0, SEEK_SET);

    // Folosim &nr pentru a da adresa de memorie a lui nr
    if (fscanf(file, "%d", &nr) != EOF) {
        printf("%x", nr);
    }

    printf("\n");

    int nrHexa = 0x1A;
    printf("%x", nrHexa);

    printf("\n");

    int nrBinar = 0b1010; 
    printf("%d", nrBinar);

    printf("\n");

    // DISPLAY FILE SIZE
    FILE* inputFile = fopen("input.txt", "r");
    fseek(inputFile, 0, SEEK_END);
    int inputFileSize = ftell(inputFile);
    printf("%d", inputFileSize);

    return 0;
}
