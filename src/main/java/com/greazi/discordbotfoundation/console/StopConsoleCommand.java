package com.greazi.discordbotfoundation.console;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;

import java.util.List;

public class StopConsoleCommand extends SimpleConsoleCommand {

    public StopConsoleCommand() {
        super("stop");
        setDescription("Stops the bot");
        setUsage("stop");
    }

    @Override
    public void execute(List<String> args) {
        Thread thread = new Thread(() -> {
            //TODO
//            while(false){
//                System.out.println(ConsoleColor.RED+"Waiting for MYSQL to stop..."+ConsoleColor.RESET);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }

            System.out.println(ConsoleColor.RED+"Stopping JDA..."+ConsoleColor.RESET);
            SimpleBot.getJDA().shutdown();
            System.out.println(ConsoleColor.RED+"Stopping bot..."+ConsoleColor.RESET);
            System.exit(0);
        });
        thread.start();
    }
}
