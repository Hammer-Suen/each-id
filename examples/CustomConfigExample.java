import com.eachid.EachId;

/**
 * EachId 完全自定义位数配置示例
 * 展示如何根据业务场景精准分配 63 位
 */
public class CustomConfigExample {

    public static void main(String[] args) {
        // 高并发场景：追求极致 QPS（推荐生产环境）
        EachId highThroughput = new EachId()
                .setTimestampBits(35)     // 35位时间戳 ≈ 109年（100ms步长）
                .setWorkerIdBits(8)       // 8位 → 256 个节点
                .setSequenceBits(20)      // 20位 → 每100ms可生成 104万 ID
                .setStepMs(100)
                .autoWorkerId();

        // 超大容量场景：追求最大 ID 数量
        EachId largeCapacity = new EachId()
                .setTimestampBits(31)     // 31位 ≈ 68年（1000ms步长）
                .setWorkerIdBits(6)       // 6位 → 64 个节点
                .setSequenceBits(26)      // 26位 → 每秒可生成 6700万 ID！
                .setStepMs(1000)
                .setWorkerId(1);          // 手动指定 WorkerId

        System.out.println("高并发配置（推荐）：");
        System.out.println(highThroughput.getInfo());

        System.out.println("\n超大容量配置：");
        System.out.println(largeCapacity.getInfo());

        System.out.println("\n生成 ID 验证：");
        System.out.println("高并发ID: " + highThroughput.nextId());
        System.out.println("大容量ID: " + largeCapacity.nextId());
    }
}