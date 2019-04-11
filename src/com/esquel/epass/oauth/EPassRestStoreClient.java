package com.esquel.epass.oauth;

import java.util.concurrent.Executors;

import org.restlet.data.Reference;
import org.restlet.engine.connector.ClientHelper;
import org.restlet.ext.httpclient.HttpClientHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.esquel.epass.activity.BaseActivity;
import com.esquel.epass.utils.BuildConfig;
import com.google.common.util.concurrent.MoreExecutors;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.exception.NotAuthorizedException;
import com.joyaether.datastore.rest.OAuthRestStoreClient;
import com.joyaether.datastore.rest.RestRequest;
import com.joyaether.datastore.rest.oauth.Token;
import com.joyaether.datastore.rest.oauth.Token.Type;
import com.joyaether.datastore.rest.security.IdToken;

/**
 * 
 * @author joyaether
 * 
 */
public class EPassRestStoreClient extends OAuthRestStoreClient {
	private static final int THREAD_POOL_NUMBER = 5;
    private static final String API_URL = BuildConfig.getServerAPIEndPoint();
    private static final String OAUTH_END_POINT = API_URL + "auth";
    private static final String REVOKE_END_POINT = API_URL + "token/revoke";
    private static final String CLIENT_ID = "g4YbcLRFB7Emsm6";
//    private static final String CLIENT_ID = "trNqZ46pAbyYrQD";
    private static final String CLIENT_SECRET = "D1HUcISL0v0iPQxdHNCUe2XGmQACqywuGxDsIDOrI5o9RqI9prsEKWv4OpHVCEz8";
//    private static final String CLIENT_SECRET = "rkNToLHnVMxafB1AWxZ5kybpXiK94VhHvsdjxbf9Apl9hUR9WnX9gSNJTnvJN9ZY";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ID_TOKEN = "id-token";
    
    public EPassRestStoreClient(Context context) {
        this(context, null);
    }

    public EPassRestStoreClient(Context context, Token token) {
        super(context, new Reference(API_URL), new Reference(OAUTH_END_POINT), null, token,
        		MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(THREAD_POOL_NUMBER)));
        init();
    }

    @SuppressWarnings("deprecation")
	private void init() {
        setClientCredentials(CLIENT_ID, CLIENT_SECRET);
        setTokenType(Type.MAC);
        setRevokeEndpointRef(REVOKE_END_POINT);
        
        ClientHelper helper = new HttpClientHelper(null);
        helper.getHelpedParameters().add("idleCheckInterval", "10000");
        setClientHelper(helper);
    }

    @Override
    public synchronized void setToken(Token token) {
        super.setToken(token);
        if (token != null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            Editor editor = pref.edit();
            if (token instanceof IdToken) {
            	String idToken = ((IdToken) token).getIdToken();
            	if (idToken != null) {
            		editor.putString(KEY_ID_TOKEN, idToken);
            	}
            }
            
            String tokenString = token.serialize();          
            editor.putString(KEY_TOKEN, tokenString).commit();            
        }
    }
    
    public ClientHelper getClientHelper() {
    	return null;
    }
    
    protected void fail(RestRequest request, DatastoreException ex) {
		super.fail(request, ex);
		if (ex.getCause() instanceof NotAuthorizedException
				&& getToken() != null) {
			onLogout();
			handleLogout();
		}
	}
	
	private void handleLogout() {		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(BaseActivity.ACTION_TOKEN_EXPIRED);
		getContext().sendBroadcast(broadcastIntent);
	}

}
