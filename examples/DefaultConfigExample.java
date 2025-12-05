import com.eachid.EachId;

/**
 * EachId 默认配置示例
 * 使用零配置即可获得极佳性能：约 1000万+ QPS，兼容 JDK 8+
 */
public class DefaultConfigExample {

    public static void main(String[] args) {
        // 一行代码，零配置启动！自动分配 WorkerId
        EachId eachId = new EachId().autoWorkerId();

        System.out.println("EachId 默认配置信息：");
        System.out.println(eachId.getInfo());
        // 输出示例：
        // Epoch           : 2025-01-01
        // StepMs          : 100 ms
        // Bits            : 35(ts)+0(dc)+8(wk)+20(seq)=63 bits
        // Capacity        : 256 nodes | 1,048,576 IDs/100ms (≈1048万QPS理论值)

        System.out.println("\n生成 10 个全局单调递增 ID：");
        for (int i = 0; i < 10; i++) {
            System.out.println("ID = " + eachId.nextId());
        }

        // 批量生成（极致性能场景）
        long startId = eachId.nextId(1000);
        System.out.println("\n批量预生成 1000 个连续 ID，从 " + startId + " 开始");
    }
}