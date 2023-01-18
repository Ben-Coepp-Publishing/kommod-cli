package de.bencoepp.command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bencoepp.entity.Check;
import picocli.CommandLine;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "doctor",
        sortOptions = false,
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = "check for virtualization tools",
        description = "check for virtualization tools and software and its specific use cases for production")
public class DoctorCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-v", "--verbose"},
            description = "print full output to screen")
    boolean verbose;
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;
    @Override
    public Integer call() throws Exception {
        boolean ok = true;
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/doctor/all"))
                .GET()
                .build();
        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Check> checks = mapper.readValue(response.body(), new TypeReference<ArrayList<Check>>() {});

        boolean isThereIssue = false;
        int issueCount = 0;

        System.out.println("Doctor summary (to see all details, run honnet doctor -v):");
        for (Check check : checks) {
            if(check.getOk()){
                if(isThereIssue != true){
                    isThereIssue = false;
                }
                System.out.println("[√] " + check.getTitle() + " (" + check.getCommand() + ")");
            }else{
                isThereIssue = true;
                issueCount++;
                System.out.println("[!] " + check.getTitle() + " (" + check.getCommand() + ")");
            }
            if(verbose){
                System.out.println(check.getDescription());
            }
        }
        System.out.println("");
        if(isThereIssue){
            System.out.println("! Doctor found issues in " + issueCount + " category.");
        }else{
            System.out.println("√ Doctor found no issues but please re run this command from time to time.");
        }
        return ok ? 0 : 1;
    }
}
