import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class ReplaceCurrency {
    public static void main(String[] args) throws Exception {
        String baseDir = "client/src/main";
        Files.walk(Paths.get(baseDir))
            .filter(Files::isRegularFile)
            .forEach(path -> {
                try {
                    String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                    if (content.contains("₪")) {
                        content = content.replace("₪", "₹");
                        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
                        System.out.println("Replaced in " + path);
                    }
                } catch (Exception e) {}
            });
    }
}
