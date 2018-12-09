package com.ishyiga.ssl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.ishyiga.ssl.Greeting;

public class TestClient {
	public static final String CLIENT_TRUSTSTORE = "ssl/client_truststore.jks";
	public static final String CLIENT_KEYSTORE = "ssl/client_keystore.jks";

	private static final char[] KEYPASS_AND_STOREPASS_VALUE = "snaplogic".toCharArray();
	
	protected KeyStore getStore(final String storeFileName, final char[] password)
			throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
		final KeyStore store = KeyStore.getInstance("jks");
		URL url = getClass().getClassLoader().getResource(storeFileName);
		InputStream inputStream = url.openStream();
		try {
			store.load(inputStream, password);
		} finally {
			inputStream.close();
		}

		return store;
	}

	@Test
	public void testWithRestTemplate1() throws Exception {
		Greeting expected = new Greeting("Hello, Roche!");
		 SSLContext sslContext1 = SSLContextBuilder
	                .create()
	                .loadKeyMaterial(ResourceUtils.getFile("classpath:ssl/client_keystore.jks"), KEYPASS_AND_STOREPASS_VALUE, KEYPASS_AND_STOREPASS_VALUE)
	                .loadTrustMaterial(ResourceUtils.getFile("classpath:ssl/client_truststore.jks"), KEYPASS_AND_STOREPASS_VALUE)
	                .build();
		

		HttpClient  httpClient = HttpClients.custom().setSSLContext(sslContext1).
				setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		
		requestFactory.setHttpClient(httpClient);

		RestTemplate template = new RestTemplate(requestFactory);

		ResponseEntity<Greeting> responseEntity = template
				.getForEntity("https://localhost:" + 8443 + "/greeting?name={name}", Greeting.class, "Roche");
		System.out.println("RESPONSE:");
		System.out.println(responseEntity.getBody().getContent());
		assertThat(responseEntity.getBody().getContent(), equalTo(expected.getContent()));
		
	}

	@Test
	public void testWithRestTemplate() throws Exception {
		Greeting expected = new Greeting("Hello, Roche!");

		SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(getStore(CLIENT_KEYSTORE, KEYPASS_AND_STOREPASS_VALUE), KEYPASS_AND_STOREPASS_VALUE)
				.loadTrustMaterial(getStore(CLIENT_TRUSTSTORE, KEYPASS_AND_STOREPASS_VALUE),
						new TrustSelfSignedStrategy())
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).
				setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		
		requestFactory.setHttpClient(httpClient);

		RestTemplate template = new RestTemplate(requestFactory);

		ResponseEntity<Greeting> responseEntity = template
				.getForEntity("https://localhost:" + 8443 + "/greeting?name={name}", Greeting.class, "Roche");

		assertThat(responseEntity.getBody().getContent(), equalTo(expected.getContent()));
	}
	
	@Test
    public void testWithHttpClient()
            throws Exception {

        KeyStore clientTrustStore = getStore(CLIENT_TRUSTSTORE, KEYPASS_AND_STOREPASS_VALUE);
        KeyStore clientKeyStore = getStore(CLIENT_KEYSTORE, KEYPASS_AND_STOREPASS_VALUE);

        SSLContext sslContext =
                new SSLContextBuilder()
                        .loadTrustMaterial(clientTrustStore, new TrustSelfSignedStrategy())
                        .loadKeyMaterial(clientKeyStore, KEYPASS_AND_STOREPASS_VALUE)
                        .build();

        CloseableHttpClient httpclient = HttpClients.custom().setSSLContext(sslContext).build();

        try {
           
            httpclient.execute(new HttpGet("https://localhost:" + 8443 + "/greeting?name=name"));
        } finally {
        }
    }

}
