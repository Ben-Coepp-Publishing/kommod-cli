package de.bencoepp;

import de.bencoepp.command.DoctorCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "honnet", mixinStandardHelpOptions = true, version = "demo v. 1.8",
        description = "The best way to test and analyze containers and systems on a professional scale",
        commandListHeading = "%nCommands:%n%nThe most commonly used honnet commands are:%n",
        footer = "%nSee 'probatio help <command>' to read about a specific subcommand or concept.",
        subcommands = {
                DoctorCommand.class,
                CommandLine.HelpCommand.class
        })
public class Honnet implements Runnable{
    @CommandLine.Option(names = {"-V", "--version"}, versionHelp = true, description = "display version info")
    boolean versionInfoRequested;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;
    public static void main(String... args) {
        System.exit(new CommandLine(new Honnet()).execute(args));
    }

    @Override
    public void run() {
        spec.commandLine().usage(System.err);
    }
}
