package aionem.net.sdk.web.utils;

import aionem.net.sdk.core.utils.UtilsText;


public class UtilsDrive {

    public static final String DRIVE_FILE_URL = "url";
    public static final String DRIVE_FILE_PATH = "path";
    public static final String DRIVE_FILE_NAME = "name";
    public static final String DRIVE_FILE_SIZE = "size";
    public static final String DRIVE_FILE_EXTENSION = "extension";

    public static String generateFileName(final String db, final long id, final String extension) {
        return generateFileName(db, UtilsText.toString(id), extension);
    }

    public static String generateFileName(final String db, final String id, final String extension) {
        return db + "-" + id + "." + extension;
    }

    public static String getFileExtension(final String fileName) {
        String fileExtension = "";
        final int lastIndex = fileName.lastIndexOf(".");
        if(lastIndex > 0 && lastIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(lastIndex + 1);
        }
        return fileExtension;
    }

    public static String getUploadFolder(final String fileExtension) {
        String uploadFolder = "others";

        if(!UtilsText.isEmpty(fileExtension)) {

            switch (fileExtension.toLowerCase()) {

                // Images
                case "png":
                case "jpg":
                case "jpeg":
                case "gif":
                case "bmp":
                    uploadFolder = "images";
                    break;

                // Videos
                case "mp4":
                case "avi":
                case "mov":
                case "mkv":
                case "wmv":
                    uploadFolder = "videos";
                    break;

                // Documents
                case "doc":
                case "docx":
                case "pdf":
                case "txt":
                case "rtf":
                    uploadFolder = "documents";
                    break;

                // Compressed
                case "zip":
                case "rar":
                case "7z":
                case "gz":
                case "tar":
                    uploadFolder = "compressed";
                    break;

                default:
                    uploadFolder = "others";
            }
        }

        return uploadFolder;
    }

}
