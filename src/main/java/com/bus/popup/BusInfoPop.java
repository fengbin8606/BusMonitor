
package com.bus.popup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class BusInfoPop {

    // 732路公交 华农楚天学院———>光谷广场方向
    String url732 = "http://bus.wuhancloud.cn:9087/website//web/420100/line/732/0.do?Type=LineDetail&lineNo=732&direction=0";

    String url755 = "http://bus.wuhancloud.cn:9087/website//web/420100/line/755/0.do?Type=LineDetail&lineNo=755&direction=0";

    OkHttpClient client = new OkHttpClient();

    private static Logger logger = LoggerFactory.getLogger(BusInfoPop.class);

    @Async
    @Scheduled(cron = "0 */1 * * * ? ")
    public void pop() {

        BottomRightPop busInfoPop = new BottomRightPop();

        List<String> buses = getStateOfLine(url732);
        String display = "";
        if (buses.size() == 0) {
            logger.error("未获取到车辆信息");
        }

        int i = 1;
        for (String str : buses) {
            if (str.equals("未发车")) {
                display = str;
            } else {
                display = display + "第" + i + "辆车" + str + "\n";
            }
            i++;
        }

        busInfoPop.show("732路", display);

    }

    public List<String> getStateOfLine(String url) {
        List<String> result = new ArrayList<String>();
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String respStr = response.body().string();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(respStr);

                JsonNode buses = jsonNode.findValue("buses");

                // 没有车的情况
                if (buses.size() == 0) {
                    String text = "未发车";
                    result.add(text);
                }

                for (JsonNode bus : buses) {
                    // System.out.println(bus.asText());
                    String[] info = bus.asText().split("\\|");
                    int local = Integer.valueOf(info[2]);// 当前到站的位置
                    // System.out.println(local);
                    if (local < 10) {
                        int remainder = 10 - local;
                        String text = remainder + "站后到达";
                        result.add(text);
                    }
                    if (local == 10) {
                        String text = "即将到达";
                        result.add(text);
                    }
                }
                if (result.size() == 0) {
                    String text = "未发车";
                    result.add(text);
                }

                Collections.reverse(result);
                return result;

            }
        } catch (Exception e) {
            logger.error("出错了", e);
        }
        return result;
    }

    public static void main(String[] args) {
        BusInfoPop b = new BusInfoPop();
        String str = "6888|11|3|0|114.42473962973061|30.414283429242595";
        String[] strs = str.split("[|]");
        for (int i = 0; i < strs.length; i++) {
            System.out.println(strs[i]);
        }
    }

}
