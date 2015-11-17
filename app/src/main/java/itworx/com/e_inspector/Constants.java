package itworx.com.e_inspector;

/**
 * Created by karim on 10/25/2015.
 */
public class Constants {
    //Crud
    public static final String ACCOUNT_NAME = "eeinspector";
    public static final String ACCOUNT_KEY = "pbMmUir93skVkx+MgAse/cm9kqaetMSraPGdWk1V7SSAPgxdh6psGBdqpTFj/5VzkDssnPtgz++FmNnLu22QaA==";
    public static final String CASES_URI = "http://einspector.azurewebsites.net/tables/case";

    //Login
    // AAD PARAMETERS
    // https://login.windows.net/tenantInfo
    static final String AUTHORITY_URL = "https://login.windows.net/common";

    // Clientid is given from AAD page when you register your Android app
    static final String CLIENT_ID = "a4063e37-07c9-471d-b935-fd38d60dcb7d";

    // RedirectUri
    static final String REDIRECT_URL = "http://helloapp";

    // URI for the resource. You need to setupRecorder this resource at AAD
    static final String RESOURCE_ID = "2720df1b-a913-4b2d-be8c-e759a4aaa36a";

    // Endpoint we are targeting for the deployed WebAPI service
    static final String SERVICE_URL = "https://android.azurewebsites.net/api/values";



}
