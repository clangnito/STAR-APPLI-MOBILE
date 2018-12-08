package com.example.constantlangnito.starv1dl;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by matok on 21/11/2017.
 */


public class DecompresZip {

    private String _zipFile;
    private String _location;

    public DecompresZip(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;
        _dirChecker("");
    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public boolean unzip() {
        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }
                    bufout.close();
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
            Log.d("Unzip", "Dezipping end. path :  " + _location);
            return true ;
        } catch (Exception e) {

        }
        return false ;

    }



}
