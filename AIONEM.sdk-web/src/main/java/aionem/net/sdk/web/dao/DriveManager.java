package aionem.net.sdk.web.dao;

import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.web.AioWeb;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


@Log4j2
public class DriveManager {

    public static final String DRIVE_API_PATH = "/ui.drive/uploads/";
    public static final String DRIVE_FILE = "file";
    public static final String DRIVE_FILE_URL = "url";
    public static final String DRIVE_FILE_PATH = "path";
    public static final String DRIVE_FILE_NAME = "name";
    public static final String DRIVE_FILE_SIZE = "size";
    public static final String DRIVE_FILE_EXTENSION = "extension";

    private final AioWeb aioWeb;

    public DriveManager(final AioWeb aioWeb) {
        this.aioWeb = aioWeb;
    }

    public DaoRes uploadFile(final Part filePart, String uploadName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(filePart != null) {

                final InputStream fileStream = filePart.getInputStream();
                final String fileName = UtilsText.notEmpty(filePart.getName(), filePart.getName());
                uploadName = UtilsText.notEmpty(uploadName, fileName);

                return uploadFile(fileStream, uploadName, fileName);

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final File file, String uploadName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(file.exists() && file.isFile()) {

                return uploadFile(new FileInputStream(file), uploadName, file.getName());

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final InputStream fileStream, String uploadName, String fileName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(fileStream != null) {

                String fileExtension = getFileExtension(uploadName);

                if(UtilsText.isEmpty(fileExtension)) {
                    fileExtension = getFileExtension(fileName);
                    uploadName = uploadName + (!uploadName.endsWith(".") ? "." : "") + fileExtension;
                }

                final String fileFolder = getFileFolder(fileExtension);
                final String filePath = fileFolder +"/" + uploadName;
                final String fileUrl = aioWeb.getContextPath(DRIVE_API_PATH) +"/"+ filePath;

                final File fileDirectory = new File(aioWeb.getRealPathRoot(DRIVE_API_PATH +"/"+ fileFolder));

                final boolean isDirectory;
                if(!fileDirectory.exists()) {
                    isDirectory = fileDirectory.mkdirs();
                }else {
                    isDirectory = true;
                }

                if(isDirectory) {

                    final File file = new File(fileDirectory.getPath() + "/" + uploadName);
                    final FileOutputStream outputStream = new FileOutputStream(file);

                    final byte[] buffer = new byte[4096];
                    int readSize;
                    while((readSize = fileStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, readSize);
                    }
                    fileStream.close();

                    final long fileSize = file.length();

                    resUpload.setSuccess(true);

                    resUpload.put(DRIVE_FILE, fileName);
                    resUpload.put(DRIVE_FILE_NAME, uploadName);
                    resUpload.put(DRIVE_FILE_PATH, filePath);
                    resUpload.put(DRIVE_FILE_URL, fileUrl);
                    resUpload.put(DRIVE_FILE_SIZE, fileSize);
                    resUpload.put(DRIVE_FILE_EXTENSION, fileExtension);

                }else {
                    resUpload.setError("Directory doesn't exist");
                }

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public static String generateFileName(final String db, final long id, final String extension) {
        return generateFileName(db, UtilsText.toString(id), extension);
    }

    public static String generateFileName(final String db, final String id, final String extension) {
        return db +"-"+ id +"."+ extension;
    }

    public static String getFileExtension(final String fileName) {
        String fileExtension = "";
        final int lastIndex = fileName.lastIndexOf(".");
        if(lastIndex > 0 && lastIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(lastIndex + 1);
        }
        return fileExtension;
    }

    public static String getFileFolder(final String fileExtension) {
        String fileFolder = "others";

        if(!UtilsText.isEmpty(fileExtension)) {

            switch (fileExtension.toLowerCase()) {

                // Images
                case "png":
                case "jpg":
                case "jpeg":
                case "gif":
                case "bmp":
                    fileFolder = "images";
                    break;

                // Videos
                case "mp4":
                case "avi":
                case "mov":
                case "mkv":
                case "wmv":
                    fileFolder = "videos";
                    break;

                // Documents
                case "doc":
                case "docx":
                case "pdf":
                case "txt":
                case "rtf":
                    fileFolder = "documents";
                    break;

                // Compressed
                case "zip":
                case "rar":
                case "7z":
                case "gz":
                case "tar":
                    fileFolder = "compressed";
                    break;

                default:
                    fileFolder = "others";
            }
        }

        return fileFolder;
    }

}
