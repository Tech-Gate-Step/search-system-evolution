package tech.gate.step.searchsystemevolution.util;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * products.csv 생성기 (1,000,000건)
 * DB 테이블 products와 컬럼 순서 맞춤
 */
public class CsvDataGenerator {

    public static void main(String[] args) throws IOException {
        String file = "products.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            String[] brands = {"삼성", "애플", "LG", "샤오미", "나이키", "아디다스", "소니", "MSI"};
            String[] adjectives = {"프리미엄", "가성비", "신형", "대용량", "경량", "한정판", "베스트"};
            String[] features = {"무선", "방수", "고속충전", "에너지절약", "HDR", "QLED", "OLED", "HEPA필터"};

            Random random = new Random();

            // ✅ CSV 헤더 추가
            writer.write("sku,name,description,brand,category_id,price,stock,rating,sales_count");
            writer.newLine();

            int total = 1_000_000;
            for (int i = 0; i < total; i++) {
                String sku = "SKU" + String.format("%08d", i);
                String brand = brands[random.nextInt(brands.length)];
                String name = brand + " " + adjectives[random.nextInt(adjectives.length)] + " 모델 " + i;
                String desc = name + " - " + features[random.nextInt(features.length)];

                int categoryId = random.nextInt(10) + 1; // 1~10
                int price = 1_000 + random.nextInt(1_000_000); // 1천~100만
                int stock = random.nextInt(500); // 재고 0~499
                double rating = Math.round((1 + random.nextDouble() * 4) * 10.0) / 10.0; // 1.0~5.0
                int salesCount = random.nextInt(10_000); // 0~9999

                // CSV 라인 생성 (id, created_at 제외)
                String line = String.join(",",
                        sku,
                        "\"" + name + "\"",
                        "\"" + desc + "\"",
                        brand,
                        String.valueOf(categoryId),
                        String.valueOf(price),
                        String.valueOf(stock),
                        String.valueOf(rating),
                        String.valueOf(salesCount)
                );

                writer.write(line);
                writer.newLine();

                if (i % 100_000 == 0) {
                    System.out.println("✅ " + i + " rows generated...");
                }
            }

            System.out.println("🎉 CSV 파일 생성 완료: " + file);
        }
    }
}
