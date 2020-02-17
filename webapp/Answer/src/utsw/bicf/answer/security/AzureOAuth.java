package utsw.bicf.answer.security;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;

public class AzureOAuth {

	String msGraphUrl;
	String accountName;
	String accountKey;
	String containerName;
	OtherProperties otherProps;
	private HttpHost proxyHost = null;
	private HttpClient client = null;
	private Proxy proxy = null;
	
	private void setupClient() {
		if (otherProps.getProxyHostname() != null) {
			proxyHost = new HttpHost(otherProps.getProxyHostname(), otherProps.getProxyPort());
			client = HttpClientBuilder.create().setProxy(proxyHost).build();
		}
		else {
			client = HttpClientBuilder.create().build();
		}
	}
	
	private void setupProxy() {
		if (this.proxy == null && otherProps.getProxyHostname() != null) {
			this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.swmed.edu", 3128));
		}
	}

	public boolean isUserValid(OtherProperties otherProps, String token) {
		this.otherProps = otherProps;
		try {
			if (token == null || token.equals("")) {
				return false;
			}
			HttpGet requestGet = new HttpGet(msGraphUrl);
			requestGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			setupClient();
			HttpResponse response = client.execute(requestGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false; // authentication failed. Wrong password or other server issue
		}
	}
	
	 public static String generateSAS(CloudBlobContainer container, CloudBlob cloudBlob) throws Exception {
	        // Create a new shared access policy.
	        SharedAccessBlobPolicy sasPolicy = new SharedAccessBlobPolicy();
	        // Create a UTC Gregorian calendar value.
	        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
	        // Specify the current time as the start time for the shared access
	        // signature.
	        //
	        calendar.setTime(new Date());
	        sasPolicy.setSharedAccessStartTime(calendar.getTime());
	        // Use the start time delta one hour as the end time for the shared
	        // access signature.
	        calendar.add(Calendar.HOUR, 1);
	        sasPolicy.setSharedAccessExpiryTime(calendar.getTime());
	        sasPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ, SharedAccessBlobPermissions.LIST));
	        // Create the container permissions.
	        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
	        // Turn public access to the container off.
	        containerPermissions.setPublicAccess(BlobContainerPublicAccessType.BLOB);
	        container.uploadPermissions(containerPermissions);
	        // Create a shared access signature for the container.
	        String sas = cloudBlob.generateSharedAccessSignature(sasPolicy, null);
	        // HACK: when the just generated SAS is used straight away, we get an
	        // authorization error intermittently. Sleeping for 1.5 seconds fixes that
	        // on my box.
//	        Thread.sleep(1500);
	        // Return to caller with the shared access signature.
	        return sas;
	    }
	 
	 public String getFileSAS(String caseName, String directoryName, String fileName) {
		    setupProxy();
//		    String fileURI = "373403926-94278412/ORD3450-27-11410_T_DNA_panel1385v2-1/ORD3450-27-11410_T_DNA_panel1385v2-1.hist.txt";
		    if (fileName.endsWith("bam")) {
		    	
		    }
		    String fileURI = caseName + "/" + directoryName + "/" + fileName;
		    StorageCredentialsAccountAndKey storageCredentials = new StorageCredentialsAccountAndKey(accountName, accountKey);
			try {
				CloudStorageAccount storageAccount = new CloudStorageAccount(storageCredentials, true);
				CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
				if (this.proxy != null) {
					OperationContext.setDefaultProxy(this.proxy);
				}
				CloudBlobContainer container = blobClient.getContainerReference("cases");
				CloudBlob cloudBlob = container.getBlobReferenceFromServer(fileURI);
				String sasToken = AzureOAuth.generateSAS(container, cloudBlob);
				String sasUrl = cloudBlob.getUri() + "?" + sasToken;
				return sasUrl;
			} catch (URISyntaxException | StorageException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return null;

		}
	
	public String getMsGraphUrl() {
		return msGraphUrl;
	}

	public void setMsGraphUrl(String msGraphUrl) {
		this.msGraphUrl = msGraphUrl;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
}
