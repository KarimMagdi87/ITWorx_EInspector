package itworx.com.e_inspector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
public class AzureBlopManager {

    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=eeinspector;" +
                    "AccountKey=pbMmUir93skVkx+MgAse/cm9kqaetMSraPGdWk1V7SSAPgxdh6psGBdqpTFj/5VzkDssnPtgz++FmNnLu22QaA==";

    public static URI uploadBlop(String filePath,String filename) {
        URI url = null;
        try {

            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference("mycontainer");
            container.createIfNotExists();
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
            container.uploadPermissions(containerPermissions);
            //final String filePath = "/storage/emulated/0/DCIM/Facebook/FB_IMG_1419809576298.jpg";
            CloudBlockBlob blob = container.getBlockBlobReference(filename);
            File source = new File(filePath);
            blob.upload(new FileInputStream(source), source.length());
             url =   blob.getQualifiedUri();
            Log.d("", "url" + url);
        } catch (Exception e) {
            // Output the stack trace.
            Log.d("", "einspector_error:" + e.getMessage());
        }
        return url;
    }
    public static void allImages(Context context)
    {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor;
        Uri allimagessuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media._ID + " != 0";

        cursor = cr.query(allimagessuri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.i("Image", fullpath + "");


                } while (cursor.moveToNext());
            }
            cursor.close();
        }

    }

    public static void getAllBlops()
    {
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

            // Loop over blobs within the container and output the URI to each of them.

            for (ListBlobItem blobItem : container.listBlobs()) {
                System.out.println(blobItem.getUri());
            }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }



}
