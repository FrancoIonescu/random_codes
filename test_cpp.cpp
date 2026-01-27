#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/applink.c>
#include <openssl/evp.h>
#include <memory.h>

int main() {

	// CERINTA 1

	unsigned  char ciphertext[32];
	unsigned char aes_cbc_key_128[] = { 0xff, 0xff, 0xff, 0xff, 0x00, 0x00, 0x00, 0x00, 0x08, 0x07, 0x06, 0x05, 0x00, 0x00, 0x00, 0x00 };
	unsigned char aes_cbc_iv[] = { 0xff, 0xff, 0xff, 0xff, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12 };

	FILE* aes_encrypted_file = fopen("encrypted.aes", "rb");

	fseek(aes_encrypted_file, 0, SEEK_END);

	int aesFileSize = ftell(aes_encrypted_file);

	fseek(aes_encrypted_file, 0, SEEK_SET);

	unsigned short int bytes_read = 0; 
	bytes_read = (unsigned short int)fread(ciphertext, sizeof(unsigned char), aesFileSize, aes_encrypted_file);

	AES_KEY aes_key_128;

	int no_blocks_ciphertext = aesFileSize / AES_BLOCK_SIZE;

	unsigned char* restore = (unsigned char*)malloc(aesFileSize);
	AES_set_decrypt_key(aes_cbc_key_128, (const int)(sizeof(aes_cbc_key_128) * 8), &aes_key_128);

	unsigned char* decrypted_buffer = (unsigned char*)malloc(no_blocks_ciphertext * AES_BLOCK_SIZE);

	AES_cbc_encrypt(ciphertext, decrypted_buffer, (no_blocks_ciphertext * AES_BLOCK_SIZE),
		&aes_key_128, aes_cbc_iv, AES_DECRYPT);
	printf("Decryption AES-CBC: ");
	for (unsigned int i = 0; i < (unsigned int)(no_blocks_ciphertext * AES_BLOCK_SIZE); i++)
	{
		printf("%c", decrypted_buffer[i]);
	}
	printf("\n\n");

	FILE* restored = fopen("restored.txt", "w+");

	for (unsigned char i = 0; i < no_blocks_ciphertext * AES_BLOCK_SIZE; i++)
		fprintf(restored, "%c", decrypted_buffer[i]);

	fclose(aes_encrypted_file);
	fclose(restored);

	// CERINTA 2

	RSA* key_pair = NULL;
	FILE* pub_file = fopen("public.pem", "r");
	key_pair = PEM_read_RSAPublicKey(pub_file, NULL, NULL, NULL);

	int key_size = RSA_size(key_pair);
	unsigned char* signature = (unsigned char*)malloc(key_size);

	FILE* sig_file = fopen("esign.sig", "rb");
	fread(signature, sizeof(unsigned char), key_size, sig_file);

	unsigned char* restore_message_digest = (unsigned char*)malloc(sizeof(SHA256_DIGEST_LENGTH));
	int dec_size = RSA_public_decrypt(key_size, signature, restore_message_digest, key_pair, RSA_PKCS1_PADDING);

	printf("Decrypted signature (message digest) is: ");
	for (unsigned char i = 0; i < SHA256_DIGEST_LENGTH; i++)
	{
		printf("%02X", restore_message_digest[i]);
	}
	printf("\n\n");

	FILE* sha_restore = fopen("SHA-256.txt", "w+");
	for (unsigned char i = 0; i < SHA256_DIGEST_LENGTH; i++)
	{
		fprintf(sha_restore, "%02X", restore_message_digest[i]);
	}

	// CERINTA 3

	unsigned char message_digest[SHA256_DIGEST_LENGTH];
	SHA256(decrypted_buffer, aesFileSize, message_digest);

	if (memcmp(message_digest, restore_message_digest, SHA256_DIGEST_LENGTH) == 0)
	{
		printf("Signature is valid.");
	}
	else
	{
		printf("Signature is not valid.");
	}

	RSA_free(key_pair);
	free(signature);
	fclose(pub_file);
	fclose(sig_file);
	fclose(sha_restore);

	return 0;
}