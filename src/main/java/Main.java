import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpGet request = new HttpGet(
                    "https://api.nasa.gov/planetary/apod?api_key=g5ye0pl9z5A9keaAxvEchDsSeqlFBn8M2YLBBW2M");

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                Post post = mapper.readValue(response.getEntity().getContent(), new TypeReference<Post>() {
                });

                HttpGet requestForImg = new HttpGet(post.getHdurl());
                try (CloseableHttpResponse responseImg = httpClient.execute(requestForImg)) {

                    downloadImg(getName(post), responseImg);

                }
            }
        }
    }

    public static String getName(Post post) {
        return post.getHdurl().substring(post.getHdurl().lastIndexOf("/") + 1);
    }

    public static File createImg(String name) {
        File file = new File("C://nasa/" + name);
        try {
            file.createNewFile();
            System.out.println(name + " - создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return file;
    }

    public static void downloadImg(String name, CloseableHttpResponse response) throws IOException {
        byte[] bytes = response.getEntity().getContent().readAllBytes();
        try (FileOutputStream fos = new FileOutputStream(createImg(name));
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            System.out.println("Файл " + name + " записан");
        }
    }
}
