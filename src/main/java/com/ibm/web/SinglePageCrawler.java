package com.ibm.web;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * seed link
 * https://mp.weixin.qq.com/s?__biz=MzA5MTkxNTMzNg==&mid=2650280147&idx=2&sn=e83e5f3d263f0840e7d69845c0c41fbf&key=ebcfb2f67d8558367b6cf74e65fe1cf69b34f05684e6c4074b1c0991cf8b141b032327265c28e1c088b9f3b50fd1d616162cc371dae83f76ead8155476665cd5e66e06ba0192f1841a01fee21e29e98cbf237c9a3018731e4eaa2c9e4b38e6a23e1af718cab0a97164d0fae294ef2356091368362912d68a19b59a06cf77186e&ascene=1&uin=MTI4NDE2MDYxNQ%3D%3D&devicetype=Windows+10+x64&version=6300002f&lang=zh_CN&exportkey=A0k5%2BojRAl9%2F2GyQTtBt5LQ%3D&pass_ticket=EU3drAj9qyMvhE7szc5OQrlK4DpI8jFADfptWX7Hm%2B5fjQVs97UQmWIb1EiBVjmG&wx_header=0
 * get the picture and save it into desk
 * get the link of the picture and print into console
 */
public class SinglePageCrawler {

    private static final String START_URL = "https://mp.weixin.qq.com/s?__biz=MzA5MTkxNTMzNg==&mid=2650280147&idx=2&sn=e83e5f3d263f0840e7d69845c0c41fbf&key=ebcfb2f67d8558367b6cf74e65fe1cf69b34f05684e6c4074b1c0991cf8b141b032327265c28e1c088b9f3b50fd1d616162cc371dae83f76ead8155476665cd5e66e06ba0192f1841a01fee21e29e98cbf237c9a3018731e4eaa2c9e4b38e6a23e1af718cab0a97164d0fae294ef2356091368362912d68a19b59a06cf77186e&ascene=1&uin=MTI4NDE2MDYxNQ%3D%3D&devicetype=Windows+10+x64&version=6300002f&lang=zh_CN&exportkey=A0k5%2BojRAl9%2F2GyQTtBt5LQ%3D&pass_ticket=EU3drAj9qyMvhE7szc5OQrlK4DpI8jFADfptWX7Hm%2B5fjQVs97UQmWIb1EiBVjmG&wx_header=0";

    // get the photo file and save it into this file
//    private static final String IMG_SAVE_PATH = "E:/result"; // save into pc desk
    private static final String IMG_SAVE_PATH = "/root"; // save into linux

    public static void main(String[] args) throws Exception {
        Document rootdocument = Jsoup.connect(START_URL).get();
        List<String> urlList = new ArrayList();
        singlePageParseImg(rootdocument, urlList);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        int i = 0;
        for (String url : urlList) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();
            File file = new File(IMG_SAVE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            String imgName = IMG_SAVE_PATH + File.separator + "result" + i + ".jpg";
            FileOutputStream fos = new FileOutputStream(imgName);
            i++;
            byte[] data = new byte[1024];
            int len;
            while ((len = inputStream.read(data)) != -1) {
                fos.write(data, 0, len);
            }
            response.close();
        }
        httpClient.close();
    }

    public static void singlePageParseImg(Document document, List<String> urls) {
        Elements divElements = document.select("[class=rich_media_content]");
        Elements pElements = divElements.select("figure");
        Elements imgElements = pElements.select("img");
        for (Element imgElement : imgElements) {
            // print out the photo link
            System.out.println(imgElement.attr("data-src"));
            urls.add(imgElement.attr("data-src"));
        }
    }
}
