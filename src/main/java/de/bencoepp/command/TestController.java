package de.bencoepp.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bencoepp.entity.test.Test;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;
import org.barfuin.texttree.api.style.TreeStyle;
import org.json.JSONObject;
import picocli.CommandLine;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "test",
        sortOptions = false,
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = "test your system and tools",
        description = "test local tools and your system in a variety of ways to make sure that you are ready for production\n")
public class TestController implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;
    @Override
    public Integer call() {
        boolean ok = true;
        spec.commandLine().usage(System.err);
        return ok ? 0 : 1;
    }

    @CommandLine.Command(name = "list")
    public Integer list() throws URISyntaxException, IOException, InterruptedException {
        boolean ok = true;
        ArrayList<Test> tests = getAllTests();
        DefaultNode tree = new DefaultNode("Kommod Tests");
        DefaultNode system = new DefaultNode("System");
        DefaultNode docker = new DefaultNode("Docker");

        for (Test test : tests) {
            if(test.getType().equals(Test.TYPE_SYSTEM)){
                DefaultNode nodeSystem = new DefaultNode(test.getTitle());
                nodeSystem.setAnnotation(test.getSubTitle());
                system.addChild(nodeSystem);
            }else if(test.getType().equals(Test.TYPE_DOCKER)){
                DefaultNode nodeDocker = new DefaultNode(test.getTitle());
                nodeDocker.setAnnotation(test.getSubTitle());
                docker.addChild(nodeDocker);
            }
        }

        tree.addChild(system);
        tree.addChild(docker);
        TreeOptions options = new TreeOptions();
        options.setStyle(new TreeStyle("├── ", "│   ", "╰── ", "<", ">"));
        String rendered = TextTree.newInstance(options).render(tree);
        System.out.println(rendered);

        return ok ? 0 : 1;
    }

    @CommandLine.Command(name = "run")
    public Integer run() throws URISyntaxException, IOException, InterruptedException {
        boolean ok = true;

        System.out.println("Please select what type of test run you want to make." +
                " If you are not sure how this will work please run this command again with --help." +
                " This will print a short description of everything that this command can do.\n");

        Scanner userInput = new Scanner(System.in);
        JSONObject body = new JSONObject();
        boolean depthChosen = false;

        System.out.println("[1] Simple");
        System.out.println("[2] Normal");
        System.out.println("[3] Full");
        System.out.println("[4] Specific");
        System.out.println("\nPlease select the depth you want to run");

        while (depthChosen == false){
            String str = userInput.nextLine();
            try {
                int depthSelected = Integer.parseInt(str);
                body.put("depth", depthSelected);
                depthChosen = true;
            }catch (Exception e){
                System.out.println("Please select a depth to run");
            }
        }

        if(depthChosen && body.getInt("depth") == 4){
            System.out.println("Please choose the desired tests you want to run from the below list.\n" +
                    "A example list would look like this.\n" +
                    "1,4,23,33\n");

            ArrayList<Test> tests = getAllTests();
            for (int i = 0; i < tests.size(); i++) {
                System.out.println("[" + i  + "] " + tests.get(i).getType());
            }
            boolean testsChosen = false;
            while (testsChosen == false){
                System.out.println("\nInput your desired tests:");
                String str = userInput.nextLine();
                try{
                    String[] arrayTests = str.split(",");
                    ArrayList<String> arrayTitles = new ArrayList<>();
                    for (String strS : arrayTests) {
                        arrayTitles.add(tests.get(Integer.parseInt(strS)).getTitle());
                    }
                    body.put("tests", arrayTitles);
                    testsChosen = true;
                }catch (Exception e){
                    System.out.println("Please try again");
                }
            }
        }

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/test/run"))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());
        //TODO implement wait and then get response method
        return ok ? 0 : 1;
    }

    private ArrayList<Test> getAllTests() throws URISyntaxException, IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/test/all"))
                .GET()
                .build();
        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(response.body(), new TypeReference<ArrayList<Test>>() {});
    }
}
