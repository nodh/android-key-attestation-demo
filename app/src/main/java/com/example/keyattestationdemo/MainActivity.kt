package com.example.keyattestationdemo

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.CertPathValidator
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.PKIXParameters
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btAction.setOnClickListener {
            try {
                validateAttestation()
            } catch (t: Throwable) {
                Log.e("Attestation", "Exception", t)
                tvStatus.text = "Error: ${t.localizedMessage}"
            }
        }
    }

    private fun validateAttestation() {
        val challengeBytes = BigInteger(128, Random()).toByteArray()
        val alias = "alias"
        val purposes = KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        val keyGenSpec = KeyGenParameterSpec.Builder(alias, purposes)
            .setKeySize(256)
            .setAttestationChallenge(challengeBytes)
            .build()
        KeyPairGenerator.getInstance("EC", "AndroidKeyStore").also {
            it.initialize(keyGenSpec)
            it.generateKeyPair()
        }
        validateCertificateChain(alias)
    }

    private fun validateCertificateChain(alias: String) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").also {
            it.load(null, null)
        }
        val certificateChain = keyStore.getCertificateChain(alias).toList()
        certificateChain.forEach {
            Log.d("Attestation", Base64.encodeToString(it.encoded, Base64.NO_WRAP))
        }
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val path = certificateFactory.generateCertPath(certificateChain)
        val validator = CertPathValidator.getInstance("PKIX")
        val trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).also {
            it.load(null, "password".toCharArray())
        }
        trustKeyStore.setCertificateEntry("google-hw-root", getGoogleHwRoot())
        val params = PKIXParameters(trustKeyStore)
        params.isRevocationEnabled = false
        try {
            val result = validator.validate(path, params)
            Log.d("Attestation", "Result: $result")
            tvStatus.text = "Success: $result"
        } catch (e: Exception) {
            Log.e("Attestation", "Failed", e)
            tvStatus.text = "Error: ${e.localizedMessage}"
        }
    }

    private fun getGoogleHwRoot(): Certificate {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val cert = "MIIFYDCCA0igAwIBAgIJAOj6GWMU0voYMA0GCSqGSIb3DQEBCwUAMBsxGTAXBgNVBAUTEGY5MjAwOWU4NTNiNmIwNDUwHhcNM" +
                "TYwNTI2MTYyODUyWhcNMjYwNTI0MTYyODUyWjAbMRkwFwYDVQQFExBmOTIwMDllODUzYjZiMDQ1MIICIjANBgkqhkiG9w0BAQEFA" +
                "AOCAg8AMIICCgKCAgEAr7bHgiuxpwHsK7Qui8xUFmOr75gvMsd/dTEDDJdSSxtf6An7xyqpRR90PL2abxM1dEqlXnf2tqw1Ne4Xw" +
                "l5jlRfdnJLmN0pTy/4lj4/7tv0Sk3iiKkypnEUtR6WfMgH0QZfKHM1+di+y9TFRtv6y//0rb+T+W8a9nsNL/ggjnar86461qO0rO" +
                "s2cXjp3kOG1FEJ5MVmFmBGtnrKpa73XpXyTqRxB/M0n1n/W9nGqC4FSYa04T6N5RIZGBN2z2MT5IKGbFlbC8UrW0DxW7AYImQQcH" +
                "tGl/m00QLVWutHQoVJYnFPlXTcHYvASLu+RhhsbDmxMgJJ0mcDpvsC4PjvB+TxywElgS70vE0XmLD+OJtvsBslHZvPBKCOdT0MS+" +
                "tgSOIfga+z1Z1g7+DVagf7quvmag8jfPioyKvxnK/EgsTUVi2ghzq8wm27ud/mIM7AY2qEORR8Go3TVB4HzWQgpZrt3i5MIlCaY5" +
                "04LzSRiigHCzAPlHws+W0rB5N+er5/2pJKnfBSDiCiFAVtCLOZ7gLiMm0jhO2B6tUXHI/+MRPjy02i59lINMRRev56GKtcd9qO/0" +
                "kUJWdZTdA2XoS82ixPvZtXQpUpuL12ab+9EaDK8Z4RHJYYfCT3Q5vNAXaiWQ+8PTWm2QgBR/bkwSWc+NpUFgNPN9PvQi8WEg5UmA" +
                "GMCAwEAAaOBpjCBozAdBgNVHQ4EFgQUNmHhAHyIBQlRi0RsR/8aTMnqTxIwHwYDVR0jBBgwFoAUNmHhAHyIBQlRi0RsR/8aTMnqT" +
                "xIwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAYYwQAYDVR0fBDkwNzA1oDOgMYYvaHR0cHM6Ly9hbmRyb2lkLmdvb2dsZ" +
                "WFwaXMuY29tL2F0dGVzdGF0aW9uL2NybC8wDQYJKoZIhvcNAQELBQADggIBACDIw41L3KlXG0aMiS//cqrG+EShHUGo8HNsw30W1" +
                "kJtjn6UBwRM6jnmiwfBPb8VA91chb2vssAtX2zbTvqBJ9+LBPGCdw/E53Rbf86qhxKaiAHOjpvAy5Y3m00mqC0w/Zwvju1twb4vh" +
                "LaJ5NkUJYsUS7rmJKHHBnETLi8GFqiEsqTWpG/6ibYCv7rYDBJDcR9W62BW9jfIoBQcxUCUJouMPH25lLNcDc1ssqvC2v7iUgI9L" +
                "eoM1sNovqPmQUiG9rHli1vXxzCyaMTjwftkJLkf6724DFhuKug2jITV0QkXvaJWF4nUaHOTNA4uJU9WDvZLI1j83A+/xnAJUucIv" +
                "/zGJ1AMH2boHqF8CY16LpsYgBt6tKxxWH00XcyDCdW2KlBCeqbQPcsFmWyWugxdcekhYsAWyoSf818NUsZdBWBaR/OukXrNLfkQ7" +
                "9IyZohZbvabO/X+MVT3rriAoKc8oE2Uws6DF+60PV7/WIPjNvXySdqspImSN78mflxDqwLqRBYkA3I75qppLGG9rp7UCdRjxMl8Z" +
                "DBld+7yvHVgt1cVzJx9xnyGCC23UaicMDSXYrB4I4WHXPGjxhZuCuPBLTdOLU8YRvMYdEvYebWHMpvwGCF6bAx3JBpIeOQ1wDB5y" +
                "0USicV3YgYGmi+NZfhA4URSh77Yd6uuJOJENRaNVTzk"
        return certificateFactory.generateCertificate(ByteArrayInputStream(Base64.decode(cert, Base64.NO_WRAP)))
    }

}
