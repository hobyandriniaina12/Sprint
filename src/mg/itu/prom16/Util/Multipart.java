package mg.itu.prom16.Util;

import java.io.*;
import jakarta.servlet.http.Part;

import mg.itu.prom16.Annotation.Attribut;

public class Multipart {
    @Attribut(value = "file")
    private Part part;

    public Multipart() {

    }

    public Multipart(Part part) {
        this.part = part;
    }

    public String getOriginalFilename() {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String content : contentDisp.split(";")) {
                if (content.trim().startsWith("filename")) {
                    return content.substring(content.indexOf("=") + 2, content.length() - 1);
                }
            }
        }
        return null;
    }

    public void transferTo(String uploadDirPath) throws IOException {
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        File file = new File(uploadDir, getOriginalFilename());

        try (InputStream inputStream = part.getInputStream();
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
