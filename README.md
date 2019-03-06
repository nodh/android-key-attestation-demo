# Android Key Attestation Demo

Install this app, press the button. It will create a key pair in the Android KeyStore and request a [key attestation](https://developer.android.com/training/articles/security-key-attestation). Then the app will try to verify the validity of the attestation certificate chain, with the [Google Hardware Root Certificate](https://github.com/googlesamples/android-key-attestation/blob/master/server/src/main/java/com/android/example/KeyAttestationExample.java#L126) as the trust anchor. Usually, this verification will succeed.

But on Pixel 2 devices, it will fail with the message `java.security.cert.CertPathValidatorException: CA key usage check failed: keyCertSign bit is not set`, since the first intermediate certificate is broken (key usage is digital signature):
```
-----BEGIN CERTIFICATE-----
MIICKzCCAbKgAwIBAgIKEXhzFQJ5hgIAEDAKBggqhkjOPQQDAjAbMRkwFwYDVQQF
ExA4N2Y0NTE0NDc1YmEwYTJiMB4XDTE2MDUyNjE3MTUwMloXDTI2MDUyNDE3MTUw
MlowGzEZMBcGA1UEBRMQYzYwNDc1NzFkOGYwZDE3YzBZMBMGByqGSM49AgEGCCqG
SM49AwEHA0IABOoJkrjZcbPu6IcDxyrvlugASVQm5MX7OGGT0T34rzlwwbR9UV2A
Tu6aMiEa8uuQdP3iy5qSUYeCzUuneIdo7dujgd0wgdowHQYDVR0OBBYEFHlfwP7+
91r1xLPq/o7/eYXAU9ocMB8GA1UdIwQYMBaAFDBEI+Wi9gbhUKt3XxYWu5HMY8ZZ
MAwGA1UdEwEB/wQCMAAwDgYDVR0PAQH/BAQDAgeAMCQGA1UdHgQdMBugGTAXghVp
bnZhbGlkO2VtYWlsOmludmFsaWQwVAYDVR0fBE0wSzBJoEegRYZDaHR0cHM6Ly9h
bmRyb2lkLmdvb2dsZWFwaXMuY29tL2F0dGVzdGF0aW9uL2NybC8xMTc4NzMxNTAy
Nzk4NjAyMDAxMDAKBggqhkjOPQQDAgNnADBkAjAMOvX7podpWf2gJjzut3Woz/bq
1B42pC7Bu511pv1zj4jbtsdhhYCo/u/pnylG3LMCMCgdkdZQBPOEaJuBTYmxGiWr
qVFe6vTsX60SJ4vqa1PruSZzEFcyukXMckPn1wcz8A==
-----END CERTIFICATE-----
```

On other devices, like a Pixel 3, the first intermediate certificate has the correct key usage (certificate signing):
```
-----BEGIN CERTIFICATE-----
MIICJTCCAaugAwIBAgIKBQFBMZUIaJgwUzAKBggqhkjOPQQDAjApMRkwFwYDVQQF
ExBlMThjNGYyY2E2OTk3MzlhMQwwCgYDVQQMDANURUUwHhcNMTgwNzIzMjAzMzI4
WhcNMjgwNzIwMjAzMzI4WjApMRkwFwYDVQQFExBhMGI2M2EzNTc0MzY3M2I3MQww
CgYDVQQMDANURUUwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAATTv5kVHCdbVoeY
YPnrPjNJ0QNtZMeJAKq0e0w5XfCMc2iHvHmZZ+hmWJud1S94wLKPJKx8OkJfpJg2
fZoISK4Wo4G6MIG3MB0GA1UdDgQWBBRGZfPoN9ZqhkjAS/oWiLsbm6HO9TAfBgNV
HSMEGDAWgBStaJfkd3MUTYzmNFYScunw3VEFvjAPBgNVHRMBAf8EBTADAQH/MA4G
A1UdDwEB/wQEAwICBDBUBgNVHR8ETTBLMEmgR6BFhkNodHRwczovL2FuZHJvaWQu
Z29vZ2xlYXBpcy5jb20vYXR0ZXN0YXRpb24vY3JsLzA1MDE0MTMxOTUwODY4OTgz
MDUzMAoGCCqGSM49BAMCA2gAMGUCMQDtPMGkhcrmmUIm0iDXKgtlIGE/27j9e44P
9lEoqbvgy4wT4pmfxYrP7S6JunE+2qcCMHt3SohaS7BaOu7awdlsdLoRO1avJURZ
fJnen8gPP+oiqw/P5tWy59kgcDOEcohbvg==
-----END CERTIFICATE-----
```
