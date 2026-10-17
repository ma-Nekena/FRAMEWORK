package mg.itu.framework.util;

public class FileData {
    private String fileName;    
    private String extension;    
    private byte[] bytes;        

    public FileData() {}

    public FileData(String fileName, String extension, byte[] bytes) {
        this.fileName = fileName;
        this.extension = extension;
        this.bytes = bytes;
    }

    public void writeTo(String folderPath) throws java.io.IOException {
        java.io.File dir = new java.io.File(folderPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        java.io.File targetFile = new java.io.File(dir, this.fileName);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(targetFile)) {
            fos.write(this.bytes);
        }
    }

    public String getFileName() { 
        return fileName; 
    }
    public void setFileName(String fileName) { 
        this.fileName = fileName; 
    }

    public String getExtension() { 
        return extension; 
    }
    public void setExtension(String extension) { 
        this.extension = extension; 
    }

    public byte[] getBytes() { 
        return bytes; 
    }
    public void setBytes(byte[] bytes) { 
        this.bytes = bytes; 
    }
}