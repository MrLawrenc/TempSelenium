package com.github.mrlawrenc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author : MrLawrenc
 * date  2020/9/26 22:54
 */
public class CsdnReptile {
    /**
     * blog-content-box
     * p 是页数，一个搜索结果一般20页
     * https://so.csdn.net/so/search/s.do?q=java&t=all&platform=pc&p=40&s=&tm=&v=&l=&u=&ft=
     */
    public static void main(String[] args) throws Exception {

        String seedUrlFormat = "https://so.csdn.net/so/search/s.do?q=java&t=all&platform=pc&p=%d&s=&tm=&v=&l=&u=&ft=";
        new CsdnReptile().seed(seedUrlFormat, 1);

      /*  HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://blog.csdn.net/qq_40695278/article/details/89073220"))
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        System.out.println(response.body());*/
    }

    public void seed(String format, int page) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .executor(Executors.newCachedThreadPool())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(format, page)))
                .GET()
                .timeout(Duration.ofSeconds(60))
                .build();


        AtomicInteger count = new AtomicInteger(0);
        int sum = 20;
        CompletableFuture<?>[] futures = IntStream.range(0, sum).mapToObj(i ->
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).whenComplete((r, t) -> {
                    if (t == null) {
                        count.incrementAndGet();
                    }
                })
        ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
        for (CompletableFuture<?> future : futures) {
            HttpResponse<String> response = (HttpResponse<String>) future.get();
            System.out.println(response.body());
        }
        System.out.println("成功数:" + count.get() + "  比例:" + count.get() / sum);
    }
}