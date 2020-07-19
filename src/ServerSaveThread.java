import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class ServerSaveThread extends TimerTask {

    @Override
    public void run() {
        try {
            System.out.println("[CacheSaver]: Beginning contingency save of data to disk");
            DataProcessor.doingFullWrite = true;

            Iterator<Map.Entry<String, ServerObject>> it = DataProcessor.servers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ServerObject> entry = it.next();

                Iterator<Map.Entry<String, ServerObject.TreeMapPair>> channelIter = entry.getValue().channelPairs.entrySet().iterator();
                while (channelIter.hasNext()) {
                    Map.Entry<String, ServerObject.TreeMapPair> channel = channelIter.next();

                    entry.getValue().saveChannelNoRemove(channel.getKey());
                }

                DataProcessor.writeToFileNoRemove(entry.getKey());
                System.out.println("[CacheSaver]: Replicated guild " + entry.getKey() + " to disk");
            }
        } catch (Exception e){
            System.err.println("[ERROR]: Save failed, data was not replicated to disk");
            e.printStackTrace();
        }finally {
            DataProcessor.doingFullWrite = false;
        }
    }
}
