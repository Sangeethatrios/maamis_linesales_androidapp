package trios.linesales;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class PermissionManager {

    public static Context context;
    private static PermissionManager permissionManager = new PermissionManager();

    private ArrayList<String> defaultPermissionGroup =
            new ArrayList<>(Arrays.asList(Manifest.permission.READ_PHONE_STATE));

    private ArrayList<String> locationGroup =
            new ArrayList<>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION));

    private ArrayList<String> StoragePermissionGroup =
            new ArrayList<>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE));

    private ArrayList<String> StoragePermissionGroup_Android11 =
            new ArrayList<>(Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE));

    private ArrayList<String> StoragePermissionGroup_Android13 =
            new ArrayList<>(Arrays.asList(Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO));

    private ArrayList<String> StoragePermissionGroup_Android14 =
            new ArrayList<>(Arrays.asList(Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED));

    private ArrayList<String> BluetoothPermissionGroup_Android12AndAbove =
            new ArrayList<>(Arrays.asList(Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT));



    public static PermissionManager getInstance(){
        return permissionManager;
    }

    public static boolean isNeedToAskRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public boolean checkAndAskPermissionForThisEvent(final Object invoker, final int reqCode){

        if(!isNeedToAskRunTimePermission())
            return true;

        Activity activity = (Activity) invoker;
        context = activity.getApplicationContext();

        final ArrayList<String> permissions = new ArrayList<>();
        addPermissionsByReqType(reqCode, permissions);

        if(permissions.size() == 0)
            return true;

        showSystemPermissionDialog(permissions, (Activity) invoker, reqCode);

        return false;
    }

    private void addPermissionsByReqType(int reqCode, ArrayList<String> permissions){
        switch (reqCode) {
            case Constants.DEFAULT_PERMISSION:
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    defaultPermissionGroup.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    defaultPermissionGroup.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                checkAndAddPermissionInList(permissions, defaultPermissionGroup);
                break;

            case Constants.WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14
                    checkAndAddPermissionInList(permissions, StoragePermissionGroup_Android14);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
                    checkAndAddPermissionInList(permissions, StoragePermissionGroup_Android13);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11
                    checkAndAddPermissionInList(permissions, StoragePermissionGroup_Android11);
                } else {
                    checkAndAddPermissionInList(permissions, StoragePermissionGroup);
                }
                break;

            case Constants.PERMISSION_LOCATION:
                locationGroup.clear();
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    locationGroup.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    locationGroup.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                checkAndAddPermissionInList(permissions, locationGroup);
                break;

            case Constants.BLUETOOTH_PERMISSION_ANDROID_12_AND_ABOVE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkAndAddPermissionInList(permissions, BluetoothPermissionGroup_Android12AndAbove);
                }
                break;

            default:
                break;
        }
    }

    private static void checkAndAddPermissionInList(ArrayList<String> toBeAsked, ArrayList<String> groupList){
        for(String permissionString: groupList) {
            if (ContextCompat.checkSelfPermission(context, permissionString) != PackageManager.PERMISSION_GRANTED) {
                toBeAsked.add(permissionString);
            }
        }
    }

    private void showSystemPermissionDialog(ArrayList<String> permissions, final Activity activity, final int reqCode){
        ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), reqCode);
    }

}

