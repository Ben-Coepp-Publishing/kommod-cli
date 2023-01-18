package de.bencoepp.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bencoepp.entity.Check;
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
        DefaultNode tree = new DefaultNode("Honnet Tests");
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
        String jsonString = new JSONObject()
                .put("runType", "3")
                .toString();

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/test/run"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());

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
