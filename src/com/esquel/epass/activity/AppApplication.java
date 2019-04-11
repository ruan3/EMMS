package com.esquel.epass.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.esquel.epass.ConfigurationManager;
import com.esquel.epass.R;
import com.esquel.epass.RetrieveNewsDatabaseManager;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.oauth.EPassRestStoreClient;
import com.esquel.epass.utils.BuildConfig;
import com.esquel.epass.utils.EsquelPassRegion;
import com.esquel.epass.utils.LocaleUtils;
import com.esquel.epass.utils.LocaleUtils.SupportedLanguage;
import com.esquel.epass.utils.LogUtils;
import com.joyaether.datastore.Datastore;
import com.joyaether.datastore.rest.RestStore;
import com.joyaether.datastore.rest.oauth.OAuthToken;
import com.joyaether.datastore.rest.oauth.Token;
import com.joyaether.datastore.sqlite.SqliteStore;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * 
 * @author joyaether
 * 
 */
@ReportsCrashes(formKey = "", mailTo = "torreslam@joyaether.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.acra_message_to_users)

public class AppApplication extends Application {
	
	public static final String ACTION_LOGOUT = "com.esquel.epass.ACTION_LOGOUT";

    private static final int MILLISECOND = 1000;
    private static final int BUFFER_SIZE = 1024;
    private EPassSqliteStoreOpenHelper sqliteStoreOpenHelper;
    private RestStore restStore;
    private int offsetTime = -1;
    private boolean isLaunchApp = true;
    private ImageDownloader downloader;
    private static final String FILE_PATH = "image_cache";
    public static final String DEFAULT_TIMEZONE = "GMT+08";

    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("ssl.TrustManagerFactory.algorithm",
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
//        ACRA.init(this);
        try {
            replaceDatabase();
        } catch (Exception e) {
        }
        SupportedLanguage language = LocaleUtils.getLanguage(this);
		LocaleUtils.setLanguage(this, language != null ? language : SupportedLanguage.getSupportedLanguage(Locale.CHINESE.toString()));
    }
    
    public String getDefaultTimeZone() {
    	return getResources().getString(R.string.default_time_zone);
    }

  

   

    public synchronized SqliteStore getSqliteStore() {
        if (sqliteStoreOpenHelper == null) {
            sqliteStoreOpenHelper = new EPassSqliteStoreOpenHelper(this);
        }
        return Datastore.getInstance().getSqliteStore(sqliteStoreOpenHelper);
    }

    public synchronized RestStore getRestStore() {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (restStore == null) {    
        	LogUtils.e("111restStore-->");
            String tokenString = pref.getString(EPassRestStoreClient.KEY_TOKEN,null);
            String apiEndPoint = pref.getString(
            		ConfigurationManager.RESOURCE_END_POINT_FIELD_NAME, 
            		BuildConfig.getServerAPIEndPoint());
            String authEndPoint = pref.getString(
                    ConfigurationManager.AUTHORIZATION_END_POINT, 
                    BuildConfig.getServerAPIEndPoint() + "auth/");
            String revokeEndPoint = pref.getString(
                    ConfigurationManager.REVOCATION_END_POINT, 
                    BuildConfig.getServerAPIEndPoint() + "token/revoke/");          
            EPassRestStoreClient client = null;
            if (tokenString == null) {
            	client = new EPassRestStoreClient(this);
            } else {
            	Token token = OAuthToken.deserialize(tokenString);
            	client = new EPassRestStoreClient(this, token);
            }

            if (apiEndPoint != null) {
            	LogUtils.e("111apiEndPoint-->"+apiEndPoint);
                client.setReference(apiEndPoint);
            }

            if (authEndPoint != null) {
            	LogUtils.e("111authEndPoint-->"+authEndPoint);
                client.setAuthEndpointRef(authEndPoint);
            }

            if (revokeEndPoint != null) {
            	LogUtils.e("111revokeEndPoint-->"+revokeEndPoint);
                client.setRevokeEndpointRef(revokeEndPoint);
            }

            restStore = Datastore.getInstance().getRestStore(client);
            
        }
       
        return restStore;
    }

    public synchronized void resetRestStore() {
        if (restStore != null) {
        	
        	SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(this);
            String apiEndPoint = pref.getString(
                    ConfigurationManager.RESOURCE_END_POINT_FIELD_NAME, null);
            String authEndPoint = pref.getString(
                    ConfigurationManager.AUTHORIZATION_END_POINT, null);
            String revokeEndPoint = pref.getString(
                    ConfigurationManager.REVOCATION_END_POINT, null);
            if (apiEndPoint != null) {
            	restStore.getClient().setReference(apiEndPoint);
            }

            if (authEndPoint != null) {
            	restStore.getClient().setAuthEndpointRef(authEndPoint);
            }

            if (revokeEndPoint != null) {
            	restStore.getClient().setRevokeEndpointRef(revokeEndPoint);
            }          
        }
    }

    public synchronized void releaseDataStore() {
        if (sqliteStoreOpenHelper != null) {
            Datastore.getInstance().releaseStore(getSqliteStore());
            sqliteStoreOpenHelper = null;
        }
    }

    public void replaceDatabase() throws IOException {
        // release the data store first
        releaseDataStore();
        String dbName = EsquelPassRegion.getDefault(this).toString() + ".db";
        File downloadDB = new File(getExternalFilesDir(null).getParent() + "/"
                + RetrieveNewsDatabaseManager.DOWNLOAD_DB_PATH, dbName);
        File currentDB = new File(getExternalFilesDir(null).getAbsolutePath(),
                dbName);
        if (!downloadDB.exists() || !currentDB.exists()) {
            return;
        }
        moveFile(downloadDB, currentDB);
    }

    private void moveFile(File source, File destination) throws IOException {
        FileInputStream inputStream = new FileInputStream(source);
        moveFile(inputStream, source.getName(), destination);
        inputStream.close();
        // delete file on original place
        source.delete();
    }

    private void moveFile(InputStream inputStream, String fileName,
            File destination) throws IOException {

        File dbFile = getDatabasePath(fileName);
        if (dbFile != null && !dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
            dbFile.createNewFile();
        }
        OutputStream out = new FileOutputStream(destination.getAbsolutePath());

        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.close();
        // delete file on original place
    }

    public boolean isResumeApp() {
        return !isLaunchApp;
    }

    public void setIsLaunchApp(boolean b) {
        isLaunchApp = b;
    }

    public int getOffSet() {
        if (offsetTime == -1) {
            TimeZone tz = TimeZone.getDefault();
            Date now = new Date();
            offsetTime = tz.getOffset(now.getTime()) / MILLISECOND;
        }
        return offsetTime;
    }

    public ImageDownloader getImageDownloader() {
        if (downloader == null) {
            downloader = new ImageDownloader(this, FILE_PATH);
        }
        return downloader;
    }
}
