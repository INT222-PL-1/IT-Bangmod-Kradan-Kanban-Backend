package sit.int221.itbkkbackend.config;

import com.azure.identity.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.authentication.BaseBearerTokenAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class MicrosoftGraphConfig {
    public static GraphServiceClient getGraphClient(String oboToken) {

        final String clientId = "67fe1c4b-efd8-4f69-94d8-08e67f2c4c60";
        final String tenantId = "79845616-9df0-43e0-8842-e300feb2642a";
        final String clientSecret = "e-G8Q~iYSzeTPHfnS6bfF307cE5RI5gdgFFFfaGZ";

// The client credentials flow requires that you request the
// /.default scope, and pre-configure your permissions on the
// app registration in Azure. An administrator must grant consent
// to those permissions beforehand.
        final String[] scopes = new String[] { "https://graph.microsoft.com/.default" };

//        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
//                .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret).build();

        final OnBehalfOfCredential credential = new OnBehalfOfCredentialBuilder()
                .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
                .userAssertion(oboToken).build();

        return new GraphServiceClient(credential, scopes);
    }
}
