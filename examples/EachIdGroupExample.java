import com.eachid.EachIdGroup;

/**
 * EachIdGroup 多实例并行生成示例
 * 适用于超高并发场景：> 5000万 QPS
 */
public class EachIdGroupExample {

    public static void main(String[] args) throws InterruptedException {
        // 创建 4 个实例，WorkerId 从 10 开始分配：10,11,12,13
        EachIdGroup group = new EachIdGroup()
                .setTimestampBits(35)
                .setWorkerIdBits(8)
                .setSequenceBits(20)
                .setStepMs(100)
                .setStartWorkerIdAndCount(10, 4)
                .setBalancingStrategy(EachIdGroup.BalancingStrategy.THREAD_LOCAL_FIXED); // 最佳性能

        System.out.println("EachIdGroup 配置（4实例）：");
        System.out.println(group.getInfo());

        // 模拟高并发生成
        System.out.println("\n高并发生成 20 个 ID：");
        for (int i = 0; i < 20; i++) {
            System.out.println("ID = " + group.nextId());
        }

        // 性能测试（手动）
        long start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            group.nextId();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("\n1百万 ID 生成耗时: %.2f ms (≈ %.0f 万 QPS)%n",
                elapsed / 1_000_000.0, 1_000_000.0 / (elapsed / 1_000_000_000.0) / 10_000);
    }
}