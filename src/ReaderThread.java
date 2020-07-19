import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.http.client.ClientException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class ReaderThread extends Thread{

    public void run(){
        while(true){
            if(!DataProcessor.readerQueue.isEmpty()){
                ReadQueueObject q = DataProcessor.readerQueue.poll();
                if(q != null) {
                    try {
                        switch (q.type) {
                            case ALL_WORDS: {
                                ArrayList<Word> top = DataProcessor.topWords(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used (non necessarily dictionary) words\nfor " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case DICT:{
                                ArrayList<Word> top = DataProcessor.topDict(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used dictionary words\nfor " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case NONDICT:{
                                ArrayList<Word> top = DataProcessor.nonDict(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used non-dictionary words\nfor " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case MENTIONS:{
                                ArrayList<Word> top = DataProcessor.mentions(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most mentioned users for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append("<@").append(BotUtils.capitalizeFirst(z.name)).append(">\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("User", x.toString(), true);
                                    e.addField("Mentions", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case SYMBOLS:{
                                ArrayList<Word> top = DataProcessor.symbols(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used symbols for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Symbol", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case EVERYTHING:{
                                ArrayList<Word> top = DataProcessor.allTop(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most common strings of all types (word, symbol, mention)\nfor " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("String", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case BOTTOM: {
                                ArrayList<Word> top = DataProcessor.bottomWords(q.serverID, 10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Least used (non necessarily dictionary) words for\n" + q.guild.getName() + " that occur at least once");

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case EMOTE:{
                                ArrayList<Word> top = DataProcessor.emotes(q.serverID, 10);

                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most common Discord and server-specific emotes for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(z.name).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Symbol", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }

                            case READ_SINGLE_WORD:{
                                Word w = DataProcessor.getWord(q.serverID, q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Word statistics for \"" + BotUtils.capitalizeFirst(q.parameter) + "\"");
                                    e.addField("Count", finalW.value + "", false);
                                    e.addField("Is dictionary word?", BotUtils.dictionary.isWord(finalW.name) ? "Yes" : "No", false);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case READ_SINGLE_MENTION:{
                                Word w = DataProcessor.getMention(q.serverID, q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Mention statistics for <@" + finalW.name + ">");
                                    e.addField("Mentions", finalW.value + "", true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case READ_SINGLE_SYMBOL:{
                                Word w = DataProcessor.getSymbol(q.serverID, q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Statistics for symbol " + finalW.name);
                                    e.addField("Count", finalW.value + "", true);
                                    e.addField("Is basic ASCII?", (int) finalW.name.charAt(0) > 255 ? "No" : "Yes", false);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }

                            case ALL_WORDS_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topWordsChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used (non necessarily dictionary) words in <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case DICT_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topDictChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used dictionary words in <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case NONDICT_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topNonDictChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used non-dictionary words in <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case SYMBOLS_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topSymbolChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most used symbols in <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case MENTIONS_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topMentionChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most mentioned users in\n<#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append("<@").append(BotUtils.capitalizeFirst(z.name)).append(">\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case EVERYTHING_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topAllChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most common strings of all types (word, symbol, mention)\nin <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        if(BotUtils.isNumeric(z.name)){
                                            x.append("<@").append(BotUtils.capitalizeFirst(z.name)).append(">\n");
                                        }
                                        else {
                                            x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        }
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case BOTTOM_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.bottomWordsChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Least used words in <#" + q.parameter + "> for " + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                        x.append(BotUtils.capitalizeFirst(z.name)).append("\n");
                                        y.append(z.value).append("\n");
                                    });

                                    e.addField("Word", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case EMOTE_CHANNEL:{
                                ArrayList<Word> top = DataProcessor.topEmoteChannel(q.serverID, q.parameter,10);
                                Consumer<EmbedCreateSpec> spec = e -> {
                                    e.setDescription("Most common Discord and server-specific emotes in <#" + q.parameter + "> for\n" + q.guild.getName());

                                    StringBuilder x = new StringBuilder();
                                    StringBuilder y = new StringBuilder();

                                    top.forEach(z -> {
                                         x.append(z.name).append("\n");
                                            y.append(z.value).append("\n");
                                    });

                                    e.addField("Emote", x.toString(), true);
                                    e.addField("Instances", y.toString(), true);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }

                            case READ_SINGLE_WORD_CHANNEL:{
                                Word w = DataProcessor.getWordChannel(q.serverID, q.additionalParameters[0], q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Word statistics for \"" + BotUtils.capitalizeFirst(q.parameter) + "\" in <#" + q.additionalParameters[0] + ">");
                                    e.addField("Count", finalW.value + "", false);
                                    e.addField("Is dictionary word?", BotUtils.dictionary.isWord(finalW.name) ? "Yes" : "No", false);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case READ_SINGLE_SYMBOL_CHANNEL:{
                                Word w = DataProcessor.getSymbolChannel(q.serverID, q.additionalParameters[0], q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Statistics for " + BotUtils.capitalizeFirst(q.parameter) + " in <#" + q.additionalParameters[0] + ">");
                                    e.addField("Count", finalW.value + "", false);
                                    e.addField("Is basic ASCII?", (int) finalW.name.charAt(0) > 255 ? "No" : "Yes", false);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }
                            case READ_SINGLE_MENTION_CHANNEL:{
                                Word w = DataProcessor.getMentionChannel(q.serverID, q.additionalParameters[0], q.parameter);
                                Consumer<EmbedCreateSpec> spec;

                                if(w == null){
                                    w = new Word(q.parameter, 0);
                                }

                                Word finalW = w;
                                spec = e -> {
                                    e.setDescription("Mentions for <@" + q.parameter + "> in <#" + q.additionalParameters[0] + ">");
                                    e.addField("Count", finalW.value + "", false);

                                    //e.setFooter("Stats since " + BotUtils.sdf.format(new Date(q.guild.getJoinTime().toEpochMilli())), null);
                                };

                                BotUtils.sendMessage(q.channel, spec);
                                break;
                            }




                        }
                    } catch (ClientException ignored) {
                        System.out.println("[ReaderThread]: ClientException (probably embed with empty field)");
                    } catch (Exception e) {
                        if (q != null) {
                            //DataProcessor.readerQueue.offer(q);
                        }
                        e.printStackTrace();
                    }
                }
            }
            else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

}
