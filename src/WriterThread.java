public class WriterThread extends Thread{

    @Override
    public void run() {
        System.out.println("[WriterThread]: " + this.getName() + " has begun execution");
        while (true) {
            if (!DataProcessor.writerQueue.isEmpty()) {
                QueueObject q = DataProcessor.writerQueue.poll();

                if (q != null) {
                    try {
                        DataProcessor.add(q.serverID, q.channelID, q.word);
                    } catch (Exception e) {
                        DataProcessor.writerQueue.offer(q);
                        System.out.println("[ERROR]: Error processing " + q.word + " from guild " + q.serverID);
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(75);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
