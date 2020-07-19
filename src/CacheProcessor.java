import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class CacheProcessor extends TimerTask {

    long timeInMills;

    CacheProcessor(long timeInMills){
        this.timeInMills = timeInMills;
    }

    @Override
    public void run() {
        if(!DataProcessor.doingFullWrite) {
            try {
                System.out.println("[CacheProcessor]: Beginning execution of cache optimization");
                long time = new Date().getTime();

                Iterator<Map.Entry<String, ServerObject>> it = DataProcessor.servers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ServerObject> entry = it.next();

                    Iterator<Map.Entry<String, ServerObject.TreeMapPair>> channelIter = entry.getValue().channelPairs.entrySet().iterator();
                    while (channelIter.hasNext()) {
                        Map.Entry<String, ServerObject.TreeMapPair> channel = channelIter.next();

                        if (time - channel.getValue().lastAccessTime > timeInMills) {
                            entry.getValue().saveChannelNoRemove(channel.getKey());
                            channelIter.remove();
                        }
                    }

                    if (time - entry.getValue().lastAccessTime > timeInMills) {
                        DataProcessor.writeToFileNoRemove(entry.getKey());
                        it.remove();
                        System.out.println("[Unload]: " + entry.getKey() + " was unloaded and moved to disk");
                    }
                }
            } catch (Exception e) {
                System.err.println("[ERROR]: Caching operation failed! Cache was not cleaned and optimized!");
            }
        }
        else {
            System.out.println("[CacheProcessor]: Skipping cache cleanup due to full replication running");
        }
    }
}
