package com.example.zkproject.viewmodel;

import com.example.zkproject.repository.DocumentRepository;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

import java.util.*;

public class DashboardViewModel {

    private DocumentRepository documentRepository = new DocumentRepository();

    @Init
    public void init() {
        Map<String, Map<String, Integer>> stats = documentRepository.countByTypeAndTrangThai();

        int[] den = new int[4];
        int[] di = new int[4];

        List<String> trangThaiList = Arrays.asList("CHO_XU_LY", "DANG_XU_LY", "HOAN_THANH", "QUA_HAN");

        for (String trangThai : trangThaiList) {
            int index = -1;
            switch (trangThai) {
                case "CHO_XU_LY":
                    index = 0;
                    break;
                case "DANG_XU_LY":
                    index = 1;
                    break;
                case "HOAN_THANH":
                    index = 2;
                    break;
                case "QUA_HAN":
                    index = 3;
                    break;
            }

            if (index != -1) {
                den[index] = stats.getOrDefault("DEN", Collections.emptyMap()).getOrDefault(trangThai, 0);
                di[index] = stats.getOrDefault("DI", Collections.emptyMap()).getOrDefault(trangThai, 0);
            }
        }

        String labels = "['CHỜ XỬ LÝ', 'Đang xử lý', 'HOÀN THÀNH', 'Quá Hạn']";
        String dataDen = Arrays.toString(den);
        String dataDi = Arrays.toString(di);

        String js = "function drawChart() {\n" +
                "  const ctx = document.getElementById('statusChart').getContext('2d');\n" +
                "  new Chart(ctx, {\n" +
                "    type: 'bar',\n" +
                "    data: {\n" +
                "      labels: " + labels + ",\n" +
                "      datasets: [\n" +
                "        {\n" +
                "          label: 'Văn bản đến',\n" +
                "          data: " + dataDen + ",\n" +
                "          backgroundColor: 'rgba(54, 162, 235, 0.6)',\n" +
                "          stack: 'Stack 0'\n" +
                "        },\n" +
                "        {\n" +
                "          label: 'Văn bản đi',\n" +
                "          data: " + dataDi + ",\n" +
                "          backgroundColor: 'rgba(255, 99, 132, 0.6)',\n" +
                "          stack: 'Stack 0'\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    options: {\n" +
                "      responsive: true,\n" +
                "      scales: {\n" +
                "        x: {\n" +
                "          stacked: true\n" +
                "        },\n" +
                "        y: {\n" +
                "          stacked: true,\n" +
                "          beginAtZero: true\n" +
                "        }\n" +
                "      },\n" +
                "      plugins: {\n" +
                "        legend: { display: true }\n" +
                "      }\n" +
                "    }\n" +
                "  });\n" +
                "}\n" +
                "if (typeof Chart === 'undefined') {\n" +
                "  const script = document.createElement('script');\n" +
                "  script.src = 'https://cdn.jsdelivr.net/npm/chart.js';\n" +
                "  script.onload = drawChart;\n" +
                "  document.head.appendChild(script);\n" +
                "} else {\n" +
                "  drawChart();\n" +
                "}";

        Clients.evalJavaScript(js);
    }
}