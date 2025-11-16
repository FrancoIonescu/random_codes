
You are a forensic cybersecurity specialist employed to recover data from a ransomware attack. This is what you know:
•
•
The ransomware attack has been conducted by a parasitic virus that encrypted most available files.
The encryption was based on AES, with a 128 bit key, randomly secure generated (you can't brute force it) The attack was stopped before the virus had a chance to send the encryption key to the C&C (Command & Control) center.
• The key is stored locally in a random file. From previous investigations you know that is in one of the files from System32 (see the system32.zip given archive - extract the folder).
•
Fortunately, you have the SHA2 fingerprint for all those files, computed 1 month ago (before the attack). They are given in the fingerprints.txt file. The values are stored in Base64 encoding.
(30 pts) Use the fingerprints.txt content to identify the file from system32 folder which has been changed. (-10 pts if the system32 folder is not placed at the root of the project and you are using absolute paths)
(30pts) Using the random password, extracted from the file identified at the previous step, decrypt the "financialdata.enc" file into "financialdata.txt". The virus has encrypted it using AES in CBC mode, with PKCS5 Padding. Reverse engineering the virus you find out that that the IV had 1st byte (from right to left) equal with 23, 2nd byte equal with 20, 3rd byte equal with 2 and 4th byte equal with 3. The rest of them are all Os.
(30pts) To confirm your success and get your bounty, write the value of the 1st IBAN into myresponse.txt and compute the file HashMAC(based on SHA1). Write the MAC value as Base64 encoded into myresponse_mac.txt text file. Don't forget to send the "financialdata.txt", "myresponse.txt" and "myresponse_mac.txt".
Upload
the .java file with your solution
financialdata.txt
·
myresponse.txt
•
myresponse_mac.txt
