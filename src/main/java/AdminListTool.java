import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminListTool {
    public static final String version = "0.01";
    private static final int UID_LENGTH = 18;

    public static void main(String[] args){
        System.out.println("AdminListTool v" + version);
        String uid;
        uid = (args.length > 0 && args[0] != null) ? args[0] : readUID();
        ArrayList<Long> list = new ArrayList<>();
        list.add(Long.parseLong(uid));
        saveSerializedAdminList(list);
    }

    private static String readUID() {
        System.out.println("Enter a Discord UID to generate a adminList.obj file :");
        Scanner s = new Scanner(System.in);
        String uid = s.nextLine();
        if(checkUID(uid)){
            return uid;
        } else {
            return readUID();
        }
    }

    private static boolean checkUID(String uid) {
        return uid.length() == UID_LENGTH;
    }

    public static void saveSerializedAdminList(ArrayList<Long> list){
        System.out.println("Attempting to save admin list object...");
        FileOutputStream file;
        try{
            file = new FileOutputStream("adminList.obj");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(list);
            stream.flush();
            stream.close();
            System.out.println("Success.");
        } catch (IOException e) {
            // file is locked by another process, or file is non existent even though it was previously opened, or couldn't write file
            System.out.println("Admin list object \"adminList.obj\" failed to save. Check your write privileges.");
        }
    }
}
