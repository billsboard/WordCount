
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class Main {

    static DiscordClient client;
    static GatewayDiscordClient gateway;

    public static void main(String[] args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application Terminating...");
            System.out.println("Deregistering event listener");
            gateway.getEventDispatcher().shutdown();
            System.out.println("Clearing memory cache");
            new CacheProcessor(0).run();
            System.out.println("Done");
        }));

        Scanner scan = new Scanner(new FileReader("token.txt"));

        DiscordClient client = DiscordClient.create(scan.nextLine());
        gateway = client.login().block();

        gateway.on(ReadyEvent.class)
                .subscribe(ready -> {System.out.println("Logged in as " + ready.getSelf().getUsername());
                System.out.println("Servicing " + ready.getGuilds().size() + " guilds");});


        System.out.println("ki!");


        /* ---- Init Data Here ---- */
        BotUtils.initData();
        for (int i = 0; i < 2; i++) {
            WriterThread thread = new WriterThread();
            DataProcessor.writerThreadPool.add(thread);
            thread.start();
        }
        for (int i = 0; i < 3; i++) {
            ReaderThread thread = new ReaderThread();
            DataProcessor.readerThreadPool.add(thread);
            thread.start();
        }

        CacheProcessor cacheProcessor = new CacheProcessor(5*60*1000);
        Timer cacheTimer = new Timer();
        cacheTimer.schedule(cacheProcessor, 0, TimeUnit.SECONDS.toMillis(60));

        ServerSaveThread saveProcessor = new ServerSaveThread();
        Timer saveTimer = new Timer();
        saveTimer.schedule(saveProcessor, 0, TimeUnit.MINUTES.toMillis(10));

        /* Remove weird symbols
        for (ServerMessagesObject s : ChannelObjectProcessor.servers) {
            if(s.symbols != null) s.symbols.remove("\u200B");
            for (ChannelMessagesObject c: s.channels) {
                if(c.symbols != null) c.symbols.remove("\u200B");
            }
        } */

        gateway.getEventDispatcher().on(MessageCreateEvent.class)
            .subscribe(x -> {
                try {
                    EventProcessor.onMessageReceived(x.getMessage());
                } catch (Exception e) {
                    //System.out.println(e.getClass().getSimpleName());
                    e.printStackTrace();
                }
            });

        gateway.onDisconnect().block();




    }
}
