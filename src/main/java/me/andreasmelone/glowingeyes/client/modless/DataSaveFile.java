package me.andreasmelone.glowingeyes.client.modless;


import com.google.gson.Gson;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DataSaveFile {
    private File file;

    // here we need to implement some gson stuff to save the data to a file and load it from a file
    public void init() {
        // get the .minecraft folder path
        String path = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
        // create a new file in the .minecraft folder called "glowingeyes_data.json" if it doesn't exist
        File file = new File(path + "/glowingeyes_data.json");
        try {
            if(file.createNewFile()) {
                // if the file was created, write the default data to it
                FileOutputStream fos = new FileOutputStream(file);
                fos.write("{\"hasGlowingEyes\":false,\"glowingEyesType\":0}".getBytes());
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.file = file;
    }

    public boolean getHasGlowingEyes() {
        Gson gson = new Gson();
        // we need to get the "hasGlowingEyes" boolean
        try {
            // read the file
            String fileContent = new String(Files.readAllBytes(this.file.toPath()));
            // parse the json
            DataSaveFileModel data = gson.fromJson(fileContent, DataSaveFileModel.class);
            // return the boolean
            return data.hasGlowingEyes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setHasGlowingEyes(boolean hasGlowingEyes) {
        Gson gson = new Gson();
        // we need to set the "hasGlowingEyes" boolean
        try {
            // read the file
            String fileContent = new String(Files.readAllBytes(this.file.toPath()));
            // parse the json
            DataSaveFileModel data = gson.fromJson(fileContent, DataSaveFileModel.class);
            // set the boolean
            data.hasGlowingEyes = hasGlowingEyes;
            // write the new data to the file
            FileOutputStream fos = new FileOutputStream(this.file);
            fos.write(gson.toJson(data).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getGlowingEyesType() {
        Gson gson = new Gson();
        // we need to get the "glowingEyesType" integer
        try {
            // read the file
            String fileContent = new String(Files.readAllBytes(this.file.toPath()));
            // parse the json
            DataSaveFileModel data = gson.fromJson(fileContent, DataSaveFileModel.class);
            // return the integer
            return data.glowingEyesType;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setGlowingEyesType(int type) {
        Gson gson = new Gson();
        // we need to set the "glowingEyesType" integer
        try {
            // read the file
            String fileContent = new String(Files.readAllBytes(this.file.toPath()));
            // parse the json
            DataSaveFileModel data = gson.fromJson(fileContent, DataSaveFileModel.class);
            // set the integer
            data.glowingEyesType = type;
            // write the new data to the file
            FileOutputStream fos = new FileOutputStream(this.file);
            fos.write(gson.toJson(data).getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
